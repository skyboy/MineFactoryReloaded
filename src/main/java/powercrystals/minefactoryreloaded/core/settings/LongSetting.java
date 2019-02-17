package powercrystals.minefactoryreloaded.core.settings;

import powercrystals.minefactoryreloaded.api.util.IFactorySettings.SettingType;

import java.util.function.LongSupplier;

@FunctionalInterface
public interface LongSetting extends LongSupplier, ISetting {

	LongSetting NULL = () -> 0;

	default boolean isType(SettingType type) {

		return type == SettingType.LONG;
	}

}
