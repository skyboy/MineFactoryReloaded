package powercrystals.minefactoryreloaded.api.integration;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import powercrystals.minefactoryreloaded.api.handler.*;
import powercrystals.minefactoryreloaded.api.laser.EnumFactoryLaserColor;
import powercrystals.minefactoryreloaded.api.mob.IFactoryGrindable;
import powercrystals.minefactoryreloaded.api.mob.IFactoryRanchable;
import powercrystals.minefactoryreloaded.api.mob.IRandomMobProvider;
import powercrystals.minefactoryreloaded.api.plant.*;
import powercrystals.minefactoryreloaded.api.rednet.IRedNetLogicCircuit;

import javax.annotation.Nonnull;
import java.io.File;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This interface is used to integrate into MFR.
 * <p>
 * Classes implementing this interface will be automatically located and instantiated using the default parameter-less constructor.
 */
@SuppressWarnings("unused")
public interface IMFRIntegrator {

	/**
	 * Apply this annotation to your Integrator if it depends on other mods,
	 * they will be checked to see if they are loaded before creating your Integrator.
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	@interface DependsOn {

		String[] value();
	}

	IRegistry REGISTRY = IRegistry.EMPTY_REGISTRY;

	/**
	 * Read configuration options for your integrator from the provided config file. This runs before {@link #preLoad}.
	 * <p>
	 * You are handed a {@link File} instead of a {@link net.minecraftforge.common.config.Configuration Configuration}
	 * so that you may either use a different format, or ignore this entirely if you have no settings.
	 */
	default void readConfig(File config) throws Throwable {

	}

	/**
	 * Pre-initialization of your integration, this runs during MFR's pre-initialization phase,
	 * so may run BEFORE your {@link net.minecraftforge.fml.common.Mod @Mod}'s pre-init phase.
	 * <p>
	 * Be careful not to crash when using this method.
	 */
	default void preLoad() throws Throwable {

	}

	/**
	 * Initialization of your integration, this runs during MFR's initialization phase.
	 * <p>
	 * By this stage, all mods should have registered their blocks and items, and most Ore Dictionary entries.
	 */
	void load() throws Throwable;

	/**
	 * Post-initialization of your integration, this runs during MFR's post-initialization phase.
	 * <p>
	 * By this stage, all mods should have registered ALL Ore Dictionary entries and
	 * processed their {@link net.minecraftforge.fml.common.event.FMLInterModComms.IMCEvent IMC} messages.
	 */
	default void postLoad() throws Throwable {

	}

	/**
	 * Post-Post-initialization of your integration, this runs during MFR's
	 * {@link net.minecraftforge.fml.common.event.FMLLoadCompleteEvent load-complete} phase.
	 * <p>
	 * You will most likely not need to use this at all, but is available for completeness' sake.
	 * There will be no further method calls on your Integrator, and references to it will not be kept after this method is called.
	 */
	default void completeLoad() throws Throwable {

	}

	/**
	 * The name of your integrator, by default the Class's name is used
	 *
	 * @return String    The name of this integrator used in MFR's config for enabling/disabling
	 */
	default String getIntegratorName() {

		return this.getClass().getSimpleName();
	}

	/**
	 * Apply this annotation to your Integrator if it depends on other Integrators.
	 * they will be sorted out after creation, before running {@link #preLoad()}.
	 * <p>
	 * Cyclic dependencies will result in a random order for Integrators in the cycle.
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	@interface After {

		String[] value();
	}

	// fragment into multiple IRegistries? this is kind of a beast to interact with for all the methods
	interface IRegistry {

		/**
		 * Registers a plantable object with the Planter.
		 */
		void registerPlantable(@Nonnull IFactoryPlantable plantable);

		/**
		 * Registers a harvestable block with the Harvester.
		 */
		void registerHarvestable(@Nonnull IFactoryHarvestable harvestable);

		/**
		 * Registers a fertilizable block with the Fertilizer.
		 */
		void registerFertilizable(@Nonnull IFactoryFertilizable fertilizable);

		/**
		 * Registers a fertilizer item Fertilizer.
		 */
		void registerFertilizer(@Nonnull IFactoryFertilizer fertilizer);

		/**
		 * Registers a ranchable entity with the Rancher.
		 */
		void registerRanchable(@Nonnull IFactoryRanchable ranchable);

		/**
		 * Registers a grindable entity with the Grinder.
		 */
		void registerGrindable(@Nonnull IFactoryGrindable grindable);

		/**
		 * Bans an entity Class from being automatically ground by the Grinder
		 */
		void registerGrinderBlacklist(@Nonnull Class<?> ungrindable);

