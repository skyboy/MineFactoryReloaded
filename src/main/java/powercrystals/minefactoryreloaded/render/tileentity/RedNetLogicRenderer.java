package powercrystals.minefactoryreloaded.render.tileentity;

import codechicken.lib.lighting.LightModel;
import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.OBJParser;
import codechicken.lib.render.item.IItemRenderer;
import codechicken.lib.texture.TextureUtils;
import codechicken.lib.util.TransformUtils;
import codechicken.lib.vec.Scale;
import codechicken.lib.vec.uv.IconTransformation;
import cofh.core.util.helpers.RenderHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.PerspectiveMapWrapper;
import net.minecraftforge.common.model.IModelState;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL11;
import powercrystals.minefactoryreloaded.MFRProps;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.block.BlockRedNetLogic;
import powercrystals.minefactoryreloaded.render.item.RedNetCardItemRenderer;
import powercrystals.minefactoryreloaded.render.model.RedNetCardsModel;
import powercrystals.minefactoryreloaded.tile.rednet.TileEntityRedNetLogic;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RedNetLogicRenderer extends TileEntitySpecialRenderer<TileEntityRedNetLogic> implements IItemRenderer, IBakedModel {

	protected static CCModel base;
	protected static CCModel cards;
	private RedNetCardsModel cardsModel = new RedNetCardsModel();

	public static final ResourceLocation textureLocation = new ResourceLocation(
			MFRProps.PREFIX + "blocks/tile.mfr.rednet.logic");
	public static IconTransformation uvt;

	static {
		try {
			Map<String, CCModel> cableModels = OBJParser.parseModels(MineFactoryReloadedCore.class.
							getResourceAsStream("/powercrystals/minefactoryreloaded/models/RedComp.obj"),
					7, new Scale(1 / 16f));
			base = cableModels.get("case").backfacedCopy();
			compute(base);

			cards = cableModels.get("cards").backfacedCopy();
			compute(cards);
		} catch (Throwable ex) { ex.printStackTrace(); }
	}

	private static void compute(CCModel m) {

		m.computeNormals();
		m.shrinkUVs(RenderHelper.RENDER_OFFSET);
	}

	public static void updateUVT(TextureAtlasSprite icon) {

		uvt = new IconTransformation(icon);
	}

	@Override
	public void render(TileEntityRedNetLogic te, double x, double y, double z, float partialTicks, int destroyStage,
			float alpha) {

		GlStateManager.pushMatrix();

		CCRenderState ccrs = CCRenderState.instance();
		ccrs.reset();

		GlStateManager.translate(x + 0.5f, y + 0.5f, z + 0.5f);

		GlStateManager.pushMatrix();
		float rotation = getWorld().getBlockState(te.getPos()).getValue(BlockRedNetLogic.FACING).getHorizontalAngle();
		GlStateManager.rotate(-rotation, 0, 1, 0);

		net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.enableBlend();
		GlStateManager.disableCull();

		if (Minecraft.isAmbientOcclusionEnabled()) {
			GlStateManager.shadeModel(GL11.GL_SMOOTH);
		} else {
			GlStateManager.shadeModel(GL11.GL_FLAT);
		}
		ccrs.startDrawing(7, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);

		net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.enableBlend();
		GlStateManager.disableCull();

		if (Minecraft.isAmbientOcclusionEnabled()) {
			GlStateManager.shadeModel(GL11.GL_SMOOTH);
		} else {
			GlStateManager.shadeModel(GL11.GL_FLAT);
		}
		TextureUtils.bindBlockTexture();

		base.render(ccrs, LightModel.standardLightModel, uvt);
		cards.render(ccrs, LightModel.standardLightModel, uvt);

		ccrs.draw();

		// Techne sucks so do some crazy rotations to turn techne coords into
		// real coords
		GlStateManager.rotate(180, 0, 0, 1);
		GlStateManager.rotate(-90, 0, 1, 0);

		// Manually translate and then render each slot with the cards texture
		// up
		bindTexture(RedNetCardItemRenderer.textureLocation);
		GlStateManager.translate(-0.4375f, -0.375f, -0.390625f);
		renderCard(te.getLevelForSlot(0));

		GlStateManager.translate(0, 0, 0.234375f);
		renderCard(te.getLevelForSlot(1));

		GlStateManager.translate(0, 0, 0.234375f);
		renderCard(te.getLevelForSlot(2));

		GlStateManager.translate(0, 0.375f, -0.46875f);
		renderCard(te.getLevelForSlot(3));

		GlStateManager.translate(0, 0, 0.234375f);
		renderCard(te.getLevelForSlot(4));

		GlStateManager.translate(0, 0, 0.234375f);
		renderCard(te.getLevelForSlot(5));

		net.minecraft.client.renderer.RenderHelper.enableStandardItemLighting();

		GlStateManager.popMatrix();

		GlStateManager.popMatrix();
	}

	private void renderCard(int cardLevel) {

		switch (cardLevel) {
		case 1:
			cardsModel.renderLevel1(0.0625f);
			break;
		case 2:
			cardsModel.renderLevel2(0.0625f);
			break;
		case 3:
			cardsModel.renderLevel3(0.0625f);
			break;
		default:
			cardsModel.renderEmptySlot(0.0625f);
		}
	}

	@Override
	public void renderItem(@Nonnull ItemStack item, ItemCameraTransforms.TransformType transformType) {

		GlStateManager.pushMatrix();
		GlStateManager.pushAttrib();

		GlStateManager.translate(0.5f, 0.5f, 0.5f);
		GlStateManager.rotate(180, 0, 1, 0);

		net.minecraft.client.renderer.RenderHelper.enableStandardItemLighting();

		CCRenderState ccrs = CCRenderState.instance();
		ccrs.reset();

		TextureUtils.bindBlockTexture();

		ccrs.startDrawing(7, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);

		base.render(ccrs, uvt);
		cards.render(ccrs, uvt);

		ccrs.draw();

		GlStateManager.popAttrib();
		GlStateManager.popMatrix();
	}

	@Override
	public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType) {

		return PerspectiveMapWrapper.handlePerspective(this, TransformUtils.DEFAULT_BLOCK, cameraTransformType);
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
