package powercrystals.minefactoryreloaded.tile.base;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraftforge.fluids.IFluidTank;
import powercrystals.minefactoryreloaded.core.MFRLiquidMover;
import powercrystals.minefactoryreloaded.core.MFRUtil;
import powercrystals.minefactoryreloaded.net.MFRPacket;
import powercrystals.minefactoryreloaded.setup.Machine;

public abstract class TileEntityFactoryTickable extends TileEntityFactoryInventory implements ITickable {

	private boolean prevActive;
	private long lastActive = -100;
	private static final int FAILED_DROP_TICKS_MAX = 20;
	private int failedDropTicks = 0;

	protected TileEntityFactoryTickable(Machine machine) {

		super(machine);
	}

	@Override
	public void update() {

		if (!world.isRemote && prevActive != isActive() && lastActive < world.getTotalWorldTime()) {
			prevActive = isActive();
			MFRUtil.notifyBlockUpdate(world, pos);
		}

		if (!world.isRemote && shouldPumpLiquid()) {
			for (IFluidTank tank : getTanks())
				if (shouldPumpTank(tank))
					MFRLiquidMover.pumpLiquid(tank, this);
		}

		if (failedDrops != null) {
			if (failedDropTicks < FAILED_DROP_TICKS_MAX) {
				failedDropTicks++;
				return;
			}
			failedDropTicks = 0;
			if (!doDrop(failedDrops)) {
				return;
			}
			failedDrops = null;
			markDirty();
		}
	}

	@Override
	public void setIsActive(boolean isActive) {

		if (this.isActive() != isActive & world != null &&
				!world.isRemote && lastActive < world.getTotalWorldTime()) {
			lastActive = world.getTotalWorldTime() + _activeSyncTimeout;
			prevActive = this.isActive();
			MFRUtil.notifyBlockUpdate(world, pos);
		}

		super.setIsActive(isActive);
	}

	@Override
	protected void handlePacketData(NBTTagCompound tag) {

		prevActive = isActive();

		super.handlePacketData(tag);

		if (prevActive != isActive())
			MFRUtil.notifyBlockUpdate(world, pos);
		if (lastActive < 0 && hasHAM()) {
			MFRPacket.sendHAMUpdateToServer(this);
		}
		lastActive = 5;
	}

	@Override
	public void markForUpdate() {

		super.markForUpdate();
		lastActive = 0;
	}

}
