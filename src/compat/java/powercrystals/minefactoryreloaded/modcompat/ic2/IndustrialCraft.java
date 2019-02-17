package powercrystals.minefactoryreloaded.modcompat.ic2;

import ic2.api.item.IC2Items;
import ic2.api.recipe.IRecipeInput;
import ic2.api.recipe.ISemiFluidFuelManager.BurnProperty;
import ic2.api.recipe.Recipes;
import ic2.core.item.tool.ItemToolWrench;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.logging.log4j.Level;
import powercrystals.minefactoryreloaded.api.handler.IFactoryTool;
import powercrystals.minefactoryreloaded.api.integration.IMFRIntegrator;
import powercrystals.minefactoryreloaded.farmables.fertilizables.FertilizerStandard;
import powercrystals.minefactoryreloaded.farmables.harvestables.HarvestableTreeLeaves;
import powercrystals.minefactoryreloaded.farmables.plantables.PlantableSapling;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

import static powercrystals.minefactoryreloaded.api.integration.IMFRIntegrator.findBlock;
import static powercrystals.minefactoryreloaded.modcompat.Compats.ModIds.INDUSTRIAL_CRAFT;
import static powercrystals.minefactoryreloaded.modcompat.Compats.ModIds.MFR;

@IMFRIntegrator.DependsOn(INDUSTRIAL_CRAFT)
public class IndustrialCraft implements IMFRIntegrator {

	public void postLoad() {

		ItemArmor boots = net.minecraft.init.Items.LEATHER_BOOTS;
		@Nonnull
		ItemStack booties = new ItemStack(boots, 64, 0);
		boots.setColor(booties, 0x3479F2);
		OreDictionary.registerOre("greggy_greg_do_please_kindly_stuff_a_sock_in_it", booties);
	}

	public void load() {

		final Block rubberSaplingBlock = findBlock(MFR, "rubber_wood_sapling");

		try {
			IFactoryTool IC2wrench = new IFactoryTool() {

				@Override
				public boolean isFactoryToolUsable(EntityPlayer player, EnumHand hand, ItemStack stack, BlockPos pos, EnumFacing side) {

					if (stack.getItem() instanceof ItemToolWrench)
						return ((ItemToolWrench) stack.getItem()).canTakeDamage(stack, 1);
					return false;
				}

				@Override
				public boolean onFactoryToolUsed(EntityPlayer player, EnumHand hand, ItemStack stack, BlockPos pos, EnumFacing side) {

					if (stack.getItem() instanceof ItemToolWrench) {
						((ItemToolWrench) stack.getItem()).damage(stack, 1, player);
						return true;
					}
					return false;
				}
			};

			ItemStack wrench = IC2Items.getItem("wrench");

			// fail if the class name changes, item name changes, or `canTakeDamage` changes
			IC2wrench.isFactoryToolUsable(null, null, wrench, null, null);
			// fail if `damage` changes: calls into ItemStack#damageItem which will exit without doing anything on null player
			IC2wrench.onFactoryToolUsed(null, null, wrench, null, null);

			// hey! non-API still works, register it
			REGISTRY.addToolHandler(IC2wrench);
		} catch (Throwable p) {
			// eh.
			FMLLog.log.debug("IC2 wrench internals changed:");
			FMLLog.log.catching(Level.DEBUG, p);
		}

		@Nonnull
		ItemStack crop = IC2Items.getItem("crop");
		@Nonnull
		ItemStack rubber = IC2Items.getItem("rubber").copy();
		@Nonnull
		ItemStack rubberSapling = IC2Items.getItem("rubberSapling");
		@Nonnull
		ItemStack rubberLeaves = IC2Items.getItem("rubberLeaves");
		@Nonnull
		ItemStack rubberWood = IC2Items.getItem("rubberWood");
		@Nonnull
		ItemStack stickyResin = IC2Items.getItem("resin");

		if (rubberSapling != null) {
			REGISTRY.registerPlantable(new PlantableSapling(rubberSapling.getItem(), Block.getBlockFromItem(rubberSapling.getItem())));
			REGISTRY.registerFertilizable(new FertilizableIC2RubberTree(Block.getBlockFromItem(rubberSapling.getItem())));
		}
		if (rubberLeaves != null) {
			REGISTRY.registerHarvestable(new HarvestableTreeLeaves(Block.getBlockFromItem(rubberLeaves.getItem())));
		}
		if (rubberWood != null) {
			REGISTRY.registerHarvestable(new HarvestableIC2RubberWood(Block.getBlockFromItem(rubberWood.getItem()), stickyResin.getItem()));
			REGISTRY.registerFruitLogBlock(Block.getBlockFromItem(rubberWood.getItem()));
			FruitIC2Resin resin = new FruitIC2Resin(rubberWood, stickyResin);
			REGISTRY.registerFruit(resin);
			REGISTRY.registerFertilizable(resin);
		}

		@Nonnull
		ItemStack fertilizer = IC2Items.getItem("fertilizer");
		if (fertilizer != null) {
			REGISTRY.registerFertilizer(new FertilizerStandard(fertilizer.getItem(), fertilizer.getItemDamage()));
		}

		if (crop != null) {
			IC2Crop ic2crop = new IC2Crop(Block.getBlockFromItem(crop.getItem()));
			REGISTRY.registerHarvestable(ic2crop);
			REGISTRY.registerFertilizable(ic2crop);
			REGISTRY.registerFruit(ic2crop);
		}

		copyEthanol();

		@Nonnull
		ItemStack item = new ItemStack(rubberSaplingBlock);
		rubber.setCount(1);
		Recipes.extractor.addRecipe(
				new IRecipeInput() {

					@Override
					public boolean matches(@Nonnull ItemStack itemStack) {

						return itemStack.getItem() == Item.getItemFromBlock(rubberSaplingBlock);
					}

					@Override
					public int getAmount() {

						return 1;
					}

					@Override
					public List<ItemStack> getInputs() {

						return Collections.singletonList(item);
					}
				}, null, false, rubber);
	}

	private static void copyEthanol() {

		BurnProperty q = Recipes.semiFluidGenerator.getBurnProperty(FluidRegistry.getFluid("bioethanol"));
		if (q != null)
			Recipes.semiFluidGenerator.addFluid("biofuel", q.amount, q.power);
		else if (FluidRegistry.getFluid("bioethanol") == null)
			Recipes.semiFluidGenerator.addFluid("biofuel", 10, 16);
	}

}
