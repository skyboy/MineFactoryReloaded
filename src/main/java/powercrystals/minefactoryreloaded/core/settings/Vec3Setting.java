package powercrystals.minefactoryreloaded.core.settings;

import net.minecraft.util.math.Vec3i;
import powercrystals.minefactoryreloaded.api.util.IFactorySettings.SettingType;

@FunctionalInterface
public interface Vec3Setting extends ISetting {

	Vec3i getAsVec3();

	default boolean isType(SettingType type) {

		return type == SettingType.VEC3;
	}

	Vec3Setting NULL = () -> Vec3i.NULL_VECTOR;

	static Vec3Setting of(final Vec3i pos) {

		return () -> pos;
	}

}
