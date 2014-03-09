package powercrystals.minefactoryreloaded.setup.recipe;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.setup.MFRConfig;
import powercrystals.minefactoryreloaded.setup.Machine;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;

public class ThermalExpansion extends Vanilla
{
	ItemStack conduitLiquid;
	ItemStack tankBasic;
	ItemStack lamp;
	ItemStack machineFrame;
	ItemStack pneumaticServo;
	ItemStack powerCoilElectrum;
	ItemStack powerCoilGold;
	ItemStack powerCoilSilver;
	ItemStack tesseractFrameFull;
	ItemStack strongBox;
	ItemStack strongBoxBasic;
	ItemStack multimeter;
	ItemStack invarAxe;
	ItemStack invarSword;
	ItemStack invarPickaxe;
	ItemStack dynamoSteam;
	ItemStack tesseract;
	ItemStack tesseractFrameEmpty;
	ItemStack cellResonant;
	
	@Override
	protected void gatherItems()
	{
		conduitLiquid = GameRegistry.findItemStack("ThermalExpansion", "conduitFluidOpaque", 1);
		tankBasic = GameRegistry.findItemStack("ThermalExpansion", "tankBasic", 1);
		lamp = GameRegistry.findItemStack("ThermalExpansion", "lamp", 1);
		machineFrame = GameRegistry.findItemStack("ThermalExpansion", "machineFrame", 1);
		pneumaticServo = GameRegistry.findItemStack("ThermalExpansion", "pneumaticServo", 1);
		powerCoilElectrum = GameRegistry.findItemStack("ThermalExpansion", "powerCoilElectrum", 1);
		powerCoilGold = GameRegistry.findItemStack("ThermalExpansion", "powerCoilGold", 1);
		powerCoilSilver = GameRegistry.findItemStack("ThermalExpansion", "powerCoilSilver", 1);
		tesseractFrameFull = GameRegistry.findItemStack("ThermalExpansion", "tesseractFrameFull", 1);
		strongBox = GameRegistry.findItemStack("ThermalExpansion", "strongboxReinforced", 1);
		strongBoxBasic = GameRegistry.findItemStack("ThermalExpansion", "strongboxBasic", 1);
		multimeter = GameRegistry.findItemStack("ThermalExpansion", "multimeter", 1);
		invarAxe = GameRegistry.findItemStack("ThermalExpansion", "toolInvarAxe", 1);
		invarSword = GameRegistry.findItemStack("ThermalExpansion", "toolInvarSword", 1);
		invarPickaxe = GameRegistry.findItemStack("ThermalExpansion", "toolInvarPickaxe", 1);
		dynamoSteam = GameRegistry.findItemStack("ThermalExpansion", "dynamoSteam", 1);
		tesseract = new ItemStack(GameRegistry.findBlock("ThermalExpansion", "Tesseract"));
		tesseractFrameEmpty = GameRegistry.findItemStack("ThermalExpansion", "tesseractFrameEmpty", 1);
		cellResonant = GameRegistry.findItemStack("ThermalExpansion", "cellResonant", 1);
	}
	
