package powercrystals.minefactoryreloaded.item.gun;

import codechicken.lib.model.ModelRegistryHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import powercrystals.minefactoryreloaded.MFRProps;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.core.UtilInventory;
import powercrystals.minefactoryreloaded.entity.EntityFlyingItem;
import powercrystals.minefactoryreloaded.item.base.ItemFactoryGun;
import powercrystals.minefactoryreloaded.render.ModelHelper;
import powercrystals.minefactoryreloaded.render.entity.RenderSafarinet;
import powercrystals.minefactoryreloaded.render.item.PotatoLauncherItemRenderer;

import javax.annotation.Nonnull;

public class ItemPotatoCannon extends ItemFactoryGun {

	private static final Item[] ammo = { Items.POTATO, Items.POISONOUS_POTATO, Items.SNOWBALL, Items.CLAY_BALL,
		Items.APPLE, Items.BOWL, Items.BRICK, Items.NETHERBRICK };
	private static final float[] dmg = { 1f, 1f, 0.3f, 0.6f, 1f, 1.1f, 1.3f, 0.9f };
	private static final int[] recover = { 7, 7, 0, 5, 8, 2, 1, 1 };

	public ItemPotatoCannon() {

		setUnlocalizedName("mfr.potato_launcher");
		setMaxStackSize(1);
	}

	@Override
	protected boolean hasGUI(@Nonnull ItemStack stack) {

		return false;
	}

	public int cofh_canEnchantApply(@Nonnull ItemStack stack, Enchantment ench) { //TODO implement (or change to an anvil event)

		if (ench == Enchantments.LOOTING)
			return 1;
		if (ench.type == EnumEnchantmentType.BOW)
			return 1;
		return -1;
	}

	@Override
	public boolean isEnchantable(@Nonnull ItemStack stack) {

		return true;
	}

	@Override
	public int getItemEnchantability() {

		return 1;
	}

	@Override
	protected boolean fire(@Nonnull ItemStack stack, World world, EntityPlayer player) {

		boolean flag = player.capabilities.isCreativeMode, a = false;

		int i = 0;
		if (!flag) {
			flag = EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, stack) > 0;
			for (; !a && i < ammo.length; ++i)
				a = UtilInventory.playerHasItem(player, ammo[i]);
			if (a) --i;
			else if (flag) i = 0;
		}
		if (flag || a) {

			@Nonnull ItemStack fStack = new ItemStack(ammo[i]);
            if (EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAME, stack) > 0) {
            	@Nonnull ItemStack sStack = FurnaceRecipes.instance().getSmeltingResult(fStack);
            	if (!sStack.isEmpty())
            		fStack = sStack;
            }
            fStack.setCount(1);
			EntityFlyingItem item = new EntityFlyingItem(world, player, fStack);
			item.shoot(player, player.rotationPitch, player.rotationYaw, 0, 1.5f, 0.5f);

            int k = Math.max(0, EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, stack));

            item.setDamage(dmg[i] * (item.getDamage() + k * 1.2f));
            item.pickupChance = recover[i];

            int l = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, stack);
            item.setKnockbackStrength(l);

			if (flag) {
				item.canBePickedUp = 2;
			} else {
				UtilInventory.extractItem(player, ammo[i]);
			}
			if (!world.isRemote) {
				world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1F, 0.5F / (itemRand.nextFloat() * 0.4F + 1.2F));
				world.spawnEntity(item);
			}
			return true;
		}
		return false;
	}

	@Override
	protected int getDelay(@Nonnull ItemStack stack, boolean fired) {

		return fired ? 10 : 20;
	}

	@Override
	protected String getDelayTag(@Nonnull ItemStack stack) {

		return "mfr:PotatoLaunched";
	}

	@Override
	public boolean initialize() {

		super.initialize();
		EntityRegistry.registerModEntity(new ResourceLocation(MFRProps.MOD_ID, "potato_cannon"), EntityFlyingItem.class, "PotatoCannon", 5, MineFactoryReloadedCore.instance(), 160, 7, true);

		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels() {

		ModelHelper.registerModel(this, "potato_launcher");
		ModelRegistryHelper.register(new ModelResourceLocation(MFRProps.PREFIX + "potato_launcher", "inventory"), new PotatoLauncherItemRenderer());
		RenderingRegistry.registerEntityRenderingHandler(EntityFlyingItem.class,
				new IRenderFactory<EntityFlyingItem>() {

					@Override
					@SideOnly(Side.CLIENT)
					public Render<? super EntityFlyingItem> createRenderFor(RenderManager manager) {

						return new RenderSafarinet(manager, Minecraft.getMinecraft().getRenderItem());
					}
				});
	}
}
