package powercrystals.minefactoryreloaded.modhelpers.dyetrees;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;
import powercrystals.minefactoryreloaded.api.HarvestType;
import powercrystals.minefactoryreloaded.farmables.harvestables.HarvestableTreeLeaves;

import Reika.DyeTrees.API.TreeGetter;

public class HarvestableDyeLeaves extends HarvestableTreeLeaves
{
	public HarvestableDyeLeaves(int id)
	{
		super(id);
	}
	
	@Override
	public List<ItemStack> getDrops(World world, Random rand, Map<String, Boolean> harvesterSettings, int x, int y, int z)
	{
		if(harvesterSettings.get("silkTouch") != null && harvesterSettings.get("silkTouch"))
		{
			int blockId = world.getBlockId(x, y, z);
			Block block = Block.blocksList[blockId];
			if (block instanceof IShearable)
			{
				ItemStack stack = new ItemStack(Item.shears, 1, 0);
				if (((IShearable)block).isShearable(stack, world, x, y, z))
				{
					return ((IShearable)block).onSheared(stack, world, x, y, z, 0);
				}
			}
			ArrayList<ItemStack> drops = new ArrayList<ItemStack>();
			int meta = world.getBlockMetadata(x, y, z);
			if(blockId == Block.leaves.blockID)
			{
				meta = meta & 0x03;
			}
			drops.add(new ItemStack(getPlantId(), 1, meta));
			return drops;
		}
		else if(getPlantId() == TreeGetter.getNaturalDyeLeafID())
		{
			ArrayList<ItemStack> drops = new ArrayList<ItemStack>();
			if(rand.nextInt(20) == 0) drops.add(TreeGetter.getDyeSapling(world.getBlockMetadata(x, y, z)));
			if(rand.nextInt(200) == 0) drops.add(new ItemStack(Item.appleRed, 1, 0));
			if(rand.nextInt(10) == 0) drops.add(new ItemStack(Item.dyePowder.itemID, 1, world.getBlockMetadata(x, y, z)));
			//for rainbow saplings, need API entry first :D
			//if(rand.nextInt(10000) == 0) drops.add(new ItemStack(Item.dyePowder.itemID, 1, world.getBlockMetadata(x, y, z)));
			return drops;
		}
		else
		{
			return Block.blocksList[getPlantId()].getBlockDropped(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
		}
	}
}
