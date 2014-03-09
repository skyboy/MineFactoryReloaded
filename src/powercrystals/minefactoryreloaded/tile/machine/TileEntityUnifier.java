package powercrystals.minefactoryreloaded.tile.machine;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.oredict.OreDictionary;

import powercrystals.core.oredict.OreDictTracker;
import powercrystals.core.util.UtilInventory;
import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.core.ITankContainerBucketable;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryInventory;
import powercrystals.minefactoryreloaded.gui.client.GuiUnifier;
import powercrystals.minefactoryreloaded.gui.container.ContainerUnifier;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryInventory;

public class TileEntityUnifier extends TileEntityFactoryInventory implements ITankContainerBucketable
{
	private static FluidStack _biofuel;
	private static FluidStack _ethanol;
	private static FluidStack _essence;
	private static FluidStack _liquidxp;
	private int _roundingCompensation;
	private boolean ignoreChange = false;
	
	private Map<String, ItemStack> _preferredOutputs = new HashMap<String, ItemStack>();
	
	public TileEntityUnifier()
	{
		super(Machine.Unifier);
		_roundingCompensation = 1;
		setManageSolids(true);
	}

	public static void updateUnifierLiquids()
	{
		_biofuel = FluidRegistry.getFluidStack("biofuel", 1);
		_ethanol = FluidRegistry.getFluidStack("bioethanol", 1);
		_essence = FluidRegistry.getFluidStack("mobessence", 1);
		_liquidxp = FluidRegistry.getFluidStack("immibis.liquidxp", 1);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public GuiFactoryInventory getGui(InventoryPlayer inventoryPlayer)
	{
		return new GuiUnifier(getContainer(inventoryPlayer), this);
	}
	
	@Override
	public ContainerUnifier getContainer(InventoryPlayer inventoryPlayer)
	{
		return new ContainerUnifier(this, inventoryPlayer);
	}
	
	@Override
	public boolean canUpdate()
	{
		return false;
	}
	
	private void unifyInventory()
	{
		if(worldObj != null && !worldObj.isRemote)
		{
			ItemStack output = null;
			if(_inventory[0] != null)
			{
				List<String> names = OreDictTracker.getNamesFromItem(_inventory[0]);
				// tracker does *not* also check the wildcard meta,
				// avoiding issues with saplings and logs, etc.
				
				if(names == null || names.size() != 1 || MFRRegistry.getUnifierBlacklist().containsKey(names.get(0)))
				{
					output = _inventory[0].copy();
				}
				else if(_preferredOutputs.containsKey(names.get(0)))
				{
					output = _preferredOutputs.get(names.get(0)).copy();
					output.stackSize = _inventory[0].stackSize;
				}
				else
				{
					output = OreDictionary.getOres(names.get(0)).get(0).copy();
					output.stackSize = _inventory[0].stackSize;
				}
				
				if (output != null && _inventory[0].itemID == output.itemID)
					output = _inventory[0].copy();
				
				moveItemStack(output);
			}
		}
	}
	
	private void moveItemStack(ItemStack source)
	{
		if(source == null)
		{
			return;
		}
		
		int amt = source.stackSize;
		
		if(_inventory[1] == null)
		{
			amt = Math.min(Math.min(getInventoryStackLimit(), source.getMaxStackSize()),
					source.stackSize);
		}
		else if(!UtilInventory.stacksEqual(source, _inventory[1], false))
		{
			return;
		}
		else if(source.getTagCompound() != null | _inventory[1].getTagCompound() != null)
		{
			return;
		}
		else
		{
			amt = Math.min(source.stackSize,
					_inventory[1].getMaxStackSize() - _inventory[1].stackSize);
		}
		
		if(_inventory[1] == null)
		{
			_inventory[1] = source.copy();
			_inventory[1].stackSize = amt;
			_inventory[0].stackSize -= amt;
		}
		else
		{
			_inventory[1].stackSize += amt;
			_inventory[0].stackSize -= amt;
		}
		
		if(_inventory[0].stackSize == 0)
		{
			_inventory[0] = null;
		}
	}
	
	@Override
	public void setInventorySlotContents(int slot, ItemStack stack)
	{
		_inventory[slot] = stack;
		if (slot > 1)
			updatePreferredOutput();
		if (stack != null && stack.stackSize <= 0)
			_inventory[slot] = null;
		unifyInventory();
		ignoreChange = true;
		onInventoryChanged();
		ignoreChange = false;
	}
	
	protected void updatePreferredOutput()
	{
		_preferredOutputs.clear();
		for(int i = 2; i < 11; i++)
		{
			if(_inventory[i] == null)
			{
				continue;
			}
			List<String> names = OreDictTracker.getNamesFromItem(_inventory[i]);
			if(names != null)
			{
				for(String name : names)
				{
					_preferredOutputs.put(name, _inventory[i].copy());
				}
			}
		}
	}
	
	@Override
	protected void onFactoryInventoryChanged()
	{
		if (!ignoreChange)
		{
			updatePreferredOutput();
			unifyInventory();
		}
	}
	
	@Override
	public int getSizeInventory()
	{
		return 11;
	}
	
	@Override
	public boolean shouldDropSlotWhenBroken(int slot)
	{
		return slot < 2;
	}
	
	@Override
	public int getInventoryStackLimit()
	{
		return 64;
	}
	
	@Override
	public int getSizeInventorySide(ForgeDirection side)
	{
		return 2;
	}
	
	@Override
	public boolean canInsertItem(int slot, ItemStack stack, int sideordinal)
	{
		return slot == 0;
	}
	
	@Override
	public boolean canExtractItem(int slot, ItemStack itemstack, int sideordinal)
	{
		return slot == 1;
	}
	
	@Override
	public boolean allowBucketFill()
	{
		return true;
	}
	
	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill)
	{
		if(resource == null || resource.amount == 0) return 0;
		
		FluidStack converted = unifierTransformLiquid(resource, doFill);
		
		if(converted == null || converted.amount == 0) return 0;
		
		int filled = _tanks[0].fill(converted, doFill);
		
		if(filled == converted.amount)
		{
			return resource.amount;
		}
		else
		{
			return filled * resource.amount / converted.amount +
					(resource.amount & _roundingCompensation);
		}
	}
	
	private FluidStack unifierTransformLiquid(FluidStack resource, boolean doFill)
	{
		if(_ethanol != null & _biofuel != null)
		{
			if (_ethanol.isFluidEqual(resource))
				return new FluidStack(_biofuel.fluidID, resource.amount);
			else if (_biofuel.isFluidEqual(resource))
				return new FluidStack(_ethanol.fluidID, resource.amount);
		}
		if(_essence != null & _liquidxp != null)
		{
			if (_liquidxp.isFluidEqual(resource))
				return new FluidStack(_essence.fluidID, resource.amount * 2);
			else if (_essence.isFluidEqual(resource))
			{
				if(doFill) _roundingCompensation ^= (resource.amount & 1);
				return new FluidStack(_liquidxp.fluidID, 
						resource.amount / 2 + (resource.amount & _roundingCompensation));
			}
		}
		return null;
	}
	
	@Override
	public boolean allowBucketDrain()
	{
		return true;
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
	protected FluidTank[] createTanks()
	{
		return new FluidTank[]{new FluidTank(4 * FluidContainerRegistry.BUCKET_VOLUME)};
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid)
	{
		return true;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid)
	{
		return true;
	}
}
