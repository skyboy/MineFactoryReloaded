package powercrystals.minefactoryreloaded.circuits.digital;

import net.minecraft.nbt.NBTTagCompound;
import powercrystals.minefactoryreloaded.api.rednet.IRedNetLogicCircuit;

public class Counter implements IRedNetLogicCircuit
{
	private int _count;
	
	private boolean _lastIncrementState;
	private boolean _lastDecrementState;
	
	@Override
	public byte getInputCount()
	{
		return 4;
	}
	
	@Override
	public byte getOutputCount()
	{
		return 2;
	}
	
	@Override
	public int[] recalculateOutputValues(long worldTime, int[] inputValues)
	{
		int _preset = inputValues[2];
		if (_preset <= 0)
		{
			return new int[] {0, _count};
		}
		
		_count = inputValues[3] > 0 ? 0 : inputValues[3] < 0 ? _preset - 1 : _count;
		
		if(inputValues[0] > 0 && !_lastIncrementState)
		{
			_count++;
		}
		else if(inputValues[1] > 0 && !_lastDecrementState)
		{
			_count--;
		}
		
		_lastIncrementState = inputValues[0] > 0;
		_lastDecrementState = inputValues[1] > 0;
		
		
		if(_count >= _preset)
		{
			_count = 0;
			return new int[] { 15, _count };
		}
		else if(_count < 0)
		{
			_count = _preset - 1;
			return new int[] { 15, _count };
		}
		return new int[] { 0, _count };
	}
	
	@Override
	public String getUnlocalizedName()
	{
		return "circuit.mfr.counter";
	}
	
	@Override
	public String getInputPinLabel(int pin)
	{
		switch (pin) {
		case 0: return "INC";
		case 1: return "DEC";
		case 2: return "PRE";
		case 3: return "R";
		}
		return "";
	}
	
	@Override
	public String getOutputPinLabel(int pin)
	{
		return pin == 0 ? "Q" : "V";
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		_count = tag.getInteger("count");
		_lastIncrementState = tag.getBoolean("lastIncrementState");
		_lastDecrementState = tag.getBoolean("lastDecrementState");
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag)
	{
		tag.setInteger("count", _count);
		tag.setBoolean("lastIncrementState", _lastIncrementState);
		tag.setBoolean("lastDecrementState", _lastDecrementState);
	}
}
