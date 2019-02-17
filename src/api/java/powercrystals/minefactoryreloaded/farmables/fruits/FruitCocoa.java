package powercrystals.minefactoryreloaded.farmables.fruits;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCocoa;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FruitCocoa extends FactoryFruitStandard
{
	public FruitCocoa(Block block) {
		super(block);
	}

	@Override
	public boolean canBePicked(World world, BlockPos pos)
	{
		return world.getBlockState(pos).getValue(BlockCocoa.AGE) >= 2;
	}
}
