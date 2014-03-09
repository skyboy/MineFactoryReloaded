package powercrystals.minefactoryreloaded.core;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidContainerItem;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fluids.IFluidTank;
import powercrystals.core.position.BlockPosition;
import powercrystals.core.util.UtilInventory;
import powercrystals.minefactoryreloaded.setup.MFRConfig;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactory;

public abstract class MFRLiquidMover
{
	/**
	 * Attempts to fill tank with the player's current item.
	 * @param	itcb			the tank the liquid is going into
	 * @param	entityplayer	the player trying to fill the tank
	 * @return	True if liquid was transferred to the tank.
	 */
	public static boolean manuallyFillTank(ITankContainerBucketable itcb, EntityPlayer entityplayer)
	{
		ItemStack ci = entityplayer.inventory.getCurrentItem();
		FluidStack liquid = FluidContainerRegistry.getFluidForFilledItem(ci);
		if(liquid != null)
		{
			Item item = ci.getItem();
			if(itcb.fill(ForgeDirection.UNKNOWN, liquid, false) == liquid.amount)
			{
				itcb.fill(ForgeDirection.UNKNOWN, liquid, true);
				if(!entityplayer.capabilities.isCreativeMode)
				{
					if (item.hasContainerItem()) {
						ItemStack drop = item.getContainerItemStack(ci);
						if (drop.isItemStackDamageable() && drop.getItemDamage() > drop.getMaxDamage())
							drop = null;
						disposePlayerItem(ci, drop, entityplayer, true);
					} else
						entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, UtilInventory.consumeItem(ci, entityplayer));
				}
				return true;
			}
		}
		else if (ci != null && ci.getItem() instanceof IFluidContainerItem)
		{
			Item item = ci.getItem();
			IFluidContainerItem fluidContainer = (IFluidContainerItem)item;
			liquid = fluidContainer.getFluid(ci);
			if (itcb.fill(ForgeDirection.UNKNOWN, liquid, false) > 0) {
				int amount = itcb.fill(ForgeDirection.UNKNOWN, liquid, true);
				ItemStack drop = ci.splitStack(1);
				ci.stackSize++;
				fluidContainer.drain(drop, amount, true);
				if (!entityplayer.capabilities.isCreativeMode) {
					if (item.hasContainerItem()) {
						drop = item.getContainerItemStack(drop);
						if (drop.isItemStackDamageable() && drop.getItemDamage() > drop.getMaxDamage())
							drop = null;
					}
					disposePlayerItem(ci, drop, entityplayer, true);
				}
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Attempts to drain tank into the player's current item.
	 * @param	itcb			the tank the liquid is coming from
	 * @param	entityplayer	the player trying to take liquid from the tank
	 * @return	True if liquid was transferred from the tank.
	 */
	public static boolean manuallyDrainTank(ITankContainerBucketable itcb, EntityPlayer entityplayer)
	{
		ItemStack ci = entityplayer.inventory.getCurrentItem();
		boolean isSmartContainer = false;
		IFluidContainerItem fluidContainer;
		if(ci != null && (FluidContainerRegistry.isEmptyContainer(ci) ||
				(isSmartContainer = ci.getItem() instanceof IFluidContainerItem)))
		{
			for(FluidTankInfo tank : itcb.getTankInfo(ForgeDirection.UNKNOWN))
			{
				FluidStack tankLiquid = tank.fluid;
				if (tankLiquid == null || tankLiquid.amount == 0)
					continue;
				ItemStack filledBucket = null;
				FluidStack bucketLiquid = null;
				if (isSmartContainer)
				{
					fluidContainer = (IFluidContainerItem)ci.getItem();
					filledBucket = ci.copy();
					filledBucket.stackSize = 1;
					if (fluidContainer.fill(filledBucket, tankLiquid, false) > 0) {
						int amount = fluidContainer.fill(filledBucket, tankLiquid, true);
						bucketLiquid = new FluidStack(tankLiquid.fluidID, amount);
						FluidStack l = itcb.drain(ForgeDirection.UNKNOWN, bucketLiquid, false); 
						if (l == null || l.amount < amount)
							filledBucket = null;
					}
					else
						filledBucket = null;
				}
				else
				{
					filledBucket = FluidContainerRegistry.fillFluidContainer(tankLiquid, ci);
					if(FluidContainerRegistry.isFilledContainer(filledBucket))
					{
						bucketLiquid = FluidContainerRegistry.getFluidForFilledItem(filledBucket);
						FluidStack l = itcb.drain(ForgeDirection.UNKNOWN, bucketLiquid, false);
						if (l == null || l.amount < bucketLiquid.amount)
							filledBucket = null;
					}
					else
						filledBucket = null;
				}
				if (filledBucket != null)
				{
					if (disposePlayerItem(ci, filledBucket, entityplayer, MFRConfig.dropFilledContainers.getBoolean(true)))
					{
						itcb.drain(ForgeDirection.UNKNOWN, bucketLiquid, true);
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public static boolean disposePlayerItem(ItemStack stack, ItemStack dropStack, EntityPlayer entityplayer, boolean allowDrop)
	{
		return disposePlayerItem(stack, dropStack, entityplayer, allowDrop, true);
	}
	
	public static boolean disposePlayerItem(ItemStack stack, ItemStack dropStack,
			EntityPlayer entityplayer, boolean allowDrop, boolean allowReplace)
	{
		if (entityplayer == null || entityplayer.capabilities.isCreativeMode)
			return true;
		if (allowReplace && stack.stackSize <= 1)
		{
			entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, dropStack);
			return true;
		}
		else if (allowDrop)
		{
			stack.stackSize -= 1;
			if (dropStack != null && !entityplayer.inventory.addItemStackToInventory(dropStack))
			{
				entityplayer.dropPlayerItem(dropStack);
			}
			return true;
		}
		return false;
	}
	
	public static void pumpLiquid(IFluidTank iFluidTank, TileEntityFactory from)
	{
		if(iFluidTank != null && iFluidTank.getFluid() != null && iFluidTank.getFluid().amount > 0)
		{
			FluidStack l = iFluidTank.getFluid().copy();
			l.amount = Math.min(l.amount, FluidContainerRegistry.BUCKET_VOLUME);
			for(BlockPosition adj : new BlockPosition(from).getAdjacent(true))
			{
				TileEntity tile = from.worldObj.getBlockTileEntity(adj.x, adj.y, adj.z);
				if(tile instanceof IFluidHandler)
				{
					int filled = ((IFluidHandler)tile).fill(adj.orientation.getOpposite(), l, true);
					iFluidTank.drain(filled, true);
					l.amount -= filled;
					if(l.amount <= 0)
					{
						break;
					}
				}
			}
		}
	}
	
}