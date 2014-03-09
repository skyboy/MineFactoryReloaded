package powercrystals.minefactoryreloaded.modhelpers.thaumcraft;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.GameRegistry;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.oredict.OreDictionary;

import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.api.FertilizerType;
import powercrystals.minefactoryreloaded.api.HarvestType;
import powercrystals.minefactoryreloaded.farmables.fertilizables.FertilizableCocoa;
import powercrystals.minefactoryreloaded.farmables.fruits.FruitCocoa;
import powercrystals.minefactoryreloaded.farmables.harvestables.HarvestableCocoa;
import powercrystals.minefactoryreloaded.farmables.harvestables.HarvestableStandard;
import powercrystals.minefactoryreloaded.farmables.harvestables.HarvestableWood;
import powercrystals.minefactoryreloaded.farmables.plantables.PlantableCocoa;
import powercrystals.minefactoryreloaded.setup.MFRConfig;
import powercrystals.minefactoryreloaded.setup.Machine;

@Mod(modid = "MineFactoryReloaded|CompatThaumcraft", name = "MFR Compat: Thaumcraft", version = MineFactoryReloadedCore.version, dependencies = "after:MineFactoryReloaded;after:Thaumcraft")
@NetworkMod(clientSideRequired = false, serverSideRequired = false)
public class Thaumcraft
{
	@EventHandler
	public static void load(FMLPostInitializationEvent e)
	{
		if(!Loader.isModLoaded("Thaumcraft"))
		{
			FMLLog.warning("Thaumcraft missing - MFR Thaumcraft Compat not loading");
			return;
		}
		
		try
		{
			final Block tcSapling = GameRegistry.findBlock("Thaumcraft", "blockCustomPlant");
			final Block tcLog = GameRegistry.findBlock("Thaumcraft", "blockMagicalLog");
			final Block tcLeaves = GameRegistry.findBlock("Thaumcraft", "blockMagicalLeaves");
			final Block tcFibres = GameRegistry.findBlock("Thaumcraft", "blockTaintFibres");
			final Block tcPod = GameRegistry.findBlock("Thaumcraft", "blockManaPod");
			final Item tcBean = GameRegistry.findItem("Thaumcraft", "ItemManaBean");
			Class<?> golem = Class.forName("thaumcraft.common.entities.golems.EntityGolemBase");
			Class<?> trunk = Class.forName("thaumcraft.common.entities.golems.EntityTravelingTrunk");
			Class<?> pech = Class.forName("thaumcraft.common.entities.monster.EntityPech");
			
			MFRRegistry.registerAutoSpawnerBlacklistClass(golem);
			MFRRegistry.registerAutoSpawnerBlacklistClass(trunk);
			
			MFRRegistry.registerSpawnHandler(new SpawnablePech(pech));
			
			MFRRegistry.registerGrinderBlacklist(golem);
			MFRRegistry.registerGrinderBlacklist(trunk);
			
			if (MFRConfig.conveyorNeverCapturesTCGolems.getBoolean(false))
			{
				MFRRegistry.registerConveyerBlacklist(golem);
				MFRRegistry.registerConveyerBlacklist(trunk);
			}
			
			MFRRegistry.registerHarvestable(new HarvestableWood(tcLog.blockID));
			MFRRegistry.registerHarvestable(new HarvestableStandard(tcFibres.blockID, HarvestType.Normal));
			MFRRegistry.registerHarvestable(new HarvestableThaumcraftLeaves(tcLeaves.blockID, tcSapling.blockID));
			MFRRegistry.registerHarvestable(new HarvestableThaumcraftPlant(tcSapling.blockID));
			MFRRegistry.registerHarvestable(new HarvestableCocoa(tcPod.blockID));
			
			MFRRegistry.registerPlantable(new PlantableThaumcraftTree(tcSapling.blockID, tcSapling.blockID));
			MFRRegistry.registerPlantable(new PlantableCocoa(tcBean.itemID, tcPod.blockID) {
				@Override
				protected boolean isNextToAcceptableLog(World world, int x, int y, int z)
				{
					boolean isMagic = false;
					BiomeGenBase biome = world.getBiomeGenForCoords(x, z);
					if (biome != null)
						isMagic = BiomeDictionary.isBiomeOfType(biome, BiomeDictionary.Type.MAGICAL);
					return isMagic &&
							isGoodLog(world, x+1, y, z) ||
							isGoodLog(world, x-1, y, z) ||
							isGoodLog(world, x, y, z+1) ||
							isGoodLog(world, x, y, z-1);
				}
				
				@Override
				protected boolean isGoodLog(World world, int x, int y, int z)
				{
					int id = world.getBlockId(x, y, z);
					return id == Block.wood.blockID | id == tcLog.blockID;
				}
			});
			
			MFRRegistry.registerFruitLogBlockId(tcLog.blockID);
			MFRRegistry.registerFruit(new FruitCocoa(tcPod.blockID));
			
			MFRRegistry.registerFertilizable(new FertilizableCocoa(tcPod.blockID, FertilizerType.GrowMagicalCrop));
			MFRRegistry.registerFertilizable(new FertilizableTCSapling(tcSapling));
			
			Class<?> Aspect = Class.forName("thaumcraft.api.aspects.Aspect");
			aspects = (LinkedHashMap<String, ? extends Object>)Aspect.
					getDeclaredField("aspects").get(null);
			Class<?> ThaumcraftApi = Class.forName("thaumcraft.api.ThaumcraftApi");
			AspectList = Class.forName("thaumcraft.api.aspects.AspectList");
			registerItem = ThaumcraftApi.getDeclaredMethod("registerObjectTag",
					int.class, int.class, AspectList);
			registerEntity = ThaumcraftApi.getDeclaredMethod("registerEntityTag",
					String.class, AspectList, NBTBase[].class);
			addAspect = AspectList.getDeclaredMethod("add", Aspect, int.class);
			newAspectList = AspectList.getDeclaredConstructor(int.class, int.class);
			
			doAspects();
		}
		catch(Throwable x)
		{
			x.printStackTrace();
		}
	}
	
