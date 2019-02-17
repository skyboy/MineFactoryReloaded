package powercrystals.minefactoryreloaded.item.base;

import cofh.core.item.ItemArmorCore;
import cofh.core.render.IModelRegister;
import cofh.core.util.core.IInitializer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import powercrystals.minefactoryreloaded.MFRProps;
import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.render.ModelHelper;
import powercrystals.minefactoryreloaded.setup.MFRThings;

import java.util.Locale;

public class ItemFactoryArmor extends ItemArmorCore implements IInitializer, IModelRegister {

	public static final ItemArmor.ArmorMaterial PLASTIC_ARMOR = EnumHelper.addArmorMaterial("mfr:plastic", "plastic", 3, new int[] { 1, 2, 2, 1 }, 7, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 0);
	public static final ItemArmor.ArmorMaterial GLASS_ARMOR = EnumHelper.addArmorMaterial("mfr:glass", "glass", 3, new int[] { 0, 0, 0, 0 }, 0, SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND, 0);

	private String modelName;
	private String variant;

	private static final String getName(ItemArmor.ArmorMaterial mat) {

		String r = mat.name().toLowerCase(Locale.US);
		int i = r.indexOf(':') + 1;
		return i > 0 ? r.substring(i, r.length()) : r;
	}

	public ItemFactoryArmor(ItemArmor.ArmorMaterial mat, EntityEquipmentSlot type) {

		super(mat, type);
		setMaxStackSize(1);
		String prefix = MFRProps.ARMOR_TEXTURE_FOLDER + getName(mat);
		setArmorTextures(new String[] { prefix + "_layer_1.png", prefix + "_layer_2.png" });
		MFRThings.registerInitializer(this);
		MineFactoryReloadedCore.proxy.addModelRegister(this);
	}

	@Override
	public Item setUnlocalizedName(String name) {

		super.setUnlocalizedName(name);
		return this;
	}

	@Override
	public String toString() {

		StringBuilder b = new StringBuilder(getClass().getName());
		b.append('@').append(System.identityHashCode(this)).append('{');
		b.append("l:").append(getUnlocalizedName());
		b.append('}');
		return b.toString();
	}

	@Override public boolean preInit() {

		return false;
	}

	@Override
	public boolean initialize() {

		MFRRegistry.registerItem(this);
		return true;
	}

	public ItemFactoryArmor setModelLocation(String modelName, String variant) {

		this.modelName = modelName;
		this.variant = variant;

		return this;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels() {

		ModelHelper.registerModel(this, modelName, variant);
	}
}
