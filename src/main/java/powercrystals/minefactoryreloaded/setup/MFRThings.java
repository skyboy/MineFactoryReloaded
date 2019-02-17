package powercrystals.minefactoryreloaded.setup;

import cofh.core.util.core.IInitializer;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import powercrystals.minefactoryreloaded.MFRProps;
import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.block.*;
import powercrystals.minefactoryreloaded.block.decor.*;
import powercrystals.minefactoryreloaded.block.fluid.BlockTank;
import powercrystals.minefactoryreloaded.block.transport.*;
import powercrystals.minefactoryreloaded.entity.DebugTracker;
import powercrystals.minefactoryreloaded.entity.EntityPinkSlime;
import powercrystals.minefactoryreloaded.item.*;
import powercrystals.minefactoryreloaded.item.base.*;
import powercrystals.minefactoryreloaded.item.gun.ItemNeedleGun;
import powercrystals.minefactoryreloaded.item.gun.ItemPotatoCannon;
import powercrystals.minefactoryreloaded.item.gun.ItemRocketLauncher;
import powercrystals.minefactoryreloaded.item.gun.ItemSafariNetLauncher;
import powercrystals.minefactoryreloaded.item.gun.ammo.*;
import powercrystals.minefactoryreloaded.item.syringe.*;
import powercrystals.minefactoryreloaded.item.tool.*;

import javax.annotation.Nonnull;
import java.util.ArrayList;

import static net.minecraft.init.Items.MILK_BUCKET;
import static net.minecraft.init.Items.POTIONITEM;
import static net.minecraft.init.Items.WATER_BUCKET;
import static net.minecraftforge.oredict.OreDictionary.registerOre;
import static powercrystals.minefactoryreloaded.api.integration.IMFRRecipeSet.stack;
import static powercrystals.minefactoryreloaded.api.integration.IMFRRecipeSet.stack_wildcard;

public class MFRThings {

	public static TIntObjectMap<Block> machineBlocks = new TIntObjectHashMap<>();

	private static ArrayList<IInitializer> initList = new ArrayList<>();

	public static void registerInitializer(IInitializer init) {

		initList.add(init);
	}

	public static Block conveyorBlock;

	public static Block factoryGlassBlock;
	public static Block factoryGlassPaneBlock;
	public static Block factoryRoadBlock;
	public static Block factoryPlasticBlock;
	public static Block factoryDecorativeBrickBlock;
	public static Item factoryDecorativeBrickItem;
	public static Block factoryDecorativeStoneBlock;
	public static Block pinkSlimeBlock;

	public static Block rubberWoodBlock;
	public static Block rubberLeavesBlock;
	public static BlockRubberSapling rubberSaplingBlock;

	public static Item rubberWoodItem;
	public static Item rubberLeavesItem;
	public static Item rubberSaplingItem;

	public static Block railPickupCargoBlock;
	public static Block railDropoffCargoBlock;
	public static Block railPickupPassengerBlock;
	public static Block railDropoffPassengerBlock;

	public static BlockRedNetCable rednetCableBlock;
	public static BlockPlasticPipe plasticPipeBlock;

	public static BlockRedNetLogic rednetLogicBlock;
	public static BlockRedNetPanel rednetPanelBlock;

	public static Block plasticTank;
	public static Item plasticTankItem;

	public static Block fakeLaserBlock;

	public static Block vineScaffoldBlock;

	public static Block detCordBlock;

	public static Block fertileSoil;

	public static Block machineBlock;
	public static Item machineBaseItem;

