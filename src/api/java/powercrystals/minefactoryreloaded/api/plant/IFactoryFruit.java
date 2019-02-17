package powercrystals.minefactoryreloaded.api.plant;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

/**
 * Defines a fruit entry for the Fruit Picker.
 *
 * @author powercrystals
 */
public interface IFactoryFruit {

	/**
	 * @return The block this fruit has in the world.
	 */
	@Nonnull
	Block getPlant();

	/**
	 * Used to determine if this fruit can be picked (is it ripe yet, etc)
	 *
	 * @param world
	 * 		The world where the fruit is being picked
	 * @param pos
	 * 		The position of the fruit
	 *
	 * @return True if the fruit can be picked
	 */
	boolean canBePicked(World world, BlockPos pos);

	/**
	 * Called by the Fruit Picker to determine what block to replace the picked
	 * block with. At the time this method is called, the fruit still exists.
	 *
	 * @param world
	 * 		The world where the fruit is being picked
	 * @param pos
	 * 		The position of the fruit
	 *
	 * @return The block to replace the fruit block with, or null for air.
	 */
	@Nullable
	IReplacementBlock getReplacementBlock(World world, BlockPos pos);

	/**
	 * Called by the Fruit Picker to determine what drops to generate. At the
	 * time this method is called, the fruit still exists.
	 *
	 * @param world
	 * 		The world where the fruit is being picked
	 * @param pos
	 * 		The position of the fruit
	 */
	@Nullable
	List<ItemStack> getDrops(World world, Random rand, BlockPos pos);

}
