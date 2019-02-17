package powercrystals.minefactoryreloaded.core.settings;

import powercrystals.minefactoryreloaded.api.util.IFactorySettings.SettingType;

import java.util.UUID;

@FunctionalInterface
public interface UUIDSetting extends ISetting {

	UUIDSetting NULL = () -> null;

	UUID getAsUUID();

	default boolean isType(SettingType type) {

		return type == SettingType.UUID;
	}

}
