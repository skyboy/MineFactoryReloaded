package powercrystals.minefactoryreloaded.tile.machine.animals;

import cofh.core.fluid.FluidTankCore;
import cofh.core.util.helpers.MathHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import powercrystals.minefactoryreloaded.block.BlockFactoryMachine;
import powercrystals.minefactoryreloaded.core.Area;
import powercrystals.minefactoryreloaded.core.MFRLiquidMover;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryInventory;
import powercrystals.minefactoryreloaded.gui.client.GuiSewer;
import powercrystals.minefactoryreloaded.gui.container.ContainerSewer;
import powercrystals.minefactoryreloaded.setup.MFRFluids;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryTickable;

import javax.annotation.Nonnull;
import java.util.List;

public class TileEntitySewer extends TileEntityFactoryTickable {

	private boolean _jammed;
	private int _tick;
	private long _nextSewerCheckTick;

	public TileEntitySewer() {

		super(Machine.Sewer);
		createHAM(this, 0, 1, 0, false);
		_areaManager.setOverrideDirection(EnumFacing.UP);
		_tanks[0].setLock(MFRFluids.getFluid("sewage"));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiFactoryInventory getGui(InventoryPlayer inventoryPlayer) {

		return new GuiSewer(getContainer(inventoryPlayer), this);
	}

	@Override
	public ContainerSewer getContainer(InventoryPlayer inventoryPlayer) {

		return new ContainerSewer(this, inventoryPlayer);
	}

	@Override
	protected boolean shouldPumpLiquid() {

		return true;
	}

	@Override
	public void update() {

		super.update();
		if (world.isRemote) {
			return;
		}
		_tick++;

		if (_nextSewerCheckTick <= world.getTotalWorldTime()) {
			Area a = new Area(pos, _areaManager.getRadius(), 2, 2);
			_jammed = false;
			for (BlockPos bp : a.getPositionsBottomFirst()) {
				IBlockState state = world.getBlockState(bp);
				if (state.getBlock().equals(_machine.getBlock()) &&
						state.getValue(BlockFactoryMachine.TYPE).getMeta() == _machine.getMeta() &&
						!(bp.equals(pos))) {
					_jammed = true;
					break;
				}
			}

			_nextSewerCheckTick = world.getTotalWorldTime() + 800 + world.rand.nextInt(800);
		}

		if (_tick >= 31 && !_jammed) {
			_tick = 0;
			double massFound = 0;
			long worldTime = world.getTotalWorldTime();
			AxisAlignedBB box = _areaManager.getHarvestArea().toAxisAlignedBB();
			l:
			{
				int maxAmount = _tanks[1].getSpace();
				if (maxAmount <= 0) {
					break l;
				}

				List<EntityXPOrb> entities = world.getEntitiesWithinAABB(EntityXPOrb.class, box);
				for (EntityXPOrb orb : entities) {
					if (!orb.isDead) {
						if (MFRLiquidMover.fillTankWithXP(_tanks[1], orb) == 0)
							break;
					}
				}
			}

			List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, box);
			for (EntityLivingBase o : entities) {
				if (o instanceof EntityAnimal || o instanceof EntityVillager || (o.isSneaking() && o instanceof EntityPlayer)) {
					if (o.getEntityData().getLong("mfr:sewerTime") > worldTime) {
						continue;
					}
					o.getEntityData().setLong("mfr:sewerTime", worldTime + 30);
					massFound += Math.pow(o.getEntityBoundingBox().getAverageEdgeLength(), 2);
				}
			}

			if (massFound > 0) {
				_tanks[0].fill(FluidRegistry.getFluidStack("sewage", (int) (25 * massFound)), true);
			}
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {

		tag = super.writeToNBT(tag);

		tag.setBoolean("jammed", _jammed);
		tag.setByte("tick", (byte) _tick);
		tag.setLong("next", _nextSewerCheckTick);

		return tag;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {

		super.readFromNBT(tag);

		_jammed = tag.getBoolean("jammed");
		_tick = tag.hasKey("tick") ? tag.getByte("tick") : MathHelper.RANDOM.nextInt(32);
		_nextSewerCheckTick = tag.getLong("next");
	}

	@Override
	protected FluidTankCore[] createTanks() {

		return new FluidTankCore[] { new FluidTankCore(BUCKET_VOLUME),
				new FluidTankCore(BUCKET_VOLUME * 4) };
	}

	@Override
	public int getSizeInventory() {

		return 1;
	}

	@Override
	public int getUpgradeSlot() {

		return 0;
	}

	@Override
	public boolean canInsertItem(int slot, @Nonnull ItemStack itemstack, EnumFacing side) {

		return slot == 0 && isUsableAugment(itemstack);
	}

	@Override
	public boolean canExtractItem(int slot, @Nonnull ItemStack itemstack, EnumFacing side) {

		return false;
	}

	@Override
	public boolean allowBucketDrain(EnumFacing facing, @Nonnull ItemStack stack) {

		return true;
	}

	@Override
	protected boolean canFillTank(EnumFacing facing, int index) {

		return false;
	}

	@Override
	public int fill(EnumFacing facing, FluidStack resource, boolean doFill) {

		return 0;
	}

}
