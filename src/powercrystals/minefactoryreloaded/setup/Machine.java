package powercrystals.minefactoryreloaded.setup;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.registry.GameRegistry;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Icon;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Property;

import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.block.BlockFactoryMachine;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactory;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryPowered;
import powercrystals.minefactoryreloaded.tile.machine.TileEntityAutoAnvil;
import powercrystals.minefactoryreloaded.tile.machine.TileEntityAutoBrewer;
import powercrystals.minefactoryreloaded.tile.machine.TileEntityAutoDisenchanter;
import powercrystals.minefactoryreloaded.tile.machine.TileEntityAutoEnchanter;
import powercrystals.minefactoryreloaded.tile.machine.TileEntityAutoJukebox;
import powercrystals.minefactoryreloaded.tile.machine.TileEntityAutoSpawner;
import powercrystals.minefactoryreloaded.tile.machine.TileEntityBioFuelGenerator;
import powercrystals.minefactoryreloaded.tile.machine.TileEntityBioReactor;
import powercrystals.minefactoryreloaded.tile.machine.TileEntityBlockBreaker;
import powercrystals.minefactoryreloaded.tile.machine.TileEntityBlockPlacer;
import powercrystals.minefactoryreloaded.tile.machine.TileEntityBlockSmasher;
import powercrystals.minefactoryreloaded.tile.machine.TileEntityBreeder;
import powercrystals.minefactoryreloaded.tile.machine.TileEntityChronotyper;
import powercrystals.minefactoryreloaded.tile.machine.TileEntityChunkLoader;
import powercrystals.minefactoryreloaded.tile.machine.TileEntityCollector;
import powercrystals.minefactoryreloaded.tile.machine.TileEntityComposter;
import powercrystals.minefactoryreloaded.tile.machine.TileEntityDeepStorageUnit;
import powercrystals.minefactoryreloaded.tile.machine.TileEntityEjector;
import powercrystals.minefactoryreloaded.tile.machine.TileEntityEnchantmentRouter;
import powercrystals.minefactoryreloaded.tile.machine.TileEntityFertilizer;
import powercrystals.minefactoryreloaded.tile.machine.TileEntityFisher;
import powercrystals.minefactoryreloaded.tile.machine.TileEntityFruitPicker;
import powercrystals.minefactoryreloaded.tile.machine.TileEntityGrinder;
import powercrystals.minefactoryreloaded.tile.machine.TileEntityHarvester;
import powercrystals.minefactoryreloaded.tile.machine.TileEntityItemRouter;
import powercrystals.minefactoryreloaded.tile.machine.TileEntityLaserDrill;
import powercrystals.minefactoryreloaded.tile.machine.TileEntityLaserDrillPrecharger;
import powercrystals.minefactoryreloaded.tile.machine.TileEntityLavaFabricator;
import powercrystals.minefactoryreloaded.tile.machine.TileEntityLiquiCrafter;
import powercrystals.minefactoryreloaded.tile.machine.TileEntityLiquidRouter;
import powercrystals.minefactoryreloaded.tile.machine.TileEntityMeatPacker;
import powercrystals.minefactoryreloaded.tile.machine.TileEntityMobCounter;
import powercrystals.minefactoryreloaded.tile.machine.TileEntityOilFabricator;
import powercrystals.minefactoryreloaded.tile.machine.TileEntityPlanter;
import powercrystals.minefactoryreloaded.tile.machine.TileEntityRancher;
import powercrystals.minefactoryreloaded.tile.machine.TileEntityRedNote;
import powercrystals.minefactoryreloaded.tile.machine.TileEntitySewer;
import powercrystals.minefactoryreloaded.tile.machine.TileEntitySlaughterhouse;
import powercrystals.minefactoryreloaded.tile.machine.TileEntitySludgeBoiler;
import powercrystals.minefactoryreloaded.tile.machine.TileEntitySteamTurbine;
import powercrystals.minefactoryreloaded.tile.machine.TileEntityUnifier;
import powercrystals.minefactoryreloaded.tile.machine.TileEntityVet;
import powercrystals.minefactoryreloaded.tile.machine.TileEntityWeather;

