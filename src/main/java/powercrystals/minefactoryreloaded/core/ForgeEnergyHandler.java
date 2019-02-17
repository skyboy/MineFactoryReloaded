package powercrystals.minefactoryreloaded.core;

import cofh.redstoneflux.api.IEnergyHandler;
import cofh.redstoneflux.api.IEnergyProvider;
import cofh.redstoneflux.api.IEnergyReceiver;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public abstract class ForgeEnergyHandler implements IEnergyHandler {

	private TileEntity tile;

	ForgeEnergyHandler(TileEntity tile) {

		this.tile = tile;
	}

	IEnergyStorage getStorage(EnumFacing from) {

		if (tile.hasCapability(CapabilityEnergy.ENERGY, from)) {
			return tile.getCapability(CapabilityEnergy.ENERGY, from);
		}
		return null;
	}

	@Override
	public int getEnergyStored(EnumFacing from) {

		IEnergyStorage storage = getStorage(from);
		return storage == null ? 0 : storage.getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored(EnumFacing from) {

		IEnergyStorage storage = getStorage(from);
		return storage == null ? 0 : storage.getMaxEnergyStored();
	}

	@Override
	public boolean canConnectEnergy(EnumFacing from) {

		return true;
	}

	public static class Receiver extends ForgeEnergyHandler implements IEnergyReceiver {

		public Receiver(TileEntity tile) {

			super(tile);
		}

		@Override
		public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {

			IEnergyStorage storage = getStorage(from);
			return storage == null ? 0 : storage.receiveEnergy(maxReceive, simulate);
		}
	}

	public static class Provider extends ForgeEnergyHandler implements IEnergyProvider {

		public Provider(TileEntity tile) {

			super(tile);
		}

		@Override
		public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate) {

			IEnergyStorage storage = getStorage(from);
			return storage == null ? 0 : storage.extractEnergy(maxExtract, simulate);
		}
	}
}
