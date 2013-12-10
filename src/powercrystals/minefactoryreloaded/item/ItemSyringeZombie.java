package powercrystals.minefactoryreloaded.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import powercrystals.minefactoryreloaded.setup.MFRConfig;

public class ItemSyringeZombie extends ItemSyringe
{
	public ItemSyringeZombie()
	{
		super(MFRConfig.syringeZombieId.getInt());
	}
	
	@Override
	public boolean canInject(World world, EntityLivingBase entity, ItemStack syringe)
	{
		return entity instanceof EntityAgeable && ((EntityAgeable)entity).getGrowingAge() < 0;
	}
	
	@Override
	public boolean inject(World world, EntityLivingBase entity, ItemStack syringe)
	{
		if(world.rand.nextInt(100) < 5)
		{
			Entity e = null;
			if (entity instanceof EntityPig)
			{
				e = new EntityPigZombie(world);
			}
			else if (entity instanceof EntityHorse)
			{
				EntityHorse ent = (EntityHorse)entity;
				switch (ent.getHorseType())
				{
				case 1:
					ent.setHorseType(3);
					break;
				case 3:
					ent.setHorseType(4);
					break;
				}
			}
			else
			{
				e = new EntityZombie(world);
			}
			
			if (e != null)
			{
				e.setLocationAndAngles(entity.posX, entity.posY, entity.posZ, entity.rotationYaw, entity.rotationPitch);
				world.spawnEntityInWorld(e);
				entity.setDead();
			}
		}
		else
		{
			((EntityAgeable)entity).setGrowingAge(0);
		}
		return true;
	}
	
}
