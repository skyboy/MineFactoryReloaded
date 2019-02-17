package powercrystals.minefactoryreloaded.tile.machine.processing;

import cofh.core.fluid.FluidTankCore;
import cofh.core.util.CoreUtils;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.api.plant.IFactoryPlantable;
import powercrystals.minefactoryreloaded.core.UtilInventory;
import powercrystals.minefactoryreloaded.gui.client.GuiBioReactor;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryInventory;
import powercrystals.minefactoryreloaded.gui.container.ContainerBioReactor;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryTickable;

import javax.annotation.Nonnull;
import java.util.Map;

public class TileEntityBioReactor extends TileEntityFactoryTickable {

	private int _burnTime;
	private static final int _burnTimeMax = 8000;
	private static final int _bioFuelPerTick = 1;
	private static final int _burnTimeDecreasePerTick = 1;

	// start at 0 for 0 slots; increase by 5, then an additional 10 each time (upward-sloping curve)
	private static final int[] _outputValues = { 0, 5, 25, 70, 150, 275, 455, 700, 1020, 1425 };

	public TileEntityBioReactor() {

		super(Machine.BioReactor);
		setManageSolids(true);
	}

	public int getBurnTime() {

		return _burnTime;
	}

	public void setBurnTime(int burnTime) {

		_burnTime = burnTime;
	}

	public int getBurnTimeMax() {

		return _burnTimeMax;
	}

	public int getOutputValue() {

		int occupiedSlots = 0;
		for (int i = 9; i < 18; i++) {
			if (!_inventory.get(i).isEmpty()) {
				occupiedSlots++;
			}
		}

		return _outputValues[occupiedSlots];
	}

	public int getOutputValueMax() {

		return _outputValues[9];
	}

	@Override
	public void update() {

		super.update();

		if (!world.isRemote) {
			Map<Item, IFactoryPlantable> plantables = MFRRegistry.getPlantables();
			for (int i = 0; i < 9; i++) {
				@Nonnull ItemStack item = _inventory.get(i);
				if (item.isEmpty())
					continue;
				if (plantables.containsKey(item.getItem()) &&
						plantables.get(item.getItem()).canBePlanted(item, true)) {
					int targetSlot = findMatchingSlot(_inventory.get(i));
					if (targetSlot < 0)
						continue;

					if (_inventory.get(targetSlot).isEmpty()) {
						_inventory.set(targetSlot, _inventory.get(i));
						_inventory.set(i, ItemStack.EMPTY);
					} else {
						UtilInventory.mergeStacks(_inventory.get(targetSlot), _inventory.get(i));
						if (_inventory.get(i).getCount() <= 0)
							_inventory.set(i, ItemStack.EMPTY);
					}
				}
			}

			if (_rednetState != 0 || CoreUtils.isRedstonePowered(this))
				return;

			int newBurn = getOutputValue();
			if (_burnTimeMax - _burnTime >= newBurn) {
				_burnTime += newBurn;
				for (int i = 9; i < 18; i++)
					if (!_inventory.get(i).isEmpty())
						decrStackSize(i, 1);
			}

			if (_burnTime > 0 && _tanks[0].getFluidAmount() <= _tanks[0].getCapacity() - _bioFuelPerTick) {
				_burnTime -= _burnTimeDecreasePerTick;
				_tanks[0].fill(FluidRegistry.getFluidStack("biofuel", _bioFuelPerTick), true);
			}
		}
	}

	private int findMatchingSlot(@Nonnull ItemStack s) {

		int emptySlot = -1;
		for (int i = 9; i < 18; i++) {
			if (_inventory.get(i).isEmpty()) {
				if (emptySlot == -1)
					emptySlot = i;
			} else if (_inventory.get(i).isItemEqual(s)) {
				return i;
			}
		}
		return emptySlot;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiFactoryInventory getGui(InventoryPlayer inventoryPlayer) {

		return new GuiBioReactor(getContainer(inventoryPlayer), this);
	}

	@Override
	public ContainerBioReactor getContainer(InventoryPlayer inventoryPlayer) {

		return new ContainerBioReactor(this, inventoryPlayer);
	}

	@Override
	protected boolean shouldPumpLiquid() {

		return true;
	}

	@Override
	public int getSizeInventory() {

		return 18;
	}

	@Override
	public int getSizeInventorySide(EnumFacing side) {

		return 9;
	}

	@Override
	public boolean canInsertItem(int slot, @Nonnull ItemStack stack, EnumFacing side) {

		if (!stack.isEmpty())
			if (slot < 9) {
				IFactoryPlantable p = MFRRegistry.getPlantables().get(stack.getItem());
				return p != null && p.canBePlanted(stack, true);
			}
		return false;
	}

	@Override
	protected FluidTankCore[] createTanks() {

		return new FluidTankCore[] { new FluidTankCore(4 * BUCKET_VOLUME) };
	}

	@Override
	public void writeItemNBT(NBTTagCompound nbttagcompound) {

		super.writeItemNBT(nbttagcompound);
		if (_burnTime != 0)
			nbttagcompound.setInteger("burnTime", _burnTime);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {

		super.readFromNBT(nbttagcompound);
		_burnTime = nbttagcompound.getInteger("burnTime");
	}

	@Override
	protected boolean canFillTank(EnumFacing facing, int index) {

		return false;
	}

	@Override
	public int fill(EnumFacing facing, FluidStack resource, boolean doFill) {

		return 0;
	}

	@Override
	public boolean allowBucketDrain(EnumFacing facing, @Nonnull ItemStack stack) {

		return true;
	}

}
