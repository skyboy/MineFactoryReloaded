package powercrystals.minefactoryreloaded.tile.machine.power;

import cofh.core.fluid.FluidTankCore;

import net.minecraftforge.fluids.FluidStack;

import powercrystals.minefactoryreloaded.setup.MFRFluids;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.base.TileEntityLiquidGenerator;

public class TileEntitySteamTurbine extends TileEntityLiquidGenerator
{
	public static final int liquidConsumedPerTick = Machine.SteamTurbine.getActivationEnergy() / 2;
	public static final int ticksBetweenConsumption = 1;

	public TileEntitySteamTurbine()
	{
		super(Machine.SteamTurbine, liquidConsumedPerTick, ticksBetweenConsumption);
		_tanks[0].setLock(MFRFluids.getFluid("steam"));
	}
	
	@Override
	protected FluidTankCore[] createTanks()
	{
		return new FluidTankCore[] {new FluidTankCore(BUCKET_VOLUME * 8)};
	}

	@Override
	protected boolean isFluidFuel(FluidStack fuel)
	{
		String name = getFluidName(fuel);
		if (name == null)
			return false;
		return name.equals("steam");
	}
}
