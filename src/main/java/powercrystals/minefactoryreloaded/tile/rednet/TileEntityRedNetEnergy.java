package powercrystals.minefactoryreloaded.tile.rednet;

import appeng.api.implementations.tiles.ICrankable;
import codechicken.lib.raytracer.IndexedCuboid6;
import codechicken.lib.vec.Vector3;
import cofh.core.util.helpers.EnergyHelper;
import cofh.redstoneflux.api.IEnergyConnection;
import cofh.redstoneflux.api.IEnergyProvider;
import cofh.redstoneflux.api.IEnergyReceiver;
import cofh.redstoneflux.impl.EnergyStorage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import powercrystals.minefactoryreloaded.api.rednet.connectivity.RedNetConnectionType;
import powercrystals.minefactoryreloaded.core.ForgeEnergyHandler;
import powercrystals.minefactoryreloaded.core.IGridController;
import powercrystals.minefactoryreloaded.core.MFRUtil;
import powercrystals.minefactoryreloaded.modhelpers.Compats;
import powercrystals.minefactoryreloaded.net.Packets;

import java.util.Arrays;
import java.util.List;

import static powercrystals.minefactoryreloaded.block.transport.BlockRedNetCable.subSelection;
import static powercrystals.minefactoryreloaded.tile.rednet.RedstoneEnergyNetwork.TRANSFER_RATE;

