package powercrystals.minefactoryreloaded.circuits.analog;

import powercrystals.minefactoryreloaded.circuits.base.StatelessCircuit;

public class AdderAnalog extends StatelessCircuit
{
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
		return new int[] { inputValues[0] + inputValues[1] };
	}
	
	@Override
	public String getUnlocalizedName()
	{
		return "circuit.mfr.adder.analog";
	}
	
	@Override
	public String getInputPinLabel(int pin)
	{
		return "I" + pin;
	}
	
	@Override
	public String getOutputPinLabel(int pin)
	{
		return "O";
	}
}
