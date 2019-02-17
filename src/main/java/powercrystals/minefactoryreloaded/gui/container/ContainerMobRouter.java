package powercrystals.minefactoryreloaded.gui.container;

import net.minecraft.entity.player.InventoryPlayer;

import net.minecraft.inventory.IContainerListener;
import powercrystals.minefactoryreloaded.gui.slot.SlotAcceptReusableSafariNet;
import powercrystals.minefactoryreloaded.tile.machine.mobs.TileEntityMobRouter;


public class ContainerMobRouter extends ContainerFactoryPowered
{
	private TileEntityMobRouter _router;
	public ContainerMobRouter(TileEntityMobRouter te, InventoryPlayer inv)
	{
		super(te, inv);
		_router = te;
	}
	
	@Override
	protected void addSlots()
	{
		addSlotToContainer(new SlotAcceptReusableSafariNet(_te, 0, 8, 24));
	}
	
	@Override
	public void detectAndSendChanges()
	{
		super.detectAndSendChanges();
		int data = (_router.getWhiteList() ? 1 : 0) |
				(_router.getMatchMode() << 1);
		for (IContainerListener listener : listeners) {
			listener.sendWindowProperty(this, 100, data);
		}
	}
	
	@Override
	public void updateProgressBar(int var, int value)
	{
		super.updateProgressBar(var, value);
		if (var == 100)
		{
			_router.setWhiteList((value & 1) == 1);
			_router.setMatchMode((value & 0xFFFF) >> 1);
		}
	}
}
