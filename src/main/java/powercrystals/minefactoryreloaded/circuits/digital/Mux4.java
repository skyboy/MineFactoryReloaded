package powercrystals.minefactoryreloaded.circuits.digital;

import powercrystals.minefactoryreloaded.circuits.base.StatelessCircuit;

public class Mux4 extends StatelessCircuit
{
	private static String[] _inputPinNames = new String[] { "I0", "I1", "I2", "I3", "S0", "S1" };
	
	@Override
	public byte getInputCount()
	{
		return 6;
	}
	
	@Override
	public byte getOutputCount()
	{
		return 1;
	}
	
	@Override
	public int[] recalculateOutputValues(long worldTime, int[] inputValues)
	{
		int channel = 0;
		
		if(inputValues[4] > 0 && inputValues[5] == 0) channel = 1;
		else if(inputValues[4] == 0 && inputValues[5] > 0) channel = 2;
		else if(inputValues[4] > 0 && inputValues[5] > 0) channel = 3;
		
		return new int[] { inputValues[channel] };
	}
	
	@Override
	public String getUnlocalizedName()
	{
		return "circuit.mfr.mux.4";
	}
	
	@Override
	public String getInputPinLabel(int pin)
	{
		return _inputPinNames[pin];
	}
	
	@Override
	public String getOutputPinLabel(int pin)
	{
		return "O";
	}
}
