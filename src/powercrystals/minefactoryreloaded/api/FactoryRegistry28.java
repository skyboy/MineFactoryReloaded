package powercrystals.minefactoryreloaded.api;

import java.lang.reflect.Method;

/**
 * @author PowerCrystals
 * 
 * Class used to register plants and other farming-related things with MFR. Will do nothing if MFR does not exist, but your mod should be set to load
 * after MFR or things may not work properly.
 * 
 * To avoid breaking the API, additional FactoryRegistry##s will appear on major MFR versions that contain API additions. On a Minecraft version change, 
 * these will be rolled back into this class.
 * 
 */
 public class FactoryRegistry28
 {
	 /**
	  * Registers a spawn handler with the Auto-Spawner.
	  * 
	  * @param spawnHandler The IMobSpawnHandler.
	  */
	 public static void registerSpawnHandler(IMobSpawnHandler spawnHandler)
	 {
		 try
		 {
			 Class<?> registry = Class.forName("powercrystals.minefactoryreloaded.MFRRegistry");
			 if(registry != null)
			 {
				 Method reg = registry.getMethod("registerSpawnHandler", IMobSpawnHandler.class);
				 reg.invoke(registry, spawnHandler);
			 }
		 }
		 catch(Exception e)
		 {
			 e.printStackTrace();
		 }
	 }

 }
