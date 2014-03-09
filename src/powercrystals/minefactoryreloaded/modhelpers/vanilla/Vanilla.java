package powercrystals.minefactoryreloaded.modhelpers.vanilla;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.api.FertilizerType;
import powercrystals.minefactoryreloaded.api.HarvestType;
import powercrystals.minefactoryreloaded.api.INeedleAmmo;
import powercrystals.minefactoryreloaded.api.MobDrop;
import powercrystals.minefactoryreloaded.circuits.Fanout;
import powercrystals.minefactoryreloaded.circuits.Noop;
import powercrystals.minefactoryreloaded.circuits.Passthrough;
import powercrystals.minefactoryreloaded.circuits.PassthroughGated;
import powercrystals.minefactoryreloaded.circuits.PassthroughRoundRobin;
import powercrystals.minefactoryreloaded.circuits.analog.AdderAnalog;
import powercrystals.minefactoryreloaded.circuits.analog.DecomposeIntToDecimal;
import powercrystals.minefactoryreloaded.circuits.analog.Max2;
import powercrystals.minefactoryreloaded.circuits.analog.Max3;
import powercrystals.minefactoryreloaded.circuits.analog.Max4;
import powercrystals.minefactoryreloaded.circuits.analog.Min2;
import powercrystals.minefactoryreloaded.circuits.analog.Min3;
import powercrystals.minefactoryreloaded.circuits.analog.Min4;
import powercrystals.minefactoryreloaded.circuits.analog.Negator;
import powercrystals.minefactoryreloaded.circuits.analog.RandomizerAnalog;
import powercrystals.minefactoryreloaded.circuits.analog.Scaler;
import powercrystals.minefactoryreloaded.circuits.analog.SchmittTrigger;
import powercrystals.minefactoryreloaded.circuits.analog.Subtractor;
import powercrystals.minefactoryreloaded.circuits.digital.AdderDigitalFull;
import powercrystals.minefactoryreloaded.circuits.digital.AdderDigitalHalf;
import powercrystals.minefactoryreloaded.circuits.digital.Counter;
import powercrystals.minefactoryreloaded.circuits.digital.DeMux16Analog;
import powercrystals.minefactoryreloaded.circuits.digital.DeMux4;
import powercrystals.minefactoryreloaded.circuits.digital.Inverter;
import powercrystals.minefactoryreloaded.circuits.digital.Mux4;
import powercrystals.minefactoryreloaded.circuits.digital.RandomizerDigital;
import powercrystals.minefactoryreloaded.circuits.digital.SevenSegmentEncoder;
import powercrystals.minefactoryreloaded.circuits.latch.FlipFlopJK;
import powercrystals.minefactoryreloaded.circuits.latch.FlipFlopT;
import powercrystals.minefactoryreloaded.circuits.latch.LatchDGated;
import powercrystals.minefactoryreloaded.circuits.latch.LatchSR;
import powercrystals.minefactoryreloaded.circuits.latch.LatchSRGated;
import powercrystals.minefactoryreloaded.circuits.logic.And2;
import powercrystals.minefactoryreloaded.circuits.logic.And3;
import powercrystals.minefactoryreloaded.circuits.logic.And4;
import powercrystals.minefactoryreloaded.circuits.logic.Nand2;
import powercrystals.minefactoryreloaded.circuits.logic.Nand3;
import powercrystals.minefactoryreloaded.circuits.logic.Nand4;
import powercrystals.minefactoryreloaded.circuits.logic.Nor2;
import powercrystals.minefactoryreloaded.circuits.logic.Nor3;
import powercrystals.minefactoryreloaded.circuits.logic.Nor4;
import powercrystals.minefactoryreloaded.circuits.logic.Or2;
import powercrystals.minefactoryreloaded.circuits.logic.Or3;
import powercrystals.minefactoryreloaded.circuits.logic.Or4;
import powercrystals.minefactoryreloaded.circuits.logic.Xnor2;
import powercrystals.minefactoryreloaded.circuits.logic.Xnor3;
import powercrystals.minefactoryreloaded.circuits.logic.Xnor4;
import powercrystals.minefactoryreloaded.circuits.logic.Xor2;
import powercrystals.minefactoryreloaded.circuits.logic.Xor3;
import powercrystals.minefactoryreloaded.circuits.logic.Xor4;
import powercrystals.minefactoryreloaded.circuits.logicboolean.Equal;
import powercrystals.minefactoryreloaded.circuits.logicboolean.Greater;
import powercrystals.minefactoryreloaded.circuits.logicboolean.GreaterOrEqual;
import powercrystals.minefactoryreloaded.circuits.logicboolean.Less;
import powercrystals.minefactoryreloaded.circuits.logicboolean.LessOrEqual;
import powercrystals.minefactoryreloaded.circuits.logicboolean.NotEqual;
import powercrystals.minefactoryreloaded.circuits.timing.Delay;
import powercrystals.minefactoryreloaded.circuits.timing.Multipulse;
import powercrystals.minefactoryreloaded.circuits.timing.OneShot;
import powercrystals.minefactoryreloaded.circuits.timing.PulseLengthener;
import powercrystals.minefactoryreloaded.circuits.wave.SawtoothFalling;
import powercrystals.minefactoryreloaded.circuits.wave.SawtoothRising;
import powercrystals.minefactoryreloaded.circuits.wave.Sine;
import powercrystals.minefactoryreloaded.circuits.wave.Square;
import powercrystals.minefactoryreloaded.circuits.wave.Triangle;
import powercrystals.minefactoryreloaded.entity.EntityPinkSlime;
import powercrystals.minefactoryreloaded.farmables.drinkhandlers.DrinkHandlerBiofuel;
import powercrystals.minefactoryreloaded.farmables.drinkhandlers.DrinkHandlerChocolateMilk;
import powercrystals.minefactoryreloaded.farmables.drinkhandlers.DrinkHandlerLava;
import powercrystals.minefactoryreloaded.farmables.drinkhandlers.DrinkHandlerMeat;
import powercrystals.minefactoryreloaded.farmables.drinkhandlers.DrinkHandlerMilk;
import powercrystals.minefactoryreloaded.farmables.drinkhandlers.DrinkHandlerMobEssence;
import powercrystals.minefactoryreloaded.farmables.drinkhandlers.DrinkHandlerMushroomSoup;
import powercrystals.minefactoryreloaded.farmables.drinkhandlers.DrinkHandlerPinkSlime;
import powercrystals.minefactoryreloaded.farmables.drinkhandlers.DrinkHandlerSewage;
import powercrystals.minefactoryreloaded.farmables.drinkhandlers.DrinkHandlerSludge;
import powercrystals.minefactoryreloaded.farmables.drinkhandlers.DrinkHandlerWater;
import powercrystals.minefactoryreloaded.farmables.egghandlers.VanillaEggHandler;
import powercrystals.minefactoryreloaded.farmables.fertilizables.FertilizableCocoa;
import powercrystals.minefactoryreloaded.farmables.fertilizables.FertilizableCropPlant;
import powercrystals.minefactoryreloaded.farmables.fertilizables.FertilizableGiantMushroom;
import powercrystals.minefactoryreloaded.farmables.fertilizables.FertilizableGrass;
import powercrystals.minefactoryreloaded.farmables.fertilizables.FertilizableNetherWart;
import powercrystals.minefactoryreloaded.farmables.fertilizables.FertilizableSapling;
import powercrystals.minefactoryreloaded.farmables.fertilizables.FertilizableStemPlants;
import powercrystals.minefactoryreloaded.farmables.fertilizables.FertilizerStandard;
import powercrystals.minefactoryreloaded.farmables.fruits.FruitCocoa;
import powercrystals.minefactoryreloaded.farmables.grindables.GrindableSlime;
import powercrystals.minefactoryreloaded.farmables.grindables.GrindableStandard;
import powercrystals.minefactoryreloaded.farmables.grindables.GrindableZombiePigman;
import powercrystals.minefactoryreloaded.farmables.harvestables.HarvestableCocoa;
import powercrystals.minefactoryreloaded.farmables.harvestables.HarvestableCropPlant;
import powercrystals.minefactoryreloaded.farmables.harvestables.HarvestableMushroom;
import powercrystals.minefactoryreloaded.farmables.harvestables.HarvestableNetherWart;
import powercrystals.minefactoryreloaded.farmables.harvestables.HarvestableShrub;
import powercrystals.minefactoryreloaded.farmables.harvestables.HarvestableStandard;
import powercrystals.minefactoryreloaded.farmables.harvestables.HarvestableStemPlant;
import powercrystals.minefactoryreloaded.farmables.harvestables.HarvestableTreeLeaves;
import powercrystals.minefactoryreloaded.farmables.harvestables.HarvestableVine;
import powercrystals.minefactoryreloaded.farmables.harvestables.HarvestableWood;
import powercrystals.minefactoryreloaded.farmables.plantables.PlantableCocoa;
import powercrystals.minefactoryreloaded.farmables.plantables.PlantableCropPlant;
import powercrystals.minefactoryreloaded.farmables.plantables.PlantableNetherWart;
import powercrystals.minefactoryreloaded.farmables.plantables.PlantableStandard;
import powercrystals.minefactoryreloaded.farmables.ranchables.RanchableChicken;
import powercrystals.minefactoryreloaded.farmables.ranchables.RanchableCow;
import powercrystals.minefactoryreloaded.farmables.ranchables.RanchableMooshroom;
import powercrystals.minefactoryreloaded.farmables.ranchables.RanchableSheep;
import powercrystals.minefactoryreloaded.farmables.ranchables.RanchableSquid;
import powercrystals.minefactoryreloaded.farmables.safarinethandlers.EntityAgeableHandler;
import powercrystals.minefactoryreloaded.farmables.safarinethandlers.EntityLivingBaseHandler;
import powercrystals.minefactoryreloaded.farmables.safarinethandlers.EntityLivingHandler;
import powercrystals.minefactoryreloaded.farmables.safarinethandlers.SheepHandler;
import powercrystals.minefactoryreloaded.farmables.safarinethandlers.SlimeHandler;
import powercrystals.minefactoryreloaded.farmables.spawnhandlers.SpawnableHorse;
import powercrystals.minefactoryreloaded.setup.MFRConfig;

