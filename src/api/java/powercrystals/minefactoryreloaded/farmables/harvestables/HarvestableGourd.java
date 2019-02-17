package powercrystals.minefactoryreloaded.farmables.harvestables;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import powercrystals.minefactoryreloaded.api.plant.HarvestType;

public class HarvestableGourd extends HarvestableStandard {

	public HarvestableGourd(Block block, HarvestType harvestType) {

		super(block, harvestType);
	}

	public HarvestableGourd(Block block) {

		super(block);
	}

	@Override
	public boolean postHarvest(World world, BlockPos pos, IBlockState harvestState) {

		Block ground = world.getBlockState(pos.down()).getBlock();
		if (ground.equals(Blocks.DIRT) || ground.equals(Blocks.GRASS)) {
			world.setBlockState(pos.down(), Blocks.FARMLAND.getDefaultState());
		}
		return super.postHarvest(world, pos, harvestState);
	}

}
