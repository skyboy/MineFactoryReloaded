package powercrystals.minefactoryreloaded.gui.client;

import powercrystals.minefactoryreloaded.core.MFRUtil;
import powercrystals.minefactoryreloaded.gui.container.ContainerBioReactor;
import powercrystals.minefactoryreloaded.tile.machine.processing.TileEntityBioReactor;

public class GuiBioReactor extends GuiFactoryInventory {

	private static final int _barBurnIndex = 1;
	private static final int _barValueIndex = 0;

	protected TileEntityBioReactor _teReactor;

	public GuiBioReactor(ContainerBioReactor container, TileEntityBioReactor tileentity) {

		super(container, tileentity);
		_teReactor = tileentity;
		ySize = 195;
		_tanksOffsetX = 132;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {

		super.drawGuiContainerForegroundLayer(mouseX, mouseY);

		drawBar(150, 75, _teReactor.getOutputValueMax(), _teReactor.getOutputValue(), _barValueIndex);
		drawBar(160, 75, _teReactor.getBurnTimeMax(), _teReactor.getBurnTime(), _barBurnIndex);
	}

	@Override
	protected void drawTooltips(int mouseX, int mouseY) {

		if (isPointInRegion(150, 15, 8, 60, mouseX, mouseY)) {
			drawBarTooltip(MFRUtil.efficiency(), "", _teReactor.getOutputValue(), _teReactor.getOutputValueMax(), mouseX, mouseY);
		} else if (isPointInRegion(160, 15, 8, 60, mouseX, mouseY)) {
			drawBarTooltip(MFRUtil.buffer(), "", _teReactor.getBurnTime(), _teReactor.getBurnTimeMax(), mouseX, mouseY);
		} else
			super.drawTooltips(mouseX, mouseY);
	}

}
