package powercrystals.minefactoryreloaded.tile.rednet;

import static powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryPowered.energyPerEU;
import static powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryPowered.energyPerMJ;
import static powercrystals.minefactoryreloaded.tile.rednet.RedstoneEnergyNetwork.TRANSFER_RATE;

import buildcraft.api.power.IPowerEmitter;
import buildcraft.api.power.IPowerReceptor;
import buildcraft.api.power.PowerHandler.PowerReceiver;
import buildcraft.api.power.PowerHandler.Type;

import cofh.api.energy.IEnergyHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergySource;
import ic2.api.energy.tile.IEnergyTile;

import java.util.Arrays;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;

import powercrystals.core.net.PacketWrapper;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.net.GridTickHandler;
import powercrystals.minefactoryreloaded.net.Packets;

public class TileEntityRedNetEnergy extends TileEntityRedNetCable implements
						IPowerEmitter, IEnergySink, IEnergyHandler//, IEnergyInfo
{
	private byte[] sideMode = {1,1, 1,1,1,1, 0};
	private IEnergyHandler[] handlerCache = null;
	private IPowerReceptor[] receiverCache = null;
	private IEnergySource[] sourceCache = null;
	private IEnergySink[] sinkCache = null;
	private boolean deadCache = false;
	
	int energyForGrid = 0;
	boolean isNode = false;
	
	RedstoneEnergyNetwork grid;
	
	public TileEntityRedNetEnergy() {}
	
	@Override
	public void validate() {
		super.validate();
		deadCache = true;
		handlerCache = null;
		receiverCache = null;
		sourceCache = null;
		sinkCache = null;
		GridTickHandler.addConduit(this);
	}
	
	@Override
	public void onChunkUnload() { super.onChunkUnload();
		if (grid != null) {
			grid.removeConduit(this);
			grid.storage.modifyEnergyStored(-energyForGrid);
			int c = 0;
			for (int i = 6; i --> 0; )
				if ((sideMode[i] >> 1) == 4)
					++c;
			if (c > 1)
				grid.regenerate();
		}
	}
	
	private void reCache() {
		if (deadCache) {
			for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS)
				onNeighborTileChange(xCoord + dir.offsetX,
						yCoord + dir.offsetY, zCoord + dir.offsetZ);
			deadCache = false;
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
		updateInternalTypes();
	}

	public void firstTick() {
		if (worldObj == null) return;
		reCache();
		if (grid == null) {
			for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
				TileEntity tile = worldObj.getBlockTileEntity(xCoord + dir.offsetX,
						yCoord + dir.offsetY, zCoord + dir.offsetZ);
				if (tile instanceof TileEntityRedNetEnergy && ((TileEntityRedNetEnergy)tile).grid != null) {
					setGrid(((TileEntityRedNetEnergy)tile).grid);
					break;
				}
			}
			onInventoryChanged();
		}
		if (grid != null) {
			grid.addConduit(this);
			for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
				TileEntity tile = worldObj.getBlockTileEntity(xCoord + dir.offsetX,
						yCoord + dir.offsetY, zCoord + dir.offsetZ);
				if (tile instanceof TileEntityRedNetEnergy)
					grid.addConduit((TileEntityRedNetEnergy)tile);
			}
		} else if (!worldObj.isRemote)
			setGrid(new RedstoneEnergyNetwork(this)); 
	}
	
	@Override
	public void onNeighboorChanged() { super.onNeighboorChanged();
		deadCache = true;
		reCache();
		/* multipart doesn't issue a tile change event
		 * so when an IEnergyHandler part is removed
		 * the multipart will NPE when it receives energy
		 */
	}
	
	@Override
	public void onNeighborTileChange(int x, int y, int z) {
		TileEntity tile = worldObj.getBlockTileEntity(x, y, z);
		
		if (x < xCoord)
			addCache(tile, 5);
		else if (x > xCoord)
			addCache(tile, 4);
		else if (z < zCoord)
			addCache(tile, 3);
		else if (z > zCoord)
			addCache(tile, 2);
		else if (y < yCoord)
			addCache(tile, 1);
		else if (y > yCoord)
			addCache(tile, 0);
	}
	
	private void addCache(TileEntity tile, int side) {
		if (handlerCache != null)
			handlerCache[side] = null;
		if (receiverCache != null)
			receiverCache[side] = null;
		if (sourceCache != null)
			sourceCache[side] = null;
		if (sinkCache != null)
			sinkCache[side] = null;
		int lastMode = sideMode[side];
		sideMode[side] &= 1;
		if (tile instanceof TileEntityRedNetEnergy) {
			if (((TileEntityRedNetEnergy)tile).canInterface(this)) {
				sideMode[side] = (4 << 1) | 1; // always enable
				if (((TileEntityRedNetEnergy)tile).grid == null)
					((TileEntityRedNetEnergy)tile).setGrid(grid);
			}
		} else if (tile instanceof IEnergyHandler) {
			if (((IEnergyHandler)tile).canInterface(ForgeDirection.VALID_DIRECTIONS[side])) {
				if (handlerCache == null) handlerCache = new IEnergyHandler[6];
				handlerCache[side] = (IEnergyHandler)tile;
				sideMode[side] |= 1 << 1;
			}
		} else if (tile instanceof IEnergyTile) {
			ForgeDirection fSide = ForgeDirection.VALID_DIRECTIONS[side];
			if (tile instanceof IEnergySource && ((IEnergySource)tile).emitsEnergyTo(this, fSide)) {
				if (sourceCache == null) sourceCache = new IEnergySource[6];
				sourceCache[side] = (IEnergySource)tile;
				sideMode[side] |= 3 << 1;
			}
			if (tile instanceof IEnergySink && ((IEnergySink)tile).acceptsEnergyFrom(this, fSide)) {
				if (sinkCache == null) sinkCache = new IEnergySink[6];
				sinkCache[side] = (IEnergySink)tile;
				sideMode[side] |= 3 << 1;
			}
		} else if (tile instanceof IPowerReceptor) {
			PowerReceiver pp = ((IPowerReceptor)tile).
					getPowerReceiver(ForgeDirection.VALID_DIRECTIONS[side]);
			if (pp != null) {
				if (receiverCache == null) receiverCache = new IPowerReceptor[6];
				receiverCache[side] = (IPowerReceptor)tile;
				sideMode[side] |= 2 << 1;
			}
		}
		if (!deadCache) {
			updateInternalTypes();
			if (lastMode != sideMode[side])
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
	}
	
	@Override
	public Packet getDescriptionPacket()
	{
		if (deadCache)
			return null;
		return PacketWrapper.createPacket(MineFactoryReloadedCore.modNetworkChannel,
				Packets.EnergyCableDescription, new Object[]
				{
					xCoord, yCoord, zCoord,
					_sideColors[0], _sideColors[1], _sideColors[2],
					_sideColors[3], _sideColors[4], _sideColors[5],
					_mode, sideMode[0], sideMode[1], sideMode[2], 
					sideMode[3], sideMode[4], sideMode[5] 
				});
	}
	
	@SideOnly(Side.CLIENT)
	public void setModes(byte[] modes) {
		sideMode = modes;
	}
	
	// IEnergyHandler

	@Override
	public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
		if ((sideMode[from.ordinal()] & 1) != 0 & grid != null)
			return grid.storage.receiveEnergy(maxReceive, simulate);
		return 0;
	}

	@Override
	public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
		return 0;
	}

	@Override
	public boolean canInterface(ForgeDirection from) {
		return (sideMode[from.ordinal()] & 1) != 0 & grid != null;
	}

	@Override
	public int getEnergyStored(ForgeDirection from) {
		if ((sideMode[from.ordinal()] & 1) != 0 & grid != null)
			return grid.storage.getEnergyStored();
		return 0;
	}

	@Override
	public int getMaxEnergyStored(ForgeDirection from) {
		if ((sideMode[from.ordinal()] & 1) != 0 & grid != null)
			return grid.storage.getMaxEnergyStored();
		return 0;
	}
	
	// IPowerEmitter

	@Override
	public boolean canEmitPowerFrom(ForgeDirection side) {
		return canInterface(side);
	}
	
	// IEnergySink

	@Override
	public boolean acceptsEnergyFrom(TileEntity emitter, ForgeDirection direction) {
		return canInterface(direction);
	}

	@Override
	public double demandedEnergyUnits() {
		return RedstoneEnergyNetwork.TRANSFER_RATE / energyPerEU;
	}

	@Override
	public double injectEnergyUnits(ForgeDirection from, double amount) {
		if ((sideMode[from.ordinal()] & 1) != 0) {
			int r = (int)(amount * energyPerEU);
			return amount - (grid.storage.receiveEnergy(r, false) / (float)energyPerEU);
		}
		return amount;
	}

	@Override
	public int getMaxSafeInput() {
		return Integer.MAX_VALUE;
	}
	
	// internal
	
	public boolean isInterfacing(ForgeDirection to) {
		int bSide = to.getOpposite().ordinal();
		int mode = sideMode[bSide] >> 1;
		return (sideMode[bSide] & 1) != 0 & mode != 0;
	}
	
	public int interfaceMode(ForgeDirection to) {
		int bSide = to.getOpposite().ordinal();
		int mode = sideMode[bSide] >> 1;
		return (sideMode[bSide] & 1) != 0 ? mode : 0;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) { super.readFromNBT(nbt);
		sideMode = nbt.getByteArray("SideMode");
		if (sideMode.length != 7)
			sideMode = new byte[]{1,1, 1,1,1,1, 0};
		energyForGrid = nbt.getInteger("Energy");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) { super.writeToNBT(nbt);
		nbt.setByteArray("SideMode", sideMode);
		if (grid != null) {
			if (isNode) {
				energyForGrid = grid.getNodeShare(this);
				nbt.setInteger("Energy", energyForGrid);
			}
		} else if (energyForGrid > 0)
			nbt.setInteger("Energy", energyForGrid);
		else
			energyForGrid = 0;
	}
	
	void extract(ForgeDirection side) {
		if (deadCache) return;
		int bSide = side.ordinal();
		if ((sideMode[bSide] & 1) != 0) {
			switch (sideMode[bSide] >> 1) {
			case 1: // IEnergyHandler
				/*
				IEnergyHandler handlerTile = handlerCache[bSide];
				if (handlerTile != null)
				{
					int e = handlerTile.extractEnergy(side, TRANSFER_RATE, true);
					if (e > 0)
						handlerTile.extractEnergy(side, grid.storage.receiveEnergy(e, false), false);
				}//*/
				break;
			case 2: // IPowerReceptor
				// can not extract from IPowerEmitter either
				break;
			case 3: // IEnergyTile
				if (sourceCache != null)
				{
					int e = Math.min((int)(sourceCache[bSide].getOfferedEnergy() * energyPerEU), TRANSFER_RATE);
					if (e > 0) {
						e = grid.storage.receiveEnergy(e, false);
						if (e > 0)
							sourceCache[bSide].drawEnergy(e / (float)energyPerEU);
					}
				}
				break;
			case 4: // TileEntityRednetCable
			case 0: // no mode
				// no-op
				break;
			}
		}
	}
	
	int transfer(ForgeDirection side, int energy) {
		if (deadCache) return 0;
		int bSide = side.ordinal();
		if ((sideMode[bSide] & 1) != 0) {
			switch (sideMode[bSide] >> 1) {
			case 1: // IEnergyHandler
				IEnergyHandler handlerTile = handlerCache[bSide];
				if (handlerTile != null)
					return handlerTile.receiveEnergy(side, energy, false);
				break;
			case 2: // IPowerReceptor
				IPowerReceptor receiverTile = receiverCache[bSide];
				PowerReceiver pp = null;
				if (receiverTile != null)
					pp = receiverTile.getPowerReceiver(side);
				
				if (pp != null) {
					float max = pp.getMaxEnergyReceived();
					float powerToSend = Math.min(max, pp.getMaxEnergyStored() - pp.getEnergyStored());
					if (powerToSend > 0) {
						powerToSend = Math.min(energy / (float)energyPerMJ, powerToSend);
						return (int)Math.ceil(pp.receiveEnergy(Type.PIPE, powerToSend, side) * energyPerMJ);
					}
				}
				break;
			case 3: // IEnergyTile
				if (sinkCache != null) {
					IEnergySink sink = sinkCache[bSide];
					int e = (int)Math.min(sink.getMaxSafeInput() * (long)energyPerEU, energy);
					e = Math.min((int)(sink.demandedEnergyUnits() * energyPerEU), e);
					if (e > 0) {
						e -= (int)Math.ceil(sink.injectEnergyUnits(side, e / (float)energyPerEU) * energyPerEU);
						return e;
					}
				}
				break;
			case 4: // TileEntityRednetCable
			case 0: // no mode
				// no-op
				break;
			}
		}
		return 0;
	}

	void setGrid(RedstoneEnergyNetwork newGrid) {
		grid = newGrid;
	}
	
	public void updateInternalTypes() {
		if (deadCache) return;
		isNode = false;
		for (int i = 0; i < 6; i++) {
			int mode = sideMode[i] >> 1;
			if ((sideMode[i] & 1) != 0 & mode != 0 & mode != 4) {
				isNode = true;
			}
		}
		if (grid != null)
			grid.addConduit(this);
	}

	public void getTileInfo(List<String> info, ForgeDirection side, EntityPlayer player, boolean debug) {
		if (grid != null) {
			info.add("Grid:" + grid);
			info.add("Conduits: " + grid.getConduitCount() + ", Nodes: " + grid.getNodeCount());
			info.add("Grid Max: " + grid.storage.getMaxEnergyStored());
			info.add("Grid Cur: " + grid.storage.getEnergyStored());
		} else {
			info.add("Null Grid");
		}
		info.add("SideType: " + Arrays.toString(sideMode));
		info.add("Node: " + isNode);
		if (debug) {
			//grid.regenerate();
			return;
		}
	}
	
	@Override
	public String toString() {
		return "(x="+xCoord+",y="+yCoord+",z="+zCoord+")@"+hashCode();
	}
}
