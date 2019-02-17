package powercrystals.minefactoryreloaded.tile.transport;

import cofh.core.fluid.FluidTankCore;
import cofh.core.util.helpers.FluidHelper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import powercrystals.minefactoryreloaded.core.ArrayHashList;
import powercrystals.minefactoryreloaded.core.IGrid;
import powercrystals.minefactoryreloaded.core.MFRUtil;
import powercrystals.minefactoryreloaded.net.GridTickHandler;

import java.util.Iterator;
import java.util.LinkedHashSet;

public class FluidNetwork implements IGrid {

	public static final int TRANSFER_RATE = 80;
	public static final int STORAGE_CAPACITY = TRANSFER_RATE * 6;
	FluidTankCore storage = new FluidTankCore(320);

	private TileEntityPlasticPipe master;
	private ArrayHashList<TileEntityPlasticPipe> nodeSet = new ArrayHashList<>();
	private LinkedHashSet<TileEntityPlasticPipe> conduitSet = new LinkedHashSet<>();

	private boolean regenerating = false;
	private int overflowSelector;

	static final GridTickHandler<FluidNetwork, TileEntityPlasticPipe> HANDLER =	GridTickHandler.fluid;

	private FluidNetwork() {
		storage.setCapacity(0);
	}

	public FluidNetwork(TileEntityPlasticPipe base) {
		this();
		regenerating = true;
		addToGrid(base);
		regenerating = false;
	}

	public boolean addPipe(TileEntityPlasticPipe pipe) {

		if(pipe.grid == this)
			return true;

		if (!addToGrid(pipe))
			return false;

		updateNodeData(pipe);

		return true;
	}

	public void updateNodeData(TileEntityPlasticPipe pipe) {

		if (pipe.isNode) {
			addNode(pipe);
		} else if (!nodeSet.isEmpty()) {
			removeNode(pipe);
		}
	}

	private void addNode(TileEntityPlasticPipe pipe) {

		if (nodeSet.add(pipe)) {
			if (!hasMaster()) {
				setMaster(pipe);
				HANDLER.addGrid(this);
			}
			rebalanceGrid();
			fillGrid(pipe.fluidForGrid);
			setPipeFluid(pipe);
		}
	}

	private boolean addToGrid(TileEntityPlasticPipe pipe) {

		if (pipe.grid != null) {
			return mergeGrid(pipe.grid);
		}

		if (pipe.fluidForGrid != null) {
			if (FluidHelper.isFluidEqualOrNull(pipe.fluidForGrid, storage.getFluid())) {
				pipe.grid = this;
				storage.fill(new FluidStack(pipe.fluidForGrid, 0), true);
			} else {
				return false;
			}
			conduitSet.add(pipe);
		} else {
			pipe.grid = this;
			setPipeFluid(pipe);
			conduitSet.add(pipe);
		}
		return true;
	}

	public boolean canMergeGrid(FluidNetwork grid) {
		if (grid == null) return false;
		return FluidHelper.isFluidEqualOrNull(grid.storage.getFluid(), storage.getFluid());
	}

	public boolean mergeGrid(FluidNetwork grid) {

		if (grid == this) return true;
		if (!canMergeGrid(grid)) return false;

		if (storage.getFluid() == null && grid.storage.getFluid() != null) {
			return grid.mergeGrid(this);
		}
		grid.destroyGrid();

		if (!regenerating && grid.regenerating) {
			regenerate();
		}

		boolean eitherRegenerating = regenerating || grid.regenerating;
		regenerating = true;
		for (TileEntityPlasticPipe pipe : grid.conduitSet)
			addPipe(pipe);
		regenerating = eitherRegenerating;

		grid.conduitSet.clear();
		grid.nodeSet.clear();

		return true;
	}

	public int getNodeShare(TileEntityPlasticPipe pipe) {
		int size = nodeSet.size();
		if (size == 1)
			return storage.getFluidAmount();
		int amt = storage.getFluidAmount() / size;
		if (isMaster(pipe)) amt += storage.getFluidAmount() % size;
		return amt;
	}

	private boolean isMaster(TileEntityPlasticPipe pipe) {

		return master == pipe;
	}

	public void setMaster(TileEntityPlasticPipe pipe) {

		master = pipe;
	}
	private boolean hasMaster() {

		return master != null;
	}

	public void fillGrid(FluidStack fluid) {

		storage.fill(fluid, true);
	}

	public void setPipeFluid(TileEntityPlasticPipe pipe) {

		pipe.fluidForGrid = storage.drain(0, false);
	}

	public void destroyGrid() {
		master = null;
		regenerating = true;
		for (TileEntityPlasticPipe curCond : nodeSet)
			destroyNode(curCond);
		for (TileEntityPlasticPipe curCond : conduitSet) {
			if (!curCond.isNode)
				destroyConduit(curCond);
		}
		HANDLER.removeGrid(this);
	}

	public void destroyNode(TileEntityPlasticPipe cond) {
		cond.fluidForGrid = storage.drain(getNodeShare(cond), false);
		cond.grid = null;
	}


	public void destroyConduit(TileEntityPlasticPipe cond) {
		cond.fluidForGrid = storage.drain(0, false);
		cond.grid = null;
	}

	public void rebalanceGrid() {
		storage.setCapacity(getNodeCount() * STORAGE_CAPACITY);
	}

	public void regenerate() {
		regenerating = true;
		HANDLER.regenerateGrid(this);
	}

