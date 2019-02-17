package powercrystals.minefactoryreloaded.block;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class ItemBlockFactoryRoad extends ItemBlockFactory {

	public ItemBlockFactoryRoad(net.minecraft.block.Block blockId) {

		super(blockId);
		setMaxDamage(0);
		setHasSubtypes(true);
		setNames(new String[] { "default", "light.off", "light.on", "light.inverted.off", "light.inverted.on" });
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {

		if (isInCreativeTab(tab)) {
			items.add(new ItemStack(this, 1, 0));
			items.add(new ItemStack(this, 1, 1));
			items.add(new ItemStack(this, 1, 4));
		}
	}
}
