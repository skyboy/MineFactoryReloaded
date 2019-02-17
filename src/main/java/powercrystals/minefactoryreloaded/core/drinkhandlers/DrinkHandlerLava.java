package powercrystals.minefactoryreloaded.core.drinkhandlers;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;

import net.minecraftforge.fluids.FluidStack;
import powercrystals.minefactoryreloaded.api.handler.ILiquidDrinkHandler;

public class DrinkHandlerLava implements ILiquidDrinkHandler {

	@Override
	public void onDrink(EntityLivingBase player, FluidStack fluid) {

		player.attackEntityFrom(new InternalLavaDamage(), 7);
		player.setFire(30);
		NBTTagCompound tag = player.getEntityData();
		tag.setLong("drankLavaTime", player.world.getTotalWorldTime());
	}
	
	protected class InternalLavaDamage extends DamageSource {

		public InternalLavaDamage() {

			super(DamageSource.LAVA.damageType);
			this.setDamageBypassesArmor();
			this.setFireDamage();
			this.setDifficultyScaled();
		}

	}

}
