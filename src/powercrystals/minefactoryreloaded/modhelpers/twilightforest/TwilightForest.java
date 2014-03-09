package powercrystals.minefactoryreloaded.modhelpers.twilightforest;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;

import java.util.HashMap;

import net.minecraft.block.Block;

import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.api.HarvestType;
import powercrystals.minefactoryreloaded.farmables.fertilizables.FertilizableSapling;
import powercrystals.minefactoryreloaded.farmables.harvestables.HarvestableStandard;
import powercrystals.minefactoryreloaded.farmables.harvestables.HarvestableTreeLeaves;
import powercrystals.minefactoryreloaded.farmables.harvestables.HarvestableWood;
import powercrystals.minefactoryreloaded.farmables.plantables.PlantableStandard;

@Mod(modid = "MineFactoryReloaded|CompatTwilightForest", name = "MFR Compat: TwilightForest", version = MineFactoryReloadedCore.version, dependencies = "after:MineFactoryReloaded;after:TwilightForest")
@NetworkMod(clientSideRequired = false, serverSideRequired = false)
public class TwilightForest
{
	@SuppressWarnings("rawtypes")
	public static HashMap entityEggs; 
	
	public static ModContainer twilightForestContainer;
	
	@SuppressWarnings("rawtypes")
	@EventHandler
	public static void load(FMLInitializationEvent e)
	{
		if(!Loader.isModLoaded("TwilightForest"))
		{
			FMLLog.warning("Twilight Forest missing - MFR Twilight Forest Compat not loading");
			return;
		}
		
		try
		{
			
			entityEggs = (HashMap)Class.forName("twilightforest.entity.TFCreatures").getField("entityEggs").get(null);
			twilightForestContainer = FMLCommonHandler.instance().findContainerFor(Class.forName("twilightforest.TwilightForestMod").getField("instance").get(null));
			
			Class tfBighorn = Class.forName("twilightforest.entity.passive.EntityTFBighorn");
			Class tfHydra = Class.forName("twilightforest.entity.EntityTFHydra");
			Class tfHydraHead = Class.forName("twilightforest.entity.EntityTFHydraHead");
			Class tfHydraNeck = Class.forName("twilightforest.entity.EntityTFHydraNeck");
			Class tfHydraPart = Class.forName("twilightforest.entity.EntityTFHydraPart");
			Class tfKingSpider = Class.forName("twilightforest.entity.EntityTFKingSpider");
			Class tfLich = Class.forName("twilightforest.entity.EntityTFLich");
			Class tfNaga = Class.forName("twilightforest.entity.EntityTFNaga");
			Class tfNagaSegment = Class.forName("twilightforest.entity.EntityTFNagaSegment");
			Class tfQuestRam = Class.forName("twilightforest.entity.passive.EntityTFQuestRam");
			Class tfUrGhast = Class.forName("twilightforest.entity.EntityTFUrGhast");
			
			MFRRegistry.registerSafariNetBlacklist(tfHydra);
			MFRRegistry.registerSafariNetBlacklist(tfHydraHead);
			MFRRegistry.registerSafariNetBlacklist(tfHydraNeck);
			MFRRegistry.registerSafariNetBlacklist(tfHydraPart);
			MFRRegistry.registerSafariNetBlacklist(tfKingSpider);
			MFRRegistry.registerSafariNetBlacklist(tfLich);
			MFRRegistry.registerSafariNetBlacklist(tfNaga);
			MFRRegistry.registerSafariNetBlacklist(tfNagaSegment);
			MFRRegistry.registerSafariNetBlacklist(tfQuestRam);
			MFRRegistry.registerSafariNetBlacklist(tfUrGhast);

			MFRRegistry.registerGrinderBlacklist(tfUrGhast);
			MFRRegistry.registerGrinderBlacklist(tfNagaSegment);
			MFRRegistry.registerGrinderBlacklist(tfNaga);
			MFRRegistry.registerGrinderBlacklist(tfLich);
			MFRRegistry.registerGrinderBlacklist(tfHydraPart);
			MFRRegistry.registerGrinderBlacklist(tfHydraNeck);
			MFRRegistry.registerGrinderBlacklist(tfHydraHead);
			MFRRegistry.registerGrinderBlacklist(tfHydra);
			
			MFRRegistry.registerMobEggHandler(new TwilightForestEggHandler());
			
			MFRRegistry.registerRanchable(new RanchableTFBighorn(tfBighorn));
			
			Class tfBlocks = Class.forName("twilightforest.block.TFBlocks");
			if(tfBlocks != null)
			{
				MFRRegistry.registerHarvestable(new HarvestableWood(((Block)tfBlocks.getField("log").get(null)).blockID));
				MFRRegistry.registerHarvestable(new HarvestableWood(((Block)tfBlocks.getField("magicLog").get(null)).blockID));
				MFRRegistry.registerHarvestable(new HarvestableWood(((Block)tfBlocks.getField("magicLogSpecial").get(null)).blockID));
				MFRRegistry.registerHarvestable(new HarvestableStandard(((Block)tfBlocks.getField("root").get(null)).blockID, HarvestType.TreeFlipped));
				MFRRegistry.registerHarvestable(new HarvestableTreeLeaves(((Block)tfBlocks.getField("leaves").get(null)).blockID));
				MFRRegistry.registerHarvestable(new HarvestableTreeLeaves(((Block)tfBlocks.getField("magicLeaves").get(null)).blockID));
				MFRRegistry.registerHarvestable(new HarvestableTreeLeaves(((Block)tfBlocks.getField("hedge").get(null)).blockID));
				MFRRegistry.registerHarvestable(new HarvestableTreeLeaves(((Block)tfBlocks.getField("firefly").get(null)).blockID));
				MFRRegistry.registerHarvestable(new HarvestableTreeLeaves(((Block)tfBlocks.getField("cicada").get(null)).blockID));
				MFRRegistry.registerHarvestable(new HarvestableStandard(((Block)tfBlocks.getField("plant").get(null)).blockID, HarvestType.Normal));
				
				MFRRegistry.registerPlantable(new PlantableStandard(((Block)tfBlocks.getField("sapling").get(null)).blockID, ((Block)tfBlocks.getField("sapling").get(null)).blockID));
				
				MFRRegistry.registerFertilizable(new FertilizableSapling(((Block)tfBlocks.getField("sapling").get(null)).blockID));
			}
			
			MFRRegistry.registerRandomMobProvider(new TwilightForestMobProvider());
		}
		catch(Exception x)
		{
			x.printStackTrace();
		}
	}
}
