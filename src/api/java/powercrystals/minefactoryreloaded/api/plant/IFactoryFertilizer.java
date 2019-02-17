package powercrystals.minefactoryreloaded.api.plant;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

/**
 * Defines a fertilizer item for use in the Fertilizer.
 *
 * @author PowerCrystals
 */
public interface IFactoryFertilizer {

	/**
	 * @return The fertilizer item managed by this instance.
	 */
	@Nonnull
	Item getFertilizer();

	/**
	 * Called to determine the type of fertilizer.
	 *
	 * @param stack
	 * 		The ItemStack used to fertilize.
	 *
	 * @return The type of fertilizer the stack is.
	 */
	@Nonnull
	FertilizerType getFertilizerType(@Nonnull ItemStack stack);

	/**
	 * Called when a fertilization is successful.
	 * <p>
	 * If you set the ItemStack size to 0, it will be deleted by the fertilizer.
	 *
	 * @param fertilizer
	 * 		The stack used to fertilize.
	 */
	void consume(@Nonnull ItemStack fertilizer);

}
