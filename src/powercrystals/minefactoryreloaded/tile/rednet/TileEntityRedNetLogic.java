package powercrystals.minefactoryreloaded.tile.rednet;

import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;

import powercrystals.core.net.PacketWrapper;
import powercrystals.core.position.BlockPosition;
import powercrystals.core.position.IRotateableTile;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.api.rednet.IConnectableRedNet;
import powercrystals.minefactoryreloaded.api.rednet.IRedNetLogicCircuit;
import powercrystals.minefactoryreloaded.api.rednet.IRedNetNetworkContainer;
import powercrystals.minefactoryreloaded.circuits.Noop;
import powercrystals.minefactoryreloaded.item.ItemLogicUpgradeCard;
import powercrystals.minefactoryreloaded.net.Packets;

public class TileEntityRedNetLogic extends TileEntity implements IRotateableTile
{
	public static class PinMapping
	{
		public PinMapping(int pin, int buffer)
		{
			this.pin = pin;
			this.buffer = buffer;
		}

		public int pin;
		public int buffer;
	}
	private static int[] CONSTS = new int[256];

	static
	{
		//init constants
		for(int i = 0, e = CONSTS.length; i < e; ++i)
		{
			CONSTS[i] = i;
		}
	}

	private int _circuitCount = 6;

	private int _variableCount = 16;

	private IRedNetLogicCircuit[] _circuits = new IRedNetLogicCircuit[_circuitCount];
	private boolean[] _updatable = new boolean[_circuitCount];

	// 0-5 in, 6-11 out, 12 const, 13 var, 14 null
	private int[][] _buffers = new int[15][];
	private int[][] _backBuffer = new int[6][];

	private BlockPosition bp = new BlockPosition(0, 0, 0);

	private PinMapping[][] _pinMappingInputs = new PinMapping[_circuitCount][];
	private PinMapping[][] _pinMappingOutputs = new PinMapping[_circuitCount][];

	private int[] _upgradeLevel = new int[6];

	public int crafters = 0; 

	public TileEntityRedNetLogic()
	{	
		// init I/O buffers
		for(int i = 0; i < 12; i++)
		{
			_buffers[i] = new int[16];
		}

		for(int i = 0; i < 6; i++)
		{
			_backBuffer[i] = new int[16];
		}

		//init constants
		_buffers[12] = CONSTS;
		// init variable buffer
		_buffers[13] = new int[_variableCount];
		// init null buffer
		_buffers[14] = new int[1];

		// init circuits
		for(int i = 0; i < _circuits.length; i++)
		{
			initCircuit(i, new Noop());
		}
	}

	public int getVariableBufferSize()
	{
		return _variableCount;
	}

	public int getCircuitCount()
	{
		return _circuitCount;
	}

	public int getBufferLength(int buffer)
	{
		return _buffers[buffer].length;
	}

	public int getVariableValue(int var)
	{
		return _buffers[13][var];
	}

	public IRedNetLogicCircuit getCircuit(int index)
	{
		return _circuits[index];
	}

	public PinMapping getInputPinMapping(int circuitIndex, int pinIndex)
	{
		return _pinMappingInputs[circuitIndex][pinIndex];
	}

	public void setInputPinMapping(int circuitIndex, int pinIndex, int buffer, int pin)
	{
		_pinMappingInputs[circuitIndex][pinIndex] = new PinMapping(pin, buffer);
	}

	public PinMapping getOutputPinMapping(int circuitIndex, int pinIndex)
	{
		return _pinMappingOutputs[circuitIndex][pinIndex];
	}

	public void setOutputPinMapping(int circuitIndex, int pinIndex, int buffer, int pin)
	{
		_pinMappingOutputs[circuitIndex][pinIndex] = new PinMapping(pin, buffer);
	}

	private IRedNetLogicCircuit getNewCircuit(String className)
	{
		try
		{
			return (IRedNetLogicCircuit) Class.forName(className).newInstance();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return new Noop();
		}
	}

	public void initCircuit(int index, String circuitClassName)
	{
		initCircuit(index, getNewCircuit(circuitClassName));
	}

	private void initCircuit(int index, IRedNetLogicCircuit circuit)
	{
		_updatable[index] = !(circuit instanceof Noop);
		_circuits[index] = circuit;
		if(_pinMappingInputs[index] == null)
		{
			_pinMappingInputs[index] = new PinMapping[_circuits[index].getInputCount()];
		}
		else
		{
			_pinMappingInputs[index] = Arrays.copyOf(_pinMappingInputs[index], _circuits[index].getInputCount());
		}

		if(_pinMappingOutputs[index] == null)
		{
			_pinMappingOutputs[index] = new PinMapping[_circuits[index].getOutputCount()];
		}
		else
		{
			_pinMappingOutputs[index] = Arrays.copyOf(_pinMappingOutputs[index], _circuits[index].getOutputCount());
		}

		for(int i = 0; i < _pinMappingInputs[index].length; i++)
		{
			if(_pinMappingInputs[index][i] == null)
			{
				_pinMappingInputs[index][i] = new PinMapping(0, 12);
			}
		}

		for(int i = 0; i < _pinMappingOutputs[index].length; i++)
		{
			if(_pinMappingOutputs[index][i] == null)
			{
				_pinMappingOutputs[index][i] = new PinMapping(0, 14);
			}
		}
	}

