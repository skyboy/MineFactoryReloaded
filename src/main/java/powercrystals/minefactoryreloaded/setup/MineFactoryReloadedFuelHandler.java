package powercrystals.minefactoryreloaded.setup;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.minecraftforge.fluids.UniversalBucket;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import powercrystals.minefactoryreloaded.core.MFRUtil;

@Mod.EventBusSubscriber
public class MineFactoryReloadedFuelHandler {

	@SubscribeEvent
	public static void getBurnTime(FurnaceFuelBurnTimeEvent e) {

		ItemStack fuel = e.getItemStack();
		if (fuel.isEmpty())
			return;
		Item item = fuel.getItem();
		if (item instanceof UniversalBucket && ItemStack.areItemStacksEqual(MFRUtil.getBucketFor(MFRFluids.biofuel), fuel)) {
			e.setBurnTime(22500);
		}
	}

}
