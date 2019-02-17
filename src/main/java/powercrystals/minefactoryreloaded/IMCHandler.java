package powercrystals.minefactoryreloaded;

import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCMessage;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import powercrystals.minefactoryreloaded.api.ValuedItem;
import powercrystals.minefactoryreloaded.api.handler.ILiquidDrinkHandler;
import powercrystals.minefactoryreloaded.api.handler.IMobEggHandler;
import powercrystals.minefactoryreloaded.api.handler.IMobSpawnHandler;
import powercrystals.minefactoryreloaded.api.handler.ISafariNetHandler;
import powercrystals.minefactoryreloaded.api.mob.IFactoryGrindable;
import powercrystals.minefactoryreloaded.api.mob.IFactoryRanchable;
import powercrystals.minefactoryreloaded.api.mob.IRandomMobProvider;
import powercrystals.minefactoryreloaded.api.plant.*;
import powercrystals.minefactoryreloaded.api.rednet.IRedNetLogicCircuit;
import powercrystals.minefactoryreloaded.farmables.fertilizables.*;
import powercrystals.minefactoryreloaded.farmables.harvestables.*;
import powercrystals.minefactoryreloaded.farmables.plantables.PlantableCropPlant;
import powercrystals.minefactoryreloaded.farmables.plantables.PlantableSapling;
import powercrystals.minefactoryreloaded.farmables.plantables.PlantableStandard;

import javax.annotation.Nonnull;
import java.util.List;

public class IMCHandler {

