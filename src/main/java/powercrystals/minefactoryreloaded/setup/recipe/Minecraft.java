package powercrystals.minefactoryreloaded.setup.recipe;

import cofh.core.util.helpers.RecipeHelper;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import powercrystals.minefactoryreloaded.api.integration.IMFRRecipeSet;
import powercrystals.minefactoryreloaded.block.ItemBlockRedNetLogic;
import powercrystals.minefactoryreloaded.core.MFRUtil;
import powercrystals.minefactoryreloaded.core.UtilRecipe;
import powercrystals.minefactoryreloaded.item.ItemUpgrade;
import powercrystals.minefactoryreloaded.setup.MFRFluids;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.setup.RecipeManager;
import powercrystals.minefactoryreloaded.setup.recipe.handler.ShapelessMachineTinker;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.Arrays;
import java.util.List;

import static cofh.core.util.helpers.ItemHelper.cloneStack;
import static net.minecraft.init.Blocks.*;
import static net.minecraft.init.Items.*;
import static net.minecraftforge.oredict.OreDictionary.getOres;
import static powercrystals.minefactoryreloaded.api.integration.IMFRRecipeSet.stack;
import static powercrystals.minefactoryreloaded.api.integration.IMFRRecipeSet.stack_wildcard;
import static powercrystals.minefactoryreloaded.setup.MFRThings.*;

public class Minecraft implements IMFRRecipeSet {

	public static final String[] DYES = { "White", "Orange", "Magenta",
			"LightBlue", "Yellow", "Lime", "Pink", "Gray", "LightGray",
			"Cyan", "Purple", "Blue", "Brown", "Green", "Red", "Black" };

	//region initialization
	//region config
	private static Property enableCheapCL;
	private static Property enableCheapDSU;

	private static Property craftSingleDSU;

	private static Property enableExpensiveUpgrades;
	private static Property enableExpensiveSafariNet;

	private static Property enablePortaSpawner;

	private static Property enableSyringes;

	private static Property enableGuns;

	private static Property enableNetLauncher;

	private static Property enableFancySafariNet;

	private static Property enableMossyBlocksRecipe;
	private static Property enableSmoothSlabRecipe;
	private static Property enablePodzolRecipe;

	public static void readCommonConfig(File config) {

		Configuration c = new Configuration(config);
		c.load();
		//{ Alterations to recipes
		String category = "Recipe";
		enableCheapCL = c.get(category, "CheaperChunkLoader", false).setRequiresMcRestart(true);
		enableCheapCL.setComment("If true, the ChunkLoader can be built out of cheaper materials. Does nothing if the recipe is disabled.");
		enableCheapDSU = c.get(category, "CheaperDSU", false).setRequiresMcRestart(true);
		enableCheapDSU.setComment("If true, the DSU can be built out of chests instead of ender pearls. Does nothing if the recipe is disabled.");

		craftSingleDSU = c.get(category, "SingleDSU", true).setRequiresMcRestart(true);
		craftSingleDSU.setComment("DSU recipes will always craft one DSU. Does nothing for recipes that already only craft one DSU (cheap mode, etc).");

		enableExpensiveUpgrades = c.get(category, "ExpensiveRangeUpgrades", false).setRequiresMcRestart(true);
		enableExpensiveUpgrades.setComment("If true, upgrades will require the previous level upgrade and a diamond. NOTE: this option requires all upgrades have recipes");

		enableExpensiveSafariNet = c.get(category, "ExpensiveSafariNet", false).setRequiresMcRestart(true);
		enableExpensiveSafariNet
				.setComment("If true, the reusable safarinet will require a portaspawner to craft. The portaspawner must be enabled for the safarinet to be craftable.");

		enablePortaSpawner = c.get(category, "PortaSpawner", true).setRequiresMcRestart(true);
		enablePortaSpawner.setComment("If true, the PortaSpawner will be craftable.");

		enableSyringes = c.get(category, "Syringes", true).setRequiresMcRestart(true);
		enableSyringes.setComment("If true, the Syringes will be craftable.");

		enableGuns = c.get(category, "Guns", true).setRequiresMcRestart(true);
		enableGuns.setComment("If true, the Guns will be craftable.");

		enableNetLauncher = c.get(category, "NetLauncher", true).setRequiresMcRestart(true);
		enableNetLauncher.setComment("If true, the SafariNet launcher will be craftable.");

		enableFancySafariNet = c.get(category, "GoldenJailerSafariNet", true).setRequiresMcRestart(true);
		enableFancySafariNet.setComment("If true, the golden jailer safarinet will be craftable. It causes released mobs to always render their name tag, like a player would.");

		enableMossyBlocksRecipe = c.get(category, "MossyBlocks", true).setRequiresMcRestart(true);
		enableMossyBlocksRecipe.setComment("If true, mossy cobble and stone bricks can be craftable.");
		//enableSmoothSlabRecipe = c.get(category, "SmoothSlab", true).setRequiresMcRestart(true);
		//enableSmoothSlabRecipe.setComment("If true, smooth double stone slabs can be craftable.");
		enablePodzolRecipe = c.get(category, "Podzol", true).setRequiresMcRestart(true);
		enablePodzolRecipe.setComment("If true, podzol can be craftable.");
		//}
		c.save();
	}
	//endregion

