package powercrystals.minefactoryreloaded.farmables.drinkhandlers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import powercrystals.minefactoryreloaded.api.ILiquidDrinkHandler;

public class DrinkHandlerMeat implements ILiquidDrinkHandler
{
	@Override
	public void onDrink(EntityPlayer player)
	{
		player.addPotionEffect(new PotionEffect(Potion.field_76444_x.id, 5 * 20, 2));
		player.addPotionEffect(new PotionEffect(Potion.damageBoost.id, 8 * 20, 0));
		player.addPotionEffect(new PotionEffect(Potion.field_76443_y.id, 15 * 20, 2));
		player.addPotionEffect(new PotionEffect(Potion.hunger.id, 6 * 20, 0));
	}
}
