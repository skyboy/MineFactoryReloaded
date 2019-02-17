package powercrystals.minefactoryreloaded.api.handler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

/**
 * This interface is <b>not</b> intended for you to implement on your items.
 * <p>
 * This exists to tell MFR how to *reactively* interact with various wrenches and other tools that do not handle it themselves.
 */
public interface IFactoryTool {

	/**
	 * Called to determine if this ItemStack is a valid tool.
	 *
	 * @param player
	 * 		The player using the ItemStack
	 * @param hand
	 * 		The hand the player is holding the ItemStack in
	 * @param stack
	 * 		The ItemStack being an ItemStack
	 * @param pos
	 * 		The position of the block that will be interacted with
	 * @param side
	 * 		The side of the block that will be interacted with
	 *
	 * @return True if this ItemStack is a valid tool and meets any constraints,
	 * such as durability or power, or whatever.
	 */
	boolean isFactoryToolUsable(EntityPlayer player, EnumHand hand, ItemStack stack, BlockPos pos, EnumFacing side);

	/**
	 * Called to let you modify the ItemStack if you have any reason to do so after a successful usage.
	 *
	 * @param player
	 * 		The player using the ItemStack
	 * @param hand
	 * 		The hand the player is holding the ItemStack in
	 * @param stack
	 * 		The ItemStack being an ItemStack
	 * @param pos
	 * 		The position of the block that will be interacted with
	 * @param side
	 * 		The side of the block that will be interacted with
	 *
	 * @return True if this ItemStack was used as a tool.
	 */
	boolean onFactoryToolUsed(EntityPlayer player, EnumHand hand, ItemStack stack, BlockPos pos, EnumFacing side);

}