	public static Item factoryHammerItem;
	public static Item fertilizerItem;
	public static Item plasticSheetItem;
	public static Item rubberBarItem;
	public static Item rawPlasticItem;
	public static Item syringeEmptyItem;
	public static Item syringeHealthItem;
	public static Item syringeGrowthItem;
	public static Item rawRubberItem;
	public static Item safariNetItem;
	public static ItemMulti ceramicDyeItem;
	public static Item blankRecordItem;
	public static Item syringeZombieItem;
	public static Item safariNetSingleItem;
	public static ItemMulti upgradeItem;
	public static Item safariNetLauncherItem;
	public static Item sugarCharcoalItem;
	public static Item milkBottleItem;
	public static Item spyglassItem;
	public static Item portaSpawnerItem;
	public static Item strawItem;
	public static Item xpExtractorItem;
	public static Item syringeSlimeItem;
	public static Item syringeCureItem;
	public static Item logicCardItem;
	public static Item rednetMeterItem;
	public static Item rednetMemoryCardItem;
	public static Item rulerItem;
	public static Item meatIngotRawItem;
	public static Item meatIngotCookedItem;
	public static Item meatNuggetRawItem;
	public static Item meatNuggetCookedItem;
	public static Item pinkSlimeItem;
	public static Item safariNetJailerItem;
	public static ItemMulti laserFocusItem;
	public static Item needlegunItem;
	public static Item needlegunAmmoEmptyItem;
	public static Item needlegunAmmoStandardItem;
	public static Item needlegunAmmoPierceItem;
	public static Item needlegunAmmoLavaItem;
	public static Item needlegunAmmoSludgeItem;
	public static Item needlegunAmmoSewageItem;
	public static Item needlegunAmmoFireItem;
	public static Item needlegunAmmoAnvilItem;
	public static Item rocketLauncherItem;
	public static Item rocketItem;
	public static ItemFactoryCup plasticCupItem;
	public static Item plasticCellItem;
	public static Item fishingRodItem;
	public static Item plasticBagItem;
	public static ItemFactoryArmor plasticGlasses;
	public static ItemFactoryArmor plasticHelmetItem;
	public static ItemFactoryArmor plasticChestplateItem;
	public static ItemFactoryArmor plasticLeggingsItem;
	public static ItemFactoryArmor plasticBootsItem;
	public static Item safariNetFancyJailerItem;
	public static Item potatoLauncherItem;

