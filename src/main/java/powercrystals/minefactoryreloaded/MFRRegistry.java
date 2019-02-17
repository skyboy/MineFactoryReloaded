package powercrystals.minefactoryreloaded;

import codechicken.lib.reflect.ReflectionManager;
import gnu.trove.map.hash.TObjectIntHashMap;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.WeightedRandom;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import powercrystals.minefactoryreloaded.api.handler.*;
import powercrystals.minefactoryreloaded.api.integration.IMFRIntegrator;
import powercrystals.minefactoryreloaded.api.laser.EnumFactoryLaserColor;
import powercrystals.minefactoryreloaded.api.mob.IFactoryGrindable;
import powercrystals.minefactoryreloaded.api.mob.IFactoryRanchable;
import powercrystals.minefactoryreloaded.api.mob.IRandomMobProvider;
import powercrystals.minefactoryreloaded.api.plant.*;
import powercrystals.minefactoryreloaded.api.rednet.IRedNetLogicCircuit;
import powercrystals.minefactoryreloaded.core.UtilInventory;
import powercrystals.minefactoryreloaded.core.WeightedRandomItemStack;
import powercrystals.minefactoryreloaded.setup.MFRThings;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.util.*;

import static powercrystals.minefactoryreloaded.setup.MFRThings.fakeLaserBlock;

public abstract class MFRRegistry {

	private static Map<Item, IFactoryPlantable> _plantables = new HashMap<>();

	private static Map<Block, IFactoryHarvestable> _harvestables = new HashMap<>();

	private static Map<Item, IFactoryFertilizer> _fertilizers = new HashMap<>();

	private static Map<Block, IFactoryFertilizable> _fertilizables = new HashMap<>();

	private static Map<Class<? extends EntityLivingBase>, IFactoryRanchable> _ranchables = new HashMap<>();

	private static Map<String, ILiquidDrinkHandler> _liquidDrinkHandlers = new HashMap<>();

	private static Map<Item, INeedleAmmo> _needleAmmoTypes = new HashMap<>();

	private static List<Block> _fruitLogBlocks = new ArrayList<>();
	private static Map<Block, IFactoryFruit> _fruitBlocks = new HashMap<>();

	private static List<WeightedRandom.Item> _sludgeDrops = new ArrayList<>();

	private static Set<ResourceLocation> _rubberTreeBiomes = new HashSet<>();

	private static List<IRedNetLogicCircuit> _redNetLogicCircuits = new ArrayList<>();

	private static List<IFactoryTool> _reactiveToolHandlers = new ArrayList<>();

	private static Map<Class<? extends EntityLivingBase>, IFactoryGrindable> _grindables = new HashMap<>();
	private static List<Class<?>> _grindableBlacklist = new ArrayList<>();

	private static List<Class<?>> _safariNetBlacklist = new ArrayList<>();
	private static List<IMobEggHandler> _eggHandlers = new ArrayList<>();
	private static List<ISafariNetHandler> _safariNetHandlers = new ArrayList<>();
	private static List<IRandomMobProvider> _randomMobProviders = new ArrayList<>();

	private static Map<Class<? extends EntityLivingBase>, IMobSpawnHandler> _spawnHandlers = new HashMap<>();
	private static List<String> _autoSpawnerBlacklist = new ArrayList<>();
	private static List<Class<?>> _autoSpawnerClassBlacklist = new ArrayList<>();
	private static TObjectIntHashMap<String> _autoSpawnerCostMap = new TObjectIntHashMap<>(10, 0.5f, 0);

	private static List<Class<?>> _slaughterhouseBlacklist = new ArrayList<>();

	private static List<Class<? extends Entity>> _conveyorBlacklist = new ArrayList<>();

	private static Map<String, Boolean> _unifierBlacklist = new TreeMap<>();

	private static List<WeightedRandom.Item> _laserOres = new ArrayList<>();
	private static Map<Integer, List<ItemStack>> _laserPreferredOres = new HashMap<>(16);

