package powercrystals.minefactoryreloaded.circuits.digital;

import powercrystals.minefactoryreloaded.circuits.base.StatelessCircuit;

public class Mux16Analog extends StatelessCircuit
{
	@Override
	public byte getInputCount()
	{
		return 16;
	}
	
	@Override
	public byte getOutputCount()
	{
		return 1;
	}
	
	@Override
	public int[] recalculateOutputValues(long worldTime, int[] inputValues)
	{
		int channel = Math.max(Math.min(inputValues[15], 14), 0);
		
		return new int[] { inputValues[channel] };
	}
	
	@Override
	public String getUnlocalizedName()
	{
		return "circuit.mfr.mux.16.analog";
	}
	
	@Override
	public String getInputPinLabel(int pin)
	{
		return pin == 15 ? "S" : "I" + pin;
	}
	
	@Override
	public String getOutputPinLabel(int pin)
	{
		return "O";
	}
}
