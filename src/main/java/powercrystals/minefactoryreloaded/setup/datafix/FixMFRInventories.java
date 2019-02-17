package powercrystals.minefactoryreloaded.setup.datafix;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.datafix.DataFixesManager;
import net.minecraft.util.datafix.IDataFixer;
import net.minecraft.util.datafix.IDataWalker;
import powercrystals.minefactoryreloaded.MFRProps;

public class FixMFRInventories implements IDataWalker {

	private static String[] INVENTORY_TAGS = new String[] {
			"Items", // FactoryInventory
			"DropItems", // FactoryInventory
			"OutItems", // LiquiCrafter
			"SmashedItems" // BlockSmasher
	};

	@Override
	public NBTTagCompound process(IDataFixer fixer, NBTTagCompound tag, int versionIn) {

		ResourceLocation id = new ResourceLocation(tag.getString("id"));
		if (MFRProps.MOD_ID.equals(id.getResourceDomain())) {
			for (String s : INVENTORY_TAGS)
				tag = DataFixesManager.processInventory(fixer, tag, versionIn, s);

			tag = DataFixesManager.processItemStack(fixer, tag, versionIn, "storedStack"); // DeepStorageUnit
		}

		return tag;
	}

}
