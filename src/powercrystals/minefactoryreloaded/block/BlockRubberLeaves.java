package powercrystals.minefactoryreloaded.block;

import static powercrystals.minefactoryreloaded.item.base.ItemMulti.getName;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.util.ForgeDirection;

import powercrystals.minefactoryreloaded.api.rednet.connectivity.IRedNetNoConnection;
import powercrystals.minefactoryreloaded.gui.MFRCreativeTab;
import powercrystals.minefactoryreloaded.setup.MFRThings;

public class BlockRubberLeaves extends BlockLeaves implements IRedNetNoConnection
{
	static String[] _names = {null, "dry"};
	private IIcon[] _iconOpaque = new IIcon[_names.length];
	private IIcon[] _iconTransparent = new IIcon[_names.length];

	public BlockRubberLeaves()
	{
		setBlockName("mfr.rubberwood.leaves");
		setCreativeTab(MFRCreativeTab.tab);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister ir)
	{
		String unlocalizedName = getUnlocalizedName();
		for (int i = _names.length; i --> 0; )
		{
			String name = getName(unlocalizedName, _names[i]);
			_iconOpaque[i] = ir.registerIcon("minefactoryreloaded:" + name + ".opaque");
			_iconTransparent[i] = ir.registerIcon("minefactoryreloaded:" + name + ".transparent");
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side)
	{
		return getIcon(side, world.getBlockMetadata(x, y, z));
	}

	@Override
	public IIcon getIcon(int side, int meta)
	{
		meta %= 4; // bits 3 and 4 are state flags
		// using mod instead of bitand to preserve sign just incase something is passing us a negative value
		if (meta < 0 || meta >= _names.length) {
			return Blocks.bedrock.getIcon(0, 0); // invalid metadata gets something distinct (green bedrock!)
		}

		return isOpaqueCube() ? _iconOpaque[meta] : _iconTransparent[meta];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderColor(int par1)
	{
		return par1 == 1 ? 0xFFFFFF : super.getRenderColor(par1);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int colorMultiplier(IBlockAccess iba, int x, int y, int z)
	{
		int meta = iba.getBlockMetadata(x, y, z) & 3;
		int r = 0;
		int g = 0;
		int b = 0;

		for (int l1 = -1; l1 <= 1; ++l1)
			for (int i2 = -1; i2 <= 1; ++i2)
			{
				int j2 = iba.getBiomeGenForCoords(x + i2, z + l1).getBiomeFoliageColor(x, y, z);
				r += (j2 & 16711680) >> 16;
			g += (j2 & 65280) >> 8;
			b += j2 & 255;
			}

		r = (r / 9 & 255);
		g = (g / 9 & 255);
		b = (b / 9 & 255);
		if (meta == 1)
			return (r / 4 << 16 | g / 4 << 8 | b / 4) + 0xc0c0c0;

		return r << 16 | g << 8 | b;
	}

	@Override
	public boolean isOpaqueCube()
	{
		return Blocks.leaves.isOpaqueCube();
	}

	@Override
	public Item getItemDropped(int par1, Random par2Random, int par3)
	{
		return Item.getItemFromBlock(MFRThings.rubberSaplingBlock);
	}

	@Override
	protected boolean canSilkHarvest()
	{
		return false;
	}

	private ThreadLocal<Boolean> updating = new ThreadLocal<Boolean>();

	@Override
	public void dropBlockAsItemWithChance(World world, int x, int y, int z, int meta, float chance, int fortune)
	{
		if (updating.get() != null)
			return;
		super.dropBlockAsItemWithChance(world, x, y, z, meta, chance, fortune);
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int meta, int fortune)
	{
		ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
		if ((meta & 4) != 0)
			return ret;

		int chance = 20 + 15 * (meta & 3);

		if (fortune > 0)
			chance = Math.max(chance - (2 << fortune), 10);

		if (world.rand.nextInt(chance) == 0)
			ret.add(new ItemStack(getItemDropped(meta & 3, world.rand, fortune), 1,
					world.rand.nextInt(50000) == 0 ? 2 : 0));

		return ret;
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random rand)
	{
		if (world.isRemote)
			return;
		int l = world.getBlockMetadata(x, y, z), meta = l & 3;
		if (meta == 0 & ((l & 4) == 0))
		{
			boolean decay = (l & 8) != 0;
			if (decay)
			{
				updating.set(Boolean.TRUE);
				super.updateTick(world, x, y, z, rand);
				updating.set(null);
				if (!world.getBlock(x, y, z).equals(this))
					dropBlockAsItem(world, x, y, z, l, 0);
				return;
			}
			int chance = 15;
			BiomeGenBase b = world.getBiomeGenForCoords(x, z);
			if (b != null)
			{
				float temp = b.temperature;
				float rain = b.rainfall; // getFloatRainfall is client only!?
				boolean t;
				decay |= (t = rain <= 0.05f);
				if (t) chance -= 5;
				decay |= ((rain <= 0.2f) & temp >= 1.2f);
				decay |= (t = temp > 1.8f);
				if (t) chance -= 5;
				if (rain >= 0.4f & temp <= 1.4f)
					chance += 7;
				else if (temp < 0.8f)
					chance += 3;
			}
			if (decay && rand.nextInt(chance) == 0)
			{
				world.setBlockMetadataWithNotify(x, y, z, l | 1, 2);
				return;
			}
		}
		super.updateTick(world, x, y, z, rand);
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block id, int meta)
	{
		if (updating.get() != null)
		{
			boolean decay = false;
			int chance = 15;
			BiomeGenBase b = world.getBiomeGenForCoords(x, z);
			if (b != null)
			{
				float temp = b.temperature;
				float rain = b.rainfall; // getFloatRainfall is client only!?
				boolean t;
				decay |= (t = rain <= 0.05f);
				if (t) chance -= 5;
				decay |= ((rain <= 0.2f) & temp >= 1.2f);
				decay |= (t = temp > 1.8f);
				if (t) chance -= 5;
				if (rain >= 0.4f & temp <= 1.4f)
					chance += 7;
				else if (temp < 0.8f)
					chance += 3;
			}
			if (decay && world.rand.nextInt(chance) == 0)
				world.setBlock(x, y, z, id, meta | 1, 2);
		}
	}

	@Override
	public int getFireSpreadSpeed(IBlockAccess world, int x, int y, int z, ForgeDirection face)
	{
		return super.getFireSpreadSpeed(world, x, y, z, face) * ((world.getBlockMetadata(x, y, z) & 3) * 2 + 1);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockAccess iba, int x, int y, int z, int side)
	{
		boolean cube = isOpaqueCube();
		if (cube && iba.getBlock(x, y, z) == this)
			return false;
		return cube ? super.shouldSideBeRendered(iba, x, y, z, side) : true;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void getSubBlocks(Item blockId, CreativeTabs creativeTab, List subTypes)
	{
		subTypes.add(new ItemStack(blockId, 1, 0));
		subTypes.add(new ItemStack(blockId, 1, 1));
	}

	@Override public String[] func_150125_e() { return null; }

}