		/**
		 * Registers a possible output with the Sludge Boiler.
		 *
		 * @param weight
		 * 		Likelihood that this item will be produced. Lower means rarer. Must be greater than 0.
		 * @param drop
		 * 		The thing being produced by the sludge boiler.
		 */
		void registerSludgeDrop(int weight, @Nonnull ItemStack drop);

		/**
		 * Registers a Safari Net handler to properly serialize a type of mob.
		 */
		void registerSafariNetHandler(@Nonnull ISafariNetHandler handler);

		/**
		 * Bans an entity Class from being collected by Safari Nets
		 */
		void registerSafariNetBlacklist(@Nonnull Class<?> entityClass);

		/**
		 * Registers a provider to create possible outputs from mystery Safari Nets.
		 */
		void registerRandomMobProvider(@Nonnull IRandomMobProvider mobProvider);

		/**
		 * Registers a mob egg handler, which allows the Safari Net to properly change colors.
		 */
		void registerMobEggHandler(@Nonnull IMobEggHandler handler);

		/**
		 * Allows Rubber Trees to spawn in the specified biome.
		 *
		 * @param biome
		 * 		The registry id of the biome to spawn in
		 */
		void registerRubberTreeBiome(@Nonnull ResourceLocation biome);

		/**
		 * Allows Rubber Trees to spawn in the specified biome.
		 *
		 * @param biome
		 * 		The biome to spawn in
		 */
		default void registerRubberTreeBiome(@Nonnull Biome biome) {

			if (biome.delegate.name() == null)
				throw new IllegalArgumentException("Attempted to add unregistered Biome to rubber tree spawn list.");
			this.registerRubberTreeBiome(biome.delegate.name());
		}

		/**
		 * Registers a handler for drinking liquids with the straw.
		 *
		 * @param fluidID
		 * 		The fluid ID the handler handles.
		 * @param liquidDrinkHandler
		 * 		The drink handler instance.
		 */
		void registerLiquidDrinkHandler(@Nonnull String fluidID, @Nonnull ILiquidDrinkHandler liquidDrinkHandler);

		/**
		 * Registers a possible output with the laser drill.
		 *
		 * @param weight
		 * 		Likelihood that this item will be produced. Lower means rarer. Must be greater than 0.
		 * @param ore
		 * 		The thing being produced by the laser drill.
		 */
		void registerLaserOre(int weight, @Nonnull ItemStack ore);

		/**
		 * Registers a preferred ore with the laser drill. Focuses with the specified color will make the specified ore more likely.
		 * Used by MFR itself for vanilla: Black (Coal), Azure (Diamond), Lime (Emerald), Yellow (Gold), Brown (Iron), Blue (Lapis),
		 * Red (Redstone), and White (Nether Quartz).
		 *
		 * @param color
		 * 		The color that the preferred ore is being set for.
		 * @param ore
		 * 		The ore that will be preferred by the drill when a focus with the specified color is present.
		 */
		void addLaserPreferredOre(EnumFactoryLaserColor color, @Nonnull ItemStack ore);

		/**
		 * Registers a Block as a fruit tree log. When the Fruit Picker sees this block on the ground, it will
		 * begin a search in tree mode for any fruit nearby.
		 *
		 * @param fruitLogBlock
		 * 		The block to mark as a fruit tree log.
		 */
		void registerFruitLogBlock(@Nonnull Block fruitLogBlock);

		/**
		 * Registers a fruit for the Fruit Picker.
		 */
		void registerFruit(@Nonnull IFactoryFruit fruit);

		/**
		 * Registers an entity Class as an invalid entry for the Auto-Spawner.
		 *
		 * @param entityClass
		 * 		The entity Class to blacklist.
		 */
		void registerAutoSpawnerBlacklistClass(@Nonnull Class<? extends EntityLivingBase> entityClass);

		/**
		 * Registers an entity id as an invalid entry for the Auto-Spawner.
		 * See also: {@link net.minecraft.entity.EntityList}'s classToStringMapping and stringToClassMapping.
		 *
		 * @param entityString
		 * 		The entity id to blacklist.
		 */
		// TODO docs
		void registerAutoSpawnerBlacklist(@Nonnull String entityString);

		/**
		 * Registers a spawn handler for the Auto-Spawner for tighter control over the spawn results
		 * <p>
		 * Used by MFR to erase horse inventories and clear held blocks for endermen
		 */
		void registerSpawnHandler(@Nonnull IMobSpawnHandler spawnHandler);

