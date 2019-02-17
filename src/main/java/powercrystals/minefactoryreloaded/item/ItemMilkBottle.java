package powercrystals.minefactoryreloaded.item;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import powercrystals.minefactoryreloaded.item.base.ItemFactory;
import powercrystals.minefactoryreloaded.render.ModelHelper;

import javax.annotation.Nonnull;

public class ItemMilkBottle extends ItemFactory {

	public ItemMilkBottle() {

		setContainerItem(Items.GLASS_BOTTLE);
		setUnlocalizedName("mfr.milk_bottle");
		setMaxStackSize(16);
	}

	@Nonnull
	@Override
	public ItemStack onItemUseFinish(@Nonnull ItemStack stack, World world, EntityLivingBase entity) {

		if (!(entity instanceof EntityPlayer))
			return stack;

		EntityPlayer player = (EntityPlayer) entity;

		if (!world.isRemote) {
			player.curePotionEffects(new ItemStack(Items.MILK_BUCKET));
		}

		if (!player.capabilities.isCreativeMode) {
			stack.shrink(1);

			if (stack.getCount() <= 0) {
				return new ItemStack(Items.GLASS_BOTTLE);
			} else if (!player.inventory.addItemStackToInventory(new ItemStack(Items.GLASS_BOTTLE))) {
				player.dropItem(new ItemStack(Items.GLASS_BOTTLE), false, true);
			}
		}

		if (stack.getCount() <= 0) {
			stack.setCount(0);
		}

		return stack;
	}

	@Override
	public int getMaxItemUseDuration(@Nonnull ItemStack stack) {

		return 32;
	}

	@Override
	public EnumAction getItemUseAction(@Nonnull ItemStack stack) {

		return EnumAction.DRINK;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {

		player.setActiveHand(hand);
		return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels() {

		ModelHelper.registerModel(this, "milk_bottle");
	}
}
