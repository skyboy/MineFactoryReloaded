package powercrystals.minefactoryreloaded.circuits.digital;

import java.util.Random;

import powercrystals.minefactoryreloaded.circuits.base.StatelessCircuit;

public class RandomizerDigital extends StatelessCircuit
{
	private Random _rand;
	
	@Override
	public byte getInputCount()
	{
		return 0;
	}
	
	@Override
	public byte getOutputCount()
	{
		return 1;
	}
	
	@Override
	public int[] recalculateOutputValues(long worldTime, int[] inputValues)
	{
		if(_rand == null)
		{
			_rand = new Random(worldTime);
		}
		if(_rand.nextBoolean())
		{
			return new int[] { 15 };
		}
		else
		{
			return new int[] { 0 };
		}
	}
	
	@Override
	public String getUnlocalizedName()
	{
		return "circuit.mfr.randomizer.digital";
	}
	
	@Override
	public String getInputPinLabel(int pin)
	{
		return "";
	}
	
	@Override
	public String getOutputPinLabel(int pin)
	{
		return "Q";
	}
}