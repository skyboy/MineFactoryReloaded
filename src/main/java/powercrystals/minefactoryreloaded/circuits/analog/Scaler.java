package powercrystals.minefactoryreloaded.circuits.analog;

import powercrystals.minefactoryreloaded.circuits.base.StatelessCircuit;

public class Scaler extends StatelessCircuit
{
	private static String[] _inputPinNames = new String[] { "I", "IMn", "IMx", "OMn", "OMx" };
	
	@Override
	public byte getInputCount()
	{
		return 5;
	}
	
	@Override
	public byte getOutputCount()
	{
		return 1;
	}
	
	@Override
	public int[] recalculateOutputValues(long worldTime, int[] inputValues)
	{
		if(inputValues[4] - inputValues[3] == 0 || inputValues[2] - inputValues[1] == 0)
		{
			return new int [] { inputValues[0] };
		}
		else
		{
			return new int[] { (inputValues[0] - inputValues[1]) * (inputValues[4] - inputValues[3]) / (inputValues[2] - inputValues[1]) + inputValues[3] };
		}
	}
	
	@Override
	public String getUnlocalizedName()
	{
		return "circuit.mfr.scaler";
	}
	
	@Override
	public String getInputPinLabel(int pin)
	{
		return _inputPinNames[pin];
	}
	
	@Override
	public String getOutputPinLabel(int pin)
	{
		return "O" + pin;
	}
}
