
package powercrystals.minefactoryreloaded.setup.recipe;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fml.common.registry.GameRegistry;
import powercrystals.minefactoryreloaded.api.integration.IMFRRecipeSet;
import powercrystals.minefactoryreloaded.core.MFRUtil;

import static net.minecraft.init.Blocks.*;
import static net.minecraft.init.Items.*;

@IMFRRecipeSet.DependsOn("thermalexpansion")
public class ThermalExpansion implements IMFRRecipeSet {

	//region materials

	//region power
	@GameRegistry.ItemStackHolder(value = "thermalfoundation:material", meta = 512)
	public static final ItemStack redstoneServo = ItemStack.EMPTY;
	@GameRegistry.ItemStackHolder(value = "thermalfoundation:material", meta = 513)
	public static final ItemStack powerCoilGold = ItemStack.EMPTY;
	@GameRegistry.ItemStackHolder(value = "thermalfoundation:material", meta = 514)
	public static final ItemStack powerCoilSilver = ItemStack.EMPTY;
	@GameRegistry.ItemStackHolder(value = "thermalfoundation:material", meta = 515)
	public static final ItemStack powerCoilElectrum = ItemStack.EMPTY;
	//endregion

	//region parts
	@GameRegistry.ItemStackHolder(value = "thermalfoundation:material", meta = 657)
	public static final ItemStack sawblade = ItemStack.EMPTY;
	@GameRegistry.ItemStackHolder(value = "thermalfoundation:material", meta = 656)
	public static final ItemStack drillHead = ItemStack.EMPTY;
	//endregion

	//region frames
	@GameRegistry.ItemStackHolder(value = "thermalexpansion:frame", meta = 0)
	public static final ItemStack machineFrame = ItemStack.EMPTY;
	@GameRegistry.ItemStackHolder(value = "thermalexpansion:frame", meta = 64)
	public static final ItemStack deviceFrame = ItemStack.EMPTY;
	//endregion

	//region components
	@GameRegistry.ItemStackHolder(value = "thermalexpansion:tank")
	public static final ItemStack portable_tank = ItemStack.EMPTY;
	@GameRegistry.ItemStackHolder(value = "thermalexpansion:strongbox")
	public static final ItemStack strongbox = ItemStack.EMPTY;
	@GameRegistry.ItemStackHolder(value = "thermalexpansion:cell")
	public static final ItemStack energy_cell = ItemStack.EMPTY;
	@GameRegistry.ItemStackHolder("minecraft:glowstone") // TODO: glowstone illuminators
	public static final ItemStack lamp = ItemStack.EMPTY;
	//endregion

	//region tools
	@GameRegistry.ItemStackHolder(value = "thermalfoundation:tool.axe_invar")
	public static final ItemStack invarAxe = ItemStack.EMPTY;
	@GameRegistry.ItemStackHolder(value = "thermalfoundation:tool.pickaxe_invar")
	public static final ItemStack invarPickaxe = ItemStack.EMPTY;
	@GameRegistry.ItemStackHolder(value = "thermalfoundation:tool.shovel_invar")
	public static final ItemStack invarShovel = ItemStack.EMPTY;
	@GameRegistry.ItemStackHolder(value = "thermalfoundation:tool.sword_invar")
	public static final ItemStack invarSword = ItemStack.EMPTY;
	//endregion

	//region other
	@GameRegistry.ItemStackHolder("thermalfoundation:tome_lexicon")
	public static final ItemStack lexicon = ItemStack.EMPTY;
	//endregion

	//endregion

