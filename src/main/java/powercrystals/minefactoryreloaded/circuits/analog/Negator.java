package powercrystals.minefactoryreloaded.circuits.analog;

import powercrystals.minefactoryreloaded.circuits.base.StatelessCircuit;

public class Negator extends StatelessCircuit
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
		return new int[] { -inputValues[0] };
	}
	
	@Override
	public String getUnlocalizedName()
	{
		return "circuit.mfr.negator";
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