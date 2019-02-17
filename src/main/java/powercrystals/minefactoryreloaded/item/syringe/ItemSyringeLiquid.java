package powercrystals.minefactoryreloaded.item.syringe;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.*;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.api.handler.ILiquidDrinkHandler;
import powercrystals.minefactoryreloaded.render.ModelHelper;
import powercrystals.minefactoryreloaded.render.model.MFRModelLoader;
import powercrystals.minefactoryreloaded.render.model.SyringeModel;
import powercrystals.minefactoryreloaded.setup.MFRThings;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ItemSyringeLiquid extends ItemSyringe
{
	private boolean _prefix = false;

	public ItemSyringeLiquid() {

		setUnlocalizedName("mfr.syringe.empty");
	}

	@Override
	public String getUnlocalizedName(@Nonnull ItemStack stack)
	{
		if (getFluidHandler(stack).getTankProperties()[0].getContents() != null)
			return getUnlocalizedName() + (_prefix ? ".prefix" : ".suffix");
		return getUnlocalizedName();
	}

	public String getLocalizedName(String str)
	{
		String name = getUnlocalizedName() + "." + str;
		if (I18n.canTranslate(name))
			return I18n.translateToLocal(name);
		return null;
	}
	@Override
	public int getItemStackLimit(@Nonnull ItemStack stack) {
		NBTTagCompound tag = stack.getTagCompound();
		if (tag != null && tag.hasKey("fluidName"))
			return 1;
		return maxStackSize;
	}

	@Override
	public String getItemStackDisplayName(@Nonnull ItemStack item)
	{
		String ret = getFluidName(item), t = getLocalizedName(ret);
		if (t != null && !t.isEmpty())
			return TextFormatting.RESET + t + TextFormatting.RESET;
		if (ret == null)
		{
			return super.getItemStackDisplayName(item);
		}
		FluidStack liquid = getFluidHandler(item).getTankProperties()[0].getContents();
		if (liquid != null)
		{
			ret = liquid.getFluid().getLocalizedName(liquid);
		}
		_prefix = true;
		t = super.getItemStackDisplayName(item);
		_prefix = false;
		ret = (t.isEmpty() ? "" : t + " ") + ret;
		t = super.getItemStackDisplayName(item);
		ret += t.isEmpty() ? " Syringe" : " " + t;
		return ret;
	}

	// TODO: subItems to provide a syringe for all fluids via creative under a config

	public String getFluidName(@Nonnull ItemStack container)
	{
		NBTTagCompound tag = container.getTagCompound();
		return tag == null || !tag.hasKey("fluidName") ? null :
							tag.getString("fluidName");
	}

	@Override
	public boolean canInject(World world, EntityLivingBase entity, @Nonnull ItemStack syringe)
	{
		IFluidTankProperties[] tankProps = getFluidHandler(syringe).getTankProperties();
		FluidStack fluid = tankProps[0].getContents();
		return fluid != null && fluid.amount >= tankProps[0].getCapacity();
	}

	@Override
	public boolean inject(World world, EntityLivingBase entity, @Nonnull ItemStack syringe)
	{
		ILiquidDrinkHandler handler = MFRRegistry.getLiquidDrinkHandlers().
				get(getFluidName(syringe));
		if (handler != null)
		{
			FluidStack stack = getFluidHandler(syringe).drain(Integer.MAX_VALUE, true);
			handler.onDrink(entity, stack);
			return true;
		}
		return false;
	}

	private IFluidHandler getFluidHandler(@Nonnull ItemStack syringe) {
		return syringe.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
	}

	@Override
	public boolean isEmpty(@Nonnull ItemStack syringe)
	{
		return getFluidName(syringe) == null;
	}

	@Nonnull
	@Override
	public ItemStack getEmptySyringe(@Nonnull ItemStack syringe)
	{
		return new ItemStack(MFRThings.syringeEmptyItem);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels() {

		ModelHelper.registerModel(this, "syringe", "variant=empty");
		MFRModelLoader.registerModel(SyringeModel.MODEL_LOCATION, SyringeModel.MODEL);
	}

	@Override
	public ICapabilityProvider initCapabilities(@Nonnull ItemStack stack, NBTTagCompound nbt) {

		return new SyringeFluidHandler(stack);
	}

	private class SyringeFluidHandler implements ICapabilityProvider, IFluidHandlerItem {

		@Nonnull
		private ItemStack stack;
		public SyringeFluidHandler(@Nonnull ItemStack stack) {

			this.stack = stack;
		}

		@Override
		public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {

			return capability == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY;
		}

		@Override
		public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {

			if (capability == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY)
			{
				return CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY.cast(this);
			}
			return null;
		}

		@Override
		public IFluidTankProperties[] getTankProperties() {

			NBTTagCompound tag = stack.getTagCompound();
			FluidStack contents = null;

			if (tag != null && tag.hasKey("fluid")) {
				contents = FluidStack.loadFluidStackFromNBT(tag.getCompoundTag("fluid"));

				if (contents == null) {
					tag.removeTag("fluid");
				}
			}

			return new IFluidTankProperties[]{new FluidTankProperties(contents, Fluid.BUCKET_VOLUME)};
		}

		@Override
		public int fill(FluidStack resource, boolean doFill) {

			if (resource == null)
				//|| resource.getFluid().getTemperature(resource) > MELTING_POINT)
				return 0;
			int fillAmount = 0, capacity = getFluidHandler(stack).getTankProperties()[0].getCapacity();
			NBTTagCompound tag = stack.getTagCompound(), fluidTag = null;
			FluidStack fluid = null;
			if (tag == null || !tag.hasKey("fluid") ||
					(fluidTag = tag.getCompoundTag("fluid")).hasNoTags() ||
					(fluid = FluidStack.loadFluidStackFromNBT(fluidTag)) == null)
				fillAmount = Math.min(capacity, resource.amount);
			if (fluid == null)
			{
				if (doFill)
				{
					fluid = resource.copy();
					fluid.amount = 0;
				}
			}
			else if (!fluid.isFluidEqual(resource))
				return 0;
			else
				fillAmount = Math.min(capacity - fluid.amount, resource.amount);
			fillAmount = Math.max(fillAmount, 0);
			if (doFill)
			{
				if (tag == null) {
					stack.setTagCompound(new NBTTagCompound());
					tag = stack.getTagCompound();
				}
				fluid.amount += fillAmount;
				tag.setTag("fluid", fluid.writeToNBT(fluidTag == null ? new NBTTagCompound() : fluidTag));
				tag.setString("fluidName", fluid.getFluid().getName());
			}
			return fillAmount;
		}

		@Nullable
		@Override
		public FluidStack drain(FluidStack resource, boolean doDrain) {

			NBTTagCompound tag = stack.getTagCompound(), fluidTag;
			FluidStack fluid;
			if (tag == null || !tag.hasKey("fluid") ||
					(fluidTag = tag.getCompoundTag("fluid")).hasNoTags() ||
					(fluid = FluidStack.loadFluidStackFromNBT(fluidTag)) == null ||
					!(fluid.getFluid().equals(resource.getFluid())))
				return null;

			return drain(resource.amount, doDrain, tag, fluid);
		}

		@Nullable
		@Override
		public FluidStack drain(int maxDrain, boolean doDrain) {

			NBTTagCompound tag = stack.getTagCompound(), fluidTag;
			FluidStack fluid;
			if (tag == null || !tag.hasKey("fluid") ||
					(fluidTag = tag.getCompoundTag("fluid")).hasNoTags() ||
					(fluid = FluidStack.loadFluidStackFromNBT(fluidTag)) == null)
				return null;

			return drain(maxDrain, doDrain, tag, fluid);
		}

		private FluidStack drain(int maxDrain, boolean doDrain, NBTTagCompound tag, FluidStack fluid) {
			int drainAmount = Math.min(maxDrain, fluid.amount);
			if (doDrain)
			{
				tag.removeTag("fluid");
				tag.removeTag("uniqifier");
				tag.removeTag("fluidName");
				if (tag.hasNoTags())
					stack.setTagCompound(null);
				fluid.amount -= drainAmount;
				if (fluid.amount > 0)
					fill(fluid, true);
			}
			fluid.amount = drainAmount;
			return fluid;
		}

		@Nonnull
		@Override
		public ItemStack getContainer() {

			return stack;
		}
	}
}
