package powercrystals.minefactoryreloaded.item.base;


import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public abstract class ItemFactoryGun extends ItemFactory {

	protected abstract boolean hasGUI(@Nonnull ItemStack stack);

	protected boolean openGUI(@Nonnull ItemStack stack, World world, EntityPlayer player) {
		return false;
	}

	protected abstract boolean fire(@Nonnull ItemStack stack, World world, EntityPlayer player);

	protected abstract int getDelay(@Nonnull ItemStack stack, boolean fired);

	protected abstract String getDelayTag(@Nonnull ItemStack stack);

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {

		@Nonnull ItemStack stack = player.getHeldItem(hand);

		if (stack.getTagCompound() == null)
			stack.setTagCompound(new NBTTagCompound());

		EnumActionResult result = EnumActionResult.FAIL;
		if (!(hasGUI(stack) && openGUI(stack, world, player))) {
			NBTTagCompound tag = player.getEntityData();
			String delayTag = getDelayTag(stack);
			if (tag.getLong(delayTag) < world.getTotalWorldTime()) {
				result = EnumActionResult.SUCCESS;
				tag.setLong(delayTag, world.getTotalWorldTime() + getDelay(stack, fire(stack, world, player)));
			}
		} else {
			result = EnumActionResult.SUCCESS;
		}
		return new ActionResult<>(result, stack);
	}

}
