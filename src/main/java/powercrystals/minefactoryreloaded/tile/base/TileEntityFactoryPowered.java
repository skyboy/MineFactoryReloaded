package powercrystals.minefactoryreloaded.tile.base;

import appeng.api.implementations.tiles.ICrankable;
import cofh.api.item.IAugmentItem;
import cofh.core.util.CoreUtils;
import cofh.core.util.helpers.AugmentHelper;
import cofh.redstoneflux.api.IEnergyReceiver;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import powercrystals.minefactoryreloaded.modhelpers.Compats;
import powercrystals.minefactoryreloaded.setup.Machine;

import javax.annotation.Nonnull;

/*
 * There are three pieces of information tracked - energy, work, and idle ticks.
 * Energy is stored and used when the _machine activates. The energy stored must be >= energyActivation for the activateMachine() method to be called.
 * If activateMachine() returns true, energy will be drained.
 * Work is built up and then when at 100% something happens. This is tracked/used entirely by the derived class. If not used (f.ex. harvester), return max 1.
 * Idle ticks cause an artificial delay before activateMachine() is called again. Max should be the highest value the _machine will use, to draw the
 * progress bar correctly.
 */
@Optional.Interface(iface = "appeng.api.implementations.tiles.ICrankable", modid = Compats.ModIds.APP_ENG)
public abstract class TileEntityFactoryPowered extends TileEntityFactoryTickable implements IEnergyReceiver, ICrankable {

	public static final int energyPerAE = 2;
	public static final int energyPerEU = 4;
	public static final int energyPerMJ = 10;

	private static final int energyFudge = 80;

	private int _energyStored;
	private int _maxEnergyStored;
	private int _maxEnergyTick;
	private int _energyRequiredThisTick = 0;

	protected int _energyActivation;

	private int _workTicks = 1;
	private int _workDone;

	private int _idleTicks;

	// constructors

	protected TileEntityFactoryPowered(Machine machine) {

		this(machine, machine.getActivationEnergy());
	}

	protected TileEntityFactoryPowered(Machine machine, int activationCost) {

		super(machine);
		_maxEnergyStored = machine.getMaxEnergyStorage();
		setActivationEnergy(activationCost);
		setIsActive(false);
	}

	// local methods

	protected void setActivationEnergy(int activationCost) {

		_energyActivation = activationCost;
		_maxEnergyTick = Math.min(activationCost * 4, _maxEnergyStored);
	}

	@Override
	public void update() {

		super.update();

		_energyStored = Math.min(_energyStored, getEnergyStoredMax());

		if (world.isRemote) {
			machineDisplayTick();
			return;
		}

		markChunkDirty();

		int energyRequired = Math.min(getEnergyStoredMax() - getEnergyStored(), getActivationEnergy() + energyFudge);

		_energyRequiredThisTick = Math.max(_energyRequiredThisTick + energyRequired, getMaxEnergyPerTick());

		setIsActive(updateIsActive(failedDrops != null));

		if (failedDrops != null) {
			setIdleTicks(getIdleTicksMax());
			return;
		}

		if (_rednetState > 0 || CoreUtils.isRedstonePowered(this)) {
			setIdleTicks(getIdleTicksMax());
		} else if (_idleTicks > 0) {
			_idleTicks--;
		} else if (_energyStored >= _energyActivation) {
			int i = 0;
			for (; i < _workTicks && activateMachine(); ++i);
			if (i > 0) {
				_energyStored -= _energyActivation;
			}
		}
	}

	@Override
	protected void onFactoryInventoryChanged() {

		super.onFactoryInventoryChanged();
		int i = getUpgradeSlot();
		if (i < 0) {
			return;
		}
		@Nonnull ItemStack stack = getStackInSlot(i);
		if (AugmentHelper.isAugmentItem(stack)) {
			IAugmentItem item = (IAugmentItem) stack.getItem();
			if ("machineSpeed".equals(item.getAugmentIdentifier(stack))) {
				int level = item.getAugmentType(stack) == IAugmentItem.AugmentType.BASIC ? 1 : 2; //TODO this needs review
				_workTicks = (int) Math.pow(2, level);
				int e = _machine.getActivationEnergy();
				setActivationEnergy(_workTicks * (e + ((e * level) >> 1)));
			}
		} else {
			if (_workTicks > 1) {
				setActivationEnergy(_machine.getActivationEnergy());
			}
			_workTicks = 1;
		}
	}

	@Override
	protected boolean canUseUpgrade(@Nonnull ItemStack stack, IAugmentItem item) {

		return super.canUseUpgrade(stack, item) || "machineSpeed".equals(item.getAugmentIdentifier(stack));
	}

	protected boolean updateIsActive(boolean failedDrops) {

		return !failedDrops && hasSufficientPower();
	}

