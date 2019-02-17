package powercrystals.minefactoryreloaded.api.mob;

import net.minecraft.entity.Entity;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.function.BiFunction;

public class RandomMobProvider extends WeightedRandom.Item {

	private final BiFunction<World, Vec3d, Entity> _mob;

	public RandomMobProvider(int weight, BiFunction<World, Vec3d, Entity> init) {

		super(weight);
		_mob = init;
	}

	public Entity getMob(World world, Vec3d pos) {

		return _mob.apply(world, pos);
	}

}
