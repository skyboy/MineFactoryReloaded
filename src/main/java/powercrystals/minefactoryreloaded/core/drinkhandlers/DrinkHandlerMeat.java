package powercrystals.minefactoryreloaded.core.drinkhandlers;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;

import net.minecraftforge.fluids.FluidStack;
import powercrystals.minefactoryreloaded.api.handler.ILiquidDrinkHandler;

public class DrinkHandlerMeat implements ILiquidDrinkHandler {

	@Override
	public void onDrink(EntityLivingBase player, FluidStack fluid) {

		player.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, 5 * 20, 2));
		player.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 8 * 20, 0));
		player.addPotionEffect(new PotionEffect(MobEffects.SATURATION, 15 * 20, 2));
		player.addPotionEffect(new PotionEffect(MobEffects.HUNGER, 6 * 20, 0));
	}

}
