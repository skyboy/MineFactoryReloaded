package powercrystals.minefactoryreloaded.setup.recipe.handler;

import cofh.core.util.helpers.RecipeHelper;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.RecipeSorter;
import powercrystals.minefactoryreloaded.core.UtilInventory;
import powercrystals.minefactoryreloaded.setup.Machine;

import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.List;

public abstract class ShapelessMachineTinker extends ShapelessRecipes {

	protected List<List<ItemStack>> _tinkerItems;
	@Nonnull
	protected ItemStack _machine;

	private static @Nonnull ItemStack createMachineWithLore(Machine machine, String lore) {

		@Nonnull ItemStack o = machine.getItemStack();
		NBTTagCompound tag = new NBTTagCompound();
		tag.setTag("display", new NBTTagCompound());
		NBTTagList list = new NBTTagList();
		tag.getCompoundTag("display").setTag("Lore", list);
		list.appendTag(new NBTTagString(lore));
		o.setTagCompound(tag);
		return o;
	}

	private static NonNullList<Ingredient> createIngredientListForNEI(Machine machine, @Nonnull ItemStack... items) {

		NonNullList<Ingredient> ret = RecipeHelper.buildInput(items);
		ret.add(Ingredient.fromStacks(machine.getItemStack()));
		return ret;
	}

	private ShapelessMachineTinker(Machine machine, ItemStack output, NonNullList<Ingredient> ingredients) {

		super(RecipeHelper.getNameForRecipe(output).toString(), output, ingredients);
		_machine = machine.getItemStack();
		_tinkerItems = new LinkedList<>();

		RecipeSorter.register("minefactoryreloaded:shapelessTinker", getClass(), RecipeSorter.Category.SHAPELESS,
				"after:minecraft:shapeless");
	}

	public ShapelessMachineTinker(@Nonnull Machine machine, String lore, String... tinkerItems) {

		this(machine, createMachineWithLore(machine, lore), NonNullList.create());
		for (String s : tinkerItems)
			_tinkerItems.add(OreDictionary.getOres(s));
	}

	protected ShapelessMachineTinker(@Nonnull Machine machine, String lore, @Nonnull ItemStack... tinkerItems) {

		this(machine, createMachineWithLore(machine, lore), createIngredientListForNEI(machine, tinkerItems));
		for (@Nonnull ItemStack s : tinkerItems) {
			List<ItemStack> l = new LinkedList<>();
			l.add(s);
			_tinkerItems.add(l);
		}
	}

	protected abstract boolean isMachineTinkerable(@Nonnull ItemStack machine);

	@Nonnull
	protected abstract ItemStack getTinkeredMachine(@Nonnull ItemStack machine);

	@Override
	public boolean matches(InventoryCrafting grid, World world) {

		int size = grid.getSizeInventory();
		boolean foundMachine = false;

		List<List<ItemStack>> items = new LinkedList<>();
		items.addAll(_tinkerItems);

		while (size-- > 0) {
			@Nonnull ItemStack gridItem = grid.getStackInSlot(size);
			if (gridItem.isEmpty())
				continue;

			if (UtilInventory.stacksEqual(_machine, gridItem, false))
				if (foundMachine || !isMachineTinkerable(gridItem))
					return false;
				else
					foundMachine = true;
			else
				lists:{
					if (foundMachine && items.isEmpty())
						return true;
					for (List<ItemStack> l : items)
						for (@Nonnull ItemStack i : l)
							if (UtilInventory.stacksEqual(gridItem, i)) {
								items.remove(l);
								break lists;
							}
					return false;
				}
		}

		return foundMachine && items.isEmpty();
	}

	@Nonnull
	@Override
	public ItemStack getCraftingResult(InventoryCrafting grid) {

		int size = grid.getSizeInventory();

		while (size-- > 0) {
			@Nonnull ItemStack gridItem = grid.getStackInSlot(size);
			if (UtilInventory.stacksEqual(_machine, gridItem, false))
				if (isMachineTinkerable(gridItem))
					return getTinkeredMachine(gridItem);
		}

		return ItemStack.EMPTY;
	}
}
