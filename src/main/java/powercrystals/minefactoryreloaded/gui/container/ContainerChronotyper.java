package powercrystals.minefactoryreloaded.gui.container;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;
import powercrystals.minefactoryreloaded.tile.machine.animals.TileEntityChronotyper;

public class ContainerChronotyper extends ContainerFactoryPowered
{
	private TileEntityChronotyper _chronotyper;
	
	public ContainerChronotyper(TileEntityChronotyper te, InventoryPlayer inv)
	{
		super(te, inv);
		_chronotyper = te;
	}
	
	@Override
	public void detectAndSendChanges()
	{
		super.detectAndSendChanges();
		for (IContainerListener listener : listeners) {
			listener.sendWindowProperty(this, 100, _chronotyper.getMoveOld() ? 1 : 0);
		}
	}
	
	@Override
	public void updateProgressBar(int var, int value)
	{
		super.updateProgressBar(var, value);
		
		if(var == 100)
		{
			_chronotyper.setMoveOld(value == 0 ? false : true);
		}
	}
}
