package powercrystals.minefactoryreloaded.api.handler;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public interface INeedleAmmo {

	boolean onHitEntity(@Nonnull ItemStack stack, EntityPlayer owner, Entity hit, double distance);

	void onHitBlock(@Nonnull ItemStack stack, EntityPlayer owner, World world, BlockPos pos, EnumFacing side, double distance);
	// TODO: needle entity should be available

	float getSpread(@Nonnull ItemStack stack);

}
