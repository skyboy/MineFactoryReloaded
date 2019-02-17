package powercrystals.minefactoryreloaded.api.mob;

import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandom;

import javax.annotation.Nonnull;

public class MobDrop extends WeightedRandom.Item {

	@Nonnull
	private ItemStack _stack;

	public MobDrop(int weight, @Nonnull ItemStack stack) {

		super(weight);
		_stack = stack;
	}

	@Nonnull
	public ItemStack getStack() {

		if (_stack.isEmpty()) return ItemStack.EMPTY;
		return _stack.copy();
	}

}
