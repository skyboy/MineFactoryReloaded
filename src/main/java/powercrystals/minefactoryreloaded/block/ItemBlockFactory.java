package powercrystals.minefactoryreloaded.block;

import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import powercrystals.minefactoryreloaded.core.MFRUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import static powercrystals.minefactoryreloaded.item.base.ItemMulti.getName;

public class ItemBlockFactory extends ItemBlock
{
	protected String[] _names = {null};
	protected int metaOffset = 0;

	public ItemBlockFactory(Block block)
	{
		super(block);
		setMaxDamage(0);
	}

	public ItemBlockFactory(Block block, String[] names)
	{
		this(block);
		setNames(names);
	}

	public ItemBlockFactory(Block block, Integer metaOffset)
	{
		this(block);
		this.metaOffset = metaOffset.intValue();
	}

	protected void setNames(String[] names)
	{
		_names = names;
		setHasSubtypes(true);
	}

	protected String name(@Nonnull ItemStack stack)
	{
		return _names[Math.min(stack.getItemDamage(), _names.length - 1)];
	}

	@Override
	public int getMetadata(int meta)
	{
		return (metaOffset + meta) & 15;
	}

	@Override
	public String getUnlocalizedName(@Nonnull ItemStack stack)
	{
		return getName(getUnlocalizedName(), name(stack));
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag tooltipFlag) {

		String str = getName("tip.info" + getUnlocalizedName().substring(4), name(stack));
		str = MFRUtil.localize(str, true, null);
		if (str != null)
			tooltip.add(str);
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {

		if (isInCreativeTab(tab)) {
			for(int i = 0; i < _names.length; i++)
			{
				items.add(new ItemStack(this, 1, i));
			}
		}
	}
}