	public static void registerRecipeHolders() {

		//region machines
		List<Machine> stackMachines = Arrays.asList(Machine.ItemCollector, Machine.Sewer, Machine.Ejector, Machine.ItemRouter, Machine.LiquidRouter);
		int[] stackSizes = { 8, 4, 8, 8, 8 };
		for (Machine machine : Machine.values())
			if (machine != Machine.DeepStorageUnit && machine != Machine.ChunkLoader && !stackMachines.contains(machine))
				RecipeManager.addRecipe(machine.getName(), machine.getItemStack(), machine::getIsRecipeEnabled);
		{
			int i = 0;
			for (Machine machine : stackMachines) {
				RecipeManager.addRecipe(machine.getName(), cloneStack(machine.getItemStack(), stackSizes[i++]), machine::getIsRecipeEnabled);
			}
		}
		{
			final Machine machine = Machine.DeepStorageUnit;
			ItemStack stack = machine.getItemStack();
			RecipeManager.addRecipe("cheap_" + machine.getName(), stack, () -> machine.getIsRecipeEnabled() && enableCheapDSU.getBoolean());
			stack.setCount(craftSingleDSU.getBoolean() ? 1 : 4);
			RecipeManager.addRecipe(machine.getName(), stack, () -> machine.getIsRecipeEnabled() && !enableCheapDSU.getBoolean());
		}
		{
			final Machine machine = Machine.ChunkLoader;
			ItemStack stack = machine.getItemStack();
			RecipeManager.addRecipe(machine.getName(), stack, () -> machine.getIsRecipeEnabled() && !enableCheapCL.getBoolean());
			RecipeManager.addRecipe("cheap_" + machine.getName(), stack, () -> machine.getIsRecipeEnabled() && enableCheapCL.getBoolean());
		}
		RecipeManager.addRecipe("plastic_tank", stack(plasticTank, 1));
		//endregion

		//region radius upgrades
		for (int meta : upgradeItem.getMetadataValues()) {
			String radius = meta >= ItemUpgrade.NEGATIVE_START ? "negative_" + (meta - ItemUpgrade.NEGATIVE_START + 1) : "positive_" + (meta + 1);
			RecipeManager.addRecipe("upgrade_radius_" + radius, stack(upgradeItem, 1, meta), () -> !enableExpensiveUpgrades.getBoolean());
			RecipeManager.addRecipe("expensive_upgrade_radius_" + radius, stack(upgradeItem, 1, meta), enableExpensiveUpgrades::getBoolean);
		}//endregion

		//region conveyor and laser focus
		RecipeManager.addRecipe("conveyor_default", stack(conveyorBlock, 16, 16));

		for (int i = 0; i < 16; i++) {
			RecipeManager.addRecipe("conveyor_dyed_" + DYES[i], stack(conveyorBlock, 1, i)).setRecipeGroup("dyed_conveyor");
			RecipeManager.addRecipe("laser_focus_" + DYES[i], stack(laserFocusItem, 1, i)).setRecipeGroup("laser_focus");
		}
		//endregion

		//region plastics
		{
			RecipeManager.addRecipe("machine_block", stack(machineBaseItem, 3));

			RecipeManager.addRecipe("plastic_sheet", stack(plasticSheetItem, 4));

			RecipeManager.addRecipe("plastic_block", stack(factoryPlasticBlock, 1)).setRecipeGroup("plastic_block");
			RecipeManager.addRecipe("plastic_block_to_sheet", stack(plasticSheetItem, 4)).setRecipeGroup("plastic_sheet_from_block");

			RecipeManager.addRecipe("hammer", stack(factoryHammerItem, 1));

			RecipeManager.addRecipe("straw", stack(strawItem));

			RecipeManager.addRecipe("ruler", stack(rulerItem));

			RecipeManager.addRecipe("plastic_cup", stack(plasticCupItem, 16));

			RecipeManager.addRecipe("plastic_cell", stack(plasticCellItem, 12), () -> false);

			RecipeManager.addRecipe("plastic_bag", stack(plasticBagItem, 3));

			RecipeManager.addRecipe("plastic_bag_erasure", stack(plasticBagItem)).setRecipeGroup("erasure");

			RecipeManager.addRecipe("plastic_pipe", stack(plasticPipeBlock, 8));
		}//endregion

		//region syringes
		{
			RecipeManager.addRecipe("xp_extractor", stack(xpExtractorItem), enableSyringes::getBoolean);

			RecipeManager.addRecipe("syringe_empty", stack(syringeEmptyItem, 1), enableSyringes::getBoolean);

			RecipeManager.addRecipe("syringe_health", stack(syringeHealthItem), enableSyringes::getBoolean);

			RecipeManager.addRecipe("syringe_growth", stack(syringeGrowthItem), enableSyringes::getBoolean);
			RecipeManager.addRecipe("syringe_zombie", stack(syringeZombieItem, 1), enableSyringes::getBoolean);

			RecipeManager.addRecipe("syringe_slime", stack(syringeSlimeItem, 1), enableSyringes::getBoolean);

			RecipeManager.addRecipe("syringe_cure", stack(syringeCureItem), enableSyringes::getBoolean);
		}//endregion

		//region armor
		{
			RecipeManager.addRecipe("armor_glasses", stack(plasticGlasses, 1)).setRecipeGroup("glasses");

			RecipeManager.addRecipe("armor_plastic_helmet", stack(plasticHelmetItem, 1)).setRecipeGroup("plastic_armor");
			RecipeManager.addRecipe("armor_plastic_chestplate", stack(plasticChestplateItem, 1)).setRecipeGroup("plastic_armor");
			RecipeManager.addRecipe("armor_plastic_leggings", stack(plasticLeggingsItem, 1)).setRecipeGroup("plastic_armor");
			RecipeManager.addRecipe("armor_plastic_boots", stack(plasticBootsItem, 1)).setRecipeGroup("plastic_armor");
		}//endregion

		//region safari nets
		{
			RecipeManager.addRecipe("safarinet", stack(safariNetItem, 1), () -> !enableExpensiveSafariNet.getBoolean());
			RecipeManager.addRecipe("expensive_safarinet", stack(safariNetItem, 1), enableExpensiveSafariNet::getBoolean);

			RecipeManager.addRecipe("safarinet_singleuse", stack(safariNetSingleItem, 3));

			RecipeManager.addRecipe("safarinet_jailer", stack(safariNetJailerItem, 1));

			RecipeManager.addRecipe("safarinet_fancy", stack(safariNetFancyJailerItem, 1), enableFancySafariNet::getBoolean);

			RecipeManager.addRecipe("safarinet_launcher", stack(safariNetLauncherItem, 1), enableNetLauncher::getBoolean);
		}//endregion

		//region rednet
		{
			RecipeManager.addRecipe("rednet_cable", stack(rednetCableBlock, 8));

			RecipeManager.addRecipe("rednet_cable_from_plastic_pipe", stack(rednetCableBlock, 5));

			RecipeManager.addRecipe("rednet_cable_energy_single", stack(rednetCableBlock, 1, 2));

			RecipeManager.addRecipe("rednet_cable_energy_multi", stack(rednetCableBlock, 6, 2));

			RecipeManager.addRecipe("rednet_panel", stack(rednetPanelBlock, 1, 0));

			RecipeManager.addRecipe("rednet_controller_housing", stack(machineBlock, 1, 1));

			RecipeManager.addRecipe("rednet_controller", stack(rednetLogicBlock));

			RecipeManager.addRecipe("logic_card_1", stack(logicCardItem, 1, 0));
			RecipeManager.addRecipe("logic_card_2", stack(logicCardItem, 1, 1));
			RecipeManager.addRecipe("logic_card_3", stack(logicCardItem, 1, 2));

			RecipeManager.addRecipe("rednet_meter", stack(rednetMeterItem, 1, 0));
			RecipeManager.addRecipe("rednet_multimeter", stack(rednetMeterItem, 1, 1));

			RecipeManager.addRecipe("rednet_memorycard", stack(rednetMemoryCardItem, 1, 0));
			RecipeManager.addRecipe("rednet_memorycard_erasure", stack(rednetMemoryCardItem, 1, 0)).setRecipeGroup("erasure");
		}//endregion

		//region misc
		{
			RecipeManager.addRecipe("fertilizer_item", stack(fertilizerItem, 16)).setRecipeGroup("fertilizer_item");

			RecipeManager.addRecipe("spyglass", stack(spyglassItem));

			RecipeManager.addRecipe("porta_spawner", stack(portaSpawnerItem), enablePortaSpawner::getBoolean);

			RecipeManager.addRecipe("detcord", stack(detCordBlock, 12));

			RecipeManager.addRecipe("explosive_fishing_rod", stack(fishingRodItem, 1));
		}//endregion

		//region guns
		{
			RecipeManager.addRecipe("needlegun", stack(needlegunItem), enableGuns::getBoolean);

			RecipeManager.addRecipe("potato_cannon", stack(potatoLauncherItem), enableGuns::getBoolean);

			RecipeManager.addRecipe("rocket_launcher", stack(rocketLauncherItem), enableGuns::getBoolean);

			RecipeManager.addRecipe("needle_magazine", stack(needlegunAmmoEmptyItem, 4));

			RecipeManager.addRecipe("rocket_tracking", stack(rocketItem, 2, 0), enableGuns::getBoolean);

			RecipeManager.addRecipe("rocket_dumb", stack(rocketItem, 2, 1), enableGuns::getBoolean);

			RecipeManager.addRecipe("rocket_tracking_from_dumb", stack(rocketItem, 2, 0), enableGuns::getBoolean);

			RecipeManager.addRecipe("needle_ammo_standard", stack(needlegunAmmoStandardItem), enableGuns::getBoolean);

			RecipeManager.addRecipe("needle_ammo_shrapnel", stack(needlegunAmmoPierceItem), enableGuns::getBoolean);

			RecipeManager.addRecipe("needle_ammo_anvil", stack(needlegunAmmoAnvilItem), enableGuns::getBoolean);

			RecipeManager.addRecipe("needle_ammo_fire", stack(needlegunAmmoFireItem), enableGuns::getBoolean);

			RecipeManager.addRecipe("needle_ammo_lava", stack(needlegunAmmoLavaItem), enableGuns::getBoolean);

			RecipeManager.addRecipe("needle_ammo_sludge", stack(needlegunAmmoSludgeItem), enableGuns::getBoolean);

			RecipeManager.addRecipe("needle_ammo_sewage", stack(needlegunAmmoSewageItem), enableGuns::getBoolean);
		}//endregion

		//region rails
		{
			RecipeManager.addRecipe("rail_cargo_pickup", stack(railPickupCargoBlock, 2));

			RecipeManager.addRecipe("rail_cargo_dropoff", stack(railDropoffCargoBlock, 2));

			RecipeManager.addRecipe("rail_passenger_pickup", stack(railPickupPassengerBlock, 3));

			RecipeManager.addRecipe("rail_passenger_dropoff", stack(railDropoffPassengerBlock, 3));
		}//endregion

		//region smelting
		{
			RecipeManager.addRecipe("rubber_bar", stack(rubberBarItem));
			RecipeManager.addRecipe("raw_plastic", stack(rawPlasticItem));

			RecipeManager.addRecipe("plastic_block_to_raw_plastic", stack(rawPlasticItem, 4)).setRecipeGroup("recycle_plastics");
			RecipeManager.addRecipe("plastic_sheet_to_raw_plastic", stack(rawPlasticItem)).setRecipeGroup("recycle_plastics");

			RecipeManager.addRecipe("plastic_bag_to_raw_plastic", stack(rawPlasticItem, 2)).setRecipeGroup("recycle_plastics");
			RecipeManager.addRecipe("straw_to_raw_plastic", stack(rawPlasticItem, 4)).setRecipeGroup("recycle_plastics");
			RecipeManager.addRecipe("ruler_to_raw_plastic", stack(rawPlasticItem, 2)).setRecipeGroup("recycle_plastics");

			RecipeManager.addRecipe("meat_nugget_raw_to_cooked", stack(meatNuggetCookedItem));

			RecipeManager.addRecipe("sugar_charcoal", stack(sugarCharcoalItem));
			RecipeManager.addRecipe("meat_block_cooked_to_charcoal", stack(COAL, 3, 1));
			RecipeManager.addRecipe("rubberwood_to_charcoal", stack(COAL, 1, 1));

			RecipeManager.addRecipe("pinkslime_gem", stack(pinkSlimeItem, 1, 1));
		}//endregion

		//region decorative
		{
			RecipeManager.addRecipe("road_block", stack(factoryRoadBlock, 16)).setRecipeGroup("road_block");
			RecipeManager.addRecipe("road_light", stack(factoryRoadBlock, 4, 4)).setRecipeGroup("road_light");
			RecipeManager.addRecipe("road_light_inverted", stack(factoryRoadBlock, 1, 1)).setRecipeGroup("road_light");

			//region glass
			{
				for (int i = 0; i < 16; i++) {
					ItemStack ceramicDye = stack(ceramicDyeItem, 1, i);
					ItemStack glassStack = stack(factoryGlassBlock, 1, i);
					ItemStack paneStack = stack(factoryGlassPaneBlock, 1, i);
					String dye = DYES[i];
					RecipeManager.addRecipe("ceramic_dye_4_" + dye, cloneStack(ceramicDye, 4)).setRecipeGroup("ceramic_dye");
					RecipeManager.addRecipe("ceramic_dye_8_" + dye, cloneStack(ceramicDye, 8)).setRecipeGroup("ceramic_dye");
					RecipeManager.addRecipe("glass_1_" + dye, cloneStack(glassStack, 1)).setRecipeGroup("stained_glass_block");
					RecipeManager.addRecipe("glass_3_" + dye, cloneStack(glassStack, 3)).setRecipeGroup("stained_glass_block");
					RecipeManager.addRecipe("glass_6_" + dye, cloneStack(glassStack, 6)).setRecipeGroup("stained_glass_block");
					RecipeManager.addRecipe("pane_1_" + dye, cloneStack(paneStack, 1)).setRecipeGroup("stained_glass_pane_redye");
					RecipeManager.addRecipe("pane_3_" + dye, cloneStack(paneStack, 3)).setRecipeGroup("stained_glass_pane_redye");
					RecipeManager.addRecipe("pane_8_" + dye, cloneStack(paneStack, 8)).setRecipeGroup("stained_glass_pane_redye");

					RecipeManager.addRecipe("pane_16_" + dye, cloneStack(paneStack, 16)).setRecipeGroup("stained_glass_pane");
				}
			}//endregion

			RecipeManager.addRecipe("plastic_block_paver", stack(factoryPlasticBlock, 1, 1)).setRecipeGroup("plastic_block_decoration");
			RecipeManager.addRecipe("plastic_block_column", stack(factoryPlasticBlock, 3, 2)).setRecipeGroup("plastic_block_decoration");
			RecipeManager.addRecipe("plastic_block_bricks_large", stack(factoryPlasticBlock, 4, 3)).setRecipeGroup("plastic_block_decoration");
			RecipeManager.addRecipe("plastic_block_chiseled", stack(factoryPlasticBlock, 4, 4)).setRecipeGroup("plastic_block_decoration");
			RecipeManager.addRecipe("plastic_block_road", stack(factoryPlasticBlock, 8, 5)).setRecipeGroup("plastic_block_decoration");
			RecipeManager.addRecipe("plastic_block_bricks_small", stack(factoryPlasticBlock, 4, 6)).setRecipeGroup("plastic_block_decoration");

			//region bricks
			RecipeManager.addRecipe("ice_brick_small", stack(factoryDecorativeBrickBlock, 8, 0)).setRecipeGroup("ice_brick");
			RecipeManager.addRecipe("glowstone_brick_small", stack(factoryDecorativeBrickBlock, 8, 1)).setRecipeGroup("glowstone_brick");
			RecipeManager.addRecipe("lapis_brick_small", stack(factoryDecorativeBrickBlock, 8, 2)).setRecipeGroup("lapis_brick");
			RecipeManager.addRecipe("obsidian_brick_small", stack(factoryDecorativeBrickBlock, 8, 3)).setRecipeGroup("obsidian_brick");
			RecipeManager.addRecipe("pavedstone_brick_small", stack(factoryDecorativeBrickBlock, 8, 4)).setRecipeGroup("pavedstone_brick");
			RecipeManager.addRecipe("snow_brick_small", stack(factoryDecorativeBrickBlock, 8, 5)).setRecipeGroup("snow_brick");

			RecipeManager.addRecipe("ice_brick_large", stack(factoryDecorativeBrickBlock, 8, 6)).setRecipeGroup("ice_brick");
			RecipeManager.addRecipe("glowstone_brick_large", stack(factoryDecorativeBrickBlock, 8, 7)).setRecipeGroup("glowstone_brick");
			RecipeManager.addRecipe("lapis_brick_large", stack(factoryDecorativeBrickBlock, 8, 8)).setRecipeGroup("lapis_brick");
			RecipeManager.addRecipe("obsidian_brick_large", stack(factoryDecorativeBrickBlock, 8, 9)).setRecipeGroup("obsidian_brick");
			RecipeManager.addRecipe("pavedstone_brick_large", stack(factoryDecorativeBrickBlock, 4, 10)).setRecipeGroup("pavedstone_brick");
			RecipeManager.addRecipe("snow_brick_large", stack(factoryDecorativeBrickBlock, 8, 11)).setRecipeGroup("snow_brick");
			// 12 & 13 are special blocks
			RecipeManager.addRecipe("brick_brick_large", stack(factoryDecorativeBrickBlock, 8, 14)).setRecipeGroup("brick_brick");
			// 15 is special

			RecipeManager.addRecipe("ice_brick_small_from_large", stack(factoryDecorativeBrickBlock, 4, 0)).setRecipeGroup("ice_brick");
			RecipeManager.addRecipe("glowstone_brick_from_large", stack(factoryDecorativeBrickBlock, 4, 1)).setRecipeGroup("glowstone_brick");
			RecipeManager.addRecipe("lapis_brick_small_from_large", stack(factoryDecorativeBrickBlock, 4, 2)).setRecipeGroup("lapis_brick");
			RecipeManager.addRecipe("obsidian_brick_small_from_large", stack(factoryDecorativeBrickBlock, 4, 3)).setRecipeGroup("obsidian_brick");
			RecipeManager.addRecipe("pavedstone_brick_small_from_large", stack(factoryDecorativeBrickBlock, 4, 4)).setRecipeGroup("pavedstone_brick");
			RecipeManager.addRecipe("snow_brick_small_from_large", stack(factoryDecorativeBrickBlock, 4, 5)).setRecipeGroup("snow_brick");
			RecipeManager.addRecipe("brick_from_large_brick", stack(BRICK_BLOCK, 2, 0)).setRecipeGroup("brick_brick");
			//endregion

			//region stone
			RecipeManager.addRecipe("blackstone_smooth", stack(factoryDecorativeStoneBlock, 8, 0));
			RecipeManager.addRecipe("whitestone_smooth", stack(factoryDecorativeStoneBlock, 8, 1));

			RecipeManager.addRecipe("blackstone_cobble", stack(factoryDecorativeStoneBlock, 8, 2));
			RecipeManager.addRecipe("whitestone_cobble", stack(factoryDecorativeStoneBlock, 8, 3));

			RecipeManager.addRecipe("blackstone_brick_large", stack(factoryDecorativeStoneBlock, 8, 4));
			RecipeManager.addRecipe("whitestone_brick_large", stack(factoryDecorativeStoneBlock, 8, 5));
			RecipeManager.addRecipe("blackstone_brick_large_from_smooth", stack(factoryDecorativeStoneBlock, 4, 4));
			RecipeManager.addRecipe("whitestone_brick_large_from_smooth", stack(factoryDecorativeStoneBlock, 4, 5));

			RecipeManager.addRecipe("blackstone_brick_small", stack(factoryDecorativeStoneBlock, 8, 6));
			RecipeManager.addRecipe("whitestone_brick_small", stack(factoryDecorativeStoneBlock, 8, 7));
			RecipeManager.addRecipe("blackstone_brick_small_from_large", stack(factoryDecorativeStoneBlock, 4, 6));
			RecipeManager.addRecipe("whitestone_brick_small_from_large", stack(factoryDecorativeStoneBlock, 4, 7));

			RecipeManager.addRecipe("blackstone_gravel", stack(factoryDecorativeStoneBlock, 8, 8));
			RecipeManager.addRecipe("whitestone_gravel", stack(factoryDecorativeStoneBlock, 8, 9));

			RecipeManager.addRecipe("blackstone_paver", stack(factoryDecorativeStoneBlock, 4, 10));
			RecipeManager.addRecipe("whitestone_paver", stack(factoryDecorativeStoneBlock, 4, 11));
			RecipeManager.addRecipe("blackstone_paver_from_smooth", stack(factoryDecorativeStoneBlock, 1, 10));
			RecipeManager.addRecipe("whitestone_paver_from_smooth", stack(factoryDecorativeStoneBlock, 1, 11));
			RecipeManager.addRecipe("blackstone_smooth_from_paver", stack(factoryDecorativeStoneBlock, 1, 0));
			RecipeManager.addRecipe("whitestone_smooth_from_paver", stack(factoryDecorativeStoneBlock, 1, 1));
			//endregion

			//region meat
			RecipeManager.addRecipe("meat_ingot_raw", stack(meatIngotRawItem));
			RecipeManager.addRecipe("meat_ingot_cooked", stack(meatIngotCookedItem));
			RecipeManager.addRecipe("meat_block_raw", stack(factoryDecorativeBrickBlock, 1, 12));
			RecipeManager.addRecipe("meat_block_cooked", stack(factoryDecorativeBrickBlock, 1, 13));

			RecipeManager.addRecipe("meat_ingot_raw_from_block", stack(meatIngotRawItem, 9)).setRecipeGroup("meat_ingot_raw_from_block");
			RecipeManager.addRecipe("meat_ingot_cooked_from_block", stack(meatIngotCookedItem, 9)).setRecipeGroup("meat_ingot_cooked_from_block");
			RecipeManager.addRecipe("meat_nugget_raw_from_ingot", stack(meatNuggetRawItem, 9)).setRecipeGroup("meat_nugget_raw_from_ingot");
			RecipeManager.addRecipe("meat_nugget_cooked_from_ingot", stack(meatNuggetCookedItem, 9)).setRecipeGroup("meat_nugget_cooked_from_ingot");
			//endregion

			RecipeManager.addRecipe("pinkslime_block", stack(pinkSlimeBlock));
			RecipeManager.addRecipe("pinkslime_block_to_ball", stack(pinkSlimeItem, 9)).setRecipeGroup("pinkslime_ball_from_block");
		}//endregion

		//region vanilla improvements
		{
			RecipeManager.addRecipe("rubberwood_planks", stack(PLANKS, 3, 3)).setRecipeGroup("rubber_wood_planks");

			RecipeManager.addRecipe("stick_piston_clean_with_milk", stack(PISTON, 1, 0)).setRecipeGroup("clean_sticky_piston");

			RecipeManager.addRecipe("sticky_piston_from_raw_rubber", stack(STICKY_PISTON)).setRecipeGroup("sticky_piston_from_rubber");

			RecipeManager.addRecipe("record_blank", stack(blankRecordItem, 1));

			RecipeManager.addRecipe("mossy_cobblestone", stack(MOSSY_COBBLESTONE), enableMossyBlocksRecipe::getBoolean);
			RecipeManager.addRecipe("mossy_stone_brick", stack(STONEBRICK, 1, 1), enableMossyBlocksRecipe::getBoolean);

			// double-slabs no longer have an Item

			RecipeManager.addRecipe("vine_scaffold", stack(vineScaffoldBlock, 8));

			RecipeManager.addRecipe("milk_bottle", stack(milkBottleItem));

			RecipeManager.addRecipe("podzol", stack(DIRT, 1, 2), enablePodzolRecipe::getBoolean);

			RecipeManager.addRecipe("fertile_soil", stack(fertileSoil));

			RecipeManager.addRecipe("chocolate_milk_bucket", MFRUtil.getBucketFor(MFRFluids.chocolateMilk)).setRecipeGroup("chocolate_milk");

			RecipeManager.addRecipe("sugar_charcoal_block", stack(factoryDecorativeBrickBlock, 1, 15));

			RecipeManager.addRecipe("torch_3", stack(TORCH, 3)).setRecipeGroup("torch_from_rubber");

			RecipeManager.addRecipe("torch_2", stack(TORCH, 2)).setRecipeGroup("torch_from_sugar_charcoal");

			for (@Nonnull ItemStack torchStone : getOres("torchStone")) {
				if (torchStone.isEmpty())
					continue;
				RecipeManager.addRecipe("torch_stone_3", cloneStack(torchStone, 3)).setRecipeGroup("stone_torch_from_rubber");

				RecipeManager.addRecipe("torch_stone_2", cloneStack(torchStone, 2)).setRecipeGroup("stone_torch_from_sugar_charcoal");
				break;
			}
		}//endregion
	}
	//endregion

