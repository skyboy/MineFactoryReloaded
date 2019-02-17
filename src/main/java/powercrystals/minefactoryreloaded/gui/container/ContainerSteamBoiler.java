package powercrystals.minefactoryreloaded.gui.container;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;

import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryInventory;
import powercrystals.minefactoryreloaded.tile.machine.power.TileEntitySteamBoiler;

public class ContainerSteamBoiler extends ContainerFactoryInventory
{
	private int workTemp;
	public ContainerSteamBoiler(TileEntityFactoryInventory tileentity, InventoryPlayer inv)
	{
		super(tileentity, inv);
	}
	
	@Override
	protected void addSlots()
	{
		for (int i = 0; i < 4; ++i)
			addSlotToContainer(new Slot(_te, i, 8 + i * 18, 23));
	}
	
	@Override
	public void detectAndSendChanges()
	{
		super.detectAndSendChanges();
		for (IContainerListener listener : listeners) {
			listener.sendWindowProperty(this, 0, ((TileEntitySteamBoiler) _te).getWorkDone() & 65535);
			listener.sendWindowProperty(this, 1, ((TileEntitySteamBoiler) _te).getWorkDone() >> 16);
			int temp = (int) (((TileEntitySteamBoiler) _te).getTemp() * 10);
			listener.sendWindowProperty(this, 2, temp);
			listener.sendWindowProperty(this, 3, ((TileEntitySteamBoiler) _te).getWorkMax() & 65535);
			listener.sendWindowProperty(this, 4, ((TileEntitySteamBoiler) _te).getWorkMax() >> 16);
		}
	}
	
	@Override
	public void updateProgressBar(int var, int value)
	{
		super.updateProgressBar(var, value);
		
		if(var == 0) workTemp = (value & 65535);
		else if(var == 1) ((TileEntitySteamBoiler)_te).setWorkDone(((value & 65535) << 16) | workTemp);
		else if(var == 2) ((TileEntitySteamBoiler)_te).setTemp(value);
		else if(var == 3) workTemp = (value & 65535);
		else if(var == 4) ((TileEntitySteamBoiler)_te).setWorkMax(((value & 65535) << 16) | workTemp);
	}
}
