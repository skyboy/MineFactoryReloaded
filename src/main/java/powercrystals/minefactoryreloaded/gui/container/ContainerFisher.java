package powercrystals.minefactoryreloaded.gui.container;

import cofh.core.util.helpers.InventoryHelper;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryPowered;
import powercrystals.minefactoryreloaded.tile.machine.animals.TileEntityFisher;

public class ContainerFisher extends ContainerFactoryPowered {

	public static String background;

	public ContainerFisher(TileEntityFactoryPowered te, InventoryPlayer inv) {

		super(te, inv);
	}

	@Override
	public void addSlots() {

		IItemHandler handler = InventoryHelper.getItemHandlerCap(_te, null);
		addSlotToContainer(new SlotItemHandler(handler, 0, 8, 24));

		getSlot(0).setBackgroundName(background);
	}

	@Override
	public void detectAndSendChanges() {

		super.detectAndSendChanges();
		for (IContainerListener listener : listeners) {
			listener.sendWindowProperty(this, 100, ((TileEntityFactoryPowered) _te).getWorkMax() & 65535);
			listener.sendWindowProperty(this, 101, ((TileEntityFactoryPowered) _te).getWorkMax() >>> 16);
		}
	}

	@Override
	public void updateProgressBar(int var, int value) {

		super.updateProgressBar(var, value);

		if (var == 100)
			workTemp = (value & 65535);
		else if (var == 101)
			((TileEntityFisher) _te).setWorkMax(((value & 65535) << 16) | workTemp);
	}

}
