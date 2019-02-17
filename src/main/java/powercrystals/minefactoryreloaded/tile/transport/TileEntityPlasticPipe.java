package powercrystals.minefactoryreloaded.tile.transport;

import codechicken.lib.raytracer.IndexedCuboid6;
import codechicken.lib.vec.Vector3;
import cofh.core.render.hitbox.CustomHitBox;
import cofh.core.render.hitbox.ICustomHitBox;
import cofh.core.util.CoreUtils;
import cofh.core.util.helpers.FluidHelper;
import cofh.core.util.helpers.StringHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import powercrystals.minefactoryreloaded.core.IGridController;
import powercrystals.minefactoryreloaded.core.INode;
import powercrystals.minefactoryreloaded.core.ITraceable;
import powercrystals.minefactoryreloaded.core.MFRUtil;
import powercrystals.minefactoryreloaded.tile.base.TileEntityBase;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static powercrystals.minefactoryreloaded.block.transport.BlockRedNetCable.subSelection;
import static powercrystals.minefactoryreloaded.tile.transport.FluidNetwork.TRANSFER_RATE;
import static powercrystals.minefactoryreloaded.tile.transport.TileEntityPlasticPipe.ConnectionType.*;

public class TileEntityPlasticPipe extends TileEntityBase implements INode, ITraceable, ICustomHitBox {

	FluidNetwork grid;

	private ConnectionType[] sideConnection = { NONE, NONE, NONE, NONE, NONE, NONE };
	private boolean isPowered = false;
	private boolean cableOnly = false;
	boolean isNode = false;
	FluidStack fluidForGrid = null;

	private IFluidHandler[] handlerCache = null;
	private PlasticPipeUpgrade upgrade = PlasticPipeUpgrade.NONE;
	private boolean deadCache = true;

	private boolean initialized = false;

	public TileEntityPlasticPipe() {

	}

	@Override
	// cannot share mcp names
	public boolean isNotValid() {

		return tileEntityInvalid;
	}

	@Override
	public void invalidate() {

		if (grid != null) {
			removeFromGrid();
		}
		super.invalidate();
	}

	@Override
	public void onChunkUnload() {

		if (grid != null) {
			removeFromGrid();
		}
		super.onChunkUnload();
	}

	private void removeFromGrid() {

		grid.removePipe(this);
		markForRegen();
		deadCache = true;
		grid = null;
	}

	private void markForRegen() {

		int cableConnections = 0;
		for (EnumFacing facing : EnumFacing.VALUES) {
			if (sideConnection[facing.ordinal()] == CABLE) {
				++cableConnections;
				if (cableConnections > 1) {
					grid.regenerate();
					return;
				}
			}
		}
	}

	private void updateCache() {

		for (EnumFacing side : EnumFacing.VALUES) {
			BlockPos offsetPos = pos.offset(side);
			updateSideCache(offsetPos, side);
		}

		deadCache = false;
		FluidNetwork.HANDLER.addConduitForUpdate(this);

		if (grid == null) {
			grid = new FluidNetwork(this);
		}

		initialized = true;
	}

