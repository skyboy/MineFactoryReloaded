package powercrystals.minefactoryreloaded.circuits.timing;

import java.util.ArrayList;

import net.minecraft.nbt.NBTTagCompound;
import powercrystals.minefactoryreloaded.circuits.base.StatelessCircuit;

public class Delay extends StatelessCircuit
{
	private int delayTime;
	private ArrayList<Integer> history = new ArrayList<>();
	
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
		
		int output = 0;
		
		this.delayTime = inputValues[1];
		
		if(delayTime < 0)
		{
			delayTime = 0;
		}
		
		this.history.add(0, inputValues[0]);
		
		if(this.history.size() > delayTime)
		{
			output = this.history.get(this.delayTime);
			
			for(int i = this.delayTime; i < this.history.size(); i++)
			{
				this.history.remove(i);
			}
		}
		
		return new int[] { output };
	}
	
	@Override
	public String getUnlocalizedName()
	{
		return "circuit.mfr.delay";
	}
	
	@Override
	public String getInputPinLabel(int pin)
	{
		return pin == 0 ? "I" : "D";
	}
	
	@Override
	public String getOutputPinLabel(int pin)
	{
		return "O";
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		delayTime = tag.getInteger("delayTime");
		
		for(int i = 0; i < delayTime; i++)
		{
			history.add(tag.getInteger("inputH" + i));
		}
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag)
	{
		tag.setInteger("delayTime", delayTime);
		
		for(Integer inputH : history)
		{
			int i = 0;
			
			tag.setInteger("inputH" + i, inputH);
			
			i++;
		}
		
	}
}
