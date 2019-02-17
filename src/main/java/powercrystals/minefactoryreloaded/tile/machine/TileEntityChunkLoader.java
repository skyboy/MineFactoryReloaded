package powercrystals.minefactoryreloaded.tile.machine;

import cofh.core.fluid.FluidTankCore;
import gnu.trove.map.hash.TObjectIntHashMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.ForgeChunkManager.Type;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.api.laser.IFactoryLaserTarget;
import powercrystals.minefactoryreloaded.gui.client.GuiChunkLoader;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryInventory;
import powercrystals.minefactoryreloaded.gui.container.ContainerChunkLoader;
import powercrystals.minefactoryreloaded.gui.container.ContainerFactoryPowered;
import powercrystals.minefactoryreloaded.net.ConnectionHandler;
import powercrystals.minefactoryreloaded.setup.MFRConfig;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryPowered;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.Set;

public class TileEntityChunkLoader extends TileEntityFactoryPowered implements IFactoryLaserTarget {

	private static void bypassLimit(Ticket tick) {

		try {
			Field f = Ticket.class.getDeclaredField("maxDepth");
			f.setAccessible(true);
			f.setInt(tick, Short.MAX_VALUE);
		} catch (Throwable t) {
		}
	}

	protected static TObjectIntHashMap<String> fluidConsumptionRate = new TObjectIntHashMap<>();
	static {
		fluidConsumptionRate.put("mob_essence", 10);
		fluidConsumptionRate.put("liquidessence", 20);
		fluidConsumptionRate.put("ender", 40);
	}

	protected short _radius;
	protected boolean activated, unableToRequestTicket;
	public boolean useAltPower;
	protected Ticket _ticket;
	protected int consumptionTicks;
	protected int emptyTicks, prevEmpty;
	protected int unactivatedTicks;

