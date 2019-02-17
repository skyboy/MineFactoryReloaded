package powercrystals.minefactoryreloaded.core.settings;

import powercrystals.minefactoryreloaded.api.util.IFactorySettings.SettingType;

import java.util.List;

@FunctionalInterface
public interface ListSetting<T> extends ISetting {

	ListSetting<?> NULL = () -> null;

	List<T> getAsList();

	default boolean isType(SettingType type) {

		return type == SettingType.LIST;
	}

}
