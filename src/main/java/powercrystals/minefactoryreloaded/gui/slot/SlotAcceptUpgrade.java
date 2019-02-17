package powercrystals.minefactoryreloaded.gui.slot;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryInventory;

import javax.annotation.Nonnull;

public class SlotAcceptUpgrade extends Slot
{
	protected TileEntityFactoryInventory _inv;

	public static TextureAtlasSprite background;

	public SlotAcceptUpgrade(TileEntityFactoryInventory inv, int index, int x, int y)
	{
		super(inv, index, x, y);
		_inv = inv;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite getBackgroundSprite() {

		return background;
	}

	@Override
	public boolean isItemValid(@Nonnull ItemStack stack)
	{
		return _inv.isUsableAugment(stack);
	}
}
