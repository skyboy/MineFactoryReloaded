package powercrystals.minefactoryreloaded.gui.client;

import powercrystals.minefactoryreloaded.gui.container.ContainerFactoryInventory;
import powercrystals.minefactoryreloaded.tile.machine.routing.TileEntityLiquidRouter;

public class GuiLiquidRouter extends GuiFactoryInventory {

	public GuiLiquidRouter(ContainerFactoryInventory container, TileEntityLiquidRouter router) {

		super(container, router);
		ySize = 134;
		_renderTanks = false;
	}
}
