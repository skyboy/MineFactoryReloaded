package powercrystals.minefactoryreloaded.block;

import java.util.Map.Entry;

import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

import powercrystals.core.inventory.IInventoryManager;
import powercrystals.core.inventory.InventoryManager;
import powercrystals.core.util.UtilInventory;

public class BlockRailCargoPickup extends BlockFactoryRail
{
	public BlockRailCargoPickup(int id)
	{
		super(id, true, false);
		setUnlocalizedName("mfr.rail.cargo.pickup");
	}

	@Override
	public void onMinecartPass(World world, EntityMinecart entity, int x, int y, int z)
	{
		if (world.isRemote || !(entity instanceof IInventory))
			return;

		IInventoryManager minecart = InventoryManager.create((IInventory)entity, ForgeDirection.UNKNOWN);

		for(Entry<ForgeDirection, IInventory> inventory : UtilInventory.findChests(world, x, y, z).entrySet())
		{
			IInventoryManager chest = InventoryManager.create(inventory.getValue(), inventory.getKey().getOpposite()); 
			for(Entry<Integer, ItemStack> contents : chest.getContents().entrySet())
			{
				if(contents.getValue() == null)
				{
					continue;
				}
				ItemStack stackToAdd = contents.getValue().copy();

				ItemStack remaining = minecart.addItem(stackToAdd);

				if(remaining != null)
				{
					stackToAdd.stackSize -= remaining.stackSize;
					if(stackToAdd.stackSize > 0)
					{
						chest.removeItem(stackToAdd.stackSize, stackToAdd);
					}
				}
				else
				{
					chest.removeItem(stackToAdd.stackSize, stackToAdd);
					break;
				}
			}
		}
	}
}
