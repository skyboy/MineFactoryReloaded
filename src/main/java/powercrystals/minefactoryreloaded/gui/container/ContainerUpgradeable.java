package powercrystals.minefactoryreloaded.gui.container;

import net.minecraft.entity.player.InventoryPlayer;

import powercrystals.minefactoryreloaded.gui.slot.SlotAcceptUpgrade;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryPowered;

public class ContainerUpgradeable extends ContainerFactoryPowered {

	public ContainerUpgradeable(TileEntityFactoryPowered te, InventoryPlayer inv) {

		super(te, inv);
	}

	@Override
	protected void addSlots() {

		if (_te.getSizeInventory() > 1) {
			super.addSlots();
		}

		addSlotToContainer(new SlotAcceptUpgrade(_te, _te.getUpgradeSlot(), 152, 79) {

			@Override
			public int getSlotStackLimit() {

				return 1;
			}

		});
	}

	@Override
	protected int getPlayerInventoryVerticalOffset() {

		return 99;
	}

}
