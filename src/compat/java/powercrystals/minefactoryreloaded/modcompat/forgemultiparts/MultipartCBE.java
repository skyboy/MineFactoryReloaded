package powercrystals.minefactoryreloaded.modcompat.forgemultiparts;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.registry.GameRegistry;
import powercrystals.minefactoryreloaded.api.integration.IMFRIntegrator;

import javax.annotation.Nonnull;

import static powercrystals.minefactoryreloaded.api.integration.IMFRRecipeSet.stack;
import static powercrystals.minefactoryreloaded.modcompat.Compats.ModIds.MFR;
import static powercrystals.minefactoryreloaded.modcompat.Compats.ModIds.MULTIPARTCBE;

@IMFRIntegrator.DependsOn(MULTIPARTCBE)
public class MultipartCBE implements IMFRIntegrator {

	@GameRegistry.ObjectHolder(value = MFR + ":decorative_brick")
	public static final Item factoryDecorativeBrickBlock = Items.AIR;
	@GameRegistry.ObjectHolder(value = MFR + ":decorative_stone")
	public static final Item factoryDecorativeStoneBlock = Items.AIR;
	@GameRegistry.ObjectHolder(value = MFR + ":stained_glass_block")
	public static final Item factoryGlassBlock = Items.AIR;
	@GameRegistry.ObjectHolder(value = MFR + ":rubber_wood_leaves")
	public static final Item rubberLeavesBlock = Items.AIR;
	@GameRegistry.ObjectHolder(value = MFR + ":road")
	public static final Item factoryRoadBlock = Items.AIR;
	@GameRegistry.ObjectHolder(value = MFR + ":rubber_wood_log")
	public static final Item rubberWoodBlock = Items.AIR;
	@GameRegistry.ObjectHolder(value = MFR + ":machine_0")
	public static final Item machine_0 = Items.AIR;
	@GameRegistry.ObjectHolder(value = MFR + ":machine_1")
	public static final Item machine_1 = Items.AIR;
	@GameRegistry.ObjectHolder(value = MFR + ":machine_2")
	public static final Item machine_2 = Items.AIR;

	public void load() {

		addSubtypes(factoryDecorativeBrickBlock);
		addSubtypes(factoryDecorativeStoneBlock);
		addSubtypes(factoryGlassBlock);
		addSubtypes(factoryRoadBlock);
		for (Item block : new Item[] {machine_0, machine_1, machine_2})
			addSubtypes(block);
		addSubtypes(rubberLeavesBlock);
		sendComm(stack(rubberWoodBlock, 1, 0));
	}

	private void addSubtypes(Item item) {

		NonNullList<ItemStack> items = NonNullList.create();
		item.getSubItems(CreativeTabs.SEARCH, items);
		for (int i = items.size(); i-- > 0; )
			sendComm(items.get(i));
	}

	private void sendComm(@Nonnull ItemStack data) {

		FMLInterModComms.sendMessage(MULTIPARTCBE, "microMaterial", data);
	}

}
