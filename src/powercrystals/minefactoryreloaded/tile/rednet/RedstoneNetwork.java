package powercrystals.minefactoryreloaded.tile.rednet;

import static powercrystals.minefactoryreloaded.setup.MFRThings.rednetCableBlock;

import cofh.lib.util.LinkedHashList;
import cofh.lib.util.position.BlockPosition;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.StringFormatterMessageFactory;

import powercrystals.minefactoryreloaded.api.rednet.IRedNetInputNode;
import powercrystals.minefactoryreloaded.api.rednet.IRedNetOutputNode;
import powercrystals.minefactoryreloaded.api.rednet.connectivity.IRedstoneAlike;
import powercrystals.minefactoryreloaded.core.ArrayHashList;
import powercrystals.minefactoryreloaded.core.IGrid;
import powercrystals.minefactoryreloaded.core.MFRUtil;
import powercrystals.minefactoryreloaded.net.GridTickHandler;
import powercrystals.minefactoryreloaded.setup.MFRConfig;
import powercrystals.minefactoryreloaded.setup.MFRThings;

public class RedstoneNetwork implements IGrid {

	static final GridTickHandler<RedstoneNetwork, TileEntityRedNetCable> HANDLER =
			GridTickHandler.redstone;

	private boolean _ignoreUpdates;

	private boolean _mustUpdate, updatePowerLevels;
	private boolean[] _updateSubnets = new boolean[16];

	@SuppressWarnings("unchecked")
	// cast is a warning too. using a generic type is an error?
	private Set<BlockPosition>[] _singleNodes = new Set[16];
	private Set<BlockPosition> _omniNodes = new LinkedHashSet<BlockPosition>();

	private Set<BlockPosition> _weakNodes = new LinkedHashSet<BlockPosition>();

	private boolean regenerating;
	private ArrayHashList<TileEntityRedNetCable> nodeSet = new ArrayHashList<TileEntityRedNetCable>();
	private LinkedHashList<TileEntityRedNetCable> conduitSet;

	private int[] _powerLevelOutput = new int[16];
	private BlockPosition[] _powerProviders = new BlockPosition[16];

	private World _world;

	private static boolean log = false;
	private static Logger _log = LogManager.getLogger("RedNet Debug", StringFormatterMessageFactory.INSTANCE);

	public static void log(String format, Object... data) {

		if (log & format != null) {
			_log.debug(format, data);
		}
	}

	private RedstoneNetwork(World world) {

		_world = world;
		log = MFRConfig.redNetDebug.getBoolean(false);

		for (int i = 0; i < 16; i++)
			_singleNodes[i] = new LinkedHashSet<BlockPosition>();
	}

	public RedstoneNetwork(TileEntityRedNetCable base) {

		this(base.getWorldObj());
		conduitSet = new LinkedHashList<TileEntityRedNetCable>();
		regenerating = true;
		addConduit(base);
		regenerating = false;
	}

	@Override
	public void doGridPreUpdate() {

		// may be able to not tick at all if _mustUpdate and updatePowerLevels are false
		if (_mustUpdate) {
			for (int i = 16; i-- > 0;)
				if (_updateSubnets[i])
					notifyNodes(i);
			_mustUpdate = false;
		}
	}

	@Override
	public void doGridUpdate() {

		if (updatePowerLevels) {
			updatePowerLevels();
			updatePowerLevels = false;
		}
	}

	public void addConduit(TileEntityRedNetCable cond) {

		if (conduitSet.add(cond))
			if (!conduitAdded(cond))
				return;
		if (cond.isRSNode) {
			if (nodeSet.add(cond)) {
				nodeAdded(cond);
			}
		} else if (!nodeSet.isEmpty()) {
			if (nodeSet.remove(cond)) {
				nodeRemoved(cond);
			}
		}
	}

	public void removeConduit(TileEntityRedNetCable cond) {

		conduitSet.remove(cond);
		if (!nodeSet.isEmpty()) {
			if (nodeSet.remove(cond)) {
				nodeRemoved(cond);
			}
		}
	}

