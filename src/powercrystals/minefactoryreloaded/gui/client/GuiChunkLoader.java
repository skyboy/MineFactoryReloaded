package powercrystals.minefactoryreloaded.gui.client;

import cpw.mods.fml.common.network.PacketDispatcher;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.StatCollector;

import powercrystals.core.net.PacketWrapper;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.gui.container.ContainerFactoryPowered;
import powercrystals.minefactoryreloaded.net.Packets;
import powercrystals.minefactoryreloaded.tile.machine.TileEntityChunkLoader;

public class GuiChunkLoader extends GuiFactoryPowered
{
	private GuiButton _radiusMinus;
	private GuiButton _radiusPlus;
	private TileEntityChunkLoader _cl;
	
	public GuiChunkLoader(ContainerFactoryPowered container, TileEntityChunkLoader te)
	{
		super(container, te);
		_cl = te;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void initGui()
	{
		super.initGui();
		
		int xOffset = (this.width - this.xSize) / 2;
		int yOffset = (this.height - this.ySize) / 2;
		
		_radiusMinus = new GuiButton(1, xOffset + 7, yOffset + 25, 20, 20, "-");
		_radiusPlus =  new GuiButton(2, xOffset + 50, yOffset + 25, 20, 20, "+");
		
		buttonList.add(_radiusMinus);
		buttonList.add(_radiusPlus);
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		
		fontRenderer.drawString(StatCollector.translateToLocal("container.mfr.radius"), _xOffset, 16, 4210752);
		fontRenderer.drawString(_cl.getRadius() + "", _xOffset + 25, 31, 4210752);
		fontRenderer.drawString(StatCollector.translateToLocal("container.mfr.power"), _xOffset, 51, 4210752);
		fontRenderer.drawString(_cl.getActivationEnergy() + " RF", _xOffset + 17, 51 + 11, 4210752);
	}
	
	@Override
	protected void actionPerformed(GuiButton button)
	{
		if (button.id == 1)
		{
			PacketDispatcher.sendPacketToServer(PacketWrapper.createPacket(MineFactoryReloadedCore.modNetworkChannel, Packets.RouterButton,
					new Object[] { _cl.xCoord, _cl.yCoord, _cl.zCoord, _cl.getRadius() - 1 }));
		}
		else if (button.id == 2)
		{
			PacketDispatcher.sendPacketToServer(PacketWrapper.createPacket(MineFactoryReloadedCore.modNetworkChannel, Packets.RouterButton,
					new Object[] { _cl.xCoord, _cl.yCoord, _cl.zCoord, _cl.getRadius() + 1 }));
		}
	}
}
