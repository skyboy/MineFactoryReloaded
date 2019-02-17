package powercrystals.minefactoryreloaded.item;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import powercrystals.minefactoryreloaded.MFRProps;
import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.core.IAdvFluidContainerItem;
import powercrystals.minefactoryreloaded.core.IUseHandler;
import powercrystals.minefactoryreloaded.core.IUsable;
import powercrystals.minefactoryreloaded.core.usehandlers.DefaultUseHandler;
import powercrystals.minefactoryreloaded.core.usehandlers.DrinkUseHandler;
import powercrystals.minefactoryreloaded.item.base.ItemFactory;
import powercrystals.minefactoryreloaded.render.ModelHelper;
import powercrystals.minefactoryreloaded.render.model.MFRModelLoader;
import powercrystals.minefactoryreloaded.render.model.PlasticCupModel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;

public class ItemFactoryCup extends ItemFactory implements IUsable {

	public final static int MELTING_POINT = 523; // melting point of Polyethylene terphthalate
	public final static IUseHandler defaultUseAction = new DefaultUseHandler();
	public final static IUseHandler drinkUseAction = new DrinkUseHandler();

	private boolean _prefix = false;
	protected List<IUseHandler> useHandlers;

	public ItemFactoryCup(int stackSize, int maxUses) {
		this.setMaxStackSize(stackSize);
		this.setMaxDamage(maxUses);
		this.setHasSubtypes(true);
		useHandlers = new LinkedList<>();
		useHandlers.add(defaultUseAction);
		useHandlers.add(drinkUseAction);
		setUnlocalizedName("mfr.plastic.cup");
	}

	@Override
	public int getItemStackLimit(@Nonnull ItemStack stack) {
		NBTTagCompound tag = stack.getTagCompound();
		if (tag != null && tag.hasKey("fluid"))
			return 1;
		return maxStackSize;
	}

	@Override
	public String getUnlocalizedName(@Nonnull ItemStack stack) {
		if (getFluid(stack) != null)
			return getUnlocalizedName() + (_prefix ? ".prefix" : ".suffix");
		return getUnlocalizedName();
	}

	private FluidStack getFluid(@Nonnull ItemStack stack) {

		IFluidTankProperties[] tankProps = getFluidHandler(stack).getTankProperties();
		if (tankProps.length > 0) {
			return tankProps[0].getContents();
		}

		return null;
	}

	public String getLocalizedName(String str) {
		String name = getUnlocalizedName() + "." + str;
		if (I18n.canTranslate(name))
			return I18n.translateToLocal(name);
		return null;
	}

	@Override
	public String getItemStackDisplayName(@Nonnull ItemStack item) {
		String ret = getFluidName(item), t = getLocalizedName(ret);
		if (t != null && !t.isEmpty())
			return TextFormatting.RESET + t + TextFormatting.RESET;
		if (ret == null) {
			return super.getItemStackDisplayName(item);
		}
		FluidStack liquid = getFluid(item);
		if (liquid != null) {
			ret = liquid.getFluid().getLocalizedName(liquid);
		}
		_prefix = true;
		t = super.getItemStackDisplayName(item);
		_prefix = false;
		ret = (t.isEmpty() ? "" : t + " ") + ret;
		t = super.getItemStackDisplayName(item);
		ret += t.isEmpty() ? " Cup" : " " + t;
		return ret;
	}

	public String getFluidName(@Nonnull ItemStack stack) {
		NBTTagCompound tag = stack.getTagCompound();
		return tag == null || !tag.hasKey("fluid") ? null : tag.getCompoundTag("fluid").getString("FluidName");
	}

	@Override
	public Item getContainerItem() {
		return this;
	}

	@Nonnull
	@Override
	public ItemStack getContainerItem(@Nonnull ItemStack stack) {
		if (stack.getCount() <= 0)
			return ItemStack.EMPTY;
		@Nonnull ItemStack r = stack.copy();
		NBTTagCompound tag = r.getTagCompound();
		if (tag != null) {
			if (tag.hasKey("drained")) {
				r.setCount(1);
				r.attemptDamageItem(1, itemRand, null);
			}
			tag.removeTag("drained");
			tag.removeTag("fluid");
			tag.removeTag("toDrain");
			if (tag.hasNoTags())
				r.setTagCompound(null);
		}
		return r;
	}

	@Override
	public boolean hasContainerItem(@Nonnull ItemStack stack) {
		NBTTagCompound tag = stack.getTagCompound();
		return tag != null && (tag.hasKey("fluid") || tag.hasKey("drained"));
	}

	public boolean hasDrinkableLiquid(@Nonnull ItemStack stack) {

		IFluidTankProperties[] tankProps = getFluidHandler(stack).getTankProperties();
		return stack.getCount() == 1 &&
				MFRRegistry.getLiquidDrinkHandlers().containsKey(getFluidName(stack)) &&
				getFluid(stack).amount == tankProps[0].getCapacity();
	}

	@Nonnull
	@Override
	public ItemStack onItemUseFinish(@Nonnull ItemStack stack, World worldIn, EntityLivingBase entity) {
		for (IUseHandler handler : useHandlers)
			if (handler.isUsable(stack))
				return handler.onUse(stack, entity, entity.getActiveHand());
		return stack;
	}

