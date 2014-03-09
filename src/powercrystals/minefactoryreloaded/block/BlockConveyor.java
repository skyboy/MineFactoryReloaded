package powercrystals.minefactoryreloaded.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.particle.EntityDiggingFX;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

import powercrystals.core.position.BlockPosition;
import powercrystals.core.position.IRotateableTile;
import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.api.rednet.IConnectableRedNet;
import powercrystals.minefactoryreloaded.api.rednet.RedNetConnectionType;
import powercrystals.minefactoryreloaded.core.BlockNBTManager;
import powercrystals.minefactoryreloaded.core.IEntityCollidable;
import powercrystals.minefactoryreloaded.core.MFRUtil;
import powercrystals.minefactoryreloaded.gui.MFRCreativeTab;
import powercrystals.minefactoryreloaded.item.ItemPlasticBoots;
import powercrystals.minefactoryreloaded.tile.conveyor.TileEntityConveyor;

public class BlockConveyor extends BlockContainer implements IConnectableRedNet
{
	private String[] _names = new String[]
			{ "white", "orange", "magenta", "lightblue", "yellow", "lime", "pink", "gray", "lightgray", "cyan", "purple", "blue", "brown", "green", "red", "black", "default" };
	private Icon[] _iconsActive = new Icon[_names.length];
	private Icon[] _iconsStopped = new Icon[_names.length];
	
