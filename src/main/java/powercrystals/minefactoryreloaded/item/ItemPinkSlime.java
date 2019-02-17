package powercrystals.minefactoryreloaded.item;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import powercrystals.minefactoryreloaded.item.base.ItemMulti;
import powercrystals.minefactoryreloaded.render.ModelHelper;

public class ItemPinkSlime extends ItemMulti {

	public ItemPinkSlime() {

		setNames("ball", "gem");
		setUnlocalizedName("mfr.pink_slime");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels() {

		ModelHelper.registerModel(this, "pink_slime", "variant=ball");
		ModelHelper.registerModel(this, 1, "pink_slime", "variant=gem");
	}
}
