package powercrystals.minefactoryreloaded.gui.client;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import powercrystals.minefactoryreloaded.core.MFRUtil;
import powercrystals.minefactoryreloaded.gui.container.ContainerLiquiCrafter;
import powercrystals.minefactoryreloaded.tile.machine.TileEntityLiquiCrafter;

import java.util.Collections;

public class GuiLiquiCrafter extends GuiFactoryInventory {

	private TileEntityLiquiCrafter _crafter;
	private static final int TANK_OFFSET_X = -22;

	public GuiLiquiCrafter(ContainerLiquiCrafter container, TileEntityLiquiCrafter router) {

		super(container, router);
		_crafter = router;
		xSize = 232;
		ySize = 215;
		_xOffset = _xOffset + 28;
		_renderTanks = false;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {

		super.drawGuiContainerForegroundLayer(mouseX, mouseY);

		fontRenderer.drawString(I18n.translateToLocal("info.mfr.template"), 67 + 27, 27, 4210752);
		fontRenderer.drawString(I18n.translateToLocal("info.mfr.output"), 128 + 27, 26, 4210752);

		FluidTankInfo[] tanks = _crafter.getTankInfo();

		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		for (int i = 0; i < 9; i++) {
			FluidStack l = tanks[i].fluid;
			if (l != null) {
				drawTank(TANK_OFFSET_X + (i % 3 * 18), 43 + (i / 3 * 35), l, l.amount * 33 / tanks[i].capacity);
			}
		}

		if (_container.drops) {
			bindTexture(texture);
			this.drawTexturedModalRect(130, 39, 232, 50, 24, 16);
		}
	}

	@Override
	protected void drawTooltips(int mouseX, int mouseY) {

		if (isPointInRegion(TANK_OFFSET_X + 1, 11, 18 * 3 - 2, 35 * 3 - 2, mouseX, mouseY)) {
			int tankX = mouseX - TANK_OFFSET_X - this.guiLeft;
			int tankY = mouseY - 10 - this.guiTop;
			if ((tankX % 18 >= 16) | tankY % 35 >= 33)
				return;
			tankX /= 18;
			tankY /= 35;
			int i = tankX + tankY * 3;
			drawTankTooltip(_crafter.getTanks()[i], mouseX, mouseY);
		} else if (_container.drops && isPointInRegion(130, 39, 24, 16, mouseX, mouseY)) {
			this.drawTooltip(Collections.singletonList(MFRUtil.localize("container.mfr.pendingDrops")), mouseX, mouseY);
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float gameTicks, int mouseX, int mouseY) {

		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		bindTexture(texture);
		int x = (width - xSize - 56) / 2;
		int y = (height - ySize) / 2;
		this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
	}

	//TODO replace with call to drawFluid from GuiBase
	@Override
	protected void drawTank(int xOffset, int yOffset, FluidStack stack, int level) {

		if (stack == null) return;
		Fluid fluid = stack.getFluid();
		if (fluid == null) return;

		int vertOffset = 0;

		ResourceLocation icon = fluid.getStill();
		if (icon == null)
			icon = FluidRegistry.LAVA.getFlowing();

		while (level > 0) {
			int texHeight = 0;

			if (level > 16) {
				texHeight = 16;
				level -= 16;
			} else {
				texHeight = level;
				level = 0;
			}

			bindTexture();

			drawTexturedModalRect(xOffset, yOffset - texHeight - vertOffset, mc.getTextureMapBlocks().getAtlasSprite(icon.toString()), 16, texHeight);
			vertOffset = vertOffset + 16;
		}

		bindTexture(texture);
		this.drawTexturedModalRect(xOffset, yOffset - 33, 232, 0, 16, 33);
	}

}
