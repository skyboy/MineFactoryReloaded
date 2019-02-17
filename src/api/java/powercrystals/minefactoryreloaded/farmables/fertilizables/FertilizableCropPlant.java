package powercrystals.minefactoryreloaded.farmables.fertilizables;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.api.plant.FertilizerType;

public class FertilizableCropPlant extends FertilizableStandard
{
	protected final int targetMeta;
	
	public FertilizableCropPlant(Block block, FertilizerType type, int targetMeta)
	{
		super(block, type);
		this.targetMeta = targetMeta;
	}
	
	public FertilizableCropPlant(IGrowable block, FertilizerType type, int targetMeta)
	{
		super(block, type);
		this.targetMeta = targetMeta;
	}
	
	public FertilizableCropPlant(IGrowable block, int targetMeta)
	{
		this(block, FertilizerType.GrowPlant, targetMeta);
	}

	@Override
	protected boolean canFertilize(IBlockState state)
	{
		//TODO look into using properties rather than meta (maybe not doable because of IMC)
		return state.getBlock().getMetaFromState(state) < targetMeta;
	}
	
	@Override
	public boolean fertilize(World world, Random rand, BlockPos pos, FertilizerType fertilizerType)
	{
		IBlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		int meta = block.getMetaFromState(state);
		((IGrowable)block).grow(world, rand, pos, state);
		return block.getMetaFromState(world.getBlockState(pos)) != meta;
	}
}
