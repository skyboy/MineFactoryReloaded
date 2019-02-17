package powercrystals.minefactoryreloaded.gui.client;

import net.minecraft.client.gui.GuiButton;

import powercrystals.minefactoryreloaded.gui.container.ContainerFactoryInventory;
import powercrystals.minefactoryreloaded.net.MFRPacket;
import powercrystals.minefactoryreloaded.tile.machine.routing.TileEntityEnchantmentRouter;

public class GuiEnchantmentRouter extends GuiFactoryInventory {

	private TileEntityEnchantmentRouter _router;

	private GuiButton _matchLevels;
	private GuiButton _rejectUnmapped;

	public GuiEnchantmentRouter(ContainerFactoryInventory container, TileEntityEnchantmentRouter router) {

		super(container, router);
		_router = router;
		ySize = 226;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initGui() {

		super.initGui();

		int xOffset = (this.width - this.xSize) / 2;
		int yOffset = (this.height - this.ySize) / 2;

		_matchLevels = new GuiButton(1, xOffset + 0 + 7 + 0, yOffset + 15, 80, 20, "Levels: NO");
		_rejectUnmapped = new GuiButton(2, xOffset + 176 - 7 - 80, yOffset + 15, 80, 20, "Unmapped: YES");

		buttonList.add(_matchLevels);
		buttonList.add(_rejectUnmapped);

		updateButtons();
	}

	@Override
	public void updateScreen() {

		super.updateScreen();
		updateButtons();
	}

	private void updateButtons() {

		_matchLevels.displayString = _router.getMatchLevels() ? "Levels: YES" : "Levels: NO";
		_rejectUnmapped.displayString = _router.getRejectUnmapped() ? "Unmapped: NO" : "Unmapped: YES";
	}

	@Override
	protected void actionPerformed(GuiButton button) {

		MFRPacket.sendRouterButtonToServer(_tileEntity, button.id);
	}
}
