package powercrystals.minefactoryreloaded.core.settings;

import powercrystals.minefactoryreloaded.api.util.IFactorySettings.SettingType;

@FunctionalInterface
public interface StringSetting extends ISetting {

	StringSetting NULL = () -> null;

	String getAsString();

	default boolean isType(SettingType type) {

		return type == SettingType.STRING;
	}

}
