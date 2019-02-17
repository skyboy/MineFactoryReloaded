package powercrystals.minefactoryreloaded.tile.machine.enchantment;

import cofh.core.fluid.FluidTankCore;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import powercrystals.minefactoryreloaded.gui.client.GuiAutoAnvil;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryInventory;
import powercrystals.minefactoryreloaded.gui.container.ContainerAutoAnvil;
import powercrystals.minefactoryreloaded.gui.container.ContainerFactoryPowered;
import powercrystals.minefactoryreloaded.setup.MFRFluids;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryPowered;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

public class TileEntityAutoAnvil extends TileEntityFactoryPowered {

	private float maximumCost;
	private int stackSizeToBeUsedInRepair;
	private boolean repairOnly;

	@Nonnull
	private ItemStack _output = ItemStack.EMPTY;

	public TileEntityAutoAnvil() {

		super(Machine.AutoAnvil);
		setManageSolids(true);
		_tanks[0].setLock(MFRFluids.getFluid("mob_essence"));
	}

	@Override
	public int getSizeInventory() {

		return 3;
	}

	@Override
	public boolean canInsertItem(int slot, @Nonnull ItemStack stack, EnumFacing side) {

		if (stack.isEmpty()) return false;
		Item item = stack.getItem();
		if (repairOnly) {
			if (slot == 0) return item.isRepairable();
			if (slot == 1 && !_inventory.get(0).isEmpty() && item.isRepairable())
				return _inventory.get(0).getItem().equals(item);
			return false;
		}
		if (slot == 0) return (item.isEnchantable(stack) || item.equals(Items.ENCHANTED_BOOK)) || item.isRepairable();
		if (slot == 1 && !_inventory.get(0).isEmpty()) {
			if (item.equals(Items.ENCHANTED_BOOK) && !ItemEnchantedBook.getEnchantments(stack).hasNoTags())
				return true;
			return (item.equals(_inventory.get(0).getItem()) &&
					stack.isItemStackDamageable() && item.isRepairable()) ||
					_inventory.get(0).getItem().getIsRepairable(_inventory.get(0), stack);
		}
		return false;
	}

	@Override
	public boolean canExtractItem(int slot, @Nonnull ItemStack itemstack, EnumFacing side) {

		if (slot == 2) return true;
		return false;
	}

