package powercrystals.minefactoryreloaded.setup;

import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.io.File;

import static net.minecraftforge.common.config.Configuration.CATEGORY_GENERAL;

public class MFRConfig {

	// client config
	public static Property spyglassRange;
	public static Property brightRednetBand;
	public static boolean TESRCables;

	// common config
	public static Property dropFilledContainers;
	public static Property autoRegisterHarvestables;

	public static Property zoologist; //TODO remove

	public static Property enableBonemealFertilizing;
	public static Property conveyorCaptureNonItems;
	public static Property conveyorNeverCapturesPlayers;
	public static Property conveyorNeverCapturesTCGolems;
	public static Property playSounds;
	public static Property defaultRedNetCableOnly;
	public static Property fisherNeedsRod;

	public static Property treeSearchMaxVertical;
	public static Property treeSearchMaxHorizontal;
	public static Property verticalHarvestSearchMaxVertical;
	public static Property fruitTreeSearchMaxVertical;
	public static Property fruitTreeSearchMaxHorizontal;
	public static Property breederShutdownThreshold;
	public static Property autospawnerCostStandard;
	public static Property autospawnerCostExact;
	public static Property laserdrillCost;
	public static Property disenchanterEssence;
	public static Property steamBoilerExplodes;
	public static Property autobrewerFluidCost;

	public static Property largeSlimesDrop;
	public static Property meatSaturation;
	public static Property fishingDropRate;
	public static Property armorStacks;

	public static Property vanillaOverrideMilkBucket;

	public static Property enableLiquidSyringe;
	public static Property enableSPAMRExploding;
	public static Property enableFuelExploding;
	public static Property enableSpawnerCarts;

	public static Property enableChunkLimitBypassing;
	public static Property enableChunkLoaderRequiresOwner;
	public static Property enableConfigurableCLEnergy;

	public static Property redNetDebug;
	public static Property redNetConnectionBlacklist;

	public static Property worldGenDimensionBlacklist;

	public static Property rubberTreeWorldGen;
	public static Property rubberTreeBiomeWhitelist;
	public static Property rubberTreeBiomeBlacklist;
	public static Property enableMassiveTree;

	public static Property mfrLakeWorldGen;
	public static Property mfrLakeSewageRarity;
	public static Property mfrLakeSewageBiomeList;
	public static Property mfrLakeSewageBiomeListToggle;
	public static Property mfrLakeSludgeRarity;
	public static Property mfrLakeSludgeBiomeList;
	public static Property mfrLakeSludgeBiomeListToggle;

	public static Property mfrLakeSewageRetrogen;
	public static Property mfrLakeSludgeRetrogen;
	public static Property rubberTreeRetrogen;

	public static Property unifierBlacklist;
	public static Property spawnerBlacklist;
	public static Property safarinetBlacklist;

	public static ConfigCategory spawnerCustomization;
	public static Property harvesterSkip;

	public static Property passengerRailSearchMaxHorizontal;
	public static Property passengerRailSearchMaxVertical;

	public static String CATEGORY_ITEM = "item";

	public static void loadClientConfig(File configFile) {

		Configuration c = new Configuration(configFile, true);

		spyglassRange = c.get(CATEGORY_GENERAL, "SpyglassRange", 200);
		spyglassRange.setComment("The maximum number of blocks the spyglass and ruler can look to find something. This calculation is performed only on the client side.");
		brightRednetBand = c.get(CATEGORY_GENERAL, "BrightRedNetColors", false);
		brightRednetBand.setComment("If true, RedNet color bands will always be bright.");
		TESRCables = !c.get(CATEGORY_GENERAL, "DisableRedNetFramerateStabilization", false,
				"Set to true to disable RedNet cables switching to TESRs when they detect " +
				"that they are updating too rapidly.").getBoolean(false);

		c.save();
	}

	static Configuration config;

	public static boolean isIntegrationEnabled(String name, String from) {

		boolean isVanilla = name.equals("Minecraft");
		Property value = config.get("Integration.Mod", name, true).setRequiresMcRestart(true);
		if (isVanilla) {
			value.setComment("If true, MFR will enable its standard (vanilla-only) integration.");
		} else {
			value.setComment("If true, MFR will enable " + name + " integration. Provided by " + from);
		}
		return value.getBoolean();
	}

