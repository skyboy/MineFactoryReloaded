package powercrystals.minefactoryreloaded.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.ArrayList;

import net.minecraft.block.BlockContainer;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.Event.Result;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;

import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.api.rednet.IConnectableRedNet;
import powercrystals.minefactoryreloaded.api.rednet.RedNetConnectionType;
import powercrystals.minefactoryreloaded.core.BlockNBTManager;
import powercrystals.minefactoryreloaded.core.MFRUtil;
import powercrystals.minefactoryreloaded.gui.MFRCreativeTab;
import powercrystals.minefactoryreloaded.item.ItemLogicUpgradeCard;
import powercrystals.minefactoryreloaded.item.ItemRedNetMemoryCard;
import powercrystals.minefactoryreloaded.item.ItemRedNetMeter;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.rednet.TileEntityRedNetLogic;

public class BlockRedNetLogic extends BlockContainer implements IConnectableRedNet
{
	private int[] _sideRemap = new int[] { 3, 1, 2, 0 };
	
	public BlockRedNetLogic(int id)
	{
		super(id, Machine.MATERIAL);
		setUnlocalizedName("mfr.rednet.logic");
		setHardness(0.8F);
		setCreativeTab(MFRCreativeTab.tab);
	}
	
	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack)
	{
		if(entity == null)
		{
			return;
		}
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if(te instanceof TileEntityRedNetLogic)
		{
			int facing = MathHelper.floor_double((entity.rotationYaw * 4F) / 360F + 0.5D) & 3;
			world.setBlockMetadataWithNotify(x, y, z, (facing + 3) & 3, 3);
			/*
			if(facing == 0)
			{
				world.setBlockMetadataWithNotify(x, y, z, 3, 3);
			}
			else if(facing == 1)
			{
				world.setBlockMetadataWithNotify(x, y, z, 0, 3);
			}
			else if(facing == 2)
			{
				world.setBlockMetadataWithNotify(x, y, z, 1, 3);
			}
			else if(facing == 3)
			{
				world.setBlockMetadataWithNotify(x, y, z, 2, 3);
			}//*/
			
			if(stack.hasTagCompound())
			{
				stack.getTagCompound().setInteger("x", x);
				stack.getTagCompound().setInteger("y", y);
				stack.getTagCompound().setInteger("z", z);
				te.readFromNBT(stack.getTagCompound());
			}
		}
	}
	
	@Override
	public void breakBlock(World world, int x, int y, int z, int blockId, int meta)
	{
		BlockNBTManager.setForBlock(world.getBlockTileEntity(x, y, z));
		super.breakBlock(world, x, y, z, blockId, meta);
	}
	
	@Override
	public TileEntity createNewTileEntity(World world)
	{
		return new TileEntityRedNetLogic();
	}
	
	@Override
	public boolean rotateBlock(World world, int x, int y, int z, ForgeDirection axis)
	{
        if (world.isRemote)
        {
            return false;
        }
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if(te instanceof TileEntityRedNetLogic)
		{
			if (((TileEntityRedNetLogic)te).crafters > 0)
			{
				return false;
			}
		}
		int nextMeta = (world.getBlockMetadata(x, y, z) + 1) & 3; // % 4
		world.setBlockMetadataWithNotify(x, y, z, nextMeta, 3);
		return true;
	}
	
	@Override
	public RedNetConnectionType getConnectionType(World world, int x, int y, int z, ForgeDirection side)
	{
		TileEntityRedNetLogic logic = (TileEntityRedNetLogic)world.getBlockTileEntity(x, y, z);
		if(logic != null && side.ordinal() > 1 && side.ordinal() < 6)
		{
			if(world.getBlockMetadata(x, y, z) == _sideRemap[side.ordinal() - 2])
			{
				return RedNetConnectionType.None;
			}
		}
		return RedNetConnectionType.CableAll;
	}
	
	@Override
	public int getOutputValue(World world, int x, int y, int z, ForgeDirection side, int subnet)
	{
		TileEntityRedNetLogic logic = (TileEntityRedNetLogic)world.getBlockTileEntity(x, y, z);
		if(logic != null)
		{
			return logic.getOutputValue(side, subnet);
		}
		else
		{
			return 0;
		}
	}
	
	@Override
	public int[] getOutputValues(World world, int x, int y, int z, ForgeDirection side)
	{
		TileEntityRedNetLogic logic = (TileEntityRedNetLogic)world.getBlockTileEntity(x, y, z);
		if(logic != null)
		{
			return logic.getOutputValues(side);
		}
		else
		{
			return new int[16];
		}
	}
	
	@Override
	public void onInputsChanged(World world, int x, int y, int z, ForgeDirection side, int[] inputValues)
	{
		TileEntityRedNetLogic logic = (TileEntityRedNetLogic)world.getBlockTileEntity(x, y, z);
		if(logic != null)
		{
			logic.onInputsChanged(side, inputValues);
		}
	}
	
	@Override
	public void onInputChanged(World world, int x, int y, int z, ForgeDirection side, int inputValue)
	{
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float xOffset, float yOffset, float zOffset)
	{
		PlayerInteractEvent e = new PlayerInteractEvent(player, Action.RIGHT_CLICK_BLOCK, x, y, z, side);
		if(MinecraftForge.EVENT_BUS.post(e) || e.getResult() == Result.DENY || e.useBlock == Result.DENY)
		{
			return false;
		}
		
		if(MFRUtil.isHoldingHammer(player))
		{
			if (rotateBlock(world, x, y, z, ForgeDirection.getOrientation(side)))
			{
				return true;
			}
		}
		
		if(MFRUtil.isHolding(player, ItemLogicUpgradeCard.class))
		{
			TileEntityRedNetLogic logic = (TileEntityRedNetLogic)world.getBlockTileEntity(x, y, z);
			if(logic != null)
			{
				if(logic.insertUpgrade(player.inventory.getCurrentItem().getItemDamage() + 1));
				{
					if(!player.capabilities.isCreativeMode)
					{
						player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
					}
					return true;
				}
			}
			return false;
		}
		else if(!MFRUtil.isHolding(player, ItemRedNetMeter.class) && !MFRUtil.isHolding(player, ItemRedNetMemoryCard.class))
		{
			if(!world.isRemote)
			{
				player.openGui(MineFactoryReloadedCore.instance(), 0, world, x, y, z);
			}
			return true;
		}
		return false;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister ir)
	{
		blockIcon = ir.registerIcon("minefactoryreloaded:" + getUnlocalizedName());
	}
	
	@Override
	public int getRenderType()
	{
		return MineFactoryReloadedCore.renderIdRedNetLogic;
	}
	
	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}
	
	@Override
	public boolean isBlockNormalCube(World world, int x, int y, int z)
	{
		return false;
	}
	
	@Override
	public boolean isBlockSolidOnSide(World world, int x, int y, int z, ForgeDirection side)
	{
		return true;
	}
	
	@Override
	public ArrayList<ItemStack> getBlockDropped(World world, int x, int y, int z, int metadata, int fortune)
	{
		ArrayList<ItemStack> drops = new ArrayList<ItemStack>();
		ItemStack prc = new ItemStack(idDropped(blockID, world.rand, fortune), 1, damageDropped(0));
		prc.setTagCompound(BlockNBTManager.getForBlock(x, y, z));
		drops.add(prc);
		return drops;
	}
}
