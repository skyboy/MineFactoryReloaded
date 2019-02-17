package powercrystals.minefactoryreloaded.tile.machine.processing;

import cofh.core.fluid.FluidTankCore;
import cofh.core.util.helpers.ItemHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import powercrystals.minefactoryreloaded.core.UtilInventory;
import powercrystals.minefactoryreloaded.gui.client.GuiAutoBrewer;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryInventory;
import powercrystals.minefactoryreloaded.gui.container.ContainerAutoBrewer;
import powercrystals.minefactoryreloaded.gui.container.ContainerFactoryPowered;
import powercrystals.minefactoryreloaded.setup.MFRConfig;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryPowered;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileEntityAutoBrewer extends TileEntityFactoryPowered {

	protected boolean _inventoryDirty;
	protected byte[] spareResources;

	public TileEntityAutoBrewer() {

		super(Machine.AutoBrewer);
		spareResources = new byte[getSizeInventory() / 5];
		setManageSolids(true);
		_tanks[0].setLock(FluidRegistry.WATER);
	}

	private int getProcessSlot(int row) {

		return row * 5;
	}

	private int getTemplateSlot(int row) {

		return row * 5 + 1;
	}

	private int getResourceSlot(int row, int slot) {

		return row * 5 + slot + 2;
	}

	@Override
	protected FluidTankCore[] createTanks() {

		return new FluidTankCore[] { new FluidTankCore(BUCKET_VOLUME * 4) };
	}

	@Override
	public int getSizeInventory() {

		// 6 sets of: process, template, res, res, res
		// 30 is output, 31 is empty bottle input
		return 32;
	}

	@Override
	public ContainerFactoryPowered getContainer(InventoryPlayer inventoryPlayer) {

		return new ContainerAutoBrewer(this, inventoryPlayer);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiFactoryInventory getGui(InventoryPlayer inventoryPlayer) {

		return new GuiAutoBrewer(getContainer(inventoryPlayer), this);
	}

	@Override
	protected boolean activateMachine() {

		boolean hasWorkToDo = false, didWork = false;
		boolean doingWork = getWorkDone() > 0;
		if (doingWork && !_inventoryDirty)
			hasWorkToDo = true;
		else {
			final int waterCost = MFRConfig.autobrewerFluidCost.getInt();
			for (int row = 6; row-- > 0; ) {
				int processSlot = getProcessSlot(row), templateSlot = getTemplateSlot(row);
				if (!_inventory.get(31).isEmpty() && _inventory.get(processSlot).isEmpty() && !_inventory.get(templateSlot).isEmpty()) {

					if (row == 0 || _inventory.get(getTemplateSlot(row - 1)).isEmpty()) {
						@Nonnull ItemStack waterBottle = PotionUtils
								.addPotionToItemStack(new ItemStack(Items.POTIONITEM), PotionTypes.WATER);
						if (BrewingRecipeRegistry.hasOutput(waterBottle, _inventory.get(templateSlot)))
							if (drain(waterCost, false, _tanks[0]) == waterCost) {
								drain(waterCost, true, _tanks[0]);
								_inventory.set(31, ItemHelper.consumeItem(_inventory.get(31)));
								_inventory.set(processSlot, waterBottle);
								didWork = true;
							}
					}
				}
				if (!_inventory.get(processSlot).isEmpty()) {
					if (_inventory.get(getProcessSlot(row + 1)).isEmpty() && canBrew(row))
						hasWorkToDo = true;
				}
			}
			_inventoryDirty = false;
		}

		if (!hasWorkToDo) {
			setWorkDone(0);
			setIdleTicks(getIdleTicksMax());
			return didWork;
		}

		if (getWorkDone() < getWorkMax()) {
			return incrementWorkDone();
		}

		setWorkDone(0);

		for (int row = 6; row-- > 0;) {
			@Nonnull ItemStack current = _inventory.get(getProcessSlot(row));
			if (current.isEmpty()) {
				continue;
			}

			@Nonnull ItemStack next = _inventory.get(getProcessSlot(row + 1));
			if (!next.isEmpty()) {
				continue;
				// no exiting early, we know there's a potion that can be moved/brewed
			}

			@Nonnull ItemStack template = _inventory.get(getTemplateSlot(row));

			if (template.isEmpty()) {
				continue;
			}

			for (int col = 3; col-- > 0; ) {
				final int slot = getResourceSlot(row, col);
				ItemStack ingredient = _inventory.get(slot);
				if (spareResources[row] <= 0 && !ingredientsEqual(template, ingredient)) {
					continue;
				}

				@Nonnull ItemStack newPotion = this.getPotionResult(current, template);

				if (!newPotion.isEmpty()) {
					_inventory.set(getProcessSlot(row + 1), newPotion);
				} else {
					_inventory.set(getProcessSlot(row + 1), current);
				}

				_inventory.set(getProcessSlot(row), ItemStack.EMPTY);

				if (current == newPotion)
					break;

				consumeIngredient(ingredient, row, col, slot);

				break;
			}
		}
		return true;
	}

	// FIXME: validate all of this logic against new stack logic
	private void consumeIngredient(ItemStack template, int row, int column, int slot) {

		@Nonnull ItemStack ingredient = _inventory.get(slot);
		if (spareResources[row] == 0) {
			ingredient.shrink(1);

			if (template.getItem().hasContainerItem(ingredient)) {
				@Nonnull ItemStack r = template.getItem().getContainerItem(ingredient);
				if (!r.isEmpty() && r.isItemStackDamageable() && r.getItemDamage() > r.getMaxDamage())
					r = ItemStack.EMPTY;
				if (ingredient.getCount() <= 0) {
					_inventory.set(slot, r);
					ingredient = r;
				}
				else {
					if (column < 2 && _inventory.get(slot + 1).isEmpty()) {
						_inventory.set(slot + 1, r);
					} else if (column < 1 && _inventory.get(slot + 2).isEmpty()) {
						_inventory.set(slot + 2, r);
					} else if (_inventory.get(getProcessSlot(6)).isEmpty()) {
						_inventory.set(getProcessSlot(6), r);
					} else {
						UtilInventory.dropStack(this, r);
					}
				}
			}
			if (ingredient.isEmpty())
				_inventory.set(slot, ItemStack.EMPTY);

			spareResources[row]++;
		} else {
			spareResources[row]--;
		}
	}
/*
	private void consumeIngredient(ItemStack template, int row, int col, int slot) {

		ItemStack ingredient = _inventory[slot];
		if (spareResources[row] == 0) {
			--ingredient.stackSize;

			if (template.getItem().hasContainerItem(ingredient)) {
				ItemStack r = template.getItem().getContainerItem(ingredient);
				if (r != null && (r.stackSize <= 0 || (r.isItemStackDamageable() && r.getItemDamage() > r.getMaxDamage())))
					r = null;
				if (ingredient.stackSize <= 0)
					_inventory[slot] = ingredient = r;
				else {
					if (col < 2 && _inventory[slot + 1] == null) {
						_inventory[slot + 1] = r;
					} else if (col < 1 && _inventory[slot + 2] == null) {
						_inventory[slot + 2] = r;
					} else if (_inventory[getProcessSlot(6)] == null) {
						_inventory[getProcessSlot(6)] = r;
					} else {
						UtilInventory.dropStack(this, r);
					}
				}
			}
			if (ingredient != null && ingredient.stackSize <= 0)
				_inventory[slot] = null;

			spareResources[row]++;
		} else {
			spareResources[row]--;
		}
	}//*/

	private boolean canBrew(int row) {

		@Nonnull ItemStack ingredient = _inventory.get(getTemplateSlot(row));
		if (ingredient.isEmpty()) {
			return false;
		}

		if (!BrewingRecipeRegistry.isValidIngredient(ingredient)) {
			return false;
		}

		boolean hasIngredients = spareResources[row] > 0;
		if (!hasIngredients) {
			for (int i = 0; i < 3; i++) {
				if (ingredientsEqual(ingredient, _inventory.get(getResourceSlot(row, i)))) {
					hasIngredients = true;
					break;
				}
			}
		}

		if (!hasIngredients) {
			return false;
		}

		@Nonnull ItemStack existingPotion = _inventory.get(getProcessSlot(row));
		if (!existingPotion.isEmpty()) {
			ItemStack newPotion = this.getPotionResult(existingPotion, ingredient);

			if (!newPotion.isEmpty()) {
				return existingPotion != newPotion;
			}
		}

		return false;
	}

	@Nonnull
	private ItemStack getPotionResult(@Nonnull ItemStack existingPotion, @Nonnull ItemStack ingredient) {

		if (ingredient.isEmpty() || !BrewingRecipeRegistry.isValidIngredient(ingredient)) {
			return existingPotion;
		}
		return BrewingRecipeRegistry.getOutput(existingPotion, ingredient);
	}

	@Override
	public int getWorkMax() {

		return 160;
	}

	@Override
	public int getIdleTicksMax() {

		return 10;
	}

	@Override
	public boolean canInsertItem(int slot, @Nonnull ItemStack itemstack, EnumFacing side) {

		int row = slot / 5;
		int column = slot % 5;

		if (itemstack.isEmpty()) return false;
		if (slot == 31) return itemstack.getItem().equals(Items.GLASS_BOTTLE);
		if (row == 6) return false;
		if (column == 1) return false;

		@Nonnull ItemStack template = _inventory.get(getTemplateSlot(row));
		if (column == 0) return !template.isEmpty() && BrewingRecipeRegistry.hasOutput(itemstack, template) &&
				(row == 0 || _inventory.get(getTemplateSlot(row - 1)).isEmpty());
		return ingredientsEqual(template, itemstack);
	}

	@Override
	public boolean canExtractItem(int slot, @Nonnull ItemStack itemstack, EnumFacing side) {

		int row = slot / 5;
		int column = slot % 5;

		if (row == 6) return slot != 31;
		if (column == 1) return false;
		if (column == 0) return _inventory.get(getTemplateSlot(row)).isEmpty();
		return !ingredientsEqual(_inventory.get(getTemplateSlot(row)), itemstack);
	}

	private boolean ingredientsEqual(@Nonnull ItemStack template, @Nonnull ItemStack ingredient) {

		if (template.isEmpty() || ingredient.isEmpty() || !BrewingRecipeRegistry.isValidIngredient(template)) {
			return false;
		}

		if ((!template.getItem().equals(ingredient.getItem())) ||
				(template.getHasSubtypes() && template.getItemDamage() != ingredient.getItemDamage())) {
			return false;
		}
		if (!template.hasTagCompound() && !ingredient.hasTagCompound()) {
			return true;
		}
		NBTTagCompound tagA = template.getTagCompound();
		NBTTagCompound tagB = ingredient.getTagCompound();
		if (tagB != null) {
			tagB = tagB.copy();
			tagB.removeTag("display");
			tagB.removeTag("ench");
			tagB.removeTag("RepairCost");
		}
		return tagA == null ? tagB.hasNoTags() : (tagB == null ? tagA.hasNoTags() : tagA.equals(tagB));
	}

	@Override
	public void setInventorySlotContents(int slot, @Nonnull ItemStack itemstack) {

		if (!shouldDropSlotWhenBroken(slot)) {
			if (!itemstack.isEmpty()) {
				itemstack = itemstack.copy();
				itemstack.setCount(1);
				NBTTagCompound tagA = itemstack.getTagCompound();
				if (tagA != null) {
					tagA.removeTag("display");
					tagA.removeTag("ench");
					tagA.removeTag("RepairCost");
					if (tagA.hasNoTags()) {
						itemstack.setTagCompound(null);
					}
				}
			}
			if (!ingredientsEqual(_inventory.get(slot), itemstack)) {
				spareResources[slot / 5] = 0;
			}
		}
		super.setInventorySlotContents(slot, itemstack);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {

		tag = super.writeToNBT(tag);

		tag.setByteArray("spareResources", spareResources);

		return tag;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {

		super.readFromNBT(tag);

		if (tag.hasKey("spareResources")) {
			spareResources = tag.getByteArray("spareResources");
		}
	}

	@Override
	protected void onFactoryInventoryChanged() {

		super.onFactoryInventoryChanged();
		_inventoryDirty = true;
	}

	@Override
	public boolean shouldDropSlotWhenBroken(int slot) {

		return slot % 5 != 1 || slot == 31;
	}

	@Override
	public void writePortableData(EntityPlayer player, NBTTagCompound tag) {

		// TODO: read/write template slots
	}

	@Override
	public void readPortableData(EntityPlayer player, NBTTagCompound tag) {

	}

	@Override
	public boolean allowBucketDrain(EnumFacing facing, @Nonnull ItemStack stack) {

		return !stack.getItem().equals(Items.GLASS_BOTTLE);
	}

	@Override
	public boolean allowBucketFill(EnumFacing facing, @Nonnull ItemStack stack) {

		return !stack.getItem().equals(Items.POTIONITEM);
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
