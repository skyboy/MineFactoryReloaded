package powercrystals.minefactoryreloaded.tile.machine.routing;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import powercrystals.minefactoryreloaded.core.IEntityCollidable;
import powercrystals.minefactoryreloaded.core.UtilInventory;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryInventory;
import powercrystals.minefactoryreloaded.gui.client.GuiItemRouter;
import powercrystals.minefactoryreloaded.gui.container.ContainerFactoryInventory;
import powercrystals.minefactoryreloaded.gui.container.ContainerItemRouter;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryTickable;

import javax.annotation.Nonnull;

public class TileEntityItemRouter extends TileEntityFactoryTickable implements IEntityCollidable {

	private boolean _routing = false;

	private boolean _rejectUnmapped;

	protected static final int[] _invOffsets = new int[] { 0, 0, 9, 18, 36, 27 };
	protected static final EnumFacing[] _outputDirections = new EnumFacing[] { EnumFacing.DOWN, EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.EAST,
			EnumFacing.WEST };

	private int[] _defaultRoutes = new int[_outputDirections.length];

	public TileEntityItemRouter() {

		this(Machine.ItemRouter);
	}

	public TileEntityItemRouter(Machine machine) {

		super(machine);
		setManageSolids(true);
	}

	public boolean getRejectUnmapped() {

		return _rejectUnmapped;
	}

	public void setRejectUnmapped(boolean rejectUnmapped) {

		_rejectUnmapped = rejectUnmapped;
	}

	@Override
	public void update() {

		super.update();
		if (!world.isRemote) {
			for (int i = 45; i < getSizeInventory(); i++) {
				if (!_inventory.get(i).isEmpty()) {
					_inventory.set(i, routeItem(_inventory.get(i)));
				}
			}
		}
	}

	@Override
	public void onEntityCollided(Entity entity) {

		if (entity instanceof EntityItem && !entity.isDead) {
			@Nonnull ItemStack s = routeItem(((EntityItem) entity).getItem());
			if (s.isEmpty())
				entity.setDead();
			else
				((EntityItem) entity).setItem(s);
		}
	}

	@Nonnull
	private ItemStack routeItem(@Nonnull ItemStack stack) {

		int[] filteredRoutes = getRoutesForItem(stack);

		_routing = true;
		if (hasRoutes(filteredRoutes)) {
			stack = weightedRouteItem(stack, filteredRoutes);
			stack = (stack.isEmpty() || stack.getCount() <= 0) ? ItemStack.EMPTY : stack;
		}
		else if (!_rejectUnmapped && hasRoutes(_defaultRoutes)) {
			stack = weightedRouteItem(stack, _defaultRoutes);
			stack = (stack.isEmpty() || stack.getCount() <= 0) ? ItemStack.EMPTY : stack;
		}
		_routing = false;
		return stack;
	}

	@Nonnull
	private ItemStack weightedRouteItem(@Nonnull ItemStack stack, int[] routes) {

		@Nonnull ItemStack remainingOverall = stack.copy();
		int weight = totalWeight(routes);
		if (stack.getCount() >= weight) {
			int startingAmount = stack.getCount();
			for (int i = 0; i < routes.length; i++) {
				@Nonnull ItemStack stackForThisRoute = stack.copy();
				stackForThisRoute.setCount(startingAmount * routes[i] / weight);
				if (stackForThisRoute.getCount() > 0) {
					@Nonnull ItemStack remainingFromThisRoute = UtilInventory.dropStack(this, stackForThisRoute, _outputDirections[i], _outputDirections[i]);
					if (remainingFromThisRoute.isEmpty()) {
						remainingOverall.shrink(stackForThisRoute.getCount());
					}
					else {
						remainingOverall.shrink((stackForThisRoute.getCount() - remainingFromThisRoute.getCount()));
					}

					if (remainingOverall.getCount() <= 0) {
						break;
					}
				}
			}
		}

		if (0 < remainingOverall.getCount() && remainingOverall.getCount() < totalWeight(routes)) {
			int outDir = weightedRandomSide(routes);
			remainingOverall = UtilInventory.dropStack(this, remainingOverall, _outputDirections[outDir], _outputDirections[outDir]);
		}
		return remainingOverall;
	}

	private int weightedRandomSide(int[] routeWeights) {

		int random = world.rand.nextInt(totalWeight(routeWeights));
		for (int i = 0; i < routeWeights.length; i++) {
			random -= routeWeights[i];
			if (random < 0)
				return i;
		}

		return -1;
	}

