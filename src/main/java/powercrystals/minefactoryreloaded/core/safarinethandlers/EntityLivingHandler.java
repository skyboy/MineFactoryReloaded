package powercrystals.minefactoryreloaded.core.safarinethandlers;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import powercrystals.minefactoryreloaded.api.handler.ISafariNetHandler;

import javax.annotation.Nonnull;
import java.util.List;

import static net.minecraft.util.text.TextFormatting.DARK_GRAY;
import static net.minecraft.util.text.TextFormatting.GRAY;
import static net.minecraft.util.text.TextFormatting.ITALIC;

public class EntityLivingHandler implements ISafariNetHandler {

	@Override
	public Class<?> validFor() {

		return EntityLiving.class;
	}

	@Override
	public void addInformation(@Nonnull NBTTagCompound tag, World world, List<String> infoList, ITooltipFlag tooltipFlag) {

		if (tag.getBoolean("LeftHanded"))
			infoList.add(GRAY + "Southpaw");

		if (tooltipFlag.isAdvanced() && tag.getBoolean("NoAI"))
			infoList.add(DARK_GRAY + (ITALIC + "Brain Dead"));

		if (tooltipFlag.isAdvanced() && tag.getBoolean("PersistenceRequired"))
			infoList.add(DARK_GRAY + (ITALIC + "Persistent"));
	}

}
