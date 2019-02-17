package powercrystals.minefactoryreloaded.modhelpers.vanilla.handlers;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import powercrystals.minefactoryreloaded.farmables.plantables.PlantableStandard;

import javax.annotation.Nonnull;

public class PlantableChorus extends PlantableStandard {

	public PlantableChorus() {

		super(Blocks.CHORUS_FLOWER);
	}

	@Override
	public boolean canBePlantedHere(World world, BlockPos pos, @Nonnull ItemStack stack) {

		if (!world.isAirBlock(pos))
			return false;

		if (world.getBlockState(pos.down()).getBlock() != Blocks.END_STONE) {
			return false;
		}

		for (EnumFacing side : EnumFacing.HORIZONTALS) {
			BlockPos offsetPos = pos.offset(side);
			if (!world.isBlockLoaded(offsetPos))
				return false;

			Block block = world.getBlockState(offsetPos).getBlock();
			if (block == Blocks.CHORUS_FLOWER || block == Blocks.CHORUS_PLANT) {
				return false;
			}
		}

		return true;
	}
}
