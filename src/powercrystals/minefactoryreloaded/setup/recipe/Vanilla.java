package powercrystals.minefactoryreloaded.setup.recipe;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.block.ItemBlockRedNetLogic;
import powercrystals.minefactoryreloaded.setup.MFRConfig;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.setup.recipe.handler.ShapelessMachineTinker;
import cpw.mods.fml.common.registry.GameRegistry;

public class Vanilla
{
	// prevent derived recipe sets from double-registering this one if multiple sets are enabled
	private static boolean _registeredMachines;
	private static boolean _registeredMachineUpgrades;
	private static boolean _registeredMachineTinkers;
	private static boolean _registeredConveyors;
	private static boolean _registeredDecorative;
	private static boolean _registeredSyringes;
	private static boolean _registeredPlastics;
	private static boolean _registeredMiscItems;
	private static boolean _registeredSafariNets;
	private static boolean _registeredVanillaImprovements;
	private static boolean _registeredRails;
	private static boolean _registeredGuns;
	private static boolean _registeredRedNet;
	private static boolean _registeredRedNetManual;
	
	public final void registerRecipes()
	{
		gatherItems();
		registerMachines();
		registerMachineUpgrades();
		registerMachineTinkers();
		registerConveyors();
		registerDecorative();
		if (MFRConfig.enableSyringes.getBoolean(true))
				registerSyringes();
		registerPlastics();
		registerMiscItems();
		registerSafariNets();
		registerVanillaImprovements();
		registerRails();
		if (MFRConfig.enableGuns.getBoolean(true))
			registerGuns();
		registerRedNet();
		registerRedNetManual();
	}
	
	protected void gatherItems()
	{
	}
	
