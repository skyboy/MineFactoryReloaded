package powercrystals.minefactoryreloaded.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import powercrystals.minefactoryreloaded.MFRProps;
import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.api.handler.IMobEggHandler;
import powercrystals.minefactoryreloaded.api.mob.IRandomMobProvider;
import powercrystals.minefactoryreloaded.api.handler.ISafariNetHandler;
import powercrystals.minefactoryreloaded.api.mob.RandomMobProvider;
import powercrystals.minefactoryreloaded.core.MFRUtil;
import powercrystals.minefactoryreloaded.core.UtilInventory;
import powercrystals.minefactoryreloaded.item.base.ItemFactory;
import powercrystals.minefactoryreloaded.render.IColorRegister;
import powercrystals.minefactoryreloaded.setup.MFRThings;
import powercrystals.minefactoryreloaded.setup.village.Zoologist;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class ItemSafariNet extends ItemFactory implements IColorRegister {

	private final boolean multiuse;
	private final int type;

	public ItemSafariNet(int type) {

		this(type, false);
	}

	public ItemSafariNet(int type, boolean multiuse) {

		this.multiuse = multiuse;
		this.type = type;
		setMaxStackSize(!multiuse && (type & 1) == 0 ? 12 : 1);
		MineFactoryReloadedCore.proxy.addColorRegister(this);
	}

	@Override
	public String getUnlocalizedName() {

		return super.getUnlocalizedName();
	}

	@Override
	public int getItemStackLimit(@Nonnull ItemStack stack) {

		if (!isSingleUse(stack) || !isEmpty(stack))
			return 1;
		return maxStackSize;
	}

	@Override
	public void addInformation(@Nonnull ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag tooltipFlag) {

		super.addInformation(stack, world, tooltip, tooltipFlag);

		int type = ((ItemSafariNet) stack.getItem()).type;
		if (1 == (type & 1)) {
			tooltip.add(I18n.translateToLocal("tip.info.mfr.safarinet.persistent"));
		}

		if (2 == (type & 2)) {
			tooltip.add(I18n.translateToLocal("tip.info.mfr.safarinet.nametag"));
		}

		if (getEntityData(stack) == null) {
			return;
		}

		if (getEntityData(stack).getBoolean("mfr:hide")) {
			tooltip.add(I18n.translateToLocal("tip.info.mfr.safarinet.mystery"));
		} else {
			ResourceLocation entityId = new ResourceLocation(getEntityData(stack).getString("id"));
			tooltip.add(MFRUtil.localize("entity.", EntityList.getTranslationName(entityId)));
			// See Entity.getName()
			Class<?> c = EntityList.getClass(entityId);
			if (c == null) {
				return;
			}
			for (ISafariNetHandler handler : MFRRegistry.getSafariNetHandlers()) {
				if (handler.validFor().isAssignableFrom(c)) {
					handler.addInformation(getEntityData(stack), world, tooltip, tooltipFlag);
				}
			}
		}
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side,
			float xOffset, float yOffset, float zOffset) {

		@Nonnull ItemStack itemstack = player.getHeldItem(hand);

		if (world.isRemote) {
			return EnumActionResult.PASS;
		} else if (isEmpty(itemstack)) {
			return EnumActionResult.SUCCESS;
		} else {
			if (player.capabilities.isCreativeMode) {
				itemstack = itemstack.copy();
				NBTTagCompound tag = getEntityData(itemstack);
				if (tag != null) {
					tag.removeTag("UUID");
					tag.removeTag("UUIDMost");
					tag.removeTag("UUIDLeast");
				}
			}
			return releaseEntity(itemstack, world, pos, side) != null ? EnumActionResult.SUCCESS : EnumActionResult.PASS;
		}
	}

	public static Entity releaseEntity(@Nonnull ItemStack itemstack, World world, BlockPos pos, EnumFacing side) {

		if (world.isRemote) {
			return null;
		}
		final int type = ((ItemSafariNet) itemstack.getItem()).type;

		Entity spawnedCreature;
		Block block = world.getBlockState(pos).getBlock();
		pos = pos.offset(side);
		double spawnOffsetY = 0.0D;

		if (side == EnumFacing.UP && block instanceof BlockFence) {
			spawnOffsetY = 0.5D;
		}

		spawnedCreature = spawnCreature(world, getEntityData(itemstack), pos.getX() + 0.5D, pos.getY() + spawnOffsetY, pos.getZ() + 0.5D, side);

		if (spawnedCreature != null) {
			if (itemstack.hasDisplayName()) {
				spawnedCreature.setCustomNameTag(itemstack.getDisplayName());
			}

			if (spawnedCreature.hasCustomName()) {
				if (2 == (type & 2))
					spawnedCreature.setAlwaysRenderNameTag(true);
			}

			if ((spawnedCreature instanceof EntityLiving)) {
				if (1 == (type & 1)) {
					((EntityLiving) spawnedCreature).enablePersistence();
				}
			}

			if (isSingleUse(itemstack)) {
				itemstack.shrink(1);
			} else if (itemstack.getItemDamage() != 0) {
				itemstack.setItemDamage(0);
			}
			itemstack.setTagCompound(null);
		}

		return spawnedCreature;
	}

	private static Entity spawnCreature(World world, NBTTagCompound mobTag, double x, double y, double z, EnumFacing side) {

		int offsetX = side.getFrontOffsetX();
		int offsetY = side == EnumFacing.DOWN ? -1 : 0;
		int offsetZ = side.getFrontOffsetZ();

		Entity e;
		if (mobTag.getBoolean("mfr:hide") && !mobTag.hasKey("id", Constants.NBT.TAG_STRING)) {
			List<RandomMobProvider> mobs = new ArrayList<>();

			for (IRandomMobProvider p : MFRRegistry.getRandomMobProviders()) {
				mobs.addAll(p.getRandomMobs(world));
			}
			e = WeightedRandom.getRandomItem(world.rand, mobs).getMob(world, new Vec3d(x + offsetX, y + offsetY, z + offsetZ));
		} else {
			NBTTagList pos = mobTag.getTagList("Pos", 6);
			pos.set(0, new NBTTagDouble(x));
			pos.set(1, new NBTTagDouble(y));
			pos.set(2, new NBTTagDouble(z));
			mobTag.setTag("Pos", pos);
			mobTag.removeTag("Dimension");

			e = EntityList.createEntityFromNBT(mobTag, world);
		}

		if (e != null) {
			AxisAlignedBB bb = e.getEntityBoundingBox();

			e.setLocationAndAngles(x + (bb.maxX - bb.minX) * 0.5 * offsetX,
					y + (bb.maxY - bb.minY) * 0.5 * offsetY,
					z + (bb.maxZ - bb.minZ) * 0.5 * offsetZ,
					world.rand.nextFloat() * 360.0F, 0.0F);

			world.spawnEntity(e);
			if (e instanceof EntityLiving) {
				((EntityLiving) e).playLivingSound();
			}

			LinkedList<Entity> ridingEntities = new LinkedList<>();
			ridingEntities.addAll(e.getPassengers());
			while (ridingEntities.size() > 0) {
				Entity riddenByEntity = ridingEntities.removeFirst();
				riddenByEntity.setLocationAndAngles(x, y, z, world.rand.nextFloat() * 360.0F, 0.0F);

				world.spawnEntity(riddenByEntity);
				if (riddenByEntity instanceof EntityLiving) {
					((EntityLiving) riddenByEntity).playLivingSound();
				}

				ridingEntities.addAll(riddenByEntity.getPassengers());
			}
		}

		return e;
	}

	@Override
	public boolean itemInteractionForEntity(@Nonnull ItemStack itemstack, EntityPlayer player, EntityLivingBase entity, EnumHand hand) {

		return captureEntity(itemstack, entity, player, hand);
	}

	public static boolean captureEntity(@Nonnull ItemStack itemstack, EntityLivingBase entity) {

		return captureEntity(itemstack, entity, null, null);
	}

	public static boolean captureEntity(@Nonnull ItemStack itemstack, EntityLivingBase entity, EntityPlayer player, EnumHand hand) {

		if (entity.world.isRemote) {
			return false;
		}
		if (!isEmpty(itemstack)) {
			return false;
		} else if (MFRRegistry.getSafariNetBlacklist().contains(entity.getClass())) {
			return false;
		}
		else if (!(entity instanceof EntityPlayer)) {
			boolean flag = player != null && player.capabilities.isCreativeMode;
			NBTTagCompound entityTagCompound = new NBTTagCompound();

			{
				if (entity.isRiding()) {
					entity.dismountRidingEntity();
				}
				if (entity.isBeingRidden()) {
					for (Entity e : entity.getPassengers())
						e.dismountRidingEntity();
				}
				if (entity.isRiding() || entity.isBeingRidden()) {
					return false;
				}

				entity.writeToNBT(entityTagCompound);

				entityTagCompound.setString("id", EntityList.getKey(entity).toString());

				if (entity.isDead)
					return false;
				if (!flag)
					entity.setDead();

				if (flag || entity.isDead) {
					flag = false;
					itemstack.shrink(1);
					if (itemstack.getCount() > 0) {
						flag = true;
						itemstack = itemstack.copy();
					}
					itemstack.setCount(1);
					itemstack.setTagInfo("EntityData", entityTagCompound);
					if (flag && (player == null || !player.inventory.addItemStackToInventory(itemstack)))
						UtilInventory.dropStackInAir(entity.world, entity, itemstack);
					else if (flag) {
						player.openContainer.detectAndSendChanges();
						((EntityPlayerMP) player).sendAllContents(player.openContainer,
								player.openContainer.getInventory());
					} else if (player != null && hand != null) {
						player.setHeldItem(hand, itemstack);
					}

					return true;
				} else {
					return false;
				}
			}
		}
		return true;
	}

	public static boolean isEmpty(@Nonnull ItemStack s) {

		return !isSafariNet(s) || (getEntityData(s) == null);
	}

	public static boolean isSingleUse(@Nonnull ItemStack s) {

		return isSafariNet(s) && !((ItemSafariNet) s.getItem()).multiuse;
	}

	public static boolean isSafariNet(@Nonnull ItemStack s) {

		return !s.isEmpty() && (s.getItem() instanceof ItemSafariNet);
	}

	@Nonnull
	public static ItemStack makeMysteryNet(@Nonnull ItemStack s) {

		if (isSafariNet(s)) {
			NBTTagCompound c = new NBTTagCompound();
			c.setBoolean("mfr:hide", true);
			s.setTagInfo("EntityData", c);
		}
		return s;
	}

	public static NBTTagCompound getEntityData(@Nonnull ItemStack s) {

		return s.hasTagCompound() && s.getTagCompound().hasKey("EntityData", Constants.NBT.TAG_COMPOUND) ? s.getTagCompound().getCompoundTag("EntityData") : null;
	}

	public static Class<? extends Entity> getEntityClass(@Nonnull ItemStack s) {

		if (!isSafariNet(s) || isEmpty(s))
			return null;
		String mobId = getEntityData(s).getString("id");
		if (!ForgeRegistries.ENTITIES.containsKey(new ResourceLocation(mobId)))
			return null;
		return EntityList.getClass(new ResourceLocation(mobId));
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {

		if (isInCreativeTab(tab)) {
			super.getSubItems(tab, items);
			if (this.equals(MFRThings.safariNetSingleItem)) {
				items.add(Zoologist.getHiddenNetStack());
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels() {

		ModelResourceLocation empty = new ModelResourceLocation(MFRProps.PREFIX + "safari_net", "variant=" + variant + "_empty");
		ModelResourceLocation full = new ModelResourceLocation(MFRProps.PREFIX + "safari_net", "variant=" + variant);

		ModelLoader.setCustomMeshDefinition(this, stack -> {

			if (ItemSafariNet.isEmpty(stack))
				return empty;
			return full;
		});
		ModelLoader.registerItemVariants(this, empty, full);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerColorHandlers() {

		final Random colorRand = new Random();
		final IItemColor colorHandler = new IItemColor() {

			@Override
			@SideOnly(Side.CLIENT)
			public int colorMultiplier(@Nonnull ItemStack stack, int tintIndex) {

				if (getEntityData(stack) == null) {
					return 16777215;
				}

				if (getEntityData(stack).getBoolean("mfr:hide")) {
					World world = Minecraft.getMinecraft().world;
					colorRand.setSeed(world.getSeed() ^ (world.getTotalWorldTime() / (7 * 20)));
					if (tintIndex == 2) {
						colorRand.nextDouble(); // skip some data
						return colorRand.nextInt();
					} else if (tintIndex == 1)
						return colorRand.nextInt();
					else
						return 16777215;
				}

				EntityList.EntityEggInfo egg = getEgg(stack);

				if (egg == null) {
					return 16777215;
				} else if (tintIndex == 2) {
					return egg.primaryColor;
				} else if (tintIndex == 1) {
					return egg.secondaryColor;
				} else {
					return 16777215;
				}
			}

			private EntityList.EntityEggInfo getEgg(@Nonnull ItemStack safariStack) {

				if (getEntityData(safariStack) == null) {
					return null;
				}

				for (IMobEggHandler handler : MFRRegistry.getModMobEggHandlers()) {
					EntityList.EntityEggInfo egg = handler.getEgg(getEntityData(safariStack));
					if (egg != null) {
						return egg;
					}
				}

				return null;
			}
		};

		Minecraft.getMinecraft().getItemColors().registerItemColorHandler(colorHandler, this);
	}
}
