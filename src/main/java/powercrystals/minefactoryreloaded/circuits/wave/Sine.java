package powercrystals.minefactoryreloaded.circuits.wave;

import powercrystals.minefactoryreloaded.circuits.base.StatelessCircuit;

public class Sine extends StatelessCircuit
{
	@Override
	public byte getInputCount()
	{
		return 0;
	}
	
	@Override
	public byte getOutputCount()
	{
		return 1;
	}
	
	@Override
	public int[] recalculateOutputValues(long worldTime, int[] inputValues)
	{
		int value = (int)(7.5 * (Math.sin(Math.PI / 8.0 * (worldTime % 16)) + 1));
		return new int[] { value };
	}
	
	@Override
	public String getUnlocalizedName()
	{
		return "circuit.mfr.wavegenerator.sine";
	}
	
	@Override
	public String getInputPinLabel(int pin)
	{
		return "";
	}
	
	@Override
	public String getOutputPinLabel(int pin)
	{
		return "Q";
	}
}
