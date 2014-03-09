package powercrystals.minefactoryreloaded.world;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import powercrystals.core.util.UtilInventory;

import skyboy.core.world.WorldProxy;

// Nigel says: This is a
public class SmashingWorld extends WorldProxy
{
	protected int blockID, meta;
	protected int x = 0, y = 1, z = 0;
	protected Material mat;

	public SmashingWorld(World world)
	{
		super(world);
	}
	
	@Override
	public boolean spawnEntityInWorld(Entity par1Entity)
	{
		return true;
	}

	@Override
	public boolean setBlock(int par1, int par2, int par3, int par4, int par5, int par6)
	{
		return true;
	}

	@Override
	public boolean setBlockMetadataWithNotify(int par1, int par2, int par3, int par4, int par5)
	{
		return true;
	}

	@Override
	public boolean setBlockToAir(int par1, int par2, int par3)
	{
		return true;
	}

	@Override
	public boolean destroyBlock(int par1, int par2, int par3, boolean par4)
	{
		return true;
	}

	@Override
	public boolean setBlock(int par1, int par2, int par3, int par4)
	{
		return true;
	}

	@Override
	public int getBlockId(int X, int Y, int Z)
	{
		if (x == X & y == Y & z == Z)
			return blockID;
		return 0;
	}

	@Override
	public TileEntity getBlockTileEntity(int X, int Y, int Z)
	{
		return null;
	}

	@Override
	public Material getBlockMaterial(int X, int Y, int Z)
	{
		if (x == X & y == Y & z == Z)
			return mat;
		return Material.air;
	}

	@Override
	public int getBlockMetadata(int X, int Y, int Z)
	{
		if (x == X & y == Y & z == Z)
			return meta;
		return 0;
	}

	@SuppressWarnings("rawtypes")
	public ArrayList smashBlock(ItemStack input, Block block, int blockId, int meta, int fortune)
	{
		ArrayList drops = null;
		if (block != null)
		{
			blockID = blockId;
			this.meta = meta;
			mat = block.blockMaterial;
			drops = block.getBlockDropped(this, x, y, z, meta, fortune);
			if (drops.size() == 1)
				if (UtilInventory.stacksEqual((ItemStack)drops.get(0), input, false))
					return null;
		}
		return drops;
	}
}
