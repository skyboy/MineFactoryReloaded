package powercrystals.minefactoryreloaded.core.drinkhandlers;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;

import net.minecraftforge.fluids.FluidStack;
import powercrystals.minefactoryreloaded.api.handler.ILiquidDrinkHandler;

public class DrinkHandlerSludge implements ILiquidDrinkHandler {

	@Override
	public void onDrink(EntityLivingBase player, FluidStack fluid) {
		player.addPotionEffect(new PotionEffect(MobEffects.WITHER, 40 * 20, 0));
		player.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 40 * 20, 0));
		player.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 40 * 20, 0));
	}

}
