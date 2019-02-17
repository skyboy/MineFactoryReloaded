package powercrystals.minefactoryreloaded.tile.rednet;

import codechicken.lib.raytracer.IndexedCuboid6;
import codechicken.lib.vec.Vector3;
import cofh.core.render.hitbox.CustomHitBox;
import cofh.core.render.hitbox.ICustomHitBox;
import gnu.trove.set.hash.THashSet;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.StringUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import powercrystals.minefactoryreloaded.api.rednet.IRedNetLogicPoint;
import powercrystals.minefactoryreloaded.api.rednet.connectivity.IRedNetConnection;
import powercrystals.minefactoryreloaded.api.rednet.connectivity.IRedNetDecorative;
import powercrystals.minefactoryreloaded.api.rednet.connectivity.IRedNetNoConnection;
import powercrystals.minefactoryreloaded.api.rednet.connectivity.RedNetConnectionType;
import powercrystals.minefactoryreloaded.block.transport.BlockRedNetCable;
import powercrystals.minefactoryreloaded.core.IGridController;
import powercrystals.minefactoryreloaded.core.INode;
import powercrystals.minefactoryreloaded.core.ITraceable;
import powercrystals.minefactoryreloaded.core.MFRDyeColor;
import powercrystals.minefactoryreloaded.core.MFRUtil;
import powercrystals.minefactoryreloaded.item.tool.ItemRedNetMeter;
import powercrystals.minefactoryreloaded.net.Packets;
import powercrystals.minefactoryreloaded.setup.MFRConfig;
import powercrystals.minefactoryreloaded.tile.base.TileEntityBase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static powercrystals.minefactoryreloaded.api.rednet.connectivity.RedNetConnectionType.None;
import static powercrystals.minefactoryreloaded.block.transport.BlockRedNetCable.subSelection;
import static powercrystals.minefactoryreloaded.setup.MFRThings.rednetCableBlock;

public class TileEntityRedNetCable extends TileEntityBase implements INode, ITraceable, ICustomHitBox {

	private static boolean isClient = FMLCommonHandler.instance().getSide() == Side.CLIENT;
	protected int[] _sideColors = new int[6];
	protected byte[] _cableMode = { 1, 1, 1, 1, 1, 1, 0 };
	protected RedNetConnectionType[] _connectionState = { None, None, None, None, None, None };
	protected IRedNetLogicPoint[] _sideUpgrade = { null, null, null, null, null, null };

	RedstoneNetwork _network;

	@SideOnly(Side.CLIENT)
	private long[] __lastUpdates;
	@SideOnly(Side.CLIENT)
	private byte __updatePos;
	@SideOnly(Side.CLIENT)
	private boolean __useTESR;

	boolean isRSNode = false;
	private boolean dirty;
	private boolean readFromNBT;

	private static THashSet<Block> _connectionBlackList;

	public TileEntityRedNetCable() {

		if (_connectionBlackList == null) {
			THashSet<Block> connectionBlackList = new THashSet<>(256);
			for (String s : MFRConfig.redNetConnectionBlacklist.getStringList()) {
				if (!StringUtils.isNullOrEmpty(s))
					connectionBlackList.add(Block.getBlockFromName(s));
				else
					System.out.println("Empty or invalid rednet blacklist entry found. Not adding to rednet blacklist.");
			}
			List<Integer> wl = Arrays.asList(23, 25, 27, 28, 29, 33, 46, 50, 55, 64, 69, 70, 71, 72, 75,
					76, 77, 93, 94, 96, 107, 123, 124, 131, 137, 143, 147, 148, 149, 150, 151, 152, 154,
					157, 158);
			int i = 1 + 175; // as of 1.7.10
			while (i-- > 0)
				if (!wl.contains(i))
					connectionBlackList.add(Block.getBlockById(i));
			_connectionBlackList = connectionBlackList;
		}
		if (isClient)
			__lastUpdates = new long[10];
	}

	@Override
	public void validate() {

		super.validate();
		if (world.isRemote)
			return;

		RedstoneNetwork.HANDLER.addConduitForTick(this);
	}

