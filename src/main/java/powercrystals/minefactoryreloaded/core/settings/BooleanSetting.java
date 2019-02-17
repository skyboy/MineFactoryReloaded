package powercrystals.minefactoryreloaded.core.settings;

import powercrystals.minefactoryreloaded.api.util.IFactorySettings.SettingType;

import java.util.function.BooleanSupplier;

@FunctionalInterface
public interface BooleanSetting extends BooleanSupplier, ISetting {

	BooleanSetting TRUE = () -> true;
	BooleanSetting FALSE = () -> false;

	default boolean isType(SettingType type) {

		return type == SettingType.BOOL;
	}

}