	@Override
	public EnumAction getItemUseAction(@Nonnull ItemStack item) {
		for (IUseHandler handler : useHandlers)
			if (handler.isUsable(item))
				return handler.useAction(item);
		return EnumAction.NONE;
	}

	@Override
	public int getMaxItemUseDuration(@Nonnull ItemStack item) {
		for (IUseHandler handler : useHandlers)
			if (handler.isUsable(item))
				return handler.getMaxUseDuration(item);
		return 0;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {

		@Nonnull ItemStack item = player.getHeldItem(hand);
		for (IUseHandler handler : useHandlers)
			if (handler.canUse(item, player, hand))
				return new ActionResult<>(EnumActionResult.SUCCESS, handler.onTryUse(item, world, player, hand));
		return new ActionResult<>(EnumActionResult.PASS, item);
	}

	@Override
	public boolean isValidArmor(@Nonnull ItemStack stack, EntityEquipmentSlot armorType, Entity entity) {
		if (armorType == EntityEquipmentSlot.HEAD)
			if (entity instanceof EntityPlayer &&
					entity.getName().equalsIgnoreCase("Eyamaz"))
				return true;

		return false;
	}

	@Override
	public String getArmorTexture(@Nonnull ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
		return MFRProps.TEXTURE_FOLDER + "armor/plastic_layer_1.png";
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ModelBiped getArmorModel(EntityLivingBase entityLiving, @Nonnull ItemStack itemStack, EntityEquipmentSlot armorSlot, ModelBiped _default) {
		if (armorSlot == EntityEquipmentSlot.HEAD) {
			return null; // TODO
		}
		return null;
	}

	@Override
	public RayTraceResult rayTrace(World world, EntityLivingBase entity, boolean useLiquids) {

		float f = entity.rotationPitch;
		float f1 = entity.rotationYaw;
		double d0 = entity.posX;
		double d1 = entity.posY + (double)entity.getEyeHeight();
		double d2 = entity.posZ;
		Vec3d vec3d = new Vec3d(d0, d1, d2);
		float f2 = MathHelper.cos(-f1 * 0.017453292F - (float)Math.PI);
		float f3 = MathHelper.sin(-f1 * 0.017453292F - (float)Math.PI);
		float f4 = -MathHelper.cos(-f * 0.017453292F);
		float f5 = MathHelper.sin(-f * 0.017453292F);
		float f6 = f3 * f4;
		float f7 = f2 * f4;
		double d3 = 5.0D;
		if (entity instanceof net.minecraft.entity.player.EntityPlayerMP)
		{
			d3 = ((net.minecraft.entity.player.EntityPlayerMP)entity).interactionManager.getBlockReachDistance();
		}
		Vec3d vec3d1 = vec3d.addVector((double)f6 * d3, (double)f5 * d3, (double)f7 * d3);
		return world.rayTraceBlocks(vec3d, vec3d1, useLiquids, !useLiquids, false);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels() {

		ModelHelper.registerModel(this, "plastic_cup");
		MFRModelLoader.registerModel(PlasticCupModel.MODEL_LOCATION, PlasticCupModel.MODEL);
	}

	private IAdvFluidContainerItem getFluidHandler(@Nonnull ItemStack stack) {

		return (IAdvFluidContainerItem) stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
	}

	@Override
	public ICapabilityProvider initCapabilities(@Nonnull ItemStack stack, NBTTagCompound nbt) {

		return new FactoryCupFluidHandler(stack);
	}

	private class FactoryCupFluidHandler implements ICapabilityProvider, IAdvFluidContainerItem {

		@Nonnull
		private ItemStack stack;

		public FactoryCupFluidHandler(@Nonnull ItemStack stack) {
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

			if (resource == null || stack.getCount() != 1)
				//|| resource.getFluid().getTemperature(resource) > MELTING_POINT)
				return 0;
			int fillAmount = 0, capacity = getTankProperties()[0].getCapacity();
			NBTTagCompound tag = stack.getTagCompound(), fluidTag = null;
			FluidStack fluid = null;
			if (tag == null || !tag.hasKey("fluid") ||
					(fluidTag = tag.getCompoundTag("fluid")).hasNoTags() ||
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
				tag.setTag("fluid", fluid.writeToNBT(fluidTag == null ? new NBTTagCompound() : fluidTag));
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
			if (doDrain) {
				tag.removeTag("fluid");
				tag.setBoolean("drained", true);
				fluid.amount -= drainAmount;
				if (fluid.amount > 0)
					fill(fluid, true);
				if (tag.hasKey("toDrain")) {
					drainAmount = tag.getInteger("toDrain");
					tag.removeTag("toDrain");
				} else
					drainAmount *= (Math.max(Math.random() - 0.75, 0) + 0.75);
			} else {
				drainAmount *= (Math.max(Math.random() - 0.75, 0) + 0.75);
				tag.setInteger("toDrain", drainAmount);
			}
			fluid.amount = drainAmount;
			return fluid;
		}

		@Override
		public boolean canBeFilledFromWorld() {

			return true;
		}

		@Override
		public boolean canPlaceInWorld() {

			return false;
		}

		@Override
		public boolean shouldReplaceWhenFilled() {

			return true;
		}

		@Nonnull
		@Override
		public ItemStack getContainer() {

			return stack;
		}
	}
}