	public static void preInit() {

		MFRThings.machineBlocks.put(0, new BlockFactoryMachine(0));
		MFRThings.machineBlocks.put(1, new BlockFactoryMachine(1));
		MFRThings.machineBlocks.put(2, new BlockFactoryMachine(2));

		conveyorBlock = new BlockConveyor();
		factoryGlassBlock = new BlockFactoryGlass();
		factoryGlassPaneBlock = new BlockFactoryGlassPane();
		factoryRoadBlock = new BlockFactoryRoad();
		factoryPlasticBlock = new BlockFactoryPlastic();
		factoryDecorativeBrickBlock = new BlockDecorativeBricks();
		factoryDecorativeStoneBlock = new BlockDecorativeStone();
		pinkSlimeBlock = new BlockPinkSlime();
		rubberWoodBlock = new BlockRubberWood();
		rubberLeavesBlock = new BlockRubberLeaves();
		rubberSaplingBlock = new BlockRubberSapling();
		railDropoffCargoBlock = new BlockRailCargoDropoff();
		railPickupCargoBlock = new BlockRailCargoPickup();
		railDropoffPassengerBlock = new BlockRailPassengerDropoff();
		railPickupPassengerBlock = new BlockRailPassengerPickup();
		rednetCableBlock = new BlockRedNetCable();
		rednetLogicBlock = new BlockRedNetLogic();
		rednetPanelBlock = new BlockRedNetPanel();
		fakeLaserBlock = new BlockFakeLaser();
		vineScaffoldBlock = new BlockVineScaffold();
		detCordBlock = new BlockDetCord();
		plasticPipeBlock = new BlockPlasticPipe();
		fertileSoil = new BlockFertileSoil();
		machineBlock = new BlockFactoryDecoration();
		plasticTank = new BlockTank();

		factoryHammerItem = new ItemFactoryHammer();
		plasticHelmetItem = new ItemFactoryArmor(ItemFactoryArmor.PLASTIC_ARMOR, EntityEquipmentSlot.HEAD)
				.setModelLocation("armor", "type=helm");
		plasticChestplateItem = new ItemFactoryArmor(ItemFactoryArmor.PLASTIC_ARMOR, EntityEquipmentSlot.CHEST)
				.setModelLocation("armor", "type=chest");
		plasticLeggingsItem = new ItemFactoryArmor(ItemFactoryArmor.PLASTIC_ARMOR, EntityEquipmentSlot.LEGS)
				.setModelLocation("armor", "type=legs");
		plasticBootsItem = new ItemPlasticBoots();
		plasticGlasses = new ItemFactoryArmor(ItemFactoryArmor.GLASS_ARMOR, EntityEquipmentSlot.HEAD)
				.setModelLocation("armor", "type=glass_helm");
		{
			int i = MFRConfig.armorStacks.getBoolean(false) ? 4 : 1;
			plasticHelmetItem.setRepairIngot("itemPlastic").setUnlocalizedName("mfr.plastic.armor.helm").setMaxStackSize(i);
			plasticChestplateItem.setRepairIngot("itemPlastic").setUnlocalizedName("mfr.plastic.armor.chest").setMaxStackSize(i);
			plasticLeggingsItem.setRepairIngot("itemPlastic").setUnlocalizedName("mfr.plastic.armor.legs").setMaxStackSize(i);
			plasticBootsItem.setRepairIngot("itemPlastic").setMaxStackSize(i);
			plasticGlasses.setRepairIngot("itemPlastic").setUnlocalizedName("mfr.glass.armor.helm");
		}

		rawRubberItem = (new ItemFactory() {

			@Override
			public int getItemBurnTime(ItemStack stack) {

				return 30;
			}
		}).setModelLocation("material", "type=rubber_raw").setUnlocalizedName("mfr.rubber.raw");
		rubberBarItem = (new ItemFactory() {

			@Override
			public int getItemBurnTime(ItemStack stack) {

				return 90;
			}
		}).setModelLocation("material", "type=rubber_bar").setUnlocalizedName("mfr.rubber.bar");

		rawPlasticItem = (new ItemFactory()).setModelLocation("material", "type=plastic_raw")
				.setUnlocalizedName("mfr.plastic.raw");
		plasticSheetItem = (new ItemFactory()).setModelLocation("material", "type=plastic_sheet")
				.setUnlocalizedName("mfr.plastic.sheet").setMaxStackSize(96);

		upgradeItem = new ItemUpgrade();

		rednetMeterItem = new ItemRedNetMeter();
		rednetMemoryCardItem = new ItemRedNetMemoryCard();
		logicCardItem = new ItemLogicUpgradeCard();

		float meatNuggetSaturation = MFRConfig.meatSaturation.getBoolean(false) ? 0.1F : 0.2F;
		float meatIngotSaturation = MFRConfig.meatSaturation.getBoolean(false) ? 0.2F : 0.8F;
		meatIngotRawItem = (new ItemFactoryFood(4, meatIngotSaturation))
				.setModelLocation("food", "variant=meat_ingot_raw").setUnlocalizedName("mfr.meat.ingot.raw");
		meatIngotCookedItem = (new ItemFactoryFood(10, meatIngotSaturation))
				.setModelLocation("food", "variant=meat_ingot_cooked").setUnlocalizedName("mfr.meat.ingot.cooked");
		meatNuggetRawItem = (new ItemFactoryFood(1, meatNuggetSaturation))
				.setModelLocation("food", "variant=meat_nugget_raw").setUnlocalizedName("mfr.meat.nugget.raw");
		meatNuggetCookedItem = (new ItemFactoryFood(4, meatNuggetSaturation))
				.setModelLocation("food", "variant=meat_nugget_cooked").setUnlocalizedName("mfr.meat.nugget.cooked");
		pinkSlimeItem = new ItemPinkSlime();

		if (MFRConfig.enableLiquidSyringe.getBoolean(true))
			syringeEmptyItem = new ItemSyringeLiquid();
		else
			syringeEmptyItem = (new ItemFactory()).setModelLocation("syringe", "variant=empty")
					.setUnlocalizedName("mfr.syringe.empty").setRegistryName(MFRProps.MOD_ID, "syringe_empty");
		syringeHealthItem = new ItemSyringeHealth();
		syringeGrowthItem = new ItemSyringeGrowth();
		syringeZombieItem = new ItemSyringeZombie();
		syringeSlimeItem = new ItemSyringeSlime();
		syringeCureItem = new ItemSyringeCure();

		safariNetLauncherItem = new ItemSafariNetLauncher();
		safariNetItem = (new ItemSafariNet(0, true)).setModelLocation("safari_net", "reusable")
				.setUnlocalizedName("mfr.safarinet.reusable");
		safariNetSingleItem = (new ItemSafariNet(0)).setModelLocation("safari_net", "single_use")
				.setUnlocalizedName("mfr.safarinet.single_use");
		safariNetJailerItem = (new ItemSafariNet(1)).setModelLocation("safari_net", "jailer")
				.setUnlocalizedName("mfr.safarinet.jailer");
		safariNetFancyJailerItem = (new ItemSafariNet(3)).setModelLocation("safari_net", "jailer_fancy")
				.setUnlocalizedName("mfr.safarinet.jailer.fancy");

		portaSpawnerItem = new ItemPortaSpawner();

		xpExtractorItem = new ItemXpExtractor();
		strawItem = new ItemStraw();
		milkBottleItem = new ItemMilkBottle();
		plasticCupItem = new ItemFactoryCup(24, 16);

		/*
		CarbonContainer.cell = new CarbonContainer(MFRConfig.plasticCellItemId.getInt(), 64, "mfr.bucket.plasticcell", false);
		CarbonContainer.cell.setFilledItem(CarbonContainer.cell).setEmptyItem(CarbonContainer.cell);

		MinecraftForge.EVENT_BUS.register(new LiquidRegistry(_configFolder,
				Loader.instance().activeModContainer()));//*/
		//plasticCellItem = CarbonContainer.cell;
		plasticBagItem = new ItemFactoryBag();

		sugarCharcoalItem = (new ItemFactory() {

			@Override
			public int getItemBurnTime(ItemStack stack) {

				return 400;
			}
		}).setModelLocation("material", "type=sugar_charcoal")
				.setUnlocalizedName("mfr.sugar_charcoal");
		fertilizerItem = (new ItemFactory()).setModelLocation("material", "type=fertilizer").setUnlocalizedName("mfr.fertilizer");

		ceramicDyeItem = new ItemCeramicDye();
		(laserFocusItem = new ItemFactoryColored()).setModelLocation("laser_focus", "").setUnlocalizedName("mfr.laser.focus")
				.setMaxStackSize(1);

		blankRecordItem = (new ItemFactory()).setModelLocation("material", "type=blank_record")
				.setUnlocalizedName("mfr.record.blank").setMaxStackSize(1);
		spyglassItem = new ItemSpyglass();
		rulerItem = new ItemRuler();
		fishingRodItem = new ItemFishingRod();

		potatoLauncherItem = new ItemPotatoCannon();

		needlegunItem = new ItemNeedleGun();
		needlegunAmmoEmptyItem = (new ItemFactory()).setModelLocation("needle_gun_ammo", "variant=empty")
				.setUnlocalizedName("mfr.needlegun.ammo.empty");
		needlegunAmmoStandardItem = (new ItemNeedlegunAmmoStandard())
				.setModelLocation("needle_gun_ammo", "variant=standard").setUnlocalizedName("mfr.needlegun.ammo.standard");
		needlegunAmmoPierceItem = (new ItemNeedlegunAmmoStandard(16, 2f, 8))
				.setModelLocation("needle_gun_ammo", "variant=pierce").setUnlocalizedName("mfr.needlegun.ammo.pierce");
		needlegunAmmoLavaItem = (new ItemNeedlegunAmmoBlock(Blocks.FLOWING_LAVA.getDefaultState(), 3))
				.setModelLocation("needle_gun_ammo", "variant=lava").setUnlocalizedName("mfr.needlegun.ammo.lava");
		needlegunAmmoSludgeItem = (new ItemNeedlegunAmmoBlock(MFRFluids.sludgeLiquid.getDefaultState(), 6))
				.setModelLocation("needle_gun_ammo", "variant=sludge").setUnlocalizedName("mfr.needlegun.ammo.sludge");
		needlegunAmmoSewageItem = (new ItemNeedlegunAmmoBlock(MFRFluids.sewageLiquid.getDefaultState(), 6))
				.setModelLocation("needle_gun_ammo", "variant=sewage").setUnlocalizedName("mfr.needlegun.ammo.sewage");
		needlegunAmmoFireItem = (new ItemNeedlegunAmmoFire()).setUnlocalizedName("mfr.needlegun.ammo.fire");
		needlegunAmmoAnvilItem = (new ItemNeedlegunAmmoAnvil()).setUnlocalizedName("mfr.needlegun.ammo.anvil");

		rocketLauncherItem = new ItemRocketLauncher();
		rocketItem = new ItemRocket();

		EntityRegistry
				.registerModEntity(new ResourceLocation(MFRProps.MOD_ID, "pink_slime"), EntityPinkSlime.class, "mfrEntityPinkSlime", 1, MineFactoryReloadedCore.instance(), 160, 5,
						true);
		LootTableList.register(EntityPinkSlime.PINK_SLIME);

		EntityRegistry
				.registerModEntity(new ResourceLocation(MFRProps.MOD_ID, "debug_tracker"), DebugTracker.class, "DebugTracker", 99, MineFactoryReloadedCore.instance(), 250, 10, true);


		for (IInitializer init : initList) {
			init.initialize();
		}

		machineBaseItem = MFRRegistry.getItemBlock(machineBlock);
		plasticTankItem = MFRRegistry.getItemBlock(plasticTank);

		rubberSaplingItem = MFRRegistry.getItemBlock(rubberSaplingBlock);
		rubberWoodItem = MFRRegistry.getItemBlock(rubberWoodBlock);
		rubberLeavesItem = MFRRegistry.getItemBlock(rubberLeavesBlock);

		factoryDecorativeBrickItem = MFRRegistry.getItemBlock(factoryDecorativeBrickBlock);
	}

