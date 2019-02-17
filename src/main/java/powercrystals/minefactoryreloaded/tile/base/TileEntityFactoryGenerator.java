package powercrystals.minefactoryreloaded.tile.base;

import cofh.core.util.CoreUtils;
import cofh.core.util.helpers.EnergyHelper;
import cofh.redstoneflux.api.IEnergyProvider;
import cofh.redstoneflux.api.IEnergyReceiver;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import powercrystals.minefactoryreloaded.core.ForgeEnergyHandler;
import powercrystals.minefactoryreloaded.setup.Machine;

import javax.annotation.Nonnull;

public abstract class TileEntityFactoryGenerator extends TileEntityFactoryTickable
		implements IEnergyProvider {
	private boolean deadCache;
	private IEnergyReceiver[] receiverCache;

	private int _ticksBetweenConsumption;
	private int _outputPulseSize;

	private int _ticksSinceLastConsumption = 0;
	private int _energyMax;
	private int _energy;

	protected TileEntityFactoryGenerator(Machine machine, int ticksBetweenConsumption) {
		super(machine);
		if (machine.getActivationEnergy() <= 0)
			throw new IllegalStateException("Generators cannot produce 0 energy.");
		_ticksBetweenConsumption = ticksBetweenConsumption;
		_outputPulseSize = machine.getActivationEnergy();
		_energyMax = machine.getMaxEnergyStorage();
	}

	@Override
	public void validate() {
		super.validate();
		deadCache = true;
		receiverCache = null;
	}

	@Override
	public void update() {
		super.update();
		if (!world.isRemote) {
			if (deadCache) reCache();

			boolean skipConsumption = ++_ticksSinceLastConsumption < _ticksBetweenConsumption;

			if (_rednetState != 0 || CoreUtils.isRedstonePowered(this)) {
				setIsActive(false);
				return;
			}

			setIsActive(hasFuel());

			int pulse = Math.min(_energy, _outputPulseSize);
			_energy -= pulse - transmitEnergy(pulse);

			if (skipConsumption || !canConsumeFuel(_energyMax - _energy))
				return;

			if (consumeFuel()) {
				_energy += produceEnergy();
				_ticksSinceLastConsumption = 0;
			}
		}
	}

	protected abstract boolean canConsumeFuel(int space);
	protected abstract boolean consumeFuel();
	protected abstract boolean hasFuel();
	protected abstract int produceEnergy();

	protected final int transmitEnergy(int energy) {
		if (!_inventory.get(0).isEmpty())
			energy -= EnergyHelper.insertEnergyIntoContainer(_inventory.get(0), energy, false);
		if (energy <= 0)
			return 0;

		if (receiverCache != null) {
			for (int i = receiverCache.length; i --> 0; ) {
				IEnergyReceiver tile = receiverCache[i];
				if (tile == null)
					continue;

				EnumFacing from = EnumFacing.VALUES[i];
				if (tile.receiveEnergy(from, energy, true) > 0)
					energy -= tile.receiveEnergy(from, energy, false);
				if (energy <= 0)
					return 0;
			}
		}

		return energy;
	}

	public int getBuffer() {
		return _energy;
	}

	public void setBuffer(int buffer) {
		_energy = buffer;
	}

	public int getBufferMax() {
		return _energyMax;
	}

	@Override
	public int getSizeInventory() {
		return 1;
	}

	@Override
	public boolean canInsertItem(int slot, @Nonnull ItemStack itemstack, EnumFacing side) {
		return EnergyHelper.isEnergyContainerItem(itemstack);
	}

	@Override
	public boolean canExtractItem(int slot, @Nonnull ItemStack itemstack, EnumFacing side) {
		return !_inventory.get(0).isEmpty() && EnergyHelper.insertEnergyIntoContainer(_inventory.get(0), 2, true) < 2;
	}

	private void reCache() {
		if (deadCache) {
			for (EnumFacing dir : EnumFacing.VALUES)
				onNeighborTileChange(pos.offset(dir));
			deadCache = false;
		}
	}

	@Override
	public void onNeighborTileChange(BlockPos pos) {

		TileEntity tile = world.getTileEntity(pos);

		int xCoord = this.pos.getX(), yCoord = this.pos.getY(), zCoord = this.pos.getZ();
		int x = pos.getX(), y = pos.getY(), z = pos.getZ();

		if (x < xCoord)
				addCache(tile, EnumFacing.EAST);
		else if (x > xCoord)
				addCache(tile, EnumFacing.WEST);
		else if (z < zCoord)
				addCache(tile, EnumFacing.SOUTH);
		else if (z > zCoord)
				addCache(tile, EnumFacing.NORTH);
		else if (y < yCoord)
				addCache(tile, EnumFacing.UP);
		else if (y > yCoord)
				addCache(tile, EnumFacing.DOWN);
	}

	private void addCache(TileEntity tile, EnumFacing side) {
		if (receiverCache != null)
			receiverCache[side.ordinal()] = null;

		if (tile instanceof IEnergyReceiver) {
			if (((IEnergyReceiver)tile).canConnectEnergy(side)) {
				if (receiverCache == null) receiverCache = new IEnergyReceiver[6];
				receiverCache[side.ordinal()] = (IEnergyReceiver)tile;
			}
		} else if (EnergyHelper.isEnergyHandler(tile, side)) {
			if (tile.getCapability(CapabilityEnergy.ENERGY, side).canReceive()) {
				if (receiverCache == null) receiverCache = new IEnergyReceiver[6];
				receiverCache[side.ordinal()] = new ForgeEnergyHandler.Receiver(tile);
			}
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {

		tag = super.writeToNBT(tag);

		if (_ticksSinceLastConsumption > 0)
			tag.setInteger("ticksSinceLastConsumption", _ticksSinceLastConsumption);

		return tag;
	}

	@Override
	public void writeItemNBT(NBTTagCompound tag) {
		super.writeItemNBT(tag);

		if (_energy > 0)
			tag.setInteger("energyStored", _energy);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);

		_ticksSinceLastConsumption = tag.getInteger("ticksSinceLastConsumption");
		_energy = tag.getInteger(tag.hasKey("energyStored") ? "energyStored" : "buffer");
	}

	// TE methods

	@Override
	public boolean canConnectEnergy(EnumFacing from) {
		return true;
	}

	@Override
	public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate) {
		maxExtract = Math.min(_energy, Math.min(_outputPulseSize, maxExtract));
		if (maxExtract <= 0) return 0;

		if (!simulate)
			_energy -= maxExtract;
		return maxExtract;
	}

	@Override
	public int getEnergyStored(EnumFacing from) {
		return _energy;
	}

	@Override
	public int getMaxEnergyStored(EnumFacing from) {
		return _energyMax;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {

		return capability == CapabilityEnergy.ENERGY || super.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {

		if (capability == CapabilityEnergy.ENERGY) {
			return CapabilityEnergy.ENERGY.cast(new net.minecraftforge.energy.IEnergyStorage() {

				@Override
				public int
				receiveEnergy(int maxReceive, boolean simulate) {

					return 0;
				}

				@Override
				public int extractEnergy(int maxExtract, boolean simulate) {

					return TileEntityFactoryGenerator.this.extractEnergy(facing, maxExtract, simulate);
				}

				@Override
				public int getEnergyStored() {

					return TileEntityFactoryGenerator.this.getEnergyStored(facing);
				}

				@Override
				public int getMaxEnergyStored() {

					return TileEntityFactoryGenerator.this.getMaxEnergyStored(facing);
				}

				@Override
				public boolean canExtract() {

					return true;
				}

				@Override
				public boolean canReceive() {

					return false;
				}
			});
		}

		return super.getCapability(capability, facing);
	}
}
