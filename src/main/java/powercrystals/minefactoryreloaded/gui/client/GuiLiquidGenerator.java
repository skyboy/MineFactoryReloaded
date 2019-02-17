package powercrystals.minefactoryreloaded.gui.client;

import powercrystals.minefactoryreloaded.core.MFRUtil;
import powercrystals.minefactoryreloaded.gui.container.ContainerFactoryInventory;
import powercrystals.minefactoryreloaded.tile.base.TileEntityLiquidGenerator;

public class GuiLiquidGenerator extends GuiFactoryInventory {

	private static final int _barEnergyIndex = 0;

	protected TileEntityLiquidGenerator _teGenerator;

	public GuiLiquidGenerator(ContainerFactoryInventory container, TileEntityLiquidGenerator tileentity) {

		super(container, tileentity);
		_teGenerator = tileentity;
		ySize = 166;
		_tanksOffsetX = 142;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {

		super.drawGuiContainerForegroundLayer(mouseX, mouseY);

		drawBar(160, 75, _teGenerator.getBufferMax(), _teGenerator.getBuffer(), _barEnergyIndex);
	}

	@Override
	protected void drawTooltips(int mouseX, int mouseY) {

		if (isPointInRegion(160, 15, 8, 60, mouseX, mouseY)) {
			drawBarTooltip(MFRUtil.energy(), "RF", _teGenerator.getBuffer(), _teGenerator.getBufferMax(), mouseX, mouseY);
		} else
			super.drawTooltips(mouseX, mouseY);
	}

}
