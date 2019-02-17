/*
package powercrystals.minefactoryreloaded.modhelpers.thaumcraft;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.farmables.harvestables.HarvestableTreeLeaves;

public class HarvestableThaumcraftLeaves extends HarvestableTreeLeaves
{
	private Item _plant;
	
	public HarvestableThaumcraftLeaves(Block block, Item plant)
	{
		super(block);
		_plant = plant;
	}
	
	@Override
	public List<ItemStack> getDrops(World world, Random rand, Map<String, Boolean> harvesterSettings, BlockPos pos)
	{
		if(harvesterSettings.get("silkTouch") != null && harvesterSettings.get("silkTouch"))
		{
			ArrayList<ItemStack> drops = new ArrayList<ItemStack>();
			drops.add(new ItemStack(getPlant(), 1, world.getBlockMetadata(x, y, z) & 0x01));
			return drops;
		}
		else
		{
			int leafMeta = world.getBlockMetadata(x, y, z) & 0x01;
			NonNullList<ItemStack> drops = new LinkedList<@Nonnull ItemStack>();
			if(rand.nextInt(leafMeta == 0 ? 300 : 500) == 0)
			{
				drops.add(new ItemStack(_plant, 1, leafMeta));
			}
			return drops;
		}
	}
}
*/