	@Override
	protected void registerMachines()
	{
		if(!Loader.isModLoaded("ThermalExpansion"))
		{
			return;
		}
		try
		{
			registerMachine(Machine.Planter, new Object[] {
					"PTP",
					"SFS",
					"OCO",
					'P', "sheetPlastic",
					'T', Item.flowerPot,
					'S', Block.pistonBase,
					'F', machineFrame,
					'O', "ingotCopper",
					'C', powerCoilGold,
						} );
			
			registerMachine(Machine.Fisher, new Object[] {
					"PTP",
					"SFS",
					"OCO",
					'P', "sheetPlastic",
					'T', Item.fishingRod,
					'S', Item.bucketEmpty,
					'F', machineFrame,
					'O', "ingotIron",
					'C', powerCoilGold
						} );
			
			registerMachine(Machine.Harvester, new Object[] {
					"PTP",
					"SFS",
					"OCO",
					'P', "sheetPlastic",
					'T', invarAxe,
					'S', Item.shears,
					'F', machineFrame,
					'O', "ingotGold",
					'C', powerCoilGold
						} );
			
			registerMachine(Machine.Rancher, new Object[] {
					"PTP",
					"SFS",
					"OCO",
					'P', "sheetPlastic",
					'T', conduitLiquid,
					'S', Item.shears,
					'F', machineFrame,
					'O', "ingotTin",
					'C', powerCoilGold
						} );
			
			registerMachine(Machine.Fertilizer, new Object[] {
					"PTP",
					"SFS",
					"OCO",
					'P', "sheetPlastic",
					'T', Item.glassBottle,
					'S', Item.leather,
					'F', machineFrame,
					'O', "ingotSilver",
					'C', powerCoilGold
						} );
			
			registerMachine(Machine.Vet, new Object[] {
					"PTP",
					"TFT",
					"OCO",
					'P', "sheetPlastic",
					'T', MineFactoryReloadedCore.syringeEmptyItem,
					'F', machineFrame,
					'O', "ingotCopper",
					'C', powerCoilGold
						} );
			
			registerMachine(Machine.ItemCollector, 8, new Object[] {
					"P P",
					" F ",
					"PCP",
					'P', "sheetPlastic",
					'F', machineFrame,
					'C', Block.chest
						} );
			
			registerMachine(Machine.BlockBreaker, new Object[] {
					"PTP",
					"SFS",
					"OCO",
					'P', "sheetPlastic",
					'T', "gearInvar",
					'S', invarPickaxe,
					'F', machineFrame,
					'O', "ingotIron",
					'C', powerCoilGold
						} );
			
			registerMachine(Machine.WeatherCollector, new Object[] {
					"PTP",
					" F ",
					"OCO",
					'P', "sheetPlastic",
					'T', Item.bucketEmpty,
					'F', machineFrame,
					'O', "ingotCopper",
					'C', powerCoilGold
						} );
			
			registerMachine(Machine.SludgeBoiler, new Object[] {
					"PTP",
					"SFS",
					"OCO",
					'P', "sheetPlastic",
					'T', Item.bucketEmpty,
					'S', Block.furnaceIdle,
					'F', machineFrame,
					'O', "ingotIron",
					'C', powerCoilGold
						} );
			
			registerMachine(Machine.Sewer, 4, new Object[] {
					"PTP",
					"SFS",
					"SQS",
					'P', "sheetPlastic",
					'T', Item.bucketEmpty,
					'S', Item.brick,
					'F', machineFrame,
					'Q', pneumaticServo,
						} );
			
			registerMachine(Machine.Composter, new Object[] {
					"PTP",
					"SFS",
					"OCO",
					'P', "sheetPlastic",
					'T', Block.furnaceIdle,
					'S', Block.pistonBase,
					'F', machineFrame,
					'O', Item.brick,
					'C', powerCoilGold
						} );
			
			registerMachine(Machine.Breeder, new Object[] {
					"PTP",
					"SFS",
					"OCO",
					'P', "sheetPlastic",
					'T', Item.appleGold,
					'S', Item.goldenCarrot,
					'F', machineFrame,
					'O', "dyePurple",
					'C', powerCoilGold
						} );
			
			registerMachine(Machine.Grinder, new Object[] {
					"PTP",
					"SFS",
					"OCO",
					'P', "sheetPlastic",
					'T', invarSword,
					'S', "gearTin",
					'F', machineFrame,
					'O', Item.book,
					'C', powerCoilGold
						} );
			
			registerMachine(Machine.AutoEnchanter, new Object[] {
					"PTP",
					"SFS",
					"OCO",
					'P', "sheetPlastic",
					'T', Block.obsidian,
					'S', Item.book,
					'F', machineFrame,
					'O', Item.diamond,
					'C', powerCoilGold
						} );
			
			registerMachine(Machine.Chronotyper, new Object[] {
					"PTP",
					"TFT",
					"OCO",
					'P', "sheetPlastic",
					'T', Item.emerald,
					'F', machineFrame,
					'O', "dyePurple",
					'C', powerCoilGold
						} );
			
			registerMachine(Machine.Ejector, 8, new Object[] {							
					"PPP",
					" T ",
					"OFO",
					'P', "sheetPlastic",
					'T', pneumaticServo,
					'F', machineFrame,
					'O', Item.redstone
						} );
			
			registerMachine(Machine.ItemRouter, 8, new Object[] {					
					"PTP",
					"SFS",
					"PSP",
					'P', "sheetPlastic",
					'T', Block.chest,
					'S', multimeter,
					'F', machineFrame
						} );
			
			registerMachine(Machine.LiquidRouter, 8, new Object[] {					
					"PTP",
					"SFS",
					"PSP",
					'P', "sheetPlastic",
					'T', conduitLiquid,
					'S', multimeter,
					'F', machineFrame
						} );
			
			int dsuCount = MFRConfig.craftSingleDSU.getBoolean(false) ? 1 : 4;
			registerMachine(Machine.DeepStorageUnit, dsuCount, new Object[] {					
				"PCP",
				"CFC",
				"PCP",
				'P', "sheetPlastic",
				'C', strongBox,
				'F', tesseractFrameFull
					} );
			
			if(MFRConfig.enableCheapDSU.getBoolean(false))
			{
				registerMachine(Machine.DeepStorageUnit, new Object[] {					
					"PCP",
					"CFC",
					"PCP",
					'P', "sheetPlastic",
					'C', strongBoxBasic,
					'F', machineFrame
						} );
			}
			
			registerMachine(Machine.LiquiCrafter, new Object[] {						
					"PTP",
					"SFS",
					"OCO",
					'P', "sheetPlastic",
					'T', Block.workbench,
					'S', tankBasic,
					'F', machineFrame,
					'O', Item.book,
					'C', pneumaticServo
						} );
			
			registerMachine(Machine.LavaFabricator, new Object[] {
					"PTP",
					"SFS",
					"OCO",
					'P', "sheetPlastic",
					'T', Block.obsidian,
					'S', Item.magmaCream,
					'F', machineFrame,
					'O', Item.blazeRod,
					'C', powerCoilGold
						} );
			
			registerMachine(Machine.OilFabricator, new Object[] {
					"PTP",
					"OFO",
					"OCO",
					'P', "sheetPlastic",
					'T', Block.tnt,
					'F', machineFrame,
					'O', Block.obsidian,
					'C', powerCoilGold
						} );
			
			registerMachine(Machine.AutoJukebox, new Object[] {
					"PJP",
					" F ",
					" P ",
					'P', "sheetPlastic",
					'J', Block.jukebox,
					'F', machineFrame
						} );
			
			registerMachine(Machine.Unifier, new Object[] {
					"PTP",
					"SFS",
					"OCO",
					'P', "sheetPlastic",
					'T', multimeter,
					'S', "ingotSilver",
					'F', machineFrame,
					'O', Item.comparator,
					'C', Item.book
						} );
			
			registerMachine(Machine.AutoSpawner, new Object[] {
					"PTP",
					"SFS",
					"OCO",
					'P', "sheetPlastic",
					'T', Item.netherStalkSeeds,
					'S', Item.magmaCream,
					'F', machineFrame,
					'O', Item.emerald,
					'C', powerCoilGold
						} );
			
			registerMachine(Machine.BioReactor, new Object[] {
					"PTP",
					"SFS",
					"OCO",
					'P', "sheetPlastic",
					'T', Item.fermentedSpiderEye,
					'S', "slimeball",
					'F', machineFrame,
					'O', Item.brick,
					'C', Item.sugar
						} );
			
			registerMachine(Machine.BioFuelGenerator, new Object[] {
					"PTP",
					"SFS",
					"OCO",
					'P', "sheetPlastic",
					'T', Block.furnaceIdle,
					'S', Block.pistonBase,
					'F', machineFrame,
					'O', Item.blazeRod,
					'C', powerCoilSilver
						} );
			
			registerMachine(Machine.AutoDisenchanter, new Object[] {
					"PTP",
					"SFS",
					"OCO",
					'P', "sheetPlastic",
					'T', Block.netherBrick,
					'S', Item.book,
					'F', machineFrame,
					'O', Item.diamond,
					'C', powerCoilGold
						} );
			
			registerMachine(Machine.Slaughterhouse, new Object[] {
					"GIG",
					"SFS",
					"XCX",
					'G', "sheetPlastic",
					'S', invarSword,
					'X', invarAxe,
					'I', "gearInvar",
					'F', machineFrame,
					'C', powerCoilGold
						} );
			
			registerMachine(Machine.MeatPacker, new Object[] {
					"GSG",
					"BFB",
					"BCB",
					'G', "sheetPlastic",
					'B', Block.brick,
					'S', Item.flintAndSteel,
					'F', machineFrame,
					'C', powerCoilGold
						} );
			
			registerMachine(Machine.EnchantmentRouter, new Object[] {					
					"PBP",
					"SFS",
					"PSP",
					'P', "sheetPlastic",
					'B', Item.book,
					'S', Item.redstoneRepeater,
					'F', machineFrame
						} );
			
			registerMachine(Machine.LaserDrill, new Object[] {
					"GFG",
					"CFC",
					"DHD",
					'G', "sheetPlastic",
					'D', Item.diamond,
					'H', "glassHardened",
					'F', lamp,
					'C', powerCoilGold
						} );
			
			registerMachine(Machine.LaserDrillPrecharger, new Object[] {
					"GSG",
					"HFH",
					"CDC",
					'G', "sheetPlastic",
					'D', Item.diamond,
					'S', MineFactoryReloadedCore.pinkSlimeballItem,
					'H', "glassHardened",
					'F', lamp,
					'C', powerCoilElectrum
						} );
			
			registerMachine(Machine.AutoAnvil, new Object[] {
					"GAG",
					"AFA",
					" C ",
					'G', "sheetPlastic",
					'A', Block.anvil,
					'F', machineFrame,
					'C', powerCoilGold
						} );
			
			registerMachine(Machine.BlockSmasher, new Object[] {
					"GPG",
					"HFH",
					"BCB",
					'G', "sheetPlastic",
					'P', Block.pistonBase,
					'H', MineFactoryReloadedCore.factoryHammerItem,
					'B', Item.book,
					'F', machineFrame,
					'C', powerCoilGold
						} );
			
			registerMachine(Machine.RedNote, new Object[] {
					"GNG",
					"CFC",
					'G', "sheetPlastic",
					'C', MineFactoryReloadedCore.rednetCableBlock,
					'N', Block.music,
					'F', machineFrame
						} );
			
			registerMachine(Machine.AutoBrewer, new Object[] {
					"GBG",
					"CFC",
					"RCR",
					'G', "sheetPlastic",
					'C', conduitLiquid,
					'B', Item.brewingStand,
					'R', Item.redstoneRepeater,
					'F', machineFrame,
					'C', powerCoilGold
						} );
			
			registerMachine(Machine.FruitPicker, new Object[] {
					"GXG",
					"SFS",
					"SCS",
					'G', "sheetPlastic",
					'X', invarAxe,
					'S', Item.shears,
					'F', machineFrame,
					'C', powerCoilGold
						} );
			
			registerMachine(Machine.BlockPlacer, new Object[]
						{
					"GDG",
					"DMD",
					"GSG",
					'G', "sheetPlastic",
					'D', Block.dispenser,
					'S', powerCoilGold,
					'M', machineFrame,
						} );
			
			registerMachine(Machine.MobCounter, new Object[]
						{
					"GGG",
					"RCR",
					"SMS",
					'G', "sheetPlastic",
					'R', Item.redstoneRepeater,
					'C', Item.comparator,
					'S', multimeter,
					'M', machineFrame,
						} );
			
			registerMachine(Machine.SteamTurbine, new Object[] {
					"PTP",
					"SFS",
					"OCO",
					'P', "sheetPlastic",
					'T', dynamoSteam,
					'S', Block.pistonBase,
					'F', machineFrame,
					'O', "ingotSilver",
					'C', powerCoilSilver
						} );
			
			registerMachine(Machine.ChunkLoader, new Object[] {
					"PEP",
					"TFT",
					"OCO",
					'P', "sheetPlastic",
					'T', tesseract,
					'E', cellResonant,
					'F', tesseractFrameEmpty,
					'O', "ingotElectrum",
					'C', powerCoilElectrum
						} );
		}
		catch (Exception x)
		{
			x.printStackTrace();
		}
	}

