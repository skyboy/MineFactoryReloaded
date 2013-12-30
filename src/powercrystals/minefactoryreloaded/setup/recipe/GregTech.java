package powercrystals.minefactoryreloaded.setup.recipe;

import gregtechmod.api.GregTech_API;
import ic2.api.item.Items;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.setup.MFRConfig;
import powercrystals.minefactoryreloaded.setup.Machine;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;

public class GregTech extends Vanilla
{
	@Override
	protected void registerMachines()
	{
		if(!Loader.isModLoaded("gregtech_addon") || !Loader.isModLoaded("IC2"))
		{
			return;
		}
		try
		{
			ItemStack steelPipe = GregTech_API.ItemStack getGregTechBlock(1, 1, 1801);
			ItemStack itemClearer = GregTech_API.ItemStack getGregTechBlock(1, 1, 24);
			ItemStack cropHarvester = GregTech_API.ItemStack getGregTechBlock(1, 1, 26);
			ItemStack generator = Items.getItem("generator");
			ItemStack compressor = Items.getItem("compressor");
			ItemStack luminator = Items.getItem("luminator");
			ItemStack mfsUnit = Items.getItem("mfsUnit");
			ItemStack reactorChamber = Items.getItem("reactorChamber");
			ItemStack reinforcedGlass = Items.getItem("reinforcedGlass");
			
			if(Machine.Planter.getIsRecipeEnabled())
			{
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.machineBlocks.get(0), 1, 0), new Object[]
						{
					"PIP",
					"CBC",
					"   ",
					'P', "sheetPlastic",
					'I', "itemClearer",
					'B', MineFactoryReloadedCore.machineBaseItem,
					'C', "craftingCircuitTier04",
						} ));
			}
			
			if(Machine.Fisher.getIsRecipeEnabled())
			{
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.machineBlocks.get(0), 1, 1), new Object[]
						{
					"CIC",
					"FBF",
					"   ",
					'B', MineFactoryReloadedCore.machineBaseItem,
					'I', "itemClearer",
					'F', "steelPipe",
					'C', "craftingCircuitTier04"
						} ));
			}
			
			if(Machine.Harvester.getIsRecipeEnabled())
			{
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.machineBlocks.get(0), 1, 2), new Object[]
						{
					"PHP",
					"CBC",
					"   ",
					'P', "sheetPlastic",
					'H', "cropHarvester",
					'B', MineFactoryReloadedCore.machineBaseItem,
					'C', "craftingCircuitTier02"
						} ));
			}
			
			if(Machine.Rancher.getIsRecipeEnabled())
			{
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.machineBlocks.get(0), 1, 3), new Object[]
						{
					"PTP",
					"SFS",
					"OCO",
					'P', "sheetPlastic",
					'T', "craftingPump",
					'S', Item.shears,
					'F', "craftingRawMachineTier01",
					'O', "plateTin",
					'C', "craftingCircuitTier04"
						} ));
			}
			
			if(Machine.Fertilizer.getIsRecipeEnabled())
			{
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.machineBlocks.get(0), 1, 4), new Object[]
						{
					"PTP",
					"SFS",
					"OCO",
					'P', "sheetPlastic",
					'T', Item.glassBottle,
					'S', Item.leather,
					'F', "craftingRawMachineTier01",
					'O', "plateSilver",
					'C', "craftingCircuitTier04"
						} ));
			}
			
			if(Machine.Vet.getIsRecipeEnabled())
			{
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.machineBlocks.get(0), 1, 5), new Object[]
						{
					"PTP",
					"TFT",
					"OCO",
					'P', "sheetPlastic",
					'T', MineFactoryReloadedCore.syringeEmptyItem,
					'F', "craftingRawMachineTier01",
					'O', "plateZinc",
					'C', "craftingCircuitTier04"
						} ));
			}
			
			if(Machine.ItemCollector.getIsRecipeEnabled())
			{
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.machineBlocks.get(0), 1, 6), new Object[]
						{
					"PVP",
					" F ",
					"PCP",
					'P', "sheetPlastic",
					'F', "craftingRawMachineTier01",
					'C', Block.chest,
					'V', "craftingConveyor"
						} ));
			}
			
			if(Machine.BlockBreaker.getIsRecipeEnabled())
			{
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.machineBlocks.get(0), 1, 7), new Object[]
						{
					"PTP",
					"SFS",
					"OCO",
					'P', "sheetPlastic",
					'T', "craftingItemValve",
					'S', Item.pickaxeIron,
					'F', "craftingRawMachineTier02",
					'O', "plateAluminium",
					'C', "craftingCircuitTier04"
						} ));
			}
			
			if(Machine.WeatherCollector.getIsRecipeEnabled())
			{
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.machineBlocks.get(0), 1, 8), new Object[]
						{
					"PTP",
					"TFT",
					"OCO",
					'P', "sheetPlastic",
					'T', Item.bucketEmpty,
					'F', "craftingRawMachineTier02",
					'O', "plateBrass",
					'C', "craftingCircuitTier04"
						} ));
			}
			
			if(Machine.SludgeBoiler.getIsRecipeEnabled())
			{
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.machineBlocks.get(0), 1, 9), new Object[]
						{
					"PTP",
					"SFS",
					"OCO",
					'P', "sheetPlastic",
					'T', Item.bucketEmpty,
					'S', Block.furnaceIdle,
					'F', "craftingRawMachineTier02",
					'O', "plateIron",
					'C', "craftingCircuitTier04"
						} ));
			}
			
			if(Machine.Sewer.getIsRecipeEnabled())
			{
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.machineBlocks.get(0), 1, 10), new Object[]
						{
					"PTP",
					"SFS",
					"SSS",
					'P', "sheetPlastic",
					'T', Item.bucketEmpty,
					'S', Block.brick,
					'F', "craftingRawMachineTier01",
						} ));
			}
			
			if(Machine.Composter.getIsRecipeEnabled())
			{
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.machineBlocks.get(0), 1, 11), new Object[]
						{
					"PTP",
					"SFS",
					"OCO",
					'P', "sheetPlastic",
					'T', Block.furnaceIdle,
					'S', Block.pistonBase,
					'F', "craftingRawMachineTier01",
					'O', Block.brick,
					'C', "craftingCircuitTier04"
						} ));
			}
			
			if(Machine.Breeder.getIsRecipeEnabled())
			{
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.machineBlocks.get(0), 1, 12), new Object[]
						{
					"PTP",
					"SFS",
					"OCO",
					'P', "sheetPlastic",
					'T', Item.appleGold,
					'S', Item.goldenCarrot,
					'F', "craftingRawMachineTier02",
					'O', new ItemStack(Item.dyePowder, 1, 5),
					'C', "craftingCircuitTier04"
						} ));
			}
			
			if(Machine.Grinder.getIsRecipeEnabled())
			{
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.machineBlocks.get(0), 1, 13), new Object[]
						{
					"PTP",
					"SFS",
					"OCO",
					'P', "sheetPlastic",
					'S', "craftingMachineParts",
					'T', "craftingGrinder",
					'F', "craftingRawMachineTier02",
					'O', Item.book,
					'C', "craftingCircuitTier04"
						} ));
			}
			
			if(Machine.AutoEnchanter.getIsRecipeEnabled())
			{
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.machineBlocks.get(0), 1, 14), new Object[]
						{
					"PTP",
					"SFS",
					"OCO",
					'P', "sheetPlastic",
					'T', "plateAlloyIridium",
					'S', Item.book,
					'F', "craftingRawMachineTier04",
					'O', "craftingCircuitTier06",
					'C', Block.obsidian
						} ));
			}
			
			if(Machine.Chronotyper.getIsRecipeEnabled())
			{
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.machineBlocks.get(0), 1, 15), new Object[]
						{
					"PTP",
					"TFT",
					"OCO",
					'P', "sheetPlastic",
					'T', "gemEmerald",
					'F', "craftingRawMachineTier02",
					'O', new ItemStack(Item.dyePowder, 1, 5),
					'C', "craftingCircuitTier06"
						} ));
			}
			
			if(Machine.Ejector.getIsRecipeEnabled())
			{
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.machineBlocks.get(1), 1, 0), new Object[]
						{							
					"PTP",
					" F ",
					"OOO",
					'P', "sheetPlastic",
					'T', "craftingRedstoneReceiver",
					'F', "craftingRawMachineTier02",
					'O', "dustRedstone"
						} ));
			}
			
			if(Machine.ItemRouter.getIsRecipeEnabled())
			{
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.machineBlocks.get(1), 1, 1), new Object[]
						{					
					"PTP",
					"SFS",
					"PSP",
					'P', "sheetPlastic",
					'T', Block.chest,
					'S', Item.redstoneRepeater,
					'F', "craftingRawMachineTier02"
						} ));
			}
			
			if(Machine.LiquidRouter.getIsRecipeEnabled())
			{
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.machineBlocks.get(1), 1, 2), new Object[]
						{					
					"PTP",
					"SFS",
					"PSP",
					'P', "sheetPlastic",
					'T', "craftingPump",
					'S', Item.redstoneRepeater,
					'F', "craftingRawMachineTier02"
						} ));
			}
			
			if(Machine.DeepStorageUnit.getIsRecipeEnabled())
			{
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.machineBlocks.get(1), 1, 3), new Object[]
						{					
					"PDP",
					"CFC",
					"PEP",
					'P', "sheetPlastic",
					'C', "craftingCircuitTier07",
					'E', Item.eyeOfEnder,
					'D', "craftingCircuitTier08",
					'F', "craftingRawMachineTier04"
						} ));
				
				if(MFRConfig.enableCheapDSU.getBoolean(false))
				{
					GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.machineBlocks.get(1), 1, 3), new Object[]
							{					
						"PCP",
						"CFC",
						"PCP",
						'P', "sheetPlastic",
						'C', Block.chest,
						'F', "craftingRawMachineTier01"
							} ));
				}
			}
			
			if(Machine.LiquiCrafter.getIsRecipeEnabled())
			{
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.machineBlocks.get(1), 1, 4), new Object[]
						{						
					"PTP",
					"SFS",
					"OCO",
					'P', "sheetPlastic",
					'T', Block.workbench,
					'S', "craftingPump",
					'F', "craftingRawMachineTier01",
					'O', Item.book,
					'C', "craftingLiquidMeter"
						} ));
			}
			
			if(Machine.LavaFabricator.getIsRecipeEnabled())
			{
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.machineBlocks.get(1), 1, 5), new Object[]
						{
					"PTP",
					"SFS",
					"OCO",
					'P', "sheetPlastic",
					'T', "plateSteel",
					'S', Item.magmaCream,
					'F', "craftingRawMachineTier03",
					'O', Item.blazeRod,
					'C', "craftingCircuitTier04"
						} ));
			}
			
			if(Machine.OilFabricator.getIsRecipeEnabled())
			{
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.machineBlocks.get(1), 1, 6), new Object[]
						{
					"PTP",
					"OFO",
					"OCO",
					'P', "sheetPlastic",
					'T', Block.tnt,
					'F', "craftingRawMachineTier03",
					'O', Block.obsidian,
					'C', "craftingCircuitTier04"
						} ));
			}
			
			if(Machine.AutoJukebox.getIsRecipeEnabled())
			{
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.machineBlocks.get(1), 1, 7), new Object[]
						{
					"PJP",
					" F ",
					" P ",
					'P', "sheetPlastic",
					'J', Block.jukebox,
					'F', "craftingRawMachineTier01"
						} ));
			}
			
			if(Machine.Unifier.getIsRecipeEnabled())
			{
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.machineBlocks.get(1), 1, 8), new Object[]
						{
					"PTP",
					"SFL",
					"OCO",
					'P', "sheetPlastic",
					'T', "plateCopper",
					'S', "plateSilver",
					'L', "plateGold",
					'F', "craftingRawMachineTier01",
					'O', Item.comparator,
					'C', Item.book
						} ));
			}
			
			if(Machine.AutoSpawner.getIsRecipeEnabled())
			{
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.machineBlocks.get(1), 1, 9), new Object[]
						{
					"PTP",
					"SFS",
					"OCO",
					'P', "sheetPlastic",
					'T', "plateAlloyIridium",
					'S', Item.magmaCream,
					'F', "craftingRawMachineTier02",
					'O', "gemRuby",
					'C', "craftingCircuitTier05"
						} ));
			}
			
			if(Machine.BioReactor.getIsRecipeEnabled())
			{
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.machineBlocks.get(1), 1, 10), new Object[]
						{
					"PTP",
					"SFS",
					"OCO",
					'P', "sheetPlastic",
					'T', Item.fermentedSpiderEye,
					'S', Item.slimeBall,
					'F', "craftingRawMachineTier03",
					'O', "craftingItemValve",
					'C', "craftingPump"
						} ));
			}
			
			if(Machine.BioFuelGenerator.getIsRecipeEnabled())
			{
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.machineBlocks.get(1), 1, 11), new Object[]
						{
					"PCP",
					"SFS",
					"OCO",
					'P', "sheetPlastic",
					'S', "plateIron",
					'F', generator,
					'O', Item.blazeRod,
					'C', "craftingCircuitTier04"
						} ));
			}
			
			if(Machine.AutoDisenchanter.getIsRecipeEnabled())
			{
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.machineBlocks.get(1), 1, 12), new Object[]
						{
					"PTP",
					"SFS",
					"OCO",
					'P', "sheetPlastic",
					'T', "plateAlloyIridium",
					'S', Item.book,
					'F', "craftingRawMachineTier03",
					'O', "craftingCircuitTier06",
					'C', Block.netherBrick
						} ));
			}
			
			if(Machine.Slaughterhouse.getIsRecipeEnabled())
			{
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.machineBlocks.get(1), 1, 13), new Object[]
						{
					"GIG",
					"SFS",
					"XCX",
					'G', "sheetPlastic",
					'S', "craftingPump",
					'X', "craftingGrinder",
					'I', "craftingDiamondBlade",
					'F', "craftingRawMachineTier02",
					'C', "craftingCircuitTier04"
						} ));
			}
			
			if(Machine.MeatPacker.getIsRecipeEnabled())
			{
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.machineBlocks.get(1), 1, 14), new Object[]
						{
					"GSG",
					"BFB",
					"TCT",
					'G', "sheetPlastic",
					'B', "craftingHeatingCoilTier01",
					'S', "craftingPump",
					'F', compressor,
					'C', "craftingMachineParts",
					'T', "craftingPump"
						} ));
			}
			
			if(Machine.EnchantmentRouter.getIsRecipeEnabled())
			{
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.machineBlocks.get(1), 1, 15), new Object[]
						{					
					"PBP",
					"SFS",
					"PSP",
					'P', "sheetPlastic",
					'B', Item.book,
					'S', Item.redstoneRepeater,
					'F', "craftingRawMachineTier02"
						} ));
			}
			
			if(Machine.LaserDrill.getIsRecipeEnabled())
			{
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.machineBlocks.get(2), 1, 0), new Object[]
						{
					"GFG",
					"CRC",
					"DLD",
					'G', "sheetPlastic",
					'D', "gemDiamond",
					'L', reinforcedGlass,
					'R', reactorChamber,
					'F', "craftingRawMachineTier04",
					'C', "craftingSuperconductor"
						} ));
			}
			
			if(Machine.LaserDrillPrecharger.getIsRecipeEnabled())
			{
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.machineBlocks.get(2), 1, 1), new Object[]
						{
					"GSG",
					"RFL",
					"DCD",
					'G', "sheetPlastic",
					'D', "gemDiamond",
					'S', MineFactoryReloadedCore.pinkSlimeballItem,
					'L', luminator,
					'F', mfsUnit,
					'C', "craftingCircuitTier07",
					'R', "craftingSuperconductor"
						} ));
			}
			
			if(Machine.AutoAnvil.getIsRecipeEnabled())
			{
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.machineBlocks.get(2), 1, 2), new Object[]
						{
					"GIG",
					"SFS",
					"ACA",
					'G', "sheetPlastic",
					'A', Block.anvil,
					'S', "plateSteel",
					'F', "craftingRawMachineTier04",
					'C', "craftingCircuitTier07",
					'I', "plateAlloyIridium"
						} ));
			}
			
			
			if(Machine.BlockSmasher.getIsRecipeEnabled())
			{
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.machineBlocks.get(2), 1, 3), new Object[]
						{
					"GPG",
					"HFH",
					"BCB",
					'G', "sheetPlastic",
					'P', Block.pistonBase,
					'H', MineFactoryReloadedCore.factoryHammerItem,
					'B', "craftingItemValve",
					'F', "craftingRawMachineTier03",
					'C', "craftingCircuitTier06"
						} ));
			}
			
			if(Machine.RedNote.getIsRecipeEnabled())
			{
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.machineBlocks.get(2), 1, 4), new Object[]
						{
					"GNG",
					"CFC",
					"GNG",
					'G', "sheetPlastic",
					'C', MineFactoryReloadedCore.rednetCableBlock,
					'N', Block.music,
					'F', "craftingRawMachineTier01"
						} ));
			}
			
			if(Machine.AutoBrewer.getIsRecipeEnabled())
			{
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.machineBlocks.get(2), 1, 5), new Object[]
						{
					"GBG",
					"CFC",
					"RCR",
					'G', "sheetPlastic",
					'C', "craftingPump",
					'B', Item.brewingStand,
					'R', "craftingItemValve",
					'F', "craftingRawMachineTier02",
					'C', "craftingCircuitTier05"
						} ));
			}
			
			if(Machine.FruitPicker.getIsRecipeEnabled())
			{
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.machineBlocks.get(2), 1, 6), new Object[]
						{
					"GXG",
					"SFS",
					"SCS",
					'G', "sheetPlastic",
					'X', Item.axeGold,
					'S', Item.shears,
					'F', "craftingRawMachineTier03",
					'C', "craftingCircuitTier04"
						} ));
			}
		}
		catch (Exception x)
		{
			x.printStackTrace();
		}
	}
	
	@Override
	protected void registerMachineUpgrades()
	{
		if(!Loader.isModLoaded("gregtech_addon") || !Loader.isModLoaded("IC2"))
		{
			return;
		}
		try
		{
			ItemStack insulatedGoldCableItem = Items.getItem("insulatedGoldCableItem");
			
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.upgradeItem, 1, 0), new Object[]
					{
				"III",
				"PPP",
				"RGR",
				'I', "dyeBlue",
				'P', "dustPlastic",
				'R', insulatedGoldCableItem,
				'G', "craftingCircuitTier02",
					} ));
			
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.upgradeItem, 1, 1), new Object[]
					{
				"III",
				"PPP",
				"RGR",
				'I', Item.ingotIron,
				'P', "dustPlastic",
				'R', insulatedGoldCableItem,
				'G', "craftingCircuitTier02",
					} ));
			
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.upgradeItem, 1, 2), new Object[]
					{
				"III",
				"PPP",
				"RGR",
				'I', "ingotTin",
				'P', "dustPlastic",
				'R', insulatedGoldCableItem,
				'G', "craftingCircuitTier02",
					} ));
			
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.upgradeItem, 1, 3), new Object[]
					{
				"III",
				"PPP",
				"RGR",
				'I', "ingotCopper",
				'P', "dustPlastic",
				'R', insulatedGoldCableItem,
				'G', "craftingCircuitTier04",
					} ));
			
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.upgradeItem, 1, 4), new Object[]
					{
				"III",
				"PPP",
				"RGR",
				'I', "ingotBronze",
				'P', "dustPlastic",
				'R', insulatedGoldCableItem,
				'G', "craftingCircuitTier04",
					} ));
			
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.upgradeItem, 1, 5), new Object[]
					{
				"III",
				"PPP",
				"RGR",
				'I', "ingotSilver",
				'P', "dustPlastic",
				'R', insulatedGoldCableItem,
				'G', "craftingCircuitTier04",
					} ));
			
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.upgradeItem, 1, 6), new Object[]
					{
				"III",
				"PPP",
				"RGR",
				'I', "ingotGold",
				'P', "dustPlastic",
				'R', insulatedGoldCableItem,
				'G', "craftingCircuitTier04",
					} ));
			
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.upgradeItem, 1, 7), new Object[]
					{
				"III",
				"PPP",
				"RGR",
				'I', Item.netherQuartz,
				'P', "dustPlastic",
				'R', insulatedGoldCableItem,
				'G', "craftingCircuitTier06",
					} ));
			
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.upgradeItem, 1, 8), new Object[]
					{
				"III",
				"PPP",
				"RGR",
				'I', "gemDiamond",
				'P', "dustPlastic",
				'R', insulatedGoldCableItem,
				'G', "craftingCircuitTier06",
					} ));
			
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.upgradeItem, 1, 9), new Object[]
					{
				"III",
				"PPP",
				"RGR",
				'I', "ingotPlatinum",
				'P', "dustPlastic",
				'R', insulatedGoldCableItem,
				'G', "craftingCircuitTier06",
					} ));
			
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.upgradeItem, 1, 10), new Object[]
					{
				"III",
				"PPP",
				"RGR",
				'I', Item.emerald,
				'P', "dustPlastic",
				'R', insulatedGoldCableItem,
				'G', "craftingCircuitTier06",
					} ));
			
			for(int i = 0; i < 16; i++)
			{
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.laserFocusItem, 1, i), new Object[]
						{
					"ENE",
					"NGN",
					"ENE",
					'E', Item.emerald,
					'N', "nuggetChrome",
					'G', new ItemStack(MineFactoryReloadedCore.factoryGlassPaneBlock, 1, i)
						} ));
			}
		}
		catch (Exception x)
		{
			x.printStackTrace();
		}
	}
	
	@Override
	protected void registerConveyors()
	{
		if(!Loader.isModLoaded("gregtech_addon") || !Loader.isModLoaded("IC2"))
		{
			return;
		}
		try
		{
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.conveyorBlock, 16, 16), new Object[]
					{
				"UUU",
				"RIR",
				'U', "itemRubber",
				'R', "dustRedstone",
				'I', "plateIron",
					} ));
			
			for(int i = 0; i < 16; i++)
			{
				GameRegistry.addShapelessRecipe(new ItemStack(MineFactoryReloadedCore.conveyorBlock, 1, i), new ItemStack(MineFactoryReloadedCore.conveyorBlock, 1, 16), new ItemStack(MineFactoryReloadedCore.ceramicDyeItem, 1, i));
			}
		}
		catch (Exception x)
		{
			x.printStackTrace();
		}
	}
	
	@Override
	protected void registerSyringes()
	{
		if(!Loader.isModLoaded("gregtech_addon") || !Loader.isModLoaded("IC2"))
		{
			return;
		}
		try
		{
			ItemStack cell = Items.getItem("cell");
			
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.syringeEmptyItem, 1), new Object[]
					{
				"PRP",
				"PCP",
				" I ",
				'P', "sheetPlastic",
				'R', "itemRubber",
				'I', Item.ingotIron,
				'C', cell
					} ));
			
			GameRegistry.addShapelessRecipe(new ItemStack(MineFactoryReloadedCore.syringeHealthItem), new Object[] { MineFactoryReloadedCore.syringeEmptyItem, Item.appleRed });
			GameRegistry.addShapelessRecipe(new ItemStack(MineFactoryReloadedCore.syringeGrowthItem), new Object[] { MineFactoryReloadedCore.syringeEmptyItem, Item.goldenCarrot });
			
			GameRegistry.addRecipe(new ItemStack(MineFactoryReloadedCore.syringeZombieItem, 1), new Object[]
					{
				"FFF",
				"FSF",
				"FFF",
				'F', Item.rottenFlesh,
				'S', MineFactoryReloadedCore.syringeEmptyItem,
					} );
			
			GameRegistry.addRecipe(new ItemStack(MineFactoryReloadedCore.syringeSlimeItem, 1), new Object[]
					{
				"   ",
				" S ",
				"BLB",
				'B', Item.slimeBall,
				'L', new ItemStack(Item.dyePowder, 1, 4),
				'S', MineFactoryReloadedCore.syringeEmptyItem,
					} );
			
			GameRegistry.addShapelessRecipe(new ItemStack(MineFactoryReloadedCore.syringeCureItem), new Object[] { MineFactoryReloadedCore.syringeEmptyItem, Item.appleGold });
		}
		catch (Exception x)
		{
			x.printStackTrace();
		}
	}
	
	@Override
	protected void registerMiscItems()
	{
		if(!Loader.isModLoaded("gregtech_addon") || !Loader.isModLoaded("IC2"))
		{
			return;
		}
		try
		{
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.plasticSheetItem, 4), new Object[]
					{
				"##",
				"##",
				'#', "dustPlastic",
					} ));
			
			GameRegistry.addRecipe(new ItemStack(MineFactoryReloadedCore.fertilizerItem, 16), new Object[]
					{
				"WBW",
				"STS",
				"WBW",
				'W', Item.wheat,
				'B', new ItemStack(Item.dyePowder, 1, 15),
				'S', Item.silk,
				'T', Item.stick,
					} );
			
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.safariNetItem, 1), new Object[]
					{
				" E ",
				"CGC",
				" E ",
				'E', Item.enderPearl,
				'G', Item.ghastTear,
				'C', "craftingCircuitTier04"
					} ));
			
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.safariNetSingleItem, 1), new Object[]
					{
				"SLS",
				"CBC",
				"S S",
				'S', Item.silk,
				'L', Item.leather,
				'B', Item.slimeBall,
				'C', "craftingCircuitTier02"
					} ));
			
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.safariNetJailerItem, 1), new Object[]
					{
				" P ",
				"ISI",
				" P ",
				'S', MineFactoryReloadedCore.safariNetSingleItem,
				'I', Block.fenceIron,
				'P', "plateIron"
					} ));
			
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.safariNetLauncherItem, 1), new Object[]
					{
				"PGP",
				"LGL",
				"IRI",
				'P', "sheetPlastic",
				'L', Item.glowstone,
				'G', Item.gunpowder,
				'I', "plateIron",
				'R', "craftingItemValve"
					} ));
			
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.factoryHammerItem, 1), new Object[]
					{
				"PPP",
				" S ",
				" S ",
				'P', "sheetPlastic",
				'S', Item.stick,
					} ));
			
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.blankRecordItem, 1), new Object[]
					{
				"RRR",
				"RPR",
				"RRR",
				'R', "dustPlastic",
				'P', Item.paper,
					} ));
			
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.spyglassItem), new Object[]
					{
				"GLG",
				"PLP",
				" S ",
				'G', "ingotGold",
				'L', Block.glass,
				'P', "sheetPlastic",
				'S', Item.stick
					} ));
			
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.portaSpawnerItem), new Object[]
					{
				"GLG",
				"DND",
				"GLG",
				'G', "plateChrome",
				'L', "plateAlloyIridium",
				'D', "gemDiamond",
				'N', Item.netherStar
					} ));
			
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.strawItem), new Object[]
					{
				"PP",
				"P ",
				"P ",
				'P', "sheetPlastic",
					} ));
			
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.xpExtractorItem), new Object[]
					{
				"PLP",
				"PLP",
				"RPR",
				'R', "itemRubber",
				'L', Block.glass,
				'P', "sheetPlastic",
					} ));
			
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.rulerItem), new Object[]
					{
				"P",
				"A",
				"P",
				'P', "sheetPlastic",
				'A', Item.paper,
					} ));
			
			GameRegistry.addRecipe(new ItemStack(MineFactoryReloadedCore.vineScaffoldBlock, 8), new Object[]
					{
				"VV",
				"VV",
				"VV",
				'V', Block.vine,
					} );
			
			GameRegistry.addShapelessRecipe(new ItemStack(MineFactoryReloadedCore.chocolateMilkBucketItem), Item.bucketMilk, Item.bucketEmpty, new ItemStack(Item.dyePowder, 1, 3));
			
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.plasticCupItem, 16), new Object[]
					{
				" P ",
				"P P",
				'P', "sheetPlastic",
					} ));
		}
		catch (Exception x)
		{
			x.printStackTrace();
		}
	}
	
	@Override
	protected void registerRails()
	{
		if(!Loader.isModLoaded("gregtech_addon") || !Loader.isModLoaded("IC2"))
		{
			return;
		}
		try
		{
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.railPickupCargoBlock, 1), new Object[]
					{
				" C ",
				"SDS",
				"SSS",
				'C', "craftingConveyor",
				'S', "sheetPlastic",
				'D', Block.railDetector
					} ));
			
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.railDropoffCargoBlock, 1), new Object[]
					{
				"SSS",
				"SDS",
				" C ",
				'C', "craftingConveyor",
				'S', "sheetPlastic",
				'D', Block.railDetector
					} ));
			
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.railPickupPassengerBlock, 1), new Object[]
					{
				" L ",
				"SDS",
				"SSS",
				'L', Block.blockLapis,
				'S', "sheetPlastic",
				'D', Block.railDetector
					} ));
			
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.railDropoffPassengerBlock, 1), new Object[]
					{
				"SSS",
				"SDS",
				" L ",
				'L', Block.blockLapis,
				'S', "sheetPlastic",
				'D', Block.railDetector
					} ));
		}
		catch (Exception x)
		{
			x.printStackTrace();
		}
	}
	
	@Override
	protected void registerRedNet()
	{
		if(!Loader.isModLoaded("gregtech_addon") || !Loader.isModLoaded("IC2"))
		{
			return;
		}
		try
		{
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.rednetCableBlock, 8), new Object[]
					{
				"PPP",
				"RRR",
				"PPP",
				'R', "dustRedstone",
				'P', "sheetPlastic",
					} ));
			
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.factoryDecorativeBrickBlock, 1, 11), new Object[]
					{
				"PRP",
				"RGR",
				"PIP",
				'R', "dustRedstone",
				'P', "sheetPlastic",
				'G', Block.glass,
				'I', "plateIron",
					} ));
			
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.rednetLogicBlock), new Object[]
					{
				"RDR",
				"LGL",
				"PHP",
				'H', new ItemStack(MineFactoryReloadedCore.factoryDecorativeBrickBlock, 1, 11),
				'P', "sheetPlastic",
				'G', "plateGold",
				'L', "craftingCircuitTier04",
				'D', "gemDiamond",
				'R', "dustRedstone",
					} ));
			
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.logicCardItem, 1, 0), new Object[]
					{
				"RPR",
				"PGP",
				"RPR",
				'P', "sheetPlastic",
				'G', "ingotGold",
				'R', "dustRedstone",
					} ));
			
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.logicCardItem, 1, 1), new Object[]
					{
				"GPG",
				"PCP",
				"RGR",
				'C', new ItemStack(MineFactoryReloadedCore.logicCardItem, 1, 0),
				'P', "sheetPlastic",
				'G', "plateGold",
				'R', "dustRedstone",
					} ));
			
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.logicCardItem, 1, 2), new Object[]
					{
				"DPD",
				"RCR",
				"GDG",
				'C', new ItemStack(MineFactoryReloadedCore.logicCardItem, 1, 1),
				'P', "sheetPlastic",
				'G', "plateSteel",
				'D', "gemDiamond",
				'R', "dustRedstone",
					} ));
			
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.rednetMeterItem, 1, 0), new Object[]
					{
				" G",
				"PR",
				"PP",
				'P', "sheetPlastic",
				'G', "nuggetGold",
				'R', "dustRedstone",
					} ));
			
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.rednetMemoryCardItem, 1, 0), new Object[]
					{
				"GGG",
				"PRP",
				"PPP",
				'P', "sheetPlastic",
				'G', "nuggetGold",
				'R', "dustRedstone",
					} ));
			
			GameRegistry.addShapelessRecipe(new ItemStack(MineFactoryReloadedCore.rednetMemoryCardItem, 1, 0), new ItemStack(MineFactoryReloadedCore.rednetMemoryCardItem, 1, 0));
			
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.rednetPanelBlock, 1, 0), new Object[]
					{
				"PCP",
				"IBI",
				"KPK",
				'P', "sheetPlastic",
				'C', MineFactoryReloadedCore.rednetCableBlock,
				'B', "craftingMonitorTier02",
				'I', "craftingCircuitTier02",
				'K', new ItemStack(Item.dyePowder, 1, 0)
					} ));
		}
		catch (Exception x)
		{
			x.printStackTrace();
		}
	}
}
