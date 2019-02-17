package powercrystals.minefactoryreloaded.circuits.digital;

import powercrystals.minefactoryreloaded.circuits.base.StatelessCircuit;

public class AdderDigitalFull extends StatelessCircuit
{
	@Override
	public byte getInputCount()
	{
		return 3;
	}
	
	@Override
	public byte getOutputCount()
	{
		return 2;
	}
	
	@Override
	public int[] recalculateOutputValues(long worldTime, int[] inputValues)
	{
		int s = ((inputValues[0] > 0 ^ inputValues[1] > 0) ^ inputValues[2] > 0) ? 15 : 0;
		int c = ((inputValues[0] > 0 && inputValues[1] > 0) || ((inputValues[0] > 0 ^ inputValues[1] > 0) && inputValues[2] > 0)) ? 15 : 0;
		return new int[] { s, c };
	}
	
	@Override
	public String getUnlocalizedName()
	{
		return "circuit.mfr.adder.digital.full";
	}
	
	@Override
	public String getInputPinLabel(int pin)
	{
		return pin == 0 ? "A" : pin == 1 ? "B" : "Cin";
	}
	
	@Override
	public String getOutputPinLabel(int pin)
	{
		return pin == 0 ? "S" : "Cout";
	}
}
