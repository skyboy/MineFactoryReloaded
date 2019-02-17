package powercrystals.minefactoryreloaded.circuits.timing;

import powercrystals.minefactoryreloaded.api.rednet.IRedNetLogicCircuit;
import net.minecraft.nbt.NBTTagCompound;

public class Multipulse implements IRedNetLogicCircuit
{
	private static final String[] _inputPinNames = new String[] { "I", "CLK", "CNT", "THi", "TLo" };
	
	private int _pulsesCompleted;
	private int _ticksCompleted;
	private boolean _isActive;
	private boolean _lastClockState;
	
	@Override
	public byte getInputCount()
	{
		return 5;
	}
	
	@Override
	public byte getOutputCount()
	{
		return 2;
	}
	
	@Override
	public int[] recalculateOutputValues(long worldTime, int[] inputValues)
	{
		boolean isDone = false;
		int output = 0;
		
		if(!_lastClockState && inputValues[1] != 0)
		{
			_pulsesCompleted = 0;
			_ticksCompleted = 0;
			_isActive = true;
		}
		
		if(_pulsesCompleted < inputValues[2])
		{
			if(_isActive)
			{
				output = inputValues[0];
				if(_ticksCompleted >= inputValues[3])
				{
					_isActive = false;
					_ticksCompleted = 0;
					_pulsesCompleted++;
				}
				else
				{
					_ticksCompleted++;
				}
			}
			else
			{
				if(_ticksCompleted >= inputValues[4])
				{
					_isActive = true;
					_ticksCompleted = 0;
				}
				else
				{
					_ticksCompleted++;
				}
			}
		}
		else
		{
			isDone = true;
		}
		
		_lastClockState = (inputValues[1] > 0);
		return new int[] { output, isDone ? 15 : 0 };
	}
	
	@Override
	public String getUnlocalizedName()
	{
		return "circuit.mfr.multipulse";
	}
	
	@Override
	public String getInputPinLabel(int pin)
	{
		return _inputPinNames[pin];
	}
	
	@Override
	public String getOutputPinLabel(int pin)
	{
		return pin == 0 ? "Q" : "DN";
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		_pulsesCompleted = tag.getInteger("pulsesCompleted");
		_ticksCompleted = tag.getInteger("ticksCompleted");
		_isActive = tag.getBoolean("isActive");
		_lastClockState = tag.getBoolean("lastClockState");
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag)
	{
		tag.setInteger("pulsesCompleted", _pulsesCompleted);
		tag.setInteger("ticksCompleted", _ticksCompleted);
		tag.setBoolean("isActive", _isActive);
		tag.setBoolean("lastClockState", _lastClockState);
	}
}
