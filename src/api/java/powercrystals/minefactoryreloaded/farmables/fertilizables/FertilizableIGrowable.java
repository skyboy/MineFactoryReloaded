package powercrystals.minefactoryreloaded.farmables.fertilizables;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.api.plant.FertilizerType;
import powercrystals.minefactoryreloaded.api.plant.IFactoryFertilizable;

public class FertilizableIGrowable implements IFactoryFertilizable
{
	protected final Block block;
	protected final IGrowable fertilizable;
	protected final FertilizerType validFertilizer;
	
	public FertilizableIGrowable(Block block, FertilizerType type)
	{
		this.block = block;
		fertilizable = (IGrowable)block;
		validFertilizer = type;
	}
	
	public FertilizableIGrowable(Block block)
	{
		this(block, FertilizerType.GrowPlant);
	}
	
	@Override
	public Block getPlant()
	{
		return block;
	}
	
	@Override
	public boolean canFertilize(World world, BlockPos pos, FertilizerType fertilizerType)
	{
		return fertilizerType == validFertilizer && fertilizable.canGrow(world, pos, world.getBlockState(pos), world.isRemote);
	}

	@Override
	public boolean fertilize(World world, Random rand, BlockPos pos, FertilizerType fertilizerType)
	{
		IBlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		int meta = block.getMetaFromState(state);
		fertilizable.grow(world, rand, pos, state);
		state = world.getBlockState(pos);
		return block != state.getBlock() || meta != state.getBlock().getMetaFromState(state);
	}
}
