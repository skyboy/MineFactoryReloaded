package powercrystals.minefactoryreloaded.circuits.digital;

import powercrystals.minefactoryreloaded.circuits.base.StatelessCircuit;

public class DeMux16Analog extends StatelessCircuit
{
	@Override
	public byte getInputCount()
	{
		return 2;
	}
	
	@Override
	public byte getOutputCount()
	{
		return 16;
	}
	
	@Override
	public int[] recalculateOutputValues(long worldTime, int[] inputValues)
	{
		int channel = Math.max(Math.min(inputValues[1], 15), 0);
		
		int[] output = new int[16];
		output[channel] = inputValues[0];
		
		return output;
	}
	
	@Override
	public String getUnlocalizedName()
	{
		return "circuit.mfr.demux.16.analog";
	}
	
	@Override
	public String getInputPinLabel(int pin)
	{
		return pin == 0 ? "I" : "S";
	}
	
	@Override
	public String getOutputPinLabel(int pin)
	{
		return "O" + pin;
	}
}
