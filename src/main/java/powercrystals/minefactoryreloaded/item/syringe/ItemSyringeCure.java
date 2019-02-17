package powercrystals.minefactoryreloaded.item.syringe;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityZombieVillager;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.render.ModelHelper;
import powercrystals.minefactoryreloaded.setup.MFRThings;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

public class ItemSyringeCure extends ItemSyringe {

	public ItemSyringeCure() {

		setUnlocalizedName("mfr.syringe.cure");
		setContainerItem(MFRThings.syringeEmptyItem);
	}

	@Override
	public boolean canInject(World world, EntityLivingBase entity, @Nonnull ItemStack syringe) {

		return entity instanceof EntityZombieVillager;
	}

	@Override
	public boolean inject(World world, EntityLivingBase entity, @Nonnull ItemStack syringe) {

		startConverting((EntityZombieVillager) entity, null,  300);
		return true;
	}

	private static final Method START_CONVERTING = ReflectionHelper.findMethod(EntityZombieVillager.class,
			"startConverting", "func_191991_a", UUID.class, int.class);
	private void startConverting(EntityZombieVillager zombieVillager, @Nullable UUID conversionStarter, int conversionTime) {

		try {
			START_CONVERTING.invoke(zombieVillager, conversionStarter, conversionTime);
		} catch (IllegalAccessException | InvocationTargetException e) {
			MineFactoryReloadedCore.log().error("Zombie villager conversion failed \n", e);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels() {

		ModelHelper.registerModel(MFRThings.syringeCureItem, "syringe", "variant=cure");
	}
}
