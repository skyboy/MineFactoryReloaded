package powercrystals.minefactoryreloaded.tile.machine;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.List;
import java.util.Map;
import java.util.Random;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeDirection;

import powercrystals.core.position.Area;
import powercrystals.core.position.BlockPosition;
import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.api.IFactoryFruit;
import powercrystals.minefactoryreloaded.core.FruitHarvestManager;
import powercrystals.minefactoryreloaded.core.HarvestAreaManager;
import powercrystals.minefactoryreloaded.core.HarvestMode;
import powercrystals.minefactoryreloaded.core.IHarvestManager;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryInventory;
import powercrystals.minefactoryreloaded.gui.client.GuiFruitPicker;
import powercrystals.minefactoryreloaded.gui.container.ContainerFruitPicker;
import powercrystals.minefactoryreloaded.setup.MFRConfig;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryPowered;

public class TileEntityFruitPicker extends TileEntityFactoryPowered
{
	private IHarvestManager _treeManager;
	
	private Random _rand;
	
	public TileEntityFruitPicker()
	{
		super(Machine.FruitPicker);
		_areaManager = new HarvestAreaManager(this, 1, 0, 0);
		_rand = new Random();
		setManageSolids(true);
		setCanRotate(true);
	}
	
	@Override
	public void validate()
	{
		super.validate();
		if (!worldObj.isRemote)
		{
			_treeManager = new FruitHarvestManager(worldObj,
					new Area(new BlockPosition(this), 0, 0, 0),
					HarvestMode.FruitTree);
		}
	}

	@Override
	public int getSizeInventory()
	{
		return 1;
	}
	
	@Override
	public ContainerFruitPicker getContainer(InventoryPlayer inventoryPlayer)
	{
		return new ContainerFruitPicker(this, inventoryPlayer);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public GuiFactoryInventory getGui(InventoryPlayer inventoryPlayer)
	{
		return new GuiFruitPicker(getContainer(inventoryPlayer), this);
	}
	
	@Override
	protected boolean activateMachine()
	{
		int harvestedBlockId = 0;
		int harvestedBlockMetadata = 0;
		
		BlockPosition targetCoords = getNextTree();
		if (targetCoords == null)
		{
			setIdleTicks(getIdleTicksMax());
			return false;
		}

		harvestedBlockId = worldObj.getBlockId(targetCoords.x, targetCoords.y, targetCoords.z);
		harvestedBlockMetadata = worldObj.getBlockMetadata(targetCoords.x, targetCoords.y, targetCoords.z);
		
		IFactoryFruit harvestable = MFRRegistry.getFruits().get(new Integer(harvestedBlockId));
		
		harvestable.prePick(worldObj, targetCoords.x, targetCoords.y, targetCoords.z);
		
		List<ItemStack> drops = harvestable.getDrops(worldObj, _rand, targetCoords.x, targetCoords.y, targetCoords.z);
		
		ItemStack replacement = harvestable.getReplacementBlock(worldObj, targetCoords.x, targetCoords.y, targetCoords.z);
		
		if (replacement == null)
		{
			if (!worldObj.setBlockToAir(targetCoords.x, targetCoords.y, targetCoords.z))
				return false;
			if(MFRConfig.playSounds.getBoolean(true))
			{
				worldObj.playAuxSFXAtEntity(null, 2001, targetCoords.x, targetCoords.y,
						targetCoords.z, harvestedBlockId + (harvestedBlockMetadata << 12));
			}
		}
		else
		{
			if (!worldObj.setBlock(targetCoords.x, targetCoords.y, targetCoords.z,
					replacement.itemID, replacement.getItemDamage(), 3))
				return false;
		}
		
		doDrop(drops);
		
		harvestable.postPick(worldObj, targetCoords.x, targetCoords.y, targetCoords.z);
		
		return true;
	}
	
	private BlockPosition getNextTree()
	{
		BlockPosition bp = _areaManager.getNextBlock();
		
		Integer searchId = worldObj.getBlockId(bp.x, bp.y, bp.z);
		
		if (!MFRRegistry.getFruitLogBlockIds().contains(searchId))
			return null;
		
		BlockPosition temp = getNextTreeSegment(bp);
		if (temp != null)
			_areaManager.rewindBlock();

		return temp;
	}
	
	private BlockPosition getNextTreeSegment(BlockPosition pos)
	{
		Integer blockId;
		
		if (_treeManager.getIsDone() || !_treeManager.getOrigin().equals(pos))
		{
			int lowerBound = 0;
			int upperBound = MFRConfig.fruitTreeSearchMaxVertical.getInt();

			Area a = new Area(pos.copy(), MFRConfig.fruitTreeSearchMaxHorizontal.getInt(), lowerBound, upperBound);

			_treeManager.reset(worldObj, a, HarvestMode.FruitTree);
		}
		
		Map<Integer, IFactoryFruit> fruits = MFRRegistry.getFruits(); 
		while (!_treeManager.getIsDone())
		{
			BlockPosition bp = _treeManager.getNextBlock();
			blockId = worldObj.getBlockId(bp.x, bp.y, bp.z);
			IFactoryFruit fruit = fruits.containsKey(blockId) ? fruits.get(blockId) : null;

			if (fruit != null && fruit.canBePicked(worldObj, bp.x, bp.y, bp.z))
				return bp;

			_treeManager.moveNext();
		}
		return null;
	}
	
	@Override
	public int getWorkMax()
	{
		return 1;
	}
	
	@Override
	public int getIdleTicksMax()
	{
		return 5;
	}
	
	@Override
	public boolean canInsertItem(int slot, ItemStack itemstack, int side)
	{
		return false;
	}
	
	@Override
	public boolean canExtractItem(int slot, ItemStack itemstack, int side)
	{
		return false;
	}
	
	@Override
	protected void onFactoryInventoryChanged()
	{
		_areaManager.updateUpgradeLevel(_inventory[0]);
	}
	
	@Override
	public ForgeDirection getDropDirection()
	{
		return getDirectionFacing().getOpposite();
	}
}
