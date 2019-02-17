package powercrystals.minefactoryreloaded.modhelpers.vanilla;

import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockNetherWart;
import net.minecraft.block.IGrowable;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.*;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.api.integration.IMFRIntegrator;
import powercrystals.minefactoryreloaded.api.mob.MobDrop;
import powercrystals.minefactoryreloaded.api.plant.FertilizerType;
import powercrystals.minefactoryreloaded.api.plant.HarvestType;
import powercrystals.minefactoryreloaded.core.drinkhandlers.DrinkHandlerLava;
import powercrystals.minefactoryreloaded.core.drinkhandlers.DrinkHandlerWater;
import powercrystals.minefactoryreloaded.farmables.fertilizables.*;
import powercrystals.minefactoryreloaded.modhelpers.vanilla.handlers.FruitChorus;
import powercrystals.minefactoryreloaded.farmables.fruits.FruitCocoa;
import powercrystals.minefactoryreloaded.modhelpers.vanilla.handlers.PlantableChorus;
import powercrystals.minefactoryreloaded.modhelpers.vanilla.handlers.PlantableNetherWart;
import powercrystals.minefactoryreloaded.modhelpers.vanilla.handlers.grindables.GrindableEnderman;
import powercrystals.minefactoryreloaded.modhelpers.vanilla.handlers.grindables.GrindableSlime;
import powercrystals.minefactoryreloaded.farmables.grindables.GrindableStandard;
import powercrystals.minefactoryreloaded.modhelpers.vanilla.handlers.grindables.GrindableZombiePigman;
import powercrystals.minefactoryreloaded.farmables.harvestables.*;
import powercrystals.minefactoryreloaded.farmables.plantables.*;
import powercrystals.minefactoryreloaded.farmables.ranchables.*;
import powercrystals.minefactoryreloaded.modhelpers.vanilla.handlers.ranchables.RanchableChicken;
import powercrystals.minefactoryreloaded.modhelpers.vanilla.handlers.ranchables.RanchableCow;
import powercrystals.minefactoryreloaded.modhelpers.vanilla.handlers.ranchables.RanchableMooshroom;
import powercrystals.minefactoryreloaded.modhelpers.vanilla.handlers.spawnhandlers.SpawnableEnderman;
import powercrystals.minefactoryreloaded.modhelpers.vanilla.handlers.spawnhandlers.SpawnableHorse;
import powercrystals.minefactoryreloaded.modhelpers.vanilla.handlers.VanillaEggHandler;
import powercrystals.minefactoryreloaded.setup.MFRConfig;

import javax.annotation.Nonnull;

public class Minecraft implements IMFRIntegrator {

