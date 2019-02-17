package powercrystals.minefactoryreloaded.gui.client;

import powercrystals.minefactoryreloaded.core.MFRUtil;
import powercrystals.minefactoryreloaded.gui.container.ContainerFactoryPowered;
import powercrystals.minefactoryreloaded.tile.machine.processing.TileEntityAutoBrewer;

public class GuiAutoBrewer extends GuiFactoryPowered {

	public GuiAutoBrewer(ContainerFactoryPowered container, TileEntityAutoBrewer te) {

		super(container, te);
		ySize = 244;
		_tanksOffsetX = 146;
		_tanksOffsetY = 79;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {

		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		fontRenderer.drawString(MFRUtil.localize("container.mfr.autobrewer.resources"), 79, 14, 0x808080);
	}

}
