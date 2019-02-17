package powercrystals.minefactoryreloaded.render.item;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.item.IItemRenderer;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.PerspectiveMapWrapper;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseItemRenderer implements IItemRenderer, IBakedModel {

	ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> transformations;

	@Override
	public void renderItem(@Nonnull ItemStack stack, ItemCameraTransforms.TransformType transformType) {

		GlStateManager.pushMatrix();
		CCRenderState ccrs = CCRenderState.instance();
		ccrs.reset();

		GlStateManager.pushMatrix();
		GlStateManager.translate(0.5, 0, 0.5);
		GlStateManager.rotate(-90 * (2 + 2), 0, 1, 0);

		drawModel(ccrs, stack);

		GlStateManager.popMatrix();

		GlStateManager.popMatrix();
	}

	protected void drawModel(CCRenderState ccrs, @Nonnull ItemStack stack) {}

	@Override
	public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType) {

		return PerspectiveMapWrapper.handlePerspective(this, transformations, cameraTransformType);
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

	@Override
	public IModelState getTransforms() {

		return null;
	}
}
