package powercrystals.minefactoryreloaded.tile.tank;

import cofh.core.util.helpers.FluidHelper;
import net.minecraft.util.math.BlockPos;

import java.util.LinkedHashSet;

import net.minecraft.world.World;
import net.minecraft.util.EnumFacing;

import powercrystals.minefactoryreloaded.core.IDelayedValidate;
import powercrystals.minefactoryreloaded.core.MFRUtil;
import powercrystals.minefactoryreloaded.net.ConnectionHandler;

public class TankNetwork implements IDelayedValidate {

	private LinkedHashSet<TileEntityTank> nodeSet;
	private TileEntityTank master;
	private FluidTankMulti storage = new FluidTankMulti(this);

	public TankNetwork() {

	}

	public TankNetwork(TileEntityTank base) {

		nodeSet = new LinkedHashSet<>();
		addNode(base);
	}

	public boolean addNode(TileEntityTank cond) {

		if (nodeSet.add(cond))
			return nodeAdded(cond);
		return false;
	}

	public void removeNode(TileEntityTank cond) {

		if (!nodeSet.isEmpty()) {
			if (nodeSet.remove(cond)) {
				nodeRemoved(cond);
			}
		}
	}

	@Override
	public boolean isNotValid() {

		return nodeSet.size() == 0;
	}

	@Override // re-using interface for a lighter 'single delayed tick'
	public void firstTick() {

		markSweep();
	}

	public synchronized void markSweep() {

		destroyGrid();
		if (nodeSet.isEmpty())
			return;
		TileEntityTank main = nodeSet.iterator().next();
		LinkedHashSet<TileEntityTank> oldSet = nodeSet;
		nodeSet = new LinkedHashSet<>(Math.min(oldSet.size() / 6, 5));

		LinkedHashSet<TileEntityTank> toCheck = new LinkedHashSet<>();
		LinkedHashSet<TileEntityTank> checked = new LinkedHashSet<>();
		BlockPos bp = new BlockPos(0, 0, 0);
		EnumFacing[] dir = EnumFacing.VALUES;
		toCheck.add(main);
		checked.add(main);
		while (!toCheck.isEmpty()) {
			main = toCheck.iterator().next();
			addNode(main);
			World world = main.getWorld();
			for (int i = 6; i-- > 0;) {
				bp = main.getPos().offset(dir[i]);
				if (world.isBlockLoaded(bp)) {
					TileEntityTank te = MFRUtil.getTile(world, bp, TileEntityTank.class);
					if (te != null) {
						if (main.isInterfacing(dir[i]) && !checked.contains(te))
							toCheck.add(te);
						checked.add(te);
					}
				}
			}
			toCheck.remove(main);
			oldSet.remove(main);
		}
		if (!oldSet.isEmpty()) {
			TankNetwork newGrid = new TankNetwork();
			newGrid.nodeSet = oldSet;
			newGrid.markSweep();
		}
		updateNodes();
	}

	public void destroyGrid() {

		master = null;
		for (TileEntityTank curCond : nodeSet)
			curCond.grid = null;
		storage.empty();
	}

	void updateNodes() {

		for (TileEntityTank node : nodeSet) {
			node.markDirty();
			MFRUtil.notifyBlockUpdate(node.getWorld(), node.getPos());
		}
	}

	public boolean canMergeGrid(TankNetwork grid) {

		if (grid == null) return false;
		return FluidHelper.isFluidEqualOrNull(grid.storage.getFluid(), storage.getFluid());
	}

	public synchronized void mergeGrid(TankNetwork grid) {

		if (grid == this) return;
		if (storage.getFluid() == null && grid.storage.getFluid() != null) {
			grid.mergeGrid(this);
			return;
		}
		grid.destroyGrid();

		for (TileEntityTank cond : grid.nodeSet)
			addNode(cond);

		grid.nodeSet.clear();
		updateNodes();
	}

	public void nodeRemoved(TileEntityTank cond) {

		if (cond == master) {
			if (nodeSet.isEmpty()) {
				master = null;
			} else {
				master = nodeSet.iterator().next();
			}
		}
		storage.removeTank(cond._tank);
		if (cond.interfaceCount() > 1)
			ConnectionHandler.update(this);
	}

	public boolean nodeAdded(TileEntityTank cond) {

		if (cond.grid != null) {
			if (cond.grid != this) {
				nodeSet.remove(cond);
				if (canMergeGrid(cond.grid)) {
					mergeGrid(cond.grid);
				} else
					return false;
			} else
				return false;
		} else if (cond._tank.getFluid() != null) {
			if (!FluidHelper.isFluidEqualOrNull(cond._tank.getFluid(), storage.getFluid())) {
				nodeSet.remove(cond);
				return false;
			} else
				cond.grid = this;
		} else {
			cond.grid = this;
		}
		if (master == null) {
			master = cond;
		}
		storage.addTank(cond._tank);
		return true;
	}

	public synchronized FluidTankMulti getStorage() {

		return storage;
	}

	public int getSize() {

		return nodeSet.size();
	}

	@Override
	public String toString() {

		return "TankNetwork@" + Integer.toString(hashCode()) + "; master:" + master;
	}

}
