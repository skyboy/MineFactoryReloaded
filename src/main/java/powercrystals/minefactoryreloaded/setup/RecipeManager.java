package powercrystals.minefactoryreloaded.setup;

import codechicken.lib.reflect.ReflectionManager;
import cofh.core.util.helpers.RecipeHelper;
import com.google.common.collect.Iterators;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.*;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.RegistrySimple;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.discovery.ModCandidate;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.apache.logging.log4j.Level;
import org.objectweb.asm.Type;
import powercrystals.minefactoryreloaded.MFRProps;
import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.api.integration.IMFRRecipeSet;
import powercrystals.minefactoryreloaded.setup.recipe.Minecraft;

import javax.annotation.Nonnull;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.BooleanSupplier;
import java.util.function.Function;

import static powercrystals.minefactoryreloaded.MineFactoryReloadedCore.log;

@Mod.EventBusSubscriber(modid = MFRProps.MOD_ID)
public class RecipeManager {

	private static final RegistrySimple<ResourceLocation, RecipeContainer> recipes = new RegistrySimple<ResourceLocation, RecipeContainer>() {

		@Override
		protected Map<ResourceLocation, RecipeContainer> createUnderlyingMap() {
			return new LinkedHashMap<>();
		}
	};
	private static final LinkedList<RecipeSetContainer> recipeSets = new LinkedList<>();
	private static RecipeSetContainer VANILLA;
	private static String ACTIVE_SET = null;

	public static RecipeContainer addRecipe(RecipeContainer container) {

		recipes.putObject(container.getName(), container);
		return container;
	}

	public static RecipeContainer addRecipe(String id, ItemStack output, BooleanSupplier enabled) {

		return addRecipe(new RecipeContainer(id, output, enabled));
	}

	public static RecipeContainer addRecipe(String id, ItemStack output) {

		return addRecipe(new RecipeContainer(id, output));
	}

	public static void load(ASMDataTable data) {

		for (ASMDataTable.ASMData obj : data.getAll(Type.getInternalName(IMFRRecipeSet.class))) {
			RecipeSetContainer container = new RecipeSetContainer(obj);
			if ("Minecraft".equals(container.name)) {
				VANILLA = container;
			} else
				recipeSets.add(container);
		}
		if (VANILLA == null) {
			throw new IllegalStateException("Vanilla recipe set removed from jar");
		}
	}