	public BlockConveyor(int id)
	{
		super(id, Material.circuits);
		setHardness(0.5F);
		setUnlocalizedName("mfr.conveyor");
		setBlockBounds(0.0F, 0.0F, 0.0F, 0.1F, 0.01F, 0.1F);
		setCreativeTab(MFRCreativeTab.tab);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister ir)
	{
		for(int i = 0; i < _names.length; i++)
		{
			_iconsActive[i] = ir.registerIcon("minefactoryreloaded:" + getUnlocalizedName() + ".active." + _names[i]);
			_iconsStopped[i] = ir.registerIcon("minefactoryreloaded:" + getUnlocalizedName() + ".stopped." + _names[i]);
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean addBlockHitEffects(World world, MovingObjectPosition target, EffectRenderer effectRenderer)
	{
		int x = target.blockX, y = target.blockY, z = target.blockZ;
		TileEntity tile = world.getBlockTileEntity(x, y, z);

		if (tile instanceof TileEntityConveyor)
		{
			float f = 0.1F;
			Random rand = new Random();
			double d0 = x + rand.nextDouble() * (getBlockBoundsMaxX() - getBlockBoundsMinX() - (f * 2.0F)) + f + getBlockBoundsMinX();
			double d1 = y + rand.nextDouble() * (getBlockBoundsMaxY() - getBlockBoundsMinY() - (f * 2.0F)) + f + getBlockBoundsMinY();
			double d2 = z + rand.nextDouble() * (getBlockBoundsMaxZ() - getBlockBoundsMinZ() - (f * 2.0F)) + f + getBlockBoundsMinZ();

			switch (target.sideHit)
			{
			case 0:
				d1 = y + getBlockBoundsMinY() - f;
				break;
			case 1:
				d1 = y + getBlockBoundsMaxY() + f;
				break;
			case 2:
				d2 = z + getBlockBoundsMinZ() - f;
				break;
			case 3:
				d2 = z + getBlockBoundsMaxZ() + f;
				break;
			case 4:
				d0 = x + getBlockBoundsMinX() - f;
				break;
			case 5:
				d0 = x + getBlockBoundsMaxX() + f;
				break;
			}

			effectRenderer.addEffect((new EntityDiggingFX(world, d0, d1, d2, 0.0D, 0.0D, 0.0D, this,
					getDamageValue(world, x, y, z))).applyColourMultiplier(x, y, z).
					multiplyVelocity(0.2F).multipleParticleScaleBy(0.6F));
			return true;
		}
		return false;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean addBlockDestroyEffects(World world, int x, int y, int z, int meta, EffectRenderer effectRenderer)
	{
		TileEntity tile = world.getBlockTileEntity(x, y, z);

		if (tile instanceof TileEntityConveyor)
		{
			int particles = 4 - (Minecraft.getMinecraft().gameSettings.particleSetting * 2);
			particles &= ~particles >> 31;

			for (int xOff = 0; xOff < particles; ++xOff)
			{
				for (int yOff = 0; yOff < particles; ++yOff)
				{
					for (int zOff = 0; zOff < particles; ++zOff)
					{
						double d0 = x + (xOff + 0.5D) / particles;
						double d1 = y + (yOff + 0.5D) / particles;
						double d2 = z + (zOff + 0.5D) / particles;
						effectRenderer.addEffect((new EntityDiggingFX(world, d0, d1, d2, 
								d0 - x - 0.5D, d1 - y - 0.5D, d2 - z - 0.5D,
								this, getDamageValue(world, x, y, z))).
								applyColourMultiplier(x, y, z));
					}
				}
			}
			return true;
		}
		return false;
	}
	
	@Override
	public boolean recolourBlock(World world, int x, int y, int z, ForgeDirection side, int colour)
	{
		TileEntity tile = world.getBlockTileEntity(x, y, z);

		if (tile instanceof TileEntityConveyor)
		{
			int dye = ((TileEntityConveyor)tile).getDyeColor();
			((TileEntityConveyor)tile).setDyeColor(colour);
			return dye != ((TileEntityConveyor)tile).getDyeColor();
		}
		return false;
	}
	
	@Override
	public Icon getBlockTexture(IBlockAccess iblockaccess, int x, int y, int z, int side)
	{
		TileEntity te = iblockaccess.getBlockTileEntity(x, y, z);
		if(te instanceof TileEntityConveyor)
		{
			int dyeColor = ((TileEntityConveyor)te).getDyeColor();
			if(dyeColor == -1) dyeColor = 16;
			if(((TileEntityConveyor)te).getConveyorActive())
			{
				return _iconsActive[dyeColor];
			}
			else
			{
				return _iconsStopped[dyeColor];
			}
		}
		else
		{
			return _iconsStopped[_iconsStopped.length - 1];
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public Icon getIcon(int side, int meta)
	{
		return _iconsStopped[meta];
	}
	
	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack)
	{
		if(entity == null)
		{
			return;
		}
		int facing = MathHelper.floor_double((entity.rotationYaw * 4F) / 360F + 0.5D) & 3;
		if(facing == 0)
		{
			world.setBlockMetadataWithNotify(x, y, z, 1, 2);
		}
		if(facing == 1)
		{
			world.setBlockMetadataWithNotify(x, y, z, 2, 2);
		}
		if(facing == 2)
		{
			world.setBlockMetadataWithNotify(x, y, z, 3, 2);
		}
		if(facing == 3)
		{
			world.setBlockMetadataWithNotify(x, y, z, 0, 2);
		}
		
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if(te instanceof TileEntityConveyor)
		{
			((TileEntityConveyor)te).setDyeColor(stack.getItemDamage() == 16 ? -1 : stack.getItemDamage());
		}
	}
	
	@Override
    public int getDamageValue(World world, int x, int y, int z)
    {
		TileEntity te = world.getBlockTileEntity(x, y, z);
		int dyeColor = 16;
		if(te instanceof TileEntityConveyor)
		{
			dyeColor = ((TileEntityConveyor)te).getDyeColor();
			if(dyeColor == -1) dyeColor = 16;
		}
		return dyeColor;
    }
	
	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity)
	{
		boolean isItem = entity instanceof EntityItem || entity instanceof EntityXPOrb;
		if (!isItem)
			for (Class<?> blacklist : MFRRegistry.getConveyerBlacklist())
				if (blacklist.isInstance(entity))
					return;
		
		if (!(isItem || entity instanceof EntityLivingBase || entity instanceof EntityTNTPrimed))
			return;
		
		TileEntity conveyor = world.getBlockTileEntity(x, y, z);
		if(!(conveyor instanceof TileEntityConveyor && ((TileEntityConveyor)conveyor).getConveyorActive()))
		{
			return;
		}
		
		if (!world.isRemote)
		l: {
			if (entity instanceof EntityItem)
				specialRoute(world, x, y, z, (EntityItem)entity);
			else if (entity instanceof EntityLivingBase)
			{
				ItemStack item = ((EntityLivingBase)entity).getCurrentItemOrArmor(1);
				if (item == null) break l;
				if (item.getItem() instanceof ItemPlasticBoots)
					return;
			}
		}
		
		double xVelocity = 0;
		double yVelocity = 0;
		double zVelocity = 0;
		
		int md = world.getBlockMetadata(x, y, z);
		
		int horizDirection = md & 0x03;
		boolean isUphill = (md & 0x04) != 0;
		boolean isDownhill = (md & 0x08) != 0;
		
		if(isUphill)
		{
			yVelocity = 0.17D;
		}
		else if (entity.posY - y < 0.1 & entity.posY - y > 0)
		{
			entity.posY = y + 0.1;
		}
		
		if(isUphill || isDownhill)
		{
			entity.onGround = false;
		}
		
		if(horizDirection == 0)
		{
			xVelocity = 0.1D;
		}
		else if(horizDirection == 1)
		{
			zVelocity = 0.1D;
		}
		else if(horizDirection == 2)
		{
			xVelocity = -0.1D;
		}
		else if(horizDirection == 3)
		{
			zVelocity = -0.1D;
		}
		
		if(horizDirection == 0 || horizDirection == 2)
		{
			if(entity.posZ > z + 0.55D)
			{
				zVelocity = -0.1D;
			}
			else if(entity.posZ < z + 0.45D)
			{
				zVelocity = 0.1D;
			}
		}
		else if(horizDirection == 1 || horizDirection == 3)
		{
			if(entity.posX > x + 0.55D)
			{
				xVelocity = -0.1D;
			}
			else if(entity.posX < x + 0.45D)
			{
				xVelocity = 0.1D;
			}
		}
		
		setEntityVelocity(entity, xVelocity, yVelocity, zVelocity);
		
		if(entity instanceof EntityLivingBase)
		{
			((EntityLivingBase)entity).fallDistance = 0;
		}
		else if(entity instanceof EntityItem)
		{
			((EntityItem)entity).delayBeforeCanPickup = 40;
		}
	}
	
	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z)
	{
		int md = world.getBlockMetadata(x, y, z);
		
		if((md & 0x0C) == 0)
		{
			return AxisAlignedBB.getAABBPool().getAABB(x + 0.05F, y, z + 0.05F, x + 1 - 0.05F, y + 0.01F, z + 1 - 0.05F);
		}
		else
		{
			return AxisAlignedBB.getAABBPool().getAABB(x + 0.1F, y, z + 0.1F, x + 1 - 0.1F, y + 0.01F, z + 1 - 0.1F);
		}
	}
	
	@Override
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z)
	{
		int md = world.getBlockMetadata(x, y, z);
		
		if((md & 0x0C) == 0)
		{
			return AxisAlignedBB.getAABBPool().getAABB(x + 0.05F, y, z + 0.05F, x + 1 - 0.05F, y + 0.1F, z + 1 - 0.05F);
		}
		else
		{
			return AxisAlignedBB.getAABBPool().getAABB(x + 0.1F, y, z + 0.1F, x + 1 - 0.1F, y + 0.1F, z + 1 - 0.1F);
		}
	}
	
	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}
	
