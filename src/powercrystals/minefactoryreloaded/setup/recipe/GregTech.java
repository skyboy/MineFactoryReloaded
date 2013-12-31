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
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.conveyorBlock, 16, 16), new Object[]
					{
				"UUU",
				"PVP",
				'U', "itemRubber",
				'V', "craftingConveyor",
				'P', "plateSteel"
					} ));
					
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.conveyorBlock, 16, 16), new Object[]
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
			ItemStack cell = Items.getItem("cell");
			
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
			
			GameRegistry.addShapelessRecipe(new ItemStack(MineFactoryReloadedCore.syringeHealthItem), new Object[] { MineFactoryReloadedCore.syringeEmptyItem, Item.appleRed });
			GameRegistry.addShapelessRecipe(new ItemStack(MineFactoryReloadedCore.syringeGrowthItem), new Object[] { MineFactoryReloadedCore.syringeEmptyItem, Item.goldenCarrot });
			
			GameRegistry.addRecipe(new ItemStack(MineFactoryReloadedCore.syringeZombieItem, 1), new Object[]
					{
				"FFF",
				"FSF",
				"FFF",
				'F', Item.rottenFlesh,
				'S', MineFactoryReloadedCore.syringeEmptyItem
					} );
			
			GameRegistry.addRecipe(new ItemStack(MineFactoryReloadedCore.syringeSlimeItem, 1), new Object[]
					{
				"   ",
				" S ",
				"BLB",
				'B', Item.slimeBall,
				'L', new ItemStack(Item.dyePowder, 1, 4),
				'S', MineFactoryReloadedCore.syringeEmptyItem
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
				'#', "dustPlastic"
					} ));
			
			GameRegistry.addRecipe(new ItemStack(MineFactoryReloadedCore.fertilizerItem, 4), new Object[]
					{
				"WBW",
				"STS",
				"WBW",
				'W', Item.wheat,
				'B', new ItemStack(Item.dyePowder, 1, 15),
				'S', Item.silk,
				'T', Item.stick
					} );
			
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
				'I', "stickIron"
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
			
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.blankRecordItem, 1), new Object[]
					{
				"RRR",
				"RPR",
				"RRR",
				'R', "dustPlastic",
				'P', Item.paper
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
