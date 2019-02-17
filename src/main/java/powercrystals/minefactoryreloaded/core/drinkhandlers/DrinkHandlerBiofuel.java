package powercrystals.minefactoryreloaded.core.drinkhandlers;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.fluids.FluidStack;
import powercrystals.minefactoryreloaded.api.handler.ILiquidDrinkHandler;

public class DrinkHandlerBiofuel implements ILiquidDrinkHandler {

	@Override
	public void onDrink(EntityLivingBase player, FluidStack fluid) {

		player.addPotionEffect(new PotionEffect(MobEffects.SPEED, 40 * 20, 0));
		player.addPotionEffect(new PotionEffect(MobEffects.HASTE, 40 * 20, 0));
	}

}
