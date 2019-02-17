package powercrystals.minefactoryreloaded.circuits.wave;

import powercrystals.minefactoryreloaded.circuits.base.StatelessCircuit;

public class SawtoothRising extends StatelessCircuit
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
		return new int[] { (int)(worldTime % 16) };
	}
	
	@Override
	public String getUnlocalizedName()
	{
		return "circuit.mfr.wavegenerator.sawtooth.rising";
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
