package powercrystals.minefactoryreloaded.gui.client;

import net.minecraft.client.gui.GuiButton;

import powercrystals.minefactoryreloaded.gui.container.ContainerMobRouter;
import powercrystals.minefactoryreloaded.net.MFRPacket;
import powercrystals.minefactoryreloaded.tile.machine.mobs.TileEntityMobRouter;

public class GuiMobRouter extends GuiFactoryPowered
{
	private TileEntityMobRouter _router;
	
	private GuiButton _toggle;
	private GuiButton _mode;
	
	public GuiMobRouter(ContainerMobRouter container, TileEntityMobRouter te)
	{
		super(container, te);
		_router = te;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void initGui()
	{
		super.initGui();
		
		int xOffset = (this.width - this.xSize) / 2;
		int yOffset = (this.height - this.ySize) / 2;
		
		_toggle = new GuiButton(1, xOffset + 7 + 16 + 3, yOffset + 23, 85, 20, "Whitelist");
		_mode = new GuiButton(2, xOffset + 7 + 16 + 3, yOffset + 23 + 19, 85, 20, "Mode");
		updateButtons();
		
		buttonList.add(_toggle);
		buttonList.add(_mode);
	}
	
	@Override
	public void updateScreen()
	{
		super.updateScreen();
		updateButtons();
	}
	
	protected void updateButtons()
	{
		_toggle.displayString = _router.getWhiteList() ? "Whitelist" : "Blacklist";
		String mode = "Unknown";
		switch (_router.getMatchMode())
		{
		case 0:
			mode = "Type Match";
			break;
		case 1:
			mode = "SubType Match";
			break;
		case 2:
			mode = "SuperType Match";
			break;
		case 3:
			mode = "UltraType Match";
			break;
		}
		_mode.displayString = mode;
	}
	
	@Override
	protected void actionPerformed(GuiButton button)
	{
		MFRPacket.sendRouterButtonToServer(_tileEntity, button.id);
	}
}