	@SuppressWarnings("unchecked")
	public static void processIMC(List<IMCMessage> l) {

		Logger _log = MineFactoryReloadedCore.instance().getLogger();
		for (IMCMessage m : l) {
			try {
				String k = m.key;
				_log.debug("[IMCHandler] %s from %s", k, m.getSender());
				//{ FactoryRegistry methods
				/*
				 * Laser Preferred Ores
				 */
				if ("addLaserPreferredOre".equals(k)) {
					if (m.isNBTMessage()) {
						NBTTagCompound itemNBT = m.getNBTValue();
						MFRRegistry.addLaserPreferredOre(itemNBT.getInteger("value"),
							new ItemStack(itemNBT));
					} else {
						ValuedItem item = (ValuedItem) getValue(m);
						MFRRegistry.addLaserPreferredOre(item.value, item.item);
					}
				}
				/*
				 * AutoSpawner Blacklist
				 */
				else if ("registerAutoSpawnerBlacklist".equals(k)) {
					if (m.isStringMessage())
						MFRRegistry.registerAutoSpawnerBlacklist(m.getStringValue());
					else
						MFRRegistry.registerAutoSpawnerBlacklistClass((Class<? extends EntityLivingBase>)
								getValue(m));
				}
				/*
				 * Fertilizables
				 */
				else if ("registerFertilizable".equals(k)) {
					MFRRegistry.registerFertilizable((IFactoryFertilizable) getValue(m));
				}
				/*
				 * Fertilizers
				 */
				else if ("registerFertilizer".equals(k)) {
					MFRRegistry.registerFertilizer((IFactoryFertilizer) getValue(m));
				}
				/*
				 * Fruit logs
				 */
				else if ("registerFruitLog".equals(k)) {
					MFRRegistry.registerFruitLogBlock(Block.getBlockFromName(m.getStringValue()));
				}
				/*
				 * Grinding handlers
				 */
				else if ("registerGrindable".equals(k)) {
					MFRRegistry.registerGrindable((IFactoryGrindable) getValue(m));
				}
				/*
				 * Grinder blacklist
				 */
				else if ("registerGrinderBlacklist".equals(k)) {
					MFRRegistry.registerGrinderBlacklist((Class<? extends EntityLivingBase>) getValue(m));
				}
				/*
				 * Harvestables
				 */
				else if ("registerHarvestable".equals(k)) {
					MFRRegistry.registerHarvestable((IFactoryHarvestable) getValue(m));
				}
				/*
				 * Laser ores drops
				 */
				else if ("registerLaserOre".equals(k)) {
					if (m.isNBTMessage()) {
						NBTTagCompound itemNBT = m.getNBTValue();
						MFRRegistry.registerLaserOre(itemNBT.getInteger("value"),
							new ItemStack(itemNBT));
					} else {
						ValuedItem item = (ValuedItem) getValue(m);
						MFRRegistry.registerLaserOre(item.value, item.item);
					}
				}
				/*
				 * Liquid Drinking Handlers
				 */
				else if ("registerLiquidDrinkHandler".equals(k)) {
					ValuedItem item = (ValuedItem) getValue(m);
					MFRRegistry.registerLiquidDrinkHandler(item.key, (ILiquidDrinkHandler) item.object);
				}
				/*
				 * Mob egg handlers
				 */
				else if ("registerMobEggHandler".equals(k)) {
					MFRRegistry.registerMobEggHandler((IMobEggHandler) getValue(m));
				}
				/*
				 * Fruit Handlers
				 */
				else if ("registerPickableFruit".equals(k)) {
					MFRRegistry.registerFruit((IFactoryFruit) getValue(m));
				}
				/*
				 * Plantables
				 */
				else if ("registerPlantable".equals(k)) {
					MFRRegistry.registerPlantable((IFactoryPlantable) getValue(m));
				}
				/*
				 * Ranching Handlers
				 */
				else if ("registerRanchable".equals(k)) {
					MFRRegistry.registerRanchable((IFactoryRanchable) getValue(m));
				}
				/*
				 * RedNet Logic Circuits
				 */
				else if ("registerRedNetLogicCircuit".equals(k)) {
					if (m.isStringMessage()) {
						MFRRegistry.registerRedNetLogicCircuit((IRedNetLogicCircuit)
								Class.forName(m.getStringValue()).newInstance());
					} else {
						MFRRegistry.registerRedNetLogicCircuit((IRedNetLogicCircuit) getValue(m));
					}
				}
				/*
				 * Rubber tree biome whitelisting
				 */
				else if ("registerRubberTreeBiome".equals(k)) {
					MFRRegistry.registerRubberTreeBiome(new ResourceLocation(m.getStringValue()));
				}
				/*
				 * SafariNet Blacklist
				 */
				else if ("registerSafariNetBlacklist".equals(k)) {
					MFRRegistry.registerSafariNetBlacklist((Class<? extends EntityLivingBase>)
							getValue(m));
				}
				/*
				 * SafariNet Information Handler
				 */
				else if ("registerSafariNetHandler".equals(k)) {
					MFRRegistry.registerSafariNetHandler((ISafariNetHandler) getValue(m));
				}
				/*
				 * Sludge drop list
				 */
				else if ("registerSludgeDrop".equals(k)) {
					if (m.isNBTMessage()) {
						NBTTagCompound itemNBT = m.getNBTValue();
						MFRRegistry.registerSludgeDrop(itemNBT.getInteger("value"),
							new ItemStack(itemNBT));
					} else {
						ValuedItem item = (ValuedItem) getValue(m);
						MFRRegistry.registerSludgeDrop(item.value, item.item);
					}
				}
				/*
				 * Mob Spawning handlers
				 */
				else if ("registerSpawnHandler".equals(k)) {
					MFRRegistry.registerSpawnHandler((IMobSpawnHandler) getValue(m));
				}
				/*
				 * Random mob providers
				 */
				else if ("registerVillagerTradeMob".equals(k)) {
					MFRRegistry.registerRandomMobProvider((IRandomMobProvider) getValue(m));
				}
				//}
				//{ Simple implementations
				/**
				 * {Harvestables
				 */
				/*
				 * HarvestableStandard
				 */
				else if ("registerHarvestable_Standard".equals(k)) {
					MFRRegistry.registerHarvestable(new HarvestableStandard(
							Block.getBlockFromName(m.getStringValue())));
				}
				/*
				 * HarvestableWood
				 */
				else if ("registerHarvestable_Log".equals(k)) {
					MFRRegistry.registerHarvestable(new HarvestableWood(
							Block.getBlockFromName(m.getStringValue())));
				}
				/*
				 * HarvestableTreeLeaves
				 */
				else if ("registerHarvestable_Leaves".equals(k)) {
					MFRRegistry.registerHarvestable(new HarvestableTreeLeaves(
							Block.getBlockFromName(m.getStringValue())));
				}
				/*
				 * HarvestableVine
				 */
				else if ("registerHarvestable_Vine".equals(k)) {
					MFRRegistry.registerHarvestable(new HarvestableVine(
							Block.getBlockFromName(m.getStringValue())));
				}
				/*
				 * HarvestableShrub
				 */
				else if ("registerHarvestable_Shrub".equals(k)) {
					MFRRegistry.registerHarvestable(new HarvestableShrub(
							Block.getBlockFromName(m.getStringValue())));
				}
				/*
				 * HarvestableMushroom
				 */
				else if ("registerHarvestable_Mushroom".equals(k)) {
					MFRRegistry.registerHarvestable(new HarvestableMushroom(
							Block.getBlockFromName(m.getStringValue())));
				}
				/*
				 * HarvestableCrop
				 */
				else if ("registerHarvestable_Crop".equals(k)) {
					if (m.isItemStackMessage()) {
						@Nonnull ItemStack item = m.getItemStackValue();
						//MFRRegistry.registerHarvestable(new HarvestableCropPlant(Block.getBlockFromItem(item.getItem()), item.getItemDamage()));
					} else {
						ValuedItem item = (ValuedItem) getValue(m);
						//MFRRegistry.registerHarvestable(new HarvestableCropPlant((Block) item.object, item.value));
					}
				}
				/*
				 * HarvestableStemPlant
				 */
				else if ("registerHarvestable_Gourd".equals(k)) {
					NBTTagCompound item = m.getNBTValue();
					MFRRegistry.registerHarvestable(new HarvestableStemPlant(
							Block.getBlockFromName(item.getString("stem")),
							Block.getBlockFromName(item.getString("fruit"))));
					MFRRegistry.registerHarvestable(new HarvestableGourd(
							Block.getBlockFromName(item.getString("fruit"))));
				}
				/**
				 * }
				 * {Plantables
				 */
				/*
				 * PlantableCropPlant
				 */
				else if ("registerPlantable_Crop".equals(k)) {
					NBTTagCompound item = m.getNBTValue();
					if (item.hasKey("meta"))
						MFRRegistry.registerPlantable(new PlantableCropPlant(
								Item.REGISTRY.getObject(new ResourceLocation(item.getString("seed"))),
								Block.getBlockFromName(item.getString("crop")),
								item.getInteger("meta")));
					else
						MFRRegistry.registerPlantable(new PlantableCropPlant(
								Item.REGISTRY.getObject(new ResourceLocation(item.getString("seed"))),
								Block.getBlockFromName(item.getString("crop"))));
				}
				/*
				 * PlantableSapling
				 */
				else if ("registerPlantable_Sapling".equals(k)) {
					NBTTagCompound item = m.getNBTValue();
					if (item.hasKey("seed"))
						MFRRegistry.registerPlantable(new PlantableSapling(
								Item.REGISTRY.getObject(new ResourceLocation(item.getString("seed"))),
								Block.getBlockFromName(item.getString("sapling"))));
					else
						MFRRegistry.registerPlantable(new PlantableSapling(
								Block.getBlockFromName(item.getString("sapling"))));
				}
				/*
				 * PlantableStandard
				 */
				else if ("registerPlantable_Standard".equals(k)) {
					NBTTagCompound item = m.getNBTValue();
					if (item.hasKey("meta"))
						MFRRegistry.registerPlantable(new PlantableStandard(
								Item.REGISTRY.getObject(new ResourceLocation(item.getString("seed"))),
								Block.getBlockFromName(item.getString("crop")),
								item.getInteger("meta")));
					else
						MFRRegistry.registerPlantable(new PlantableStandard(
								Item.REGISTRY.getObject(new ResourceLocation(item.getString("seed"))),
								Block.getBlockFromName(item.getString("crop"))));
				}
				/**
				 * }
				 * {Fertilizer/Fertilizables
				 */
				/*
				 * FertilizerStandard
				 */
				else if ("registerFertilizer_Standard".equals(k)) {
					NBTTagCompound item = m.getNBTValue();
					MFRRegistry.registerFertilizer(new FertilizerStandard(
							Item.REGISTRY.getObject(new ResourceLocation(item.getString("fert"))),
							item.getInteger("meta"),
							FertilizerType.values()[item.getInteger("type")]));
				}
				/*
				 * FertilizableGrass
				 */
				else if ("registerFertilizable_Grass".equals(k)) {
					MFRRegistry.registerFertilizable(new FertilizableGrass(
							Block.getBlockFromName(m.getStringValue())));
				}
				/*
				 * FertilizableStemPlants
				 */
				else if ("registerFertilizable_Gourd".equals(k)) {
					MFRRegistry.registerFertilizable(new FertilizableStemPlants(
							(IGrowable) Block.getBlockFromName(m.getStringValue())));
				}
				/*
				 * FertilizableCropPlant
				 */
				else if ("registerFertilizable_Crop".equals(k)) {
					NBTTagCompound item = m.getNBTValue();
					if (item.hasKey("type"))
						MFRRegistry.registerFertilizable(new FertilizableCropPlant(
								(IGrowable) Block.getBlockFromName(item.getString("plant")),
								FertilizerType.values()[item.getInteger("type")],
								item.getInteger("meta")));
					else
						MFRRegistry.registerFertilizable(new FertilizableCropPlant(
								(IGrowable) Block.getBlockFromName(item.getString("plant")),
								item.getInteger("meta")));
				}
				/*
				 * FertilizableCocoa
				 */
				else if ("registerFertilizable_Cocoa".equals(k)) {
					NBTTagCompound item = m.getNBTValue();
					if (item.hasKey("type"))
						MFRRegistry.registerFertilizable(new FertilizableCocoa(
								Block.getBlockFromName(item.getString("plant")),
								FertilizerType.values()[item.getInteger("type")]));
					else
						MFRRegistry.registerFertilizable(new FertilizableCocoa(
								Block.getBlockFromName(item.getString("plant"))));
				}
				/*
				 * FertilizableStandard
				 */
				else if ("registerFertilizable_Standard".equals(k)) {
					NBTTagCompound item = m.getNBTValue();
					if (item.hasKey("type"))
						MFRRegistry.registerFertilizable(new FertilizableStandard(
								(IGrowable) Block.getBlockFromName(item.getString("plant")),
								FertilizerType.values()[item.getInteger("type")]));
					else
						MFRRegistry.registerFertilizable(new FertilizableStandard(
								(IGrowable) Block.getBlockFromName(item.getString("plant"))));
				}
				/**
				 * }
				 */
				//}
				/**
				 * Unknown IMC message
				 */
				else
					bigWarning(_log, Level.WARN, "Unknown IMC message (%s)\nfrom %s", k, m.getSender());
			} catch (Throwable t) {
				bigWarning(_log, Level.ERROR, "Bad IMC message (%s)\nfrom %s", m.key, m.getSender());
				_log.catching(t);
			}
		}
	}

	private static Object getValue(IMCMessage m) {

		return ReflectionHelper.getPrivateValue(IMCMessage.class, m, "value");
	}

	private static void bigWarning(Logger log, Level Level, String format, Object... data) {

		String o = String.format(format, data);
		String err = "************************";
		err += err;
		log.log(Level, err);
		log.log(Level, err);
		for (String str : o.split("\n", 0)) {
			log.log(Level, str);
		}
		log.log(Level, err);
		log.log(Level, err);
	}

}
