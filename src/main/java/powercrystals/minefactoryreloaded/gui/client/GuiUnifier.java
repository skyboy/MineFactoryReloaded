package powercrystals.minefactoryreloaded.gui.client;

import powercrystals.minefactoryreloaded.core.MFRUtil;
import powercrystals.minefactoryreloaded.gui.container.ContainerUnifier;
import powercrystals.minefactoryreloaded.tile.machine.processing.TileEntityUnifier;

public class GuiUnifier extends GuiFactoryInventory {

	public GuiUnifier(ContainerUnifier container, TileEntityUnifier te) {

		super(container, te);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {

		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		fontRenderer.drawString(MFRUtil.localize("info.mfr.preferences"), 48, 14, 4210752);
	}

}
