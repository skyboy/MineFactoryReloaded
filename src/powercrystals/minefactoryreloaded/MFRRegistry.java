package powercrystals.minefactoryreloaded;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandomItem;

import powercrystals.core.random.WeightedRandomItemStack;
import powercrystals.core.util.UtilInventory;
import powercrystals.minefactoryreloaded.api.IFactoryFertilizable;
import powercrystals.minefactoryreloaded.api.IFactoryFertilizer;
import powercrystals.minefactoryreloaded.api.IFactoryFruit;
import powercrystals.minefactoryreloaded.api.IFactoryGrindable;
import powercrystals.minefactoryreloaded.api.IFactoryHarvestable;
import powercrystals.minefactoryreloaded.api.IFactoryPlantable;
import powercrystals.minefactoryreloaded.api.IFactoryRanchable;
import powercrystals.minefactoryreloaded.api.ILiquidDrinkHandler;
import powercrystals.minefactoryreloaded.api.IMobEggHandler;
import powercrystals.minefactoryreloaded.api.IMobSpawnHandler;
import powercrystals.minefactoryreloaded.api.INeedleAmmo;
import powercrystals.minefactoryreloaded.api.IRandomMobProvider;
import powercrystals.minefactoryreloaded.api.ISafariNetHandler;
import powercrystals.minefactoryreloaded.api.rednet.IRedNetLogicCircuit;

public abstract class MFRRegistry
{
	private static Map<Integer, IFactoryPlantable> _plantables = new HashMap<Integer, IFactoryPlantable>();
	private static Map<Integer, IFactoryHarvestable> _harvestables = new HashMap<Integer, IFactoryHarvestable>();
	private static Map<Integer, IFactoryFertilizer> _fertilizers = new HashMap<Integer, IFactoryFertilizer>();
	private static Map<Integer, IFactoryFertilizable> _fertilizables = new HashMap<Integer, IFactoryFertilizable>();
	private static Map<Class<?>, IFactoryRanchable> _ranchables = new HashMap<Class<?>, IFactoryRanchable>();
	private static Map<Class<?>, IFactoryGrindable> _grindables = new HashMap<Class<?>, IFactoryGrindable>();
	private static Map<Class<?>, List<ItemStack>> _breederFoods = new HashMap<Class<?>, List<ItemStack>>();
	private static Map<String, ILiquidDrinkHandler> _liquidDrinkHandlers = new HashMap<String, ILiquidDrinkHandler>();
	private static Map<Integer, INeedleAmmo> _needleAmmoTypes = new HashMap<Integer, INeedleAmmo>();
	private static Map<Class<? extends EntityLivingBase>, IMobSpawnHandler> _spawnHandlers =
			new HashMap<Class<? extends EntityLivingBase>, IMobSpawnHandler>();
	
	private static List<Integer> _fruitLogBlocks = new ArrayList<Integer>();
	private static Map<Integer, IFactoryFruit> _fruitBlocks = new HashMap<Integer, IFactoryFruit>();

	private static List<WeightedRandomItem> _sludgeDrops  = new ArrayList<WeightedRandomItem>();
	private static List<IMobEggHandler> _eggHandlers = new ArrayList<IMobEggHandler>();
	private static List<ISafariNetHandler> _safariNetHandlers = new ArrayList<ISafariNetHandler>();
	private static List<String> _rubberTreeBiomes = new ArrayList<String>();
	private static List<Class<?>> _safariNetBlacklist = new ArrayList<Class<?>>();
	private static List<IRandomMobProvider> _randomMobProviders = new ArrayList<IRandomMobProvider>();
	private static List<IRedNetLogicCircuit> _redNetLogicCircuits = new ArrayList<IRedNetLogicCircuit>();
	private static List<WeightedRandomItem> _laserOres  = new ArrayList<WeightedRandomItem>();
	private static List<Class<?>> _grindableBlacklist = new ArrayList<Class<?>>();
	private static List<String> _autoSpawnerBlacklist = new ArrayList<String>();
	private static List<Class<?>> _autoSpawnerClassBlacklist = new ArrayList<Class<?>>();
	private static List<Class<?>> _slaughterhouseBlacklist = new ArrayList<Class<?>>();
	private static List<Class<?>> _conveyerBlacklist = new ArrayList<Class<?>>();
	private static Map<String, Boolean> _unifierBlacklist  = new TreeMap<String, Boolean>();

