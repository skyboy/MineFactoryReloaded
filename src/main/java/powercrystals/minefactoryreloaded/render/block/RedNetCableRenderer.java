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
import powercrystals.minefactoryreloaded.api.rednet.connectivity.RedNetConnectionType;
import powercrystals.minefactoryreloaded.block.transport.BlockRedNetCable;
import powercrystals.minefactoryreloaded.core.MFRDyeColor;
import powercrystals.minefactoryreloaded.setup.MFRConfig;
import powercrystals.minefactoryreloaded.tile.rednet.TileEntityRedNetCable;
import powercrystals.minefactoryreloaded.tile.rednet.TileEntityRedNetEnergy;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class RedNetCableRenderer implements ISimpleBlockBakery {

	public static final ModelResourceLocation MODEL_LOCATION = new ModelResourceLocation(MFRProps.PREFIX + "rednet_cable", "normal");

	public static final RedNetCableRenderer INSTANCE = new RedNetCableRenderer();
	protected static CCModel base;
	protected static CCModel cage;
	protected static CCModel[] cable = new CCModel[6];
	protected static CCModel[] iface = new CCModel[6];
	protected static CCModel[] band  = new CCModel[6];
	protected static CCModel[] plate = new CCModel[6];
	protected static CCModel[] platef = new CCModel[6];
	protected static CCModel[] grip  = new CCModel[6];
	protected static CCModel[] wire  = new CCModel[6];
	protected static CCModel[] caps  = new CCModel[6];

	public static TextureAtlasSprite sprite;
	private static IconTransformation iconTransform;

	private static boolean brightBand;

	static {
		try {
			Map<String, CCModel> cableModels = OBJParser.parseModels(MineFactoryReloadedCore.class.
							getResourceAsStream("/powercrystals/minefactoryreloaded/models/RedNetCable.obj"),
					7, new Scale(1/16f));
			Vector3 p = new Vector3(0, 0, 0);
			base = cableModels.get("base").backfacedCopy();
			compute(base);

			cage = cableModels.get("cage").backfacedCopy();
			compute(cage);

			cable[5] = cableModels.get("cable").backfacedCopy();
			calculateSidedModels(cable, p);

			iface[5] = cableModels.get("interface").backfacedCopy();
			calculateSidedModels(iface, p);

			band[5] = cableModels.get("band").backfacedCopy();
			calculateSidedModels(band, p);

			plate[5] = cableModels.get("plate").backfacedCopy();
			calculateSidedModels(plate, p);

			platef[5] = cableModels.get("plateface").backfacedCopy();
			calculateSidedModels(platef, p);

			grip[5] = cableModels.get("grip").backfacedCopy();
			calculateSidedModels(grip, p);

			wire[5] = cableModels.get("wire").backfacedCopy();
			calculateSidedModels(wire, p);

			caps[5] = cableModels.get("cap").backfacedCopy();
			calculateSidedModels(caps, p);
		} catch (Throwable ex) { ex.printStackTrace(); }

		brightBand = MFRConfig.brightRednetBand.getBoolean(true);
	}

	public static void setSprite(TextureAtlasSprite textureAtlasSprite) {

		sprite = textureAtlasSprite;
		iconTransform = new IconTransformation(textureAtlasSprite);
	}


	private RedNetCableRenderer() {
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

		m.apply(new Translation(0.5, 0.5, 0.5));
		m.computeNormals();
		//m.computeLighting(LightModel.standardLightModel);
		m.shrinkUVs(RenderHelper.RENDER_OFFSET);
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

		int brightness = ccrs.brightness;
		int bandBrightness = brightBand ? 0xd00070 : brightness;

		base.render(ccrs, iconTransform);

		BlockRedNetCable.Variant variant = state.getValue(BlockRedNetCable.VARIANT);
		if (variant == BlockRedNetCable.Variant.ENERGY || variant == BlockRedNetCable.Variant.ENERGY_GLASS) {
			cage.render(ccrs, iconTransform);
		}

		EnumFacing[] dirs = EnumFacing.VALUES;

		for (int i = dirs.length; i --> 0; ) {
			boolean showBand = state.getValue(BlockRedNetCable.BAND[i]);
			boolean showPlateFace = state.getValue(BlockRedNetCable.PLATE_FACE[i]);

			if (showBand || showPlateFace) {
				int color = state.getValue(BlockRedNetCable.COLOR[i]) == 16 ? -1 :
						(MFRDyeColor.byMetadata(state.getValue(BlockRedNetCable.COLOR[i]) & 15).getColor() << 8) | 0xFF;

				ccrs.brightness = bandBrightness;
				if (showBand) {
					band[i].setColour(color);
					band[i].render(ccrs, iconTransform);
				}
				if (showPlateFace) {
					platef[i].setColour(color);
					platef[i].render(ccrs, iconTransform);
				}
				ccrs.brightness = brightness;
			}

			if (state.getValue(BlockRedNetCable.IFACE[i]))
				iface[i].render(ccrs, iconTransform);

			if (state.getValue(BlockRedNetCable.GRIP[i]))
				grip[i].render(ccrs, iconTransform);

			if (state.getValue(BlockRedNetCable.CABLE_CONNECTION[i]))
				cable[i].render(ccrs, iconTransform);

			if (state.getValue(BlockRedNetCable.PLATE[i]))
				plate[i].render(ccrs, iconTransform);

			if (state.getValue(BlockRedNetCable.WIRE[i]))
				wire[i].render(ccrs, iconTransform);

			if (state.getValue(BlockRedNetCable.CAPS[i]))
				caps[i].render(ccrs, iconTransform);
		}
	}

	@Override
	public IExtendedBlockState handleState(IExtendedBlockState blockState, IBlockAccess world, BlockPos pos) {

		TileEntityRedNetCable cable = (TileEntityRedNetCable) world.getTileEntity(pos);
		TileEntityRedNetEnergy energyCable = null;

		if (cable instanceof TileEntityRedNetEnergy) {
			energyCable = (TileEntityRedNetEnergy)cable;
		} else if (cable == null) {
			cable = new TileEntityRedNetCable();
		}

		EnumFacing[] dirs = EnumFacing.VALUES;

		for (int i = dirs.length; i --> 0; ) {
			EnumFacing f = dirs[i];
			blockState = blockState.withProperty(BlockRedNetCable.COLOR[i], cable.getSideColor(f));
			blockState = blockState.withProperty(BlockRedNetCable.BAND[i], false);
			blockState = blockState.withProperty(BlockRedNetCable.IFACE[i], false);
			blockState = blockState.withProperty(BlockRedNetCable.GRIP[i], false);
			blockState = blockState.withProperty(BlockRedNetCable.CABLE_CONNECTION[i], false);
			blockState = blockState.withProperty(BlockRedNetCable.PLATE_FACE[i], false);
			blockState = blockState.withProperty(BlockRedNetCable.PLATE[i], false);
			blockState = blockState.withProperty(BlockRedNetCable.WIRE[i], false);
			blockState = blockState.withProperty(BlockRedNetCable.CAPS[i], false);

			RedNetConnectionType state = cable.getCachedConnectionState(f);
			switch (state.flags & 31) {
			case 11: // isCable, isSingleSubnet
				blockState = blockState.withProperty(BlockRedNetCable.BAND[i], true);
			case 19: // isCable, isAllSubnets
				if (state.isSingleSubnet) {
					blockState = blockState.withProperty(BlockRedNetCable.IFACE[i], true);
					blockState = blockState.withProperty(BlockRedNetCable.GRIP[i], true);
				} else
					blockState = blockState.withProperty(BlockRedNetCable.CABLE_CONNECTION[i], true);
				break;
			case 13: // isPlate, isSingleSubnet
				blockState = blockState.withProperty(BlockRedNetCable.BAND[i], true);
				blockState = blockState.withProperty(BlockRedNetCable.PLATE_FACE[i], true);
			case 21: // isPlate, isAllSubnets
				blockState = blockState.withProperty(BlockRedNetCable.IFACE[i], true);
				blockState = blockState.withProperty(BlockRedNetCable.PLATE[i], true);
				if (state.isAllSubnets) {
					blockState = blockState.withProperty(BlockRedNetCable.PLATE_FACE[i], true);
					blockState = blockState.withProperty(BlockRedNetCable.COLOR[i], -1);
				}
			default:
				break;
			}
			if (energyCable != null && energyCable.isInterfacing(f.getOpposite())) {
				blockState = blockState.withProperty(BlockRedNetCable.WIRE[i], true);
				if (energyCable.interfaceMode(f.getOpposite()) != 4)
					blockState = blockState.withProperty(BlockRedNetCable.CAPS[i], true);
			}
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
		if (stack.getMetadata() == 3 | stack.getMetadata() == 2)
		{
			cage.render(ccrs, iconTransform);
			wire[4].render(ccrs, iconTransform);
			wire[5].render(ccrs, iconTransform);
			caps[4].render(ccrs, iconTransform);
			caps[5].render(ccrs, iconTransform);
		}

		buffer.finishDrawing();
		return buffer.bake();
	}
}
