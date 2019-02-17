package powercrystals.minefactoryreloaded.modcompat.railcraft;

import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import powercrystals.minefactoryreloaded.api.integration.IMFRIntegrator;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static powercrystals.minefactoryreloaded.api.integration.IMFRIntegrator.findBlock;
import static powercrystals.minefactoryreloaded.modcompat.Compats.ModIds.MFR;
import static powercrystals.minefactoryreloaded.modcompat.Compats.ModIds.RAILCRAFT;

@IMFRIntegrator.DependsOn(RAILCRAFT)
public class Railcraft implements IMFRIntegrator {

	public void load() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {

		final Block factoryDecorativeStoneBlock = findBlock(MFR, "decorative_stone");

		String id = Block.REGISTRY.getNameForObject(factoryDecorativeStoneBlock).toString();
		FMLInterModComms.sendMessage(RAILCRAFT, "balast", String.format("%s@%s", id, 8));
		FMLInterModComms.sendMessage(RAILCRAFT, "balast", String.format("%s@%s", id, 9));
		// white sand? black sand?

		Object rockCrusher = Class.forName("mods.railcraft.api.crafting.RailcraftCraftingManager").getField("rockCrusher")
				.get(null);
		Method createNewRecipe = Class.forName("mods.railcraft.api.crafting.IRockCrusherCraftingManager").getMethod(
				"createNewRecipe", ItemStack.class, boolean.class, boolean.class);
		Method addOutput = Class.forName("mods.railcraft.api.crafting.IRockCrusherRecipe").getMethod("addOutput",
				ItemStack.class, float.class);

		Object recipe = createNewRecipe.invoke(rockCrusher, new ItemStack(factoryDecorativeStoneBlock, 1, 10),
				true, false);
		addOutput.invoke(recipe, new ItemStack(factoryDecorativeStoneBlock, 1, 2), 1.0f); // Paved Blackstone -> Cobble

		recipe = createNewRecipe
				.invoke(rockCrusher, new ItemStack(factoryDecorativeStoneBlock, 1, 11), true, false);
		addOutput.invoke(recipe, new ItemStack(factoryDecorativeStoneBlock, 1, 3), 1.0f); // Paved Whitestone -> Cobble

		recipe = createNewRecipe.invoke(rockCrusher, new ItemStack(factoryDecorativeStoneBlock, 1, 0), true, false);
		addOutput.invoke(recipe, new ItemStack(factoryDecorativeStoneBlock, 1, 2), 1.0f); // Smooth Blackstone -> Cobble

		recipe = createNewRecipe.invoke(rockCrusher, new ItemStack(factoryDecorativeStoneBlock, 1, 1), true, false);
		addOutput.invoke(recipe, new ItemStack(factoryDecorativeStoneBlock, 1, 3), 1.0f); // Smooth Whitestone -> Cobble

		recipe = createNewRecipe.invoke(rockCrusher, new ItemStack(factoryDecorativeStoneBlock, 1, 2), true, false);
		addOutput.invoke(recipe, new ItemStack(factoryDecorativeStoneBlock, 1, 8), 1.0f); // Cobble Blackstone -> Gravel + flint
		addOutput.invoke(recipe, new ItemStack(Items.FLINT, 1, 0), 0.05f);

		recipe = createNewRecipe.invoke(rockCrusher, new ItemStack(factoryDecorativeStoneBlock, 1, 3), true, false);
		addOutput.invoke(recipe, new ItemStack(factoryDecorativeStoneBlock, 1, 9), 1.0f); // Cobble Whitestone -> Gravel + flint
		addOutput.invoke(recipe, new ItemStack(Items.FLINT, 1, 0), 0.05f);
	}

}
