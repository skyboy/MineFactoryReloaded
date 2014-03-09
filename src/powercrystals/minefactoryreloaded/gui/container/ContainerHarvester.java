package powercrystals.minefactoryreloaded.gui.container;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;
import powercrystals.minefactoryreloaded.gui.slot.SlotAcceptUpgrade;
import powercrystals.minefactoryreloaded.tile.machine.TileEntityHarvester;

public class ContainerHarvester extends ContainerUpgradable
{
	public ContainerHarvester(TileEntityHarvester te, InventoryPlayer inv)
	{
		super(te, inv);
	}
	
	@Override
	protected void addSlots()
	{
		addSlotToContainer(new SlotAcceptUpgrade(_te, 0, 152, 79));
	}
	
	@Override
	public void detectAndSendChanges()
	{
		super.detectAndSendChanges();
		
		for(int i = 0; i < crafters.size(); i++)
		{
			((ICrafting)crafters.get(i)).sendProgressBarUpdate(this, 100, getSetting("silkTouch"));
			((ICrafting)crafters.get(i)).sendProgressBarUpdate(this, 101, getSetting("harvestSmallMushrooms"));
		}
	}
	
	@Override
	public void updateProgressBar(int var, int value)
	{
		super.updateProgressBar(var, value);
		
		if(var == 100) setSetting("silkTouch", value);
		if(var == 101) setSetting("harvestSmallMushrooms", value);
	}
	
	private int getSetting(String setting)
	{
		TileEntityHarvester h = (TileEntityHarvester)_te;
		if(h.getSettings().get(setting) == null)
		{
			return 0;
		}
		return h.getSettings().get(setting) ? 1 : 0;
	}
	
	private void setSetting(String setting, int value)
	{
		((TileEntityHarvester)_te).getSettings().put(setting, value == 0 ? false : true);
	}
}
