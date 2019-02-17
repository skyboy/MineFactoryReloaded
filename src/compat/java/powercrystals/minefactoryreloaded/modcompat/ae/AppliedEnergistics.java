package powercrystals.minefactoryreloaded.modcompat.ae;

import net.minecraft.item.Item;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import powercrystals.minefactoryreloaded.api.integration.IMFRIntegrator;

import static powercrystals.minefactoryreloaded.api.integration.IMFRIntegrator.findItem;
import static powercrystals.minefactoryreloaded.api.integration.IMFRRecipeSet.stack;
import static powercrystals.minefactoryreloaded.modcompat.Compats.ModIds.APP_ENG;
import static powercrystals.minefactoryreloaded.modcompat.Compats.ModIds.MFR;

@IMFRIntegrator.DependsOn(APP_ENG)
public class AppliedEnergistics implements IMFRIntegrator {

	@Override
	public void load() {
		final Item rednetCableBlock = findItem(MFR, "rednet_cable");
		final Item plasticPipeBlock = findItem(MFR, "plastic_pipe");
		final Item conveyorBlock = findItem(MFR, "conveyor");
		final Item machine_1 = findItem(MFR, "machine_1");

		FMLInterModComms.sendMessage(APP_ENG, "whitelist-spatial",
				"powercrystals.minefactoryreloaded.tile.base.TileEntityBase");

		FMLInterModComms.sendMessage(APP_ENG, "add-p2p-attunement-redstone",
				stack(rednetCableBlock, 1, 0));
		FMLInterModComms.sendMessage(APP_ENG, "add-p2p-attunement-redstone",
				stack(rednetCableBlock, 1, 1));
		FMLInterModComms.sendMessage(APP_ENG, "add-p2p-attunement-rf-power",
				stack(rednetCableBlock, 1, 2));
		FMLInterModComms.sendMessage(APP_ENG, "add-p2p-attunement-rf-power",
				stack(rednetCableBlock, 1, 3));
		FMLInterModComms.sendMessage(APP_ENG, "add-p2p-attunement-fluid",
				stack(plasticPipeBlock, 1, 0));
		FMLInterModComms.sendMessage(APP_ENG, "add-p2p-attunement-fluid",
				stack(machine_1, 1, 2));
		FMLInterModComms.sendMessage(APP_ENG, "add-p2p-attunement-item",
				stack(machine_1, 1, 1));
		for (int i = 17; i-- > 0; )
			FMLInterModComms.sendMessage(APP_ENG, "add-p2p-attunement-item",
					stack(conveyorBlock, 1, i));
	}

}
