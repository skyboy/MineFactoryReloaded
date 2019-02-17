package powercrystals.minefactoryreloaded.circuits.timing;

import net.minecraft.nbt.NBTTagCompound;
import powercrystals.minefactoryreloaded.api.rednet.IRedNetLogicCircuit;

public class OneShot implements IRedNetLogicCircuit
{
	private boolean _lastState;
	
	@Override
	public byte getInputCount()
	{
		return 1;
	}
	
	@Override
	public byte getOutputCount()
	{
		return 1;
	}
	
	@Override
	public int[] recalculateOutputValues(long worldTime, int[] inputValues)
	{
		int output;
		if(inputValues[0] > 0 && !_lastState)
		{
			output = 15;
		}
		else
		{
			output = 0;
		}
		_lastState = inputValues[0] > 0;
		return new int[] { output };
	}
	
	@Override
	public String getUnlocalizedName()
	{
		return "circuit.mfr.oneshot";
	}
	
	@Override
	public String getInputPinLabel(int pin)
	{
		return "I";
	}
	
	@Override
	public String getOutputPinLabel(int pin)
	{
		return "O";
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		_lastState = tag.getBoolean("lastState");
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag)
	{
		tag.setBoolean("lastState", _lastState);
	}
}
