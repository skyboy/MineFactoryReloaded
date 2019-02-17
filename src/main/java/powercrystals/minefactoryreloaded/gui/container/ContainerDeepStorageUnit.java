package powercrystals.minefactoryreloaded.gui.container;

import cofh.core.gui.slot.SlotLocked;
import cofh.core.gui.slot.SlotRemoveOnly;
import cofh.core.util.helpers.InventoryHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import powercrystals.minefactoryreloaded.gui.slot.SlotInvisible;
import powercrystals.minefactoryreloaded.tile.machine.storage.TileEntityDeepStorageUnit;

import javax.annotation.Nonnull;

public class ContainerDeepStorageUnit extends ContainerFactoryInventory {

	private TileEntityDeepStorageUnit _dsu;
	private int _tempQuantity;

	public ContainerDeepStorageUnit(TileEntityDeepStorageUnit dsu, InventoryPlayer inventoryPlayer) {

		super(dsu, inventoryPlayer);
		_dsu = dsu;
	}

	@Override
	protected void addSlots() {

		IItemHandler handler = InventoryHelper.getItemHandlerCap(_te, null);
		addSlotToContainer(new SlotItemHandler(handler, 0, 134, 16));
		addSlotToContainer(new SlotItemHandler(handler, 1, 152, 16));
		addSlotToContainer(new SlotRemoveOnly(_te, 2, 152, 49));
		addSlotToContainer(new SlotLocked(_te, 3, 9, 63) {
			@Nonnull
			@Override
			public ItemStack getStack() {

				return _dsu.getStoredItemRaw();
			}
		});
		for (int i = 34; i-- > 0;)
			addSlotToContainer(new SlotInvisible(_te, 4 + i, 170, 16, 0));
	}

	@Override
	public void putStackInSlot(int slot, @Nonnull ItemStack stack) {

		if (slot == 3) {
			_dsu.setStoredItemRaw(stack);
		} else {
			super.putStackInSlot(slot, stack);
		}
	}

	@Override
	protected boolean performMerge(int slot, @Nonnull ItemStack stackInSlot) {

		if (slot < 38) {
			if (mergeItemStack(stackInSlot, 38, inventorySlots.size(), true)) {
				sendSlots(0, 4);
				return true;
			}
		} else if (_dsu.isItemValidForSlot(0, stackInSlot) && mergeItemStack(stackInSlot, 0, 36, false)) {
			sendSlots(0, 4);
			return true;
		}
		return false;
	}

	@Nonnull
	@Override
	public ItemStack slotClick(int slotId, int mouseButton, ClickType modifier, EntityPlayer player) {

		@Nonnull ItemStack r = super.slotClick(slotId, mouseButton, modifier, player);
		if (slotId < 4) {
			sendSlots(0, 4);
		}
		return r;
	}

	@Override
	protected boolean supportsShiftClick(EntityPlayer player, int slot) {

		return !player.world.isRemote ? true : slot > 37;
	}

	@Override
	public void detectAndSendChanges() {

		super.detectAndSendChanges();

		int v = _dsu.getQuantity();
		for (IContainerListener listener : listeners) {
			listener.sendWindowProperty(this, 200, v);
			listener.sendWindowProperty(this, 201, v >> 16);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int var, int value) {

		super.updateProgressBar(var, value);

		if (var == 200) _tempQuantity = value & 65535;
		if (var == 201) _dsu.setQuantity(_tempQuantity | (value << 16));
	}

	@Override
	protected int getPlayerInventoryVerticalOffset() {

		return 124;
	}
}
