package powercrystals.minefactoryreloaded.modcompat.forestry;

import forestry.api.arboriculture.ITreeRoot;
import forestry.api.genetics.AlleleManager;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import powercrystals.minefactoryreloaded.api.plant.FertilizerType;
import powercrystals.minefactoryreloaded.api.plant.IFactoryFertilizable;
import powercrystals.minefactoryreloaded.farmables.plantables.PlantableStandard;

import javax.annotation.Nonnull;
import java.util.Random;

public class ForestrySapling extends PlantableStandard implements IFactoryFertilizable {

	private ITreeRoot root;

	ForestrySapling(Item item, Block block) {

		super(item, block, WILDCARD, null);
		root = (ITreeRoot) AlleleManager.alleleRegistry.getSpeciesRoot("rootTrees");
		_plantedBlock = (world, pos, stack) -> root.plantSapling(world, root.getMember(stack), null, pos);
	}

	@Override
	public Block getPlant() {

		return _block;
	}

	@Override
	public boolean canBePlantedHere(World world, BlockPos pos, @Nonnull ItemStack stack) {

		if (!world.isAirBlock(pos))
			return false;

		return root.getMember(stack).canStay(world, pos);
	}

	@Override
	public boolean canFertilize(World world, BlockPos pos, FertilizerType fertilizerType) {

		return true;
	}

	@Override
	public boolean fertilize(World world, Random rand, BlockPos pos, FertilizerType fertilizerType) {

		Block block = world.getBlockState(pos).getBlock();
		root.getTree(world, pos).getTreeGenerator(world, pos, true).generate(world, rand, pos);
		return world.getBlockState(pos).getBlock() != block;
	}

}
