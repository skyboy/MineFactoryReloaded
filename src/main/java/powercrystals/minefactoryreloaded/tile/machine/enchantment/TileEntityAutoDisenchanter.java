package powercrystals.minefactoryreloaded.tile.machine.enchantment;

import cofh.core.fluid.FluidTankCore;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import powercrystals.minefactoryreloaded.gui.client.GuiAutoDisenchanter;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryInventory;
import powercrystals.minefactoryreloaded.gui.container.ContainerAutoDisenchanter;
import powercrystals.minefactoryreloaded.setup.MFRFluids;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryPowered;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Locale;

public class TileEntityAutoDisenchanter extends TileEntityFactoryPowered {

	private boolean _repeatDisenchant;

	public TileEntityAutoDisenchanter() {

		super(Machine.AutoDisenchanter);
		setManageSolids(true);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiFactoryInventory getGui(InventoryPlayer inventoryPlayer) {

		return new GuiAutoDisenchanter(getContainer(inventoryPlayer), this);
	}

	@Override
	public ContainerAutoDisenchanter getContainer(InventoryPlayer inventoryPlayer) {

		return new ContainerAutoDisenchanter(this, inventoryPlayer);
	}

	public boolean getRepeatDisenchant() {

		return _repeatDisenchant;
	}

	public void setRepeatDisenchant(boolean repeatDisenchant) {

		_repeatDisenchant = repeatDisenchant;
	}

	@Override
	public int getSizeInventory() {

		return 5;
	}

	@Override
	public int getSizeInventorySide(EnumFacing side) {

		return 4;
	}

	@Override
	public boolean canInsertItem(int slot, @Nonnull ItemStack stack, EnumFacing side) {

		if (stack.isEmpty())
			return false;
		if (slot == 0) {
			return !stack.getEnchantmentTagList().hasNoTags() || stack.getItem().equals(Items.ENCHANTED_BOOK);
		} else if (slot == 1) {
			return stack.getItem().equals(Items.BOOK);
		}
		return false;
	}

	@Override
	public boolean canExtractItem(int slot, @Nonnull ItemStack itemstack, EnumFacing side) {

		if (slot == 2 || slot == 3) return true;
		return false;
	}

	@Override
	protected boolean activateMachine() {

		if (_inventory.get(4).isEmpty()) {
			if (_inventory.get(0).isEmpty())
				return false;
			_inventory.set(4, _inventory.get(0).splitStack(1));
			if (_inventory.get(0).getCount() <= 0)
				_inventory.set(0, ItemStack.EMPTY);
			markChunkDirty();
		}

		@Nonnull ItemStack stack = _inventory.get(4);
		boolean isBook = stack.getItem().equals(Items.ENCHANTED_BOOK);
		NBTTagList list = isBook ? ItemEnchantedBook.getEnchantments(stack) : stack.getEnchantmentTagList();
		if (list.tagCount() <= 0 && _inventory.get(2).isEmpty()) {
			_inventory.set(2, stack);
			setInventorySlotContents(4, ItemStack.EMPTY);
		} else if (list.tagCount() > 0 &&
				(!_inventory.get(1).isEmpty() && _inventory.get(1).getItem().equals(Items.BOOK)) &
				_inventory.get(2).isEmpty() &
				_inventory.get(3).isEmpty()) {
			if (getWorkDone() >= getWorkMax()) {
				decrStackSize(1, 1);

				NBTTagCompound enchTag;
				if (isBook) {
					enchTag = list.getCompoundTagAt(0);
					list.removeTag(0);
					if (list.tagCount() == 0) {
						_inventory.set(4, new ItemStack(Items.BOOK, 1));
					}
				} else {
					int enchIndex = world.rand.nextInt(list.tagCount());
					enchTag = list.getCompoundTagAt(enchIndex);

					list.removeTag(enchIndex);
					if (list.tagCount() == 0) {
						stack.getTagCompound().removeTag("ench");
						if (stack.getTagCompound().hasNoTags()) {
							stack.setTagCompound(null);
						}
					}

					if (stack.isItemStackDamageable()) {
						int damage = world.rand.nextInt(1 + (stack.getMaxDamage() / 4));
						int m = stack.getMaxDamage();
						damage = Math.min(m, damage + 1 + (m / 10)) + (m == 1 ? 1 : 0);
						if (stack.attemptDamageItem(damage, world.rand, null)) {
							_inventory.set(4, ItemStack.EMPTY);
						}
					}
				}

				if (!_repeatDisenchant || (!_inventory.get(4).isEmpty() && _inventory.get(4).getEnchantmentTagList().hasNoTags())) {
					_inventory.set(2, _inventory.get(4));
					_inventory.set(4, ItemStack.EMPTY);
				}

				setInventorySlotContents(3, new ItemStack(Items.ENCHANTED_BOOK, 1));

				NBTTagCompound baseTag = new NBTTagCompound();
				NBTTagList enchList = new NBTTagList();
				enchList.appendTag(enchTag);
				baseTag.setTag("StoredEnchantments", enchList);
				_inventory.get(3).setTagCompound(baseTag);

				setWorkDone(0);
			} else {
				markChunkDirty();
				if (!incrementWorkDone())
					return false;
			}

			return true;
		}
		return false;
	}

	@Override
	public int getWorkMax() {

		return 600;
	}

	@Override
	public int getIdleTicksMax() {

		return 1;
	}

	@Override
	public void writePortableData(EntityPlayer player, NBTTagCompound tag) {

		tag.setBoolean("repeatDisenchant", _repeatDisenchant);
	}

	@Override
	public void readPortableData(EntityPlayer player, NBTTagCompound tag) {

		_repeatDisenchant = tag.getBoolean("repeatDisenchant");
	}

	@Override
	public void writeItemNBT(NBTTagCompound tag) {

		super.writeItemNBT(tag);
		if (_repeatDisenchant)
			tag.setBoolean("repeatDisenchant", _repeatDisenchant);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {

		super.readFromNBT(tag);
		_repeatDisenchant = tag.getBoolean("repeatDisenchant");
	}

	public static class Fluid extends TileEntityAutoDisenchanter {

		public Fluid() {

			_tanks[0].setLock(MFRFluids.getFluid("mob_essence"));
		}

		@Override
		protected boolean incrementWorkDone() {

			if (drain(4, false, _tanks[0]) != 4)
				return false;
			drain(4, true, _tanks[0]);
			return super.incrementWorkDone();
		}

		@Override
		public String getGuiBackground() {

			if (_machine == null)
				return null;
			return _machine.getName().toLowerCase(Locale.US) + "2";
		}

		@Override
		protected FluidTankCore[] createTanks() {

			return new FluidTankCore[] { new FluidTankCore(4 * BUCKET_VOLUME) };
		}

		@Override
		public boolean allowBucketFill(EnumFacing facing, @Nonnull ItemStack stack) {

			return true;
		}

		@Override
		protected boolean canDrainTank(EnumFacing facing, int index) {

			return false;
		}

		@Nullable
		@Override
		public FluidStack drain(EnumFacing facing, FluidStack resource, boolean doDrain) {

			return null;
		}

		@Nullable
		@Override
		public FluidStack drain(EnumFacing facing, int maxDrain, boolean doDrain) {

			return null;
		}

	}

}