	public static void registerPlantable(IFactoryPlantable plantable) {

		_plantables.put(plantable.getSeed(), plantable);
	}

	public static Map<Item, IFactoryPlantable> getPlantables() {

		return _plantables;
	}

	public static void registerHarvestable(IFactoryHarvestable harvestable) {

		_harvestables.put(harvestable.getPlant(), harvestable);
	}

	public static Map<Block, IFactoryHarvestable> getHarvestables() {

		return _harvestables;
	}

	public static void registerFertilizable(IFactoryFertilizable fertilizable) {

		_fertilizables.put(fertilizable.getPlant(), fertilizable);
	}

	public static Map<Block, IFactoryFertilizable> getFertilizables() {

		return _fertilizables;
	}

	public static void registerFertilizer(IFactoryFertilizer fertilizer) {

		_fertilizers.put(fertilizer.getFertilizer(), fertilizer);
	}

	public static Map<Item, IFactoryFertilizer> getFertilizers() {

		return _fertilizers;
	}

	public static void registerRanchable(IFactoryRanchable ranchable) {

		_ranchables.put(ranchable.getRanchableEntity(), ranchable);
	}

	public static Map<Class<? extends EntityLivingBase>, IFactoryRanchable> getRanchables() {

		return _ranchables;
	}

	public static void registerGrindable(IFactoryGrindable grindable) {

		_grindables.put(grindable.getGrindableEntity(), grindable);
	}

	public static Map<Class<? extends EntityLivingBase>, IFactoryGrindable> getGrindables() {

		return _grindables;
	}

	public static void registerGrinderBlacklist(Class<?> ungrindable) {

		_grindableBlacklist.add(ungrindable);
		if (MFRRegistry._safariNetBlacklist.contains(ungrindable))
			_slaughterhouseBlacklist.add(ungrindable);
	}

	public static List<Class<?>> getGrinderBlacklist() {

		return _grindableBlacklist;
	}

	public static List<Class<?>> getSlaughterhouseBlacklist() {

		return _slaughterhouseBlacklist;
	}

	public static void registerSludgeDrop(int weight, @Nonnull ItemStack drop) {

		_sludgeDrops.add(new WeightedRandomItemStack(drop.copy(), weight));
	}

	public static List<WeightedRandom.Item> getSludgeDrops() {

		return _sludgeDrops;
	}

	public static void registerMobEggHandler(IMobEggHandler handler) {

		_eggHandlers.add(handler);
	}

	public static List<IMobEggHandler> getModMobEggHandlers() {

		return _eggHandlers;
	}

	public static void registerSafariNetHandler(ISafariNetHandler handler) {

		_safariNetHandlers.add(handler);
	}

	public static List<ISafariNetHandler> getSafariNetHandlers() {

		return _safariNetHandlers;
	}

	public static void registerRubberTreeBiome(ResourceLocation biome) {

		_rubberTreeBiomes.add(biome);
	}

	public static Set<ResourceLocation> getRubberTreeBiomes() {

		return _rubberTreeBiomes;
	}

	public static void registerSafariNetBlacklist(Class<?> entityClass) {

		_safariNetBlacklist.add(entityClass);
		if (MFRRegistry._grindableBlacklist.contains(entityClass))
			_slaughterhouseBlacklist.add(entityClass);
	}

	public static List<Class<?>> getSafariNetBlacklist() {

		return _safariNetBlacklist;
	}

	public static void registerRandomMobProvider(IRandomMobProvider mobProvider) {

		_randomMobProviders.add(mobProvider);
	}

	public static List<IRandomMobProvider> getRandomMobProviders() {

		return _randomMobProviders;
	}

	public static void registerLiquidDrinkHandler(String liquidId, ILiquidDrinkHandler liquidDrinkHandler) {

		_liquidDrinkHandlers.put(liquidId, liquidDrinkHandler);
	}

	public static Map<String, ILiquidDrinkHandler> getLiquidDrinkHandlers() {

		return _liquidDrinkHandlers;
	}

