package powercrystals.minefactoryreloaded.gui.client;

import powercrystals.minefactoryreloaded.gui.container.ContainerBioReactor;
import powercrystals.minefactoryreloaded.tile.machine.TileEntityBioReactor;

public class GuiBioReactor extends GuiFactoryInventory
{
	private static final int _barColorBurn =  (79)  | (44 << 8)  | (63 << 16)  | (255 << 24);
	private static final int _barColorValue = (55)  | (182 << 8) | (211 << 16) | (255 << 24);
	
	public GuiBioReactor(ContainerBioReactor container, TileEntityBioReactor tileentity)
	{
		super(container, tileentity);
		ySize = 194;
		_tanksOffsetX = 132;
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		
		drawBar(150, 75, ((TileEntityBioReactor)_tileEntity).getOutputValueMax(), ((TileEntityBioReactor)_tileEntity).getOutputValue(), _barColorValue);
		drawBar(160, 75, ((TileEntityBioReactor)_tileEntity).getBurnTimeMax(), ((TileEntityBioReactor)_tileEntity).getBurnTime(), _barColorBurn);
	}
	
	@Override
	protected void drawTooltips(int mouseX, int mouseY)
	{
		if(isPointInRegion(150, 15, 8, 60, mouseX, mouseY))
		{
			drawBarTooltip("Efficiency", "", ((TileEntityBioReactor)_tileEntity).getOutputValue(), ((TileEntityBioReactor)_tileEntity).getOutputValueMax(), mouseX, mouseY);
		}
		else if(isPointInRegion(160, 15, 8, 60, mouseX, mouseY))
		{
			drawBarTooltip("Buffer", "", ((TileEntityBioReactor)_tileEntity).getBurnTime(), ((TileEntityBioReactor)_tileEntity).getBurnTimeMax(), mouseX, mouseY);
		}
		else
			super.drawTooltips(mouseX, mouseY);
	}
}
