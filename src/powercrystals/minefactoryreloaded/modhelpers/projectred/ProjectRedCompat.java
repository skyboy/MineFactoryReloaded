package powercrystals.minefactoryreloaded.modhelpers.projectred;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.GameRegistry;

import net.minecraft.block.Block;

import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.farmables.fertilizables.FertilizableSapling;
import powercrystals.minefactoryreloaded.farmables.harvestables.HarvestableTreeLeaves;
import powercrystals.minefactoryreloaded.farmables.plantables.PlantableStandard;

@Mod(modid = "MineFactoryReloaded|CompatProjRed", name = "MFR Compat: ProjectRed", version = MineFactoryReloadedCore.version, 
	 dependencies = "after:MineFactoryReloaded;after:ProjRed|Core;after:ProjRed|Exploration")
@NetworkMod(clientSideRequired = false, serverSideRequired = false)
public class ProjectRedCompat
{
	@EventHandler
	public static void postInit(FMLPostInitializationEvent e)
	{
		if(!Loader.isModLoaded("ProjRed|Exploration"))
		{
			FMLLog.warning("ProjRed|Exploration missing - MFR ProjectRed Compat not loading");
			return;
		}
		try
		{
			Block stainedLeaf = GameRegistry.findBlock("ProjRed|Exploration", "projectred.exploration.dyeleaf");
			Block stainedSapling = GameRegistry.findBlock("ProjRed|Exploration", "projectred.exploration.dyesapling");
			
			MFRRegistry.registerPlantable(new PlantableStandard(stainedSapling.blockID, stainedSapling.blockID));
			MFRRegistry.registerHarvestable(new HarvestableTreeLeaves(stainedLeaf.blockID));
			MFRRegistry.registerFertilizable(new FertilizableSapling(stainedSapling.blockID));				
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
}