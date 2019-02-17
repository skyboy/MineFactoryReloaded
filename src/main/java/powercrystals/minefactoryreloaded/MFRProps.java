package powercrystals.minefactoryreloaded;

import codechicken.lib.CodeChickenLib;
import cofh.CoFHCore;
import cofh.cofhworld.CoFHWorld;
import cofh.redstoneflux.RedstoneFlux;
import net.minecraft.util.ResourceLocation;

import java.util.Locale;

public class MFRProps {

	public static final String MOD_NAME = "MineFactory Reloaded";
	public static final String VERSION = "2.9.0B1";

	public static final int DATA_VERSION = 100;

	public static final String MOD_ID = "minefactoryreloaded";

	public static final String DEPENDENCIES = CoFHCore.VERSION_GROUP + CodeChickenLib.MOD_VERSION_DEP + RedstoneFlux.VERSION_GROUP + CoFHWorld.VERSION_GROUP;

	public static final String MOD_NETWORK_CHANNEL = "MFReloaded";

	public static final String PREFIX = MOD_ID.toLowerCase(Locale.US) + ':';
	public static final String MODEL_FOLDER = PREFIX + "models/";
	public static final String TEXTURE_FOLDER = PREFIX + "textures/";
	public static final String ARMOR_TEXTURE_FOLDER = TEXTURE_FOLDER + "armor/";
	public static final String MODEL_TEXTURE_FOLDER = TEXTURE_FOLDER + "itemmodels/";
	public static final String MOB_TEXTURE_FOLDER = TEXTURE_FOLDER + "mob/";
	public static final String TILE_ENTITY_FOLDER = TEXTURE_FOLDER + "tileentity/";
	public static final String VILLAGER_FOLDER = TEXTURE_FOLDER + "villager/";
	public static final String HUD_FOLDER = TEXTURE_FOLDER + "hud/";
	public static final String GUI_FOLDER = TEXTURE_FOLDER + "gui/";

	public static final ResourceLocation CHEST_GEN = new ResourceLocation("mfr:villageZoolologist");

}
