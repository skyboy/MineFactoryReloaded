package powercrystals.minefactoryreloaded.render.tileentity;

import codechicken.lib.render.item.IItemRenderer;
import codechicken.lib.texture.TextureUtils;
import codechicken.lib.util.TransformUtils;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.PerspectiveMapWrapper;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL11;
import powercrystals.minefactoryreloaded.MFRProps;
import powercrystals.minefactoryreloaded.render.model.RedNetHistorianModel;
import powercrystals.minefactoryreloaded.tile.rednet.TileEntityRedNetHistorian;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import java.util.ArrayList;
import java.util.List;

public class RedNetHistorianRenderer extends TileEntitySpecialRenderer<TileEntityRedNetHistorian.Client>
		implements IItemRenderer, IBakedModel {

	private static final ResourceLocation historianTex = new ResourceLocation(
			MFRProps.TILE_ENTITY_FOLDER + "historian.png");
	private static ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> transformations;

	private RedNetHistorianModel model;
	private static final double renderMin = 1.0 / 16.0;
	private static final double renderMax = 15.0 / 16.0;

	public RedNetHistorianRenderer() {

		model = new RedNetHistorianModel();

		TRSRTransformation thirdPerson = TransformUtils.create(0, 3, 3, 0, 0, 0, 0.375f);
		ImmutableMap.Builder<ItemCameraTransforms.TransformType, TRSRTransformation> builder = ImmutableMap.builder();
		builder.put(ItemCameraTransforms.TransformType.GUI, TransformUtils.create(3, -1, 0, 30, 45, 0, 0.625f));
		builder.put(ItemCameraTransforms.TransformType.GROUND, TransformUtils.create(0, 0, 0, 0, 0, 0, 0.25f));
		builder.put(ItemCameraTransforms.TransformType.FIXED, TransformUtils.create(0, 0, 3, 0, 0, 0, 0.5f));
		builder.put(ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, thirdPerson);
		builder.put(ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND, TransformUtils.flipLeft(thirdPerson));
		builder.put(ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND,
				TransformUtils.create(0, 3, 2, -30, -70, 0, 0.4f));
		builder.put(ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND, TransformUtils.create(0, 3, 2, -30, -70, 0, 0.4f));
		transformations = builder.build();
	}

	@Override
	public void render(TileEntityRedNetHistorian.Client historian, double x, double y, double z, float partialTicks, int destroyStage,
			float alpha) {

		TextureManager renderEngine = Minecraft.getMinecraft().renderEngine;

		if (renderEngine != null) {
			renderEngine.bindTexture(historianTex);
		}

		GlStateManager.pushMatrix();

		GlStateManager.translate((float) x, (float) y, (float) z);

		if (historian.getDirectionFacing() == EnumFacing.EAST) {
			GlStateManager.translate(1, 0, 0);
			GlStateManager.rotate(270, 0, 1, 0);
		} else if (historian.getDirectionFacing() == EnumFacing.SOUTH) {
			GlStateManager.translate(1, 0, 1);
			GlStateManager.rotate(180, 0, 1, 0);
		} else if (historian.getDirectionFacing() == EnumFacing.WEST) {
			GlStateManager.translate(0, 0, 1);
			GlStateManager.rotate(90, 0, 1, 0);
		}

		model.render(historian);

		GlStateManager.pushAttrib();
		RenderHelper.disableStandardItemLighting();
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);

		GlStateManager.disableTexture2D();

		Tessellator t = Tessellator.getInstance();
		BufferBuilder buffer = t.getBuffer();
		buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
		GlStateManager.glLineWidth(2.0F);

		Integer[] values = historian.getValues();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

		int yMin = Integer.MAX_VALUE;
		int yMax = Integer.MIN_VALUE;

		for (Integer v : values) {
			if (v == null) {
				continue;
			}
			if (v > yMax) {
				yMax = v;
			}
			if (v < yMin) {
				yMin = v;
			}
		}

		yMax = Math.max(yMax, 15);
		yMin = Math.min(yMin, 0);

		Integer lastValue = null;
		int lastX = 0;
		for (int i = 1; i < values.length; i++) {
			if (values[i] == null) {
				continue;
			}
			if (lastValue == null) {
				lastValue = values[i];
				lastX = i;
			} else {
				double x1 = (14.0 / 16.0) / values.length * lastX + (1.0 / 16.0);
				double x2 = (14.0 / 16.0) / values.length * (i) + (1.0 / 16.0);
				double y1 = (values[i - 1] - yMin) * (renderMax - renderMin) / (yMax - yMin) + renderMin;
				double y2 = (values[i] - yMin) * (renderMax - renderMin) / (yMax - yMin) + renderMin;

				buffer.pos(x1, y1, 0.253).endVertex();
				buffer.pos(x2, y2, 0.253).endVertex();

				lastValue = values[i];
				lastX = i;
			}
		}

		t.draw();

		GlStateManager.enableLighting();
		GlStateManager.enableTexture2D();
		GlStateManager.popAttrib();
		GlStateManager.popMatrix();

	}

	@Override
	public void renderItem(@Nonnull ItemStack item, ItemCameraTransforms.TransformType transformType) {

		GlStateManager.pushMatrix();

		TextureUtils.changeTexture(historianTex);

		model.render(null);

		GlStateManager.popMatrix();
	}

	@Override
	public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType) {

		return PerspectiveMapWrapper.handlePerspective(this, transformations, cameraTransformType);
	}

	@Override
	public IModelState getTransforms() {

		return null;
	}

	@Override
	public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {

		return new ArrayList<>();
	}

	@Override
	public boolean isAmbientOcclusion() {

		return false;
	}

	@Override
	public boolean isGui3d() {

		return false;
	}

	@Override
	public boolean isBuiltInRenderer() {

		return true;
	}

	@Override
	public TextureAtlasSprite getParticleTexture() {

		return null;
	}

	@Override
	public ItemCameraTransforms getItemCameraTransforms() {

		return ItemCameraTransforms.DEFAULT;
	}

	@Override
	public ItemOverrideList getOverrides() {

		return ItemOverrideList.NONE;
	}
}
