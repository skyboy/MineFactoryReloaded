package powercrystals.minefactoryreloaded.tile.machine.plants;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.api.plant.IFactoryPlantable;
import powercrystals.minefactoryreloaded.api.plant.IReplacementBlock;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryInventory;
import powercrystals.minefactoryreloaded.gui.client.GuiPlanter;
import powercrystals.minefactoryreloaded.gui.container.ContainerPlanter;
import powercrystals.minefactoryreloaded.gui.container.ContainerUpgradeable;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryPowered;

import javax.annotation.Nonnull;

public class TileEntityPlanter extends TileEntityFactoryPowered {

	protected boolean keepLastItem = false;

	public TileEntityPlanter() {

		super(Machine.Planter);
		createHAM(this, 1);
		_areaManager.setOverrideDirection(EnumFacing.UP);
		_areaManager.setOriginOffset(new BlockPos(0, 1, 0));
		setManageSolids(true);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiFactoryInventory getGui(InventoryPlayer inventoryPlayer) {

		return new GuiPlanter(getContainer(inventoryPlayer), this);
	}

	@Override
	public ContainerUpgradeable getContainer(InventoryPlayer inventoryPlayer) {

		return new ContainerPlanter(this, inventoryPlayer);
	}

	@Override
	public boolean activateMachine() {

		BlockPos bp = _areaManager.getNextBlock();
		if (!world.isBlockLoaded(bp)) {
			setIdleTicks(getIdleTicksMax());
			return false;
		}

		ItemStack match = _inventory.get(getPlanterSlotIdFromBp(bp));

		for (int stackIndex = 10, stackEnd = getSizeInventory(); stackIndex < stackEnd; stackIndex++) {
			ItemStack availableStack = getStackInSlot(stackIndex);

			if (keepLastItem && availableStack.getCount() < 2) {
				continue;
			}

			// skip planting attempt if there's no stack in that slot,
			// or if there's a template item that's not matched
			if (availableStack.isEmpty() ||
					!(match.isEmpty() || stacksEqual(match, availableStack))) {
				continue;
			}

			IFactoryPlantable plantable = MFRRegistry.getPlantables().get(availableStack.getItem());
			if (plantable == null || !plantable.canBePlanted(availableStack, false) || // redundancy checks, in case something changed
					!plantable.canBePlantedHere(world, bp, availableStack))
				continue;

			IReplacementBlock block = plantable.getPlantedBlock(world, bp, availableStack);
			if (!block.replaceBlock(world, bp, availableStack))
				continue;
			decrStackSize(stackIndex, 1);
			return true;
		}

		setIdleTicks(getIdleTicksMax());
		return false;
	}

	@Override
	public void writePortableData(EntityPlayer player, NBTTagCompound tag) {

		tag.setBoolean("keepLastItem", keepLastItem);
	}

	@Override
	public void readPortableData(EntityPlayer player, NBTTagCompound tag) {

		keepLastItem = tag.getBoolean("keepLastItem");
	}

	@Override
	public void writeItemNBT(NBTTagCompound tag) {

		super.writeItemNBT(tag);
		if (keepLastItem)
			tag.setBoolean("keepLastItem", keepLastItem);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {

		super.readFromNBT(tag);
		keepLastItem = tag.getBoolean("keepLastItem");
	}

	protected boolean stacksEqual(@Nonnull ItemStack a, @Nonnull ItemStack b) {

		if (a.isEmpty() || b.isEmpty() ||
				(!a.getItem().equals(b.getItem())) ||
				(a.getItemDamage() != b.getItemDamage()) ||
				a.hasTagCompound() != b.hasTagCompound()) {
			return false;
		}
		if (!a.hasTagCompound()) {
			return true;
		}
		NBTTagCompound tagA = a.getTagCompound().copy(), tagB = b.getTagCompound().copy();
		tagA.removeTag("display");
		tagB.removeTag("display");
		tagA.removeTag("ench");
		tagB.removeTag("ench");
		tagA.removeTag("RepairCost");
		tagB.removeTag("RepairCost");
		return tagA.equals(tagB);
	}

	//assumes a 3x3 grid in inventory slots 0-8
	//slot 0 is northwest, slot 2 is northeast, etc
	protected int getPlanterSlotIdFromBp(BlockPos bp) {

		int radius = _areaManager.getRadius();
		int xAdjusted = Math.round(1.49F * (bp.getX() - pos.getX()) / radius);
		int zAdjusted = Math.round(1.49F * (bp.getZ() - pos.getZ()) / radius);
		return 4 + xAdjusted + 3 * zAdjusted;
	}

	public boolean getConsumeAll() {

		return keepLastItem;
	}

	public void setConsumeAll(boolean b) {

		keepLastItem = b;
	}

	@Override
	public int getSizeInventory() {

		return 26;
	}

	@Override
	public int getWorkMax() {

		return 1;
	}

	@Override
	public int getIdleTicksMax() {

		return 5;
	}

	@Override
	public int getStartInventorySide(EnumFacing side) {

		return 9;
	}

	@Override
	public boolean shouldDropSlotWhenBroken(int slot) {

		return slot > 8;
	}

	@Override
	public int getSizeInventorySide(EnumFacing side) {

		return 17;
	}

	@Override
	public int getUpgradeSlot() {

		return 9;
	}

	@Override
	public boolean canInsertItem(int slot, @Nonnull ItemStack stack, EnumFacing side) {

		if (!stack.isEmpty()) {
			if (slot > 9) {
				IFactoryPlantable p = MFRRegistry.getPlantables().get(stack.getItem());
				return p != null && p.canBePlanted(stack, false);
			} else if (slot == 9) {
				return isUsableAugment(stack);
			}
		}
		return false;
	}

	@Override
	public boolean canExtractItem(int slot, @Nonnull ItemStack itemstack, EnumFacing side) {

		return slot > 9;
	}

}
