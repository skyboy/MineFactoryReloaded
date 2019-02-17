package powercrystals.minefactoryreloaded.item.base;

import com.google.common.collect.Multimap;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class ItemFactoryTool extends ItemFactory {

	protected int getWeaponDamage(@Nonnull ItemStack stack) {
		return 0;
	}

	@Override
	public Multimap getAttributeModifiers(EntityEquipmentSlot slot, @Nonnull ItemStack stack) {
		Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(slot, stack);
		
		if (slot != EntityEquipmentSlot.MAINHAND)
			return multimap;
		
		int dmg = getWeaponDamage(stack);
		if (dmg != 0) {
			multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(),
					new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", dmg, 0));
		}
		return multimap;
	}

}
