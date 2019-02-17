package powercrystals.minefactoryreloaded.core.settings;

import powercrystals.minefactoryreloaded.api.util.IFactorySettings.SettingType;

public class PrimitiveSetting implements IntSetting, LongSetting, DoubleSetting {

	private final Number value;

	public PrimitiveSetting(Number value) {

		this.value = value;
	}

	public final int getAsInt() {

		return value.intValue();
	}

	public final long getAsLong() {

		return value.longValue();
	}

	public final double getAsDouble() {

		return value.doubleValue();
	}

	@Override
	public boolean isType(SettingType type) {

		return type.number;
	}

}
