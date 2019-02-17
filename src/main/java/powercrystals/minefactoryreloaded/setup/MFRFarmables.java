package powercrystals.minefactoryreloaded.setup;

import cofh.core.util.oredict.OreDictionaryArbiter;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.IGrowable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.INpc;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.api.handler.INeedleAmmo;
import powercrystals.minefactoryreloaded.circuits.*;
import powercrystals.minefactoryreloaded.circuits.analog.*;
import powercrystals.minefactoryreloaded.circuits.digital.*;
import powercrystals.minefactoryreloaded.circuits.latch.*;
import powercrystals.minefactoryreloaded.circuits.logic.*;
import powercrystals.minefactoryreloaded.circuits.logicboolean.*;
import powercrystals.minefactoryreloaded.circuits.timing.Delay;
import powercrystals.minefactoryreloaded.circuits.timing.Multipulse;
import powercrystals.minefactoryreloaded.circuits.timing.OneShot;
import powercrystals.minefactoryreloaded.circuits.timing.PulseLengthener;
import powercrystals.minefactoryreloaded.circuits.wave.*;
import powercrystals.minefactoryreloaded.core.drinkhandlers.*;
import powercrystals.minefactoryreloaded.core.safarinethandlers.*;
import powercrystals.minefactoryreloaded.entity.EntityPinkSlime;
import powercrystals.minefactoryreloaded.farmables.fertilizables.FertilizableIGrowable;
import powercrystals.minefactoryreloaded.farmables.fertilizables.FertilizableStandard;
import powercrystals.minefactoryreloaded.farmables.fertilizables.FertilizerStandard;
import powercrystals.minefactoryreloaded.farmables.harvestables.HarvestableCropPlant;
import powercrystals.minefactoryreloaded.farmables.harvestables.HarvestableTreeLeaves;
import powercrystals.minefactoryreloaded.farmables.harvestables.HarvestableWood;
import powercrystals.minefactoryreloaded.farmables.plantables.PlantableSapling;
import powercrystals.minefactoryreloaded.farmables.plantables.PlantableSoil;
import powercrystals.minefactoryreloaded.modhelpers.vanilla.handlers.grindables.GrindableSlime;

import javax.annotation.Nonnull;
import java.util.ArrayList;

import static powercrystals.minefactoryreloaded.setup.MFRThings.*;

public class MFRFarmables {

