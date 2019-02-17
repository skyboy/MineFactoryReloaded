package powercrystals.minefactoryreloaded.item.gun;

import codechicken.lib.model.ModelRegistryHelper;
import cofh.core.util.helpers.ItemHelper;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import powercrystals.minefactoryreloaded.MFRProps;
import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.core.UtilInventory;
import powercrystals.minefactoryreloaded.entity.EntityNeedle;
import powercrystals.minefactoryreloaded.item.base.ItemFactoryGun;
import powercrystals.minefactoryreloaded.render.ModelHelper;
import powercrystals.minefactoryreloaded.render.entity.EntityNeedleRenderer;
import powercrystals.minefactoryreloaded.render.item.NeedleGunItemRenderer;
import powercrystals.minefactoryreloaded.setup.MFRThings;

import javax.annotation.Nonnull;

public class ItemNeedleGun extends ItemFactoryGun {

	public ItemNeedleGun() {

		setUnlocalizedName("mfr.needlegun"); // FIXME: relocalize to needle_gun
		setMaxStackSize(1);
	}

	@Override
	protected boolean hasGUI(@Nonnull ItemStack stack) {
		return true;
	}

	@Override
	protected boolean openGUI(@Nonnull ItemStack stack, World world, EntityPlayer player) {
		NBTTagCompound tag = stack.getSubCompound("ammo");
		boolean needsAmmo = tag == null || tag.hasNoTags() || player.isSneaking();
		if (needsAmmo & !world.isRemote)
			player.openGui(MineFactoryReloadedCore.instance(), 1, world, 0, 0, 0);

		return needsAmmo;
	}

	@Override
	protected boolean fire(@Nonnull ItemStack stack, World world, EntityPlayer player) {
		@Nonnull ItemStack ammo = new ItemStack(stack.getTagCompound().getCompoundTag("ammo"));
		boolean reloaded = false, creative = player.capabilities.isCreativeMode;

		if (!world.isRemote) {
			float spread = 1f;
			if (MFRRegistry.getNeedleAmmoTypes().containsKey(ammo.getItem()))
				spread = MFRRegistry.getNeedleAmmoTypes().get(ammo.getItem()).getSpread(ammo);
			EntityNeedle needle = new EntityNeedle(world, player, ammo, spread);
			world.spawnEntity(needle);
			world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + 2.0F);
		}

		NBTTagCompound t = new NBTTagCompound();
		if (!creative) {
			ammo.setItemDamage(ammo.getItemDamage() + 1);
		}

		if (ammo.getItemDamage() <= ammo.getMaxDamage()) {
			ammo.writeToNBT(t);
		} else {
			NonNullList<ItemStack> inv = player.inventory.mainInventory;
			for (int i = 0, e = inv.size(); i < e; ++i) {
				@Nonnull ItemStack item = inv.get(i);
				if (!item.isEmpty() && ammo.getItem().equals(item.getItem())) {
					ammo = ItemHelper.cloneStack(item, 1);
					ammo.writeToNBT(t);
					if (!creative) inv.set(i, ItemHelper.consumeItem(item));
					reloaded = true;
					break;
				}
			}

			if (!(world.isRemote | creative)) {
				UtilInventory.dropStackInAir(player.world, player, new ItemStack(MFRThings.needlegunAmmoEmptyItem, 1), 5);
			}
		}
		stack.setTagInfo("ammo", t);
		return reloaded;
	}

	@Override
	protected int getDelay(@Nonnull ItemStack stack, boolean fired) {
		return 5 + (fired ? 20 : 0);
	}

	@Override
	protected String getDelayTag(@Nonnull ItemStack stack) {
		return "mfr:NeedleLaunched";
	}

	@Override
	public boolean initialize() {

		super.initialize();
		EntityRegistry.registerModEntity(new ResourceLocation(MFRProps.MOD_ID, "needle_gun"), EntityNeedle.class, "Needle", 2, MineFactoryReloadedCore.instance(), 160, 3, true);

		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels() {

		ModelHelper.registerModel(this, "needle_gun");
		ModelRegistryHelper.register(new ModelResourceLocation(MFRProps.PREFIX + "needle_gun", "inventory"), new NeedleGunItemRenderer());
		RenderingRegistry.registerEntityRenderingHandler(EntityNeedle.class, EntityNeedleRenderer::new);

	}
}
