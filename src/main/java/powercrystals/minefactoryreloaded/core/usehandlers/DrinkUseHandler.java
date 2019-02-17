package powercrystals.minefactoryreloaded.core.usehandlers;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.core.IUseHandler;

import javax.annotation.Nonnull;

public class DrinkUseHandler implements IUseHandler {
	@Override
	public boolean canUse(@Nonnull ItemStack item, EntityLivingBase entity, EnumHand hand) {
		return entity instanceof EntityPlayer && isUsable(item);
	}

	@Nonnull
	@Override
	public ItemStack onTryUse(@Nonnull ItemStack item, World world, EntityLivingBase entity, EnumHand hand) {
		if (canUse(item, entity, hand))
			entity.setActiveHand(hand);
		return item;
	}

	@Override
	public int getMaxUseDuration(@Nonnull ItemStack item) {
		return 32;
	}

	@Override
	public boolean isUsable(@Nonnull ItemStack item) {
		return item.getCount() == 1 && isDrinkableLiquid(getFluidName(item));
	}

	@Override
	public EnumAction useAction(@Nonnull ItemStack item) {
		return isUsable(item) ? EnumAction.DRINK : EnumAction.NONE;
	}

	@Nonnull
	@Override
	public ItemStack onUse(@Nonnull ItemStack item, EntityLivingBase entity, EnumHand hand) {

		String liquid = getFluidName(item);
		@Nonnull ItemStack r = item;
		if (item.getCount() == 1 && liquid != null &&
				entity instanceof EntityPlayer && isDrinkableLiquid(liquid)) {
			EntityPlayer player = (EntityPlayer)entity;
			FluidStack stack;
			if (!player.capabilities.isCreativeMode) {
				@Nonnull ItemStack drop = item.splitStack(1);
				IFluidHandler fluidHandler = drop.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
				stack = fluidHandler.drain(Fluid.BUCKET_VOLUME, true);
				if (item.getCount() < 1)
					item = drop;
				else if (!drop.isEmpty() && !player.inventory.addItemStackToInventory(drop))
					player.dropItem(drop, false, true);
			} else {
				IFluidHandler fluidHandler = item.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
				stack = fluidHandler.drain(Fluid.BUCKET_VOLUME, false);
			}
			MFRRegistry.getLiquidDrinkHandlers().get(liquid).onDrink(player, stack);
		}
		if (item.isEmpty())
		{
			item = r;
			item.setCount(0);
		}
		return item;
	}

	public String getFluidName(@Nonnull ItemStack item) {

		if (item.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)) {
			IFluidHandler fluidHandler = item.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
			if (fluidHandler.getTankProperties().length > 0) {
				FluidStack liquid = fluidHandler.getTankProperties()[0].getContents();
				if (liquid != null && liquid.amount >= Fluid.BUCKET_VOLUME)
					return liquid.getFluid().getName();
			}
		}
		return null;
	}

	public boolean isDrinkableLiquid(String name) {
		return name != null && MFRRegistry.getLiquidDrinkHandlers().containsKey(name);
	}
}
