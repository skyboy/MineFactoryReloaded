package powercrystals.minefactoryreloaded.modhelpers.atum;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;

import java.lang.reflect.Method;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.api.HarvestType;
import powercrystals.minefactoryreloaded.farmables.harvestables.HarvestableStandard;
import powercrystals.minefactoryreloaded.farmables.harvestables.HarvestableTreeLeaves;
import powercrystals.minefactoryreloaded.farmables.harvestables.HarvestableWood;
import powercrystals.minefactoryreloaded.farmables.plantables.PlantableStandard;
import powercrystals.minefactoryreloaded.modhelpers.FertilizableCropReflection;
import powercrystals.minefactoryreloaded.modhelpers.FertilizableSaplingReflection;

@Mod(modid = "MineFactoryReloaded|CompatAtum", name = "MFR Compat: Atum", version = MineFactoryReloadedCore.version, dependencies = "after:MineFactoryReloaded;after:Atum")
@NetworkMod(clientSideRequired = false, serverSideRequired = false)
public class Atum
{
	private static final String lastUpdated = "Atum 0.4.3B, current release as of Jul 3 2013";
	
	@SuppressWarnings("rawtypes")
	@EventHandler
	public static void load(FMLInitializationEvent e)
	{
		if(!Loader.isModLoaded("Atum"))
		{
			FMLLog.warning("Atum missing - MFR Atum Compat not loading");
			return;
		}
		try
		{
			Class AtumItems = Class.forName("rebelkeithy.mods.atum.AtumItems");
			Class AtumBlocks = Class.forName("rebelkeithy.mods.atum.AtumBlocks");
			
			String entityprefix = "rebelkeithy.mods.atum.entities.Entity";
			
			Class banditWarlord = Class.forName(entityprefix + "BanditWarlord");
			Class pharaoh = Class.forName(entityprefix + "Pharaoh");
			
			int atumLogId = ((Block)AtumBlocks.getField("log").get(null)).blockID;
			int atumLeavesId = ((Block)AtumBlocks.getField("leaves").get(null)).blockID;
			int atumSaplingId = ((Block)AtumBlocks.getField("palmSapling").get(null)).blockID;
			
			int flaxSeedsId = ((Item)AtumItems.getField("flaxSeeds").get(null)).itemID;
			
			int flaxId = ((Block)AtumBlocks.getField("flax").get(null)).blockID;
			int papyrusId = ((Block)AtumBlocks.getField("papyrus").get(null)).blockID;
			int shrubId = ((Block)AtumBlocks.getField("shrub").get(null)).blockID;
			int weedId = ((Block)AtumBlocks.getField("weed").get(null)).blockID;
			
			Method atumSaplingGrowTree = Class.forName("rebelkeithy.mods.atum.blocks.BlockPalmSapling").getMethod("growTree", World.class, int.class, int.class, int.class, Random.class);
			Method atumFlaxFertilize = Class.forName("rebelkeithy.mods.atum.blocks.BlockFlax").getMethod("fertilize", World.class, int.class, int.class, int.class);
			
			MFRRegistry.registerSafariNetBlacklist(banditWarlord);
			MFRRegistry.registerSafariNetBlacklist(pharaoh);

			MFRRegistry.registerGrinderBlacklist(banditWarlord);
			MFRRegistry.registerGrinderBlacklist(pharaoh);
			
			MFRRegistry.registerPlantable(new PlantableStandard(atumSaplingId, atumSaplingId));
			MFRRegistry.registerPlantable(new PlantableStandard(flaxSeedsId, flaxId));
			
			MFRRegistry.registerFertilizable(new FertilizableCropReflection(flaxId, atumFlaxFertilize, 5));
			MFRRegistry.registerFertilizable(new FertilizableSaplingReflection(atumSaplingId, atumSaplingGrowTree));
			
			MFRRegistry.registerHarvestable(new HarvestableWood(atumLogId));
			MFRRegistry.registerHarvestable(new HarvestableTreeLeaves(atumLeavesId));
			MFRRegistry.registerHarvestable(new HarvestableStandard(flaxId, HarvestType.Normal));
			MFRRegistry.registerHarvestable(new HarvestableStandard(papyrusId, HarvestType.LeaveBottom));
			MFRRegistry.registerHarvestable(new HarvestableStandard(shrubId, HarvestType.Normal));
			MFRRegistry.registerHarvestable(new HarvestableStandard(weedId, HarvestType.Normal));
			
		}
		catch (Exception x)
		{
			System.out.println("Last updated for " + lastUpdated);
			x.printStackTrace();
		}
	}
}