	private static Map<Integer, List<ItemStack>> _laserPreferredOres = new HashMap<Integer, List<ItemStack>>(16);

	public static void registerPlantable(IFactoryPlantable plantable)
	{
		_plantables.put(new Integer(plantable.getSeedId()), plantable);
	}

	public static Map<Integer, IFactoryPlantable> getPlantables()
	{
		return _plantables;
	}

	public static void registerHarvestable(IFactoryHarvestable harvestable)
	{
		_harvestables.put(harvestable.getPlantId(), harvestable);
	}

	public static Map<Integer, IFactoryHarvestable> getHarvestables()
	{
		return _harvestables;
	}

	public static void registerFertilizable(IFactoryFertilizable fertilizable)
	{
		_fertilizables.put(fertilizable.getFertilizableBlockId(), fertilizable);
	}

	public static Map<Integer, IFactoryFertilizable> getFertilizables()
	{
		return _fertilizables;
	}

	public static void registerFertilizer(IFactoryFertilizer fertilizer)
	{
		Integer i = new Integer(fertilizer.getFertilizerId());
		if(!_fertilizers.containsKey(i))
		{
			_fertilizers.put(i, fertilizer);
		}
	}

	public static Map<Integer, IFactoryFertilizer> getFertilizers()
	{
		return _fertilizers;
	}

	public static void registerRanchable(IFactoryRanchable ranchable)
	{
		_ranchables.put(ranchable.getRanchableEntity(), ranchable);
	}

	public static Map<Class<?>, IFactoryRanchable> getRanchables()
	{
		return _ranchables;
	}

	public static void registerGrindable(IFactoryGrindable grindable)
	{
		_grindables.put(grindable.getGrindableEntity(), grindable);
	}

	public static Map<Class<?>, IFactoryGrindable> getGrindables()
	{
		return _grindables;
	}

	public static void registerGrinderBlacklist(Class<?> ...ungrindables)
	{
		for (Class<?> ungrindable : ungrindables)
		{
			_grindableBlacklist.add(ungrindable);
			if (MFRRegistry._safariNetBlacklist.contains(ungrindable))
				_slaughterhouseBlacklist.add(ungrindable);
		}
	}

	public static List<Class<?>> getGrinderBlacklist()
	{
		return _grindableBlacklist;
	}

	public static List<Class<?>> getSlaughterhouseBlacklist()
	{
		return _slaughterhouseBlacklist;
	}

	public static void registerSludgeDrop(int weight, ItemStack drop)
	{
		_sludgeDrops.add(new WeightedRandomItemStack(weight, drop.copy()));
	}

	public static List<WeightedRandomItem> getSludgeDrops()
	{
		return _sludgeDrops;
	}

	public static void registerBreederFood(Class<?> entityToBreed, ItemStack food)
	{
		if(_breederFoods.containsKey(entityToBreed))
		{
			_breederFoods.get(entityToBreed).add(food);
		}
		else
		{
			ArrayList<ItemStack> foodToAdd = new ArrayList<ItemStack>();
			foodToAdd.add(food.copy());
			_breederFoods.put(entityToBreed, foodToAdd);
		}
	}

	public static Map<Class<?>, List<ItemStack>> getBreederFoods()
	{
		return _breederFoods;
	}

	public static void registerMobEggHandler(IMobEggHandler handler)
	{
		_eggHandlers.add(handler);
	}

	public static List<IMobEggHandler> getModMobEggHandlers()
	{
		return _eggHandlers;
	}

	public static void registerSafariNetHandler(ISafariNetHandler handler)
	{
		_safariNetHandlers.add(handler);
	}

	public static List<ISafariNetHandler> getSafariNetHandlers()
	{
		return _safariNetHandlers;
	}

	public static void registerRubberTreeBiome(String biome)
	{
		_rubberTreeBiomes.add(biome);
	}

	public static List<String> getRubberTreeBiomes()
	{
		return _rubberTreeBiomes;
	}

	public static void registerSafariNetBlacklist(Class<?> entityClass)
	{
		_safariNetBlacklist.add(entityClass);
		if (MFRRegistry._grindableBlacklist.contains(entityClass))
			_slaughterhouseBlacklist.add(entityClass);
	}

	public static List<Class<?>> getSafariNetBlacklist()
	{
		return _safariNetBlacklist;
	}

	public static void registerRandomMobProvider(IRandomMobProvider mobProvider)
	{
		_randomMobProviders.add(mobProvider);
	}

