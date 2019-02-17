package powercrystals.minefactoryreloaded.setup.datafix;

import com.google.common.collect.Maps;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.datafix.IFixableData;
import powercrystals.minefactoryreloaded.MFRProps;

import java.util.Locale;
import java.util.Map;

public class TileIdFix implements IFixableData {

	private static final Map<String, String> OLD_TO_NEW_ID_MAP = Maps.newHashMap();

	public int getFixVersion() {

		return 50;
	}

	public NBTTagCompound fixTagCompound(NBTTagCompound compound) {

		String id = compound.getString("id");
		String s = OLD_TO_NEW_ID_MAP.get(id);

		if (s != null) {
			compound.setString("id", s);
		}

		return compound;
	}

	static {
		add("factoryRedstoneCable", MFRProps.PREFIX + "rednet_cable");
		add("factoryRedstoneCableEnergy", MFRProps.PREFIX + "rednet_cable_energy");
		add("factoryPlasticPipe", MFRProps.PREFIX + "plastic_pipe");
		add("factoryConveyor", MFRProps.PREFIX + "conveyor");
		add("factoryTank", MFRProps.PREFIX + "tank");
		add("factoryRednetHistorian", MFRProps.PREFIX + "rednet_panel");
		add("factoryRednetLogic", MFRProps.PREFIX + "rednet_logic");

		add("factoryPlanter", MFRProps.PREFIX + "Planter");
		add("factoryFisher", MFRProps.PREFIX + "Fisher");
		add("factoryHarvester", MFRProps.PREFIX + "Harvester");
		add("factoryRancher", MFRProps.PREFIX + "Rancher");
		add("factoryFertilizer", MFRProps.PREFIX + "Fertilizer");
		add("factoryVet", MFRProps.PREFIX + "Vet");
		add("factoryItemCollector", MFRProps.PREFIX + "ItemCollector");
		add("factoryBlockBreaker", MFRProps.PREFIX + "BlockBreaker");
		add("factoryWeatherCollector", MFRProps.PREFIX + "WeatherCollector");
		add("factorySludgeBoiler", MFRProps.PREFIX + "SludgeBoiler");
		add("factorySewer", MFRProps.PREFIX + "Sewer");
		add("factoryComposter", MFRProps.PREFIX + "Composter");
		add("factoryBreeder", MFRProps.PREFIX + "Breeder");
		add("factoryGrinder", MFRProps.PREFIX + "Grinder");
		add("factoryAutoEnchanter", MFRProps.PREFIX + "AutoEnchanter");
		add("factoryChronotyper", MFRProps.PREFIX + "Chronotyper");

		add("factoryEjector", MFRProps.PREFIX + "Ejector");
		add("factoryItemRouter", MFRProps.PREFIX + "ItemRouter");
		add("factoryLiquidRouter", MFRProps.PREFIX + "LiquidRouter");
		add("factoryDeepStorageUnit", MFRProps.PREFIX + "DeepStorageUnit");
		add("factoryLiquiCrafter", MFRProps.PREFIX + "LiquiCrafter");
		add("factoryLavaFabricator", MFRProps.PREFIX + "LavaFabricator");
		add("factorySteamBoiler", MFRProps.PREFIX + "SteamBoiler");
		add("factoryAutoJukebox", MFRProps.PREFIX + "AutoJukebox");
		add("factoryUnifier", MFRProps.PREFIX + "Unifier");
		add("factoryAutoSpawner", MFRProps.PREFIX + "AutoSpawner");
		add("factoryBioReactor", MFRProps.PREFIX + "BioReactor");
		add("factoryBioFuelGenerator", MFRProps.PREFIX + "BioFuelGenerator");
		add("factoryAutoDisenchanter", MFRProps.PREFIX + "AutoDisenchanter");
		add("factorySlaughterhouse", MFRProps.PREFIX + "Slaughterhouse");
		add("factoryMeatPacker", MFRProps.PREFIX + "MeatPacker");
		add("factoryEnchantmentRouter", MFRProps.PREFIX + "EnchantmentRouter");

		add("factoryLaserDrill", MFRProps.PREFIX + "LaserDrill");
		add("factoryLaserDrillPrecharger", MFRProps.PREFIX + "LaserDrillPrecharger");
		add("factoryAutoAnvil", MFRProps.PREFIX + "AutoAnvil");
		add("factoryBlockSmasher", MFRProps.PREFIX + "BlockSmasher");
		add("factoryRedNote", MFRProps.PREFIX + "RedNote");
		add("factoryAutoBrewer", MFRProps.PREFIX + "AutoBrewer");
		add("factoryFruitPicker", MFRProps.PREFIX + "FruitPicker");
		add("factoryBlockPlacer", MFRProps.PREFIX + "BlockPlacer");
		add("factoryMobCounter", MFRProps.PREFIX + "MobCounter");
		add("factorySteamTurbine", MFRProps.PREFIX + "SteamTurbine");
		add("factoryChunkLoader", MFRProps.PREFIX + "ChunkLoader");
		add("factoryFountain", MFRProps.PREFIX + "Fountain");
		add("factoryMobRouter", MFRProps.PREFIX + "MobRouter");
	}

	private static void add(String old_id, String new_id) {

		OLD_TO_NEW_ID_MAP.put(old_id, new_id);
		OLD_TO_NEW_ID_MAP.put("minecraft:" + old_id.toLowerCase(Locale.ROOT), new_id);
	}

}
