package powercrystals.minefactoryreloaded.item;

import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.core.MFRUtil;

public class ItemPortaSpawner extends ItemFactory
{
	private static int _blockId = Block.mobSpawner.blockID;
	public static final String spawnerTag = "spawner";
	private static final String placeTag = "placeDelay";

	public ItemPortaSpawner(int id)
	{
		super(id);
	}

	public static NBTTagCompound getSpawnerTag(ItemStack stack)
	{
		NBTTagCompound tag = stack.getTagCompound();
		if (tag != null)
		{
			if (tag.hasKey(spawnerTag))
				return tag.getCompoundTag(spawnerTag);
			if (tag.hasKey("EntityId"))
				return tag;
		}
		return null;
	}

	private static String getEntityId(ItemStack stack)
	{
		NBTTagCompound tag = getSpawnerTag(stack);
		if (tag != null)
			return tag.getString("EntityId");
		return null;
	}

	public static boolean hasData(ItemStack stack)
	{
		return getEntityId(stack) != null;
	}

	private static int getDelay(ItemStack stack)
	{
		NBTTagCompound tag = stack.getTagCompound();
		if (tag != null)
		{
			return tag.getInteger(placeTag);
		}
		return 0;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List infoList, boolean advancedTooltips)
	{
		String id = getEntityId(stack);
		if (id != null)
			infoList.add(MFRUtil.localize("tile.mobSpawner") + ": " +
					MFRUtil.localize("entity.", id));
		int delay = getDelay(stack);
		if (delay > 0)
		{
			String s = MFRUtil.localize("tip.info.mfr.cannotplace", true, "%s");
			infoList.add(String.format(s, Math.ceil(delay / 20f)));
		}
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int par4, boolean par5)
	{
		NBTTagCompound tag = stack.getTagCompound();
		if (tag != null && tag.hasKey(placeTag) && tag.getInteger(placeTag) > 0)
		{
			tag.setInteger(placeTag, tag.getInteger(placeTag) - 1);
		}
	}

	@Override
	public boolean onItemUse(ItemStack itemstack, EntityPlayer player, World world, int x, int y, int z, int side, float xOffset, float yOffset, float zOffset)
	{
		if(world.isRemote)
		{
			return true;
		}
		if (getEntityId(itemstack) == null)
		{
			int blockId = world.getBlockId(x, y, z);
			if(blockId != _blockId)
			{
				return false;
			}
			else
			{
				TileEntity te = world.getBlockTileEntity(x, y, z);
				NBTTagCompound tag = new NBTTagCompound();
				tag.setCompoundTag(spawnerTag, new NBTTagCompound());
				te.writeToNBT(tag.getCompoundTag(spawnerTag));
				tag.setInteger(placeTag, 40 * 20);
				itemstack.setTagCompound(tag);
				world.setBlockToAir(x, y, z);
				return true;
			}
		}
		else
		{
			if (getDelay(itemstack) <= 0 &&
					placeBlock(itemstack, player, world, x, y, z, side, xOffset, yOffset, zOffset))
			{
				return true;
			}
			return false;
		}
	}

	private boolean placeBlock(ItemStack itemstack, EntityPlayer player, World world, int x, int y, int z, int side, float xOffset, float yOffset, float zOffset)
	{
		int blockId = world.getBlockId(x, y, z);

		if(blockId == Block.snow.blockID && (world.getBlockMetadata(x, y, z) & 7) < 1)
		{
			side = 1;
		}
		else if(blockId != Block.vine.blockID && blockId != Block.tallGrass.blockID && blockId != Block.deadBush.blockID && (Block.blocksList[blockId] == null || !Block.blocksList[blockId].isBlockReplaceable(world, x, y, z)))
		{
			switch (side)
			{
			case 0:
				--y;
				break;
			case 1:
				++y;
				break;
			case 2:
				--z;
				break;
			case 3:
				++z;
				break;
			case 4:
				--x;
				break;
			case 5:
				++x;
				break;
			}
		}

		if(itemstack.stackSize == 0)
		{
			return false;
		}
		else if(!player.canPlayerEdit(x, y, z, side, itemstack))
		{
			return false;
		}
		else if(y == 255 && Block.blocksList[_blockId].blockMaterial.isSolid())
		{
			return false;
		}
		else if(world.canPlaceEntityOnSide(_blockId, x, y, z, false, side, player, itemstack))
		{
			Block block = Block.blocksList[_blockId];
			int meta = Block.blocksList[_blockId].onBlockPlaced(world, x, y, z, side, xOffset, yOffset, zOffset, 0);

			if(placeBlockAt(itemstack, player, world, x, y, z, side, xOffset, yOffset, zOffset, meta))
			{
				world.playSoundEffect(x + 0.5F, y + 0.5F, z + 0.5F, block.stepSound.getPlaceSound(), (block.stepSound.getVolume() + 1.0F) / 2.0F, block.stepSound.getPitch() * 0.8F);
				--itemstack.stackSize;
			}

			return true;
		}
		else
		{
			return false;
		}
	}

	private boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata)
	{
		if(!world.setBlock(x, y, z, _blockId, metadata, 3))
		{
			return false;
		}

		if(world.getBlockId(x, y, z) == _blockId)
		{
			Block.blocksList[_blockId].onBlockPlacedBy(world, x, y, z, player, stack);
			Block.blocksList[_blockId].onPostBlockPlaced(world, x, y, z, metadata);
			TileEntity te = world.getBlockTileEntity(x, y, z);
			NBTTagCompound tag = stack.getTagCompound();
			if (tag.hasKey(spawnerTag))
				tag = tag.getCompoundTag(spawnerTag);
			tag.setInteger("x", x);
			tag.setInteger("y", y);
			tag.setInteger("z", z);
			te.readFromNBT(tag);
			PacketDispatcher.sendPacketToAllAround(x, y, z, 50, player.worldObj.provider.dimensionId, te.getDescriptionPacket());
		}
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack stack)
	{
		return hasData(stack);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public EnumRarity getRarity(ItemStack par1ItemStack)
	{
		return hasData(par1ItemStack) ? EnumRarity.epic : EnumRarity.rare;
	}
}
