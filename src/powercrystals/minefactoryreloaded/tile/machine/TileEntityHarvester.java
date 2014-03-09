package powercrystals.minefactoryreloaded.tile.machine;

import com.google.common.collect.ImmutableMap;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

import powercrystals.core.position.Area;
import powercrystals.core.position.BlockPosition;
import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.api.HarvestType;
import powercrystals.minefactoryreloaded.api.IFactoryHarvestable;
import powercrystals.minefactoryreloaded.core.HarvestAreaManager;
import powercrystals.minefactoryreloaded.core.HarvestMode;
import powercrystals.minefactoryreloaded.core.IHarvestManager;
import powercrystals.minefactoryreloaded.core.ITankContainerBucketable;
import powercrystals.minefactoryreloaded.core.TreeHarvestManager;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryInventory;
import powercrystals.minefactoryreloaded.gui.client.GuiHarvester;
import powercrystals.minefactoryreloaded.gui.container.ContainerHarvester;
import powercrystals.minefactoryreloaded.setup.MFRConfig;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryPowered;

public class TileEntityHarvester extends TileEntityFactoryPowered implements ITankContainerBucketable
{
	private Map<String, Boolean> _settings;
	
	private Random _rand;
	
	private IHarvestManager _treeManager;
	private BlockPosition _lastTree;
	
	public TileEntityHarvester()
	{
		super(Machine.Harvester);
		_areaManager = new HarvestAreaManager(this, 1, 0, 0);
		setManageSolids(true);
		
		_settings = new HashMap<String, Boolean>();
		_settings.put("silkTouch", false);
		_settings.put("harvestSmallMushrooms", false);
		_settings.put("playSounds", MFRConfig.playSounds.getBoolean(true));
		
		_rand = new Random();
		setCanRotate(true);
	}
	
	@Override
	public void onChunkUnload()
	{
		super.onChunkUnload();
		if (_treeManager != null)
			_treeManager.free();
		_lastTree = null;
	}
	
