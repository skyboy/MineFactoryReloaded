package powercrystals.minefactoryreloaded.core;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import powercrystals.minefactoryreloaded.api.util.IFactorySettings;

public interface IHarvestManager
{
	void moveNext();
	BlockPos getNextBlock();
	BlockPos getOrigin();
	void reset(World world, Area area, HarvestMode harvestMode, IFactorySettings settings);
	void setWorld(World world);
	boolean getIsDone();
	void writeToNBT(NBTTagCompound tag);
	void readFromNBT(NBTTagCompound tag);
	void free();
}
