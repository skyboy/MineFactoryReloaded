package powercrystals.minefactoryreloaded.tile.rednet;

import cofh.api.energy.EnergyStorage;

import java.util.LinkedHashSet;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

import powercrystals.core.position.BlockPosition;
import powercrystals.minefactoryreloaded.net.GridTickHandler;

public class RedstoneEnergyNetwork
{
	public static final int TRANSFER_RATE = 1000;
	public static final int STORAGE = TRANSFER_RATE * 6;

	private LinkedHashSet<TileEntityRedNetEnergy> nodeSet = new LinkedHashSet<TileEntityRedNetEnergy>();
	private LinkedHashSet<TileEntityRedNetEnergy> conduitSet;
	private TileEntityRedNetEnergy master;
	private boolean regenerating = false;
	EnergyStorage storage = new EnergyStorage(480, 80);
	
	protected RedstoneEnergyNetwork() {
		storage.setCapacity(STORAGE);
		storage.setMaxTransfer(TRANSFER_RATE);
	}

	public RedstoneEnergyNetwork(TileEntityRedNetEnergy base) { this();
		conduitSet = new LinkedHashSet<TileEntityRedNetEnergy>();
		addConduit(base);
	}

	public int getNodeShare(TileEntityRedNetEnergy cond) {
		int size = nodeSet.size();
		if (size == 1)
			return storage.getEnergyStored();
		int amt = 0;
		if (master == cond) amt = storage.getEnergyStored() % size;
		return amt + storage.getEnergyStored() / size;
	}

	public void addConduit(TileEntityRedNetEnergy cond) {
		if (conduitSet.add(cond))
			if (!conduitAdded(cond))
				return;
		if (cond.isNode) {
			if (nodeSet.add(cond)) {
				nodeAdded(cond);
			}
		} else if (!nodeSet.isEmpty()) {
			int share = getNodeShare(cond);
			if (nodeSet.remove(cond)) {
				cond.energyForGrid = storage.extractEnergy(share, false);
				nodeRemoved(cond);
			}
		}
	}

	public void removeConduit(TileEntityRedNetEnergy cond) {
		conduitSet.remove(cond);
		if (!nodeSet.isEmpty()) {
			int share = getNodeShare(cond);
			if (nodeSet.remove(cond)) {
				cond.energyForGrid = storage.extractEnergy(share, false);
				nodeRemoved(cond);
			}
		}
	}
	
	public void regenerate() {
		regenerating = true;
		GridTickHandler.regenerateGrid(this);
	}
	
	public void markSweep() {
		destroyGrid();
		if (conduitSet.isEmpty())
			return;
		TileEntityRedNetEnergy main = conduitSet.iterator().next();
		LinkedHashSet<TileEntityRedNetEnergy> oldSet = conduitSet;
		nodeSet.clear();
		conduitSet = new LinkedHashSet<TileEntityRedNetEnergy>(Math.min(oldSet.size() / 6, 5));
		rebalanceGrid();
		
		LinkedHashSet<TileEntityRedNetEnergy> toCheck = new LinkedHashSet<TileEntityRedNetEnergy>();
		BlockPosition bp = new BlockPosition(0,0,0);
		ForgeDirection[] dir = ForgeDirection.VALID_DIRECTIONS;
		toCheck.add(main);
		while (!toCheck.isEmpty()) {
			main = toCheck.iterator().next();
			addConduit(main);
			World world = main.worldObj;
			for (int i = 6; i --> 0; ) {
				bp.x = main.xCoord; bp.y = main.yCoord; bp.z = main.zCoord;
				bp.orientation = dir[i];
				bp.moveForwards(1);
				if (world.blockExists(bp.x, bp.y, bp.z)) {
					TileEntity te = bp.getTileEntity(world);
					if (te instanceof TileEntityRedNetEnergy)
						if (main.canInterface((TileEntityRedNetEnergy)te) && !conduitSet.contains(te))
							toCheck.add((TileEntityRedNetEnergy)te);
				}
			}
			toCheck.remove(main);
			oldSet.remove(main);
		}
		if (!oldSet.isEmpty()) {
			RedstoneEnergyNetwork newGrid = new RedstoneEnergyNetwork();
			newGrid.conduitSet = oldSet;
			newGrid.markSweep();
		}
		if (nodeSet.isEmpty())
			GridTickHandler.removeGrid(this);
		else
			GridTickHandler.addGrid(this);
		regenerating = false;
	}
	
