package powercrystals.minefactoryreloaded.gui.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import powercrystals.minefactoryreloaded.tile.rednet.TileEntityRedNetLogic;

import javax.annotation.Nonnull;

public class ContainerRedNetLogic extends Container
{
	private TileEntityRedNetLogic logic;
	
	public ContainerRedNetLogic(TileEntityRedNetLogic logic)
	{
		this.logic = logic;
		logic.crafters++;
	}
	
	@Override
	public void onContainerClosed(EntityPlayer entityplayer)
	{
		super.onContainerClosed(entityplayer);
		logic.crafters--;
	}
	
	@Override
	public void putStackInSlot(int par1, @Nonnull ItemStack par2ItemStack)
	{
		return;
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer entityplayer)
	{
		return true;
	}
}
