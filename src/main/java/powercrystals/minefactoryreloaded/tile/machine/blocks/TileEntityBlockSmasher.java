package powercrystals.minefactoryreloaded.tile.machine.blocks;

import cofh.core.fluid.FluidTankCore;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import powercrystals.minefactoryreloaded.core.UtilInventory;
import powercrystals.minefactoryreloaded.gui.client.GuiBlockSmasher;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryInventory;
import powercrystals.minefactoryreloaded.gui.container.ContainerBlockSmasher;
import powercrystals.minefactoryreloaded.setup.MFRFluids;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryPowered;
import powercrystals.minefactoryreloaded.world.SmashingWorld;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileEntityBlockSmasher extends TileEntityFactoryPowered {

	public static final int MAX_FORTUNE = 3;
	private int _fortune = 0;

	@Nonnull
	private ItemStack _lastInput = ItemStack.EMPTY;
	private NonNullList<ItemStack> _lastOutput;

	private SmashingWorld _smashingWorld;
	private boolean _shouldWork = true;

	public TileEntityBlockSmasher() {

		super(Machine.BlockSmasher);
		setManageSolids(true);
		_tanks[0].setLock(MFRFluids.getFluid("mob_essence"));
	}

	@Override
	public void setWorld(World world) {

		super.setWorld(world);
		_smashingWorld = new SmashingWorld();
	}

	@Override
	public int getSizeInventory() {

		return 2;
	}

	@Override
	public ContainerBlockSmasher getContainer(InventoryPlayer inventoryPlayer) {

		return new ContainerBlockSmasher(this, inventoryPlayer);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiFactoryInventory getGui(InventoryPlayer inventoryPlayer) {

		return new GuiBlockSmasher(getContainer(inventoryPlayer), this);
	}

	@Override
	protected boolean activateMachine() {

		if (_shouldWork && _inventory.get(0).isEmpty()) {
			setWorkDone(0);
			return false;
		}

		if (!_inventory.get(0).isEmpty() && (_lastInput.isEmpty() || !UtilInventory.stacksEqual(_lastInput, _inventory.get(0)))) {
			_lastInput = _inventory.get(0).copy(); // protect against amorphous itemstacks
			_lastOutput = getOutput(_lastInput);
		}
		if (_lastOutput == null) {
			setWorkDone(0);
			return false;
		}
		if (_shouldWork && _fortune > 0 && (drain(_fortune, false, _tanks[0]) != _fortune)) {
			return false;
		}
		@Nonnull ItemStack outSlot = _inventory.get(1);
		@Nonnull ItemStack output = getEqualStack(outSlot, _lastOutput);
		// TODO: ^ inefficient
		if (output.isEmpty()) {
			if (_shouldWork)
				setWorkDone(0);
			return false;
		}
		if (!outSlot.isEmpty() && outSlot.getMaxStackSize() - outSlot.getCount() < output.getCount()) {
			return false;
		}

		if (getWorkDone() >= getWorkMax()) {
			if (_shouldWork) {
				_inventory.get(0).shrink(1);
				if (_inventory.get(0).getCount() == 0) {
					_inventory.set(0, ItemStack.EMPTY);
				}
			}
			_shouldWork = false;
			if (_inventory.get(1).isEmpty()) {
				_inventory.set(1, output.copy());
			} else {
				_inventory.get(1).grow(output.getCount());
			}
			_lastOutput.remove(output);
			if (_lastOutput.size() == 0) {
				setWorkDone(0);
				_shouldWork = true;
				_lastInput = ItemStack.EMPTY;
				_lastOutput = null;
			}
		} else {
			if (!incrementWorkDone()) return false;
			drain(_fortune, true, _tanks[0]);
		}
		return true;
	}

	private static @Nonnull ItemStack getEqualStack(@Nonnull ItemStack a, NonNullList<ItemStack> b) {

		if (!a.isEmpty() && b != null && a.getCount() > 0 && b.size() > 0)
			for (@Nonnull ItemStack i : b)
				if (UtilInventory.stacksEqual(a, i)) return i;
		return a.isEmpty() && b.size() > 0 ? b.get(0) : ItemStack.EMPTY;
	}

	private NonNullList<ItemStack> getOutput(@Nonnull ItemStack input) {

		if (!(input.getItem() instanceof ItemBlock)) {
			return null;
		}
		ItemBlock block = (ItemBlock) input.getItem();
		Block b = block.getBlock();

		NonNullList<ItemStack> drops = _smashingWorld.smashBlock(input, b, block.getMetadata(input.getItemDamage()), _fortune);
		if (drops != null && drops.size() > 0) {
			return drops;
		}
		return null;
	}

	public int getFortune() {

		return _fortune;
	}

	public void setFortune(int fortune) {

		if (fortune >= 0 && fortune <= MAX_FORTUNE) {
			if (_fortune < fortune) {
				setWorkDone(0);
			}
			_fortune = fortune;
		}
	}

	@Override
	public int getWorkMax() {

		return 60;
	}

	@Override
	public int getIdleTicksMax() {

		return 1;
	}

	@Override
	public boolean canInsertItem(int slot, @Nonnull ItemStack stack, EnumFacing side) {

		return slot == 0;
	}

	@Override
	public boolean canExtractItem(int slot, @Nonnull ItemStack itemstack, EnumFacing side) {

		return slot == 1;
	}

	@Override
	protected FluidTankCore[] createTanks() {

		return new FluidTankCore[] { new FluidTankCore(4 * BUCKET_VOLUME) };
	}

	@Override
	public void writePortableData(EntityPlayer player, NBTTagCompound tag) {

		tag.setInteger("fortune", _fortune);
	}

	@Override
	public void readPortableData(EntityPlayer player, NBTTagCompound tag) {

		setFortune(tag.getInteger("fortune"));
	}

	@Override
	public void writeItemNBT(NBTTagCompound tag) {

		super.writeItemNBT(tag);
		if (_fortune > 0)
			tag.setInteger("fortune", _fortune);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {

		tag = super.writeToNBT(tag);
		tag.setBoolean("shouldWork", _shouldWork);
		if (!_lastInput.isEmpty())
			tag.setTag("stack", _lastInput.writeToNBT(new NBTTagCompound()));

		if (_lastOutput != null) {
			NBTTagList nbttaglist = new NBTTagList();
			for (@Nonnull ItemStack item : _lastOutput) {
				NBTTagCompound tagCompound = new NBTTagCompound();
				item.writeToNBT(tagCompound);
				nbttaglist.appendTag(tagCompound);
			}
			tag.setTag("SmashedItems", nbttaglist);
		}

		return tag;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {

		super.readFromNBT(tag);
		_fortune = tag.getInteger("fortune");
		_shouldWork = tag.hasKey("shouldWork") ? tag.getBoolean("shouldWork") : true;
		if (tag.hasKey("stack"))
			_lastInput = new ItemStack(tag.getCompoundTag("stack"));

		if (tag.hasKey("SmashedItems")) {
			NonNullList<ItemStack> drops = NonNullList.create();
			NBTTagList nbttaglist = tag.getTagList("SmashedItems", 10);
			for (int i = nbttaglist.tagCount(); i-- > 0;) {
				NBTTagCompound tagCompound = nbttaglist.getCompoundTagAt(i);
				@Nonnull ItemStack item = new ItemStack(tagCompound);
				if (!item.isEmpty() && item.getCount() > 0) {
					drops.add(item);
				}
			}
			if (drops.size() != 0) {
				_lastOutput = drops;
			}
		}
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
