package powercrystals.minefactoryreloaded.api.handler;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * Defines a syringe for use in the Vet machine.
 *
 * @author PowerCrystals
 */
public interface ISyringe {

	/**
	 * Called when the vet is deciding if it should use this syringe.
	 *
	 * @param world
	 * 		The world instance.
	 * @param entity
	 * 		The entity being injected.
	 * @param syringe
	 * 		The syringe ItemStack.
	 *
	 * @return True if the entity can be injected by this syringe.
	 */
	boolean canInject(World world, EntityLivingBase entity, @Nonnull ItemStack syringe);

	/**
	 * Called to perform an injection.
	 *
	 * @param world
	 * 		The world instance.
	 * @param entity
	 * 		The entity being injected.
	 * @param syringe
	 * 		The syringe ItemStack.
	 *
	 * @return True if injection was successful.
	 */
	boolean inject(World world, EntityLivingBase entity, @Nonnull ItemStack syringe);

	/**
	 * Called to check if a syringe is empty
	 *
	 * @param syringe
	 * 		The syringe ItemStack.
	 *
	 * @return True if the syringe is empty
	 */
	boolean isEmpty(@Nonnull ItemStack syringe);

	/**
	 * Called to get the empty syringe
	 * <p>
	 * <b>Note</b>: this will replace the syringe, max stack size should be 1
	 *
	 * @param syringe
	 * 		The syringe ItemStack.
	 *
	 * @return An empty syringe ItemStack
	 */
	@Nonnull
	ItemStack getEmptySyringe(@Nonnull ItemStack syringe);

}
