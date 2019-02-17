package powercrystals.minefactoryreloaded.item;

import cofh.api.item.IAugmentItem;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import powercrystals.minefactoryreloaded.core.MFRUtil;
import powercrystals.minefactoryreloaded.item.base.ItemMulti;
import powercrystals.minefactoryreloaded.render.ModelHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemUpgrade extends ItemMulti implements IAugmentItem {

	public static final int NEGATIVE_START = (Short.MIN_VALUE >>> 1) & Short.MAX_VALUE;
	
	public ItemUpgrade() {

		setNames(0, "lapis", "tin", "iron", "copper", "bronze", "silver", "gold", "quartz", "diamond", "platinum", "emerald");
		setNames(NEGATIVE_START, "cobble");
		setUnlocalizedName("mfr.upgrade.radius");
		setMaxStackSize(64);
	}

	@Override
	public void addInformation(@Nonnull ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag tooltipFlag) {

		super.addInformation(stack, world, tooltip, tooltipFlag);
		tooltip.add(String.format(MFRUtil.localize("tip.info.mfr.upgrade.radius", true), getAugmentLevel(stack, "radius")));
	}

	//TODO fix upgrades when it comes to former augment level implementation
	//@Override
	public int getAugmentLevel(@Nonnull ItemStack stack, String type) {

		if (type.equals("radius")) {
			int dmg = stack.getItemDamage();
			int mult = dmg >= NEGATIVE_START ? -1 : 1;
			dmg &= NEGATIVE_START - 1;
			return (dmg + 1) * mult;
		}
		return 0;
	}

	@Override
	public AugmentType getAugmentType(@Nonnull ItemStack stack) {

		return AugmentType.BASIC;
	}

	@Override
	public String getAugmentIdentifier(@Nonnull ItemStack stack) {

		return "radius";
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels() {

		for(int i : getMetadataValues()) {
			ModelHelper.registerModel(this, i, "upgrade", "variant=" + getName(i));
		}
	}
}
