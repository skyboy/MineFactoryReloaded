
package powercrystals.minefactoryreloaded.modcompat.twilightforest;

import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.item.Item;
import powercrystals.minefactoryreloaded.api.integration.IMFRIntegrator;
import powercrystals.minefactoryreloaded.api.plant.HarvestType;
import powercrystals.minefactoryreloaded.farmables.fertilizables.FertilizableStandard;
import powercrystals.minefactoryreloaded.farmables.harvestables.HarvestableShearable;
import powercrystals.minefactoryreloaded.farmables.harvestables.HarvestableStandard;
import powercrystals.minefactoryreloaded.farmables.harvestables.HarvestableTreeLeaves;
import powercrystals.minefactoryreloaded.farmables.harvestables.HarvestableWood;
import powercrystals.minefactoryreloaded.farmables.plantables.PlantableSapling;
import powercrystals.minefactoryreloaded.farmables.plantables.PlantableStandard;

import static powercrystals.minefactoryreloaded.modcompat.Compats.ModIds.TWILIGHT_FOREST;

@IMFRIntegrator.DependsOn(TWILIGHT_FOREST)
public class TwilightForest implements IMFRIntegrator {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void load() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {

		Class tfBighorn = Class.forName("twilightforest.entity.passive.EntityTFBighorn");
		Class tfHydra = Class.forName("twilightforest.entity.boss.EntityTFHydra");
		Class tfHydraHead = Class.forName("twilightforest.entity.boss.EntityTFHydraHead");
		Class tfHydraNeck = Class.forName("twilightforest.entity.boss.EntityTFHydraNeck");
		Class tfHydraPart = Class.forName("twilightforest.entity.boss.EntityTFHydraPart");
		Class tfKingSpider = Class.forName("twilightforest.entity.EntityTFKingSpider");
		Class tfLich = Class.forName("twilightforest.entity.boss.EntityTFLich");
		Class tfNaga = Class.forName("twilightforest.entity.boss.EntityTFNaga");
		Class tfNagaSegment = Class.forName("twilightforest.entity.boss.EntityTFNagaSegment");
		Class tfQuestRam = Class.forName("twilightforest.entity.passive.EntityTFQuestRam");
		Class tfUrGhast = Class.forName("twilightforest.entity.boss.EntityTFUrGhast");
		Class tfYeti = Class.forName("twilightforest.entity.boss.EntityTFYetiAlpha");
		Class tfSnowQueen = Class.forName("twilightforest.entity.boss.EntityTFSnowQueen");
		Class tfPhantomKnight = Class.forName("twilightforest.entity.boss.EntityTFKnightPhantom");

		REGISTRY.registerSafariNetBlacklist(tfHydra);
		REGISTRY.registerSafariNetBlacklist(tfHydraHead);
		REGISTRY.registerSafariNetBlacklist(tfHydraNeck);
		REGISTRY.registerSafariNetBlacklist(tfHydraPart);
		REGISTRY.registerSafariNetBlacklist(tfKingSpider);
		REGISTRY.registerSafariNetBlacklist(tfLich);
		REGISTRY.registerSafariNetBlacklist(tfNaga);
		REGISTRY.registerSafariNetBlacklist(tfNagaSegment);
		REGISTRY.registerSafariNetBlacklist(tfQuestRam);
		REGISTRY.registerSafariNetBlacklist(tfUrGhast);
		REGISTRY.registerSafariNetBlacklist(tfYeti);
		REGISTRY.registerSafariNetBlacklist(tfSnowQueen);
		REGISTRY.registerSafariNetBlacklist(tfPhantomKnight);

		REGISTRY.registerGrinderBlacklist(tfUrGhast);
		REGISTRY.registerGrinderBlacklist(tfNagaSegment);
		REGISTRY.registerGrinderBlacklist(tfNaga);
		REGISTRY.registerGrinderBlacklist(tfLich);
		REGISTRY.registerGrinderBlacklist(tfHydraPart);
		REGISTRY.registerGrinderBlacklist(tfHydraNeck);
		REGISTRY.registerGrinderBlacklist(tfHydraHead);
		REGISTRY.registerGrinderBlacklist(tfHydra);
		REGISTRY.registerGrinderBlacklist(tfYeti);
		REGISTRY.registerGrinderBlacklist(tfSnowQueen);
		REGISTRY.registerGrinderBlacklist(tfPhantomKnight);

		//MFRRegistry.registerMobEggHandler(new TwilightForestEggHandler());

		REGISTRY.registerRanchable(new RanchableTFBighorn(tfBighorn));

		Class tfBlocks = Class.forName("twilightforest.block.TFBlocks");
		Class tfItems = Class.forName("twilightforest.item.TFItems");

		REGISTRY.registerHarvestable(new HarvestableWood(((Block) tfBlocks.getField("log").get(null))));
		REGISTRY.registerHarvestable(new HarvestableWood(((Block) tfBlocks.getField("giantLog").get(null))));
		REGISTRY.registerHarvestable(new HarvestableWood(((Block) tfBlocks.getField("magicLog").get(null))));
		REGISTRY.registerHarvestable(new HarvestableWood(((Block) tfBlocks.getField("magicLogSpecial").get(null))));
		REGISTRY.registerHarvestable(new HarvestableWood(((Block) tfBlocks.getField("hugeStalk").get(null))));
		REGISTRY.registerHarvestable(new HarvestableStandard(((Block) tfBlocks.getField("root").get(null)),
				HarvestType.Tree));
		REGISTRY.registerHarvestable(new HarvestableStandard(((Block) tfBlocks.getField("hugeGloomBlock").get(null))));
		REGISTRY.registerHarvestable(new HarvestableTreeLeaves(((Block) tfBlocks.getField("leaves").get(null))));
		REGISTRY.registerHarvestable(new HarvestableTreeLeaves(((Block) tfBlocks.getField("leaves3").get(null))));
		REGISTRY.registerHarvestable(new HarvestableTreeLeaves(((Block) tfBlocks.getField("darkleaves").get(null))));
		REGISTRY.registerHarvestable(new HarvestableTreeLeaves(((Block) tfBlocks.getField("giantLeaves").get(null))));
		REGISTRY.registerHarvestable(new HarvestableTreeLeaves(((Block) tfBlocks.getField("magicLeaves").get(null))));
		REGISTRY.registerHarvestable(new HarvestableTreeLeaves(((Block) tfBlocks.getField("thornRose").get(null))));
		REGISTRY.registerHarvestable(new HarvestableTreeLeaves(((Block) tfBlocks.getField("hedge").get(null))));
		REGISTRY.registerHarvestable(new HarvestableTreeLeaves(((Block) tfBlocks.getField("firefly").get(null))));
		REGISTRY.registerHarvestable(new HarvestableTreeLeaves(((Block) tfBlocks.getField("cicada").get(null))));
		REGISTRY.registerHarvestable(new HarvestableShearable(((Block) tfBlocks.getField("plant").get(null))));
		REGISTRY.registerHarvestable(new HarvestableShearable(((Block) tfBlocks.getField("trollBer").get(null)),
				HarvestType.Column));

		REGISTRY.registerPlantable(new PlantableSapling(((Block) tfBlocks.getField("sapling").get(null))));
		REGISTRY.registerPlantable(new PlantableStandard(((Item) tfItems.getField("torchberries").get(null)),
				((Block) tfBlocks.getField("unripeTrollBer").get(null))));

		REGISTRY.registerFertilizable(new FertilizableStandard(((IGrowable) tfBlocks.getField("sapling").get(null))));

		REGISTRY.registerRandomMobProvider(new TwilightForestMobProvider());
	}

}
