package powercrystals.minefactoryreloaded.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.api.rednet.IRedNetNoConnection;
import powercrystals.minefactoryreloaded.gui.MFRCreativeTab;

public class BlockRubberLeaves extends BlockLeaves implements IRedNetNoConnection
{
	private Icon _iconOpaque;
	private Icon _iconTransparent;

	public BlockRubberLeaves(int id)
	{
		super(id);
		setHardness(0.2F);
		setLightOpacity(1);
		setStepSound(soundGrassFootstep);
		setUnlocalizedName("mfr.rubberwood.leaves");
		setCreativeTab(MFRCreativeTab.tab);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister ir)
	{
		_iconOpaque = ir.registerIcon("minefactoryreloaded:" + getUnlocalizedName() + ".opaque");
		_iconTransparent = ir.registerIcon("minefactoryreloaded:" + getUnlocalizedName() + ".transparent");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Icon getBlockTexture(IBlockAccess world, int x, int y, int z, int side)
	{
		return Block.leaves.graphicsLevel ? _iconTransparent : _iconOpaque;
	}

	@Override
	public Icon getIcon(int side, int meta)
	{
		return Block.leaves.graphicsLevel ? _iconTransparent : _iconOpaque;
	}

	@Override
	public boolean isOpaqueCube()
	{
		return !Block.leaves.graphicsLevel;
	}

	@Override
	public int idDropped(int par1, Random par2Random, int par3)
	{
		return MineFactoryReloadedCore.rubberSaplingBlock.blockID;
	}

	@Override
	public int damageDropped(int par1)
	{
		return 0;
	}

	@Override
	protected boolean canSilkHarvest()
	{
		return false;
	}

	@Override
	public void dropBlockAsItemWithChance(World par1World, int x, int y, int z, int meta, float chance, int fortune)
	{
		if (!par1World.isRemote)
		{
			ArrayList<ItemStack> items = getBlockDropped(par1World, x, y, z, meta, fortune);
			//chance = ForgeEventFactory.fireBlockHarvesting(items, par1World, this, x, y, z, meta, fortune, chance, false, harvesters.get());

			for (ItemStack item : items)
				if (par1World.rand.nextFloat() <= chance)
					this.dropBlockAsItem_do(par1World, x, y, z, item);
		}
	}
	@Override
	public ArrayList<ItemStack> getBlockDropped(World world, int x, int y, int z, int meta, int fortune)
	{
		ArrayList<ItemStack> ret = new ArrayList<ItemStack>();

		int chance = 20;

		if (fortune > 0)
			chance = Math.min(chance - (2 << fortune), 10);

		if (world.rand.nextInt(chance) == 0)
			ret.add(new ItemStack(idDropped(meta, world.rand, fortune), 1, damageDropped(meta)));

		/* TODO: drop book (counts as fuel) with info on MFR
		chance = 100;

		if (fortune > 0)
			chance = Math.min(chance - (8 << fortune), 30);

		if (world.rand.nextInt(chance) == 0)
			ret.add(new ItemStack(Item.appleRed, 1, 0));
		//*/
		return ret;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockAccess iba, int x, int y, int z, int side)
	{
		return Block.leaves.graphicsLevel ? true : super.shouldSideBeRendered(iba, x, y, z, side);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void getSubBlocks(int blockId, CreativeTabs creativeTab, List subTypes)
	{
		subTypes.add(new ItemStack(blockId, 1, 0));
	}
}
