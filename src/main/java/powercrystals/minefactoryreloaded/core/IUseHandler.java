package powercrystals.minefactoryreloaded.core;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public interface IUseHandler {
	boolean canUse(@Nonnull ItemStack item, EntityLivingBase entity, EnumHand hand);
	@Nonnull ItemStack onTryUse(@Nonnull ItemStack item, World world, EntityLivingBase entity, EnumHand hand);
	int getMaxUseDuration(@Nonnull ItemStack item);
	boolean isUsable(@Nonnull ItemStack item);
	EnumAction useAction(@Nonnull ItemStack item);
	@Nonnull ItemStack onUse(@Nonnull ItemStack item, EntityLivingBase entity, EnumHand hand);
}
