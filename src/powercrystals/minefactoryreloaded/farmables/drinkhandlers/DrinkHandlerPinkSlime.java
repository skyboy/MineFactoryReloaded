package powercrystals.minefactoryreloaded.farmables.drinkhandlers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import powercrystals.minefactoryreloaded.api.ILiquidDrinkHandler;

public class DrinkHandlerPinkSlime implements ILiquidDrinkHandler
{
	@Override
	public void onDrink(EntityPlayer player)
	{
		player.addPotionEffect(new PotionEffect(Potion.confusion.id, 24 * 20, 0));
		player.addPotionEffect(new PotionEffect(Potion.weakness.id, 12 * 20, 0));
		player.addPotionEffect(new PotionEffect(Potion.hunger.id, 12 * 20, 0));
		player.addPotionEffect(new PotionEffect(Potion.moveSpeed.id, 60 * 20, 2));
	}
}