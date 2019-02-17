package powercrystals.minefactoryreloaded.tile.base;

import cofh.api.core.IPortableData;
import cofh.api.tileentity.IInventoryConnection;
import com.google.common.base.Strings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import powercrystals.minefactoryreloaded.MineFactoryReloadedClient;
import powercrystals.minefactoryreloaded.core.HarvestAreaManager;
import powercrystals.minefactoryreloaded.core.IHarvestAreaContainer;
import powercrystals.minefactoryreloaded.core.IRotatableTile;
import powercrystals.minefactoryreloaded.core.MFRUtil;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryInventory;
import powercrystals.minefactoryreloaded.gui.container.ContainerFactoryInventory;
import powercrystals.minefactoryreloaded.net.MFRPacket;
import powercrystals.minefactoryreloaded.setup.Machine;

import java.util.Locale;

public abstract class TileEntityFactory extends TileEntityBase
		implements IRotatableTile, IInventoryConnection, IPortableData,
				   IHarvestAreaContainer/*, IPipeConnection*/ {

	protected static class FactoryAreaManager extends HarvestAreaManager<TileEntityFactory> {

		public FactoryAreaManager(TileEntityFactory owner, int harvestRadius,
				int harvestAreaUp, int harvestAreaDown, float upgradeModifier, boolean usesBlocks) {

			super(owner, harvestRadius, harvestAreaUp, harvestAreaDown, upgradeModifier, usesBlocks);
		}
	}

	private EnumFacing _forwardDirection;
	private boolean _canRotate = false;

	private boolean _manageFluids = false;
	private boolean _manageSolids = false;

	protected byte _activeSyncTimeout = 101;
	private int _lastUpgrade = 0;

	protected int _rednetState;

	protected HarvestAreaManager<TileEntityFactory> _areaManager;
	protected Machine _machine;

	protected String _owner = "";

	protected TileEntityFactory(Machine machine) {

		this._machine = machine;
		_forwardDirection = EnumFacing.NORTH;
	}

	@Override
	public void validate() {

		super.validate();
		if (world.isRemote && hasHAM()) {
			MineFactoryReloadedClient.addTileToAreaList(this);
		}
	}

	@Override
	public void invalidate() {

		removeTileFromAreaList();
		super.invalidate();
	}

	private void removeTileFromAreaList() {

		if (world != null && world.isRemote && hasHAM()) {
			MineFactoryReloadedClient.removeTileFromAreaList(this);
		}
	}

	@Override
	public void onChunkUnload() {

		removeTileFromAreaList();
		super.onChunkUnload();
	}

	/**
	 * Used to create HarvestAreas for entity-interacting machines.
	 */
	protected static void createEntityHAM(TileEntityFactory owner) {

		createHAM(owner, 2, 2, 1, 1.0f, false);
	}

	/**
	 * Used to create HarvestAreas for block-interacting machines
	 */
	protected static void createHAM(TileEntityFactory owner, int harvestRadius) {

		createHAM(owner, harvestRadius, 0, 0, 1.0f, true);
	}

	protected static void createHAM(TileEntityFactory owner, int harvestRadius, int harvestAreaUp, int harvestAreaDown,
			boolean usesBlocks) {

		createHAM(owner, harvestRadius, harvestAreaUp, harvestAreaDown, 1.0f, usesBlocks);
	}

	protected static void createHAM(TileEntityFactory owner, int harvestRadius, int harvestAreaUp, int harvestAreaDown,
			float upgradeModifier, boolean usesBlocks) {

		owner._areaManager = new FactoryAreaManager(owner, harvestRadius, harvestAreaUp, harvestAreaDown,
				upgradeModifier, usesBlocks);
	}

	@Override
	public boolean hasHAM() {

		return getHAM() != null;
	}

	@Override
	public HarvestAreaManager<TileEntityFactory> getHAM() {

		return _areaManager;
	}

	public World getWorld() {

		return world;
	}

	@Override
	public EnumFacing getDirectionFacing() {

		return _forwardDirection;
	}

	@Override
	public boolean canRotate() {

		return _canRotate;
	}

	@Override
	public boolean canRotate(EnumFacing axis) {

		return _canRotate;
	}

	protected void setCanRotate(boolean canRotate) {

		_canRotate = canRotate;
	}

	@Override
	public void rotate(EnumFacing axis) {

		if (canRotate())
			rotate(false);
	}

	public void rotate(boolean reverse) {

		if (world != null && !world.isRemote) {
			switch ((reverse ? _forwardDirection.getOpposite() : _forwardDirection).ordinal()) {
			case 2://NORTH:
				_forwardDirection = EnumFacing.EAST;
				break;
			case 5://EAST:
				_forwardDirection = EnumFacing.SOUTH;
				break;
			case 3://SOUTH:
				_forwardDirection = EnumFacing.WEST;
				break;
			case 4://WEST:
				_forwardDirection = EnumFacing.NORTH;
				break;
			default:
				_forwardDirection = EnumFacing.NORTH;
			}

			onRotate();
		}
	}

	@Override
	public void rotateDirectlyTo(int rotation) {

		EnumFacing p = _forwardDirection;
		if (rotation > 0 && rotation < EnumFacing.VALUES.length) {
			_forwardDirection = EnumFacing.VALUES[rotation];
		} else {
			_forwardDirection = EnumFacing.NORTH;
		}

		if (p != _forwardDirection) {
			onRotate();
		}
	}

	protected void onRotate() {

		if (world != null && !isInvalid() && world.isBlockLoaded(pos)) {
			markForUpdate();
			MFRUtil.notifyNearbyBlocks(world, pos, getBlockType());
			MFRUtil.notifyBlockUpdate(world, pos);
		}
	}

	public void markForUpdate() {

	}

	public EnumFacing getDropDirection() {

		if (canRotate())
			return getDirectionFacing().getOpposite();
		return EnumFacing.UP;
	}

	public EnumFacing[] getDropDirections() {

		return EnumFacing.VALUES;
	}

	public void setOwner(String owner) {

		if (owner == null)
			owner = "";
		if (_owner == null || _owner.isEmpty())
			_owner = owner;
	}

	@SideOnly(Side.CLIENT)
	public GuiFactoryInventory getGui(InventoryPlayer inventoryPlayer) {

		return null;
	}

	public ContainerFactoryInventory getContainer(InventoryPlayer inventoryPlayer) {

		return null;
	}

	public String getGuiBackground() {

		if (_machine == null)
			return null;
		return _machine.getName().toLowerCase(Locale.US);
	}

	@Override
	public void markDirty() {

		if (world != null && !world.isRemote && hasHAM()) {
			HarvestAreaManager<TileEntityFactory> ham = getHAM();
			int u = ham.getUpgradeLevel();
			if (_lastUpgrade != u)
				MFRPacket.sendUpgradeLevelToClient(this);
			_lastUpgrade = u;
		}
		super.markDirty();
	}

	@Override
	protected NBTTagCompound writePacketData(NBTTagCompound tag) {

		tag = super.writePacketData(tag);

		tag.setByte("r", (byte) _forwardDirection.ordinal());

		return tag;
	}

	@Override
	protected void handlePacketData(NBTTagCompound tag) {

		super.handlePacketData(tag);

		rotateDirectlyTo(tag.getByte("r"));
	}

	@Override
	public String getDataType() {

		return _machine.getInternalName() + ".name";
	}

	@Override
	public void writePortableData(EntityPlayer player, NBTTagCompound tag) {

	}

	@Override
	public void readPortableData(EntityPlayer player, NBTTagCompound tag) {

	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {

		tag = super.writeToNBT(tag);

		tag.setInteger("rotation", getDirectionFacing().ordinal());
		if (!Strings.isNullOrEmpty(_owner))
			tag.setString("owner", _owner);

		return tag;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {

		super.readFromNBT(tag);
		if (tag.hasKey("rotation")) {
			_forwardDirection = null;
			rotateDirectlyTo(tag.getInteger("rotation"));
		}
		if (tag.hasKey("owner"))
			_owner = tag.getString("owner");
	}

	public void onRedNetChanged(EnumFacing side, int value) {

		_rednetState = value;
	}

	public int getRedNetOutput(EnumFacing side) {

		return 0;
	}

	// hoisted IMachine methods

	public void setManageFluids(boolean manageFluids) {

		_manageFluids = manageFluids;
	}

	public boolean manageFluids() {

		return _manageFluids;
	}

	public void setManageSolids(boolean manageSolids) {

		_manageSolids = manageSolids;
	}

	public boolean manageSolids() {

		return _manageSolids;
	}

	@Override
	public ConnectionType canConnectInventory(EnumFacing from) {

		return manageSolids() ? ConnectionType.FORCE : ConnectionType.DENY;
	}

/*	TODO: readd once BC team figure out what they want to do here
	@Override
	public ConnectOverride overridePipeConnection(PipeType type, EnumFacing with) {

		if (type == PipeType.FLUID)
			return manageFluids() ? ConnectOverride.CONNECT : ConnectOverride.DISCONNECT;
		if (type == PipeType.ITEM)
			return canConnectInventory(with).canConnect ? ConnectOverride.CONNECT : ConnectOverride.DISCONNECT;
		if (type == PipeType.STRUCTURE)
			return ConnectOverride.CONNECT;
		return ConnectOverride.DEFAULT;
	}
*/

}
