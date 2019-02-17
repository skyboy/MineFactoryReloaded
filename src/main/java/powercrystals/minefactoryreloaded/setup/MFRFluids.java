package powercrystals.minefactoryreloaded.setup;

import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import powercrystals.minefactoryreloaded.MFRProps;
import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.block.fluid.BlockExplodingFluid;
import powercrystals.minefactoryreloaded.block.fluid.BlockFactoryFluid;
import powercrystals.minefactoryreloaded.block.fluid.BlockPinkSlimeFluid;
import powercrystals.minefactoryreloaded.core.FluidHandlerItemStackSimpleSingleFluid;
import powercrystals.minefactoryreloaded.core.UtilInventory;
import powercrystals.minefactoryreloaded.core.WeightedRandomItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Locale;

public class MFRFluids {

	public static final MFRFluids INSTANCE = new MFRFluids();

	public static Fluid milk;
	public static Fluid sludge;
	public static Fluid sewage;
	public static Fluid essence;
	public static Fluid biofuel;
	public static Fluid meat;
	public static Fluid pinkSlime;
	public static Fluid chocolateMilk;
	public static Fluid mushroomSoup;
	public static Fluid steam;
	public static BlockFactoryFluid milkLiquid;
	public static BlockFactoryFluid sludgeLiquid;
	public static BlockFactoryFluid sewageLiquid;
	public static BlockFactoryFluid essenceLiquid;
	public static BlockFactoryFluid biofuelLiquid;
	public static BlockFactoryFluid meatLiquid;
	public static BlockFactoryFluid pinkSlimeLiquid;
	public static BlockFactoryFluid chocolateMilkLiquid;
	public static BlockFactoryFluid mushroomSoupLiquid;
	public static BlockFactoryFluid steamFluid;

	private static enum FluidData {
		MILK {
			@Override public boolean vaporize(@Nullable EntityPlayer player, World world, BlockPos pos, FluidStack fluidStack) {

				if (world.rand.nextInt(50) == 0)
					drop(world, pos, new ItemStack(Items.DYE, world.rand.nextInt(2), 15));
				return true;
			}
		},
		CHOCOLATE_MILK {
			@Override public boolean vaporize(@Nullable EntityPlayer player, World world, BlockPos pos, FluidStack fluidStack) {

				if (world.rand.nextBoolean())
					drop(world, pos, new ItemStack(Items.DYE, world.rand.nextInt(2), 3));
				return true;
			}
		},
		MUSHROOM_SOUP {
			@Override public boolean vaporize(@Nullable EntityPlayer player, World world, BlockPos pos, FluidStack fluidStack) {

				if (world.rand.nextInt(5) == 0)
					world.setBlockState(pos, (world.rand.nextBoolean() ? Blocks.BROWN_MUSHROOM : Blocks.RED_MUSHROOM).getDefaultState(), 3);
				else if (world.rand.nextBoolean())
					drop(world, pos, new ItemStack(Blocks.BROWN_MUSHROOM, world.rand.nextInt(2)));
				else
					drop(world, pos, new ItemStack(Blocks.RED_MUSHROOM, world.rand.nextInt(2)));
				return true;
			}
		},
		SLUDGE {
			@Override public boolean vaporize(@Nullable EntityPlayer player, World world, BlockPos pos, FluidStack fluidStack) {

				drop(world, pos, ((WeightedRandomItemStack) WeightedRandom.
						getRandomItem(world.rand, MFRRegistry.getSludgeDrops())).getStack());
				return true;
			}
		},
		SEWAGE {
			@Override public boolean vaporize(@Nullable EntityPlayer player, World world, BlockPos pos, FluidStack fluidStack) {

				drop(world, pos, new ItemStack(MFRThings.fertilizerItem, 1 + world.rand.nextInt(2)));
				return true;
			}
		},
		MOB_ESSENCE(true) {
			@Override public boolean vaporize(@Nullable EntityPlayer player, World world, BlockPos pos, FluidStack fluidStack) {

				int i = world.rand.nextInt(5) + 10;
				while (i > 0) {
					int j = EntityXPOrb.getXPSplit(i);
					i -= j;
					world.spawnEntity(new EntityXPOrb(world,
							pos.getX() + world.rand.nextDouble(), pos.getY() + world.rand.nextDouble(), pos.getZ() + world.rand.nextDouble(), j));
				}
				return true;
			}
		},
		PINK_SLIME(true) {
			@Override public boolean vaporize(@Nullable EntityPlayer player, World world, BlockPos pos, FluidStack fluidStack) {

				@Nonnull ItemStack drop;
				if (world.rand.nextBoolean())
					drop = new ItemStack(MFRThings.pinkSlimeItem, world.rand.nextInt(3));
				else if (world.rand.nextInt(5) != 0)
					drop = new ItemStack(MFRThings.meatNuggetRawItem, world.rand.nextInt(2));
				else
					drop = new ItemStack(MFRThings.meatNuggetCookedItem, world.rand.nextInt(2));
				drop(world, pos, drop);
				return true;
			}
		},
		MEAT {
			@Override public boolean vaporize(@Nullable EntityPlayer player, World world, BlockPos pos, FluidStack fluidStack) {

				@Nonnull ItemStack drop;
				if (world.rand.nextInt(5) != 0)
					drop = new ItemStack(MFRThings.meatIngotRawItem, world.rand.nextInt(2));
				else
					drop = new ItemStack(MFRThings.meatIngotCookedItem, world.rand.nextInt(2));
				drop(world, pos, drop);
				return true;
			}
		},
		BIOFUEL {
			@Override public boolean vaporize(@Nullable EntityPlayer player, World world, BlockPos pos, FluidStack fluidStack) {

				if (MFRConfig.enableFuelExploding.getBoolean(true)) {
					world.createExplosion(null, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 8, true);
					return false;
				} else {
					return true;
				}
			}
		},
		STEAM {
			@Override public boolean doesVaporize(FluidStack fluidStack) {

				return false;
			}
		};

