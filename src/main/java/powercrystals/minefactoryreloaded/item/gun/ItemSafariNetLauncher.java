package powercrystals.minefactoryreloaded.item.gun;

import cofh.core.util.helpers.ItemHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import powercrystals.minefactoryreloaded.MFRProps;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.entity.EntitySafariNet;
import powercrystals.minefactoryreloaded.item.ItemSafariNet;
import powercrystals.minefactoryreloaded.item.base.ItemFactoryGun;
import powercrystals.minefactoryreloaded.render.ModelHelper;
import powercrystals.minefactoryreloaded.render.entity.RenderSafarinet;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemSafariNetLauncher extends ItemFactoryGun {

	public ItemSafariNetLauncher() {
		setUnlocalizedName("mfr.safarinet.launcher");
		setMaxStackSize(1);
	}

	@Override
	public void addInformation(@Nonnull ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag tooltipFlag) {
		super.addInformation(stack, world, tooltip, tooltipFlag);
		tooltip.add(I18n.translateToLocal("tip.info.mfr.safarinet.mode"));
	}

	@Override
	protected boolean hasGUI(@Nonnull ItemStack stack) {
		return false;
	}

	@Override
	protected boolean fire(@Nonnull ItemStack stack, World world, EntityPlayer player) {
		if (player.isSneaking()) {
			stack.setItemDamage(stack.getItemDamage() == 0 ? 1 : 0);
			if (world.isRemote) {
				if (isCaptureMode(stack)) {
					player.sendMessage(new TextComponentTranslation("chat.info.mfr.safarinet.capture"));
				} else {
					player.sendMessage(new TextComponentTranslation("chat.info.mfr.safarinet.release"));
				}
			}
			return false;
		}

		for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
			@Nonnull ItemStack ammo = player.inventory.getStackInSlot(i);
			if (ItemSafariNet.isSafariNet(ammo)) {
				if (ItemSafariNet.isEmpty(ammo) == isCaptureMode(stack)) {
					player.inventory.setInventorySlotContents(i, ItemHelper.consumeItem(ammo));
					if (ammo.getCount() > 0) {
						ammo = ammo.copy();
					}
					ammo.setCount(1);
					if (!world.isRemote) {
						EntitySafariNet esn = new EntitySafariNet(world, player, ammo);
						esn.shoot(player, player.rotationPitch, player.rotationYaw, 0, 2f, .5f);
						world.spawnEntity(esn);

						world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS,  0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
					}
					return true;
				}
			}
		}
		return false;
	}

	protected boolean isCaptureMode(@Nonnull ItemStack stack) {
		return !stack.isEmpty() && stack.getItemDamage() == 1;
	}

	@Override
	protected int getDelay(@Nonnull ItemStack stack, boolean fired) {
		return fired ? 10 : 3;
	}

	@Override
	protected String getDelayTag(@Nonnull ItemStack stack) {
		return "mfr:SafariLaunch";
	}

	@Override
	public boolean initialize() {

		super.initialize();
		EntityRegistry.registerModEntity(new ResourceLocation(MFRProps.MOD_ID, "safari_net"), EntitySafariNet.class, "SafariNet", 0, MineFactoryReloadedCore.instance(), 160, 5, true);

		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels() {

		ModelHelper.registerModel(this, "safari_net_launcher");
		ModelHelper.registerModel(this, 1, "safari_net_launcher");
		RenderingRegistry.registerEntityRenderingHandler(EntitySafariNet.class,
				new IRenderFactory<EntitySafariNet>() {

					@Override
					@SideOnly(Side.CLIENT)
					public Render<? super EntitySafariNet> createRenderFor(RenderManager manager) {

						return new RenderSafarinet(manager, Minecraft.getMinecraft().getRenderItem());
					}
				});
	}
}
