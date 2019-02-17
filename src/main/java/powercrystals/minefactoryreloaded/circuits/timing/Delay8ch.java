package powercrystals.minefactoryreloaded.circuits.timing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.nbt.NBTTagCompound;
import powercrystals.minefactoryreloaded.circuits.base.StatelessCircuit;

public class Delay8ch extends StatelessCircuit
{
	private int delayTime;
	private Map<Integer, List<Integer>> history = new HashMap<>();
	
	public Delay8ch()
	{
		for(int i = 0; i < 8; i++)
		{
			history.put(i, new ArrayList<>());
		}
	}
	
	@Override
	public byte getInputCount()
	{
		return 9;
	}
	
	@Override
	public byte getOutputCount()
	{
		return 8;
	}
	
	@Override
	public int[] recalculateOutputValues(long worldTime, int[] inputValues)
	{
		
		int[] output = new int[8];
		
		this.delayTime = inputValues[8];
		
		if(delayTime < 0)
		{
			delayTime = 0;
		}
		

		for(int p = 0; p < 8; p++)
		{
			this.history.get(p).add(0, inputValues[p]);
			
			if(this.history.get(p).size() > delayTime)
			{
				output[p] = this.history.get(p).get(this.delayTime);
				
				for(int i = this.delayTime; i < this.history.size(); i++)
				{
					this.history.get(p).remove(i);
				}
			}
		}
		
		return output;
	}
	
	@Override
	public String getUnlocalizedName()
	{
		return "circuit.mfr.delay8ch";
	}
	
	@Override
	public String getInputPinLabel(int pin)
	{
		return pin == 8 ? "D" : "I" + pin;
	}
	
	@Override
	public String getOutputPinLabel(int pin)
	{
		return "O" + pin;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		delayTime = tag.getInteger("delayTime");
		
		for(int p = 0; p < 8; p++)
		{
			for(int i = 0; i < delayTime; i++)
			{
				history.get(p).add(tag.getInteger("input" + p + "H" + i));
			}
		}
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag)
	{
		tag.setInteger("delayTime", delayTime);

		for(int p = 0; p < 8; p++)
		{
			for(Integer inputH : history.get(p))
			{
				int i = 0;
				
				tag.setInteger("input" + p + "H" + i, inputH);
				
				i++;
			}
		}
	}
}
