package powercrystals.minefactoryreloaded.tile.base;

import buildcraft.api.power.IPowerReceptor;
import buildcraft.api.power.PowerHandler;
import buildcraft.api.power.PowerHandler.PowerReceiver;
import buildcraft.api.transport.IPipeTile.PipeType;

import cofh.api.energy.IEnergyHandler;
import cofh.api.tileentity.IEnergyInfo;

import ic2.api.Direction;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergySink;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.MinecraftForge;

import powercrystals.core.util.Util;
import powercrystals.minefactoryreloaded.setup.Machine;

/*
 * There are three pieces of information tracked - energy, work, and idle ticks.
 * 
 * Energy is stored and used when the _machine activates. The energy stored must be >= energyActivation for the activateMachine() method to be called.
 * If activateMachine() returns true, energy will be drained.
 * 
 * Work is built up and then when at 100% something happens. This is tracked/used entirely by the derived class. If not used (f.ex. harvester), return max 1.
 * 
 * Idle ticks cause an artificial delay before activateMachine() is called again. Max should be the highest value the _machine will use, to draw the
 * progress bar correctly.
 */

public abstract class TileEntityFactoryPowered extends TileEntityFactoryInventory
											implements IPowerReceptor, IEnergySink,
														IEnergyHandler, IEnergyInfo
{	
	public static final int energyPerEU = 4;
	public static final int energyPerMJ = 10;
	
	private static final int energyFudge = 80;
	
	private int _energyStored;
	private int _maxEnergyStored;
	private int _maxEnergyTick;
	private int _energyRequiredThisTick = 0;
	
	protected int _energyActivation;
	
	private int _workDone;
	
	private int _idleTicks;
	
	// buildcraft-related fields
	
	protected PowerHandler _powerProvider;
	
	// IC2-related fields
	
	private boolean _isAddedToIC2EnergyNet;
	private boolean _addToNetOnNextTick;
	
	// constructors
	
	protected TileEntityFactoryPowered(Machine machine)
	{
		this(machine, machine.getActivationEnergy());
	}
	
	protected TileEntityFactoryPowered(Machine machine, int activationCost)
	{
		super(machine);
		_maxEnergyStored = machine.getMaxEnergyStorage();
		_powerProvider = new PowerHandler(this, PowerHandler.Type.MACHINE);
		setActivationEnergy(activationCost);
		setIsActive(false);
	}
	
	// local methods
	
	protected void setActivationEnergy(int activationCost)
	{
		_energyActivation = activationCost;
		_maxEnergyTick = Math.min(activationCost * 4, _maxEnergyStored);
		configurePowerProvider();
	}
	
	@Override
	public void updateEntity()
	{
		super.updateEntity();
		
		_energyStored = Math.min(_energyStored, getEnergyStoredMax());
		
		if(worldObj.isRemote)
		{
			return;
		}
		
		if(_addToNetOnNextTick)
		{
			if(!worldObj.isRemote)
			{
				MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
			}
			_addToNetOnNextTick = false;
			_isAddedToIC2EnergyNet = true;
		}
		
		int energyRequired = Math.min(getEnergyStoredMax() - getEnergyStored(),
				getActivationEnergy() + energyFudge);
		
		_energyRequiredThisTick = Math.max(_energyRequiredThisTick + energyRequired,
				getMaxEnergyPerTick());
		
		setIsActive(_energyStored >= _energyActivation * 2);
		
		if (failedDrops != null)
		{
			setIdleTicks(getIdleTicksMax());
			return;
		}
		
		if(Util.isRedstonePowered(this))
		{
			setIdleTicks(getIdleTicksMax());
		}
		else if(_idleTicks > 0)
		{
			_idleTicks--;
		}
		else if(_energyStored >= _energyActivation)
		{
			if(activateMachine())
			{
				_energyStored -= _energyActivation;
			}
		}
	}
	
	@Override
	public void validate()
	{
		super.validate();
		if(!_isAddedToIC2EnergyNet)
		{
			_addToNetOnNextTick = true;
		}
	}
	
	@Override
	public void onChunkUnload()
	{
		super.onChunkUnload();
		if(_isAddedToIC2EnergyNet)
		{
			if(worldObj != null && !worldObj.isRemote)
			{
				MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
			}
			_isAddedToIC2EnergyNet = false;
		}
	}
	
	protected abstract boolean activateMachine();
	
	@Override
	public void onDisassembled()
	{
		super.onDisassembled();
		if(_isAddedToIC2EnergyNet)
		{
			if(worldObj != null && !worldObj.isRemote)
			{
				MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
			}
			_isAddedToIC2EnergyNet = false;
		}
	}
	
	public int getActivationEnergy()
	{
		return _energyActivation;
	}
	
	@Override // internal method and a TE method. used for the same thing
	public int getMaxEnergyPerTick()
	{
		return _maxEnergyTick;
	}
	
	public int getEnergyStored()
	{
		return _energyStored;
	}
	
	public int getEnergyStoredMax()
	{
		return _maxEnergyStored;
	}
	
	public void setEnergyStored(int energy)
	{
		_energyStored = energy;
	}
	
	public void drainEnergy(int drainAmount)
	{
		_energyStored -= drainAmount;
	}
	
	public int getWorkDone()
	{
		return _workDone;
	}
	
	public abstract int getWorkMax();
	
	public void setWorkDone(int work)
	{
		_workDone = work;
	}
	
	public int getIdleTicks()
	{
		return _idleTicks;
	}
	
	public abstract int getIdleTicksMax();
	
	public void setIdleTicks(int ticks)
	{
		_idleTicks = ticks;
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag)
	{
		super.writeToNBT(tag);
		
		tag.setInteger("energyStored", _energyStored);
		tag.setInteger("workDone", _workDone);
		NBTTagCompound pp = new NBTTagCompound();
		_powerProvider.writeToNBT(pp);
		tag.setCompoundTag("powerProvider", pp);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		super.readFromNBT(tag);
		
		_energyStored = Math.min(tag.getInteger("energyStored"), getEnergyStoredMax());
		_workDone = Math.min(tag.getInteger("workDone"), getWorkMax());
		if (tag.hasKey("powerProvider"))
		{
			_powerProvider.readFromNBT(tag.getCompoundTag("powerProvider"));
		}
		configurePowerProvider();
	}
	
	public int getEnergyRequired()
	{
		return Math.min(getEnergyStoredMax() - getEnergyStored(), _energyRequiredThisTick);
	}
	
	public int storeEnergy(int energy, boolean doStore)
	{
		int energyInjected = Math.max(Math.min(energy, getEnergyRequired()), 0);
		if (doStore)
		{
			_energyStored += energyInjected;
			_energyRequiredThisTick -= energyInjected;
		}
		return energyInjected;
	}
	
	public int storeEnergy(int energy) { return storeEnergy(energy, true); }
	
	// TE methods

	@Override
	public int getEnergyPerTick()
	{
		return getEnergyRequired();
	}

	@Override
	public int getEnergy()
	{
		return getEnergyStored();
	}

	@Override
	public int getMaxEnergy()
	{
		return getEnergyStoredMax();
	}
	
	@Override
	public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate)
	{
		return storeEnergy(maxReceive, !simulate);
	}

	@Override
	public int extractEnergy(ForgeDirection from, int maxExtract, boolean doExtract)
	{
		return 0;
	}

	@Override
	public boolean canInterface(ForgeDirection from)
	{
		return true;
	}

	@Override
	public int getEnergyStored(ForgeDirection from)
	{
		return getEnergyStored();
	}

    @Override
	public int getMaxEnergyStored(ForgeDirection from)
	{
		return getEnergyStoredMax();
	}
	
	// BC methods
	
	protected void configurePowerProvider()
	{
		int maxReceived = getMaxEnergyPerTick() / energyPerMJ;
		_powerProvider.configure(getMinMJ(), maxReceived, 0.1f, 1000);
		_powerProvider.configurePowerPerdition(0, 0);
	}
	
	protected float getMinMJ()
	{
		return getActivationEnergy() < 100 ? 0.1f : 10f;
	}
	
	@Override
	public final PowerReceiver getPowerReceiver(ForgeDirection side)
	{
		if (getEnergyRequired() > 0)
		{
			_powerProvider.configure(getMinMJ(), getEnergyRequired() / energyPerMJ, 0.1f, 1000);
			return _powerProvider.getPowerReceiver();
		}
		_powerProvider.configure(0, 0, 1, 1000);
		return null;
	}
	
	@Override
	public final void doWork(PowerHandler pp)
	{
		bcpower: if(pp != null)
		{
			float mjRequired = getEnergyRequired() / (float)energyPerMJ;
			if (mjRequired <= 0) break bcpower;
			float mjGain = 0.002f;
			
			if(pp.useEnergy(0, mjRequired, false) > 0)
			{
				int energyGained = (int)((pp.useEnergy(0, mjRequired, true) + 0.009) * energyPerMJ);
				storeEnergy(energyGained);
				mjGain += 0.001f;
			}
			pp.addEnergy(mjGain);
		}
	}

	@Override
	public ConnectOverride overridePipeConnection(PipeType type, ForgeDirection with) {
		if (type == PipeType.POWER && getActivationEnergy() > 0)
			return ConnectOverride.CONNECT;
		return super.overridePipeConnection(type, with);
	}
	
	// IC2 methods
	
	@Override
	public double demandedEnergyUnits()
	{
		return Math.max(Math.ceil(getEnergyRequired() / (double)energyPerEU), 0);
	}
	
	@Override
	public double injectEnergyUnits(ForgeDirection from, double amount)
	{
		double euLeftOver = Math.max(amount, 0);
		euLeftOver -= storeEnergy((int)(euLeftOver * energyPerEU)) / (double)energyPerEU;
		return euLeftOver;
	}
	
	@Override
	public boolean acceptsEnergyFrom(TileEntity emitter, ForgeDirection direction)
	{
		return true;
	}
	
	@Override
	public int getMaxSafeInput()
	{
		return Integer.MAX_VALUE;
	}
	
	// IC2-lf methods
	
	public int demandsEnergy()
	{
		return Math.max(getEnergyRequired() / energyPerEU, 0);
	}

	public int injectEnergy(Direction directionFrom, int amount)
	{
		int euLeftOver = Math.max(amount, 0);
		euLeftOver -= storeEnergy(euLeftOver * energyPerEU) / energyPerEU;
		return euLeftOver;
	}
	
	public boolean acceptsEnergyFrom(TileEntity tile, Direction side)
	{
		return true;
	}
}
