package powercrystals.minefactoryreloaded.farmables.plantables;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import powercrystals.minefactoryreloaded.api.plant.IReplacementBlock;

import javax.annotation.Nonnull;

public class PlantableCropPlant extends PlantableStandard {

	public static final IReplacementBlock TILL_SOIL = (world, pos, stack) -> {

		Block ground = world.getBlockState(pos).getBlock();
		if (ground.equals(Blocks.GRASS) || ground.equals(Blocks.DIRT)) {
			return world.setBlockState(pos, Blocks.FARMLAND.getDefaultState());
		}
		return ground.equals(Blocks.FARMLAND);
	};

	static IReplacementBlock getReplacementBlock(final Item seed, final Block plant, int meta) {

		if (meta == WILDCARD) {
			return (world, pos, stack) -> world.setBlockState(pos, plant.getStateFromMeta(seed.getMetadata(stack.getMetadata())));
		} else {
			return IReplacementBlock.of(plant.getStateFromMeta(meta));
		}
	}

	public PlantableCropPlant(Item seed, Block plant) {

		this(seed, plant, WILDCARD);
	}

	public PlantableCropPlant(Item seed, Block plant, int meta) {

		super(seed, plant, meta, getReplacementBlock(seed, plant, meta).above(TILL_SOIL));
	}

	@Override
	public boolean canBePlantedHere(World world, BlockPos pos, @Nonnull ItemStack stack) {

		if (!world.isAirBlock(pos))
			return false;

		if (!(world.getLight(pos) >= 8 || world.canSeeSky(pos)))
			return false;

		Block ground = world.getBlockState(pos.down()).getBlock();
		return ground.equals(Blocks.FARMLAND) ||
				ground.equals(Blocks.GRASS) ||
				ground.equals(Blocks.DIRT) ||
				super.canBePlantedHere(world, pos, stack);
	}

}
