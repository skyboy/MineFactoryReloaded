package powercrystals.minefactoryreloaded.tile.machine;

import cofh.core.util.CoreUtils;
import cofh.core.util.fluid.FluidTankAdv;
import cofh.lib.util.helpers.ItemHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;

import powercrystals.minefactoryreloaded.core.ITankContainerBucketable;
import powercrystals.minefactoryreloaded.core.UtilInventory;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryInventory;
import powercrystals.minefactoryreloaded.gui.client.GuiSteamBoiler;
import powercrystals.minefactoryreloaded.gui.container.ContainerSteamBoiler;
import powercrystals.minefactoryreloaded.setup.MFRConfig;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryInventory;

public class TileEntitySteamBoiler extends TileEntityFactoryInventory
								implements ITankContainerBucketable
{
	public static final int maxTemp = 730;
	public static final int getItemBurnTime(ItemStack stack)
	{
		// TODO: special-case some items (e.g., TE's dynamo)
		return TileEntityFurnace.getItemBurnTime(stack) / 2;
	}

	private final int _liquidId;
	private int _ticksUntilConsumption = 0;
	private int _ticksSinceLastConsumption = 0;
	private int _totalBurningTime;
	private float _temp;

	public TileEntitySteamBoiler()
	{
		super(Machine.SteamBoiler);
		setManageSolids(true);
		_liquidId = FluidRegistry.getFluid("steam").getID();
		_tanks[0].setLock(FluidRegistry.getFluid("steam"));
		_tanks[1].setLock(FluidRegistry.getFluid("water"));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiFactoryInventory getGui(InventoryPlayer inventoryPlayer)
	{
		return new GuiSteamBoiler(getContainer(inventoryPlayer), this);
	}

	@Override
	public ContainerSteamBoiler getContainer(InventoryPlayer inventoryPlayer)
	{
		return new ContainerSteamBoiler(this, inventoryPlayer);
	}

	public float getTemp()
	{
		return _temp;
	}

	public int getWorkMax()
	{
		return _ticksUntilConsumption;
	}

	public int getWorkDone()
	{
		return _ticksSinceLastConsumption;
	}

	@SideOnly(Side.CLIENT)
	public int getFuelConsumptionPerTick()
	{
		return 1 + (Math.abs(Math.max(_totalBurningTime, -180)) + 1063) / 1064;
	}

	@SideOnly(Side.CLIENT)
	public void setTemp(int temp)
	{
		_temp = (temp / 10f);
	}

	@SideOnly(Side.CLIENT)
	public void setWorkDone(int a)
	{
		_ticksSinceLastConsumption = a;
	}

	@SideOnly(Side.CLIENT)
	public void setWorkMax(int a)
	{
		_ticksUntilConsumption = a;
	}

	@Override
	public void updateEntity()
	{
		super.updateEntity();
		if (!worldObj.isRemote)
		{
			boolean active = _ticksSinceLastConsumption < _ticksUntilConsumption;
			setIsActive(active);

			if (_ticksUntilConsumption > 0)
			{
				int inc = 1 + (Math.abs(_totalBurningTime) + 1063) / 1064;
				_ticksSinceLastConsumption = Math.min(_ticksSinceLastConsumption + inc, _ticksUntilConsumption);
			}
			boolean skipConsumption = _ticksSinceLastConsumption < _ticksUntilConsumption;

			if (active)
				_totalBurningTime = Math.max(Math.min(_totalBurningTime + 1, 10649), -180);
			else if (_temp != 0)
			{
				_totalBurningTime = Math.max(_totalBurningTime - 16, -(10649 * 2));
				_ticksUntilConsumption = 0;
			}

			if (_temp == 0 && _inventory[3] == null)
			{
				if ((worldObj.getTotalWorldTime() & 0x6F) == 0 && !(_rednetState != 0 || CoreUtils.isRedstonePowered(this)))
					mergeFuel();
				return; // we're not burning anything and not changing the temp
			}

			if (_temp == maxTemp ? _totalBurningTime < 0 : (_totalBurningTime > 0 ? true : _temp != 0))
			{
				float diff = (float)Math.sqrt(Math.abs(_totalBurningTime)) / 103f;
				diff = Math.copySign(diff, _totalBurningTime) / 1.26f;

				_temp = Math.max(Math.min(_temp + (diff * diff * diff) / 50f, maxTemp), 0);
			}

			if (_temp > 80)
			{
				int maxDrain = Math.min(_tanks[0].getSpace(), 400);
				
				if ( maxDrain >= 4 ) // as it gets divided by 4, < 4 = 0 (see below)
				{
					int i = drain(_tanks[1], (maxDrain / 4), true);
					_tanks[0].fill(new FluidStack(_liquidId, i * 4), true);
				}
				
			}

			if (skipConsumption || CoreUtils.isRedstonePowered(this))
				return;

			if (consumeFuel())
				_ticksSinceLastConsumption = 0;

			mergeFuel();
		}
	}

	protected void mergeFuel()
	{
		if (_inventory[3] != null)
			for (int i = 0; _inventory[3].stackSize < _inventory[3].getMaxStackSize() && i < 3; ++i)
			{
				UtilInventory.mergeStacks(_inventory[3], _inventory[i]);
				if (_inventory[i] != null && _inventory[i].stackSize == 0)
					_inventory[i] = null;
			}
		else
			for (int i = 0; i < 3; ++i)
				if (_inventory[i] != null)
				{
					_inventory[3] = _inventory[i];
					_inventory[i] = null;
					break;
				}
	}

	protected boolean consumeFuel()
	{
		if (_inventory[3] == null)
			return false;

		int burnTime = getItemBurnTime(_inventory[3]);
		if (burnTime <= 0)
			return false;

		_ticksUntilConsumption = burnTime;
		_inventory[3] = ItemHelper.consumeItem(_inventory[3]);
		notifyNeighborTileChange();

		return true;
	}

	@Override
	public void writeToNBT(NBTTagCompound tag)
	{
		super.writeToNBT(tag);

		tag.setInteger("ticksSinceLastConsumption", _ticksSinceLastConsumption);
		tag.setInteger("ticksUntilConsumption", _ticksUntilConsumption);
		tag.setInteger("buffer", _totalBurningTime);
		tag.setFloat("temp", _temp);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		super.readFromNBT(tag);

		_ticksSinceLastConsumption = tag.getInteger("ticksSinceLastConsumption");
		_ticksUntilConsumption = tag.getInteger("ticksUntilConsumption");
		_totalBurningTime = tag.getInteger("buffer");
		_temp = tag.getFloat("temp");
	}

	//{ Solids
	@Override
	public int getSizeInventory()
	{
		return 4;
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack stack, int sideordinal)
	{
		if (stack != null)
			return getItemBurnTime(stack) > 0;

		return false;
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack itemstack, int sideordinal)
	{
		return getItemBurnTime(_inventory[slot]) <= 0;
	}
	//}

	//{ Fluids
	@Override
	protected boolean shouldPumpLiquid()
	{
		return true;
	}

	@Override
	protected boolean shouldPumpTank(IFluidTank tank)
	{
		return tank == _tanks[0];
	}

	@Override
	protected FluidTankAdv[] createTanks()
	{
		return new FluidTankAdv[] {new FluidTankAdv(BUCKET_VOLUME * 32),
				new FluidTankAdv(BUCKET_VOLUME * 16)};
	}

	@Override
	public boolean allowBucketFill(ItemStack stack)
	{
		return true;
	}

	@Override
	public boolean allowBucketDrain(ItemStack stack)
	{
		return true;
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill)
	{
		if (resource != null && resource.getFluid() == FluidRegistry.WATER)
		{
			if (MFRConfig.steamBoilerExplodes.getBoolean(false)) {
				if (_temp > 80 && _tanks[1].getFluidAmount() == 0) {
					worldObj.createExplosion(null, xCoord + 0.5d, yCoord + 0.5d, zCoord + 0.5d, 3, true);
				}
			}
			return _tanks[1].fill(resource, doFill);
		}
		return 0;
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain)
	{
		FluidTankAdv _tank = _tanks[0];
		if (_tank.getFluidAmount() > 0)
			return _tank.drain(maxDrain, doDrain);
		return null;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain)
	{
		if (resource != null)
		{
			FluidTankAdv _tank = _tanks[0];
			if (resource.isFluidEqual(_tank.getFluid()))
				return _tank.drain(resource.amount, doDrain);
		}
		return null;
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid)
	{
		return fluid == null || fluid == FluidRegistry.WATER;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid)
	{
		return true;
	}
	//}
}