@Mod(modid = "MineFactoryReloaded|CompatVanilla", name = "MFR Compat: Vanilla", version = MineFactoryReloadedCore.version, dependencies = "after:MineFactoryReloaded")
@NetworkMod(clientSideRequired = false, serverSideRequired = false)
public class Vanilla
{
	@EventHandler
	public void load(FMLInitializationEvent event)
	{
		MFRRegistry.registerPlantable(new PlantableStandard(Block.sapling.blockID, Block.sapling.blockID));
		MFRRegistry.registerPlantable(new PlantableStandard(Item.pumpkinSeeds.itemID, Block.pumpkinStem.blockID));
		MFRRegistry.registerPlantable(new PlantableStandard(Item.melonSeeds.itemID, Block.melonStem.blockID));
		MFRRegistry.registerPlantable(new PlantableStandard(Block.mushroomBrown.blockID, Block.mushroomBrown.blockID));
		MFRRegistry.registerPlantable(new PlantableStandard(Block.mushroomRed.blockID, Block.mushroomRed.blockID));
		MFRRegistry.registerPlantable(new PlantableCropPlant(Item.seeds.itemID, Block.crops.blockID));
		MFRRegistry.registerPlantable(new PlantableCropPlant(Item.carrot.itemID, Block.carrot.blockID));
		MFRRegistry.registerPlantable(new PlantableCropPlant(Item.potato.itemID, Block.potato.blockID));
		MFRRegistry.registerPlantable(new PlantableNetherWart());
		MFRRegistry.registerPlantable(new PlantableCocoa(Item.dyePowder.itemID, Block.cocoaPlant.blockID, 3));
		MFRRegistry.registerPlantable(new PlantableStandard(MineFactoryReloadedCore.rubberSaplingBlock.blockID, MineFactoryReloadedCore.rubberSaplingBlock.blockID));
		
		MFRRegistry.registerHarvestable(new HarvestableWood(Block.wood.blockID));
		MFRRegistry.registerHarvestable(new HarvestableTreeLeaves(Block.leaves.blockID));
		MFRRegistry.registerHarvestable(new HarvestableStandard(Block.reed.blockID, HarvestType.LeaveBottom));
		MFRRegistry.registerHarvestable(new HarvestableStandard(Block.cactus.blockID, HarvestType.LeaveBottom));
		MFRRegistry.registerHarvestable(new HarvestableStandard(Block.plantRed.blockID, HarvestType.Normal));
		MFRRegistry.registerHarvestable(new HarvestableStandard(Block.plantYellow.blockID, HarvestType.Normal));
		MFRRegistry.registerHarvestable(new HarvestableShrub(Block.tallGrass.blockID));
		MFRRegistry.registerHarvestable(new HarvestableShrub(Block.deadBush.blockID));
		MFRRegistry.registerHarvestable(new HarvestableStandard(Block.mushroomCapBrown.blockID, HarvestType.Tree));
		MFRRegistry.registerHarvestable(new HarvestableStandard(Block.mushroomCapRed.blockID, HarvestType.Tree));
		MFRRegistry.registerHarvestable(new HarvestableMushroom(Block.mushroomBrown.blockID));
		MFRRegistry.registerHarvestable(new HarvestableMushroom(Block.mushroomRed.blockID));
		MFRRegistry.registerHarvestable(new HarvestableStemPlant(Block.pumpkin.blockID, HarvestType.Normal));
		MFRRegistry.registerHarvestable(new HarvestableStemPlant(Block.melon.blockID, HarvestType.Normal));
		MFRRegistry.registerHarvestable(new HarvestableCropPlant(Block.crops.blockID, 7));
		MFRRegistry.registerHarvestable(new HarvestableCropPlant(Block.carrot.blockID, 7));
		MFRRegistry.registerHarvestable(new HarvestableCropPlant(Block.potato.blockID, 7));
		MFRRegistry.registerHarvestable(new HarvestableVine());
		MFRRegistry.registerHarvestable(new HarvestableNetherWart());
		MFRRegistry.registerHarvestable(new HarvestableCocoa());
		MFRRegistry.registerHarvestable(new HarvestableWood(MineFactoryReloadedCore.rubberWoodBlock.blockID));
		MFRRegistry.registerHarvestable(new HarvestableTreeLeaves(MineFactoryReloadedCore.rubberLeavesBlock.blockID));
		
		MFRRegistry.registerFertilizable(new FertilizableSapling(Block.sapling.blockID));
		MFRRegistry.registerFertilizable(new FertilizableCropPlant(Block.crops.blockID, 7));
		MFRRegistry.registerFertilizable(new FertilizableCropPlant(Block.carrot.blockID, 7));
		MFRRegistry.registerFertilizable(new FertilizableCropPlant(Block.potato.blockID, 7));
		MFRRegistry.registerFertilizable(new FertilizableGiantMushroom(Block.mushroomBrown.blockID));
		MFRRegistry.registerFertilizable(new FertilizableGiantMushroom(Block.mushroomRed.blockID));
		MFRRegistry.registerFertilizable(new FertilizableStemPlants(Block.pumpkinStem.blockID));
		MFRRegistry.registerFertilizable(new FertilizableStemPlants(Block.melonStem.blockID));
		MFRRegistry.registerFertilizable(new FertilizableNetherWart());
		MFRRegistry.registerFertilizable(new FertilizableCocoa(Block.cocoaPlant.blockID));
		MFRRegistry.registerFertilizable(new FertilizableGrass());
		MFRRegistry.registerFertilizable(new FertilizableSapling(MineFactoryReloadedCore.rubberSaplingBlock.blockID));
		
		MFRRegistry.registerFertilizer(new FertilizerStandard(MineFactoryReloadedCore.fertilizerItem.itemID, 0));
		if(MFRConfig.enableBonemealFertilizing.getBoolean(false))
		{
			MFRRegistry.registerFertilizer(new FertilizerStandard(Item.dyePowder.itemID, 15));
		}
		else
		{
			MFRRegistry.registerFertilizer(new FertilizerStandard(Item.dyePowder.itemID, 15, FertilizerType.Grass));
		}
		
		MFRRegistry.registerRanchable(new RanchableCow());
		MFRRegistry.registerRanchable(new RanchableMooshroom());
		MFRRegistry.registerRanchable(new RanchableSheep());
		MFRRegistry.registerRanchable(new RanchableSquid());
		MFRRegistry.registerRanchable(new RanchableChicken());
		
		if (MFRConfig.conveyorNeverCapturesPlayers.getBoolean(false))
		{
			MFRRegistry.registerConveyerBlacklist(EntityPlayer.class);
		}
		
		if (!MFRConfig.conveyorCaptureNonItems.getBoolean(true))
		{
			MFRRegistry.registerConveyerBlacklist(Entity.class);
		}

		MFRRegistry.registerGrinderBlacklist(EntityPlayer.class);
		MFRRegistry.registerGrinderBlacklist(EntityDragon.class);
		MFRRegistry.registerGrinderBlacklist(EntityWither.class);
		MFRRegistry.registerGrinderBlacklist(EntityVillager.class);
		
		MFRRegistry.registerGrindable(new GrindableStandard(EntityChicken.class, new MobDrop[]
				{
			new MobDrop(30, null),
			new MobDrop(10, new ItemStack(Item.egg))
				}, false));
		MFRRegistry.registerGrindable(new GrindableStandard(EntityOcelot.class, new MobDrop[]
				{
			new MobDrop(10, new ItemStack(Item.fishRaw)),
			new MobDrop(10, new ItemStack(Item.silk))
				}));
		MFRRegistry.registerGrindable(new GrindableStandard(EntityWolf.class, new ItemStack(Item.bone)));
		MFRRegistry.registerGrindable(new GrindableZombiePigman());
		MFRRegistry.registerGrindable(new GrindableSlime(EntitySlime.class, new ItemStack(Item.slimeBall), 1));
		MFRRegistry.registerGrindable(new GrindableSlime(EntityPinkSlime.class, new ItemStack(MineFactoryReloadedCore.pinkSlimeballItem), 1));
		MFRRegistry.registerGrindable(new GrindableSlime(EntityMagmaCube.class, new ItemStack(Item.magmaCream), 1) {
			@Override
			protected boolean shouldDrop(EntitySlime slime) {
				return slime.getSlimeSize() <= dropSize;
			}
		});
		
		MFRRegistry.registerSludgeDrop(50, new ItemStack(Block.sand));
		MFRRegistry.registerSludgeDrop(40, new ItemStack(Block.dirt));
		MFRRegistry.registerSludgeDrop(30, new ItemStack(Item.clay, 4));
		MFRRegistry.registerSludgeDrop(10, new ItemStack(Block.gravel));
		MFRRegistry.registerSludgeDrop(5, new ItemStack(Block.slowSand));
		MFRRegistry.registerSludgeDrop(3, new ItemStack(Block.mycelium));
		MFRRegistry.registerSludgeDrop(1, new ItemStack(Block.netherrack));
		
		MFRRegistry.registerSafariNetHandler(new EntityLivingBaseHandler());
		MFRRegistry.registerSafariNetHandler(new EntityLivingHandler());
		MFRRegistry.registerSafariNetHandler(new EntityAgeableHandler());
		MFRRegistry.registerSafariNetHandler(new SheepHandler());
		MFRRegistry.registerSafariNetHandler(new SlimeHandler());
		
		MFRRegistry.registerMobEggHandler(new VanillaEggHandler());
		
		MFRRegistry.registerRubberTreeBiome("Swampland");
		MFRRegistry.registerRubberTreeBiome("Forest");
		MFRRegistry.registerRubberTreeBiome("Taiga");
		MFRRegistry.registerRubberTreeBiome("TaigaHills");
		MFRRegistry.registerRubberTreeBiome("Jungle");
		MFRRegistry.registerRubberTreeBiome("JungleHills");
		
		MFRRegistry.registerSafariNetBlacklist(EntityPlayer.class);
		MFRRegistry.registerSafariNetBlacklist(EntityDragon.class);
		MFRRegistry.registerSafariNetBlacklist(EntityWither.class);
		
		MFRRegistry.registerRandomMobProvider(new VanillaMobProvider());
		
		MFRRegistry.registerLiquidDrinkHandler("water", new DrinkHandlerWater());
		MFRRegistry.registerLiquidDrinkHandler("lava", new DrinkHandlerLava());
		MFRRegistry.registerLiquidDrinkHandler("milk", new DrinkHandlerMilk());
		MFRRegistry.registerLiquidDrinkHandler("biofuel", new DrinkHandlerBiofuel());
		MFRRegistry.registerLiquidDrinkHandler("bioethanol", new DrinkHandlerBiofuel());
		MFRRegistry.registerLiquidDrinkHandler("sewage", new DrinkHandlerSewage());
		MFRRegistry.registerLiquidDrinkHandler("sludge", new DrinkHandlerSludge());
		MFRRegistry.registerLiquidDrinkHandler("mobessence", new DrinkHandlerMobEssence());
		MFRRegistry.registerLiquidDrinkHandler("meat", new DrinkHandlerMeat());
		MFRRegistry.registerLiquidDrinkHandler("pinkslime", new DrinkHandlerPinkSlime());
		MFRRegistry.registerLiquidDrinkHandler("chocolatemilk", new DrinkHandlerChocolateMilk());
		MFRRegistry.registerLiquidDrinkHandler("mushroomsoup", new DrinkHandlerMushroomSoup());
		
		MFRRegistry.registerRedNetLogicCircuit(new AdderAnalog());
		MFRRegistry.registerRedNetLogicCircuit(new AdderDigitalFull());
		MFRRegistry.registerRedNetLogicCircuit(new AdderDigitalHalf());
		MFRRegistry.registerRedNetLogicCircuit(new And2());
		MFRRegistry.registerRedNetLogicCircuit(new And3());
		MFRRegistry.registerRedNetLogicCircuit(new And4());
		MFRRegistry.registerRedNetLogicCircuit(new Counter());
		MFRRegistry.registerRedNetLogicCircuit(new DecomposeIntToDecimal());
		MFRRegistry.registerRedNetLogicCircuit(new Delay());
		MFRRegistry.registerRedNetLogicCircuit(new DeMux16Analog());
		MFRRegistry.registerRedNetLogicCircuit(new DeMux4());
		MFRRegistry.registerRedNetLogicCircuit(new Equal());
		MFRRegistry.registerRedNetLogicCircuit(new Fanout());
		MFRRegistry.registerRedNetLogicCircuit(new FlipFlopJK());
		MFRRegistry.registerRedNetLogicCircuit(new FlipFlopT());
		MFRRegistry.registerRedNetLogicCircuit(new Greater());
		MFRRegistry.registerRedNetLogicCircuit(new GreaterOrEqual());
		MFRRegistry.registerRedNetLogicCircuit(new Inverter());
		MFRRegistry.registerRedNetLogicCircuit(new LatchDGated());
		MFRRegistry.registerRedNetLogicCircuit(new LatchSR());
		MFRRegistry.registerRedNetLogicCircuit(new LatchSRGated());
		MFRRegistry.registerRedNetLogicCircuit(new Less());
		MFRRegistry.registerRedNetLogicCircuit(new LessOrEqual());
		MFRRegistry.registerRedNetLogicCircuit(new Max2());
		MFRRegistry.registerRedNetLogicCircuit(new Max3());
		MFRRegistry.registerRedNetLogicCircuit(new Max4());
		MFRRegistry.registerRedNetLogicCircuit(new Min2());
		MFRRegistry.registerRedNetLogicCircuit(new Min3());
		MFRRegistry.registerRedNetLogicCircuit(new Min4());
		MFRRegistry.registerRedNetLogicCircuit(new Multipulse());
		MFRRegistry.registerRedNetLogicCircuit(new Mux4());
		MFRRegistry.registerRedNetLogicCircuit(new Nand2());
		MFRRegistry.registerRedNetLogicCircuit(new Nand3());
		MFRRegistry.registerRedNetLogicCircuit(new Nand4());
		MFRRegistry.registerRedNetLogicCircuit(new Negator());
		MFRRegistry.registerRedNetLogicCircuit(new Noop());
		MFRRegistry.registerRedNetLogicCircuit(new Nor2());
		MFRRegistry.registerRedNetLogicCircuit(new Nor3());
		MFRRegistry.registerRedNetLogicCircuit(new Nor4());
		MFRRegistry.registerRedNetLogicCircuit(new NotEqual());
		MFRRegistry.registerRedNetLogicCircuit(new OneShot());
		MFRRegistry.registerRedNetLogicCircuit(new Or2());
		MFRRegistry.registerRedNetLogicCircuit(new Or3());
		MFRRegistry.registerRedNetLogicCircuit(new Or4());
		MFRRegistry.registerRedNetLogicCircuit(new Passthrough());
		MFRRegistry.registerRedNetLogicCircuit(new PassthroughGated());
		MFRRegistry.registerRedNetLogicCircuit(new PassthroughRoundRobin());
		MFRRegistry.registerRedNetLogicCircuit(new PulseLengthener());
		MFRRegistry.registerRedNetLogicCircuit(new RandomizerAnalog());
		MFRRegistry.registerRedNetLogicCircuit(new RandomizerDigital());
		MFRRegistry.registerRedNetLogicCircuit(new SevenSegmentEncoder());
		MFRRegistry.registerRedNetLogicCircuit(new SawtoothFalling());
		MFRRegistry.registerRedNetLogicCircuit(new SawtoothRising());
		MFRRegistry.registerRedNetLogicCircuit(new Scaler());
		MFRRegistry.registerRedNetLogicCircuit(new SchmittTrigger());
		MFRRegistry.registerRedNetLogicCircuit(new Sine());
		MFRRegistry.registerRedNetLogicCircuit(new Square());
		MFRRegistry.registerRedNetLogicCircuit(new Subtractor());
		MFRRegistry.registerRedNetLogicCircuit(new Triangle());
		MFRRegistry.registerRedNetLogicCircuit(new Xnor2());
		MFRRegistry.registerRedNetLogicCircuit(new Xnor3());
		MFRRegistry.registerRedNetLogicCircuit(new Xnor4());
		MFRRegistry.registerRedNetLogicCircuit(new Xor2());
		MFRRegistry.registerRedNetLogicCircuit(new Xor3());
		MFRRegistry.registerRedNetLogicCircuit(new Xor4());
		
		MFRRegistry.registerFruitLogBlockId(Block.wood.blockID);
		MFRRegistry.registerFruit(new FruitCocoa(Block.cocoaPlant.blockID));
		
		MFRRegistry.registerAutoSpawnerBlacklist("VillagerGolem");
		
		MFRRegistry.registerSpawnHandler(new SpawnableHorse());
		
		MFRRegistry.registerNeedleAmmoType(MineFactoryReloadedCore.needlegunAmmoStandardItem.itemID,
				(INeedleAmmo)MineFactoryReloadedCore.needlegunAmmoStandardItem);
		MFRRegistry.registerNeedleAmmoType(MineFactoryReloadedCore.needlegunAmmoLavaItem.itemID,
				(INeedleAmmo)MineFactoryReloadedCore.needlegunAmmoLavaItem);
		MFRRegistry.registerNeedleAmmoType(MineFactoryReloadedCore.needlegunAmmoSludgeItem.itemID,
				(INeedleAmmo)MineFactoryReloadedCore.needlegunAmmoSludgeItem);
		MFRRegistry.registerNeedleAmmoType(MineFactoryReloadedCore.needlegunAmmoSewageItem.itemID,
				(INeedleAmmo)MineFactoryReloadedCore.needlegunAmmoSewageItem);
		MFRRegistry.registerNeedleAmmoType(MineFactoryReloadedCore.needlegunAmmoFireItem.itemID,
				(INeedleAmmo)MineFactoryReloadedCore.needlegunAmmoFireItem);
		MFRRegistry.registerNeedleAmmoType(MineFactoryReloadedCore.needlegunAmmoAnvilItem.itemID,
				(INeedleAmmo)MineFactoryReloadedCore.needlegunAmmoAnvilItem);
	}
	
