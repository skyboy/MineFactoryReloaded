package powercrystals.minefactoryreloaded.gui.slot;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotViewOnly extends Slot
{
	public SlotViewOnly(IInventory inv, int index, int x, int y)
	{
		super(inv, index, x, y);
	}
	
	@Override
	public void putStack(ItemStack stack)
	{
	}
	
	@Override
	public ItemStack decrStackSize(int par1)
	{
		return null;
	}
	
	@Override
	public boolean canTakeStack(EntityPlayer p)
	{
		return false;
	}
	
	@Override
	public boolean isItemValid(ItemStack stack)
	{
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
    public boolean func_111238_b()
    {
        return false;
    }
}
