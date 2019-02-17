package powercrystals.minefactoryreloaded.core.safarinethandlers;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import powercrystals.minefactoryreloaded.api.handler.ISafariNetHandler;

import javax.annotation.Nonnull;
import java.util.List;

public class EntityAgeableHandler implements ISafariNetHandler {

	@Override
	public Class<?> validFor() {

		return EntityAgeable.class;
	}

	@Override
	public void addInformation(@Nonnull NBTTagCompound safariNetEntity, World world, List<String> infoList, ITooltipFlag tooltipFlag) {

		if (safariNetEntity.getInteger("Age") < 0) {
			infoList.add("Baby");
		}
	}

}
