package powercrystals.minefactoryreloaded;

import net.minecraft.util.datafix.FixTypes;
import net.minecraft.util.datafix.IFixType;
import net.minecraft.util.datafix.walkers.ItemStackData;
import net.minecraftforge.common.util.CompoundDataFixer;
import net.minecraftforge.common.util.ModFixs;
import net.minecraftforge.fml.common.FMLCommonHandler;
import powercrystals.minefactoryreloaded.entity.EntityFlyingItem;
import powercrystals.minefactoryreloaded.entity.EntityNeedle;
import powercrystals.minefactoryreloaded.entity.EntitySafariNet;
import powercrystals.minefactoryreloaded.setup.datafix.*;

public class DataFixer {

	private static final IFixType TYPE = null;

	@SuppressWarnings("deprecated")
	static void init() {

		CompoundDataFixer datafixer = FMLCommonHandler.instance().getDataFixer();
		ModFixs fixer = datafixer.init(MFRProps.MOD_ID, MFRProps.DATA_VERSION);
		fixer.registerFix(FixTypes.BLOCK_ENTITY, new TileIdFix());
		fixer.registerFix(FixTypes.ITEM_INSTANCE, new FixSafariNet());

		// this method name implies something else
		datafixer.registerVanillaWalker(FixTypes.BLOCK_ENTITY, new FixMFRInventories());
		datafixer.registerVanillaWalker(FixTypes.ITEM_INSTANCE, new FixPlasticBags());
		datafixer.registerVanillaWalker(FixTypes.ITEM_INSTANCE, new FixNeedleGun());
		datafixer.registerVanillaWalker(FixTypes.ITEM_INSTANCE, new FixSafariNet());
		datafixer.registerVanillaWalker(FixTypes.ITEM_INSTANCE, new FixPortaSpawner());
		datafixer.registerVanillaWalker(FixTypes.ENTITY, new ItemStackData(EntitySafariNet.class, "safariNetStack"));
		datafixer.registerVanillaWalker(FixTypes.ENTITY, new ItemStackData(EntityFlyingItem.class, "safariNetStack"));
		datafixer.registerVanillaWalker(FixTypes.ENTITY, new ItemStackData(EntityNeedle.class, "ammoSource"));
	}

}
