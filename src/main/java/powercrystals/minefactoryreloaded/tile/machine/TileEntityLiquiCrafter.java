package powercrystals.minefactoryreloaded.tile.machine;

import cofh.core.fluid.FluidTankCore;
import cofh.core.util.CoreUtils;
import cofh.core.util.helpers.ItemHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import powercrystals.minefactoryreloaded.core.MFRUtil;
import powercrystals.minefactoryreloaded.core.RemoteInventoryCrafting;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryInventory;
import powercrystals.minefactoryreloaded.gui.client.GuiLiquiCrafter;
import powercrystals.minefactoryreloaded.gui.container.ContainerLiquiCrafter;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryTickable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

// slots 0-8 craft grid, 9 craft grid template output, 10 output, 11-28 resources
public class TileEntityLiquiCrafter extends TileEntityFactoryTickable {

	private boolean _lastRedstoneState;
	private boolean _resourcesChangedSinceLastFailedCraft = true;

	protected RemoteInventoryCrafting craft = new RemoteInventoryCrafting();
	protected IRecipe recipe;
	protected ArrayList<ItemStack> outputs = new ArrayList<>();
	private List<ItemResourceTracker> requiredItems = new LinkedList<>();

	public TileEntityLiquiCrafter() {

		super(Machine.LiquiCrafter);
		setManageSolids(true);
	}

