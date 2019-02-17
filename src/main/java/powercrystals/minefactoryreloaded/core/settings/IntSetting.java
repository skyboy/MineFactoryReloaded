package powercrystals.minefactoryreloaded.core.settings;

import powercrystals.minefactoryreloaded.api.util.IFactorySettings.SettingType;

import java.util.function.IntSupplier;

@FunctionalInterface
public interface IntSetting extends IntSupplier, ISetting {

	IntSetting NULL = () -> 0;

	default boolean isType(SettingType type) {

		return type == SettingType.INT;
	}

}
