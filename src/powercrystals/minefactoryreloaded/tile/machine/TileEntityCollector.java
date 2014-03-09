package powercrystals.minefactoryreloaded.tile.machine;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.ForgeDirection;

import powercrystals.core.util.UtilInventory;
import powercrystals.minefactoryreloaded.core.IEntityCollidable;
import powercrystals.minefactoryreloaded.core.MFRUtil;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryInventory;

public class TileEntityCollector extends TileEntityFactoryInventory implements IEntityCollidable
{
	protected boolean canStuff;
	
	public TileEntityCollector()
	{
		super(Machine.ItemCollector);
		setManageSolids(true);
		canStuff = false;
	}

	@Override
	public void onEntityCollided(Entity entity)
	{
		if (failedDrops == null && entity instanceof EntityItem)
			addToChests((EntityItem)entity);
	}

	protected void addToChests(EntityItem i)
	{
		if (i.isDead)
			return;
		
		ItemStack s = addToChests(i.getEntityItem());
		if (s == null)
		{
			i.setDead();
			return;
		}
		i.setEntityItemStack(s);
	}
	
	protected ItemStack addToChests(ItemStack s)
	{
		s = UtilInventory.dropStack(this, s,
				MFRUtil.directionsWithoutConveyors(worldObj, xCoord, yCoord, zCoord), ForgeDirection.UNKNOWN);
		if (canStuff & failedDrops == null & s != null)
		{
			doDrop(s);
			s = null;
		}
		return s;
	}

	@Override
	public boolean hasWorldObj()
	{
		return worldObj != null & failedDrops != null;
	}

	@Override
	public int getComparatorOutput(int side)
	{
		return failedDrops != null ? 15 : 0;
	}
	
	@Override
	public ForgeDirection getDropDirection()
	{
		return ForgeDirection.UNKNOWN;
	}
	
	@Override
	public ForgeDirection[] getDropDirections()
	{
		return MFRUtil.directionsWithoutConveyors(worldObj, xCoord, yCoord, zCoord);
	}
	
	@Override
	public int getSizeInventory()
	{
		return 0;
	}
	
	@Override
	protected void writeItemNBT(NBTTagCompound tag)
	{
		super.writeItemNBT(tag);
		if (canStuff)
			tag.setBoolean("hasTinkerStuff", true);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		super.readFromNBT(tag);
		canStuff = tag.getBoolean("hasTinkerStuff");
		setIsActive(canStuff);
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag)
	{
		super.writeToNBT(tag);
		tag.setBoolean("hasTinkerStuff", canStuff);
	}
}
