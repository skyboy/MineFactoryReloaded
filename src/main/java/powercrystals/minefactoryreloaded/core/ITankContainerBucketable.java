package powercrystals.minefactoryreloaded.core;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Emy
 *
 * Extends the IFluidHandler interface to allow manual draining/filling via buckets.
 *
 * what am I even doing here
 */
public interface ITankContainerBucketable
{
	/**
	 * Called to determine if the IFluidHandler should be filled by buckets.
	 * @return True if the IFluidHandler is allowed to be filled manually (with buckets)
	 */
	boolean allowBucketFill(EnumFacing facing, @Nonnull ItemStack stack);

	/**
	 * Called to determine if the IFluidHandler should be drained by buckets.
	 * @return True if the IFluidHandler is allowed to be drained manually (with buckets)
	 */
	boolean allowBucketDrain(EnumFacing facing, @Nonnull ItemStack stack);

	/**
	 * Returns an array of objects which represent the internal tanks.
	 * These objects cannot be used to manipulate the internal tanks.
	 *
	 * @return Properties for the relevant internal tanks.
	 */
	IFluidTankProperties[] getTankProperties(EnumFacing facing);

	/**
	 * Fills fluid into internal tanks, distribution is left entirely to the IFluidHandler.
	 *
	 * @param resource FluidStack representing the Fluid and maximum amount of fluid to be filled.
	 * @param doFill   If false, fill will only be simulated.
	 * @return Amount of resource that was (or would have been, if simulated) filled.
	 */
	int fill(EnumFacing facing, FluidStack resource, boolean doFill);

	/**
	 * Drains fluid out of internal tanks, distribution is left entirely to the IFluidHandler.
	 *
	 * @param resource FluidStack representing the Fluid and maximum amount of fluid to be drained.
	 * @param doDrain  If false, drain will only be simulated.
	 * @return FluidStack representing the Fluid and amount that was (or would have been, if
	 * simulated) drained.
	 */
	@Nullable
	FluidStack drain(EnumFacing facing, FluidStack resource, boolean doDrain);

	/**
	 * Drains fluid out of internal tanks, distribution is left entirely to the IFluidHandler.
	 * <p/>
	 * This method is not Fluid-sensitive.
	 *
	 * @param maxDrain Maximum amount of fluid to drain.
	 * @param doDrain  If false, drain will only be simulated.
	 * @return FluidStack representing the Fluid and amount that was (or would have been, if
	 * simulated) drained.
	 */
	@Nullable
	FluidStack drain(EnumFacing facing, int maxDrain, boolean doDrain);

	class FluidHandlerWrapper implements IFluidHandler {

		private final ITankContainerBucketable itcb;
		private final EnumFacing side;

		public FluidHandlerWrapper(ITankContainerBucketable itcb, EnumFacing side) {

			this.itcb = itcb;
			this.side = side;
		}

		@Override
		public IFluidTankProperties[] getTankProperties() {

			return itcb.getTankProperties(side);
		}

		@Override
		public int fill(FluidStack resource, boolean doFill) {

			return itcb.fill(side, resource, doFill);
		}

		@Nullable
		@Override
		public FluidStack drain(FluidStack resource, boolean doDrain) {

			return itcb.drain(side, resource, doDrain);
		}

		@Nullable
		@Override
		public FluidStack drain(int maxDrain, boolean doDrain) {

			return itcb.drain(side, maxDrain, doDrain);
		}
	}

}
