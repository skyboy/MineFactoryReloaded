package powercrystals.minefactoryreloaded.modcompat.forestry;

import forestry.core.items.ItemWrench;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import powercrystals.minefactoryreloaded.api.handler.IFactoryTool;
import powercrystals.minefactoryreloaded.api.integration.IMFRIntegrator;
import powercrystals.minefactoryreloaded.farmables.fertilizables.FertilizerStandard;
import powercrystals.minefactoryreloaded.farmables.harvestables.HarvestableWood;

import static net.minecraft.init.Blocks.AIR;
import static net.minecraftforge.fml.common.FMLLog.log;
import static powercrystals.minefactoryreloaded.api.integration.IMFRIntegrator.findBlock;
import static powercrystals.minefactoryreloaded.api.integration.IMFRIntegrator.findItem;
import static powercrystals.minefactoryreloaded.modcompat.Compats.ModIds.FORESTRY;

@IMFRIntegrator.DependsOn(FORESTRY)
public class Forestry implements IMFRIntegrator {

	public void load() {

		// *best* tool
		REGISTRY.addToolHandler(new IFactoryTool() {

			@Override
			public boolean isFactoryToolUsable(EntityPlayer player, EnumHand hand, ItemStack stack, BlockPos pos, EnumFacing side) {

				return stack.getItem() instanceof ItemWrench;
			}

			@Override
			public boolean onFactoryToolUsed(EntityPlayer player, EnumHand hand, ItemStack stack, BlockPos pos, EnumFacing side) {

				return stack.getItem() instanceof ItemWrench;
			}
		});

		Item item = findItem(FORESTRY, "fertilizerCompound");
		if (item != null)
			REGISTRY.registerFertilizer(new FertilizerStandard(item, 0));
		else
			log.error("Forestry fertilizer null!");

		item = findItem(FORESTRY, "fertilizerBio");
		if (item != null)
			REGISTRY.registerFertilizer(new FertilizerStandard(item, 0));
		else
			log.error("Forestry compost null!");

		item = findItem(FORESTRY, "peat");
		if (item != null)
			REGISTRY.registerSludgeDrop(10, new ItemStack(item));
		else
			log.error("Forestry peat null!");

		item = findItem(FORESTRY, "ash");
		if (item != null)
			REGISTRY.registerSludgeDrop(1, new ItemStack(item));
		else
			log.error("Forestry ash null!");

		item = findItem(FORESTRY, "decayingWheat");
		if (item != null)
			REGISTRY.registerSludgeDrop(20, new ItemStack(item));
		else
			log.error("Forestry wheat null!");

		item = findItem(FORESTRY, "sapling");
		Block block = findBlock(FORESTRY, "saplingGE");
		if (item != null && block != AIR) {
			ForestrySapling sapling = new ForestrySapling(item, block);
			REGISTRY.registerPlantable(sapling);
			REGISTRY.registerFertilizable(sapling);
		} else
			log.error("Forestry sapling/block null!");

		block = findBlock(FORESTRY, "soil");
		if (block != AIR) {
			ForestryBogEarth bog = new ForestryBogEarth(block);
			REGISTRY.registerPlantable(bog);
			REGISTRY.registerFertilizable(bog);
			REGISTRY.registerHarvestable(bog);
			REGISTRY.registerFruit(bog);
		} else
			log.error("Forestry bog earth null!");

		for (int i = 1; true; ++i) {
			block = findBlock(FORESTRY, "log" + i);
			l: if (block == AIR) {
				if (i > 1)
					log.debug("Forestry logs null at " + i + ".");
				else {
					block = findBlock(FORESTRY, "logs");
					if (block != AIR) {
						break l;
					}
					log.error("Forestry logs null!");
				}
				break;
			}
			REGISTRY.registerHarvestable(new HarvestableWood(block));
			REGISTRY.registerFruitLogBlock(block);
		}

		for (int i = 1; true; ++i) {
			block = findBlock(FORESTRY, "fireproofLog" + i);
			l: if (block == AIR) {
				if (i > 1)
					log.debug("Forestry logs null at " + i + ".");
				else {
					block = findBlock(FORESTRY, "logsFireproof");
					if (block != AIR) {
						break l;
					}
					log.error("Forestry logs null!");
				}
				break;
			}
			REGISTRY.registerHarvestable(new HarvestableWood(block));
			REGISTRY.registerFruitLogBlock(block);
		}

		block = findBlock(FORESTRY, "leaves");
		if (block != AIR) {
			ForestryLeaf leaf = new ForestryLeaf(block);
			REGISTRY.registerFertilizable(leaf);
			REGISTRY.registerHarvestable(leaf);
			REGISTRY.registerFruit(leaf);
		} else
			log.error("Forestry leaves null!");

		block = findBlock(FORESTRY, "pods");
		item = findItem(FORESTRY, "grafterProven");
		if (block != AIR) {
			ForestryPod pod = new ForestryPod(block, item);
			REGISTRY.registerFertilizable(pod);
			REGISTRY.registerHarvestable(pod);
			REGISTRY.registerFruit(pod);
		} else
			log.error("Forestry pods null!");
	}

	public void postLoad() {

		REGISTRY.registerLiquidDrinkHandler("bioethanol", (player, fluid) -> {

			player.addPotionEffect(new PotionEffect(MobEffects.SPEED, 40 * 20, 0));
			player.addPotionEffect(new PotionEffect(MobEffects.HASTE, 40 * 20, 0));
		});
	}

}