	public static void registerRedNetLogicCircuit(IRedNetLogicCircuit circuit) {

		_redNetLogicCircuits.add(circuit);
	}

	public static List<IRedNetLogicCircuit> getRedNetLogicCircuits() {

		return _redNetLogicCircuits;
	}

	public static void registerLaserOre(int weight, @Nonnull ItemStack ore) {

		for (WeightedRandom.Item item : _laserOres)
			if (UtilInventory.stacksEqual(((WeightedRandomItemStack) item).getStack(), ore)) {
				item.itemWeight += weight;
				item.itemWeight /= 2;
				return;
			}
		_laserOres.add(new WeightedRandomItemStack(ore.copy(), weight));
	}

	public static List<WeightedRandom.Item> getLaserOres() {

		return _laserOres;
	}

	public static void registerFruitLogBlock(Block fruitLogBlock) {

		_fruitLogBlocks.add(fruitLogBlock);
	}

	public static List<Block> getFruitLogBlocks() {

		return _fruitLogBlocks;
	}

	public static void registerFruit(IFactoryFruit fruit) {

		_fruitBlocks.put(fruit.getPlant(), fruit);
	}

	public static Map<Block, IFactoryFruit> getFruits() {

		return _fruitBlocks;
	}

	public static void registerAutoSpawnerBlacklistClass(Class<? extends EntityLivingBase> entityClass) {

		_autoSpawnerClassBlacklist.add(entityClass);
	}

	public static List<Class<?>> getAutoSpawnerClassBlacklist() {

		return _autoSpawnerClassBlacklist;
	}

	public static void registerAutoSpawnerBlacklist(String entityString) {

		_autoSpawnerBlacklist.add(entityString);
	}

	public static List<String> getAutoSpawnerBlacklist() {

		return _autoSpawnerBlacklist;
	}

	public static void registerSpawnHandler(IMobSpawnHandler spawnHandler) {

		_spawnHandlers.put(spawnHandler.getMobClass(), spawnHandler);
	}

	public static Map<Class<? extends EntityLivingBase>, IMobSpawnHandler> getSpawnHandlers() {

		return _spawnHandlers;
	}

	public static void setBaseSpawnCost(String id, int cost) {

		_autoSpawnerCostMap.put(id, cost);
	}

	public static int getBaseSpawnCost(String id) {

		return _autoSpawnerCostMap.get(id);
	}

	public static void registerUnifierBlacklist(String string) {

		_unifierBlacklist.put(string, null);
	}

	public static Map<String, Boolean> getUnifierBlacklist() {

		return _unifierBlacklist;
	}

	public static void registerConveyorBlacklist(Class<? extends Entity> entityClass) {

		_conveyorBlacklist.add(entityClass);
	}

	public static List<Class<? extends Entity>> getConveyorBlacklist() {

		return _conveyorBlacklist;
	}

	public static void addLaserPreferredOre(int color, @Nonnull ItemStack ore) {

		if (color < 0 || 16 <= color) return;

		List<ItemStack> oresForColor = _laserPreferredOres.get(color);

		if (oresForColor == null) {
			NonNullList<ItemStack> oresList = NonNullList.create();
			oresList.add(ore);
			_laserPreferredOres.put(color, oresList);
		} else {
			for (@Nonnull ItemStack registeredOre : oresForColor) {
				if (UtilInventory.stacksEqual(registeredOre, ore)) {
					return;
				}
			}
			oresForColor.add(ore);
		}
	}

	public static List<ItemStack> getLaserPreferredOres(int color) {

		return _laserPreferredOres.get(color);
	}

	public static void registerNeedleAmmoType(Item item, INeedleAmmo ammo) {

		_needleAmmoTypes.put(item, ammo);
	}

	public static Map<Item, INeedleAmmo> getNeedleAmmoTypes() {

		return _needleAmmoTypes;
	}

	public static List<IFactoryTool> getToolHandlers() {

		return _reactiveToolHandlers;
	}