	@Override
	public int getInventoryStackLimit() {

		return 1;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiFactoryInventory getGui(InventoryPlayer inventoryPlayer) {

		return new GuiAutoAnvil(getContainer(inventoryPlayer), this);
	}

	@Override
	public ContainerFactoryPowered getContainer(InventoryPlayer inventoryPlayer) {

		return new ContainerAutoAnvil(this, inventoryPlayer);
	}

	@Override
	protected boolean activateMachine() {

		if (_output.isEmpty() || !_inventory.get(2).isEmpty()) {
			return false;
		} else {
			if (repairOnly) {
				if (!_inventory.get(0).isEmpty() && !_inventory.get(1).isEmpty() &&
						_inventory.get(0).getItem().equals(_inventory.get(1).getItem()) &&
						_inventory.get(0).getItem().isRepairable()) {
					if (!incrementWorkDone()) return false;

					if (getWorkDone() >= getWorkMax()) {
						_inventory.set(0, ItemStack.EMPTY);
						_inventory.set(1, ItemStack.EMPTY);
						_inventory.set(2, _output);

						setWorkDone(0);
						_output = ItemStack.EMPTY;
					}
					return true;
				}

				return false;
			}

			if (drain(4, false, _tanks[0]) != 4) {
				return false;
			}
			if (stackSizeToBeUsedInRepair > 0 && (_inventory.get(1).isEmpty() || _inventory.get(1).getCount() < stackSizeToBeUsedInRepair)) {
				return false;
			}

			drain(4, true, _tanks[0]);
			if (!incrementWorkDone()) return false;

			if (getWorkDone() >= getWorkMax()) {
				_inventory.set(0, ItemStack.EMPTY);
				_inventory.set(2, _output);

				if (stackSizeToBeUsedInRepair > 0 && _inventory.get(1).getCount() > stackSizeToBeUsedInRepair) {
					_inventory.get(1).shrink(stackSizeToBeUsedInRepair);
				} else {
					_inventory.set(1, ItemStack.EMPTY);
				}

				setWorkDone(0);
				_output = ItemStack.EMPTY;
			}

			return true;
		}
	}

	@Override
	protected void onFactoryInventoryChanged() {

		super.onFactoryInventoryChanged();

		_output = getAnvilOutput();
		if (!_output.isEmpty()) {
			setIdleTicks(getIdleTicksMax());
		}
		setWorkDone(0);
	}

	@Nonnull
	public ItemStack getRepairOutput() {

		return _output;
	}

	@Nonnull
	private ItemStack getAnvilOutput() {

		@Nonnull ItemStack startingItem = _inventory.get(0);
		this.maximumCost = 0;
		int totalEnchCost = 0;

		if (startingItem.isEmpty()) {
			return ItemStack.EMPTY;
		} else if (repairOnly) {
			this.stackSizeToBeUsedInRepair = 0;
			@Nonnull ItemStack addedItem = _inventory.get(1);
			Item item = startingItem.getItem();

			if (!addedItem.isEmpty() && item.isRepairable() &&
					startingItem.getItem() == addedItem.getItem()) {
				int d = item.getMaxDamage();
				int k = startingItem.getItemDamage();
				int l = addedItem.getItemDamage();
				int i1 = (d - k) + (d - l) + (d / 10);
				int j1 = Math.max(d - i1, 0);

				this.maximumCost = ((k + l) / 2 - j1) / 100f;

				return new ItemStack(startingItem.getItem(), 1, j1);
			}
			return ItemStack.EMPTY;
		} else {
			@Nonnull ItemStack outputItem = startingItem.copy();
			@Nonnull ItemStack addedItem = _inventory.get(1);

			Map<Enchantment, Integer> existingEnchantments = EnchantmentHelper.getEnchantments(outputItem);

			if (outputItem.getItem().equals(Items.BOOK))
				outputItem = new ItemStack(Items.ENCHANTED_BOOK);

			boolean enchantingWithBook = false;
			int repairCost = outputItem.getRepairCost() + (addedItem.isEmpty() ? 0 : addedItem.getRepairCost());
			this.stackSizeToBeUsedInRepair = 0;

			if (!addedItem.isEmpty()) {
				{ // anvil event (canceled: don't anvil; output != null: item & cost calculated by listener)
					AnvilUpdateEvent e = new AnvilUpdateEvent(outputItem.copy(), addedItem.copy(), "", repairCost);
					if (MinecraftForge.EVENT_BUS.post(e)) return ItemStack.EMPTY;
					if (!e.getOutput().isEmpty()) {
						maximumCost = e.getCost();
						return e.getOutput();
					}
				}
				{ // eclipse is indenting weird again
					enchantingWithBook = addedItem.getItem().equals(Items.ENCHANTED_BOOK) &&
							!ItemEnchantedBook.getEnchantments(addedItem).hasNoTags();
				}
				int addedEnchants = 0;

				if (outputItem.isItemStackDamageable() &&
						outputItem.getItem().getIsRepairable(outputItem, addedItem)) {
					int currentDamage = Math.min(outputItem.getItemDamage(), outputItem.getMaxDamage() / 4);

					if (currentDamage <= 0) {
						return ItemStack.EMPTY;
					}

					int repairStackSize = 0;
					for (; currentDamage > 0 && repairStackSize < addedItem.getCount(); repairStackSize++) {
						outputItem.setItemDamage(outputItem.getItemDamage() - currentDamage);
						totalEnchCost += Math.max(1, currentDamage / 100) + existingEnchantments.size();
						currentDamage = Math.min(outputItem.getItemDamage(), outputItem.getMaxDamage() / 4);
					}

					this.stackSizeToBeUsedInRepair = repairStackSize;
					if (repairStackSize > 0)
						++addedEnchants;
				} else {
					if (!enchantingWithBook && (!outputItem.getItem().equals(addedItem.getItem()) ||
							!outputItem.isItemStackDamageable())) {
						return ItemStack.EMPTY;
					}

					if (outputItem.isItemStackDamageable() && !enchantingWithBook) {
						int currentDamage = outputItem.getMaxDamage() - outputItem.getItemDamage();
						int addedItemDamage = addedItem.getMaxDamage() - addedItem.getItemDamage();
						int newDamage = addedItemDamage + outputItem.getMaxDamage() * 12 / 100;
						int leftoverDamage = currentDamage + newDamage;
						int repairedDamage = outputItem.getMaxDamage() - leftoverDamage;

						if (repairedDamage < 0) {
							repairedDamage = 0;
						}

						if (repairedDamage < outputItem.getItemDamage()) {
							++addedEnchants;
							outputItem.setItemDamage(repairedDamage);
							totalEnchCost += Math.max(1, newDamage / 100);
						}
					}

					Map<Enchantment, Integer> addedEnchantments = EnchantmentHelper.getEnchantments(addedItem);

					for (Enchantment addedEnchant : addedEnchantments.keySet()) {
						if (addedEnchant == null)
							continue;
						int existingEnchLevel = existingEnchantments.getOrDefault(addedEnchant, 0);
						int addedEnchLevel = addedEnchantments.get(addedEnchant);
						int newEnchLevel;

						if (existingEnchLevel == addedEnchLevel) {
							++addedEnchLevel;
							newEnchLevel = addedEnchLevel;
						} else {
							newEnchLevel = Math.max(addedEnchLevel, existingEnchLevel);
						}

						addedEnchLevel = newEnchLevel;
						int levelDifference = addedEnchLevel - existingEnchLevel;
						boolean canEnchantmentBeAdded = addedEnchant.canApply(outputItem);

						if (outputItem.getItem().equals(Items.ENCHANTED_BOOK)) {
							canEnchantmentBeAdded = true;
						}

						for (Enchantment existingEnchant : existingEnchantments.keySet()) {
							if (!existingEnchant.equals(addedEnchant) && !addedEnchant.isCompatibleWith(existingEnchant)) {
								canEnchantmentBeAdded = false;
								totalEnchCost += levelDifference;
							}
						}

						if (canEnchantmentBeAdded) {
							if (newEnchLevel > addedEnchant.getMaxLevel()) {
								newEnchLevel = addedEnchant.getMaxLevel();
							} else {
								++addedEnchants;
							}

							existingEnchantments.put(addedEnchant, newEnchLevel);
							int enchCost = 0;

							switch (addedEnchant.getRarity()) {
								case COMMON:
									enchCost = 1;
									break;
								case UNCOMMON:
									enchCost = 2;
									break;
								case RARE:
									enchCost = 4;
									break;
								case VERY_RARE:
									enchCost = 8;
							}

							if (enchantingWithBook) {
								enchCost = Math.max(1, enchCost / 2);
							}

							totalEnchCost += enchCost * levelDifference;
						}
					}
				}

				if (addedEnchants == 0) {
					totalEnchCost = 0;
				}
			}

			int enchCount = 0;

			for (Enchantment existingEnchant : existingEnchantments.keySet()) {
				int existingEnchLevel = existingEnchantments.get(existingEnchant);
				int enchCost = 0;
				++enchCount;

				switch (existingEnchant.getRarity()) {
					case COMMON:
						enchCost = 1;
						break;
					case UNCOMMON:
						enchCost = 2;
						break;
					case RARE:
						enchCost = 4;
						break;
					case VERY_RARE:
						enchCost = 8;
				}

				if (enchantingWithBook) {
					enchCost = Math.max(1, enchCost / 2);
				}
				repairCost += enchCount + existingEnchLevel * enchCost;
			}

			if (enchantingWithBook) {
				repairCost = Math.max(1, repairCost / 2);
			}

			if (enchantingWithBook && !outputItem.getItem().isBookEnchantable(outputItem, addedItem)) {
				outputItem = ItemStack.EMPTY;
			}

			this.maximumCost = repairCost + totalEnchCost;

			if (totalEnchCost <= 0) {
				outputItem = ItemStack.EMPTY;
			}

			if (!outputItem.isEmpty()) {
				int newRepairCost = outputItem.getRepairCost();

				if (!addedItem.isEmpty() && newRepairCost < addedItem.getRepairCost()) {
					newRepairCost = addedItem.getRepairCost();
				}
				if (totalEnchCost > (outputItem.hasDisplayName() ? 1 : 0)) {
					// vanilla parity: named items immune to small changes
					newRepairCost = newRepairCost * 2 + 1;
				}

				if (newRepairCost > 0) {
					// vanilla enhancement: don't write a 0. if it's not there, it's a 0 anyway.
					outputItem.setRepairCost(newRepairCost);
				}
				EnchantmentHelper.setEnchantments(existingEnchantments, outputItem);
			}

			return outputItem;
		}
	}

	@Override
	public void writePortableData(EntityPlayer player, NBTTagCompound tag) {

		tag.setBoolean("repairOnly", repairOnly);
	}

	@Override
	public void readPortableData(EntityPlayer player, NBTTagCompound tag) {

		setRepairOnly(tag.getBoolean("repairOnly"));
	}

	@Override
	public void writeItemNBT(NBTTagCompound tag) {

		super.writeItemNBT(tag);
		if (repairOnly)
			tag.setBoolean("repairOnly", repairOnly);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {

		super.readFromNBT(tag);
		repairOnly = tag.getBoolean("repairOnly");
	}

	public boolean getRepairOnly() {

		return repairOnly;
	}

	public void setRepairOnly(boolean v) {

		repairOnly = v;
		onFactoryInventoryChanged();
	}

	@Override
	public int getWorkMax() {

		return (int) (100f * maximumCost);
	}

	@Override
	public int getIdleTicksMax() {

		return 30;
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
	protected boolean canDrainTank(EnumFacing facing, int index) {

		return false;
	}

	@Override
	public boolean allowBucketFill(EnumFacing facing, @Nonnull ItemStack stack) {

		return true;
	}

	@Override
	protected FluidTankCore[] createTanks() {

		return new FluidTankCore[] { new FluidTankCore(4 * BUCKET_VOLUME) };
	}

}
