package powercrystals.minefactoryreloaded.farmables.harvestables;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import powercrystals.minefactoryreloaded.api.plant.HarvestType;
import powercrystals.minefactoryreloaded.api.util.IFactorySettings;

import java.util.List;
import java.util.Random;

public class HarvestableVine extends HarvestableStandard {

	public HarvestableVine(net.minecraft.block.Block vine) {

		super(vine, HarvestType.TreeFruit);
	}

	@Override
	public List<ItemStack> getDrops(World world, BlockPos pos, IBlockState harvestState, Random rand, IFactorySettings settings) {

		NonNullList<ItemStack> drops = NonNullList.create();
		drops.add(new ItemStack(getPlant()));
		return drops;
	}

}
