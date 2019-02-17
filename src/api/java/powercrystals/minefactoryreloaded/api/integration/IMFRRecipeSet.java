package powercrystals.minefactoryreloaded.api.integration;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.io.File;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static net.minecraftforge.oredict.OreDictionary.WILDCARD_VALUE;

public interface IMFRRecipeSet {

	/**
	 * Apply this annotation to your RecipeSet if it depends on other mods,
	 * they will be checked to see if they are loaded before creating your RecipeSet.
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	@interface DependsOn {

		String[] value();
	}

	/**
	 * Register ore dictionary entries for MFR's (or your) items that are not otherwise registered by default.
	 */
	default void registerOredict() {

	}

	/**
	 * Read configuration options for your recipe set from the provided config.
	 * <p>
	 * You are handed a {@link File} instead of a {@link net.minecraftforge.common.config.Configuration Configuration}
	 * so that you may either use a different format, or ignore this entirely if you have no settings without creating an empty file.
	 */
	default void readConfig(File config) {

	}

	/**
	 * Register your recipes for MFR's items.
	 * <p>
	 * Any recipes you do not register will have the vanilla recipe applied automatically.
	 */
	void registerRecipes();

	/**
	 * The name of your recipe set, by default the Class's name is used.
	 *
	 * @return String    The name of this set used in MFR's config for enabling/disabling sets
	 */
	default String getSetName() {

		return this.getClass().getSimpleName();
	}

	/**
	 * This interface is how RecipeSets add recipes for MFR's items.
	 * <p>
	 * Any fields with this type will never be null, unmatched recipes will receive the {@link IRecipeHolder#EMPTY} object.
	 * <p><p>
	 * Fields declared <code>final</code> with this type and the name of a recipe will be populated before {@link #registerOredict()}.
	 * <p>
	 * Fields declared <code>final</code> with an array of this type will be populated with all recipes starting with the field name.
	 */
	interface IRecipeHolder {

		/**
		 * The name of this Recipe
		 *
		 * @return The name of this Recipe
		 */
		@Nonnull
		ResourceLocation getName();

		/**
		 * Test whether or not this RecipeHolder is enabled.
		 * <p>
		 * This is only needed if you want to test for special conditions, and is automatically handled otherwise.
		 *
		 * @return true if this recipe is enabled
		 */
		boolean isEnabled();

		/**
		 * Get the ItemStack for this RecipeHolder.
		 * <p>
		 * This is only needed for special conditions within your own code.
		 *
		 * @return The ItemStack for this RecipeHolder
		 */
		@Nonnull
		ItemStack getItemStack();

		/**
		 * Get the ItemStack for this RecipeHolder.
		 * <p>
		 * This is only needed for special conditions within your own code.
		 *
		 * @param count
		 * 		The count the returned ItemStack should have
		 *
		 * @return The ItemStack for this RecipeHolder
		 */
		@Nonnull
		default ItemStack getItemStack(int count) {

			ItemStack r = getItemStack().copy();
			r.setCount(count);
			return r;
		}

		/**
		 * Add a shaped crafting recipe for this.
		 * <p>
		 * Any {@link IRecipeHolder}s in the input will be replaced with {@link IRecipeHolder#getItemStack(int) getItemStack(1)}
		 *
		 * @param input
		 * 		The Shape of this recipe followed by the list of ingredients
		 */
		void addShaped(@Nonnull Object... input);

		/**
		 * Add a shapeless crafting recipe for this.
		 * <p>
		 * Any {@link IRecipeHolder}s in the input will be replaced with {@link IRecipeHolder#getItemStack(int) getItemStack(1)}
		 *
		 * @param input
		 * 		The list of ingredients
		 */
		void addShapeless(@Nonnull Object... input);

		/**
		 * Add a smelting recipe for this.
		 * <p>
		 * If the input is an empty ItemStack it will be ignored.
		 *
		 * @param input
		 * 		The {@link ItemStack} that will smelt into {@link #getItemStack()}
		 * @param xp
		 * 		The XP value for this smelting recipe
		 */
		void addSmelting(@Nonnull ItemStack input, float xp);

		/**
		 * Add a smelting recipe for this, with 0 XP.
		 *
		 * @param input
		 * 		The {@link ItemStack} that will smelt into {@link #getItemStack()}
		 */
		default void addSmelting(@Nonnull ItemStack input) {

			this.addSmelting(input, 0f);
		}

		default void addSmelting(@Nonnull IRecipeHolder holder) {

			this.addSmelting(holder.getItemStack(1));
		}

		default void addSmelting(@Nonnull IRecipeHolder holder, float xp) {

			this.addSmelting(holder.getItemStack(1), xp);
		}

		/**
		 * Calling this method will disable the fallback vanilla recipe from registering.
		 */
		void disableFallback();

		/**
		 * This is just a dummy object, so that you may populate your fields with it
		 * if your IDE is complaining about nulls.
		 */
		IRecipeHolder EMPTY = new IRecipeHolder() {

			ResourceLocation DUMMY = new ResourceLocation("", "");

			@Override
			@Nonnull
			public ResourceLocation getName() {

				return DUMMY;
			}

			@Override
			public boolean isEnabled() {

				return false;
			}

			@Override
			@Nonnull
			public ItemStack getItemStack() {

				return ItemStack.EMPTY;
			}

			@Override
			public void addShaped(@Nonnull Object... input) {

			}

			@Override
			public void addShapeless(@Nonnull Object... input) {

			}

			@Override
			public void addSmelting(@Nonnull ItemStack input, float xp) {

			}

			@Override
			public void disableFallback() {

			}

		};
		/**
		 * This is just a dummy object, so that you may populate your fields with it
		 * if your IDE is complaining about nulls.
		 */
		IRecipeHolder[] EMPTY_ARRAY = { EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY };

	}

	//region CREATING ItemStacks
	static ItemStack stack(ItemStack input) {

		return new ItemStack(input.getItem());
	}

	static ItemStack stack(ItemStack input, int size) {

		return new ItemStack(input.getItem(), size);
	}

	static ItemStack stack(ItemStack input, int size, int meta) {

		return new ItemStack(input.getItem(), size, meta);
	}

	static ItemStack stack(Item input) {

		return new ItemStack(input);
	}

	static ItemStack stack(Item input, int size) {

		return new ItemStack(input, size);
	}

	static ItemStack stack(Item input, int size, int meta) {

		return new ItemStack(input, size, meta);
	}

	static ItemStack stack(Block input) {

		return new ItemStack(input);
	}

	static ItemStack stack(Block input, int size) {

		return new ItemStack(input, size);
	}

	static ItemStack stack(Block input, int size, int meta) {

		return new ItemStack(input, size, meta);
	}

	static ItemStack stack_wildcard(ItemStack input) {

		return new ItemStack(input.getItem(), 1, WILDCARD_VALUE);
	}

	static ItemStack stack_wildcard(ItemStack input, int size) {

		return new ItemStack(input.getItem(), size, WILDCARD_VALUE);
	}

	static ItemStack stack_wildcard(Item input) {

		return new ItemStack(input, 1, WILDCARD_VALUE);
	}

	static ItemStack stack_wildcard(Item input, int size) {

		return new ItemStack(input, size, WILDCARD_VALUE);
	}

	static ItemStack stack_wildcard(Block input) {

		return new ItemStack(input, 1, WILDCARD_VALUE);
	}

	static ItemStack stack_wildcard(Block input, int size) {

		return new ItemStack(input, size, WILDCARD_VALUE);
	}
	//endregion

}
