package powercrystals.minefactoryreloaded.item.tool;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import powercrystals.minefactoryreloaded.core.MFRUtil;
import powercrystals.minefactoryreloaded.core.UtilInventory;
import powercrystals.minefactoryreloaded.item.base.ItemFactoryTool;
import powercrystals.minefactoryreloaded.render.ModelHelper;
import powercrystals.minefactoryreloaded.setup.MFRFluids;
import powercrystals.minefactoryreloaded.setup.MFRThings;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ItemXpExtractor extends ItemFactoryTool {

	public static DamageSource damage = new DamageSource("mfr.xpsuck").setDamageBypassesArmor().setDamageIsAbsolute();

	public ItemXpExtractor() {

		this.addPropertyOverride(new ResourceLocation("pull"), new IItemPropertyGetter() {
		
			@SideOnly(Side.CLIENT)
			public float apply(@Nonnull ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entity)	{
				if (entity == null)	{
					return 1F;
				} else {
					if (entity.isHandActive() && !stack.isEmpty() && entity.getActiveItemStack() == stack && stack.getItem() == MFRThings.xpExtractorItem) {
						int useRemaining = entity.getItemInUseCount();
						if (useRemaining > 24) return 1F;
						if (useRemaining > 12) return 2F;
						return 3F;
					}
					return 1F;
				}
			}
		});

		setUnlocalizedName("mfr.xp_extractor");
		setMaxStackSize(1);
	}
	
	@Override
	public EnumAction getItemUseAction(@Nonnull ItemStack stack) {
		return EnumAction.BOW;
	}

	@Override
	public int getMaxItemUseDuration(@Nonnull ItemStack stack) {
		return 32;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {

		if (player.experienceLevel > 0 && UtilInventory.playerHasItem(player, Items.BUCKET)) {
			player.setActiveHand(hand);
		}

		return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
	}

	@Nonnull
	@Override
	public ItemStack onItemUseFinish(@Nonnull ItemStack stack, World world, EntityLivingBase entity) {
		if (world.isRemote || !(entity instanceof EntityPlayer))
			return stack;
		EntityPlayer player = (EntityPlayer) entity;
		suckExperience(player, player);
		return stack;
	}

	private void suckExperience(EntityPlayer target, EntityPlayer player) {
		if (target.capabilities.isCreativeMode && !player.capabilities.isCreativeMode)
			return;

		if (target.experienceLevel > 0) {
			if (!UtilInventory.extractItem(player, Items.BUCKET).isEmpty()) {
				if (!target.capabilities.isCreativeMode) {
					target.addExperienceLevel(-1);
					target.attackEntityFrom(damage, 0.25f);
					target.world.playSound(null, target.posX, target.posY, target.posZ, SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 0.15f, 0.25f);
				}
				@Nonnull ItemStack essenceBucket = MFRUtil.getBucketFor(MFRFluids.essence);
				if (!player.inventory.addItemStackToInventory(essenceBucket)) {
					player.entityDropItem(essenceBucket, 0f);
				}
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels() {

		ModelHelper.registerModel(this, "xp_extractor_1");
	}
}
