package powercrystals.minefactoryreloaded.gui.container;

import net.minecraft.entity.player.InventoryPlayer;

import powercrystals.minefactoryreloaded.gui.slot.SlotAcceptUpgrade;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryPowered;

public class ContainerFountain extends ContainerUpgradeable
{
	public ContainerFountain(TileEntityFactoryPowered te, InventoryPlayer inv)
	{
		super(te, inv);
	}

	@Override
	protected void addSlots()
	{
		addSlotToContainer(new SlotAcceptUpgrade(_te, 0, 152, 79));
	}
}