		public static HashMap<String, FluidData> names;
		static {
			names = new HashMap<>();
			for (FluidData data : FluidData.values())
				names.put(data.name().toLowerCase(Locale.US), data);
		}

		public final String name;

		private FluidData() {

			this(false);
		}

		private FluidData(boolean unique) {

			name = name().toLowerCase(Locale.US);
		}

		protected void drop(World world, BlockPos pos, @Nonnull ItemStack stack) {

			UtilInventory.dropStackInAir(world, pos, stack);
		}

		public boolean doesVaporize(FluidStack fluidStack) {

			return true;
		}

		public boolean vaporize(@Nullable EntityPlayer player, World worldIn, BlockPos pos, FluidStack fluidStack) {

			return false; // "call super" for the smoke effect
		}

	}

	private MFRFluids() {

	}

	public static Fluid getFluid(String fluid) {

		return FluidRegistry.getFluid(fluid);
	}

	public static void preInit() {

		milk = registerFluid(FluidData.MILK, 1050, EnumRarity.COMMON);
		sludge = registerFluid(FluidData.SLUDGE, 1700, EnumRarity.COMMON);
		sewage = registerFluid(FluidData.SEWAGE, 1200, EnumRarity.COMMON);
		essence = registerFluid(FluidData.MOB_ESSENCE, 400, 9, 310, EnumRarity.EPIC);
		biofuel = registerFluid(FluidData.BIOFUEL, 800, EnumRarity.UNCOMMON);
		meat = registerFluid(FluidData.MEAT, 2000, EnumRarity.COMMON);
		pinkSlime = registerFluid(FluidData.PINK_SLIME, 3000, EnumRarity.RARE);
		chocolateMilk = registerFluid(FluidData.CHOCOLATE_MILK, 1100, EnumRarity.COMMON);
		mushroomSoup = registerFluid(FluidData.MUSHROOM_SOUP, 1500, EnumRarity.COMMON);
		steam = registerFluid(FluidData.STEAM, -100, 0, 673, EnumRarity.COMMON);

		FluidRegistry.addBucketForFluid(milk);
		FluidRegistry.addBucketForFluid(sludge);
		FluidRegistry.addBucketForFluid(sewage);
		FluidRegistry.addBucketForFluid(essence);
		FluidRegistry.addBucketForFluid(biofuel);
		FluidRegistry.addBucketForFluid(meat);
		FluidRegistry.addBucketForFluid(pinkSlime);
		FluidRegistry.addBucketForFluid(chocolateMilk);
		FluidRegistry.addBucketForFluid(mushroomSoup);

		milkLiquid = new BlockFactoryFluid("milk");
		sludgeLiquid = new BlockFactoryFluid("sludge");
		sewageLiquid = new BlockFactoryFluid("sewage");
		essenceLiquid = new BlockFactoryFluid("mob_essence");
		biofuelLiquid = new BlockExplodingFluid("biofuel");
		meatLiquid = new BlockFactoryFluid("meat");
		pinkSlimeLiquid = new BlockPinkSlimeFluid("pink_slime");
		chocolateMilkLiquid = new BlockFactoryFluid("chocolate_milk");
		mushroomSoupLiquid = new BlockFactoryFluid("mushroom_soup");
		steamFluid = new BlockFactoryFluid("steam", BlockFactoryFluid.material);

		Blocks.FIRE.setFireInfo(MFRFluids.biofuelLiquid, 300, 30);

		MFRRegistry.registerBlock(milkLiquid, new ItemBlock(milkLiquid));
		MFRRegistry.registerBlock(sludgeLiquid, new ItemBlock(sludgeLiquid));
		MFRRegistry.registerBlock(sewageLiquid, new ItemBlock(sewageLiquid));
		MFRRegistry.registerBlock(essenceLiquid, new ItemBlock(essenceLiquid));
		MFRRegistry.registerBlock(biofuelLiquid, new ItemBlock(biofuelLiquid));
		MFRRegistry.registerBlock(meatLiquid, new ItemBlock(meatLiquid));
		MFRRegistry.registerBlock(pinkSlimeLiquid, new ItemBlock(pinkSlimeLiquid));
		MFRRegistry.registerBlock(chocolateMilkLiquid, new ItemBlock(chocolateMilkLiquid));
		MFRRegistry.registerBlock(mushroomSoupLiquid, new ItemBlock(mushroomSoupLiquid));
		MFRRegistry.registerBlock(steamFluid, new ItemBlock(steamFluid));


		if (MFRConfig.vanillaOverrideMilkBucket.getBoolean(true)) {
			//needs to be the vanilla milk bucket registry name to properly replace vanilla one
			ForgeRegistries.ITEMS.register(new ItemBucket(milkLiquid).setUnlocalizedName("mfr.bucket.milk").setRegistryName("minecraft:milk_bucket"));
		}
	}

