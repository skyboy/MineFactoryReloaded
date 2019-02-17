package powercrystals.minefactoryreloaded.gui.container;

import net.minecraft.inventory.IContainerListener;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.minecraft.entity.player.InventoryPlayer;

import powercrystals.minefactoryreloaded.gui.slot.SlotFake;
import powercrystals.minefactoryreloaded.tile.machine.routing.TileEntityEjector;

public class ContainerEjector extends ContainerFactoryInventory
{
	private TileEntityEjector _ejector;
	
	public ContainerEjector(TileEntityEjector tileentity, InventoryPlayer inv)
	{
		super(tileentity, inv);
		_ejector = tileentity;
	}
	
	@Override
	protected void addSlots()
	{
		addSlotToContainer(new SlotFake(_te, 0, 8, 15));
		addSlotToContainer(new SlotFake(_te, 1, 26, 15));
		addSlotToContainer(new SlotFake(_te, 2, 44, 15));
		addSlotToContainer(new SlotFake(_te, 3, 8, 33));
		addSlotToContainer(new SlotFake(_te, 4, 26, 33));
		addSlotToContainer(new SlotFake(_te, 5, 44, 33));
		addSlotToContainer(new SlotFake(_te, 6, 8, 51));
		addSlotToContainer(new SlotFake(_te, 7, 26, 51));
		addSlotToContainer(new SlotFake(_te, 8, 44, 51));
	}
	
	@Override
	public void detectAndSendChanges()
	{
		super.detectAndSendChanges();
		int data = (_ejector.getIsWhitelist() ? 1 : 0) |
				(_ejector.getIsNBTMatch() ? 2 : 0) |
				(_ejector.getIsIDMatch() ? 4 : 0);
		for (IContainerListener listener : listeners) {
			listener.sendWindowProperty(this, 100, data);
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int var, int value)
	{
		super.updateProgressBar(var, value);
		
		if (var == 100)
		{
			_ejector.setIsWhitelist((value & 1) == 1);
			_ejector.setIsNBTMatch((value & 2) == 2);
			_ejector.setIsIDMatch((value & 4) == 4);
		}
	}
}
