package powercrystals.minefactoryreloaded.gui.client;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import powercrystals.minefactoryreloaded.MFRProps;

import javax.annotation.Nonnull;

public class GuiNeedlegun extends GuiContainer
{
	private static final ResourceLocation needleGunGUI = new ResourceLocation(MFRProps.GUI_FOLDER + "needlegun.png");
	private String name;
	public GuiNeedlegun(Container container, @Nonnull ItemStack item)
	{
		super(container);
		name = item.getDisplayName();
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		fontRenderer.drawString(name, 8, 6, 4210752);
		fontRenderer.drawString(I18n.translateToLocal("container.inventory"), 8, ySize - 96 + 4, 4210752);

		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float gameTicks, int mouseX, int mouseY)
	{
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.renderEngine.bindTexture(needleGunGUI);
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
	}
}
