package powercrystals.minefactoryreloaded.render;

import codechicken.lib.model.DummyBakedModel;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import powercrystals.minefactoryreloaded.MFRProps;
import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.core.MFRDyeColor;

public class ModelHelper {

	public static final DummyBakedModel DUMMY_MODEL = new DummyBakedModel();

	public static void registerModel(Item item, String modelName) {
	
		registerModel(item, modelName, "inventory");
	}

	public static void registerModel(Item item, String modelName, String variant) {
		
		registerModel(item, 0, modelName, variant);
	}

	public static void registerModel(Item item, int meta, String modelName) {
		
		registerModel(item, meta, modelName, "inventory");
	}

	public static void registerModel(Item item, int meta, String modelName, String variant) {

		ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(MFRProps.PREFIX + modelName, variant));
	}

	public static void registerModel(Block block, String propertyName, String[] values, IProperty<?>... propertiesToIgnore) {

		if (propertiesToIgnore.length > 0)
			ModelLoader.setCustomStateMapper(block, new StateMap.Builder().ignore(propertiesToIgnore).build());

		Item item = MFRRegistry.getItemBlock(block);
		if (item != null) {
			for (int i = 0; i < values.length; i++) {
				ModelLoader.setCustomModelResourceLocation(item, i, new ModelResourceLocation(block.getRegistryName(), propertyName + "=" + values[i]));
			}
		}
	}

	public static void registerModel(Block block, ModelResourceLocation modelLocation) {

		ModelLoader.setCustomStateMapper(block, new StateMapperBase() {
			@Override
			protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
				return modelLocation;
			}
		});
		Item item = MFRRegistry.getItemBlock(block);
		if (item != null)
			ModelLoader.setCustomModelResourceLocation(item, 0,  modelLocation);
	}

	public static void registerModel(Block block, IProperty<?>... propertiesToIgnore) {

		if (propertiesToIgnore.length > 0)
			ModelLoader.setCustomStateMapper(block, new StateMap.Builder().ignore(propertiesToIgnore).build());

		Item item = MFRRegistry.getItemBlock(block);
		if (item != null)
			ModelLoader.setCustomModelResourceLocation(item, 0,  new ModelResourceLocation(block.getRegistryName(), "normal"));
	}

	public static void registerColoredItemModels(Item item, String modelName) {

		for (MFRDyeColor color : MFRDyeColor.values()) {
			ModelLoader.setCustomModelResourceLocation(item, color.ordinal(), new ModelResourceLocation(MFRProps.PREFIX + modelName, "color=" + color.getName()));
		}
	}
}
