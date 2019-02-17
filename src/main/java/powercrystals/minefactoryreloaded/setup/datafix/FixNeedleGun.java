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

public class FixNeedleGun implements IDataWalker {

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

		if (!MFRThings.needlegunItem.getRegistryName().equals(id)) {
			return tag;
		}
		if (!tag.hasKey("id", Constants.NBT.TAG_STRING)) {
			tag.setString("id", id.toString());
		}

		if (tag.hasKey("tag", Constants.NBT.TAG_COMPOUND)) {
			DataFixesManager.processItemStack(fixer, tag.getCompoundTag("tag"), versionIn, "ammo");
		}

		return null;
	}
}
