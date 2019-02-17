package powercrystals.minefactoryreloaded.tile.machine.processing;

import cofh.core.util.helpers.MathHelper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.api.laser.IFactoryLaserTarget;
import powercrystals.minefactoryreloaded.core.MFRDyeColor;
import powercrystals.minefactoryreloaded.core.MFRUtil;
import powercrystals.minefactoryreloaded.core.UtilInventory;
import powercrystals.minefactoryreloaded.core.WeightedRandomItemStack;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryInventory;
import powercrystals.minefactoryreloaded.gui.client.GuiLaserDrill;
import powercrystals.minefactoryreloaded.gui.container.ContainerFactoryInventory;
import powercrystals.minefactoryreloaded.gui.container.ContainerLaserDrill;
import powercrystals.minefactoryreloaded.setup.MFRConfig;
import powercrystals.minefactoryreloaded.setup.MFRThings;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryTickable;

import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class TileEntityLaserDrill extends TileEntityFactoryTickable implements IFactoryLaserTarget {

	private static final int _energyPerWork = Machine.LaserDrillPrecharger.getActivationEnergy() * 4;
	private static final int _energyStoredMax = 1000000;

	private int color = 0xFFFFFF;

	private int _energyStored;

	private int _workStoredMax = MFRConfig.laserdrillCost.getInt();
	private float _workStored;

	private int _bedrockLevel;

	private Random _rand;

	public static boolean canReplaceBlock(Block block, World world, BlockPos replacePos) {

		IBlockState state = world.getBlockState(replacePos);
		return block == null || state.getBlockHardness(world, replacePos) == 0 || world.isAirBlock(replacePos);
	}

	public TileEntityLaserDrill() {

		super(Machine.LaserDrill);
		_rand = new Random();
		setManageSolids(true);
	}

	@Override
	public ContainerFactoryInventory getContainer(InventoryPlayer inventoryPlayer) {

		return new ContainerLaserDrill(this, inventoryPlayer);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiFactoryInventory getGui(InventoryPlayer inventoryPlayer) {

		return new GuiLaserDrill(getContainer(inventoryPlayer), this);
	}

	@Override
	public boolean canFormBeamWith(EnumFacing from) {

		return from.ordinal() > 1 && from.ordinal() < 6;
	}

	@Override
	public int addEnergy(EnumFacing from, int energy, boolean simulate) {

		if (!canFormBeamWith(from))
			return energy;
		int energyToAdd = Math.min(energy, _energyStoredMax - _energyStored);
		if (!simulate)
			_energyStored += energyToAdd;
		return energy - energyToAdd;
	}

	@Override
	public void update() {

		if (isInvalid() || world.isRemote) {
			return;
		}

		super.update();

		if (hasDrops())
			return;

		if (shouldCheckDrill()) {
			updateDrill();
		}

		BlockPos downPos = pos.offset(EnumFacing.DOWN);
		Block lowerId = world.getBlockState(downPos).getBlock();

		if (_bedrockLevel < 0) {
			if (lowerId.equals(MFRThings.fakeLaserBlock)) {
				world.setBlockToAir(downPos);
			}
			return;
		}

		if (!lowerId.equals(MFRThings.fakeLaserBlock) &&
				canReplaceBlock(lowerId, world, downPos)) {
			world.setBlockState(downPos, MFRThings.fakeLaserBlock.getDefaultState());
		}

		int energyToDraw = Math.min(_energyPerWork, _energyStored);
		float energyPerWorkHere = _energyPerWork * (1.2f - 0.4f * Math.min(pos.getY() - _bedrockLevel, 128f) / 128f);

		float workDone = energyToDraw / energyPerWorkHere;
		_workStored += workDone;
		_energyStored -= workDone * energyPerWorkHere;

		while (_workStored >= _workStoredMax) {
			_workStored -= _workStoredMax;
			doDrop(getRandomDrop());
		}
	}

	public int getWorkDone() {

		return (int) _workStored;
	}

	public void setWorkDone(int work) {

		_workStored = work;
	}

	public int getWorkMax() {

		return _workStoredMax;
	}

	public int getEnergyStored() {

		return _energyStored;
	}

	public void setEnergyStored(int energy) {

		_energyStored = energy;
	}

	public int getEnergyMax() {

		return _energyStoredMax;
	}

	private boolean shouldCheckDrill() {

		return world.getTotalWorldTime() % 32 == 0;
	}

	private void updateDrill() {

		int y = Integer.MAX_VALUE;
		for (y = pos.getY(); y-- > 0;) {
			BlockPos offsetPos = new BlockPos(pos.getX(), y, pos.getZ());
			Block block = world.getBlockState(offsetPos).getBlock();
			if (!block.equals(MFRThings.fakeLaserBlock)) {
				if (!world.isAirBlock(offsetPos) &&
						canReplaceBlock(block, world, offsetPos))
					if (world.destroyBlock(offsetPos, true))
						continue;

				if (block.isAssociatedBlock(Blocks.BEDROCK)) {
					_bedrockLevel = y;
					return;
				} else if (!world.isAirBlock(offsetPos)) {
					_bedrockLevel = -1;
					return;
				}

			}
		}

		_bedrockLevel = 0;
	}

	@Override
	protected void onFactoryInventoryChanged() {

		super.onFactoryInventoryChanged();

		int r = 0, g = 0, b = 0, d = 0;
		for (@Nonnull ItemStack s : _inventory) {
			++d;
			if (s.isEmpty() || !s.getItem().equals(MFRThings.laserFocusItem)) {
				r += 255;
				g += 255;
				b += 255;
				continue;
			}
			int c = MFRDyeColor.byMetadata(s.getItemDamage()).getColor();
			r += (c >> 16) & 255;
			g += (c >> 8) & 255;
			b += (c >> 0) & 255;
		}
		if (d == 0) {
			return;
		}
		r /= d;
		g /= d;
		b /= d;
		color = (r << 16) | (g << 8) | b;
		if (world != null) {
			MFRUtil.notifyBlockUpdate(world, pos);
		}
	}

	public int getColor() {

		return color;
	}

	@Override
	protected NBTTagCompound writePacketData(NBTTagCompound tag) {

		tag = super.writePacketData(tag);

		tag.setInteger("color", color);

		return tag;
	}

	@Override
	protected void handlePacketData(NBTTagCompound tag) {

		super.handlePacketData(tag);

		color = tag.getInteger("color");
	}

	@Nonnull
	private ItemStack getRandomDrop() {

		List<WeightedRandomItemStack> drops = new LinkedList<>();
		int boost = WeightedRandom.getTotalWeight(MFRRegistry.getLaserOres()) / 30;

		for (WeightedRandom.Item i : MFRRegistry.getLaserOres()) {
			WeightedRandomItemStack oldStack = (WeightedRandomItemStack) i;
			WeightedRandomItemStack newStack = new WeightedRandomItemStack(oldStack.getStack(), oldStack.itemWeight);
			drops.add(newStack);
			for (@Nonnull ItemStack s : _inventory) {
				if (s.isEmpty() || !s.getItem().equals(MFRThings.laserFocusItem) ||
						MFRRegistry.getLaserPreferredOres(s.getItemDamage()) == null) {
					continue;
				}

				List<ItemStack> preferredOres = MFRRegistry.getLaserPreferredOres(s.getItemDamage());
				int realBoost = boost / Math.max(1, preferredOres.size() / 2) + 1;

				for (@Nonnull ItemStack preferredOre : preferredOres) {
					if (UtilInventory.stacksEqual(newStack.getStack(), preferredOre)) {
						newStack.itemWeight += realBoost;
					}
				}
			}
		}

		return WeightedRandom.getRandomItem(_rand, drops).getStack();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() {

		return INFINITE_EXTENT_AABB;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public double getMaxRenderDistanceSquared() {

		return 65536;
	}

	public boolean shouldDrawBeam() {

		if (shouldCheckDrill()) {
			updateDrill();
		}
		return _bedrockLevel >= 0;
	}

	public int getBeamHeight() {

		return pos.getY() - _bedrockLevel;
	}

	@Override
	public void writeItemNBT(NBTTagCompound tag) {

		super.writeItemNBT(tag);

		if (_energyStored > 0)
			tag.setInteger("energyStored", _energyStored);
		if (!MathHelper.between(-1e-5, _workStored, 1e-5))
			tag.setFloat("workDone", _workStored);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {

		super.readFromNBT(tag);

		_energyStored = Math.min(tag.getInteger("energyStored"), _energyStoredMax);
		_workStored = Math.min(tag.getFloat("workDone"), getWorkMax());
	}

	@Override
	public int getSizeInventory() {

		return 6;
	}

	@Override
	public int getInventoryStackLimit() {

		return 1;
	}

	@Override
	public boolean canInsertItem(int slot, @Nonnull ItemStack itemstack, EnumFacing side) {

		return false;
	}

	@Override
	public boolean canExtractItem(int slot, @Nonnull ItemStack itemstack, EnumFacing side) {

		return false;
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer entityplayer) {

		return entityplayer.getDistanceSq(pos) <= 64;
	}

	@Override
	public boolean isItemValidForSlot(int i, @Nonnull ItemStack itemstack) {

		return false;
	}
}