public class Machine
{
	public static final Material MATERIAL = new MachineMaterial(MapColor.ironColor);
	private static List<Machine> _machines = new LinkedList<Machine>();
	private static Map<Integer, Machine> _machineMappings = new HashMap<Integer, Machine>();
	private static Map<Integer, Integer> _highestMetas = new HashMap<Integer, Integer>();
	
	public static Machine Planter = new Machine(0, 0, "Planter", TileEntityPlanter.class, "factoryPlanter", 160, 8000);
	public static Machine Fisher = new Machine(0, 1, "Fisher", TileEntityFisher.class, "factoryFisher", 20, 16000);
	public static Machine Harvester = new Machine(0, 2, "Harvester", TileEntityHarvester.class, "factoryHarvester", 240, 16000);
	public static Machine Rancher = new Machine(0, 3, "Rancher", TileEntityRancher.class, "factoryRancher", 320, 32000);
	public static Machine Fertilizer = new Machine(0, 4, "Fertilizer", TileEntityFertilizer.class, "factoryFertilizer", 960, 32000);
	public static Machine Vet = new Machine(0, 5, "Vet", TileEntityVet.class, "factoryVet", 320, 32000);
	public static Machine ItemCollector = new Machine(0, 6, "ItemCollector", TileEntityCollector.class, "factoryItemCollector");
	public static Machine BlockBreaker = new Machine(0, 7, "BlockBreaker", TileEntityBlockBreaker.class, "factoryBlockBreaker", 960, 64000);
	public static Machine WeatherCollector = new Machine(0, 8, "WeatherCollector", TileEntityWeather.class, "factoryWeather", 40, 16000);
	public static Machine SludgeBoiler = new Machine(0, 9, "SludgeBoiler", TileEntitySludgeBoiler.class, "factorySludgeBoiler", 20, 16000);
	public static Machine Sewer = new Machine(0, 10, "Sewer", TileEntitySewer.class, "factorySewer");
	public static Machine Composter = new Machine(0, 11, "Composter", TileEntityComposter.class, "factoryComposter", 20, 16000);
	public static Machine Breeder = new Machine(0, 12, "Breeder", TileEntityBreeder.class, "factoryBreeder", 640, 16000);
	public static Machine Grinder = new Machine(0, 13, "Grinder", TileEntityGrinder.class, "factoryGrinder", 3200, 32000);
	public static Machine AutoEnchanter = new Machine(0, 14, "AutoEnchanter", TileEntityAutoEnchanter.class, "factoryEnchanter", 160, 16000);
	public static Machine Chronotyper = new Machine(0, 15, "Chronotyper", TileEntityChronotyper.class, "factoryChronotyper", 1280, 16000);
	
