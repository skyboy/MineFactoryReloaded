package powercrystals.minefactoryreloaded.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class ItemBlockFactoryTree extends ItemBlockFactory
{
	public ItemBlockFactoryTree(int id)
	{
		super(id);
		setNames(new String[] {null, "massive"});
		setHasSubtypes(true);
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List info, boolean adv)
	{
		if (stack.getItemDamage() == 1)
			info.add("Warning: Pakratt's world ship.");
	}
}
