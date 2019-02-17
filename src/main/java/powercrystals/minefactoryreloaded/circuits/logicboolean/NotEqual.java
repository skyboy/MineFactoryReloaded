package powercrystals.minefactoryreloaded.circuits.logicboolean;

import powercrystals.minefactoryreloaded.circuits.base.StatelessCircuit;

public class NotEqual extends StatelessCircuit
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
		return new int[] { inputValues[0] != inputValues[1] ? 15 : 0 };
	}
	
	@Override
	public String getUnlocalizedName()
	{
		return "circuit.mfr.boolean.notequal";
	}
	
	@Override
	public String getInputPinLabel(int pin)
	{
		return pin == 0 ? "A" : "B";
	}
	
	@Override
	public String getOutputPinLabel(int pin)
	{
		return "Q";
	}
}
