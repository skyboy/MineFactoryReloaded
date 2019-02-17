package powercrystals.minefactoryreloaded.circuits.base;

import net.minecraft.nbt.NBTTagCompound;
import powercrystals.minefactoryreloaded.api.rednet.IRedNetLogicCircuit;

public abstract class StatelessCircuit implements IRedNetLogicCircuit
{
	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag)
	{
	}
}
