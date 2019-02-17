package powercrystals.minefactoryreloaded.gui.container;

import cofh.core.gui.slot.SlotRemoveOnly;
import cofh.core.util.helpers.InventoryHelper;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import powercrystals.minefactoryreloaded.tile.machine.enchantment.TileEntityAutoDisenchanter;

public class ContainerAutoDisenchanter extends ContainerFactoryPowered {

	public static String background;
	private TileEntityAutoDisenchanter _disenchanter;

	public ContainerAutoDisenchanter(TileEntityAutoDisenchanter disenchanter, InventoryPlayer inv) {

		super(disenchanter, inv);
		_disenchanter = disenchanter;
	}

	@Override
	protected void addSlots() {

		IItemHandler handler = InventoryHelper.getItemHandlerCap(_te, null);
		addSlotToContainer(new SlotItemHandler(handler, 0, 8, 18));
		addSlotToContainer(new SlotItemHandler(handler, 1, 26, 18));

		addSlotToContainer(new SlotRemoveOnly(_te, 4, 8, 37));

		addSlotToContainer(new SlotRemoveOnly(_te, 2, 8, 56));
		addSlotToContainer(new SlotRemoveOnly(_te, 3, 26, 56));

		getSlot(1).setBackgroundName(background);
		getSlot(4).setBackgroundName(background);
		// getSlot is for the slot id (order it was added) not the slot index
	}

	@Override
	public void detectAndSendChanges() {

		super.detectAndSendChanges();
		for (IContainerListener listener : listeners) {
			listener.sendWindowProperty(this, 100, _disenchanter.getRepeatDisenchant() ? 1 : 0);
		}
	}

	@Override
	public void updateProgressBar(int var, int value) {

		super.updateProgressBar(var, value);
		if (var == 100)
			_disenchanter.setRepeatDisenchant(value == 1 ? true : false);
	}
}