	protected void registerMachines()
	{
		if(_registeredMachines)
		{
			return;
		}
		_registeredMachines = true;
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.machineBaseItem, 3), new Object[]
				{
			"PPP",
			"SSS",
			'P', "sheetPlastic",
			'S', "stone",
				} ));
		
		// regex: if\s*\((Machine\.\w+)[^\n]+\n[^\n]+\n[^\n]+\n\s+(\{[^}]+\} \))[^\n]+\n[^\n]+
		
		registerMachine(Machine.Planter, new Object[]
					{
				"GGG",
				"CPC",
				" M ",
				'G', "sheetPlastic",
				'P', Block.pistonBase,
				'C', Item.flowerPot,
				'M', MineFactoryReloadedCore.machineBaseItem,
					} );
		
		registerMachine(Machine.Fisher, new Object[]
					{
				"GGG",
				"RRR",
				"BMB",
				'G', "sheetPlastic",
				'R', Item.fishingRod,
				'B', Item.bucketEmpty,
				'M', MineFactoryReloadedCore.machineBaseItem,
					} );
		
		registerMachine(Machine.Harvester, new Object[]
					{
				"GGG",
				"SXS",
				" M ",
				'G', "sheetPlastic",
				'X', Item.axeGold,
				'S', Item.shears,
				'M', MineFactoryReloadedCore.machineBaseItem,
					} );
		
		registerMachine(Machine.Rancher, new Object[]
					{
				"GGG",
				"SBS",
				" M ",
				'G', "sheetPlastic",
				'B', Item.bucketEmpty,
				'S', Item.shears,
				'M', MineFactoryReloadedCore.machineBaseItem,
					} );
		
		registerMachine(Machine.Fertilizer, new Object[]
					{
				"GGG",
				"LBL",
				" M ",
				'G', "sheetPlastic",
				'L', Item.leather,
				'B', Item.glassBottle,
				'M', MineFactoryReloadedCore.machineBaseItem,
					} );
		
		registerMachine(Machine.Vet, new Object[]
					{
				"GGG",
				"SSS",
				"EME",
				'G', "sheetPlastic",
				'E', Item.spiderEye,
				'S', MineFactoryReloadedCore.syringeEmptyItem,
				'M', MineFactoryReloadedCore.machineBaseItem,
					} );
		
		registerMachine(Machine.ItemCollector, 8, new Object[]
					{
				"GGG",
				" C ",
				" M ",
				'G', "sheetPlastic",
				'C', Block.chest,
				'M', MineFactoryReloadedCore.machineBaseItem,
					} );
		
		registerMachine(Machine.BlockBreaker, new Object[]
					{
				"GGG",
				"PHS",
				" M ",
				'G', "sheetPlastic",
				'P', Item.pickaxeGold,
				'H', MineFactoryReloadedCore.factoryHammerItem,
				'S', Item.shovelGold,
				'M', MineFactoryReloadedCore.machineBaseItem,
					} );
		
		registerMachine(Machine.WeatherCollector, new Object[]
					{
				"GGG",
				"BBB",
				"UMU",
				'G', "sheetPlastic",
				'B', Block.fenceIron,
				'U', Item.bucketEmpty,
				'M', MineFactoryReloadedCore.machineBaseItem,
					} );
		
		registerMachine(Machine.SludgeBoiler, new Object[]
					{
				"GGG",
				"FFF",
				" M ",
				'G', "sheetPlastic",
				'F', Block.furnaceIdle,
				'M', MineFactoryReloadedCore.machineBaseItem,
					} );
		
		registerMachine(Machine.Sewer, 4, new Object[]
					{
				"GGG",
				"BUB",
				"BMB",
				'G', "sheetPlastic",
				'B', Item.brick,
				'U', Item.bucketEmpty,
				'M', MineFactoryReloadedCore.machineBaseItem,
					} );
		
		registerMachine(Machine.Composter, new Object[]
					{
				"GGG",
				"PFP",
				" M ",
				'G', "sheetPlastic",
				'P', Block.pistonBase,
				'F', Block.furnaceIdle,
				'M', MineFactoryReloadedCore.machineBaseItem,
					} );
		
		registerMachine(Machine.Breeder, new Object[]
					{
				"GGG",
				"CAC",
				"PMP",
				'G', "sheetPlastic",
				'P', "dyePurple",
				'C', Item.goldenCarrot,
				'A', Item.appleGold,
				'M', MineFactoryReloadedCore.machineBaseItem,
					} );
		
		registerMachine(Machine.Grinder, new Object[]
					{
				"GGG",
				"BSP",
				" M ",
				'G', "sheetPlastic",
				'P', Block.pistonBase,
				'B', Item.book,
				'S', Item.swordGold,
				'M', MineFactoryReloadedCore.machineBaseItem,
					} );
		
		registerMachine(Machine.AutoEnchanter, new Object[]
					{
				"GGG",
				"BBB",
				"DMD",
				'G', "sheetPlastic",
				'B', Item.book,
				'D', Item.diamond,
				'M', MineFactoryReloadedCore.machineBaseItem,
					} );
		
		registerMachine(Machine.Chronotyper, new Object[]
					{
				"GGG",
				"EEE",
				"PMP",
				'G', "sheetPlastic",
				'E', Item.emerald,
				'P', "dyePurple",
				'M', MineFactoryReloadedCore.machineBaseItem,
					} );
		
		registerMachine(Machine.Ejector, 8, new Object[]
					{
				"GGG",
				" D ",
				"RMR",
				'G', "sheetPlastic",
				'D', Block.dropper,
				'R', Item.redstone,
				'M', MineFactoryReloadedCore.machineBaseItem,
					} );
		
		registerMachine(Machine.ItemRouter, 8, new Object[]
					{
				"GGG",
				"RCR",
				" M ",
				'G', "sheetPlastic",
				'C', Block.chest,
				'R', Item.redstoneRepeater,
				'M', MineFactoryReloadedCore.machineBaseItem,
					} );
		
		registerMachine(Machine.LiquidRouter, 8, new Object[]
					{
				"GGG",
				"RBR",
				"BMB",
				'G', "sheetPlastic",
				'B', Item.bucketEmpty,
				'R', Item.redstoneRepeater,
				'M', MineFactoryReloadedCore.machineBaseItem,
					} );
		
		int dsuCount = MFRConfig.craftSingleDSU.getBoolean(false) ? 1 : 4;
		registerMachine(Machine.DeepStorageUnit, dsuCount, new Object[]
					{
				"GGG",
				"PPP",
				"EME",
				'G', "sheetPlastic",
				'P', Item.enderPearl,
				'E', Item.eyeOfEnder,
				'M', MineFactoryReloadedCore.machineBaseItem,
					} );
			
		if(MFRConfig.enableCheapDSU.getBoolean(false))
		{
			registerMachine(Machine.DeepStorageUnit, new Object[]
					{
				"GGG",
				"CCC",
				"CMC",
				'G', "sheetPlastic",
				'C', Block.chest,
				'M', MineFactoryReloadedCore.machineBaseItem,
					} );
		}
		
		registerMachine(Machine.LiquiCrafter, new Object[]
					{
				"GGG",
				"BWB",
				"FMF",
				'G', "sheetPlastic",
				'B', Item.bucketEmpty,
				'W', Block.workbench,
				'F', Item.itemFrame,
				'M', MineFactoryReloadedCore.machineBaseItem,
					} );
		
		registerMachine(Machine.LavaFabricator, new Object[]
					{
				"GGG",
				"OBO",
				"CMC",
				'G', "sheetPlastic",
				'O', Block.obsidian,
				'B', Item.blazeRod,
				'C', Item.magmaCream,
				'M', MineFactoryReloadedCore.machineBaseItem,
					} );
		
		registerMachine(Machine.OilFabricator, new Object[]
					{
				"GGG",
				"OTO",
				"OMO",
				'G', "sheetPlastic",
				'O', Block.obsidian,
				'T', Block.tnt,
				'M', MineFactoryReloadedCore.machineBaseItem,
					} );
		
		registerMachine(Machine.AutoJukebox, new Object[]
					{
				"GGG",
				" J ",
				" M ",
				'G', "sheetPlastic",
				'J', Block.jukebox,
				'M', MineFactoryReloadedCore.machineBaseItem,
					} );
		
		registerMachine(Machine.Unifier, new Object[]
					{
				"GGG",
				"CBC",
				" M ",
				'G', "sheetPlastic",
				'B', Item.book,
				'C', Item.comparator,
				'M', MineFactoryReloadedCore.machineBaseItem,
					} );
		
		registerMachine(Machine.AutoSpawner, new Object[]
					{
				"GGG",
				"ECE",
				"NMS",
				'G', "sheetPlastic",
				'E', Item.emerald,
				'C', Item.magmaCream,
				'N', Item.netherStalkSeeds,
				'S', Item.sugar,
				'M', MineFactoryReloadedCore.machineBaseItem,
					} );
		
		registerMachine(Machine.BioReactor, new Object[]
					{
				"GGG",
				"UEU",
				"SMS",
				'G', "sheetPlastic",
				'U', Item.sugar,
				'E', Item.fermentedSpiderEye,
				'S', Item.slimeBall,
				'M', MineFactoryReloadedCore.machineBaseItem,
					} );
		
		registerMachine(Machine.BioFuelGenerator, new Object[]
					{
				"GGG",
				"PFP",
				"RMR",
				'G', "sheetPlastic",
				'F', Block.furnaceIdle,
				'P', Block.pistonBase,
				'R', Item.blazeRod,
				'M', MineFactoryReloadedCore.machineBaseItem,
					} );
		
		registerMachine(Machine.AutoDisenchanter, new Object[]
					{
				"GGG",
				"RDR",
				"BMB",
				'G', "sheetPlastic",
				'B', Item.book,
				'D', Item.diamond,
				'R', Block.netherBrick,
				'M', MineFactoryReloadedCore.machineBaseItem,
					} );
		
		registerMachine(Machine.Slaughterhouse, new Object[]
					{
				"GGG",
				"SSS",
				"XMX",
				'G', "sheetPlastic",
				'S', Item.swordGold,
				'X', Item.axeGold,
				'M', MineFactoryReloadedCore.machineBaseItem,
					} );
		
		registerMachine(Machine.MeatPacker, new Object[]
					{
				"GGG",
				"BFB",
				"BMB",
				'G', "sheetPlastic",
				'B', Block.brick,
				'F', Item.flintAndSteel,
				'M', MineFactoryReloadedCore.machineBaseItem,
					} );
		
		registerMachine(Machine.EnchantmentRouter, new Object[]
					{
				"GGG",
				"RBR",
				" M ",
				'G', "sheetPlastic",
				'B', Item.book,
				'R', Item.redstoneRepeater,
				'M', MineFactoryReloadedCore.machineBaseItem,
					} );
		
		registerMachine(Machine.LaserDrill, new Object[]
					{
				"GGG",
				"LLL",
				"DMD",
				'G', "sheetPlastic",
				'L', Block.glowStone,
				'D', Item.diamond,
				'M', MineFactoryReloadedCore.machineBaseItem,
					} );
		
		registerMachine(Machine.LaserDrillPrecharger, new Object[]
					{
				"GGG",
				"LSL",
				"DMD",
				'G', "sheetPlastic",
				'L', Block.glowStone,
				'D', Item.diamond,
				'S', MineFactoryReloadedCore.pinkSlimeballItem,
				'M', MineFactoryReloadedCore.machineBaseItem,
					} );
		
		registerMachine(Machine.AutoAnvil, new Object[]
					{
				"GGG",
				"AAA",
				" M ",
				'G', "sheetPlastic",
				'A', Block.anvil,
				'M', MineFactoryReloadedCore.machineBaseItem,
					} );
		
		registerMachine(Machine.BlockSmasher, new Object[]
					{
				"GGG",
				"HHH",
				"BMB",
				'G', "sheetPlastic",
				'H', MineFactoryReloadedCore.factoryHammerItem,
				'B', Item.book,
				'M', MineFactoryReloadedCore.machineBaseItem,
					} );
		
		registerMachine(Machine.RedNote, new Object[]
					{
				"GGG",
				"CNC",
				" M ",
				'G', "sheetPlastic",
				'C', MineFactoryReloadedCore.rednetCableBlock,
				'N', Block.music,
				'M', MineFactoryReloadedCore.machineBaseItem,
					} );
		
		registerMachine(Machine.AutoBrewer, new Object[]
					{
				"GGG",
				"CBC",
				"RMR",
				'G', "sheetPlastic",
				'C', Block.chest,
				'B', Item.brewingStand,
				'R', Item.redstoneRepeater,
				'M', MineFactoryReloadedCore.machineBaseItem,
					} );
		
		registerMachine(Machine.FruitPicker, new Object[]
					{
				"GGG",
				"SXS",
				"SMS",
				'G', "sheetPlastic",
				'X', Item.axeGold,
				'S', Item.shears,
				'M', MineFactoryReloadedCore.machineBaseItem,
					} );
		
		registerMachine(Machine.BlockPlacer, new Object[]
					{
				"GGG",
				"DDD",
				" M ",
				'G', "sheetPlastic",
				'D', Block.dispenser,
				'M', MineFactoryReloadedCore.machineBaseItem,
					} );
		
		registerMachine(Machine.MobCounter, new Object[]
					{
				"GGG",
				"RCR",
				"SMS",
				'G', "sheetPlastic",
				'R', Item.redstoneRepeater,
				'C', Item.comparator,
				'S', MineFactoryReloadedCore.spyglassItem,
				'M', MineFactoryReloadedCore.machineBaseItem,
					} );
		
		registerMachine(Machine.SteamTurbine, new Object[]
					{
				"GGG",
				"PFP",
				"RMR",
				'G', "sheetPlastic",
				'F', Block.furnaceIdle,
				'P', Block.pistonBase,
				'R', Item.netherrackBrick,
				'M', MineFactoryReloadedCore.machineBaseItem,
					} );
		
		registerMachine(Machine.ChunkLoader, new Object[]
					{
				"GGG",
				"PFP",
				"RMR",
				'G', "sheetPlastic",
				'F', Item.netherStar,
				'P', Machine.DeepStorageUnit.getItemStack(),
				'R', Block.blockRedstone,
				'M', MineFactoryReloadedCore.machineBaseItem,
					} );
	}
	
	protected void registerMachine(Machine machine, Object... recipe)
	{
		registerMachine(machine, 1, recipe);
	}
	
	protected void registerMachine(Machine machine, int amount, Object... recipe)
	{
		if(machine.getIsRecipeEnabled())
		{
			ItemStack item = machine.getItemStack();
			item.stackSize = amount;
			GameRegistry.addRecipe(new ShapedOreRecipe(item, recipe));
		}
	}
	
	protected void registerMachineUpgrades()
	{
		if(_registeredMachineUpgrades)
		{
			return;
		}
		_registeredMachineUpgrades = true;
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.upgradeItem, 1, 0), new Object[]
				{
			"III",
			"PPP",
			"RGR",
			'I', new ItemStack(Item.dyePowder, 1, 4),
			'P', "dustPlastic",
			'R', Item.redstone,
			'G', "nuggetGold",
				} ));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.upgradeItem, 1, 1), new Object[]
				{
			"III",
			"PPP",
			"RGR",
			'I', Item.ingotIron,
			'P', "dustPlastic",
			'R', Item.redstone,
			'G', "nuggetGold",
				} ));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.upgradeItem, 1, 2), new Object[]
				{
			"III",
			"PPP",
			"RGR",
			'I', "ingotTin",
			'P', "dustPlastic",
			'R', Item.redstone,
			'G', "nuggetGold",
				} ));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.upgradeItem, 1, 3), new Object[]
				{
			"III",
			"PPP",
			"RGR",
			'I', "ingotCopper",
			'P', "dustPlastic",
			'R', Item.redstone,
			'G', "nuggetGold",
				} ));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.upgradeItem, 1, 4), new Object[]
				{
			"III",
			"PPP",
			"RGR",
			'I', "ingotBronze",
			'P', "dustPlastic",
			'R', Item.redstone,
			'G', "nuggetGold",
				} ));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.upgradeItem, 1, 5), new Object[]
				{
			"III",
			"PPP",
			"RGR",
			'I', "ingotSilver",
			'P', "dustPlastic",
			'R', Item.redstone,
			'G', "nuggetGold",
				} ));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.upgradeItem, 1, 6), new Object[]
				{
			"III",
			"PPP",
			"RGR",
			'I', "ingotGold",
			'P', "dustPlastic",
			'R', Item.redstone,
			'G', "nuggetGold",
				} ));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.upgradeItem, 1, 7), new Object[]
				{
			"III",
			"PPP",
			"RGR",
			'I', Item.netherQuartz,
			'P', "dustPlastic",
			'R', Item.redstone,
			'G', "nuggetGold",
				} ));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.upgradeItem, 1, 8), new Object[]
				{
			"III",
			"PPP",
			"RGR",
			'I', Item.diamond,
			'P', "dustPlastic",
			'R', Item.redstone,
			'G', "nuggetGold",
				} ));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.upgradeItem, 1, 9), new Object[]
				{
			"III",
			"PPP",
			"RGR",
			'I', "ingotPlatinum",
			'P', "dustPlastic",
			'R', Item.redstone,
			'G', "nuggetGold",
				} ));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.upgradeItem, 1, 10), new Object[]
				{
			"III",
			"PPP",
			"RGR",
			'I', Item.emerald,
			'P', "dustPlastic",
			'R', Item.redstone,
			'G', "nuggetGold",
				} ));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.upgradeItem, 1, 11), new Object[]
				{
			"III",
			"PPP",
			"RGR",
			'I', "cobblestone",
			'P', "dustPlastic",
			'R', Item.redstone,
			'G', "nuggetGold",
				} ));
		
		for(int i = 0; i < 16; i++)
		{
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.laserFocusItem, 1, i), new Object[]
					{
				"ENE",
				"NGN",
				"ENE",
				'E', Item.emerald,
				'N', "nuggetGold",
				'G', new ItemStack(MineFactoryReloadedCore.factoryGlassPaneBlock, 1, i)
					} ));
		}
	}

	protected void registerMachineTinkers()
	{
		if(_registeredMachineTinkers)
		{
			return;
		}
		_registeredMachineTinkers = true;
		
		GameRegistry.addRecipe(new ShapelessMachineTinker(Machine.ItemCollector, "Emits comparator signal",
				new ItemStack(Item.goldNugget)) {
			@Override
			protected boolean isMachineTinkerable(ItemStack machine)
			{
				return !machine.hasTagCompound() || !machine.getTagCompound().hasKey("hasTinkerStuff");
			}

			@Override
			protected ItemStack getTinkeredMachine(ItemStack machine)
			{
				machine = machine.copy();
				NBTTagCompound tag = machine.getTagCompound();
				if (tag == null) machine.setTagCompound(tag = new NBTTagCompound());
				tag.setBoolean("hasTinkerStuff", true);
				return machine;
			}
		});
	}
	
	protected void registerConveyors()
	{
		if(_registeredConveyors)
		{
			return;
		}
		_registeredConveyors = true;
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.conveyorBlock, 16, 16), new Object[]
				{
			"UUU",
			"RIR",
			'U', "itemRubber",
			'R', Item.redstone,
			'I', Item.ingotIron,
				} ));

		String[] dyes = { "Black", "Red", "Green", "Brown", "Blue", "Purple",
				"Cyan", "LightGray", "Gray", "Pink", "Lime", "Yellow",
				"LightBlue", "Magenta", "Orange", "White" }; // order copied from forge
		
		for(int i = 0; i < 16; i++)
		{
			GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(MineFactoryReloadedCore.conveyorBlock, 1, i),
					new ItemStack(MineFactoryReloadedCore.conveyorBlock, 1, 16),
					"dyeCeramic" + dyes[15 - i]));
		}
	}
	
	protected void registerDecorative()
	{
		if(_registeredDecorative)
		{
			return;
		}
		_registeredDecorative = true;
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.factoryRoadBlock, 16),
				new Object[] {
			"BBB",
			"BPB",
			"BBB",
			'P', "sheetPlastic",
			'B', new ItemStack(Block.stoneBrick, 1, 0),
				} ));
		
		GameRegistry.addRecipe(new ItemStack(MineFactoryReloadedCore.factoryRoadBlock, 4, 1), new Object[]
				{
			"R R",
			" G ",
			"R R",
			'R', new ItemStack(MineFactoryReloadedCore.factoryRoadBlock, 1, 0),
			'G', Block.redstoneLampIdle,
				} );
		
		GameRegistry.addShapelessRecipe(new ItemStack(MineFactoryReloadedCore.factoryRoadBlock, 1, 4), new ItemStack(MineFactoryReloadedCore.factoryRoadBlock, 1, 1));
		GameRegistry.addShapelessRecipe(new ItemStack(MineFactoryReloadedCore.factoryRoadBlock, 1, 1), new ItemStack(MineFactoryReloadedCore.factoryRoadBlock, 1, 4));

		String[] dyes = { "Black", "Red", "Green", "Brown", "Blue", "Purple",
				"Cyan", "LightGray", "Gray", "Pink", "Lime", "Yellow",
				"LightBlue", "Magenta", "Orange", "White" }; // order copied from forge
		
		ItemStack pane = new ItemStack(Block.thinGlass);
		for(int i = 0; i < 16; i++)
		{
			ItemStack dye = new ItemStack(MineFactoryReloadedCore.ceramicDyeItem, 4, i);
			GameRegistry.addRecipe(new ShapelessOreRecipe(dye, new ItemStack(Item.clay), "dye" + dyes[15 - i]));
			dye.stackSize = 1;
			ItemStack glassStack = new ItemStack(MineFactoryReloadedCore.factoryGlassBlock, 1, i);
			ItemStack paneStack = new ItemStack(MineFactoryReloadedCore.factoryGlassPaneBlock, 1, i);
			OreDictionary.registerOre("glass" + dyes[15 - i], glassStack.copy());
			OreDictionary.registerOre("glassPane" + dyes[15 - i], paneStack.copy());
			OreDictionary.registerOre("dyeCeramic" + dyes[15 - i], dye.copy());
			GameRegistry.addRecipe(new ShapelessOreRecipe(glassStack, dye, "glass"));
			glassStack.stackSize = 3;
			GameRegistry.addRecipe(new ShapelessOreRecipe(glassStack, dye, "glass", "glass", "glass"));
			GameRegistry.addShapelessRecipe(paneStack.copy(), dye, pane);
			paneStack.stackSize = 3;
			GameRegistry.addShapelessRecipe(paneStack.copy(), dye, pane, pane, pane);
			paneStack.stackSize = 8;
			GameRegistry.addShapelessRecipe(paneStack.copy(), dye, pane, pane, pane, pane, pane, pane, pane, pane);
			
			GameRegistry.addRecipe(new ItemStack(MineFactoryReloadedCore.factoryGlassPaneBlock, 16, i), new Object[]
					{
				"GGG",
				"GGG",
				'G', new ItemStack(MineFactoryReloadedCore.factoryGlassBlock, 1, i)
					} );
		}
		
		
		
		GameRegistry.addRecipe(new ItemStack(MineFactoryReloadedCore.factoryDecorativeBrickBlock, 8, 0), new Object[]
				{
			"M M",
			" B ",
			"M M",
			'B', Block.brick,
			'M', Block.ice,
				} );
		
		GameRegistry.addRecipe(new ItemStack(MineFactoryReloadedCore.factoryDecorativeBrickBlock, 8, 1), new Object[]
				{
			"M M",
			" B ",
			"M M",
			'B', Block.brick,
			'M', Block.glowStone,
				} );
		
		GameRegistry.addRecipe(new ItemStack(MineFactoryReloadedCore.factoryDecorativeBrickBlock, 8, 2), new Object[]
				{
			"M M",
			" B ",
			"M M",
			'B', Block.brick,
			'M', Block.blockLapis,
				} );
		
		GameRegistry.addRecipe(new ItemStack(MineFactoryReloadedCore.factoryDecorativeBrickBlock, 8, 3), new Object[]
				{
			"M M",
			" B ",
			"M M",
			'B', Block.brick,
			'M', Block.obsidian,
				} );
		
		GameRegistry.addRecipe(new ItemStack(MineFactoryReloadedCore.factoryDecorativeBrickBlock, 8, 4), new Object[]
				{
			"M M",
			" B ",
			"M M",
			'B', Block.brick,
			'M', new ItemStack(Block.stoneSingleSlab, 1, 0),
				} );
		
		GameRegistry.addRecipe(new ItemStack(MineFactoryReloadedCore.factoryDecorativeBrickBlock, 8, 5), new Object[]
				{
			"M M",
			" B ",
			"M M",
			'B', Block.brick,
			'M', Block.blockSnow,
				} );
		
		GameRegistry.addRecipe(new ItemStack(MineFactoryReloadedCore.factoryDecorativeBrickBlock, 8, 6), new Object[]
				{
			"M M",
			" B ",
			"M M",
			'B', Block.stoneBrick,
			'M', Block.glowStone,
				} );
		
		GameRegistry.addRecipe(new ItemStack(MineFactoryReloadedCore.factoryDecorativeBrickBlock, 8, 7), new Object[]
				{
			"M M",
			" B ",
			"M M",
			'B', Block.stoneBrick,
			'M', Block.ice,
				} );
		
		GameRegistry.addRecipe(new ItemStack(MineFactoryReloadedCore.factoryDecorativeBrickBlock, 8, 8), new Object[]
				{
			"M M",
			" B ",
			"M M",
			'B', Block.stoneBrick,
			'M', Block.blockLapis,
				} );
		
		GameRegistry.addRecipe(new ItemStack(MineFactoryReloadedCore.factoryDecorativeBrickBlock, 8, 9), new Object[]
				{
			"M M",
			" B ",
			"M M",
			'B', Block.stoneBrick,
			'M', Block.obsidian,
				} );
		
		GameRegistry.addRecipe(new ItemStack(MineFactoryReloadedCore.factoryDecorativeBrickBlock, 8, 10), new Object[]
				{
			"M M",
			" B ",
			"M M",
			'B', Block.stoneBrick,
			'M', Block.blockSnow,
				} );
		
		/**
		 * Smooth:
		 **/
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.factoryDecorativeStoneBlock, 8, 0), new Object[]
				{
			"SSS",
			"SDS",
			"SSS",
			'S', "stone",
			'D', new ItemStack(Item.dyePowder, 1, 0),
				}));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.factoryDecorativeStoneBlock, 8, 1), new Object[]
				{
			"SSS",
			"SDS",
			"SSS",
			'S', "stone",
			'D', Item.sugar,
				}));
		
		OreDictionary.registerOre("stone", new ItemStack(MineFactoryReloadedCore.factoryDecorativeStoneBlock, 8, 0));
		OreDictionary.registerOre("stone", new ItemStack(MineFactoryReloadedCore.factoryDecorativeStoneBlock, 8, 1));
		
		// cobble->smooth
		int stoneID = MineFactoryReloadedCore.factoryDecorativeStoneBlock.blockID;
		FurnaceRecipes.smelting().addSmelting(stoneID, 2, new ItemStack(MineFactoryReloadedCore.factoryDecorativeStoneBlock, 1, 0), 0.0001F);
		FurnaceRecipes.smelting().addSmelting(stoneID, 3, new ItemStack(MineFactoryReloadedCore.factoryDecorativeStoneBlock, 1, 1), 0.0001F);
		
		/**
		 * Cobble:
		 **/
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.factoryDecorativeStoneBlock, 8, 2), new Object[]
				{
			"SSS",
			"SDS",
			"SSS",
			'S', "cobblestone",
			'D', new ItemStack(Item.dyePowder, 1, 0),
				}));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.factoryDecorativeStoneBlock, 8, 3), new Object[]
				{
			"SSS",
			"SDS",
			"SSS",
			'S', "cobblestone",
			'D', Item.sugar,
				}));
		
		OreDictionary.registerOre("cobblestone", new ItemStack(MineFactoryReloadedCore.factoryDecorativeStoneBlock, 8, 2));
		OreDictionary.registerOre("cobblestone", new ItemStack(MineFactoryReloadedCore.factoryDecorativeStoneBlock, 8, 3));
		
		// meta-sensitive optional override in block code?
		
		/**
		 * Large brick:
		 **/
		GameRegistry.addRecipe(new ItemStack(MineFactoryReloadedCore.factoryDecorativeStoneBlock, 8, 4), new Object[]
				{
			"SSS",
			"SDS",
			"SSS",
			'S', Block.stoneBrick,
			'D', new ItemStack(Item.dyePowder, 1, 0),
				} );
		
		GameRegistry.addRecipe(new ItemStack(MineFactoryReloadedCore.factoryDecorativeStoneBlock, 8, 5), new Object[]
				{
			"SSS",
			"SDS",
			"SSS",
			'S', Block.stoneBrick,
			'D', Item.sugar,
				} );
		
		// smooth->large brick
		GameRegistry.addRecipe(new ItemStack(MineFactoryReloadedCore.factoryDecorativeStoneBlock, 4, 4), new Object[]
				{
			"SS",
			"SS",
			'S', new ItemStack(MineFactoryReloadedCore.factoryDecorativeStoneBlock, 1, 0),
				} );
		
		GameRegistry.addRecipe(new ItemStack(MineFactoryReloadedCore.factoryDecorativeStoneBlock, 4, 5), new Object[]
				{
			"SS",
			"SS",
			'S', new ItemStack(MineFactoryReloadedCore.factoryDecorativeStoneBlock, 1, 1),
				} );
		
		/**
		 * Small brick:
		 **/
		GameRegistry.addRecipe(new ItemStack(MineFactoryReloadedCore.factoryDecorativeStoneBlock, 8, 6), new Object[]
				{
			"SSS",
			"SDS",
			"SSS",
			'S', Block.brick,
			'D', new ItemStack(Item.dyePowder, 1, 0),
				} );
		
		GameRegistry.addRecipe(new ItemStack(MineFactoryReloadedCore.factoryDecorativeStoneBlock, 8, 7), new Object[]
				{
			"SSS",
			"SDS",
			"SSS",
			'S', Block.brick,
			'D', Item.sugar,
				} );
		
		// large brick->small brick
		GameRegistry.addRecipe(new ItemStack(MineFactoryReloadedCore.factoryDecorativeStoneBlock, 4, 6), new Object[]
				{
			"SS",
			"SS",
			'S', new ItemStack(MineFactoryReloadedCore.factoryDecorativeStoneBlock, 1, 4),
				} );
		
		GameRegistry.addRecipe(new ItemStack(MineFactoryReloadedCore.factoryDecorativeStoneBlock, 4, 7), new Object[]
				{
			"SS",
			"SS",
			'S', new ItemStack(MineFactoryReloadedCore.factoryDecorativeStoneBlock, 1, 5),
				} );
		
		/**
		 * Gravel:
		 **/
		GameRegistry.addRecipe(new ItemStack(MineFactoryReloadedCore.factoryDecorativeStoneBlock, 8, 8), new Object[]
				{
			"SSS",
			"SDS",
			"SSS",
			'S', Block.gravel,
			'D', new ItemStack(Item.dyePowder, 1, 0),
				} );
		
		GameRegistry.addRecipe(new ItemStack(MineFactoryReloadedCore.factoryDecorativeStoneBlock, 8, 9), new Object[]
				{
			"SSS",
			"SDS",
			"SSS",
			'S', Block.gravel,
			'D', Item.sugar,
				} );
		
		// FZ grinder?
		
		/**
		 * Paved:
		 **/
		GameRegistry.addRecipe(new ItemStack(MineFactoryReloadedCore.factoryDecorativeStoneBlock, 4, 10), new Object[]
				{
			"SSS",
			"SDS",
			"SSS",
			'S', new ItemStack(Block.stoneSingleSlab, 1, 0),
			'D', new ItemStack(Item.dyePowder, 1, 0),
				} );
		
		GameRegistry.addRecipe(new ItemStack(MineFactoryReloadedCore.factoryDecorativeStoneBlock, 4, 11), new Object[]
				{
			"SSS",
			"SDS",
			"SSS",
			'S', new ItemStack(Block.stoneSingleSlab, 1, 0),
			'D', Item.sugar,
				} );
		
		// smooth->paved
		GameRegistry.addShapelessRecipe(new ItemStack(MineFactoryReloadedCore.factoryDecorativeStoneBlock, 1, 10), 
				new ItemStack(MineFactoryReloadedCore.factoryDecorativeStoneBlock, 1, 0));
		GameRegistry.addShapelessRecipe(new ItemStack(MineFactoryReloadedCore.factoryDecorativeStoneBlock, 1, 11), 
				new ItemStack(MineFactoryReloadedCore.factoryDecorativeStoneBlock, 1, 1));
		
		// paved->smooth
		GameRegistry.addShapelessRecipe(new ItemStack(MineFactoryReloadedCore.factoryDecorativeStoneBlock, 1, 0), 
				new ItemStack(MineFactoryReloadedCore.factoryDecorativeStoneBlock, 1, 10));
		GameRegistry.addShapelessRecipe(new ItemStack(MineFactoryReloadedCore.factoryDecorativeStoneBlock, 1, 1), 
				new ItemStack(MineFactoryReloadedCore.factoryDecorativeStoneBlock, 1, 11));
		
		// TODO: add white/black sand?
		
		GameRegistry.addRecipe(new ItemStack(MineFactoryReloadedCore.factoryDecorativeBrickBlock, 1, 12), new Object[]
				{
			"MMM",
			"MMM",
			"MMM",
			'M', MineFactoryReloadedCore.meatIngotRawItem,
				} );
		
		GameRegistry.addRecipe(new ItemStack(MineFactoryReloadedCore.factoryDecorativeBrickBlock, 1, 13), new Object[]
				{
			"MMM",
			"MMM",
			"MMM",
			'M', MineFactoryReloadedCore.meatIngotCookedItem,
				} );
		
		GameRegistry.addShapelessRecipe(new ItemStack(MineFactoryReloadedCore.meatIngotRawItem, 9), new Object[]
				{
			new ItemStack(MineFactoryReloadedCore.factoryDecorativeBrickBlock, 1, 12)
				} );
		
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(MineFactoryReloadedCore.meatIngotCookedItem, 9), new Object[]
				{
			new ItemStack(MineFactoryReloadedCore.factoryDecorativeBrickBlock, 1, 13)
				}));
		
		int brickID = MineFactoryReloadedCore.factoryDecorativeBrickBlock.blockID;
		FurnaceRecipes.smelting().addSmelting(brickID, 13, new ItemStack(Item.coal, 3, 1), 0.001F);
		
		GameRegistry.addRecipe(new ItemStack(MineFactoryReloadedCore.meatIngotRawItem), new Object[]
				{
			"MMM",
			"MMM",
			"MMM",
			'M', MineFactoryReloadedCore.meatNuggetRawItem,
				} );
		
		GameRegistry.addRecipe(new ItemStack(MineFactoryReloadedCore.meatIngotCookedItem), new Object[]
				{
			"MMM",
			"MMM",
			"MMM",
			'M', MineFactoryReloadedCore.meatNuggetCookedItem,
				} );
		
		GameRegistry.addShapelessRecipe(new ItemStack(MineFactoryReloadedCore.meatNuggetRawItem, 9), new Object[]
				{
			new ItemStack(MineFactoryReloadedCore.meatIngotRawItem)
				} );
		
		GameRegistry.addShapelessRecipe(new ItemStack(MineFactoryReloadedCore.meatNuggetCookedItem, 9), new Object[]
				{
			new ItemStack(MineFactoryReloadedCore.meatIngotCookedItem)
				} );
	}
	
	protected void registerSyringes()
	{
		if(_registeredSyringes)
		{
			return;
		}
		_registeredSyringes = true;
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.xpExtractorItem), new Object[]
				{
			"PLP",
			"PLP",
			"RPR",
			'R', "itemRubber",
			'L', "glass",
			'P', "sheetPlastic",
				} ));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.syringeEmptyItem, 1), new Object[]
				{
			"PRP",
			"P P",
			" I ",
			'P', "sheetPlastic",
			'R', "itemRubber",
			'I', Item.ingotIron,
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
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.syringeSlimeItem, 1), new Object[]
				{
			"   ",
			" S ",
			"BLB",
			'B', "slimeball",
			'L', new ItemStack(Item.dyePowder, 1, 4),
			'S', MineFactoryReloadedCore.syringeEmptyItem,
				}));
		
		GameRegistry.addShapelessRecipe(new ItemStack(MineFactoryReloadedCore.syringeCureItem), new Object[] { MineFactoryReloadedCore.syringeEmptyItem, Item.appleGold });
	}
	
	protected void registerPlastics()
	{
		if(_registeredPlastics)
		{
			return;
		}
		_registeredPlastics = true;
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.plasticSheetItem, 4), new Object[]
				{
			"##",
			"##",
			'#', "dustPlastic",
				} ));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.factoryHammerItem, 1), new Object[]
				{
			"PPP",
			" S ",
			" S ",
			'P', "sheetPlastic",
			'S', "stickWood",
				} ));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.strawItem), new Object[]
				{
			"PP",
			"P ",
			"P ",
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
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.plasticCupItem, 16), new Object[]
				{
			" P ",
			"P P",
			'P', "sheetPlastic",
				} ));
		/*
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.plasticCellItem, 12), new Object[]
				{
			" P ",
			"P P",
			" P ",
			'P', "sheetPlastic",
				} ));//*/
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.bagItem, 3), new Object[]
				{
			"SPS",
			"P P",
			"PPP",
			'P', "sheetPlastic",
			'S', Item.silk
				} ));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.plasticBootsItem, 1), new Object[]
				{
			"P P",
			"P P",
			'P', "sheetPlastic",
				} ));
	}
	
	protected void registerMiscItems()
	{
		if(_registeredMiscItems)
		{
			return;
		}
		_registeredMiscItems = true;
		
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
			'G', "ingotGold",
			'L', "glass",
			'D', Item.diamond,
			'N', Item.netherStar
				}));
		
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
	
	protected void registerSafariNets()
	{
		if(_registeredSafariNets)
		{
			return;
		}
		_registeredSafariNets = true;
		
		GameRegistry.addRecipe(new ItemStack(MineFactoryReloadedCore.safariNetItem, 1), new Object[]
				{
			" E ",
			"EGE",
			" E ",
			'E', Item.enderPearl,
			'G', Item.ghastTear,
				} );
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.safariNetSingleItem, 1), new Object[]
				{
			"SLS",
			" B ",
			"S S",
			'S', Item.silk,
			'L', Item.leather,
			'B', "slimeball",
				}));
		
		GameRegistry.addRecipe(new ItemStack(MineFactoryReloadedCore.safariNetJailerItem, 1), new Object[]
				{
			" I ",
			"ISI",
			" I ",
			'S', MineFactoryReloadedCore.safariNetSingleItem,
			'I', Block.fenceIron
				} );
		
		if (MFRConfig.enableNetLauncher.getBoolean(true))
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.safariNetLauncherItem, 1), new Object[]
				{
			"PGP",
			"LGL",
			"IRI",
			'P', "sheetPlastic",
			'L', Item.glowstone,
			'G', Item.gunpowder,
			'I', Item.ingotIron,
			'R', Item.redstone,
				} ));
	}
	
	protected void registerVanillaImprovements()
	{
		if(_registeredVanillaImprovements)
		{
			return;
		}
		_registeredVanillaImprovements = true;
		
		FurnaceRecipes.smelting().addSmelting(MineFactoryReloadedCore.rawRubberItem.itemID, 0,
				new ItemStack(MineFactoryReloadedCore.rubberBarItem), 0.1F);
		FurnaceRecipes.smelting().addSmelting(MineFactoryReloadedCore.rubberWoodBlock.blockID, 0,
				new ItemStack(Item.coal, 1, 1), 0.1F);
		
		GameRegistry.addShapelessRecipe(new ItemStack(Block.planks, 3, 3),
				new ItemStack(MineFactoryReloadedCore.rubberWoodBlock));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Block.pistonStickyBase), new Object[]
				{
			"R",
			"P",
			'R', "itemRawRubber",
			'P', Block.pistonBase
				}));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.blankRecordItem, 1), new Object[]
				{
			"RRR",
			"RPR",
			"RRR",
			'R', "dustPlastic",
			'P', Item.paper,
				} ));
		
		if(MFRConfig.vanillaOverrideIce.getBoolean(true))
		{
			GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Block.ice, 1, 1), new ItemStack(Block.ice, 1, 0), "dustPlastic"));
		}
		
		if(MFRConfig.enableMossyCobbleRecipe.getBoolean(true))
		{
			GameRegistry.addShapelessRecipe(new ItemStack(Block.cobblestoneMossy), new Object[]
					{
				Block.cobblestone,
				Item.bucketWater,
				Item.wheat
					} );
		}
		
		GameRegistry.addRecipe(new ItemStack(MineFactoryReloadedCore.vineScaffoldBlock, 8), new Object[]
				{
			"VV",
			"VV",
			"VV",
			'V', Block.vine,
				} );
		
		GameRegistry.addShapelessRecipe(new ItemStack(MineFactoryReloadedCore.milkBottleItem), new Object[]
				{
			Item.bucketMilk,
			Item.glassBottle
				} );
		
		GameRegistry.addShapelessRecipe(new ItemStack(MineFactoryReloadedCore.chocolateMilkBucketItem), Item.bucketMilk, Item.bucketEmpty, new ItemStack(Item.dyePowder, 1, 3));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Block.torchWood, 4), new Object[]
				{
			"R",
			"S",
			'R', "itemRawRubber",
			'S', "stickWood",
				} ));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Block.torchWood, 1), new Object[]
				{
			"C",
			"S",
			'C', "itemCharcoalSugar",
			'S', "stickWood",
				} ));
		
		for (ItemStack torchStone : OreDictionary.getOres("torchStone"))
		{
			if (torchStone == null)
				continue;
			torchStone = torchStone.copy();
			torchStone.stackSize = 4;
			GameRegistry.addRecipe(new ShapedOreRecipe(torchStone, new Object[]
					{
				"R",
				"S",
				'R', "itemRawRubber",
				'S', "stoneRod",
					} ));
			torchStone = torchStone.copy();
			torchStone.stackSize = 1;
			
			GameRegistry.addRecipe(new ShapedOreRecipe(torchStone, new Object[]
					{
				"C",
				"S",
				'C', "itemCharcoalSugar",
				'S', "stoneRod",
					} ));
		}
	}
	
	protected void registerRails()
	{
		if(_registeredRails)
		{
			return;
		}
		_registeredRails = true;
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.railPickupCargoBlock, 2), new Object[]
				{
			" C ",
			"SDS",
			"SSS",
			'C', Block.chest,
			'S', "sheetPlastic",
			'D', Block.railDetector
				} ));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.railDropoffCargoBlock, 2), new Object[]
				{
			"SSS",
			"SDS",
			" C ",
			'C', Block.chest,
			'S', "sheetPlastic",
			'D', Block.railDetector
				} ));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.railPickupPassengerBlock, 3), new Object[]
				{
			" L ",
			"SDS",
			"SSS",
			'L', Block.blockLapis,
			'S', "sheetPlastic",
			'D', Block.railDetector
				} ));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.railDropoffPassengerBlock, 3), new Object[]
				{
			"SSS",
			"SDS",
			" L ",
			'L', Block.blockLapis,
			'S', "sheetPlastic",
			'D', Block.railDetector
				} ));
	}
	
	protected void registerGuns()
	{
		if(_registeredGuns)
		{
			return;
		}
		_registeredGuns = true;
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.needlegunItem), new Object[]
				{
					"PGP",
					"PLP",
					"SIS",
					'P', "sheetPlastic",
					'I', Item.ingotIron,
					'S', Item.magmaCream,
					'L', MineFactoryReloadedCore.safariNetLauncherItem,
					'G', MineFactoryReloadedCore.spyglassItem
				}));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.rocketLauncherItem), new Object[]
				{
					"PCP",
					"PRP",
					"ILI",
					'P', "sheetPlastic",
					'I', Item.minecartEmpty,
					'L', MineFactoryReloadedCore.needlegunItem,
					'R', new ItemStack(MineFactoryReloadedCore.logicCardItem, 1, 1),
					'C', new ItemStack(MineFactoryReloadedCore.logicCardItem, 1, 2)
				}));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.needlegunAmmoEmptyItem, 4), new Object[]
				{
					"P P",
					"PIP",
					"PPP",
					'P', "sheetPlastic",
					'I', Item.ingotIron,
				}));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.rocketItem, 2, 0), new Object[]
				{
					"PCP",
					"PTP",
					"IMI",
					'C', new ItemStack(MineFactoryReloadedCore.logicCardItem, 1, 0),
					'M', MineFactoryReloadedCore.needlegunAmmoEmptyItem,
					'P', "sheetPlastic",
					'T', Block.tnt,
					'I', Item.firework
				}));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.rocketItem, 2, 1), new Object[]
				{
					"PPP",
					"PTP",
					"IMI",
					'M', MineFactoryReloadedCore.needlegunAmmoEmptyItem,
					'P', "sheetPlastic",
					'T', Block.tnt,
					'I', Item.firework
				}));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.needlegunAmmoStandardItem), new Object[]
				{
					"AAA",
					"AAA",
					"GMG",
					'A', Item.arrow,
					'M', MineFactoryReloadedCore.needlegunAmmoEmptyItem,
					'G', Item.gunpowder
				}));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MineFactoryReloadedCore.needlegunAmmoAnvilItem), new Object[]
				{
					"SMS",
					"SAS",
					"STS",
					'A', new ItemStack(Block.anvil, 1, 0),
					'M', MineFactoryReloadedCore.needlegunAmmoEmptyItem,
					'S', Item.silk,
					'T', Block.tnt
				}));
		
		GameRegistry.addShapelessRecipe(new ItemStack(MineFactoryReloadedCore.needlegunAmmoFireItem),
				MineFactoryReloadedCore.needlegunAmmoStandardItem, Item.flintAndSteel);
		
		GameRegistry.addShapelessRecipe(new ItemStack(MineFactoryReloadedCore.needlegunAmmoLavaItem),
				MineFactoryReloadedCore.needlegunAmmoStandardItem, MineFactoryReloadedCore.plasticCupItem,
				Item.bucketLava);
		
		GameRegistry.addShapelessRecipe(new ItemStack(MineFactoryReloadedCore.needlegunAmmoSludgeItem),
				MineFactoryReloadedCore.needlegunAmmoStandardItem, MineFactoryReloadedCore.plasticCupItem,
				MineFactoryReloadedCore.sludgeBucketItem);
		
		GameRegistry.addShapelessRecipe(new ItemStack(MineFactoryReloadedCore.needlegunAmmoSewageItem),
				MineFactoryReloadedCore.needlegunAmmoStandardItem, MineFactoryReloadedCore.plasticCupItem,
				MineFactoryReloadedCore.sewageBucketItem);
	}
	
	protected void registerRedNet()
	{
		if(_registeredRedNet)
		{
			return;
		}
		_registeredRedNet = true;
		
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
			"nuggetGold",
			"nuggetGold",
			"nuggetGold",
			Item.redstone,
			Item.redstone,
			new ItemStack(MineFactoryReloadedCore.rednetCableBlock),
				} ));
		
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(MineFactoryReloadedCore.rednetCableBlock, 1, 2), new Object[]
				{
			"ingotGold",
			"ingotGold",
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
	
	private final void registerRedNetManual()
	{
		if(_registeredRedNetManual)
		{
			return;
		}
		_registeredRedNetManual = true;
		
		GameRegistry.addShapelessRecipe(ItemBlockRedNetLogic.manual, MineFactoryReloadedCore.plasticSheetItem, Item.redstone, Item.book);
	}
}