	//region SHAPES
	//@formatter:off
	private static final String[] ROTATED_GEAR = {
			"X X",
			" C ",
			"X X"
	}, GEAR = {
			" X ",
			"XCX",
			" X "
	}, SURROUND = {
			"XXX",
			"XCX",
			"XXX"
	}, HOLLOW = {
			"XXX",
			"X X",
			"XXX"
	}, STORAGE = {
			"XXX",
			"XXX",
			"XXX"
	}, FENCE = {
			"XXX",
			"XXX"
	}, LAYER = {
			"XXX"
	}, DOOR = {
			"XX",
			"XX",
			"XX"
	}, STORAGE_SMALL = {
			"XX",
			"XX"
	}, SINGLE = {
			"X"
	};
	//@formatter:on
	//endregion

	@Override
	public final void registerRecipes() {

		registerConveyors();
		registerMachines();
		registerMachineUpgrades();
		registerMachineTinkers();
		registerPlastics();
		registerArmor();
		registerDecorative();
		registerMiscItems();
		registerSmelting();
		registerVanillaImprovements();
		registerSafariNets();
		registerRails();
		registerSyringes();
		registerGuns();
		registerRedNet();
		registerRedNetManual();
	}

	//region machines
	private final IRecipeHolder Planter = IRecipeHolder.EMPTY;
	private final IRecipeHolder Fisher = IRecipeHolder.EMPTY;
	private final IRecipeHolder Harvester = IRecipeHolder.EMPTY;
	private final IRecipeHolder Rancher = IRecipeHolder.EMPTY;
	private final IRecipeHolder Fertilizer = IRecipeHolder.EMPTY;
	private final IRecipeHolder Vet = IRecipeHolder.EMPTY;
	private final IRecipeHolder ItemCollector = IRecipeHolder.EMPTY;
	private final IRecipeHolder BlockBreaker = IRecipeHolder.EMPTY;
	private final IRecipeHolder WeatherCollector = IRecipeHolder.EMPTY;
	private final IRecipeHolder SludgeBoiler = IRecipeHolder.EMPTY;
	private final IRecipeHolder Sewer = IRecipeHolder.EMPTY;
	private final IRecipeHolder Composter = IRecipeHolder.EMPTY;
	private final IRecipeHolder Breeder = IRecipeHolder.EMPTY;
	private final IRecipeHolder Grinder = IRecipeHolder.EMPTY;
	private final IRecipeHolder AutoEnchanter = IRecipeHolder.EMPTY;
	private final IRecipeHolder Chronotyper = IRecipeHolder.EMPTY;
	private final IRecipeHolder Ejector = IRecipeHolder.EMPTY;
	private final IRecipeHolder ItemRouter = IRecipeHolder.EMPTY;
	private final IRecipeHolder LiquidRouter = IRecipeHolder.EMPTY;
	private final IRecipeHolder LiquiCrafter = IRecipeHolder.EMPTY;
	private final IRecipeHolder LavaFabricator = IRecipeHolder.EMPTY;
	private final IRecipeHolder SteamBoiler = IRecipeHolder.EMPTY;
	private final IRecipeHolder AutoJukebox = IRecipeHolder.EMPTY;
	private final IRecipeHolder Unifier = IRecipeHolder.EMPTY;
	private final IRecipeHolder AutoSpawner = IRecipeHolder.EMPTY;
	private final IRecipeHolder BioReactor = IRecipeHolder.EMPTY;
	private final IRecipeHolder BiofuelGenerator = IRecipeHolder.EMPTY;
	private final IRecipeHolder AutoDisenchanter = IRecipeHolder.EMPTY;
	private final IRecipeHolder Slaughterhouse = IRecipeHolder.EMPTY;
	private final IRecipeHolder MeatPacker = IRecipeHolder.EMPTY;
	private final IRecipeHolder EnchantmentRouter = IRecipeHolder.EMPTY;
	private final IRecipeHolder LaserDrill = IRecipeHolder.EMPTY;
	private final IRecipeHolder LaserDrillPrecharger = IRecipeHolder.EMPTY;
	private final IRecipeHolder AutoAnvil = IRecipeHolder.EMPTY;
	private final IRecipeHolder BlockSmasher = IRecipeHolder.EMPTY;
	private final IRecipeHolder RedNote = IRecipeHolder.EMPTY;
	private final IRecipeHolder AutoBrewer = IRecipeHolder.EMPTY;
	private final IRecipeHolder FruitPicker = IRecipeHolder.EMPTY;
	private final IRecipeHolder BlockPlacer = IRecipeHolder.EMPTY;
	private final IRecipeHolder MobCounter = IRecipeHolder.EMPTY;
	private final IRecipeHolder SteamTurbine = IRecipeHolder.EMPTY;
	private final IRecipeHolder Fountain = IRecipeHolder.EMPTY;
	private final IRecipeHolder MobRouter = IRecipeHolder.EMPTY;
	private final IRecipeHolder DeepStorageUnit = IRecipeHolder.EMPTY;
	private final IRecipeHolder cheap_DeepStorageUnit = IRecipeHolder.EMPTY;
	private final IRecipeHolder ChunkLoader = IRecipeHolder.EMPTY;
	private final IRecipeHolder cheap_ChunkLoader = IRecipeHolder.EMPTY;
	private final IRecipeHolder plastic_tank = IRecipeHolder.EMPTY;

