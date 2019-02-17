package powercrystals.minefactoryreloaded.tile.base;

import cofh.api.item.IAugmentItem;
import cofh.core.fluid.FluidTankCore;
import cofh.core.util.helpers.FluidHelper;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import powercrystals.minefactoryreloaded.core.ITankContainerBucketable;
import powercrystals.minefactoryreloaded.core.UtilInventory;
import powercrystals.minefactoryreloaded.item.ItemUpgrade;
import powercrystals.minefactoryreloaded.setup.Machine;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public abstract class TileEntityFactoryInventory extends TileEntityFactory implements ISidedInventory, ITankContainerBucketable {

	private final static FluidTankCore[] emptyIFluidTank = new FluidTankCore[] { };
	private final static FluidTankInfo[] emptyFluidTankInfo = FluidHelper.NULL_TANK_INFO;
	public final static IFluidTankProperties[] emptyIFluidTankProperties = new IFluidTankProperties[] { };
	protected final static int BUCKET_VOLUME = Fluid.BUCKET_VOLUME;

	protected NonNullList<ItemStack> failedDrops = null;
	protected FluidTankCore[] _tanks;

	@Nonnull
	protected NonNullList<ItemStack> _inventory;

	protected boolean internalChange = false;

	private boolean isActive = false;

	protected TileEntityFactoryInventory(Machine machine) {

		super(machine);
		_inventory = NonNullList.withSize(getSizeInventory(), ItemStack.EMPTY);
		_tanks = createTanks();
		setManageFluids(_tanks != null);
	}

	@Override
	public String getName() {

		return _invName != null ? _invName : I18n.
				translateToLocal(_machine.getInternalName() + ".name");
	}

	@Override
	public boolean hasCustomName() {

		return _invName != null;
	}

	public void onDisassembled() {

		if (failedDrops != null)
			inv: while (failedDrops.size() > 0) {
				@Nonnull ItemStack itemstack = failedDrops.remove(0);
				if (itemstack.isEmpty()) {
					continue;
				}
				float xOffset = world.rand.nextFloat() * 0.8F + 0.1F;
				float yOffset = world.rand.nextFloat() * 0.8F + 0.1F;
				float zOffset = world.rand.nextFloat() * 0.8F + 0.1F;
				do {
					if (itemstack.getCount() <= 0) {
						continue inv;
					}
					int amountToDrop = world.rand.nextInt(21) + 10;
					if (amountToDrop > itemstack.getCount()) {
						amountToDrop = itemstack.getCount();
					}
					itemstack.shrink(amountToDrop);
					EntityItem entityitem = new EntityItem(world,
							pos.getX() + xOffset, pos.getY() + yOffset, pos.getZ() + zOffset,
							new ItemStack(itemstack.getItem(), amountToDrop, itemstack.getItemDamage()));
					if (itemstack.getTagCompound() != null) {
						entityitem.getItem().setTagCompound(itemstack.getTagCompound());
					}
					float motionMultiplier = 0.05F;
					entityitem.motionX = (float) world.rand.nextGaussian() * motionMultiplier;
					entityitem.motionY = (float) world.rand.nextGaussian() * motionMultiplier + 0.2F;
					entityitem.motionZ = (float) world.rand.nextGaussian() * motionMultiplier;
					world.spawnEntity(entityitem);
				} while (true);
			}
	}

	public void onBlockBroken() {

		NBTTagCompound tag = new NBTTagCompound();
		writeItemNBT(tag);
		onDisassembled();
	}

	public FluidTankInfo[] getTankInfo() {

		IFluidTank[] tanks = getTanks();
		if (tanks.length == 0)
			return emptyFluidTankInfo;
		FluidTankInfo[] r = new FluidTankInfo[tanks.length];
		for (int i = tanks.length; i-- > 0;)
			r[i] = tanks[i].getInfo();
		return r;
	}

	@Nullable
	protected FluidTankCore[] createTanks() {

		return null;
	}

	public FluidTankCore[] getTanks() {

		if (_tanks != null)
			return _tanks;
		return emptyIFluidTank;
	}

	@Override
	public IFluidTankProperties[] getTankProperties(EnumFacing facing) {

		FluidTankCore[] tanks = getTanks();

		if (tanks.length == 0)
			return emptyIFluidTankProperties;

		IFluidTankProperties[] tankProps = new IFluidTankProperties[tanks.length];
		for(int i=0; i<tanks.length; i++) {
			tankProps[i] = new FluidTankProperties(tanks[i].getFluid(), tanks[i].getCapacity(),
					canFillTank(facing, i), canDrainTank(facing, i));
		}

		return tankProps;
	}

	public int drain(int maxDrain, boolean doDrain, FluidTankCore tank) {

		if (tank.getFluidAmount() > 0) {
			FluidStack drained = tank.drain(maxDrain, doDrain);
			if (drained != null) {
				if (doDrain) {
					internalChange = true;
					markDirty();
					internalChange = false;
				}
				return drained.amount;
			}
		}
		return 0;
	}

	@Nullable
	@Override
	public FluidStack drain(EnumFacing facing, FluidStack resource, boolean doDrain) {

		if (resource != null)
			for (FluidTankCore tank : getTanks())
				if (resource.isFluidEqual(tank.getFluid()))
					return tank.drain(resource.amount, doDrain);
		return null;
	}

	@Nullable
	@Override
	public FluidStack drain(EnumFacing facing, int maxDrain, boolean doDrain) {

		for (FluidTankCore tank : getTanks())
			if (tank.getFluidAmount() > 0)
				return tank.drain(maxDrain, doDrain);
		return null;
	}

	@Override
	public int fill(EnumFacing facing, FluidStack resource, boolean doFill) {

		if (resource != null)
			for (FluidTankCore tank : getTanks())
				if (FluidHelper.isFluidEqualOrNull(tank.getFluid(), resource))
					return tank.fill(resource, doFill);
		return 0;
	}

	protected boolean canFillTank(EnumFacing facing, int index) {

		return true;
	}

	protected boolean canDrainTank(EnumFacing facing, int index) {

		return true;
	}

	protected boolean shouldPumpLiquid() {

		return false;
	}

	protected boolean shouldPumpTank(IFluidTank tank) {

		return true;
	}

	@Override
	public boolean allowBucketFill(EnumFacing facing, @Nonnull ItemStack stack) {

		return false;
	}

	@Override
	public boolean allowBucketDrain(EnumFacing facing, @Nonnull ItemStack stack) {

		return false;
	}

	public boolean doDrop(@Nonnull ItemStack drop) {

		drop = UtilInventory.dropStack(this, drop, this.getDropDirections(), this.getDropDirection());
		if (!drop.isEmpty() && drop.getCount() > 0) {
			if (failedDrops == null) {
				failedDrops = NonNullList.create();
			}
			failedDrops.add(drop);
			markDirty();
		}
		return true;
	}

	public boolean doDrop(List<ItemStack> drops) {

		if (drops == null || drops.size() <= 0) {
			return true;
		}

		NonNullList<ItemStack> missed = NonNullList.create();

		for (int i = drops.size(); i-- > 0;) {
			@Nonnull ItemStack dropStack = drops.get(i);
			dropStack = UtilInventory.dropStack(this, dropStack, this.getDropDirections(), this.getDropDirection());
			if (!dropStack.isEmpty() && dropStack.getCount() > 0) {
				missed.add(dropStack);
			}
		}

		if (!missed.isEmpty()) {
			if (drops != failedDrops) {
				if (failedDrops == null) {
					failedDrops = NonNullList.create();
				}
				failedDrops.addAll(missed);
			}
			else {
				failedDrops.clear();
				failedDrops.addAll(missed);
			}
			markDirty();
			return false;
		}

		return true;
	}

	public boolean hasDrops() {

		return failedDrops != null;
	}

	public int getUpgradeSlot() {

		return -1;
	}

	protected boolean canUseUpgrade(@Nonnull ItemStack stack, IAugmentItem item) {

		return _areaManager != null && item instanceof ItemUpgrade && ((ItemUpgrade) item).getAugmentLevel(stack, "radius") != 0;
	}

	public boolean isUsableAugment(@Nonnull ItemStack stack) {

		if (stack.isEmpty() || !(stack.getItem() instanceof IAugmentItem))
			return false;
		return canUseUpgrade(stack, (IAugmentItem) stack.getItem());
	}

	public boolean acceptUpgrade(@Nonnull ItemStack stack) {

		int slot = getUpgradeSlot();
		if (slot < 0 || stack.isEmpty() || !isUsableAugment(stack))
			return false;
		if (!getStackInSlot(slot).isEmpty())
			return false;

		setInventorySlotContents(slot, stack.splitStack(1));
		return true;
	}

	@Nonnull
	@Override
	public ItemStack getStackInSlot(int i) {

		return _inventory.get(i);
	}

	@Override
	public void openInventory(EntityPlayer player) {

	}

	@Override
	public void closeInventory(EntityPlayer player) {

	}

	@Nonnull
	@Override
	public ItemStack decrStackSize(int slot, int size) {

		if (!_inventory.get(slot).isEmpty()) {
			if (_inventory.get(slot).getCount() <= size) {
				@Nonnull ItemStack itemstack = _inventory.get(slot);
				_inventory.set(slot, ItemStack.EMPTY);
				markDirty();
				return itemstack;
			}
			@Nonnull ItemStack itemstack1 = _inventory.get(slot).splitStack(size);
			if (_inventory.get(slot).getCount() <= 0) {
				_inventory.set(slot, ItemStack.EMPTY);
			}
			markDirty();
			return itemstack1;
		}
		else {
			markDirty();
			return ItemStack.EMPTY;
		}
	}

	@Override
	public void setInventorySlotContents(int i, @Nonnull ItemStack itemstack) {

		if (!itemstack.isEmpty()) {
			if (itemstack.getCount() > getInventoryStackLimit())
				itemstack.setCount(getInventoryStackLimit());
			else if (itemstack.getCount() < 0)
				itemstack = ItemStack.EMPTY;
		}
		_inventory.set(i, itemstack);
		markDirty();
	}

	@Override
	public void markDirty() {

		if (!internalChange)
			onFactoryInventoryChanged();
		super.markDirty();
	}

	protected void onFactoryInventoryChanged() {

		if (_areaManager != null && getUpgradeSlot() >= 0 && !internalChange) {
			_areaManager.updateUpgradeLevel(getStackInSlot(getUpgradeSlot()));
		}
	}

	@Override
	public int getInventoryStackLimit() {

		return 127;
	}

	@Override
	public boolean isItemValidForSlot(int slot, @Nonnull ItemStack itemstack) {

		int start = getStartInventorySide(null);
		if (slot < start ||
				slot > (start + getSizeInventorySide(null)))
			return false;
		if (itemstack.isEmpty())
			return true;
		if (itemstack.getCount() > Math.min(itemstack.getMaxStackSize(), getInventoryStackLimit()))
			return false;
		@Nonnull ItemStack slotContent = this.getStackInSlot(slot);
		return slotContent.isEmpty() || UtilInventory.stacksEqual(itemstack, slotContent);
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer entityplayer) {

		if (isInvalid() || world.getTileEntity(pos) != this) {
			return false;
		}
		return entityplayer.getDistanceSq(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64D;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {

		super.readFromNBT(tag);

		isActive = tag.getBoolean("a");

		_inventory = NonNullList.withSize(getSizeInventory(), ItemStack.EMPTY);
		NBTTagList nbttaglist;
		if (tag.hasKey("Items")) {
			nbttaglist = tag.getTagList("Items", 10);
			for (int i = nbttaglist.tagCount(); i-- > 0;) {
				NBTTagCompound slotNBT = nbttaglist.getCompoundTagAt(i);
				int j = slotNBT.getByte("Slot") & 0xff;
				if (j < _inventory.size()) {
					_inventory.set(j, new ItemStack(slotNBT));
					if (_inventory.get(j).getCount() < 0)
						_inventory.set(j, ItemStack.EMPTY);
				}
			}
		}
		markDirty();

		if (manageFluids() && tag.hasKey("Tanks")) {
			IFluidTank[] _tanks = getTanks();

			nbttaglist = tag.getTagList("Tanks", 10);
			for (int i = 0; i < nbttaglist.tagCount(); i++) {
				NBTTagCompound tagCompound = nbttaglist.getCompoundTagAt(i);
				int j = tagCompound.getByte("Tank") & 0xff;
				if (j < _tanks.length) {
					FluidStack l = FluidStack.loadFluidStackFromNBT(tagCompound);
					if (l != null) {
						((FluidTankCore) _tanks[j]).setFluid(l);
					}
				}
			}
		}

		if (tag.hasKey("DropItems")) {
			NonNullList<ItemStack> drops = NonNullList.create();
			nbttaglist = tag.getTagList("DropItems", 10);
			for (int i = nbttaglist.tagCount(); i-- > 0;) {
				NBTTagCompound tagCompound = nbttaglist.getCompoundTagAt(i);
				@Nonnull ItemStack item = new ItemStack(tagCompound);
				if (!item.isEmpty() && item.getCount() > 0) {
					drops.add(item);
				}
			}
			if (drops.size() != 0) {
				failedDrops = drops;
			}
		}
	}

	@Override
	protected NBTTagCompound writePacketData(NBTTagCompound tag) {

		tag = super.writePacketData(tag);

		tag.setBoolean("a", isActive);

		return tag;
	}

	@Override
	protected void handlePacketData(NBTTagCompound tag) {

		super.handlePacketData(tag);

		isActive = tag.getBoolean("a");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {

		tag = super.writeToNBT(tag);
		if (_inventory.size() > 0) {
			NBTTagList items = new NBTTagList();
			for (int i = 0; i < _inventory.size(); i++) {
				if (!_inventory.get(i).isEmpty() && _inventory.get(i).getCount() >= 0) {
					NBTTagCompound slot = new NBTTagCompound();
					slot.setByte("Slot", (byte) i);
					_inventory.get(i).writeToNBT(slot);
					items.appendTag(slot);
				}
			}
			if (items.tagCount() > 0)
				tag.setTag("Items", items);
		}

		if (failedDrops != null) {
			NBTTagList dropItems = new NBTTagList();
			for (@Nonnull ItemStack item : failedDrops) {
				NBTTagCompound tagCompound = new NBTTagCompound();
				item.writeToNBT(tagCompound);
				dropItems.appendTag(tagCompound);
			}
			if (dropItems.tagCount() > 0)
				tag.setTag("DropItems", dropItems);
		}

		tag.setBoolean("a", isActive);

		return tag;
	}

	@Override
	public void writeItemNBT(NBTTagCompound tag) {

		super.writeItemNBT(tag);

		IFluidTank[] _tanks = getTanks();
		if (_tanks.length > 0) {
			NBTTagList tanks = new NBTTagList();
			for (int i = 0, n = _tanks.length; i < n; i++) {
				FluidStack fluid = _tanks[i].getFluid();
				if (fluid != null && fluid.amount > 0) {
					NBTTagCompound tagCompound = new NBTTagCompound();
					tagCompound.setByte("Tank", (byte) i);

					fluid.writeToNBT(tagCompound);
					tanks.appendTag(tagCompound);
				}
			}
			if (tanks.tagCount() > 0)
				tag.setTag("Tanks", tanks);
		}
	}

	@Nonnull
	@Override
	public ItemStack removeStackFromSlot(int slot) {

		return ItemStack.EMPTY;
	}

	public boolean shouldDropSlotWhenBroken(int slot) {

		return true;
	}

	@Override
	public int[] getSlotsForFace(EnumFacing side) {

		int start = getStartInventorySide(side);
		int size = getSizeInventorySide(side);

		int[] slots = new int[size];
		for (int i = 0; i < size; i++) {
			slots[i] = i + start;
		}
		return slots;
	}

	public int getStartInventorySide(EnumFacing side) {

		return 0;
	}

	public int getSizeInventorySide(EnumFacing side) {

		return getSizeInventory();
	}

	@Override
	public boolean canInsertItem(int slot, @Nonnull ItemStack itemstack, EnumFacing side) {

		return itemstack.isEmpty() || this.isItemValidForSlot(slot, itemstack);
	}

	@Override
	public boolean canExtractItem(int slot, @Nonnull ItemStack itemstack, EnumFacing side) {

		return true;
	}

	public int getComparatorOutput() {

		IFluidTank[] tanks = getTanks();
		IFluidTank tank = null;
		if (tanks.length > 0)
			tank = tanks[0];
		float tankPercent = 0, invPercent = 0;
		boolean hasTank = false, hasInventory = false;
		if (tank != null) {
			hasTank = true;
			if (tank.getFluid() != null) {
				tankPercent = ((float) tank.getFluid().amount) / tank.getCapacity();
			}
		}
		if (_inventory.size() > 0) {
			hasInventory = true;
			int len = 0;
			float ret = 0;
			for (int slot = _inventory.size(); slot-- > 0;) {
				if (canInsertItem(slot, ItemStack.EMPTY, null)) {
					@Nonnull ItemStack stack = getStackInSlot(slot);
					if (!stack.isEmpty()) {
						float maxStack = Math.min(stack.getMaxStackSize(), getInventoryStackLimit());
						ret += Math.max(Math.min(stack.getCount() / maxStack, 1), 0);
					}
					++len;
				}
			}
			invPercent = ret / len;
		}
		float mult = hasTank && hasInventory ? (tankPercent + invPercent) / 2 : hasTank ? tankPercent : hasInventory ? invPercent : 0f;
		return (int) Math.ceil(15 * mult);
	}

	public boolean isActive() {

		return isActive;
	}

	public void setIsActive(boolean isActive) {

		this.isActive = isActive;
	}


	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {

	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {
		for(int slot=0; slot < getSizeInventory(); slot++) {
			removeStackFromSlot(slot);
		}
	}

	@Override
	public boolean isEmpty() {

		for (int slot=0; slot < getSizeInventory(); slot++) {
			if (!_inventory.get(slot).isEmpty())
				return false;
		}

		return true;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {

		if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
			return manageFluids();

		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
			return manageSolids();

		return super.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {

		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
			if (manageFluids())
				return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(new FactoryFluidHandler(this, facing));
			return null; // no external overriding via events
		} else if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			if (manageSolids()) {
				//TODO implement proper item handler and drop IInventory instead of using wrappers?
				if (facing != null) {
					return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(new SidedInvWrapper(this, facing));
				} else {
					return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(new InvWrapper(this));
				}
			}
			return null;
		}

		return super.getCapability(capability, facing);
	}


	public static class FactoryFluidHandler implements IFluidHandler {

		private ITankContainerBucketable tile;
		private EnumFacing facing;

		public FactoryFluidHandler(ITankContainerBucketable tile, EnumFacing facing) {

			this.tile = tile;
			this.facing = facing;
		}

		@Override
		public IFluidTankProperties[] getTankProperties() {

			return tile.getTankProperties(facing);
		}

		@Override
		public int fill(FluidStack resource, boolean doFill) {

			return tile.fill(facing, resource, doFill);
		}

		@Nullable
		@Override
		public FluidStack drain(FluidStack resource, boolean doDrain) {

			return tile.drain(facing, resource, doDrain);
		}

		@Nullable
		@Override
		public FluidStack drain(int maxDrain, boolean doDrain) {

			return tile.drain(facing, maxDrain, doDrain);
		}

	}
}
