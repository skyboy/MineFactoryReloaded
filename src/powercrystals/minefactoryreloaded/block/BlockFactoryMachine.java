package powercrystals.minefactoryreloaded.block;

import cofh.api.block.IDismantleable;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.ArrayList;

import net.minecraft.block.BlockContainer;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.Event.Result;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.IFluidContainerItem;

import powercrystals.core.position.IRotateableTile;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.api.rednet.IConnectableRedNet;
import powercrystals.minefactoryreloaded.api.rednet.RedNetConnectionType;
import powercrystals.minefactoryreloaded.core.BlockNBTManager;
import powercrystals.minefactoryreloaded.core.IEntityCollidable;
import powercrystals.minefactoryreloaded.core.ITankContainerBucketable;
import powercrystals.minefactoryreloaded.core.MFRLiquidMover;
import powercrystals.minefactoryreloaded.gui.MFRCreativeTab;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactory;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryInventory;
import powercrystals.minefactoryreloaded.tile.machine.TileEntityLaserDrill;

public class BlockFactoryMachine extends BlockContainer
							implements IConnectableRedNet, IDismantleable
{
	private int _mfrMachineBlockIndex;

	public BlockFactoryMachine(int blockId, int index)
	{
		super(blockId, Machine.MATERIAL);
		setHardness(0.5F);
		setStepSound(soundMetalFootstep);
		setCreativeTab(MFRCreativeTab.tab);
		setUnlocalizedName("mfr.machine." + index);
		_mfrMachineBlockIndex = index;
	}

	public int getBlockIndex()
	{
		return _mfrMachineBlockIndex;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister ir)
	{
		Machine.LoadTextures(_mfrMachineBlockIndex, ir);
	}

	@Override
	public Icon getBlockTexture(IBlockAccess iblockaccess, int x, int y, int z, int side)
	{
		int md = iblockaccess.getBlockMetadata(x, y, z);
		boolean isActive = false;
		TileEntity te = iblockaccess.getBlockTileEntity(x, y, z);
		if(te instanceof TileEntityFactory)
		{
			side = ((TileEntityFactory)te).getRotatedSide(side);
			isActive = ((TileEntityFactory)te).isActive();
		}
		return Machine.getMachineFromIndex(_mfrMachineBlockIndex, md).getIcon(side, isActive);
	}

	@Override
	public Icon getIcon(int side, int meta)
	{
		if(side > 1)
		{
			side += 2;
			if(side > 5)
			{
				side -= 4;
			}
		}
		return Machine.getMachineFromIndex(_mfrMachineBlockIndex, meta).getIcon(side, false);
	}

	@Override
	public int getLightOpacity(World world, int x, int y, int z)
	{
		if(world.getBlockTileEntity(x, y, z) instanceof TileEntityLaserDrill)
		{
			return 0;
		}
		return super.getLightOpacity(world, x, y, z);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z)
	{
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if (te instanceof IEntityCollidable)
		{
			float shrinkAmount = 0.125F;
			return AxisAlignedBB.getBoundingBox(x + shrinkAmount, y + shrinkAmount, z + shrinkAmount,
					x + 1 - shrinkAmount, y + 1 - shrinkAmount, z + 1 - shrinkAmount);
		}
		else
		{
			return super.getCollisionBoundingBoxFromPool(world, x, y, z);
		}
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity)
	{
		if (world.isRemote)
			return;

		TileEntity te = world.getBlockTileEntity(x, y, z);
		if (te instanceof IEntityCollidable)
			((IEntityCollidable)te).onEntityCollided(entity);
		
		super.onEntityCollidedWithBlock(world, x, y, z, entity);
	}
	
	@Override
	public void onNeighborTileChange(World world, int x, int y, int z, int tileX, int tileY, int tileZ)
    {
		TileEntity te = world.getBlockTileEntity(x, y, z);
		
		if(te instanceof TileEntityFactory)
		{
			((TileEntityFactory)te).onNeighborTileChange(tileX, tileY, tileZ);
		}
    }

	private void dropContents(TileEntity te)
	{
		if (te instanceof IInventory)
		{
			World world = te.worldObj;
			IInventory inventory = ((IInventory)te);
			TileEntityFactoryInventory factoryInv = null;
			if (te instanceof TileEntityFactoryInventory)
				factoryInv = (TileEntityFactoryInventory)te;
			
			inv: for (int i = inventory.getSizeInventory(); i --> 0 ; )
			{
				if (factoryInv != null)
					if (!factoryInv.shouldDropSlotWhenBroken(i))
						continue;

				ItemStack itemstack = inventory.getStackInSlot(i);
				if (itemstack == null)
					continue;
				inventory.setInventorySlotContents(i, null);

				do
				{
					if (itemstack.stackSize <= 0)
						continue inv;
					
					float xOffset = world.rand.nextFloat() * 0.8F + 0.1F;
					float yOffset = world.rand.nextFloat() * 0.8F + 0.1F;
					float zOffset = world.rand.nextFloat() * 0.8F + 0.1F;
					
					int amountToDrop = Math.min(world.rand.nextInt(21) + 10, itemstack.stackSize);
					
					EntityItem entityitem = new EntityItem(world,
							te.xCoord + xOffset, te.yCoord + yOffset, te.zCoord + zOffset,
							itemstack.splitStack(amountToDrop));

					float motionMultiplier = 0.05F;
					entityitem.motionX = (float)world.rand.nextGaussian() * motionMultiplier;
					entityitem.motionY = (float)world.rand.nextGaussian() * motionMultiplier + 0.2F;
					entityitem.motionZ = (float)world.rand.nextGaussian() * motionMultiplier;
					
					world.spawnEntityInWorld(entityitem);
				} while(true);
			}
		}
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, int blockId, int meta)
	{
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if (te != null)
		{
			dropContents(te);

			if (te instanceof TileEntityFactoryInventory)
				((TileEntityFactoryInventory)te).onBlockBroken();

			world.markTileEntityForDespawn(te);
			te.invalidate();
			world.removeBlockTileEntity(x, y, z);
		}
		super.breakBlock(world, x, y, z, blockId, meta);
	}

	@Override
	public ArrayList<ItemStack> getBlockDropped(World world, int x, int y, int z, int metadata,
			int fortune)
	{
		ArrayList<ItemStack> drops = new ArrayList<ItemStack>();
		ItemStack machine = new ItemStack(idDropped(blockID, world.rand, fortune), 1,
				damageDropped(metadata));
		machine.setTagCompound(BlockNBTManager.getForBlock(x, y, z));
		drops.add(machine);
		return drops;
	}

	@Override
	public ItemStack dismantleBlock(EntityPlayer player, World world, int x, int y, int z,
			boolean returnBlock)
	{
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if (te instanceof TileEntityFactory)
		{
			ItemStack machine = new ItemStack(idDropped(blockID, world.rand, 0), 1,
					damageDropped(world.getBlockMetadata(x, y, z)));
			
			dropContents(te);
			if(te instanceof TileEntityFactoryInventory)
				((TileEntityFactoryInventory)te).onDisassembled();
			
			NBTTagCompound tag = new NBTTagCompound();
			te.writeToNBT(tag);
			if (te instanceof IInventory && ((IInventory)te).isInvNameLocalized())
			{
				NBTTagCompound name = new NBTTagCompound();
				name.setString("Name", ((IInventory)te).getInvName());
				tag.setTag("display", name);
			}
			machine.setTagCompound(tag);
			
			world.setBlockToAir(x, y, z);
			if (!returnBlock)
				dropBlockAsItem_do(world, x, y, z, machine);
			return machine;
		}
		return null;
	}

	@Override
	public boolean canDismantle(EntityPlayer player, World world, int x, int y, int z)
	{
		return world.getBlockTileEntity(x, y, z) instanceof TileEntityFactory;
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity,
			ItemStack stack)
	{
		if(entity == null)
		{
			return;
		}
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if(stack.getTagCompound() != null)
		{
			stack.getTagCompound().setInteger("x", x);
			stack.getTagCompound().setInteger("y", y);
			stack.getTagCompound().setInteger("z", z);
			te.readFromNBT(stack.getTagCompound());
		}

		if(te instanceof TileEntityFactory)
		{
			if (((TileEntityFactory)te).canRotate())
				switch (MathHelper.floor_double((entity.rotationYaw * 4F) / 360F + 0.5D) & 3)
				{
				case 0:
					((TileEntityFactory)te).rotateDirectlyTo(3);
					break;
				case 1:
					((TileEntityFactory)te).rotateDirectlyTo(4);
					break;
				case 2:
					((TileEntityFactory)te).rotateDirectlyTo(2);
					break;
				case 3:
					((TileEntityFactory)te).rotateDirectlyTo(5);
					break;
				}

			if (te instanceof TileEntityFactoryInventory)
			{
				if (stack.hasDisplayName())
				{
					((TileEntityFactoryInventory)te).setInvName(stack.getDisplayName());
				}
			}
			
			if (entity instanceof ICommandSender && entity.addedToChunk)
				((TileEntityFactory)te).setOwner(((ICommandSender)entity).getCommandSenderName());
			else
				((TileEntityFactory)te).setOwner(null);
		}
	}

	@Override
	public int damageDropped(int i)
	{
		return i;
	}

	@Override
	public TileEntity createNewTileEntity(World world)
	{
		return null;
	}

	@Override
	public TileEntity createTileEntity(World world, int md)
	{
		return Machine.getMachineFromIndex(_mfrMachineBlockIndex, md).getNewTileEntity();
	}

	@Override
	public boolean rotateBlock(World world, int x, int y, int z, ForgeDirection axis)
	{
		if (world.isRemote)
		{
			return false;
		}
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if(te instanceof IRotateableTile)
		{
			IRotateableTile tile = ((IRotateableTile)te);
			if (tile.canRotate())
			{
				tile.rotate();
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean hasComparatorInputOverride()
	{
		return true;
	}

	@Override
	public int getComparatorInputOverride(World world, int x, int y, int z, int side)
	{
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if (te instanceof TileEntityFactoryInventory)
			return ((TileEntityFactoryInventory)te).getComparatorOutput(side);
		return 0;
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityplayer,
			int side, float xOffset, float yOffset, float zOffset)
	{
		PlayerInteractEvent e = new PlayerInteractEvent(entityplayer, Action.RIGHT_CLICK_BLOCK,
				x, y, z, side);
		if(MinecraftForge.EVENT_BUS.post(e) || e.getResult() == Result.DENY || e.useBlock == Result.DENY)
		{
			return false;
		}

		TileEntity te = world.getBlockTileEntity(x, y, z);
		if(te == null)
		{
			return false;
		}
		ItemStack ci = entityplayer.inventory.getCurrentItem();
		if(te instanceof ITankContainerBucketable)
		{
			boolean isFluidContainer = ci != null && ci.getItem() instanceof IFluidContainerItem;
			if(((ITankContainerBucketable)te).allowBucketDrain() &&
				(isFluidContainer || FluidContainerRegistry.isEmptyContainer(ci)))
			{
				if(MFRLiquidMover.manuallyDrainTank((ITankContainerBucketable)te, entityplayer))
				{
					return true;
				}
			}
			if(((ITankContainerBucketable)te).allowBucketFill() &&
					(isFluidContainer || FluidContainerRegistry.isFilledContainer(ci)))
			{
				if(MFRLiquidMover.manuallyFillTank((ITankContainerBucketable)te, entityplayer))
				{
					return true;
				}
			}
		}
		
		if(te instanceof TileEntityFactory &&
				((TileEntityFactory)te).getContainer(entityplayer.inventory) != null)
		{
			if(!world.isRemote)
			{
				entityplayer.openGui(MineFactoryReloadedCore.instance(), 0, world, x, y, z);
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean isBlockSolidOnSide(World world, int x, int y, int z, ForgeDirection side)
	{
		return true;
	}

	@Override
	public boolean isBlockNormalCube(World world, int x, int y, int z)
	{
		return false;
	}

	@Override
	public boolean canProvidePower()
	{
		return true;
	}

	@Override
	public int isProvidingWeakPower(IBlockAccess world, int x, int y, int z, int side)
	{
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if(te instanceof TileEntityFactory)
		{
			return ((TileEntityFactory)te).getRedNetOutput(ForgeDirection.getOrientation(side));
		}
		return 0;
	}

	@Override
	public int isProvidingStrongPower(IBlockAccess world, int x, int y, int z, int side)
	{
		return isProvidingWeakPower(world, x, y, z, side);
	}

	@Override
	public RedNetConnectionType getConnectionType(World world, int x, int y, int z, ForgeDirection side)
	{
		return RedNetConnectionType.CableSingle;
	}

	@Override
	public int[] getOutputValues(World world, int x, int y, int z, ForgeDirection side)
	{
		return null;
	}

	@Override
	public int getOutputValue(World world, int x, int y, int z, ForgeDirection side, int subnet)
	{
		return 0;
	}

	@Override
	public void onInputsChanged(World world, int x, int y, int z, ForgeDirection side, int[] inputValues)
	{
	}

	@Override
	public void onInputChanged(World world, int x, int y, int z, ForgeDirection side, int inputValue)
	{
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if(te instanceof TileEntityFactory)
		{
			((TileEntityFactory)te).onRedNetChanged(side, inputValue);
			onNeighborBlockChange(world, x, y, z, MineFactoryReloadedCore.rednetCableBlock.blockID);
		}
	}
}
