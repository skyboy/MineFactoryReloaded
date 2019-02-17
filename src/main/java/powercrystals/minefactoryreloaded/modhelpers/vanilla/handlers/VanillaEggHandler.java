package powercrystals.minefactoryreloaded.modhelpers.vanilla.handlers;

import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityList.EntityEggInfo;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import powercrystals.minefactoryreloaded.api.handler.IMobEggHandler;

import javax.annotation.Nonnull;

public class VanillaEggHandler implements IMobEggHandler {

	@Override
	public EntityEggInfo getEgg(@Nonnull NBTTagCompound safariNet) {

		return EntityList.ENTITY_EGGS.get(new ResourceLocation(safariNet.getString("id")));
	}

}
