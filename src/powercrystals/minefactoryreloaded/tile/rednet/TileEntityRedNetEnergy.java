package powercrystals.minefactoryreloaded.tile.rednet;

import static powercrystals.minefactoryreloaded.block.transport.BlockRedNetCable.subSelection;
import static powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryPowered.energyPerEU;
import static powercrystals.minefactoryreloaded.tile.rednet.RedstoneEnergyNetwork.TRANSFER_RATE;

import appeng.api.implementations.tiles.ICrankable;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyConnection;
import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import cofh.api.energy.IEnergyTransport;
import cofh.asm.relauncher.Strippable;
import cofh.lib.util.position.BlockPosition;
import cofh.repack.codechicken.lib.raytracer.IndexedCuboid6;
import cofh.repack.codechicken.lib.vec.Vector3;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import ic2.api.energy.EnergyNet;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergySource;
import ic2.api.energy.tile.IEnergyTile;

import java.util.Arrays;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.common.util.ForgeDirection;

import powercrystals.minefactoryreloaded.api.rednet.connectivity.RedNetConnectionType;
import powercrystals.minefactoryreloaded.core.IGridController;
import powercrystals.minefactoryreloaded.core.MFRUtil;
import powercrystals.minefactoryreloaded.net.Packets;

@Strippable("appeng.api.implementations.tiles.ICrankable")
public class TileEntityRedNetEnergy extends TileEntityRedNetCable implements
																	IEnergyTransport, ICrankable//, IEnergyInfo
{

	private static boolean IC2Classes = false, IC2Net = false;

	static {
		try {
			Class.forName("ic2.api.energy.tile.IEnergySource");
			Class.forName("ic2.api.energy.tile.IEnergySink");
			IC2Classes = true;
			Class.forName("ic2.api.energy.EnergyNet");
			IC2Net = true;
		} catch (Throwable _) {
		}
	}

	private byte[] sideMode = { 1, 1, 1, 1, 1, 1, 0 };
	private InterfaceType[] transportTypes = null;
	private IEnergyReceiver[] receiverCache = null;
	private IEnergyProvider[] providerCache = null;
	private IC2Cache ic2Cache = null;
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
		ic2Cache = null;
		if (worldObj.isRemote)
			return;
	}

	@Override
	public void invalidate() {

		if (_grid != null) {
			removeFromGrid();
		}
		super.invalidate();
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
		for (int i = 6; i-- > 0;)
			if ((sideMode[i]) == 9)
				++c;
		if (c > 1)
			_grid.regenerate();
	}

	private void reCache() {

		if (deadCache) {
			for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS)
				if (BlockPosition.blockExists(this, dir))
					addCache(BlockPosition.getAdjacentTileEntity(this, dir));
			deadCache = false;
			RedstoneEnergyNetwork.HANDLER.addConduitForUpdate(this);
		}
	}

	@Override
	public void cofh_validate() {

		super.cofh_validate();
		if (worldObj.isRemote) return;
		if (_grid == null) {
			incorporateTiles();
			if (_grid == null) {
				setGrid(new RedstoneEnergyNetwork(this));
			}
		}
		readFromNBT = true;
		reCache();
		markDirty();
		Packets.sendToAllPlayersWatching(this);
	}

	private void incorporateTiles() {

		if (_grid == null) {
			for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
				if (readFromNBT && (sideMode[dir.getOpposite().ordinal()] & 1) == 0) continue;
				if (BlockPosition.blockExists(this, dir)) {
					TileEntityRedNetEnergy pipe = BlockPosition.getAdjacentTileEntity(this, dir, TileEntityRedNetEnergy.class);
					if (pipe != null) {
						if (pipe._grid != null && pipe.canInterface(this, dir)) {
							pipe._grid.addConduit(this);
						}
					}
				}
			}
		}
	}

	public boolean canInterface(TileEntityRedNetEnergy with, ForgeDirection dir) {

		return (sideMode[dir.ordinal()] & 1) != 0;
	}

	@Override
	public void onNeighborTileChange(int x, int y, int z) {

		if (worldObj.isRemote | deadCache)
			return;
		TileEntity tile = worldObj.blockExists(x, y, z) ? worldObj.getTileEntity(x, y, z) : null;

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

	private void addCache(TileEntity tile) {

		if (tile == null) return;
		int x = tile.xCoord, y = tile.yCoord, z = tile.zCoord;

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

		if (receiverCache != null)
			receiverCache[side] = null;
		if (providerCache != null)
			providerCache[side] = null;
		if (ic2Cache != null)
			ic2Cache.erase(side);
		int lastMode = sideMode[side];
		sideMode[side] &= 1;
		if (tile instanceof TileEntityRedNetEnergy) {
			TileEntityRedNetEnergy cable = ((TileEntityRedNetEnergy) tile);
			sideMode[side] |= (4 << 1);
			if (cable.canInterface(this, ForgeDirection.getOrientation(side ^ 1))) {
				if (_grid == null && cable._grid != null) {
					cable._grid.addConduit(this);
				}
				if (cable._grid == _grid || _grid.addConduit(cable)) {
					sideMode[side] |= 1; // always enable
				}
			} else {
				sideMode[side] &= ~1;
			}
		} else if (tile instanceof IEnergyConnection) {
			if (((IEnergyConnection) tile).canConnectEnergy(ForgeDirection.VALID_DIRECTIONS[side])) {
				if (tile instanceof IEnergyTransport) {
					IEnergyTransport transport = (IEnergyTransport) tile;
					InterfaceType type = transport.getTransportState(ForgeDirection.getOrientation(side)).getOpposite();
					if (type != InterfaceType.BALANCE) {
						createTransportTypes();
						transportTypes[side] = type;
					}
					sideMode[side] |= 2 << 1;
				} else {
					sideMode[side] |= 1 << 1;
					if (transportTypes != null) {
						transportTypes[side] = InterfaceType.BALANCE;
					}
				}
				if (tile instanceof IEnergyReceiver) {
					if (receiverCache == null) receiverCache = new IEnergyReceiver[6];
					receiverCache[side] = (IEnergyReceiver) tile;
				}
				if (tile instanceof IEnergyProvider) {
					if (providerCache == null) providerCache = new IEnergyProvider[6];
					providerCache[side] = (IEnergyProvider) tile;
				}
			}
		} else if (checkIC2Tiles(tile, side)) {
			;
		}
		if (!deadCache) {
			if (lastMode != sideMode[side]) {
				RedstoneEnergyNetwork.HANDLER.addConduitForUpdate(this);
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			}
		}
	}

	private void createTransportTypes() {

		transportTypes = new InterfaceType[6];
		for (int i = transportTypes.length; i-- > 0; ) {
			transportTypes[i] = InterfaceType.BALANCE;
		}
	}

	private boolean checkIC2Tiles(TileEntity tile, int side) {

		if (!IC2Classes)
			return false;
		if (ic2Cache == null) ic2Cache = new IC2Cache();
		return ic2Cache.add(tile, side);
	}

	@Override
	public Packet getDescriptionPacket() {

		if (deadCache)
			return null;
		NBTTagCompound data = new NBTTagCompound();
		data.setIntArray("colors", _sideColors);
		data.setInteger("mode[0]", (_cableMode[0] & 0xFF) | ((_cableMode[1] & 0xFF) << 8) | ((_cableMode[2] & 0xFF) << 16) |
				((_cableMode[3] & 0xFF) << 24));
		data.setInteger("mode[1]", (_cableMode[4] & 0xFF) | ((_cableMode[5] & 0xFF) << 8) | ((_cableMode[6] & 0xFF) << 16));
		data.setInteger("mode[2]", (sideMode[0] & 0xFF) | ((sideMode[1] & 0xFF) << 8) | ((sideMode[2] & 0xFF) << 16) |
				((sideMode[3] & 0xFF) << 24));
		data.setInteger("mode[3]", (sideMode[4] & 0xFF) | ((sideMode[5] & 0xFF) << 8) | ((sideMode[6] & 0xFF) << 16));
		data.setInteger("state[0]", _connectionState[0].ordinal() | (_connectionState[1].ordinal() << 4) |
				(_connectionState[2].ordinal() << 8) | (_connectionState[3].ordinal() << 12) |
				(_connectionState[4].ordinal() << 16) | (_connectionState[5].ordinal() << 20));
		S35PacketUpdateTileEntity packet = new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, data);
		return packet;
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {

		super.onDataPacket(net, pkt);
		NBTTagCompound data = pkt.func_148857_g();
		switch (pkt.func_148853_f()) {
		case 0:
			int mode = data.getInteger("mode[2]");
			sideMode[0] = (byte) (mode & 0xFF);
			sideMode[1] = (byte) ((mode >> 8) & 0xFF);
			sideMode[2] = (byte) ((mode >> 16) & 0xFF);
			sideMode[3] = (byte) ((mode >> 24) & 0xFF);
			mode = data.getInteger("mode[3]");
			sideMode[4] = (byte) (mode & 0xFF);
			sideMode[5] = (byte) ((mode >> 8) & 0xFF);
			sideMode[6] = (byte) ((mode >> 16) & 0xFF);
			break;
		}
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	@SideOnly(Side.CLIENT)
	public void setModes(byte[] modes) {

		sideMode = modes;
	}

	// IEnergyHandler

	@Override
	public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {

		if (from == ForgeDirection.UNKNOWN) return 0;
		if ((sideMode[from.ordinal() ^ 1] & 1) != 0 & _grid != null)
			return _grid.storage.receiveEnergy(maxReceive, simulate);
		return 0;
	}

	@Override
	public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {

		return 0;
	}

	@Override
	public boolean canConnectEnergy(ForgeDirection from) {

		if (from == ForgeDirection.UNKNOWN) return false;
		return (sideMode[from.ordinal() ^ 1] & 1) != 0 & _grid != null;
	}

	@Override
	public int getEnergyStored(ForgeDirection from) {

		if (from == ForgeDirection.UNKNOWN) return 0;
		if ((sideMode[from.ordinal() ^ 1] & 1) != 0 & _grid != null)
			return _grid.storage.getEnergyStored();
		return 0;
	}

	@Override
	public int getMaxEnergyStored(ForgeDirection from) {

		if (from == ForgeDirection.UNKNOWN) return 0;
		if ((sideMode[from.ordinal() ^ 1] & 1) != 0 & _grid != null)
			return _grid.storage.getMaxEnergyStored();
		return 0;
	}

	@Override
	public InterfaceType getTransportState(ForgeDirection from) {

		if (transportTypes != null) {
			return transportTypes[from.ordinal() ^ 1];
		}
		return InterfaceType.BALANCE;
	}

	@Override
	public boolean setTransportState(InterfaceType state, ForgeDirection from) {

		if ((sideMode[from.ordinal() ^ 1] >> 1) == 1 || !isInterfacing(from)) {
			return false;
		}
		if (transportTypes == null) {
			createTransportTypes();
		}
		transportTypes[from.ordinal() ^ 1] = state;
		return true;
	}

	// ICrankable

	@Override
	public boolean canTurn() {

		return _grid.storage.getEnergyStored() < _grid.storage.getMaxEnergyStored();
	}

	@Override
	public void applyTurn() {

		_grid.storage.receiveEnergy(90, false);
	}

	@Override
	public boolean canCrankAttach(ForgeDirection directionToCrank) {

		return true;
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
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);
		sideMode = nbt.getByteArray("SideMode");
		if (sideMode.length != 7)
			sideMode = new byte[] { 1, 1, 1, 1, 1, 1, 0 };
		energyForGrid = nbt.getInteger("Energy");
		readFromNBT = true;
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);
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
	}

	void extract(ForgeDirection side, EnergyStorage storage) {

		if (deadCache) return;
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
			case 2: {// IEnergyTransport
				InterfaceType state = getTransportState(side);
				if (providerCache != null && state == InterfaceType.RECEIVE) {
					IEnergyProvider handlerTile = providerCache[bSide];
					if (handlerTile != null) {
						int e = handlerTile.extractEnergy(side, TRANSFER_RATE, true);
						if (e > 0)
							handlerTile.extractEnergy(side, storage.receiveEnergy(e, false), false);
					}
				}
				break;
			}
			case 3: // IEnergyTile
				if (ic2Cache != null)
					ic2Cache.extract(bSide);
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
				if (receiverCache != null) {
					IEnergyReceiver handlerTile = receiverCache[bSide];
					if (handlerTile != null)
						return handlerTile.receiveEnergy(side, energy, false);
				}
				break;
			case 2: {// IEnergyTransport
				InterfaceType state = getTransportState(side);
				if (receiverCache != null && state != InterfaceType.RECEIVE) {
					IEnergyReceiver handlerTile = receiverCache[bSide];
					if (handlerTile != null && (state == InterfaceType.SEND || handlerTile.getEnergyStored(side) < _grid.storage.getEnergyStored()))
						return handlerTile.receiveEnergy(side, energy, false);
				}
				break;
			}
			case 3: // IEnergyTile
				if (ic2Cache != null)
					return ic2Cache.transmit(energy, side, bSide);
				break;
			case 4: // TileEntityRednetCable
			case 0: // no mode
				// no-op
				break;
			}
		}
		return 0;
	}

	private class IC2Cache {

		IEnergySource[] sourceCache = null;
		IEnergySink[] sinkCache = null;

		void erase(int side) {

			if (sourceCache != null)
				sourceCache[side] = null;
			if (sinkCache != null)
				sinkCache[side] = null;
		}

		public boolean add(TileEntity tile, int side) {

			boolean r = false;
			if (tile instanceof IEnergyTile) {
				ForgeDirection fSide = ForgeDirection.VALID_DIRECTIONS[side];
				if (tile instanceof IEnergySource && ((IEnergySource) tile).emitsEnergyTo(TileEntityRedNetEnergy.this, fSide)) {
					if (sourceCache == null) sourceCache = new IEnergySource[6];
					sourceCache[side] = (IEnergySource) tile;
					sideMode[side] |= 3 << 1;
					r = true;
				}
				if (tile instanceof IEnergySink && ((IEnergySink) tile).acceptsEnergyFrom(TileEntityRedNetEnergy.this, fSide)) {
					if (sinkCache == null) sinkCache = new IEnergySink[6];
					sinkCache[side] = (IEnergySink) tile;
					sideMode[side] |= 3 << 1;
					r = true;
				}
			}
			return r;
		}

		void extract(int bSide) {

			if (sourceCache != null) {
				IEnergySource source = sourceCache[bSide];
				if (source == null) return;
				int e = Math.min((int) (source.getOfferedEnergy() * energyPerEU), TRANSFER_RATE);
				if (e > 0) {
					e = _grid.storage.receiveEnergy(e, false);
					if (e > 0)
						source.drawEnergy(e / (float) energyPerEU);
				}
			}
			return;
		}

		int transmit(int energy, ForgeDirection side, int bSide) {

			if (sinkCache != null) {
				IEnergySink sink = sinkCache[bSide];
				if (sink == null) return 0;
				int e = (int) Math.min(getPowerFromTier(sink.getSinkTier()) * energyPerEU, energy);
				e = Math.min((int) (sink.getDemandedEnergy() * energyPerEU), e);
				if (e > 0) {
					float v = e / (float) energyPerEU;
					e -= (int) Math.ceil(sink.injectEnergy(side, v, getPowerFromTier(getTierFromPower(v))) * energyPerEU);
					return e;
				}
			}
			return 0;
		}

		private double getPowerFromTier(int t) {

			if (IC2Net) return getPower(t);
			return t * 32;
		}

		private double getPower(int t) {

			if (EnergyNet.instance != null)
				return EnergyNet.instance.getPowerFromTier(t);
			return t * 32;
		}

		private int getTierFromPower(double t) {

			if (IC2Net) return getTier(t);
			return 1;
		}

		private int getTier(double t) {

			if (EnergyNet.instance != null)
				return EnergyNet.instance.getTierFromPower(t);
			return 1;
		}

		@Override
		public String toString() {

			return "{Source:" + Arrays.toString(sourceCache) + ", Sink:" + Arrays.toString(sinkCache) + "}";
		}
	}

	void setGrid(RedstoneEnergyNetwork newGrid) {

		_grid = newGrid;
	}

	@Override
	public void updateInternalTypes(IGridController grid) {

		super.updateInternalTypes(grid);
		if (deadCache) return;
		if (grid != RedstoneEnergyNetwork.HANDLER) return;
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
	public boolean onPartHit(EntityPlayer player, int side, int subHit) {

		if (subHit >= (2 + 6 * 4) && subHit < (2 + 6 * 6)) {
			if (MFRUtil.isHoldingUsableTool(player, xCoord, yCoord, zCoord)) {
				if (!player.worldObj.isRemote) {
					int dir = ForgeDirection.OPPOSITES[side];
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
					worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
				}
				return true;
			}
		}
		return false;
	}

	@Override
	public void addTraceableCuboids(List<IndexedCuboid6> list, boolean forTrace, boolean hasTool) {

		Vector3 offset = new Vector3(xCoord, yCoord, zCoord);

		IndexedCuboid6 main = new IndexedCuboid6(0, subSelection[1]); // main body
		list.add(main);

		ForgeDirection[] side = ForgeDirection.VALID_DIRECTIONS;
		int[] opposite = ForgeDirection.OPPOSITES;
		for (int i = side.length; i-- > 0;) {
			RedNetConnectionType c = getConnectionState(side[i], true);
			int mode = sideMode[opposite[i]];
			boolean iface = mode > 1 & ((mode & 1) == 1 | hasTool);
			int o = 2 + i, k = o;
			l: if (c.isConnected) {
				if (c.isPlate)
					o += 6;
				else if (c.isCable)
					if (c.isAllSubnets) {
						if (!iface | !hasTool) {
							o = 2 + 6 * 3 + i;
						}
						list.add((IndexedCuboid6) new IndexedCuboid6(hasTool ? k + 6 * 6 : 1, subSelection[o]).add(offset)); // cable part or connection point
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
			} else if (forTrace & hasTool & _cableMode[6] != 1 && (c = getConnectionState(side[i], false)).isConnected) { // cable-only
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
	public void getTileInfo(List<IChatComponent> info, ForgeDirection side, EntityPlayer player, boolean debug) {

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
				info.add(text("Caches: (RF, EU):({" + Arrays.toString(receiverCache) + "," +
						Arrays.toString(providerCache) + "}, " +
						ic2Cache + ")"));
			} else {
				info.add(text("Null Grid"));
			}
			info.add(text("SideType: " + Arrays.toString(sideMode)));
			info.add(text("Node: " + isNode + ", Energy: " + energyForGrid));
			return;
		}
	}
}
