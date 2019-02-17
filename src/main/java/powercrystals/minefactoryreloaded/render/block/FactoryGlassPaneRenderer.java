package powercrystals.minefactoryreloaded.render.block;

import codechicken.lib.model.bakery.generation.ISimpleBlockBakery;
import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.buffer.BakingVertexBuffer;
import codechicken.lib.vec.uv.IconTransformation;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.BlockPane;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.property.IExtendedBlockState;
import powercrystals.minefactoryreloaded.MFRProps;
import powercrystals.minefactoryreloaded.block.decor.BlockFactoryGlassPane;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FactoryGlassPaneRenderer implements ISimpleBlockBakery {

	public static final ModelResourceLocation MODEL_LOCATION = new ModelResourceLocation(MFRProps.PREFIX + "stained_glass_pane", "normal");
	public static final FactoryGlassPaneRenderer INSTANCE = new FactoryGlassPaneRenderer();
	private CCModel post;
	private Map<EnumFacing, CCModel> sideModels = new HashMap<>();
	private TextureAtlasSprite glassTexture;
	private TextureAtlasSprite glassStreaksTexture;

	private FactoryGlassPaneRenderer() {

		post = CCModel.quadModel(24);
		post.generateBlock(0, 0.4375, 0, 0.4375, 0.5625, 1, 0.5625).computeNormals();

		CCModel north = CCModel.quadModel(24).generateBlock(0, 0.4375, 0, 0, 0.5625, 1, 0.4375).computeNormals();
		CCModel south = CCModel.quadModel(24).generateBlock(0, 0.4375, 0, 0.5625, 0.5625, 1, 1).computeNormals();
		CCModel west = CCModel.quadModel(24).generateBlock(0, 0, 0, 0.4375, 0.4375, 1, 0.5625).computeNormals();
		CCModel east = CCModel.quadModel(24).generateBlock(0, 0.5625, 0, 0.4375, 1, 1, 0.5625).computeNormals();

		sideModels.put(EnumFacing.NORTH, north);
		sideModels.put(EnumFacing.SOUTH, south);
		sideModels.put(EnumFacing.WEST, west);
		sideModels.put(EnumFacing.EAST, east);

		glassTexture = FactoryGlassRenderer.spriteSheet.getSprite(63);
		glassStreaksTexture = FactoryGlassRenderer.spriteSheet.getSprite(62);

	}

	@Nonnull
	@Override
	public List<BakedQuad> bakeQuads(EnumFacing face, IExtendedBlockState state) {

		if (face != null) {
			return Collections.emptyList();
		}

		BakingVertexBuffer buffer = BakingVertexBuffer.create();
		buffer.begin(7, DefaultVertexFormats.ITEM);
		CCRenderState ccrs = CCRenderState.instance();
		ccrs.reset();
		ccrs.bind(buffer);

		renderPane(state, ccrs);

		buffer.finishDrawing();
		return buffer.bake();
	}

	private void renderPane(IBlockState state, CCRenderState ccrs) {

		IExtendedBlockState exState = (IExtendedBlockState) state;

		int color = (state.getValue(BlockFactoryGlassPane.COLOR).getColor() << 8) + 0xFF;
		Map<EnumFacing, TextureAtlasSprite> overlayTextures = getOverlayTextures(exState);
		int ctmValueSouth = exState.getValue(BlockFactoryGlassPane.CTM_VALUE[0]);
		int ctmValueWest = exState.getValue(BlockFactoryGlassPane.CTM_VALUE[1]);
		Map<EnumFacing, Boolean> connections = getConnections(ctmValueSouth, ctmValueWest);

		renderPaneParts(exState, ccrs, color, overlayTextures, connections);
	}

	private void renderPaneParts(IExtendedBlockState state, CCRenderState ccrs, int color, Map<EnumFacing, TextureAtlasSprite> overlayTextures,
			Map<EnumFacing, Boolean> connections) {

		int facesToShow = state.getValue(BlockFactoryGlassPane.FACES);
		renderPaneSide(ccrs, EnumFacing.NORTH, color, overlayTextures, connections, state.getValue(BlockPane.NORTH), facesToShow);
		renderPaneSide(ccrs, EnumFacing.SOUTH, color, overlayTextures, connections, state.getValue(BlockPane.SOUTH), facesToShow);
		renderPaneSide(ccrs, EnumFacing.WEST, color, overlayTextures, connections, state.getValue(BlockPane.WEST), facesToShow);
		renderPaneSide(ccrs, EnumFacing.EAST, color, overlayTextures, connections, state.getValue(BlockPane.EAST), facesToShow);

		boolean renderUp = shouldRenderFace(facesToShow, EnumFacing.UP);
		if (!connections.get(EnumFacing.UP) || renderUp) {
			renderPostFace(ccrs, EnumFacing.UP, FactoryGlassRenderer.spriteSheet.getSprite(61), color, !renderUp);
		}

		boolean renderDown = shouldRenderFace(facesToShow, EnumFacing.DOWN);
		if (!connections.get(EnumFacing.DOWN) || renderDown) {
			renderPostFace(ccrs, EnumFacing.DOWN, FactoryGlassRenderer.spriteSheet.getSprite(61), color, !renderDown);
		}
	}

	private Map<EnumFacing, TextureAtlasSprite> getOverlayTextures(IExtendedBlockState exState) {

		ImmutableMap.Builder<EnumFacing, TextureAtlasSprite> builder = ImmutableMap.builder();

		for (EnumFacing facing : EnumFacing.HORIZONTALS) {
			builder.put(facing, FactoryGlassRenderer.getSpriteByCTMValue(exState.getValue(BlockFactoryGlassPane.CTM_VALUE[facing.getHorizontalIndex()])));
		}
		return builder.build();
	}

	private Map<EnumFacing, Boolean> getConnections(int ctmValueSouth, int ctmValueWest) {

		Map<EnumFacing, Boolean> connections = new HashMap<>();
		connections.put(EnumFacing.UP, ((ctmValueSouth >> 2) & 1) == 1);
		connections.put(EnumFacing.DOWN, ((ctmValueSouth >> 1) & 1) == 1);
		connections.put(EnumFacing.WEST, ((ctmValueSouth >> 3) & 1) == 1);
		connections.put(EnumFacing.EAST, (ctmValueSouth & 1) == 1);
		connections.put(EnumFacing.NORTH, ((ctmValueWest >> 3) & 1) == 1);
		connections.put(EnumFacing.SOUTH, (ctmValueWest & 1) == 1);
		return connections;
	}

	private boolean shouldRenderFace(int facesToShow, EnumFacing side) {

		return ((facesToShow >> side.ordinal()) & 1) == 1;
	}

	private boolean shouldRenderBorder(int facesToShow, EnumFacing upDownSide, EnumFacing horizontalSide) {

		int offset = upDownSide == EnumFacing.UP ? 6 : 10;
		return ((facesToShow >> (offset + horizontalSide.getHorizontalIndex())) & 1) == 1;
	}

	private void renderPaneSide(CCRenderState ccrs, EnumFacing facing, int color, Map<EnumFacing, TextureAtlasSprite> overlayTextures,
			Map<EnumFacing, Boolean> connections, boolean renderPart, int facesToShow) {

		if (renderPart) {
			renderPanePart(ccrs, facing, overlayTextures, color, connections, facesToShow);
		} else {
			renderPostFace(ccrs, facing, overlayTextures.get(facing), color, true);
		}
	}

	private void renderPostFace(CCRenderState ccrs, EnumFacing facing, TextureAtlasSprite texture, int color, boolean renderOverlay) {

		renderModelFace(ccrs, post, facing, glassTexture, color);
		renderModelFace(ccrs, post, facing, glassStreaksTexture, color);
		if (renderOverlay)
			renderModelFace(ccrs, post, facing, texture);
	}

	private void renderPanePart(CCRenderState ccrs, @Nullable EnumFacing side, Map<EnumFacing, TextureAtlasSprite> overlayTextures, int color,
			Map<EnumFacing, Boolean> connections, int facesToShow) {

		if (side == null) {
			return;
		}

		CCModel model = sideModels.get(side).copy();

		boolean renderFaceOnSide = shouldRenderFace(facesToShow, side);
		for (EnumFacing facing : EnumFacing.HORIZONTALS) {
			//skip the face that's next to post and the face that's connected to another pane and is not supposed to be rendered (same color pane)
			if (facing == side.getOpposite() || (facing == side && connections.get(side) && !renderFaceOnSide))
				continue;

			renderGlassFaces(ccrs, model, facing, overlayTextures.get(facing), color, side != facing || !renderFaceOnSide);
		}

		boolean renderUp = shouldRenderFace(facesToShow, EnumFacing.UP);
		boolean renderBorderUp = shouldRenderBorder(facesToShow, EnumFacing.UP, side);
		if (!connections.get(EnumFacing.UP) || renderUp || renderBorderUp) {
			renderGlassFaces(ccrs, model, EnumFacing.UP, FactoryGlassRenderer.spriteSheet.getSprite(61), color, !renderUp || renderBorderUp);
		}

		boolean renderDown = shouldRenderFace(facesToShow, EnumFacing.DOWN);
		boolean renderBorderDown = shouldRenderBorder(facesToShow, EnumFacing.DOWN, side);
		if (!connections.get(EnumFacing.DOWN) || renderDown) {
			renderGlassFaces(ccrs, model, EnumFacing.DOWN, FactoryGlassRenderer.spriteSheet.getSprite(61), color, !renderDown || renderBorderDown);
		}
	}

	private void renderGlassFaces(CCRenderState ccrs, CCModel model, EnumFacing facing, TextureAtlasSprite overlayTexture, int color, boolean renderOverlay) {

		renderModelFace(ccrs, model, facing, glassTexture, color);
		renderModelFace(ccrs, model, facing, glassStreaksTexture);
		if (renderOverlay)
			renderModelFace(ccrs, model, facing, overlayTexture);
	}

	private void renderModelFace(CCRenderState ccrs, CCModel model, EnumFacing facing, TextureAtlasSprite texture) {

		model.render(ccrs, facing.ordinal() * 4, (facing.ordinal() + 1) * 4, new IconTransformation(texture));
	}

	private void renderModelFace(CCRenderState ccrs, CCModel model, EnumFacing facing, TextureAtlasSprite texture, int color) {

		model.copy().setColour(color).render(ccrs, facing.ordinal() * 4, (facing.ordinal() + 1) * 4, new IconTransformation(texture));
	}

	@Override
	public IExtendedBlockState handleState(IExtendedBlockState state, IBlockAccess access, BlockPos pos) {

		return null;
	}

	@Nonnull
	@Override
	public List<BakedQuad> bakeItemQuads(EnumFacing face, @Nonnull ItemStack stack) {

		return ImmutableList.of();
	}

}