	private static LinkedHashMap<String, ? extends Object> aspects = null;
	private static Method registerItem = null;
	private static Method registerEntity = null;
	private static Class<?> AspectList = null;
	private static Constructor<?> newAspectList = null;
	private static Method addAspect = null;
	
	private static void parseAspects(ItemStack item, String toadd, boolean craftedAspects) throws Throwable
	{
		Object aspectList;
		if (craftedAspects)
			aspectList = newAspectList.newInstance(item.itemID, item.getItemDamage());
		else
			aspectList = AspectList.newInstance();
		if (!toadd.trim().isEmpty())
		{
			String[] list = toadd.split(",");
			for (int i = list.length; i --> 0; )
			{
				String[] temp = list[i].trim().split(" ");
				temp[1] = temp[1].toLowerCase();
				if (aspects.containsKey(temp[1]))
					addAspect.invoke(aspectList, aspects.get(temp[1]), Integer.parseInt(temp[0], 10));
				else
					FMLLog.severe("%s aspect missing.", temp[1]);
			}
		}
		registerItem.invoke(null, item.itemID, item.getItemDamage(), aspectList);
	}
	
	private static void parseAspects(String entity, String toadd) throws Throwable
	{
		String[] list = toadd.split(",");
		Object aspectList = AspectList.newInstance();
		for (int i = list.length; i --> 0; )
		{
			String[] temp = list[i].trim().split(" ");
			temp[1] = temp[1].toLowerCase();
			if (aspects.containsKey(temp[1]))
				addAspect.invoke(aspectList, aspects.get(temp[1]), Integer.parseInt(temp[0], 10));
		}
		registerEntity.invoke(null, entity, aspectList, null);
	}
	
	private static void parseAspects(Item item, String toadd) throws Throwable
	{
		parseAspects(item, OreDictionary.WILDCARD_VALUE, toadd, true);
	}
	
	private static void parseAspects(Item item, int meta, String toadd, boolean craftedAspects) throws Throwable
	{
		parseAspects(new ItemStack(item, 1, meta), toadd, craftedAspects);
	}
	
