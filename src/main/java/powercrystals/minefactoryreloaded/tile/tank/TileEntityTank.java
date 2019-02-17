package powercrystals.minefactoryreloaded.tile.tank;

import cofh.core.fluid.FluidTankCore;
import cofh.core.util.helpers.FluidHelper;
import cofh.core.util.helpers.StringHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import powercrystals.minefactoryreloaded.core.IDelayedValidate;
import powercrystals.minefactoryreloaded.core.ITankContainerBucketable;
import powercrystals.minefactoryreloaded.core.MFRUtil;
import powercrystals.minefactoryreloaded.setup.MFRThings;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactory;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryInventory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class TileEntityTank extends TileEntityFactory implements ITankContainerBucketable, IDelayedValidate {

	public static int CAPACITY = FluidHelper.BUCKET_VOLUME * 4;
	TankNetwork grid;
	FluidTankCore _tank;
	protected byte sides;

	public TileEntityTank() {

		super(null);
		setManageFluids(true);
		_tank = new FluidTankCore(CAPACITY);
	}

	@Override
	public void invalidate() {

		removeTankFromGrid(false);

		super.invalidate();
	}

	private void removeTankFromGrid(boolean chunkUnloaded) {

		if (grid != null) {
			for (EnumFacing to : EnumFacing.VALUES) {
				BlockPos offsetPos = pos.offset(to);
				if ((sides & (1 << to.ordinal())) == 0 || (chunkUnloaded && !world.isBlockLoaded(offsetPos)))
					continue;
				TileEntityTank tank = MFRUtil.getTile(world, offsetPos, TileEntityTank.class);
				if (tank != null)
					tank.part(to.getOpposite());
			}
			if (grid != null)
				grid.removeNode(this);
			grid = null;
		}
	}

	@Override
	public void onChunkUnload() {

		removeTankFromGrid(true);
		super.onChunkUnload();
	}

	@Override
	public final boolean isNotValid() {

		return isInvalid();
	}

	@Override
	public void firstTick() {

		if (!inWorld) return;
		for (EnumFacing to : EnumFacing.VALUES) {
			if (to.getFrontOffsetY() != 0 || !world.isBlockLoaded(pos.offset(to)))
				continue;
			TileEntityTank tank = MFRUtil.getTile(world, pos.offset(to), TileEntityTank.class);
			if (tank != null && tank.grid != null && FluidHelper.isFluidEqualOrNull(tank.grid.getStorage().getFluid(), _tank.getFluid())) {
				if (tank.grid != null)
					if (tank.grid == grid || tank.grid.addNode(this)) {
						tank.join(to.getOpposite());
						join(to);
					}
			}
		}
		if (grid == null)
			grid = new TankNetwork(this);
	}

	@Override
	public void onLoad() {

		super.onLoad();

		if (world.isRemote)
			return;
		firstTick();
		markDirty();
	}

	public void join(EnumFacing from) {

		sides |= (1 << from.ordinal());
		markChunkDirty();
		MFRUtil.notifyBlockUpdate(world, pos);
	}

	public void part(EnumFacing from) {

		sides &= ~(1 << from.ordinal());
		markChunkDirty();
		MFRUtil.notifyBlockUpdate(world, pos);
	}

	public boolean isInterfacing(EnumFacing to) {

		return 0 != (sides & (1 << to.ordinal()));
	}

	int interfaceCount() {

		return Integer.bitCount(sides);
	}

	@Override
	protected NBTTagCompound writePacketData(NBTTagCompound tag) {

		if (grid == null)
			return super.writePacketData(tag);

		FluidStack fluid = grid.getStorage().drain(1, false); //TODO does this need to be drain instead of simple getFluid??
		if (fluid != null)
			tag.setTag("fluid", fluid.writeToNBT(new NBTTagCompound()));
		tag.setByte("sides", sides);

		return super.writePacketData(tag);
	}

	@Override
	protected void handlePacketData(NBTTagCompound tag) {

		super.handlePacketData(tag);

		FluidStack fluid = FluidStack.loadFluidStackFromNBT(tag.getCompoundTag("fluid"));
		_tank.setFluid(fluid);
		sides = tag.getByte("sides");

		world.checkLight(pos);
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {

		if (grid == null)
			return null;

		return super.getUpdatePacket();
	}

	@Override
	public String getDataType() {

		return "tile.mfr.tank.name";
	}

	@Override
	public void writeItemNBT(NBTTagCompound tag) {

		super.writeItemNBT(tag);
		if (_tank.getFluidAmount() != 0)
			tag.setTag("tank", _tank.writeToNBT(new NBTTagCompound()));
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {

		super.readFromNBT(tag);
		_tank.readFromNBT(tag.getCompoundTag("tank"));
	}

	public FluidStack getFluid() {

		if (grid == null)
			return _tank.getFluid();
		return grid.getStorage().getFluid();
	}

	@Override
	public boolean allowBucketFill(EnumFacing facing, @Nonnull ItemStack stack) {

		return stack.getItem() != MFRThings.plasticTankItem;
	}

	@Override
	public boolean allowBucketDrain(EnumFacing facing, @Nonnull ItemStack stack) {

		return true;
	}

	@Override
	public IFluidTankProperties[] getTankProperties(EnumFacing facing) {

		if (grid == null)
			return TileEntityFactoryInventory.emptyIFluidTankProperties;
		return grid.getStorage().getTankProperties();
	}


	@Override
	public int fill(EnumFacing facing, FluidStack resource, boolean doFill) {

		if (grid == null)
			return 0;
		return grid.getStorage().fill(resource, doFill);
	}

	@Nullable
	@Override
	public FluidStack drain(EnumFacing facing, FluidStack resource, boolean doDrain) {

		if (grid == null)
			return null;
		return grid.getStorage().drain(resource, doDrain);
	}

	@Nullable
	@Override
	public FluidStack drain(EnumFacing facing, int maxDrain, boolean doDrain) {

		if (grid == null)
			return world.isRemote ? _tank.drain(maxDrain, false) : null;
		return grid.getStorage().drain(maxDrain, doDrain);
	}

	@Override
	public void getTileInfo(List<ITextComponent> info, EnumFacing side, EntityPlayer player, boolean debug) {

		if (debug) {
			info.add(new TextComponentString("Grid: " + grid));
			if (grid != null)
				info.add(new TextComponentString(Arrays.toString(grid.getStorage().tanks)));
		}
		if (grid == null) {
			info.add(new TextComponentString("Null Grid!!"));
			if (debug)
				info.add(new TextComponentString("FluidForGrid: " +
						StringHelper.getFluidName(_tank.getFluid(), "") + '@' + _tank.getFluidAmount()));
			return;
		}
		if (grid.getStorage().getFluidAmount() == 0)
			info.add(new TextComponentString(MFRUtil.empty()));
		else
			info.add(new TextComponentString(MFRUtil.getFluidName(grid.getStorage().getFluid())));
		info.add(new TextComponentString((grid.getStorage().getFluidAmount() / (float) grid.getStorage().getCapacity() * 100f) + "%"));
		if (debug) {
			info.add(new TextComponentString("Sides: " + Integer.toBinaryString(sides)));
			info.add(new TextComponentString(grid.getStorage().getFluidAmount() + " / " + grid.getStorage().getCapacity()));
			info.add(new TextComponentString("Size: " + grid.getSize() + " | FluidForGrid: " +
					StringHelper.getFluidName(_tank.getFluid(), "") + '@' + _tank.getFluidAmount()));
			info.add(new TextComponentString("Length: " + grid.getStorage().length + " | Index: " + grid.getStorage().index +
					" | Reserve: " + grid.getStorage().tanks.length));
		}
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {

		if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
			return manageFluids();

		return super.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {

		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
			if (manageFluids())
				return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(new TileEntityFactoryInventory.FactoryFluidHandler(this, facing));
			return null; // no external overriding via events
		}

		return super.getCapability(capability, facing);
	}

}
