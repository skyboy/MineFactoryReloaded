package powercrystals.minefactoryreloaded.core;

import cofh.core.fluid.FluidTankCore;
import net.minecraft.util.math.BlockPos;

import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.*;

import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import powercrystals.minefactoryreloaded.setup.MFRConfig;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactory;

public abstract class MFRLiquidMover {

	/**
	 * Attempts to fill tank with the player's current item.
	 *
	 * @param    itcb            the tank the liquid is going into
	 * @param    entityplayer    the player trying to fill the tank
	 * @return True if liquid was transferred to the tank.
	 */
	public static boolean manuallyFillTank(ITankContainerBucketable itcb, EnumFacing facing, EntityPlayer entityplayer, ItemStack heldItem) {

		if (heldItem != null && heldItem.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null)) {
			IFluidHandler fluidHandler = heldItem.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
			IFluidTankProperties[] itemTanks = fluidHandler.getTankProperties();
			boolean tankFilled = false;

			// TODO: this logic broken. consumes full buckets for 1 mB
			for (IFluidTankProperties itemTank : itemTanks) {
				FluidStack liquid = itemTank.getContents();
				if (liquid != null && itcb.fill(facing, liquid, false) > 0) {
					tankFilled = true;
					liquid.amount = itcb.fill(facing, liquid, true);
					ItemStack drop = heldItem.splitStack(1);
					heldItem.stackSize++;
					drop.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null).drain(liquid, true);
					if (!entityplayer.capabilities.isCreativeMode) {
						disposePlayerItem(heldItem, drop, entityplayer, true);
						if (!entityplayer.worldObj.isRemote) {
							entityplayer.openContainer.detectAndSendChanges();
							((EntityPlayerMP) entityplayer).updateCraftingInventory(entityplayer.openContainer, entityplayer.openContainer.getInventory());
						}
					}
				}
			}
			return tankFilled;
		}
		return false;
	}

	/**
	 * Attempts to drain tank into the player's current item.
	 *
	 * @param    itcb            the tank the liquid is coming from
	 * @param    entityplayer    the player trying to take liquid from the tank
	 * @return True if liquid was transferred from the tank.
	 */
	public static boolean manuallyDrainTank(ITankContainerBucketable itcb, EnumFacing facing, EntityPlayer entityplayer, ItemStack heldItem) {

		if (heldItem.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null)) {
			IFluidHandler fluidHandler = heldItem.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);

			for (IFluidTankProperties tank : itcb.getTankProperties(facing)) {
				FluidStack tankLiquid = tank.getContents();
				if (tankLiquid == null || tankLiquid.amount == 0)
					continue;
				ItemStack containerToDrop = null;
				FluidStack bucketLiquid;

				if (heldItem.stackSize > 1) {
					containerToDrop = heldItem.copy();
					containerToDrop.stackSize = 1;
					fluidHandler = containerToDrop.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
				}

				int amount = fluidHandler.fill(tankLiquid, false);
				if (amount > 0) {
					bucketLiquid = new FluidStack(tankLiquid, amount);
					FluidStack l = itcb.drain(facing, bucketLiquid, false);
					if (l == null || l.amount < amount)
						continue;
					fluidHandler.fill(tankLiquid, true);
				} else
					continue;

				if (containerToDrop == null || disposePlayerItem(heldItem, containerToDrop, entityplayer, MFRConfig.dropFilledContainers.getBoolean(true))) {
					if (!entityplayer.worldObj.isRemote) {
						entityplayer.openContainer.detectAndSendChanges();
						((EntityPlayerMP) entityplayer).updateCraftingInventory(entityplayer.openContainer, entityplayer.openContainer.getInventory());
					}
					itcb.drain(facing, bucketLiquid, true);
					return true;
				}
			}
		}
		return false;
	}

	private static boolean disposePlayerItem(ItemStack stack, ItemStack dropStack, EntityPlayer entityplayer, boolean allowDrop) {

		return disposePlayerItem(stack, dropStack, entityplayer, allowDrop, true);
	}

	public static boolean disposePlayerItem(ItemStack stack, ItemStack dropStack,
			EntityPlayer entityplayer, boolean allowDrop, boolean allowReplace) {

		if (entityplayer == null || entityplayer.capabilities.isCreativeMode)
			return true;
		if (allowReplace && stack.stackSize <= 1) {
			entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, dropStack);
			return true;
		} else if (allowDrop) {
			stack.stackSize -= 1;
			if (dropStack != null && !entityplayer.inventory.addItemStackToInventory(dropStack)) {
				entityplayer.dropItem(dropStack, false, true);
			}
			return true;
		}
		return false;
	}

	public static int fillTankWithXP(FluidTankCore tank, EntityXPOrb orb) {

		int maxAmount = tank.getSpace(), maxXP = (int) (maxAmount / 66.66666667f);
		if (maxAmount <= 0) {
			return 0;
		}
		int found = Math.min(orb.xpValue, maxXP);
		orb.xpValue -= found;
		if (orb.xpValue <= 0) {
			orb.setDead();
			found = Math.max(found, 0);
		}
		if (found > 0) {
			found = (int) (found * 66.66666667f);
			tank.fill(FluidRegistry.getFluidStack("mob_essence", found), true);
			return found;
		}
		return 0;
	}
	
	public static int fillTankWithXP(FluidTankCore tank, int XP) {

		int maxAmount = tank.getSpace(), maxXP = (int) (maxAmount / 66.66666667f);
		if (maxAmount <= 0) {
			return 0;
		}
		int found = Math.min(XP, maxXP);
		XP -= found;
		if (XP <= 0) {
			found = Math.max(found, 0);
		}
		if (found > 0) {
			found = (int) (found * 66.66666667f);
			tank.fill(FluidRegistry.getFluidStack("mob_essence", found), true);
			return found;
		}
		return 0;
	}
	
	public static void pumpLiquid(IFluidTank iFluidTank, TileEntityFactory from) {

		if (iFluidTank != null && iFluidTank.getFluid() != null && iFluidTank.getFluid().amount > 0) {
			FluidStack l = iFluidTank.getFluid().copy();
			l.amount = Math.min(l.amount, Fluid.BUCKET_VOLUME);
			for (EnumFacing facing : EnumFacing.VALUES) {
				BlockPos adj = from.getPos().offset(facing);
				TileEntity tile = from.getWorld().getTileEntity(adj);
				if (tile != null && tile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing.getOpposite())) {
					IFluidHandler fluidHandler = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing.getOpposite());
					if (fluidHandler.fill(l, false) == 0)
						continue;
					int filled = fluidHandler.fill(l, true);
					iFluidTank.drain(filled, true);
					l.amount -= filled;
					if (l.amount <= 0) {
						break;
					}
				}
			}
		}
	}

}
