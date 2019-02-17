package powercrystals.minefactoryreloaded.circuits.digital;

import powercrystals.minefactoryreloaded.circuits.base.StatelessCircuit;

public class AdderDigitalHalf extends StatelessCircuit
{
	@Override
	public byte getInputCount()
	{
		return 2;
	}
	
	@Override
	public byte getOutputCount()
	{
		return 2;
	}
	
	@Override
	public int[] recalculateOutputValues(long worldTime, int[] inputValues)
	{
		int c = (inputValues[0] > 0 && inputValues[1] > 0) ? 15 : 0;
		int s = (inputValues[0] > 0 ^ inputValues[1] > 0) ? 15 : 0;
		return new int[] { s, c };
	}
	
	@Override
	public String getUnlocalizedName()
	{
		return "circuit.mfr.adder.digital.half";
	}
	
	@Override
	public String getInputPinLabel(int pin)
	{
		return pin == 0 ? "A" : "B";
	}
	
	@Override
	public String getOutputPinLabel(int pin)
	{
		return pin == 0 ? "S" : "C";
	}
}