	@Override
	public void onLoad() {

		super.onLoad();
		if (_network == null) {
			incorporateTiles();
			if (_network == null) {
				setNetwork(new RedstoneNetwork(this));
			}
		}
		readFromNBT = true;
		RedstoneNetwork.HANDLER.addConduitForUpdate(this);
		markDirty();
		Packets.sendToAllPlayersWatching(this);
	}

	@Override
	public void invalidate() {

		if (_network != null) {
			removeFromGrid();
		}
		super.invalidate();
	}

	@Override
	public void onChunkUnload() {

		if (_network != null) {
			removeFromGrid();
		}
		super.onChunkUnload();
	}

	private void removeFromGrid() {

		markForRegen();
		for (EnumFacing d : EnumFacing.VALUES)
			_network.removeNode(new RedstoneNode(pos.offset(d), d), true);
		_network.removeConduit(this);
		if (!_network.isRegenerating())
			_network.updatePowerLevels(); //need to update network power levels in case this node was providing power
		_network = null;
	}

	private void markForRegen() {

		int c = 0;
		for (int i = 6; i-- > 0; )
			if (_connectionState[i].isAllSubnets)
				++c;
		if (c > 1)
			_network.regenerate();
	}

	@Override
	public final boolean isNotValid() {

		return tileEntityInvalid;
	}

	@Override
	public void onNeighborBlockChange() {

		RedstoneNetwork.HANDLER.addConduitForUpdate(this);
	}

	@Override
	public void onMatchedNeighborBlockChange() {

		RedstoneNetwork.HANDLER.addConduitForUpdate(this);
	}

	private void incorporateTiles() {

		if (_network == null) {
			for (EnumFacing dir : EnumFacing.VALUES) {
				if (readFromNBT && (_cableMode[dir.ordinal()] & 1) == 0)
					continue;
				if (world.isBlockLoaded(pos.offset(dir))) {
					TileEntityRedNetCable pipe = MFRUtil.getTile(world, pos.offset(dir), TileEntityRedNetCable.class);
					if (pipe != null) {
						boolean canInterface = pipe.canInterface(this, dir.getOpposite());
						if (canInterface && pipe._network != null) {
							pipe._network.addConduit(this);
						}
						if (!canInterface) {
							_cableMode[dir.ordinal()] &= ~1;
						}
					}
				}
			}
		}
	}

	public void setNetwork(RedstoneNetwork network) {

		_network = network;
	}

	public RedstoneNetwork getNetwork() {

		return _network;
	}

	@Override
	public void updateInternalTypes(IGridController grid) {

		if (grid != RedstoneNetwork.HANDLER)
			return;
		boolean lastNode = isRSNode;
		EnumFacing[] dirs = EnumFacing.VALUES;
		dirty = false;
		for (EnumFacing d : dirs)
			updateNearbyNode(d);
		isRSNode = false;
		for (int i = _connectionState.length; i-- > 0; ) {
			EnumFacing d = dirs[i];
			BlockPos offsetPos = pos.offset(d);
			if (world.isBlockLoaded(offsetPos) && !world.getBlockState(offsetPos).getBlock().equals(rednetCableBlock))
				isRSNode |= _connectionState[i].isConnected;
		}
		if (lastNode != isRSNode)
			_network.addConduit(this);
		markChunkDirty();
		if (dirty) {
			MFRUtil.notifyBlockUpdate(world, pos);
		}
	}

	public void updateNearbyNode(EnumFacing from) {

		updateNearbyNode(getSideColor(from), from);
	}

	public void updateNearbyNode(int subnet, EnumFacing from) {

		RedstoneNode node = new RedstoneNode(pos.offset(from), from);
		updateNearbyNode(node, subnet);
	}

