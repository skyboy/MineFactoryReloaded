package powercrystals.minefactoryreloaded;

import cofh.cofhworld.init.WorldHandler;
import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.entity.EntityList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import org.apache.logging.log4j.Logger;
import powercrystals.minefactoryreloaded.setup.MFRFarmables;
import powercrystals.minefactoryreloaded.gui.MFRGUIHandler;
import powercrystals.minefactoryreloaded.net.CommonProxy;
import powercrystals.minefactoryreloaded.net.EntityHandler;
import powercrystals.minefactoryreloaded.net.GridTickHandler;
import powercrystals.minefactoryreloaded.net.MFRPacket;
import powercrystals.minefactoryreloaded.setup.*;
import powercrystals.minefactoryreloaded.setup.village.VillageCreationHandler;
import powercrystals.minefactoryreloaded.setup.village.Zoologist;
import powercrystals.minefactoryreloaded.tile.machine.processing.TileEntityUnifier;
import powercrystals.minefactoryreloaded.world.MineFactoryReloadedWorldGen;

import static powercrystals.minefactoryreloaded.setup.MFRThings.*;

@SuppressWarnings("unused")
@Mod(modid = MFRProps.MOD_ID, name = MFRProps.MOD_NAME, version = MFRProps.VERSION, dependencies = MFRProps.DEPENDENCIES)
public class MineFactoryReloadedCore extends BaseMod {

	@SidedProxy(clientSide = "powercrystals.minefactoryreloaded.net.ClientProxy",
			serverSide = "powercrystals.minefactoryreloaded.net.ServerProxy")
	public static CommonProxy proxy;

	public static SimpleNetworkWrapper networkWrapper = null;

	public static Object balance = "balance";

	private static MineFactoryReloadedCore instance;

	public static MineFactoryReloadedCore instance() {

		return instance;
	}

	public static Logger log() {

		return instance.getLogger();
	}

	public MineFactoryReloadedCore() {

		FluidRegistry.enableUniversalBucket();
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent evt) {

		instance = this;
		DataFixer.init();
		setConfigFolderBase(evt.getModConfigurationDirectory());

		MFRRegistry.setup();

		RecipeManager.load(evt.getAsmData());
		IntegrationManager.load(evt.getAsmData());

		if (FMLCommonHandler.instance().getSide().isClient()) {
			MFRConfig.loadClientConfig(getClientConfig());
		}
		MFRConfig.loadCommonConfig(getCommonConfig());

		MFRFluids.preInit();
		MFRThings.preInit();
		
		proxy.preInit();

		RecipeManager.configure(this::getConfig);
		IntegrationManager.configure(this::getConfig);

		MFRConfig.saveCommon();
		IntegrationManager.preInit();
	}

	@EventHandler
	public void init(FMLInitializationEvent evt) {

		MinecraftForge.EVENT_BUS.register(rednetCableBlock);
		MinecraftForge.EVENT_BUS.register(new EntityHandler());
		MinecraftForge.EVENT_BUS.register(MFRFluids.INSTANCE);

		proxy.init();
		MFRFarmables.load();

		NetworkRegistry.INSTANCE.registerGuiHandler(this, new MFRGUIHandler());

		MFRPacket.initialize();

		addDispenserBehavior();

		MFRLoot.init();

		Zoologist.init();

		VillagerRegistry.instance().registerVillageCreationHandler(new VillageCreationHandler());

		WorldHandler.registerReloadCallback(() -> WorldHandler.registerFeature(MineFactoryReloadedWorldGen.INSTANCE));

		//UpdateManager.registerUpdater(new UpdateManager(this, null, CoFHProps.DOWNLOAD_URL));
		IntegrationManager.init();
	}

	private void addDispenserBehavior() {

		IBehaviorDispenseItem behavior = new BehaviorDispenseSafariNet();
		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(safariNetItem, behavior);
		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(safariNetSingleItem, behavior);
		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(safariNetJailerItem, behavior);
	}

	private void addChestGenItems() {

		/*
		//{ DimensionalDoors chestgen compat
		// reference weights[iron: 160; coal: 120; gold: 80; golden apple: 10]
		ChestGenHooks.getInfo("dimensionalDungeonChest").addItem(
			new WeightedRandomChestContent(new ItemStack(safariNetJailerItem), 1, 1, 15));
		ChestGenHooks.getInfo("dimensionalDungeonChest").addItem(
			new WeightedRandomChestContent(new ItemStack(rubberSaplingBlock), 1, 8, 70));
		ChestGenHooks.getInfo("dimensionalDungeonChest").addItem(
			new WeightedRandomChestContent(new ItemStack(pinkSlimeItem), 1, 1, 1));
		// tempting as a sacred sapling is, chests are too common with too few possible items
		// maybe as a custom dungeon for integration
		///}

		//}*/
	}

	@EventHandler
	public void handleIMC(IMCEvent e) {

		IMCHandler.processIMC(e.getMessages());
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent evt) {

		TileEntityUnifier.updateUnifierLiquids();

		String[] list = MFRConfig.rubberTreeBiomeWhitelist.getStringList();
		for (String biome : list) {
			MFRRegistry.registerRubberTreeBiome(new ResourceLocation(biome));
		}

		list = MFRConfig.unifierBlacklist.getStringList();
		for (String entry : list) {
			MFRRegistry.registerUnifierBlacklist(entry);
		}

		list = MFRConfig.spawnerBlacklist.getStringList();
		for (String entry : list) {
			MFRRegistry.registerAutoSpawnerBlacklist(entry);
		}

		MFRFarmables.post();
		IntegrationManager.postInit();
		TileEntityUnifier.updateUnifierLiquids();
	}

	@EventHandler
	public void loadComplete(FMLLoadCompleteEvent evt) {

		IMCHandler.processIMC(FMLInterModComms.fetchRuntimeMessages(this));

		IntegrationManager.completeInit();

		// catch biomes whitelisted via IMC that are in the config blacklist
		String[] list = MFRConfig.rubberTreeBiomeBlacklist.getStringList();
		for (String biome : list) {
			MFRRegistry.getRubberTreeBiomes().remove(new ResourceLocation(biome));
		}
		for (Property prop : MFRConfig.spawnerCustomization.values()) {
			MFRRegistry.setBaseSpawnCost(prop.getName(), prop.getInt(0));
		}
		list = MFRConfig.safarinetBlacklist.getStringList();
		for (String s : list) {
			Class<?> cl = EntityList.getClass(new ResourceLocation(s));
			if (cl != null)
				MFRRegistry.registerSafariNetBlacklist(cl);
		}

		powercrystals.minefactoryreloaded.core.OreDictionaryArbiter.initialize();
	}

	@EventHandler
	public void remap(FMLModIdMappingEvent evt) {

		powercrystals.minefactoryreloaded.core.OreDictionaryArbiter.bake();
	}

	@EventHandler
	public void serverStopped(FMLServerStoppedEvent evt) {

		GridTickHandler.fluid.clear();
		GridTickHandler.energy.clear();
		GridTickHandler.redstone.clear();
	}

	@Override
	public String getModId() {

		return MFRProps.MOD_ID;
	}

}
