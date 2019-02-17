package powercrystals.minefactoryreloaded.item.gun.ammo;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class ItemNeedlegunAmmoStandard extends ItemNeedlegunAmmo {

	protected int damage;
	protected float spread;

	public ItemNeedlegunAmmoStandard(int damage, float spread, int shots) {
		this.spread = spread;
		setDamage(damage);
		setShots(shots);
	}

	public ItemNeedlegunAmmoStandard(int damage, float spread) {
		this(damage, spread, 12);
	}

	public ItemNeedlegunAmmoStandard(int damage, int shots) {
		this(damage, 1f, shots);
	}

	public ItemNeedlegunAmmoStandard(int damage) {
		this(damage, 1f);
	}

	public ItemNeedlegunAmmoStandard() {
		this(8);
	}

	protected ItemNeedlegunAmmoStandard setDamage(int damage) {
		this.damage = damage;
		return this;
	}

	protected ItemNeedlegunAmmoStandard setShots(int shots) {
		setMaxDamage(shots - 1);
		return this;
	}

	@Override
	public boolean onHitEntity(@Nonnull ItemStack stack, EntityPlayer owner, Entity hit, double distance) {
		// TODO: API needs to accept the needle entity
		hit.attackEntityFrom(DamageSource.causePlayerDamage(owner).setProjectile(), damage);
		return true;
	}

	@Override
	public void onHitBlock(@Nonnull ItemStack stack, EntityPlayer owner, World world, BlockPos pos, EnumFacing side, double distance) {
	}

	@Override
	public float getSpread(@Nonnull ItemStack stack) {
		return spread;
	}

}
