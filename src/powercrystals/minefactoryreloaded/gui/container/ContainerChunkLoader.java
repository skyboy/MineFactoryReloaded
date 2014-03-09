package powercrystals.minefactoryreloaded.gui.container;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;

import powercrystals.minefactoryreloaded.tile.machine.TileEntityChunkLoader;

public class ContainerChunkLoader extends ContainerFactoryPowered
{
	protected TileEntityChunkLoader _cl;

	public ContainerChunkLoader(TileEntityChunkLoader te, InventoryPlayer inv)
	{
		super(te, inv);
		_cl = te;
	}

	@Override
	public void detectAndSendChanges()
	{
		super.detectAndSendChanges();

		short radius = _cl.getRadius();
		short empty = _cl.getEmpty();
		for(int i = 0; i < crafters.size(); i++)
		{
			((ICrafting)crafters.get(i)).sendProgressBarUpdate(this, 100, radius);
			((ICrafting)crafters.get(i)).sendProgressBarUpdate(this, 101, empty);
		}
	}

	@Override
	public void updateProgressBar(int var, int value)
	{
		super.updateProgressBar(var, value);

		if(var == 100) _cl.setRadius((short)value);
		if(var == 101) _cl.setEmpty(value & 65535);
	}

	@Override public void addSlots() {}
}
