package powercrystals.minefactoryreloaded.modhelpers.vanilla.handlers.grindables;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import powercrystals.minefactoryreloaded.api.mob.IFactoryGrindable;
import powercrystals.minefactoryreloaded.api.mob.MobDrop;
import powercrystals.minefactoryreloaded.core.MFRUtil;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class GrindableZombiePigman implements IFactoryGrindable
{
	private static final UUID signBoostModifierUUID = UUID.fromString("1CBBA087-C48F-4F33-8BD4-0E85FC2F6A0A");
	private static final AttributeModifier modifier = (new AttributeModifier(signBoostModifierUUID,
			"Battlesign boost", 10D, 0)).setSaved(false);
	@Nonnull
	public static final ItemStack sign;
	static {
		sign = new ItemStack(Items.SIGN);
		sign.addEnchantment(Enchantments.SHARPNESS, 4);
		sign.addEnchantment(Enchantments.KNOCKBACK, 2);
		sign.addEnchantment(Enchantments.FIRE_ASPECT, 1);
		NBTTagList list = new NBTTagList();
		list.appendTag(MFRUtil.writeModifierToNBT(
				SharedMonsterAttributes.MAX_HEALTH.getName(), modifier));
		sign.setTagInfo("AttributeModifiers", list);
	}

	@Override
	public Class<? extends EntityLivingBase> getGrindableEntity()
	{
		return EntityPigZombie.class;
	}

	@Override
	public List<MobDrop> grind(World world, EntityLivingBase entity, Random random)
	{
		List<MobDrop> drops = new ArrayList<>();

		if (random.nextInt(3000) == 0)
			drops.add(new MobDrop(10, sign.copy()));

		return drops;
	}

	@Override
	public boolean processEntity(EntityLivingBase entity)
	{
		return false;
	}
}