	private void registerMachines() {

		Planter.addShaped(
				"GGG",
				"CPC",
				" M ",
				'G', "sheetPlastic",
				'P', PISTON,
				'C', Items.FLOWER_POT,
				'M', machine_block
		);

		Fisher.addShaped(
				"GGG",
				"RRR",
				"BMB",
				'G', "sheetPlastic",
				'R', FISHING_ROD,
				'B', BUCKET,
				'M', machine_block
		);

		Harvester.addShaped(
				"GGG",
				"XSX",
				" M ",
				'G', "sheetPlastic",
				'X', GOLDEN_AXE,
				'S', SHEARS,
				'M', machine_block
		);

		Rancher.addShaped(
				"GGG",
				"SBS",
				"PMP",
				'G', "sheetPlastic",
				'B', BUCKET,
				'S', SHEARS,
				'P', plastic_pipe,
				'M', machine_block
		);

		Fertilizer.addShaped(
				"GGG",
				"LBL",
				" M ",
				'G', "sheetPlastic",
				'L', LEATHER,
				'B', GLASS_BOTTLE,
				'M', machine_block
		);

		Vet.addShaped(
				"GGG",
				"SSS",
				"EME",
				'G', "sheetPlastic",
				'E', SPIDER_EYE,
				'S', syringe_empty,
				'M', machine_block
		);

		ItemCollector.addShaped(
				"GGG",
				" C ",
				" M ",
				'G', "sheetPlastic",
				'C', CHEST,
				'M', machine_block
		);

		BlockBreaker.addShaped(
				"GGG",
				"PHS",
				" M ",
				'G', "sheetPlastic",
				'P', GOLDEN_PICKAXE,
				'H', hammer,
				'S', GOLDEN_SHOVEL,
				'M', machine_block
		);

		WeatherCollector.addShaped(
				"GGG",
				"BBB",
				"UMU",
				'G', "sheetPlastic",
				'B', IRON_BARS,
				'U', BUCKET,
				'M', machine_block
		);

		SludgeBoiler.addShaped(
				"GGG",
				"FFF",
				" M ",
				'G', "sheetPlastic",
				'F', FURNACE,
				'M', machine_block
		);

		Sewer.addShaped(
				"GGG",
				"BUB",
				"BMB",
				'G', "sheetPlastic",
				'B', BRICK,
				'U', BUCKET,
				'M', machine_block
		);

		Composter.addShaped(
				"GGG",
				"PFP",
				" M ",
				'G', "sheetPlastic",
				'P', PISTON,
				'F', FURNACE,
				'M', machine_block
		);

		Breeder.addShaped(
				"GGG",
				"CAC",
				"PMP",
				'G', "sheetPlastic",
				'P', "dyePurple",
				'C', GOLDEN_CARROT,
				'A', GOLDEN_APPLE,
				'M', machine_block
		);

		Grinder.addShaped(
				"GGG",
				"BPS",
				" M ",
				'G', "sheetPlastic",
				'B', BOOK,
				'P', PISTON,
				'S', GOLDEN_SWORD,
				'M', machine_block
		);

		AutoEnchanter.addShaped(
				"GGG",
				"BBB",
				"DMD",
				'G', "sheetPlastic",
				'B', BOOK,
				'D', DIAMOND,
				'M', machine_block
		);

		Chronotyper.addShaped(
				"GGG",
				"EEE",
				"PMP",
				'G', "sheetPlastic",
				'E', EMERALD,
				'P', "dyePurple",
				'M', machine_block
		);

		Ejector.addShaped(
				"GGG",
				" D ",
				"RMR",
				'G', "sheetPlastic",
				'D', DROPPER,
				'R', "dustRedstone",
				'M', machine_block
		);

		ItemRouter.addShaped(
				"GGG",
				"RCR",
				" M ",
				'G', "sheetPlastic",
				'C', CHEST,
				'R', REPEATER,
				'M', machine_block
		);

		LiquidRouter.addShaped(
				"GGG",
				"RBR",
				"PMP",
				'G', "sheetPlastic",
				'R', rednet_multimeter,
				'B', BUCKET,
				'P', plastic_pipe,
				'M', machine_block
		);

		DeepStorageUnit.addShaped(
				"GGG",
				"SPS",
				"EME",
				'G', "sheetPlastic",
				'S', SHULKER_SHELL,
				'P', CHORUS_FRUIT_POPPED,
				'E', ENDER_EYE,
				'M', machine_block
		);
		cheap_DeepStorageUnit.addShaped(
				"GGG",
				"CCC",
				"CMC",
				'G', "sheetPlastic",
				'C', CHEST,
				'M', machine_block
		);

		LiquiCrafter.addShaped(
				"GGG",
				"BWB",
				"FMF",
				'G', "sheetPlastic",
				'B', BUCKET,
				'W', CRAFTING_TABLE,
				'F', ITEM_FRAME,
				'M', machine_block
		);

		LavaFabricator.addShaped(
				"GGG",
				"OBO",
				"CMC",
				'G', "sheetPlastic",
				'O', OBSIDIAN,
				'B', BLAZE_ROD,
				'C', MAGMA,
				'M', machine_block
		);

		SteamBoiler.addShaped(
				"GGG",
				"OTO",
				"NBN",
				'G', "sheetPlastic",
				'T', Items.CAULDRON,
				'O', OBSIDIAN,
				'N', NETHER_BRICK_STAIRS,
				'B', SludgeBoiler
		);

		AutoJukebox.addShaped(
				"GGG",
				" J ",
				" M ",
				'G', "sheetPlastic",
				'J', JUKEBOX,
				'M', machine_block
		);

		Unifier.addShaped(
				"GGG",
				"CBC",
				" M ",
				'G', "sheetPlastic",
				'B', BOOK,
				'C', COMPARATOR,
				'M', machine_block
		);

		AutoSpawner.addShaped(
				"GGG",
				"NCS",
				"EME",
				'G', "sheetPlastic",
				'C', MAGMA_CREAM,
				'N', Items.NETHER_WART,
				'S', SUGAR,
				'E', "gemEmerald",
				'M', machine_block
		);

		BioReactor.addShaped(
				"GGG",
				"UEU",
				"SMS",
				'G', "sheetPlastic",
				'U', SUGAR,
				'E', FERMENTED_SPIDER_EYE,
				'S', "slimeball",
				'M', machine_block
		);

		BiofuelGenerator.addShaped(
				"GGG",
				"PFP",
				"RMR",
				'G', "sheetPlastic",
				'F', FURNACE,
				'P', PISTON,
				'R', BLAZE_ROD,
				'M', machine_block
		);

		AutoDisenchanter.addShaped(
				"GGG",
				"RDR",
				"BMB",
				'G', "sheetPlastic",
				'B', BOOK,
				'D', DIAMOND,
				'R', NETHER_BRICK,
				'M', machine_block
		);

		Slaughterhouse.addShaped(
				"GGG",
				"SSS",
				"XMX",
				'G', "sheetPlastic",
				'S', GOLDEN_SWORD,
				'X', GOLDEN_AXE,
				'M', machine_block
		);

		MeatPacker.addShaped(
				"GGG",
				"BFB",
				"BMB",
				'G', "sheetPlastic",
				'B', BRICK_BLOCK,
				'F', FLINT_AND_STEEL,
				'M', machine_block
		);

		EnchantmentRouter.addShaped(
				"GGG",
				"RBR",
				" M ",
				'G', "sheetPlastic",
				'B', OBSERVER,
				'R', BOOK,
				'M', machine_block
		);

		LaserDrill.addShaped(
				"GGG",
				"LLL",
				"DMD",
				'G', "sheetPlastic",
				'L', GLOWSTONE,
				'D', "gemDiamond",
				'M', machine_block
		);

		LaserDrillPrecharger.addShaped(
				"GGG",
				"LSL",
				"DMD",
				'G', "sheetPlastic",
				'L', GLOWSTONE,
				'D', "gemDiamond",
				'S', pinkslime_gem,
				'M', machine_block
		);

		AutoAnvil.addShaped(
				"GGG",
				"AAA",
				" M ",
				'G', "sheetPlastic",
				'A', ANVIL,
				'M', machine_block
		);

		BlockSmasher.addShaped(
				"GGG",
				"HHH",
				"BMB",
				'G', "sheetPlastic",
				'H', hammer,
				'B', BOOK,
				'M', machine_block
		);

		RedNote.addShaped(
				"GGG",
				"CNC",
				" M ",
				'G', "sheetPlastic",
				'C', "cableRedNet",
				'N', NOTEBLOCK,
				'M', machine_block
		);

		AutoBrewer.addShaped(
				"GGG",
				"CBC",
				"RMR",
				'G', "sheetPlastic",
				'C', plastic_pipe,
				'B', Items.BREWING_STAND,
				'R', OBSERVER,
				'M', machine_block
		);

		FruitPicker.addShaped(
				"GGG",
				"SXS",
				" M ",
				'G', "sheetPlastic",
				'S', SHEARS,
				'X', GOLDEN_AXE,
				'M', machine_block
		);

		BlockPlacer.addShaped(
				"GGG",
				"DDD",
				" M ",
				'G', "sheetPlastic",
				'D', DISPENSER,
				'M', machine_block
		);

		MobCounter.addShaped(
				"GGG",
				"RCR",
				"SMS",
				'G', "sheetPlastic",
				'R', COMPARATOR,
				'C', OBSERVER,
				'S', spyglass,
				'M', machine_block
		);

		SteamTurbine.addShaped(
				"GGG",
				"PFP",
				"RMR",
				'G', "sheetPlastic",
				'F', FURNACE,
				'P', PISTON,
				'R', NETHERBRICK,
				'M', machine_block
		);

		ChunkLoader.addShaped(
				"GGG",
				"PFP",
				"RMR",
				'G', "sheetPlastic",
				'F', NETHER_STAR,
				'P', DeepStorageUnit,
				'R', "blockRedstone",
				'M', machine_block
		);
		cheap_ChunkLoader.addShaped(
				"GGG",
				"PFP",
				"RMR",
				'G', "sheetPlastic",
				'F', "blockGold",
				'P', ENDER_EYE,
				'R', "blockRedstone",
				'M', machine_block
		);

		Fountain.addShaped(
				"GBG",
				"GBG",
				"UMU",
				'G', "sheetPlastic",
				'B', IRON_BARS,
				'U', BUCKET,
				'M', machine_block
		);

		MobRouter.addShaped(
				"GGG",
				"BRB",
				"PCP",
				'G', "sheetPlastic",
				'B', IRON_BARS,
				'R', ItemRouter,
				'P', "dyeOrange",
				'C', Chronotyper
		);

		plastic_tank.addShaped(
				"PPP",
				"P P",
				"PMP",
				'P', "sheetPlastic",
				'M', machine_block
		);
	}

