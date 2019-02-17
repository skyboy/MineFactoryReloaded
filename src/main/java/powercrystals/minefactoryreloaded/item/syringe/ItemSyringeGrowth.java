package powercrystals.minefactoryreloaded.item.syringe;


import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityGiantZombie;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import powercrystals.minefactoryreloaded.render.ModelHelper;
import powercrystals.minefactoryreloaded.setup.MFRThings;

import javax.annotation.Nonnull;

public class ItemSyringeGrowth extends ItemSyringe
{
	public ItemSyringeGrowth()
	{
		setUnlocalizedName("mfr.syringe.growth");
		setContainerItem(MFRThings.syringeEmptyItem);
	}

	@Override
	public boolean canInject(World world, EntityLivingBase entity, @Nonnull ItemStack syringe)
	{
		return (entity instanceof EntityAgeable && ((EntityAgeable)entity).getGrowingAge() < 0) || entity instanceof EntityZombie;
	}
	
	@Override
	public boolean inject(World world, EntityLivingBase entity, @Nonnull ItemStack syringe)
	{
		if(entity instanceof EntityAgeable)
		{
			((EntityAgeable)entity).setGrowingAge(0);
		}
		else
		{
			EntityGiantZombie e = new EntityGiantZombie(world);
			e.setLocationAndAngles(entity.posX, entity.posY, entity.posZ, entity.rotationYaw, entity.rotationPitch);
			world.spawnEntity(e);
			entity.setDead();
		}
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels() {

		ModelHelper.registerModel(MFRThings.syringeGrowthItem, "syringe", "variant=growth");
	}
}
