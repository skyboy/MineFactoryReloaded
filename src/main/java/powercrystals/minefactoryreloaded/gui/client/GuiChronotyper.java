package powercrystals.minefactoryreloaded.gui.client;

import net.minecraft.client.gui.GuiButton;

import powercrystals.minefactoryreloaded.gui.container.ContainerFactoryPowered;
import powercrystals.minefactoryreloaded.net.MFRPacket;
import powercrystals.minefactoryreloaded.tile.machine.animals.TileEntityChronotyper;

public class GuiChronotyper extends GuiFactoryPowered
{
	private GuiButton _ageToggle;
	private TileEntityChronotyper _chronotyper;
	
	public GuiChronotyper(ContainerFactoryPowered container, TileEntityChronotyper te)
	{
		super(container, te);
		_chronotyper = te;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void initGui()
	{
		super.initGui();
		
		int xOffset = (this.width - this.xSize) / 2;
		int yOffset = (this.height - this.ySize) / 2;
		
		_ageToggle = new GuiButton(1, xOffset + 7, yOffset + 14, 110, 20, "Moving: ");
		
		buttonList.add(_ageToggle);
	}
	
	@Override
	public void updateScreen()
	{
		super.updateScreen();
		_ageToggle.displayString = "Moving: " + (_chronotyper.getMoveOld() ? "Adults" : "Babies");
	}
	
	@Override
	protected void actionPerformed(GuiButton button)
	{
		if(button.id == 1)
		{
			MFRPacket.sendChronotyperButtonToServer(_tileEntity);
		}
	}
}
