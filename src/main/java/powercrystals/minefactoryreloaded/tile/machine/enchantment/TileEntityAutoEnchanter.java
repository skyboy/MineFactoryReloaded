package powercrystals.minefactoryreloaded.tile.machine.enchantment;

import cofh.core.fluid.FluidTankCore;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import powercrystals.minefactoryreloaded.core.AutoEnchantmentHelper;
import powercrystals.minefactoryreloaded.gui.client.GuiAutoEnchanter;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryInventory;
import powercrystals.minefactoryreloaded.gui.container.ContainerAutoEnchanter;
import powercrystals.minefactoryreloaded.setup.MFRFluids;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryPowered;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Random;

public class TileEntityAutoEnchanter extends TileEntityFactoryPowered {

	private Random _rand;
	private int _targetLevel;

	public TileEntityAutoEnchanter() {

		super(Machine.AutoEnchanter);
		_rand = new Random();

		_targetLevel = 30;
		setManageSolids(true);
		_tanks[0].setLock(MFRFluids.getFluid("mob_essence"));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiFactoryInventory getGui(InventoryPlayer inventoryPlayer) {

		return new GuiAutoEnchanter(getContainer(inventoryPlayer), this);
	}

	@Override
	public ContainerAutoEnchanter getContainer(InventoryPlayer inventoryPlayer) {

		return new ContainerAutoEnchanter(this, inventoryPlayer);
	}

	@Override
	public int getWorkMax() {

		if(!_inventory.get(0).isEmpty() && _inventory.get(0).getItem().equals(Items.GLASS_BOTTLE)) {

			return 250;
		}
		return (_targetLevel + (int)(Math.pow((_targetLevel) / 7.5, 4) * 10 * getEnchantmentMultiplier()));
	}

	private double getEnchantmentMultiplier() {

		@Nonnull ItemStack s = _inventory.get(0);
		if(s.isEmpty()) {
			return 1;
		}

		Map<Enchantment, Integer> enchantments = AutoEnchantmentHelper.getEnchantments(s);
		if(enchantments.size() == 0) {
			return 1;
		}

		return Math.pow(enchantments.size() + 1.0, 2);
	}

	@Override
	public int getIdleTicksMax() {

		return 1;
	}

	public int getTargetLevel() {

		return _targetLevel;
	}

	public void setTargetLevel(int targetLevel) {

		_targetLevel = targetLevel;
		if(_targetLevel > 30) _targetLevel = 30;
		if(_targetLevel < 1) _targetLevel = 1;
		if(getWorkDone() >= getWorkMax()) {
			activateMachine();
		}
	}

	@Override
	protected boolean activateMachine() {

		if (world.isRemote) {
			return false;
		}
		@Nonnull ItemStack input = _inventory.get(0);
		@Nonnull ItemStack output = _inventory.get(1);
		if(input.isEmpty()) {
			setWorkDone(0);
			return false;
		}
		if (input.getCount() <= 0) {
			setInventorySlotContents(0, ItemStack.EMPTY);
			setWorkDone(0);
			return false;
		}
		if (!output.isEmpty()) {
			if (output.getCount() >= output.getMaxStackSize() || output.getCount() >= getInventoryStackLimit()) {
				setWorkDone(0);
				return false;
			}
			if (output.getCount() <= 0) {
				setInventorySlotContents(1, ItemStack.EMPTY);
				output = ItemStack.EMPTY;
			}
		}
		if ((input.getItem().getItemEnchantability(input) == 0 &&
				!input.getItem().equals(Items.GLASS_BOTTLE)) ||
				input.getItem().equals(Items.ENCHANTED_BOOK)) {
			if (output.isEmpty()) {
				_inventory.set(0, ItemStack.EMPTY);
				setInventorySlotContents(1, input);
			}
			else if (input.isItemEqual(output) && ItemStack.areItemStackTagsEqual(input, output)) {
				int amountToCopy = Math.min(output.getMaxStackSize() - output.getCount(), input.getCount());
				amountToCopy = Math.min(getInventoryStackLimit() - output.getCount(), amountToCopy);
				if (amountToCopy <= 0) {
					setWorkDone(0);
					return false;
				}
				output.grow(amountToCopy);
				input.shrink(amountToCopy);
				if (input.getCount() <= 0) {
					setInventorySlotContents(0, ItemStack.EMPTY);
				}
			}
			else {
				setWorkDone(0);
				return false;
			}
			setWorkDone(0);
			return true;
		}
		else if (getWorkDone() >= getWorkMax()) {
			if (input.getItem().equals(Items.GLASS_BOTTLE)) {
				if (output.isEmpty()) {
					output = new ItemStack(Items.EXPERIENCE_BOTTLE, 0, 0);
				}
				if (!output.getItem().equals(Items.EXPERIENCE_BOTTLE)) {
					setWorkDone(0);
					return false;
				}
				input.shrink(1);
				if (input.getCount() <= 0) {
					_inventory.set(0, ItemStack.EMPTY);
				}
				output.shrink(1);
				setInventorySlotContents(1, output);
				setWorkDone(0);
			}
			else if (output.isEmpty()) {
				output = AutoEnchantmentHelper.addRandomEnchantment(this._rand, input, _targetLevel);
				if (input.getCount() <= 0) {
					_inventory.set(0, ItemStack.EMPTY);
				}
				setInventorySlotContents(1, output);
				setWorkDone(0);
			}
			else {
				return false;
			}
			return true;
		}
		else if (drain(4, false, _tanks[0]) == 4) {
			if (!incrementWorkDone()) return false;
			drain(4, true, _tanks[0]);
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	public int getSizeInventory() {

		return 2;
	}

	@Override
	public int getInventoryStackLimit() {

		return 64;
	}

	@Override
	public int getSizeInventorySide(EnumFacing side) {

		return 2;
	}

	@Override
	public boolean canInsertItem(int slot, @Nonnull ItemStack input, EnumFacing side) {

		if(slot == 0) {
			@Nonnull ItemStack contents = _inventory.get(0);
			// TODO: limit input to glass bottles and items with an enchantability > 0
			return contents.isEmpty() || (contents.getCount() < getInventoryStackLimit() &&
					input.isItemEqual(contents) && ItemStack.areItemStackTagsEqual(input, contents));
		}
		return false;
	}

	@Override
	public boolean canExtractItem(int slot, @Nonnull ItemStack itemstack, EnumFacing side) {

		if(slot == 1) return true;
		return false;
	}

	@Override
	public void writePortableData(EntityPlayer player, NBTTagCompound tag) {

		tag.setInteger("targetLevel", _targetLevel);
	}

	@Override
	public void readPortableData(EntityPlayer player, NBTTagCompound tag) {

		setTargetLevel(tag.getInteger("targetLevel"));
	}

	@Override
	public void writeItemNBT(NBTTagCompound tag) {

		super.writeItemNBT(tag);
		if (_targetLevel != 30)
			tag.setInteger("targetLevel", _targetLevel);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {

		super.readFromNBT(tag);
		if (tag.hasKey("targetLevel"))
			_targetLevel = tag.getInteger("targetLevel");
	}

	@Override
	protected FluidTankCore[] createTanks() {

		return new FluidTankCore[]{new FluidTankCore(4 * BUCKET_VOLUME)};
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