	@EventHandler
	public void postLoad(FMLPostInitializationEvent event)
	{
		
		MFRRegistry.registerLaserOre(80, new ItemStack(Block.glowStone));
		MFRRegistry.registerLaserOre(175, new ItemStack(Block.oreCoal));
		MFRRegistry.addLaserPreferredOre(black, new ItemStack(Block.oreCoal));
		MFRRegistry.addLaserPreferredOre(yellow, new ItemStack(Block.glowStone));
		registerOreDictLaserOre(175 / 2, "oreNetherCoal",  black, null); // coal isn't in the oredict??
		registerOreDictLaserOre(150, "Iron",               brown, false);
		registerOreDictLaserOre(100, "Redstone",             red, false);
		registerOreDictLaserOre(100, "Nikolite",       lightBlue, false);
		registerOreDictLaserOre( 90, "Copper",            orange, false);
		registerOreDictLaserOre( 85, "Tin",               silver, false);
		registerOreDictLaserOre( 85, "oreCheese",         yellow, null);
		registerOreDictLaserOre( 85, "Force",             yellow,  true);
		registerOreDictLaserOre( 80, "Lapis",               blue,  true);
		registerOreDictLaserOre( 70, "Gold",              yellow, false);
		registerOreDictLaserOre( 70, "oreQuartz",          white, null);
		registerOreDictLaserOre( 60, "Lead",              purple, false);
		registerOreDictLaserOre( 60, "oreZinc",            white, "oreSphalerite", orange, "dustZinc");
		registerOreDictLaserOre( 60, "NaturalAluminum",    white, false);
		registerOreDictLaserOre( 60, "Aluminium",          white, false);
		registerOreDictLaserOre( 60, "Aluminum",           white, false);
		registerOreDictLaserOre( 60, "oreSodalite",         blue, null);
		registerOreDictLaserOre( 55, "Mythril",             blue, false);
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
		registerOreDictLaserOre( 45, "oreTungstate",       black, "oreNetherTungsten", "dustTungsten");
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
		registerOreDictLaserOre( 25, "Amethyst",         magenta,  true);
		registerOreDictLaserOre( 20, "Uranium",             lime, false);
		registerOreDictLaserOre( 20, "orePitchblende",     black, "oreNetherUranium", lime, "dustUranium");
		registerOreDictLaserOre( 20, "Yellorite",         yellow, false);
		registerOreDictLaserOre( 20, "oreFirestone",         red, null);
		registerOreDictLaserOre( 20, "MonazitOre",         green, null);
		registerOreDictLaserOre( 15, "Cinnabar",             red,  true);
		registerOreDictLaserOre( 15, "Platinum",       lightBlue, false);
		registerOreDictLaserOre( 15, "oreCooperite",      yellow, "oreNetherPlatinum", lightBlue, "dustPlatinum");
		registerOreDictLaserOre( 10, "oreArdite",         orange, null);
		registerOreDictLaserOre( 10, "oreCobalt",           blue, null);
		registerOreDictLaserOre(  5, "Iridium",            white, false);
		
		// rarity/usefulness unknown
		registerOreDictLaserOre( 20, "oreTetrahedrite", orange, null);
		registerOreDictLaserOre( 20, "oreCadmium", lightBlue, null);
		registerOreDictLaserOre( 20, "oreIndium", silver, null);
		registerOreDictLaserOre( 20, "oreAmmonium", white, null);
		registerOreDictLaserOre( 20, "oreCalcite", orange, null);
		registerOreDictLaserOre( 20, "oreFluorite", silver, null);
	}
	
