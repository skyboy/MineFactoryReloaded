package powercrystals.minefactoryreloaded.circuits.analog;

import java.util.Random;

import powercrystals.minefactoryreloaded.circuits.base.StatelessCircuit;

public class RandomizerAnalog extends StatelessCircuit
{
	private Random _rand;
	
	@Override
	public byte getInputCount()
	{
		return 2;
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
		
		if(inputValues[1] - inputValues[0] <= 0)
		{
			return new int[] { inputValues[0] };
		}
		else
		{
			return new int[] { _rand.nextInt(inputValues[1] - inputValues[0]) + inputValues[0] };
		}
	}
	
	@Override
	public String getUnlocalizedName()
	{
		return "circuit.mfr.randomizer.analog";
	}
	
	@Override
	public String getInputPinLabel(int pin)
	{
		return pin == 0 ? "Min" : "Max";
	}
	
	@Override
	public String getOutputPinLabel(int pin)
	{
		return "Q";
	}
}