	private void updateNearbyNode(RedstoneNode node, int subnet) {

		if (_network == null)
			return;
		RedNetConnectionType connectionType = getConnectionState(node.getFacing());

		if (!connectionType.isDecorative & connectionType.isConnected && !world.isAirBlock(node.getPos())) {
			if (connectionType.isAllSubnets) {
				_network.addOrUpdateNode(node);
			} else {
				_network.addOrUpdateNode(node, subnet, connectionType.isPlate);
			}
		} else {
			_network.removeNode(node, false);
		}
	}

	public int getWeakPower(EnumFacing to) {

		if (_network == null)
			return 0;
		RedNetConnectionType state = _connectionState[to.ordinal()];
		if (!state.isConnected | !state.isSingleSubnet) {
			return 0;
		}

		RedstoneNode nodebp = new RedstoneNode(pos.offset(to), to);

		int subnet = getSideColor(to), power;

		RedstoneNetwork.log("Asked for weak power at %s", nodebp);
		if (_network.isPowerProvider(subnet, nodebp)) {
			RedstoneNetwork.log("\t- power provider for network %s, power 0", _network.hashCode());
			return 0;
		} else {
			power = Math.min(Math.max(_network.getPowerLevelOutput(subnet), 0), 15);
			RedstoneNetwork.log("\t- got %s from network %s:%s", power, _network.hashCode(), subnet);
		}
		return power;
	}

	public int getStrongPower(EnumFacing to) {

		if (_network == null)
			return 0;
		RedNetConnectionType state = _connectionState[to.ordinal()];
		if (!state.isConnected | !state.isSingleSubnet) {
			return 0;
		}

		RedstoneNode nodebp = new RedstoneNode(pos.offset(to), to);

		int subnet = getSideColor(nodebp.getFacing());

		RedstoneNetwork.log("Asked for strong power at %s", nodebp);
		if (_network.isPowerProvider(subnet, nodebp)) {
			RedstoneNetwork.log("\t- power provider for network %s, power 0", _network.hashCode());
			return 0;
		}
		IBlockState nodeState = world.getBlockState(nodebp.getPos());
		boolean checkWeak = nodeState.getBlock().
				shouldCheckWeakPower(nodeState, world, nodebp.getPos(), to.getOpposite());
		if (checkWeak && _network.isWeakNode(nodebp)) {
			RedstoneNetwork.log("\t- weak node for network %s, power 0", _network.hashCode());
		} else if (state.isCable) {
			int power = Math.min(Math.max(_network.getPowerLevelOutput(subnet), 0), 15);
			RedstoneNetwork.log("\t- got %s from network %s:%s", power, _network.hashCode(), subnet);
			return power;
		}
		return 0;
	}

	@Override
	public boolean onPartHit(EntityPlayer player, EnumHand hand, int subSide, int subHit) {

		markChunkDirty();
		return false;
	}

	@Override
	public void addTraceableCuboids(List<IndexedCuboid6> list, boolean forTrace, boolean hasTool, boolean offsetCuboids) {

		Vector3 offset = offsetCuboids ? Vector3.fromBlockPos(pos) : new Vector3(0, 0, 0);

		IndexedCuboid6 main = new IndexedCuboid6(0, subSelection[0]); // main body
		list.add(main);

		EnumFacing[] sides = EnumFacing.VALUES;
		for (int i = sides.length; i-- > 0; ) {
			RedNetConnectionType c = getConnectionState(sides[i], true);
			int o = 2 + i, k = o;
			if (c.isConnected) {
				if (c.isPlate)
					o += 6;
				else if (c.isCable)
					if (c.isAllSubnets) {
						k = 2 + 6 * 3 + i;
						list.add((IndexedCuboid6) new IndexedCuboid6(hasTool ? k + 18 : 1, subSelection[k])
								.add(offset)); // cable part
						continue;
					}
				k = 2 + 6 * 3 + i;
				list.add((IndexedCuboid6) new IndexedCuboid6(1, subSelection[k]).add(offset)); // cable part
				list.add((IndexedCuboid6) new IndexedCuboid6(o, subSelection[o]).add(offset)); // connection point
				o = k - 6;
				if (c.isSingleSubnet) // color band
					list.add((IndexedCuboid6) new IndexedCuboid6(o, subSelection[o]).add(offset));
			} else if (forTrace & hasTool & _cableMode[6] != 1 &&
					(c = getConnectionState(sides[i], false)).isConnected) { // cable-only
				if (c.isAllSubnets & c.isCable) {
					k += 6 * 3;
					o += 6 * 6;
				}
				list.add((IndexedCuboid6) new IndexedCuboid6(o, subSelection[k]).add(offset)); // connection point (raytrace)
			}
		}
		main.add(offset);
	}

