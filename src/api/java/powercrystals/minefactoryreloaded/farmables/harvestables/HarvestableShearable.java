package powercrystals.minefactoryreloaded.farmables.harvestables;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;
import powercrystals.minefactoryreloaded.api.plant.HarvestType;
import powercrystals.minefactoryreloaded.api.util.IFactorySettings;
import powercrystals.minefactoryreloaded.api.util.IFactorySettings.SettingNames;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HarvestableShearable extends HarvestableStandard {

	public HarvestableShearable(Block block, HarvestType harvestType) {

		super(block, harvestType);
	}

	public HarvestableShearable(Block block) {

		super(block);
	}

	@Override
	public List<ItemStack> getDrops(World world, BlockPos pos, IBlockState harvestState, Random rand, IFactorySettings settings) {

		Block block = harvestState.getBlock();
		if (settings.getBoolean(SettingNames.SHEARS_MODE)) {
			if (block instanceof IShearable) {
				ItemStack stack = new ItemStack(Items.SHEARS, 1, 0);
				if (((IShearable) block).isShearable(stack, world, pos)) {
					return ((IShearable) block).onSheared(stack, world, pos, 0);
				}
			}
			if (Item.getItemFromBlock(block) != Items.AIR) {
				ArrayList<ItemStack> drops = new ArrayList<>();
				drops.add(block.getItem(world, pos, harvestState));
				return drops;
			}
		}

		return block.getDrops(world, pos, harvestState, 0);
	}

}