	public static void registerToolHandler(IFactoryTool handler) {

		_reactiveToolHandlers.add(handler);
	}

	// INTERNAL ONLY

	static void setup() {

		try {
			Field field = IMFRIntegrator.class.getField("REGISTRY");
			field.setAccessible(true);

			ReflectionManager.removeFinal(field);

			field.set(null, new IMFRIntegrator.IRegistry() {

					@Override
					public void registerPlantable(@Nonnull IFactoryPlantable plantable) {

						MFRRegistry.registerPlantable(plantable);
					}

					@Override
					public void registerHarvestable(@Nonnull IFactoryHarvestable harvestable) {

						MFRRegistry.registerHarvestable(harvestable);
					}

					@Override
					public void registerFertilizable(@Nonnull IFactoryFertilizable fertilizable) {

						MFRRegistry.registerFertilizable(fertilizable);
					}

					@Override
					public void registerFertilizer(@Nonnull IFactoryFertilizer fertilizer) {

						MFRRegistry.registerFertilizer(fertilizer);
					}

					@Override
					public void registerRanchable(@Nonnull IFactoryRanchable ranchable) {

						MFRRegistry.registerRanchable(ranchable);
					}

					@Override
					public void registerGrindable(@Nonnull IFactoryGrindable grindable) {

						MFRRegistry.registerGrindable(grindable);
					}

					@Override
					public void registerGrinderBlacklist(@Nonnull Class<?> ungrindable) {

						MFRRegistry.registerGrinderBlacklist(ungrindable);
					}

					@Override
					public void registerSludgeDrop(int weight, @Nonnull ItemStack drop) {

						MFRRegistry.registerSludgeDrop(weight, drop);
					}

					@Override
					public void registerSafariNetHandler(@Nonnull ISafariNetHandler handler) {

						MFRRegistry.registerSafariNetHandler(handler);
					}

					@Override
					public void registerMobEggHandler(@Nonnull IMobEggHandler handler) {

						MFRRegistry.registerMobEggHandler(handler);
					}

					@Override
					public void registerRubberTreeBiome(@Nonnull ResourceLocation biome) {

						MFRRegistry.registerRubberTreeBiome(biome);
					}

					@Override
					public void registerSafariNetBlacklist(@Nonnull Class<?> entityClass) {

						MFRRegistry.registerSafariNetBlacklist(entityClass);
					}

					@Override
					public void registerRandomMobProvider(@Nonnull IRandomMobProvider mobProvider) {

						MFRRegistry.registerRandomMobProvider(mobProvider);
					}

					@Override
					public void registerLiquidDrinkHandler(@Nonnull String fluidID, @Nonnull ILiquidDrinkHandler liquidDrinkHandler) {

						MFRRegistry.registerLiquidDrinkHandler(fluidID, liquidDrinkHandler);
					}

					@Override
					public void registerLaserOre(int weight, @Nonnull ItemStack ore) {

						MFRRegistry.registerLaserOre(weight, ore);
					}

					@Override
					public void addLaserPreferredOre(@Nonnull EnumFactoryLaserColor color, @Nonnull ItemStack ore) {

						MFRRegistry.addLaserPreferredOre(color.ordinal(), ore);
					}

					@Override
					public void registerFruitLogBlock(@Nonnull Block fruitLogBlock) {

						MFRRegistry.registerFruitLogBlock(fruitLogBlock);
					}

					@Override
					public void registerFruit(@Nonnull IFactoryFruit fruit) {

						MFRRegistry.registerFruit(fruit);
					}

					@Override
					public void registerAutoSpawnerBlacklistClass(@Nonnull Class<? extends EntityLivingBase> entityClass) {

						MFRRegistry.registerAutoSpawnerBlacklistClass(entityClass);
					}

					@Override
					public void registerAutoSpawnerBlacklist(@Nonnull String entityString) {

						MFRRegistry.registerAutoSpawnerBlacklist(entityString);
					}

					@Override
					public void registerSpawnHandler(@Nonnull IMobSpawnHandler spawnHandler) {

						MFRRegistry.registerSpawnHandler(spawnHandler);
					}

					@Override
					public void registerUnifierBlacklist(@Nonnull String oredict) {

						MFRRegistry.registerUnifierBlacklist(oredict);
					}

					@Override
					public void registerConveyorBlacklist(@Nonnull Class<? extends Entity> entityClass) {

						MFRRegistry.registerConveyorBlacklist(entityClass);
					}

					@Override
					public void registerRedNetLogicCircuit(@Nonnull IRedNetLogicCircuit circuit) {

						MFRRegistry.registerRedNetLogicCircuit(circuit);
					}

					@Override
					public void addToolHandler(IFactoryTool handler) {

						MFRRegistry.registerToolHandler(handler);
					}

					@Override
					public void registerNeedleAmmoType(@Nonnull Item item, @Nonnull INeedleAmmo ammo) {

						MFRRegistry.registerNeedleAmmoType(item, ammo);
					}

				});
		} catch (IllegalAccessException | NoSuchFieldException e) {
			e.printStackTrace();
		}
	}

