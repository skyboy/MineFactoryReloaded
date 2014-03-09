package powercrystals.minefactoryreloaded.farmables.fertilizables;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.api.FertilizerType;

public class FertilizableCropPlant extends FertilizableStandard
{
	protected final int targetMeta;
	
	public FertilizableCropPlant(int blockID, FertilizerType type, int targetMeta)
	{
		super(blockID, type);
		this.targetMeta = targetMeta;
	}
	
	public FertilizableCropPlant(int blockID, int targetMeta)
	{
		this(blockID, FertilizerType.GrowPlant, targetMeta);
	}

	@Override
	protected boolean canFertilize(int metadata)
	{
		return metadata < targetMeta;
	}
	
	@Override
	public boolean fertilize(World world, Random rand, int x, int y, int z, FertilizerType fertilizerType)
	{
		((BlockCrops)Block.crops).fertilize(world, x, y, z);
		return true;
	}
}
