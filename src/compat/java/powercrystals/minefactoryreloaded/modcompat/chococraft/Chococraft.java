package powercrystals.minefactoryreloaded.modcompat.chococraft;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import powercrystals.minefactoryreloaded.api.integration.IMFRIntegrator;
import powercrystals.minefactoryreloaded.farmables.plantables.PlantableCropPlant;

import static powercrystals.minefactoryreloaded.modcompat.Compats.ModIds.CHOCOCRAFT;

@IMFRIntegrator.DependsOn(CHOCOCRAFT)
public class Chococraft implements IMFRIntegrator {

	public void load() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {

		Class<?> blocks = Class.forName("chococraft.common.config.ChocoCraftBlocks");

		Block blockId = ((Block) (blocks.getField("gysahlStemBlock").get(null)));

		Class<?> items = Class.forName("chococraft.common.config.ChocoCraftItems");
		Item seedId = ((Item) (items.getField("gysahlSeedsItem").get(null)));

		REGISTRY.registerPlantable(new PlantableCropPlant(seedId, blockId));
		REGISTRY.registerHarvestable(new HarvestableChococraft(blockId));
		REGISTRY.registerFertilizable(new FertilizableChococraft(blockId));
	}

}

