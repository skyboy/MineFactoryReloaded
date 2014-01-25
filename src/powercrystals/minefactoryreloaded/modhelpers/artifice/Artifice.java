package powercrystals.minefactoryreloaded.modhelpers.artifice;

import net.minecraft.block.Block;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;

import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;

@Mod(modid = "MineFactoryReloaded|CompatArtifice", name = "MFR Compat: Artifice", version = MineFactoryReloadedCore.version, dependencies = "after:MineFactoryReloaded;after:Artifice")
@NetworkMod(clientSideRequired = false, serverSideRequired = false)
public class Artifice
{
	@EventHandler
	public static void load(FMLInitializationEvent e)
	{
		if(!Loader.isModLoaded("Artifice"))
		{
			FMLLog.warning("Artifice missing - MFR Artifice Compat not loading");
			return;
		}
		try
		{
			Class<?> blockClass = Class.forName("shukaro.artifice.ArtificeBlocks");
			int flowerID = ((Block)blockClass.getField("blockFlora").get(null)).blockID;
			MFRRegistry.registerHarvestable(new ArtificeFlower(flowerID));
		}
		catch (Exception x)
		{
			x.printStackTrace();
		}
	}
}