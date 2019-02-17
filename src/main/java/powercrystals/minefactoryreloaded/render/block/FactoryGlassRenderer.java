package powercrystals.minefactoryreloaded.render.block;

import codechicken.lib.model.PlanarFaceBakery;
import codechicken.lib.model.bakery.generation.ISimpleBlockBakery;
import codechicken.lib.texture.SpriteSheetManager;
import codechicken.lib.texture.TextureUtils;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.property.IExtendedBlockState;
import powercrystals.minefactoryreloaded.MFRProps;
import powercrystals.minefactoryreloaded.block.decor.BlockFactoryGlass;
import powercrystals.minefactoryreloaded.core.MFRDyeColor;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FactoryGlassRenderer implements ISimpleBlockBakery {

	public static final ModelResourceLocation MODEL_LOCATION = new ModelResourceLocation(MFRProps.PREFIX + "stained_glass", "normal");
	private static final ResourceLocation SPRITE_LOCATION = new ResourceLocation(MFRProps.TEXTURE_FOLDER + "blocks/tile.mfr.stainedglass.png");
	public static final int FULL_FRAME = 0;
	public static SpriteSheetManager.SpriteSheet spriteSheet = SpriteSheetManager.getSheet(8, 8, SPRITE_LOCATION);

	static {

		for (int i = 0; i < 64; i++)
			spriteSheet.setupSprite(i); //TODO do not setup for blank textures

		TextureUtils.addIconRegister(FactoryGlassRenderer.spriteSheet);
	}

	public static final FactoryGlassRenderer INSTANCE = new FactoryGlassRenderer();

	public static TextureAtlasSprite getSpriteByCTMValue(int ctmValue) {

		int index = (ctmValue & 15);
		ctmValue = ctmValue >> 4;
		int w;
		switch (index) {
			case 3: // bottom right connection
				index ^= ((ctmValue & 1) << 4); // bithack: add 16 if connection
				break;
			case 5: // top right connection
				index ^= ((ctmValue & 8) << 1); // bithack: add 16 if connection
				break;
			case 7: // left empty
				w = ctmValue & 9;
				index ^= ((w & (w << 3)) << 1); // bithack: add 16 if both connections
				if ((w == 1) | w == 8) // bottom right, top right
					index = 32 | (w >> 3);
				break;
			case 10: // bottom left connection
				index ^= ((ctmValue & 2) << 3); // bithack: add 16 if connection
				break;
			case 11: // top empty
				w = ctmValue & 3;
				index ^= ((w & (w << 1)) << 3); // bithack: add 16 if both connections
				if ((w == 1) | w == 2) // bottom right, bottom left
					index = 34 | (w >> 1);
				break;
			case 12: // top left connection
				index ^= ((ctmValue & 4) << 2); // bithack: add 16 if connection
				break;
			case 13: // bottom empty
				w = ctmValue & 12;
				index ^= ((w & (w << 1)) << 1); // bithack: add 16 if both connections
				if ((w == 4) | w == 8) // top left, top right
					index = 36 | (w >> 3);
				break;
			case 14: // right empty
				w = ctmValue & 6;
				index ^= ((w & (w << 1)) << 2); // bithack: add 16 if both connections
				if ((w == 2) | w == 4) // bottom left, top left
					index = 38 | (w >> 2);
				break;
			case 15: // all sides
				index = 40 + ctmValue;
			default:
		}
		return spriteSheet.getSprite(index);
	}

	@Nonnull
	@Override
	public List<BakedQuad> bakeQuads(EnumFacing face, IExtendedBlockState state) {

		if (face == null) {
			return Collections.emptyList();
		}

		List<BakedQuad> quads = new ArrayList<>(3);
		quads.addAll(getCoreQuadsForSide(state.getValue(BlockFactoryGlass.COLOR), face));
		quads.add(getFrameQuadForSide(face, state.getValue(BlockFactoryGlass.CTM_VALUE[face.ordinal()])));
		return quads;
	}

	private BakedQuad getFrameQuadForSide(EnumFacing side, int ctmValue) {

		return PlanarFaceBakery.bakeFace(side, getSpriteByCTMValue(ctmValue));
	}

	private List<BakedQuad> getCoreQuadsForSide(MFRDyeColor color, EnumFacing side) {

		int colorValue = (color.getColor() << 8) + 0xFF;

		List<BakedQuad> faceQuads = new ArrayList<>();
		faceQuads.add(PlanarFaceBakery.bakeFace(side, spriteSheet.getSprite(63), DefaultVertexFormats.ITEM, colorValue));
		faceQuads.add(PlanarFaceBakery.bakeFace(side, spriteSheet.getSprite(62)));

		return faceQuads;
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
