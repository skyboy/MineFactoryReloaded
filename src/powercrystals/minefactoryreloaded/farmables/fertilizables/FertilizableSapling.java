package powercrystals.minefactoryreloaded.farmables.fertilizables;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSapling;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.api.FertilizerType;

public class FertilizableSapling extends FertilizableBase
{
	public FertilizableSapling(int blockId)
	{
		this(blockId, FertilizerType.GrowPlant);
	}
	
	public FertilizableSapling(int blockId, FertilizerType type)
	{
		super(blockId, type);
	}
	
	@Override
	public boolean fertilize(World world, Random rand, int x, int y, int z, FertilizerType fertilizerType)
	{
		((BlockSapling)Block.blocksList[world.getBlockId(x, y, z)]).growTree(world, x, y, z, world.rand);
		return world.getBlockId(x, y, z) != fertilizableId;
	}
}
