package powercrystals.minefactoryreloaded.modcompat.projectred;

import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.util.ResourceLocation;
import powercrystals.minefactoryreloaded.api.integration.IMFRIntegrator;
import powercrystals.minefactoryreloaded.farmables.fertilizables.FertilizableStandard;
import powercrystals.minefactoryreloaded.farmables.harvestables.HarvestableTreeLeaves;
import powercrystals.minefactoryreloaded.farmables.plantables.PlantableSapling;

import static powercrystals.minefactoryreloaded.modcompat.Compats.ModIds.PROJECT_RED_EXPLORATION;

@IMFRIntegrator.DependsOn(PROJECT_RED_EXPLORATION)
public class ProjectRed implements IMFRIntegrator {

	public static Block findBlock(String modId, String blockName) {

		return Block.REGISTRY.getObject(new ResourceLocation(modId, blockName));
	}

	public void load() {

		Block stainedLeaf = findBlock(PROJECT_RED_EXPLORATION, "dyeleaf");
		Block stainedSapling = findBlock(PROJECT_RED_EXPLORATION, "dyesapling");

		REGISTRY.registerPlantable(new PlantableSapling(stainedSapling));
		REGISTRY.registerHarvestable(new HarvestableTreeLeaves(stainedLeaf));
		REGISTRY.registerFertilizable(new FertilizableStandard((IGrowable) stainedSapling));
	}

}
