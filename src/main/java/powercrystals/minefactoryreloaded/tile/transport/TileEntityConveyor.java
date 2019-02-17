package powercrystals.minefactoryreloaded.tile.transport;

import cofh.api.tileentity.IInventoryConnection;
import cofh.core.util.CoreUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import powercrystals.minefactoryreloaded.block.transport.BlockConveyor;
import powercrystals.minefactoryreloaded.core.IRotatableTile;
import powercrystals.minefactoryreloaded.core.MFRDyeColor;
import powercrystals.minefactoryreloaded.core.MFRUtil;
import powercrystals.minefactoryreloaded.tile.base.TileEntityBase;

import javax.annotation.Nonnull;

import static powercrystals.minefactoryreloaded.block.transport.BlockConveyor.ConveyorDirection.*;

public class TileEntityConveyor extends TileEntityBase
		implements IRotatableTile, ISidedInventory, /*IPipeConnection,*/ IInventoryConnection
{
	MFRDyeColor _dye = null;

	private boolean _rednetReversed = false;
	private boolean _isReversed = false;
	private boolean _gateReversed = false;

	private boolean _redNetAllowsActive = true;
	private boolean _gateAllowsActive = true;
	private boolean _conveyorActive = true;

	private boolean _isFast = false;

	public MFRDyeColor getDyeColor()
	{
		return _dye;
	}

	public void setDyeColor(MFRDyeColor dye)
	{
		if(world != null && !world.isRemote && _dye != dye)
		{
			MFRUtil.notifyBlockUpdate(world, pos);
		}
		_dye = dye;
	}

	@Override
	public NBTTagCompound writePacketData(NBTTagCompound tag)
	{
		tag.setInteger("dye", _dye == null ? -1 : _dye.getMetadata());
		tag.setBoolean("conveyorActive", _conveyorActive);
		tag.setBoolean("isFast", _isFast);

		return super.writePacketData(tag);
	}

	@Override
	public void handlePacketData(NBTTagCompound tag)
	{
		super.handlePacketData(tag);

		_dye = tag.getInteger("dye") == -1 ? null : MFRDyeColor.byMetadata(tag.getInteger("dye"));
		_conveyorActive = tag.getBoolean("conveyorActive");
		_isFast = tag.getBoolean("isFast");
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState)
	{
		return oldState.getBlock() != newState.getBlock();
	}

	@Override
	public void rotate(EnumFacing axis)
	{
		BlockConveyor.ConveyorDirection dir = world.getBlockState(pos).getValue(BlockConveyor.DIRECTION);
		if (dir == EAST)
		{
			if (isSideSolid(EnumFacing.EAST, EnumFacing.WEST))
			{
				rotateTo(world, pos, ASCENDING_EAST);
			}
			else if (isSideSolid(EnumFacing.WEST, EnumFacing.EAST))
			{
				rotateTo(world, pos, DESCENDING_EAST);
			}
			else
			{
				rotateTo(world, pos, SOUTH);
			}
		}
		else if (dir == ASCENDING_EAST)
		{
			if (isSideSolid(EnumFacing.WEST, EnumFacing.EAST))
			{
				rotateTo(world, pos, DESCENDING_EAST);
			}
			else
			{
				rotateTo(world, pos, SOUTH);
			}
		}
		else if (dir == DESCENDING_EAST)
		{
			rotateTo(world, pos, SOUTH);
		}
		else

		if (dir == SOUTH)
		{
			if (isSideSolid(EnumFacing.SOUTH, EnumFacing.NORTH))
			{
				rotateTo(world, pos, ASCENDING_SOUTH);
			}
			else if (isSideSolid(EnumFacing.NORTH, EnumFacing.SOUTH))
			{
				rotateTo(world, pos, DESCENDING_SOUTH);
			}
			else
			{
				rotateTo(world, pos, WEST);
			}
		}
		else if (dir == ASCENDING_SOUTH)
		{
			if (isSideSolid(EnumFacing.NORTH, EnumFacing.SOUTH))
			{
				rotateTo(world, pos, DESCENDING_SOUTH);
			}
			else
			{
				rotateTo(world, pos, WEST);
			}
		}
		else if (dir == DESCENDING_SOUTH)
		{
			rotateTo(world, pos, WEST);
		}
		else

		if (dir == WEST)
		{
			if (isSideSolid(EnumFacing.WEST, EnumFacing.EAST))
			{
				rotateTo(world, pos, ASCENDING_WEST);
			}
			else if (isSideSolid(EnumFacing.EAST, EnumFacing.WEST) )
			{
				rotateTo(world, pos, DESCENDING_WEST);
			}
			else
			{
				rotateTo(world, pos, NORTH);
			}
		}
		else if (dir == ASCENDING_WEST)
		{
			if (isSideSolid(EnumFacing.EAST, EnumFacing.WEST))
			{
				rotateTo(world, pos, DESCENDING_WEST);
			}
			else
			{
				rotateTo(world, pos, NORTH);
			}
		}
		else if (dir == DESCENDING_WEST)
		{
			rotateTo(world, pos, NORTH);
		}
		else

		if (dir == NORTH)
		{
			if (isSideSolid(EnumFacing.NORTH, EnumFacing.SOUTH))
			{
				rotateTo(world, pos, ASCENDING_NORTH);
			}
			else if (isSideSolid(EnumFacing.SOUTH, EnumFacing.NORTH))
			{
				rotateTo(world, pos, DESCENDING_NORTH);
			}
			else
			{
				rotateTo(world, pos, EAST);
			}
		}
		else if (dir == ASCENDING_NORTH)
		{
			if (isSideSolid(EnumFacing.SOUTH, EnumFacing.NORTH))
			{
				rotateTo(world, pos, DESCENDING_NORTH);
			}
			else
			{
				rotateTo(world, pos, EAST);
			}
		}
		else if (dir == DESCENDING_NORTH)
		{
			rotateTo(world, pos, EAST);
		}
	}

	private boolean isSideSolid(EnumFacing offset, EnumFacing dir)
	{
		return world.isSideSolid(pos.offset(offset), dir) &&
				((!world.isSideSolid(pos.offset(offset).up(), dir) ||
						!world.isAirBlock(pos.up())) ||
							!world.isSideSolid(pos.offset(offset.getOpposite()), EnumFacing.UP));
	}

	private void rotateTo(World world, BlockPos pos, BlockConveyor.ConveyorDirection newDir)
	{
		world.setBlockState(pos, world.getBlockState(pos).withProperty(BlockConveyor.DIRECTION, newDir), 2);
	}

	@Override
	public void rotateDirectlyTo(int facing)
	{
		//TODO rotateDirectlyTo in cofhcore needs to be changed to EnumFacing
		if (facing >= 2 && facing <= 5)
			rotateTo(world, pos, byFacing(EnumFacing.VALUES[facing]));
	}

	@Override
	public boolean canRotate()
	{
		return true;
	}

	@Override
	public boolean canRotate(EnumFacing axis)
	{
		return true;
	}

	@Override
	public EnumFacing getDirectionFacing()
	{
		return null;
	}

	public boolean isFast()
	{
		return _isFast;
	}

	public void setFast(boolean fast)
	{
		_isFast = fast;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag)
	{
		tag = super.writeToNBT(tag);

		tag.setInteger("dyeColor", _dye == null ? -1 : _dye.getMetadata());
		tag.setBoolean("isReversed", _isReversed);
		tag.setBoolean("redNetActive", _conveyorActive);
		tag.setBoolean("gateActive", _gateAllowsActive);
		tag.setBoolean("redNetReversed", _rednetReversed);
		tag.setBoolean("gateReversed", _gateReversed);
		tag.setBoolean("glowstone", _isFast);

		return tag;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		super.readFromNBT(tag);

		if(tag.hasKey("dyeColor"))
		{
			_dye = tag.getInteger("dyeColor") == -1 ? null : MFRDyeColor.byMetadata(tag.getInteger("dyeColor"));
		}
		if (tag.hasKey("redNetActive"))
		{
			_conveyorActive = tag.getBoolean("redNetActive");
		}
		if (tag.hasKey("gateActive"))
		{
			_gateAllowsActive = tag.getBoolean("gateActive");
		}
		_isReversed = tag.getBoolean("isReversed");
		_rednetReversed = tag.getBoolean("redNetReversed");
		_gateReversed = tag.getBoolean("gateReversed");
		_isFast = tag.getBoolean("glowstone");
	}

	//IInventory
	@Override
	public int getSizeInventory()
	{
		return 7;
	}

	@Nonnull
	@Override
	public ItemStack getStackInSlot(int slot)
	{
		return ItemStack.EMPTY;
	}

	@Nonnull
	@Override
	public ItemStack decrStackSize(int slot, int count)
	{
		return ItemStack.EMPTY;
	}

	@Nonnull
	@Override
	public ItemStack removeStackFromSlot(int slot)
	{
		return ItemStack.EMPTY;
	}

	@Override
	public void setInventorySlotContents(int slot, @Nonnull ItemStack stack)
	{
		if (stack.isEmpty())
		{
			return;
		}

		float dropOffsetX = 0.5F;
		float dropOffsetY = 0.4F;
		float dropOffsetZ = 0.5F;
		double motionX = 0.0D;
		double motionY = 0.0D;
		double motionZ = 0.0D;

		//because of the setup, slot is also the EnumFacing ordinal from which the item is being inserted
		switch(slot)
		{
			case 0: //DOWN
				dropOffsetY = 0.3F;
				motionY = 0.15D;
				break;
			case 1: //UP
				dropOffsetY = 0.8F;
				motionY = -0.15D;
				break;
			case 2: //NORTH
				dropOffsetZ = 0.2F;
				motionZ = 0.15D;
				break;
			case 3: //SOUTH
				dropOffsetZ = 0.8F;
				motionZ = -0.15D;
				break;
			case 4: //WEST
				dropOffsetX = 0.2F;
				motionX = 0.15D;
				break;
			case 5: //EAST
				dropOffsetX = 0.8F;
				motionX = -0.15D;
				break;
			case 6: //UNKNOWN
		}

		EntityItem entityitem = new EntityItem(world, pos.getX() + dropOffsetX, pos.getY() + dropOffsetY, pos.getZ() + dropOffsetZ, stack.copy());
		entityitem.motionX = motionX;
		entityitem.motionY = motionY;
		entityitem.motionZ = motionZ;
		entityitem.setPickupDelay(20);
		world.spawnEntity(entityitem);
	}

	@Override
	public String getName()
	{
		return "Conveyor Belt";
	}

	@Override
	public boolean hasCustomName()
	{
		return false;
	}

	@Override
	public int getInventoryStackLimit()
	{
		return 64;
	}

	@Override
	public void markDirty()
	{
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player)
	{
		return false;
	}

    @Override
	public void openInventory(EntityPlayer player)
    {
    }

    @Override
	public void closeInventory(EntityPlayer player)
    {
    }

    @Override
	public boolean isItemValidForSlot(int slot, @Nonnull ItemStack stack)
    {
    	return _conveyorActive;
    }

	@Override
	public int getField(int id)
	{
		return 0;
	}

	@Override
	public void setField(int id, int value)
	{
	}

	@Override
	public int getFieldCount()
	{
		return 0;
	}

	@Override
	public void clear()
	{
	}

	@Override
	public boolean isEmpty() {

		return true;
	}

	//ISidedInventory
    @Override
	public int[] getSlotsForFace(EnumFacing side)
    {
    	int[] accessibleSlot = {side.ordinal()};
    	return accessibleSlot;
    }

    /*
     * From above: returns true if the conveyor is not going uphill
     * For the NSEW sides: returns true if (conveyor is going uphill) || (!conveyor is facing in the 'from' direction)
     * From below/unknown: returns true
     */
    @Override
	public boolean canInsertItem(int slot, @Nonnull ItemStack stack, EnumFacing side)
    {
    	if (!_conveyorActive)
    		return false;

    	IBlockState state = world.getBlockState(pos);
		BlockConveyor.ConveyorDirection dir = state.getValue(BlockConveyor.DIRECTION);

		if (side == EnumFacing.UP)
			return !dir.isUphill();

		if (side != EnumFacing.DOWN)
			return dir.isUphill() || dir.getFacing() != side;

		return true;
    }

    @Override
	public boolean canExtractItem(int slot, @Nonnull ItemStack stack, EnumFacing side)
    {
    	return false;
    }

	// RedNet
	public void onRedNetChanged(int value)
	{
		if(_redNetAllowsActive ^ value <= 0)
		{
			_redNetAllowsActive = value <= 0;
			updateConveyorActive();
		}
		setReversed(_gateReversed || (_rednetReversed = value < 0));
		MFRUtil.notifyBlockUpdate(world, pos);
	}

	public void updateConveyorActive()
	{
		setConveyorActive(_gateAllowsActive && _redNetAllowsActive && !CoreUtils.isRedstonePowered(this));
	}

	public boolean getConveyorActive()
	{
		return _conveyorActive;
	}

	public void setConveyorActive(boolean conveyorActive)
	{
		boolean wasActive = _conveyorActive;
		_conveyorActive = conveyorActive;

		if(wasActive ^ _conveyorActive)
		{
			MFRUtil.notifyBlockUpdate(world, pos);
		}
	}

	public void setConveyorActiveFromGate(boolean conveyorActive)
	{
		boolean wasActive = _gateAllowsActive;
		_gateAllowsActive = conveyorActive;

		if(wasActive ^ _gateAllowsActive)
		{
			updateConveyorActive();
		}
	}

	public boolean getConveyorReversed()
	{
		return _isReversed;
	}

	private void setReversed(boolean isReversed)
	{
		boolean wasReversed = _isReversed;
		_isReversed = isReversed;

		if(wasReversed ^ _isReversed)
		{
			IBlockState state = world.getBlockState(pos);
			world.setBlockState(pos, state.withProperty(BlockConveyor.DIRECTION, state.getValue(BlockConveyor.DIRECTION).getReverse()));
		}
	}

	@SuppressWarnings("unused")
	private void reverseConveyor()
	{
		setReversed(_rednetReversed || (_gateReversed = !_isReversed));
	}

	@Override
	public ConnectionType canConnectInventory(EnumFacing from)
	{
		return ConnectionType.FORCE;
	}

/*	TODO: readd once BC team figure out what they want to do with this

@Override
	public ConnectOverride overridePipeConnection(Object type, EnumFacing with) {
		if (type == PipeType.ITEM)
			return ConnectOverride.CONNECT;
		if (with == EnumFacing.DOWN & type == PipeType.STRUCTURE)
			return ConnectOverride.CONNECT;
		return ConnectOverride.DISCONNECT;
	}*/

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {

		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {

    	if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
    		if (facing != null) {
    			return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(new SidedInvWrapper(this, facing));
			} else {
    			return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(new InvWrapper(this));
			}
		}
		return super.getCapability(capability, facing);
	}
}
