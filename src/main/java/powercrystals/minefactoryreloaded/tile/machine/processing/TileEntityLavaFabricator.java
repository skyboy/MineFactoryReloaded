package powercrystals.minefactoryreloaded.tile.machine.processing;

import powercrystals.minefactoryreloaded.setup.MFRFluids;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.base.TileEntityLiquidFabricator;

public class TileEntityLavaFabricator extends TileEntityLiquidFabricator
{
	public TileEntityLavaFabricator()
	{
		super(MFRFluids.getFluid("lava"), 20, Machine.LavaFabricator);
	}
}
