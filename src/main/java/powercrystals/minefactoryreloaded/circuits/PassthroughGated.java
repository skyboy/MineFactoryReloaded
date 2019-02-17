package powercrystals.minefactoryreloaded.circuits;

import powercrystals.minefactoryreloaded.circuits.base.StatelessCircuit;

public class PassthroughGated extends StatelessCircuit
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
		int output = inputValues[1] > 0 ? inputValues[0] : 0;
		return new int[] { output };
	}
	
	@Override
	public String getUnlocalizedName()
	{
		return "circuit.mfr.passthrough.gated";
	}
	
	@Override
	public String getInputPinLabel(int pin)
	{
		return pin == 0 ? "I" : "E";
	}
	
	@Override
	public String getOutputPinLabel(int pin)
	{
		return "O";
	}
}