	@Override
	public void markSweep() {

		destroyGrid();
		if (conduitSet.isEmpty())
			return;
		TileEntityRedNetCable main = conduitSet.iterator().next();
		nodeSet.clear();
		//{ clearing nodes
		_omniNodes.clear();
		_weakNodes.clear();
		for (Set<BlockPosition> a : _singleNodes)
			a.clear();
		//}
		LinkedHashList<TileEntityRedNetCable> oldSet = conduitSet;
		conduitSet = new LinkedHashList<TileEntityRedNetCable>(Math.min(oldSet.size() / 6, 5));

		LinkedHashList<TileEntityRedNetCable> toCheck = new LinkedHashList<TileEntityRedNetCable>();
		LinkedHashList<TileEntityRedNetCable> checked = new LinkedHashList<TileEntityRedNetCable>();
		BlockPosition bp = new BlockPosition(0, 0, 0);
		ForgeDirection[] dir = ForgeDirection.VALID_DIRECTIONS;
		toCheck.add(main);
		checked.add(main);
		while (!toCheck.isEmpty()) {
			main = toCheck.shift();
			addConduit(main);
			World world = main.getWorldObj();
			for (int i = 6; i-- > 0;) {
				bp.x = main.xCoord;
				bp.y = main.yCoord;
				bp.z = main.zCoord;
				bp.step(dir[i]);
				if (world.blockExists(bp.x, bp.y, bp.z)) {
					TileEntity te = bp.getTileEntity(world);
					if (te instanceof TileEntityRedNetCable) {
						TileEntityRedNetCable tec = (TileEntityRedNetCable) te;
						if (main.canInterface(tec, dir[i]) && checked.add(tec))
							toCheck.add(tec);
					}
				}
			}
			oldSet.remove(main);
		}
		if (!oldSet.isEmpty()) {
			RedstoneNetwork newGrid = new RedstoneNetwork(_world);
			newGrid.conduitSet = oldSet;
			newGrid.regenerating = true;
			newGrid.markSweep();
		}
		if (nodeSet.isEmpty())
			HANDLER.removeGrid(this);
		else
			HANDLER.addGrid(this);
		regenerating = false;
		updatePowerLevels = true;
	}

	public void destroyGrid() {

		regenerating = true;
		Arrays.fill(_powerLevelOutput, 0);
		Arrays.fill(_powerProviders, null);
		for (TileEntityRedNetCable curCond : nodeSet)
			destroyNode(curCond);
		for (TileEntityRedNetCable curCond : conduitSet)
			destroyConduit(curCond);
		HANDLER.removeGrid(this);
	}

	public void destroyNode(TileEntityRedNetCable cond) {

		cond._network = null;
	}

	public void destroyConduit(TileEntityRedNetCable cond) {

		cond._network = null;
	}

	public void nodeAdded(TileEntityRedNetCable cond) {

		HANDLER.addConduitForUpdate(cond);
		if (!nodeSet.isEmpty()) {
			HANDLER.addGrid(this);
		}
	}

	public void nodeRemoved(TileEntityRedNetCable cond) {

		if (nodeSet.isEmpty()) {
			HANDLER.removeGrid(this);
		}
	}

	public boolean conduitAdded(TileEntityRedNetCable cond) {

		if (cond._network != null) {
			if (cond._network != this) {
				conduitSet.remove(cond);
				if (canMergeGrid(cond._network)) {
					mergeGrid(cond._network);
				} else
					return false;
			} else
				return false;
		} else
			cond.setNetwork(this);
		return true;
	}

	public boolean canMergeGrid(RedstoneNetwork otherGrid) {

		if (otherGrid == null || otherGrid == this) return false;
		return true;
	}

	public void mergeGrid(RedstoneNetwork grid) {

		if (grid == this) return;
		boolean r = regenerating || grid.regenerating;
		grid.destroyGrid();
		if (!regenerating & r)
			regenerate();

		regenerating = true;
		for (TileEntityRedNetCable cond : grid.conduitSet)
			addConduit(cond);
		regenerating = r;

		grid.conduitSet.clear();
		grid.nodeSet.clear();
	}

	public void regenerate() {

		regenerating = true;
		HANDLER.regenerateGrid(this);
	}

	public boolean isRegenerating() {

		return regenerating;
	}

	public int getConduitCount() {

		return conduitSet.size();
	}

	public int getNodeCount() {

		return nodeSet.size();
	}

	public int getPowerLevelOutput(int subnet)
	{

		return _powerLevelOutput[subnet];
	}

	boolean isPowerProvider(int subnet, BlockPosition node)
	{

		return node.equals(_powerProviders[subnet]);
	}

	@Override
	public String toString() {

		return "RedstoneNetwork@" + Integer.toString(hashCode()) + "; regenerating:" + regenerating + "; isTicking:" + HANDLER.isGridTicking(this);
	}

	/// OLD CODE

	public boolean isWeakNode(BlockPosition node) {

		return _weakNodes.contains(node);
	}

