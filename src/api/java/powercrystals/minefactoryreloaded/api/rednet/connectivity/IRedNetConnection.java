package powercrystals.minefactoryreloaded.api.rednet.connectivity;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nonnull;

/**
 * Defines a Block that can connect to RedNet cables. This must be implemented on your Block class.
 */
public interface IRedNetConnection {

	/**
	 * Returns the connection type of this Block. If this value must be changed
	 * while the block is alive, it must notify neighbors of a change.
	 * <p>
	 * For nodes that want to interact with rednet,
	 * see IRedNetInputNode, IRedNetOutputNode, and IRedNetOmniNode
	 *
	 * @param world
	 * 		The world this block is in.
	 * @param pos
	 * 		This block's position.
	 * @param side
	 * 		The side that connection information is required for.
	 *
	 * @return The connection type.
	 */
	@Nonnull
	RedNetConnectionType getConnectionType(World world, BlockPos pos, EnumFacing side);

}
