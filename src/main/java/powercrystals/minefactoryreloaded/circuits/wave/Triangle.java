package powercrystals.minefactoryreloaded.circuits.wave;

import net.minecraft.nbt.NBTTagCompound;
import powercrystals.minefactoryreloaded.api.rednet.IRedNetLogicCircuit;

public class Triangle implements IRedNetLogicCircuit
{
	private boolean _invert = false;
	
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
		int value = (int)(worldTime % 16);
		
		if(value == 0)
		{
			_invert = !_invert;
		}
		return new int[] { _invert ? 15 - value : value };
	}
	
	@Override
	public String getUnlocalizedName()
	{
		return "circuit.mfr.wavegenerator.triangle";
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
	
	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		_invert = tag.getBoolean("inverted");
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag)
	{
		tag.setBoolean("inverted", _invert);
	}
}
