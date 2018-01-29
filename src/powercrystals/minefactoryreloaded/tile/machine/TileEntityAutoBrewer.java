package powercrystals.minefactoryreloaded.tile.machine;

import cofh.core.fluid.FluidTankCore;
import cofh.lib.util.helpers.ItemHelper;
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
				if (_inventory[31] != null && _inventory[processSlot] == null && _inventory[templateSlot] != null) {

					if (row == 0 || _inventory[getTemplateSlot(row - 1)] == null) {
						ItemStack waterBottle = PotionUtils
								.addPotionToItemStack(new ItemStack(Items.POTIONITEM), PotionTypes.WATER);
						if (BrewingRecipeRegistry.hasOutput(waterBottle, _inventory[templateSlot]))
							if (drain(waterCost, false, _tanks[0]) == waterCost) {
								drain(waterCost, true, _tanks[0]);
								_inventory[31] = ItemHelper.consumeItem(_inventory[31]);
								_inventory[processSlot] = waterBottle;
								didWork = true;
							}
					}
				}
				if (_inventory[processSlot] != null) {
					if (_inventory[getProcessSlot(row + 1)] == null && canBrew(row))
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

		for (int row = 6; row-- > 0; ) {
			ItemStack current = _inventory[getProcessSlot(row)];
			if (current == null) {
				continue;
			}
			ItemStack next = _inventory[getProcessSlot(row + 1)];
			if (next != null) {
				continue;
				// no exiting early, we know there's a potion that can be moved/brewed
			}

			ItemStack template = _inventory[getTemplateSlot(row)];

			if (template == null) {
				continue;
			}
			for (int col = 3; col-- > 0; ) {
				final int slot = getResourceSlot(row, col);
				ItemStack ingredient = _inventory[slot];
				if (spareResources[row] <= 0 && !UtilInventory.stacksEqual(template, ingredient)) {
					continue;
				}

				ItemStack newPotion = this.getPotionResult(current, template);

				if (newPotion != null) {
					_inventory[getProcessSlot(row + 1)] = newPotion;
				} else {
					_inventory[getProcessSlot(row + 1)] = current;
				}

				_inventory[getProcessSlot(row)] = null;

				if (current == newPotion)
					break;

				consumeIngredient(ingredient, row, col, slot);

				break;
			}
		}
		return true;
	}

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
	}

	private boolean canBrew(int row) {

		ItemStack ingredient = _inventory[getTemplateSlot(row)];

		if (ingredient == null) {
			return false;
		}

		if (!BrewingRecipeRegistry.isValidIngredient(ingredient)) {
			return false;
		}

		boolean hasIngredients = spareResources[row] > 0;
		if (!hasIngredients)
			for (int i = 0; i < 3; i++) {
				if (ingredientsEqual(ingredient, _inventory[getResourceSlot(row, i)])) {
					hasIngredients = true;
					break;
				}
			}
		if (!hasIngredients) {
			return false;
		}

		ItemStack existingPotion = _inventory[getProcessSlot(row)];
		if (existingPotion != null) {
			ItemStack newPotion = this.getPotionResult(existingPotion, ingredient);

			if (newPotion != null) {
				return existingPotion != newPotion;
			}
		}

		return false;
	}

	private ItemStack getPotionResult(ItemStack existingPotion, ItemStack ingredient) {

		if (ingredient == null || !BrewingRecipeRegistry.isValidIngredient(ingredient)) {
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
	public boolean canInsertItem(int slot, ItemStack itemstack, EnumFacing side) {

		int row = slot / 5;
		int column = slot % 5;

		if (itemstack == null)
			return false;
		if (slot == 31)
			return itemstack.getItem().equals(Items.GLASS_BOTTLE);
		if (row == 6)
			return false;
		if (column == 1)
			return false;

		ItemStack template = _inventory[getTemplateSlot(row)];
		if (column == 0)
			return template != null && BrewingRecipeRegistry.hasOutput(itemstack, template) &&
					(row == 0 || _inventory[getTemplateSlot(row - 1)] == null);
		return ingredientsEqual(template, itemstack);
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack itemstack, EnumFacing side) {

		int row = slot / 5;
		int column = slot % 5;

		if (row == 6)
			return slot != 31;
		if (column == 1)
			return false;
		if (column == 0)
			return _inventory[getTemplateSlot(row)] == null;
		return !ingredientsEqual(_inventory[getTemplateSlot(row)], itemstack);
	}

	private boolean ingredientsEqual(ItemStack template, ItemStack ingredient) {

		if (template == null || ingredient == null || !BrewingRecipeRegistry.isValidIngredient(template)) {
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
	public void setInventorySlotContents(int slot, ItemStack itemstack) {

		if (!shouldDropSlotWhenBroken(slot)) {
			if (itemstack != null) {
				itemstack = itemstack.copy();
				itemstack.stackSize = 1;
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
			if (!ingredientsEqual(_inventory[slot], itemstack)) {
				spareResources[slot / 5] = 0;
			}
		}
		super.setInventorySlotContents(slot, itemstack);
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
	public boolean allowBucketDrain(EnumFacing facing, ItemStack stack) {

		return !stack.getItem().equals(Items.GLASS_BOTTLE);
	}

	@Override
	public boolean allowBucketFill(EnumFacing facing, ItemStack stack) {

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
}
