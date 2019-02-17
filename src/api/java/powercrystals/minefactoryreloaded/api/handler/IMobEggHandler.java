package powercrystals.minefactoryreloaded.api.handler;

import net.minecraft.entity.EntityList.EntityEggInfo;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;

/**
 * Defines a class that MFR will use to local egg info for a given mob. This is
 * used to color the Safari Net based on the captured mob.
 *
 * @author PowerCrystals
 */
public interface IMobEggHandler {

	/**
	 * @param safariNetEntity
	 * 		The Entity NBT the Safari Net is looking for egg info.
	 *
	 * @return An EntityEggInfo, or null if this instance cannot handle this mob.
	 */
	// TODO: separate from vanilla here
	EntityEggInfo getEgg(@Nonnull NBTTagCompound safariNetEntity);

}