	public static Machine Ejector = new Machine(1, 0, "Ejector", TileEntityEjector.class, "factoryEjector");
	public static Machine ItemRouter = new Machine(1, 1, "ItemRouter", TileEntityItemRouter.class, "factoryItemRouter");
	public static Machine LiquidRouter = new Machine(1, 2, "LiquidRouter", TileEntityLiquidRouter.class, "factoryLiquidRouter");
	public static Machine DeepStorageUnit = new Machine(1, 3, "DeepStorageUnit", TileEntityDeepStorageUnit.class, "factoryDeepStorageUnit") {
		@Override
		public void addInformation(ItemStack stack, EntityPlayer player, List<String> info, boolean adv)
		{
			NBTTagCompound c = stack.getTagCompound();
			if (c != null && c.hasKey("storedStack"))
			{
				ItemStack storedItem = ItemStack.loadItemStackFromNBT(c.getCompoundTag("storedStack"));
				int storedQuantity = c.getInteger("storedQuantity");
				if (storedItem != null & storedQuantity > 0)
				{
					info.add(StatCollector.translateToLocal("tip.info.mfr.dsu.contains") + 
							" " + storedQuantity + " " + storedItem.getDisplayName() +
							(adv ? " (" + storedItem.itemID + ":" +
							storedItem.getItemDamageForDisplay() + ")" : ""));
				}
			}
			super.addInformation(stack, player, info, adv);
		}
	};
	public static Machine LiquiCrafter = new Machine(1, 4, "LiquiCrafter", TileEntityLiquiCrafter.class, "factoryLiquiCrafter");
	public static Machine LavaFabricator = new Machine(1, 5, "LavaFabricator", TileEntityLavaFabricator.class, "factoryLavaFabricator", 200, 16000);
	public static Machine OilFabricator = new Machine(1, 6, "OilFabricator", TileEntityOilFabricator.class, "factoryOilFabricator", 5880, 16000);
	public static Machine AutoJukebox = new Machine(1, 7, "AutoJukebox", TileEntityAutoJukebox.class, "factoryAutoJukebox");
	public static Machine Unifier = new Machine(1, 8, "Unifier", TileEntityUnifier.class, "factoryUnifier");
	public static Machine AutoSpawner = new Machine(1, 9, "AutoSpawner", TileEntityAutoSpawner.class, "factoryAutoSpawner", 600, 32000);
	public static Machine BioReactor = new Machine(1, 10, "BioReactor", TileEntityBioReactor.class, "factoryBioReactor");
	public static Machine BioFuelGenerator = new Machine(1, 11, "BioFuelGenerator", TileEntityBioFuelGenerator.class, "factoryBioFuelGenerator", 160, 10000) {
		@Override
		public void addInformation(ItemStack stack, EntityPlayer player, List<String> info, boolean adv)
		{
			super.addInformation(stack, player, info, adv);
			info.add(StatCollector.translateToLocal("tip.info.mfr.generator.produces"));
		}
	};
	public static Machine AutoDisenchanter = new Machine(1, 12, "AutoDisenchanter", TileEntityAutoDisenchanter.class, "factoryDisenchanter", 320, 16000);
	public static Machine Slaughterhouse = new Machine(1, 13, "Slaughterhouse", TileEntitySlaughterhouse.class, "factorySlaughterhouse", 1000, 16000);
	public static Machine MeatPacker = new Machine(1, 14, "MeatPacker", TileEntityMeatPacker.class, "factoryMeatPacker", 20, 16000);
	public static Machine EnchantmentRouter = new Machine(1, 15, "EnchantmentRouter", TileEntityEnchantmentRouter.class, "factoryEnchantmentRouter");
	
	public static Machine LaserDrill = new Machine(2, 0, "LaserDrill", TileEntityLaserDrill.class, "factoryLaserDrill");
	public static Machine LaserDrillPrecharger = new Machine(2, 1, "LaserDrillPrecharger", TileEntityLaserDrillPrecharger.class, "factoryLaserDrillPrecharger", 5000, 96000);
	public static Machine AutoAnvil = new Machine(2, 2, "AutoAnvil", TileEntityAutoAnvil.class, "factoryAnvil", 160, 16000);
	public static Machine BlockSmasher = new Machine(2, 3, "BlockSmasher", TileEntityBlockSmasher.class, "factoryBlockSmasher", 10, 16000);
	public static Machine RedNote = new Machine(2, 4, "RedNote", TileEntityRedNote.class, "factoryRedNote");
	public static Machine AutoBrewer = new Machine(2, 5, "AutoBrewer", TileEntityAutoBrewer.class, "factoryAutoBrewer", 40, 16000);
	public static Machine FruitPicker = new Machine(2, 6, "FruitPicker", TileEntityFruitPicker.class, "factoryFruitPicker", 320, 16000);
	public static Machine BlockPlacer = new Machine(2, 7, "BlockPlacer", TileEntityBlockPlacer.class, "factoryBlockPlacer", 10, 16000);
	public static Machine MobCounter = new Machine(2, 8, "MobCounter", TileEntityMobCounter.class, "factoryMobCounter");
	public static Machine SteamTurbine = new Machine(2, 9, "SteamTurbine", TileEntitySteamTurbine.class, "factorySteamTurbine", 160, 10000) {
		@Override
		public void addInformation(ItemStack stack, EntityPlayer player, List<String> info, boolean adv)
		{
			super.addInformation(stack, player, info, adv);
			info.add(StatCollector.translateToLocal("tip.info.mfr.generator.produces"));
		}
	};
	public static Machine ChunkLoader = new Machine(2, 10, "ChunkLoader", TileEntityChunkLoader.class, "factoryChunkLoader", Short.MAX_VALUE, Integer.MAX_VALUE, false);
	
