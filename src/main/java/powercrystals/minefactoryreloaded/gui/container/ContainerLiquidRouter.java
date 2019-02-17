package powercrystals.minefactoryreloaded.gui.container;

import net.minecraft.entity.player.InventoryPlayer;

import powercrystals.minefactoryreloaded.gui.slot.SlotFake;
import powercrystals.minefactoryreloaded.tile.machine.routing.TileEntityLiquidRouter;

public class ContainerLiquidRouter extends ContainerFactoryInventory {

	public ContainerLiquidRouter(TileEntityLiquidRouter router, InventoryPlayer inventoryPlayer) {

		super(router, inventoryPlayer);
	}

	@Override
	protected void addSlots() {

		for (int i = 0; i < 6; i++) {
			addSlotToContainer(new SlotFake(_te, i, 8 + i * 18, 20));
		}
	}

	@Override
	protected int getPlayerInventoryVerticalOffset() {

		return 52;
	}

	@Override
	public boolean supportsShiftClick(int slot) {

		return false;
	}

}
