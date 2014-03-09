package powercrystals.minefactoryreloaded.farmables.fertilizables;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockMushroom;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.api.FertilizerType;

public class FertilizableGiantMushroom extends FertilizableBase
{
	public FertilizableGiantMushroom(int id)
	{
		super(id);
	}
	
	@Override
	public boolean fertilize(World world, Random rand, int x, int y, int z, FertilizerType fertilizerType)
	{
		int blockId = world.getBlockId(x, y, z);
		return ((BlockMushroom)Block.blocksList[blockId]).fertilizeMushroom(world, x, y, z, world.rand);
	}
}
