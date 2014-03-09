package powercrystals.minefactoryreloaded.core;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import powercrystals.core.position.Area;
import powercrystals.core.position.BlockPosition;

public interface IHarvestManager
{
	public void moveNext();
	public BlockPosition getNextBlock();
	public BlockPosition getOrigin();
	public void reset(World world, Area area, HarvestMode harvestMode);
	public void setWorld(World world);
	public boolean getIsDone();
	public void writeToNBT(NBTTagCompound tag);
	public void readFromNBT(NBTTagCompound tag);
	public void free();
}
