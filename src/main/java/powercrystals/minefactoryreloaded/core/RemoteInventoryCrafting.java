package powercrystals.minefactoryreloaded.core;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;

public class RemoteInventoryCrafting extends InventoryCrafting
{
	public RemoteInventoryCrafting()
	{
		super(new Container()
		{
			@Override
			public boolean canInteractWith(EntityPlayer var1)
			{
				return false;
			}
		},
		3, 3);
	}
}