	@Override
	public boolean shouldDropSlotWhenBroken(int slot) {

		return slot > 9;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiFactoryInventory getGui(InventoryPlayer inventoryPlayer) {

		return new GuiLiquiCrafter(getContainer(inventoryPlayer), this);
	}

	@Override
	public ContainerLiquiCrafter getContainer(InventoryPlayer inventoryPlayer) {

		return new ContainerLiquiCrafter(this, inventoryPlayer);
	}

	@Override
	public void update() {

		super.update();
		if (world.isRemote)
			return;

		{
			int s = outputs.size();
			if (s > 0) {
				if (_inventory.get(10).isEmpty()) {
					_inventory.set(10, outputs.get(--s));
					outputs.remove(s);
				}
				return;
			}
		}

		boolean redstoneState = _rednetState != 0 || CoreUtils.isRedstonePowered(this);

		if (redstoneState && !_lastRedstoneState) {
			if (_resourcesChangedSinceLastFailedCraft && recipe != null &&
					!_inventory.get(9).isEmpty() &&
					(_inventory.get(10).isEmpty() ||
							(_inventory.get(10).getCount() + _inventory.get(9).getCount() <= _inventory.get(9).getMaxStackSize() &&
									ItemHelper.itemsEqualWithMetadata(_inventory.get(9), _inventory.get(10), true))))
				checkResources();
		}

		_lastRedstoneState = redstoneState;
	}

	@Override
	public boolean hasDrops() {

		return outputs.size() != 0;
	}

	private void checkResources() {

		List<ItemResourceTracker> requiredItems = this.requiredItems;
		requiredItems.clear();

		/**
		 * Tracking
		 */
		for (int i = 0; i < 9; i++) {
			if (!_inventory.get(i).isEmpty()) {
				FluidStack l = MFRUtil.getFluidContents(_inventory.get(i));
				if (l != null) {
					ItemResourceTracker t = new ItemResourceTracker(i, l, l.amount);
					t.item = _inventory.get(i);
					requiredItems.add(t);
					continue;
				}

				requiredItems.add(new ItemResourceTracker(i, _inventory.get(i), 1));
			}
		}

		/**
		 * Checking
		 */
		for (int i = 11; i < 29; i++) {
			@Nonnull ItemStack item = _inventory.get(i);
			if (!item.isEmpty()) {
				int size = item.getCount();
				for (ItemResourceTracker t : requiredItems) {
					if (t.fluid != null && t.fluid.isFluidEqual(MFRUtil.getFluidContents(item))) {
						int a = MFRUtil.getFluidContents(item).amount;
						int f = Math.min(a * size, t.required - t.found);
						t.found += f;
						size -= (int) Math.ceil(f / (float) a);
					} else if (ItemHelper.itemsEqualForCrafting(t.item, item)) {
						int f = Math.min(size, t.required - t.found);
						t.found += f;
						size -= f;
					}
					if (size <= 0)
						break;
				}
			}
		}

		for (FluidTankCore _tank : _tanks) {
			FluidStack l = _tank.getFluid();
			if (l == null || l.amount == 0)
				continue;

			int amt = l.amount;
			for (ItemResourceTracker t : requiredItems) {
				if (l.isFluidEqual(t.fluid)) {
					t.found += Math.min(amt, t.required - t.found);
					amt -= t.found;
					if (amt <= 0)
						break;
				}
			}
		}

		/**
		 * Abort if check failed
		 */
		for (ItemResourceTracker t : requiredItems) {
			if (t.found < t.required) {
				_resourcesChangedSinceLastFailedCraft = false;
				return;
			}
		}

		/**
		 * Consuming
		 */
		// TODO: this stage needs broken apart; cloning into the <tt>craft</tt> object, then getCraftingResult, then call IRecipe.getRemainingItems
		// afterwards we can then consume items and process the outputs correctly; extra outputs should be done after the main crafting output logic to ensure ordering consistency
		for (int i = 11; i < 29; i++) {
			@Nonnull ItemStack item = _inventory.get(i);
			if (!item.isEmpty()) {
				for (ItemResourceTracker t : requiredItems) {
					boolean fluid = t.fluid != null &&
							t.fluid.isFluidEqual(MFRUtil.getFluidContents(item));
					if (fluid || ItemHelper.itemsEqualForCrafting(t.item, item)) {
						int use = 0;
						if (fluid) {
							use = MFRUtil.getFluidContents(item).amount;
						}
						if (item.getItem().hasContainerItem(item)) {
							if (!fluid)
								use = 1;
							@Nonnull ItemStack container = item.getItem().getContainerItem(_inventory.get(i));
							boolean nul = true;
							l:
							{
								if (container.isEmpty())
									break l;
								if (!container.isItemStackDamageable() ||
										container.getItemDamage() <= container.getMaxDamage()) {
									_inventory.set(i, container);
									nul = false;
								}
							}
							if (nul)
								_inventory.set(i, ItemStack.EMPTY);
						} else if (fluid) {
							int use2 = Math.min((int) Math.ceil(t.required / (float) use), item.getCount());
							item.shrink(use2);
							use = Math.min(use * use2, t.required);
						} else {
							use = Math.min(t.required, item.getCount());
							item.shrink(use);
						}
						t.required -= use;

						if (item.getCount() <= 0)
							_inventory.set(i, ItemStack.EMPTY);

						if (t.required == 0) {
							craft.setInventorySlotContents(t.slot, ItemHelper.cloneStack(item, use));
							requiredItems.remove(t);
							--i;
							break;
						}
					}
				}
			}
		}

		for (int i = 0; i < _tanks.length; i++) {
			FluidStack l = _tanks[i].getFluid();
			if (l == null || l.amount == 0)
				continue;

			for (ItemResourceTracker t : requiredItems) {
				if (t.required != 0 && l.isFluidEqual(t.fluid)) {
					int use = Math.min(t.required, l.amount);
					_tanks[i].drain(use, true);
					t.required -= use;

					if (t.required == 0) {
						craft.setInventorySlotContents(t.slot, ItemHelper.cloneStack(_inventory.get(t.slot)));
						requiredItems.remove(t);
						--i;
						break;
					}
				}
			}
		}

		/**
		 * Crafting
		 */
		try {
			_inventory.set(9, recipe.getCraftingResult(craft));
		} catch (Throwable t) {
			if (recipe.matches(craft, world))
				_inventory.set(9, recipe.getCraftingResult(craft));
		}

		if (_inventory.get(9).isEmpty())
			return;

		if (_inventory.get(10).isEmpty()) {
			_inventory.set(10, ItemHelper.cloneStack(_inventory.get(9)));
		} else {
			if (ItemHelper.itemsEqualWithMetadata(_inventory.get(10), _inventory.get(9), true))
				_inventory.get(10).grow(_inventory.get(9).getCount());
			else
				outputs.add(ItemHelper.cloneStack(_inventory.get(9)));
		}
	}

	private void calculateOutput() {

		_inventory.set(9, findMatchingRecipe());
	}

	@Override
	public int getSizeInventory() {

		return 29;
	}

	@Override
	public void setInventorySlotContents(int slot, @Nonnull ItemStack stack) {

		_inventory.set(slot, stack);
		if (slot < 9)
			calculateOutput();
		onFactoryInventoryChanged();
	}

	@Nonnull
	@Override
	public ItemStack decrStackSize(int slot, int size) {

		@Nonnull ItemStack result = super.decrStackSize(slot, size);
		if (slot < 9)
			calculateOutput();
		onFactoryInventoryChanged();
		return result;
	}

	@Override
	public int getInventoryStackLimit() {

		return 64;
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {

		return player.getDistanceSq(pos) <= 64D;
	}

	@Override
	public int getStartInventorySide(EnumFacing side) {

		return 10;
	}

	@Override
	public int getSizeInventorySide(EnumFacing side) {

		return 19;
	}

	@Override
	public boolean canInsertItem(int slot, @Nonnull ItemStack stack, EnumFacing side) {

		if (slot > 10)
			return true;
		return false;
	}

	@Override
	public boolean canExtractItem(int slot, @Nonnull ItemStack itemstack, EnumFacing side) {

		if (slot == 10)
			return true;
		return false;
	}

	@Override
	protected void onFactoryInventoryChanged() {

		_resourcesChangedSinceLastFailedCraft = true;
		super.onFactoryInventoryChanged();
	}

	@Override
	protected FluidTankCore[] createTanks() {

		FluidTankCore[] _tanks = new FluidTankCore[9];
		for (int i = 0; i < 9; i++) {
			_tanks[i] = new FluidTankCore(BUCKET_VOLUME * 10);
		}
		return _tanks;
	}

	@Override
	public FluidTankInfo[] getTankInfo() {

		FluidTankInfo[] r = new FluidTankInfo[_tanks.length];
		for (int i = _tanks.length; i-- > 0; )
			r[i] = _tanks[i].getInfo();
		return r;
	}

	@Override
	public boolean allowBucketDrain(EnumFacing facing, @Nonnull ItemStack stack) {

		return true;
	}

	@Override
	public boolean allowBucketFill(EnumFacing facing, @Nonnull ItemStack stack) {

		return true;
	}

	@Override
	public int fill(EnumFacing facing, FluidStack resource, boolean doFill) {

		int quantity;
		int match = findFirstMatchingTank(resource);
		if (match >= 0) {
			quantity = _tanks[match].fill(resource, doFill);
			if (quantity > 0)
				_resourcesChangedSinceLastFailedCraft = true;
			return quantity;
		}
		match = findFirstEmptyTank();
		if (match >= 0) {
			quantity = _tanks[match].fill(resource, doFill);
			if (quantity > 0)
				_resourcesChangedSinceLastFailedCraft = true;
			return quantity;
		}
		return 0;
	}

	@Override
	public FluidStack drain(EnumFacing facing, int maxDrain, boolean doDrain) {

		int match = findFirstNonEmptyTank();
		if (match >= 0)
			return _tanks[match].drain(maxDrain, doDrain);
		return null;
	}

	@Override
	public FluidStack drain(EnumFacing facing, FluidStack resource, boolean doDrain) {

		int match = findFirstMatchingTank(resource);
		if (match >= 0)
			return _tanks[match].drain(resource.amount, doDrain);
		return null;
	}

	private int findFirstEmptyTank() {

		for (int i = 0; i < 9; i++) {
			if (_tanks[i].getFluid() == null || _tanks[i].getFluid().amount == 0) {
				return i;
			}
		}

		return -1;
	}

	private int findFirstNonEmptyTank() {

		for (int i = 0; i < 9; i++) {
			if (_tanks[i].getFluid() != null && _tanks[i].getFluid().amount > 0) {
				return i;
			}
		}

		return -1;
	}

	private int findFirstMatchingTank(FluidStack liquid) {

		if (liquid == null) {
			return -1;
		}

		for (int i = 0; i < 9; i++) {
			if (liquid.isFluidEqual(_tanks[i].getFluid())) {
				return i;
			}
		}

		return -1;
	}

	@Nonnull
	private ItemStack findMatchingRecipe() {

		for (int i = 0; i < 9; i++) {
			craft.setInventorySlotContents(i, (_inventory.get(i).isEmpty() ? ItemStack.EMPTY : _inventory.get(i).copy()));
		}

		Collection<IRecipe> recipes = ForgeRegistries.RECIPES.getValuesCollection();
		for (IRecipe irecipe : recipes) {
			if (irecipe.matches(craft, world)) {
				recipe = irecipe;
				return irecipe.getCraftingResult(craft);
			}
		}

		recipe = null;
		return ItemStack.EMPTY;
	}

	@Override
	public void writePortableData(EntityPlayer player, NBTTagCompound tag) {

	}

	@Override
	public void readPortableData(EntityPlayer player, NBTTagCompound tag) {

		// TODO: save/write recipe
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {

		tag = super.writeToNBT(tag);

		if (outputs.size() != 0) {
			NBTTagList dropItems = new NBTTagList();
			for (@Nonnull ItemStack item : outputs) {
				NBTTagCompound tagCompound = new NBTTagCompound();
				item.writeToNBT(tagCompound);
				dropItems.appendTag(tagCompound);
			}
			if (dropItems.tagCount() > 0)
				tag.setTag("OutItems", dropItems);
		}

		return tag;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {

		super.readFromNBT(tag);
		calculateOutput();

		if (tag.hasKey("OutItems")) {
			ArrayList<ItemStack> drops = new ArrayList<>();
			NBTTagList nbttaglist = tag.getTagList("OutItems", 10);
			for (int i = nbttaglist.tagCount(); i-- > 0; ) {
				NBTTagCompound tagCompound = nbttaglist.getCompoundTagAt(i);
				@Nonnull ItemStack item = new ItemStack(tagCompound);
				if (!item.isEmpty() && item.getCount() > 0) {
					drops.add(item);
				}
			}
			outputs = drops;
		}
	}

	private static class ItemResourceTracker {

		ItemResourceTracker(int s, @Nonnull ItemStack stack, int amt) {

			slot = s;
			item = stack;
			required = amt;
		}

		ItemResourceTracker(int s, FluidStack resource, int amt) {

			slot = s;
			fluid = resource;
			required = amt;
		}

		public FluidStack fluid;
		@Nonnull
		public ItemStack item = ItemStack.EMPTY;
		public int required;
		public int found;
		public int slot;

		@Override
		public String toString() {

			return "Slot: " + slot + "; Fluid: " + fluid + "; Item: " + item + "; Required: " + required + "; Found: " + found;
		}
	}

}
