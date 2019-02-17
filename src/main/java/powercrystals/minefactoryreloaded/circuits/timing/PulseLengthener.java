package powercrystals.minefactoryreloaded.circuits.timing;

import net.minecraft.nbt.NBTTagCompound;
import powercrystals.minefactoryreloaded.api.rednet.IRedNetLogicCircuit;

public class PulseLengthener implements IRedNetLogicCircuit
{
	private int _pulseLength;
	private int _pulseCountdown;
	
	@Override
	public byte getInputCount()
	{
		return 2;
	}
	
	@Override
	public byte getOutputCount()
	{
		return 1;
	}
	
	@Override
	public int[] recalculateOutputValues(long worldTime, int[] inputValues)
	{
		_pulseLength = inputValues[1];
		int output;
		
		if(_pulseCountdown <= 0 && inputValues[0] > 0)
		{
			_pulseCountdown = _pulseLength;
			output = 15;
		}
		else if(_pulseCountdown > 0)
		{
			_pulseCountdown--;
			output = 15;
		}
		else
		{
			output = 0;
		}
		return new int[] { output };
	}
	
	@Override
	public String getUnlocalizedName()
	{
		return "circuit.mfr.pulselengthener";
	}
	
	@Override
	public String getInputPinLabel(int pin)
	{
		return pin == 0 ? "I" : "L";
	}
	
	@Override
	public String getOutputPinLabel(int pin)
	{
		return "O";
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		_pulseLength = tag.getInteger("pulseLength");
		_pulseCountdown = tag.getInteger("pulseCountdown");
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag)
	{
		tag.setInteger("pulseLength", _pulseLength);
		tag.setInteger("pulseCountdown", _pulseCountdown);
	}
}
