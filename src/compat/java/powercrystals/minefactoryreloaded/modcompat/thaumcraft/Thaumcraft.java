/*
package powercrystals.minefactoryreloaded.modhelpers.thaumcraft;

import static cofh.api.modhelpers.ThaumcraftHelper.parseAspects;
import static powercrystals.minefactoryreloaded.setup.MFRThings.*;

import cofh.asm.relauncher.Strippable;
import cofh.mod.ChildMod;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.CustomProperty;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.api.FertilizerType;
import powercrystals.minefactoryreloaded.api.HarvestType;
import powercrystals.minefactoryreloaded.farmables.fertilizables.FertilizableCocoa;
import powercrystals.minefactoryreloaded.farmables.fertilizables.FertilizableCropPlant;
import powercrystals.minefactoryreloaded.farmables.fruits.FactoryFruitStandard;
import powercrystals.minefactoryreloaded.farmables.fruits.FruitCocoa;
import powercrystals.minefactoryreloaded.farmables.harvestables.HarvestableCocoa;
import powercrystals.minefactoryreloaded.farmables.harvestables.HarvestableStandard;
import powercrystals.minefactoryreloaded.farmables.harvestables.HarvestableWood;
import powercrystals.minefactoryreloaded.farmables.plantables.PlantableCocoa;
import powercrystals.minefactoryreloaded.farmables.plantables.PlantableStandard;
import powercrystals.minefactoryreloaded.setup.MFRConfig;
import powercrystals.minefactoryreloaded.setup.Machine;

@ChildMod(parent = MineFactoryReloadedCore.modId, mod = @Mod(modid = "minefactoryreloaded_compatthaumcraft",
		name = "MFR Compat: Thaumcraft",
		version = MineFactoryReloadedCore.version,
		dependencies = "after:MineFactoryReloaded;after:Thaumcraft",
		customProperties = @CustomProperty(k = "cofhversion", v = "true")))
public class Thaumcraft {

	@EventHandler
	@Strippable("mod:Thaumcraft")
	public void load(FMLInitializationEvent e) {

		try {
			final Block tcSapling = GameRegistry.findBlock("Thaumcraft", "blockCustomPlant");
			final Block tcLog = GameRegistry.findBlock("Thaumcraft", "blockMagicalLog");
			final Block tcLeaves = GameRegistry.findBlock("Thaumcraft", "blockMagicalLeaves");
			final Block tcFibres = GameRegistry.findBlock("Thaumcraft", "blockTaintFibres");
			@SuppressWarnings("unchecked")
			Class<? extends EntityLivingBase> golem = (Class<? extends EntityLivingBase>) Class
					.forName("thaumcraft.common.entities.golems.EntityGolemBase"), trunk = (Class<? extends EntityLivingBase>) Class
					.forName("thaumcraft.common.entities.golems.EntityTravelingTrunk"), pech = (Class<? extends EntityLivingBase>) Class
					.forName("thaumcraft.common.entities.monster.EntityPech");

			MFRRegistry.registerAutoSpawnerBlacklistClass(golem);
			MFRRegistry.registerAutoSpawnerBlacklistClass(trunk);

			MFRRegistry.registerSpawnHandler(new SpawnablePech(pech));

			MFRRegistry.registerGrinderBlacklist(golem);
			MFRRegistry.registerGrinderBlacklist(trunk);

			if (MFRConfig.conveyorNeverCapturesTCGolems.getBoolean(false)) {
				MFRRegistry.registerConveyerBlacklist(golem);
				MFRRegistry.registerConveyerBlacklist(trunk);
			}

			MFRRegistry.registerHarvestable(new HarvestableWood(tcLog));
			MFRRegistry.registerHarvestable(new HarvestableStandard(tcFibres, HarvestType.Normal));
			MFRRegistry.registerHarvestable(new HarvestableThaumcraftLeaves(tcLeaves,
					Item.getItemFromBlock(tcSapling)));
			MFRRegistry.registerHarvestable(new HarvestableThaumcraftPlant(tcSapling));

			MFRRegistry.registerPlantable(new PlantableThaumcraftTree(tcSapling));

			MFRRegistry.registerFertilizable(new FertilizableTCSapling(tcSapling));
		} catch (Throwable $) {
			ModContainer This = FMLCommonHandler.instance().findContainerFor(this);
			LogManager.getLogger(This.getModId()).log(Level.ERROR, "There was a problem loading " + This.getName(), $);
		}
	}

	@EventHandler
	@Strippable("mod:Thaumcraft@[4.2.1,)")
	public void loadManabean(FMLInitializationEvent e) {

		try {
			final Block tcPod = GameRegistry.findBlock("Thaumcraft", "blockManaPod");
			final Block tcLog = GameRegistry.findBlock("Thaumcraft", "blockMagicalLog");
			final Item tcBean = GameRegistry.findItem("Thaumcraft", "ItemManaBean");

			MFRRegistry.registerHarvestable(new HarvestableStandard(tcPod) {

				@Override
				public boolean canBeHarvested(World world, java.util.Map<String, Boolean> settings, BlockPos pos) {

					if (settings.get("isHarvestingTree") == Boolean.TRUE)
						return true;
					int blockMetadata = world.getBlockMetadata(x, y, z);
					return blockMetadata >= 5;
					//while 5 and 6 have a chance to drop 2 beans, 7 seems to do so consistently
				}
			});
			MFRRegistry.registerPlantable(new PlantableStandard(tcBean, tcPod) {

				@Override
				public boolean canBePlantedHere(World world, BlockPos pos, @Nonnull ItemStack stack) {

					if (!world.isAirBlock(x, y, z))
						return false;

					return isNextToAcceptableLog(world, x, y, z);
				}

				protected boolean isNextToAcceptableLog(World world, BlockPos pos) {

					boolean isMagic = false;
					BiomeGenBase biome = world.getBiomeGenForCoords(x, z);
					if (biome != null)
						isMagic = BiomeDictionary.isBiomeOfType(biome, BiomeDictionary.Type.MAGICAL);
					return isMagic && isGoodLog(world, x, y + 1, z);
				}

				protected boolean isGoodLog(World world, BlockPos pos) {

					Block id = world.getBlock(x, y, z);
					return id == tcLog || id.equals(Blocks.LOG);
				}

				@Override
				public void postPlant(World world, BlockPos pos, @Nonnull ItemStack stack) {

					NBTTagList aspects = stack.stackTagCompound.getTagList("Aspects", 10);
					// System.out.println("Aspect_count:"+aspects.tagCount()); //should be one but who knows

					String aspectstr = "herba"; //because pods without aspect seem to drop herba beans as well
					for (int i = 0; i < aspects.tagCount(); i++) {
						NBTTagCompound aspect = aspects.getCompoundTagAt(i);
						if (aspect.hasKey("key")) {
							//System.out.println("asp:"+aspect.getString("key"));
							aspectstr = aspect.getString("key");
							break;
						}
					}

					TileEntity te = world.getTileEntity(x, y, z);
					if (te != null) {
						NBTTagCompound tag = new NBTTagCompound();
						te.writeToNBT(tag);
						tag.setString("aspect", aspectstr);
						te.readFromNBT(tag);
					} //else System.out.println("huh, no tile entity?");
				}
			});

			MFRRegistry.registerFruitLogBlock(tcLog);
			MFRRegistry.registerFruit(new FactoryFruitStandard(tcPod) {

				@Override
				public boolean canBePicked(World world, BlockPos pos) {

					int blockMetadata = world.getBlockMetadata(x, y, z);
					return blockMetadata >= 7;
				}
			});

			MFRRegistry.registerFertilizable(new FertilizableCropPlant(tcPod, FertilizerType.GrowMagicalCrop, 6) {

				@Override
				public boolean fertilize(World world, Random rand, BlockPos pos, FertilizerType fertilizerType) {

					int meta = world.getBlockMetadata(x, y, z);
					meta += rand.nextInt(3);
					return world.setBlockMetadataWithNotify(x, y, z, meta, 2);
				}
			});
		} catch (Throwable $) {
			ModContainer This = FMLCommonHandler.instance().findContainerFor(this);
			LogManager.getLogger(This.getModId()).log(Level.ERROR, "There was a problem loading " + This.getName(), $);
		}
	}

	@EventHandler
	@Strippable("mod:Thaumcraft@[4,4.2.1)")
	public void loadCocoa(FMLInitializationEvent e) {

		try {
			final Block tcPod = GameRegistry.findBlock("Thaumcraft", "blockManaPod");
			final Block tcLog = GameRegistry.findBlock("Thaumcraft", "blockMagicalLog");
			final Item tcBean = GameRegistry.findItem("Thaumcraft", "ItemManaBean");

			MFRRegistry.registerHarvestable(new HarvestableCocoa(tcPod));
			MFRRegistry.registerPlantable(new PlantableCocoa(tcBean, tcPod) {

				@Override
				protected boolean isNextToAcceptableLog(World world, BlockPos pos) {

					boolean isMagic = false;
					BiomeGenBase biome = world.getBiomeGenForCoords(x, z);
					if (biome != null)
						isMagic = BiomeDictionary.isBiomeOfType(biome, BiomeDictionary.Type.MAGICAL);
					return isMagic &&
							isGoodLog(world, x + 1, y, z) ||
							isGoodLog(world, x - 1, y, z) ||
							isGoodLog(world, x, y, z + 1) ||
							isGoodLog(world, x, y, z - 1);
				}

				@Override
				protected boolean isGoodLog(World world, BlockPos pos) {

					Block id = world.getBlock(x, y, z);
					return id == tcLog || id.equals(Blocks.LOG);
				}
			});

			MFRRegistry.registerFruitLogBlock(tcLog);
			MFRRegistry.registerFruit(new FruitCocoa(tcPod));

			MFRRegistry.registerFertilizable(new FertilizableCocoa(tcPod, FertilizerType.GrowMagicalCrop));
		} catch (Throwable $) {
			ModContainer This = FMLCommonHandler.instance().findContainerFor(this);
			LogManager.getLogger(This.getModId()).log(Level.ERROR, "There was a problem loading " + This.getName(), $);
		}
	}

	@EventHandler
	@Strippable("api:Thaumcraft|API")
	public void load(FMLLoadCompleteEvent e) throws Throwable {

		try {
			doAspects();
		} catch (Throwable $) {
			ModContainer This = FMLCommonHandler.instance().findContainerFor(this);
			LogManager.getLogger(This.getModId()).log(Level.ERROR, "There was a problem loading " + This.getName(), $);
		}
	}

	private static void parseAspects2(Machine item, String toadd) throws Throwable {

		parseAspects(item.getItemStack(), toadd, true);
	}

	private static void doAspects() throws Throwable {

		parseAspects2(Machine.AutoAnvil, "3 permutatio, 5 fabrico, 10 metallum, 5 machina");
		parseAspects2(Machine.AutoBrewer, "4 ignis, 2 fabrico, 2 aqua, 2 praecantatio, 5 machina");
		parseAspects2(Machine.AutoDisenchanter, "4 praecantatio, 4 permutatio, 5 machina");
		parseAspects2(Machine.AutoEnchanter, "8 praecantatio, 4 cognitio, 4 fabrico, 5 machina");
		parseAspects2(Machine.AutoJukebox, "4 sensus, 4 aer, 5 machina");
		parseAspects2(Machine.AutoSpawner, "4 bestia, 4 exanimis, 7 praecantatio, 4 alienis, 5 machina, 10 permutatio");
		parseAspects2(Machine.BioFuelGenerator, "15 potentia, 3 herba, 5 machina, 3 permutatio");
		parseAspects2(Machine.BioReactor, "4 herba, 2 potentia, 5 machina, 5 permutatio");
		parseAspects2(Machine.BlockBreaker, "15 perfodio, 5 machina, 5 metallum, 3 lucrum");
		parseAspects2(Machine.BlockPlacer, "1 motus, 1 ordo, 5 machina, 5 metallum, 3 lucrum");
		parseAspects2(Machine.BlockSmasher, "5 perditio, 5 machina, 3 permutatio, 3 praecantatio");
		parseAspects2(Machine.Breeder, "2 bestia, 2 fames, 5 machina");
		parseAspects2(Machine.Chronotyper, "3 tempus, 3 bestia, 5 machina, 3 sensus");
		parseAspects2(Machine.ChunkLoader,
			"100 potentia, 30 alienis, 20 praecantatio, 10 iter, 10 vacuos, 5 machina, 5 instrumentum");
		parseAspects2(Machine.Composter, "2 bestia, 2 aqua, 5 machina");
		parseAspects2(Machine.DeepStorageUnit, "4971027 vacuos, 5 machina, 15 alienis, 1 praecantatio");
		parseAspects2(Machine.Ejector, "4 motus, 5 machina");
		parseAspects2(Machine.EnchantmentRouter, "1 motus, 4 iter, 2 sensus, 5 machina, 1 praecantatio");
		parseAspects2(Machine.Fertilizer, "6 herba, 5 machina, 1 vitreus, 3 victus");
		parseAspects2(Machine.Fisher, "3 aqua, 5 machina, 4 metallum, 2 instrumentum");
		parseAspects2(Machine.Fountain, "10 aqua, 5 machina, 3 fabrico, 1 iter");
		parseAspects2(Machine.FruitPicker, "2 herba, 4 meto, 5 machina");
		parseAspects2(Machine.Grinder, "10 telum, 6 mortuus, 7 meto, 5 machina, 4 metallum, 2 lucrum");
		parseAspects2(Machine.ItemCollector, "4 vacuos, 5 machina, 2 arbor, 4 motus");
		parseAspects2(Machine.ItemRouter, "2 motus, 4 iter, 2 sensus,  5 machina");
		parseAspects2(Machine.LaserDrill, "30 perfodio, 15 lux, 5 machina, 4 victus");
		parseAspects2(Machine.LaserDrillPrecharger, "4 lux, 5 machina, 25 potentia, 2 victus");
		parseAspects2(Machine.LavaFabricator, "4 ignis, 4 terra, 4 fabrico, 5 machina");
		parseAspects2(Machine.LiquiCrafter, "5 aqua, 5 fabrico, 5 machina");
		parseAspects2(Machine.LiquidRouter, "1 motus, 4 iter, 2 sensus, 5 machina, 1 aqua");
		parseAspects2(Machine.MeatPacker, "2 ordo, 2 corpus, 2 fames, 5 machina");
		parseAspects2(Machine.MobCounter, "5 ordo, 5 machina, 5 cognitio");
		parseAspects2(Machine.MobRouter, "3 ordo, 3 bestia, 5 machina, 3 sensus");
		parseAspects2(Machine.Planter, "4 herba, 2 arbor, 4 messis, 5 machina");
		parseAspects2(Machine.Rancher, "6 meto, 5 machina, 4 metallum, 2 instrumentum");
		parseAspects2(Machine.RedNote, "4 aer, 4 sensus, 5 machina");
		parseAspects2(Machine.Sewer, "1 venenum, 3 aqua, 5 machina, 4 bestia");
		parseAspects2(Machine.Slaughterhouse, "12 telum, 10 mortuus, 5 machina, 12 metallum, 6 lucrum");
		parseAspects2(Machine.SludgeBoiler, "3 ordo, 3 venenum, 3 terra, 2 aqua, 5 machina, 2 ignis");
		parseAspects2(Machine.SteamBoiler, "3 fabrico, 7 aqua, 5 machina, 10 ignis, 3 perditio");
		parseAspects2(Machine.SteamTurbine, "3 vacuos, 3 aqua, 5 machina, 10 potentia, 1 fabrico, 3 ordo");
		parseAspects2(Machine.Unifier, "5 ordo, 2 alienis, 5 machina");
		parseAspects2(Machine.Vet, "4 sano, 5 machina, 4 bestia");
		parseAspects2(Machine.WeatherCollector, " 4 vacuos, 5 machina, 5 metallum, 4 tempestas");

		parseAspects("mfrEntityPinkSlime", "1 aqua, 2 limus, 1 corpus, 1 bestia");

		parseAspects(milkLiquid, "4 fames, 4 sano, 4 aqua, 2 victus");
		parseAspects(chocolateMilkLiquid, "4 fames, 2 motus, 2 potentia, 4 aqua");
		parseAspects(essenceLiquid, "4 praecantatio, 2 cognitio, 2 aqua");
		parseAspects(mushroomSoupLiquid, "4 fames, 4 herba, 1 aqua");
		parseAspects(pinkSlimeLiquid, "4 limus, 4 corpus, 2 aqua");
		parseAspects(sewageLiquid, "4 venenum, 2 bestia, 2 aqua, 1 vitium");
		parseAspects(sludgeLiquid, "4 venenum, 2 terra, 2 aqua, 1 mortuus");

		parseAspects(machineItem, 0, "2 fabrico, 2 machina, 1 terra"); // factory machine block
		parseAspects(machineItem, 1, "1 cognitio, 3 machina"); // PRC housing
		parseAspects(rubberBarItem, "1 motus, 1 arbor, 1 ignis"); // rubber bar
		parseAspects(rubberLeavesBlock, "1 herba"); // rubber leaves
		parseAspects(rubberSaplingBlock, "1 arbor, 1 herba"); // rubber sapling
		parseAspects(rubberWoodBlock, 0, "3 arbor"); // rubber wood
		parseAspects(rubberWoodBlock, 1, "3 arbor, 1 limus"); // rubber wood
		parseAspects(rawRubberItem, "2 limus, 1 arbor"); // raw rubber
		parseAspects(rawPlasticItem, "1 fabrico, 1 ignis, 1 ordo, 1 perditio"); // raw plastic
		parseAspects(plasticSheetItem, "1 fabrico, 1 ignis, 2 ordo"); // plastic sheet
		parseAspects(factoryPlasticBlock, "1 iter, 1 fabrico, 1 sensus"); // plastic block
		parseAspects(plasticPipeBlock, "1 aqua, 1 machina", true);
		parseAspects(plasticTank, "4 aqua, 4 vacuos");
		parseAspects(bioFuelBucketItem, "2 herba, 1 potentia, 1 aqua, 8 metallum, 1 vacuos");
		parseAspects(biofuelLiquid, "4 herba, 2 potentia, 2 aqua");
		parseAspects(blankRecordItem, "4 sensus, 4 aer, 4 lucrum, 4 vacuos");
		parseAspects(ceramicDyeItem, "1 terra, 1 aqua, 1 sensus");
		parseAspects(chocolateMilkBucketItem, "2 fames, 1 motus, 1 potentia, 2 aqua, 8 metallum, 1 vacuos");
		parseAspects(conveyorBlock, "3 motus, 1 iter, 1 machina");
		parseAspects(factoryGlassBlock, "1 vitreus, 1 sensus");
		parseAspects(factoryHammerItem, "1 instrumentum, 2 fabrico, 2 ignis, 3 ordo");
		parseAspects(fertilizerItem, "1 arbor, 1 herba, 1 messis, 1 sensus");
		parseAspects(laserFocusItem, "1 ordo, 1 vitreus, 4 lucrum");
		parseAspects(meatBucketItem, "3 corpus, 1 bestia, 1 aqua, 8 metallum, 1 vacuos");
		parseAspects(meatIngotCookedItem, "3 corpus, 2 fames, 1 ignis");
		parseAspects(meatIngotRawItem, "3 corpus, 2 fames, 1 bestia");
		parseAspects(meatLiquid, "6 corpus, 2 aqua, 2 bestia");
		parseAspects(meatNuggetCookedItem, "1 fames");
		parseAspects(meatNuggetRawItem, "1 corpus");
		parseAspects(milkBottleItem, "1 fames, 1 sano, 1 victus, 1 vitreus");
		parseAspects(mobEssenceBucketItem, "2 praecantatio, 1 cognitio, 1 aqua, 8 metallum, 1 vacuos");
		parseAspects(mushroomSoupBucketItem, "2 fames, 2 herba, 8 metallum, 1 vacuos");
		parseAspects(pinkSlimeItem, "1 limus, 1 corpus");
		parseAspects(pinkSlimeBlock, "9 limus, 9 corpus, 2 aqua");
		parseAspects(pinkSlimeBucketItem, "2 limus, 2 corpus, 1 aqua, 8 metallum, 1 vacuos");
		parseAspects(portaSpawnerItem, "8 alienis, 4 bestia, 4 exanimis, 4 iter, 8 praecantatio, 8 permutatio");
		parseAspects(rednetCableBlock, 0, "1 cognitio, 1 machina", true);
		parseAspects(rednetCableBlock, 1, "1 cognitio, 1 machina, 1 vitreus", true);
		parseAspects(rednetCableBlock, 2, "1 cognitio, 1 machina, 3 potentia", true);
		parseAspects(rednetCableBlock, 3, "1 cognitio, 1 machina, 3 potentia, 1 vitreus", true);
		parseAspects(rednetLogicBlock, "15 cognitio, 5 machina", true);
		parseAspects(rednetMemoryCardItem, "3 cognitio, 1 machina");
		parseAspects(rednetMeterItem, 0, "1 instrumentum, 1 sensus, 1 machina");
		parseAspects(rednetMeterItem, 1, "4 instrumentum, 1 sensus, 2 machina");
		parseAspects(rednetPanelBlock, "2 sensus, 2 cognitio, 2 machina");
		parseAspects(rulerItem, "1 instrumentum, 1 sensus");
		parseAspects(safariNetItem, "4 spiritus, 8 alienis, 8 iter, 4 praecantatio, 8 vinculum, 4 fabrico");
		parseAspects(safariNetJailerItem, "10 vinculum, 1 praecantatio, 4 spiritus, 2 metallum, 1 fabrico");
		parseAspects(safariNetLauncherItem, "2 volatus, 2 instrumentum");
		parseAspects(safariNetSingleItem, "4 vinculum, 4 spiritus, 2 instrumentum");
		parseAspects(sewageBucketItem, "2 venenum, 1 bestia, 1 aqua, 8 metallum, 1 vacuos");
		parseAspects(sludgeBucketItem, "2 venenum, 1 terra, 1 aqua, 1 vitium, 8 metallum, 1 vacuos");
		parseAspects(spyglassItem, "2 victus, 6 sensus");
		parseAspects(strawItem, "1 vacuos, 4 aqua, 4 fames, 1 instrumentum");
		parseAspects(sugarCharcoalItem, "2 potentia, 2 ignis");
		parseAspects(syringeCureItem, "2 sano, 1 exanimis, 1 humanus, 1 instrumentum");
		parseAspects(syringeEmptyItem, "1 vacuos, 1 sano, 1 instrumentum");
		parseAspects(syringeGrowthItem, "1 tempus, 2 sano, 1 instrumentum");
		parseAspects(syringeHealthItem, "2 sano, 1 instrumentum");
		parseAspects(syringeSlimeItem, "1 sano, 1 limus, 1 instrumentum");
		parseAspects(syringeZombieItem, "1 tempus, 1 sano, 1 exanimis, 1 instrumentum");
		parseAspects(vineScaffoldBlock, "1 herba, 1 fabrico");
		parseAspects(xpExtractorItem, "1 praecantatio, 1 permutatio, 1 vacuos, 1 instrumentum, 1 meto");
		//parseAspects(Tracks, "Tracks Currently Have No Aspects.");

		parseAspects(upgradeItem, "2 cognitio");
		for (int i = 0, n = 10; i <= n; ++i)
			parseAspects(upgradeItem, i, "2 cognitio", true);
		parseAspects(upgradeItem, 11, "1 cognitio", true);
		parseAspects(logicCardItem, 0, "4 cognitio", true);
		parseAspects(logicCardItem, 1, "7 cognitio", true);
		parseAspects(logicCardItem, 2, "10 cognitio", true);

		parseAspects(factoryDecorativeBrickBlock, 0, "2 gelum, 1 terra"); // ice
		parseAspects(factoryDecorativeBrickBlock, 1, "1 terra, 3 lux, 2 sensus"); // glowstone
		parseAspects(factoryDecorativeBrickBlock, 2, "2 terra, 4 sensus"); // lapis
		parseAspects(factoryDecorativeBrickBlock, 3, "2 terra, 3 ignis, 1 tenebrae"); // obsidian
		parseAspects(factoryDecorativeBrickBlock, 4, "2 terra, 1 ordo"); // paved stone
		parseAspects(factoryDecorativeBrickBlock, 5, "1 gelum, 1 terra"); // snow
		parseAspects(factoryDecorativeBrickBlock, 6, "2 gelum, 1 terra"); // ice large
		parseAspects(factoryDecorativeBrickBlock, 7, "1 terra, 3 lux, 2 sensus"); // glowstone large
		parseAspects(factoryDecorativeBrickBlock, 8, "2 terra, 4 sensus"); // lapis large
		parseAspects(factoryDecorativeBrickBlock, 9, "3 ignis, 2 terra, 1 tenebrae"); // obsidian large
		parseAspects(factoryDecorativeBrickBlock, 10, "3 terra"); // pavedstone large
		parseAspects(factoryDecorativeBrickBlock, 12, "1 gelum, 1 terra"); // snow large
		parseAspects(factoryDecorativeBrickBlock, 12, "3 corpus, 2 fames, 1 bestia", true); // raw meat block
		parseAspects(factoryDecorativeBrickBlock, 13, "3 corpus, 2 fames, 1 ignis", true); // cooked meat block
		parseAspects(factoryDecorativeBrickBlock, 14, "4 terra, 4 ignis"); // brick large
		parseAspects(factoryDecorativeBrickBlock, 15, "10 ignis, 10 potentia"); // sugar charcoal

		parseAspects(factoryDecorativeStoneBlock, 0, "2 terra, 1 tenebrae");
		parseAspects(factoryDecorativeStoneBlock, 1, "2 terra, 1 victus");
		parseAspects(factoryDecorativeStoneBlock, 2, "1 perditio, 1 terra, 1 tenebrae");
		parseAspects(factoryDecorativeStoneBlock, 3, "1 perditio, 1 terra, 1 victus");
		parseAspects(factoryDecorativeStoneBlock, 4, "2 terra, 1 tenebrae");
		parseAspects(factoryDecorativeStoneBlock, 5, "2 terra, 1 victus");
		parseAspects(factoryDecorativeStoneBlock, 6, "3 terra, 1 tenebrae");
		parseAspects(factoryDecorativeStoneBlock, 7, "3 terra, 1 victus");
		parseAspects(factoryDecorativeStoneBlock, 8, "2 terra, 1 tenebrae");
		parseAspects(factoryDecorativeStoneBlock, 9, "2 terra, 1 victus");
		parseAspects(factoryDecorativeStoneBlock, 10, "3 terra, 1 tenebrae");
		parseAspects(factoryDecorativeStoneBlock, 11, "3 terra, 1 victus");
		//parseAspects(factoryDecorativeStoneBlock, 12, "2 terra, 1 tenebrae");
		//parseAspects(factoryDecorativeStoneBlock, 13, "2 terra, 1 victus");

		parseAspects(factoryRoadBlock, 0, "3 iter, 1 terra, 1 sensus");
		parseAspects(factoryRoadBlock, 1, "3 iter, 1 terra, 1 sensus, 3 lux"); // road light (off)
		parseAspects(factoryRoadBlock, 2, "3 iter, 1 terra, 1 sensus, 3 lux"); // road light (on)
		parseAspects(factoryRoadBlock, 3, "3 iter, 1 terra, 1 sensus, 3 lux"); // road light inverted (off)
		parseAspects(factoryRoadBlock, 4, "3 iter, 1 terra, 1 sensus, 3 lux"); // road light inverted (on)
	}

}
*/
