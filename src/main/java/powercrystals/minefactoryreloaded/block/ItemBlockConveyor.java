package powercrystals.minefactoryreloaded.block;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class ItemBlockConveyor extends ItemBlockFactory {

	public ItemBlockConveyor(Block block, String[] names) {

		super(block, names);
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {

		if (isInCreativeTab(tab)) {
			items.add(new ItemStack(this, 1, 16));
			for (int i = 0, e = 16; i < e; i++)
				items.add(new ItemStack(this, 1, i));
		}
	}
}
