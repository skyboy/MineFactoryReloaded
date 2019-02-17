package powercrystals.minefactoryreloaded.setup.datafix;

import com.google.common.collect.Lists;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.datafix.FixTypes;
import net.minecraft.util.datafix.IDataFixer;
import net.minecraft.util.datafix.IDataWalker;
import net.minecraft.util.datafix.IFixableData;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.registries.GameData;
import powercrystals.minefactoryreloaded.item.ItemSafariNet;
import powercrystals.minefactoryreloaded.setup.MFRThings;

import javax.annotation.Nonnull;
import java.util.List;

public class FixSafariNet implements IDataWalker, IFixableData {

	private static List<ResourceLocation> SAFARI_NETS;

	private static List<ResourceLocation> getSafariNets() {

		if (SAFARI_NETS != null) {
			return SAFARI_NETS;
		}
		return SAFARI_NETS = Lists.newArrayList(
				MFRThings.safariNetFancyJailerItem.getRegistryName(),
				MFRThings.safariNetJailerItem.getRegistryName(),
				MFRThings.safariNetSingleItem.getRegistryName(),
				MFRThings.safariNetItem.getRegistryName()
		);
	}

	/*
	 * IDataWalker
	 */
	@Override
	public NBTTagCompound process(IDataFixer fixer, NBTTagCompound tag, int versionIn) {

		ResourceLocation id = null;
		if (tag.hasKey("id", Constants.NBT.TAG_STRING)) {
			id = new ResourceLocation(tag.getString("id"));
		}

		if (tag.hasKey("tag", Constants.NBT.TAG_COMPOUND)) {

			NBTTagCompound itemData = tag.getCompoundTag("tag");
			if (itemData.hasKey("EntityData", Constants.NBT.TAG_COMPOUND) &&
					ForgeRegistries.ITEMS.getValue(id) instanceof ItemSafariNet) {

				fixer.process(FixTypes.ENTITY, itemData.getCompoundTag("EntityData"), versionIn);
			}
		}

		return tag;
	}

	/*
	 * IFixableData
	 */
	@Override
	public int getFixVersion() {

		return 50;
	}

	@Override
	@Nonnull
	public NBTTagCompound fixTagCompound(NBTTagCompound tag) {

		ResourceLocation id = null;
		if (tag.hasKey("id", Constants.NBT.TAG_STRING)) {
			id = new ResourceLocation(tag.getString("id"));
		}
		// FORGEFIX: "id" of old mod items is not converted to string as of 14.23.3.2655
		else if (tag.hasKey("id", Constants.NBT.TAG_ANY_NUMERIC)) {
			id = ForgeRegistries.ITEMS.getKey(Item.getItemById(tag.getInteger("id")));
		}

		if (!getSafariNets().contains(id)) {
			return tag;
		}
		if (!tag.hasKey("id", Constants.NBT.TAG_STRING)) {
			tag.setString("id", id.toString());
		}

		boolean hadTag = false;
		if (tag.hasKey("tag", Constants.NBT.TAG_COMPOUND)) {
			NBTTagCompound itemData = tag.getCompoundTag("tag");
			if (itemData.hasKey("id", Constants.NBT.TAG_STRING)) {
				NBTTagCompound display = null;
				if (itemData.hasKey("display", Constants.NBT.TAG_COMPOUND)) {
					display = itemData.getCompoundTag("display");
					itemData.removeTag("display");
				}
				NBTTagCompound newData = new NBTTagCompound();
				if (display != null) {
					newData.setTag("display", display);
				}
				newData.setTag("EntityData", itemData);
				tag.setTag("tag", newData);
				hadTag = true;
			} else if (itemData.hasKey("hide", Constants.NBT.TAG_BYTE)) {
				NBTTagCompound newData = new NBTTagCompound();
				newData.setBoolean("mfr:hide", itemData.getBoolean("hide"));
				itemData.removeTag("hide");
				itemData.setTag("EntityData", newData);
				hadTag = true;
			}
		}
		if (!hadTag) {
			NBTTagCompound itemData = tag.getCompoundTag("tag");
			int meta = tag.getShort("Damage");
			if (meta > 0) {
				EntityEntry entry = GameData.getEntityRegistry().getValue(meta);
				if (entry != null) {
					NBTTagCompound entityData = new NBTTagCompound();
					entityData.setString("id", entry.getRegistryName().toString());
					itemData.setTag("EntityData", entityData);
				} else {
					// well, we HAD an entity. time to play "Chicken or TNT riding an xp orb!"
					NBTTagCompound mystery = new NBTTagCompound();
					mystery.setBoolean("hide", true);
					itemData.setTag("EntityData", mystery);
				}
				tag.setTag("tag", itemData);
				tag.setShort("Damage", (short) 0);
			}
		}
		return tag;
	}

}
