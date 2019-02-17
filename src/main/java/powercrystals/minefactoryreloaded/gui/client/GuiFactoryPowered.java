package powercrystals.minefactoryreloaded.gui.client;

import static powercrystals.minefactoryreloaded.core.MFRUtil.*;

import powercrystals.minefactoryreloaded.core.MFRUtil;
import powercrystals.minefactoryreloaded.gui.container.ContainerFactoryPowered;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryPowered;

public class GuiFactoryPowered extends GuiFactoryInventory {

	protected TileEntityFactoryPowered _tePowered;

	protected static final int _barEnergyIndex = 0;
	protected static final int _barWorkIndex = 1;
	protected static final int _barIdleIndex = 2;

	public GuiFactoryPowered(ContainerFactoryPowered container, TileEntityFactoryPowered te) {

		super(container, te);
		_tePowered = te;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {

		super.drawGuiContainerForegroundLayer(mouseX, mouseY);

		drawBar(140, 75, _tePowered.getEnergyStoredMax(), _tePowered.getEnergyStored(), _barEnergyIndex);
		drawBar(150, 75, _tePowered.getWorkMax(), _tePowered.getWorkDone(), _barWorkIndex);
		drawBar(160, 75, _tePowered.getIdleTicksMax(), _tePowered.getIdleTicks(), _barIdleIndex);
	}

	@Override
	protected void drawTooltips(int mouseX, int mouseY) {

		if (isPointInRegion(140, 15, 8, 60, mouseX, mouseY)) {
			drawBarTooltip(energy(), "RF", _tePowered.getEnergyStored(), _tePowered.getEnergyStoredMax(), mouseX, mouseY);
		} else if (isPointInRegion(150, 15, 8, 60, mouseX, mouseY)) {
			drawBarTooltip(work(), "Wk", _tePowered.getWorkDone(), _tePowered.getWorkMax(), mouseX, mouseY);
		} else if (isPointInRegion(160, 15, 8, 60, mouseX, mouseY)) {
			String[] e = null;
			if (_container.drops) {
				e = new String[] {"", MFRUtil.localize("container.mfr.pendingDrops")};
			} else if (_container.redstone) {
				e = new String[] {"", MFRUtil.localize("container.mfr.redstoneSignal")};
			}
			drawBarTooltip(idle(), "t", _tePowered.getIdleTicks(), _tePowered.getIdleTicksMax(), mouseX, mouseY, e);
		} else
			super.drawTooltips(mouseX, mouseY);
	}

}