	private final int _blockIndex;
	private final int _meta;
	private final int _machineIndex;
	
	private Icon[] _iconsActive = new Icon[6];
	private Icon[] _iconsIdle = new Icon[6];
	
	private final String _name;
	private final String _internalName;
	private final String _tileEntityName;
	private Class<? extends TileEntityFactory> _tileEntityClass;
	
	private int _activationEnergy;
	private int _energyStoredMax;
	private boolean _configurable;
	
	private Property _isRecipeEnabled;
	
	private Machine(int blockIndex, int meta, String name,
			Class<? extends TileEntityFactory> tileEntityClass, String tileEntityName)
	{
		this(blockIndex, meta, name, tileEntityClass, tileEntityName, 0, 0);
	}
	
	private Machine(int blockIndex, int meta, String name,
			Class<? extends TileEntityFactory> tileEntityClass, String tileEntityName,
			int activationEnergy, int energyStoredMax)
	{
		this(blockIndex, meta, name, tileEntityClass, tileEntityName, activationEnergy, energyStoredMax, true);
	}
	
	private Machine(int blockIndex, int meta, String name,
			Class<? extends TileEntityFactory> tileEntityClass, String tileEntityName,
			int activationEnergy, int energyStoredMax, boolean configurable)
	{
		_blockIndex = blockIndex;
		_meta = meta;
		_machineIndex = _meta | (_blockIndex << 4);
		
		if (_meta > 15)
		{
			throw new IllegalArgumentException("Maximum meta value for machines is 15");
		}
		
		if (_machineMappings.containsKey(_machineIndex))
		{
			throw new IllegalArgumentException("Machine with index " + blockIndex + " and meta " +
												meta + " already exists.");
		}
				
		_name = name;
		_internalName = "tile.mfr.machine." + name.toLowerCase();
		_tileEntityName = tileEntityName;
		_tileEntityClass = tileEntityClass;
		
		_activationEnergy = activationEnergy;
		_energyStoredMax = energyStoredMax;
		_configurable = configurable;
		
		_machineMappings.put(_machineIndex, this);
		_machines.add(this);
		
		if(_highestMetas.get(_blockIndex) == null || _highestMetas.get(_blockIndex) < _meta)
		{
			_highestMetas.put(_blockIndex, _meta);
		}
	}
	
	public static Machine getMachineFromIndex(int blockIndex, int meta)
	{
		return _machineMappings.get(meta | (blockIndex << 4));
	}
	
	public static Machine getMachineFromId(int blockId, int meta)
	{
		return  _machineMappings.get(meta | (((BlockFactoryMachine)Block.blocksList[blockId]).getBlockIndex() << 4));
	}
	
	public static int getHighestMetadata(int blockIndex)
	{
		return _highestMetas.get(blockIndex);
	}
	
	public static List<Machine> values()
	{
		return _machines;
	}
	
