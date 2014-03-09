package powercrystals.minefactoryreloaded.gui.container;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;

import powercrystals.minefactoryreloaded.gui.slot.SlotRemoveOnly;
import powercrystals.minefactoryreloaded.tile.machine.TileEntityAutoAnvil;

public class ContainerAutoAnvil extends ContainerFactoryPowered
{
	private TileEntityAutoAnvil _anvil;
	private boolean repairOnly;

	public ContainerAutoAnvil(TileEntityAutoAnvil anvil, InventoryPlayer inv)
	{
		super(anvil, inv);
		_anvil = anvil;
		repairOnly = anvil.getRepairOnly();
	}

	@Override
	protected void addSlots()
	{
		addSlotToContainer(new Slot(_te, 0, 8, 24));
		addSlotToContainer(new Slot(_te, 1, 26, 24));
		addSlotToContainer(new SlotRemoveOnly(_te, 2, 8, 48));
	}

	@Override
	public void detectAndSendChanges()
	{
		super.detectAndSendChanges();
		if (_anvil.getRepairOnly() != repairOnly)
		{
			repairOnly = _anvil.getRepairOnly();
			int data = (repairOnly ? 1 : 0);
			for(int i = 0; i < crafters.size(); i++)
			{
				((ICrafting)crafters.get(i)).sendProgressBarUpdate(this, 100, data);
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int var, int value)
	{
		super.updateProgressBar(var, value);

		if (var == 100)
		{
			_anvil.setRepairOnly((value & 1) == 1);
		}
	}
}