	public void addOrUpdateNode(BlockPosition node) {

		Block block = _world.getBlock(node.x, node.y, node.z);
		if (block == rednetCableBlock) {
			return;
		}

		if (!_omniNodes.contains(node)) {
			log("Network with ID %d adding omni node %s", hashCode(), node);
			_omniNodes.add(node);
			notifyOmniNode(node);
		}

		int[] powers = getOmniNodePowerLevel(node);
		if (powers == null)
			return;
		for (int subnet = 0; subnet < 16; subnet++) {
			int power = powers[subnet];
			if (Math.abs(power) > Math.abs(_powerLevelOutput[subnet])) {
				log("Network with ID %d:%d has omni node %s as new power provider", hashCode(), subnet, node);
				_powerLevelOutput[subnet] = power;
				_powerProviders[subnet] = node;
				_mustUpdate = true;
				_updateSubnets[subnet] = true;
			}
			else if (node.equals(_powerProviders[subnet]) && Math.abs(power) < Math.abs(_powerLevelOutput[subnet])) {
				updatePowerLevels = true;
			}
		}

		if (!updatePowerLevels && !_mustUpdate)
			notifyOmniNode(node);
		else if (!_mustUpdate)
			for (int i = 0; i < 16; ++i)
				_updateSubnets[i] = _mustUpdate = true;
	}

	public void addOrUpdateNode(BlockPosition node, int subnet, boolean allowWeak) {

		Block block = _world.getBlock(node.x, node.y, node.z);
		if (block == rednetCableBlock) {
			return;
		}

		if (!_singleNodes[subnet].contains(node)) {
			removeNode(node, false);
			log("Network with ID %d:%d adding node %s", hashCode(), subnet, node);

			_singleNodes[subnet].add(node);
		}

		if (allowWeak) {
			_weakNodes.add(node);
		} else {
			_weakNodes.remove(node);
		}

		int power = getSingleNodePowerLevel(node, subnet);
		log("Network with ID %d:%d calculated power for node %s as %d", hashCode(), subnet, node, power);
		if (Math.abs(power) > Math.abs(_powerLevelOutput[subnet])) {
			log("Network with ID %d:%d has node %s as new power provider", hashCode(), subnet, node);
			_powerLevelOutput[subnet] = power;
			_powerProviders[subnet] = node;
			_mustUpdate = true;
			_updateSubnets[subnet] = true;
		} else if (node.equals(_powerProviders[subnet]) && Math.abs(power) < Math.abs(_powerLevelOutput[subnet])) {
			log("Network with ID %d:%d removing power provider node, recalculating", hashCode(), subnet);
			updatePowerLevels = true;
		}

		if (!updatePowerLevels && !_updateSubnets[subnet])
			notifySingleNode(node, subnet);
		else
			_updateSubnets[subnet] = _mustUpdate = true;
	}

