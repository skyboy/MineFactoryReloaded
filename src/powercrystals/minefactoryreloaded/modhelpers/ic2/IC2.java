package powercrystals.minefactoryreloaded.modhelpers.ic2;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.GameRegistry;

import ic2.api.item.Items;
import ic2.api.recipe.IMachineRecipeManager;
import ic2.api.recipe.ISemiFluidFuelManager.BurnProperty;
import ic2.api.recipe.Recipes;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;

import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.oredict.OreDictionary;

import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.api.HarvestType;
import powercrystals.minefactoryreloaded.farmables.fertilizables.FertilizerStandard;
import powercrystals.minefactoryreloaded.farmables.harvestables.HarvestableTreeLeaves;
import powercrystals.minefactoryreloaded.farmables.plantables.PlantableStandard;

@Mod(modid = "MineFactoryReloaded|CompatIC2", name = "MFR Compat: IC2", version = MineFactoryReloadedCore.version, dependencies = "after:MineFactoryReloaded;after:IC2")
@NetworkMod(clientSideRequired = false, serverSideRequired = false)
public class IC2
{
	@EventHandler
	public static void postLoad(FMLPostInitializationEvent evt)
	{
	}

	@EventHandler
	public static void load(FMLInitializationEvent evt)
	{
		if(!Loader.isModLoaded("IC2"))
		{
			FMLLog.warning("IC2 missing - MFR IC2 Compat not loading");
			return;
		}
		try
		{
			ItemStack crop = Items.getItem("crop");
			ItemStack rubber = Items.getItem("rubber").copy();
			ItemStack rubberSapling = Items.getItem("rubberSapling");
			ItemStack rubberLeaves = Items.getItem("rubberLeaves");
			ItemStack rubberWood = Items.getItem("rubberWood");
			ItemStack stickyResin = Items.getItem("resin");
			ItemStack plantBall = Items.getItem("plantBall");

			if(rubberSapling != null)
			{
				MFRRegistry.registerPlantable(new PlantableStandard(rubberSapling.itemID, rubberSapling.itemID));
				MFRRegistry.registerFertilizable(new FertilizableIC2RubberTree(rubberSapling.itemID));
			}
			if(rubberLeaves != null)
			{
				MFRRegistry.registerHarvestable(new HarvestableTreeLeaves(rubberLeaves.itemID));
			}
			if(rubberWood != null)
			{
				MFRRegistry.registerHarvestable(new HarvestableIC2RubberWood(rubberWood.itemID, HarvestType.Tree, stickyResin.itemID));
				MFRRegistry.registerFruitLogBlockId(((ItemBlock)rubberWood.getItem()).getBlockID());
				MFRRegistry.registerFruit(new FruitIC2Resin(rubberWood, stickyResin));
			}

			ItemStack fertilizer = Items.getItem("fertilizer");
			if(fertilizer != null)
			{
				MFRRegistry.registerFertilizer(new FertilizerStandard(fertilizer.itemID, fertilizer.getItemDamage()));
			}

			MFRRegistry.registerHarvestable(new HarvestableIC2Crop(crop.itemID));

			GameRegistry.addShapedRecipe(plantBall, new Object[]
					{
					"LLL",
					"L L",
					"LLL",
					Character.valueOf('L'), new ItemStack(MineFactoryReloadedCore.rubberLeavesBlock)
					} );

			Method m = null;
			ItemStack item = new ItemStack(MineFactoryReloadedCore.rubberSaplingBlock);
			rubber.stackSize = 1;
			try
			{
				for (Method t : IMachineRecipeManager.class.getDeclaredMethods())
					if (t.getName().equals("addRecipe"))
					{
						m = t;
						break;
					}
				m.invoke(Recipes.extractor, item, rubber);
			}
			catch (Throwable _)
			{
				try
				{
					Class<?> clazz = Class.forName("ic2.api.recipe.RecipeInputItemStack");
					Constructor<?> c = clazz.getDeclaredConstructor(ItemStack.class);
					Object o = c.newInstance(item);
					m.invoke(Recipes.extractor, o, null, new ItemStack[] {rubber});
				}
				catch (Throwable e)
				{
					e.printStackTrace();
				}
			}
			copyEthanol();
		}
		catch (Exception x)
		{
			x.printStackTrace();
		}
	}

	private static void copyEthanol()
	{
		BurnProperty q = Recipes.semiFluidGenerator.getBurnProperty(FluidRegistry.getFluid("bioethanol"));
		if (q != null)
			Recipes.semiFluidGenerator.addFluid("biofuel", q.amount, q.power);
		else if (FluidRegistry.getFluid("bioethanol") == null)
			Recipes.semiFluidGenerator.addFluid("biofuel", 10, 16);
	}
}