	private int totalWeight(int[] routeWeights) {

		int total = 0;

		for (int weight : routeWeights)
			total += weight;
		return total;
	}

	private boolean hasRoutes(int[] routeWeights) {

		for (int weight : routeWeights)
			if (weight > 0) return true;

		return false;
	}

	protected int[] getRoutesForItem(@Nonnull ItemStack stack) {

		int[] routeWeights = new int[_outputDirections.length];

		Item item = stack.getItem();

		for (int i = 0; i < _outputDirections.length; i++) {
			int sideStart = _invOffsets[_outputDirections[i].ordinal()];
			routeWeights[i] = 0;
			for (int j = sideStart; j < sideStart + 9; j++) {
				if (!_inventory.get(j).isEmpty()) {
					if (_inventory.get(j).getItem().equals(item) &&
							(stack.isItemStackDamageable() ||
							_inventory.get(j).getItemDamage() == stack.getItemDamage())) {
						routeWeights[i] += _inventory.get(j).getCount();
					}
				}
			}
		}
		return routeWeights;
	}

	private void recalculateDefaultRoutes() {

		for (int i = 0; i < _outputDirections.length; i++)
			_defaultRoutes[i] = isSideEmpty(_outputDirections[i]) ? 1 : 0;
	}

	public boolean hasRouteForItem(@Nonnull ItemStack stack) {

		return hasRoutes(getRoutesForItem(stack));
	}

	private boolean isSideEmpty(EnumFacing side) {

		if (side == null || side == EnumFacing.UP) {
			return false;
		}

		int sideStart = _invOffsets[side.ordinal()];

		for (int i = sideStart; i < sideStart + 9; i++) {
			if (!_inventory.get(i).isEmpty()) {
				return false;
			}
		}

		return true;
	}

	@Override
	public int getSizeInventory() {

		return 48;
	}

	@Override
	public boolean shouldDropSlotWhenBroken(int slot) {

		return slot >= 45;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiFactoryInventory getGui(InventoryPlayer inventoryPlayer) {

		return new GuiItemRouter(getContainer(inventoryPlayer), this);
	}

	@Override
	public ContainerFactoryInventory getContainer(InventoryPlayer inventoryPlayer) {

		return new ContainerItemRouter(this, inventoryPlayer);
	}

	@Override
	public int getInventoryStackLimit() {

		return 64;
	}

	@Override
	public int getStartInventorySide(EnumFacing side) {

		return 45;
	}

	@Override
	public int getSizeInventorySide(EnumFacing side) {

		return 3;
	}

	@Override
	public void setInventorySlotContents(int i, @Nonnull ItemStack stack) {

		if (world != null && !world.isRemote) {
			int start = getStartInventorySide(null);
			if (i >= start && i <= (start + getSizeInventorySide(null))) {
				l: if (!stack.isEmpty()) {
					if (stack.getCount() <= 0) {
						stack = ItemStack.EMPTY;
						break l;
					}
					stack = routeItem(stack);
					if (!stack.isEmpty())
						if (stack.getCount() > getInventoryStackLimit()) {
							stack.setCount(getInventoryStackLimit());
						}
				}
				_inventory.set(i, stack);
				return;
			}
		}
		super.setInventorySlotContents(i, stack);
	}

	@Override
	public boolean canInsertItem(int slot, @Nonnull ItemStack itemstack, EnumFacing side) {

		return !_routing;
	}

	@Override
	public boolean isItemValidForSlot(int slot, @Nonnull ItemStack itemstack) {

		return !_routing;
	}

	@Override
	public boolean canExtractItem(int slot, @Nonnull ItemStack itemstack, EnumFacing side) {

		return false;
	}

	@Override
	protected void onFactoryInventoryChanged() {

		super.onFactoryInventoryChanged();
		recalculateDefaultRoutes();
	}

	@Override
	public void writePortableData(EntityPlayer player, NBTTagCompound tag) {

		tag.setBoolean("rejectUnmapped", _rejectUnmapped);
	}

	@Override
	public void readPortableData(EntityPlayer player, NBTTagCompound tag) {

		_rejectUnmapped = tag.getBoolean("rejectUnmapped");
		recalculateDefaultRoutes();
	}

	@Override
	public void writeItemNBT(NBTTagCompound tag) {

		super.writeItemNBT(tag);
		if (_rejectUnmapped)
			tag.setBoolean("rejectUnmapped", _rejectUnmapped);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {

		super.readFromNBT(tag);
		_rejectUnmapped = tag.getBoolean("rejectUnmapped");
		recalculateDefaultRoutes();
	}

}
