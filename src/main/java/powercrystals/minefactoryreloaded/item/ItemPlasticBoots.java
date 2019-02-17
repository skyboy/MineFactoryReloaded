package powercrystals.minefactoryreloaded.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import powercrystals.minefactoryreloaded.item.base.ItemFactoryArmor;
import powercrystals.minefactoryreloaded.render.ModelHelper;

import javax.annotation.Nonnull;

import static cofh.core.util.helpers.ItemHelper.getItemFromStack;
import static cofh.core.util.helpers.ItemHelper.itemsEqual;
import static powercrystals.minefactoryreloaded.setup.MFRThings.*;

public class ItemPlasticBoots extends ItemFactoryArmor {

	public ItemPlasticBoots() {

		super(ItemFactoryArmor.PLASTIC_ARMOR, EntityEquipmentSlot.FEET);
		setUnlocalizedName("mfr.plastic.armor.boots");
	}

	@Override
	public void onArmorTick(World world, EntityPlayer player, @Nonnull ItemStack itemStack) {

		l: if (itemsEqual(this, plasticBootsItem)) {
			if (!itemsEqual(getItemFromStack(player.getItemStackFromSlot(EntityEquipmentSlot.HEAD)), plasticHelmetItem)) {
				break l;
			}
			if (!itemsEqual(getItemFromStack(player.getItemStackFromSlot(EntityEquipmentSlot.CHEST)), plasticChestplateItem)) {
				break l;
			}
			if (!itemsEqual(getItemFromStack(player.getItemStackFromSlot(EntityEquipmentSlot.LEGS)), plasticLeggingsItem)) {
				break l;
			}
			player.removePotionEffect(MobEffects.POISON);
			player.removePotionEffect(MobEffects.NAUSEA);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels() {

		ModelHelper.registerModel(this, "armor", "type=boots");
	}
}
