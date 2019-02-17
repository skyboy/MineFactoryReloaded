package powercrystals.minefactoryreloaded.gui.client;

import powercrystals.minefactoryreloaded.gui.container.ContainerUpgradeable;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryPowered;

public class GuiUpgradeable extends GuiFactoryPowered {

	public GuiUpgradeable(ContainerUpgradeable container, TileEntityFactoryPowered te) {
		super(container, te);
		ySize = 181;
	}
}
