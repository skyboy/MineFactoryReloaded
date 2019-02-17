package powercrystals.minefactoryreloaded.modhelpers.vanilla;

import net.minecraft.block.Block;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import powercrystals.minefactoryreloaded.api.util.IFactorySettings;
import powercrystals.minefactoryreloaded.api.util.IFactorySettings.SettingNames;
import powercrystals.minefactoryreloaded.farmables.harvestables.HarvestableTreeLeaves;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HarvestableVanillaLeaves extends HarvestableTreeLeaves {

	public HarvestableVanillaLeaves(Block block) {

		super(block);
	}

	@Override
	public List<ItemStack> getDrops(World world, BlockPos pos, IBlockState harvestState, Random rand, IFactorySettings settings) {

		if (settings.getBoolean(SettingNames.SHEARS_MODE))
			return super.getDrops(world, pos, harvestState, rand, settings);

		ArrayList<ItemStack> drops = new ArrayList<>();
		IBlockState state = world.getBlockState(pos);
		int d = getPlant().damageDropped(state);
		if (rand.nextInt(d == 3 ? 40 : 20) == 0)
			drops.add(new ItemStack(getPlant().getItemDropped(state, rand, 0), 1, d));

		if (getPlant() == Blocks.LEAVES && state.getValue(BlockOldLeaf.VARIANT) == BlockPlanks.EnumType.OAK)
			if (rand.nextInt(200) == 0)
				drops.add(new ItemStack(Items.APPLE));

		return drops;
	}

}
