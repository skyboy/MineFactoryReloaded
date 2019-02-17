package powercrystals.minefactoryreloaded.render.model;

import codechicken.lib.util.TransformUtils;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.*;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public abstract class BaseFluidItemModel implements IModel {

	// minimal Z offset to prevent depth-fighting
	private static final float NORTH_Z_FLUID = 7.498f / 16f;
	private static final float SOUTH_Z_FLUID = 8.502f / 16f;

	private final Fluid fluid;

	private ResourceLocation base;
	private ResourceLocation mask;

	public BaseFluidItemModel(ResourceLocation base, ResourceLocation mask) {

		this(null, base, mask);
	}

	public BaseFluidItemModel(Fluid fluid, ResourceLocation base, ResourceLocation mask) {

		this.fluid = fluid;
		this.base = base;
		this.mask = mask;
	}

	@Override
	public Collection<ResourceLocation> getDependencies() {

		return ImmutableList.of();
	}

	@Override
	public Collection<ResourceLocation> getTextures() {

		return ImmutableList.of(base, mask);
	}

	@Override
	public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {

		ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> transformMap = PerspectiveMapWrapper.getTransforms(state);

		TRSRTransformation transform = state.apply(Optional.empty()).orElse(TRSRTransformation.identity());
		TextureAtlasSprite fluidSprite = null;
		ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();

		if (fluid != null) {
			fluidSprite = bakedTextureGetter.apply(fluid.getStill());
		}

		IBakedModel model = (new ItemLayerModel(ImmutableList.of(base))).bake(state, format, bakedTextureGetter);
		builder.addAll(model.getQuads(null, null, 0));

		if (fluidSprite != null) {
			TextureAtlasSprite liquid = bakedTextureGetter.apply(mask);
			builder.addAll(ItemTextureQuadConverter.convertTexture(format, transform, liquid, fluidSprite, NORTH_Z_FLUID, EnumFacing.NORTH, fluid.getColor()));
			builder.addAll(ItemTextureQuadConverter.convertTexture(format, transform, liquid, fluidSprite, SOUTH_Z_FLUID, EnumFacing.SOUTH, fluid.getColor()));
		}

		return createBakedModel(builder.build(), fluidSprite, format, Maps.immutableEnumMap(transformMap));
	}

	protected abstract IBakedModel createBakedModel(ImmutableList<BakedQuad> quads, TextureAtlasSprite particle, VertexFormat format, 
			ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> transforms);

	@Override
	public IModelState getDefaultState() {

		return TransformUtils.DEFAULT_ITEM;
	}

	@Override
	public BaseFluidItemModel process(ImmutableMap<String, String> customData) {

		String fluidName = customData.get("fluid");
		Fluid fluid = FluidRegistry.getFluid(fluidName);

		if (fluid == null) {
			fluid = this.fluid;
		}

		// create new model with correct liquid
		return createModel(fluid);
	}

	protected abstract BaseFluidItemModel createModel(Fluid fluid);

	protected abstract static class BakedFluidItemOverrideHandler extends ItemOverrideList {

		protected BakedFluidItemOverrideHandler() {

			super(ImmutableList.of());
		}

		@Override
		public IBakedModel handleItemState(IBakedModel originalModel, @Nonnull ItemStack stack, World world, EntityLivingBase entity) {

			BakedFluidItem model = (BakedFluidItem) originalModel;

			String name = getFluidNameFromStack(stack);

			if (name == null || name.isEmpty()) {
				return originalModel;
			}

			if (!model.cache.containsKey(name)) {
				IModel parent = model.parent.process(ImmutableMap.of("fluid", name));
				Function<ResourceLocation, TextureAtlasSprite> textureGetter
						= location -> Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString());

				IBakedModel bakedModel = parent.bake(new SimpleModelState(model.transformMap), model.format, textureGetter);
				model.cache.put(name, bakedModel);
				return bakedModel;
			}

			return model.cache.get(name);
		}

		protected abstract String getFluidNameFromStack(@Nonnull ItemStack stack);
	}

	protected static final class BakedFluidItem extends BakedItemModel {

		private final BaseFluidItemModel parent;
		// FIXME: guava cache?
		private final Map<String, IBakedModel> cache; // contains all the baked models since they'll never change
		private final VertexFormat format;
		private final ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> transformMap;

		public BakedFluidItem(BaseFluidItemModel parent,
				ImmutableList<BakedQuad> quads,
				TextureAtlasSprite particle,
				VertexFormat format,
				ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> transforms,
				Map<String, IBakedModel> cache,
				BakedFluidItemOverrideHandler overrideHandler) {
			super(quads, particle, transforms, overrideHandler);
			this.format = format;
			this.parent = parent;
			this.cache = cache;
			this.transformMap = transforms;
		}
	}
}
