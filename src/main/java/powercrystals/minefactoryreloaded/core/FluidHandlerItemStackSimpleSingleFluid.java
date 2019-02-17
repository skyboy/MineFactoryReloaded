package powercrystals.minefactoryreloaded.core;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStackSimple;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FluidHandlerItemStackSimpleSingleFluid extends FluidHandlerItemStackSimple.SwapEmpty {

	private final @Nonnull ItemStack fullContainer;
	private Fluid fluid;

	public FluidHandlerItemStackSimpleSingleFluid(@Nonnull ItemStack container, @Nonnull ItemStack fullContainer, @Nonnull ItemStack emptyContainer, Fluid fluid, int capacity) {
		super(container, emptyContainer, capacity);
		this.fullContainer = fullContainer;
		this.fluid = fluid;
	}

	@Nullable
	@Override
	public FluidStack getFluid() {

		return container.getItem() == fullContainer.getItem() ? new FluidStack(fluid, capacity) : null;
	}

	@Override
	public boolean canFillFluidType(FluidStack fluidStack) {

		return fluidStack.getFluid().equals(fluid);
	}

	@Override
	public boolean canDrainFluidType(FluidStack fluidStack) {

		return fluidStack.getFluid().equals(fluid);
	}

	@Override
	protected void setContainerToEmpty() {

		container.deserializeNBT(emptyContainer.serializeNBT());
	}

	private void setContainerToFull() {

		container.deserializeNBT(fullContainer.serializeNBT());
	}

	@Override
	public int fill(FluidStack resource, boolean doFill) {

		if (container.getCount() != 1 || resource == null || resource.amount <= 0 || !canFillFluidType(resource))
		{
			return 0;
		}

		FluidStack contained = getFluid();
		if (contained == null)
		{
			int fillAmount = Math.min(capacity, resource.amount);
			if (fillAmount == capacity) {
				if (doFill) {
					setContainerToFull();
				}

				return fillAmount;
			}
		}

		return 0;
	}
}
