package powercrystals.minefactoryreloaded.gui.container;

import net.minecraft.inventory.IContainerListener;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;

import powercrystals.minefactoryreloaded.gui.slot.SlotAcceptUpgrade;
import powercrystals.minefactoryreloaded.gui.slot.SlotFake;
import powercrystals.minefactoryreloaded.tile.machine.plants.TileEntityPlanter;

public class ContainerPlanter extends ContainerUpgradeable
{
	private TileEntityPlanter _planter;
	private boolean consumeAll;

	public ContainerPlanter(TileEntityPlanter te, InventoryPlayer inv)
	{
		super(te, inv);
		_planter = te;
		consumeAll = !te.getConsumeAll();
	}

	@Override
	public void detectAndSendChanges()
	{
		super.detectAndSendChanges();
		if (_planter.getConsumeAll() != consumeAll)
		{
			consumeAll = _planter.getConsumeAll();
			int data = (consumeAll ? 1 : 0);
			for (IContainerListener listener : listeners) {
				listener.sendWindowProperty(this, 100, data);
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
			_planter.setConsumeAll((value & 1) == 1);
		}
	}

	@Override
	protected void addSlots()
	{
		//area control slots
		addSlotToContainer(new SlotFake(_te, 0, 8, 33));
		addSlotToContainer(new SlotFake(_te, 1, 26, 33));
		addSlotToContainer(new SlotFake(_te, 2, 44, 33));
		addSlotToContainer(new SlotFake(_te, 3, 8, 51));
		addSlotToContainer(new SlotFake(_te, 4, 26, 51));
		addSlotToContainer(new SlotFake(_te, 5, 44, 51));
		addSlotToContainer(new SlotFake(_te, 6, 8, 69));
		addSlotToContainer(new SlotFake(_te, 7, 26, 69));
		addSlotToContainer(new SlotFake(_te, 8, 44, 69));

		//upgrade slot
		addSlotToContainer(new SlotAcceptUpgrade(_te, 9, 152, 79));

		//resource slots
		int xStart = 65;
		int yStart = 15;

		for(int i = 0; i < 4; i++)
		{
			for(int j = 0; j < 4; j++)
			{
				addSlotToContainer(new Slot(_te, 10 + i*4 + j, xStart + 18 * j, yStart + 18*i));
			}
		}
	}

	@Override
	protected int getPlayerInventoryVerticalOffset()
	{
		return 99 + 20;
	}
}
