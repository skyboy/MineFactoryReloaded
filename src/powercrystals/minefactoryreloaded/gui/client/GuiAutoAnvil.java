package powercrystals.minefactoryreloaded.gui.client;

import cpw.mods.fml.common.network.PacketDispatcher;

import net.minecraft.client.gui.GuiButton;

import powercrystals.core.net.PacketWrapper;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.gui.container.ContainerFactoryPowered;
import powercrystals.minefactoryreloaded.net.Packets;
import powercrystals.minefactoryreloaded.tile.machine.TileEntityAutoAnvil;

public class GuiAutoAnvil extends GuiFactoryPowered
{
	private TileEntityAutoAnvil _anvil;

	private GuiButton _repairToggle;

	public GuiAutoAnvil(ContainerFactoryPowered container, TileEntityAutoAnvil te)
	{
		super(container, te);
		_anvil = te;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initGui()
	{
		super.initGui();
		_repairToggle = new GuiButton(1, (this.width - this.xSize) / 2 + 34, (this.height - this.ySize) / 2 + 47, 85, 20, "Repair Only: Off");
		_repairToggle.displayString = _anvil.getRepairOnly() ? "Repair Only: On" : "Repair Only: Off";
		buttonList.add(_repairToggle);
	}

	@Override
	public void updateScreen()
	{
		super.updateScreen();
		_repairToggle.displayString = _anvil.getRepairOnly() ? "Repair Only: On" : "Repair Only: Off";
	}

	@Override
	protected void actionPerformed(GuiButton button)
	{
		if(button.id == 1)
		{
			PacketDispatcher.sendPacketToServer(PacketWrapper.createPacket(MineFactoryReloadedCore.modNetworkChannel, Packets.RouterButton,
					new Object[] { _anvil.xCoord, _anvil.yCoord, _anvil.zCoord, 1 }));
		}
	}

}
