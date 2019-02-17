package powercrystals.minefactoryreloaded.core.drinkhandlers;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;

import net.minecraftforge.fluids.FluidStack;
import powercrystals.minefactoryreloaded.api.handler.ILiquidDrinkHandler;

public class DrinkHandlerMushroomSoup implements ILiquidDrinkHandler {

	@Override
	public void onDrink(EntityLivingBase player, FluidStack fluid) {

		player.heal(4);
		player.addPotionEffect(new PotionEffect(MobEffects.HEALTH_BOOST, 5 * 20, 1));
		player.addPotionEffect(new PotionEffect(MobEffects.SATURATION, 15 * 20, 2));
	}

}