	public void removePipe(TileEntityPlasticPipe pipe) {
		conduitSet.remove(pipe);
		if (!nodeSet.isEmpty()) {
			if (!removeNode(pipe)) {
				pipe.fluidForGrid = storage.drain(0, false);
			}
		} else {
			pipe.fluidForGrid = storage.drain(0, false);
		}
	}

	private boolean removeNode(TileEntityPlasticPipe node) {

		int share = getNodeShare(node);
		if (!nodeSet.remove(node))
			return false;

		node.fluidForGrid = storage.drain(share, true);

		if (isMaster(node)) {
			if (nodeSet.isEmpty()) {
				master = null;
				HANDLER.removeGrid(this);
			} else {
				master = nodeSet.get(0);
			}
		}

		rebalanceGrid();

		return true;
	}

	int getNodeCount() {
		return nodeSet.size();
	}

	@Override
	public void markSweep() {
		destroyGrid();
		if (conduitSet.isEmpty())
			return;
		TileEntityPlasticPipe main = conduitSet.iterator().next();
		LinkedHashSet<TileEntityPlasticPipe> oldSet = conduitSet;
		nodeSet.clear();
		conduitSet = new LinkedHashSet<>(Math.min(oldSet.size() / 6, 5));
		rebalanceGrid();

		LinkedHashSet<TileEntityPlasticPipe> toCheck = new LinkedHashSet<>();
		LinkedHashSet<TileEntityPlasticPipe> checked = new LinkedHashSet<>();
		EnumFacing[] dir = EnumFacing.VALUES;
		toCheck.add(main);
		checked.add(main);
		while (!toCheck.isEmpty()) {
			Iterator<TileEntityPlasticPipe> it = toCheck.iterator();
			main = it.next();
			it.remove();
			addPipe(main);
			World world = main.getWorld();
			for (int i = 6; i --> 0; ) {
				BlockPos bp = main.getPos().offset(dir[i]);
				if (world.isBlockLoaded(bp)) {
					TileEntity te = MFRUtil.getTile(world, bp);
					if (te instanceof TileEntityPlasticPipe) {
						TileEntityPlasticPipe tep = (TileEntityPlasticPipe)te;
						if (main.canInterface(dir[i])
								&& FluidHelper.isFluidEqualOrNull(main.fluidForGrid, tep.fluidForGrid) && checked.add(tep))
							toCheck.add(tep);
					}
				}
			}
			oldSet.remove(main);
		}
		if (!oldSet.isEmpty()) {
			FluidNetwork newGrid = new FluidNetwork();
			newGrid.conduitSet = oldSet;
			newGrid.regenerating = true;
			newGrid.markSweep();
		}
		if (nodeSet.isEmpty())
			HANDLER.removeGrid(this);
		else
			HANDLER.addGrid(this);
		regenerating = false;
	}

	@Override
	public void doGridPreUpdate() {
		if (regenerating)
			return;
		if (nodeSet.isEmpty()) {
			HANDLER.removeGrid(this);
			return;
		}
		FluidTankCore tank = storage;
		if (tank.getSpace() <= 0)
			return;
		EnumFacing[] directions = EnumFacing.VALUES;

		for (TileEntityPlasticPipe cond : nodeSet)
			for (int i = 6; i --> 0; )
				cond.extract(directions[i], tank);
	}

	@Override
	public void doGridUpdate() {
		if (regenerating)
			return;
		if (nodeSet.isEmpty()) {
			HANDLER.removeGrid(this);
			return;
		}

		if (storage.getFluidAmount() <= 0)
			return;
		EnumFacing[] directions = EnumFacing.VALUES;
		int size = nodeSet.size();
		int toDistribute = storage.getFluidAmount() / size;
		int sideDistribute = toDistribute / 6;
		Fluid fluid = storage.getFluid().getFluid();

		FluidStack stack = storage.drain(sideDistribute, false);

		int overflow = overflowSelector = (overflowSelector + 1) % size;
		TileEntityPlasticPipe master = nodeSet.get(overflow);

		if (sideDistribute > 0) {
			transferToNodes(fluid, stack, master);
		}

		toDistribute += storage.getFluidAmount() % size;
		sideDistribute = toDistribute / 6;
		stack.amount = sideDistribute;

		if (sideDistribute > 0) {
			int e = 0;
			for (int i = 6; i --> 0; )
				e += master.transfer(directions[i], stack, fluid);
			if (e > 0) storage.drain(e, true);
		} else if (toDistribute > 0) {
			stack.amount = toDistribute;
			int e = 0;
			for (int i = 6; i --> 0 && e < toDistribute; ) {
				e += master.transfer(directions[i], stack, fluid);
				stack.amount = toDistribute - e;
			}
			if (e > 0) storage.drain(e, true);
		}
	}

	private void transferToNodes(Fluid fluid, FluidStack stack, TileEntityPlasticPipe master) {

		for (TileEntityPlasticPipe node : nodeSet) {
			if (node != master) {
				int toDrain = 0;
				for (int i = 6; i --> 0; )
					toDrain += node.transfer(EnumFacing.VALUES[i], stack, fluid);
				if (toDrain > 0) storage.drain(toDrain, true);
			}
		}
	}

	public int getConduitCount() {
		return conduitSet.size();
	}

	@Override
	public String toString() {
		return "FluidNetwork@" + Integer.toString(hashCode()) + "; master:" + master +
				"; regenerating:" + regenerating + "; isTicking:" + HANDLER.isGridTicking(this);
	}

}