	@Override
	public boolean shouldRenderCustomHitBox(int subHit, EntityPlayer player) {

		return subHit < 2;
	}

	@Override
	public CustomHitBox getCustomHitBox(int hit, EntityPlayer player) {

		final List<IndexedCuboid6> list = new ArrayList<>(7);
		addTraceableCuboids(list, true,
				MFRUtil.isHoldingUsableTool(player, EnumHand.MAIN_HAND, pos, EnumFacing.UP) || MFRUtil.isHoldingUsableTool(player, EnumHand.OFF_HAND, pos, EnumFacing.UP),
				true);
		IndexedCuboid6 cube = list.get(0);
		cube.expand(0.003);
		Vector3 min = cube.min, max = cube.max.subtract(min);
		CustomHitBox box = new CustomHitBox(max.y, max.z, max.x, min.x, min.y, min.z);
		for (int i = 1, e = list.size(); i < e; ++i) {
			cube = list.get(i);
			cube.subtract(min);
			int data = (Integer) cube.data;
			if (data < 2) {
				if (cube.min.y < 0)
					box.sideLength[0] = Math.max(box.sideLength[0], -cube.min.y);
				if (cube.min.z < 0)
					box.sideLength[2] = Math.max(box.sideLength[2], -cube.min.z);
				if (cube.min.x < 0)
					box.sideLength[4] = Math.max(box.sideLength[4], -cube.min.x);
				cube.subtract(max);
				if (cube.max.y > 0)
					box.sideLength[1] = Math.max(box.sideLength[1], cube.max.y);
				if (cube.max.z > 0)
					box.sideLength[3] = Math.max(box.sideLength[3], cube.max.z);
				if (cube.max.x > 0)
					box.sideLength[5] = Math.max(box.sideLength[5], cube.max.x);
			} else
				switch (BlockRedNetCable._subSideMappings[data]) {
				default: // no-op
					break;
				case 0:
				case 7:
					if (cube.min.y < 0)
						box.sideLength[0] = Math.min(box.sideLength[0], -cube.max.y);
					break;
				case 1:
				case 8:
					cube.subtract(max);
					if (cube.max.y > 0)
						box.sideLength[1] = Math.min(box.sideLength[1], cube.min.y);
					break;
				case 2:
				case 9:
					if (cube.min.z < 0)
						box.sideLength[2] = Math.min(box.sideLength[2], -cube.max.z);
					break;
				case 3:
				case 10:
					cube.subtract(max);
					if (cube.max.z > 0)
						box.sideLength[3] = Math.min(box.sideLength[3], cube.min.z);
					break;
				case 4:
				case 11:
					if (cube.min.x < 0)
						box.sideLength[4] = Math.min(box.sideLength[4], -cube.max.x);
					break;
				case 5:
				case 12:
					cube.subtract(max);
					if (cube.max.x > 0)
						box.sideLength[5] = Math.min(box.sideLength[5], cube.min.x);
					break;
				}
		}
		for (int i = box.sideLength.length; i-- > 0; )
			box.drawSide[i] = box.sideLength[i] > 0;
		return box;
	}

	public boolean canInterface(TileEntityRedNetCable with, EnumFacing dir) {

		if ((_cableMode[dir.ordinal()] & 1) == 0)
			return false;
		return (with._cableMode[dir.getOpposite().ordinal()] & 1) != 0;
	}

