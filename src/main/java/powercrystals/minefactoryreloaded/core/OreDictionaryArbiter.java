package powercrystals.minefactoryreloaded.core;

import cofh.core.util.helpers.ItemHelper;
import cofh.core.util.ItemWrapper;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Map.Entry;

/**
 * This class exists to optimize OreDictionary functionality, as it is embarrassingly slow otherwise.
 * 
 * The vast majority of this functionality is safely exposed through {@link ItemHelper} via a proxy. If you use the fancy functions here, READ how they work.
 * They are typically unsafe if you are stupid. Don't be stupid.
 * 
 * @author King Lemming
 * 
 */
public class OreDictionaryArbiter {

	private static BiMap<String, Integer> oreIDs = HashBiMap.create();
	private static TMap<Integer, ArrayList<ItemStack>> oreStacks = new THashMap<>();

	private static TMap<ItemWrapper, ArrayList<Integer>> stackIDs = new THashMap<>();
	private static TMap<ItemWrapper, ArrayList<String>> stackNames = new THashMap<>();

	private static String[] oreNames = new String[] {};

	public static final String UNKNOWN = "Unknown";
	private static final int UNKNOWN_ID = -1;
	private static final int WILDCARD_VALUE = Short.MAX_VALUE;

	/**
	 * Initializes all of the entries. Called on server start to make sure everything is in sync.
	 */
	public static void initialize() {

		oreIDs = HashBiMap.create();
		oreStacks = new THashMap<>();

		stackIDs = new THashMap<>();
		stackNames = new THashMap<>();

		oreNames = OreDictionary.getOreNames();

		for (String oreName : oreNames) {
			NonNullList<ItemStack> ores = OreDictionary.getOres(oreName);

			for (ItemStack ore : ores) {
				registerOreDictionaryEntry(ore, oreName);
			}
		}
	}
	
	public static void bake() {

		TMap<ItemWrapper, ArrayList<Integer>> ids = stackIDs;
		TMap<ItemWrapper, ArrayList<String>> names = stackNames;

		stackIDs = new THashMap<>();
		stackNames = new THashMap<>();

		for (Entry<ItemWrapper, ArrayList<Integer>> a : ids.entrySet())
			stackIDs.put(a.getKey(), a.getValue());

		for (Entry<ItemWrapper, ArrayList<String>> a : names.entrySet())
			stackNames.put(a.getKey(), a.getValue());
	}

	/**
	 * Register an Ore Dictionary Entry.
	 */
	public static void registerOreDictionaryEntry(@Nonnull ItemStack stack, String name) {

		int id = OreDictionary.getOreID(name);

		oreIDs.put(name, id);

		if (!oreStacks.containsKey(id)) {
			oreStacks.put(id, new ArrayList<>());
		}
		oreStacks.get(id).add(stack);

		ItemWrapper item = ItemWrapper.fromItemStack(stack);

		if (!stackIDs.containsKey(item)) {
			stackIDs.put(item, new ArrayList<>());
			stackNames.put(item, new ArrayList<>());
		}
		stackIDs.get(item).add(OreDictionary.getOreID(name));
		stackNames.get(item).add(name);
	}

	/**
	 * Retrieves the oreID, given an oreName.
	 * 
	 * Returns -1 if there is no corresponding oreID.
	 */
	public static int getOreID(String name) {

		Integer id = oreIDs.get(name);

		return id == null ? UNKNOWN_ID : id;
	}

	/**
	 * Retrieves the oreID, given an @Nonnull ItemStack.
	 * 
	 * If an @Nonnull ItemStack has more than one oreID, this returns the first one - just like Forge's Ore Dictionary.
	 * 
	 * Returns -1 if there is no corresponding oreID.
	 */
	public static int getOreID(@Nonnull ItemStack stack) {

		if (stack.isEmpty()) {
			return UNKNOWN_ID;
		}
		ArrayList<Integer> ids = stackIDs.get(new ItemWrapper(stack));

		if (ids == null) {
			ids = stackIDs.get(new ItemWrapper(stack.getItem(), WILDCARD_VALUE));
		}
		return ids == null ? UNKNOWN_ID : ids.get(0);
	}

	/**
	 * Returns a list containing ALL oreIDs for a given @Nonnull ItemStack. Returns NULL if there are none.
	 * 
	 * Input is not validated - don't be dumb!
	 */
	public static ArrayList<Integer> getAllOreIDs(@Nonnull ItemStack stack) {

		ArrayList<Integer> ids = stackIDs.get(new ItemWrapper(stack));

		return ids == null ? stackIDs.get(new ItemWrapper(stack.getItem(), WILDCARD_VALUE)) : ids;
	}

	/**
	 * Retrieves the oreName, given an oreID.
	 * 
	 * Returns "Unknown" if there is no corresponding oreName.
	 */
	public static String getOreName(int id) {

		String oreName = oreIDs.inverse().get(id);

		return oreName == null ? UNKNOWN : oreName;
	}

	/**
	 * Retrieves the oreName, given an @Nonnull ItemStack.
	 * 
	 * If an @Nonnull ItemStack has more than one oreName, this returns the first one = just like Forge's Ore Dictionary.
	 * 
	 * Returns "Unknown" if there is no corresponding oreName.
	 */
	public static String getOreName(@Nonnull ItemStack stack) {

		int id = getOreID(stack);

		return id == UNKNOWN_ID ? UNKNOWN : getOreName(id);
	}

	/**
	 * Returns a list containing ALL oreNames for a given @Nonnull ItemStack. Returns NULL if there are none.
	 * 
	 * Input is not validated - don't be dumb!
	 */
	public static ArrayList<String> getAllOreNames(@Nonnull ItemStack stack) {

		return stackNames.get(new ItemWrapper(stack));
	}

	/**
	 * Do not under ANY circumstances EVER modify these stacks. This is a direct return for time saving reasons.
	 * 
	 * Forge's Ore Dictionary has an O(N) copy here because it assumes all modders are stupid. But you're not stupid, right?
	 * 
	 * DO NOT MODIFY THESE.
	 */
	public static ArrayList<ItemStack> getOres(@Nonnull ItemStack stack) {

		return oreStacks.get(getOreID(stack));
	}

	/**
	 * Do not under ANY circumstances EVER modify these stacks. This is a direct return for time saving reasons.
	 * 
	 * Forge's Ore Dictionary has an O(N) copy here because it assumes all modders are stupid. But you're not stupid, right?
	 * 
	 * DO NOT MODIFY THESE.
	 */
	public static ArrayList<ItemStack> getOres(String name) {

		return oreStacks.get(getOreID(name));
	}

	/**
	 * Do not under ANY circumstances EVER modify this array. This is a direct return for time saving reasons.
	 * 
	 * Forge's Ore Dictionary has an O(N) copy here because it assumes all modders are stupid. But you're not stupid, right?
	 * 
	 * DO NOT MODIFY THIS.
	 */
	public static String[] getOreNames() {

		return oreNames;
	}

}
