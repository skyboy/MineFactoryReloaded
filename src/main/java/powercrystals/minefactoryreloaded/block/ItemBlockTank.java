package powercrystals.minefactoryreloaded.block;

import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import powercrystals.minefactoryreloaded.core.MFRUtil;
import powercrystals.minefactoryreloaded.tile.tank.TileEntityTank;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import static cofh.core.util.helpers.StringHelper.*;
import static powercrystals.minefactoryreloaded.core.MFRUtil.isShiftKeyDown;

public class ItemBlockTank extends ItemBlockFactory {

	public ItemBlockTank(Block p_i45328_1_) {

		super(p_i45328_1_);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag tooltipFlag) {

		if (tooltipFlag.isAdvanced() || isShiftKeyDown()) {
			IFluidTankProperties[] tankProps = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null)
					.getTankProperties();
			FluidStack fluid = tankProps[0].getContents();
			tooltip.add(localize("info.cofh.fluid") + ": " + getFluidName(fluid, MFRUtil.empty()) + END);
			int amt = (fluid == null ? 0 : fluid.amount);
			tooltip.add(localize("info.cofh.amount") + ": " + amt + " / " + tankProps[0].getCapacity() + "mB" + END);
		} else {
			tooltip.add(shiftForDetails());
		}
		super.addInformation(stack, world, tooltip, tooltipFlag);
	}

	@Override
	public ICapabilityProvider initCapabilities(@Nonnull ItemStack stack, NBTTagCompound nbt) {

		return new ItemTankFluidHandler(stack);
	}

	private class ItemTankFluidHandler implements ICapabilityProvider, IFluidHandler {

		@Nonnull
		private ItemStack stack;

		public ItemTankFluidHandler(@Nonnull ItemStack stack) {

			this.stack = stack;
		}

		@Override
		public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {

			return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;
		}

		@Override
		public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {

			if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
				return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(this);
			}
			return null;
		}

		@Override
		public IFluidTankProperties[] getTankProperties() {

			FluidStack fluid = null;
			if (stack.getTagCompound() != null && stack.getTagCompound().hasKey("tank"))
				fluid = FluidStack.loadFluidStackFromNBT(stack.getTagCompound().getCompoundTag("tank"));

			return new IFluidTankProperties[] { new FluidTankProperties(fluid, TileEntityTank.CAPACITY) };
		}

		@Override
		public int fill(FluidStack resource, boolean doFill) {

			if (resource == null || stack.getCount() != 1)
				//|| resource.getFluid().getTemperature(resource) > MELTING_POINT)
				return 0;
			int fillAmount = 0, capacity = TileEntityTank.CAPACITY;
			NBTTagCompound tag = stack.getTagCompound(), fluidTag = null;
			FluidStack fluid = null;
			if (tag == null || !tag.hasKey("tank") ||
					(fluidTag = tag.getCompoundTag("tank")).hasNoTags() ||
					(fluid = FluidStack.loadFluidStackFromNBT(fluidTag)) == null)
				fillAmount = Math.min(capacity, resource.amount);
			if (fluid == null) {
				if (doFill) {
					fluid = resource.copy();
					fluid.amount = 0;
				}
			} else if (!fluid.isFluidEqual(resource))
				return 0;
			else
				fillAmount = Math.min(capacity - fluid.amount, resource.amount);
			fillAmount = Math.max(fillAmount, 0);
			if (doFill) {
				if (tag == null) {
					stack.setTagCompound(new NBTTagCompound());
					tag = stack.getTagCompound();
				}
				fluid.amount += fillAmount;
				tag.setTag("tank", fluid.writeToNBT(fluidTag == null ? new NBTTagCompound() : fluidTag));
			}
			return fillAmount;
		}

		@Nullable
		@Override
		public FluidStack drain(FluidStack resource, boolean doDrain) {

			NBTTagCompound tag = stack.getTagCompound(), fluidTag = null;
			FluidStack fluid = null;
			if (tag == null || !tag.hasKey("tank") ||
					(fluidTag = tag.getCompoundTag("tank")).hasNoTags() ||
					(fluid = FluidStack.loadFluidStackFromNBT(fluidTag)) == null) {
				if (fluidTag != null)
					tag.removeTag("tank");
				return null;
			}

			if (!fluid.getFluid().equals(resource.getFluid()))
				return null;

			return drain(resource.amount, doDrain, tag, fluid);
		}

		@Nullable
		@Override
		public FluidStack drain(int maxDrain, boolean doDrain) {

			NBTTagCompound tag = stack.getTagCompound(), fluidTag = null;
			FluidStack fluid = null;
			if (tag == null || !tag.hasKey("tank") ||
					(fluidTag = tag.getCompoundTag("tank")).hasNoTags() ||
					(fluid = FluidStack.loadFluidStackFromNBT(fluidTag)) == null) {
				if (fluidTag != null)
					tag.removeTag("tank");
				return null;
			}

			return drain(maxDrain, doDrain, tag, fluid);
		}

		private FluidStack drain(int maxDrain, boolean doDrain, NBTTagCompound tag, FluidStack fluid) {

			int drainAmount = Math.min(maxDrain, fluid.amount);
			if (doDrain) {
				tag.removeTag("tank");
				fluid.amount -= drainAmount;
				if (fluid.amount > 0)
					fill(fluid, true);
			}
			fluid.amount = drainAmount;
			return fluid;
		}
	}
}
