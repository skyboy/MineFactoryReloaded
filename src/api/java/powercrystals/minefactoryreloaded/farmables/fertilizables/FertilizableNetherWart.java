package powercrystals.minefactoryreloaded.farmables.fertilizables;

import java.util.Random;

import net.minecraft.block.BlockNetherWart;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.api.plant.FertilizerType;

public class FertilizableNetherWart extends FertilizableBase
{
	public FertilizableNetherWart()
	{
		super(Blocks.NETHER_WART);
	}
	
	@Override
	public boolean fertilize(World world, Random rand, BlockPos pos, FertilizerType fertilizerType)
	{
		world.setBlockState(pos, world.getBlockState(pos).withProperty(BlockNetherWart.AGE, 3), 2);
		return true;
	}

	@Override
	protected boolean canFertilize(IBlockState state)
	{
		return state.getValue(BlockNetherWart.AGE) < 3;
	}
}
