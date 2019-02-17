package powercrystals.minefactoryreloaded.item.gun.ammo;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import powercrystals.minefactoryreloaded.api.handler.INeedleAmmo;
import powercrystals.minefactoryreloaded.core.MFRUtil;
import powercrystals.minefactoryreloaded.item.base.ItemFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public abstract class ItemNeedlegunAmmo extends ItemFactory implements INeedleAmmo {

	public ItemNeedlegunAmmo() {
		setHasSubtypes(false);
	}

	@Override
	public void addInformation(@Nonnull ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag tooltipFlag) {
		super.addInformation(stack, world, tooltip, tooltipFlag);
		tooltip.add(String.format(MFRUtil.localize("tip.info.mfr.needlegun.ammo", true),
				(stack.getMaxDamage() - stack.getItemDamage() + 1)));
	}

}