	public static void LoadTextures(int blockIndex, IconRegister ir)
	{
		for(Machine m : _machines)
		{
			if(m.getBlockIndex() == blockIndex)
			{
				m.loadIcons(ir);
			}
		}
	}
	
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> info, boolean adv)
	{
		String s = "tip.info.mfr." + _name.toLowerCase();
		if (StatCollector.func_94522_b(s))
		{
			s = StatCollector.translateToLocal(s);
			if (s.contains("\n"))
				info.addAll(Arrays.asList(s.split("\n")));
			else
				info.add(s);
		}
	}
	
	public final String getName()
	{
		return _name;
	}
	
	public final String getInternalName()
	{
		return _internalName;
	}
	
	public int getBlockId()
	{
		return MineFactoryReloadedCore.machineBlocks.get(_blockIndex).blockID;
	}
	
	public ItemStack getItemStack()
	{
		return new ItemStack(MineFactoryReloadedCore.machineBlocks.get(_blockIndex), 1, _meta);
	}
	
	public final int getMeta()
	{
		return _meta;
	}
	
	public final int getBlockIndex()
	{
		return _blockIndex;
	}
	
	public final boolean getIsRecipeEnabled()
	{
		return _isRecipeEnabled.getBoolean(true);
	}
	
	public TileEntityFactory getNewTileEntity()
	{
		try
		{
			TileEntityFactory tileEntity = _tileEntityClass.newInstance();
			return tileEntity;
		}
		catch(IllegalAccessException x)
		{
			FMLLog.severe("Unable to create instance of TileEntity from %s", _tileEntityClass.getName());
			return null;
		}
		catch(InstantiationException x)
		{
			FMLLog.severe("Unable to create instance of TileEntity from %s", _tileEntityClass.getName());
			return null;
		}
	}
	
	public final int getActivationEnergyMJ()
	{
		return _activationEnergy / TileEntityFactoryPowered.energyPerMJ;
	}
	
	public final int getActivationEnergy()
	{
		return _activationEnergy;
	}
	
	public final int getMaxEnergyStorage()
	{
		return _energyStoredMax;
	}
	
	public void load(Configuration c)
	{
		_isRecipeEnabled = c.get("Machine", _name + ".Recipe.Enabled", true);
		if (_configurable & _activationEnergy > 0)
		{
			_activationEnergy = c.get("Machine", _name + ".ActivationCostMJ", getActivationEnergyMJ()).getInt() * TileEntityFactoryPowered.energyPerMJ;
		}
		
		MinecraftForge.setBlockHarvestLevel(MineFactoryReloadedCore.machineBlocks.get(_blockIndex), _meta, "pickaxe", 0);
		GameRegistry.registerTileEntity(_tileEntityClass, _tileEntityName);
	}
	
	public void loadIcons(IconRegister ir)
	{
		_iconsActive[0] = ir.registerIcon("minefactoryreloaded:" + getInternalName() + ".active.bottom");
		_iconsActive[1] = ir.registerIcon("minefactoryreloaded:" + getInternalName() + ".active.top");
		_iconsActive[2] = ir.registerIcon("minefactoryreloaded:" + getInternalName() + ".active.front");
		_iconsActive[3] = ir.registerIcon("minefactoryreloaded:" + getInternalName() + ".active.back");
		_iconsActive[4] = ir.registerIcon("minefactoryreloaded:" + getInternalName() + ".active.left");
		_iconsActive[5] = ir.registerIcon("minefactoryreloaded:" + getInternalName() + ".active.right");
		_iconsIdle[0] = ir.registerIcon("minefactoryreloaded:" + getInternalName() + ".idle.bottom");
		_iconsIdle[1] = ir.registerIcon("minefactoryreloaded:" + getInternalName() + ".idle.top");
		_iconsIdle[2] = ir.registerIcon("minefactoryreloaded:" + getInternalName() + ".idle.front");
		_iconsIdle[3] = ir.registerIcon("minefactoryreloaded:" + getInternalName() + ".idle.back");
		_iconsIdle[4] = ir.registerIcon("minefactoryreloaded:" + getInternalName() + ".idle.left");
		_iconsIdle[5] = ir.registerIcon("minefactoryreloaded:" + getInternalName() + ".idle.right");
	}
	
	public Icon getIcon(int side, boolean isActive)
	{
		if(isActive) return _iconsActive[side];
		return _iconsIdle[side];
	}
}
