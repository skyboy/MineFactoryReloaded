package powercrystals.minefactoryreloaded.render.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fluids.Fluid;

import javax.annotation.Nonnull;

public class SyringeModel extends BaseFluidItemModel {

	public static final ResourceLocation MODEL_LOCATION = new ModelResourceLocation("minefactoryreloaded:syringe", "variant=empty");
	private static final ResourceLocation BASE = new ResourceLocation("minefactoryreloaded:items/item.mfr.syringe.empty");
	private static final ResourceLocation MASK = new ResourceLocation("minefactoryreloaded:items/item.mfr.syringe.empty.fill");

	public static final IModel MODEL = new SyringeModel();

	public SyringeModel() {

		super(BASE, MASK);
	}

	public SyringeModel(Fluid fluid) {

		super(fluid, BASE, MASK);
	}

	@Override
	protected IBakedModel createBakedModel(ImmutableList<BakedQuad> quads, TextureAtlasSprite particle, VertexFormat format,
			ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> transforms) {

		return new BaseFluidItemModel.BakedFluidItem(this, quads, particle, format, transforms, Maps.newHashMap(), BakedSyringeOverrideHandler.INSTANCE);
	}

	@Override
	protected BaseFluidItemModel createModel(Fluid fluid) {

		return new SyringeModel(fluid);
	}

	private static class BakedSyringeOverrideHandler extends BakedFluidItemOverrideHandler {

		public static final BakedFluidItemOverrideHandler INSTANCE = new BakedSyringeOverrideHandler();

		private BakedSyringeOverrideHandler() {}

		@Override
		protected String getFluidNameFromStack(@Nonnull ItemStack stack) {

			NBTTagCompound tag = stack.getTagCompound();
			return tag == null || !tag.hasKey("fluidName") ? null :	tag.getString("fluidName");
		}
	}
}
