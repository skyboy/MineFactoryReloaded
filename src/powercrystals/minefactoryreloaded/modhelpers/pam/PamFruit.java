package powercrystals.minefactoryreloaded.modhelpers.pam;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.api.IFactoryFruit;

public class PamFruit implements IFactoryFruit
{
	private int _sourceId;

	public PamFruit(int sourceId)
	{
		_sourceId = sourceId;
	}

	@Override
	public int getSourceBlockId()
	{
		return _sourceId;
	}

	@Override
	public boolean canBePicked(World world, int x, int y, int z)
	{
		return world.getBlockMetadata(x, y, z) >= 2;
	}

	@Override
	public ItemStack getReplacementBlock(World world, int x, int y, int z)
	{
		return new ItemStack(getSourceBlockId(), 1, 0);
	}

	@Override
	public void prePick(World world, int x, int y, int z)
	{
	}

	@Override
	public List<ItemStack> getDrops(World world, Random rand, int x, int y, int z)
	{
		int id = world.getBlockId(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);
		return Block.blocksList[id].getBlockDropped(world, x, y, z, meta, 0);
	}

	@Override
	public void postPick(World world, int x, int y, int z)
	{
	}

}