	public String getRedNetInfo(EnumFacing side, EntityPlayer player) {

		// TODO: localize
		StringBuilder o;
		if (side != null)
			o = new StringBuilder("Side " + side + " is " + ItemRedNetMeter._colorNames[getSideColor(side)]);
		else {
			o = new StringBuilder("Sides are: ");
			for (EnumFacing dir : EnumFacing.VALUES)
				o.append(dir).append(": ").append(ItemRedNetMeter._colorNames[getSideColor(dir)]).append("; ");
		}
		return o.toString();
	}

	@Override
	public void getTileInfo(List<ITextComponent> info, EnumFacing side, EntityPlayer player, boolean debug) {

		if (!debug) {
			info.add(text(getRedNetInfo(side, player)));
		} else { // debug
			if (_network != null) {
				info.add(text("Grid:" + _network));
				info.add(text("Conduits: " + _network.getConduitCount() + ", Nodes: " + _network.getNodeCount()));
				StringBuilder o = new StringBuilder("[");
				for (int i = 0; i < 15; ++i)
					o.append(_network.getPowerLevelOutput(i)).append(",");
				o.append(_network.getPowerLevelOutput(15)).append("]");
				info.add(text("Outputs: " + o));
			} else {
				info.add(text("Null Grid"));
			}
			info.add(text("ConnectionState: " + Arrays.toString(_connectionState)));
			info.add(text("SideMode: " + Arrays.toString(_cableMode)));
			info.add(text("Colors: " + Arrays.toString(_sideColors)));
			info.add(text("Upgrades: " + Arrays.toString(_sideUpgrade)));
			info.add(text("Node: " + isRSNode));
			return;
		}
	}

	public int getSideColorValue(EnumFacing side) {

		return (MFRDyeColor.byMetadata(getSideColor(side) & 15).getColor() << 8) | 0xFF;
	}

	public boolean toggleSide(EnumFacing side) {

		boolean oldMode = (_cableMode[side.ordinal()] & 1) == 1;
		boolean removed = false;
		if (_connectionState[side.ordinal()].isAllSubnets) {
			removeFromGrid();
			removed = true;
			RedstoneNetwork.HANDLER.addConduitForUpdate(this);
		}
		_cableMode[side.ordinal()] ^= 1;
		if (removed) {
			incorporateTiles();
			if (_network == null)
				setNetwork(new RedstoneNetwork(this));
		}
		updateNearbyNode(side);
		MFRUtil.notifyBlockUpdate(world, pos);
		MFRUtil.notifyNearbyBlocks(world, pos, getBlockType());
		return oldMode;
	}

	public byte getMode(int side) {

		if (side == 6)
			return _cableMode[side];
		return (byte) (_cableMode[side] >> 1);
	}

	public void setMode(int side, byte mode) {

		boolean mustUpdate;
		if (side != 6) {
			mustUpdate = (mode != (_cableMode[side] >> 1));
			_cableMode[side] = (byte) ((_cableMode[side] & 1) | (mode << 1));
			updateNearbyNode(EnumFacing.VALUES[side]);
		} else {
			mustUpdate = mode != _cableMode[side];
			_cableMode[side] = mode;
			for (EnumFacing d : EnumFacing.VALUES)
				updateNearbyNode(d);
		}
		if (mustUpdate)
			MFRUtil.notifyBlockUpdate(world, pos);
	}

	public RedNetConnectionType getCachedConnectionState(EnumFacing side) {

		return _connectionState[side.ordinal()];
	}

	public RedNetConnectionType getConnectionState(EnumFacing side) {

		RedNetConnectionType type = getConnectionState(side, true);
		dirty |= _connectionState[side.ordinal()] != type;
		_connectionState[side.ordinal()] = type;
		return type;
	}

