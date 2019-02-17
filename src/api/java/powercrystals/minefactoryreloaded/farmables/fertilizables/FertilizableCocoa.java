package powercrystals.minefactoryreloaded.farmables.fertilizables;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCocoa;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.api.plant.FertilizerType;

public class FertilizableCocoa extends FertilizableCropPlant
{
	public FertilizableCocoa(Block block)
	{
		super(block, FertilizerType.GrowPlant, 8);
	}
	
	public FertilizableCocoa(Block block, FertilizerType type)
	{
		super(block, type, 8);
	}
	
	@Override
	public boolean fertilize(World world, Random rand, BlockPos pos, FertilizerType fertilizerType)
	{
		world.setBlockState(pos, world.getBlockState(pos).withProperty(BlockCocoa.AGE, 2));
		return true;
	}
}
