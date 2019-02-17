package powercrystals.minefactoryreloaded.farmables.harvestables;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import powercrystals.minefactoryreloaded.api.plant.HarvestType;

public class HarvestableTreeLeaves extends HarvestableShearable {

	public HarvestableTreeLeaves(Block block) {

		super(block, HarvestType.TreeLeaf);
	}

	@Override
	public boolean postHarvest(World world, BlockPos pos, IBlockState harvestState) {

		Block id = getPlant();

		notifyBlock(world, pos.down(), id, pos);
		notifyBlock(world, pos.west(), id, pos);
		notifyBlock(world, pos.east(), id, pos);
		notifyBlock(world, pos.north(), id, pos);
		notifyBlock(world, pos.south(), id, pos);
		notifyBlock(world, pos.up(), id, pos);
		return true;
	}

	protected void notifyBlock(World world, BlockPos pos, Block id, BlockPos source) {

		IBlockState state = world.getBlockState(pos);
		if (!state.getBlock().isLeaves(state, world, pos)) {
			world.neighborChanged(pos, id, source);
			world.observedNeighborChanged(pos, id, source);
		}
	}

}
