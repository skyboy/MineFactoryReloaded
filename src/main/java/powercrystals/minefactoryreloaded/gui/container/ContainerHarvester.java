package powercrystals.minefactoryreloaded.gui.container;

import net.minecraft.entity.player.InventoryPlayer;

import net.minecraft.inventory.IContainerListener;
import powercrystals.minefactoryreloaded.api.util.IFactorySettings.SettingNames;
import powercrystals.minefactoryreloaded.core.settings.BooleanSetting;
import powercrystals.minefactoryreloaded.gui.slot.SlotAcceptUpgrade;
import powercrystals.minefactoryreloaded.tile.machine.plants.TileEntityHarvester;

public class ContainerHarvester extends ContainerUpgradeable {

	public ContainerHarvester(TileEntityHarvester te, InventoryPlayer inv) {

		super(te, inv);
	}

	@Override
	protected void addSlots() {

		addSlotToContainer(new SlotAcceptUpgrade(_te, 0, 152, 79));
	}

	@Override
	public void detectAndSendChanges() {

		super.detectAndSendChanges();

		for (IContainerListener listener : listeners) {
			listener.sendWindowProperty(this, 100, getSetting(SettingNames.SHEARS_MODE));
			listener.sendWindowProperty(this, 101, getSetting(SettingNames.HARVEST_SMALL_MUSHROOMS));
		}
	}

	@Override
	public void updateProgressBar(int var, int value) {

		super.updateProgressBar(var, value);

		if (var == 100) setSetting(SettingNames.SHEARS_MODE, value);
		if (var == 101) setSetting(SettingNames.HARVEST_SMALL_MUSHROOMS, value);
	}

	private int getSetting(String setting) {

		TileEntityHarvester h = (TileEntityHarvester) _te;
		if (h.getSettings().get(setting) == null) {
			return 0;
		}
		return h.getImmutableSettings().getBoolean(setting) ? 1 : 0;
	}

	private void setSetting(String setting, int value) {

		((TileEntityHarvester) _te).getSettings().put(setting, value == 0 ? BooleanSetting.TRUE : BooleanSetting.FALSE);
	}

}