	public static void load() {

		if (MFRConfig.conveyorNeverCapturesPlayers.getBoolean(false)) {
			MFRRegistry.registerConveyorBlacklist(EntityPlayer.class);
		}

		if (!MFRConfig.conveyorCaptureNonItems.getBoolean(true)) {
			MFRRegistry.registerConveyorBlacklist(Entity.class);
		}

		MFRRegistry.registerSafariNetHandler(new EntityLivingBaseHandler());
		MFRRegistry.registerSafariNetHandler(new EntityLivingHandler());
		MFRRegistry.registerSafariNetHandler(new EntityAgeableHandler());
		MFRRegistry.registerSafariNetHandler(new SheepHandler());
		MFRRegistry.registerSafariNetHandler(new SlimeHandler());

		MFRRegistry.registerSafariNetBlacklist(EntityPlayer.class);
		MFRRegistry.registerSafariNetBlacklist(EntityWither.class); // TODO: Entity now has isNonBoss (probably should be isNormalMob)
		MFRRegistry.registerSafariNetBlacklist(EntityDragon.class);

		MFRRegistry.registerGrinderBlacklist(EntityPlayer.class);
		MFRRegistry.registerGrinderBlacklist(INpc.class);
		MFRRegistry.registerGrinderBlacklist(EntityWither.class);
		MFRRegistry.registerGrinderBlacklist(EntityDragon.class);

		MFRRegistry.registerPlantable(new PlantableSapling(rubberSaplingBlock));
		MFRRegistry.registerPlantable(new PlantableSoil(fertileSoil, 3));

		MFRRegistry.registerHarvestable(new HarvestableWood(rubberWoodBlock));
		MFRRegistry.registerHarvestable(new HarvestableTreeLeaves(rubberLeavesBlock));

		MFRRegistry.registerFertilizable(new FertilizableStandard(rubberSaplingBlock));
		MFRRegistry.registerFertilizable(new FertilizableIGrowable(fertileSoil));

		MFRRegistry.registerFertilizer(new FertilizerStandard(fertilizerItem, 0));

		MFRRegistry.registerGrindable(new GrindableSlime(EntityPinkSlime.class, new ItemStack(pinkSlimeItem), 1));

		MFRRegistry.registerLiquidDrinkHandler("milk", new DrinkHandlerMilk());
		MFRRegistry.registerLiquidDrinkHandler("biofuel", new DrinkHandlerBiofuel());
		MFRRegistry.registerLiquidDrinkHandler("sewage", new DrinkHandlerSewage());
		MFRRegistry.registerLiquidDrinkHandler("sludge", new DrinkHandlerSludge());
		MFRRegistry.registerLiquidDrinkHandler("mob_essence", new DrinkHandlerMobEssence());
		MFRRegistry.registerLiquidDrinkHandler("meat", new DrinkHandlerMeat());
		MFRRegistry.registerLiquidDrinkHandler("pink_slime", new DrinkHandlerPinkSlime());
		MFRRegistry.registerLiquidDrinkHandler("chocolate_milk", new DrinkHandlerChocolateMilk());
		MFRRegistry.registerLiquidDrinkHandler("mushroom_soup", new DrinkHandlerMushroomSoup());

		MFRRegistry.registerNeedleAmmoType(needlegunAmmoStandardItem, (INeedleAmmo)needlegunAmmoStandardItem);
		MFRRegistry.registerNeedleAmmoType(needlegunAmmoPierceItem, (INeedleAmmo)needlegunAmmoPierceItem);
		MFRRegistry.registerNeedleAmmoType(needlegunAmmoLavaItem, (INeedleAmmo)needlegunAmmoLavaItem);
		MFRRegistry.registerNeedleAmmoType(needlegunAmmoSludgeItem, (INeedleAmmo)needlegunAmmoSludgeItem);
		MFRRegistry.registerNeedleAmmoType(needlegunAmmoSewageItem, (INeedleAmmo)needlegunAmmoSewageItem);
		MFRRegistry.registerNeedleAmmoType(needlegunAmmoFireItem, (INeedleAmmo)needlegunAmmoFireItem);
		MFRRegistry.registerNeedleAmmoType(needlegunAmmoAnvilItem, (INeedleAmmo)needlegunAmmoAnvilItem);

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
		MFRRegistry.registerRedNetLogicCircuit(new Multiplier());
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
	}

	public static void post() {

		if (MFRConfig.autoRegisterHarvestables.getBoolean(false)) {
			ArrayList<ItemStack> list = OreDictionaryArbiter.getOres("logWood");
			for (@Nonnull ItemStack stack : list) {
				if (stack.isEmpty())
					continue;
				Block block = Block.getBlockFromItem(stack.getItem());
				if (block != Blocks.AIR && !MFRRegistry.getHarvestables().containsKey(block))
					MFRRegistry.registerHarvestable(new HarvestableWood(block));
			}

			list = OreDictionaryArbiter.getOres("treeLeaves");
			for (@Nonnull ItemStack stack : list) {
				if (stack.isEmpty())
					continue;
				Block block = Block.getBlockFromItem(stack.getItem());
				if (block != Blocks.AIR && !MFRRegistry.getHarvestables().containsKey(block))
					MFRRegistry.registerHarvestable(new HarvestableTreeLeaves(block));
			}

			Block.REGISTRY.forEach((block) -> {
				if (MFRRegistry.getFertilizables().containsKey(block))
					return;
				if (block instanceof IGrowable) MFRRegistry.registerFertilizable(new FertilizableIGrowable(block));
				if (block instanceof BlockCrops) MFRRegistry.registerHarvestable(new HarvestableCropPlant((BlockCrops) block));
			});
		}
	}

}
