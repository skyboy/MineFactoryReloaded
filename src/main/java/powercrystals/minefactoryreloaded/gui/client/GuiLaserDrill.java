package powercrystals.minefactoryreloaded.gui.client;

import static powercrystals.minefactoryreloaded.core.MFRUtil.*;

import powercrystals.minefactoryreloaded.gui.container.ContainerFactoryInventory;
import powercrystals.minefactoryreloaded.tile.machine.processing.TileEntityLaserDrill;

public class GuiLaserDrill extends GuiFactoryInventory {

	private static final int _barEnergyIndex = 0;
	private static final int _barWorkIndex = 1;

	private TileEntityLaserDrill _drill;

	public GuiLaserDrill(ContainerFactoryInventory container, TileEntityLaserDrill tileentity) {

		super(container, tileentity);
		_drill = tileentity;
		ySize = 181;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {

		super.drawGuiContainerForegroundLayer(mouseX, mouseY);

		drawBar(150, 75, _drill.getEnergyMax(), _drill.getEnergyStored(), _barEnergyIndex);
		drawBar(160, 75, _drill.getWorkMax(), _drill.getWorkDone(), _barWorkIndex);
	}

	@Override
	protected void drawTooltips(int mouseX, int mouseY) {

		super.drawTooltips(mouseX, mouseY);

		if (isPointInRegion(150, 15, 8, 60, mouseX, mouseY)) {
			drawBarTooltip(energy(), "RF", _drill.getEnergyStored(), _drill.getEnergyMax(), mouseX, mouseY);
		} else if (isPointInRegion(160, 15, 8, 60, mouseX, mouseY)) {
			drawBarTooltip(work(), "Wk", _drill.getWorkDone(), _drill.getWorkMax(), mouseX, mouseY);
		}
	}

}