	private static void parseAspects(Block item, int meta, String toadd) throws Throwable
	{
		parseAspects(new ItemStack(item, 1, meta), toadd, true);
	}
	
	private static void parseAspects(Block item, String toadd) throws Throwable
	{
		parseAspects(item, OreDictionary.WILDCARD_VALUE, toadd);
	}
	
	private static void parseAspects(Block item, int meta, String toadd, boolean craftedAspects) throws Throwable
	{
		parseAspects(new ItemStack(item, 1, meta), toadd, craftedAspects);
	}
	
	private static void parseAspects(Block item, String toadd, boolean craftedAspects) throws Throwable
	{
		parseAspects(item, OreDictionary.WILDCARD_VALUE, toadd, craftedAspects);
	}
	
	private static void parseAspects(Machine item, String toadd) throws Throwable
	{
		parseAspects(new ItemStack(item.getBlockId(), 1, item.getMeta()), toadd, true);
	}
	
	private static void doAspects() throws Throwable
	{
		parseAspects(Machine.AutoAnvil, "3 Permutatio, 5 Fabrico, 10 Metallum, 5 Machina");
		parseAspects(Machine.AutoBrewer, "4 ignis, 2 Fabrico, 2 Aqua, 2 praecantatio, 5 Machina");
		parseAspects(Machine.AutoDisenchanter, "4 praecantatio, 4 Permutatio, 5 Machina");
		parseAspects(Machine.AutoEnchanter, "8 praecantatio, 4 Cognitio, 4 fabrico, 5 Machina");
		parseAspects(Machine.AutoJukebox, "4 Sensus, 4 Aer, 5 Machina");
		parseAspects(Machine.AutoSpawner, "4 bestia, 4 exanimis, 7 praecantatio, 4 alienis, 5 Machina, 10 permutatio");
		parseAspects(Machine.BioFuelGenerator, "5 Potentia, 3 Herba, 5 Machina, 3 permutatio");
		parseAspects(Machine.BioReactor, "4 Herba, 2 Potentia, 5 Machina, 5 permutatio");
		parseAspects(Machine.BlockBreaker, "15 Perfodio, 5 Machina, 5 metallum, 3 Lucrum");
		parseAspects(Machine.BlockPlacer, "1 motus, 1 ordo, 5 Machina, 5 metallum, 3 Lucrum");
		parseAspects(Machine.BlockSmasher, "5 Perditio, 5 Machina, 3 permutatio, 3 praecantatio");
		parseAspects(Machine.Breeder, "2 bestia, 2 fames, 5 Machina");
		parseAspects(Machine.Chronotyper, "3 Tempus, 3 bestia, 5 Machina, 3 Sensus");
		parseAspects(Machine.Composter, "2 bestia, 2 Aqua, 5 Machina");
		parseAspects(Machine.DeepStorageUnit, "4971027 vacuos, 5 Machina, 5 Alienis");
		parseAspects(Machine.Ejector, "4 Motus, 5 Machina");
		parseAspects(Machine.EnchantmentRouter, "1 Motus, 4 iter, 2 Sensus, 5 Machina, 1 praecantatio");
		parseAspects(Machine.Fertilizer, "6 Herba, 5 Machina, 1 Vitreus, 3 Victus");
		parseAspects(Machine.Fisher, "3 Aqua, 5 Machina, 4 metallum, 2 Instrumentum");
		parseAspects(Machine.FruitPicker, "2 Herba, 4 meto, 5 Machina");
		parseAspects(Machine.Grinder, "10 Telum, 6 mortuus, 7 Meto, 5 Machina, 4 metallum, 2 Lucrum");
		parseAspects(Machine.ItemCollector, "4 vacuos, 5 Machina, 2 Arbor, 4 Motus");
		parseAspects(Machine.ItemRouter, "2 Motus, 4 iter, 2 Sensus,  5 Machina");
		parseAspects(Machine.LaserDrill, "5 Perfodio, 15 Lux, 5 Machina, 4 Victus");
		parseAspects(Machine.LaserDrillPrecharger, "4 Lux, 5 Machina, 15 Potentia, 2 victus");
		parseAspects(Machine.LavaFabricator, "4 ignis, 4 Saxum, 4 Fabrico, 5 Machina");
		parseAspects(Machine.LiquiCrafter, "5 Aqua, 5 Fabrico, 5 Machina");
		parseAspects(Machine.LiquidRouter, "1 Motus, 4 iter, 2 Sensus, 5 Machina, 1 Aqua");
		parseAspects(Machine.MeatPacker, "2 Ordo, 2 Corpus, 2 fames, 5 Machina");
		//parseAspects(Machine.OilFabricator, "Not Sure What Aspects Oil Is Getting.");
		parseAspects(Machine.Planter, "4 Herba, 2 Granum, 4 messis, 5 Machina");
		parseAspects(Machine.Rancher, "6 Meto, 5 Machina, 4 metallum, 2 Instrumentum");
		parseAspects(Machine.RedNote, "4 Aer, 4 Sensus, 5 Machina");
		parseAspects(Machine.Sewer, "1 Venenum, 3 Aqua 5 Machina, 4 bestia");
		parseAspects(Machine.Slaughterhouse, "12 Telum, 10 mortuus, 5 Machina, 12 metallum, 6 Lucrum");
		parseAspects(Machine.SludgeBoiler, "3 Venenum, 3 Terra, 2 Aqua, 5 Machina, 2 ignis");
		parseAspects(Machine.Unifier, "5 Ordo, 2 Alienis, 5 Machina");
		parseAspects(Machine.Vet, "4 Sano, 5 Machina, 4 bestia");
		parseAspects(Machine.WeatherCollector, " 4 vacuos, 5 Machina, 5 metallum, 4 Tempestas");

		parseAspects("mfrEntityPinkSlime", "1 Aqua, 2 Limus, 1 Corpus, 1 Bestia");
		parseAspects(MineFactoryReloadedCore.bioFuelBucketItem, "2 Herba, 1 Potentia, 1 Aqua, 8 metallum, 1 vacuos");
		parseAspects(MineFactoryReloadedCore.biofuelLiquid, "4 Herba, 2 Potentia, 2 Aqua");
		parseAspects(MineFactoryReloadedCore.blankRecordItem, "4 Sensus, 4 Aer, 4 Lucrum, 4 vacuos");
		parseAspects(MineFactoryReloadedCore.ceramicDyeItem, "1 Terra, 1 Aqua, 1 Sensus");
		parseAspects(MineFactoryReloadedCore.chocolateMilkBucketItem, "2 Fames, 1 Motus, 1 Potentia, 2 Aqua, 8 Metallum, 1 vacuos");
		parseAspects(MineFactoryReloadedCore.chocolateMilkLiquid, "4 Fames, 2 Motus, 2 Potentia, 4 Aqua");
		parseAspects(MineFactoryReloadedCore.conveyorBlock, "3 Motus, 1 iter, 1 Machina");
		parseAspects(MineFactoryReloadedCore.essenceLiquid, "4 praecantatio, 2 cognitio, 2 Aqua");
		parseAspects(MineFactoryReloadedCore.factoryGlassBlock, "1 Vitreus, 1 Sensus");
		parseAspects(MineFactoryReloadedCore.factoryHammerItem, "1 Instrumentum, 2 Fabrico, 2 ignis, 3 Ordo");
		parseAspects(MineFactoryReloadedCore.fertilizerItem, "1 Granum, 1 Herba, 1 Messis, 1 sensus");
		parseAspects(MineFactoryReloadedCore.laserFocusItem, "1 Ordo, 1 Vitreus, 4 Lucrum");
		parseAspects(MineFactoryReloadedCore.machineBaseItem, "2 Fabrico, 2 Machina, 1 Saxum");
		parseAspects(MineFactoryReloadedCore.meatBucketItem, "3 Corpus, 1 bestia, 1 Aqua, 8 metallum, 1 vacuos");
		parseAspects(MineFactoryReloadedCore.meatIngotCookedItem, "3 Corpus, 2 fames, 1 Ignis");
		parseAspects(MineFactoryReloadedCore.meatIngotRawItem, "3 Corpus, 2 fames, 1 bestia");
		parseAspects(MineFactoryReloadedCore.meatLiquid, "6 Corpus, 2 Aqua, 2 bestia");
		parseAspects(MineFactoryReloadedCore.meatNuggetCookedItem, "1 fames");
		parseAspects(MineFactoryReloadedCore.meatNuggetRawItem, "1 Corpus");
		parseAspects(MineFactoryReloadedCore.milkBottleItem, "1 Fames, 1 Sano, 1 Victus, 1 Vitreus");
		parseAspects(MineFactoryReloadedCore.milkLiquid, "4 Fames, 4 Sano, 4 Aqua, 2 Victus");
		parseAspects(MineFactoryReloadedCore.mobEssenceBucketItem, "2 praecantatio, 1 cognitio, 1 Aqua, 8 metallum, 1 vacuos");
		parseAspects(MineFactoryReloadedCore.mushroomSoupBucketItem, "2 fames, 2 Herba, 8 metallum, 1 vacuos");
		parseAspects(MineFactoryReloadedCore.mushroomSoupLiquid, "4 fames, 4 Herba, 1 Aqua");
		parseAspects(MineFactoryReloadedCore.pinkSlimeballItem, "1 Limus, 1 Corpus");
		parseAspects(MineFactoryReloadedCore.pinkSlimeBucketItem, "2 Limus, 2 Corpus, 1 Aqua, 8 metallum, 1 vacuos");
		parseAspects(MineFactoryReloadedCore.pinkSlimeLiquid, "4 Limus, 4 Corpus, 2 Aqua");
		parseAspects(MineFactoryReloadedCore.plasticSheetItem, "1 Fabrico, 1 ignis, 2 Ordo");
		parseAspects(MineFactoryReloadedCore.portaSpawnerItem, "8 Alienis, 4 bestia, 4 exanimis, 4 iter, 8 praecantatio, 8 Permutatio");
		parseAspects(MineFactoryReloadedCore.rawPlasticItem, "1 Fabrico, 1 ignis, 1 Ordo, 1 Perditio");
		parseAspects(MineFactoryReloadedCore.rawRubberItem, "1 Limus, 2 Arbor");
		parseAspects(MineFactoryReloadedCore.rednetCableBlock, "1 cognitio, 1 Machina", true);
		parseAspects(MineFactoryReloadedCore.rednetLogicBlock, "15 cognitio, 5 Machina", true);
		parseAspects(MineFactoryReloadedCore.rednetMemoryCardItem, "3 cognitio, 1 Machina");
		parseAspects(MineFactoryReloadedCore.rednetMeterItem, "1 Instrumentum, 1 Sensus, 1 Machina");
		parseAspects(MineFactoryReloadedCore.rednetPanelBlock, "2 Sensus, 2 cognitio, 2 Machina");
		parseAspects(MineFactoryReloadedCore.rubberBarItem, "1 Motus, 1 Arbor, 1 ignis");
		parseAspects(MineFactoryReloadedCore.rubberLeavesBlock, "1 Herba");
		parseAspects(MineFactoryReloadedCore.rubberSaplingBlock, "1 Arbor, 1 Herba, 1 Granum");
		parseAspects(MineFactoryReloadedCore.rubberWoodBlock, "3 Arbor, 1 Limus");
		parseAspects(MineFactoryReloadedCore.rulerItem, "1 Instrumentum, 1 Sensus");
		parseAspects(MineFactoryReloadedCore.safariNetItem, "4 Spiritus, 8 Alienis, 8 iter, 4 praecantatio, 8 Vinculum, 4 Fabrico");
		parseAspects(MineFactoryReloadedCore.safariNetJailerItem, "10 Vinculum, 1 praecantatio, 4 spiritus, 2 metallum, 1 Fabrico");
		parseAspects(MineFactoryReloadedCore.safariNetLauncherItem, "2 Volatus, 2 Instrumentum");
		parseAspects(MineFactoryReloadedCore.safariNetSingleItem, "4 Vinculum, 4 spiritus, 2 Instrumentum");
		parseAspects(MineFactoryReloadedCore.sewageBucketItem, "2 Venenum, 1 bestia, 1 Aqua, 8 metallum, 1 vacuos");
		parseAspects(MineFactoryReloadedCore.sewageLiquid, "4 Venenum, 2 bestia, 2 Aqua");
		parseAspects(MineFactoryReloadedCore.sludgeBucketItem, "2 Venenum, 1 Terra, 1 Aqua, 1 Vitium, 8 metallum, 1 vacuos");
		parseAspects(MineFactoryReloadedCore.sludgeLiquid, "4 Venenum, 2 Terra, 2 Aqua, 1 Vitium");
		parseAspects(MineFactoryReloadedCore.spyglassItem, "2 Victus, 6 Sensus");
		parseAspects(MineFactoryReloadedCore.strawItem, "1 vacuos, 4 Aqua, 4 fames, 1 Instrumentum");
		parseAspects(MineFactoryReloadedCore.sugarCharcoalItem, "2 Potentia, 2 ignis");
		parseAspects(MineFactoryReloadedCore.syringeCureItem, "2 Sano, 1 Exanimis, 1 Humanus, 1 Instrumentum");
		parseAspects(MineFactoryReloadedCore.syringeEmptyItem, "1 vacuos, 1 Sano, 1 Instrumentum");
		parseAspects(MineFactoryReloadedCore.syringeGrowthItem, "1 tempus, 2 Sano, 1 Instrumentum");
		parseAspects(MineFactoryReloadedCore.syringeHealthItem, "2 Sano, 1 Instrumentum");
		parseAspects(MineFactoryReloadedCore.syringeSlimeItem, "1 Sano, 1 Limus, 1 Instrumentum");
		parseAspects(MineFactoryReloadedCore.syringeZombieItem, "1 tempus, 1 Sano, 1 Exanimis, 1 Instrumentum");
		parseAspects(MineFactoryReloadedCore.vineScaffoldBlock, "1 Herba, 1 Fabrico");
		parseAspects(MineFactoryReloadedCore.xpExtractorItem, "1 praecantatio, 1 Permutatio, 1 vacuos, 1 Instrumentum, 1 Meto");
		//parseAspects(Tracks, "Tracks Currently Have No Aspects.");
		
		Item item = MineFactoryReloadedCore.upgradeItem;
		
		parseAspects(item, "2 cognitio");
		for (int i = 0, n = 10; i <= n; ++i)
			parseAspects(item, i, "2 cognitio", true);
		parseAspects(MineFactoryReloadedCore.logicCardItem, 0, "4 Cognitio", true);
		parseAspects(MineFactoryReloadedCore.logicCardItem, 1, "7 Cognitio", true);
		parseAspects(MineFactoryReloadedCore.logicCardItem, 2, "10 Cognitio", true);
		
		parseAspects(MineFactoryReloadedCore.factoryDecorativeBrickBlock,  0, "2 gelum, 1 terra"); // ice
		parseAspects(MineFactoryReloadedCore.factoryDecorativeBrickBlock,  1, "1 terra, 3 Lux, 2 sensus"); // glowstone
		parseAspects(MineFactoryReloadedCore.factoryDecorativeBrickBlock,  2, "2 Terra, 4 sensus"); // lapis
		parseAspects(MineFactoryReloadedCore.factoryDecorativeBrickBlock,  3, "1 Terra, 3 ignis, 1 Saxum, 1 tenebrae"); // obsidian
		parseAspects(MineFactoryReloadedCore.factoryDecorativeBrickBlock,  4, "2 Terra, 1 Saxum"); // paved stone
		parseAspects(MineFactoryReloadedCore.factoryDecorativeBrickBlock,  5, "1 gelum, 1 terra"); // snow
		parseAspects(MineFactoryReloadedCore.factoryDecorativeBrickBlock,  6, "1 saxum, 3 Lux, 2 Sensus"); // glowstone_large
		parseAspects(MineFactoryReloadedCore.factoryDecorativeBrickBlock,  7, "2 gelum, 1 saxum"); // ice_large
		parseAspects(MineFactoryReloadedCore.factoryDecorativeBrickBlock,  8, "2 saxum, 4 sensus"); // lapis_large
		parseAspects(MineFactoryReloadedCore.factoryDecorativeBrickBlock,  9, "3 ignis, 2 Saxum, 1 tenebrae"); // obsidian large
		parseAspects(MineFactoryReloadedCore.factoryDecorativeBrickBlock, 10, "1 gelum, 1 saxum"); // snow_large
		//parseAspects(MineFactoryReloadedCore.factoryDecorativeBrickBlock, 11, ""); // PRC housing (below)
		parseAspects(MineFactoryReloadedCore.factoryDecorativeBrickBlock, 12, "3 Corpus, 2 fames, 1 bestia", true); // raw meat block
		parseAspects(MineFactoryReloadedCore.factoryDecorativeBrickBlock, 13, "3 Corpus, 2 fames, 1 Ignis", true); // cooked meat block
		parseAspects(MineFactoryReloadedCore.factoryDecorativeStoneBlock, 0, "2 Saxum, 1 tenebrae");
		parseAspects(MineFactoryReloadedCore.factoryDecorativeStoneBlock, 1, "2 Saxum, 1 Victus");
		parseAspects(MineFactoryReloadedCore.factoryDecorativeStoneBlock, 2, "1 perditio, 1 Saxum, 1 tenebrae");
		parseAspects(MineFactoryReloadedCore.factoryDecorativeStoneBlock, 3, "1 perditio, 1 Saxum, 1 Victus");
		parseAspects(MineFactoryReloadedCore.factoryDecorativeStoneBlock, 4, "2 Saxum, 1 tenebrae");
		parseAspects(MineFactoryReloadedCore.factoryDecorativeStoneBlock, 5, "2 Saxum, 1 Victus");
		parseAspects(MineFactoryReloadedCore.factoryDecorativeStoneBlock, 6, "2 Terra, 1 Saxum, 1 tenebrae");
		parseAspects(MineFactoryReloadedCore.factoryDecorativeStoneBlock, 7, "2 Terra, 1 Saxum, 1 Victus");
		parseAspects(MineFactoryReloadedCore.factoryDecorativeStoneBlock, 8, "1 Terra, 1 Saxum, 1 tenebrae");
		parseAspects(MineFactoryReloadedCore.factoryDecorativeStoneBlock, 9, "1 Terra, 1 Saxum, 1 Victus");
		parseAspects(MineFactoryReloadedCore.factoryDecorativeStoneBlock, 10, "3 Saxum, 1 tenebrae");
		parseAspects(MineFactoryReloadedCore.factoryDecorativeStoneBlock, 11, "3 Saxum, 1 Victus");
		//parseAspects(MineFactoryReloadedCore.factoryDecorativeStoneBlock, 12, "2 Saxum, 1 tenebrae");
		//parseAspects(MineFactoryReloadedCore.factoryDecorativeStoneBlock, 13, "2 Saxum, 1 Victus");

		parseAspects(MineFactoryReloadedCore.factoryDecorativeBrickBlock, 11, "1 cognitio, 3 Machina", true);
		parseAspects(MineFactoryReloadedCore.factoryRoadBlock, 0, "1 iter, 1 Saxum, 1 sensus");
		parseAspects(MineFactoryReloadedCore.factoryRoadBlock, 1, "1 iter, 1 Saxum, 1 sensus, 3 Lux"); // road light (off)
		parseAspects(MineFactoryReloadedCore.factoryRoadBlock, 2, "1 iter, 1 Saxum, 1 sensus, 3 Lux"); // road light (on)
		parseAspects(MineFactoryReloadedCore.factoryRoadBlock, 3, "1 iter, 1 Saxum, 1 sensus, 3 Lux"); // road light inverted (off)
		parseAspects(MineFactoryReloadedCore.factoryRoadBlock, 4, "1 iter, 1 Saxum, 1 sensus, 3 Lux"); // road light inverted (on)
		
	}
}