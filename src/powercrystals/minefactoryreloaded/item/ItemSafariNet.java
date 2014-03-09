package powercrystals.minefactoryreloaded.item;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityEggInfo;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Facing;
import net.minecraft.util.Icon;
import net.minecraft.util.StatCollector;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.World;
import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.api.IMobEggHandler;
import powercrystals.minefactoryreloaded.api.IRandomMobProvider;
import powercrystals.minefactoryreloaded.api.ISafariNetHandler;
import powercrystals.minefactoryreloaded.api.RandomMob;
import powercrystals.minefactoryreloaded.gui.MFRCreativeTab;
import powercrystals.minefactoryreloaded.setup.village.VillageTradeHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemSafariNet extends ItemFactory
{
	private Icon _iconEmpty;
	private Icon _iconBack;
	private Icon _iconMid;
	private Icon _iconFront;
	
	public ItemSafariNet(int id)
	{
		super(id);
		maxStackSize = 1;
		setCreativeTab(MFRCreativeTab.tab);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void addInformation(ItemStack stack, EntityPlayer player, List infoList, boolean advancedTooltips)
	{
		if(stack.getTagCompound() == null)
		{
			return;
		}
		
		if(stack.itemID == MineFactoryReloadedCore.safariNetJailerItem.itemID)
		{
			infoList.add(StatCollector.translateToLocal("tip.info.mfr.safarinet.jailer"));
		}
		
		if(stack.getTagCompound().getBoolean("hide"))
		{
			infoList.add(StatCollector.translateToLocal("tip.info.mfr.safarinet.mystery"));
		}
		else
		{
			infoList.add(StatCollector.translateToLocal("entity." + stack.getTagCompound().getString("id") + ".name")); // See Entity.getEntityName()
			Class c = (Class)EntityList.stringToClassMapping.get(stack.getTagCompound().getString("id"));
			if (c == null)
			{
				return;
			}
			for(ISafariNetHandler handler : MFRRegistry.getSafariNetHandlers())
			{
				if(handler.validFor().isAssignableFrom(c))
				{
					handler.addInformation(stack, player, infoList, advancedTooltips);
				}
			}
		}
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public Icon getIcon(ItemStack stack, int pass)
	{
		if(isEmpty(stack)) return _iconEmpty;
		if(pass == 0) return _iconBack;
		else if(pass == 1) return _iconMid;
		else if(pass == 2) return _iconFront;
		return null;
	}
	
	@Override
	public void registerIcons(IconRegister ir)
	{
		_iconEmpty = ir.registerIcon("minefactoryreloaded:" + getUnlocalizedName() + ".empty");
		_iconBack = ir.registerIcon("minefactoryreloaded:" + getUnlocalizedName() + ".back");
		_iconMid = ir.registerIcon("minefactoryreloaded:" + getUnlocalizedName() + ".mid");
		_iconFront = ir.registerIcon("minefactoryreloaded:" + getUnlocalizedName() + ".front");
		
		itemIcon = _iconEmpty;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean requiresMultipleRenderPasses()
	{
		return true;
	}
	
	@Override
	public int getRenderPasses(int metadata)
	{
		return 3;
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public int getColorFromItemStack(ItemStack stack, int pass)
	{
		if(stack.getItemDamage() == 0 && (stack.getTagCompound() == null || stack.getTagCompound().getBoolean("hide")))
		{
			return 16777215;
		}
		EntityEggInfo egg = getEgg(stack);
		
		if(egg == null)
		{
			return 16777215;
		}
		else if(pass == 2)
		{
			return egg.primaryColor;
		}
		else if(pass == 1)
		{
			return egg.secondaryColor;
		}
		else
		{
			return 16777215;
		}
	}
	
	private EntityEggInfo getEgg(ItemStack safariStack)
	{
		if(safariStack.getTagCompound() == null)
		{
			return null;
		}
		
		for(IMobEggHandler handler : MFRRegistry.getModMobEggHandlers())
		{
			EntityEggInfo egg = handler.getEgg(safariStack);
			if(egg != null)
			{
				return egg;
			}
		}
		
		return null;
	}
	
	@Override
	public boolean onItemUse(ItemStack itemstack, EntityPlayer player, World world, int x, int y, int z, int side, float xOffset, float yOffset, float zOffset)
	{
		if(world.isRemote)
		{
			return true;
		}
		else if(isEmpty(itemstack))
		{
			return true;
		}
		else
		{
			return releaseEntity(itemstack, world, x, y, z, side) != null;
		}
	}
	
	public static Entity releaseEntity(ItemStack itemstack, World world, int x, int y, int z, int side)
	{
		if(world.isRemote)
		{
			return null;
		}
		
		Entity spawnedCreature;
		int blockId = world.getBlockId(x, y, z);
		x += Facing.offsetsXForSide[side];
		y += Facing.offsetsYForSide[side];
		z += Facing.offsetsZForSide[side];
		double spawnOffsetY = 0.0D;
		
		if (side == 1 && Block.blocksList[blockId] != null && Block.blocksList[blockId].getRenderType() == 11)
		{
			spawnOffsetY = 0.5D;
		}
		
		if(itemstack.getItemDamage() != 0)
		{
			spawnedCreature = spawnCreature(world, itemstack.getItemDamage(), x + 0.5D, y + spawnOffsetY, z + 0.5D);
		}
		else
		{
			spawnedCreature = spawnCreature(world, itemstack.getTagCompound(), x + 0.5D, y + spawnOffsetY, z + 0.5D, side);
		}
		
		if(spawnedCreature != null)
		{
			if ((spawnedCreature instanceof EntityLiving) && itemstack.hasDisplayName())
			{
				((EntityLiving)spawnedCreature).setCustomNameTag(itemstack.getDisplayName());
				// TODO: secondary jailer net for:
				// ((EntityLiving)spawnedCreature).setAlwaysRenderNameTag(true);
			}
			
			if(isSingleUse(itemstack))
			{
				itemstack.stackSize--;
			}
			else if(itemstack.getItemDamage() != 0)
			{
				itemstack.setItemDamage(0);
			}
			itemstack.setTagCompound(null);
		}
		
		return spawnedCreature;
	}

	private static Entity spawnCreature(World world, NBTTagCompound mobTag, double x, double y, double z, int side)
	{
		Entity e;
		if(mobTag.getBoolean("hide"))
		{
			List<RandomMob> mobs = new ArrayList<RandomMob>();
			
			for(IRandomMobProvider p : MFRRegistry.getRandomMobProviders())
			{
				mobs.addAll(p.getRandomMobs(world));
			}
			e = ((RandomMob)WeightedRandom.getRandomItem(world.rand, mobs)).getMob();
			if (e instanceof EntityLiving)
				((EntityLiving)e).onSpawnWithEgg(null);
		}
		else
		{
			NBTTagList pos = mobTag.getTagList("Pos");
			((NBTTagDouble)pos.tagAt(0)).data = x;
			((NBTTagDouble)pos.tagAt(1)).data = y;
			((NBTTagDouble)pos.tagAt(2)).data = z;
			
			e = EntityList.createEntityFromNBT(mobTag, world);
			if (e != null)
			{
				e.readFromNBT(mobTag);
			}
		}
		
		if(e != null)
		{

			int offsetX = Facing.offsetsXForSide[side];
			int offsetY = side == 0 ? -1 : 0;
			int offsetZ = Facing.offsetsZForSide[side];
			AxisAlignedBB bb =  e.boundingBox;
			
			e.setLocationAndAngles(x + (bb.maxX - bb.minX) * 0.5 * offsetX,
					y + (bb.maxY - bb.minY) * 0.5 * offsetY,
					z + (bb.maxZ - bb.minZ) * 0.5 * offsetZ,
					world.rand.nextFloat() * 360.0F, 0.0F);
			
			world.spawnEntityInWorld(e);
			if(e instanceof EntityLiving)
			{
				((EntityLiving)e).playLivingSound();
			}
			
			Entity riddenByEntity = e.riddenByEntity;
			while(riddenByEntity != null)
			{
				riddenByEntity.setLocationAndAngles(x, y, z, world.rand.nextFloat() * 360.0F, 0.0F);
				
				world.spawnEntityInWorld(riddenByEntity);
				if(riddenByEntity instanceof EntityLiving)
				{
					((EntityLiving)riddenByEntity).playLivingSound();
				}
				
				riddenByEntity = riddenByEntity.riddenByEntity;
			}
		}
		
		return e;
	}
	
	private static Entity spawnCreature(World world, int mobId, double x, double y, double z)
	{
		if(!EntityList.entityEggs.containsKey(Integer.valueOf(mobId)))
		{
			return null;
		}
		else
		{
			Entity e = EntityList.createEntityByID(mobId, world);
			
			if (e != null)
			{
				e.setLocationAndAngles(x, y, z, world.rand.nextFloat() * 360.0F, 0.0F);
				if (e instanceof EntityLiving)
					((EntityLiving)e).onSpawnWithEgg(null);
				world.spawnEntityInWorld(e);
				if (e instanceof EntityLiving)
					((EntityLiving)e).playLivingSound();
			}
			
			return e;
		}
	}
	
	@Override
	public boolean itemInteractionForEntity(ItemStack itemstack, EntityPlayer player, EntityLivingBase entity)
	{
		return captureEntity(itemstack, entity, player);
	}
	
	public static boolean captureEntity(ItemStack itemstack, EntityLivingBase entity)
	{
		return captureEntity(itemstack, entity, null);
	}
	
	public static boolean captureEntity(ItemStack itemstack, EntityLivingBase entity, EntityPlayer player)
	{
		if(entity.worldObj.isRemote)
		{
			return false;
		}
		if(!isEmpty(itemstack))
		{
			return false;
		}
		else if(MFRRegistry.getSafariNetBlacklist().contains(entity.getClass()))
		{
			return false;
		}
		else if(!(entity instanceof EntityPlayer))
		{
			boolean flag = player != null && player.capabilities.isCreativeMode;
			NBTTagCompound c = new NBTTagCompound();
			
			entity.writeToNBT(c);
			
			c.setString("id", (String)EntityList.classToStringMapping.get(entity.getClass()));
			if(itemstack.itemID == MineFactoryReloadedCore.safariNetJailerItem.itemID)
			{
				c.setBoolean("PersistenceRequired", true);
			}
			
			if (!flag)
				entity.setDead();
			if(flag | entity.isDead)
			{
				itemstack.setTagCompound(c);
				return true;
			}
			else
			{
				return false;
			}
		}
		return true;
	}
	
	public static boolean isEmpty(ItemStack s)
	{
		return s == null || (s.getItemDamage() == 0 && (s.getTagCompound() == null || (!s.getTagCompound().hasKey("id") && !s.getTagCompound().getBoolean("hide"))));
	}
	
	public static boolean isSingleUse(ItemStack s)
	{
		return s != null && (s.itemID == MineFactoryReloadedCore.safariNetSingleItem.itemID || s.itemID == MineFactoryReloadedCore.safariNetJailerItem.itemID);
	}
	
	public static boolean isSafariNet(ItemStack s)
	{
		return s != null && (s.getItem() instanceof ItemSafariNet);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void getSubItems(int itemId, CreativeTabs creativeTab, List subTypes)
	{
		super.getSubItems(itemId, creativeTab, subTypes);
		if(itemId == MineFactoryReloadedCore.safariNetSingleItem.itemID)
		{
			subTypes.add(VillageTradeHandler.getHiddenNetStack());
		}
	}
}