	public void destroyGrid() {
		master = null;
		for (TileEntityRedNetEnergy curCond : nodeSet)
			destroyNode(curCond);
		for (TileEntityRedNetEnergy curCond : conduitSet)
			destroyConduit(curCond);
		GridTickHandler.removeGrid(this);
	}

	public void destroyNode(TileEntityRedNetEnergy cond) {
		cond.energyForGrid = getNodeShare(cond);
		cond.setGrid(null);
	}

	public void destroyConduit(TileEntityRedNetEnergy cond) {
		cond.setGrid(null);
	}

	public void doGridPreUpdate() {
		if (regenerating)
			return;
		if (nodeSet.isEmpty()) {
			GridTickHandler.removeGrid(this);
			return;
		}
		if (storage.getEnergyStored() >= storage.getMaxEnergyStored())
			return;
		ForgeDirection[] directions = ForgeDirection.VALID_DIRECTIONS;
		
		for (TileEntityRedNetEnergy cond : nodeSet)
			for (int i = 6; i --> 0; )
				cond.extract(directions[i]);
	}

	public void doGridUpdate() {
		if (regenerating)
			return;
		if (nodeSet.isEmpty()) {
			GridTickHandler.removeGrid(this);
			return;
		}
		ForgeDirection[] directions = ForgeDirection.VALID_DIRECTIONS;
		int size = nodeSet.size();
		int toDistribute = storage.getEnergyStored() / size;
		int sideDistribute = toDistribute / 6;
		
		if (sideDistribute > 0) for (TileEntityRedNetEnergy cond : nodeSet)
			if (cond != master) {
				int e = 0;
				for (int i = 6; i --> 0; )
					e += cond.transfer(directions[i], sideDistribute);
				if (e > 0) storage.modifyEnergyStored(-e);
			}
		
		toDistribute += storage.getEnergyStored() % size;
		sideDistribute = toDistribute / 6;
		
		if (sideDistribute > 0) {
			int e = 0;
			for (int i = 6; i --> 0; ) 
				e += master.transfer(directions[i], sideDistribute);
			if (e > 0) storage.modifyEnergyStored(-e);
		}
	}

	public boolean canGridMerge(RedstoneEnergyNetwork otherGrid) {
		LinkedHashSet<TileEntityRedNetEnergy> toCheck = otherGrid.conduitSet;
		return !toCheck.isEmpty() && !conduitSet.isEmpty() &&
				toCheck.iterator().next().canInterface(conduitSet.iterator().next());
	}

	public void mergeGrid(RedstoneEnergyNetwork theGrid) {
		theGrid.destroyGrid();
		for (TileEntityRedNetEnergy cond : theGrid.conduitSet)
			addConduit(cond);
		
		theGrid.conduitSet.clear();
		theGrid.nodeSet.clear();
	}

	public void nodeAdded(TileEntityRedNetEnergy cond) {
		if (master == null) {
			master = cond;
			GridTickHandler.addGrid(this);
		}
		rebalanceGrid();
		storage.modifyEnergyStored(cond.energyForGrid);
	}

	public void nodeRemoved(TileEntityRedNetEnergy cond) {
		rebalanceGrid();
		if (cond == master) {
			if (nodeSet.isEmpty()) {
				master = null;
				GridTickHandler.removeGrid(this);
			} else {
				master = nodeSet.iterator().next();
			}
		}
	}

	public boolean conduitAdded(TileEntityRedNetEnergy cond) {
		if (cond.grid != null) {
			if (cond.grid != this)
				if (canGridMerge(cond.grid))
					mergeGrid(cond.grid);
				else {
					conduitSet.remove(cond);
					return false;
				}
			else
				return false;
		} else
			cond.setGrid(this);
		return true;
	}

	public void rebalanceGrid() {
		storage.setCapacity(nodeSet.size() * STORAGE);
	}

	public int getConduitCount() {
		return conduitSet.size();
	}

	public int getNodeCount() {
		return nodeSet.size();
	}
	
	@Override
	public String toString() {
		return "RedstoneEnergyNetwork@" + Integer.toString(hashCode()) + "; master:" + master +
				"; regenerating:" + regenerating + "; isTicking:" + GridTickHandler.isGridTicking(this);
	}
}
