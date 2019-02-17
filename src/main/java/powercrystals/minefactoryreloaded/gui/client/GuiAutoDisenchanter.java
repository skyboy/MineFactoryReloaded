package powercrystals.minefactoryreloaded.gui.client;

import net.minecraft.client.gui.GuiButton;

import powercrystals.minefactoryreloaded.gui.container.ContainerFactoryPowered;
import powercrystals.minefactoryreloaded.net.MFRPacket;
import powercrystals.minefactoryreloaded.tile.machine.enchantment.TileEntityAutoDisenchanter;

public class GuiAutoDisenchanter extends GuiFactoryPowered
{
	private TileEntityAutoDisenchanter _disenchanter;

	private GuiButton _repeatToggle;

	public GuiAutoDisenchanter(ContainerFactoryPowered container, TileEntityAutoDisenchanter te)
	{
		super(container, te);
		_disenchanter = te;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initGui()
	{
		super.initGui();
		_repeatToggle = new GuiButton(1, (this.width - this.xSize) / 2 + 49, (this.height - this.ySize) / 2 + 35, 68, 20, "Repeat: No");
		buttonList.add(_repeatToggle);
	}

	@Override
	public void updateScreen()
	{
		super.updateScreen();
		_repeatToggle.displayString = _disenchanter.getRepeatDisenchant() ? "Repeat: Yes" : "Repeat: No";
	}

	@Override
	protected void actionPerformed(GuiButton button)
	{
		if(button.id == 1)
		{
			_disenchanter.setRepeatDisenchant(!_disenchanter.getRepeatDisenchant());
			MFRPacket.sendEnchanterButtonToServer(_tileEntity, (byte)(_disenchanter.getRepeatDisenchant() ? 1 : 0));
		}
	}
}
