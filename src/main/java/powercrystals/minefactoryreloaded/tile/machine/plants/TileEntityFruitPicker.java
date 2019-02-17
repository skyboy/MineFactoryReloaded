package powercrystals.minefactoryreloaded.tile.machine.plants;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.api.plant.IFactoryFruit;
import powercrystals.minefactoryreloaded.api.plant.IReplacementBlock;
import powercrystals.minefactoryreloaded.core.Area;
import powercrystals.minefactoryreloaded.core.FruitHarvestManager;
import powercrystals.minefactoryreloaded.core.HarvestMode;
import powercrystals.minefactoryreloaded.core.IHarvestManager;
import powercrystals.minefactoryreloaded.modhelpers.vanilla.handlers.FruitChorus;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryInventory;
import powercrystals.minefactoryreloaded.gui.client.GuiUpgradeable;
import powercrystals.minefactoryreloaded.gui.container.ContainerUpgradeable;
import powercrystals.minefactoryreloaded.setup.MFRConfig;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryPowered;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class TileEntityFruitPicker extends TileEntityFactoryPowered {

	private IHarvestManager _treeManager;

	private Random _rand;

	public TileEntityFruitPicker() {

		super(Machine.FruitPicker);
		createHAM(this, 1);
		_rand = new Random();
		setManageSolids(true);
		setCanRotate(true);
	}

	@Override
	public void validate() {

		super.validate();
		if (!world.isRemote) {
			_treeManager = new FruitHarvestManager(world,
					new Area(pos, 0, 0, 0),
					HarvestMode.FruitTree);
		}
	}

	@Override
	public int getSizeInventory() {

		return 1;
	}

	@Override
	public ContainerUpgradeable getContainer(InventoryPlayer inventoryPlayer) {

		return new ContainerUpgradeable(this, inventoryPlayer);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiFactoryInventory getGui(InventoryPlayer inventoryPlayer) {

		return new GuiUpgradeable(getContainer(inventoryPlayer), this);
	}

	@Override
	protected boolean activateMachine() {

		BlockPos targetCoords = getNextTree();

		if (targetCoords == null) {
			setIdleTicks(getIdleTicksMax());
			return false;
		}

		IBlockState targetState = world.getBlockState(targetCoords);
		Block harvestedBlock = targetState.getBlock();

		IFactoryFruit harvestable = MFRRegistry.getFruits().get(harvestedBlock);

		List<ItemStack> drops = harvestable.getDrops(world, _rand, targetCoords);

		IReplacementBlock replacement = harvestable.getReplacementBlock(world, targetCoords);

		if (replacement == null) {
			if (!world.setBlockToAir(targetCoords))
				return false;
		} else {
			if (!replacement.replaceBlock(world, targetCoords, ItemStack.EMPTY))
				return false;
		}
		if (MFRConfig.playSounds.getBoolean(true)) {
			world.playEvent(null, 2001, targetCoords, Block.getStateId(targetState));
		}

		doDrop(drops);

		// TODO: sludge?

		return true;
	}

	private BlockPos getNextTree() {

		BlockPos bp = _areaManager.getNextBlock();
		if (!world.isBlockLoaded(bp)) {
			return null;
		}

		Block search = world.getBlockState(bp).getBlock();

		IFactoryFruit f = MFRRegistry.getFruits().get(search);
		if (!MFRRegistry.getFruitLogBlocks().contains(search)) {
			return f != null && f.canBePicked(world, bp) ? bp : null;
		}

		BlockPos temp = getNextTreeSegment(bp, f instanceof FruitChorus);
		if (temp != null)
			_areaManager.rewindBlock();

		return temp;
	}

	private BlockPos getNextTreeSegment(BlockPos pos, boolean invertedSearch) {

		Block block;

		if (_treeManager.getIsDone() || !_treeManager.getOrigin().equals(pos)) {
			int lowerBound = 0;
			int upperBound = MFRConfig.fruitTreeSearchMaxVertical.getInt();

			Area a = new Area(pos, MFRConfig.fruitTreeSearchMaxHorizontal.getInt(), lowerBound, upperBound);

			_treeManager.reset(world, a, invertedSearch ? HarvestMode.FruitTreeInverted : HarvestMode.FruitTree, null);
		}

		Map<Block, IFactoryFruit> fruits = MFRRegistry.getFruits();
		while (!_treeManager.getIsDone()) {
			BlockPos bp = _treeManager.getNextBlock();
			block = world.getBlockState(bp).getBlock();
			IFactoryFruit fruit = fruits.getOrDefault(block, null);

			if (fruit != null && fruit.canBePicked(world, bp))
				return bp;

			_treeManager.moveNext();
		}
		return null;
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
	public boolean canInsertItem(int slot, @Nonnull ItemStack itemstack, EnumFacing side) {

		return slot == 0 && isUsableAugment(itemstack);
	}

	@Override
	public boolean canExtractItem(int slot, @Nonnull ItemStack itemstack, EnumFacing side) {

		return false;
	}

	@Override
	public int getUpgradeSlot() {

		return 0;
	}

	@Override
	public EnumFacing getDropDirection() {

		return getDirectionFacing().getOpposite();
	}

}