	public void removeNode(BlockPosition node, boolean unloading) {

		boolean omniNode = _omniNodes.remove(node);
		boolean notify = omniNode;

		notify |= _weakNodes.remove(node);

		for (int subnet = 0; subnet < 16; subnet++) {
			if (_singleNodes[subnet].contains(node)) {
				notify = true;
				log("Network with ID %d:%d removing node %s", hashCode(), subnet, node);
				_singleNodes[subnet].remove(node);
			}

			if (node.equals(_powerProviders[subnet])) {
				log("Network with ID %d:%d removing power provider node, recalculating", hashCode(), subnet);
				_mustUpdate = true;
				_updateSubnets[subnet] = true;
			}
		}

		if (notify & !unloading) {
			Block block = _world.getBlock(node.x, node.y, node.z);
			if (block == rednetCableBlock) {
				return;
			}
			else if (block instanceof IRedNetInputNode) {
				if (omniNode)
					((IRedNetInputNode) block).onInputsChanged(_world, node.x, node.y, node.z,
						node.orientation.getOpposite(), new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 });
				else
					((IRedNetInputNode) block).onInputChanged(_world, node.x, node.y, node.z, node.orientation.getOpposite(), 0);
			}
			MFRUtil.notifyNearbyBlocksExcept(_world, node.x, node.y, node.z, Blocks.air);
		}
	}

	public void updatePowerLevels() {

		for (int subnet = 0; subnet < 16; subnet++) {
			updatePowerLevels(subnet);
		}
	}

	public void updatePowerLevels(int subnet) {

		int lastPower = _powerLevelOutput[subnet];

		_powerLevelOutput[subnet] = 0;
		_powerProviders[subnet] = null;

		log("Network with ID %d:%d recalculating power levels for %d single nodes and %d omni nodes", hashCode(), subnet, _singleNodes[subnet].size(), _omniNodes.size());

		for (BlockPosition node : _singleNodes[subnet]) {
			if (!isNodeLoaded(node)) {
				continue;
			}
			int power = getSingleNodePowerLevel(node, subnet);
			if (Math.abs(power) > Math.abs(_powerLevelOutput[subnet])) {
				_powerLevelOutput[subnet] = power;
				_powerProviders[subnet] = node;
			}
		}

		for (BlockPosition node : _omniNodes) {
			if (!isNodeLoaded(node)) {
				continue;
			}
			int power = getOmniNodePowerLevel(node, subnet);
			if (Math.abs(power) > Math.abs(_powerLevelOutput[subnet])) {
				_powerLevelOutput[subnet] = power;
				_powerProviders[subnet] = node;
			}
		}

		log("Network with ID %d:%d recalculated power levels as: %d from %d with powering node %s", hashCode(), subnet, _powerLevelOutput[subnet], lastPower, _powerProviders[subnet]);
		boolean u = _powerLevelOutput[subnet] != lastPower;
		_updateSubnets[subnet] |= u;
		_mustUpdate |= u;
	}

	private void notifyNodes(int subnet) {

		if (_ignoreUpdates) {
			log("Network asked to notify nodes while ignoring updates (API misuse?)!");
			_mustUpdate = true;
			_updateSubnets[subnet] = true;
			return;
		}
		_updateSubnets[subnet] = false;
		_ignoreUpdates = true;
		log("Network with ID %d:%d notifying %d single nodes and %d omni nodes", hashCode(), subnet, _singleNodes[subnet].size(), _omniNodes.size());
		for (BlockPosition bp : _singleNodes[subnet]) {
			log("Network with ID %d:%d notifying node %s of power state change to %d", hashCode(), subnet, bp, _powerLevelOutput[subnet]);
			notifySingleNode(bp, subnet);
		}
		for (BlockPosition bp : _omniNodes) {
			log("Network with ID %d:%d notifying omni node %s of power state change to %d", hashCode(), subnet, bp, _powerLevelOutput[subnet]);
			notifyOmniNode(bp);
		}
		_ignoreUpdates = false;
	}

	private boolean isNodeLoaded(BlockPosition node) {

		return _world.getChunkProvider().chunkExists(node.x >> 4, node.z >> 4);
	}

	private void notifySingleNode(BlockPosition node, int subnet) {

		if (isNodeLoaded(node)) {
			Block block = _world.getBlock(node.x, node.y, node.z);
			if (block == rednetCableBlock) {
				return;
			} else if (block instanceof IRedNetInputNode) {
				((IRedNetInputNode) block).onInputChanged(_world, node.x, node.y, node.z, node.orientation.getOpposite(), _powerLevelOutput[subnet]);
			} else {
				MFRUtil.notifyNearbyBlocksExcept(_world, node.x, node.y, node.z, MFRThings.rednetCableBlock);
			}
		}
	}

	private void notifyOmniNode(BlockPosition node) {

		if (isNodeLoaded(node)) {
			Block block = _world.getBlock(node.x, node.y, node.z);
			if (block instanceof IRedNetInputNode) {
				((IRedNetInputNode) block).onInputsChanged(_world, node.x, node.y, node.z, node.orientation.getOpposite(), Arrays.copyOf(_powerLevelOutput, 16));
			}
		}
	}

	private int getOmniNodePowerLevel(BlockPosition node, int subnet) {

		if (!isNodeLoaded(node)) {
			return 0;
		}
		int[] levels = getOmniNodePowerLevel(node);
		return levels == null ? 0 : levels[subnet];
	}

	private int[] getOmniNodePowerLevel(BlockPosition node) {

		if (!isNodeLoaded(node)) {
			return null;
		}
		Block b = _world.getBlock(node.x, node.y, node.z);
		if (b instanceof IRedNetOutputNode) {
			return ((IRedNetOutputNode) b).getOutputValues(_world, node.x, node.y, node.z, node.orientation.getOpposite());
		} else {
			return null;
		}
	}

	private int getSingleNodePowerLevel(BlockPosition node, int subnet) {

		if (!isNodeLoaded(node)) {
			return 0;
		}

		Block block = _world.getBlock(node.x, node.y, node.z);
		if (block instanceof IRedNetOutputNode) {
			return ((IRedNetOutputNode) block).getOutputValue(_world, node.x, node.y, node.z, node.orientation, subnet);
		}

		int offset = 0;
		if (block == Blocks.redstone_wire || block instanceof IRedstoneAlike) {
			offset = -1;
		}

		int ret = 0;
		if (_weakNodes.contains(node)) {
			int weakPower = _world.getIndirectPowerLevelTo(node.x, node.y, node.z, node.orientation.ordinal()) + offset;
			int strongPower = _world.isBlockProvidingPowerTo(node.x, node.y, node.z, node.orientation.ordinal()) + offset;
			ret = Math.max(weakPower, strongPower);
		} else {
			ret = _world.isBlockProvidingPowerTo(node.x, node.y, node.z, node.orientation.ordinal()) + offset;
		}

		if (offset == ret)
			return 0;
		return ret;
	}

}
