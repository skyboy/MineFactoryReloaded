package powercrystals.minefactoryreloaded.core.safarinethandlers;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import powercrystals.minefactoryreloaded.api.handler.ISafariNetHandler;
import powercrystals.minefactoryreloaded.core.MFRUtil;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.util.List;

import static net.minecraft.util.text.TextFormatting.*;

public class EntityLivingBaseHandler implements ISafariNetHandler {

	@Override
	public Class<?> validFor() {

		return EntityLivingBase.class;
	}

	@Override
	public void addInformation(@Nonnull NBTTagCompound tag, World world, List<String> infoList, ITooltipFlag tooltipFlag) {

		if (tag.hasKey("CustomName")) {
			String name = tag.getString("CustomName");
			if (!name.isEmpty()) {
				infoList.add("Name: " + name);
			}
		}

		if (tag.getBoolean("Silent"))
			infoList.add(GOLD + (ITALIC + "Silent"));

		float abs = tag.getFloat("AbsorptionAmount");
		if (abs > 0)
			infoList.add("Absorption: " + abs);

		infoList.add("Health: " + new BigDecimal(tag.getFloat("Health")).toPlainString());

		if (tooltipFlag.isAdvanced()) {
			if (tag.hasKey("ActiveEffects")) {
				if (MFRUtil.isShiftKeyDown()) {
					NBTTagList l = tag.getTagList("ActiveEffects", 10);
					infoList.add("Potions:");

					for (int i = 0, e = l.tagCount(); i < e; ++i) {
						NBTTagCompound t = l.getCompoundTagAt(i);
						PotionEffect f = PotionEffect.readCustomPotionEffectFromNBT(t);
						Potion p = f.getPotion();
						String s = MFRUtil.localize(f.getEffectName(), true).trim();

						int a = f.getAmplifier();
						if (a > 0)
							s = (s + " " + MFRUtil.localize("potion.potency." + a, true, "x" + (a + 1))).trim();

						s += RESET + " - " + Potion.getPotionDurationString(f, 1.0F).trim();
						infoList.add("    " + (p.isBadEffect() ? RED : DARK_BLUE) + s + RESET);
					}
				} else
					infoList.add(MFRUtil.shiftForInfo());
			}
		}

		if (tooltipFlag.isAdvanced() && tag.getBoolean("Glowing"))
			infoList.add(YELLOW + (UNDERLINE + "Glowing"));
	}

}
