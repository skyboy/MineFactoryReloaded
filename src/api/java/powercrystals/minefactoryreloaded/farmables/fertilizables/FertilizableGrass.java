package powercrystals.minefactoryreloaded.farmables.fertilizables;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.api.plant.FertilizerType;
import powercrystals.minefactoryreloaded.api.plant.IFactoryFertilizable;

public class FertilizableGrass implements IFactoryFertilizable
{
	protected Block grass;
	public FertilizableGrass() { this(Blocks.GRASS); }
	public FertilizableGrass(Block grass)
	{
		this.grass = grass;
	}
	
	@Override
	public boolean canFertilize(World world, BlockPos pos, FertilizerType fertilizerType)
	{
		return (fertilizerType == FertilizerType.GrowPlant ||
				fertilizerType == FertilizerType.Grass) &&
				world.isAirBlock(pos.up());
	}
	
	@Override
	public boolean fertilize(World world, Random rand, BlockPos pos, FertilizerType fertilizerType)
	{
		IBlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		((IGrowable)block).grow(world, rand, pos, state);
		return !world.isAirBlock(pos.up());
	}
	
	@Override
	public Block getPlant()
	{
		return grass;
	}
}
