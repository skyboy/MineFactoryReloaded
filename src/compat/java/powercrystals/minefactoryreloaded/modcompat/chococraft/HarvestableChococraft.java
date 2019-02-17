
package powercrystals.minefactoryreloaded.modcompat.chococraft;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import powercrystals.minefactoryreloaded.api.plant.HarvestType;
import powercrystals.minefactoryreloaded.api.plant.IFactoryHarvestable;
import powercrystals.minefactoryreloaded.api.util.IFactorySettings;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Random;

public class HarvestableChococraft implements IFactoryHarvestable {

	private Block _block;

	HarvestableChococraft(Block block) {

		_block = block;
	}

	@Nonnull
	@Override
	public Block getPlant() {

		return _block;
	}

	@Nonnull
	@Override
	public HarvestType getHarvestType() {

		return HarvestType.Normal;
	}

	@Override
	public boolean canBeHarvested(World world, BlockPos pos, IBlockState harvestState, IFactorySettings harvesterSettings) {

		IBlockState state = world.getBlockState(pos);
		return state.getBlock().getMetaFromState(state) >= 4;
	}

	@Override
	public List<ItemStack> getDrops(World world, BlockPos pos, IBlockState harvestState, Random rand, IFactorySettings harvesterSettings) {

		return _block.getDrops(world, pos, world.getBlockState(pos), 0);
	}

	@Override
	public void preHarvest(World world, BlockPos pos, IBlockState harvestState) {

		IBlockState state = world.getBlockState(pos);
		if (state.getBlock().getMetaFromState(state) > 4) {
			world.setBlockState(pos, state.getBlock().getStateFromMeta(4), 2);
		}
	}

}
