package powercrystals.minefactoryreloaded.tile.machine;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.Random;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeDirection;

import powercrystals.core.position.BlockPosition;
import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.api.IFactoryFertilizable;
import powercrystals.minefactoryreloaded.api.IFactoryFertilizer;
import powercrystals.minefactoryreloaded.core.HarvestAreaManager;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryInventory;
import powercrystals.minefactoryreloaded.gui.client.GuiUpgradable;
import powercrystals.minefactoryreloaded.gui.container.ContainerUpgradable;
import powercrystals.minefactoryreloaded.item.ItemUpgrade;
import powercrystals.minefactoryreloaded.setup.MFRConfig;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryPowered;

public class TileEntityFertilizer extends TileEntityFactoryPowered
{
	private Random _rand;
	
	public TileEntityFertilizer()
	{
		super(Machine.Fertilizer);
		_rand = new Random();
		_areaManager = new HarvestAreaManager(this, 1, 0, 0);
		setManageSolids(true);
		setCanRotate(true);
	}
	
	@Override
	protected void onFactoryInventoryChanged()
	{
		_areaManager.updateUpgradeLevel(_inventory[9]);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public GuiFactoryInventory getGui(InventoryPlayer inventoryPlayer)
	{
		return new GuiUpgradable(getContainer(inventoryPlayer), this);
	}
	
	@Override
	public ContainerUpgradable getContainer(InventoryPlayer inventoryPlayer)
	{
		return new ContainerUpgradable(this, inventoryPlayer);
	}
	
	@Override
	public boolean activateMachine()
	{
		BlockPosition bp = _areaManager.getNextBlock();
		
		int targetId = worldObj.getBlockId(bp.x, bp.y, bp.z);
		if(!MFRRegistry.getFertilizables().containsKey(new Integer(targetId)))
		{
			setIdleTicks(getIdleTicksMax());
			return false;
		}
		for(int stackIndex = 0; stackIndex < getSizeInventory(); stackIndex++)
		{
			ItemStack fertStack = getStackInSlot(stackIndex);
			if(fertStack == null || !MFRRegistry.getFertilizers().containsKey(new Integer(fertStack.itemID)))
			{
				continue;
			}
			IFactoryFertilizer fertilizer = MFRRegistry.getFertilizers().get(new Integer(fertStack.itemID));
			IFactoryFertilizable fertilizable = MFRRegistry.getFertilizables().get(new Integer(targetId));
			
			if(fertilizer.getFertilizerMeta() != fertStack.getItemDamage())
			{
				continue;
			}
			
			if(!fertilizable.canFertilizeBlock(worldObj, bp.x, bp.y, bp.z, fertilizer.getFertilizerType()))
			{
				continue;
			}
			if(fertilizable.fertilize(worldObj, _rand, bp.x, bp.y, bp.z, fertilizer.getFertilizerType()))
			{
				fertilizer.consume(fertStack);
				if (MFRConfig.playSounds.getBoolean(true)) // particles
					worldObj.playAuxSFXAtEntity(null, 2005, bp.x, bp.y, bp.z, _rand.nextInt(10) + 5);
				if(fertStack.stackSize <= 0)
				{
					setInventorySlotContents(stackIndex, null);
				}
				return true;
			}
		}
		
		setIdleTicks(getIdleTicksMax());
		return false;
	}
	
	@Override
	public int getSizeInventory()
	{
		return 10;
	}
	
	@Override
	public int getWorkMax()
	{
		return 1;
	}
	
	@Override
	public int getIdleTicksMax()
	{
		return 20;
	}
	
	@Override
	public int getStartInventorySide(ForgeDirection side)
	{
		return 0;
	}
	
	@Override
	public int getSizeInventorySide(ForgeDirection side)
	{
		return 9;
	}
	
	@Override
	public boolean canInsertItem(int slot, ItemStack stack, int sideordinal)
	{
		if (stack != null)
		{
			if(slot < 9)
			{
				return MFRRegistry.getFertilizers().containsKey(new Integer(stack.itemID));
			}
			else if(slot == 9)
			{
				return stack.getItem() instanceof ItemUpgrade;
			}
		}
		return false;
	}
}
