package powercrystals.minefactoryreloaded.gui.client;

import net.minecraft.client.gui.GuiButton;

import powercrystals.minefactoryreloaded.core.MFRUtil;
import powercrystals.minefactoryreloaded.gui.container.ContainerUpgradeable;
import powercrystals.minefactoryreloaded.net.MFRPacket;
import powercrystals.minefactoryreloaded.tile.machine.plants.TileEntityPlanter;

public class GuiPlanter extends GuiUpgradeable {

	private TileEntityPlanter _planter;

	private GuiButton _consumeToggle;

	public GuiPlanter(ContainerUpgradeable container, TileEntityPlanter te) {

		super(container, te);
		_planter = te;
		ySize = 201;
	}

	@Override
	public void initGui() {

		super.initGui();
		_consumeToggle = new GuiButton(1, (this.width - this.xSize) / 2 + 7, (this.height - this.ySize) / 2 + 87, 100, 20, "");
		buttonList.add(_consumeToggle);
	}

	@Override
	protected void updateElementInformation() {

		_consumeToggle.displayString = _planter.getConsumeAll() ? "Consume Stack: Off" : "Consume Stack: On";
	}

	@Override
	protected void actionPerformed(GuiButton button) {

		MFRPacket.sendRouterButtonToServer(_tileEntity, button.id);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {

		super.drawGuiContainerForegroundLayer(mouseX, mouseY);

		fontRenderer.drawString(MFRUtil.localize("info.cofh.filter"), 8, 22, 4210752);
	}

}