	private void registerMachineTinkers() {

		UtilRecipe.addRecipe(new ShapelessMachineTinker(Machine.ItemCollector, "Emits comparator signal",
				stack(GOLD_NUGGET)) {

			@Override
			protected boolean isMachineTinkerable(@Nonnull ItemStack machine) {

				return !machine.hasTagCompound() || !machine.getTagCompound().hasKey("hasTinkerStuff");
			}

			@Nonnull
			@Override
			protected ItemStack getTinkeredMachine(@Nonnull ItemStack machine) {

				machine = machine.copy();
				machine.setCount(1);
				NBTTagCompound tag = machine.getTagCompound();
				if (tag == null) machine.setTagCompound(tag = new NBTTagCompound());
				tag.setBoolean("hasTinkerStuff", true);
				return machine;
			}
		});
	}
	//endregion

	//region upgrades
	private final IRecipeHolder[] upgrade_radius_positive = IRecipeHolder.EMPTY_ARRAY;
	private final IRecipeHolder upgrade_radius_negative_1 = IRecipeHolder.EMPTY;

	private final IRecipeHolder[] expensive_upgrade_radius_positive = IRecipeHolder.EMPTY_ARRAY;
	private final IRecipeHolder expensive_upgrade_radius_negative_1 = IRecipeHolder.EMPTY;

	private final IRecipeHolder[] laser_focus = IRecipeHolder.EMPTY_ARRAY;

	private void registerMachineUpgrades() {

		//region radius
		{
			final String[] shape = {
					"III",
					"PCP",
					"RGR"
			};
			String[] materials = { "gemLapis", "ingotTin", "ingotIron", "ingotCopper", "ingotBronze",
					"ingotSilver", "ingotGold", "gemQuartz", "gemDiamond", "ingotPlatinum", "gemEmerald"
			};

			//region normal
			for (int i = 0; i < upgrade_radius_positive.length; ++i) {
				upgrade_radius_positive[i].addShaped(
						shape,
						'I', materials[i],
						'P', "dustPlastic",
						'C', "dustPlastic",
						'R', "dustRedstone",
						'G', "nuggetGold"
				);
			}
			upgrade_radius_negative_1.addShaped(
					shape,
					'I', "cobblestone",
					'P', "dustPlastic",
					'C', "dustPlastic",
					'R', "dustRedstone",
					'G', "nuggetGold"
			);
			//endregion

			//region expensive
			expensive_upgrade_radius_positive[0].addShaped(
					shape,
					'I', materials[0],
					'P', "dustPlastic",
					'C', "gemDiamond",
					'R', "dustRedstone",
					'G', expensive_upgrade_radius_negative_1
			);
			for (int i = 1; i < expensive_upgrade_radius_positive.length; ++i) {
				expensive_upgrade_radius_positive[i].addShaped(
						shape,
						'I', materials[i],
						'P', "dustPlastic",
						'C', "gemDiamond",
						'R', "dustRedstone",
						'G', expensive_upgrade_radius_positive[i - 1]
				);
			}
			expensive_upgrade_radius_negative_1.addShaped(
					shape,
					'I', "stone",
					'P', "dustPlastic",
					'C', "dustPlastic", // not a diamond
					'R', "dustRedstone",
					'G', "ingotGold"
			);
			//endregion
		}
		//endregion

		//region laser focii
		for (int i = 0; i < 16; i++) {
			laser_focus[i].addShaped(
					"ENE",
					"NGN",
					"ENE",
					'E', "gemEmerald",
					'N', "nuggetGold",
					'G', pane_1[i]
			);
		}
		//endregion
	}
	//endregion

	//region conveyors
	private final IRecipeHolder conveyor_default = IRecipeHolder.EMPTY;
	private final IRecipeHolder[] conveyor_dyed = IRecipeHolder.EMPTY_ARRAY;

	private void registerConveyors() {

		conveyor_default.addShaped(
				new String[] {
						"UUU",
						"RIR",
				},
				'U', "itemRubber",
				'R', "dustRedstone",
				'I', "ingotIron"
		);

		for (int i = 0; i < 16; i++) {
			conveyor_dyed[i].addShapeless(conveyor_default, "dyeCeramic" + DYES[i]);
		}
	}
	//endregion

	//region decorative
	private final IRecipeHolder road_block = IRecipeHolder.EMPTY;
	private final IRecipeHolder road_light = IRecipeHolder.EMPTY;
	private final IRecipeHolder road_light_inverted = IRecipeHolder.EMPTY;

	private final IRecipeHolder[] ceramic_dye_4 = IRecipeHolder.EMPTY_ARRAY;
	private final IRecipeHolder[] ceramic_dye_8 = IRecipeHolder.EMPTY_ARRAY;
	private final IRecipeHolder[] glass_1 = IRecipeHolder.EMPTY_ARRAY;
	private final IRecipeHolder[] glass_3 = IRecipeHolder.EMPTY_ARRAY;
	private final IRecipeHolder[] glass_6 = IRecipeHolder.EMPTY_ARRAY;
	private final IRecipeHolder[] pane_1 = IRecipeHolder.EMPTY_ARRAY;
	private final IRecipeHolder[] pane_3 = IRecipeHolder.EMPTY_ARRAY;
	private final IRecipeHolder[] pane_8 = IRecipeHolder.EMPTY_ARRAY;
	private final IRecipeHolder[] pane_16 = IRecipeHolder.EMPTY_ARRAY;

	private final IRecipeHolder plastic_block_paver = IRecipeHolder.EMPTY;
	private final IRecipeHolder plastic_block_column = IRecipeHolder.EMPTY;
	private final IRecipeHolder plastic_block_bricks_large = IRecipeHolder.EMPTY;
	private final IRecipeHolder plastic_block_chiseled = IRecipeHolder.EMPTY;
	private final IRecipeHolder plastic_block_road = IRecipeHolder.EMPTY;
	private final IRecipeHolder plastic_block_bricks_small = IRecipeHolder.EMPTY;

	private final IRecipeHolder ice_brick_small = IRecipeHolder.EMPTY;
	private final IRecipeHolder glowstone_brick_small = IRecipeHolder.EMPTY;
	private final IRecipeHolder lapis_brick_small = IRecipeHolder.EMPTY;
	private final IRecipeHolder obsidian_brick_small = IRecipeHolder.EMPTY;
	private final IRecipeHolder pavedstone_brick_small = IRecipeHolder.EMPTY;
	private final IRecipeHolder snow_brick_small = IRecipeHolder.EMPTY;
	private final IRecipeHolder ice_brick_large = IRecipeHolder.EMPTY;
	private final IRecipeHolder glowstone_brick_large = IRecipeHolder.EMPTY;
	private final IRecipeHolder lapis_brick_large = IRecipeHolder.EMPTY;
	private final IRecipeHolder obsidian_brick_large = IRecipeHolder.EMPTY;
	private final IRecipeHolder pavedstone_brick_large = IRecipeHolder.EMPTY;
	private final IRecipeHolder snow_brick_large = IRecipeHolder.EMPTY;
	private final IRecipeHolder brick_brick_large = IRecipeHolder.EMPTY;

	private final IRecipeHolder ice_brick_small_from_large = IRecipeHolder.EMPTY;
	private final IRecipeHolder glowstone_brick_from_large = IRecipeHolder.EMPTY;
	private final IRecipeHolder lapis_brick_small_from_large = IRecipeHolder.EMPTY;
	private final IRecipeHolder obsidian_brick_small_from_large = IRecipeHolder.EMPTY;
	private final IRecipeHolder pavedstone_brick_small_from_large = IRecipeHolder.EMPTY;
	private final IRecipeHolder snow_brick_small_from_large = IRecipeHolder.EMPTY;
	private final IRecipeHolder brick_from_large_brick = IRecipeHolder.EMPTY;

	private final IRecipeHolder blackstone_smooth = IRecipeHolder.EMPTY;
	private final IRecipeHolder whitestone_smooth = IRecipeHolder.EMPTY;
	private final IRecipeHolder blackstone_cobble = IRecipeHolder.EMPTY;
	private final IRecipeHolder whitestone_cobble = IRecipeHolder.EMPTY;
	private final IRecipeHolder blackstone_brick_large = IRecipeHolder.EMPTY;
	private final IRecipeHolder whitestone_brick_large = IRecipeHolder.EMPTY;
	private final IRecipeHolder blackstone_brick_large_from_smooth = IRecipeHolder.EMPTY;
	private final IRecipeHolder whitestone_brick_large_from_smooth = IRecipeHolder.EMPTY;
	private final IRecipeHolder blackstone_brick_small = IRecipeHolder.EMPTY;
	private final IRecipeHolder whitestone_brick_small = IRecipeHolder.EMPTY;
	private final IRecipeHolder blackstone_brick_small_from_large = IRecipeHolder.EMPTY;
	private final IRecipeHolder whitestone_brick_small_from_large = IRecipeHolder.EMPTY;
	private final IRecipeHolder blackstone_gravel = IRecipeHolder.EMPTY;
	private final IRecipeHolder whitestone_gravel = IRecipeHolder.EMPTY;
	private final IRecipeHolder blackstone_paver = IRecipeHolder.EMPTY;
	private final IRecipeHolder whitestone_paver = IRecipeHolder.EMPTY;
	private final IRecipeHolder blackstone_paver_from_smooth = IRecipeHolder.EMPTY;
	private final IRecipeHolder whitestone_paver_from_smooth = IRecipeHolder.EMPTY;
	private final IRecipeHolder blackstone_smooth_from_paver = IRecipeHolder.EMPTY;
	private final IRecipeHolder whitestone_smooth_from_paver = IRecipeHolder.EMPTY;

	private final IRecipeHolder meat_ingot_raw = IRecipeHolder.EMPTY;
	private final IRecipeHolder meat_ingot_cooked = IRecipeHolder.EMPTY;
	private final IRecipeHolder meat_block_raw = IRecipeHolder.EMPTY;
	private final IRecipeHolder meat_block_cooked = IRecipeHolder.EMPTY;
	private final IRecipeHolder meat_ingot_raw_from_block = IRecipeHolder.EMPTY;
	private final IRecipeHolder meat_ingot_cooked_from_block = IRecipeHolder.EMPTY;
	private final IRecipeHolder meat_nugget_raw_from_ingot = IRecipeHolder.EMPTY;
	private final IRecipeHolder meat_nugget_cooked_from_ingot = IRecipeHolder.EMPTY;

	private final IRecipeHolder pinkslime_block = IRecipeHolder.EMPTY;
	private final IRecipeHolder pinkslime_block_to_ball = IRecipeHolder.EMPTY;

