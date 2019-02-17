package powercrystals.minefactoryreloaded.api.plant;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.Random;

/**
 * Defines a fertilizable block, and the process to fertilize it. You can assume
 * that you will never have to check that block matches the one returned by
 * getPlant().
 *
 * @author PowerCrystals
 */
public interface IFactoryFertilizable {

	/**
	 * @return The block this instance is managing.
	 */
	@Nonnull
	Block getPlant();

	/**
	 * Called to determine if the block at this location can be fertilized.
	 *
	 * @param world
	 * 		The world this block belongs to.
	 * @param pos
	 * 		The position of this block.
	 * @param fertilizerType
	 * 		The kind of fertilizer being used.
	 *
	 * @return True if the block at (x,y,z) can be fertilized with the given
	 * type of fertilizer.
	 */
	boolean canFertilize(World world, BlockPos pos, FertilizerType fertilizerType);

	/**
	 * Called to fertilize the block at this location.
	 *
	 * @param world
	 * 		The world this block belongs to.
	 * @param rand
	 * 		A Random instance to use when fertilizing, if necessary.
	 * @param pos
	 * 		The position of this block.
	 * @param fertilizerType
	 * 		The kind of fertilizer being used.
	 *
	 * @return True if fertilization was successful. If false, the Fertilizer
	 * will not consume a fertilizer item and will not drain power.
	 */
	boolean fertilize(World world, Random rand, BlockPos pos, FertilizerType fertilizerType);

}