	public static List<IRandomMobProvider> getRandomMobProviders()
	{
		return _randomMobProviders;
	}

	public static void registerLiquidDrinkHandler(String liquidId, ILiquidDrinkHandler liquidDrinkHandler)
	{
		_liquidDrinkHandlers.put(liquidId, liquidDrinkHandler);
	}

	public static Map<String, ILiquidDrinkHandler> getLiquidDrinkHandlers()
	{
		return _liquidDrinkHandlers;
	}

	public static void registerRedNetLogicCircuit(IRedNetLogicCircuit circuit)
	{
		_redNetLogicCircuits.add(circuit);
	}

	public static List<IRedNetLogicCircuit> getRedNetLogicCircuits()
	{
		return _redNetLogicCircuits;
	}

	public static void registerLaserOre(int weight, ItemStack ore)
	{
		for (WeightedRandomItem item : _laserOres)
			if (UtilInventory.stacksEqual(((WeightedRandomItemStack)item).getStack(), ore))
			{
				item.itemWeight += weight;
				item.itemWeight /= 2;
				return;
			}
		_laserOres.add(new WeightedRandomItemStack(weight, ore.copy()));
	}

	public static List<WeightedRandomItem> getLaserOres()
	{
		return _laserOres;
	}

	public static void registerFruitLogBlockId(Integer fruitLogBlockId)
	{
		_fruitLogBlocks.add(fruitLogBlockId);
	}

	public static List<Integer> getFruitLogBlockIds()
	{
		return _fruitLogBlocks;
	}

	public static void registerFruit(IFactoryFruit fruit)
	{
		_fruitBlocks.put(fruit.getSourceBlockId(), fruit);
	}

	public static Map<Integer, IFactoryFruit> getFruits()
	{
		return _fruitBlocks;
	}
	
	public static void registerAutoSpawnerBlacklistClass(Class<?> entityClass)
	{
		_autoSpawnerClassBlacklist.add(entityClass);
	}

	public static List<Class<?>> getAutoSpawnerClassBlacklist()
	{
		return _autoSpawnerClassBlacklist;
	}

	public static void registerAutoSpawnerBlacklist(String entityString)
	{
		_autoSpawnerBlacklist.add(entityString);
	}

	public static List<String> getAutoSpawnerBlacklist()
	{
		return _autoSpawnerBlacklist;
	}
	
	public static void registerSpawnHandler(IMobSpawnHandler spawnHandler)
	{
		_spawnHandlers.put(spawnHandler.getMobClass(), spawnHandler);
	}
	
	public static Map<Class<? extends EntityLivingBase>, IMobSpawnHandler> getSpawnHandlers()
	{
		return _spawnHandlers;
	}

	public static void registerUnifierBlacklist(String string)
	{
		_unifierBlacklist.put(string, null);
	}

	public static Map<String, Boolean> getUnifierBlacklist()
	{
		return _unifierBlacklist;
	}
	
	public static void registerConveyerBlacklist(Class<?> entityClass)
	{
		_conveyerBlacklist.add(entityClass);
	}

	public static List<Class<?>> getConveyerBlacklist()
	{
		return _conveyerBlacklist;
	}
	
	@Deprecated
	public static void setLaserPreferredOre(int color, ItemStack ore)
	{
		addLaserPreferredOre(color, ore);
	}

	public static void addLaserPreferredOre(int color, ItemStack ore)
	{
		if(color < 0 || 16 <= color) return;
		
		List<ItemStack> oresForColor = _laserPreferredOres.get(color);
		
		if(oresForColor == null)
		{
			List<ItemStack> oresList = new ArrayList<ItemStack>();
			oresList.add(ore);
			_laserPreferredOres.put(color, oresList);
		}
		else
		{	
			for(ItemStack registeredOre : oresForColor)
			{
				if(UtilInventory.stacksEqual(registeredOre, ore))
				{
					return;
				}
			}
			oresForColor.add(ore);
		}
	}

	public static List<ItemStack> getLaserPreferredOres(int color)
	{
		return _laserPreferredOres.get(color);
	}
	
	public static void registerNeedleAmmoType(Integer itemId, INeedleAmmo ammo)
	{
		Integer i = new Integer(itemId);
		if(!_needleAmmoTypes.containsKey(i))
		{
			_needleAmmoTypes.put(i, ammo);
		}
	}
	
	public static Map<Integer, INeedleAmmo> getNeedleAmmoTypes()
	{
		return _needleAmmoTypes;
	}
}