	private void registerDecorative() {

		//region road
		road_block.addShaped(
				SURROUND,
				'X', "sheetPlastic",
				'C', stack(STONEBRICK, 1, 0)
		);
		road_light.addShapeless(
				ROTATED_GEAR,
				'X', road_block,
				'C', REDSTONE_LAMP
		);
		road_light.addShapeless(road_light_inverted);
		road_light_inverted.addShapeless(road_light);
		//endregion

		//region glass and dye
		{
			String pane = "paneGlass", glass = "blockGlass";
			for (int i = 0; i < 16; i++) {
				String dye = DYES[i];
				String dye2 = "dyeCeramic" + dye;
				String dye3 = "dye" + dye;
				ceramic_dye_4[i].addShapeless(stack(CLAY_BALL), dye3);
				ceramic_dye_8[i].addShapeless(stack(CLAY_BALL), stack(CLAY_BALL), dye3, dye3);
				glass_1[i].addShapeless(dye2, glass);
				glass_3[i].addShapeless(dye2, glass, glass, glass);
				glass_6[i].addShapeless(dye2, dye2, glass, glass, glass, glass, glass, glass);
				pane_1[i].addShapeless(dye2, pane);
				pane_3[i].addShapeless(dye2, pane, pane, pane);
				pane_8[i].addShapeless(dye2, pane, pane, pane, pane, pane, pane, pane, pane);

				pane_16[i].addShaped(
						FENCE,
						'X', glass_1[i]
				);
			}
		}
		//endregion

		//region plastic
		plastic_block_paver.addShaped(
				SINGLE,
				'X', "blockPlastic"
		);
		plastic_block.addShaped(
				SINGLE,
				'X', plastic_block_paver
		);
		plastic_block_column.addShaped(
				LAYER,
				'X', plastic_block_paver
		);
		plastic_block_bricks_large.addShaped(
				STORAGE_SMALL,
				'X', plastic_block
		);
		plastic_block_bricks_small.addShaped(
				STORAGE_SMALL,
				'X', plastic_block_bricks_large
		);
		plastic_block_chiseled.addShaped(
				HOLLOW,
				'X', plastic_block_paver
		);
		plastic_block_road.addShaped(
				STORAGE_SMALL,
				'X', plastic_block_chiseled
		);
		//endregion

		//region bricks
		//region small bricks
		ice_brick_small.addShaped(
				ROTATED_GEAR,
				'X', stack_wildcard(BRICK_BLOCK),
				'C', stack_wildcard(ICE)
		);
		glowstone_brick_small.addShaped(
				ROTATED_GEAR,
				'X', stack_wildcard(BRICK_BLOCK),
				'C', stack_wildcard(GLOWSTONE)
		);
		lapis_brick_small.addShaped(
				ROTATED_GEAR,
				'X', stack_wildcard(BRICK_BLOCK),
				'C', "blockLapis"
		);
		obsidian_brick_small.addShaped(
				ROTATED_GEAR,
				'X', stack_wildcard(BRICK_BLOCK),
				'C', stack_wildcard(OBSIDIAN)
		);
		pavedstone_brick_small.addShaped(
				ROTATED_GEAR,
				'X', stack_wildcard(BRICK_BLOCK),
				'C', stack(STONE_SLAB, 1, 0)
		);
		snow_brick_small.addShaped(
				ROTATED_GEAR,
				'X', stack_wildcard(BRICK_BLOCK),
				'C', stack_wildcard(SNOW)
		);
		//endregion

		//region large bricks
		ice_brick_large.addShaped(
				ROTATED_GEAR,
				'X', STONEBRICK,
				'C', stack_wildcard(ICE)
		);
		glowstone_brick_large.addShaped(
				ROTATED_GEAR,
				'X', STONEBRICK,
				'C', stack_wildcard(GLOWSTONE)
		);
		lapis_brick_large.addShaped(
				ROTATED_GEAR,
				'X', STONEBRICK,
				'C', "blockLapis"
		);
		obsidian_brick_large.addShaped(
				ROTATED_GEAR,
				'X', STONEBRICK,
				'C', stack_wildcard(OBSIDIAN)
		);
		pavedstone_brick_large.addShaped(
				ROTATED_GEAR,
				'X', STONEBRICK,
				'C', stack(STONE_SLAB, 1, 0)
		);
		snow_brick_large.addShaped(
				ROTATED_GEAR,
				'X', STONEBRICK,
				'C', stack_wildcard(SNOW)
		);
		brick_brick_large.addShaped(
				ROTATED_GEAR,
				'X', STONEBRICK,
				'C', stack_wildcard(BRICK_BLOCK)
		);
		//endregion

		//region small to large
		ice_brick_small_from_large.addShaped(
				STORAGE_SMALL,
				'X', ice_brick_large
		);
		glowstone_brick_from_large.addShaped(
				STORAGE_SMALL,
				'X', glowstone_brick_large
		);
		lapis_brick_small_from_large.addShaped(
				STORAGE_SMALL,
				'X', lapis_brick_large
		);
		obsidian_brick_small_from_large.addShaped(
				STORAGE_SMALL,
				'X', obsidian_brick_large
		);
		pavedstone_brick_small_from_large.addShaped(
				STORAGE_SMALL,
				'X', pavedstone_brick_large
		);
		snow_brick_small_from_large.addShaped(
				STORAGE_SMALL,
				'X', snow_brick_large
		);
		brick_from_large_brick.addShaped(
				STORAGE_SMALL,
				'X', brick_brick_large
		);
		//endregion
		//endregion

		//region stone
		blackstone_smooth.addShaped(
				ROTATED_GEAR,
				'C', "dyeBlack",
				'X', "stone"
		);
		whitestone_smooth.addShaped(
				ROTATED_GEAR,
				'C', stack_wildcard(SUGAR),
				'X', "stone"
		);

		blackstone_cobble.addShaped(
				ROTATED_GEAR,
				'C', "dyeBlack",
				'X', "cobblestone"
		);
		whitestone_cobble.addShaped(
				ROTATED_GEAR,
				'C', stack_wildcard(SUGAR),
				'X', "cobblestone"
		);

		blackstone_brick_large.addShaped(
				ROTATED_GEAR,
				'C', "dyeBlack",
				'X', stack(STONEBRICK)
		);
		whitestone_brick_large.addShaped(
				ROTATED_GEAR,
				'C', stack_wildcard(SUGAR),
				'X', stack(STONEBRICK)
		);
		blackstone_brick_large_from_smooth.addShaped(
				STORAGE_SMALL,
				'X', blackstone_smooth
		);
		whitestone_brick_large_from_smooth.addShaped(
				STORAGE_SMALL,
				'X', whitestone_smooth
		);

		blackstone_brick_small.addShaped(
				ROTATED_GEAR,
				'C', "dyeBlack",
				'X', stack_wildcard(BRICK_BLOCK)
		);
		whitestone_brick_small.addShaped(
				ROTATED_GEAR,
				'C', stack_wildcard(SUGAR),
				'X', stack_wildcard(BRICK_BLOCK)
		);
		blackstone_brick_small_from_large.addShaped(
				STORAGE_SMALL,
				'X', blackstone_brick_large
		);
		whitestone_brick_small_from_large.addShaped(
				STORAGE_SMALL,
				'X', whitestone_brick_large
		);

		blackstone_gravel.addShaped(
				ROTATED_GEAR,
				'C', "dyeBlack",
				'X', stack_wildcard(GRAVEL)
		);
		whitestone_gravel.addShaped(
				ROTATED_GEAR,
				'C', stack_wildcard(SUGAR),
				'X', stack_wildcard(GRAVEL)
		);

		blackstone_paver.addShaped(
				SURROUND,
				'C', "dyeBlack",
				'X', stack(STONE_SLAB, 1, 0)
		);
		whitestone_paver.addShaped(
				SURROUND,
				'C', stack_wildcard(SUGAR),
				'X', stack(STONE_SLAB, 1, 0)
		);
		blackstone_paver_from_smooth.addShapeless(blackstone_smooth);
		blackstone_smooth_from_paver.addShapeless(blackstone_paver);
		whitestone_paver_from_smooth.addShapeless(whitestone_smooth);
		whitestone_smooth_from_paver.addShapeless(whitestone_paver);
		//endregion

		//region meat
		meat_ingot_raw.addShaped(
				STORAGE,
				'X', "nuggetMeatRaw"
		);
		meat_ingot_cooked.addShaped(
				STORAGE,
				'X', "nuggetMeat"
		);
		meat_block_raw.addShaped(
				STORAGE,
				'X', "ingotMeatRaw"
		);
		meat_block_cooked.addShaped(
				STORAGE,
				'X', "ingotMeat"
		);

		meat_ingot_raw_from_block.addShaped(
				SINGLE,
				'X', meat_block_raw
		);
		meat_ingot_cooked_from_block.addShaped(
				SINGLE,
				'X', meat_block_cooked
		);
		meat_nugget_raw_from_ingot.addShaped(
				SINGLE,
				'X', "ingotMeatRaw"
		);
		meat_nugget_cooked_from_ingot.addShaped(
				SINGLE,
				'X', "ingotMeat"
		);
		//endregion

		//region pinkslime
		pinkslime_block.addShaped(
				STORAGE,
				'X', pinkslime_block_to_ball
		);
		pinkslime_block_to_ball.addShaped(
				SINGLE,
				'X', pinkslime_block
		);
		//endregion
	}
	//endregion

	//region syringes
	private final IRecipeHolder xp_extractor = IRecipeHolder.EMPTY;
	private final IRecipeHolder syringe_empty = IRecipeHolder.EMPTY;
	private final IRecipeHolder syringe_health = IRecipeHolder.EMPTY;
	private final IRecipeHolder syringe_growth = IRecipeHolder.EMPTY;
	private final IRecipeHolder syringe_zombie = IRecipeHolder.EMPTY;
	private final IRecipeHolder syringe_slime = IRecipeHolder.EMPTY;
	private final IRecipeHolder syringe_cure = IRecipeHolder.EMPTY;

	private void registerSyringes() {

		xp_extractor.addShaped(
				"PLP",
				"PLP",
				"RPR",
				'R', "itemRubber",
				'L', "blockGlass",
				'P', "sheetPlastic"
		);

		syringe_empty.addShaped(
				"PRP",
				"P P",
				" I ",
				'P', "sheetPlastic",
				'R', "itemRubber",
				'I', "ingotIron"
		);

		syringe_health.addShapeless(syringe_empty, APPLE);

		syringe_growth.addShapeless(syringe_empty, GOLDEN_CARROT);
		syringe_zombie.addShapeless(syringe_empty, ROTTEN_FLESH, ROTTEN_FLESH, ROTTEN_FLESH, ROTTEN_FLESH, ROTTEN_FLESH, ROTTEN_FLESH);

		syringe_slime.addShapeless("slimeball", "gemLapis", syringe_empty, "gemLapis");

		syringe_cure.addShapeless(syringe_empty, GOLDEN_APPLE);
	}
	//endregion

	//region plastics
	private final IRecipeHolder machine_block = IRecipeHolder.EMPTY;
	private final IRecipeHolder plastic_sheet = IRecipeHolder.EMPTY;
	private final IRecipeHolder plastic_block = IRecipeHolder.EMPTY;
	private final IRecipeHolder plastic_block_to_sheet = IRecipeHolder.EMPTY;

