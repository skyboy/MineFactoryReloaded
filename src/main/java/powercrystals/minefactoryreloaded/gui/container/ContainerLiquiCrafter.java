package powercrystals.minefactoryreloaded.gui.container;

import cofh.core.gui.slot.SlotLocked;
import cofh.core.gui.slot.SlotRemoveOnly;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import powercrystals.minefactoryreloaded.gui.slot.SlotFake;
import powercrystals.minefactoryreloaded.tile.machine.TileEntityLiquiCrafter;

import javax.annotation.Nonnull;

public class ContainerLiquiCrafter extends ContainerFactoryInventory {

	public ContainerLiquiCrafter(TileEntityLiquiCrafter crafter, InventoryPlayer inventoryPlayer) {

		super(crafter, inventoryPlayer);
	}

	@Override
	protected void addSlots() {

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				addSlotToContainer(new SlotFake(_te, j + i * 3, 28 + 8 + j * 18, 20 + i * 18));
			}
		}

		addSlotToContainer(new SlotLocked(_te, 9, 28 + 80, 38));
		addSlotToContainer(new SlotRemoveOnly(_te, 10, 28 + 134, 38));

		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 9; j++) {
				addSlotToContainer(new Slot(_te, 11 + j + i * 9, 28 + 8 + j * 18, 79 + i * 18));
			}
		}
	}

	@Override
	protected boolean performMerge(int slot, @Nonnull ItemStack stackInSlot) {

		if (slot == 9) {
			return false;
		}
		if (slot < 29) {
			return mergeItemStack(stackInSlot, 29, inventorySlots.size(), true);
		}
		return mergeItemStack(stackInSlot, 11, 29, false);
	}

	@Override
	protected int getPlayerInventoryVerticalOffset() {

		return 133;
	}

	@Override
	protected int getPlayerInventoryHorizontalOffset() {

		return 8 + 28;
	}

}
