package powercrystals.minefactoryreloaded.core.settings;

import powercrystals.minefactoryreloaded.api.util.IFactorySettings.SettingType;

import java.util.function.DoubleSupplier;

@FunctionalInterface
public interface DoubleSetting extends DoubleSupplier, ISetting {

	DoubleSetting NULL = () -> 0;

	default boolean isType(SettingType type) {

		return type == SettingType.DOUBLE;
	}

}
