package powercrystals.minefactoryreloaded.tile.machine.power;

import net.minecraftforge.fluids.FluidStack;

import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.base.TileEntityLiquidGenerator;

public class TileEntityBioFuelGenerator extends TileEntityLiquidGenerator
{
	public static final int liquidConsumedPerTick = 1;
	public static final int ticksBetweenConsumption = 7;
	
	public TileEntityBioFuelGenerator()
	{
		super(Machine.BioFuelGenerator, liquidConsumedPerTick, ticksBetweenConsumption);
	}
	
	@Override
	protected boolean isFluidFuel(FluidStack fuel)
	{
		String name = getFluidName(fuel);
		if (name == null)
			return false;
		return name.equals("biofuel") || name.equals("bioethanol");
	}
}
