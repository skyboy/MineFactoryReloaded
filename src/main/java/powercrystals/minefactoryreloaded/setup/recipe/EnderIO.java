package powercrystals.minefactoryreloaded.setup.recipe;
/*
import cofh.core.util.helpers.RecipeHelper;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.UniversalBucket;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.MissingModsException;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.versioning.ArtifactVersion;
import net.minecraftforge.fml.common.versioning.DefaultArtifactVersion;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.core.MFRUtil;
import powercrystals.minefactoryreloaded.setup.MFRConfig;
import powercrystals.minefactoryreloaded.setup.Machine;

import javax.annotation.Nonnull;
import java.util.Collections;

import static cofh.core.util.helpers.ItemHelper.stack;
import static powercrystals.minefactoryreloaded.setup.MFRThings.*;

public class EnderIO extends Vanilla {

	private static final String EIO = "EnderIO";

	private String redstone = "dustRedstone";

	// Items
	private ItemStack capacitorBasic;
	private ItemStack capacitorDouble;
	private ItemStack capacitorOctadic;
	private ItemStack gear;
	private ItemStack chassis;
	private ItemStack zombieElectrode;
	private ItemStack zombieController;
	private ItemStack dsAxe;
	private ItemStack dsPick;
	private ItemStack dsSword;
	private ItemStack probe;
	private ItemStack conduitLiquid;
	private ItemStack xpRod;
	private ItemStack soulVial;
	private ItemStack fireWaterBucket;

	// Blocks
	private ItemStack light;
	private ItemStack reservoir;
	private ItemStack dimTrans;
	private ItemStack vacuumChest;
	private ItemStack tank, tankPressurized;
	private ItemStack xpObelisk;
	private ItemStack darkSteelAnvil;
	private ItemStack capBank;
	private ItemStack reinforcedObsidian;

	// Machines
	private ItemStack crafter;
	private ItemStack combustionGen;

	@Override
	protected void gatherItems() {

		if (!Loader.isModLoaded(EIO)) {
			MineFactoryReloadedCore.log().fatal("EnderIO is required for EnderIO recipes to be enabled.");
			throw new MissingModsException(Collections.singleton((ArtifactVersion) new DefaultArtifactVersion(EIO)), MineFactoryReloadedCore.modId, MineFactoryReloadedCore.modName);
		}

		// Items
		capacitorBasic = stackFor("itemBasicCapacitor");
		capacitorDouble = stackFor("itemBasicCapacitor", 1);
		capacitorOctadic = stackFor("itemBasicCapacitor", 2);
		chassis = stackFor("itemMachinePart");
		gear = stackFor("itemMachinePart", 1);
		zombieElectrode = stackFor("itemFrankenSkull");
		zombieController = stackFor("itemFrankenSkull", 1);
		dsAxe = stackFor("darkSteel_axe");
		dsSword = stackFor("darkSteel_sword");
		dsPick = stackFor("darkSteel_pickaxe");
		probe = stackFor("itemConduitProbe");
		conduitLiquid = stackFor("itemLiquidConduit");
		xpRod = stackFor("itemXpTransfer");
		soulVial = stackFor("itemSoulVessel");
		fireWaterBucket = UniversalBucket.getFilledBucket(ForgeModContainer.getInstance().universalBucket, FluidRegistry.getFluid("rocket_fuel"));

		// Blocks
		light = stackForBlock("blockElectricLight", 2);
		reservoir = stackForBlock("blockReservoir");
		dimTrans = stackForBlock("blockTransceiver");
		vacuumChest = stackForBlock("blockVacuumChest");
		tank = stackForBlock("blockTank");
		tankPressurized = stackForBlock("blockTank", 1);
		xpObelisk = stackForBlock("blockExperienceObelisk");
		darkSteelAnvil = stackForBlock("blockDarkSteelAnvil");
		capBank = stackForBlock("blockCapBank", 2);
		reinforcedObsidian = stackForBlock("blockReinforcedObsidian");

		// Machines
		crafter = stackForBlock("blockCrafter");
		combustionGen = stackForBlock("blockCombustionGenerator");
	}

	@Nonnull
	private ItemStack stackFor(String itemName) {

		return stackFor(itemName, 0);
	}

	@Nonnull
	private ItemStack stackFor(String itemName, int damage) {

		return new ItemStack(Item.REGISTRY.getObject(new ResourceLocation(EIO, itemName)), 1, damage);
	}

	@Nonnull
	private ItemStack stackForBlock(String blockName) {

		return stackForBlock(blockName, 0);
	}

	@Nonnull
	private ItemStack stackForBlock(String blockName, int damage) {

		return new ItemStack(MFRUtil.findBlock(EIO, blockName), 1, damage);
	}

	@Override
	protected void registerMachines() {

		String prefix = "ingot";

		registerMachine(Machine.Planter,
				"PTP",
				"SFS",
				"OCO",
				'P', "sheetPlastic",
				'T', Items.FLOWER_POT,
				'S', Blocks.PISTON,
				'F', chassis,
				'O', prefix + "ElectricalSteel",
				'C', zombieController);

		registerMachine(Machine.Fisher, new Object[] {
				"PTP",
				"SFS",
				"OCO",
				'P', "sheetPlastic",
				'T', Items.FISHING_ROD,
				'S', Items.BUCKET,
				'F', chassis,
				'O', prefix + "Iron",
				'C', zombieController
		});

		registerMachine(Machine.Harvester, new Object[] {
				"PSP",
				"TFT",
				"OCO",
				'P', "sheetPlastic",
				'S', Items.SHEARS,
				'T', dsAxe,
				'F', chassis,
				'O', prefix + "Gold",
				'C', zombieController
		});

		registerMachine(Machine.Rancher, new Object[] {
				"PTP",
				"SFS",
				"OCO",
				'P', "sheetPlastic",
				'T', conduitLiquid,
				'S', Items.SHEARS,
				'F', chassis,
				'O', prefix + "ElectricalSteel",
				'C', zombieController
		});

		registerMachine(Machine.Fertilizer, new Object[] {
				"PTP",
				"SFS",
				"OCO",
				'P', "sheetPlastic",
				'T', Items.GLASS_BOTTLE,
				'S', Items.LEATHER,
				'F', chassis,
				'O', prefix + "ElectricalSteel",
				'C', zombieController
		});

		registerMachine(Machine.Vet, new Object[] {
				"PTP",
				"TFT",
				"OCO",
				'P', "sheetPlastic",
				'T', syringeEmptyItem,
				'F', chassis,
				'O', prefix + "Copper",
				'C', zombieElectrode
		});

		registerMachine(Machine.ItemCollector, 8, new Object[] {
				"P P",
				" F ",
				"PCP",
				'P', "sheetPlastic",
				'F', chassis,
				'C', Blocks.CHEST
		});

		registerMachine(Machine.BlockBreaker, new Object[] {
				"PTP",
				"SFA",
				"OCO",
				'P', "sheetPlastic",
				'T', prefix + "ElectricalSteel",
				'S', dsPick,
				'F', chassis,
				'A', Items.IRON_SHOVEL,
				'O', prefix + "Iron",
				'C', redstone
		});

		registerMachine(Machine.WeatherCollector, new Object[] {
				"PBP",
				"TFT",
				"OCO",
				'P', "sheetPlastic",
				'B', Blocks.IRON_BARS,
				'T', Items.BUCKET,
				'F', chassis,
				'O', prefix + "ElectricalSteel",
				'C', redstone
		});

		registerMachine(Machine.SludgeBoiler, new Object[] {
				"PTP",
				"SFS",
				"OCO",
				'P', "sheetPlastic",
				'T', Items.BUCKET,
				'S', Blocks.FURNACE,
				'F', chassis,
				'O', prefix + "Iron",
				'C', redstone
		});

		registerMachine(Machine.Sewer, 4, new Object[] {
				"PTP",
				"SFS",
				"SQS",
				'P', "sheetPlastic",
				'T', Items.BUCKET,
				'S', Items.BRICK,
				'F', chassis,
				'Q', reservoir,
		});

		registerMachine(Machine.Composter, new Object[] {
				"PTP",
				"SFS",
				"OCO",
				'P', "sheetPlastic",
				'T', Blocks.FURNACE,
				'S', Blocks.PISTON,
				'F', chassis,
				'O', Items.BRICK,
				'C', redstone
		});

		registerMachine(Machine.Breeder, new Object[] {
				"PTP",
				"SFS",
				"OCO",
				'P', "sheetPlastic",
				'T', Items.GOLDEN_APPLE,
				'S', Items.GOLDEN_CARROT,
				'F', chassis,
				'O', "dyePurple",
				'C', zombieElectrode
		});

		registerMachine(Machine.Grinder, new Object[] {
				"PTP",
				"OFO",
				"SCS",
				'P', "sheetPlastic",
				'T', dsSword,
				'O', Items.BOOK,
				'F', chassis,
				'S', prefix + "ElectricalSteel",
				'C', conduitLiquid
		});

		registerMachine(Machine.AutoEnchanter, new Object[] {
				"PTP",
				"SFS",
				"OCO",
				'P', "sheetPlastic",
				'T', Blocks.OBSIDIAN,
				'S', Items.BOOK,
				'F', chassis,
				'O', "gemDiamond",
				'C', xpRod
		});

		registerMachine(Machine.Chronotyper, new Object[] {
				"PTP",
				"TFT",
				"OCO",
				'P', "sheetPlastic",
				'T', "gemEmerald",
				'F', chassis,
				'O', "dyePurple",
				'C', soulVial
		});

		registerMachine(Machine.Ejector, 8, new Object[] {
				"PPP",
				" T ",
				"OFO",
				'P', "sheetPlastic",
				'T', Blocks.HOPPER,
				'F', chassis,
				'O', redstone
		});

		registerMachine(Machine.ItemRouter, 8, new Object[] {
				"PTP",
				"SFS",
				"PHP",
				'P', "sheetPlastic",
				'T', Blocks.CHEST,
				'S', probe,
				'F', chassis,
				'H', Blocks.HOPPER
		});

		registerMachine(Machine.LiquidRouter, 8, new Object[] {
				"PTP",
				"SFS",
				"PHP",
				'P', "sheetPlastic",
				'T', conduitLiquid,
				'S', probe,
				'F', chassis,
				'H', Blocks.HOPPER
		});

		int dsuCount = MFRConfig.craftSingleDSU.getBoolean(false) ? 1 : 4;
		registerMachine(Machine.DeepStorageUnit, dsuCount, new Object[] {
				"PCP",
				"CFC",
				"PCP",
				'P', "sheetPlastic",
				'C', reinforcedObsidian,
				'F', dimTrans
		});

		if (MFRConfig.enableCheapDSU.getBoolean(false)) {
			registerMachine(Machine.DeepStorageUnit, new Object[] {
					"PCP",
					"CFC",
					"PCP",
					'P', "sheetPlastic",
					'C', vacuumChest,
					'F', chassis
			});
		}

		registerMachine(Machine.LiquiCrafter, new Object[] {
				"PTP",
				"SFS",
				"OCO",
				'P', "sheetPlastic",
				'T', Blocks.CRAFTING_TABLE,
				'S', tank,
				'F', chassis,
				'O', Items.BOOK,
				'C', crafter
		});

		registerMachine(Machine.LavaFabricator, new Object[] {
				"PTP",
				"SFS",
				"OCO",
				'P', "sheetPlastic",
				'T', Blocks.OBSIDIAN,
				'S', Items.MAGMA_CREAM,
				'F', chassis,
				'O', Items.BLAZE_ROD,
				'C', redstone
		});

		registerMachine(Machine.SteamBoiler, new Object[] {
				"PPP",
				"TBT",
				"OOO",
				'P', "sheetPlastic",
				'T', tankPressurized,
				'B', Machine.SludgeBoiler.getItemStack(),
				'O', Blocks.NETHER_BRICK
		});

		registerMachine(Machine.AutoJukebox, new Object[] {
				"PJP",
				"PFP",
				'P', "sheetPlastic",
				'J', Blocks.JUKEBOX,
				'F', chassis
		});

		registerMachine(Machine.Unifier, new Object[] {
				"PTP",
				"OFO",
				"SCS",
				'P', "sheetPlastic",
				'T', probe,
				'O', Items.COMPARATOR,
				'F', chassis,
				'S', prefix + "ElectricalSteel",
				'C', Items.BOOK
		});

		registerMachine(Machine.AutoSpawner, new Object[] {
				"PTP",
				"SFS",
				"OCO",
				'P', "sheetPlastic",
				'T', Items.NETHER_WART,
				'S', Items.MAGMA_CREAM,
				'F', chassis,
				'O', "gemEmerald",
				'C', zombieController
		});

		registerMachine(Machine.BioReactor, new Object[] {
				"PTP",
				"SFS",
				"OCO",
				'P', "sheetPlastic",
				'T', Items.FERMENTED_SPIDER_EYE,
				'S', "slimeball",
				'F', chassis,
				'O', Items.BRICK,
				'C', Items.SUGAR
		});

		registerMachine(Machine.BioFuelGenerator, new Object[] {
				"PTP",
				"SFS",
				"OCO",
				'P', "sheetPlastic",
				'T', Blocks.FURNACE,
				'S', Blocks.PISTON,
				'F', chassis,
				'O', Items.BLAZE_ROD,
				'C', Blocks.PISTON
		});

		registerMachine(Machine.AutoDisenchanter, new Object[] {
				"PTP",
				"SFS",
				"OCO",
				'P', "sheetPlastic",
				'T', Blocks.NETHER_BRICK,
				'S', Items.BOOK,
				'F', chassis,
				'O', "gemDiamond",
				'C', xpObelisk
		});

		registerMachine(Machine.Slaughterhouse, new Object[] {
				"GIG",
				"SFS",
				"XCX",
				'G', "sheetPlastic",
				'S', dsSword,
				'X', dsAxe,
				'I', prefix + "ElectricalSteel",
				'F', chassis,
				'C', redstone
		});

		registerMachine(Machine.MeatPacker, new Object[] {
				"GSG",
				"BFB",
				"BCB",
				'G', "sheetPlastic",
				'B', Blocks.BRICK_BLOCK,
				'S', fireWaterBucket,
				'F', chassis,
				'C', redstone
		});

		registerMachine(Machine.EnchantmentRouter, new Object[] {
				"PBP",
				"SFS",
				"PSP",
				'P', "sheetPlastic",
				'B', Items.BOOK,
				'S', Items.REPEATER,
				'F', chassis
		});

		registerMachine(Machine.LaserDrill, new Object[] {
				"GFG",
				"CFC",
				"DHD",
				'G', "sheetPlastic",
				'D', "gemDiamond",
				'H', "blockGlassHardened",
				'F', light,
				'C', capacitorOctadic
		});

		registerMachine(Machine.LaserDrillPrecharger, new Object[] {
				"GSG",
				"HFH",
				"CDC",
				'G', "sheetPlastic",
				'D', "gemDiamond",
				'S', new ItemStack(pinkSlimeItem, 1, 1),
				'H', "blockGlassHardened",
				'F', light,
				'C', capacitorDouble
		});

		registerMachine(Machine.AutoAnvil, new Object[] {
				"GGG",
				"AFA",
				"OCO",
				'G', "sheetPlastic",
				'A', Blocks.ANVIL,
				'F', chassis,
				'C', darkSteelAnvil,
				'O', prefix + "Iron"
		});

		registerMachine(Machine.BlockSmasher, new Object[] {
				"GPG",
				"HFH",
				"BCB",
				'G', "sheetPlastic",
				'P', Blocks.PISTON,
				'H', factoryHammerItem,
				'B', Items.BOOK,
				'F', chassis,
				'C', redstone
		});

		registerMachine(Machine.RedNote, new Object[] {
				"GNG",
				"CFC",
				'G', "sheetPlastic",
				'C', "cableRedNet",
				'N', Blocks.NOTEBLOCK,
				'F', chassis
		});

		registerMachine(Machine.AutoBrewer, new Object[] {
				"GBG",
				"CFC",
				"RPR",
				'G', "sheetPlastic",
				'C', conduitLiquid,
				'B', Items.BREWING_STAND,
				'R', Items.REPEATER,
				'F', chassis,
				'P', redstone
		});

		registerMachine(Machine.FruitPicker, new Object[] {
				"GXG",
				"SFS",
				"OCO",
				'G', "sheetPlastic",
				'X', dsAxe,
				'S', Items.SHEARS,
				'F', chassis,
				'C', zombieController,
				'O', prefix + "ElectricalSteel"
		});

		registerMachine(Machine.BlockPlacer, new Object[] {
				"GDG",
				"DMD",
				"GSG",
				'G', "sheetPlastic",
				'D', Blocks.DISPENSER,
				'S', redstone,
				'M', chassis,
		});

		registerMachine(Machine.MobCounter, new Object[] {
				"GGG",
				"RCR",
				"SMS",
				'G', "sheetPlastic",
				'R', Items.REPEATER,
				'C', Items.COMPARATOR,
				'S', probe,
				'M', chassis,
		});

		registerMachine(Machine.SteamTurbine, new Object[] {
				"PTP",
				"SFS",
				"OCO",
				'P', "sheetPlastic",
				'T', combustionGen,
				'S', Blocks.PISTON,
				'F', chassis,
				'O', prefix + "ElectricalSteel",
				'C', gear
		});

		registerMachine(Machine.ChunkLoader, new Object[] {
				"PEP",
				"TET",
				"OCO",
				'P', "sheetPlastic",
				'T', dimTrans,
				'E', capBank,
				'F', capacitorOctadic,
				'O', prefix + "EnergeticAlloy",
				'C', capacitorOctadic
		});
		if (MFRConfig.enableCheapCL.getBoolean(false)) {
			registerMachine(Machine.ChunkLoader, new Object[] {
					"PEP",
					"FDF",
					"OCO",
					'P', "sheetPlastic",
					'D', dimTrans,
					'E', capBank,
					'F', chassis,
					'O', prefix + "EnergeticAlloy",
					'C', capacitorOctadic
			});
		}

		registerMachine(Machine.Fountain, new Object[] {
				"PBP",
				"TFT",
				"OCO",
				'P', "sheetPlastic",
				'B', Blocks.IRON_BARS,
				'T', tank,
				'F', chassis,
				'O', prefix + "ElectricalSteel",
				'C', redstone
		});

		registerMachine(Machine.MobRouter, new Object[] {
				"PPP",
				"BRB",
				"OCO",
				'P', "sheetPlastic",
				'B', Blocks.IRON_BARS,
				'R', Machine.ItemRouter.getItemStack(),
				'O', "dyeOrange",
				'C', Machine.Chronotyper.getItemStack(),
		});

		RecipeHelper.addShapedRecipe(stack(plasticTank, 1), new Object[] {
				"PPP",
				"P P",
				"PMP",
				'P', "sheetPlastic",
				'M', machineBaseItem,
		});
	}

	@Override
	protected void registerMiscItems() {

		String prefix = "ingot";

		RecipeHelper.addShapedOreRecipe(new ItemStack(fertilizerItem, 16), new Object[] {
				"WBW",
				"STS",
				"WBW",
				'W', Items.WHEAT,
				'B', new ItemStack(Items.DYE, 1, 15),
				'S', Items.STRING,
				'T', "stickWood",
		});

		RecipeHelper.addShapedOreRecipe(new ItemStack(spyglassItem), new Object[] {
				"GLG",
				"PLP",
				" S ",
				'G', "ingotGold",
				'L', "blockGlass",
				'P', "sheetPlastic",
				'S', "stickWood",
		});

		if (MFRConfig.enablePortaSpawner.getBoolean(true))
			RecipeHelper.addShapedOreRecipe(new ItemStack(portaSpawnerItem), new Object[] {
					"GLG",
					"DND",
					"GLG",
					'G', prefix + "ElectricalSteel",
					'L', "blockGlass",
					'D', "ingotVibrantAlloy",
					'N', Items.NETHER_STAR
			});

		RecipeHelper.addShapedOreRecipe(new ItemStack(detCordBlock, 12), new Object[] {
				"PPP",
				"PTP",
				"PPP",
				'P', "itemRubber",
				'T', Blocks.TNT,
		});

		RecipeHelper.addShapedOreRecipe(new ItemStack(fishingRodItem, 1), new Object[] {
				"DD ",
				"DFD",
				"TDD",
				'D', "wireExplosive",
				'F', Items.FISHING_ROD,
				'T', Blocks.REDSTONE_TORCH
		});
	}

	@Override
	protected void registerRedNet() {

		RecipeHelper.addShapedOreRecipe(new ItemStack(rednetCableBlock, 8), new Object[] {
				"PPP",
				"RRR",
				"PPP",
				'R', redstone,
				'P', "sheetPlastic",
		});

		@Nonnull ItemStack pipe = stack(plasticPipeBlock);
		RecipeHelper.addShapelessRecipe(stack(rednetCableBlock, 5), pipe, pipe, pipe, pipe, pipe, redstone, redstone);

		RecipeHelper.addShapelessOreRecipe(new ItemStack(rednetCableBlock, 1, 2), new Object[] {
				"nuggetGold",
				"nuggetGold",
				"nuggetGold",
				redstone,
				redstone,
				new ItemStack(rednetCableBlock),
		});

		RecipeHelper.addShapelessOreRecipe(new ItemStack(rednetCableBlock, 6, 2), new Object[] {
				"ingotGold",
				"ingotGold",
				Blocks.REDSTONE_BLOCK,
				new ItemStack(rednetCableBlock),
				new ItemStack(rednetCableBlock),
				new ItemStack(rednetCableBlock),
				new ItemStack(rednetCableBlock),
				new ItemStack(rednetCableBlock),
				new ItemStack(rednetCableBlock),
		});

		RecipeHelper.addShapedOreRecipe(new ItemStack(machineBlock, 1, 1), new Object[] {
				"PRP",
				"RGR",
				"PIP",
				'R', redstone,
				'P', "sheetPlastic",
				'G', "blockGlass",
				'I', "ingotIron",
		});

		RecipeHelper.addShapedOreRecipe(new ItemStack(rednetLogicBlock), new Object[] {
				"RDR",
				"LGL",
				"PHP",
				'H', new ItemStack(machineBlock, 1, 1),
				'P', "sheetPlastic",
				'G', "ingotGold",
				'L', "gemLapis",
				'D', "gemDiamond",
				'R', redstone,
		});

		RecipeHelper.addShapedOreRecipe(new ItemStack(logicCardItem, 1, 0), new Object[] {
				"RPR",
				"PGP",
				"RPR",
				'P', "sheetPlastic",
				'G', "ingotGold",
				'R', redstone,
		});

		RecipeHelper.addShapedOreRecipe(new ItemStack(logicCardItem, 1, 1), new Object[] {
				"GPG",
				"PCP",
				"RGR",
				'C', new ItemStack(logicCardItem, 1, 0),
				'P', "sheetPlastic",
				'G', "ingotGold",
				'R', redstone,
		});

		RecipeHelper.addShapedOreRecipe(new ItemStack(logicCardItem, 1, 2), new Object[] {
				"DPD",
				"RCR",
				"GDG",
				'C', new ItemStack(logicCardItem, 1, 1),
				'P', "sheetPlastic",
				'G', "ingotGold",
				'D', "gemDiamond",
				'R', redstone,
		});

		RecipeHelper.addShapedOreRecipe(new ItemStack(rednetMeterItem, 1, 0), new Object[] {
				" G",
				"PR",
				"PP",
				'P', "sheetPlastic",
				'G', "nuggetGold",
				'R', redstone,
		});

		RecipeHelper.addShapedOreRecipe(new ItemStack(rednetMeterItem, 1, 1), new Object[] {
				"RGR",
				"IMI",
				"PPP",
				'P', "sheetPlastic",
				'G', capacitorBasic,
				'I', "ingotCopper",
				'R', redstone,
				'M', new ItemStack(rednetMeterItem, 1, 0)
		});

		RecipeHelper.addShapedOreRecipe(new ItemStack(rednetMemoryCardItem, 1, 0), new Object[] {
				"GGG",
				"PRP",
				"PPP",
				'P', "sheetPlastic",
				'G', "nuggetGold",
				'R', redstone,
		});

		RecipeHelper.addShapedOreRecipe(new ItemStack(rednetPanelBlock, 1, 0), new Object[] {
				"PCP",
				"PBP",
				"KPK",
				'P', "sheetPlastic",
				'C', rednetCableBlock,
				'B', Blocks.BOOKSHELF,
				'K', new ItemStack(Items.DYE, 1, 0)
		});

		RecipeHelper.addShapelessRecipe(new ItemStack(rednetMemoryCardItem, 1, 0), new ItemStack(rednetMemoryCardItem, 1, 0));
	}
}
		*/
