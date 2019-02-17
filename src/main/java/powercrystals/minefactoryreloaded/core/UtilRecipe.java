package powercrystals.minefactoryreloaded.core;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.GameData;

import static cofh.core.util.helpers.ItemHelper.cloneStack;
import static cofh.core.util.helpers.ItemHelper.oreNameExists;
import static cofh.core.util.helpers.RecipeHelper.*;

public class UtilRecipe {

	private UtilRecipe() {

	}

	//TODO figure out if this should go into core
	public static void addRecipe(IRecipe recipe) {

		ResourceLocation location = getNameForRecipe(recipe.getRecipeOutput());
		recipe.setRegistryName(location);
		GameData.register_impl(recipe);
	}

	public static void addSurroundRecipe(ItemStack out, String one, ItemStack eight) {

		if (out.isEmpty() || eight.isEmpty() || !oreNameExists(one)) {
			return;
		}
		addShapedRecipe(out, "XXX", "XIX", "XXX", 'X', eight, 'I', one);
	}

	public static void addSurroundRecipe(ItemStack out, ItemStack one, ItemStack eight) {

		if (out.isEmpty() || one.isEmpty() || eight.isEmpty()) {
			return;
		}
		addShapedRecipe(cloneStack(out), "XXX", "XIX", "XXX", 'X', cloneStack(eight, 1), 'I', cloneStack(one, 1));
	}

	public static void addSurroundRecipe(ItemStack out, String one, String eight) {

		if (out.isEmpty() || !oreNameExists(one) || !oreNameExists(eight)) {
			return;
		}
		addShapedRecipe(out, "XXX", "XIX", "XXX", 'X', eight, 'I', one);
	}

	public static void addSurroundRecipe(ItemStack out, ItemStack one, String eight) {

		if (out.isEmpty() || one.isEmpty() || !oreNameExists(eight)) {
			return;
		}
		addShapedRecipe(out, "XXX", "XIX", "XXX", 'X', eight, 'I', one);
	}

	public static void addRotatedGearRecipe(ItemStack gear, ItemStack ingot, ItemStack center) {

		if (gear.isEmpty() || ingot.isEmpty() || center.isEmpty()) {
			return;
		}
		addShapedRecipe(cloneStack(gear), "X X", " I ", "X X", 'X', cloneStack(ingot, 1), 'I', cloneStack(center, 1));
	}

	public static void addRotatedGearRecipe(ItemStack gear, String ingot, ItemStack center) {

		if (gear.isEmpty() || center.isEmpty() || !oreNameExists(ingot)) {
			return;
		}
		addShapedRecipe(gear, "X X", " I ", "X X", 'X', ingot, 'I', center);
	}

	public static void addTwoWayConversionRecipe(ItemStack a, ItemStack b) {

		if (a.isEmpty() || b.isEmpty()) {
			return;
		}
		addShapelessRecipe(cloneStack(a, 1), cloneStack(b, 1));
		addShapelessRecipe(cloneStack(b, 1), cloneStack(a, 1));
	}

	public static void addFenceRecipe(ItemStack out, ItemStack in) {

		if (out.isEmpty() || in.isEmpty()) {
			return;
		}
		addShapedRecipe(cloneStack(out), "XXX", "XXX", 'X', cloneStack(in, 1));
	}
}
