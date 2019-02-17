package powercrystals.minefactoryreloaded.gui.container;

import cofh.core.gui.container.ContainerInventoryItem;
import cofh.core.gui.slot.SlotLocked;
import cofh.core.gui.slot.SlotValidated;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class ContainerBag extends ContainerInventoryItem
{

	public ContainerBag(@Nonnull ItemStack stack, InventoryPlayer inv) {

		super(stack, inv);

		for (int i = 0; i < getSizeInventory(); ++i) {
			int slot = i; //because lambda needs immutable vars
			this.addSlotToContainer(new SlotValidated(s -> containerWrapper.isItemValidForSlot(slot, s), this.containerWrapper, i, 44 + i * 18, 26));
		}

		bindPlayerInventory(inv);
	}

	@Override
	protected void bindPlayerInventory(InventoryPlayer inventoryPlayer) {

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9, 8 + j * 18, 66 + i * 18));
			}
		}

		for (int i = 0; i < 9; i++) {
			if (i == containerIndex) {
				addSlotToContainer(new SlotLocked(inventoryPlayer, i, 8 + i * 18, 66 + 58));
			} else {
				addSlotToContainer(new Slot(inventoryPlayer, i, 8 + i * 18, 66 + 58));
			}
		}
	}

	@Override
	protected boolean performMerge(int slotIndex, @Nonnull ItemStack stack) {

		int invFull = getSizeInventory();

		if (slotIndex < invFull) {
			return mergeItemStack(stack, invFull, inventorySlots.size(), true);
		}
		return mergeItemStack(stack, 0, invFull, false);
	}

	@Override
	protected int getPlayerInventoryVerticalOffset() {

		return 0;
	}

}