	public static boolean isRecipeSetEnabled(String name, String from) {

		boolean isVanilla = name.equals("Minecraft");
		Property value = config.get("Integration.Recipes", name, isVanilla).setRequiresMcRestart(true);
		if (isVanilla) {
			value.setComment("If true, MFR will register its standard (vanilla-only) recipes.");
		} else {
			value.setComment("If true, MFR will register the " + name + "-based recipes. Provided by " + from);
		}
		return value.getBoolean();
	}

	public static void saveCommon() {

		config.save();
	}

	public static void loadCommonConfig(File configFile) {

		Configuration c = new Configuration(configFile, true);
		c.load();
		config = c;

		String category = "Entity", subCategory = "";
		zoologist = c.get(category, "ID.Zoologist", 330).setRequiresMcRestart(true);
		enableSpawnerCarts = c.get(category, "EnableSpawnerCarts", true);
		enableSpawnerCarts.setComment("If true, using a portaspawner on an empty minecart will make it into a spawner cart");

		playSounds = c.get(CATEGORY_GENERAL, "PlaySounds", true);
		playSounds.setComment("Set to false to disable various sounds and particle effects, such as when a block is harvested.");

		safarinetBlacklist = c.get(CATEGORY_GENERAL, "SafariNetBlacklist", new String[0]);
		safarinetBlacklist.setComment("A list of entity IDs (e.g.: CaveSpider or VillagerGolem or Forestry.butterflyGE) to blacklist from being captured by the SafariNet. The Debugger item will display an entity's ID when used.");

		//{ Searching
		treeSearchMaxHorizontal = c.get(CATEGORY_GENERAL + ".SearchDistance", "Tree.MaxHorizontal", 512);
		treeSearchMaxHorizontal.setComment("When searching for parts of a tree, how far out to the sides (radius) to search");
		verticalHarvestSearchMaxVertical = c.get(CATEGORY_GENERAL + ".SearchDistance", "StackingBlock.MaxVertical", 35);
		verticalHarvestSearchMaxVertical.setComment("How far upward to search for members of \"stacking\" blocks, like cactus and sugarcane");
		passengerRailSearchMaxVertical = c.get(CATEGORY_GENERAL + ".SearchDistance", "PassengerRail.MaxVertical", 2);
		passengerRailSearchMaxVertical.setComment("When searching for players or dropoff locations, how far up to search");
		passengerRailSearchMaxHorizontal = c.get(CATEGORY_GENERAL + ".SearchDistance", "PassengerRail.MaxHorizontal", 3);
		passengerRailSearchMaxHorizontal.setComment("When searching for players or dropoff locations, how far out to the sides (radius) to search");
		fruitTreeSearchMaxHorizontal = c.get(CATEGORY_GENERAL + ".SearchDistance", "FruitTree.MaxHorizontal", 7);
		fruitTreeSearchMaxHorizontal.setComment("When searching for parts of a fruit tree, how far out to the sides (radius) to search");
		fruitTreeSearchMaxVertical = c.get(CATEGORY_GENERAL + ".SearchDistance", "FruitTree.MaxVertical", 25);
		fruitTreeSearchMaxVertical.setComment("When searching for parts of a fruit tree, how far up to search");
		//}

		//{ RedNet
		category = CATEGORY_GENERAL + ".RedNet";
		redNetDebug = c.get(category, "Debug", false);
		redNetDebug.setComment("If true, RedNet cables will dump a massive amount of data to the log file. You should probably only use this if PC tells you to.");
		redNetConnectionBlacklist = c.get(category, "ConnectionBlackList", new String[0]).setRequiresMcRestart(true);
		redNetConnectionBlacklist.setComment("A list of block IDs to prevent RedNet cables from connecting to. (e.g., minecraft:torch)");
		defaultRedNetCableOnly = c.get(category, "CableOnly", false);
		defaultRedNetCableOnly.setComment("If true, placed rednet cable will default to cable-only connections.");
		//}

		//{ Worldgen
		category = CATEGORY_GENERAL + ".WorldGen";
		worldGenDimensionBlacklist = c.get(category, "Dimension.Blacklist", new int[0]).setRequiresMcRestart(true);
		worldGenDimensionBlacklist.setComment("A list of dimension IDs to disable MFR worldgen in.");

		subCategory = category + ".RetroGen";
		c.getCategory(subCategory).setComment("Enable or disable specific retrogen items.\nOnly has an effect if retroactive generation is enabled in CoFHWorld.");
		mfrLakeSewageRetrogen = c.get(subCategory, "SewageLakes", false).setRequiresMcRestart(true);
		mfrLakeSludgeRetrogen = c.get(subCategory, "SludgeLakes", false).setRequiresMcRestart(true);
		rubberTreeRetrogen = c.get(subCategory, "RubberTrees", true).setRequiresMcRestart(true);

		subCategory = category + ".RubberTrees";
		rubberTreeWorldGen = c.get(subCategory, "Enable", true).setRequiresMcRestart(true);
		rubberTreeWorldGen.setComment("Whether or not to generate MFR rubber trees during map generation");
		rubberTreeBiomeWhitelist = c.get(subCategory, "Biome.Whitelist", new String[0]).setRequiresMcRestart(true);
		rubberTreeBiomeWhitelist.setComment("A list of biome IDs to allow rubber trees to spawn in. Does nothing if rubber tree worldgen is disabled.");
		rubberTreeBiomeBlacklist = c.get(subCategory, "Biome.Blacklist", new String[] { "void" }).setRequiresMcRestart(true);
		rubberTreeBiomeBlacklist.setComment("A list of biome IDs to disallow rubber trees to spawn in. Overrides any other biomes added.");
		enableMassiveTree = c.get(subCategory, "SacredRubberSapling", true).setRequiresMcRestart(true);
		enableMassiveTree.setComment("If true, enable adding Enchanted Sacred Rubber Saplings to stronghold library loot.");

		subCategory = category + ".Lakes";
		mfrLakeWorldGen = c.get(subCategory, "Enable", true).setRequiresMcRestart(true);
		mfrLakeWorldGen.setComment("Whether or not to generate MFR lakes during map generation. By default, MFR will not attempt lake worldgen in dimensions where the player cannot respawn.");

		{
			String[] lakeBlacklist = { "ocean", "river", "frozen_ocean", "frozen_river", "beaches", "deep_ocean", "stone_beach", "cold_beach", "void" };

			mfrLakeSludgeRarity = c.get(subCategory + ".Sludge", "Rarity", 32).setRequiresMcRestart(true);
			mfrLakeSludgeRarity.setComment("Higher numbers make sludge lakes rarer. A value of one will be approximately one per chunk. 0 will disable.");
			mfrLakeSludgeBiomeList = c.get(subCategory + ".Sludge", "BiomeList", lakeBlacklist).setRequiresMcRestart(true);
			mfrLakeSludgeBiomeList.setComment("A list of biome IDs to allow/disallow Sludge lakes to spawn in. Does nothing if lake worldgen is disabled.");
			mfrLakeSludgeBiomeListToggle = c.get(subCategory + ".Sludge", "BiomeList.Mode", false).setRequiresMcRestart(true);
			mfrLakeSludgeBiomeListToggle.setComment("If false, the biome list is a blacklist. If true, the biome list is a whitelist.");

			mfrLakeSewageRarity = c.get(subCategory + ".Sewage", "Rarity", 32).setRequiresMcRestart(true);
			mfrLakeSewageRarity.setComment("Higher numbers make Sewage lakes rarer. A value of one will be approximately one per chunk. 0 will disable.");
			mfrLakeSewageBiomeList = c.get(subCategory + ".Sewage", "BiomeList", lakeBlacklist).setRequiresMcRestart(true);
			mfrLakeSewageBiomeList.setComment("A list of biome IDs to allow/disallow Sewage lakes to spawn in. Does nothing if lake worldgen is disabled.");
			mfrLakeSewageBiomeListToggle = c.get(subCategory + ".Sewage", "BiomeList.Mode", false).setRequiresMcRestart(true);
			mfrLakeSewageBiomeListToggle.setComment("If false, the biome list is a blacklist. If true, the biome list is a whitelist.");
		}
		//}

		//{ Item/block behavior overriding
		category = CATEGORY_ITEM + ".VanillaOverride";
		vanillaOverrideMilkBucket = c.get(category, "MilkBucket", true).setRequiresMcRestart(true);
		vanillaOverrideMilkBucket.setComment("If true, replaces the vanilla milk bucket so milk can be placed in the world.");
		//}

		//{ misc. item changes
		meatSaturation = c.get(CATEGORY_ITEM, "Meat.IncreasedSaturation", false).setRequiresMcRestart(true);
		meatSaturation.setComment("If true, meat will be worth steak saturation instead of cookie saturation.");
		fishingDropRate = c.get(CATEGORY_ITEM, "FishDropRate", 5);
		fishingDropRate.setComment("The rate at which fish are dropped from the fishing rod. The drop rate is 1 / this number. Must be greater than 0.");

		enableSPAMRExploding = c.get(CATEGORY_ITEM, "SPAMR.Exploding", true);
		enableSPAMRExploding.setComment("If true, SPAMRs will explode when they run out of fuel.");
		enableFuelExploding = c.get(CATEGORY_ITEM, "Biofuel.Exploding", true);
		enableFuelExploding.setComment("If true, biofuel will explode when in the nether.");

		enableLiquidSyringe = c.get(CATEGORY_ITEM, "LiquidSyringes", true).setRequiresMcRestart(true);
		enableLiquidSyringe.setComment("If true, Empty Syringes will be able to contain liquids and inject players.");

		largeSlimesDrop = c.get(CATEGORY_ITEM, "LargeSlimeDrop", false);
		largeSlimesDrop.setComment("If true, only pink slimes larger than tiny will drop pink slimeballs. Provided for those who want a more work-intensive laser drill. (slimes can only be made larger through the slime embiggening syringe)");

		armorStacks = c.get(CATEGORY_ITEM, "ArmorStacks", false);
		armorStacks.setComment("If true, Plastic Armor will stack to 4");
		//}

		//{ Additional machine configs
		{
			final String machine = "Machine.";
			category = machine + "Conveyor";
			conveyorCaptureNonItems = c.get(category, "CaptureNonItems", true).setRequiresMcRestart(true);
			conveyorCaptureNonItems.setComment("If false, conveyors will not grab non-item entities. Breaks conveyor mob grinders but makes them safe for golems, etc.");
			conveyorNeverCapturesPlayers = c.get(category, "NeverCapturePlayers", false).setRequiresMcRestart(true);
			conveyorNeverCapturesPlayers.setComment("If true, conveyors will NEVER capture players regardless of other settings.");
			conveyorNeverCapturesTCGolems = c.get(category, "NeverCaptureTCGolems", false).setRequiresMcRestart(true);
			conveyorNeverCapturesTCGolems.setComment("If true, conveyors will NEVER capture Thaumcraft golems regardless of other settings.");

			category = machine + Machine.ChunkLoader.getName();
			enableChunkLimitBypassing = c.get(category, "IgnoreChunkLimit", false);
			enableChunkLimitBypassing.setComment("If true, the Chunk Loader will ignore forgeChunkLoading.cfg.");
			enableChunkLoaderRequiresOwner = c.get(category, "RequiresOwnerOnline", false);
			enableChunkLoaderRequiresOwner.setComment("If true, the Chunk Loader will require that the player who placed it be online to function");
			enableConfigurableCLEnergy = c.get(category, "EnableConfigurableActivationEnergy", false).setRequiresMcRestart(true);
			enableConfigurableCLEnergy.setComment("If true, the Chunk Loader will use the activation energy config in this section. WARNING: this makes it much more expensive at lower values. (non-configurable is exponential)");

			category = machine + Machine.AutoSpawner.getName();
			spawnerBlacklist = c.get(category, "Blacklist", new String[] {"VillagerGolem"}).setRequiresMcRestart(true);
			spawnerBlacklist.setComment("A list of entity IDs (e.g.: CaveSpider or VillagerGolem or Forestry.butterflyGE) to blacklist from the AutoSpawner. The Debugger item will display an entity's ID when used.");
			category += ".Cost";
			autospawnerCostExact = c.get(category, "Exact", 5).setRequiresMcRestart(true);
			autospawnerCostExact.setComment("The multiplier for work required to generate a mob in exact mode.");
			autospawnerCostStandard = c.get(category, "Standard", 1).setRequiresMcRestart(true);
			autospawnerCostStandard.setComment("The multiplier for work required to generate a mob in standard (non-exact) mode.");
			spawnerCustomization = c.getCategory(category + ".Custom").setRequiresMcRestart(true);
			spawnerCustomization.setComment("Custom base XP costs for entities. format: I:<entity_id> = #. e.g.:\n" + "I:VillagerGolem = 25\nI:Slime = 50");

			harvesterSkip = c.get(machine + Machine.Harvester.getName(), "SkipWork", false).setRequiresMcRestart(true);
			harvesterSkip.setComment("If true, the harvester will skip scanning some bocks when filled with sludge");

			laserdrillCost = c.get(machine + Machine.LaserDrill.getName(), "Work", 300).setRequiresMcRestart(true);
			laserdrillCost.setComment("The work required by the drill to generate a single ore.");

			unifierBlacklist = c.get(machine + Machine.Unifier.getName(), "Blacklist", new String[] {"dyeBlue","dyeWhite","dyeBrown","dyeBlack","listAllwater","listAllmilk"}).setRequiresMcRestart(true);
			unifierBlacklist.setComment("A list of ore dictionary entries to disable unifying for. By default, MFR will not attempt to unify anything with more than one oredict name.");

			breederShutdownThreshold = c.get(machine + Machine.Breeder.getName(), "ShutdownThreshold", 50).setRequiresMcRestart(true);
			breederShutdownThreshold.setComment("If the number of entities in the breeder's target area exceeds this value, the breeder will cease operating. This is provided to control server lag.");

			enableBonemealFertilizing = c.get(machine + Machine.Fertilizer.getName(), "EnableBonemeal", false).setRequiresMcRestart(true);
			enableBonemealFertilizing.setComment("If true, the fertilizer will use bonemeal as well as MFR fertilizer. Provided for those who want a less work-intensive farm.");

			disenchanterEssence = c.get(machine + Machine.AutoDisenchanter.getName(), "EnableEssence", false).setRequiresMcRestart(true);
			disenchanterEssence.setComment("If true, the disenchanter will use essence to disenchant items. Provided for those who want a more work-intensive enchanting system.");

			steamBoilerExplodes = c.get(machine + Machine.SteamBoiler.getName(), "Explodes", false);
			steamBoilerExplodes.setComment("If true, the steam boiler will explode if it's hot and dry when you try to pump water into it.");

			fisherNeedsRod = c.get(machine + Machine.Fisher.getName(), "RequiresFishingRod", false);
			fisherNeedsRod.setComment("If true, the fisher will require a fishing rod to function.");

			autobrewerFluidCost = c.get(machine + Machine.AutoBrewer.getName(), "WaterPerBottle", 250).setMinValue(1).setMaxValue(4000);
			autobrewerFluidCost.setComment("The amount of water used by the Auto-Brewer to fill a bottle with water");
		}
		//}

		for (Machine machine : Machine.values()) {
			machine.load(c);
		}

		autoRegisterHarvestables = c.get(CATEGORY_GENERAL, "Harvestables.Automatic", false);
		autoRegisterHarvestables.setComment("If true, MFR will attempt to automatically detect harvestable blocks and register them.");

		// TODO: make this config per-player
		dropFilledContainers = c.get(CATEGORY_GENERAL, "Tanks.FillWithoutEmptySlots", true);
		dropFilledContainers.setComment("If true, when you have no empty slots in your inventory, you will continue filling buckets from tanks and drop them on the ground.");

		c.save();
	}

}
