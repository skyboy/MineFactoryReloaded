package powercrystals.minefactoryreloaded.modhelpers.dyetrees;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;

import java.util.HashMap;

import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.farmables.fertilizables.FertilizableSapling;
import powercrystals.minefactoryreloaded.farmables.plantables.PlantableStandard;

import Reika.DyeTrees.API.TreeGetter;

@Mod(modid = "MineFactoryReloaded|CompatDyeTrees", name = "MFR Compat: DyeTrees", version = MineFactoryReloadedCore.version, dependencies = "after:MineFactoryReloaded;after:DyeTrees")
@NetworkMod(clientSideRequired = false, serverSideRequired = false)
public class DyeTrees
{
	@EventHandler
	public static void load(FMLInitializationEvent e)
	{
		if(!Loader.isModLoaded("DyeTrees"))
		{
			FMLLog.warning("Dye Trees missing - MFR Dye Trees Compat not loading");
			return;
		}
		
		try
		{

			MFRRegistry.registerHarvestable(new HarvestableDyeLeaves(TreeGetter.getNaturalDyeLeafID()));

			MFRRegistry.registerPlantable(new PlantableStandard(TreeGetter.getSaplingID(), TreeGetter.getSaplingID()));

			MFRRegistry.registerFertilizable(new FertilizableSapling(TreeGetter.getSaplingID()));
		
		}
		catch(Exception x)
		{
			x.printStackTrace();
		}
	}
}
