package powercrystals.minefactoryreloaded.render.block;

import codechicken.lib.model.PlanarFaceBakery;
import codechicken.lib.model.bakery.generation.ISimpleBlockBakery;
import codechicken.lib.texture.SpriteSheetManager;
import codechicken.lib.texture.TextureUtils;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fluids.FluidStack;
import powercrystals.minefactoryreloaded.MFRProps;
import powercrystals.minefactoryreloaded.block.ItemBlockTank;
import powercrystals.minefactoryreloaded.block.fluid.BlockTank;
import powercrystals.minefactoryreloaded.core.MFRUtil;
import powercrystals.minefactoryreloaded.tile.tank.TileEntityTank;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BlockTankRenderer implements ISimpleBlockBakery {

	public static final ModelResourceLocation MODEL_LOCATION = new ModelResourceLocation(MFRProps.PREFIX + "plastic_tank", "normal");
	public static final ResourceLocation BOTTOM_TEXTURE_LOCATION = new ResourceLocation(MFRProps.PREFIX + "blocks/machines/tile.mfr.tank.bottom");
	public static final ResourceLocation TOP_TEXTURE_LOCATION = new ResourceLocation(MFRProps.TEXTURE_FOLDER + "blocks/machines/tile.mfr.tank.top.png");
	public static final ResourceLocation SIDE_TEXTURE_LOCATION = new ResourceLocation(MFRProps.TEXTURE_FOLDER + "blocks/machines/tile.mfr.tank.side.png");

	private static SpriteSheetManager.SpriteSheet spriteSheetTop = SpriteSheetManager.getSheet(2, 2, TOP_TEXTURE_LOCATION);
	private static SpriteSheetManager.SpriteSheet spriteSheetSide = SpriteSheetManager.getSheet(3, 3, SIDE_TEXTURE_LOCATION);

	static {
		spriteSheetTop.setupSprite(0);
		spriteSheetTop.setupSprite(1);

		spriteSheetSide.setupSprite(0);
		spriteSheetSide.setupSprite(1);
		spriteSheetSide.setupSprite(3);
		spriteSheetSide.setupSprite(4);
		spriteSheetSide.setupSprite(5);

		TextureUtils.addIconRegister(spriteSheetTop);
		TextureUtils.addIconRegister(spriteSheetSide);
	}
	private static TextureAtlasSprite bottom;

	public static final BlockTankRenderer INSTANCE = new BlockTankRenderer();

	@Override
	public IExtendedBlockState handleState(IExtendedBlockState state, IBlockAccess world, BlockPos pos) {

		String fluid = null;
		byte sides = 0;
		if (world.getTileEntity(pos) instanceof TileEntityTank) {
			TileEntityTank tank = (TileEntityTank) world.getTileEntity(pos);

			if (tank.getFluid() != null)
				fluid = tank.getFluid().getFluid().getStill().toString();

			for(EnumFacing side : EnumFacing.HORIZONTALS) {
				sides |= (tank.isInterfacing(side) ? 1 : 0) << side.getHorizontalIndex();
			}
		}
		state = state.withProperty(BlockTank.FLUID, fluid);
		state = state.withProperty(BlockTank.SIDES, sides);

		return state;
	}

	@Override
	public List<BakedQuad> bakeQuads(EnumFacing face, IExtendedBlockState state) {

		if (face == null)
			return Collections.emptyList();

		List<BakedQuad> quads = new ArrayList<>();

		String fluid = state.getValue(BlockTank.FLUID);
		addFluidFaceQuads(face, fluid, quads);
		byte sides = state.getValue(BlockTank.SIDES);
		addOverlayFaceQuads(face, sides, quads);

		return quads;
	}

	private void addFluidFaceQuads(EnumFacing face, String fluid, List<BakedQuad> quads) {

		TextureAtlasSprite fluidSprite;
		if (fluid == null) {
			fluidSprite = spriteSheetTop.getSprite(1);
		} else {
			fluidSprite = TextureUtils.getTexture(fluid);
		}

		quads.add(PlanarFaceBakery.bakeFace(face, fluidSprite));
	}

	private void addOverlayFaceQuads(EnumFacing face, byte sides, List<BakedQuad> quads) {

		TextureAtlasSprite overlaySprite;
		switch(face) {
			case UP:
				overlaySprite = spriteSheetTop.getSprite(0);
				break;
			case DOWN:
				overlaySprite = bottom;
				break;
			default:
				EnumFacing left = EnumFacing.HORIZONTALS[((face.getHorizontalIndex() + 1) % 4)];
				EnumFacing right = left.getOpposite();
				if (interfacesTo(left, sides)) {
					if (interfacesTo(right, sides)) {
						overlaySprite = spriteSheetSide.getSprite(4);
					} else {
						overlaySprite = spriteSheetSide.getSprite(5);
					}
				} else if (interfacesTo(right, sides)) {
					overlaySprite = spriteSheetSide.getSprite(3);
				} else {
					overlaySprite = spriteSheetSide.getSprite(0);
				}
		}

		quads.add(PlanarFaceBakery.bakeFace(face, overlaySprite));
	}

	private boolean interfacesTo(EnumFacing side, byte sides) {

		return ((sides >> side.getHorizontalIndex()) & 1) == 1;
	}

	@Override
	public List<BakedQuad> bakeItemQuads(EnumFacing face, @Nonnull ItemStack stack) {

		if (face == null) {
			List<BakedQuad> quads = new ArrayList<>();

			String fluid = null;
			if (stack.getItem() instanceof ItemBlockTank) {
				FluidStack fluidStack = MFRUtil.getFluidContents(stack);
				if (fluidStack != null) {
					fluid = fluidStack.getFluid().getStill().toString();
				}
			}
			
			for(EnumFacing facing : EnumFacing.VALUES) {
				addFluidFaceQuads(facing, fluid, quads);
				addOverlayFaceQuads(facing, (byte) 0, quads);
			}
			return quads;
		}
		return Collections.emptyList();
	}

	public static void updateSprites(TextureMap textureMap) {

		bottom = textureMap.getAtlasSprite(BOTTOM_TEXTURE_LOCATION.toString());
	}
}
