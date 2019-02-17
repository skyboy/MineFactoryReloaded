package powercrystals.minefactoryreloaded.gui.slot;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemRecord;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class SlotAcceptRecord extends Slot
{
	public SlotAcceptRecord(IInventory inv, int index, int x, int y)
	{
		super(inv, index, x, y);
	}
	
	@Override
	public boolean isItemValid(@Nonnull ItemStack stack)
	{
		return !stack.isEmpty() && stack.getItem() instanceof ItemRecord;
	}
}
