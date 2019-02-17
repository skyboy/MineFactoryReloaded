package powercrystals.minefactoryreloaded.item.base;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import powercrystals.minefactoryreloaded.core.MFRDyeColor;
import powercrystals.minefactoryreloaded.render.ModelHelper;

public class ItemFactoryColored extends ItemMulti {

	public ItemFactoryColored() {

		setNames(MFRDyeColor.UNLOC_NAMES);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels() {

		ModelHelper.registerColoredItemModels(this, modelName);
	}
}
