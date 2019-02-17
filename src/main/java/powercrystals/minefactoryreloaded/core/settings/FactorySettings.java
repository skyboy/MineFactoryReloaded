package powercrystals.minefactoryreloaded.core.settings;

import net.minecraft.util.math.Vec3i;
import powercrystals.minefactoryreloaded.api.util.IFactorySettings;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import static powercrystals.minefactoryreloaded.api.util.IFactorySettings.SettingType.*;

public class FactorySettings implements IFactorySettings, ISetting {

	private static final Function<ISetting, BooleanSetting> BOOLS = (a) -> a == null ? BooleanSetting.FALSE : BooleanSetting.TRUE;
	private static final Function<ISetting, IntSetting> INTS = (a) -> IntSetting.NULL;
	private static final Function<ISetting, LongSetting> LONGS = (a) -> LongSetting.NULL;
	private static final Function<ISetting, DoubleSetting> DOUBLES = (a) -> DoubleSetting.NULL;
	private static final Function<ISetting, StringSetting> STRINGS = (a) -> {
		final String value;
		if (a == null) return StringSetting.NULL;
		else if (a.isType(UUID)) value = String.valueOf(((UUIDSetting)a).getAsUUID());
		else if (a.isType(DOUBLE)) value = String.valueOf(((DoubleSetting)a).getAsDouble());
		else if (a.isType(LONG)) value = String.valueOf(((LongSetting)a).getAsLong());
		else if (a.isType(INT)) value = String.valueOf(((IntSetting)a).getAsInt());
		else if (a.isType(BOOL)) value = String.valueOf(((BooleanSetting)a).getAsBoolean());
		else return StringSetting.NULL;
		return () -> value;
	};
	private static final Function<ISetting, UUIDSetting> UUIDS = (r) -> {
		if (r != null && r.isType(STRING)) {
			final String temp = ((StringSetting) r).getAsString();
			try {
				final UUID value = java.util.UUID.fromString(temp);
				return () -> value;
			} catch (Throwable p) {
				// ignored
			}
		}
		return UUIDSetting.NULL;
	};
	private static final Function<ISetting, ListSetting> LISTS = (a) -> ListSetting.NULL;
	private static final Function<ISetting, Vec3Setting> VEC3S = (a) -> Vec3Setting.NULL;
	private static final Function<ISetting, FactorySettings> SETTINGS = (a) -> null;

	private final Map<String, ISetting> map;

	public FactorySettings(Map<String, ISetting> settings) {

		map = settings;
	}

	@SuppressWarnings("unchecked")
	private <T extends ISetting> T getOr(SettingType type, String key, Function<ISetting, T> orElse) {

		ISetting r = map.get(key);
		if (r != null && r.isType(type)) return (T) r;
		return orElse.apply(r);
	}

	@Override
	public boolean hasSetting(String key, SettingType type) {

		ISetting value = map.get(key);
		return value != null && value.isType(type);
	}

	@Override
	public boolean getBoolean(String key) {

		return getOr(BOOL, key, BOOLS).getAsBoolean();
	}

	@Override
	public int getInteger(String key) {

		return getOr(INT, key, INTS).getAsInt();
	}

	@Override
	public long getLong(String key) {

		return getOr(LONG, key, LONGS).getAsLong();
	}

	@Override
	public double getDouble(String key) {

		return getOr(DOUBLE, key, DOUBLES).getAsDouble();
	}

	@Override
	public String getString(String key) {

		return getOr(STRING, key, STRINGS).getAsString();
	}

	@Override
	public UUID getUUID(String key) {

		return getOr(UUID, key, UUIDS).getAsUUID();
	}

	@Override
	public Vec3i getVec(String key) {

		return getOr(VEC3, key, VEC3S).getAsVec3();
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> List<T> getList(String key) {

		return getOr(VEC3, key, LISTS).getAsList();
	}

	@Override
	public IFactorySettings getSettings(String key) {

		return getOr(SETTING, key, SETTINGS);
	}

	@Override
	public boolean isType(SettingType type) {

		return type == SettingType.SETTING;
	}

}
