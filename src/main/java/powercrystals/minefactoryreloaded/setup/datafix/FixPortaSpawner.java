package powercrystals.minefactoryreloaded.setup.datafix;

import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.datafix.FixTypes;
import net.minecraft.util.datafix.IDataFixer;
import net.minecraft.util.datafix.IDataWalker;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import powercrystals.minefactoryreloaded.item.ItemPortaSpawner;
import powercrystals.minefactoryreloaded.setup.MFRThings;

public class FixPortaSpawner implements IDataWalker {

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

		if (!MFRThings.portaSpawnerItem.getRegistryName().equals(id)) {
			return tag;
		}
		if (!tag.hasKey("id", Constants.NBT.TAG_STRING)) {
			tag.setString("id", id.toString());
		}

		if (tag.hasKey("tag", Constants.NBT.TAG_COMPOUND)) {
			if (tag.hasKey(ItemPortaSpawner.spawnerTag, Constants.NBT.TAG_COMPOUND)) {
				fixer.process(FixTypes.BLOCK_ENTITY, tag.getCompoundTag(ItemPortaSpawner.spawnerTag), versionIn);
			}
		}

		return tag;
	}

}
