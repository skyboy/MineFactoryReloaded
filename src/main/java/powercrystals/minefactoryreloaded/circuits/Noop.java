package powercrystals.minefactoryreloaded.circuits;

import powercrystals.minefactoryreloaded.circuits.base.StatelessCircuit;

public class Noop extends StatelessCircuit
{
	@Override
	public byte getInputCount()
	{
		return 0;
	}

	@Override
	public byte getOutputCount()
	{
		return 0;
	}

	@Override
	public int[] recalculateOutputValues(long worldTime, int[] inputValues)
	{
		return new int[getOutputCount()];
	}

	@Override
	public String getUnlocalizedName()
	{
		return "circuit.mfr.noop";
	}

	@Override
	public String getInputPinLabel(int pin)
	{
		return "";
	}

	@Override
	public String getOutputPinLabel(int pin)
	{
		return "";
	}
}
