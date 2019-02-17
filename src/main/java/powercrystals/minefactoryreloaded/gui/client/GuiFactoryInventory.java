package powercrystals.minefactoryreloaded.gui.client;

import cofh.core.fluid.FluidTankCore;
import cofh.core.gui.GuiContainerCore;
import cofh.core.init.CoreProps;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import powercrystals.minefactoryreloaded.MFRProps;
import powercrystals.minefactoryreloaded.core.MFRUtil;
import powercrystals.minefactoryreloaded.gui.container.ContainerFactoryInventory;
import powercrystals.minefactoryreloaded.gui.slot.SlotFake;
import powercrystals.minefactoryreloaded.net.MFRPacket;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryInventory;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GuiFactoryInventory extends GuiContainerCore {

	protected static DecimalFormat decimal_format = new DecimalFormat();

	static {
		decimal_format.setMaximumFractionDigits(1);
		decimal_format.setMinimumFractionDigits(1);
	}

	protected TileEntityFactoryInventory _tileEntity;
	protected ContainerFactoryInventory _container;
	protected int _barSizeMax = 60;

	protected int _tankSizeMax = 60;
	protected int _tanksOffsetX = 122;
	protected int _tanksOffsetY = 15;
	protected int _xOffset = 8;

	protected boolean _renderTanks = true;

	public GuiFactoryInventory(ContainerFactoryInventory container, TileEntityFactoryInventory tileEntity) {

		super(container, new ResourceLocation(MFRProps.GUI_FOLDER + tileEntity.getGuiBackground() + ".png"));
		_container = container;
		drawInventory = drawTitle = false;
		_tileEntity = tileEntity;
		if (CoreProps.enableColorBlindTextures) {
			ResourceLocation t = new ResourceLocation(
					MFRProps.GUI_FOLDER + _tileEntity.getGuiBackground() + "_cb.png");
			if (textureExists(t))
				texture = t;
		}
	}

	private boolean textureExists(ResourceLocation texture) {

		try {
			Minecraft.getMinecraft().getResourceManager().getAllResources(texture);
			return true;
		} catch (Throwable t) { // pokemon!
			return false;
		}
	}

	@Override
	protected void mouseClicked(int x, int y, int button) throws IOException {

		super.mouseClicked(x, y, button);

		x -= guiLeft;
		y -= guiTop;

		for (Object o : inventorySlots.inventorySlots) {
			if (!(o instanceof SlotFake)) {
				continue;
			}
			SlotFake s = (SlotFake) o;
			if (x >= s.xPos && x <= s.xPos + 16 && y >= s.yPos &&
					y <= s.yPos + 16) {
				MFRPacket.sendFakeSlotToServer(_tileEntity, Minecraft.getMinecraft().player.getEntityId(),
						s.slotNumber, (byte) button);
			}
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {

		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		GlStateManager.color(1f, 1f, 1f, 1f);
		fontRenderer.drawString(_tileEntity.getName(), _xOffset, 6, 4210752);
		fontRenderer.drawString(I18n.translateToLocal("container.inventory"), _xOffset, ySize - 96 + 3, 4210752);

		if (_renderTanks) {
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			FluidTankInfo[] tanks = _tileEntity.getTankInfo();
			int n = tanks.length > 3 ? 3 : tanks.length;
			if (n > 0) {
				for (int i = 0; i < n; ++i) {
					if (tanks[i].fluid == null)
						continue;
					int tankSize = tanks[i].fluid.amount * _tankSizeMax / tanks[i].capacity;
					drawTank(_tanksOffsetX - (i * 20), _tanksOffsetY + _tankSizeMax, tanks[i].fluid, tankSize);
				}
			}
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float gameTicks) {

		super.drawScreen(mouseX, mouseY, gameTicks);

		drawTooltips(mouseX, mouseY);
	}

	protected void drawTooltips(int mouseX, int mouseY) {

		FluidTankCore[] tanks = _tileEntity.getTanks();
		int n = tanks.length > 3 ? 3 : tanks.length;
		tanks:
		if (n > 0 && isPointInRegion(_tanksOffsetX - ((n - 1) * 20) + 1, _tanksOffsetY + 1,
				n * 20 - n - 1, _tankSizeMax - 2, mouseX, mouseY)) {
			int tankX = mouseX - this.guiLeft - _tanksOffsetX + (n - 1) * 20;
			if (tankX % 20 >= 16)
				break tanks;
			tankX /= 20;
			tankX = n - tankX - 1;
			drawTankTooltip(tanks[tankX], mouseX, mouseY);
		}
	}

	protected final void drawBar(int xOffset, int yOffset, int max, int current, int tOffset) {

		int size = max > 0 ? (int) (current * (long) _barSizeMax / max) : 0;
		if (size > _barSizeMax)
			size = max;
		if (size < 0)
			size = 0;
		bindTexture(texture);
		drawTexturedModalRect(xOffset, yOffset - size,
				xSize + tOffset * 8 + tOffset, 60 + _barSizeMax - size,
				8, size);
	}

	protected void drawTank(int xOffset, int yOffset, FluidStack stack, int level) {

		if (stack == null)
			return;
		drawFluid(xOffset, yOffset - level, stack, 16, level);
	}

	protected void bindTexture() {

		mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
	}

	protected void drawTankTooltip(FluidTankCore tank, int x, int y) {

		FluidStack fluid = tank.getFluid();
		if (fluid != null)
			drawBarTooltip(MFRUtil.getFluidName(fluid), "mB", fluid.amount, tank.getCapacity(), x, y);
		else
			drawBarTooltip(MFRUtil.empty(), "mB", 0, tank.getCapacity(), x, y);
	}

	protected void drawBarTooltip(String name, String unit, int value, int max, int x, int y) {

		drawBarTooltip(name, unit, value, max, x, y, (String[]) null);
	}

	protected void drawBarTooltip(String name, String unit, int value, int max, int x, int y, String... extra) {

		List<String> lines = new ArrayList<>(2);
		lines.add(name);
		String m = String.valueOf(max);
		StringBuilder v = new StringBuilder(String.valueOf(value));
		v.reverse();
		while (v.length() < m.length())
			v.append(' ');
		v.reverse();
		lines.add(v + " / " + m + " " + unit);
		if (extra != null) {
			Collections.addAll(lines, extra);
		}
		drawTooltip(lines, x, y);
	}

	protected void drawBarTooltip(String name, String unit, float value, float max, int x, int y) {

		List<String> lines = new ArrayList<>(2);
		lines.add(name);
		String m = decimal_format.format(max);
		StringBuilder v = new StringBuilder(decimal_format.format(value));
		v.reverse();
		while (v.length() < m.length())
			v.append(' ');
		v.reverse();
		lines.add(v + " / " + m + " " + unit);
		drawTooltip(lines, x, y);
	}

	protected void drawTooltip(List<String> lines, int x, int y) {

		GlStateManager.pushMatrix();
		GlStateManager.disableDepth();
		GlStateManager.disableRescaleNormal();
		GlStateManager.disableLighting();

		int tooltipWidth = 0;
		int tempWidth;
		int xStart;
		int yStart;

		for (String line1 : lines) {
			tempWidth = fontRenderer.getStringWidth(line1);

			if (tempWidth > tooltipWidth) {
				tooltipWidth = tempWidth;
			}
		}

		xStart = x + 12;
		yStart = y - 12;
		int tooltipHeight = 8;

		if (lines.size() > 1) {
			tooltipHeight += 2 + (lines.size() - 1) * 10;
		}

		if (xStart + tooltipWidth + 6 > width) {
			xStart -= 28 + tooltipWidth;
			xStart &= ~xStart >> 31;
		}

		if (guiTop + yStart + tooltipHeight + 6 > height) {
			yStart = height - tooltipHeight - guiTop - 6;
		}

		this.zLevel = 300.0F;
		itemRender.zLevel = 300.0F;
		int color1 = -267386864;
		drawGradientRect(xStart - 3, yStart - 4, xStart + tooltipWidth + 3, yStart - 3, color1, color1);
		drawGradientRect(xStart - 3, yStart + tooltipHeight + 3, xStart + tooltipWidth + 3, yStart + tooltipHeight + 4,
				color1, color1);
		drawGradientRect(xStart - 3, yStart - 3, xStart + tooltipWidth + 3, yStart + tooltipHeight + 3, color1, color1);
		drawGradientRect(xStart - 4, yStart - 3, xStart - 3, yStart + tooltipHeight + 3, color1, color1);
		drawGradientRect(xStart + tooltipWidth + 3, yStart - 3, xStart + tooltipWidth + 4, yStart + tooltipHeight + 3,
				color1, color1);
		int color2 = 1347420415;
		int color3 = (color2 & 16711422) >> 1 | color2 & -16777216;
		drawGradientRect(xStart - 3, yStart - 3 + 1, xStart - 3 + 1, yStart + tooltipHeight + 3 - 1, color2, color3);
		drawGradientRect(xStart + tooltipWidth + 2, yStart - 3 + 1, xStart + tooltipWidth + 3, yStart + tooltipHeight + 3 - 1,
				color2, color3);
		drawGradientRect(xStart - 3, yStart - 3, xStart + tooltipWidth + 3, yStart - 3 + 1, color2, color2);
		drawGradientRect(xStart - 3, yStart + tooltipHeight + 2, xStart + tooltipWidth + 3, yStart + tooltipHeight + 3, color3,
				color3);

		for (int stringIndex = 0; stringIndex < lines.size(); ++stringIndex) {
			String line = lines.get(stringIndex);

			if (stringIndex == 0) {
				line = "\u00a7F" + line;
			} else {
				line = "\u00a77" + line;
			}

			fontRenderer.drawStringWithShadow(line, xStart, yStart, -1);

			if (stringIndex == 0) {
				yStart += 2;
			}

			yStart += 10;
		}

		GlStateManager.popMatrix();
		GlStateManager.enableDepth();

		this.zLevel = 0.0F;
		itemRender.zLevel = 0.0F;
	}
}
