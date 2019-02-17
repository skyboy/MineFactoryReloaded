package powercrystals.minefactoryreloaded.modcompat.forestry;

import forestry.core.blocks.BlockBogEarth;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import powercrystals.minefactoryreloaded.api.plant.*;
import powercrystals.minefactoryreloaded.api.util.IFactorySettings;
import powercrystals.minefactoryreloaded.farmables.plantables.PlantableSoil;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Random;

public class ForestryBogEarth extends PlantableSoil implements IFactoryFertilizable, IFactoryHarvestable, IFactoryFruit {

	private IReplacementBlock repl;
	private Item dirt;

	ForestryBogEarth(Block block) {

		super(block, true);
		repl = new ReplacementBlock(Blocks.DIRT);
		dirt = Item.getItemFromBlock(Blocks.DIRT);
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
	public boolean canFertilize(World world, BlockPos pos, FertilizerType fertilizerType) {

		return fertilizerType == FertilizerType.GrowPlant &&
				BlockBogEarth.SoilType.fromMaturity(world.getBlockState(pos).getValue(BlockBogEarth.MATURITY)) != BlockBogEarth.SoilType.PEAT;
	}

	@Override
	public boolean canBePicked(World world, BlockPos pos) {

		return BlockBogEarth.SoilType.fromMaturity(world.getBlockState(pos).getValue(BlockBogEarth.MATURITY)) == BlockBogEarth.SoilType.PEAT;
	}

	@Override
	public boolean canBeHarvested(World world, BlockPos pos, IBlockState harvestState, IFactorySettings settings) {

		return BlockBogEarth.SoilType.fromMaturity(world.getBlockState(pos).getValue(BlockBogEarth.MATURITY)) == BlockBogEarth.SoilType.PEAT;
	}

	@Override
	public boolean fertilize(World world, Random rand, BlockPos pos, FertilizerType fertilizerType) {

		return world.setBlockState(pos, world.getBlockState(pos).withProperty(BlockBogEarth.MATURITY, 3), 3);
	}

	@Override
	public List<ItemStack> getDrops(World world, BlockPos pos, IBlockState harvestState, Random rand, IFactorySettings settings) {

		IBlockState state = world.getBlockState(pos);
		return state.getBlock().getDrops(world, pos, state, 0);
	}

	@Override
	public IReplacementBlock getReplacementBlock(World world, BlockPos pos) {

		return repl;
	}

	@Override
	public List<ItemStack> getDrops(World world, Random rand, BlockPos pos) {

		IBlockState state = world.getBlockState(pos);
		List<ItemStack> list = state.getBlock().getDrops(world, pos, state, 0);
		for (ItemStack a : list)
			if (a.getItem() == dirt) {
				list.remove(a);
				break;
			}
		return list;
	}

}
