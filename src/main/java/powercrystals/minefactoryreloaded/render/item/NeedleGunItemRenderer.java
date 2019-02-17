package powercrystals.minefactoryreloaded.render.item;

import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.OBJParser;
import codechicken.lib.texture.TextureUtils;
import codechicken.lib.util.TransformUtils;
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

public class NeedleGunItemRenderer extends BaseItemRenderer {

	private static CCModel gunModel;
	private static CCModel magazineModel;
	private static ResourceLocation textureLocation = new ResourceLocation(MFRProps.MODEL_TEXTURE_FOLDER + "needle_gun.png");

	public NeedleGunItemRenderer() {

		Map<String, CCModel> models = OBJParser
				.parseModels(new ResourceLocation(MFRProps.MODEL_FOLDER + "needle_gun.obj"), new SwapYZ());
		gunModel = models.get("gun");
		magazineModel = models.get("magazine");

		TRSRTransformation thirdPerson = TransformUtils.create(0, 0, 0, 90, 180, 0, 0.025f);
		ImmutableMap.Builder<ItemCameraTransforms.TransformType, TRSRTransformation> builder = ImmutableMap.builder();
		builder.put(ItemCameraTransforms.TransformType.GUI, TransformUtils.create(-3, -1, 0, 30, 135, 0, 0.02f));
		builder.put(ItemCameraTransforms.TransformType.GROUND, TransformUtils.create(0, 3, 0, 0, 0, 0, 0.02f));
		builder.put(ItemCameraTransforms.TransformType.FIXED, TransformUtils.create(-5, 0, 0, 0, 90, 0, 0.03f));
		builder.put(ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, thirdPerson);
		builder.put(ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND, TransformUtils.flipLeft(thirdPerson));
		builder.put(ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND, TransformUtils.create(4, 0, 0, 8, 190, 0, 0.025f));
		builder.put(ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND, TransformUtils.create(4, 0, 0, 8, 190, 0, 0.025f));
		transformations = builder.build();
	}

	@Override
	public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType) {

		return PerspectiveMapWrapper.handlePerspective(this, transformations, cameraTransformType);
	}

	protected void drawModel(CCRenderState ccrs, @Nonnull ItemStack stack) {

		TextureUtils.changeTexture(textureLocation);
		ccrs.startDrawing(4, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
		
		gunModel.render(ccrs);
		if (stack.getTagCompound() != null && stack.getTagCompound().hasKey("ammo") &&
				!stack.getTagCompound().getCompoundTag("ammo").hasNoTags()) {
			magazineModel.render(ccrs);
		}

		ccrs.draw();
	}
}
