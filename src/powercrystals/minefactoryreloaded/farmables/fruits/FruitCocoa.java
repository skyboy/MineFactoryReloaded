package powercrystals.minefactoryreloaded.farmables.fruits;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.api.IFactoryFruit;

public class FruitCocoa implements IFactoryFruit
{
	private int sourceId;
	private ItemStack replBlock;
	
	public FruitCocoa(int sourceId, ItemStack replBlock)
	{
		if(sourceId > Block.blocksList.length)
		{
			throw new IllegalArgumentException("Passed an Item ID to FactoryFruitStandard's source block argument");
		}
		this.sourceId = sourceId;
		this.replBlock = replBlock;
	}
	
	public FruitCocoa(int sourceId)
	{
		this(sourceId, null);
	}
	
	@Override
	public int getSourceBlockId()
	{
		return sourceId;
	}
	
	@Override
	public boolean canBePicked(World world, int x, int y, int z)
	{
		int blockMetadata = world.getBlockMetadata(x, y, z);
		return ((blockMetadata & 12) >> 2) >= 2;
	}
	
	@Override
	public ItemStack getReplacementBlock(World world, int x, int y, int z)
	{
		return replBlock;
	}
	
	@Override
	public void prePick(World world, int x, int y, int z)
	{
	}
	
	@Override
	public List<ItemStack> getDrops(World world, Random rand, int x, int y, int z)
	{
		return Block.blocksList[sourceId].getBlockDropped(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
	}
	
	@Override
	public void postPick(World world, int x, int y, int z)
	{
	}
}
