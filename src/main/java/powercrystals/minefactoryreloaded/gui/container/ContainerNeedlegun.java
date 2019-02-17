package powercrystals.minefactoryreloaded.gui.container;

import cofh.core.gui.slot.SlotLocked;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import powercrystals.minefactoryreloaded.core.UtilInventory;
import powercrystals.minefactoryreloaded.gui.NeedlegunContainerWrapper;
import powercrystals.minefactoryreloaded.gui.slot.SlotAcceptNeedlegunAmmo;

import javax.annotation.Nonnull;

public class ContainerNeedlegun extends Container
{
	private NeedlegunContainerWrapper _ncw;
	private int _nsi;

	public ContainerNeedlegun(NeedlegunContainerWrapper ncw, InventoryPlayer inv)
	{
		_ncw = ncw;
		_nsi = inv.currentItem;
		addSlotToContainer(new SlotAcceptNeedlegunAmmo(_ncw, 0, 80, 30));
		bindPlayerInventory(inv);
	}

	protected void bindPlayerInventory(InventoryPlayer inventoryPlayer)
	{
		for (int i = 0; i < 3; i++)
		{
			for (int j = 0; j < 9; j++)
			{
				addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}

		for (int i = 0; i < 9; i++)
		{
			if (i == _nsi)
				addSlotToContainer(new SlotLocked(inventoryPlayer, i, 8 + i * 18, 84 + 58));
			else
				addSlotToContainer(new Slot(inventoryPlayer, i, 8 + i * 18, 84 + 58));
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer entityplayer)
	{
		return true;
	}

	@Nonnull
	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slot)
	{
		return ItemStack.EMPTY;
	}

	@Override
	public void onContainerClosed(EntityPlayer player)
	{
		if (UtilInventory.stacksEqual(player.inventory.mainInventory.get(_nsi), _ncw.getStack(), false))
			player.inventory.mainInventory.set(_nsi, _ncw.getStack());
		else
			player.dropItem(inventorySlots.get(0).getStack(), false, true);
		super.onContainerClosed(player);
	}
}
