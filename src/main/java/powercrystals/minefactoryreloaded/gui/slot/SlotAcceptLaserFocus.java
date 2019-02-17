package powercrystals.minefactoryreloaded.gui.slot;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import powercrystals.minefactoryreloaded.setup.MFRThings;

import javax.annotation.Nonnull;

public class SlotAcceptLaserFocus extends Slot {

	public static TextureAtlasSprite background;

	public SlotAcceptLaserFocus(IInventory inv, int index, int x, int y) {

		super(inv, index, x, y);
	}

	@Override
	public boolean isItemValid(@Nonnull ItemStack stack) {

		return !stack.isEmpty() && stack.getItem().equals(MFRThings.laserFocusItem);
	}

	@Override
	public TextureAtlasSprite getBackgroundSprite() {
		
		return background;
	}
}