	@Override
	public void load() {

		REGISTRY.registerPlantable(new PlantableSapling(Blocks.SAPLING));
		REGISTRY.registerPlantable(new PlantableStandard(Blocks.BROWN_MUSHROOM));
		REGISTRY.registerPlantable(new PlantableStandard(Blocks.RED_MUSHROOM));
		REGISTRY.registerPlantable(new PlantableCropPlant(Items.PUMPKIN_SEEDS, Blocks.PUMPKIN_STEM));
		REGISTRY.registerPlantable(new PlantableCropPlant(Items.MELON_SEEDS, Blocks.MELON_STEM));
		REGISTRY.registerPlantable(new PlantableCropPlant(Items.WHEAT_SEEDS, Blocks.WHEAT));
		REGISTRY.registerPlantable(new PlantableCropPlant(Items.CARROT, Blocks.CARROTS));
		REGISTRY.registerPlantable(new PlantableCropPlant(Items.POTATO, Blocks.POTATOES));
		REGISTRY.registerPlantable(new PlantableCropPlant(Items.BEETROOT_SEEDS, Blocks.BEETROOTS));
		REGISTRY.registerPlantable(new PlantableNetherWart());
		REGISTRY.registerPlantable(new PlantableCocoa(Items.DYE, Blocks.COCOA, 3));
		REGISTRY.registerPlantable(new PlantableChorus());

		REGISTRY.registerHarvestable(new HarvestableWood(Blocks.LOG));
		REGISTRY.registerHarvestable(new HarvestableWood(Blocks.LOG2));
		REGISTRY.registerHarvestable(new HarvestableTreeLeaves(Blocks.LEAVES));
		REGISTRY.registerHarvestable(new HarvestableTreeLeaves(Blocks.LEAVES2));
		REGISTRY.registerHarvestable(new HarvestableStandard(Blocks.REEDS, HarvestType.LeaveBottom));
		REGISTRY.registerHarvestable(new HarvestableStandard(Blocks.CACTUS, HarvestType.LeaveBottom));
		REGISTRY.registerHarvestable(new HarvestableStandard(Blocks.RED_FLOWER, HarvestType.Normal));
		REGISTRY.registerHarvestable(new HarvestableStandard(Blocks.YELLOW_FLOWER, HarvestType.Normal));
		REGISTRY.registerHarvestable(new HarvestableShrub(Blocks.TALLGRASS));
		REGISTRY.registerHarvestable(new HarvestableShrub(Blocks.DEADBUSH));
		REGISTRY.registerHarvestable(new HarvestableShrub(Blocks.DOUBLE_PLANT));
		REGISTRY.registerHarvestable(new HarvestableStandard(Blocks.BROWN_MUSHROOM_BLOCK, HarvestType.Tree));
		REGISTRY.registerHarvestable(new HarvestableStandard(Blocks.RED_MUSHROOM_BLOCK, HarvestType.Tree));
		REGISTRY.registerHarvestable(new HarvestableMushroom(Blocks.BROWN_MUSHROOM));
		REGISTRY.registerHarvestable(new HarvestableMushroom(Blocks.RED_MUSHROOM));
		REGISTRY.registerHarvestable(new HarvestableStemPlant(Blocks.PUMPKIN_STEM, Blocks.PUMPKIN));
		REGISTRY.registerHarvestable(new HarvestableStemPlant(Blocks.MELON_STEM, Blocks.MELON_BLOCK));
		REGISTRY.registerHarvestable(new HarvestableGourd(Blocks.PUMPKIN));
		REGISTRY.registerHarvestable(new HarvestableGourd(Blocks.MELON_BLOCK));
		REGISTRY.registerHarvestable(new HarvestableCropPlant((BlockCrops) Blocks.WHEAT));
		REGISTRY.registerHarvestable(new HarvestableCropPlant((BlockCrops) Blocks.CARROTS));
		REGISTRY.registerHarvestable(new HarvestableCropPlant((BlockCrops) Blocks.POTATOES));
		REGISTRY.registerHarvestable(new HarvestableCropPlant((BlockCrops) Blocks.BEETROOTS));
		REGISTRY.registerHarvestable(new HarvestableCropMeta(Blocks.NETHER_WART, BlockNetherWart.AGE, 3));
		REGISTRY.registerHarvestable(new HarvestableVine(Blocks.VINE));
		REGISTRY.registerHarvestable(new HarvestableCocoa(Blocks.COCOA));

		REGISTRY.registerFertilizable(new FertilizableStandard((IGrowable) Blocks.SAPLING));
		REGISTRY.registerFertilizable(new FertilizableCropPlant((IGrowable) Blocks.WHEAT, 7));
		REGISTRY.registerFertilizable(new FertilizableCropPlant((IGrowable) Blocks.CARROTS, 7));
		REGISTRY.registerFertilizable(new FertilizableCropPlant((IGrowable) Blocks.POTATOES, 7));
		REGISTRY.registerFertilizable(new FertilizableCropPlant((IGrowable) Blocks.BEETROOTS, 3));
		REGISTRY.registerFertilizable(new FertilizableStandard((IGrowable) Blocks.BROWN_MUSHROOM));
		REGISTRY.registerFertilizable(new FertilizableStandard((IGrowable) Blocks.RED_MUSHROOM));
		REGISTRY.registerFertilizable(new FertilizableStemPlants((IGrowable) Blocks.PUMPKIN_STEM));
		REGISTRY.registerFertilizable(new FertilizableStemPlants((IGrowable) Blocks.MELON_STEM));
		REGISTRY.registerFertilizable(new FertilizableNetherWart());
		REGISTRY.registerFertilizable(new FertilizableCocoa(Blocks.COCOA));
		REGISTRY.registerFertilizable(new FertilizableGrass());

		if (MFRConfig.enableBonemealFertilizing.getBoolean(false)) {
			REGISTRY.registerFertilizer(new FertilizerStandard(Items.DYE, 15));
		} else {
			REGISTRY.registerFertilizer(new FertilizerStandard(Items.DYE, 15, FertilizerType.Grass));
		}

		REGISTRY.registerRanchable(new RanchableCow());
		REGISTRY.registerRanchable(new RanchableMooshroom());
		REGISTRY.registerRanchable(new RanchableSheep());
		REGISTRY.registerRanchable(new RanchableSquid());
		REGISTRY.registerRanchable(new RanchableChicken());
		REGISTRY.registerRanchable(new RanchableParrot());

		REGISTRY.registerGrinderBlacklist(EntityDragon.class);
		REGISTRY.registerGrinderBlacklist(EntityWither.class);
		REGISTRY.registerGrinderBlacklist(EntityVillager.class);

		REGISTRY.registerGrindable(new GrindableStandard(EntityChicken.class, new MobDrop[] {
				new MobDrop(30, ItemStack.EMPTY),
				new MobDrop(10, new ItemStack(Items.EGG))
		}, false));
		REGISTRY.registerGrindable(new GrindableStandard(EntityOcelot.class, new MobDrop[] {
				new MobDrop(10, new ItemStack(Items.FISH)),
				new MobDrop(10, new ItemStack(Items.STRING))
		}));
		REGISTRY.registerGrindable(new GrindableStandard(EntityWolf.class, new ItemStack(Items.BONE)));
		REGISTRY.registerGrindable(new GrindableZombiePigman());
		REGISTRY.registerGrindable(new GrindableEnderman());
		REGISTRY.registerGrindable(new GrindableSlime(EntitySlime.class, new ItemStack(Items.SLIME_BALL), 1));
		REGISTRY.registerGrindable(new GrindableSlime(EntityMagmaCube.class, new ItemStack(Items.MAGMA_CREAM), 1) {

			@Override
			protected boolean shouldDrop(EntitySlime slime) {

				return slime.getSlimeSize() <= dropSize;
			}
		});

		REGISTRY.registerSludgeDrop(50, new ItemStack(Blocks.SAND));
		REGISTRY.registerSludgeDrop(30, new ItemStack(Blocks.CLAY));
		REGISTRY.registerSludgeDrop(30, new ItemStack(Blocks.DIRT, 1, 1));
		REGISTRY.registerSludgeDrop(10, new ItemStack(Blocks.DIRT));
		REGISTRY.registerSludgeDrop(10, new ItemStack(Blocks.GRAVEL));
		REGISTRY.registerSludgeDrop(5, new ItemStack(Blocks.SAND, 1, 1));
		REGISTRY.registerSludgeDrop(5, new ItemStack(Blocks.SOUL_SAND));
		REGISTRY.registerSludgeDrop(3, new ItemStack(Blocks.MYCELIUM));
		REGISTRY.registerSludgeDrop(2, new ItemStack(Blocks.DIRT, 1, 2));
		REGISTRY.registerSludgeDrop(1, new ItemStack(Blocks.NETHERRACK));

		REGISTRY.registerMobEggHandler(new VanillaEggHandler());

		REGISTRY.registerRubberTreeBiome(Biomes.SWAMPLAND);
		REGISTRY.registerRubberTreeBiome(Biomes.MUTATED_SWAMPLAND);
		REGISTRY.registerRubberTreeBiome(Biomes.FOREST);
		REGISTRY.registerRubberTreeBiome(Biomes.MUTATED_FOREST);
		REGISTRY.registerRubberTreeBiome(Biomes.FOREST_HILLS);
		REGISTRY.registerRubberTreeBiome(Biomes.ROOFED_FOREST);
		REGISTRY.registerRubberTreeBiome(Biomes.MUTATED_ROOFED_FOREST);
		REGISTRY.registerRubberTreeBiome(Biomes.TAIGA);
		REGISTRY.registerRubberTreeBiome(Biomes.MUTATED_TAIGA);
		REGISTRY.registerRubberTreeBiome(Biomes.TAIGA_HILLS);
		REGISTRY.registerRubberTreeBiome(Biomes.COLD_TAIGA);
		REGISTRY.registerRubberTreeBiome(Biomes.MUTATED_TAIGA_COLD);
		REGISTRY.registerRubberTreeBiome(Biomes.COLD_TAIGA_HILLS);
		REGISTRY.registerRubberTreeBiome(Biomes.REDWOOD_TAIGA);
		REGISTRY.registerRubberTreeBiome(Biomes.MUTATED_REDWOOD_TAIGA);
		REGISTRY.registerRubberTreeBiome(Biomes.REDWOOD_TAIGA_HILLS);
		REGISTRY.registerRubberTreeBiome(Biomes.MUTATED_REDWOOD_TAIGA_HILLS);
		REGISTRY.registerRubberTreeBiome(Biomes.JUNGLE);
		REGISTRY.registerRubberTreeBiome(Biomes.MUTATED_JUNGLE);
		REGISTRY.registerRubberTreeBiome(Biomes.JUNGLE_HILLS);
		REGISTRY.registerRubberTreeBiome(Biomes.JUNGLE_EDGE);
		REGISTRY.registerRubberTreeBiome(Biomes.MUTATED_JUNGLE_EDGE);

		REGISTRY.registerSafariNetBlacklist(EntityDragon.class);
		REGISTRY.registerSafariNetBlacklist(EntityWither.class);

		REGISTRY.registerRandomMobProvider(new VanillaMobProvider());

		REGISTRY.registerLiquidDrinkHandler("water", new DrinkHandlerWater());
		REGISTRY.registerLiquidDrinkHandler("lava", new DrinkHandlerLava());

		REGISTRY.registerFruitLogBlock(Blocks.LOG);
		REGISTRY.registerFruitLogBlock(Blocks.CHORUS_FLOWER);
		REGISTRY.registerFruitLogBlock(Blocks.CHORUS_PLANT);
		REGISTRY.registerFruit(new FruitCocoa(Blocks.COCOA));
		REGISTRY.registerFruit(new FruitChorus(Blocks.CHORUS_FLOWER));
		REGISTRY.registerFruit(new FruitChorus(Blocks.CHORUS_PLANT));

		REGISTRY.registerSpawnHandler(new SpawnableEnderman());
		for (Class<? extends AbstractHorse> clazz : new Class[] {
				EntitySkeletonHorse.class,
				EntityZombieHorse.class,
				EntityDonkey.class,
				EntityLlama.class,
				EntityHorse.class,
				EntityMule.class
		}) {
			REGISTRY.registerSpawnHandler(new SpawnableHorse(clazz));
		}
	}

