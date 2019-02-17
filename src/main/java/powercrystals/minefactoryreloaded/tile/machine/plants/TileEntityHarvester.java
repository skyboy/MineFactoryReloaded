package powercrystals.minefactoryreloaded.tile.machine.plants;

import cofh.core.fluid.FluidTankCore;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.api.plant.HarvestType;
import powercrystals.minefactoryreloaded.api.plant.IFactoryHarvestable;
import powercrystals.minefactoryreloaded.api.util.IFactorySettings;
import powercrystals.minefactoryreloaded.api.util.IFactorySettings.SettingNames;
import powercrystals.minefactoryreloaded.core.Area;
import powercrystals.minefactoryreloaded.core.HarvestMode;
import powercrystals.minefactoryreloaded.core.IHarvestManager;
import powercrystals.minefactoryreloaded.core.TreeHarvestManager;
import powercrystals.minefactoryreloaded.core.settings.BooleanSetting;
import powercrystals.minefactoryreloaded.core.settings.FactorySettings;
import powercrystals.minefactoryreloaded.core.settings.ISetting;
import powercrystals.minefactoryreloaded.core.settings.Vec3Setting;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryInventory;
import powercrystals.minefactoryreloaded.gui.client.GuiHarvester;
import powercrystals.minefactoryreloaded.gui.container.ContainerHarvester;
import powercrystals.minefactoryreloaded.setup.MFRConfig;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryPowered;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class TileEntityHarvester extends TileEntityFactoryPowered {

	private static boolean skip = false;
	private static Map<String, ISetting> DEFAULT_SETTINGS;

	static {

		HashMap<String, ISetting> _settings = new HashMap<>();
		_settings.put(SettingNames.SHEARS_MODE, BooleanSetting.FALSE);
		_settings.put(SettingNames.HARVEST_SMALL_MUSHROOMS, BooleanSetting.FALSE);
		_settings.put(SettingNames.PLAY_SOUNDS, (BooleanSetting) MFRConfig.playSounds::getBoolean);
		DEFAULT_SETTINGS = java.util.Collections.unmodifiableMap(_settings);
	}

	private Map<String, ISetting> _settings;
	private IFactorySettings _immutableSettings;

	private Random _rand;

	private IHarvestManager _treeManager;

	public TileEntityHarvester() {

		super(Machine.Harvester);
		createHAM(this, 1);
		setManageSolids(true);

		_settings = new HashMap<>();
		_settings.putAll(DEFAULT_SETTINGS);
		_settings.put(SettingNames.HARVESTING_TREE, (BooleanSetting) () -> !_treeManager.getIsDone());
		_immutableSettings = new FactorySettings(_settings);

		_rand = new Random();
		setCanRotate(true);

		skip = MFRConfig.harvesterSkip.getBoolean(false);
	}

	@Override
	public void onChunkUnload() {

		super.onChunkUnload();
		if (_treeManager != null)
			_treeManager.free();
	}

	@Override
	public void validate() {

		super.validate();
		if (!world.isRemote) {
			createHAM(this, 1);
			onFactoryInventoryChanged();
			if (_treeManager != null && _areaManager.getHarvestArea().contains(_treeManager.getOrigin())) {
				_treeManager.setWorld(world);
			} else {
				_treeManager = new TreeHarvestManager(world,
						new Area(pos, 0, 0, 0),
						_immutableSettings);
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiFactoryInventory getGui(InventoryPlayer inventoryPlayer) {

		return new GuiHarvester(getContainer(inventoryPlayer), this);
	}

	@Override
	public ContainerHarvester getContainer(InventoryPlayer inventoryPlayer) {

		return new ContainerHarvester(this, inventoryPlayer);
	}

	public Map<String, ISetting> getSettings() {

		return _settings;
	}

	public IFactorySettings getImmutableSettings() {

		return _immutableSettings;
	}

	@Override
	protected boolean shouldPumpLiquid() {

		return true;
	}

	@Override
	public int getWorkMax() {

		return 1;
	}

	@Override
	public int getIdleTicksMax() {

		return 5 + getExtraIdleTime(10);
	}

	protected int getExtraIdleTime(int additionalDelay) {

		return (_tanks[0].getFluidAmount() * additionalDelay / _tanks[0].getCapacity());
	}

	@Override
	public boolean activateMachine() {

		BlockPos target = getNextHarvest();

		// nothing to harvest this cycle
		if (target == null) {
			setIdleTicks(getIdleTicksMax());
			return false;
		}

		IBlockState harvestState = world.getBlockState(target);

		// lookup the harvestable for this position: very redundant, but no multi-return
		IFactoryHarvestable harvestable = MFRRegistry.getHarvestables().get(harvestState.getBlock());

		List<ItemStack> drops = harvestable.getDrops(world, target, harvestState, _rand, _immutableSettings);

		// let the harvestable pre-process the block before we fire the event
		harvestable.preHarvest(world, target, harvestState);

		// fire the event so mods can alter the drop list: we look up the state again as it may be changed
		ForgeEventFactory.fireBlockHarvesting(drops, world, target, world.getBlockState(target), 0,
				1f, _immutableSettings.getBoolean(SettingNames.SHEARS_MODE), null);

		if (harvestable.breakBlock()) {
			if (!world.setBlockState(target, Blocks.AIR.getDefaultState(), 2 | 16))
				return false;
			if (_immutableSettings.getBoolean(SettingNames.PLAY_SOUNDS)) {
				world.playEvent(null, 2001, target, Block.getStateId(harvestState));
			}
		}

		_tanks[0].fill(FluidRegistry.getFluidStack("sludge", 10), true);
		doDrop(drops);

		setIdleTicks(getExtraIdleTime(10));

		if (harvestable.postHarvest(world, target, harvestState))
			if (_immutableSettings.getBoolean(SettingNames.PLAY_SOUNDS)) {
				world.playEvent(null, 2001, target, Block.getStateId(harvestState));
			}

		return true;
	}

	@Nullable
	private BlockPos getNextHarvest() {

		BlockPos bp;
		boolean skipping = false;
		// skip blocks if configured
		if (skip) {
			int extra = getExtraIdleTime(400);
			// up to 40%
			skipping = extra > 0 && extra >= 1 + _rand.nextInt(1000);
		}

		// eating a tree
		if (!_treeManager.getIsDone()) {
			// increment tree position
			bp = getNextTreeSegment(null, MFRRegistry.getHarvestables());
			return skipping ? null : bp;
		}

		// increment the counter for the manager
		bp = _areaManager.getNextBlock();
		if (skipping || !world.isBlockLoaded(bp)) {
			return null;
		}

		IBlockState harvestState = world.getBlockState(bp);
		Block search = harvestState.getBlock();

		final Map<Block, IFactoryHarvestable> harvestableMap = MFRRegistry.getHarvestables();
		IFactoryHarvestable harvestable = harvestableMap.get(search);
		if (harvestable == null) {
			return null;
		}

		// let harvestables know where we first found them
		_settings.put(SettingNames.START_POSITION, Vec3Setting.of(bp));

		HarvestType type = harvestable.getHarvestType();
		if (harvestable.canBeHarvested(world, bp, harvestState, _immutableSettings)) {
			switch (type) {
				case PlantStem:
					return getNextAdjacent(bp, harvestable);
				case Column:
				case LeaveBottom:
					return getNextVertical(bp, type == HarvestType.Column ? 0 : 1, harvestable);
				case Tree:
				case TreeLeaf:
					return getNextTreeSegment(bp, harvestableMap);
				case TreeFruit:
				case Normal:
					return bp;
			}
		}
		return null;
	}

	@Nullable
	private BlockPos getNextAdjacent(BlockPos pos, final IFactoryHarvestable harvestable) {

		// check the 4 adjacent sides
		for (EnumFacing side : EnumFacing.HORIZONTALS) {
			BlockPos offsetPos = pos.offset(side);
			if (!world.isBlockLoaded(offsetPos))
				continue;
			IBlockState harvestState = world.getBlockState(offsetPos);
			// ask the stem harvestable if this side contains a harvestable fruit
			if (harvestable.canBeHarvested(world, offsetPos, harvestState, _immutableSettings))
				return offsetPos;
		}
		return null;
	}

	@Nullable
	private BlockPos getNextVertical(BlockPos pos, int startOffset, final IFactoryHarvestable harvestable) {

		BlockPos harvestPos = null;
		int maxBlockOffset = MFRConfig.verticalHarvestSearchMaxVertical.getInt();
		IBlockState harvestState = null;

		Block plant = harvestable.getPlant();
		// scan upward until we find the top
		for (int currentYOffset = startOffset; currentYOffset < maxBlockOffset; ++currentYOffset) {
			BlockPos offsetPos = pos.offset(EnumFacing.UP, currentYOffset);
			IBlockState state = world.getBlockState(offsetPos);
			if (!state.getBlock().equals(plant))
				break;

			harvestPos = offsetPos;
			harvestState = state;
		}

		// if we have a top, check that it can be harvested: we don't care about the interim blocks
		if (harvestPos != null && harvestable.canBeHarvested(world, harvestPos, harvestState, _immutableSettings)) {
			// we have something, so come back to this block again on the next cycle
			_areaManager.rewindBlock();
			return harvestPos;
		}

		return null;
	}

	@Nullable
	private BlockPos getNextTreeSegment(@Nullable BlockPos pos, final Map<Block, IFactoryHarvestable> harvestableMap) {

		// null for continuation
		if (pos != null) {
			Area a = new Area(pos, MFRConfig.treeSearchMaxHorizontal.getInt(), pos.getY(), world.getHeight() - pos.getY());
			_treeManager.reset(world, a, HarvestMode.HarvestTree, _immutableSettings);
		}

		// we may encounter blocks that were removed before the harvester got to them, loop
		while (!_treeManager.getIsDone()) {
			BlockPos bp = _treeManager.getNextBlock();
			_treeManager.moveNext();
			if (!world.isBlockLoaded(bp)) {
				return null;
			}
			IBlockState harvestState = world.getBlockState(bp);

			IFactoryHarvestable harvestable = harvestableMap.get(harvestState.getBlock());
			if (harvestable != null) {
				HarvestType t = harvestable.getHarvestType();
				// possible that a new harvestable block was placed into a location where part of the tree was found
				if (t.isTree && harvestable.canBeHarvested(world, bp, harvestState, _immutableSettings))
					return bp;
			}
		}
		return null;
	}

	@Override
	protected FluidTankCore[] createTanks() {

		return new FluidTankCore[] { new FluidTankCore(4 * BUCKET_VOLUME) };
	}

	@Override
	public void writePortableData(EntityPlayer player, NBTTagCompound tag) {

		NBTTagCompound list = new NBTTagCompound();
		DEFAULT_SETTINGS.forEach((key, value) -> {
			if (!SettingNames.PLAY_SOUNDS.equals(key))
				list.setBoolean(key, _immutableSettings.getBoolean(key));
		});
		tag.setTag("harvesterSettings", list);
	}

	@Override
	public void readPortableData(EntityPlayer player, NBTTagCompound tag) {

		NBTTagCompound list = (NBTTagCompound) tag.getTag("harvesterSettings");
		DEFAULT_SETTINGS.forEach((key, value) -> {
			if (!SettingNames.PLAY_SOUNDS.equals(key) && list.hasKey(key, NBT.TAG_BYTE)) {
				boolean b = list.getBoolean(key);
				_settings.put(key, b ? BooleanSetting.TRUE : BooleanSetting.FALSE);
			}
		});
	}

	@Override
	public void writeItemNBT(NBTTagCompound tag) {

		super.writeItemNBT(tag);
		NBTTagCompound list = new NBTTagCompound();
		DEFAULT_SETTINGS.forEach((key, value) -> {
			if (!SettingNames.PLAY_SOUNDS.equals(key))
				list.setBoolean(key, _immutableSettings.getBoolean(key));
		});
		if (!list.hasNoTags())
			tag.setTag("harvesterSettings", list);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {

		tag = super.writeToNBT(tag);

		_treeManager.writeToNBT(tag);
		tag.setInteger("bpos", _areaManager.getPosition());

		return tag;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {

		super.readFromNBT(tag);
		NBTTagCompound list = (NBTTagCompound) tag.getTag("harvesterSettings");
		DEFAULT_SETTINGS.forEach((key, value) -> {
			if (!SettingNames.PLAY_SOUNDS.equals(key)) {
				boolean b = list.getBoolean(key);
				_settings.put(key, b ? BooleanSetting.TRUE : BooleanSetting.FALSE);
			}
		});
		if (_treeManager != null)
			_treeManager.free();
		_treeManager = new TreeHarvestManager(tag, _immutableSettings);

		_areaManager.getHarvestArea();
		_areaManager.setPosition(tag.getInteger("bpos"));
	}

	@Override
	public int getSizeInventory() {

		return 1;
	}

	@Override
	public int getStartInventorySide(EnumFacing side) {

		return 0;
	}

	@Override
	public int getSizeInventorySide(EnumFacing side) {

		return 0;
	}

	@Override
	public int getUpgradeSlot() {

		return 0;
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
	public boolean allowBucketDrain(EnumFacing facing, @Nonnull ItemStack stack) {

		return true;
	}

	@Override
	protected boolean canFillTank(EnumFacing facing, int index) {

		return false;
	}

	@Override
	public int fill(EnumFacing facing, FluidStack resource, boolean doFill) {

		return 0;
	}

}