	private static Map<String, String> remaps = new HashMap<>();
	private static Map<String, Block> blocks = new LinkedHashMap<>();
	private static Map<String, Item> items = new LinkedHashMap<>();
	static {
		remaps.put("liquid", null);
		remaps.put("armor", null);
		remaps.put("decorative", null);

		remaps.put("still", "fluid");
		remaps.put("laserair", "fake_laser");
		remaps.put("pinkslime", "pink_slime");
		remaps.put("singleuse", "single_use");
		remaps.put("rubberwood", "rubber_wood");
		remaps.put("ceramicdye", "ceramic_dye");
		remaps.put("fishingrod", "fishing_rod");
		remaps.put("milkbottle", "milk_bottle");
		remaps.put("laserfocus", "laser_focus");
		remaps.put("xpextractor", "xp_extractor");
		remaps.put("vinescaffold", "vine_scaffold");
		remaps.put("machineblock", "machine_block");
		remaps.put("stainedglass", "stained_glass");
		remaps.put("portaspawner", "porta_spawner");
		remaps.put("sugarcharcoal", "sugar_charcoal");
		remaps.put("potatolauncher", "potato_launcher");
		remaps.put("rocketlauncher", "rocket_launcher");

		remaps.put("needlegun", "needle_gun");

		remaps.put("tile.mfr.decorativebrick", "brick");
		remaps.put("tile.mfr.decorativestone", "stone");
		remaps.put("item.mfr.bucket.plasticcup", "plastic_cup");
		remaps.put("item.mfr.armor.boots.plastic", "plastic_boots");
		remaps.put("item.mfr.pinkslimeball", "pinkslime");
		remaps.put("tile.mfr.cable.redstone", "rednet_cable");
		remaps.put("cable.redstone", "rednet_cable");
		remaps.put("tile.mfr.cable.plastic", "plastic_pipe");
		remaps.put("cable.plastic", "plastic_pipe");
		remaps.put(MFRProps.PREFIX + "plastic", "plastic_block");

		// VANILLA REMAPS -- use in recipe naming
		remaps.put("tile.pistonBase", "piston");
		remaps.put("tile.pistonStickyBase", "sticky_piston");
		remaps.put("tile.brick", "brick_block");
		remaps.put("tile.wood.jungle", "jungle_planks");
		remaps.put("tile.stoneMoss", "mossy_cobblestone");
		remaps.put("tile.stonebricksmooth.mossy", "mossy_stonebrick");

		// FIXME: TEMPORARY for dev worlds
		remaps.put("stained_glass", "stained_glass_block");
		remaps.put("fertile_soil", "farmland");
		remaps.put("mobessence", "mob_essence");
		remaps.put("chocolatemilk", "chocolate_milk");
		remaps.put("mushroomsoup", "mushroom_soup");
	}

