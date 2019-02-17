package powercrystals.minefactoryreloaded.setup.datafix;

import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.datafix.DataFixesManager;
import net.minecraft.util.datafix.IDataFixer;
import net.minecraft.util.datafix.IDataWalker;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import powercrystals.minefactoryreloaded.setup.MFRThings;

public class FixPlasticBags implements IDataWalker {

	@Override
	public NBTTagCompound process(IDataFixer fixer, NBTTagCompound tag, int versionIn) {

		ResourceLocation id = null;
		if (tag.hasKey("id", Constants.NBT.TAG_STRING)) {
			id = new ResourceLocation(tag.getString("id"));
		}
		// FORGEFIX: "id" of old mod items is not converted to string as of 14.23.3.2655
		else if (tag.hasKey("id", Constants.NBT.TAG_ANY_NUMERIC)) {
			id = ForgeRegistries.ITEMS.getKey(Item.getItemById(tag.getInteger("id")));
		}

		if (!MFRThings.plasticBagItem.getRegistryName().equals(id)) {
			return tag;
		}
		if (!tag.hasKey("id", Constants.NBT.TAG_STRING)) {
			tag.setString("id", id.toString());
		}

		if (!tag.hasKey("tag", Constants.NBT.TAG_COMPOUND)) {
			return tag;
		}

		NBTTagCompound itemData = tag.getCompoundTag("tag");

		if (itemData.hasKey("inventory", Constants.NBT.TAG_COMPOUND)) {
			itemData.setTag("Inventory", itemData.getCompoundTag("inventory"));
			itemData.removeTag("inventory");
		}

		if (itemData.hasKey("Inventory", Constants.NBT.TAG_COMPOUND)) {
			NBTTagCompound inv = itemData.getCompoundTag("Inventory");
			for (String slot : inv.getKeySet()) {
				DataFixesManager.processItemStack(fixer, inv, versionIn, slot);
			}
		}


		return tag;
	}

}