	private void updateSideCache(BlockPos sidePos, EnumFacing side) {

		if (handlerCache != null)
			handlerCache[side.ordinal()] = null;
		ConnectionType lastConnection = sideConnection[side.ordinal()];

		if (world.isBlockLoaded(sidePos)) {
			TileEntity te = MFRUtil.getTile(world, sidePos);
			if (te instanceof TileEntityPlasticPipe) {
				if (!initialized || sideConnection[side.ordinal()] != CABLE_DISCONNECTED) {
					TileEntityPlasticPipe pipe = (TileEntityPlasticPipe) te;
					if (pipe.grid != null) {
						if (pipe.grid.addPipe(this)) {
							sideConnection[side.ordinal()] = CABLE;
						} else {
							sideConnection[side.ordinal()] = CABLE_DISCONNECTED;
						}
					}
				}
			} else if (te != null && te.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side)) {
				if (handlerCache == null)
					handlerCache = new IFluidHandler[6];
				handlerCache[side.ordinal()] = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side);
				if (!initialized || (sideConnection[side.ordinal()] != EXTRACT && sideConnection[side.ordinal()] != OUTPUT)) {
					sideConnection[side.ordinal()] = OUTPUT;
				}
			} else if (initialized) {
				sideConnection[side.ordinal()] = NONE;
			}
		}

		if (!deadCache && lastConnection != sideConnection[side.ordinal()])
			MFRUtil.notifyBlockUpdate(world, pos);
	}

	@Override
	public void onNeighborBlockChange() {

		boolean last = isPowered;
		isPowered = upgrade.getPowered(CoreUtils.isRedstonePowered(this));
		if (last != isPowered)
			MFRUtil.notifyBlockUpdate(world, pos);
	}

	@Override
	public void onLoad() {

		super.onLoad();
		deadCache = true;
		handlerCache = null;
		if (world.isRemote)
			return;
		updateCache();
		markDirty();
		MFRUtil.notifyBlockUpdate(world, pos);
	}

	@Override
	public void onNeighborTileChange(BlockPos neighborPos) {

		if (world.isRemote || deadCache)
			return;

		Vec3i diff = neighborPos.subtract(pos);
		updateSideCache(neighborPos, EnumFacing.getFacingFromVector(diff.getX(), diff.getY(), diff.getZ()));
		FluidNetwork.HANDLER.addConduitForUpdate(this);
	}

	public boolean canInterface(EnumFacing dir) {

		return sideConnection[dir.ordinal()] == CABLE;
	}

	@Override
	public CustomHitBox getCustomHitBox(int hit, EntityPlayer player) {

		final List<IndexedCuboid6> list = new ArrayList<>(7);
		addTraceableCuboids(list, true, MFRUtil.isHoldingUsableTool(player, EnumHand.MAIN_HAND, pos, EnumFacing.UP), true);
		IndexedCuboid6 cube = list.get(0);
		cube.expand(0.003);
		Vector3 min = cube.min, max = cube.max.subtract(min);
		CustomHitBox box = new CustomHitBox(max.x, max.y, max.z, min.x, min.y, min.z);
		for (int i = 1, e = list.size(); i < e; ++i) {
			cube = list.get(i);
			if ((Integer) cube.data < 2) {
				cube.subtract(min);
				if (cube.min.y < 0)
					box.sideLength[0] = Math.max(box.sideLength[0], -cube.min.y);
				if (cube.min.z < 0)
					box.sideLength[2] = Math.max(box.sideLength[2], -cube.min.z);
				if (cube.min.x < 0)
					box.sideLength[4] = Math.max(box.sideLength[4], -cube.min.x);
				cube.subtract(max);
				if (cube.max.y > 0)
					box.sideLength[1] = Math.max(box.sideLength[1], cube.max.y);
				if (cube.max.z > 0)
					box.sideLength[3] = Math.max(box.sideLength[3], cube.max.z);
				if (cube.max.x > 0)
					box.sideLength[5] = Math.max(box.sideLength[5], cube.max.x);
			}
		}
		for (int i = box.sideLength.length; i-- > 0; )
			box.drawSide[i] = box.sideLength[i] > 0;
		return box;
	}

	@Override
	public boolean shouldRenderCustomHitBox(int subHit, EntityPlayer player) {

		return subHit < 2;
	}

	public void setUpgrade(PlasticPipeUpgrade upgrade) {

		this.upgrade = upgrade;
	}

	public PlasticPipeUpgrade getUpgrade() {

		return upgrade;
	}

	public void updateState() {

		markChunkDirty();
		notifyNeighborTileChange();
		deadCache = true;
		updateCache();
		MFRUtil.notifyBlockUpdate(world, pos);
	}

	@Override
	public boolean onPartHit(EntityPlayer player, EnumHand hand, int subSide, int subHit) {

		if (subHit >= 0 && subHit < (2 + 6 * 2)) {
			if (MFRUtil.isHoldingUsableTool(player, hand, pos, subSide < 6 ? EnumFacing.VALUES[subSide] : EnumFacing.UP)) {
				if (world.isRemote)
					return true;

				EnumFacing side = subSide < 6 ? EnumFacing.VALUES[subSide] : null;
				if (side != null &&
						(sideConnection[side.ordinal()] == CABLE || sideConnection[side.ordinal()] == CABLE_DISCONNECTED)) {
					TileEntityPlasticPipe cable = MFRUtil.getTile(world, pos.offset(side), TileEntityPlasticPipe.class);
					if (!canInterface(side)) {
						if (cable.grid.mergeGrid(grid)) {
							sideConnection[side.ordinal()] = CABLE;
							FluidNetwork.HANDLER.addConduitForUpdate(this);
							cable.sideConnection[side.getOpposite().ordinal()] = CABLE;
							FluidNetwork.HANDLER.addConduitForUpdate(cable);
							updateState();
							cable.updateState();
						}
					} else {
						removeFromGrid();
						sideConnection[side.ordinal()] = CABLE_DISCONNECTED;
						cable.sideConnection[side.getOpposite().ordinal()] = CABLE_DISCONNECTED;
						cable.updateState();
						updateState();
						if (grid == null)
							grid = new FluidNetwork(this);
					}
					return true;
				} else if (side == null) {
					cableOnly = !cableOnly;
					FluidNetwork.HANDLER.addConduitForUpdate(this);
					markDirty();
					MFRUtil.notifyBlockUpdate(world, pos);
					if (cableOnly) {
						player.sendMessage(new TextComponentTranslation("chat.info.mfr.rednet.tile.cableonly"));
					} else {
						player.sendMessage(new TextComponentTranslation("chat.info.mfr.rednet.tile.standard"));
					}
					return true;
				}
				String messageKey;
				switch (sideConnection[side.ordinal()]) {
				case HANDLER_DISCONNECTED:
					sideConnection[side.ordinal()] = OUTPUT;
					messageKey = "chat.info.mfr.fluid.connection.output";
					break;
				case EXTRACT:
					sideConnection[side.ordinal()] = HANDLER_DISCONNECTED;
					messageKey = "chat.info.mfr.fluid.connection.disabled";
					break;
				default:
					sideConnection[side.ordinal()] = EXTRACT;
					messageKey = "chat.info.mfr.fluid.connection.extract";
				}
				FluidNetwork.HANDLER.addConduitForUpdate(this);
				markDirty();
				MFRUtil.notifyBlockUpdate(world, pos);
				player.sendMessage(new TextComponentTranslation(messageKey));
			}
		}
		return false;
	}

	public boolean isPowered() {

		return isPowered;
	}

	@Override
	public void updateInternalTypes(IGridController gridController) {

		if (gridController != FluidNetwork.HANDLER)
			return;
		if (deadCache) {
			updateCache();
			return;
		}
		boolean node = false;
		if (!cableOnly) {
			for (EnumFacing side : EnumFacing.VALUES) {
				if (sideConnection[side.ordinal()] == OUTPUT || sideConnection[side.ordinal()] == EXTRACT) {
					node = true;
					break;
				}
			}
		}
		isNode = node;
		if (grid != null)
			grid.updateNodeData(this);
	}

	void extract(EnumFacing side, IFluidTank tank) {

		if (deadCache)
			return;
		if (isPowered && sideConnection[side.ordinal()] == EXTRACT) {
			if (handlerCache != null) {
				IFluidHandler fluidHandler = handlerCache[side.ordinal()];
				FluidStack e = fluidHandler.drain(TRANSFER_RATE, false);
				if (e != null && e.amount > 0) {
					fluidHandler.drain(tank.fill(e, true), true);
				}
			}
		}
	}

	int transfer(EnumFacing side, FluidStack fluid, Fluid f) {

		if (deadCache)
			return 0;
		if (sideConnection[side.ordinal()] == OUTPUT) {
			if (handlerCache != null) {
				IFluidHandler fluidHandler = handlerCache[side.ordinal()];
				if (fluidHandler != null && fluidHandler.fill(fluid, false) > 0)
					return fluidHandler.fill(fluid, true);
			}
		}
		return 0;
	}

	@Override
	public void getTileInfo(List<ITextComponent> info, EnumFacing side, EntityPlayer player, boolean debug) {

		if (grid != null) {
			info.add(text("Powered: " + isPowered));
			/* TODO: advanced monitoring
			if (isNode) {
				info.add("Throughput All: " + _grid.distribution);
				info.add("Throughput Side: " + _grid.distributionSide);
			} else//*/
			if (!debug) {
				info.add(text("Contains: " + StringHelper.getFluidName(grid.storage.getFluid(), "<Empty>")));
				info.add(text("Saturation: " +
						(Math.ceil(grid.storage.getFluidAmount() /
								(float) grid.storage.getCapacity() * 1000) / 10f)));
			}
		} else if (!debug)
			info.add(text("Null Grid"));
		if (debug) {
			if (grid != null) {
				info.add(text("Grid:" + grid));
				info.add(text("    Conduits: " + grid.getConduitCount() + ", Nodes: " + grid.getNodeCount()));
				info.add(text("    Grid Max: " + grid.storage.getCapacity() + ", Grid Cur: " +
						grid.storage.getFluidAmount()));
				info.add(text("    Contains: " + StringHelper.getFluidName(grid.storage.getFluid(), "<Empty>")));
			} else {
				info.add(text("Grid: Null"));
			}
			info.add(text("Cache: (" + Arrays.toString(handlerCache) + ")"));
			info.add(text("FluidForGrid: " + fluidForGrid));
			info.add(text("SideConnections: " + Arrays.toString(sideConnection)));
			info.add(text("Node: " + isNode));
			return;
		}
	}

	@Override
	public void addTraceableCuboids(List<IndexedCuboid6> list, boolean forTrace, boolean hasTool, boolean offsetCuboids) {

		Vector3 offset = offsetCuboids ? Vector3.fromBlockPos(pos) : new Vector3(0, 0, 0);

		IndexedCuboid6 main = new IndexedCuboid6(0, subSelection[0]); // main body
		list.add(main);

		for (EnumFacing side : EnumFacing.VALUES) {
			int i = side.ordinal();
			ConnectionType connType = sideConnection[i];
			int o = 2 + i;
			if (connType == CABLE) {
				o = 2 + 6 * 3 + i;
				list.add((IndexedCuboid6) new IndexedCuboid6(hasTool ? 2 + i : 1,
						subSelection[o]).setSide(i, i & 1).add(offset)); // cable part
			} else if (!cableOnly && (connType == EXTRACT || connType == OUTPUT)) {
				list.add((IndexedCuboid6) new IndexedCuboid6(o, subSelection[o]).add(offset)); // connection point
				o = 2 + 6 * 3 + i;
				list.add((IndexedCuboid6) new IndexedCuboid6(1, subSelection[o]).add(offset)); // cable part
			} else if (forTrace && hasTool) {
				if (!cableOnly && connType == HANDLER_DISCONNECTED) {
					list.add((IndexedCuboid6) new IndexedCuboid6(o, subSelection[o]).add(offset)); // connection point (raytrace)
				} else if (connType == CABLE_DISCONNECTED) {
					o = 2 + 6 * 3 + i;
					list.add((IndexedCuboid6) new IndexedCuboid6(2 + i,
							subSelection[o]).setSide(i, i & 1).add(offset)); // cable part
				}
			}

		}
		main.add(offset);
	}

	@Override
	public void firstTick(IGridController grid) {

	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {

		return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {

		if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
			return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY
					.cast(new TileEntityPlasticPipe.PlasticPipeFluidHandler(facing));
		}

		return super.getCapability(capability, facing);
	}

	public ConnectionType getSideConnection(int sideOrdinal) {

		return sideConnection[sideOrdinal];
	}

	public boolean isCableOnly() {

		return cableOnly;
	}

	public enum ConnectionType implements IStringSerializable {

		NONE,
		CABLE,
		CABLE_DISCONNECTED,
		EXTRACT,
		EXTRACT_POWERED,
		OUTPUT,
		HANDLER_DISCONNECTED;

		@Override
		public String getName() {

			return this.name();
		}
	}

	private class PlasticPipeFluidHandler implements IFluidHandler {

		private EnumFacing from;

		public PlasticPipeFluidHandler(EnumFacing from) {

			this.from = from;
		}

		@Override
		public IFluidTankProperties[] getTankProperties() {

			if (grid == null)
				return FluidTankProperties.convert(FluidHelper.NULL_TANK_INFO);
			return new IFluidTankProperties[] { new FluidTankProperties(grid.storage.getFluid(), grid.storage.getCapacity()) };
		}

		@Override
		public int fill(FluidStack resource, boolean doFill) {

			if (grid == null || cableOnly)
				return 0;
			ConnectionType connType = sideConnection[from.ordinal()];
			if (connType == EXTRACT && isPowered) {
				return grid.storage.fill(resource, doFill);
			}
			return 0;
		}

		@Nullable
		@Override
		public FluidStack drain(FluidStack resource, boolean doDrain) {

			if (grid == null || cableOnly)
				return null;
			ConnectionType connType = sideConnection[from.ordinal()];
			if (connType == OUTPUT) {
				return grid.storage.drain(resource, doDrain);
			}
			return null;
		}

		@Nullable
		@Override
		public FluidStack drain(int maxDrain, boolean doDrain) {

			if (grid == null || cableOnly)
				return null;
			ConnectionType connType = sideConnection[from.ordinal()];
			if (connType == OUTPUT)
				return grid.storage.drain(maxDrain, doDrain);
			return null;
		}
	}

	@Override
	protected NBTTagCompound writePacketData(NBTTagCompound tag) {

		tag.setByteArray("SideConnections", serializeSideConnections());
		tag.setBoolean("cableOnly", cableOnly);
		tag.setBoolean("isPowered", isPowered);
		tag.setByte("upgrade", (byte) upgrade.ordinal());

		return super.writePacketData(tag);
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {

		if (deadCache)
			return null;

		return super.getUpdatePacket();
	}

	@Override
	protected void handlePacketData(NBTTagCompound tag) {

		super.handlePacketData(tag);

		deserializeSideConnections(tag.getByteArray("SideConnections"));
		cableOnly = tag.getBoolean("cableOnly");
		isPowered = tag.getBoolean("isPowered");
		upgrade = PlasticPipeUpgrade.values()[tag.getByte("upgrade")];
	}

	// internal

	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);
		initialized = true;
		upgrade = PlasticPipeUpgrade.values()[nbt.getByte("Upgrade")];
		isPowered = nbt.getBoolean("Power");
		deserializeSideConnections(nbt.getByteArray("SideConnections"));
		if (nbt.hasKey("Fluid"))
			fluidForGrid = FluidStack.loadFluidStackFromNBT(nbt.getCompoundTag("Fluid"));
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		nbt = super.writeToNBT(nbt);
		nbt.setByte("Upgrade", (byte) upgrade.ordinal());
		nbt.setBoolean("Power", isPowered);
		nbt.setByteArray("SideConnections", serializeSideConnections());
		if (grid != null) {
			if (isNode) {
				fluidForGrid = grid.storage.drain(grid.getNodeShare(this), false);
			} else {
				fluidForGrid = grid.storage.drain(0, false);
			}
			if (fluidForGrid != null)
				nbt.setTag("Fluid", fluidForGrid.writeToNBT(new NBTTagCompound()));
		} else if (fluidForGrid != null)
			nbt.setTag("Fluid", fluidForGrid.writeToNBT(new NBTTagCompound()));

		return nbt;
	}

	private void deserializeSideConnections(byte[] serializedData) {

		for (int i = 0; i < 6; i++) {
			sideConnection[i] = ConnectionType.values()[serializedData[i]];
		}
	}

	private byte[] serializeSideConnections() {

		byte[] ret = new byte[6];
		for (int i = 0; i < 6; i++) {
			ret[i] = (byte) sideConnection[i].ordinal();
		}
		return ret;
	}
}
