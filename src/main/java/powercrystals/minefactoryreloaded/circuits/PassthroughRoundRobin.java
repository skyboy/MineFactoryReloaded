package powercrystals.minefactoryreloaded.circuits;

import net.minecraft.nbt.NBTTagCompound;
import powercrystals.minefactoryreloaded.api.rednet.IRedNetLogicCircuit;

public class PassthroughRoundRobin implements IRedNetLogicCircuit
{
	private int _currentOutput;
	private boolean _lastClockState;
	
	@Override
	public byte getInputCount()
	{
		return 3;
	}
	
	@Override
	public byte getOutputCount()
	{
		return 16;
	}
	
	@Override
	public int[] recalculateOutputValues(long worldTime, int[] inputValues)
	{
		if(inputValues[2] > 0 && !_lastClockState)
		{
			_currentOutput++;
			if(_currentOutput >= Math.min(inputValues[1], 16))
			{
				_currentOutput = 0;
			}
		}
		
		_lastClockState = inputValues[2] > 0;
		
		int[] outputs = new int[16];
		outputs[_currentOutput] = inputValues[0];
		return outputs;
	}
	
	@Override
	public String getUnlocalizedName()
	{
		return "circuit.mfr.passthrough.roundrobin";
	}
	
	@Override
	public String getInputPinLabel(int pin)
	{
		return pin == 0 ? "I" : pin == 1 ? "CNT" : "CLK";
	}
	
	@Override
	public String getOutputPinLabel(int pin)
	{
		return "O" + pin;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		_lastClockState = tag.getBoolean("lastClockState");
		_currentOutput = tag.getInteger("currentOutput");
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag)
	{
		tag.setBoolean("lastClockState", _lastClockState);
		tag.setInteger("currentOutput", _currentOutput);
	}
}