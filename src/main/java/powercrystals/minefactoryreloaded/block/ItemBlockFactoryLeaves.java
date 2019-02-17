package powercrystals.minefactoryreloaded.block;

import net.minecraft.item.ItemStack;

public class ItemBlockFactoryLeaves extends ItemBlockFactory {

	public ItemBlockFactoryLeaves(net.minecraft.block.Block id) {

		super(id);
		setHasSubtypes(true);
		setNames(BlockRubberLeaves.Variant.NAMES);
	}

	@Override
	public int getMetadata(int par1) {

		return par1 ^ 4;
	}

	@Override
	public int getItemBurnTime(ItemStack stack) {

		return 4 * (stack.getItemDamage() + 1);
	}

}