		/**
		 * Registers an Ore Dictionary name that the Unifier will not unify with matching items
		 *
		 * @param oredict
		 * 		The Ore Dictionary name to disallow unification on
		 */
		void registerUnifierBlacklist(@Nonnull String oredict);

		/**
		 * Registers an entity Class that will not be moved around by Conveyors
		 *
		 * @param entityClass
		 * 		The entity Class to ignore
		 */
		void registerConveyorBlacklist(@Nonnull Class<? extends Entity> entityClass);

		/**
		 * Registers logic circuit to be usable in the Programmable RedNet Controller.
		 *
		 * @param circuit
		 * 		The circuit to be registered.
		 */
		void registerRedNetLogicCircuit(@Nonnull IRedNetLogicCircuit circuit);

		/**
		 * Registers a tool handler for telling MFR how to use tools reactively.
		 *
		 * @param handler
		 * 		The IFactoryTool instance to handle tools
		 */
		void addToolHandler(IFactoryTool handler);

		// TODO: docs
		void registerNeedleAmmoType(@Nonnull Item item, @Nonnull INeedleAmmo ammo);

		IRegistry EMPTY_REGISTRY = new IRegistry() {

			@Override
			public void registerPlantable(@Nonnull IFactoryPlantable plantable) {

			}

			@Override
			public void registerHarvestable(@Nonnull IFactoryHarvestable harvestable) {

			}

			@Override
			public void registerFertilizable(@Nonnull IFactoryFertilizable fertilizable) {

			}

			@Override
			public void registerFertilizer(@Nonnull IFactoryFertilizer fertilizer) {

			}

			@Override
			public void registerRanchable(@Nonnull IFactoryRanchable ranchable) {

			}

			@Override
			public void registerGrindable(@Nonnull IFactoryGrindable grindable) {

			}

			@Override
			public void registerGrinderBlacklist(@Nonnull Class<?> ungrindable) {

			}

			@Override
			public void registerSludgeDrop(int weight, @Nonnull ItemStack drop) {

				if (weight <= 0) {
					throw new IllegalArgumentException("Weight of drops cannot be less than 1");
				}
			}

			@Override
			public void registerMobEggHandler(@Nonnull IMobEggHandler handler) {

			}

			@Override
			public void registerSafariNetHandler(@Nonnull ISafariNetHandler handler) {

			}

			@Override
			public void registerRubberTreeBiome(@Nonnull ResourceLocation biome) {

			}

			@Override
			public void registerSafariNetBlacklist(@Nonnull Class<?> entityClass) {

			}

			@Override
			public void registerRandomMobProvider(@Nonnull IRandomMobProvider mobProvider) {

			}

			@Override
			public void registerLiquidDrinkHandler(@Nonnull String fluidID, @Nonnull ILiquidDrinkHandler liquidDrinkHandler) {

			}

			@Override
			public void registerRedNetLogicCircuit(@Nonnull IRedNetLogicCircuit circuit) {

			}

			@Override
			public void addToolHandler(IFactoryTool handler) {

			}

			@Override
			public void registerLaserOre(int weight, @Nonnull ItemStack ore) {

				if (weight <= 0) {
					throw new IllegalArgumentException("Weight of drops cannot be less than 1");
				}
			}

			@Override
			public void registerFruitLogBlock(@Nonnull Block fruitLogBlock) {

			}

			@Override
			public void registerFruit(@Nonnull IFactoryFruit fruit) {

			}

			@Override
			public void registerAutoSpawnerBlacklistClass(@Nonnull Class<? extends EntityLivingBase> entityClass) {

			}

			@Override
			public void registerAutoSpawnerBlacklist(@Nonnull String entityString) {

			}

			@Override
			public void registerSpawnHandler(@Nonnull IMobSpawnHandler spawnHandler) {

			}

			@Override
			public void registerUnifierBlacklist(@Nonnull String string) {

			}

			@Override
			public void registerConveyorBlacklist(@Nonnull Class<? extends Entity> entityClass) {

			}

			@Override
			public void addLaserPreferredOre(@Nonnull EnumFactoryLaserColor color, @Nonnull ItemStack ore) {

			}

			@Override
			public void registerNeedleAmmoType(@Nonnull Item item, @Nonnull INeedleAmmo ammo) {

			}
		};

	}

	/* HELPERS */

	static Item findItem(String modId, String itemName) {

		return Item.REGISTRY.getObject(new ResourceLocation(modId, itemName));
	}

	static Block findBlock(String modId, String blockName) {

		return Block.REGISTRY.getObject(new ResourceLocation(modId, blockName));
	}

}
