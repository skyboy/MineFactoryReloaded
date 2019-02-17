package powercrystals.minefactoryreloaded.circuits.logic;

import powercrystals.minefactoryreloaded.circuits.base.StatelessCircuit;

public class Nand2 extends StatelessCircuit
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
		if(!(inputValues[0] > 0 && inputValues[1] > 0))
		{
			return new int[] { 15 };
		}
		return new int[] { 0 };
	}
	
	@Override
	public String getUnlocalizedName()
	{
		return "circuit.mfr.nand.2";
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
