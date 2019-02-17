package powercrystals.minefactoryreloaded.render.block;

import codechicken.lib.model.bakery.generation.ISimpleBlockBakery;
import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.OBJParser;
import codechicken.lib.render.buffer.BakingVertexBuffer;
import codechicken.lib.vec.Rotation;
import codechicken.lib.vec.Scale;
import codechicken.lib.vec.Translation;
import codechicken.lib.vec.Vector3;
import codechicken.lib.vec.uv.IconTransformation;
import cofh.core.util.helpers.RenderHelper;
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
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.block.transport.BlockPlasticPipe;
import powercrystals.minefactoryreloaded.tile.transport.TileEntityPlasticPipe;
import powercrystals.minefactoryreloaded.tile.transport.TileEntityPlasticPipe.ConnectionType;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static powercrystals.minefactoryreloaded.tile.transport.TileEntityPlasticPipe.ConnectionType.*;

public class PlasticPipeRenderer implements ISimpleBlockBakery {

	public static final ModelResourceLocation MODEL_LOCATION = new ModelResourceLocation(
			MFRProps.PREFIX + "plastic_pipe", "normal");
	public static final PlasticPipeRenderer INSTANCE = new PlasticPipeRenderer();

	protected static CCModel base;
	protected static CCModel[] cable = new CCModel[6];
	protected static CCModel[] iface = new CCModel[6];
	protected static CCModel[] gripO = new CCModel[6];
	protected static CCModel[] gripI = new CCModel[6];
	protected static CCModel[] gripP = new CCModel[6];

	public static TextureAtlasSprite sprite;
	private static IconTransformation iconTransform;

	static {
		try {
			Map<String, CCModel> cableModels = OBJParser.parseModels(MineFactoryReloadedCore.class.
							getResourceAsStream("/powercrystals/minefactoryreloaded/models/PlasticPipe.obj"),
					7, new Scale(1 / 16f));
			Vector3 p = new Vector3(0, 0, 0);
			base = cableModels.get("base").backfacedCopy();
			compute(base);

			iface[5] = cableModels.get("interface").backfacedCopy();
			calculateSidedModels(iface, p);

			cable[5] = cableModels.get("cable").backfacedCopy();
			calculateSidedModels(cable, p);

			gripO[5] = cableModels.get("gripO").backfacedCopy();
			calculateSidedModels(gripO, p);

			gripI[5] = cableModels.get("gripI").backfacedCopy();
			calculateSidedModels(gripI, p);

			gripP[5] = cableModels.get("gripP").backfacedCopy();
			calculateSidedModels(gripP, p);
		} catch (Throwable ex) { ex.printStackTrace(); }
	}

	private static void calculateSidedModels(CCModel[] m, Vector3 p) {

		compute(m[4] = m[5].copy().apply(new Rotation(Math.PI * 1.0, 0, 1, 0)));
		compute(m[3] = m[5].copy().apply(new Rotation(Math.PI * -.5, 0, 1, 0)));
		compute(m[2] = m[5].copy().apply(new Rotation(Math.PI * 0.5, 0, 1, 0)));
		compute(m[1] = m[5].copy().apply(new Rotation(Math.PI * 0.5, 0, 0, 1).with(new Rotation(Math.PI, 0, 1, 0))));
		compute(m[0] = m[5].copy().apply(new Rotation(Math.PI * -.5, 0, 0, 1)));
		compute(m[5]);
	}

	private static void compute(CCModel m) {

		m.computeNormals();
		m.apply(new Translation(0.5, 0.5, 0.5));
		m.shrinkUVs(RenderHelper.RENDER_OFFSET);
	}

	public static void setSprite(TextureAtlasSprite textureAtlasSprite) {

		sprite = textureAtlasSprite;
		iconTransform = new IconTransformation(textureAtlasSprite);
	}

	private PlasticPipeRenderer() {

	}

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

		renderCable(state, ccrs);

		buffer.finishDrawing();
		return buffer.bake();
	}

	private void renderCable(IExtendedBlockState state, CCRenderState ccrs) {

		base.render(ccrs, iconTransform);
		EnumFacing[] dirs = EnumFacing.VALUES;

		for (int i = dirs.length; i-- > 0; ) {
			switch (state.getValue(BlockPlasticPipe.CONNECTION[i])) {
			case CABLE:
				cable[i].render(ccrs, iconTransform);
				break;
			case EXTRACT_POWERED:
				iface[i].render(ccrs, iconTransform);
				gripI[i].render(ccrs, iconTransform);
				break;
			case EXTRACT:
				iface[i].render(ccrs, iconTransform);
				gripP[i].render(ccrs, iconTransform);
				break;
			case OUTPUT:
				iface[i].render(ccrs, iconTransform);
				gripO[i].render(ccrs, iconTransform);
				break;
			default:
			}
		}
	}

	@Override
	public IExtendedBlockState handleState(IExtendedBlockState blockState, IBlockAccess world, BlockPos pos) {

		TileEntityPlasticPipe pipe = (TileEntityPlasticPipe) world.getTileEntity(pos);

		if (pipe == null) {
			pipe = new TileEntityPlasticPipe();
		}

		for (EnumFacing side : EnumFacing.VALUES) {
			ConnectionType connType = pipe.getSideConnection(side.ordinal());
			if((connType == OUTPUT || connType == EXTRACT || connType == EXTRACT_POWERED) && pipe.isCableOnly())
				connType = HANDLER_DISCONNECTED;
			else if(connType == EXTRACT && pipe.isPowered())
				connType = EXTRACT_POWERED;

			blockState = blockState.withProperty(BlockPlasticPipe.CONNECTION[side.ordinal()], connType);
		}

		return blockState;
	}

	@Override
	public List<BakedQuad> bakeItemQuads(EnumFacing face, @Nonnull ItemStack stack) {

		BakingVertexBuffer buffer = BakingVertexBuffer.create();
		buffer.begin(7, DefaultVertexFormats.ITEM);
		CCRenderState ccrs = CCRenderState.instance();
		ccrs.reset();
		ccrs.bind(buffer);

		base.render(ccrs, iconTransform);
		cable[4].render(ccrs, iconTransform);
		cable[5].render(ccrs, iconTransform);

		buffer.finishDrawing();
		return buffer.bake();
	}
}