	public static void configure(Function<String, File> getConfig) {

		Minecraft.readCommonConfig(getConfig.apply("recipe/" + "common"));
		for (Iterator<RecipeSetContainer> it = Iterators.concat(Iterators.singletonIterator(VANILLA), recipeSets.iterator()); it.hasNext(); ) {
			RecipeSetContainer container = it.next();
			if (container.recipe != null) {
				container.enabled = MFRConfig.isRecipeSetEnabled(container.name, container.mod);
				if (container.isEnabled()) {
					try {
						container.recipe.readConfig(getConfig.apply("recipe/" + container.name));
					} catch (Throwable t) {
						log().error("Error reading config for MFR recipe set {} from {}", container.name, container.mod);
						log().catching(Level.ERROR, t);
						container.enabled = false;
					}
				}
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void registerItems(RegistryEvent.Register<Item> evt) {

		Minecraft.registerRecipeHolders();
		for (Iterator<RecipeSetContainer> it = Iterators.concat(Iterators.singletonIterator(VANILLA), recipeSets.iterator()); it.hasNext(); ) {
			RecipeSetContainer container = it.next();
			if (container.isEnabled()) {
				try {
					container.processHolders();
				} catch (Throwable t) {
					log().error("Error processing data for MFR recipe set {} from {}", container.name, container.mod);
					log().catching(Level.ERROR, t);
					container.enabled = false;
				}
			}
		}
		recipeSets.stream().filter(RecipeSetContainer::isEnabled).forEach(v -> v.recipe.registerOredict());
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void registerRecipes(RegistryEvent.Register<IRecipe> evt) {

		recipes.iterator().forEachRemaining(RecipeContainer::clear);
		ACTIVE_SET = null;
		recipeSets.stream().filter(RecipeSetContainer::isEnabled).forEach(RecipeSetContainer::registerRecipes);
		if (VANILLA.isEnabled()) {
			VANILLA.registerRecipes();
		} else if (ACTIVE_SET != null) {
			recipes.iterator().forEachRemaining(v -> v.finalizing = true);
			VANILLA.registerRecipes(); // catch missed recipes
		}
		recipes.iterator().forEachRemaining(RecipeContainer::register);
	}

	private static class RecipeSetContainer {

		private static HashSet<String> createdSets = new HashSet<>();

		private static String asString(ModCandidate candidate) {

			String str = null;

			List<ModContainer> data = candidate.getContainedMods();

			if (data.size() == 1) {
				str = data.get(0).getName();
				if (str != null && str.length() > 60)
					str = null; // that's a long name, we'll use the jar name thanks
			}

			if (str == null) {
				str = "Mod jar: `" + candidate.getModContainer().getName() + "`";
			}

			return str;
		}

		RecipeSetContainer(ASMDataTable.ASMData asmData) {

			try {
				mod = asString(asmData.getCandidate());
				String className = Type.getObjectType(asmData.getClassName()).getClassName();
				Class<?> recipeSet = Class.forName(className, false, this.getClass().getClassLoader());
				notFound:
				{
					IMFRRecipeSet.DependsOn depends = recipeSet.getAnnotation(IMFRRecipeSet.DependsOn.class);
					if (depends != null) for (String entry : Objects.requireNonNull(depends.value(), "Cannot have null @DependsOn")) {
						if (!Loader.isModLoaded(entry))
							break notFound;
					}
					recipe = IMFRRecipeSet.class.cast(recipeSet.newInstance());
					String name = recipe.getSetName();
					if (name == null || name.contains(".") || name.toLowerCase(Locale.ROOT).equals("common") ||
							name.contains("/") || name.contains("\\") ||
							name.contains(";") || name.contains(":")) {
						throw new IllegalArgumentException(String.format("Invalid recipe set name `%s`", name));
					}
					if ("minecraft".equals(name.toLowerCase(Locale.ROOT)) && !className.equals(Minecraft.class.getName())) {
						throw new IllegalArgumentException("Cannot replace vanilla recipes");
					}
					if (!createdSets.add(name)) {
						throw new IllegalArgumentException(String.format("Duplicate recipe set `%s`", name));
					}
					this.name = name;
				}
			} catch (Throwable t) {
				log().error("Error loading MFR recipe set from {}", mod);
				log().catching(Level.ERROR, t);
				recipe = null;
			}
		}

		String mod;
		String name;
		IMFRRecipeSet recipe;

		public boolean isEnabled() {

			return name != null && enabled;
		}

		boolean enabled = false;

		private void registerRecipes() {

			ACTIVE_SET = name;
			recipe.registerRecipes();
		}

		private void processHolders() throws Throwable {

			for (Field field : recipe.getClass().getDeclaredFields()) {
				if (0 == (field.getModifiers() & Modifier.FINAL)) {
					continue;
				}
				boolean isArray = false;
				{
					Class<?> fieldType = field.getType();
					if (fieldType.isArray()) {
						isArray = true;
						fieldType = fieldType.getComponentType(); // only one layer, we have no idea what to do with a nested array
					}
					if (fieldType != IMFRRecipeSet.IRecipeHolder.class) {
						continue;
					}
				}
				ReflectionManager.removeFinal(field);
				String desiredValue = field.getName().toLowerCase(Locale.ROOT);
				field.setAccessible(true);
				if (isArray) {
					final ArrayList<IMFRRecipeSet.IRecipeHolder> data = new ArrayList<>(32);
					recipes.iterator().forEachRemaining(v -> {
						if (v.getName().getResourcePath().startsWith(desiredValue))
							data.add(v);
					});
					IMFRRecipeSet.IRecipeHolder[] dataArray;
					if (data.size() == 0) {
						dataArray = IMFRRecipeSet.IRecipeHolder.EMPTY_ARRAY;
					} else {
						dataArray = data.toArray(new IMFRRecipeSet.IRecipeHolder[0]);
					}
					field.set(recipe, dataArray);
				} else {
					IMFRRecipeSet.IRecipeHolder item = recipes.getObject(new ResourceLocation(MFRProps.MOD_ID, desiredValue));
					if (item == null) {
						item = IMFRRecipeSet.IRecipeHolder.EMPTY;
					}
					field.set(recipe, item);
				}
			}
		}

	}

	public static class RecipeContainer implements IMFRRecipeSet.IRecipeHolder {

		private static BooleanSupplier TRUE = () -> true;

		private final ResourceLocation registryName;
		private final BooleanSupplier enabled;
		private final ItemStack output;

		private String recipeName;

		private boolean finalizing = false;
		private final LinkedList<IRecipe> craftingRecipes = new LinkedList<>();
		private final LinkedList<SmeltContainer> smeltingRecipes = new LinkedList<>();
		private int recipes = 0;

		public RecipeContainer(ResourceLocation id, ItemStack result, BooleanSupplier enabledFunc) {

			registryName = id;
			output = result.copy();
			enabled = enabledFunc;
		}

		public RecipeContainer(String id, ItemStack result, BooleanSupplier enabledFunc) {

			this(new ResourceLocation(MFRProps.MOD_ID, id), result, enabledFunc);
		}

		public RecipeContainer(String id, ItemStack result) {

			this(id, result, TRUE);
		}

		private void register() {

			if (enabled.getAsBoolean() && recipes > 0 && !output.isEmpty()) {
				craftingRecipes.forEach(ForgeRegistries.RECIPES::register);
				smeltingRecipes.forEach(v -> FurnaceRecipes.instance().addSmeltingRecipe(v.input, v.output, v.xp));
			}
		}

		private void clear() {

			recipes = 0;
			finalizing = false;
			craftingRecipes.clear();
		}

		private Object[] replaceHolders(Object... input) {

			for (int i = 0; i < input.length; ++i)
				if (input[i] instanceof IMFRRecipeSet.IRecipeHolder)
					input[i] = ((IMFRRecipeSet.IRecipeHolder) input[i]).getItemStack(1);
			return input;
		}

		private void addRecipe(IRecipe recipe) {

			if (finalizing && recipes != 0) {
				return;
			}
			finalizing = false;
			disableFallback();
			recipe.setRegistryName(new ResourceLocation(registryName.toString() + '_' + recipes + '_' + ACTIVE_SET));
			nonAir:
			if (!output.isEmpty() && enabled.getAsBoolean()) {
				for (Ingredient ing : recipe.getIngredients())
					if (!ing.apply(ItemStack.EMPTY))
						break nonAir;
				throw new RuntimeException("All ingredients are air!");
			}
			craftingRecipes.add(recipe);
		}

		private String getGroup() {

			if (recipeName == null) {
				String domain = output.getItem().getRegistryName().getResourceDomain();
				if (domain.equals("minecraft")) {
					recipeName = "minecraft:" + MFRRegistry.remapName(output.getUnlocalizedName(), 1);
				} else {
					recipeName = domain + ":" + MFRRegistry.remapName(output.getUnlocalizedName(), 2);
				}
			}
			return recipeName;
		}

		public void setRecipeGroup(String group) {

			ModContainer active = Loader.instance().activeModContainer();
			if (active == null) {
				// ..?
				recipeName = "BROKEN_MOD";
			} else {
				recipeName = active.getModId();
			}
			recipeName +=  ':' + group;
		}

		@Override
		@Nonnull
		public ResourceLocation getName() {

			return registryName;
		}

		@Override
		public boolean isEnabled() {

			return enabled.getAsBoolean();
		}

		@Override
		@Nonnull
		public ItemStack getItemStack() {

			return output.copy();
		}

		@Override
		public void addShaped(@Nonnull Object... input) {

			CraftingHelper.ShapedPrimer primer = CraftingHelper.parseShaped(replaceHolders(input));
			addRecipe(new ShapedRecipes(getGroup(), primer.width, primer.height, primer.input, getItemStack()));
		}

		@Override
		public void addShapeless(@Nonnull Object... input) {

			addRecipe(new ShapelessRecipes(getGroup(), getItemStack(), RecipeHelper.buildInput(replaceHolders(input))));
		}

		@Override
		public void addSmelting(@Nonnull ItemStack input, float xp) {

			if (finalizing && recipes != 0) {
				return;
			}
			finalizing = false;
			if (!input.isEmpty()) {
				disableFallback();
				smeltingRecipes.add(new SmeltContainer(getGroup(), input, getItemStack(), xp));
			}
		}

		@Override
		public void disableFallback() {

			recipes++;
		}

		private static final class SmeltContainer {

			public final String group;
			public final ItemStack input, output;
			public final float xp;

			private SmeltContainer(String group, ItemStack input, ItemStack output, float xp) {

				this.group = group;
				this.input = input;
				this.output = output;
				this.xp = xp;
			}
		}
	}

}