	private void registerOreDictLaserOre(int weight, String suffix, int focus, boolean isGem)
	{
		registerOreDictLaserOre(weight, "ore" + suffix, focus,
				"oreNether" + suffix, focus, (isGem ? "gem" : "dust") + suffix);
	}
	
	private void registerOreDictLaserOre(int weight, String name, int focus, String netherName)
	{
		registerOreDictLaserOre(weight, name, focus, netherName, focus, null);
	}
	
	private void registerOreDictLaserOre(int weight, String name, int focus, String netherName,
			String dustName)
	{
		registerOreDictLaserOre(weight, name, focus, netherName, focus, dustName);
	}
	
	private void registerOreDictLaserOre(int weight, String name, int focus, String netherName, 
			int netherFocus, String dustName)
	{
		for (ItemStack ore : OreDictionary.getOres(name))
			if (ore != null)
			{
				ore = ore.copy();
				ore.stackSize = 1;
				MFRRegistry.registerLaserOre(weight, ore);
				if (focus >= 0)
					MFRRegistry.addLaserPreferredOre(focus, ore);
				if (netherName != null)
				{
					registerOreDictLaserOre(weight / 2, netherName, netherFocus, null, null);
				}
				return;
			}
		if (netherName != null & dustName != null)
			for (ItemStack ore : OreDictionary.getOres(dustName))
				if (ore != null)
				{
					registerOreDictLaserOre(weight / 2, netherName, netherFocus, null, null);
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
