package powercrystals.minefactoryreloaded.setup.recipe;

import gregtechmod.api.GregTech_API;
import gregtechmod.api.interfaces.IGT_RecipeAdder;
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
			ItemStack centrifuge = GregTech_API.ItemStack getGregTechBlock(1, 1, 62);
			ItemStack magicConverter = GregTech_API.ItemStack getGregTechBlock(1, 1, 42);
			ItemStack sorter = GregTech_API.ItemStack getGregTechBlock(1, 1, 23);
			ItemStack qChest = GregTech_API.ItemStack getGregTechBlock(1, 1, 49);
			ItemStack distil = GregTech_API.ItemStack getGregTechBlock(1, 1, 44);
			ItemStack dieselGen = GregTech_API.ItemStack getGregTechBlock(1, 1, 33);
			ItemStack microwave = GregTech_API.ItemStack getGregTechBlock(1, 1, 63);
			ItemStack typeSorter = GregTech_API.ItemStack getGregTechBlock(1, 1, 58);
			ItemStack metalWorkbench = GregTech_API.ItemStack getGregTechBlock(1, 1, 112);
			ItemStack GTteleporter = GregTech_API.ItemStack getGregTechBlock(1, 1, 90);
			ItemStack redstoneScale = GregTech_API.ItemStack getGregTechBlock(1, 1, 78);
			ItemStack gasTurbine = GregTech_API.ItemStack getGregTechBlock(1, 1, 34);
			ItemStack methane = GregTech_API.ItemStack getGregTechItem(0, 1, 9);
			ItemStack ICFertilizer = Items.getItem("fertilizer");
			ItemStack glassCable = Items.getItem("glassFiberCableBlock");
			
			
			registerMachine(Machine.Planter, new Object[] {
					"PIP",
					"CBC",
					"   ",
					'P', "sheetPlastic",
					'I', itemClearer,
					'B', MineFactoryReloadedCore.machineBaseItem,
					'C', "craftingCircuitTier04",
			});
						
			registerMachine(Machine.Fisher, new Object[] {
					"CIC",
					"FBF",
					"   ",
					'B', MineFactoryReloadedCore.machineBaseItem,
					'I', itemClearer,
					'F', steelPipe,
					'C', "craftingCircuitTier04"
			});
			
			registerMachine(Machine.Harvester, new Object[] {
					"PHP",
					"CBC",
					"   ",
					'P', "sheetPlastic",
					'H', cropHarvester,
					'B', MineFactoryReloadedCore.machineBaseItem,
					'C', "craftingCircuitTier04"
			});

			registerMachine(Machine.Rancher, new Object[] {
					" U ",
					"PFP",
					"CIC",
					'P', "sheetPlastic",
					'I', itemClearer,
					'B', MineFactoryReloadedCore.machineBaseItem,
					'U', new ItemStack(MineFactoryReloadedCore.upgradeItem, 1, 0),
					'C', "craftingCircuitTier04"
			);
			
			registerMachine(Machine.Fertilizer, new Object[] {
					" D ",
					"PBP",
					"CIC",
					'P', "sheetPlastic",
					'I', itemClearer,
					'B', MineFactoryReloadedCore.machineBaseItem,
					'D', "craftingCircuitTier06",
					'C', "craftingCircuitTier04"
			});
			
			registerMachine(Machine.Vet, new Object[] {
					"P P",
					"DBU",
					"CIC",
					'P', "sheetPlastic",
					'I', itemClearer,
					'B', MineFactoryReloadedCore.machineBaseItem,
					'U', new ItemStack(MineFactoryReloadedCore.upgradeItem, 1, 0),
					'D', "craftingCircuitTier06",
					'C', "craftingCircuitTier04"
			});
			
			registerMachine(Machine.ItemCollector, new Object[] {
					"PVP",
					" F ",
					"P P",
					'P', "sheetPlastic",
					'F', "craftingRawMachineTier00",
					'V', "craftingConveyor"
			});
			
			registerMachine(Machine.BlockBreaker, new Object[] {
					"VBG",
					" C ",
					"   ",
					'V', "craftingConveyor",
					'B', MineFactoryReloadedCore.machineBaseItem,
					'C', "craftingCircuitTier04",
					'G', "craftingGrinder"
			});
			
			registerMachine(Machine.WeatherCollector, new Object[] {
					" D ",
					"PFP",
					"   ",
					'P', "sheetPlastic",
					'D', "craftingDrain",
					'F', "craftingRawMachineTier00"
			});
			
			registerMachine(Machine.SludgeBoiler, new Object[] {
					"PIP",
					"CBC",
					"   ",
					'P', "sheetPlastic",
					'I', centrifuge,
					'B', MineFactoryReloadedCore.machineBaseItem,
					'C', "craftingCircuitTier04"
			});
			
			registerMachine(Machine.Sewer, new Object[] {
					"PDP",
					"SBS",
					"   ",
					'P', "sheetPlastic",
					'D', "craftingDrain",
					'S', Block.brick,
					'B', MineFactoryReloadedCore.machineBaseItem
			]);

			
			registerMachine(Machine.Composter, new Object[] {
					"PIP",
					"SBS",
					"   ",
					'P', "sheetPlastic",
					'I', centrifuge,
					'S', Block.brick,
					'B', MineFactoryReloadedCore.machineBaseItem
			} ));
			
			registerMachine(Machine.Breeder, new Object[] {
					"DUD",
					"PBP",
					"CIC",
					'P', "sheetPlastic",
					'I', itemClearer,
					'B', MineFactoryReloadedCore.machineBaseItem,
					'U', new ItemStack(MineFactoryReloadedCore.upgradeItem, 1, 0),
					'D', "craftingCircuitTier05",
					'C', "craftingCircuitTier04"
			});
			
			registerMachine(Machine.Grinder, new Object[] {
					"SIS",
					"GBG",
					"CMC",
					'I', "itemClearer",
					'M', magicConverter,
					'G', "craftingGrinder",
					'S', "craftingDiamondBlade",
					'B', MineFactoryReloadedCore.machineBaseItem,
					'C', "craftingCircuitTier04"
			});
			
			registerMachine(Machine.AutoEnchanter, new Object[] {
					"PMP",
					"DED",
					"VBV",
					'E', Block.enchantmentTable,
					'P', "sheetPlastic",
					'V', "craftingConveyor",
					'M', magicConverter,
					'B', MineFactoryReloadedCore.machineBaseItem,
					'D', "craftingCircuitTier06"
			});
			
			registerMachine(Machine.Chronotyper, new Object[] {
					"PUP",
					"CTC",
					"DBD",
					'P', "sheetPlastic",
					'T', "craftingTeleporter",
					'B', MineFactoryReloadedCore.machineBaseItem,
					'U', new ItemStack(MineFactoryReloadedCore.upgradeItem, 1, 0),
					'D', "craftingCircuitTier06",
					'C', "craftingCircuitTier05"
			});
			
			registerMachine(Machine.Ejector, new Object[] {							
					"POP",
					" F ",
					"PVP",
					'P', "sheetPlastic",
					'V', "craftingConveyer",
					'F', "craftingRawMachineTier00",
					'O', "craftingWorkController"
			});
			
			registerMachine(Machine.ItemRouter, new Object[] {					
					"PCP",
					"VSV",
					"PCP",
					'P', "sheetPlastic",
					'S', sorter,
					'V', "craftingConveyor",
					'C', "craftingCircuitTier02"
			});
			
			registerMachine(Machine.LiquidRouter, new Object[] {					
					"PFP",
					"FSF",
					"PFP",
					'P', "sheetPlastic",
					'F', steelPipe,
					'S', sorter
			});
			
			registerMachine(Machine.DeepStorageUnit, new Object[] {					
					"C  ",
					"   ",
					"   ",
					'C', qChest,
			});
			
				if(MFRConfig.enableCheapDSU.getBoolean(false))
				{
					registerMachine(Machine.Fertilizer, new Object[] {					
						"DTD",
						"PBP",
						"DMD",
						'P', "sheetPlastic",
						'B', "craftingRawMachineTier04",
						'M', "craftingMonitorTier2",
						'D', "craftingCircuitTier08",
						'T', "craftingTeleporter"
					});
				}
			}
			
                        registerMachine(Machine.LiquiCrafter, new Object[] {						
					"CRC",
					"FTF",
					"CAC",
					'C', "cellEmpty",
					'T', metalWorkbench,
					'F', steelPipe,
					'R', "craftingWorkController",
					'A', "craftingCircuitTier04"
			});
			
                        registerMachine(Machine.LavaFabricator, new Object[] {
					"CLC",
					"BEB",
					"CTC",
					'L', "bucketLava",
					'E', "crafting100kkEUStore",
					'B', "craftingRawMachineTier04",
					'T', "craftingTeleporter",
					'C', "craftingCircuitTier07"
			});
			
                        registerMachine(Machine.OilFacricator, new Object[] {
					"CLC",
					"BEB",
					"CTC",
					'L', "cellOil",
					'E', "crafting100kkEUStore",
					'B', "craftingRawMachineTier04",
					'T', "craftingTeleporter",
					'C', "craftingCircuitTier07"
			});
			
                        registerMachine(Machine.AutoJukebox, new Object[] {
					"PJP",
					" F ",
					"PCP",
					'P', "sheetPlastic",
					'C', "craftingCircuitTier02",
					'J', Block.jukebox,
					'F', "craftingRawMachineTier00"
			});
			
                        registerMachine(Machine.Unifier, new Object[] {
					"V  ",
					"B  ",
					"  ",
					'B', MineFactoryReloadedCore.machineBaseItem,
					'V', "craftingItemValve",
			});
			
                        registerMachine(Machine.AutoSpawner, new Object[] {
					"CLC",
					"BEB",
					"CTC",
					'L', magicConverter,
					'E', "crafting100kkEUStore",
					'B', "craftingRawMachineTier04",
					'T', "craftingCircuitTier08",
					'C', "craftingCircuitTier07"
			});
			
                        registerMachine(Machine.BioReactor, new Object[] {
					"CDE",
					"FBF",
					"   ",
					'D', distil,
					'E', "craftingExtractor",
					'B', MineFactoryReloadedCore.machineBaseItem,
					'F', steelPipe,
					'C', "craftingCompressor"
			});
			
                        registerMachine(Machine.BioFuelGenerator, new Object[] {
					"DDD",
					"GBG",
					"   ",
					'D', dieselGen,
					'G', "craftingGearTier02",
					'B', MineFactoryReloadedCore.machineBaseItem,
			});
			
                        registerMachine(Machine.AutoDisenchanter, new Object[] {
					"PBP",
					"DED",
					"VMV",
					'E', Block.enchantmentTable,
					'P', "sheetPlastic",
					'V', "craftingConveyor",
					'M', magicConverter,
					'B', MineFactoryReloadedCore.machineBaseItem,
					'D', "craftingCircuitTier06"
			} ));
			
                        registerMachine(Machine.Slaughterhouse, new Object[] {
					"SIS",
					"GBG",
					"CMC",
					'I', itemClearer,
					'M', centrifuge,
					'G', "craftingGrinder",
					'S', "craftingDiamondBlade",
					'B', MineFactoryReloadedCore.machineBaseItem,
					'C', steelPipe
			});
			
                        registerMachine(Machine.Meatpacker, new Object[] {
					"EMC",
					"FBF",
					"   ",
					'E', "craftingExtractor",
					'M', microwave,
					'C', "craftingCompressor",
					'B', MineFactoryReloadedCore.machineBaseItem,
					'F', steelPipe,
			});
			
                        registerMachine(Machine.EnchantmentRouter, new Object[] {					
					"T  ",
					"B  ",
					"   ",
					'T', typeSorter,
					'B', MineFactoryReloadedCore.machineBaseItem,
			});
		
                        registerMachine(Machine.LaserDrill, new Object[] {
					"STS",
					"CDC",
					"SLS",
					'L', "lenseDiamond",
					'T', GTteleporter,
					'D', supercondensator,
					'C', "craftingCircuitTier07",
					'S', "craftingSuperconductor"
			});
			
                        registerMachine(Machine.LaserDrillPrecharger, new Object[] {
					"SGG",
					"LLC",
					"SGG",
					'G', "blockGlowstone",
					'C', "craftingCircuitTier07",
					'L', "lenseDiamond",
					'S', superconductor
			});
			
                        registerMachine(Machine.AutoAnvil, new Object[] {
					"PBP",
					"DED",
					"VMV",
					'E', Block.anvil,
					'P', "sheetPlastic",
					'V', "craftingConveyor",
					'M', magicConverter,
					'B', MineFactoryReloadedCore.machineBaseItem,
					'D', "craftingCircuitTier06"
			});
			
			
                        registerMachine(Machine.Blocksmasher, new Object[] {
					"DMD",
					"GBG",
					"   ",
					'G', "craftingGrinder",
					'M', magicConverter,
					'B', MineFactoryReloadedCore.machineBaseItem,
					'D', "craftingCircuitTier06"
			});
			
                        registerMachine(Machine.RedNote, new Object[] {
					" C ",
					" N ",
					"CCC",
					'C', MineFactoryReloadedCore.rednetCableBlock,
					'N', Block.music,
					'C', "craftingCircuitTier02"
			});
			
                        registerMachine(Machine.AutoBrewer, new Object[] {
					"CSC",
					"FBF",
					"CSC",
					'S', Item.brewingStand,
					'F', steelPipe,
					'B', MineFactoryReloadedCore.machineBaseItem,
					'C', "craftingCircuitTier04"
			});
			
                        registerMachine(Machine.FruitPicker, new Object[] {
					"PHP",
					"CBC",
					"   ",
					'P', "sheetPlastic",
					'H', cropHarvester,
					'B', MineFactoryReloadedCore.machineBaseItem,
					'C', "craftingCircuitTier02"
			});
			
                        registerMachine(Machine.BlockPlacer, new Object[] {
					" C ",
					"PBV",
					" C ",
					'P', block.piston,
					'V', "craftingConveyor",
					'B', MineFactoryReloadedCore.machineBaseItem,
					'C', "craftingCircuitTier04"
			});
			
			registerMachine(Machine.MobCounter, new Object[] {
					"RRR",
					" S ",
					"   ",
					'R', MineFactoryReloadedCore.rednetCableBlock,
					'S', redstoneScale
			});
			
			registerMachine(Machine.SteamTurbine, new Object[] {
				" T ",
				"GBG",
				" T ",
				'T', gasTurbine,
				'G', "craftingGearTier02",
				'B', MineFactoryReloadedCore.machineBaseItem
			});
			
			GregTech_API.sRecipeAdder.addCannerRecipe(MineFactoryReloadedCore.syringeEmptyItem, Item.appleRed, MineFactoryReloadedCore.syringeHealthItem, null, 200, 5);
			GregTech_API.sRecipeAdder.addCannerRecipe(MineFactoryReloadedCore.syringeEmptyItem, Item.goldenCarrot, MineFactoryReloadedCore.syringeGrowthItem, null, 400, 10);
			GregTech_API.sRecipeAdder.addCannerRecipe(MineFactoryReloadedCore.syringeEmptyItem, new ItemStack(Item.rottenFlesh, 8, 0), MineFactoryReloadedCore.syringeZombieItem, null, 100, 5);
			GregTech_API.sRecipeAdder.addCannerRecipe(MineFactoryReloadedCore.syringeEmptyItem, new ItemStack(MineFactoryReloadedCore.pinkSlimeballItem, 8, 0), MineFactoryReloadedCore.syringeSlimeItem, null, 400, 10);
			GregTech_API.sRecipeAdder.addCannerRecipe(MineFactoryReloadedCore.syringeEmptyItem, Item.appleGold, MineFactoryReloadedCore.syringeCureItem, null, 400, 10);
			
			GregTech_API.sRecipeAdder.addBenderRecipe(MineFactoryReloadedCore.rawPlasticItem, MineFactoryReloadedCore.plasticSheetItem, 40, 20);
			GregTech_API.sRecipeAdder.addAssemblerRecipe(Item.paper, MineFactoryReloadedCore.rawPlasticItem, MineFactoryReloadedCore.blankRecordItem, 200, 10);
			
			GregTech_API.sRecipeAdder.addCentrifugeRecipe(new ItemStack(MineFactoryReloadedCore.fertilizerItem, 8, 0), 1, methane, ICFertilizer, null, null, 5000);
			GregTech_API.sRecipeAdder.addCentrifugeRecipe(new ItemStack(MineFactoryReloadedCore.meatIngotRawItem, 10, 0), 1, methane, null, null, null, 5000);
			GregTech_API.sRecipeAdder.addCentrifugeRecipe(new ItemStack(MineFactoryReloadedCore.meatIngotCookedItem, 12, 0), 1, methane, null, null, null, 5000);
			
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
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.upgradeItem, 1, 0), new Object[]
					{
				"GGG",
				"CTC",
				"GGG",
				'T', "craftingTeleporter",
				'C', "craftingCircuitTier04",
				'G', "glassCable"
			} ));
					
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.upgradeItem, 1, 5), new Object[]
					{
				"DBC",
				" U ",
				"   ",
				'U', new ItemStack(MineFactoryReloadedCore.upgradeItem, 1, 0),
				'C', "craftingCircuitTier06",
				'D', "craftingCircuitTier07",
				'B', "craftingRawMachineTier04",
			} ));
					
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.upgradeItem, 1, 10), new Object[]
					{
				"DBC",
				" U ",
				"   ",
				'U', new ItemStack(MineFactoryReloadedCore.upgradeItem, 1, 5),
				'C', "craftingCircuitTier06",
				'D', "craftingCircuitTier07",
				'B', "craftingRawMachineTier04",
			} ));
			
			for(int i = 2; i < 11; i + 2)
			{
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.upgradeItem, 1, i), new Object[]
					{
				" G ",
				"CUC",
				" G ",
				'U', new ItemStack(MineFactoryReloadedCore.upgradeItem, 1, i - 2),
				'C', "craftingCircuitTier06",
				'G', "glassCable"
					} ));
			}
			
			for(int i = 1; i < 11; i++)
			{
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.upgradeItem, 1, i), new Object[]
					{
				"GCG",
				"GUG",
				"GTG",
				'T', "craftingTeleporter",
				'U', new ItemStack(MineFactoryReloadedCore.upgradeItem, 1, i - 1),
				'C', "craftingCircuitTier04",
				'G', "glassCable"
			} ));
			}
			
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.laserFocusItem, 1, 0), new Object[]
					{
				"TWT",
				"CWC",
				"TWT",
				'T', "screwTungstenSteel",
				'C', "stickChrome",
				'W', "craftingLenseWhite"
			} ));
					
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.laserFocusItem, 1, 1), new Object[]
					{
				"TRT",
				"CRC",
				"TGT",
				'T', "screwTungstenSteel",
				'C', "stickChrome",
				'R', "craftingLenseRed"'
				'G', "craftingLenseGreen"
			} ));
					
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.laserFocusItem, 1, 2), new Object[]
					{
				"TGT",
				"CWC",
				"TRT",
				'T', "screwTungstenSteel",
				'C', "stickChrome",
				'W', "craftingLenseWhite",
				'R', "craftingLenseRed"'
				'G', "craftingLenseGreen"
			} ));
					
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.laserFocusItem, 1, 3), new Object[]
					{
				"TBT",
				"CWC",
				"TBT",
				'T', "screwTungstenSteel",
				'C', "stickChrome",
				'W', "craftingLenseWhite",
				'B', "craftingLenseBlue"
			} ));
					
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.laserFocusItem, 1, 4), new Object[]
					{
				"TRT",
				"CGC",
				"TGT",
				'T', "screwTungstenSteel",
				'C', "stickChrome",
				'R', "craftingLenseRed"'
				'G', "craftingLenseGreen"
			} ));
					
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.laserFocusItem, 1, 5), new Object[]
					{
				"TGT",
				"CWC",
				"TGT",
				'T', "screwTungstenSteel",
				'C', "stickChrome",
				'W', "craftingLenseWhite",
				'G', "craftingLenseGreen"
			} ));
					
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.laserFocusItem, 1, 6), new Object[]
					{
				"TWT",
				"CRC",
				"TWT",
				'T', "screwTungstenSteel",
				'C', "stickChrome",
				'W', "craftingLenseWhite",
				'R', "craftingLenseRed"
			} ));
					
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.laserFocusItem, 1, 7), new Object[]
					{
				"TDT",
				"CWC",
				"TDT",
				'T', "screwTungstenSteel",
				'C', "stickChrome",
				'W', "craftingLenseWhite",
				'D', "craftingIndustrialDiamond"
			} ));
					
					
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.laserFocusItem, 1, 8), new Object[]
					{
				"TWT",
				"CDC",
				"TWT",
				'T', "screwTungstenSteel",
				'C', "stickChrome",
				'W', "craftingLenseWhite",
				'D', "craftingIndustrialDiamond"
			} ));
					
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.laserFocusItem, 1, 9), new Object[]
					{
				"TBT",
				"CGC",
				"TBT",
				'T', "screwTungstenSteel",
				'C', "stickChrome",
				'G', "craftingLenseGreen"'
				'B', "craftingLenseBlue"
			} ));
					
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.laserFocusItem, 1, 10), new Object[]
					{
				"TRT",
				"CBC",
				"TBT",
				'T', "screwTungstenSteel",
				'C', "stickChrome",
				'R', "craftingLenseRed"'
				'B', "craftingLenseBlue"
			} ));
					
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.laserFocusItem, 1, 11), new Object[]
					{
				"TBT",
				"CBC",
				"TBT",
				'T', "screwTungstenSteel",
				'C', "stickChrome",
				'B', "craftingLenseBlue"
			} ));
					
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.laserFocusItem, 1, 12), new Object[]
					{
				"TRT",
				"CGC",
				"TBT",
				'T', "screwTungstenSteel",
				'C', "stickChrome",
				'R', "craftingLenseRed"'
				'G', "craftingLenseGreen"'
				'B', "craftingLenseBlue"
			} ));
					
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.laserFocusItem, 1, 13), new Object[]
					{
				"TGT",
				"CGC",
				"TGT",
				'T', "screwTungstenSteel",
				'C', "stickChrome",
				'G', "craftingLenseGreen"
			} ));
					
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.laserFocusItem, 1, 14), new Object[]
					{
				"TRT",
				"CRC",
				"TRT",
				'T', "screwTungstenSteel",
				'C', "stickChrome",
				'R', "craftingLenseRed"
			} ));
					
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.laserFocusItem, 1, 15), new Object[]
					{
				"TDT",
				"CDC",
				"TDT",
				'T', "screwTungstenSteel",
				'C', "stickChrome",
				'D', "craftingIndustrialDiamond"
			} ));
					
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
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.conveyorBlock, 8, 16), new Object[]
					{
				"UUU",
				"PVP",
				'U', "itemRubber",
				'V', "craftingConveyor",
				'P', "plateSteel"
			} ));
			
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.conveyorBlock, 6, 16), new Object[]
					{
				"UUU",
				"PVP",
				'U', "itemRubber",
				'V', "craftingConveyor",
				'P', "plateAluminium"
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
			
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.syringeEmptyItem, 1), new Object[]
					{
				"PRP",
				"PCP",
				" I ",
				'P', "sheetPlastic",
				'R', "itemRubber",
				'I', "stickIron",
				'C', "emptyCell"
			} ));
			
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
			
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.safariNetItem, 1), new Object[]
					{
				" C ",
				"GPG",
				" E ",
				'E', Item.enderPearl,
				'G', Item.ghastTear,
				'C', "craftingCircuitTier05"
			} ));
			
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.safariNetSingleItem, 1), new Object[]
					{
				"ILI",
				"SPS",
				"IBI",
				'S', Item.silk,
				'L', Item.leather,
				'B', Item.slimeBall,
				'P', "plateIron"'
				'I', Block.fenceIron
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
				"GPP",
				"GPP",
				"RR ",
				'P', "plateIron",
				'L', "gearIron",
				'G', "stickIron"
					} ));
			
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.factoryHammerItem, 1), new Object[]
					{
				"PPP",
				" S ",
				" S ",
				'P', "sheetPlastic",
				'S', Item.stick
					} ));
			
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.spyglassItem), new Object[]
					{
				" L ",
				" P ",
				" L ",
				'G', "plateBrass",
				'L', Block.glassPane
					} ));
			
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.portaSpawnerItem), new Object[]
					{
				"SNS",
				"PDP",
				"STS",
				'P', "plateStainlessSteel",
				'S', "screwStainlessSteel",
				'D', "craftingCircuitTier08",
				'N', Item.netherStar,
				'T', "craftingTeleporter"
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
					
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.needlegunItem, 1), new Object[]
			{
				"FG ",
				"PPP",
				"SS "
				'P', "plateSteel",
				'S', "stickSteel",
				'G', Block.glass,
				'F', Item.flintAndSteel
			} ));
			
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.needlegunAmmoEmptyItem, 1), new Object[]
			{
				"",
				"S S",
				"PSP"
				'P', "plateSteel",
				'S', "sheetPlastic"
			} ));
			
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.needlegunAmmoStandardItem, 1), new Object[]
			{
				"CGC",
				"CGC",
				"PMP"
				'M', MineFactoryReloadedCore.needlegunAmmoEmptyItem,
				'G', Item.gunpowder,
				'C', "cellLava",
				'P', "plateTin"
			} ));
			
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.needlegunAmmoSludgeItem, 1), new Object[]
			{
				"RBR",
				"RGR",
				" M "
				'M', MineFactoryReloadedCore.needlegunAmmoEmptyItem,
				'G', Item.gunpowder,
				'R', "roundTin",
				'B', MineFactoryReloadedCore.sludgeBucketItem,
			} ));
			
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.needlegunAmmoSewageItem, 1), new Object[]
			{
				"RBR",
				"RGR",
				" M "
				'M', MineFactoryReloadedCore.needlegunAmmoEmptyItem,
				'G', Item.gunpowder,
				'R', "roundTin",
				'B', MineFactoryReloadedCore.sewageBucketItem,
			} ));
			
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.needlegunAmmoFireItem, 1), new Object[]
			{
				"RFR",
				"RGR",
				"RMR"
				'M', MineFactoryReloadedCore.needlegunAmmoEmptyItem,
				'G', Item.gunpowder,
				'R', "dustImpureNetherrack",
				'F', Item.flintAndSteel
			} ));
			
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.needlegunAmmoFireItem, 1), new Object[]
			{
				"RFR",
				"RGR",
				"RMR"
				'M', MineFactoryReloadedCore.needlegunAmmoEmptyItem,
				'G', Item.gunpowder,
				'R', "dustNetherrack",
				'F', Item.flintAndSteel
			} ));
			
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.needlegunAmmoAnvilItem, 1), new Object[]
			{
				"SAS",
				"SMS",
				"STS"
				'M', MineFactoryReloadedCore.needlegunAmmoEmptyItem,
				'T', Block.tnt,
				'S', Item.string,
				'A', Block.anvil
			} ));
			
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.rocketLauncherItem, 1), new Object[]
			{
				"MCL",
				"PPP",
				"SSP"
				'M', "craftingMonitorTier2",
				'C', "craftingCircuitTier06",
				'L', "craftingLenseRed",
				'P', "plateStainlessSteel",
				'S', "stickStainlessSteel"
			} ));
			
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.rocketLauncherItem, 3, 1), new Object[]
			{
				"PPT",
				"ACI",
				"PPT"
				'T', Block.tnt,
				'I', "dustSmallIron",
				'A', "dustSmallAluminium"
				'C', "cellChlorine",
				'P', "plateStainlessSteel",
			} ));
			
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.rocketLauncherItem, 2, 1), new Object[]
			{
				"PPT",
				"DOD",
				"PPT"
				'T', Block.tnt,
				'D', "cellNitroFuel",
				'O', "cellOxygen"
				'P', "plateStainlessSteel",
			} ));
			
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.rocketLauncherItem, 1, 1), new Object[]
			{
				"PM ",
				"MOM",
				"PMT"
				'T', Block.tnt,
				'M', "cellMethane",
				'O', "cellOxygen",
				'P', "plateStainlessSteel",
			} ));
			
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.rocketLauncherItem, 1, 0), new Object[]
			{
				"TC",
				"",
				""
				'T', new ItemStack(MineFactoryReloadedCore.rocketLauncherItem, 1, 1),
				'C', "craftingCircuitTier04"
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
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.rednetCableBlock, 3), new Object[]
					{
				"PPP",
				"RRR",
				" C ",
				'R', "plateRedstone",
				'P', "sheetPlastic",
				'C', "cellSilicon"
					} ));
			
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.factoryDecorativeBrickBlock, 1, 11), new Object[]
					{
				"RSR",
				"RBR",
				"RSR",
				'R', "plateRedstone",
				'S', "cellSilicon",
				'B', MineFactoryReloadedCore.machineBaseItem
					} ));
			
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.rednetLogicBlock), new Object[]
					{
				"CDC",
				"PBP",
				"CMC",
				'B', new ItemStack(MineFactoryReloadedCore.factoryDecorativeBrickBlock, 1, 11),
				'P', "sheetPlastic",
				'D', "craftingCircuitTier06",
				'M', "craftingCircuitTier05",
				'C', "craftingCircuitTier04"
					} ));
			
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.logicCardItem, 1, 0), new Object[]
					{
				" M ",
				"CDC",
				"RRR",
				'R', MineFactoryReloadedCore.rednetCableBlock,
				'C', "craftingCircuitTier02",
				'M', "craftingCircuitTier05",
				'D', "craftingCircuitTier06"
					} ));
			
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.logicCardItem, 1, 1), new Object[]
					{
				"RM ",
				"RD ",
				"RCL",
				'L', new ItemStack(MineFactoryReloadedCore.logicCardItem, 1, 0),
				'R', MineFactoryReloadedCore.rednetCableBlock,
				'C', "craftingCircuitTier02",
				'M', "craftingCircuitTier05",
				'D', "craftingCircuitTier06"
					} ));
			
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.logicCardItem, 1, 2), new Object[]
					{
				"RM ",
				"RD ",
				"RCL",
				'L', new ItemStack(MineFactoryReloadedCore.logicCardItem, 1, 1),
				'R', MineFactoryReloadedCore.rednetCableBlock,
				'C', "craftingCircuitTier02",
				'M', "craftingCircuitTier05",
				'D', "craftingCircuitTier06"
					} ));
			
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.rednetMeterItem, 1, 0), new Object[]
					{
				"EP",
				"",
				"",
				'P', "sheetPlastic",
				'E', "craftingEnergyMeter"
					} ));
			
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.rednetMemoryCardItem, 1, 0), new Object[]
					{
				"MP",
				"",
				"",
				'P', "sheetPlastic",
				'M', "craftingCircuitTier05"
					} ));
			
			GameRegistry.addShapelessRecipe(new ItemStack(MineFactoryReloadedCore.rednetMemoryCardItem, 1, 0), new ItemStack(MineFactoryReloadedCore.rednetMemoryCardItem, 1, 0));
			
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.rednetPanelBlock, 1, 0), new Object[]
					{
				"RRR",
				" E ",
				"   ",
				'R', MineFactoryReloadedCore.rednetCableBlock,
				'E', "craftingEnergyMeter"
					} ));
		}
		catch (Exception x)
		{
			x.printStackTrace();
		}
	}
}
