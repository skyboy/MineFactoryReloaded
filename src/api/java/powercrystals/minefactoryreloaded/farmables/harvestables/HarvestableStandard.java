package powercrystals.minefactoryreloaded.farmables.harvestables;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import powercrystals.minefactoryreloaded.api.plant.HarvestType;
import powercrystals.minefactoryreloaded.api.plant.IFactoryHarvestable;
import powercrystals.minefactoryreloaded.api.util.IFactorySettings;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Random;

public class HarvestableStandard implements IFactoryHarvestable {

	private Block _block;
	private HarvestType _harvestType;

	public HarvestableStandard(Block block, HarvestType harvestType) {

		if (block == Blocks.AIR)
			throw new IllegalArgumentException("Passed air FactoryHarvestableStandard");

		_block = block;
		_harvestType = harvestType;
	}

	public HarvestableStandard(Block block) {

		this(block, HarvestType.Normal);
	}

	@Nonnull
	@Override
	public Block getPlant() {

		return _block;
	}

	@Nonnull
	@Override
	public HarvestType getHarvestType() {

		return _harvestType;
	}

	@Override
	public boolean canBeHarvested(World world, BlockPos pos, IBlockState harvestState, IFactorySettings harvesterSettings) {

		return true;
	}

	@Override
	public List<ItemStack> getDrops(World world, BlockPos pos, IBlockState harvestState, Random rand, IFactorySettings harvesterSettings) {

		return harvestState.getBlock().getDrops(world, pos, harvestState, 0);
	}

}
