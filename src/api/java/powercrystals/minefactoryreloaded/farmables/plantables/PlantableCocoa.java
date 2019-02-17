package powercrystals.minefactoryreloaded.farmables.plantables;

import net.minecraft.block.Block;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import powercrystals.minefactoryreloaded.api.plant.ReplacementBlock;

import javax.annotation.Nonnull;

public class PlantableCocoa extends PlantableStandard {

	public PlantableCocoa(Item source, Block plantedBlock) {

		this(source, plantedBlock, WILDCARD);
	}

	public PlantableCocoa(Item source, Block plantedBlock, int validMeta) {

		super(source, plantedBlock, validMeta, null);
		_plantedBlock = new ReplacementBlock(_block) {

			@Override
			public int getMeta(World world, BlockPos pos, @Nonnull ItemStack stack) {

				int meta = EnumFacing.EAST.getHorizontalIndex();
				if (isGoodLog(world, pos.west()))
					meta = EnumFacing.WEST.getHorizontalIndex();
				else if (isGoodLog(world, pos.south()))
					meta = EnumFacing.SOUTH.getHorizontalIndex();
				else if (isGoodLog(world, pos.north()))
					meta = EnumFacing.NORTH.getHorizontalIndex();

				return meta;
			}
		};
	}

	@Override
	public boolean canBePlantedHere(World world, BlockPos pos, @Nonnull ItemStack stack) {

		if (!world.isAirBlock(pos))
			return false;

		return isNextToAcceptableLog(world, pos);
	}

	protected boolean isNextToAcceptableLog(World world, BlockPos pos) {

		for (EnumFacing facing : EnumFacing.HORIZONTALS) {
			if (isGoodLog(world, pos.offset(facing)))
				return true;
		}

		return false;
	}

	protected boolean isGoodLog(World world, BlockPos pos) {

		IBlockState state = world.getBlockState(pos);
		return state.getBlock().equals(Blocks.LOG) &&
				state.getValue(BlockOldLog.VARIANT) == BlockPlanks.EnumType.JUNGLE;
	}

}
