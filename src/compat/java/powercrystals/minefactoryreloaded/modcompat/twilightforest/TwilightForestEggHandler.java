/*
package powercrystals.minefactoryreloaded.modhelpers.twilightforest;

import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry.EntityRegistration;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList.EntityEggInfo;
import net.minecraft.entity.EntityList;
import net.minecraft.item.ItemStack;

import powercrystals.minefactoryreloaded.api.IMobEggHandler;

public class TwilightForestEggHandler implements IMobEggHandler
{
	@SuppressWarnings("unchecked")
	@Override
	public EntityEggInfo getEgg(@Nonnull ItemStack safariNet)
	{
		Class<? extends Entity> entityClass = (Class<? extends Entity>)EntityList.stringToClassMapping.get(safariNet.getTagCompound().getString("id"));
		if(entityClass == null)
		{
			return null;
		}
		
		EntityRegistration er = EntityRegistry.instance().lookupModSpawn(entityClass, true);
		if(er != null && er.getContainer() == TwilightForest.twilightForestContainer)
		{
			return (EntityEggInfo)TwilightForest.entityEggs.get(er.getModEntityId());
		}
		return null;
	}
}
*/
