package powercrystals.minefactoryreloaded.tile.machine;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.api.IMobSpawnHandler;
import powercrystals.minefactoryreloaded.core.ITankContainerBucketable;
import powercrystals.minefactoryreloaded.gui.client.GuiAutoSpawner;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryInventory;
import powercrystals.minefactoryreloaded.gui.container.ContainerAutoSpawner;
import powercrystals.minefactoryreloaded.item.ItemSafariNet;
import powercrystals.minefactoryreloaded.setup.MFRConfig;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryPowered;

public class TileEntityAutoSpawner extends TileEntityFactoryPowered implements ITankContainerBucketable
{
	private static final int _spawnRange = 4;
	
	private boolean _spawnExact = false;
	
	public TileEntityAutoSpawner()
	{
		super(Machine.AutoSpawner);
		setManageSolids(true);
	}
	
	public boolean getSpawnExact()
	{
		return _spawnExact;
	}
	
	public void setSpawnExact(boolean spawnExact)
	{
		_spawnExact = spawnExact;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public GuiFactoryInventory getGui(InventoryPlayer inventoryPlayer)
	{
		return new GuiAutoSpawner(getContainer(inventoryPlayer), this);
	}
	
	@Override
	public ContainerAutoSpawner getContainer(InventoryPlayer inventoryPlayer)
	{
		return new ContainerAutoSpawner(this, inventoryPlayer);
	}
	
	@Override
	public int getSizeInventory()
	{
		return 1;
	}
	
	@Override
	protected boolean activateMachine()
	{
		ItemStack item = getStackInSlot(0);
		if(!isItemValidForSlot(0, item))
		{
			setWorkDone(0);
			return false;
		}
		NBTTagCompound itemTag = item.getTagCompound();
		String entityID = itemTag.getString("id");
		boolean isBlackListed = MFRRegistry.getAutoSpawnerBlacklist().contains(entityID);
		blackList: if (!isBlackListed)
		{
			Class<?> e = (Class<?>)EntityList.stringToClassMapping.get(entityID);
			if (e == null)
			{
				isBlackListed = true;
				break blackList;
			}
			for (Class<?> t : MFRRegistry.getAutoSpawnerClassBlacklist())
			{
				if(t.isAssignableFrom(e))
				{
					isBlackListed = true;
					break blackList;
				}
			}
		}
		if (isBlackListed)
		{
			setWorkDone(0);
			return false;
		}
		if(getWorkDone() < getWorkMax())
		{
			if (drain(_tanks[0], 10, false) == 10)
			{
				drain(_tanks[0], 10, true);
				setWorkDone(getWorkDone() + 1);
				return true;
			}
			else
			{
				return false;
			}
		}
		else
		{
			Entity spawnedEntity = EntityList.createEntityByName(entityID, worldObj);
			
			if(!(spawnedEntity instanceof EntityLivingBase))
			{
				return false;
			}
			
			EntityLivingBase spawnedLiving = (EntityLivingBase)spawnedEntity;
			
			if(_spawnExact)
			{
				NBTTagCompound tag = (NBTTagCompound)itemTag.copy();
				spawnedLiving.readEntityFromNBT(tag);
				for (int i = 0; i < 5; ++i)
				{
					if (spawnedLiving instanceof EntityLiving)
						((EntityLiving)spawnedLiving).setEquipmentDropChance(i, Float.NEGATIVE_INFINITY);
				}
			}
			
			double x = xCoord + (worldObj.rand.nextDouble() - worldObj.rand.nextDouble()) * _spawnRange;
			double y = yCoord + worldObj.rand.nextInt(3) - 1;
			double z = zCoord + (worldObj.rand.nextDouble() - worldObj.rand.nextDouble()) * _spawnRange;
			
			spawnedLiving.setLocationAndAngles(x, y, z, worldObj.rand.nextFloat() * 360.0F, 0.0F);
			
			if(!worldObj.checkNoEntityCollision(spawnedLiving.boundingBox) ||
					!worldObj.getCollidingBoundingBoxes(spawnedLiving, spawnedLiving.boundingBox).isEmpty() ||
					(worldObj.isAnyLiquid(spawnedLiving.boundingBox) != (spawnedLiving instanceof EntityWaterMob)))
			{
				return false;
			}
			
			IMobSpawnHandler handler = MFRRegistry.getSpawnHandlers().get(spawnedLiving.getClass());
			
			if (!_spawnExact)
			{
				if (spawnedLiving instanceof EntityLiving)
					((EntityLiving)spawnedLiving).onSpawnWithEgg(null);
				if (handler != null)
					handler.onMobSpawn(spawnedLiving);
			}
			else
			{
				if (handler != null)
					handler.onMobExactSpawn(spawnedLiving);
			}
			
			worldObj.spawnEntityInWorld(spawnedLiving);
			worldObj.playAuxSFX(2004, this.xCoord, this.yCoord, this.zCoord, 0);
			
			if (spawnedLiving instanceof EntityLiving)
				((EntityLiving)spawnedLiving).spawnExplosionParticle();
			setWorkDone(0);
			return true;
		}
	}
	
	@Override
	public int getWorkMax()
	{
		return _spawnExact ? MFRConfig.autospawnerCostExact.getInt() : MFRConfig.autospawnerCostStandard.getInt();
	}
	
	@Override
	public int getIdleTicksMax()
	{
		return 200;
	}
	
	@Override
	protected FluidTank[] createTanks()
	{
		return new FluidTank[] {new FluidTank(FluidContainerRegistry.BUCKET_VOLUME * 4)};
	}
	
	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill)
	{
		if(resource == null || !resource.isFluidEqual(FluidRegistry.getFluidStack("mobessence", 1)))
		{
			return 0;
		}
		return _tanks[0].fill(resource, doFill);
	}
	
	@Override
	public boolean allowBucketFill()
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
	public boolean canInsertItem(int slot, ItemStack itemstack, int side)
	{
		return isItemValidForSlot(slot, itemstack);
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack)
	{
		return ItemSafariNet.isSafariNet(itemstack) &&
				!ItemSafariNet.isSingleUse(itemstack) &&
				!ItemSafariNet.isEmpty(itemstack);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound)
	{
		super.readFromNBT(nbttagcompound);
		_spawnExact = nbttagcompound.getBoolean("spawnExact");
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound)
	{
		super.writeToNBT(nbttagcompound);
		nbttagcompound.setBoolean("spawnExact", _spawnExact);
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
