package powercrystals.minefactoryreloaded.gui.slot;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import powercrystals.minefactoryreloaded.item.ItemSafariNet;

import javax.annotation.Nonnull;

public class SlotAcceptReusableSafariNet extends Slot
{
	public static TextureAtlasSprite background;
	
	public SlotAcceptReusableSafariNet(IInventory inv, int index, int x, int y)
	{
		super(inv, index, x, y);
	}

	@Override
	public TextureAtlasSprite getBackgroundSprite() {
		
		return background;
	}

	@Override
	public boolean isItemValid(@Nonnull ItemStack stack)
	{
		return !ItemSafariNet.isEmpty(stack) && !ItemSafariNet.isSingleUse(stack);
	}
}
