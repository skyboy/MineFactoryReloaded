package powercrystals.minefactoryreloaded.item.syringe;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import powercrystals.minefactoryreloaded.render.ModelHelper;
import powercrystals.minefactoryreloaded.setup.MFRThings;

import javax.annotation.Nonnull;

public class ItemSyringeZombie extends ItemSyringe {

	public ItemSyringeZombie() {

		setUnlocalizedName("mfr.syringe.zombie");
		setContainerItem(MFRThings.syringeEmptyItem);
	}

	@Override
	public boolean canInject(World world, EntityLivingBase entity, @Nonnull ItemStack syringe) {

		return entity instanceof EntityAgeable && ((EntityAgeable) entity).getGrowingAge() < 0;
	}

	@Override
	public boolean inject(World world, EntityLivingBase entity, @Nonnull ItemStack syringe) {

		((EntityAgeable) entity).setGrowingAge(0);

		if (world.rand.nextInt(100) < 5) {
			Entity e = null;
			ResourceLocation entityType = EntityList.getKey(entity);

			if (entityType == EntityList.getKey(EntityPig.class)) {
				e = EntityList.newEntity(EntityPigZombie.class, world);
			} else if (entity instanceof AbstractHorse) {
				NBTTagCompound tag = entity.writeToNBT(new NBTTagCompound());
				if (entityType == EntityList.getKey(EntityHorse.class)) {
					e = EntityList.newEntity(EntityZombieHorse.class, world);
				} else if (entityType == EntityList.getKey(EntityZombieHorse.class)) {
					e = EntityList.newEntity(EntitySkeletonHorse.class, world);
				}
				if (e != null) {
					e.readFromNBT(tag); // horse adds 'Variant' and 'ArmorItem' but should be empty from child
				}
			} else {
				e = EntityList.newEntity(EntityZombie.class, world);
			}

			if (e != null) {
				e.setLocationAndAngles(entity.posX, entity.posY, entity.posZ, entity.rotationYaw, entity.rotationPitch);
				world.spawnEntity(e);
				entity.setDead();
			}
		}
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels() {

		ModelHelper.registerModel(MFRThings.syringeZombieItem, "syringe", "variant=zombie");
	}

}
