package powercrystals.minefactoryreloaded.api.mob;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;
import java.util.function.BiFunction;

@FunctionalInterface
public interface IRandomMobProvider {

	/**
	 * Called to provide random entities to be spawned by mystery SafariNets
	 *
	 * @param world
	 * 		The world object the entities will be spawned in.
	 *
	 * @return A list of RandomMobProvider instances of entities that are all ready to
	 * be spawned in the world with no additional method calls.
	 */
	// TODO: docs
	List<RandomMobProvider> getRandomMobs(World world);


	static <V extends Entity, T extends Class<V>> V spawnMob(T entity, World world, Vec3d pos) {

		try {
			V e = entity.getConstructor(World.class).newInstance(world);
			e.setPosition(pos.x, pos.y, pos.z);
			if (e instanceof EntityLiving) {
				((EntityLiving) e).onInitialSpawn(world.getDifficultyForLocation(new BlockPos(pos)), null);
			}
			return e;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	static <V extends Entity, T extends Class<V>> BiFunction<World, Vec3d, Entity> prepareMob(final T entity) {

		return (world, pos) -> spawnMob(entity, world, pos);
	}

}
