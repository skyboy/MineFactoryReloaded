package powercrystals.minefactoryreloaded.core;

import cofh.core.fluid.FluidTankCore;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactory;

import javax.annotation.Nonnull;

public abstract class MFRLiquidMover {

	public static boolean disposePlayerItem(@Nonnull ItemStack stack, @Nonnull ItemStack dropStack,
			EntityPlayer entityplayer, boolean allowDrop, boolean allowReplace) {

		if (entityplayer == null || entityplayer.capabilities.isCreativeMode)
			return true;
		if (allowReplace && stack.getCount() <= 1) {
			entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, dropStack);
			return true;
		} else if (allowDrop) {
			stack.shrink(1);
			if (!dropStack.isEmpty() && !entityplayer.inventory.addItemStackToInventory(dropStack)) {
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