@Optional.Interface(iface = "appeng.api.implementations.tiles.ICrankable", modid = Compats.ModIds.APP_ENG)
public class TileEntityRedNetEnergy extends TileEntityRedNetCable implements
																  IEnergyReceiver, IEnergyProvider, ICrankable//, IEnergyInfo
{

	private byte[] sideMode = { 1, 1, 1, 1, 1, 1, 0 };
	private IEnergyReceiver[] receiverCache = null;
	private IEnergyProvider[] providerCache = null;
	private boolean deadCache = false;
	private boolean readFromNBT = false;

	boolean isNode = false;
	int energyForGrid = 0;

	RedstoneEnergyNetwork _grid;

	public TileEntityRedNetEnergy() {

	}

	@Override
	public void validate() {

		super.validate();
		deadCache = true;
		receiverCache = null;
		providerCache = null;
	}

	@Override
	public void onLoad() {

		super.onLoad();

		if (world.isRemote)
			return;
		if (_grid == null) {
			incorporateTiles();
			if (_grid == null) {
				setGrid(new RedstoneEnergyNetwork(this));
			}
		}
		readFromNBT = true;
		reCache();
	}

	@Override
	public void invalidate() {

		if (_grid != null) {
			removeFromGrid();
		}
		super.invalidate();
	}

	@Override
	public void onChunkUnload() {

		if (_grid != null) {
			removeFromGrid();
		}
		super.onChunkUnload();
	}

	private void removeFromGrid() {

		_grid.removeConduit(this);
		_grid.storage.modifyEnergyStored(-energyForGrid);
		markForRegen();
		deadCache = true;
		_grid = null;
	}

	private void markForRegen() {

		int c = 0;
		for (int i = 6; i-- > 0; )
			if ((sideMode[i]) == 9)
				++c;
		if (c > 1)
			_grid.regenerate();
	}

	private void reCache() {

		if (deadCache) {
			for (EnumFacing dir : EnumFacing.VALUES)
				if (world.isBlockLoaded(this.getPos().offset(dir)))
					addCache(MFRUtil.getTile(world, this.getPos().offset(dir)));
			deadCache = false;
			RedstoneEnergyNetwork.HANDLER.addConduitForUpdate(this);
		}
	}

	private void incorporateTiles() {

		if (_grid == null) {
			for (EnumFacing dir : EnumFacing.VALUES) {
				if (readFromNBT && (sideMode[dir.getOpposite().ordinal()] & 1) == 0)
					continue;
				BlockPos offsetPos = pos.offset(dir);
				if (world.isBlockLoaded(offsetPos)) {
					TileEntityRedNetEnergy pipe = MFRUtil.getTile(world, pos.offset(dir), TileEntityRedNetEnergy.class);
					if (pipe != null) {
						if (pipe._grid != null && pipe.canInterface(this, dir)) {
							pipe._grid.addConduit(this);
						}
					}
				}
			}
		}
	}

	public boolean canInterface(TileEntityRedNetEnergy with, EnumFacing dir) {

		return (sideMode[dir.ordinal()] & 1) != 0;
	}

	@Override
	public void onNeighborTileChange(BlockPos neighborPos) {

		if (world.isRemote | deadCache)
			return;
		TileEntity tile = world.isBlockLoaded(neighborPos) ? world.getTileEntity(neighborPos) : null;

		Vec3i diff = neighborPos.subtract(pos);
		addCache(tile, EnumFacing.getFacingFromVector(diff.getX(), diff.getY(), diff.getZ()));
	}

	private void addCache(TileEntity tile) {

		if (tile == null)
			return;
		Vec3i diff = tile.getPos().subtract(pos);
		addCache(tile, EnumFacing.getFacingFromVector(diff.getX(), diff.getY(), diff.getZ()));
	}

	private void addCache(TileEntity tile, EnumFacing side) {

		if (receiverCache != null)
			receiverCache[side.ordinal()] = null;
		if (providerCache != null)
			providerCache[side.ordinal()] = null;
		int lastMode = sideMode[side.ordinal()];
		sideMode[side.ordinal()] &= 1;
		if (tile instanceof TileEntityRedNetEnergy) {
			TileEntityRedNetEnergy cable = ((TileEntityRedNetEnergy) tile);
			sideMode[side.ordinal()] |= (4 << 1);
			if (cable.canInterface(this, side.getOpposite())) {
				if (_grid == null && cable._grid != null) {
					cable._grid.addConduit(this);
				}
				if (cable._grid == _grid || _grid.addConduit(cable)) {
					sideMode[side.ordinal()] |= 1; // always enable
				}
			} else {
				sideMode[side.ordinal()] &= ~1;
			}
		} else if (tile instanceof IEnergyConnection) {
			if (((IEnergyConnection) tile).canConnectEnergy(side)) {
				sideMode[side.ordinal()] |= 1 << 1;
				if (tile instanceof IEnergyReceiver) {
					if (receiverCache == null)
						receiverCache = new IEnergyReceiver[6];
					receiverCache[side.ordinal()] = (IEnergyReceiver) tile;
				}
				if (tile instanceof IEnergyProvider) {
					if (providerCache == null)
						providerCache = new IEnergyProvider[6];
					providerCache[side.ordinal()] = (IEnergyProvider) tile;
				}
			} else if (EnergyHelper.isEnergyHandler(tile, side)) {
				IEnergyStorage energyCap = tile.getCapability(CapabilityEnergy.ENERGY, side);
				if (energyCap.canReceive()) {
					if (receiverCache == null)
						receiverCache = new IEnergyReceiver[6];
					receiverCache[side.ordinal()] = new ForgeEnergyHandler.Receiver(tile);
				} else if (energyCap.canExtract()) {
					if (providerCache == null)
						providerCache = new IEnergyProvider[6];
					providerCache[side.ordinal()] = new ForgeEnergyHandler.Provider(tile);
				}
			}

		}
		if (!deadCache) {
			if (lastMode != sideMode[side.ordinal()]) {
				RedstoneEnergyNetwork.HANDLER.addConduitForUpdate(this);
				MFRUtil.notifyBlockUpdate(world, pos);
			}
		}
	}

	@Override
	protected NBTTagCompound writePacketData(NBTTagCompound tag) {

		tag.setInteger("mode[2]", (sideMode[0] & 0xFF) | ((sideMode[1] & 0xFF) << 8) | ((sideMode[2] & 0xFF) << 16) |
				((sideMode[3] & 0xFF) << 24));
		tag.setInteger("mode[3]", (sideMode[4] & 0xFF) | ((sideMode[5] & 0xFF) << 8) | ((sideMode[6] & 0xFF) << 16));

		return super.writePacketData(tag);
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {

		if (deadCache)
			return null;

		return super.getUpdatePacket();
	}

	@Override
	protected void handlePacketData(NBTTagCompound tag) {

		super.handlePacketData(tag);

		int mode = tag.getInteger("mode[2]");
		sideMode[0] = (byte) (mode & 0xFF);
		sideMode[1] = (byte) ((mode >> 8) & 0xFF);
		sideMode[2] = (byte) ((mode >> 16) & 0xFF);
		sideMode[3] = (byte) ((mode >> 24) & 0xFF);
		mode = tag.getInteger("mode[3]");
		sideMode[4] = (byte) (mode & 0xFF);
		sideMode[5] = (byte) ((mode >> 8) & 0xFF);
		sideMode[6] = (byte) ((mode >> 16) & 0xFF);
	}

	@SideOnly(Side.CLIENT)
	public void setModes(byte[] modes) {

		sideMode = modes;
	}

	// IEnergyHandler

	@Override
	public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {

		if (from == null)
			return 0;
		if ((sideMode[from.ordinal() ^ 1] & 1) != 0 & _grid != null)
			return _grid.storage.receiveEnergy(maxReceive, simulate);
		return 0;
	}

	@Override
	public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate) {

		return 0;
	}

	@Override
	public boolean canConnectEnergy(EnumFacing from) {

		if (from == null)
			return false;
		return (sideMode[from.ordinal() ^ 1] & 1) != 0 & _grid != null;
	}

	@Override
	public int getEnergyStored(EnumFacing from) {

		if (from == null)
			return 0;
		if ((sideMode[from.ordinal() ^ 1] & 1) != 0 & _grid != null)
			return _grid.storage.getEnergyStored();
		return 0;
	}

	@Override
	public int getMaxEnergyStored(EnumFacing from) {

		if (from == null)
			return 0;
		if ((sideMode[from.ordinal() ^ 1] & 1) != 0 & _grid != null)
			return _grid.storage.getMaxEnergyStored();
		return 0;
	}

	// ICrankable

	@Optional.Method(modid = Compats.ModIds.APP_ENG)
	@Override
	public boolean canTurn() {

		return _grid.storage.getEnergyStored() < _grid.storage.getMaxEnergyStored();
	}

	@Optional.Method(modid = Compats.ModIds.APP_ENG)
	@Override
	public void applyTurn() {

		_grid.storage.receiveEnergy(90, false);
	}

	@Optional.Method(modid = Compats.ModIds.APP_ENG)
	@Override
	public boolean canCrankAttach(EnumFacing directionToCrank) {

		return true;
	}

	// internal

	public boolean isInterfacing(EnumFacing to) {

		int bSide = to.getOpposite().ordinal();
		int mode = sideMode[bSide] >> 1;
		return (sideMode[bSide] & 1) != 0 & mode != 0;
	}

	public int interfaceMode(EnumFacing to) {

		int bSide = to.getOpposite().ordinal();
		int mode = sideMode[bSide] >> 1;
		return (sideMode[bSide] & 1) != 0 ? mode : 0;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);
		sideMode = nbt.getByteArray("SideMode");
		if (sideMode.length != 7)
			sideMode = new byte[] { 1, 1, 1, 1, 1, 1, 0 };
		energyForGrid = nbt.getInteger("Energy");
		readFromNBT = true;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		nbt = super.writeToNBT(nbt);
		nbt.setByteArray("SideMode", sideMode);
		if (_grid != null) {
			if (isNode) {
				energyForGrid = _grid.getNodeShare(this);
			}
		}
		if (energyForGrid > 0)
			nbt.setInteger("Energy", energyForGrid);
		else
			energyForGrid = 0;
		return nbt;
	}

	void extract(EnumFacing side, EnergyStorage storage) {

		if (deadCache)
			return;
		int bSide = side.ordinal();
		if ((sideMode[bSide] & 1) != 0) {
			switch (sideMode[bSide] >> 1) {
			case 1: // IEnergyHandler
				if (providerCache != null) {
					IEnergyProvider handlerTile = providerCache[bSide];
					if (handlerTile != null) {
						int e = handlerTile.extractEnergy(side, TRANSFER_RATE, true);
						if (e > 0)
							handlerTile.extractEnergy(side, storage.receiveEnergy(e, false), false);
					}
				}
				break;
			case 3: // IEnergyTile
				break;
			case 4: // TileEntityRednetCable
			case 0: // no mode
				// no-op
				break;
			}
		}
	}

	int transfer(EnumFacing side, int energy) {

		if (deadCache)
			return 0;
		int bSide = side.ordinal();
		if ((sideMode[bSide] & 1) != 0) {
			switch (sideMode[bSide] >> 1) {
			case 1: // IEnergyHandler
				if (receiverCache != null) {
					IEnergyReceiver handlerTile = receiverCache[bSide];
					if (handlerTile != null)
						return handlerTile.receiveEnergy(side, energy, false);
				}
				break;
			case 3: // IEnergyTile
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

		_grid = newGrid;
	}

	@Override
	public void updateInternalTypes(IGridController grid) {

		super.updateInternalTypes(grid);
		if (deadCache)
			return;
		if (grid != RedstoneEnergyNetwork.HANDLER)
			return;
		isNode = false;
		for (int i = 0; i < 6; i++) {
			int mode = sideMode[i] >> 1;
			if (((sideMode[i] & 1) != 0) & (mode != 0) & (mode != 4)) {
				isNode = true;
			}
		}
		if (_grid != null)
			_grid.addConduit(this);
		Packets.sendToAllPlayersWatching(this);
	}

	@Override
	public boolean onPartHit(EntityPlayer player, EnumHand hand, int subSide, int subHit) {

		if (subHit >= (2 + 6 * 4) && subHit < (2 + 6 * 6)) {
			if (MFRUtil.isHoldingUsableTool(player, hand, pos, subSide < 6 ? EnumFacing.VALUES[subSide] : EnumFacing.UP)) {
				if (!player.world.isRemote) {
					int dir = subSide < 6 ? EnumFacing.VALUES[subSide].getOpposite().ordinal() : 6;
					if (sideMode[dir] == 9) {
						removeFromGrid();
					}
					sideMode[dir] ^= 1;
					markDirty();
					if (sideMode[dir] == 8) {
						deadCache = true;
						reCache();
						if (_grid == null)
							setGrid(new RedstoneEnergyNetwork(this));
					}
					RedstoneEnergyNetwork.HANDLER.addConduitForUpdate(this);
					Packets.sendToAllPlayersWatching(this);
					MFRUtil.notifyBlockUpdate(world, pos);
				}
				return true;
			}
		}
		return false;
	}

	@Override
	public void addTraceableCuboids(List<IndexedCuboid6> list, boolean forTrace, boolean hasTool, boolean offsetCuboids) {

		Vector3 offset = offsetCuboids ? Vector3.fromBlockPos(pos) : new Vector3(0, 0, 0);

		IndexedCuboid6 main = new IndexedCuboid6(0, subSelection[1]); // main body
		list.add(main);

		EnumFacing[] side = EnumFacing.VALUES;
		for (int i = side.length; i-- > 0; ) {
			RedNetConnectionType c = getConnectionState(side[i], true);
			int mode = sideMode[i];
			boolean iface = mode > 1 & ((mode & 1) == 1 | hasTool);
			int o = 2 + i, k = o;
			l:
			if (c.isConnected) {
				if (c.isPlate)
					o += 6;
				else if (c.isCable)
					if (c.isAllSubnets) {
						if (!iface | !hasTool) {
							o = 2 + 6 * 3 + i;
						}
						list.add((IndexedCuboid6) new IndexedCuboid6(hasTool ? k + 6 * 6 : 1, subSelection[o])
								.add(offset)); // cable part or connection point
						break l;
					}
				k = 2 + 6 * 3 + i;
				if (iface)
					k += forTrace ? 6 : 12;
				// cable part or energy selection box
				list.add((IndexedCuboid6) new IndexedCuboid6(iface & hasTool ? k : 1, subSelection[k]).add(offset));
				list.add((IndexedCuboid6) new IndexedCuboid6(o, subSelection[o]).add(offset)); // connection point
				o = 2 + 6 * 2 + i;
				if (c.isSingleSubnet) // color band
					list.add((IndexedCuboid6) new IndexedCuboid6(o, subSelection[o]).add(offset));
				iface = false;
			} else if (forTrace & hasTool & _cableMode[6] != 1 &&
					(c = getConnectionState(side[i], false)).isConnected) { // cable-only
				if (c.isAllSubnets & c.isCable) {
					if (!iface) {
						k += 6 * 3;
					}
					o += 6 * 6;
				}
				list.add((IndexedCuboid6) new IndexedCuboid6(o, subSelection[k]).add(offset)); // connection point (raytrace)
			}
			if (iface) {
				o = 2 + 6 * 5 + i;
				list.add((IndexedCuboid6) new IndexedCuboid6(hasTool ? o : 1, subSelection[o]).add(offset));
			}
		}
		main.add(offset);
	}

	@Override
	public void getTileInfo(List<ITextComponent> info, EnumFacing side, EntityPlayer player, boolean debug) {

		info.add(text("-Redstone-"));
		super.getTileInfo(info, side, player, debug && player.isSneaking());
		info.add(text("-Energy-"));
		if (_grid != null) {/* TODO: advanced monitoring
							if (isNode) {
							info.add("Throughput All: " + _grid.distribution);
							info.add("Throughput Side: " + _grid.distributionSide);
							} else//*/
			if (!debug) {
				float sat = 0;
				if (_grid.getNodeCount() != 0)
					sat = (float) (Math.ceil(_grid.storage.getEnergyStored() /
							(float) _grid.storage.getMaxEnergyStored() * 1000f) / 10f);
				info.add(text("Saturation: " + sat));
			}
		} else if (!debug)
			info.add(text("Null Grid"));
		if (debug) {
			if (_grid != null) {
				info.add(text("Grid:" + _grid));
				info.add(text("Conduits: " + _grid.getConduitCount() + ", Nodes: " + _grid.getNodeCount()));
				info.add(text("Grid Max: " + _grid.storage.getMaxEnergyStored() + ", Grid Cur: " +
						_grid.storage.getEnergyStored()));
				info.add(text("Caches: (RF):({" + Arrays.toString(receiverCache) + "," +
						Arrays.toString(providerCache) + "})"));
			} else {
				info.add(text("Null Grid"));
			}
			info.add(text("SideType: " + Arrays.toString(sideMode)));
			info.add(text("Node: " + isNode + ", Energy: " + energyForGrid));
			return;
		}
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

					return TileEntityRedNetEnergy.this.receiveEnergy(facing, maxReceive, simulate);
				}

				@Override
				public int extractEnergy(int maxExtract, boolean simulate) {

					return TileEntityRedNetEnergy.this.extractEnergy(facing, maxExtract, simulate);
				}

				@Override
				public int getEnergyStored() {

					return TileEntityRedNetEnergy.this.getEnergyStored(facing);
				}

				@Override
				public int getMaxEnergyStored() {

					return TileEntityRedNetEnergy.this.getMaxEnergyStored(facing);
				}

				@Override
				public boolean canExtract() {

					return TileEntityRedNetEnergy.this.canConnectEnergy(facing);
				}

				@Override
				public boolean canReceive() {

					return TileEntityRedNetEnergy.this.canConnectEnergy(facing);
				}
			});
		}

		return super.getCapability(capability, facing);
	}
}
