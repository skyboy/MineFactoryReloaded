package powercrystals.minefactoryreloaded.core;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Items;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.WeightedRandom;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.Map.Entry;

public class AutoEnchantmentHelper extends EnchantmentHelper {

	@Nonnull
	public static ItemStack addRandomEnchantment(Random rand, @Nonnull ItemStack stack, int level) {

		return addRandomEnchantment(rand, stack, level, false);
	}

	@Nonnull
	public static ItemStack addRandomEnchantment(Random rand, @Nonnull ItemStack stack, int level, boolean invalid) {

		if (stack.isEmpty()) {
			return ItemStack.EMPTY;
		}
		@Nonnull ItemStack output = stack.splitStack(1);

		List<EnchantmentData> enchantments = buildEnchantmentList(rand, output, level, !invalid);
		if (enchantments == null) {
			return output;
		}

		Map<Enchantment, Integer> existingEnchants = getEnchantments(output);

		boolean isBook = output.getItem().equals(Items.BOOK);

		if (isBook) {
			output = new ItemStack(Items.ENCHANTED_BOOK);
		}

		Collections.shuffle(enchantments);

		outerList:
		for (EnchantmentData newEnchant : enchantments) {
			if (isBook) {
				ItemEnchantedBook.addEnchantment(output, newEnchant);
				return output;
			} else {
				for (Entry<Enchantment, Integer> oldEnchant : existingEnchants.entrySet()) {
					if (oldEnchant.getKey().equals(newEnchant.enchantment)) {
						if (oldEnchant.getValue() <= newEnchant.enchantmentLevel) {
							updateEnchantment(output, oldEnchant.getKey(), (short) newEnchant.enchantmentLevel);
						}
						continue outerList;
					}
				}
				output.addEnchantment(newEnchant.enchantment, newEnchant.enchantmentLevel);
			}
		}

		return output;
	}

	private static void updateEnchantment(@Nonnull ItemStack stack, Enchantment enchantment, short newLevel) {

		NBTTagList tagList = stack.getTagCompound().getTagList("ench", 10);
		for (int i = 0, e = tagList.tagCount(); i < e; ++i) {
			NBTTagCompound entry = tagList.getCompoundTagAt(i);
			if (Enchantment.getEnchantmentByID(entry.getShort("id")) == enchantment) {
				entry.setShort("lvl", newLevel);
			}
		}
		stack.getTagCompound().setTag("ench", tagList);
	}

	public static List<EnchantmentData> buildEnchantmentList(Random rand, @Nonnull ItemStack stack,
			int level, boolean blockInvalid) {

		int itemEnchantability = stack.getItem().getItemEnchantability(stack);

		if (itemEnchantability <= 0) {
			return null;
		} else {
			itemEnchantability /= 2;
			itemEnchantability = 1 + rand.nextInt((itemEnchantability >> 1) + 1) +
					rand.nextInt((itemEnchantability >> 1) + 1);
			int var5 = itemEnchantability + level;
			float var6 = (rand.nextFloat() + rand.nextFloat() - 1.0F) * 0.15F;
			int targetEnchantability = (int) (var5 * (1.0F + var6) + 0.5F);

			if (targetEnchantability < 1) {
				targetEnchantability = 1;
			}

			List<EnchantmentData> returnList = null;
			List<EnchantmentData> enchantData = EnchantmentHelper.getEnchantmentDatas((blockInvalid ?
					targetEnchantability : Math.min(40, targetEnchantability)), stack, false);

			if (!enchantData.isEmpty()) {
				EnchantmentData enchData = WeightedRandom.getRandomItem(rand, enchantData);

				{
					returnList = new ArrayList<>();
					returnList.add(enchData);

					for (int i = targetEnchantability; rand.nextInt(50) <= i; i >>= 1) {
						if (blockInvalid)
							for (Iterator<EnchantmentData> iter = enchantData.iterator(); iter.hasNext(); ) {
								Enchantment ench = iter.next().enchantment;
								for (EnchantmentData newEnchantment : returnList)
									if (!newEnchantment.enchantment.isCompatibleWith(ench)) {
										iter.remove();
										break;
									}
							}

						if (!enchantData.isEmpty()) {
							EnchantmentData randomEnchant = WeightedRandom.getRandomItem(rand, enchantData);
							returnList.add(randomEnchant);
						} else
							break;
					}
				}
			}

			return returnList;
		}
	}

}
