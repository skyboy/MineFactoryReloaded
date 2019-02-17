package powercrystals.minefactoryreloaded.circuits.wave;

import net.minecraft.nbt.NBTTagCompound;
import powercrystals.minefactoryreloaded.api.rednet.IRedNetLogicCircuit;

public class Square implements IRedNetLogicCircuit
{
	private boolean _value;
	private int _period;
	
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
		_period = inputValues[0];
		if(_period == 0)
		{
			_period = 1;
		}
		
		if(worldTime % _period == 0)
		{
			_value = !_value;
		}
		return new int[] { _value ? 15 : 0 };
	}
	
	@Override
	public String getUnlocalizedName()
	{
		return "circuit.mfr.wavegenerator.square";
	}
	
	@Override
	public String getInputPinLabel(int pin)
	{
		return "Pd";
	}
	
	@Override
	public String getOutputPinLabel(int pin)
	{
		return "Q";
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		_value = tag.getBoolean("state");
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag)
	{
		tag.setBoolean("state", _value);
	}
}