	@Override
	public void postLoad() {

		//@formatter:off
		registerOreDictLaserOre(175, "Coal",               black, false);
		registerOreDictLaserOre(150, "Iron",               brown, false);
		registerOreDictLaserOre(100, "Redstone",             red, false);
		registerOreDictLaserOre(100, "Nikolite",       lightBlue, false);
		registerOreDictLaserOre(100, "oreSalt",            white, "oreNetherSalt");
		registerOreDictLaserOre( 90, "Copper",            orange, false);
		registerOreDictLaserOre( 85, "Tin",               silver, false);
		registerOreDictLaserOre( 85, "oreCheese",         yellow, null);
		registerOreDictLaserOre( 85, "Force",             yellow,  true);
		registerOreDictLaserOre( 80, "glowstone",         yellow, null);
		registerOreDictLaserOre( 80, "Lapis",               blue,  true);
		registerOreDictLaserOre( 70, "Gold",              yellow, false);
		registerOreDictLaserOre( 70, "oreQuartz",          white, null);
		registerOreDictLaserOre( 60, "Lead",              purple, false);
		registerOreDictLaserOre( 60, "oreZinc",            white, "oreSphalerite", orange);
		registerOreDictLaserOre( 60, "NaturalAluminum",    white, false);
		registerOreDictLaserOre( 60, "Aluminium",          white, false);
		registerOreDictLaserOre( 60, "Aluminum",           white, false);
		registerOreDictLaserOre( 60, "oreDark",            black,  true);
		registerOreDictLaserOre( 60, "oreSodalite",         blue, null);
		registerOreDictLaserOre( 55, "Mithril",             blue, false);
		registerOreDictLaserOre( 55, "Steel",               gray, false);
		registerOreDictLaserOre( 55, "oreCassiterite",     black, null);
		registerOreDictLaserOre( 55, "Diamond",        lightBlue,  true);
		registerOreDictLaserOre( 55, "oreDesh",             gray, null);
		registerOreDictLaserOre( 50, "CertusQuartz",        cyan,  true);
		registerOreDictLaserOre( 50, "Osmium",         lightBlue, false);
		registerOreDictLaserOre( 50, "oreBauxite",         brown, null);
		registerOreDictLaserOre( 45, "Rutile",             black, false);
		registerOreDictLaserOre( 45, "Titanium",           black, false);
		registerOreDictLaserOre( 45, "Tungsten",           black, false);
		registerOreDictLaserOre( 45, "oreTungstate",       black, "oreNetherTungsten");
		registerOreDictLaserOre( 45, "orePyrite",         orange, null);
		registerOreDictLaserOre( 45, "FzDarkIron",        purple, false);
		registerOreDictLaserOre( 40, "Tennantite",          lime, false);
		registerOreDictLaserOre( 40, "Nickel",            silver, false);
		registerOreDictLaserOre( 40, "Sulfur",            yellow, false);
		registerOreDictLaserOre( 40, "Saltpeter",          white, false);
		registerOreDictLaserOre( 35, "Emerald",             lime,  true);
		registerOreDictLaserOre( 35, "Ruby",                 red,  true);
		registerOreDictLaserOre( 35, "Sapphire",            blue,  true);
		registerOreDictLaserOre( 35, "GreenSapphire",      green,  true);
		registerOreDictLaserOre( 35, "Peridot",            green,  true);
		registerOreDictLaserOre( 35, "Topaz",              brown,  true);
		registerOreDictLaserOre( 35, "Tanzanite",         purple,  true);
		registerOreDictLaserOre( 35, "Malachite",           cyan,  true);
		registerOreDictLaserOre( 35, "Amber",             orange,  true);
		registerOreDictLaserOre( 30, "Adamantium",         green, false);
		registerOreDictLaserOre( 30, "Silver",              gray, false);
		registerOreDictLaserOre( 30, "Galena",            purple, false);
		registerOreDictLaserOre( 30, "Apatite",             blue,  true);
		registerOreDictLaserOre( 30, "Silicon",            black, false);
		registerOreDictLaserOre( 25, "Magnesium",         silver, false);
		registerOreDictLaserOre( 25, "Amethyst",         magenta,  true);
		registerOreDictLaserOre( 20, "Uranium",             lime, false);
		registerOreDictLaserOre( 20, "orePitchblende",     black, "oreNetherUranium", lime);
		registerOreDictLaserOre( 20, "oreFirestone",         red, null);
		registerOreDictLaserOre( 20, "MonazitOre",         green, null);
		registerOreDictLaserOre( 15, "Cinnabar",             red,  true);
		registerOreDictLaserOre( 15, "Adamantine",           red, false);
		registerOreDictLaserOre( 15, "Platinum",       lightBlue, false);
		registerOreDictLaserOre( 15, "oreCooperite",      yellow, "oreNetherPlatinum", lightBlue);
		registerOreDictLaserOre( 10, "oreArdite",         orange, null);
		registerOreDictLaserOre( 10, "oreCobalt",           blue, null);
		registerOreDictLaserOre( 10, "Yellorite",         yellow, false);
		registerOreDictLaserOre(  5, "Iridium",            white, false);

		// rarity/usefulness unknown
		registerOreDictLaserOre( 20, "oreTetrahedrite", orange, null);
		registerOreDictLaserOre( 20, "oreCadmium", lightBlue, null);
		registerOreDictLaserOre( 20, "oreIndium", silver, null);
		registerOreDictLaserOre( 20, "oreAmmonium", white, null);
		registerOreDictLaserOre( 20, "oreCalcite", orange, null);
		registerOreDictLaserOre( 20, "oreMagnetite", black, null);
		// focus also unknown
		registerOreDictLaserOre( 20, "oreManganese", pink, null);
		registerOreDictLaserOre( 20, "oreMeutoite", pink, null);
		registerOreDictLaserOre( 20, "oreEximite", pink, null);
		registerOreDictLaserOre( 20, "oreAtlarus", pink, null);
		registerOreDictLaserOre( 20, "oreOrichalcum", pink, null);
		registerOreDictLaserOre( 20, "oreRubracium", pink, null);
		registerOreDictLaserOre( 20, "oreCarmot", pink, null);
		registerOreDictLaserOre( 20, "oreAstralSilver", pink, null);
		registerOreDictLaserOre( 20, "oreOureclase", pink, null);
		registerOreDictLaserOre( 20, "oreInfuscolium", pink, null);
		registerOreDictLaserOre( 20, "oreDeepIron", pink, null);
		registerOreDictLaserOre( 20, "orePrometheum", pink, null);
		registerOreDictLaserOre( 20, "oreSanguinite", pink, null);
		registerOreDictLaserOre( 20, "oreVulcanite", pink, null);
		registerOreDictLaserOre( 20, "oreKalendrite", pink, null);
		registerOreDictLaserOre( 20, "oreAlduorite", pink, null);
		registerOreDictLaserOre( 20, "oreCeruclase", pink, null);
		registerOreDictLaserOre( 20, "oreVyroxeres", pink, null);
		registerOreDictLaserOre( 20, "oreMidasium", pink, null);
		registerOreDictLaserOre( 20, "oreLemurite", pink, null);
		registerOreDictLaserOre( 20, "oreShadowIron", pink, null);
		registerOreDictLaserOre( 20, "oreIgnatius", pink, null);
		registerOreDictLaserOre( 20, "orePotash", pink, null);
		registerOreDictLaserOre( 20, "oreBitumen", pink, null);
		registerOreDictLaserOre( 20, "orePhosphorite", pink, null);
		//@formatter:on
	}

