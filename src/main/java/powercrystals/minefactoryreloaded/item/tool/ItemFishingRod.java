package powercrystals.minefactoryreloaded.item.tool;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
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
import powercrystals.minefactoryreloaded.entity.EntityFishingRod;
import powercrystals.minefactoryreloaded.item.base.ItemFactoryTool;
import powercrystals.minefactoryreloaded.render.ModelHelper;

import javax.annotation.Nonnull;

public class ItemFishingRod extends ItemFactoryTool {

	public ItemFishingRod() {
		setUnlocalizedName("mfr.fishing_rod");
		setMaxStackSize(1);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {

		@Nonnull ItemStack stack = player.getHeldItem(hand);

		if (!player.capabilities.isCreativeMode)
			stack.shrink(1);

		world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));

		if (!world.isRemote) {
			EntityFishingRod entity = new EntityFishingRod(world, player);
			entity.shoot(player, player.rotationPitch, player.rotationYaw, 0.0F, 0.6F, 1.0F);
			world.spawnEntity(entity);
			
		}

		return new ActionResult<>(EnumActionResult.SUCCESS, stack);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldRotateAroundWhenRendering() {
		return true;
	}

	@Override
	public boolean initialize() {

		super.initialize();
		EntityRegistry.registerModEntity(new ResourceLocation(MFRProps.MOD_ID, "fishing_rod"), EntityFishingRod.class, "FishingRod", 4, MineFactoryReloadedCore.instance(), 80, 3, true);

		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels() {

		ModelHelper.registerModel(this, "tool", "variant=fishing_rod");
		RenderingRegistry.registerEntityRenderingHandler(EntityFishingRod.class,
				new IRenderFactory<EntityFishingRod>() {

					@Override
					@SideOnly(Side.CLIENT)
					public Render<? super EntityFishingRod> createRenderFor(RenderManager manager) {

						return new RenderSnowball<>(manager, ItemFishingRod.this, Minecraft.getMinecraft().getRenderItem());
					}
				});
	}
}
