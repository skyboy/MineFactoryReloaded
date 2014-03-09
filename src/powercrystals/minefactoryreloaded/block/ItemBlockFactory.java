package powercrystals.minefactoryreloaded.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.world.World;

public class ItemBlockFactory extends ItemBlock
{
	protected String[] _names = {null};

	public ItemBlockFactory(int id)
	{
		super(id);
	}

	protected void setNames(String[] names)
	{
		_names = names;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public Icon getIconFromDamage(int damage)
	{
		return Block.blocksList[getBlockID()].getIcon(2, damage);
	}

	@Override
	public int getMetadata(int meta)
	{
		return meta;
	}

	@Override
	public String getUnlocalizedName(ItemStack stack)
	{
		String str = _names[Math.min(stack.getItemDamage(), _names.length - 1)];
		return getUnlocalizedName() + (str != null ? "." + str : "");
	}

	@Override
	public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z,
			int side, float hitX, float hitY, float hitZ, int metadata)
	{
		if (world.getBlockId(x, y, z) > 0 && world.isAirBlock(x, y, z) && !world.setBlockToAir(x, y, z))
		{
			return false;
		}
		return super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, metadata);
	}

	public void getSubItems(int itemId, List<ItemStack> subTypes)
	{
		for(int i = 0; i < _names.length; i++)
		{
			subTypes.add(new ItemStack(itemId, 1, i));
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void getSubItems(int itemId, CreativeTabs creativeTab, List subTypes)
	{
		getSubItems(itemId, subTypes);
	}
}
