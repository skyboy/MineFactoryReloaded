package powercrystals.minefactoryreloaded.net;

import com.google.common.base.Throwables;
import net.minecraft.util.ReportedException;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;
import powercrystals.minefactoryreloaded.core.IGrid;
import powercrystals.minefactoryreloaded.core.IGridController;
import powercrystals.minefactoryreloaded.core.INode;
import powercrystals.minefactoryreloaded.tile.rednet.RedstoneEnergyNetwork;
import powercrystals.minefactoryreloaded.tile.rednet.RedstoneNetwork;
import powercrystals.minefactoryreloaded.tile.rednet.TileEntityRedNetCable;
import powercrystals.minefactoryreloaded.tile.rednet.TileEntityRedNetEnergy;
import powercrystals.minefactoryreloaded.tile.transport.FluidNetwork;
import powercrystals.minefactoryreloaded.tile.transport.TileEntityPlasticPipe;

import java.util.LinkedHashSet;

public class GridTickHandler<G extends IGrid, N extends INode> implements IGridController {

	public static final GridTickHandler<RedstoneEnergyNetwork, TileEntityRedNetEnergy> energy = new GridTickHandler<>("Energy");
	public static final GridTickHandler<RedstoneNetwork, TileEntityRedNetCable> redstone = new GridTickHandler<>("Redstone");
	public static final GridTickHandler<FluidNetwork, TileEntityPlasticPipe> fluid = new GridTickHandler<>("Fluid");

	private final Object addRemoveLock = new Object();

	private final LinkedHashSet<G> tickingGridsToRegenerate = new LinkedHashSet<>();
	private final LinkedHashSet<G> tickingGridsToAdd = new LinkedHashSet<>();
	private final LinkedHashSet<G> tickingGridsToRemove = new LinkedHashSet<>();
	private final LinkedHashSet<G> tickingGrids = new LinkedHashSet<>();

	private final LinkedHashSet<N> conduitToAdd = new LinkedHashSet<>();
	private final LinkedHashSet<N> conduitToUpd = new LinkedHashSet<>();
	private final LinkedHashSet<N> conduit = new LinkedHashSet<>();

	private final String label;

	public GridTickHandler(String name) {

		if (name == null)
			throw new IllegalArgumentException("name is null");
		label = "GridTickHandler[" + name + "]";
	}

	public void addGrid(G grid) {

		synchronized (addRemoveLock) {
			tickingGridsToAdd.add(grid);
			tickingGridsToRemove.remove(grid);
		}
	}

	public void removeGrid(G grid) {

		synchronized (addRemoveLock) {
			tickingGridsToRemove.add(grid);
			tickingGridsToAdd.remove(grid);
		}
	}

	public void regenerateGrid(G grid) {

		synchronized (tickingGridsToRegenerate) {
			tickingGridsToRegenerate.add(grid);
		}
	}

	public boolean isGridTicking(G grid) {

		return tickingGrids.contains(grid);
	}

	public void addConduitForTick(N node) {

		synchronized (conduitToAdd) {
			conduitToAdd.add(node);
		}
	}

	public void addConduitForUpdate(N node) {

		synchronized (conduitToUpd) {
			conduitToUpd.add(node);
		}
	}

	@SubscribeEvent
	public void tick(ServerTickEvent evt) {

		// TODO: this needs split up into groups per-world when worlds are threaded
		if (evt.phase == Phase.START)
			tickStart();
		else
			tickEnd();
	}

	public void clear() {

		clearSetSynchronized(tickingGridsToRegenerate);
		if (!tickingGridsToAdd.isEmpty() || !tickingGridsToRemove.isEmpty()) {
			synchronized (addRemoveLock) {
				tickingGridsToAdd.clear();
				tickingGridsToRemove.clear();
			}
		}
		clearSetSynchronized(tickingGrids);

		clearSetSynchronized(conduit);
		clearSetSynchronized(conduitToAdd);
		clearSetSynchronized(conduitToUpd);
	}

	private void clearSetSynchronized(LinkedHashSet set) {

		if(!set.isEmpty()) {
			synchronized (set) {
				set.clear();
			}
		}
	}

	public void tickStart() {

		//{ Grids that have had significant conduits removed and need to rebuild/split
		if (!tickingGridsToRegenerate.isEmpty())
			synchronized (tickingGridsToRegenerate) {
				for (G grid : tickingGridsToRegenerate)
					grid.markSweep();
				tickingGridsToRegenerate.clear();
			}
		//}

		//{ Updating internal types of conduits
		// this pass is needed to handle issues with threading
		if (!conduitToUpd.isEmpty())
			synchronized (conduitToUpd) {
				conduit.addAll(conduitToUpd);
				conduitToUpd.clear();
			}

		if (!conduit.isEmpty()) {
			N cond = null;
			try {
				for (N aConduit : conduit) {
					cond = aConduit;
					if (!cond.isNotValid())
						cond.updateInternalTypes(this);
				}
				conduit.clear();
			} catch (Throwable t) {
				RuntimeException error = new RuntimeException("Crashing on conduit " + cond, t);
				if (t instanceof ReportedException) {
					t.addSuppressed(error);
					Throwables.propagate(t);
				}
				throw error;
			}
		}
		//}

		//{ Early update pass to extract energy from sources
		if (!tickingGrids.isEmpty())
			for (G grid : tickingGrids)
				grid.doGridPreUpdate();
		//}
	}

	public void tickEnd() {

		//{ Changes in what grids are being ticked
		if (!tickingGridsToRemove.isEmpty())
			synchronized (addRemoveLock) {
				tickingGrids.removeAll(tickingGridsToRemove);
				tickingGridsToRemove.clear();
			}

		if (!tickingGridsToAdd.isEmpty())
			synchronized (addRemoveLock) {
				tickingGrids.addAll(tickingGridsToAdd);
				tickingGridsToAdd.clear();
			}
		//}

		//{ Ticking grids to transfer energy/etc.
		if (!tickingGrids.isEmpty())
			for (G grid : tickingGrids)
				grid.doGridUpdate();
		//}

		//{ Initial update tick for conduits added to the world
		if (!conduitToAdd.isEmpty())
			synchronized (conduitToAdd) {
				conduit.addAll(conduitToAdd);
				conduitToAdd.clear();
			}

		if (!conduit.isEmpty()) {
			N cond = null;
			try {
				for (N aConduit : conduit) {
					cond = aConduit;
					if (!cond.isNotValid())
						cond.firstTick(this);
				}
				conduit.clear();
			} catch (Throwable t) {
				RuntimeException error = new RuntimeException("Crashing on conduit " + cond, t);
				if (t instanceof ReportedException) {
					t.addSuppressed(error);
					Throwables.propagate(t);
				}
				throw error;
			}
		}
		//}
	}

	@Override
	public String toString() {

		return label + "@" + hashCode();
	}

}
