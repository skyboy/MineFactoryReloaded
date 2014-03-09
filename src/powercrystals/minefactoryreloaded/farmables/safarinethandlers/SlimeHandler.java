package powercrystals.minefactoryreloaded.farmables.safarinethandlers;

import java.util.List;

import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import powercrystals.minefactoryreloaded.api.ISafariNetHandler;

public class SlimeHandler implements ISafariNetHandler
{
	@Override
	public Class<?> validFor()
	{
		return EntitySlime.class;
	}
	
	private static double log2 = Math.log(2);
	private static String[] sizes = {"Tiny", "Medium", "Large", "Extra Large", "Massive", "Incomprehensible"};
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void addInformation(ItemStack safariNetStack, EntityPlayer player, List infoList, boolean advancedTooltips)
	{
		int index = (int)Math.round(Math.log1p(safariNetStack.getTagCompound().getInteger("Size")) / log2);
		infoList.add("Size: " + sizes[Math.min(index, sizes.length - 1)]);
	}
}
