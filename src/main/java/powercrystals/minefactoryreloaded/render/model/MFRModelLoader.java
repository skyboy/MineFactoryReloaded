package powercrystals.minefactoryreloaded.render.model;

import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;

import java.util.HashMap;
import java.util.Map;

public enum MFRModelLoader implements ICustomModelLoader {
	INSTANCE;

	private static Map<ResourceLocation, IModel> models = new HashMap<>();

	public static void registerModel(ResourceLocation modelLocation, IModel model) {

		models.put(modelLocation, model);
	}

	@Override
	public boolean accepts(ResourceLocation modelLocation) {

		return models.keySet().contains(modelLocation);
	}

	@Override
	public IModel loadModel(ResourceLocation modelLocation) {

		return models.getOrDefault(modelLocation, null);
	}

	@Override
	public void onResourceManagerReload(IResourceManager resourceManager) {
		// no need to clear cache since we create a new model instance
	}
}
