package powercrystals.minefactoryreloaded.farmables.ranchables;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityParrot;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import powercrystals.minefactoryreloaded.api.mob.IFactoryRanchable;
import powercrystals.minefactoryreloaded.api.mob.RanchedItem;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class RanchableParrot implements IFactoryRanchable {

	protected Random rand = new Random();

	@Override
	public Class<? extends EntityLivingBase> getRanchableEntity() {

		return EntityParrot.class;
	}

	@Override
	public List<RanchedItem> ranch(World world, EntityLivingBase entity, IInventory rancher) {

		NBTTagCompound tag = entity.getEntityData();
		if (tag.getLong("mfr:lastRanched") > world.getTotalWorldTime())
			return null;
		tag.setLong("mfr:lastRanched", world.getTotalWorldTime() + 20 * 90);
		List<RanchedItem> drops = new LinkedList<>();
		EntityParrot parrot = ((EntityParrot) entity);
		parrot.playSound(SoundEvents.ENTITY_CHICKEN_EGG, 1.0F, (parrot.getRNG().nextFloat() - parrot.getRNG().nextFloat()) * 0.2F + 1.0F);
		parrot.attackEntityFrom(DamageSource.GENERIC, 0.1f);
		parrot.setRevengeTarget(parrot); // panic
		int k = parrot.getRNG().nextInt(3) + 1;
		drops.add(new RanchedItem(Items.FEATHER, k));
		return drops;
	}

}
