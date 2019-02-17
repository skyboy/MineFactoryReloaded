package powercrystals.minefactoryreloaded.api.rednet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IBlockAccess;

import java.util.List;

/**
 * Defines a Block that can print information about itself using the RedNet Meter. This must be implemented on your Block class.
 */
public interface IRedNetInfo {

	/**
	 * This function is called to gather information about the block into the {@code info} parameter.
	 *
	 * @param world
	 * 		Reference to the world.
	 * @param pos
	 * 		Position of the block.
	 * @param side
	 * 		The side of the block that is being queried.
	 * @param player
	 * 		Player doing the querying - this can be <b>{@code null}</b>.
	 * @param info
	 * 		The list that the information should be appended to.
	 */
	void getRedNetInfo(IBlockAccess world, BlockPos pos, EnumFacing side, EntityPlayer player, List<ITextComponent> info);

}
