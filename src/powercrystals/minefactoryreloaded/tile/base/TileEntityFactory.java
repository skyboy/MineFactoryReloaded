package powercrystals.minefactoryreloaded.tile.base;

import buildcraft.api.transport.IPipeConnection;
import buildcraft.api.transport.IPipeTile.PipeType;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import powercrystals.core.net.PacketWrapper;
import powercrystals.core.position.IRotateableTile;
import powercrystals.minefactoryreloaded.MineFactoryReloadedClient;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.core.HarvestAreaManager;
import powercrystals.minefactoryreloaded.core.IHarvestAreaContainer;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryInventory;
import powercrystals.minefactoryreloaded.gui.container.ContainerFactoryInventory;
import powercrystals.minefactoryreloaded.net.Packets;
import powercrystals.minefactoryreloaded.setup.Machine;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class TileEntityFactory extends TileEntity
									 implements IRotateableTile, IPipeConnection,
												IHarvestAreaContainer
{
	// first index is rotation, second is side
	private static final int[][] _textureSelection = new int[][]
			{
				{ 0, 1, 2, 3, 4, 5 }, // 0 D (unused)
				{ 0, 1, 2, 3, 4, 5 }, // 1 U (unused)
				{ 0, 1, 2, 3, 4, 5 }, // 2 N
				{ 0, 1, 3, 2, 5, 4 }, // 3 S
				{ 0, 1, 5, 4, 2, 3 }, // 4 W
				{ 0, 1, 4, 5, 3, 2 }, // 5 E
			};
	
	private ForgeDirection _forwardDirection;
	private boolean _canRotate = false;
	
	private boolean _isActive = false, _prevActive;
	private long _lastActive;
	
	private boolean _manageFluids = false;
	private boolean _manageSolids = false;
	
	protected int _rednetState;
	
	protected HarvestAreaManager _areaManager;
	protected Machine _machine;
	
	protected String _owner = "";
	
	protected TileEntityFactory(Machine machine)
	{
		this._machine = machine;
		_forwardDirection = ForgeDirection.NORTH;
	}
	
	@Override
	public void validate()
	{
		super.validate();
		if (worldObj.isRemote && hasHAM())
		{
			MineFactoryReloadedClient.addTileToAreaList(this);
		}
	}
	
	@Override
	public void invalidate()
	{
		super.invalidate();
		this.onChunkUnload();
	}
	
	@Override
	public void onChunkUnload()
	{
		super.onChunkUnload();
		if (worldObj.isRemote && hasHAM())
		{
			MineFactoryReloadedClient.removeTileFromAreaList(this);
		}
	}
	
	@Override
	public boolean hasHAM()
	{
		return getHAM() != null;
	}
	
	@Override
	public HarvestAreaManager getHAM()
	{
		return _areaManager;
	}
	
	public World getWorld()
	{
		return worldObj;
	}
	
	@Override
	public ForgeDirection getDirectionFacing()
	{
		return _forwardDirection;
	}
	
	@Override
	public boolean canRotate()
	{
		return _canRotate;
	}
	
	public void setCanRotate(boolean canRotate)
	{
		_canRotate = canRotate;
	}
	
	@Override
	public void rotate()
	{
		rotate(false);
	}
	
	public void rotate(boolean reverse)
	{
		if (worldObj != null && !worldObj.isRemote)
		{
			switch (reverse ? _forwardDirection.getOpposite() : _forwardDirection)
			{
			case NORTH:
				_forwardDirection = ForgeDirection.EAST;
				break;
			case EAST:
				_forwardDirection = ForgeDirection.SOUTH;
				break;
			case SOUTH:
				_forwardDirection = ForgeDirection.WEST;
				break;
			case WEST:
				_forwardDirection = ForgeDirection.NORTH;
				break;
			default:
				_forwardDirection = ForgeDirection.NORTH;
			}
			
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			PacketDispatcher.sendPacketToAllAround(xCoord, yCoord, zCoord, 50, worldObj.provider.dimensionId, getDescriptionPacket());
		}
	}
	
	public void rotateDirectlyTo(int rotation)
	{
		_forwardDirection = ForgeDirection.getOrientation(rotation);
		if (worldObj != null)
		{
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
	}
	
	public int getRotatedSide(int side)
	{
		return _textureSelection[_forwardDirection.ordinal()][side];
	}
	
	public ForgeDirection getDropDirection()
	{
		if (canRotate())
			return getDirectionFacing().getOpposite();
		return ForgeDirection.UP;
	}
	
	public ForgeDirection[] getDropDirections()
	{
		return ForgeDirection.VALID_DIRECTIONS;
	}
	
	public boolean isActive()
	{
		return _isActive;
	}
	
	public void setIsActive(boolean isActive)
	{
		if (_isActive != isActive & worldObj != null &&
				!worldObj.isRemote && _lastActive < worldObj.getTotalWorldTime())
		{
			_lastActive = worldObj.getTotalWorldTime() + 101;
			_prevActive = _isActive;
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			PacketDispatcher.sendPacketToAllAround(xCoord, yCoord, zCoord,
					50, worldObj.provider.dimensionId, getDescriptionPacket());
		}
		_isActive = isActive;
	}
	
	@Override
	public void updateEntity()
	{
		super.updateEntity();

		if (!worldObj.isRemote && _prevActive != _isActive && _lastActive < worldObj.getTotalWorldTime())
		{
			_prevActive = _isActive;
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			PacketDispatcher.sendPacketToAllAround(xCoord, yCoord, zCoord,
					50, worldObj.provider.dimensionId, getDescriptionPacket());
		}
	}
	
	public void setOwner(String owner)
	{
		if (owner == null)
			owner = "";
		if (_owner == null || _owner.isEmpty())
			_owner = owner;
	}
	
	@SideOnly(Side.CLIENT)
	public GuiFactoryInventory getGui(InventoryPlayer inventoryPlayer)
	{
		return null;
	}
	
	public ContainerFactoryInventory getContainer(InventoryPlayer inventoryPlayer)
	{
		return null;
	}
	
	public String getGuiBackground()
	{
		if (_machine == null)
			return null;
		return _machine.getName().toLowerCase() + ".png";
	}
	
	@Override
	public void onInventoryChanged()
	{
		if (worldObj != null && !worldObj.isRemote && hasHAM())
		{
			Packet packet = getHAM().getUpgradePacket(this);
			if (packet != null)
				PacketDispatcher.sendPacketToAllAround(xCoord, yCoord, zCoord,
						50, worldObj.provider.dimensionId, packet);
		}
		super.onInventoryChanged();
	}
	
	@Override
	public Packet getDescriptionPacket()
	{
		Object[] toSend = {xCoord, yCoord, zCoord, _forwardDirection.ordinal(), _isActive};
		return PacketWrapper.createPacket(MineFactoryReloadedCore.modNetworkChannel, Packets.TileDescription, toSend);
	}
	
	public void onNeighborTileChange(int x, int y, int z) {}
	
	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound)
	{
		super.readFromNBT(nbttagcompound);
		int rotation = nbttagcompound.getInteger("rotation");
		rotateDirectlyTo(rotation);
		_owner = nbttagcompound.getString("owner");
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound)
	{
		super.writeToNBT(nbttagcompound);
		nbttagcompound.setInteger("rotation", getDirectionFacing().ordinal());
		if (_owner != null)
			nbttagcompound.setString("owner", _owner);
	}
	
	public void onRedNetChanged(ForgeDirection side, int value)
	{
		_rednetState = value;
	}
	
	public int getRedNetOutput(ForgeDirection side)
	{
		return 0;
	}

	@Override
	public ConnectOverride overridePipeConnection(PipeType type, ForgeDirection with) {
		switch (type)
		{
		case FLUID:
			return manageFluids() ? ConnectOverride.CONNECT : ConnectOverride.DISCONNECT;
		case ITEM: 
			return manageSolids() ? ConnectOverride.CONNECT : ConnectOverride.DISCONNECT;
		case STRUCTURE:
			return ConnectOverride.CONNECT;
		default:
			return ConnectOverride.DISCONNECT;
		}
	}
	
	// hoisted IMachine methods
	
	public void setManageFluids(boolean manageFluids)
	{
		_manageFluids = manageFluids;
	}
	
	public boolean manageFluids()
	{
		return _manageFluids;
	}
	
	public void setManageSolids(boolean manageSolids)
	{
		_manageSolids = manageSolids;
	}
	
	public boolean manageSolids()
	{
		return _manageSolids;
	}

    @Override
	@SideOnly(Side.CLIENT)
    public double getMaxRenderDistanceSquared()
    {
        return -1D;
    }

    @Override
	public boolean shouldRenderInPass(int pass)
    {
        return pass == 0 && getMaxRenderDistanceSquared() != -1D;
    }
}
