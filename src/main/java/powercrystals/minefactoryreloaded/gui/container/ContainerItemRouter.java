package powercrystals.minefactoryreloaded.gui.container;

import net.minecraft.inventory.IContainerListener;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.minecraft.entity.player.InventoryPlayer;

import powercrystals.minefactoryreloaded.gui.slot.SlotFake;
import powercrystals.minefactoryreloaded.tile.machine.routing.TileEntityItemRouter;

public class ContainerItemRouter extends ContainerFactoryInventory {

	private TileEntityItemRouter _router;

	public ContainerItemRouter(TileEntityItemRouter router, InventoryPlayer inventoryPlayer) {

		super(router, inventoryPlayer);
		_router = router;
	}

	@Override
	protected void addSlots() {

		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 9; j++) {
				addSlotToContainer(new SlotFake(_te, j + i * 9, 8 + j * 18, 40 + i * 18));
			}
		}
	}

	@Override
	public void detectAndSendChanges() {

		super.detectAndSendChanges();
		for (IContainerListener listener : listeners) {
			listener.sendWindowProperty(this, 100, _router.getRejectUnmapped() ? 1 : 0);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int var, int value) {

		super.updateProgressBar(var, value);

		if (var == 100) _router.setRejectUnmapped(value == 1 ? true : false);
	}

	@Override
	protected int getPlayerInventoryVerticalOffset() {

		return 144;
	}

	@Override
	protected boolean supportsShiftClick(int par2) {

		return false;
	}

}
