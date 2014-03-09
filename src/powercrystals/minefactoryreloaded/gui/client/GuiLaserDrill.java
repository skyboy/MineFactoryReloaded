package powercrystals.minefactoryreloaded.gui.client;

import java.util.ArrayList;
import java.util.List;

import powercrystals.minefactoryreloaded.gui.container.ContainerFactoryInventory;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryPowered;
import powercrystals.minefactoryreloaded.tile.machine.TileEntityLaserDrill;

public class GuiLaserDrill extends GuiFactoryInventory
{
	private static final int _barColorEnergy = (0)   | (0 << 8)   | (255 << 16) | (255 << 24);
	private static final int _barColorWork =   (0)   | (255 << 8) | (0 << 16)   | (255 << 24);
	
	private TileEntityLaserDrill _drill;
	
	public GuiLaserDrill(ContainerFactoryInventory container, TileEntityLaserDrill tileentity)
	{
		super(container, tileentity);
		_drill = tileentity;
		ySize = 180;
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		drawBar(150, 75, _drill.getEnergyMax(), _drill.getEnergyStored(), _barColorEnergy);
		drawBar(160, 75, _drill.getWorkMax(), _drill.getWorkDone(), _barColorWork);
		
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
	}
	
	@Override
	protected void drawTooltips(int mouseX, int mouseY)
	{
		super.drawTooltips(mouseX, mouseY);
		
		if(isPointInRegion(150, 15, 8, 60, mouseX, mouseY))
		{
			int energyPerMJ = TileEntityFactoryPowered.energyPerMJ;
			int energyPerEU = TileEntityFactoryPowered.energyPerEU;
			int stored = _drill.getEnergyStored();
			int storedMax = _drill.getEnergyMax();
			List<String> lines = new ArrayList<String>();
			lines.add("Energy");
			lines.add(stored + " / " + storedMax + " " + "RF");
			lines.add(stored / energyPerMJ + " / " + storedMax / energyPerMJ + " " + "MJ");
			lines.add(stored / energyPerEU + " / " + storedMax / energyPerEU + " " + "EU");
			drawTooltip(lines, mouseX, mouseY);
		}
		else if(isPointInRegion(160, 15, 8, 60, mouseX, mouseY))
		{
			drawBarTooltip("Work", "Wk", _drill.getWorkDone(), _drill.getWorkMax(), mouseX, mouseY);
		}
	}
}
