package powercrystals.minefactoryreloaded.tile.transport;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public enum PlasticPipeUpgrade {

	NONE(new ItemStack(Blocks.AIR), "none") {
		@Nonnull
		@Override
		public ItemStack getDrop() {

			return ItemStack.EMPTY;
		}
	},
	REDSTONE_TORCH(new ItemStack(Blocks.REDSTONE_TORCH), "chat.info.mfr.fluid.install.torch") {
		@Override
		public boolean getPowered(boolean redstonePowered) {

			return !redstonePowered;
		}
	},
	REDSTONE_BLOCK(new ItemStack(Blocks.REDSTONE_BLOCK), "chat.info.mfr.fluid.install.block") {
		@Override
		public boolean getPowered(boolean redstonePowered) {

			return true;
		}
	};

	@Nonnull
	private final ItemStack stack;
	private String chatMessageKey;

	PlasticPipeUpgrade(@Nonnull ItemStack stack, String chatMessageKey) {

		this.stack = stack;
		this.chatMessageKey = chatMessageKey;
	}

	public boolean getPowered(boolean redstonePowered) {

		return redstonePowered;
	}

	@Nonnull
	public ItemStack getDrop() {

		return stack.copy();
	}

	public String getChatMessageKey() {

		return chatMessageKey;
	}

	public static boolean isUpgradeItem(@Nonnull ItemStack stack) {

		for(PlasticPipeUpgrade plasticPipeUpgrade : values()) {
			if (plasticPipeUpgrade.stack.isItemEqual(stack))
				return true;
		}

		return false;
	}

	public static PlasticPipeUpgrade getUpgrade(@Nonnull ItemStack stack) {

		for(PlasticPipeUpgrade plasticPipeUpgrade : values()) {
			if (plasticPipeUpgrade.stack.isItemEqual(stack))
				return plasticPipeUpgrade;
		}

		return NONE;
	}
}
