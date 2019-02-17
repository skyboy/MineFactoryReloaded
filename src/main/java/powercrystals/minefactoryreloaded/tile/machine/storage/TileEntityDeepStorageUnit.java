package powercrystals.minefactoryreloaded.tile.machine.storage;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import powercrystals.minefactoryreloaded.api.IDeepStorageUnit;
import powercrystals.minefactoryreloaded.core.UtilInventory;
import powercrystals.minefactoryreloaded.gui.client.GuiDeepStorageUnit;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryInventory;
import powercrystals.minefactoryreloaded.gui.container.ContainerDeepStorageUnit;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryTickable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileEntityDeepStorageUnit extends TileEntityFactoryTickable implements IDeepStorageUnit {

	private boolean _ignoreChanges = true;
	private boolean _passingItem = false;

	private int _storedQuantity;
	@Nonnull
	private ItemStack _storedItem = ItemStack.EMPTY;

	public TileEntityDeepStorageUnit() {

		super(Machine.DeepStorageUnit);
		setManageSolids(true);
	}

	@Override
	public void validate() {

		super.validate();
		_ignoreChanges = false;
		onFactoryInventoryChanged();
	}

	@Override
	public void invalidate() {

		super.invalidate();
		_ignoreChanges = true;
	}

	@Override
	public boolean shouldDropSlotWhenBroken(int slot) {

		return slot < 2;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiFactoryInventory getGui(InventoryPlayer inventoryPlayer) {

		return new GuiDeepStorageUnit(getContainer(inventoryPlayer), this);
	}

	@Override
	public ContainerDeepStorageUnit getContainer(InventoryPlayer inventoryPlayer) {

		return new ContainerDeepStorageUnit(this, inventoryPlayer);
	}

	public int getQuantity() {

		return _storedQuantity;
	}

	private int getQuantityAdjusted() {

		int quantity = _storedQuantity;

		for (int i = 2; i < getSizeInventory(); i++) {
			if (!_inventory.get(i).isEmpty() && UtilInventory.stacksEqual(_storedItem, _inventory.get(i))) {
				quantity += _inventory.get(i).getCount();
			}
		}

		return quantity;
	}

	public void setQuantity(int quantity) {

		_storedQuantity = quantity;
	}

	private void clearSlots() {

		for (int i = 0; i < getSizeInventory(); i++) {
			_inventory.set(i, ItemStack.EMPTY);
		}
	}

	@Override
	public EnumFacing getDropDirection() {

		return EnumFacing.UP;
	}

	@Override
	public void setIsActive(boolean isActive) {

		super.setIsActive(isActive);
		onFactoryInventoryChanged();
	}

	@Override
	protected void onFactoryInventoryChanged() {

		super.onFactoryInventoryChanged();
		if (_ignoreChanges | world == null || world.isRemote)
			return;

		if (!isActive() && (_inventory.get(2).isEmpty()) & !_storedItem.isEmpty() & _storedQuantity == 0) {
			_storedItem = ItemStack.EMPTY;
		}
		checkInput(0);
		checkInput(1);

		if ((_inventory.get(2).isEmpty()) & !_storedItem.isEmpty() & _storedQuantity > 0) {
			_inventory.set(2, _storedItem.copy());
			_inventory.get(2).setCount(Math.min(_storedQuantity,
					Math.min(_storedItem.getMaxStackSize(), getInventoryStackLimit())));
			_storedQuantity -= _inventory.get(2).getCount();
		} else if (!_inventory.get(2).isEmpty() & _storedQuantity > 0 &&
				_inventory.get(2).getCount() < _inventory.get(2).getMaxStackSize() &&
				UtilInventory.stacksEqual(_storedItem, _inventory.get(2))) {
			int amount = Math.min(_inventory.get(2).getMaxStackSize() - _inventory.get(2).getCount(), _storedQuantity);
			_inventory.get(2).grow(amount);
			_storedQuantity -= amount;
		}
	}

	private void checkInput(int slot) {

		l:
		if (!_inventory.get(slot).isEmpty()) {
			if (_storedItem.isEmpty()) {
				_storedItem = _inventory.get(slot).copy();
				_storedItem.setCount(1);
				_storedQuantity = _inventory.get(slot).getCount();
				_inventory.set(slot, ItemStack.EMPTY);
			} else if (UtilInventory.stacksEqual(_inventory.get(slot), _storedItem)) {
				if ((getMaxStoredCount() - _storedItem.getMaxStackSize()) - _inventory.get(slot).getCount() < _storedQuantity) {
					int amt = (getMaxStoredCount() - _storedItem.getMaxStackSize()) - _storedQuantity;
					_inventory.get(slot).shrink(amt);
					_storedQuantity += amt;
				} else {
					_storedQuantity += _inventory.get(slot).getCount();
					_inventory.set(slot, ItemStack.EMPTY);
				}
			}
			// boot improperly typed items from the input slots
			else {
				_passingItem = true;
				_inventory.set(slot, UtilInventory.dropStack(this, _inventory.get(slot), this.getDropDirection()));
				_passingItem = false;
				break l;
			}
			// internal inventory is full
			if (!_inventory.get(slot).isEmpty()) {
				_passingItem = true;
				_inventory.set(slot, UtilInventory.dropStack(this, _inventory.get(slot), this.getDropDirection()));
				_passingItem = false;
			}
		}
	}

	@Override
	public int getSizeInventory() {

		return 3;
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {

		return player.getDistanceSq(pos) <= 64D;
	}

	@Override
	public int getStartInventorySide(EnumFacing side) {

		return 0;
	}

	@Override
	public int getSizeInventorySide(EnumFacing side) {

		return getSizeInventory();
	}

	@Override
	public void setInventorySlotContents(int i, @Nonnull ItemStack itemstack) {

		if (!itemstack.isEmpty()) {
			if (itemstack.getCount() < 0)
				itemstack = ItemStack.EMPTY;
		}
		_inventory.set(i, itemstack);
		markDirty();
	}

	/*
	 * Should only allow matching items to be inserted in the "in" slots. Nothing goes in the "out" slot.
	 */
	@Override
	public boolean canInsertItem(int slot, @Nonnull ItemStack stack, @Nullable EnumFacing side) {

		if (_passingItem)
			return false;
		if (slot >= 2)
			return false;
		@Nonnull ItemStack stored = _storedItem;
		if (stored.isEmpty())
			stored = _inventory.get(2);
		return stored.isEmpty() || (UtilInventory.stacksEqual(stored, stack) && (getMaxStoredCount() - stored.getMaxStackSize()) > _storedQuantity);
	}

	@Override
	public boolean isItemValidForSlot(int slot, @Nonnull ItemStack itemstack) {

		return canInsertItem(slot, itemstack, null);
	}

	@Override
	public boolean canExtractItem(int slot, @Nonnull ItemStack itemstack, EnumFacing side) {

		return true;
	}

	@Override
	public void writeItemNBT(NBTTagCompound tag) {

		int storedAdd = 0;
		@Nonnull ItemStack o = _inventory.get(2);
		if (!o.isEmpty()) {
			storedAdd = o.getCount();
			_inventory.set(2, ItemStack.EMPTY);
		}
		super.writeItemNBT(tag);
		_inventory.set(2, o);

		if (!_storedItem.isEmpty()) {
			tag.setBoolean("locked", isActive());
			tag.setTag("storedStack", _storedItem.writeToNBT(new NBTTagCompound()));
			tag.setInteger("storedQuantity", _storedQuantity + storedAdd);
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {

		@Nonnull ItemStack o = _inventory.get(2);
		_inventory.set(2, ItemStack.EMPTY);
		tag = super.writeToNBT(tag);
		_inventory.set(2, o);
		writeItemNBT(tag);

		return tag;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {

		_ignoreChanges = true;
		super.readFromNBT(tag);

		_storedQuantity = tag.getInteger("storedQuantity");
		_storedItem = ItemStack.EMPTY;

		if (tag.hasKey("storedStack")) {
			_storedItem = new ItemStack((NBTTagCompound) tag.getTag("storedStack"));
			if (!_storedItem.isEmpty()) {
				_storedItem.setCount(1);
				setIsActive(tag.getBoolean("locked"));
			}
		}

		if (_storedItem.isEmpty()) {
			_storedQuantity = 0;
		}
		_ignoreChanges = false;
	}

	@Nonnull
	public ItemStack getStoredItemRaw() {

		if (!_storedItem.isEmpty()) {
			return _storedItem.copy();
		}
		return ItemStack.EMPTY;
	}

	@SideOnly(Side.CLIENT)
	public void setStoredItemRaw(@Nonnull ItemStack type) {

		if (world.isRemote) {
			_storedItem = type;
		}
	}

	@Nonnull
	@Override
	public ItemStack getStoredItemType() {

		return _storedItem.copy();
	}

	@Override
	public int getStoredItemCount() {

		return getQuantityAdjusted();
	}

	@Override
	public void setStoredItemCount(int amount) {

		for (int i = 0; i < getSizeInventory(); i++) {
			if (UtilInventory.stacksEqual(_inventory.get(i), _storedItem)) {
				if (amount == 0) {
					_inventory.set(i, ItemStack.EMPTY);
				} else if (amount >= _inventory.get(i).getCount()) {
					amount -= _inventory.get(i).getCount();
				} else if (amount < _inventory.get(i).getCount()) {
					_inventory.get(i).setCount(amount);
					amount = 0;
				}
			}
		}
		_storedQuantity = amount;
		markDirty();
	}

	@Override
	public void setStoredItemType(@Nonnull ItemStack type, int amount) {

		if (!_storedItem.isEmpty() && isActive() && !UtilInventory.stacksEqual(type, _storedItem))
			return;
		clearSlots();
		_storedQuantity = amount;
		_storedItem = ItemStack.EMPTY;
		if (type.isEmpty())
			return;
		_storedItem = type.copy();
		_storedItem.setCount(1);
		markDirty();
	}

	@Override
	public int getMaxStoredCount() {

		return Integer.MAX_VALUE;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {

		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {

		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(new InvWrapper(this) {

				@Nonnull
				@Override
				public ItemStack getStackInSlot(int slot) {

					return slot < 2 ? super.getStackInSlot(slot) : getStoredItemType();
				}
			});
		}

		return super.getCapability(capability, facing);
	}
}