	@Override
	protected void registerMiscItems()
	{
		if(!Loader.isModLoaded("ThermalExpansion"))
		{
			return;
		}
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.fertilizerItem, 16), new Object[]
				{
			"WBW",
			"STS",
			"WBW",
			'W', Item.wheat,
			'B', new ItemStack(Item.dyePowder, 1, 15),
			'S', Item.silk,
			'T', "stickWood",
				} ));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.spyglassItem), new Object[]
				{
			"GLG",
			"PLP",
			" S ",
			'G', "ingotGold",
			'L', "glass",
			'P', "sheetPlastic",
			'S', "stickWood",
				} ));
		
		if (MFRConfig.enablePortaSpawner.getBoolean(true))
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.portaSpawnerItem), new Object[]
				{
			"GLG",
			"DND",
			"GLG",
			'G', "ingotInvar",
			'L', "glass",
			'D', "ingotEnderium",
			'N', Item.netherStar
				} ));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.detCordBlock, 6), new Object[]
				{
			"PPP",
			"PTP",
			"PPP",
			'P', "itemRubber",
			'T', Block.tnt,
				} ));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.fishingRodItem, 1), new Object[]
				{
			"DD ",
			"DFD",
			"TDD",
			'D', "wireExplosive",
			'F', Item.fishingRod,
			'T', Block.torchRedstoneActive
				} ));
	}
	
	@Override
	protected void registerRedNet()
	{
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.rednetCableBlock, 8), new Object[]
				{
			"PPP",
			"RRR",
			"PPP",
			'R', Item.redstone,
			'P', "sheetPlastic",
				} ));
		
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(MineFactoryReloadedCore.rednetCableBlock, 1, 2), new Object[]
				{
			"nuggetElectrum",
			"nuggetElectrum",
			"nuggetElectrum",
			Item.redstone,
			Item.redstone,
			new ItemStack(MineFactoryReloadedCore.rednetCableBlock),
				} ));
		
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(MineFactoryReloadedCore.rednetCableBlock, 1, 2), new Object[]
				{
			"ingotElectrum",
			"ingotElectrum",
			Block.blockRedstone,
			new ItemStack(MineFactoryReloadedCore.rednetCableBlock),
			new ItemStack(MineFactoryReloadedCore.rednetCableBlock),
			new ItemStack(MineFactoryReloadedCore.rednetCableBlock),
			new ItemStack(MineFactoryReloadedCore.rednetCableBlock),
			new ItemStack(MineFactoryReloadedCore.rednetCableBlock),
			new ItemStack(MineFactoryReloadedCore.rednetCableBlock),
				} ));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.factoryDecorativeBrickBlock, 1, 11), new Object[]
				{
			"PRP",
			"RGR",
			"PIP",
			'R', Item.redstone,
			'P', "sheetPlastic",
			'G', "glass",
			'I', Item.ingotIron,
				} ));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.rednetLogicBlock), new Object[]
				{
			"RDR",
			"LGL",
			"PHP",
			'H', new ItemStack(MineFactoryReloadedCore.factoryDecorativeBrickBlock, 1, 11),
			'P', "sheetPlastic",
			'G', "ingotGold",
			'L', new ItemStack(Item.dyePowder, 1, 4),
			'D', Item.diamond,
			'R', Item.redstone,
				} ));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.logicCardItem, 1, 0), new Object[]
				{
			"RPR",
			"PGP",
			"RPR",
			'P', "sheetPlastic",
			'G', "ingotGold",
			'R', Item.redstone,
				} ));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.logicCardItem, 1, 1), new Object[]
				{
			"GPG",
			"PCP",
			"RGR",
			'C', new ItemStack(MineFactoryReloadedCore.logicCardItem, 1, 0),
			'P', "sheetPlastic",
			'G', "ingotGold",
			'R', Item.redstone,
				} ));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.logicCardItem, 1, 2), new Object[]
				{
			"DPD",
			"RCR",
			"GDG",
			'C', new ItemStack(MineFactoryReloadedCore.logicCardItem, 1, 1),
			'P', "sheetPlastic",
			'G', "ingotGold",
			'D', Item.diamond,
			'R', Item.redstone,
				} ));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.rednetMeterItem, 1, 0), new Object[]
				{
			" G",
			"PR",
			"PP",
			'P', "sheetPlastic",
			'G', "nuggetGold",
			'R', Item.redstone,
				} ));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.rednetMemoryCardItem, 1, 0), new Object[]
				{
			"GGG",
			"PRP",
			"PPP",
			'P', "sheetPlastic",
			'G', "nuggetGold",
			'R', Item.redstone,
				} ));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.rednetPanelBlock, 1, 0), new Object[]
				{
			"PCP",
			"PBP",
			"KPK",
			'P', "sheetPlastic",
			'C', MineFactoryReloadedCore.rednetCableBlock,
			'B', Block.bookShelf,
			'K', new ItemStack(Item.dyePowder, 1, 0)
				} ));
		
		GameRegistry.addShapelessRecipe(new ItemStack(MineFactoryReloadedCore.rednetMemoryCardItem, 1, 0), new ItemStack(MineFactoryReloadedCore.rednetMemoryCardItem, 1, 0));
	}
}
