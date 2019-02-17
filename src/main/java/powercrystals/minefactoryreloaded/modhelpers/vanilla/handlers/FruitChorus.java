package powercrystals.minefactoryreloaded.modhelpers.vanilla.handlers;

import net.minecraft.block.Block;
import net.minecraft.block.BlockChorusFlower;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import powercrystals.minefactoryreloaded.farmables.fruits.FactoryFruitStandard;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class FruitChorus extends FactoryFruitStandard {

	Block block;

	public FruitChorus(Block block) {

		super(block);
		this.block = block;
	}

	@Override
	public List<ItemStack> getDrops(World world, Random rand, BlockPos pos) {

		if (block == Blocks.CHORUS_FLOWER) {
			return Collections.singletonList(new ItemStack(Item.getItemFromBlock(Blocks.CHORUS_FLOWER)));
		}

		return super.getDrops(world, rand, pos);
	}

	@Override
	public boolean canBePicked(World world, BlockPos pos) {

		IBlockState state = world.getBlockState(pos);
		if (state.getBlock() == Blocks.CHORUS_FLOWER) {
			return state.getValue(BlockChorusFlower.AGE) == 5;
		}

		int numberOfConnections = 0;
		for(EnumFacing side : EnumFacing.VALUES) {
			Block block = world.getBlockState(pos.offset(side)).getBlock();

			if (block == Blocks.CHORUS_PLANT || block == Blocks.CHORUS_FLOWER || block == Blocks.END_STONE) {
				numberOfConnections++;
			}

			if (numberOfConnections > 1) {
				return false;
			}
		}

		return true;
	}
}
