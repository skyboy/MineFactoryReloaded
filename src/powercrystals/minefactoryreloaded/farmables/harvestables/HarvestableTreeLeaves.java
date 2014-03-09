package powercrystals.minefactoryreloaded.farmables.harvestables;

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

public class HarvestableTreeLeaves extends HarvestableStandard
{
	public HarvestableTreeLeaves(int id)
	{
		super(id, HarvestType.TreeLeaf);
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
			int meta = block.damageDropped(world.getBlockMetadata(x, y, z));
			drops.add(new ItemStack(getPlantId(), 1, meta));
			return drops;
		}
		else if(getPlantId() == Block.leaves.blockID && (world.getBlockMetadata(x, y, z) & 3) == 0)
		{
			ArrayList<ItemStack> drops = new ArrayList<ItemStack>();
			if(rand.nextInt(20) == 0) drops.add(new ItemStack(Block.sapling));
			if(rand.nextInt(200) == 0) drops.add(new ItemStack(Item.appleRed));
			return drops;
		}
		else
		{
			return Block.blocksList[getPlantId()].getBlockDropped(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
		}
	}
	
	@Override
	public void postHarvest(World world, int x, int y, int z)
	{
		int id = getPlantId();
		
        notifyBlock(world, x, y - 1, z, id);
        notifyBlock(world, x - 1, y, z, id);
        notifyBlock(world, x + 1, y, z, id);
        notifyBlock(world, x, y, z - 1, id);
        notifyBlock(world, x, y, z + 1, id);
        notifyBlock(world, x, y + 1, z, id);
	}
	
	protected void notifyBlock(World world, int x, int y, int z, int id)
	{
		Block block = Block.blocksList[world.getBlockId(x, y, z)];
		if (block != null && !block.isLeaves(world, x, y, z))
			world.notifyBlockOfNeighborChange(x, y, z, id);
	}
}