	@Override
	public void validate()
	{
		super.validate();
		if (!worldObj.isRemote)
		{
			if (_treeManager != null)
				_treeManager.setWorld(worldObj);
			else
			{
				_treeManager = new TreeHarvestManager(worldObj,
						new Area(new BlockPosition(this),0,0,0),
						HarvestMode.FruitTree);
			}
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public GuiFactoryInventory getGui(InventoryPlayer inventoryPlayer)
	{
		return new GuiHarvester(getContainer(inventoryPlayer), this);
	}
	
	@Override
	public ContainerHarvester getContainer(InventoryPlayer inventoryPlayer)
	{
		return new ContainerHarvester(this, inventoryPlayer);
	}
	
	public Map<String, Boolean> getSettings()
	{
		return _settings;
	}
	
	public Map<String, Boolean> getImmutableSettings()
	{
		return ImmutableMap.copyOf(_settings);
	}
	
	@Override
	protected boolean shouldPumpLiquid()
	{
		return true;
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
	protected void onFactoryInventoryChanged()
	{
		_areaManager.updateUpgradeLevel(_inventory[0]);
	}
	
	@Override
	public boolean activateMachine()
	{
		BlockPosition targetCoords = getNextHarvest();
		
		if(targetCoords == null)
		{
			setIdleTicks(getIdleTicksMax());
			return false;
		}
		_settings.put("playSounds", MFRConfig.playSounds.getBoolean(true));
		
		int harvestedBlockId = worldObj.getBlockId(targetCoords.x, targetCoords.y, targetCoords.z);
		int harvestedBlockMetadata = worldObj.getBlockMetadata(targetCoords.x,
															targetCoords.y, targetCoords.z);
		
		IFactoryHarvestable harvestable = MFRRegistry.getHarvestables().
											get(new Integer(harvestedBlockId));
		
		harvestable.preHarvest(worldObj, targetCoords.x, targetCoords.y, targetCoords.z);
		
		List<ItemStack> drops = harvestable.getDrops(worldObj, _rand, getImmutableSettings(),
													targetCoords.x, targetCoords.y, targetCoords.z);
		
		if(harvestable.breakBlock())
		{
			if (!worldObj.setBlock(targetCoords.x, targetCoords.y, targetCoords.z, 0, 0, 2))
				return false;
			if(_settings.get("playSounds"))
			{
				worldObj.playAuxSFXAtEntity(null, 2001, targetCoords.x,
						targetCoords.y, targetCoords.z,
						harvestedBlockId + (harvestedBlockMetadata << 12));
			}
		}
		
		doDrop(drops);
		
		harvestable.postHarvest(worldObj, targetCoords.x, targetCoords.y, targetCoords.z);
		
		_tanks[0].fill(FluidRegistry.getFluidStack("sludge", 10), true);
		
		return true;
	}
	
	private BlockPosition getNextHarvest()
	{
		if (!_treeManager.getIsDone())
			return getNextTreeSegment(_lastTree, false);
		BlockPosition bp = _areaManager.getNextBlock();
		
		int searchId = worldObj.getBlockId(bp.x, bp.y, bp.z);
		
		if(!MFRRegistry.getHarvestables().containsKey(new Integer(searchId)))
		{
			_lastTree = null;
			return null;
		}
		
		IFactoryHarvestable harvestable = MFRRegistry.getHarvestables().get(new Integer(searchId));
		if(harvestable.canBeHarvested(worldObj, getImmutableSettings(), bp.x, bp.y, bp.z))
		{
			HarvestType type = harvestable.getHarvestType(); 
			switch (type)
			{
			case Column:
			case LeaveBottom:
				bp = getNextVertical(bp.x, bp.y, bp.z, type == HarvestType.Column ? 0 : 1);
			case Normal:
			default:
				_lastTree = null;
				return bp;
			case Tree:
			case TreeFlipped:
				BlockPosition temp = getNextTreeSegment(bp, type == HarvestType.TreeFlipped);
				if(temp != null)
					_areaManager.rewindBlock();
				return temp;
			}
		}
		_lastTree = null;
		return null;
	}
	
	private BlockPosition getNextVertical(int x, int y, int z, int startOffset)
	{
		int highestBlockOffset = -1;
		int maxBlockOffset = MFRConfig.verticalHarvestSearchMaxVertical.getInt();
		
		for (int currentYoffset = startOffset; currentYoffset < maxBlockOffset; ++currentYoffset)
		{
			int blockId = worldObj.getBlockId(x, y + currentYoffset, z);
			if(MFRRegistry.getHarvestables().containsKey(new Integer(blockId)) &&
					MFRRegistry.getHarvestables().get(new Integer(blockId)).
					canBeHarvested(worldObj, getImmutableSettings(), x, y + currentYoffset, z))
			{
				highestBlockOffset = currentYoffset;
			}
			else
			{
				break;
			}
		}
		
		if(highestBlockOffset < 0)
		{
			return null;
		}
		
		return new BlockPosition(x, y + highestBlockOffset, z);
	}
	
	private BlockPosition getNextTreeSegment(BlockPosition pos, boolean treeFlipped)
	{
		Integer blockId;
		
		if (!pos.equals(_lastTree) || _treeManager.getIsDone())
		{
			int lowerBound = 0;
			int upperBound = MFRConfig.treeSearchMaxVertical.getInt();
			if (treeFlipped)
			{
				lowerBound = upperBound;
				upperBound = 0;
			}
			
			_lastTree = new BlockPosition(pos);
			
			Area a = new Area(_lastTree, MFRConfig.treeSearchMaxHorizontal.getInt(), lowerBound, upperBound);
			
			_treeManager.reset(worldObj, a, treeFlipped ? HarvestMode.HarvestTreeInverted : HarvestMode.HarvestTree);
		}
		
		Map<Integer, IFactoryHarvestable> harvestables = MFRRegistry.getHarvestables();
		while (!_treeManager.getIsDone())
		{
			BlockPosition bp = _treeManager.getNextBlock();
			blockId = worldObj.getBlockId(bp.x, bp.y, bp.z);
			
			if (harvestables.containsKey(blockId))
			{
				IFactoryHarvestable obj = harvestables.get(blockId);
				HarvestType t = obj.getHarvestType();
				if (t == HarvestType.Tree | t == HarvestType.TreeFlipped | t == HarvestType.TreeLeaf)
					if (obj.canBeHarvested(worldObj, getImmutableSettings(), bp.x, bp.y, bp.z))
						return bp;
			}
			_treeManager.moveNext();
		}
		return null;
	}
	
	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill)
	{
		return 0;
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
	public void writeToNBT(NBTTagCompound tag)
	{
		super.writeToNBT(tag);
		NBTTagCompound list = new NBTTagCompound();
		for(Entry<String, Boolean> setting : _settings.entrySet())
		{
			list.setByte(setting.getKey(), (byte)(setting.getValue() ? 1 : 0));
		}
		tag.setTag("harvesterSettings", list);
		_treeManager.writeToNBT(tag);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		super.readFromNBT(tag);
		NBTTagCompound list = (NBTTagCompound)tag.getTag("harvesterSettings");
		if(list != null)
		{
			for(String s : _settings.keySet())
			{
				byte b = list.getByte(s); 
				if(b == 1)
				{
					_settings.put(s, true);
				}
			}
		}
		if (_treeManager != null)
			_treeManager.free();
		_treeManager = new TreeHarvestManager(tag);
		if (!_treeManager.getIsDone())
			_lastTree = _treeManager.getOrigin();
	}
	
	@Override
	public int getSizeInventory()
	{
		return 1;
	}
	
	@Override
	public int getStartInventorySide(ForgeDirection side)
	{
		return 0;
	}
	
	@Override
	public int getSizeInventorySide(ForgeDirection side)
	{
		return 0;
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid)
	{
		return false;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid)
	{
		return true;
	}
}
