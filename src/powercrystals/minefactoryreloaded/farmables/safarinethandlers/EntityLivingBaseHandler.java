package powercrystals.minefactoryreloaded.farmables.safarinethandlers;

import static net.minecraft.util.EnumChatFormatting.*;

import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import powercrystals.minefactoryreloaded.api.ISafariNetHandler;
import powercrystals.minefactoryreloaded.core.MFRUtil;

public class EntityLivingBaseHandler implements ISafariNetHandler
{
	@Override
	public Class<?> validFor()
	{
		return EntityLivingBase.class;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void addInformation(ItemStack safariNetStack, EntityPlayer player, List infoList, boolean advancedTooltips)
	{
		NBTTagCompound tag = safariNetStack.getTagCompound();
		float abs = tag.getFloat("AbsorptionAmount");
		if (abs > 0)
			infoList.add("Absorption: " + abs);

		if (tag.hasKey("HealF"))
			infoList.add("Health: " + (tag.getFloat("HealF")));
		else
			infoList.add("Health: " + (tag.getShort("Health")));

		if (advancedTooltips)
		{
			if (tag.hasKey("ActiveEffects"))
			{
				if (MFRUtil.isShiftKeyDown())
				{
					NBTTagList l = tag.getTagList("ActiveEffects");
					infoList.add("Potions:");

					for (int i = 0, e = l.tagCount(); i < e; ++i)
					{
						NBTTagCompound t = (NBTTagCompound)l.tagAt(i);
						PotionEffect f = PotionEffect.readCustomPotionEffectFromNBT(t);
						Potion p = Potion.potionTypes[f.getPotionID()];
						String s = MFRUtil.localize(f.getEffectName(), true).trim();
						
						int a = f.getAmplifier();
						if (a > 0)
							s = (s + " " + MFRUtil.localize("potion.potency." + a, true, "x" + (a + 1))).trim();
						
						s += RESET + " - " + Potion.getDurationString(f).trim();
						infoList.add("    " + (p.isBadEffect() ? RED : DARK_BLUE) + s + RESET);
					}
				}
				else
					infoList.add(MFRUtil.shiftForInfo());
			}
		}
	}
}
