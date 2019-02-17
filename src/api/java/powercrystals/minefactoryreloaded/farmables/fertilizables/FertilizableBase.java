package powercrystals.minefactoryreloaded.farmables.fertilizables;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.api.plant.FertilizerType;
import powercrystals.minefactoryreloaded.api.plant.IFactoryFertilizable;

public abstract class FertilizableBase implements IFactoryFertilizable
{
	protected final Block fertilizable;
	protected final FertilizerType validFertilizer;
	
	public FertilizableBase(Block block, FertilizerType type)
	{
		fertilizable = block;
		validFertilizer = type;
	}
	
	public FertilizableBase(Block block)
	{
		this(block, FertilizerType.GrowPlant);
	}
	
	@Override
	public Block getPlant()
	{
		return fertilizable;
	}
	
	@Override
	public boolean canFertilize(World world, BlockPos pos, FertilizerType fertilizerType)
	{
		return fertilizerType == validFertilizer && canFertilize(world.getBlockState(pos));
	}
	
	protected abstract boolean canFertilize(IBlockState state);

	@Override
	public abstract boolean fertilize(World world, Random rand, BlockPos pos, FertilizerType fertilizerType);
}