	@Override
	public MovingObjectPosition collisionRayTrace(World world, int i, int j, int k, Vec3 vec3d, Vec3 vec3d1)
	{
		setBlockBoundsBasedOnState(world, i, j, k);
		return super.collisionRayTrace(world, i, j, k, vec3d, vec3d1);
	}
	
	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess iblockaccess, int i, int j, int k)
	{
		int l = iblockaccess.getBlockMetadata(i, j, k);
		if(l >= 4 && l <= 11)
		{
			setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
		}
		else
		{
			setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
		}
	}
	
	@Override
	public boolean renderAsNormalBlock()
	{
		return false;
	}
	
	@Override
	public int getRenderType()
	{
		return MineFactoryReloadedCore.renderIdConveyor;
	}
	
	@Override
	public int quantityDropped(Random random)
	{
		return 1;
	}
	
	@Override
	public boolean canPlaceBlockAt(World world, int x, int y, int z)
	{
		return canBlockStay(world, x, y, z);
	}
	
	@Override
	public boolean canBlockStay(World world, int x, int y, int z)
	{
		return world.isBlockSolidOnSide(x, y - 1, z, ForgeDirection.UP);
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
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityplayer, int side, float xOffset, float yOffset, float zOffset)
	{
		if(MFRUtil.isHoldingHammer(entityplayer))
		{
			TileEntity te = world.getBlockTileEntity(x, y, z);
			if(te instanceof IRotateableTile)
			{
				((IRotateableTile)te).rotate();
			}
			return true;
		}
		return false;
	}
	
	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, int neighborId)
	{
		if(!canBlockStay(world, x, y, z))
		{
			world.setBlockToAir(x, y, z);
			dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
			return;
		}

		TileEntity tec = world.getBlockTileEntity(x, y, z);
		if(tec instanceof TileEntityConveyor)
		{
			((TileEntityConveyor)tec).updateConveyorActive();
		}
	}
	
	private void setEntityVelocity(Entity e, double x, double y, double z)
	{
		e.motionX = x;
		e.motionY = y;
		e.motionZ = z;
	}
	
	@Override
	public TileEntity createNewTileEntity(World world)
	{
		return new TileEntityConveyor();
	}
	
	@Override
	public ArrayList<ItemStack> getBlockDropped(World world, int x, int y, int z, int metadata, int fortune)
	{
		ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
		NBTTagCompound tag = BlockNBTManager.getForBlock(x, y, z);
		if (tag != null)
		{
			ItemStack r = ItemStack.loadItemStackFromNBT(tag);
			if (r != null)
				ret.add(r);
		}
		else if (world.getBlockId(x, y, z) == blockID)
		{
			ret.add(new ItemStack(blockID, 1, getDamageValue(world, x, y, z)));
			world.setBlockMetadataWithNotify(x, y, z, 15, 4);
		}
		return ret;
	}
	
	@Override
	public void onBlockHarvested(World world, int x, int y, int z, int meta, EntityPlayer player)
	{
		if (player.capabilities.isCreativeMode)
			world.setBlockMetadataWithNotify(x, y, z, 15, 4);
	}
	
	@Override
	public void breakBlock(World world, int x, int y, int z, int blockId, int meta)
	{
		if (meta != 15)
			BlockNBTManager.setForBlock(new BlockPosition(x, y, z),
					new ItemStack(blockID, 1, getDamageValue(world, x, y, z)).
					writeToNBT(new NBTTagCompound()));
		super.breakBlock(world, x, y, z, blockId, meta);
	}
	
	@Override
	public boolean canProvidePower()
	{
		return false;
	}
	
	// IConnectableRedNet
	@Override
	public RedNetConnectionType getConnectionType(World world, int x, int y, int z, ForgeDirection side)
	{
		return RedNetConnectionType.PlateSingle;
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
		if(te instanceof TileEntityConveyor)
		{
			((TileEntityConveyor)te).onRedNetChanged(inputValue);
		}
	}
	
	private void specialRoute(World world, int x, int y, int z, EntityItem entityitem)
	{
		TileEntity teBelow = world.getBlockTileEntity(x, y - 1, z);
		if(teBelow == null || entityitem.isDead)
		{
			return;
		}
		else if (teBelow instanceof IEntityCollidable)
		{
				((IEntityCollidable)teBelow).onEntityCollided(entityitem);
		}
		else if(teBelow instanceof TileEntityHopper)
		{
			if(!((TileEntityHopper)teBelow).isCoolingDown())
			{
				ItemStack toInsert = entityitem.getEntityItem().copy();
				toInsert.stackSize = 1;
				toInsert = TileEntityHopper.insertStack((IInventory)teBelow, toInsert, ForgeDirection.UP.ordinal());
				if(toInsert == null)
				{
					entityitem.getEntityItem().stackSize--;
					((TileEntityHopper)teBelow).setTransferCooldown(8);
				}
			}
		}
	}
}
