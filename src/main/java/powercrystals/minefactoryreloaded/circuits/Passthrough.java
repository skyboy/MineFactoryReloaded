package powercrystals.minefactoryreloaded.circuits;

import powercrystals.minefactoryreloaded.circuits.base.StatelessCircuit;

public class Passthrough extends StatelessCircuit
{
	@Override
	public byte getInputCount()
	{
		return 1;
	}
	
	@Override
	public byte getOutputCount()
	{
		return 1;
	}
	
	@Override
	public int[] recalculateOutputValues(long worldTime, int[] inputValues)
	{
		return new int[] { inputValues[0] };
	}
	
	@Override
	public String getUnlocalizedName()
	{
		return "circuit.mfr.passthrough";
	}
	
	@Override
	public String getInputPinLabel(int pin)
	{
		return "I";
	}
	
	@Override
	public String getOutputPinLabel(int pin)
	{
		return "O";
	}
}
