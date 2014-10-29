package powercrystals.minefactoryreloaded.modhelpers.ic2;

import cofh.asm.relauncher.Strippable;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;

import ic2.api.item.IC2Items;
import ic2.api.recipe.ISemiFluidFuelManager.BurnProperty;
import ic2.api.recipe.RecipeInputItemStack;
import ic2.api.recipe.Recipes;

import net.minecraft.block.Block;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.oredict.OreDictionary;

import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.farmables.fertilizables.FertilizerStandard;
import powercrystals.minefactoryreloaded.farmables.harvestables.HarvestableTreeLeaves;
import powercrystals.minefactoryreloaded.farmables.plantables.PlantableSapling;
import powercrystals.minefactoryreloaded.setup.MFRThings;

@Mod(modid = "MineFactoryReloaded|CompatIC2", name = "MFR Compat: IC2", version = MineFactoryReloadedCore.version, dependencies = "after:MineFactoryReloaded;after:IC2")
public class IC2
{
	@EventHandler
	public static void postLoad(FMLPostInitializationEvent evt)
	{
		//filler for if you want things here.
	}

	@EventHandler
	@Strippable("mod:IC2")
	public static void load(FMLInitializationEvent evt)
	{
		try
		{
			ItemStack crop = IC2Items.getItem("crop");
			ItemStack rubber = IC2Items.getItem("rubber").copy();
			ItemStack rubberSapling = IC2Items.getItem("rubberSapling");
			ItemStack rubberLeaves = IC2Items.getItem("rubberLeaves");
			ItemStack rubberWood = IC2Items.getItem("rubberWood");
			ItemStack stickyResin = IC2Items.getItem("resin");
			ItemStack plantBall = IC2Items.getItem("plantBall");

			if(rubberSapling != null)
			{
				MFRRegistry.registerPlantable(new PlantableSapling(rubberSapling.getItem(),
						Block.getBlockFromItem(rubberSapling.getItem())));
				MFRRegistry.registerFertilizable(new FertilizableIC2RubberTree(Block.getBlockFromItem(rubberSapling.getItem())));
			}
			if(rubberLeaves != null)
			{
				MFRRegistry.registerHarvestable(new HarvestableTreeLeaves(Block.getBlockFromItem(rubberLeaves.getItem())));
			}
			if(rubberWood != null)
			{
				MFRRegistry.registerHarvestable(new HarvestableIC2RubberWood(Block.getBlockFromItem(rubberWood.getItem()), stickyResin.getItem()));
				MFRRegistry.registerFruitLogBlock(Block.getBlockFromItem(rubberWood.getItem()));
				FruitIC2Resin resin = new FruitIC2Resin(rubberWood, stickyResin);
				MFRRegistry.registerFruit(resin);
				MFRRegistry.registerFertilizable(resin);
			}

			ItemStack fertilizer = IC2Items.getItem("fertilizer");
			if(fertilizer != null)
			{
				MFRRegistry.registerFertilizer(new FertilizerStandard(fertilizer.getItem(), fertilizer.getItemDamage()));
			}

			if (crop != null)
			{
				IC2Crop ic2crop = new IC2Crop(Block.getBlockFromItem(crop.getItem()));
				MFRRegistry.registerHarvestable(ic2crop);
				MFRRegistry.registerFertilizable(ic2crop);
				MFRRegistry.registerFruit(ic2crop);
			}

			GameRegistry.addShapedRecipe(plantBall, new Object[]
					{
					"LLL",
					"L L",
					"LLL",
					Character.valueOf('L'), new ItemStack(MFRThings.rubberLeavesBlock)
					} );

			ItemStack item = new ItemStack(MFRThings.rubberSaplingBlock);
			rubber.stackSize = 1;
			try
			{
				Recipes.extractor.addRecipe(new RecipeInputItemStack(item), null, rubber);
			}
			catch (Throwable _)
			{
				{
					_.printStackTrace();
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
