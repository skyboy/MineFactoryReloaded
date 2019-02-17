package powercrystals.minefactoryreloaded.circuits.latch;

import net.minecraft.nbt.NBTTagCompound;
import powercrystals.minefactoryreloaded.api.rednet.IRedNetLogicCircuit;

public class LatchDClocked implements IRedNetLogicCircuit
{
	private boolean _value;
	private boolean _lastClockState;
	
	@Override
	public byte getInputCount()
	{
		return 2;
	}
	
	@Override
	public byte getOutputCount()
	{
		return 2;
	}
	
	@Override
	public int[] recalculateOutputValues(long worldTime, int[] inputValues)
	{
		if(inputValues[1] != 0 && !_lastClockState)
		{
			_value = (inputValues[0] != 0);
		}

		_lastClockState = (inputValues[1] != 0);
		
		if(_value)
		{
			return new int[] { 15, 0 };
		}
		else
		{
			return new int[] { 0, 15 };
		}
	}
	
	@Override
	public String getUnlocalizedName()
	{
		return "circuit.mfr.latch.d.clocked";
	}
	
	@Override
	public String getInputPinLabel(int pin)
	{
		return pin == 0 ? "D" : "CLK";
	}
	
	@Override
	public String getOutputPinLabel(int pin)
	{
		return pin == 0 ? "Q" : "Q#";
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		_value = tag.getBoolean("state");
		_lastClockState = tag.getBoolean("lastClockState");
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag)
	{
		tag.setBoolean("state", _value);
		tag.setBoolean("lastClockState", _lastClockState);
	}
}
