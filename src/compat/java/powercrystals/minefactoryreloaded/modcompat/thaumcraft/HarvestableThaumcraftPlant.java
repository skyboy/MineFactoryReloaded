/*
package powercrystals.minefactoryreloaded.modhelpers.thaumcraft;

import java.util.List;
import java.util.Map;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.api.HarvestType;
import powercrystals.minefactoryreloaded.api.IFactoryHarvestable;

public class HarvestableThaumcraftPlant implements IFactoryHarvestable
{
	private Block _block;
	
	public HarvestableThaumcraftPlant(Block blockId)
	{
		_block = blockId;
	}
	
	@Override
	public Block getPlant()
	{
		return _block;
	}
	
	@Override
	public HarvestType getHarvestType()
	{
		return HarvestType.Normal;
	}
	
	@Override
	public boolean breakBlock()
	{
		return true;
	}
	
	@Override
	public boolean canBeHarvested(World world, Map<String, Boolean> harvesterSettings, BlockPos pos)
	{
		return world.getBlockMetadata(x, y, z) > 1;
	}
	
	@Override
	public List<ItemStack> getDrops(World world, Random rand, Map<String, Boolean> harvesterSettings, BlockPos pos)
	{
		return _block.getDrops(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
	}
	
	@Override
	public void preHarvest(World world, BlockPos pos)
	{
	}
	
	@Override
	public void postHarvest(World world, BlockPos pos)
	{
		world.notifyBlocksOfNeighborChange(x, y, z, _block);
	}
}
*/
