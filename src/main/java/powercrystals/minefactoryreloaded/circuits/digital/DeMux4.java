package powercrystals.minefactoryreloaded.circuits.digital;

import powercrystals.minefactoryreloaded.circuits.base.StatelessCircuit;

public class DeMux4 extends StatelessCircuit
{
	@Override
	public byte getInputCount()
	{
		return 3;
	}
	
	@Override
	public byte getOutputCount()
	{
		return 4;
	}
	
	@Override
	public int[] recalculateOutputValues(long worldTime, int[] inputValues)
	{
		int channel = 0;
		if(inputValues[1] > 0 && inputValues[2] == 0) channel = 1;
		else if(inputValues[1] == 0 && inputValues[2] > 0) channel = 2;
		else if(inputValues[1] > 0 && inputValues[2] > 0) channel = 3;
		int[] output = new int[4];
		output[channel] = inputValues[0];
		
		return output;
	}
	
	@Override
	public String getUnlocalizedName()
	{
		return "circuit.mfr.demux.4";
	}
	
	@Override
	public String getInputPinLabel(int pin)
	{
		return pin == 0 ? "I" : pin == 1 ? "S0" : "S1";
	}
	
	@Override
	public String getOutputPinLabel(int pin)
	{
		return "O" + pin;
	}
}
