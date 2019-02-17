package powercrystals.minefactoryreloaded.gui.container;

import cofh.core.gui.slot.SlotRemoveOnly;
import cofh.core.util.helpers.InventoryHelper;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import powercrystals.minefactoryreloaded.tile.machine.blocks.TileEntityBlockSmasher;

public class ContainerBlockSmasher extends ContainerFactoryPowered {

	private TileEntityBlockSmasher _smasher;

	public ContainerBlockSmasher(TileEntityBlockSmasher te, InventoryPlayer inv) {

		super(te, inv);
		_smasher = te;
	}

	@Override
	protected void addSlots() {

		IItemHandler handler = InventoryHelper.getItemHandlerCap(_te, null);
		addSlotToContainer(new SlotItemHandler(handler, 0, 8, 24));
		addSlotToContainer(new SlotRemoveOnly(_te, 1, 8, 54));
	}

	@Override
	public void detectAndSendChanges() {

		super.detectAndSendChanges();
		for (IContainerListener listener : listeners) {
			listener.sendWindowProperty(this, 100, _smasher.getFortune());
		}
	}

	@Override
	public void updateProgressBar(int var, int value) {

		super.updateProgressBar(var, value);
		if (var == 100)
			_smasher.setFortune(value);
	}
}
