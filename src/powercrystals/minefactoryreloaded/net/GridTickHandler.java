package powercrystals.minefactoryreloaded.net;

import cpw.mods.fml.common.IScheduledTickHandler;
import cpw.mods.fml.common.TickType;

import java.util.EnumSet;
import java.util.LinkedHashSet;

import powercrystals.minefactoryreloaded.tile.rednet.RedstoneEnergyNetwork;
import powercrystals.minefactoryreloaded.tile.rednet.TileEntityRedNetEnergy;

public class GridTickHandler implements IScheduledTickHandler
{
	private EnumSet<TickType> ticks = EnumSet.of(TickType.SERVER);
	
	private static LinkedHashSet<RedstoneEnergyNetwork> tickingGridsToRegenerate =
			new LinkedHashSet<RedstoneEnergyNetwork>();
	private static LinkedHashSet<RedstoneEnergyNetwork> tickingGridsToAdd =
			new LinkedHashSet<RedstoneEnergyNetwork>();
	private static LinkedHashSet<RedstoneEnergyNetwork> tickingGrids =
			new LinkedHashSet<RedstoneEnergyNetwork>();
	private static LinkedHashSet<RedstoneEnergyNetwork> tickingGridsToRemove =
			new LinkedHashSet<RedstoneEnergyNetwork>();
	
	private static LinkedHashSet<TileEntityRedNetEnergy> conduit =
			new LinkedHashSet<TileEntityRedNetEnergy>();
	private static LinkedHashSet<TileEntityRedNetEnergy> conduitToAdd =
			new LinkedHashSet<TileEntityRedNetEnergy>();
	
	static GridTickHandler instance = new GridTickHandler();
	
	public static void addGrid(RedstoneEnergyNetwork grid)
	{
		tickingGridsToAdd.add(grid);
		tickingGridsToRemove.remove(grid);
	}
	
	public static void removeGrid(RedstoneEnergyNetwork grid)
	{
		tickingGridsToRemove.add(grid);
		tickingGridsToAdd.remove(grid);
	}
	
	public static void regenerateGrid(RedstoneEnergyNetwork grid)
	{
		tickingGridsToRegenerate.add(grid);
	}
	
	public static boolean isGridTicking(RedstoneEnergyNetwork grid)
	{
		return tickingGrids.contains(grid);
	}
	
	public static void addConduit(TileEntityRedNetEnergy grid)
	{
		conduitToAdd.add(grid);
	}
	
	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData)
	{
		if (!tickingGridsToRegenerate.isEmpty())
		synchronized (tickingGridsToRegenerate) {
			for (RedstoneEnergyNetwork grid : tickingGridsToRegenerate)
				grid.markSweep();
		}
				
		if (!tickingGrids.isEmpty())
			for (RedstoneEnergyNetwork grid : tickingGrids)
				grid.doGridPreUpdate();
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData)
	{
		if (!tickingGridsToRemove.isEmpty())
		synchronized(tickingGridsToRemove)
		{
			tickingGrids.removeAll(tickingGridsToRemove);
			tickingGridsToRemove.clear();
		}
		
		if (!tickingGridsToAdd.isEmpty())
		synchronized(tickingGridsToAdd)
		{
			tickingGrids.addAll(tickingGridsToAdd);
			tickingGridsToAdd.clear();
		}
		
		if (!tickingGrids.isEmpty())
			for (RedstoneEnergyNetwork grid : tickingGrids)
				grid.doGridUpdate();
		
		if (!conduitToAdd.isEmpty())
		synchronized(conduitToAdd)
		{
			conduit.addAll(conduitToAdd);
			conduitToAdd.clear();
		}
		if (!conduit.isEmpty())
		{
			for (TileEntityRedNetEnergy cond : conduit)
				cond.firstTick();
			conduit.clear();
		}
	}

	@Override
	public EnumSet<TickType> ticks()
	{
		return ticks;
	}

	@Override
	public String getLabel()
	{
		return "MFR EnergyNet";
	}

	@Override
	public int nextTickSpacing()
	{
		return 1;
	}
}
