package powercrystals.minefactoryreloaded.api.mob;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.IInventory;
import net.minecraft.world.World;

import java.util.List;

/**
 * Defines a ranchable entity for use in the Rancher.
 *
 * @author PowerCrystals
 */
public interface IFactoryRanchable {

	/**
	 * @return The entity to ranch.
	 */
	Class<? extends EntityLivingBase> getRanchableEntity();

	/**
	 * @param world
	 * 		The world this entity is in.
	 * @param entity
	 * 		The entity instance being ranched.
	 * @param rancher
	 * 		The rancher instance doing the ranching. Used to access the
	 * 		Rancher's inventory when milking cows, for example.
	 *
	 * @return A list of drops. All Items BE dropped, fluids not matching the tank's contents will be discarded.
	 */
	List<RanchedItem> ranch(World world, EntityLivingBase entity, IInventory rancher);

}
