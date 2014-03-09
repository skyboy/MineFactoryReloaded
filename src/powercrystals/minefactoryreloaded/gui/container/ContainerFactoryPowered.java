package powercrystals.minefactoryreloaded.gui.container;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryPowered;

/* packet values:
 * 0: current work
 * 1: current energy
 * 2: current idle
 * 3: current tank 
 * 4: tank id
 * 5: tank meta
 */

public class ContainerFactoryPowered extends ContainerFactoryInventory
{
	int energyTemp;
	
	public ContainerFactoryPowered(TileEntityFactoryPowered te, InventoryPlayer inv)
	{
		super(te, inv);
	}
	
	@Override
	public void detectAndSendChanges()
	{
		super.detectAndSendChanges();
		for(int i = 0; i < crafters.size(); i++)
		{
			((ICrafting)crafters.get(i)).sendProgressBarUpdate(this, 0, ((TileEntityFactoryPowered)_te).getWorkDone());
			((ICrafting)crafters.get(i)).sendProgressBarUpdate(this, 1, ((TileEntityFactoryPowered)_te).getEnergyStored() & 65535);
			((ICrafting)crafters.get(i)).sendProgressBarUpdate(this, 2, ((TileEntityFactoryPowered)_te).getIdleTicks());
			((ICrafting)crafters.get(i)).sendProgressBarUpdate(this, 3, (((TileEntityFactoryPowered)_te).getEnergyStored() >> 16) & 65535);
		}
	}
	
	@Override
	public void updateProgressBar(int var, int value)
	{
		super.updateProgressBar(var, value);
		
		if(var == 0) ((TileEntityFactoryPowered)_te).setWorkDone(value);
		else if(var == 1) energyTemp = (value & 65535);
		else if(var == 2) ((TileEntityFactoryPowered)_te).setIdleTicks(value);
		else if(var == 3) ((TileEntityFactoryPowered)_te).setEnergyStored(((value & 65535) << 16) | energyTemp);
	}
}
