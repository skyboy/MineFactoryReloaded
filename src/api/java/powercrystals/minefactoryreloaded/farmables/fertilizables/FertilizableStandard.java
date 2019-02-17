package powercrystals.minefactoryreloaded.farmables.fertilizables;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.api.plant.FertilizerType;

public class FertilizableStandard extends FertilizableBase
{
	public FertilizableStandard(Block block, FertilizerType type)
	{
		super(block, type);
	}

	public FertilizableStandard(IGrowable block, FertilizerType type)
	{
		super((Block)block, type);
	}
	
	public FertilizableStandard(IGrowable block)
	{
		this(block, FertilizerType.GrowPlant);
	}
	
	@Override
	public boolean canFertilize(World world, BlockPos pos, FertilizerType fertilizerType)
	{
		return fertilizerType == validFertilizer;
	}
	
	@Override
	protected boolean canFertilize(IBlockState state)
	{
		return true;
	}
	
	@Override
	public boolean fertilize(World world, Random rand, BlockPos pos, FertilizerType fertilizerType)
	{
		IBlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		((IGrowable)block).grow(world, rand, pos, state);
		return world.getBlockState(pos).getBlock() != block;
	}
}