	private final IRecipeHolder hammer = IRecipeHolder.EMPTY;
	private final IRecipeHolder straw = IRecipeHolder.EMPTY;
	private final IRecipeHolder ruler = IRecipeHolder.EMPTY;
	private final IRecipeHolder plastic_cup = IRecipeHolder.EMPTY;
	private final IRecipeHolder plastic_cell = IRecipeHolder.EMPTY;
	private final IRecipeHolder plastic_bag = IRecipeHolder.EMPTY;
	private final IRecipeHolder plastic_bag_erasure = IRecipeHolder.EMPTY;
	private final IRecipeHolder plastic_pipe = IRecipeHolder.EMPTY;

	private void registerPlastics() {

		machine_block.addShaped(
				"PPP",
				"SSS",
				'P', "sheetPlastic",
				'S', "stone"
		);

		plastic_sheet.addShaped(
				STORAGE_SMALL,
				'X', "dustPlastic"
		);

		plastic_block.addShaped(
				STORAGE_SMALL,
				'X', "sheetPlastic"
		);
		plastic_block_to_sheet.addShaped(
				SINGLE,
				'X', "blockPlastic"
		);

		hammer.addShaped(
				"PPP",
				" S ",
				" S ",
				'P', "sheetPlastic",
				'S', "stickWood"
		);

		straw.addShaped(
				"PP",
				"P ",
				"P ",
				'P', "sheetPlastic"
		);

		ruler.addShaped(
				"P",
				"A",
				"P",
				'P', "sheetPlastic",
				'A', PAPER
		);

		plastic_cup.addShaped(
				" P ",
				"P P",
				'P', "sheetPlastic"
		);

		plastic_cell.addShaped(
				" P ",
				"P P",
				" P ",
				'P', "sheetPlastic"
		);

		plastic_bag.addShaped(
				"SPS",
				"P P",
				"PPP",
				'P', "sheetPlastic",
				'S', STRING
		);

		plastic_bag_erasure.addShapeless(plastic_bag);

		plastic_pipe.addShaped(
				"PPP",
				"   ",
				"PPP",
				'P', "sheetPlastic"
		);
	}
	//endregion

	//region armor
	private final IRecipeHolder armor_glasses = IRecipeHolder.EMPTY;
	private final IRecipeHolder armor_plastic_helmet = IRecipeHolder.EMPTY;
	private final IRecipeHolder armor_plastic_chestplate = IRecipeHolder.EMPTY;
	private final IRecipeHolder armor_plastic_leggings = IRecipeHolder.EMPTY;
	private final IRecipeHolder armor_plastic_boots = IRecipeHolder.EMPTY;

	private void registerArmor() {

		armor_glasses.addShaped(
				"GPG",
				"P P",
				'P', "sheetPlastic",
				'G', "paneGlassBlack"
		);

		armor_plastic_helmet.addShaped(
				"PPP",
				"P P",
				'P', "sheetPlastic"
		);

		armor_plastic_chestplate.addShaped(
				"P P",
				"PPP",
				"PPP",
				'P', "sheetPlastic"
		);

		armor_plastic_leggings.addShaped(
				"PPP",
				"P P",
				"P P",
				'P', "sheetPlastic"
		);

		armor_plastic_boots.addShaped(
				"P P",
				"P P",
				'P', "sheetPlastic"
		);
	}
	//endregion

	//region misc
	private final IRecipeHolder fertilizer_item = IRecipeHolder.EMPTY;
	private final IRecipeHolder spyglass = IRecipeHolder.EMPTY;
	private final IRecipeHolder porta_spawner = IRecipeHolder.EMPTY;
	private final IRecipeHolder detcord = IRecipeHolder.EMPTY;
	private final IRecipeHolder explosive_fishing_rod = IRecipeHolder.EMPTY;

	private void registerMiscItems() {

		fertilizer_item.addShaped(
				"WBW",
				"STS",
				"WBW",
				'W', Items.WHEAT,
				'B', stack(DYE, 1, 15),
				'S', STRING,
				'T', "stickWood"
		);

		spyglass.addShaped(
				"GLG",
				"PLP",
				" S ",
				'G', "ingotGold",
				'L', "blockGlass",
				'P', "sheetPlastic",
				'S', "stickWood"
		);

		porta_spawner.addShaped(
				"GLG",
				"DND",
				"GLG",
				'G', "ingotGold",
				'L', "blockGlass",
				'D', "gemDiamond",
				'N', NETHER_STAR
		);

		detcord.addShaped(
				"XXX",
				"XTX",
				"XXX",
				'X', "itemRubber",
				'T', stack_wildcard(TNT)
		);

		explosive_fishing_rod.addShaped(
				"DD ",
				"DFD",
				"TDD",
				'D', "wireExplosive",
				'F', FISHING_ROD,
				'T', REDSTONE_TORCH
		);
	}
	//endregion

	//region safarinet
	private final IRecipeHolder safarinet = IRecipeHolder.EMPTY;
	private final IRecipeHolder expensive_safarinet = IRecipeHolder.EMPTY;
	private final IRecipeHolder safarinet_singleuse = IRecipeHolder.EMPTY;
	private final IRecipeHolder safarinet_jailer = IRecipeHolder.EMPTY;
	private final IRecipeHolder safarinet_fancy = IRecipeHolder.EMPTY;
	private final IRecipeHolder safarinet_launcher = IRecipeHolder.EMPTY;

	private void registerSafariNets() {

		expensive_safarinet.addShaped(
				"SLS",
				"PBP",
				"SPS",
				'S', STRING,
				'L', LEATHER,
				'P', ENDER_PEARL,
				'B', porta_spawner
		);
		safarinet.addShaped(
				GEAR,
				'X', stack_wildcard(ENDER_PEARL),
				'C', stack_wildcard(GHAST_TEAR)
		);

		safarinet_singleuse.addShaped(
				"SPS",
				" B ",
				"S S",
				'S', STRING,
				'P', "sheetPlastic",
				'B', "slimeball"
		);

		safarinet_jailer.addShaped(
				GEAR,
				'X', stack_wildcard(IRON_BARS),
				'C', safarinet_singleuse
		);

		safarinet_fancy.addShaped(
				SURROUND,
				'X', GOLD_NUGGET,
				'C', safarinet_jailer
		);

		safarinet_launcher.addShaped(
				"PGP",
				"LGL",
				"IRI",
				'P', "sheetPlastic",
				'L', GLOWSTONE_DUST,
				'G', GUNPOWDER,
				'I', "ingotIron",
				'R', "dustRedstone"
		);
	}
	//endregion

	//region smelting
	private final IRecipeHolder rubber_bar = IRecipeHolder.EMPTY;
	private final IRecipeHolder raw_plastic = IRecipeHolder.EMPTY;
	private final IRecipeHolder plastic_block_to_raw_plastic = IRecipeHolder.EMPTY;
	private final IRecipeHolder plastic_sheet_to_raw_plastic = IRecipeHolder.EMPTY;
	private final IRecipeHolder plastic_bag_to_raw_plastic = IRecipeHolder.EMPTY;
	private final IRecipeHolder straw_to_raw_plastic = IRecipeHolder.EMPTY;
	private final IRecipeHolder ruler_to_raw_plastic = IRecipeHolder.EMPTY;
	private final IRecipeHolder meat_nugget_raw_to_cooked = IRecipeHolder.EMPTY;
	private final IRecipeHolder sugar_charcoal = IRecipeHolder.EMPTY;
	private final IRecipeHolder meat_block_cooked_to_charcoal = IRecipeHolder.EMPTY;
	private final IRecipeHolder rubberwood_to_charcoal = IRecipeHolder.EMPTY;
	private final IRecipeHolder pinkslime_gem = IRecipeHolder.EMPTY;

	private void registerSmelting() {

		rubber_bar.addSmelting(stack(rawRubberItem), 0.1f);

		for (@Nonnull ItemStack s : getOres("itemRubber"))
			raw_plastic.addSmelting(s, 0.3f);
		for (@Nonnull ItemStack s : getOres("blockPlastic"))
			plastic_block_to_raw_plastic.addSmelting(s);
		for (@Nonnull ItemStack s : getOres("sheetPlastic"))
			plastic_sheet_to_raw_plastic.addSmelting(s);

		plastic_bag_to_raw_plastic.addSmelting(plastic_bag);
		straw_to_raw_plastic.addSmelting(straw);
		ruler_to_raw_plastic.addSmelting(ruler);

		meat_ingot_cooked.addSmelting(meat_ingot_raw, 0.5f);
		meat_nugget_raw_to_cooked.addSmelting(meat_nugget_raw_from_ingot, 0.3f);
		sugar_charcoal.addSmelting(stack(SUGAR), 0.1f);
		meat_block_cooked_to_charcoal.addSmelting(meat_block_cooked, 0.1f);
		rubberwood_to_charcoal.addSmelting(stack(rubberWoodBlock), 0.1f);

		pinkslime_gem.addSmelting(pinkslime_block, 0.5f);

		whitestone_smooth.addSmelting(whitestone_cobble, 0.1f);
		blackstone_smooth.addSmelting(blackstone_cobble, 0.1f);
		whitestone_paver.addSmelting(whitestone_smooth);
		blackstone_paver.addSmelting(blackstone_smooth);
	}
	//endregion

	//region vanilla improvement
	private final IRecipeHolder rubberwood_planks = IRecipeHolder.EMPTY;

	private final IRecipeHolder stick_piston_clean_with_milk = IRecipeHolder.EMPTY;
	private final IRecipeHolder sticky_piston_from_raw_rubber = IRecipeHolder.EMPTY;

	private final IRecipeHolder record_blank = IRecipeHolder.EMPTY;
	private final IRecipeHolder vine_scaffold = IRecipeHolder.EMPTY;

	private final IRecipeHolder milk_bottle = IRecipeHolder.EMPTY;

	private final IRecipeHolder mossy_cobblestone = IRecipeHolder.EMPTY;
	private final IRecipeHolder mossy_stone_brick = IRecipeHolder.EMPTY;
	private final IRecipeHolder podzol = IRecipeHolder.EMPTY;
	private final IRecipeHolder fertile_soil = IRecipeHolder.EMPTY;

	private final IRecipeHolder chocolate_milk_bucket = IRecipeHolder.EMPTY;
	private final IRecipeHolder sugar_charcoal_block = IRecipeHolder.EMPTY;

	private final IRecipeHolder torch_3 = IRecipeHolder.EMPTY;
	private final IRecipeHolder torch_2 = IRecipeHolder.EMPTY;
	private final IRecipeHolder torch_stone_3 = IRecipeHolder.EMPTY;
	private final IRecipeHolder torch_stone_2 = IRecipeHolder.EMPTY;

