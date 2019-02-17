package powercrystals.minefactoryreloaded.core.safarinethandlers;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import powercrystals.minefactoryreloaded.api.handler.ISafariNetHandler;

import javax.annotation.Nonnull;
import java.util.List;

public class SheepHandler implements ISafariNetHandler {

	@Override
	public Class<?> validFor() {

		return EntitySheep.class;
	}

	@Override
	public void addInformation(@Nonnull NBTTagCompound safariNetEntity, World world, List<String> infoList, ITooltipFlag tooltipFlag) {

		infoList.add("Wool: " + EnumDyeColor.byMetadata((safariNetEntity.getByte("Color") & 15)));
	}

}