	@Override
	public final void registerRecipes() {

		registerMachines();
		registerMiscItems();
		registerRedNet();
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
	private final IRecipeHolder DeepStorageUnit = IRecipeHolder.EMPTY;
	private final IRecipeHolder cheap_DeepStorageUnit = IRecipeHolder.EMPTY;
	private final IRecipeHolder ChunkLoader = IRecipeHolder.EMPTY;
	private final IRecipeHolder cheap_ChunkLoader = IRecipeHolder.EMPTY;

	//ingredients
	private final IRecipeHolder syringe_empty = IRecipeHolder.EMPTY;
	private final IRecipeHolder pinkslime_gem = IRecipeHolder.EMPTY;
	private final IRecipeHolder plastic_pipe = IRecipeHolder.EMPTY;
	private final IRecipeHolder plastic_tank = IRecipeHolder.EMPTY;
	private final IRecipeHolder hammer = IRecipeHolder.EMPTY;

	private void registerMachines() {

		final String prefix = "ingot";

		Planter.addShaped(
				"PTP",
				"SFS",
				"OCO",
				'P', "sheetPlastic",
				'T', Items.FLOWER_POT,
				'S', PISTON,
				'F', machineFrame,
				'O', "gearCopper",
				'C', powerCoilGold
		);

		Fisher.addShaped(
				"PTP",
				"SFS",
				"OCO",
				'P', "sheetPlastic",
				'T', FISHING_ROD,
				'S', BUCKET,
				'F', machineFrame,
				'O', "plateIron",
				'C', powerCoilGold
		);

		Harvester.addShaped(
				"PSP",
				"TFT",
				"OCO",
				'P', "sheetPlastic",
				'S', sawblade,
				'T', invarAxe,
				'F', machineFrame,
				'O', "gearConstantan",
				'C', powerCoilGold
		);

		Rancher.addShaped(
				"PTP",
				"SFS",
				"OCO",
				'P', "sheetPlastic",
				'T', plastic_pipe,
				'S', SHEARS,
				'F', machineFrame,
				'O', "plateTin",
				'C', powerCoilGold
		);

		Fertilizer.addShaped(
				"PTP",
				"SFS",
				"OCO",
				'P', "sheetPlastic",
				'T', GLASS_BOTTLE,
				'S', LEATHER,
				'F', machineFrame,
				'O', "plateNickel",
				'C', powerCoilGold
		);

		Vet.addShaped(
				"PTP",
				"TFT",
				"OCO",
				'P', "sheetPlastic",
				'T', syringe_empty,
				'F', machineFrame,
				'O', "plateCopper",
				'C', powerCoilGold
		);

		ItemCollector.addShaped(
				"P P",
				" F ",
				"PCP",
				'P', "sheetPlastic",
				'F', deviceFrame,
				'C', CHEST
		);

		BlockBreaker.addShaped(
				"PTP",
				"SFA",
				"OCO",
				'P', "sheetPlastic",
				'T', drillHead,
				'S', invarPickaxe,
				'F', machineFrame,
				'A', invarShovel,
				'O', "plateIron",
				'C', powerCoilGold
		);

		WeatherCollector.addShaped(
				"PBP",
				"TFT",
				"OCO",
				'P', "sheetPlastic",
				'B', IRON_BARS,
				'T', BUCKET,
				'F', machineFrame,
				'O', prefix + "Copper",
				'C', powerCoilGold
		);

		SludgeBoiler.addShaped(
				"PTP",
				"SFS",
				"OCO",
				'P', "sheetPlastic",
				'T', BUCKET,
				'S', FURNACE,
				'F', machineFrame,
				'O', prefix + "Iron",
				'C', powerCoilGold
		);

		Sewer.addShaped(
				"PTP",
				"SFS",
				"SQS",
				'P', "sheetPlastic",
				'T', BUCKET,
				'S', BRICK,
				'F', deviceFrame,
				'Q', redstoneServo
		);

		Composter.addShaped(
				"PTP",
				"SFS",
				"OCO",
				'P', "sheetPlastic",
				'T', FURNACE,
				'S', PISTON,
				'F', machineFrame,
				'O', BRICK,
				'C', powerCoilGold
		);

		Breeder.addShaped(
				"PTP",
				"SFS",
				"OCO",
				'P', "sheetPlastic",
				'T', GOLDEN_APPLE,
				'S', GOLDEN_CARROT,
				'F', machineFrame,
				'O', "dyePurple",
				'C', powerCoilGold
		);

		Grinder.addShaped(
				"POP",
				"TFT",
				"SCS",
				'P', "sheetPlastic",
				'O', BOOK,
				'T', invarSword,
				'F', machineFrame,
				'S', "gearSteel",
				'C', powerCoilGold
		);

		AutoEnchanter.addShaped(
				"PTP",
				"SFS",
				"OCO",
				'P', "sheetPlastic",
				'T', OBSIDIAN,
				'S', BOOK,
				'F', machineFrame,
				'O', "gemDiamond",
				'C', powerCoilGold
		);

		Chronotyper.addShaped(
				"PTP",
				"TFT",
				"OCO",
				'P', "sheetPlastic",
				'T', "gemEmerald",
				'F', machineFrame,
				'O', "dyePurple",
				'C', powerCoilGold
		);

		Ejector.addShaped(
				"PFP",
				"OTO",
				'P', "sheetPlastic",
				'F', deviceFrame,
				'O', "dustRedstone",
				'T', redstoneServo
		);

		ItemRouter.addShaped(
				"PTP",
				"SFS",
				"PSP",
				'P', "sheetPlastic",
				'T', CHEST,
				'S', rednet_multimeter,
				'F', deviceFrame
		);

		LiquidRouter.addShaped(
				"PTP",
				"SFS",
				"PSP",
				'P', "sheetPlastic",
				'T', plastic_pipe,
				'S', rednet_multimeter,
				'F', deviceFrame
		);

		DeepStorageUnit.addShaped(
				"PSP",
				"FEF",
				"CSC",
				'P', "sheetPlastic",
				'S', SHULKER_SHELL,
				'F', CHORUS_FRUIT_POPPED,
				'E', forBucketFluid("ender"), // TODO: placeholder?
				'C', forLevel(strongbox, 2)
		);

		cheap_DeepStorageUnit.addShaped(
				"PCP",
				"CFC",
				"PCP",
				'P', "sheetPlastic",
				'C', forLevel(strongbox, 0),
				'F', machineFrame
		);

		LiquiCrafter.addShaped(
				"PTP",
				"SFS",
				"OCO",
				'P', "sheetPlastic",
				'T', CRAFTING_TABLE,
				'S', forLevel(portable_tank, 0),
				'F', deviceFrame,
				'O', BOOK,
				'C', redstoneServo
		);

		LavaFabricator.addShaped(
				"PTP",
				"SFS",
				"OCO",
				'P', "sheetPlastic",
				'T', OBSIDIAN,
				'S', MAGMA,
				'F', machineFrame,
				'O', BLAZE_ROD,
				'C', powerCoilGold
		);

		SteamBoiler.addShaped(
				"PPP",
				"PBP",
				"STS",
				'P', "sheetPlastic",
				'T', forLevel(portable_tank, 1),
				'B', SludgeBoiler,
				'S', NETHER_BRICK_STAIRS
		);

		AutoJukebox.addShaped(
				"PJP",
				"PFP",
				'P', "sheetPlastic",
				'J', JUKEBOX,
				'F', deviceFrame
		);

		Unifier.addShaped(
				"PTP",
				"OFO",
				"SCS",
				'P', "sheetPlastic",
				'T', lexicon,
				'O', OBSERVER,
				'F', deviceFrame,
				'S', "ingotSilver",
				'C', BOOK
		);

		AutoSpawner.addShaped(
				"PTP",
				"SFS",
				"OCO",
				'P', "sheetPlastic",
				'T', Items.NETHER_WART,
				'S', MAGMA_CREAM,
				'F', machineFrame,
				'O', "gemEmerald",
				'C', powerCoilGold
		);

		BioReactor.addShaped(
				"PTP",
				"SFS",
				"OCO",
				'P', "sheetPlastic",
				'T', FERMENTED_SPIDER_EYE,
				'S', "slimeball",
				'F', deviceFrame,
				'O', BRICK,
				'C', SUGAR
		);

		BiofuelGenerator.addShaped(
				"PTP",
				"SFS",
				"OCO",
				'P', "sheetPlastic",
				'T', FURNACE,
				'S', PISTON,
				'F', machineFrame,
				'O', BLAZE_ROD,
				'C', powerCoilSilver
		);

		AutoDisenchanter.addShaped(
				"PTP",
				"SFS",
				"OCO",
				'P', "sheetPlastic",
				'T', NETHER_BRICK,
				'S', BOOK,
				'F', machineFrame,
				'O', "gemDiamond",
				'C', powerCoilGold
		);

		Slaughterhouse.addShaped(
				"GIG",
				"SFS",
				"XCX",
				'G', "sheetPlastic",
				'I', "gearSteel",
				'S', invarAxe,
				'F', machineFrame,
				'X', sawblade,
				'C', powerCoilGold
		);

		MeatPacker.addShaped(
				"GSG",
				"BFB",
				"BCB",
				'G', "sheetPlastic",
				'S', FLINT_AND_STEEL,
				'B', BRICK_BLOCK,
				'F', machineFrame,
				'C', powerCoilGold
		);

		EnchantmentRouter.addShaped(
				"PPP",
				"BFB",
				"POP",
				'P', "sheetPlastic",
				'B', BOOK,
				'F', deviceFrame,
				'O', OBSERVER
		);

		LaserDrill.addShaped(
				"GFG",
				"DFD",
				"CHC",
				'G', "sheetPlastic",
				'F', lamp,
				'D', "gemDiamond",
				'H', "blockGlassHardened",
				'C', "platePlatinum"
		);

		LaserDrillPrecharger.addShaped(
				"GCG",
				"SFD",
				"HCH",
				'G', "sheetPlastic",
				'C', powerCoilElectrum,
				'S', pinkslime_gem,
				'F', lamp,
				'D', "gemDiamond",
				'H', "blockGlassHardened"
		);

		AutoAnvil.addShaped(
				"GGG",
				"AFA",
				"OCO",
				'G', "sheetPlastic",
				'A', ANVIL,
				'F', machineFrame,
				'O', "gearSteel",
				'C', powerCoilGold
		);

		BlockSmasher.addShaped(
				"GPG",
				"HFH",
				"BCB",
				'G', "sheetPlastic",
				'P', PISTON,
				'H', hammer,
				'F', machineFrame,
				'B', BOOK,
				'C', powerCoilGold
		);

		RedNote.addShaped(
				"GNG",
				"CFC",
				'G', "sheetPlastic",
				'N', NOTEBLOCK,
				'C', "cableRedNet",
				'F', deviceFrame
		);

		AutoBrewer.addShaped(
				"GBG",
				"CFC",
				"RPR",
				'G', "sheetPlastic",
				'B', Items.BREWING_STAND,
				'C', plastic_pipe,
				'F', machineFrame,
				'R', REPEATER,
				'P', powerCoilGold
		);

		FruitPicker.addShaped(
				"GXG",
				"SFS",
				"OCO",
				'G', "sheetPlastic",
				'X', invarAxe,
				'S', SHEARS,
				'F', machineFrame,
				'O', "plateTin",
				'C', powerCoilGold
		);

		BlockPlacer.addShaped(
				"GDG",
				"DMD",
				"GSG",
				'G', "sheetPlastic",
				'D', DISPENSER,
				'M', machineFrame,
				'S', powerCoilGold
		);

		MobCounter.addShaped(
				"GGG",
				"RMR",
				"SCS",
				'G', "sheetPlastic",
				'R', spyglass,
				'M', deviceFrame,
				'S', COMPARATOR,
				'C', OBSERVER
		);

		SteamTurbine.addShaped(
				"PTP",
				"SFS",
				"OCO",
				'P', "sheetPlastic",
				'T', PISTON,
				'S', "gearSteel",
				'F', machineFrame,
				'O', "plateInvar",
				'C', powerCoilSilver
		);

		ChunkLoader.addShaped(
				"PEP",
				"TFT",
				"OCO",
				'P', "sheetPlastic",
				'E', forLevel(energy_cell, 5),
				'T', "ingotEnderium",
				'F', "blockEnderium", // TODO: placeholder
				'O', "plateElectrum",
				'C', powerCoilElectrum
		);

		cheap_ChunkLoader.addShaped(
				"PEP",
				"TFT",
				"OCO",
				'P', "sheetPlastic",
				'E', forLevel(energy_cell, 0),
				'T', "ingotEnderium",
				'F', machineFrame,
				'O', "plateElectrum",
				'C', powerCoilElectrum
		);

		Fountain.addShaped(
				"PBP",
				"TFT",
				"OCO",
				'P', "sheetPlastic",
				'B', IRON_BARS,
				'T', plastic_tank,
				'F', machineFrame,
				'O', "plateNickel",
				'C', powerCoilGold
		);
	}
	//endregion

	//region misc
	private final IRecipeHolder fertilizer_item = IRecipeHolder.EMPTY;
	private final IRecipeHolder porta_spawner = IRecipeHolder.EMPTY;
	private final IRecipeHolder spyglass = IRecipeHolder.EMPTY;

	private void registerMiscItems() {

		fertilizer_item.addShaped(
				"WBW",
				"STS",
				"WBW",
				'W', Items.WHEAT,
				'B', new ItemStack(DYE, 1, 15),
				'S', "dustSulfur",
				'T', "stickWood"
		);

		spyglass.addShaped(
				"GLG",
				"PLP",
				" S ",
				'G', "ingotBronze",
				'L', "blockGlass",
				'P', "sheetPlastic",
				'S', "stickWood"
		);

		porta_spawner.addShaped(
				"GLG",
				"DND",
				"GLG",
				'G', "ingotInvar",
				'L', "blockGlass",
				'D', "ingotEnderium",
				'N', NETHER_STAR
		);
	}
	//endregion

	//region rednet
	private final IRecipeHolder rednet_cable_energy_single = IRecipeHolder.EMPTY;
	private final IRecipeHolder rednet_cable_energy_multi = IRecipeHolder.EMPTY;
	private final IRecipeHolder rednet_multimeter = IRecipeHolder.EMPTY;

	//ingredients
	private final IRecipeHolder rednet_cable = IRecipeHolder.EMPTY;
	private final IRecipeHolder rednet_meter = IRecipeHolder.EMPTY;

	private void registerRedNet() {

		rednet_cable_energy_single.addShapeless(
				rednet_cable, "dustRedstone", "dustRedstone",
				"nuggetElectrum", "nuggetElectrum", "nuggetElectrum"
		);

		rednet_cable_energy_multi.addShapeless(
				REDSTONE_BLOCK,
				"ingotElectrum", "ingotElectrum",
				rednet_cable, rednet_cable, rednet_cable,
				rednet_cable, rednet_cable, rednet_cable
		);

		rednet_multimeter.addShaped(
				"RGR",
				"IMI",
				"PPP",
				'R', "dustRedstone",
				'G', powerCoilElectrum,
				'I', "ingotCopper",
				'M', rednet_meter,
				'P', "sheetPlastic"
		);
	}
	//endregion

	public static Ingredient forLevel(ItemStack stack, final int level) {

		stack = stack.copy();
		stack.setTagInfo("Level", new NBTTagInt(level));
		return new Ingredient(stack) {

			@Override
			public boolean apply(ItemStack stack) {

				if (super.apply(stack)) {
					NBTTagCompound tag = stack.getTagCompound();
					if (tag == null || !tag.hasKey("Level", Constants.NBT.TAG_ANY_NUMERIC))
						return level == 0;
					return level == tag.getInteger("Level");
				}
				return false;
			}

			@Override
			public boolean isSimple() {

				return false;
			}
		};
	}

	private static Ingredient forBucketFluid(final String fluidName) {

		return new Ingredient(MFRUtil.getBucketFor(fluidName)) {
			@Override
			public boolean apply(ItemStack stack) {

				if (super.apply(stack)) {
					FluidStack fluid = FluidUtil.getFluidContained(stack);
					return fluid != null && fluidName.equals(fluid.getFluid().getName());
				}
				return false;
			}

			@Override
			public boolean isSimple() {

				return false;
			}
		};
	}

}

