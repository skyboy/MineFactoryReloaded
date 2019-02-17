package powercrystals.minefactoryreloaded.api.rednet;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.util.EnumFacing;

/**
 * You should not implement this yourself. Instead, use this to look for cables to notify from your IRedNetOmniNode as this does not
 * require a block update. This will be implemented on the cable's Block class.
 */
public interface IRedNetNetworkContainer {

	/**
	 * Tells the network to recalculate all subnets.
	 *
	 * @param world
	 * 		The world this cable is in.
	 * @param pos
	 * 		The position of this cable.
	 */
	void updateNetwork(World world, BlockPos pos, EnumFacing from);

	/**
	 * Tells the network to recalculate a specific subnet.
	 *
	 * @param world
	 * 		The world this cable is in.
	 * @param pos
	 * 		The position of this cable.
	 * @param subnet
	 * 		The subnet to recalculate.
	 */
	void updateNetwork(World world, BlockPos pos, int subnet, EnumFacing from);

}
