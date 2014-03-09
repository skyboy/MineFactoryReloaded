package powercrystals.minefactoryreloaded.farmables.drinkhandlers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import powercrystals.minefactoryreloaded.api.ILiquidDrinkHandler;

public class DrinkHandlerChocolateMilk implements ILiquidDrinkHandler
{
	@Override
	public void onDrink(EntityPlayer player)
	{
		player.addPotionEffect(new PotionEffect(Potion.moveSpeed.id, 60 * 20, 3));
		player.addPotionEffect(new PotionEffect(Potion.digSpeed.id, 60 * 20, 2));
		player.addPotionEffect(new PotionEffect(Potion.field_76434_w.id, 5 * 20, 1));
	}
}
