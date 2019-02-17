package powercrystals.minefactoryreloaded.setup;

import com.google.common.collect.ImmutableList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootEntryTable;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import powercrystals.minefactoryreloaded.MFRProps;

import java.util.List;

public class MFRLoot {

	private static final List<String> CHEST_TABLES;
	static {
		
		ImmutableList.Builder<String> builder = new ImmutableList.Builder<>();
		builder.add("abandoned_mineshaft", "desert_pyramid", "jungle_temple", "jungle_temple_dispenser", "simple_dungeon");
		
		if (MFRConfig.enableMassiveTree.getBoolean(true)) {
			builder.add("stronghold_library");
		}
		CHEST_TABLES = builder.build();
	} 
	private static final List<String> FISHING_TABLES = ImmutableList.of("junk", "treasure");
	
	private	static final String CHESTS_PREFIX = "minecraft:chests/";
	private	static final String FISHING_PREFIX = "minecraft:gameplay/fishing/";
	public static final ResourceLocation ZOOLOGIST_CHEST = new ResourceLocation(MFRProps.PREFIX + "chests/zoologist");
	public static final ResourceLocation FACTORY_BAG = new ResourceLocation(MFRProps.PREFIX + "factory_bag");
	
	private static final MFRLoot INSTANCE = new MFRLoot();
	private MFRLoot() {}

	public static void init() {
		
		registerLootTables(CHEST_TABLES, "inject/chests/");
		registerLootTables(FISHING_TABLES, "inject/gameplay/fishing/");
		LootTableList.register(ZOOLOGIST_CHEST);
		LootTableList.register(FACTORY_BAG);

		MinecraftForge.EVENT_BUS.register(INSTANCE);
	}

	private static void registerLootTables(List<String> list, String prefix) {
		
		for(String s : list) {
			LootTableList.register(new ResourceLocation(MFRProps.MOD_ID, prefix + s));
		}
	}

	@SubscribeEvent
	public void lootLoad(LootTableLoadEvent evt) {

		injectLootPool(evt, CHESTS_PREFIX, CHEST_TABLES);
		injectLootPool(evt, FISHING_PREFIX, FISHING_TABLES);
	}

	private void injectLootPool(LootTableLoadEvent evt, String prefix, List<String> list) {
		
		String name = evt.getName().toString();

		if(name.startsWith(prefix) && list.contains(name.substring(prefix.length()))) {
			String file = name.substring("minecraft:".length());
			evt.getTable().addPool(getInjectPool(file));
		}
	}

	private LootPool getInjectPool(String entryName) {
		
		return new LootPool(new LootEntry[] {getInjectEntry(entryName, 1)}, new LootCondition[0], new RandomValueRange(1), new RandomValueRange(0, 1), "mfr_inject_pool");
	}

	private LootEntryTable getInjectEntry(String name, int weight) {
		return new LootEntryTable(new ResourceLocation(MFRProps.MOD_ID, "inject/" + name), weight, 0, new LootCondition[0], "mfr_inject_entry");
	}
}
