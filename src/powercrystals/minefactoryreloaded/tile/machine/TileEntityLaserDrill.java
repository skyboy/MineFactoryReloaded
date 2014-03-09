package powercrystals.minefactoryreloaded.tile.machine;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.WeightedRandomItem;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import powercrystals.core.random.WeightedRandomItemStack;
import powercrystals.core.util.UtilInventory;
import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.api.IFactoryLaserTarget;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryInventory;
import powercrystals.minefactoryreloaded.gui.client.GuiLaserDrill;
import powercrystals.minefactoryreloaded.gui.container.ContainerFactoryInventory;
import powercrystals.minefactoryreloaded.gui.container.ContainerLaserDrill;
import powercrystals.minefactoryreloaded.setup.MFRConfig;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryInventory;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityLaserDrill extends TileEntityFactoryInventory implements IFactoryLaserTarget
{
	private static final int _energyPerWork = Machine.LaserDrillPrecharger.getActivationEnergy() * 4;
	private static final int _energyStoredMax = 1000000;
	
	private int _energyStored;
	
	private int _workStoredMax = MFRConfig.laserdrillCost.getInt();
	private float _workStored;
	
	private int _bedrockLevel;
	
	private Random _rand;
	
	public static boolean canReplaceBlock(Block block, World world, int x, int y, int z)
	{
		return block == null || block.getBlockHardness(world, x, y, z) == 0 || block.isAirBlock(world, x, y, z);
	}
	
	public TileEntityLaserDrill()
	{
		super(Machine.LaserDrill);
		_rand = new Random();
		setManageSolids(true);
	}
	
	@Override
	public ContainerFactoryInventory getContainer(InventoryPlayer inventoryPlayer)
	{
		return new ContainerLaserDrill(this, inventoryPlayer);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public GuiFactoryInventory getGui(InventoryPlayer inventoryPlayer)
	{
		return new GuiLaserDrill(getContainer(inventoryPlayer), this);
	}
	
	@Override
	public boolean canFormBeamWith(ForgeDirection from)
	{
		return from.ordinal() > 1 && from.ordinal() < 6;
	}
	
	@Override
	public int addEnergy(ForgeDirection from, int energy, boolean simulate)
	{
		if (!canFormBeamWith(from))
			return energy;
		int energyToAdd = Math.min(energy, _energyStoredMax - _energyStored);
		if (!simulate)
			_energyStored += energyToAdd;
		return energy - energyToAdd;
	}
	
	@Override
	public void updateEntity()
	{
		if (isInvalid() || worldObj.isRemote)
		{
			return;
		}
		
		super.updateEntity();
		
		if (hasDrops())
			return;
		
		if(shouldCheckDrill())
		{
			updateDrill();
		}
		
		int lowerId = worldObj.getBlockId(xCoord, yCoord - 1, zCoord);
		
		if(_bedrockLevel < 0)
		{
			if(lowerId == MineFactoryReloadedCore.fakeLaserBlock.blockID)
			{
				worldObj.setBlockToAir(xCoord, yCoord - 1, zCoord);
			}
			return;
		}
		
		if(lowerId != MineFactoryReloadedCore.fakeLaserBlock.blockID && (Block.blocksList[lowerId] == null || Block.blocksList[lowerId].isAirBlock(worldObj, xCoord, yCoord - 1, zCoord)))
		{
			worldObj.setBlock(xCoord, yCoord - 1, zCoord, MineFactoryReloadedCore.fakeLaserBlock.blockID);
		}
		
		int energyToDraw = Math.min(_energyPerWork, _energyStored);
		float energyPerWorkHere = (float)(_energyPerWork * (1 - 0.2 * Math.min(yCoord - _bedrockLevel, 128.0) / 128.0));
		
		float workDone = energyToDraw / energyPerWorkHere;
		_workStored += workDone;
		_energyStored -= workDone * energyPerWorkHere;
		
		while(_workStored >= _workStoredMax)
		{
			_workStored -= _workStoredMax;
			doDrop(getRandomDrop());
		}
	}
	
	public int getWorkDone()
	{
		return (int)_workStored;
	}
	
	public void setWorkDone(int work)
	{
		_workStored = work;
	}
	
	public int getWorkMax()
	{
		return _workStoredMax;
	}
	
	public int getEnergyStored()
	{
		return _energyStored;
	}
	
	public void setEnergyStored(int energy)
	{
		_energyStored = energy;
	}
	
	public int getEnergyMax()
	{
		return _energyStoredMax;
	}
	
	private boolean shouldCheckDrill()
	{
		return worldObj.getTotalWorldTime() % 32 == 0;
	}
	
	private void updateDrill()
	{
		int y = Integer.MAX_VALUE;
		for(y = yCoord - 1; y >= 0; y--)
		{
			int id = worldObj.getBlockId(xCoord, y, zCoord);
			if (id != MineFactoryReloadedCore.fakeLaserBlock.blockID)
			{
				Block block = Block.blocksList[id];
				if (id > 0 && canReplaceBlock(block, worldObj, xCoord, y, zCoord))
					if (worldObj.setBlockToAir(xCoord, y, zCoord))
						continue;
				
				if(id == Block.bedrock.blockID)
				{
					_bedrockLevel = y;
					return;
				}
				else if (!worldObj.isAirBlock(xCoord, y, zCoord))
				{
					_bedrockLevel = -1;
					return;
				}

			}
		}
		
		_bedrockLevel = 0;
	}
	
	private ItemStack getRandomDrop()
	{
		List<WeightedRandomItemStack> drops = new LinkedList<WeightedRandomItemStack>();
		int boost = WeightedRandom.getTotalWeight(MFRRegistry.getLaserOres()) / 30;
		
		for(WeightedRandomItem i : MFRRegistry.getLaserOres())
		{
			WeightedRandomItemStack oldStack = (WeightedRandomItemStack)i;
			WeightedRandomItemStack newStack = new WeightedRandomItemStack(oldStack.itemWeight, oldStack.getStack());
			drops.add(newStack);
			for(ItemStack s : _inventory)
			{
				if(s == null || s.itemID != MineFactoryReloadedCore.laserFocusItem.itemID || MFRRegistry.getLaserPreferredOres(s.getItemDamage()) == null)
				{
					continue;
				}
				
				List<ItemStack> preferredOres = MFRRegistry.getLaserPreferredOres(s.getItemDamage());
				
				for(ItemStack preferredOre : preferredOres)
				{
					if(UtilInventory.stacksEqual(newStack.getStack(), preferredOre))
					{
						newStack.itemWeight += boost;
					}
				}
			}
		}
		
		return ((WeightedRandomItemStack)WeightedRandom.getRandomItem(_rand, drops)).getStack();
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox()
	{
		return INFINITE_EXTENT_AABB;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public double getMaxRenderDistanceSquared()
	{
		return 65536;
	}
	
	public boolean shouldDrawBeam()
	{
		if(shouldCheckDrill())
		{
			updateDrill();
		}
		return _bedrockLevel >= 0;
	}
	
	public int getBeamHeight()
	{
		return yCoord - _bedrockLevel;
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag)
	{
		super.writeToNBT(tag);
		
		tag.setInteger("energyStored", _energyStored);
		tag.setFloat("workDone", _workStored);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		super.readFromNBT(tag);
		
		_energyStored = Math.min(tag.getInteger("energyStored"), _energyStoredMax);
		_workStored = Math.min(tag.getFloat("workDone"), getWorkMax());
	}
	
	@Override
	public int getSizeInventory()
	{
		return 6;
	}
	
	@Override
	public int getInventoryStackLimit()
	{
		return 1;
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
	public boolean isUseableByPlayer(EntityPlayer entityplayer)
	{
		return entityplayer.getDistanceSq(xCoord, yCoord, zCoord) <= 64;
	}
	
	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack)
	{
		return false;
	}
}