	public static void initialize() {

		for (IInitializer init : initList) {
			//init.initialize();
		}
	}

	public static void registerOredict() {

		registerOre("itemRubber", rubberBarItem);
		registerOre("itemRawRubber", rawRubberItem);
		registerOre("woodRubber", rubberWoodBlock);
		registerOre("logWood", rubberWoodBlock);
		registerOre("leavesRubber", rubberLeavesBlock);
		registerOre("treeLeaves", rubberLeavesBlock);
		registerOre("treeSapling", rubberSaplingBlock);
		registerOre("blockPlastic", stack_wildcard(factoryPlasticBlock));
		registerOre("sheetPlastic", plasticSheetItem);
		registerOre("itemPlastic", plasticSheetItem);
		registerOre("dustPlastic", rawPlasticItem);
		registerOre("itemPlastic", rawPlasticItem);
		registerOre("ingotMeat", meatIngotCookedItem);
		registerOre("ingotMeatRaw", meatIngotRawItem);
		registerOre("nuggetMeat", meatNuggetCookedItem);
		registerOre("nuggetMeatRaw", meatNuggetRawItem);
		registerOre("blockMeat", stack(factoryDecorativeBrickBlock, 1, 13));
		registerOre("blockMeatRaw", stack(factoryDecorativeBrickBlock, 1, 12));
		registerOre("itemCharcoalSugar", sugarCharcoalItem);
		registerOre("blockCharcoalSugar", stack(factoryDecorativeBrickBlock, 1, 15));
		registerOre("cableRedNet", stack(rednetCableBlock, 1, 0));
		registerOre("cableRedNet", stack(rednetCableBlock, 1, 1));
		registerOre("cableRedNetEnergy", stack(rednetCableBlock, 1, 2));
		registerOre("cableRedNetEnergy", stack(rednetCableBlock, 1, 3));
		registerOre("slimeballPink", stack(pinkSlimeItem, 1, 0));
		registerOre("slimeball", stack(pinkSlimeItem, 1, 0));
		registerOre("blockSlimePink", stack(pinkSlimeBlock));
		registerOre("blockSlime", stack(pinkSlimeBlock));
		registerOre("fertilizerOrganic", fertilizerItem);
		registerOre("fertilizer", fertilizerItem);
		registerOre("dyeBrown", fertilizerItem);
		registerOre("wireExplosive", detCordBlock);
		registerOre("listAllmilk", milkBottleItem);
		registerOre("listAllmeatraw", meatIngotRawItem);
		registerOre("listAllmeatcooked", meatIngotCookedItem);

		{ // GLASS:
			String[] DYES = { "Black", "Red", "Green", "Brown",
					"Blue", "Purple", "Cyan", "LightGray", "Gray", "Pink", "Lime",
					"Yellow", "LightBlue", "Magenta", "Orange", "White" };

			String pane = "paneGlass", glass = "blockGlass";
			@Nonnull
			ItemStack glassStack = stack_wildcard(factoryGlassBlock, 1);
			@Nonnull ItemStack paneStack = stack_wildcard(factoryGlassPaneBlock, 1);
			registerOre(glass, glassStack.copy());
			registerOre(pane, paneStack.copy());

			for (int i = 0; i < 16; i++) {
				@Nonnull ItemStack ceramicDye = stack(ceramicDyeItem, 1, i);
				glassStack = stack(factoryGlassBlock, 1, i);
				paneStack = stack(factoryGlassPaneBlock, 1, i);
				String dye = DYES[15 - i];
				String dye2 = "dyeCeramic" + dye;
				registerOre(glass + dye, glassStack.copy());
				registerOre(pane + dye, paneStack.copy());
				registerOre(dye2, ceramicDye.copy());
			}
		}

		registerOre("stone", stack(factoryDecorativeStoneBlock, 1, 0));
		registerOre("stone", stack(factoryDecorativeStoneBlock, 1, 1));
		registerOre("cobblestone", stack(factoryDecorativeStoneBlock, 1, 2));
		registerOre("cobblestone", stack(factoryDecorativeStoneBlock, 1, 3));

		// vanilla items
		registerOre("listAllmilk", MILK_BUCKET);
		registerOre("listAllwater", WATER_BUCKET);
		registerOre("listAllwater", stack(POTIONITEM, 1, 0));
	}

}
