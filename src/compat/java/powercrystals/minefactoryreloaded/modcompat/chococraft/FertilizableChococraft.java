
package powercrystals.minefactoryreloaded.modcompat.chococraft;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import powercrystals.minefactoryreloaded.api.plant.FertilizerType;
import powercrystals.minefactoryreloaded.api.plant.IFactoryFertilizable;

import java.util.Random;

public class FertilizableChococraft implements IFactoryFertilizable {

	private final Block _blockId;

	FertilizableChococraft(Block blockId) {

		this._blockId = blockId;
	}

	@Override
	public Block getPlant() {

		return this._blockId;
	}

	@Override
	public boolean canFertilize(World world, BlockPos pos, FertilizerType fertilizerType) {

		if (fertilizerType != FertilizerType.GrowPlant)
			return false;

		IBlockState state = world.getBlockState(pos);
		return state.getBlock().getMetaFromState(state) <= 4;
	}

	@Override
	public boolean fertilize(World world, Random rand, BlockPos pos, FertilizerType fertilizerType) {

		IBlockState state = world.getBlockState(pos);
		world.setBlockState(pos, state.getBlock().getStateFromMeta(4));
		return true;
	}

}
