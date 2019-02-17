package powercrystals.minefactoryreloaded.gui.client;

import static powercrystals.minefactoryreloaded.core.MFRUtil.work;

import powercrystals.minefactoryreloaded.gui.container.ContainerFactoryInventory;
import powercrystals.minefactoryreloaded.tile.machine.power.TileEntitySteamBoiler;

public class GuiSteamBoiler extends GuiFactoryInventory {

	protected static final int _barWorkIndex = 1;
	protected static final int _barTempIndex = 0;
	private TileEntitySteamBoiler _boiler;

	public GuiSteamBoiler(ContainerFactoryInventory container, TileEntitySteamBoiler tileentity) {

		super(container, tileentity);
		_boiler = tileentity;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {

		super.drawGuiContainerForegroundLayer(mouseX, mouseY);

		drawBar(150, 75, _boiler.getWorkMax(), _boiler.getWorkDone(), _barWorkIndex);
		drawBar(160, 75, TileEntitySteamBoiler.maxTemp + 20, (int) _boiler.getTemp() + 20, _barTempIndex);
	}

	@Override
	protected void drawTooltips(int mouseX, int mouseY) {

		if (isPointInRegion(150, 15, 8, 60, mouseX, mouseY)) {
			drawBarTooltip(work(), "Wk", _boiler.getWorkDone(), _boiler.getWorkMax(), mouseX, mouseY);
		} else if (isPointInRegion(160, 15, 8, 60, mouseX, mouseY)) {
			drawBarTooltip("Temperature", "C", _boiler.getTemp() + 20, TileEntitySteamBoiler.maxTemp + 20, mouseX, mouseY);
		} else
			super.drawTooltips(mouseX, mouseY);
	}

}
