package powercrystals.minefactoryreloaded.item.syringe;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.render.ModelHelper;
import powercrystals.minefactoryreloaded.setup.MFRThings;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ItemSyringeSlime extends ItemSyringe {

	public ItemSyringeSlime() {

		setUnlocalizedName("mfr.syringe.slime");
		setContainerItem(MFRThings.syringeEmptyItem);
	}

	@Override
	public boolean canInject(World world, EntityLivingBase entity, @Nonnull ItemStack syringe) {

		return entity instanceof EntitySlime && ((EntitySlime) entity).getSlimeSize() < 8;
	}

	@Override
	public boolean inject(World world, EntityLivingBase entity, @Nonnull ItemStack syringe) {

		EntitySlime slime = (EntitySlime) entity;
		setSlimeSize(slime, slime.getSlimeSize() << 1, true);
		return true;
	}

	private static final Method SET_SLIME_SIZE = ReflectionHelper.findMethod(EntitySlime.class, "setSlimeSize", "func_70799_a", int.class, boolean.class);

	private void setSlimeSize(EntitySlime slime, int size, boolean resetHealth) {

		try {
			SET_SLIME_SIZE.invoke(slime, size, resetHealth);
		} catch (IllegalAccessException | InvocationTargetException e) {
			MineFactoryReloadedCore.log().error("Can't set slime size\n", e);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels() {

		ModelHelper.registerModel(this, "syringe", "variant=slime");
	}
}
