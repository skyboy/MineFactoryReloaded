package powercrystals.minefactoryreloaded.gui.client;

import net.minecraft.client.gui.GuiButton;

import powercrystals.minefactoryreloaded.gui.container.ContainerFactoryInventory;
import powercrystals.minefactoryreloaded.net.MFRPacket;
import powercrystals.minefactoryreloaded.tile.machine.routing.TileEntityItemRouter;

public class GuiItemRouter extends GuiFactoryInventory
{
	private TileEntityItemRouter _router;

	private GuiButton _matchLevels;

	public GuiItemRouter(ContainerFactoryInventory container, TileEntityItemRouter router)
	{
		super(container, router);
		_router = router;
		ySize = 226;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initGui()
	{
		super.initGui();

		int xOffset = (this.width - this.xSize) / 2;
		int yOffset = (this.height - this.ySize) / 2;

		_matchLevels =  new GuiButton(1, xOffset + 7,  yOffset + 15, 120, 20, "Reject Unmapped: NO");

		buttonList.add(_matchLevels);
	}

	@Override
	public void updateScreen()
	{
		super.updateScreen();
		_matchLevels.displayString  = _router.getRejectUnmapped() ? "Reject Unmapped: YES" : "Reject Unmapped: NO";
	}

	@Override
	protected void actionPerformed(GuiButton button)
	{
		MFRPacket.sendRouterButtonToServer(_tileEntity, button.id);
	}
}
