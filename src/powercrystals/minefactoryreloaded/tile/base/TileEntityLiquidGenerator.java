package powercrystals.minefactoryreloaded.tile.base;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import powercrystals.core.util.Util;
import powercrystals.minefactoryreloaded.core.ITankContainerBucketable;
import powercrystals.minefactoryreloaded.gui.client.GuiLiquidGenerator;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryInventory;
import powercrystals.minefactoryreloaded.gui.container.ContainerLiquidGenerator;
import powercrystals.minefactoryreloaded.setup.Machine;

public abstract class TileEntityLiquidGenerator extends TileEntityGenerator implements ITankContainerBucketable
{
	private int _liquidConsumedPerTick;
	private int _powerProducedPerConsumption;
	private int _ticksBetweenConsumption;
	private int _outputPulseSize;
	
	private int _ticksSinceLastConsumption = 0;
	private int _bufferMax;
	private int _buffer;
	
	public TileEntityLiquidGenerator(Machine machine, int liquidConsumedPerTick,
			int ticksBetweenConsumption)
	{
		this(machine, liquidConsumedPerTick,
				machine.getActivationEnergy() * ticksBetweenConsumption,
				ticksBetweenConsumption);
	}
	
	public TileEntityLiquidGenerator(Machine machine, int liquidConsumedPerTick,
			int powerProducedPerConsumption, int ticksBetweenConsumption)
	{
		super(machine);
		assert machine.getActivationEnergy() != 0 : "Generators cannot produce 0 energy.";
		_liquidConsumedPerTick = liquidConsumedPerTick;
		_powerProducedPerConsumption = powerProducedPerConsumption;
		_ticksBetweenConsumption = ticksBetweenConsumption;
		_outputPulseSize = machine.getActivationEnergy();
		_bufferMax = machine.getMaxEnergyStorage();
	}
	
	@Override
	protected FluidTank[] createTanks()
	{
		return new FluidTank[] {new FluidTank(FluidContainerRegistry.BUCKET_VOLUME * 4)};
	}
	
	protected abstract boolean isFluidFuel(FluidStack fuel);
	
	protected String getFluidName(FluidStack fluid)
	{
		if (fluid == null || fluid.getFluid() == null)
			return null;
		String name = fluid.getFluid().getName();
		if (name == null)
			return null;
		return name.trim().toLowerCase();
	}
	
	public int getBuffer()
	{
		return _buffer;
	}
	
	public void setBuffer(int buffer)
	{
		_buffer = buffer;
	}
	
	public int getBufferMax()
	{
		return _bufferMax;
	}
	
	@Override
	public void updateEntity()
	{
		super.updateEntity();
		if(!worldObj.isRemote)
		{
			setIsActive(_buffer > _outputPulseSize * 2);
			
			boolean skipConsumption = ++_ticksSinceLastConsumption < _ticksBetweenConsumption;
			
			if(Util.isRedstonePowered(this))
			{
				return;
			}
			
			int pulse = Math.min(_buffer, _outputPulseSize);
			_buffer -= pulse;
			_buffer += producePower(pulse);
			
			if (_bufferMax - _buffer < _powerProducedPerConsumption | skipConsumption)
				return;
			
			FluidStack drained = drain(ForgeDirection.UNKNOWN, _liquidConsumedPerTick, false);
			
			if (drained == null || drained.amount != _liquidConsumedPerTick)
				return;
			
			_ticksSinceLastConsumption = 0;
			drain(ForgeDirection.UNKNOWN, _liquidConsumedPerTick, true);
			_buffer += _powerProducedPerConsumption;
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public GuiFactoryInventory getGui(InventoryPlayer inventoryPlayer)
	{
		return new GuiLiquidGenerator(getContainer(inventoryPlayer), this);
	}
	
	@Override
	public ContainerLiquidGenerator getContainer(InventoryPlayer inventoryPlayer)
	{
		return new ContainerLiquidGenerator(this, inventoryPlayer);
	}
	
	@Override
	public String getGuiBackground()
	{
		return "biofuelgenerator.png";
	}
	
	@Override
	public int getSizeInventory()
	{
		return 0;
	}
	
	@Override
	public boolean allowBucketFill()
	{
		return true;
	}
	
	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill)
	{
		if (resource != null && isFluidFuel(resource))
			for (FluidTank _tank : (FluidTank[])getTanks())
				if (_tank.getFluidAmount() == 0 || resource.isFluidEqual(_tank.getFluid()))
					return _tank.fill(resource, doFill);
		return 0;
	}
	
	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain)
	{
		for (FluidTank _tank : (FluidTank[])getTanks())
			if (_tank.getFluidAmount() > 0)
				return _tank.drain(maxDrain, doDrain);
		return null;
	}
	
	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain)
	{
		if (resource != null)
			for (FluidTank _tank : (FluidTank[])getTanks())
				if (resource.isFluidEqual(_tank.getFluid()))
					return _tank.drain(resource.amount, doDrain);
		return null;
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound)
	{
		super.writeToNBT(nbttagcompound);
		nbttagcompound.setInteger("ticksSinceLastConsumption", _ticksSinceLastConsumption);
		nbttagcompound.setInteger("buffer", _buffer);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound)
	{
		super.readFromNBT(nbttagcompound);
		
		_ticksSinceLastConsumption = nbttagcompound.getInteger("ticksSinceLastConsumption");
		_buffer = nbttagcompound.getInteger("buffer");
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid)
	{
		return true;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid)
	{
		return false;
	}
}
