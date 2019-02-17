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

public class PlasticCupModel extends BaseFluidItemModel {
	
	public static final ResourceLocation MODEL_LOCATION = new ModelResourceLocation("minefactoryreloaded:plastic_cup", "inventory");
	private static final ResourceLocation BASE = new ResourceLocation("minefactoryreloaded:items/item.mfr.plastic.cup");
	private static final ResourceLocation MASK = new ResourceLocation("minefactoryreloaded:items/item.mfr.plastic.cup.fill");

	public static final IModel MODEL = new PlasticCupModel();
	
	public PlasticCupModel() {

		super(BASE, MASK);
	}
	
	public PlasticCupModel(Fluid fluid) {
		
		super(fluid, BASE, MASK);
	}

	@Override
	protected IBakedModel createBakedModel(ImmutableList<BakedQuad> quads, TextureAtlasSprite particle, VertexFormat format,
			ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> transforms) {
		
		return new BaseFluidItemModel.BakedFluidItem(this, quads, particle, format, transforms, Maps.newHashMap(), BakedPlasticCupOverrideHandler.INSTANCE);
	}

	@Override
	protected BaseFluidItemModel createModel(Fluid fluid) {
		
		return new PlasticCupModel(fluid);
	}
	
	private static class BakedPlasticCupOverrideHandler extends BakedFluidItemOverrideHandler {

		public static final BakedFluidItemOverrideHandler INSTANCE = new BakedPlasticCupOverrideHandler();
		
		private BakedPlasticCupOverrideHandler() {}

		@Override
		protected String getFluidNameFromStack(@Nonnull ItemStack stack) {

			NBTTagCompound tag = stack.getTagCompound();
			return tag != null && tag.hasKey("fluid") ? stack.getTagCompound().getCompoundTag("fluid").getString("FluidName") : null;
		}
	} 
}