	public TileEntityChunkLoader() {

		super(Machine.ChunkLoader);
		_radius = 0;
		useAltPower = MFRConfig.enableConfigurableCLEnergy.getBoolean(false);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiFactoryInventory getGui(InventoryPlayer inventoryPlayer) {

		return new GuiChunkLoader(getContainer(inventoryPlayer), this);
	}

	@Override
	public ContainerFactoryPowered getContainer(InventoryPlayer inventoryPlayer) {

		if (unableToRequestTicket &&
				inventoryPlayer.player.getName().equals(_owner)) {
			inventoryPlayer.player.sendMessage(
					new TextComponentTranslation("chat.info.mfr.chunkloader.noticket"));
		}
		return new ContainerChunkLoader(this, inventoryPlayer);
	}

	@Override
	protected FluidTankCore[] createTanks() {

		return new FluidTankCore[] { new FluidTankCore(BUCKET_VOLUME * 10) };
	}

	@Override
	public void invalidate() {

		super.invalidate();
		if (_ticket != null) {
			unforceChunks();
			ForgeChunkManager.releaseTicket(_ticket);
		}
	}

	@Override
	public void onChunkUnload() {

		if (_ticket != null) {
			unforceChunks();
			ForgeChunkManager.releaseTicket(_ticket);
		}
		super.onChunkUnload();
	}

	public void setRadius(short r) {

		int maxR = 38;
		if (_ticket != null)
			maxR = Math.min((int) Math.sqrt(_ticket.getChunkListDepth() / Math.PI), maxR);
		if (r < 0 || r > maxR || r == _radius)
			return;
		_radius = r;
		markDirty();
		if (world != null && !world.isRemote)
			forceChunks();
	}

	@Override
	protected boolean activateMachine() {

		activated = true;
		unactivatedTicks = 0;
		if (consumptionTicks > 0)
			--consumptionTicks;
		else {
			emptyTicks = Math.min(65535, emptyTicks + 1);
			FluidStack s = _tanks[0].getFluid();
			if (drain(1, true, _tanks[0]) == 1) {
				consumptionTicks = fluidConsumptionRate.get(getFluidName(s));
				emptyTicks = Math.max(-65535, emptyTicks - 2);
			}
		}
		return true;
	}

	@Override
	public void update() {

		if (_owner.isEmpty())
			return;
		if (unableToRequestTicket) {
			setIdleTicks(getIdleTicksMax());
			super.setIsActive(false);
			return;
		}
		activated = false;
		if (!world.isRemote && MFRConfig.enableChunkLoaderRequiresOwner.getBoolean(false) &&
				!ConnectionHandler.onlinePlayerMap.containsKey(_owner)) {
			setIdleTicks(getIdleTicksMax());
		}
		super.update();
		if (world.isRemote)
			return;
		if (getIdleTicks() > 0) {
			if (_ticket != null)
				unforceChunks();
			return;
		}

		if (!activated)
			l: {
				if (_ticket != null) {
					Set<ChunkPos> chunks = _ticket.getChunkList();
					if (chunks.size() == 0)
						break l;

					unactivatedTicks = Math.min(_tanks[0].getCapacity() + 10, unactivatedTicks + 1);
					if (consumptionTicks > 0)
						consumptionTicks /= 10;
					else {
						emptyTicks = Math.min(65535, emptyTicks + 1);
						FluidStack s = _tanks[0].getFluid();
						if (drain(Math.min(unactivatedTicks,
							_tanks[0].getFluidAmount()), true, _tanks[0]) == unactivatedTicks) {
							consumptionTicks = fluidConsumptionRate.get(getFluidName(s));
							consumptionTicks = Math.max(0, consumptionTicks - unactivatedTicks);
							activated = emptyTicks == 1 && unactivatedTicks < _tanks[0].getCapacity();
							emptyTicks = Math.max(-65535, emptyTicks - 2);
							if (activated)
								break l;
						}
					}

					for (ChunkPos c : chunks)
						ForgeChunkManager.unforceChunk(_ticket, c);
				}
			}
		else if (activated && !isActive()) {
			if (_ticket == null) {
				_ticket = ForgeChunkManager.
						requestPlayerTicket(MineFactoryReloadedCore.instance(),
							_owner, world, Type.NORMAL);
				if (_ticket == null) {
					unableToRequestTicket = true;
					return;
				}
				_ticket.getModData().setInteger("X", pos.getX());
				_ticket.getModData().setInteger("Y", pos.getY());
				_ticket.getModData().setInteger("Z", pos.getZ());

			}
			forceChunks();
		}

		if (prevEmpty != emptyTicks) {
			prevEmpty = emptyTicks;
			onFactoryInventoryChanged();
		}

		super.setIsActive(activated);
	}

	protected void unforceChunks() {

		Set<ChunkPos> chunks = _ticket.getChunkList();
		if (chunks.size() == 0)
			return;

		for (ChunkPos c : chunks)
			ForgeChunkManager.unforceChunk(_ticket, c);
	}

	protected void forceChunks() {

		if (_ticket == null)
			return;
		if (MFRConfig.enableChunkLimitBypassing.getBoolean(false))
			bypassLimit(_ticket);
		Set<ChunkPos> chunks = _ticket.getChunkList();
		int x = pos.getX() >> 4;
		int z = pos.getZ() >> 4;
		int r = _radius * _radius;
		for (ChunkPos c : chunks) {
			int xS = c.x - x;
			int zS = c.z - z;
			if ((xS * xS + zS * zS) > r)
				ForgeChunkManager.unforceChunk(_ticket, c);
		}
		for (int xO = -_radius; xO <= _radius; ++xO) {
			int xS = xO * xO;
			for (int zO = -_radius; zO <= _radius; ++zO)
				if (xS + zO * zO <= r) {
					ChunkPos p = new ChunkPos(x + xO, z + zO);
					if (!chunks.contains(p))
						ForgeChunkManager.forceChunk(_ticket, p);
				}
		}
	}

	@Override
	protected void onFactoryInventoryChanged() {

		super.onFactoryInventoryChanged();
		if (isInvalid())
			return;
		int r = _radius + 1, c, r2 = _radius * _radius;
		if (_ticket == null) {
			// {int t = _radius * _radius; c = (int)(t * (float)Math.PI);}
			// this is the actual math for calculating the radius of a circle
			// and it is inaccurate for calculating the area of the loaded
			// circle of square chunks, so simulate the number of loaded chunks
			c = 0;
			for (int xO = -_radius; xO <= _radius; ++xO) {
				int xS = xO * xO;
				for (int zO = -_radius; zO <= _radius; ++zO)
					if (xS + zO * zO <= r2)
						++c;
			}
		}
		else
			c = _ticket.getChunkList().size();
		int energy;
		if (useAltPower) {
			int a = (r2 + 1) * c * 16 * _machine.getActivationEnergy();
			a &= ~a >> 31;
			energy = a;
			c *= 16;
		}
		else {
			double a = (r * r * 32 - 17 + r * r * r);
			for (int i = r / 10; i-- > 0;)
				a *= r / 6d;
			energy = (int) (a * 10);
		}
		energy += (int) (StrictMath.cbrt(emptyTicks) * c);
		energy &= ~energy >> 31;
		if (energy == 0)
			energy = 1;
		setActivationEnergy(energy);
	}

	public boolean receiveTicket(Ticket ticket) {

		if (!MFRConfig.enableChunkLoaderRequiresOwner.getBoolean(false) ||
				ConnectionHandler.onlinePlayerMap.containsKey(_owner)) {
			if (_ticket == null) {
				ForgeChunkManager.releaseTicket(ticket);
				_ticket = ForgeChunkManager.
						requestPlayerTicket(MineFactoryReloadedCore.instance(),
							_owner, world, Type.NORMAL);
				if (_ticket == null) {
					unableToRequestTicket = true;
					return true;
				}
				_ticket.getModData().setInteger("X", pos.getX());
				_ticket.getModData().setInteger("Y", pos.getY());
				_ticket.getModData().setInteger("Z", pos.getZ());
			}
			return true;
		}
		else {
			_ticket = ticket;
			unforceChunks();
			_ticket = null;
		}
		return false;
	}

	@Override
	public void writePortableData(EntityPlayer player, NBTTagCompound tag) {

		tag.setShort("radius", _radius);
	}

	@Override
	public void readPortableData(EntityPlayer player, NBTTagCompound tag) {

		setRadius(tag.getShort("radius"));
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {

		tag = super.writeToNBT(tag);

		tag.setShort("radius", _radius);
		tag.setInteger("empty", emptyTicks);
		tag.setInteger("inactive", unactivatedTicks);
		tag.setInteger("consumed", consumptionTicks);

		return tag;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {

		super.readFromNBT(tag);

		setRadius(tag.getShort("radius"));
		emptyTicks = tag.getInteger("empty");
		unactivatedTicks = tag.getInteger("inactive");
		consumptionTicks = tag.getInteger("consumed");
		onFactoryInventoryChanged();
	}

	protected boolean isFluidFuel(FluidStack fuel) {

		String name = getFluidName(fuel);
		if (name == null)
			return false;
		return fluidConsumptionRate.containsKey(name);
	}

	protected String getFluidName(FluidStack fluid) {

		if (fluid == null || fluid.getFluid() == null)
			return null;
		String name = fluid.getFluid().getName();
		if (name == null)
			return null;
		return name;
	}

	@Override
	public int getSizeInventory() {

		return 0;
	}

	@Override
	public int getWorkMax() {

		return 1;
	}

	@Override
	public int getIdleTicksMax() {

		return 40;
	}

	public short getRadius() {

		return _radius;
	}

	// reflection related helper for other mods
	public boolean getUnableToWork() {

		return unableToRequestTicket;
	}

	@SideOnly(Side.CLIENT)
	public void setEmpty(int r) {

		emptyTicks = r;
		onFactoryInventoryChanged();
	}

	public short getEmpty() {

		return (short) emptyTicks;
	}

	@Override
	public void setIsActive(boolean a) {

	}

	@Override
	public boolean canFormBeamWith(EnumFacing from) {

		return true;
	}

	@Override
	public int addEnergy(EnumFacing from, int energy, boolean simulate) {

		return storeEnergy(energy, !simulate);
	}

	@Override
	public int fill(EnumFacing facing, FluidStack resource, boolean doFill) {

		if (!unableToRequestTicket && resource != null && isFluidFuel(resource))
			for (FluidTankCore _tank : getTanks())
				if (_tank.getFluidAmount() == 0 || resource.isFluidEqual(_tank.getFluid()))
					return _tank.fill(resource, doFill);
		return 0;
	}

	@Override
	protected boolean canFillTank(EnumFacing facing, int index) {

		return !unableToRequestTicket;
	}

	@Override
	protected boolean canDrainTank(EnumFacing facing, int index) {

		return unableToRequestTicket;
	}

	@Nullable
	@Override
	public FluidStack drain(EnumFacing facing, FluidStack resource, boolean doDrain) {

		return !unableToRequestTicket ? null : super.drain(facing, resource, doDrain);
	}

	@Nullable
	@Override
	public FluidStack drain(EnumFacing facing, int maxDrain, boolean doDrain) {

		return !unableToRequestTicket ? null : super.drain(facing, maxDrain, doDrain);
	}

	@Override
	public boolean allowBucketFill(EnumFacing facing, @Nonnull ItemStack stack) {

		return !unableToRequestTicket;
	}

	@Override
	public boolean allowBucketDrain(EnumFacing facing, @Nonnull ItemStack stack) {

		return true;
	}

}
