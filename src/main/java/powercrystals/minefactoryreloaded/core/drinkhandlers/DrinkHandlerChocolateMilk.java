package powercrystals.minefactoryreloaded.core.drinkhandlers;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;

import net.minecraftforge.fluids.FluidStack;
import powercrystals.minefactoryreloaded.api.handler.ILiquidDrinkHandler;

public class DrinkHandlerChocolateMilk implements ILiquidDrinkHandler  {

	@Override
	public void onDrink(EntityLivingBase player, FluidStack fluid) {
		player.addPotionEffect(new PotionEffect(MobEffects.SPEED, 60 * 20, 3));
		player.addPotionEffect(new PotionEffect(MobEffects.HASTE, 60 * 20, 2));
		player.addPotionEffect(new PotionEffect(MobEffects.HEALTH_BOOST, 5 * 20, 1));
	}

}
