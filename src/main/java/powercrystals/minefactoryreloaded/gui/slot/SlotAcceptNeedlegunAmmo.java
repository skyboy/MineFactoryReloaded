package powercrystals.minefactoryreloaded.gui.slot;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import powercrystals.minefactoryreloaded.api.handler.INeedleAmmo;

import javax.annotation.Nonnull;

public class SlotAcceptNeedlegunAmmo extends Slot
{
	public SlotAcceptNeedlegunAmmo(IInventory inv, int index, int x, int y)
	{
		super(inv, index, x, y);
	}
	
	@Override
	public boolean isItemValid(@Nonnull ItemStack stack)
	{
		return !stack.isEmpty() && stack.getItem() instanceof INeedleAmmo;
	}
}
