package powercrystals.minefactoryreloaded.api.handler;

import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fluids.FluidStack;

@FunctionalInterface
public interface ILiquidDrinkHandler {

	/**
	 * Called when an entity has consumed the fluid this manages.
	 *
	 * @param entity
	 * 		The entity that has consumed the fluid this
	 * 		ILiquidDrinkHandler manages
	 * @param fluid
	 * 		The FluidStack that this entity is consuming
	 */
	void onDrink(EntityLivingBase entity, FluidStack fluid);

	// TODO: on onInject? `source`?
}
