package powercrystals.minefactoryreloaded.gui.container;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;

import powercrystals.minefactoryreloaded.gui.slot.SlotAcceptBlankRecord;
import powercrystals.minefactoryreloaded.gui.slot.SlotAcceptRecord;
import powercrystals.minefactoryreloaded.tile.machine.TileEntityAutoJukebox;

public class ContainerAutoJukebox extends ContainerFactoryInventory {

	public static String background;
	private TileEntityAutoJukebox _jukebox;

	public ContainerAutoJukebox(TileEntityAutoJukebox tileentity, InventoryPlayer inv) {

		super(tileentity, inv);
		_jukebox = tileentity;
	}

	@Override
	protected void addSlots() {

		addSlotToContainer(new SlotAcceptRecord(_te, 0, 8, 24));
		addSlotToContainer(new SlotAcceptBlankRecord(_te, 1, 8, 54));

		getSlot(0).setBackgroundName(background);
		getSlot(1).setBackgroundName(background);
	}

	@Override
	public void detectAndSendChanges() {

		super.detectAndSendChanges();
		for (IContainerListener listener : listeners) {
			if (listener != null) {
				listener.sendWindowProperty(this, 100, (_jukebox.getCanCopy() ? 1 : 0) | (_jukebox.getCanPlay() ? 2 : 0));
			}
		}
	}

	@Override
	public void updateProgressBar(int var, int value) {

		super.updateProgressBar(var, value);
		if (var == 100) {
			_jukebox.setCanCopy((value & 1) != 0 ? true : false);
			_jukebox.setCanPlay((value & 2) != 0 ? true : false);
		}
	}

}
