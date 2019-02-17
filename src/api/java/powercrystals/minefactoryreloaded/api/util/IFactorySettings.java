package powercrystals.minefactoryreloaded.api.util;

import net.minecraft.util.math.Vec3i;

import java.util.List;
import java.util.UUID;

public interface IFactorySettings {

	boolean hasSetting(String key, SettingType type);

	boolean getBoolean(String key);

	int getInteger(String key);

	long getLong(String key);

	double getDouble(String key);

	String getString(String key);

	UUID getUUID(String key);

	Vec3i getVec(String key);

	<T> List<T> getList(String key);

	IFactorySettings getSettings(String key);

	class SettingNames {
		public static final String
				SHEARS_MODE = "silkTouch",
				PLAY_SOUNDS = "playSounds",
				HARVEST_SMALL_MUSHROOMS = "harvestSmallMushrooms",
				HARVESTING_TREE = "isHarvestingTree",
				START_POSITION = "start-position";
	}

	enum SettingType {
		BOOL,
		INT,
		LONG,
		DOUBLE,
		STRING,
		UUID,
		VEC3,
		LIST,
		SETTING;

		public final boolean number;

		private SettingType() {

			boolean num = false;
			switch (ordinal()) {
				case 0: // bool
					break;
				case 1: // int
				case 2: // long
				case 3: // double
					num = true;
				case 4: // string
					break;
				case 5: // uuid
					break;
				case 6: // vec3
					break;
				case 7: // list
					break;
				case 8: // setting
					break;
			}
			number = num;

		}

	}

}
