package powercrystals.minefactoryreloaded.api.mob;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

/**
 * Defines a grindable entity for the Grinder.
 *
 * @author PowerCrystals
 */
public interface IFactoryGrindable {

	/**
	 * @return The class that this grindable instance is handling.
	 */
	Class<? extends EntityLivingBase> getGrindableEntity();

	/**
	 * @param world
	 * 		The world this entity is in.
	 * @param entity
	 * 		The entity instance being ground.
	 * @param random
	 * 		A Random instance.
	 *
	 * @return The drops generated when this entity is killed. Only one of these will be chosen.
	 */
	// TODO: change drop contract
	List<MobDrop> grind(World world, EntityLivingBase entity, Random random);

	/**
	 * @param entity
	 * 		The entity instance being ground.
	 *
	 * @return Whether this entity has been fully processed or not. (e.g., it is already dead)
	 */
	boolean processEntity(EntityLivingBase entity);

}
