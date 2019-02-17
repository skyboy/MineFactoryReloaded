package powercrystals.minefactoryreloaded.block;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class ItemBlockFactoryTree extends ItemBlockFactory {

	public ItemBlockFactoryTree(net.minecraft.block.Block id) {

		super(id);
		setNames(new String[] { "normal", "sacred", "mega", "massive" });
	}

	@Override
	public boolean hasEffect(@Nonnull ItemStack stack) {

		return stack.getItemDamage() == 3;
	}

	@Override
	public int getItemBurnTime(ItemStack stack) {

		return 130;
	}

}