	public static Fluid registerFluid(FluidData data, int density, EnumRarity rarity) {

		return registerFluid(data, density, -1, -1, rarity);
	}

	public static Fluid registerFluid(final FluidData data, int density, int lightValue, int temp, EnumRarity rarity) {

		String name = data.name().toLowerCase(Locale.US);
		Fluid fluid = new Fluid(data.name, new ResourceLocation("minefactoryreloaded:blocks/fluid/fluid.mfr." + name + ".still"),
				new ResourceLocation("minefactoryreloaded:blocks/fluid/fluid.mfr." + name + ".flowing")) {

			@Override
			public boolean doesVaporize(FluidStack fluidStack) {

				return data.doesVaporize(fluidStack);
			}

			@Override
			public void vaporize(EntityPlayer player, World world, BlockPos pos, FluidStack fluidStack) {

				if (data.vaporize(player, world, pos, fluidStack)) {
					super.vaporize(player, world, pos, fluidStack);
				}
			}

		};
		setFluidData(fluid, name, density, lightValue, temp, rarity); // if we get a different Fluid from registering, we still want the original to have its values
		if (!FluidRegistry.registerFluid(fluid))
			fluid = FluidRegistry.getFluid(data.name);
		setFluidData(fluid, name, density, lightValue, temp, rarity);
		return fluid;
	}

	private static void setFluidData(Fluid fluid, String name, int density, int lightValue, int temp, EnumRarity rarity) {
		if (density != 0) {
			fluid.setDensity(density);
			fluid.setViscosity(Math.abs(density)); // works for my purposes
		}
		if (lightValue >= 0)
			fluid.setLuminosity(lightValue);
		if (temp >= 0)
			fluid.setTemperature(temp);
		fluid.setUnlocalizedName("mfr." + name + ".still.name");
		fluid.setRarity(rarity);
	}

	private static final @Nonnull ItemStack MILK_BOTTLE = new ItemStack(MFRThings.milkBottleItem);
	private static final @Nonnull ItemStack GLASS_BOTTLE = new ItemStack(Items.GLASS_BOTTLE);
	private static final @Nonnull ItemStack MUSHROOM_STEW = new ItemStack(Items.MUSHROOM_STEW);
	private static final @Nonnull ItemStack BOWL = new ItemStack(Items.BOWL);

	@SubscribeEvent
	public void onItemStackConstruct(AttachCapabilitiesEvent<ItemStack> evt) {

		Item item = evt.getObject().getItem();
		if ((item == Items.GLASS_BOTTLE && PotionUtils.getEffectsFromStack(evt.getObject()).isEmpty()) ||
				item == MFRThings.milkBottleItem) {
			evt.addCapability(new ResourceLocation(MFRProps.PREFIX + "milk_bottle_cap"),
					new FluidHandlerItemStackSimpleSingleFluid(evt.getObject(), MILK_BOTTLE, GLASS_BOTTLE,
							MFRFluids.getFluid("milk"), Fluid.BUCKET_VOLUME));
		} else if (item == Items.BOWL || item == Items.MUSHROOM_STEW) {
			evt.addCapability(new ResourceLocation(MFRProps.PREFIX + "mushroom_soup_cap"),
					new FluidHandlerItemStackSimpleSingleFluid(evt.getObject(), MUSHROOM_STEW, BOWL,
							MFRFluids.getFluid("mushroom_soup"), Fluid.BUCKET_VOLUME));
		}
	}
}
