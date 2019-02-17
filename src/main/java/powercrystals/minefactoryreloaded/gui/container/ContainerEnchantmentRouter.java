package powercrystals.minefactoryreloaded.gui.container;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;
import powercrystals.minefactoryreloaded.gui.slot.SlotFake;
import powercrystals.minefactoryreloaded.tile.machine.routing.TileEntityEnchantmentRouter;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerEnchantmentRouter extends ContainerFactoryInventory
{
	private TileEntityEnchantmentRouter _router;
	
	public ContainerEnchantmentRouter(TileEntityEnchantmentRouter router, InventoryPlayer inventoryPlayer)
	{
		super(router, inventoryPlayer);	
		_router = router;
	}
	
	@Override
	protected void addSlots()
	{
		for(int i = 0; i < 5; i++)
		{
			for(int j = 0; j < 9; j++)
			{
				addSlotToContainer(new SlotFake(_te, j + i * 9, 8 + j * 18, 40 + i * 18));
			}
		}
	}
	
	@Override
	public void detectAndSendChanges()
	{
		super.detectAndSendChanges();
		for (IContainerListener listener : listeners) {
			int data = (_router.getRejectUnmapped() ? 1 : 0) |
					(_router.getMatchLevels() ? 2 : 0);
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
			_router.setRejectUnmapped((value & 1) == 1 ? true : false);
			_router.setMatchLevels((value & 2) == 2 ? true : false);
		}
	}
	
	@Override
	protected int getPlayerInventoryVerticalOffset()
	{
		return 144;
	}
}