	private void registerVanillaImprovements() {

		rubberwood_planks.addShapeless(stack(rubberWoodBlock));

		stick_piston_clean_with_milk.addShapeless(stack(STICKY_PISTON, 1, 0), "listAllmilk");

		sticky_piston_from_raw_rubber.addShaped(
				"R",
				"P",
				'R', "itemRawRubber",
				'P', PISTON
		);

		record_blank.addShaped(
				SURROUND,
				'X', raw_plastic,
				'C', PAPER
		);

		podzol.addShaped(
				SURROUND,
				'X', stack(LEAVES, 1, 1),
				'C', stack(DIRT)
		);

		mossy_cobblestone.addShapeless(
				COBBLESTONE, COBBLESTONE, COBBLESTONE,
				COBBLESTONE, COBBLESTONE, COBBLESTONE,
				COBBLESTONE, "listAllwater", Items.WHEAT
		);
		mossy_stone_brick.addShapeless(
				stack(STONEBRICK), stack(STONEBRICK), stack(STONEBRICK),
				stack(STONEBRICK), stack(STONEBRICK), stack(STONEBRICK),
				stack(STONEBRICK), "listAllwater", Items.WHEAT
		);

/*		TODO double stone slab has no item as it did before. Use something else instead of it?
		if (enableSmoothSlabRecipe.getBoolean(true)) {
			addRecipe(stack(DOUBLE_STONE_SLAB, 3, 0), new Object[] {
				"VVV",
				'V', stack(DOUBLE_STONE_SLAB, 1, 8)
			});

			addRecipe(stack(DOUBLE_STONE_SLAB, 1, 8), new Object[] {
					"VV",
					'V', stack(STONE_SLAB, 1, 0)
			});
			addRecipe(stack(DOUBLE_STONE_SLAB, 1, 9), new Object[] {
				"VV",
				'V', stack(STONE_SLAB, 1, 1)
			});
		}
*/

		vine_scaffold.addShaped(
				DOOR,
				'X', VINE
		);

		milk_bottle.addShapeless(MILK_BUCKET, GLASS_BOTTLE);

		fertile_soil.addShapeless(podzol, fertilizer_item, "listAllmilk");

		chocolate_milk_bucket.addShapeless("listAllmilk", BUCKET, stack(DYE, 1, 3));

		sugar_charcoal_block.addShaped(
				STORAGE,
				'X', "itemCharcoalSugar"
		);

		torch_3.addShaped(
				"R",
				"S",
				'R', "itemRawRubber",
				'S', "stickWood"
		);

		torch_2.addShaped(
				"C",
				"S",
				'C', "itemCharcoalSugar",
				'S', "stickWood"
		);

		torch_stone_3.addShaped(
				"R",
				"S",
				'R', "itemRawRubber",
				'S', "stoneRod"
		);

		torch_stone_2.addShaped(
				"C",
				"S",
				'C', "itemCharcoalSugar",
				'S', "stoneRod"
		);
	}
	//endregion

	//region rails
	private final IRecipeHolder rail_cargo_pickup = IRecipeHolder.EMPTY;
	private final IRecipeHolder rail_cargo_dropoff = IRecipeHolder.EMPTY;
	private final IRecipeHolder rail_passenger_pickup = IRecipeHolder.EMPTY;
	private final IRecipeHolder rail_passenger_dropoff = IRecipeHolder.EMPTY;

	private void registerRails() {

		rail_cargo_pickup.addShaped(
				" C ",
				"SDS",
				"SSS",
				'C', CHEST,
				'S', "sheetPlastic",
				'D', DETECTOR_RAIL
		);

		rail_cargo_dropoff.addShaped(
				"SSS",
				"SDS",
				" C ",
				'C', CHEST,
				'S', "sheetPlastic",
				'D', DETECTOR_RAIL
		);

		rail_passenger_pickup.addShaped(
				" L ",
				"SDS",
				"SSS",
				'L', LAPIS_BLOCK,
				'S', "sheetPlastic",
				'D', DETECTOR_RAIL
		);

		rail_passenger_dropoff.addShaped(
				"SSS",
				"SDS",
				" L ",
				'L', LAPIS_BLOCK,
				'S', "sheetPlastic",
				'D', DETECTOR_RAIL
		);
	}
	//endregion

	//region guns
	private final IRecipeHolder needlegun = IRecipeHolder.EMPTY;
	private final IRecipeHolder potato_cannon = IRecipeHolder.EMPTY;
	private final IRecipeHolder rocket_launcher = IRecipeHolder.EMPTY;
	private final IRecipeHolder needle_magazine = IRecipeHolder.EMPTY;
	private final IRecipeHolder rocket_tracking = IRecipeHolder.EMPTY;
	private final IRecipeHolder rocket_dumb = IRecipeHolder.EMPTY;
	private final IRecipeHolder rocket_tracking_from_dumb = IRecipeHolder.EMPTY;
	private final IRecipeHolder needle_ammo_standard = IRecipeHolder.EMPTY;
	private final IRecipeHolder needle_ammo_shrapnel = IRecipeHolder.EMPTY;
	private final IRecipeHolder needle_ammo_anvil = IRecipeHolder.EMPTY;
	private final IRecipeHolder needle_ammo_fire = IRecipeHolder.EMPTY;
	private final IRecipeHolder needle_ammo_lava = IRecipeHolder.EMPTY;
	private final IRecipeHolder needle_ammo_sludge = IRecipeHolder.EMPTY;
	private final IRecipeHolder needle_ammo_sewage = IRecipeHolder.EMPTY;

	private void registerGuns() {

		needlegun.addShaped(
				"PGP",
				"PLP",
				"SIS",
				'P', "sheetPlastic",
				'I', "ingotIron",
				'S', MAGMA_CREAM,
				'L', safarinet_launcher,
				'G', spyglass
		);

		potato_cannon.addShaped(
				" L ",
				"PLP",
				"PTP",
				'P', "sheetPlastic",
				'L', plastic_pipe,
				'T', plastic_tank
		);

		rocket_launcher.addShaped(
				"PCP",
				"PRP",
				"ILI",
				'P', "sheetPlastic",
				'I', MINECART,
				'L', needlegun,
				'R', logic_card_2,
				'C', logic_card_3
		);

		needle_magazine.addShaped(
				"P P",
				"PIP",
				"PPP",
				'P', "sheetPlastic",
				'I', "ingotIron"
		);

		rocket_tracking.addShaped(
				"PCP",
				"PTP",
				"IMI",
				'C', logic_card_1,
				'M', needle_magazine,
				'P', "sheetPlastic",
				'T', TNT,
				'I', FIREWORKS
		);

		rocket_dumb.addShaped(
				"PPP",
				"PTP",
				"IMI",
				'M', needle_magazine,
				'P', "sheetPlastic",
				'T', TNT,
				'I', FIREWORKS
		);

		rocket_tracking_from_dumb.addShapeless(logic_card_1, rocket_dumb);

		needle_ammo_standard.addShaped(
				"AAA",
				"AAA",
				"GMG",
				'A', ARROW,
				'M', needle_magazine,
				'G', GUNPOWDER
		);

		needle_ammo_shrapnel.addShaped(
				"AAA",
				"AAA",
				"GMG",
				'A', FLINT,
				'M', needle_magazine,
				'G', GUNPOWDER
		);

		needle_ammo_anvil.addShaped(
				"SAS",
				"STS",
				"SMS",
				'S', STRING,
				'A', stack(ANVIL),
				'T', TNT,
				'M', needle_magazine
		);

		needle_ammo_fire.addShapeless(needle_ammo_shrapnel, FLINT_AND_STEEL);

		needle_ammo_lava.addShapeless(needle_ammo_standard, plastic_cup, LAVA_BUCKET);

		needle_ammo_sludge.addShapeless(needle_ammo_standard, plastic_cup, MFRUtil.getBucketFor(MFRFluids.sludge));

		needle_ammo_sewage.addShapeless(needle_ammo_standard, plastic_cup, MFRUtil.getBucketFor(MFRFluids.sewage));
	}
	//endregion

	//region rednet
	private final IRecipeHolder rednet_cable = IRecipeHolder.EMPTY;
	private final IRecipeHolder rednet_cable_from_plastic_pipe = IRecipeHolder.EMPTY;
	private final IRecipeHolder rednet_cable_energy_single = IRecipeHolder.EMPTY;
	private final IRecipeHolder rednet_cable_energy_multi = IRecipeHolder.EMPTY;
	private final IRecipeHolder rednet_panel = IRecipeHolder.EMPTY;
	private final IRecipeHolder rednet_controller_housing = IRecipeHolder.EMPTY;
	private final IRecipeHolder rednet_controller = IRecipeHolder.EMPTY;
	private final IRecipeHolder logic_card_1 = IRecipeHolder.EMPTY;
	private final IRecipeHolder logic_card_2 = IRecipeHolder.EMPTY;
	private final IRecipeHolder logic_card_3 = IRecipeHolder.EMPTY;
	private final IRecipeHolder rednet_meter = IRecipeHolder.EMPTY;
	private final IRecipeHolder rednet_multimeter = IRecipeHolder.EMPTY;
	private final IRecipeHolder rednet_memorycard = IRecipeHolder.EMPTY;
	private final IRecipeHolder rednet_memorycard_erasure = IRecipeHolder.EMPTY;

	private void registerRedNet() {

		rednet_cable.addShaped(
				"PPP",
				"RRR",
				"PPP",
				'R', "dustRedstone",
				'P', "sheetPlastic"
		);

		rednet_cable_from_plastic_pipe.addShapeless(
				"dustRedstone",
				"dustRedstone",
				plastic_pipe,
				plastic_pipe,
				plastic_pipe,
				plastic_pipe,
				plastic_pipe
		);

		rednet_cable_energy_single.addShapeless(
				"nuggetGold",
				"nuggetGold",
				"nuggetGold",
				"dustRedstone",
				"dustRedstone",
				rednet_cable
		);

		rednet_cable_energy_multi.addShapeless(
				"ingotGold",
				"ingotGold",
				"blockRedstone",
				rednet_cable,
				rednet_cable,
				rednet_cable,
				rednet_cable,
				rednet_cable,
				rednet_cable
		);

		rednet_controller_housing.addShaped(
				"PRP",
				"RGR",
				"PIP",
				'R', "dustRedstone",
				'P', "sheetPlastic",
				'G', "blockGlass",
				'I', "ingotIron"
		);

		rednet_controller.addShaped(
				"RDR",
				"LGL",
				"PHP",
				'H', rednet_controller_housing,
				'P', "sheetPlastic",
				'G', "ingotGold",
				'L', "gemLapis",
				'D', "gemDiamond",
				'R', "dustRedstone"
		);

		logic_card_1.addShaped(
				"RPR",
				"PGP",
				"RPR",
				'P', "sheetPlastic",
				'G', "ingotGold",
				'R', "dustRedstone"
		);

		logic_card_2.addShaped(
				"GPG",
				"PCP",
				"RGR",
				'C', logic_card_1,
				'P', "sheetPlastic",
				'G', "ingotGold",
				'R', "dustRedstone"
		);

		logic_card_3.addShaped(
				"DPD",
				"RCR",
				"GDG",
				'C', logic_card_2,
				'P', "sheetPlastic",
				'G', "ingotGold",
				'D', "gemDiamond",
				'R', "dustRedstone"
		);

		rednet_meter.addShaped(
				" G",
				"PR",
				"PP",
				'P', "sheetPlastic",
				'G', "nuggetGold",
				'R', "dustRedstone"
		);

		rednet_multimeter.addShaped(
				"RGR",
				"IMI",
				"PPP",
				'P', "sheetPlastic",
				'G', "ingotGold",
				'I', "ingotIron",
				'R', "dustRedstone",
				'M', rednet_meter
		);

		rednet_memorycard.addShaped(
				"GGG",
				"PRP",
				"PPP",
				'P', "sheetPlastic",
				'G', "nuggetGold",
				'R', "dustRedstone"
		);
		rednet_memorycard_erasure.addShapeless(rednet_memorycard);

		rednet_panel.addShaped(
				"PCP",
				"PBP",
				"KPK",
				'P', "sheetPlastic",
				'C', rednetCableBlock,
				'B', BOOKSHELF,
				'K', "dyeBlack"
		);
	}
	//endregion

	private void registerRedNetManual() {

		RecipeHelper.addShapelessRecipe(ItemBlockRedNetLogic.manual, plasticSheetItem, "dustRedstone", BOOK);
	}

}