	protected abstract boolean activateMachine();

	@SideOnly(Side.CLIENT)
	protected void machineDisplayTick() {

	}

	public final boolean hasSufficientPower() {

		return _energyStored >= _energyActivation * 2;
	}

	public int getActivationEnergy() {

		return _energyActivation;
	}

	public int getMaxEnergyPerTick() {

		return _maxEnergyTick;
	}

	public int getEnergyStored() {

		return _energyStored;
	}

	public int getEnergyStoredMax() {

		return _maxEnergyStored;
	}

	public void setEnergyStored(int energy) {

		_energyStored = energy;
	}

	public void drainEnergy(int drainAmount) {

		_energyStored -= drainAmount;
	}

	public int getWorkDone() {

		return _workDone;
	}

	public abstract int getWorkMax();

	public void setWorkDone(int work) {

		_workDone = work;
	}

	protected boolean incrementWorkDone() {

		setWorkDone(getWorkDone() + 1);
		return true;
	}

	public int getIdleTicks() {

		return _idleTicks;
	}

	public abstract int getIdleTicksMax();

	public void setIdleTicks(int ticks) {

		_idleTicks = ticks;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {

		tag = super.writeToNBT(tag);

		if (_workDone > 0)
			tag.setInteger("workDone", _workDone);

		if (_idleTicks > 0)
			tag.setInteger("idleDone", _idleTicks);

		return tag;
	}

	@Override
	public void writeItemNBT(NBTTagCompound tag) {

		super.writeItemNBT(tag);

		if (_energyStored > 0)
			tag.setInteger("energyStored", _energyStored);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {

		super.readFromNBT(tag);

		_energyStored = Math.min(tag.getInteger("energyStored"), getEnergyStoredMax());
		_idleTicks = Math.min(tag.getInteger("idleDone"), getIdleTicksMax());
		_workDone = Math.min(tag.getInteger("workDone"), getWorkMax());
	}

	public int getEnergyRequired() {

		return Math.min(getEnergyStoredMax() - getEnergyStored(), _energyRequiredThisTick);
	}

	public int storeEnergy(int energy, boolean doStore) {

		int energyInjected = Math.max(Math.min(energy, getEnergyRequired()), 0);
		if (doStore) {
			_energyStored += energyInjected;
			_energyRequiredThisTick -= energyInjected;
		}
		return energyInjected;
	}

	public int storeEnergy(int energy) {

		return storeEnergy(energy, true);
	}

	// TE methods
	/*
		@Override
		public int getInfoEnergyPerTick() {
			return getEnergyRequired();
		}

		@Override
		public int getInfoMaxEnergyPerTick() {
			return getMaxEnergyPerTick();
		}

		@Override
		public int getInfoEnergyStored() {
			return getEnergyStored();
		}

		@Override
		public int getInfoMaxEnergyStored() {
			return getEnergyStoredMax();
		}//*/

	@Override
	public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {

		return storeEnergy(maxReceive, !simulate);
	}

	@Override
	public boolean canConnectEnergy(EnumFacing from) {

		return true;
	}

	@Override
	public int getEnergyStored(EnumFacing from) {

		return getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored(EnumFacing from) {

		return getEnergyStoredMax();
	}

	// AE methods

	@Optional.Method(modid = Compats.ModIds.APP_ENG)
	@Override
	public boolean canTurn() {

		return getEnergyStored() < getEnergyStoredMax();
	}

	@Optional.Method(modid = Compats.ModIds.APP_ENG)
	@Override
	public void applyTurn() {

		storeEnergy(90, true);
	}

	@Optional.Method(modid = Compats.ModIds.APP_ENG)
	@Override
	public boolean canCrankAttach(EnumFacing directionToCrank) {

		return true;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {

		return capability == CapabilityEnergy.ENERGY || super.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {

		if (capability == CapabilityEnergy.ENERGY) {
			return CapabilityEnergy.ENERGY.cast(new IEnergyStorage() {

				@Override
				public int receiveEnergy(int maxReceive, boolean simulate) {

					return TileEntityFactoryPowered.this.receiveEnergy(facing, maxReceive, simulate);
				}

				@Override
				public int extractEnergy(int maxExtract, boolean simulate) {

					return 0;
				}

				@Override
				public int getEnergyStored() {

					return TileEntityFactoryPowered.this.getEnergyStored();
				}

				@Override
				public int getMaxEnergyStored() {

					return TileEntityFactoryPowered.this.getMaxEnergyStored(facing);
				}

				@Override
				public boolean canExtract() {

					return false;
				}

				@Override
				public boolean canReceive() {

					return true;
				}
			});
		}

		return super.getCapability(capability, facing);
	}

}
