package powercrystals.minefactoryreloaded.api;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public interface IDeepStorageUnit {

	/**
	 * @return A populated @Nonnull ItemStack with stackSize for the full amount of
	 * materials in the DSU. <br>
	 * May have a stackSize > getMaxStackSize(). May have a stackSize of
	 * 0 (indicating locked contents).
	 */
	@Nonnull
	ItemStack getStoredItemType();

	/**
	 * Sets the total amount of the item currently being stored, or zero if all
	 * items are to be removed.
	 */
	void setStoredItemCount(int amount);

	/**
	 * Sets the type of the stored item and initializes the number of stored
	 * items to amount.
	 * <p>
	 * Will overwrite any existing stored items.
	 */
	void setStoredItemType(@Nonnull ItemStack type, int amount);

	/**
	 * @return The maximum number of items the DSU can hold. <br>
	 * May change based on the current type stored.
	 */
	int getMaxStoredCount();

	//TODO add proper comment and change the one for getStoredItemType

	default int getStoredItemCount() {

		return this.getStoredItemType().getCount();
	}

}
