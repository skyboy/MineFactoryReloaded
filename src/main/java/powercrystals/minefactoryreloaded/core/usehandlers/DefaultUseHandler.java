package powercrystals.minefactoryreloaded.core.usehandlers;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import powercrystals.minefactoryreloaded.core.IAdvFluidContainerItem;
import powercrystals.minefactoryreloaded.core.IUseHandler;
import powercrystals.minefactoryreloaded.core.IUsable;
import powercrystals.minefactoryreloaded.core.MFRLiquidMover;

import javax.annotation.Nonnull;

public class DefaultUseHandler implements IUseHandler {

	@Override
	public boolean canUse(@Nonnull ItemStack bucket, EntityLivingBase entity, EnumHand hand) {

		IAdvFluidContainerItem fluidHandler = (IAdvFluidContainerItem) bucket
				.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);

		FluidStack liquid =
				fluidHandler.getTankProperties().length > 0 ? fluidHandler.getTankProperties()[0].getContents() : null;
		if (liquid == null || liquid.amount <= 0)
			return fluidHandler.canBeFilledFromWorld();
		return fluidHandler.canPlaceInWorld();
	}

	@Nonnull
	@Override
	public ItemStack onTryUse(@Nonnull ItemStack bucket, World world, EntityLivingBase entity, EnumHand hand) {

		EntityPlayer player = entity instanceof EntityPlayer ? (EntityPlayer) entity : null;
		if (world.isRemote)
			return bucket;
		Item item = bucket.getItem();

		IAdvFluidContainerItem fluidHandler = (IAdvFluidContainerItem) bucket
				.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);

		IFluidTankProperties[] tankProperties = fluidHandler.getTankProperties();
		FluidStack liquid =	tankProperties.length > 0 ? tankProperties[0].getContents() : null;

		if (liquid == null || liquid.amount <= 0 || liquid.amount + Fluid.BUCKET_VOLUME <= tankProperties[0].getCapacity()) {
			if (!fluidHandler.canBeFilledFromWorld())
				return bucket;
			RayTraceResult objectPosition = ((IUsable) item).rayTrace(world, entity, true);
			if (objectPosition != null && objectPosition.typeOfHit == Type.BLOCK) {
				BlockPos pos = objectPosition.getBlockPos();
				if (canEntityAct(world, entity, pos, objectPosition.sideHit, bucket, false)) {
					FluidActionResult result = FluidUtil
							.tryPickUpFluid(bucket, player, world, objectPosition.getBlockPos(), objectPosition.sideHit);
					if (result.isSuccess()) {
						if (!fluidHandler.shouldReplaceWhenFilled() || bucket.getCount() > 1) {
							MFRLiquidMover.disposePlayerItem(bucket, result.getResult(), player, true, fluidHandler.shouldReplaceWhenFilled());
						} else {
							return result.getResult();
						}
					}
				}
			}
		}

		if (liquid != null && liquid.amount >= Fluid.BUCKET_VOLUME && fluidHandler.canPlaceInWorld()) {
			RayTraceResult objectPosition = ((IUsable) item).rayTrace(world, entity, false);
			if (objectPosition != null && objectPosition.typeOfHit == Type.BLOCK) {
				BlockPos pos = objectPosition.getBlockPos();
				if (canEntityAct(world, entity, pos, objectPosition.sideHit, bucket, true)) {
					FluidActionResult result = FluidUtil
							.tryPlaceFluid(player, world, objectPosition.getBlockPos().offset(objectPosition.sideHit), bucket, liquid);
					if (result.isSuccess()) {
						@Nonnull ItemStack drop = result.getResult();
						bucket.shrink(1);
						if (item.hasContainerItem(drop)) {
							drop = item.getContainerItem(drop);
							if (!drop.isEmpty() && drop.isItemStackDamageable() && drop.getItemDamage() > drop.getMaxDamage())
								drop = ItemStack.EMPTY;
						}
						return drop;
					}
				}
			}
		}
		return bucket;
	}

	private boolean canEntityAct(World world, EntityLivingBase entity, BlockPos pos, EnumFacing side,
			@Nonnull ItemStack item, boolean isPlace) {

		EntityPlayer player = (entity instanceof EntityPlayer) ? (EntityPlayer) entity : null;
		return (player == null || (world.isBlockModifiable(player, pos) &&
				player.canPlayerEdit(pos, side, item)));
	}

	@Override
	public int getMaxUseDuration(@Nonnull ItemStack item) {

		return 0;
	}

	@Override
	public boolean isUsable(@Nonnull ItemStack item) {

		return false;
	}

	@Override
	public EnumAction useAction(@Nonnull ItemStack item) {

		return EnumAction.NONE;
	}

	@Nonnull
	@Override
	public ItemStack onUse(@Nonnull ItemStack item, EntityLivingBase entity, EnumHand hand) {

		return item;
	}
}
