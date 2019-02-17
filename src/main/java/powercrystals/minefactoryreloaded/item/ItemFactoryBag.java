package powercrystals.minefactoryreloaded.item;

import cofh.api.item.IInventoryContainerItem;
import cofh.core.gui.container.InventoryContainerItemWrapper;
import cofh.core.util.helpers.ItemHelper;
import cofh.core.util.helpers.StringHelper;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.core.MFRUtil;
import powercrystals.minefactoryreloaded.item.base.ItemFactory;
import powercrystals.minefactoryreloaded.render.ModelHelper;
import powercrystals.minefactoryreloaded.setup.MFRLoot;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemFactoryBag extends ItemFactory implements IInventoryContainerItem {

	public ItemFactoryBag() {

		setUnlocalizedName("mfr.plastic.bag");
		setMaxStackSize(24);
	}

	@Override
	public int getItemStackLimit(@Nonnull ItemStack stack) {

		NBTTagCompound tag = stack.getTagCompound();
		if (tag != null && (tag.hasKey("inventory") || tag.hasKey("Inventory") || tag.hasKey("loot")))
			return 1;
		return maxStackSize;
	}

	@Override
	public void addInformation(@Nonnull ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag tooltipFlag) {

		super.addInformation(stack, world, tooltip, tooltipFlag);

		if (getItemStackLimit(stack) == 1) {
			if (stack.getTagCompound().hasKey("loot")) {
				tooltip.add(MFRUtil.localize("info.mfr.loot", true));
			} else if (stack.getTagCompound().hasKey("inventory")) {
				tooltip.add(MFRUtil.localize("info.mfr.legacy", true));
			} else if (!StringHelper.displayShiftForDetail || MFRUtil.isShiftKeyDown()) {
				ItemHelper.addAccessibleInventoryInformation(stack, tooltip, 0, Integer.MAX_VALUE);
			} else {
				tooltip.add(MFRUtil.shiftForInfo());
			}
		} else {
			tooltip.add(MFRUtil.localize("info.mfr.folded", true));
		}
	}

	@Override
	public int getSizeInventory(@Nonnull ItemStack container) {

		return 5;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {

		@Nonnull ItemStack stack = player.getHeldItem(hand);

		if (stack.getCount() != 1) {
			if (!world.isRemote)
				player.sendMessage(new TextComponentTranslation("chat.info.mfr.bag.stacksize"));
			return new ActionResult<>(EnumActionResult.SUCCESS, stack);
		}

		if (!world.isRemote && stack.hasTagCompound() && stack.getTagCompound().hasKey("loot")) {
			stack = fillWithLoot((WorldServer) world, player, stack);
			stack.getTagCompound().removeTag("loot");
		}

		if (!world.isRemote)
			player.openGui(MineFactoryReloadedCore.instance(), 2, world, 0, 0, 0);
		return new ActionResult<>(EnumActionResult.SUCCESS, stack);
	}

	@Nonnull
	private ItemStack fillWithLoot(WorldServer world, EntityPlayer player, @Nonnull ItemStack stack) {
		
		LootTable lootTable = world.getLootTableManager().getLootTableFromLocation(MFRLoot.FACTORY_BAG);
		InventoryContainerItemWrapper wrapper = new InventoryContainerItemWrapper(stack);
		lootTable.fillInventory(wrapper, world.rand, new LootContext.Builder(world).withLuck(player.getLuck()).build());
		
		return wrapper.getContainerStack();
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels() {

		ModelHelper.registerModel(this, "plastic_bag");
	}

}
