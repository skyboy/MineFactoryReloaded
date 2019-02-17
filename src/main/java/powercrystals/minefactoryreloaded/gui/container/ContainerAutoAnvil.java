package powercrystals.minefactoryreloaded.gui.container;

import cofh.core.gui.slot.SlotLocked;
import cofh.core.gui.slot.SlotRemoveOnly;
import cofh.core.util.helpers.InventoryHelper;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import powercrystals.minefactoryreloaded.tile.machine.enchantment.TileEntityAutoAnvil;

import javax.annotation.Nonnull;

public class ContainerAutoAnvil extends ContainerFactoryPowered {

	private TileEntityAutoAnvil _anvil;
	private boolean repairOnly;

	public ContainerAutoAnvil(TileEntityAutoAnvil anvil, InventoryPlayer inv) {

		super(anvil, inv);
		_anvil = anvil;
		repairOnly = !anvil.getRepairOnly();
	}

	@Override
	protected void addSlots() {

		IItemHandler handler = InventoryHelper.getItemHandlerCap(_te, null);
		addSlotToContainer(new SlotItemHandler(handler, 0, 8, 24));
		addSlotToContainer(new SlotItemHandler(handler, 1, 26, 24));
		addSlotToContainer(new SlotRemoveOnly(_te, 2, 8, 48));
		addSlotToContainer(new SlotLocked(_te, 3, 45, 24) {

			@Nonnull
			@Override
			public ItemStack getStack() {

				return _anvil.getRepairOutput();
			}

			@Override
			public void onSlotChanged() {

			}
		});

		getSlot(1).setBackgroundName(ContainerAutoDisenchanter.background);
	}

	@Override
	public void detectAndSendChanges() {

		super.detectAndSendChanges();
		if (_anvil.getRepairOnly() != repairOnly) {
			repairOnly = _anvil.getRepairOnly();
			int data = (repairOnly ? 1 : 0);
			for (IContainerListener listener : listeners) {
				listener.sendWindowProperty(this, 100, data);
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int var, int value) {

		super.updateProgressBar(var, value);

		if (var == 100) {
			_anvil.setRepairOnly((value & 1) == 1);
		}
	}
}