	public void reinitialize(EntityPlayer player)
	{
		for(int i = 0; i < _upgradeLevel.length; i++)
		{
			if(_upgradeLevel[i] > 0)
			{
				ItemStack card = new ItemStack(MineFactoryReloadedCore.logicCardItem.itemID, 1, _upgradeLevel[i] - 1);
				if(!player.inventory.addItemStackToInventory(card))
				{
					player.entityDropItem(card, 0.0F);
				}
				_upgradeLevel[i] = 0;
			}
		}
		updateUpgradeLevels();
		// init I/O buffers
		for(int i = 0; i < 12; i++)
		{
			_buffers[i] = new int[16];
		}

		for(int i = 0; i < 6; i++)
		{
			_backBuffer[i] = new int[16];
		}

		//init constants
		_buffers[12] = CONSTS;
		// init variable buffer
		_buffers[13] = new int[_variableCount];
		// init null buffer
		_buffers[14] = new int[1];

		// init circuits
		for(int i = 0; i < _circuits.length; i++)
		{
			initCircuit(i, new Noop());
			sendCircuitDefinition(i);
		}
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	public void setCircuitFromPacket(DataInputStream packet)
	{
		try
		{
			int circuitIndex = packet.readInt();
			String circuitName = packet.readUTF();

			initCircuit(circuitIndex, circuitName);

			int inputCount = packet.readInt();
			for(int p = 0; p < inputCount; p++)
			{
				int buffer = packet.readInt();
				int pin = packet.readInt();
				_pinMappingInputs[circuitIndex][p] = new PinMapping(pin, buffer);
			}

			int outputCount = packet.readInt();
			for(int p = 0; p < outputCount; p++)
			{
				int buffer = packet.readInt();
				int pin = packet.readInt();
				_pinMappingOutputs[circuitIndex][p] = new PinMapping(pin, buffer);
			}
		}
		catch(IOException x)
		{
			x.printStackTrace();
		}
	}

	public void sendCircuitDefinition(int circuit)
	{
		List<Object> data = new ArrayList<Object>();

		data.add(xCoord);
		data.add(yCoord);
		data.add(zCoord);

		data.add(circuit);

		data.add(_circuits[circuit].getClass().getName());
		data.add(_circuits[circuit].getInputCount());
		for(int p = 0; p < _pinMappingInputs[circuit].length; p++)
		{
			data.add(_pinMappingInputs[circuit][p].buffer);
			data.add(_pinMappingInputs[circuit][p].pin);
		}
		data.add(_circuits[circuit].getOutputCount());
		for(int p = 0; p < _pinMappingOutputs[circuit].length; p++)
		{
			data.add(_pinMappingOutputs[circuit][p].buffer);
			data.add(_pinMappingOutputs[circuit][p].pin);
		}

		PacketDispatcher.sendPacketToAllAround(xCoord, yCoord, zCoord, 5, worldObj.provider.dimensionId,
				PacketWrapper.createPacket(MineFactoryReloadedCore.modNetworkChannel, Packets.LogicCircuitDefinition, data.toArray()));
	}

	@Override
	public void updateEntity()
	{
		if(worldObj.isRemote)
		{
			return;
		}

		for(int i = 0; i < 6; i++)
		{
			int[] buffer = _buffers[i + 6];
			int[] backbuffer = _backBuffer[i];
			for (int j = 16; j --> 0; )
			{
				backbuffer[j] = buffer[j];
				buffer[j] = 0;
			}
		}

		for (int circuitNum = 0, e = _circuits.length; circuitNum < e; ++circuitNum)
		{	
			if (_updatable[circuitNum])
			{
				IRedNetLogicCircuit circuit = _circuits[circuitNum];
				PinMapping[] mappings = _pinMappingInputs[circuitNum];
				int[] input = new int[circuit.getInputCount()];
				for(int pinNum = 0, j = input.length; pinNum < j; ++pinNum)
				{
					PinMapping mapping = mappings[pinNum];
					input[pinNum] = _buffers[mapping.buffer][mapping.pin];
				}

				int[] output = circuit.recalculateOutputValues(worldObj.getTotalWorldTime(), input);
				mappings = _pinMappingOutputs[circuitNum];
				for(int pinNum = 0, j = output.length; pinNum < j; ++pinNum)
				{
					PinMapping mapping = mappings[pinNum];
					_buffers[mapping.buffer][mapping.pin] = output[pinNum];
				}
			}
		}

		for(int i = 0; i < 6; i++)
		{
			if(!areEqual(_backBuffer[i], _buffers[i + 6]))
			{
				bp.x = xCoord; bp.y = yCoord; bp.z = zCoord;
				ForgeDirection o = ForgeDirection.VALID_DIRECTIONS[i];
				bp.orientation = o;
				bp.moveForwards(1);
				Block b = Block.blocksList[worldObj.getBlockId(bp.x, bp.y, bp.z)];
				if(b instanceof IRedNetNetworkContainer)
				{
					((IRedNetNetworkContainer)b).updateNetwork(worldObj, bp.x, bp.y, bp.z);
				}
				else if(b instanceof IConnectableRedNet)
				{
					((IConnectableRedNet)b).onInputsChanged(worldObj, bp.x, bp.y, bp.z,
							o.getOpposite(), _buffers[i + 6]);
				}
			}
		}
	}

	public int getOutputValue(ForgeDirection side, int subnet)
	{
		if(side == ForgeDirection.UNKNOWN)
		{
			return 0;
		}
		return _buffers[side.ordinal() + 6][subnet];
	}

	public int[] getOutputValues(ForgeDirection side)
	{
		if(side == ForgeDirection.UNKNOWN)
		{
			return new int[16];
		}
		return _buffers[side.ordinal() + 6];
	}

	public void onInputsChanged(ForgeDirection side, int[] values)
	{
		if(side != ForgeDirection.UNKNOWN)
		{
			_buffers[side.ordinal()] = values;
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound)
	{
		super.writeToNBT(nbttagcompound);

		NBTTagList circuits = new NBTTagList();
		for(int c = 0; c < _circuits.length; c++)
		{
			NBTTagCompound circuit = new NBTTagCompound();
			circuit.setString("circuit", _circuits[c].getClass().getName());

			NBTTagList inputPins = new NBTTagList();
			for(int p = 0; p < _pinMappingInputs[c].length; p++)
			{
				NBTTagCompound pin = new NBTTagCompound();
				pin.setInteger("buffer", _pinMappingInputs[c][p].buffer);
				pin.setInteger("pin", _pinMappingInputs[c][p].pin);

				inputPins.appendTag(pin);
			}
			circuit.setTag("inputPins", inputPins);

			NBTTagList outputPins = new NBTTagList();
			for(int p = 0; p < _pinMappingOutputs[c].length; p++)
			{
				NBTTagCompound pin = new NBTTagCompound();
				pin.setInteger("buffer", _pinMappingOutputs[c][p].buffer);
				pin.setInteger("pin", _pinMappingOutputs[c][p].pin);

				outputPins.appendTag(pin);
			}
			circuit.setTag("outputPins", outputPins);

			NBTTagCompound circuitState = new NBTTagCompound();
			_circuits[c].writeToNBT(circuitState);
			circuit.setTag("state", circuitState);

			circuits.appendTag(circuit);
		}

		nbttagcompound.setTag("circuits", circuits);
		nbttagcompound.setIntArray("upgrades", _upgradeLevel);
		nbttagcompound.setIntArray("vars", _buffers[13]);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound)
	{
		super.readFromNBT(nbttagcompound);

		int[] upgrades = nbttagcompound.getIntArray("upgrades");
		if(upgrades != null && upgrades.length == _upgradeLevel.length)
		{
			_upgradeLevel = upgrades;
		}
		updateUpgradeLevels();

		int[] vars = nbttagcompound.getIntArray("vars");
		if(vars != null && vars.length == _buffers[13].length)
		{
			_buffers[13] = vars;
		}

		readCircuitsOnly(nbttagcompound);
	}

	public void readCircuitsOnly(NBTTagCompound nbttagcompound)
	{
		NBTTagList circuits = nbttagcompound.getTagList("circuits");
		if(circuits != null)
		{
			for(int c = 0; c < circuits.tagCount(); c++)
			{
				initCircuit(c, ((NBTTagCompound)circuits.tagAt(c)).getString("circuit"));

				NBTTagList inputPins = ((NBTTagCompound)circuits.tagAt(c)).getTagList("inputPins");
				if(inputPins != null)
				{
					for(int i = 0; i < inputPins.tagCount() && i < _pinMappingInputs[c].length; i++)
					{
						int pin = ((NBTTagCompound)inputPins.tagAt(i)).getInteger("pin");
						int buffer = ((NBTTagCompound)inputPins.tagAt(i)).getInteger("buffer");
						_pinMappingInputs[c][i] = new PinMapping(pin, buffer);
					}
				}

				NBTTagList outputPins = ((NBTTagCompound)circuits.tagAt(c)).getTagList("outputPins");
				if(outputPins != null)
				{
					for(int i = 0; i < outputPins.tagCount() && i < _pinMappingOutputs[c].length; i++)
					{
						int pin = ((NBTTagCompound)outputPins.tagAt(i)).getInteger("pin");
						int buffer = ((NBTTagCompound)outputPins.tagAt(i)).getInteger("buffer");
						_pinMappingOutputs[c][i] = new PinMapping(pin, buffer);
					}
				}

				NBTTagCompound circuitState = ((NBTTagCompound)circuits.tagAt(c)).
						getCompoundTag("state");
				if(circuitState != null)
				{
					_circuits[c].readFromNBT(circuitState);
				}
			}
		}
	}

	@Override
	public Packet getDescriptionPacket()
	{
		NBTTagCompound data = new NBTTagCompound();
		data.setIntArray("upgrades", _upgradeLevel);
		Packet132TileEntityData packet = new Packet132TileEntityData(xCoord, yCoord, zCoord, 0, data);
		return packet;
	}

	@Override
	public void onDataPacket(INetworkManager net, Packet132TileEntityData pkt)
	{
		_upgradeLevel = pkt.data.getIntArray("upgrades");
		updateUpgradeLevels();
	}

	public boolean insertUpgrade(int level)
	{
		for(int i = 0; i < 6; i++)
		{
			if(_upgradeLevel[i] == 0)
			{
				if(!worldObj.isRemote)
				{
					_upgradeLevel[i] = level;
					updateUpgradeLevels();
				}
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
				return true;
			}
		}
		return false;
	}

	public void setUpgrade(int slot, int level)
	{
		_upgradeLevel[slot] = level;
	}

	public int getLevelForSlot(int slot)
	{
		return _upgradeLevel[slot];
	}

	private void updateUpgradeLevels()
	{
		// recalculate sizes
		int circuitCount = 6;
		int variableCount = 16;
		for(int i = 0; i < _upgradeLevel.length; i++)
		{
			circuitCount += ItemLogicUpgradeCard.getCircuitsForLevel(_upgradeLevel[i]);
			variableCount += ItemLogicUpgradeCard.getVariablesForLevel(_upgradeLevel[i]);
		}

		_circuitCount = circuitCount;
		_variableCount = variableCount;

		// re-init circuit array and variable buffer
		_circuits = Arrays.copyOf(_circuits, _circuitCount);
		_updatable = Arrays.copyOf(_updatable, _circuitCount);
		_buffers[13] = Arrays.copyOf(_buffers[13], _variableCount);

		// re-init pinmapping arrays
		PinMapping[][] inputMappings = new PinMapping[_circuitCount][];
		for(int i = 0; i < inputMappings.length; i++)
		{
			if(i < _pinMappingInputs.length && _pinMappingInputs[i] != null)
			{
				inputMappings[i] = _pinMappingInputs[i];
			}
		}
		_pinMappingInputs = inputMappings;

		PinMapping[][] outputMappings = new PinMapping[_circuitCount][];
		for(int i = 0; i < outputMappings.length; i++)
		{
			if(i < _pinMappingOutputs.length && _pinMappingOutputs[i] != null)
			{
				outputMappings[i] = _pinMappingOutputs[i];
			}
		}
		_pinMappingOutputs = outputMappings;

		// finally, init any new circuits
		for(int i = 0; i < _circuits.length; i++)
		{
			if(_circuits[i] == null)
			{
				initCircuit(i, new Noop());
			}
		}
	}

	@Override
	public boolean canRotate()
	{
		return crafters == 0;
	}

	@Override
	public void rotate()
	{
		if (canRotate())
		{
			int nextMeta = (worldObj.getBlockMetadata(xCoord, yCoord, zCoord) + 1) & 3; // % 4
			worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, nextMeta, 3);
		}
	}

	@Override
	public ForgeDirection getDirectionFacing() {
		int meta = worldObj.getBlockMetadata(xCoord, yCoord, zCoord) & 3;
		return ForgeDirection.getOrientation(meta + 2);
	}

	private static boolean areEqual(int[] a, int[] b)
	{
		if ((a == null | b == null) ||
				a.length != b.length)
			return false;

		//if (a == b) return true;

		for (int i = a.length; i --> 0; )
			if (a[i] != b[i])
				return false;

		return true;
	}

    @Override
	@SideOnly(Side.CLIENT)
    public double getMaxRenderDistanceSquared()
    {
        return 4096.0D;
    }
}
