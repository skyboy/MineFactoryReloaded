package powercrystals.minefactoryreloaded.item.syringe;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import powercrystals.minefactoryreloaded.api.handler.ISyringe;
import powercrystals.minefactoryreloaded.item.base.ItemFactory;
import powercrystals.minefactoryreloaded.setup.MFRThings;

import javax.annotation.Nonnull;

public abstract class ItemSyringe extends ItemFactory implements ISyringe
{
	public ItemSyringe()
	{
		setMaxStackSize(1);
	}

	@Override
	public boolean itemInteractionForEntity(@Nonnull ItemStack stack, EntityPlayer player, EntityLivingBase entity, EnumHand hand)
	{
		if (!entity.world.isRemote && canInject(entity.world, entity, stack))
		{
			if (inject(entity.world, entity, stack))
			{
				player.setHeldItem(hand, new ItemStack(MFRThings.syringeEmptyItem));
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean isEmpty(@Nonnull ItemStack syringe)
	{
		return false;
	}

	@Nonnull
	@Override
	public ItemStack getEmptySyringe(@Nonnull ItemStack syringe)
	{
		return new ItemStack(MFRThings.syringeEmptyItem);
	}
}
