package powercrystals.minefactoryreloaded.render.item;

import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.OBJParser;
import codechicken.lib.texture.TextureUtils;
import codechicken.lib.util.TransformUtils;
import codechicken.lib.vec.Scale;
import codechicken.lib.vec.SwapYZ;
import com.google.common.collect.ImmutableMap;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.PerspectiveMapWrapper;
import net.minecraftforge.common.model.TRSRTransformation;
import org.apache.commons.lang3.tuple.Pair;
import powercrystals.minefactoryreloaded.MFRProps;

import javax.annotation.Nonnull;
import javax.vecmath.Matrix4f;
import java.util.Map;

public class RocketLauncherItemRenderer extends BaseItemRenderer {

	CCModel launcherModel;
	RocketLauncherItemRenderer offHandRenderer;
	private static ResourceLocation textureLocation = new ResourceLocation(MFRProps.MODEL_TEXTURE_FOLDER + "rocket_launcher.png");

	private RocketLauncherItemRenderer(boolean offHand) {

		Map<String, CCModel> models = OBJParser
				.parseModels(new ResourceLocation(MFRProps.MODEL_FOLDER + "rocket_launcher.obj"), new SwapYZ());
		launcherModel = models.get("Box009");
	}

	public RocketLauncherItemRenderer() {

		offHandRenderer = new RocketLauncherItemRenderer(true);

		Map<String, CCModel> models = OBJParser.parseModels(new ResourceLocation(MFRProps.MODEL_FOLDER + "rocket_launcher.obj"), new SwapYZ());
		launcherModel = models.get("Box009").copy().apply(new Scale(-1, 1, 1)).backfacedCopy();

		setupTransformations();
	}

	private void setupTransformations() {
		TRSRTransformation thirdPerson = TransformUtils.create(0, 0, 2, 0, 180, 0, 0.02f);
		ImmutableMap.Builder<ItemCameraTransforms.TransformType, TRSRTransformation> builder = ImmutableMap.builder();
		builder.put(ItemCameraTransforms.TransformType.GUI, TransformUtils.create(0, -1, 0, 30, 135, 0, 0.015f));
		builder.put(ItemCameraTransforms.TransformType.GROUND, TransformUtils.create(0, 3, 0, 0, 0, 0, 0.01f));
		builder.put(ItemCameraTransforms.TransformType.FIXED, TransformUtils.create(0, 0, 0, 0, 90, 0, 0.03f));
		builder.put(ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, thirdPerson);
		builder.put(ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND, TransformUtils.flipLeft(thirdPerson));
		builder.put(ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND, TransformUtils.create(0, 5, 4, 8, 180, 0, 0.025f));
		builder.put(ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND, TransformUtils.create(0, 5, 4, 8, 180, 0, 0.025f));
		transformations = builder.build();
	}

	@Override
	public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType) {

		if (cameraTransformType == ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND
				|| cameraTransformType == ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND)
			return PerspectiveMapWrapper.handlePerspective(offHandRenderer, transformations, cameraTransformType);

		return PerspectiveMapWrapper.handlePerspective(this, transformations, cameraTransformType);
	}

	@Override
	protected void drawModel(CCRenderState ccrs, @Nonnull ItemStack stack) {

		TextureUtils.changeTexture(textureLocation);
		ccrs.startDrawing(4, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);

		launcherModel.render(ccrs);

		ccrs.draw();
	}
}