	private static String remapPhrase(String s) {

		if (!remaps.containsKey(s))
			return s;
		return remaps.get(s);
	}

	private static String remapInternal(String[] v, int o, int l) {

		StringBuilder nameBuilder = new StringBuilder(l+10);

		for (int i = o, e = v.length; i < e; ++i)
			v[i - o] = remapPhrase(v[i]);
		for (int i = 0, e = v.length - o; i < e; ++i)
			if (v[i] != null)
				nameBuilder.append('_').append(v[i]);

		return nameBuilder.substring(1);
	}

	public static String remapName(String s, int offset) {

		String name = remaps.get(s);

		if (name == null) {
			String[] v = s.split("\\.");
			if (v.length < offset + 1)
				return null;

			name = remapInternal(v, offset, s.length());

			remaps.put(s, name);
		}

		return name;
	}

	private static String remapName(String s) {

		String name = remaps.get(s);

		if (name == null) {
			String[] v = s.split("\\.");
			if (v.length < 3)
				return null;

			name = remapInternal(v, 2, s.length());

			remaps.put(s, name);
		}

		return name;
	}

	private static Block remapBlock(ResourceLocation id) {

		Block block = blocks.get(remaps.get(id.toString()));
		if (block == null)
			block = blocks.get(id.getResourcePath());
		if (block == null) {
			String name = id.getResourcePath();
			name = remapInternal(name.split("[._]"), 0, name.length());
			if (name != null)
				block = blocks.get(name);
		}

		return block;
	}

	private static Item remapItem(ResourceLocation id) {

		Item item = items.get(remaps.get(id.toString()));
		if (item == null)
			item = items.get(id.getResourcePath());
		if (item == null) {
			String name = id.getResourcePath();
			name = remapInternal(name.split("[._]"), 0, name.length());
			if (name != null)
				item = items.get(name);
		}
		return item;
	}

	public static void registerBlock(Block block, ItemBlock itemBlock) {

		String name = remapName(block.getUnlocalizedName());
		blocks.put(name, block.setRegistryName(MFRProps.MOD_ID, name));
		if (itemBlock != null) {
			items.put(name, itemBlock.setRegistryName(MFRProps.MOD_ID, name));
		}
	}
	
	public static void registerItem(Item item) {

		String name = remapName(item.getUnlocalizedName());
		items.put(name, item.setRegistryName(MFRProps.MOD_ID, name));
	}

	public static Item getItemBlock(Block block) {

		return items.get(block.getRegistryName().getResourcePath());
	}

	@Mod.EventBusSubscriber(modid = MFRProps.MOD_ID)
	private static class RegistryHandler {

		@SubscribeEvent
		public static void registerStuff(RegistryEvent.Register<Block> evt) {

			for (Block item : blocks.values()) {
				evt.getRegistry().register(item);
			}
		}

		@SubscribeEvent
		public static void registerItems(RegistryEvent.Register<Item> evt) {

			for (Item item : items.values()) {
				evt.getRegistry().register(item);
			}

			MFRThings.registerOredict();
		}

		@SubscribeEvent
		public static void missingBlockMappings(RegistryEvent.MissingMappings<Block> evt) {

			for (RegistryEvent.MissingMappings.Mapping<Block> mapping : evt.getMappings()) {
				Block block = MFRRegistry.remapBlock(mapping.key);
				if (block != null)
					mapping.remap(block);
				else if ("tile.null".equals(mapping.key.getResourcePath()))
					mapping.remap(fakeLaserBlock);
				else
					mapping.warn();
			}
		}

		@SubscribeEvent
		public static void missingItemMappings(RegistryEvent.MissingMappings<Item> evt) {

			for (RegistryEvent.MissingMappings.Mapping<Item> mapping : evt.getMappings()) {
				Item item = MFRRegistry.remapItem(mapping.key);
				if (item != null)
					mapping.remap(item);
				else
					mapping.warn();
			}
		}

	}

}