	protected RedNetConnectionType getConnectionState(EnumFacing side, boolean decorative) {

		int _mode = _cableMode[side.ordinal()];
		if (decorative & (_mode & 1) == 0)
			return RedNetConnectionType.None;
		{ // eclipse freaks out with bit shifts.
			_mode >>>= 1;
		}
		if (!decorative)
			_mode = 1;
		if (_cableMode[6] == 1)
			_mode = 3;

		BlockPos offsetPos = pos.offset(side);

		if (!world.isBlockLoaded(offsetPos))
			return RedNetConnectionType.None;

		Block b = world.getBlockState(offsetPos).getBlock();
		boolean node = false;

		// cables - always connect
		if (b == rednetCableBlock) {
			if (!decorative || ((TileEntityRedNetCable) world.getTileEntity(offsetPos)).canInterface(this, side.getOpposite()))
				return RedNetConnectionType.CableAll;
			else
				return RedNetConnectionType.None;
		}
		// cable-only, and not a cable - don't connect
		else if (_mode == 3) {
			return RedNetConnectionType.None;
		}

		short nodeFlags = -1;
		if (b instanceof IRedNetConnection) { // API node - let them figure it out
			RedNetConnectionType type = ((IRedNetConnection) b).
					getConnectionType(world, offsetPos, side.getOpposite());
			if (_mode == 0 && type.isConnectionForced)
				return RedNetConnectionType.None;
			else if (!type.isDecorative)
				return type;
			node = true;
			nodeFlags = type.flags;
		}
		// IRedNetNoConnection or air - don't connect
		// Placed here so subclasses that are API nodes can override
		else if (b instanceof IRedNetNoConnection || world.isAirBlock(offsetPos)) {
			return RedNetConnectionType.None;
		}

		/**
		 * The else/if chain is broken here and no values are directly
		 * returned from the function below here for support of API nodes
		 * that want to use the standard connection logic.
		 */
		RedNetConnectionType ret;

		IBlockState state = world.getBlockState(offsetPos);

		// mode 2 forces cable mode for strong power
		if (_mode == 2) {
			if (b.isSideSolid(state, world, offsetPos, side.getOpposite()))
				ret = RedNetConnectionType.ForcedCableSingle;
			else
				ret = RedNetConnectionType.ForcedPlateSingle;
		}
		// mode 1 forces plate mode for weak power
		else if (_mode == 1) {
			ret = RedNetConnectionType.ForcedPlateSingle;
		}
		// standard connection logic, then figure out if we shouldn't connect
		// modes 1, 2, and 3 will skip this. _connectionBlackList is a HashMap
		else if (!node && (_connectionBlackList.contains(b) ||
				b instanceof IRedNetDecorative)) {
			ret = RedNetConnectionType.None;
		} else if (b.isSideSolid(state, world, offsetPos, side.getOpposite())) {
			ret = RedNetConnectionType.CableSingle;
		} else {
			ret = RedNetConnectionType.PlateSingle;
		}
		/**
		 * End of conditions.
		 */
		if (nodeFlags > 0) {
			short type = ret.flags;
			type &= ~24; // 24 represents the two connection type bits
			type |= nodeFlags;
			ret = RedNetConnectionType.fromInternal(type);
		}
		return ret;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {

		tag = super.writeToNBT(tag);
		tag.setIntArray("sideSubnets", _sideColors);
		tag.setByte("v", (byte) 5);
		tag.setByteArray("cableMode", _cableMode);

		return tag;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {

		super.readFromNBT(tag);
		_sideColors = tag.getIntArray("sideSubnets");
		if (_sideColors.length < 6)
			_sideColors = new int[6];

		byte _mode = tag.getByte("mode");
		_cableMode = tag.getByteArray("cableMode");
		if (_cableMode.length < 6)
			_cableMode = new byte[] { 0, 0, 0, 0, 0, 0, 0 };
		switch (tag.getByte("v")) {
		case 0:
			if (_mode == 2)
				_mode = 3;
		case 1:
			_cableMode = new byte[] { _mode, _mode, _mode,
					_mode, _mode, _mode, _mode };
		case 2:
			_cableMode[6] = (byte) (_cableMode[6] == 3 ? 1 : 0);
		case 3:
			for (int i = 6; i-- > 0; )
				_cableMode[i] = (byte) ((_cableMode[i] << 1) | 1);
		case 4:
			_cableMode[6] = (byte) (_cableMode[6] > 0 ? 1 : 0);
			break;
		case 5:
		default:
			break;
		}
		readFromNBT = true;
	}

	public void setSideColor(EnumFacing side, int color) {

		if (side == null) {
			return;
		}
		int previousColor = _sideColors[side.ordinal()];
		_sideColors[side.ordinal()] = color;

		updateNearbyNode(side);
		_network.updatePowerLevels(previousColor);
		MFRUtil.notifyBlockUpdate(world, pos);
	}

	public int getSideColor(EnumFacing side) {

		if (side == null) {
			return 0;
		}
		return _sideColors[side.ordinal()];
	}

	public boolean isSolidOnSide(EnumFacing side) {

		if (_cableMode[6] == 1)
			return false;
		int m = _cableMode[side.ordinal()], i;
		{
			i = m >> 1;
		}
		return ((m & 1) == 1) & i != 3 & i != 1;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public double getMaxRenderDistanceSquared() {

		return 65536D;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldRenderInPass(int pass) {

		return pass == 0 ? true : false;
	}

	@SideOnly(Side.CLIENT)
	public boolean onRender() {

		//TODO remove if we only use TESR rendering
		long[] a = __lastUpdates;
		int i = a.length, e = i, p = __updatePos;
		a[p] = world.getTotalWorldTime();
		__updatePos = (byte) ((p + 1) % e);
		long gap = 0;
		while (i-- > 1)
			gap += a[(i + p + 1) % e] - a[(i + p) % e];
		__useTESR = gap <= (30 * e);
		return __useTESR;
	}

	@Override
	protected NBTTagCompound writePacketData(NBTTagCompound tag) {

		tag.setIntArray("colors", _sideColors);
		tag.setInteger("mode[0]", (_cableMode[0] & 0xFF) | ((_cableMode[1] & 0xFF) << 8) | ((_cableMode[2] & 0xFF) << 16) |
				((_cableMode[3] & 0xFF) << 24));
		tag.setInteger("mode[1]", (_cableMode[4] & 0xFF) | ((_cableMode[5] & 0xFF) << 8) | ((_cableMode[6] & 0xFF) << 16));
		tag.setInteger("state[0]", _connectionState[0].ordinal() | (_connectionState[1].ordinal() << 4) |
				(_connectionState[2].ordinal() << 8) | (_connectionState[3].ordinal() << 12) |
				(_connectionState[4].ordinal() << 16) | (_connectionState[5].ordinal() << 20));

		return tag;
	}

	@Override
	protected void handlePacketData(NBTTagCompound tag) {

		_sideColors = tag.getIntArray("colors");
		int mode = tag.getInteger("mode[0]");
		_cableMode[0] = (byte) ((mode) & 0xFF);
		_cableMode[1] = (byte) ((mode >> 8) & 0xFF);
		_cableMode[2] = (byte) ((mode >> 16) & 0xFF);
		_cableMode[3] = (byte) ((mode >> 24) & 0xFF);
		mode = tag.getInteger("mode[1]");
		_cableMode[4] = (byte) ((mode) & 0xFF);
		_cableMode[5] = (byte) ((mode >> 8) & 0xFF);
		_cableMode[6] = (byte) ((mode >> 16) & 0xFF);
		mode = tag.getInteger("state[0]");
		_connectionState[0] = RedNetConnectionType.values()[(mode) & 0xF];
		_connectionState[1] = RedNetConnectionType.values()[(mode >> 4) & 0xF];
		_connectionState[2] = RedNetConnectionType.values()[(mode >> 8) & 0xF];
		_connectionState[3] = RedNetConnectionType.values()[(mode >> 12) & 0xF];
		_connectionState[4] = RedNetConnectionType.values()[(mode >> 16) & 0xF];
		_connectionState[5] = RedNetConnectionType.values()[(mode >> 20) & 0xF];
	}

	@Override
	public void firstTick(IGridController grid) {

		// TODO Auto-generated method stub

	}
}