	private void registerOreDictLaserOre(int weight, String suffix, int focus, boolean isGem) {

		registerOreDictLaserOre(weight, "ore" + suffix, focus, "oreNether" + suffix, focus);
	}

	private void registerOreDictLaserOre(int weight, String name, int focus, String netherName) {

		registerOreDictLaserOre(weight, name, focus, netherName, focus);
	}

	private void registerOreDictLaserOre(int weight, String name, int focus, String netherName, int netherFocus) {

		for (@Nonnull ItemStack ore : OreDictionary.getOres(name))
			if (!ore.isEmpty()) {
				ore = ore.copy();
				ore.setCount(1);
				MFRRegistry.registerLaserOre(weight, ore);
				if (focus >= 0)
					MFRRegistry.addLaserPreferredOre(focus, ore);
				if (netherName != null) {
					registerOreDictLaserOre(weight / 2, netherName, netherFocus, null);
				}
				return;
			}
		if (netherName != null)
			for (@Nonnull ItemStack ore : OreDictionary.getOres(netherName))
				if (!ore.isEmpty()) {
					registerOreDictLaserOre(weight / 2, netherName, netherFocus, null);
					return;
				}
	}

	private static final int black = 15;
	private static final int red = 14;
	private static final int green = 13;
	private static final int brown = 12;
	private static final int blue = 11;
	private static final int purple = 10;
	private static final int cyan = 9;
	private static final int silver = 8;
	private static final int gray = 7;
	@SuppressWarnings("unused")
	private static final int pink = 6;
	private static final int lime = 5;
	private static final int yellow = 4;
	private static final int lightBlue = 3;
	private static final int magenta = 2;
	private static final int orange = 1;
	private static final int white = 0;
}
