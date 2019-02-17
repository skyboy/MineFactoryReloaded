package powercrystals.minefactoryreloaded.circuits.analog;

import net.minecraft.nbt.NBTTagCompound;
import powercrystals.minefactoryreloaded.api.rednet.IRedNetLogicCircuit;

public class SchmittTrigger implements IRedNetLogicCircuit
{
	private int _activeTicks = 0;
	
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
		if(inputValues[0] != 0)
		{
			_activeTicks ++;
		}
		else
		{
			_activeTicks = 0;
		}
		
		if(_activeTicks > inputValues[1])
		{
			return new int[] { inputValues[0] };
		}
		else
		{
			return new int[] { 0 };
		}
	}

	@Override
	public String getUnlocalizedName()
	{
		return "circuit.mfr.schmitttrigger";
	}

	@Override
	public String getInputPinLabel(int pin)
	{
		return pin == 0 ? "I" : "Pd";
	}

	@Override
	public String getOutputPinLabel(int pin)
	{
		return "O";
	}

	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		_activeTicks = tag.getInteger("activeTicks");
	}

	@Override
	public void writeToNBT(NBTTagCompound tag)
	{
		tag.setInteger("activeTicks", _activeTicks);
	}
}
