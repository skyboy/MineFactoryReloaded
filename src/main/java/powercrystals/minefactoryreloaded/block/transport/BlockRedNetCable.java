package powercrystals.minefactoryreloaded.block.transport;

import codechicken.lib.model.ModelRegistryHelper;
import codechicken.lib.model.bakery.CCBakeryModel;
import codechicken.lib.model.bakery.IBakeryProvider;
import codechicken.lib.model.bakery.ModelBakery;
import codechicken.lib.model.bakery.generation.IBakery;
import codechicken.lib.raytracer.RayTracer;
import codechicken.lib.vec.Cuboid6;
import cofh.api.block.IBlockInfo;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.common.property.Properties;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import powercrystals.minefactoryreloaded.MFRProps;
import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.api.rednet.IRedNetInfo;
import powercrystals.minefactoryreloaded.api.rednet.IRedNetNetworkContainer;
import powercrystals.minefactoryreloaded.api.rednet.connectivity.RedNetConnectionType;
import powercrystals.minefactoryreloaded.block.BlockFactory;
import powercrystals.minefactoryreloaded.block.ItemBlockFactory;
import powercrystals.minefactoryreloaded.core.MFRUtil;
import powercrystals.minefactoryreloaded.item.tool.ItemRedNetMeter;
import powercrystals.minefactoryreloaded.render.block.RedNetCableRenderer;
import powercrystals.minefactoryreloaded.setup.MFRConfig;
import powercrystals.minefactoryreloaded.tile.rednet.RedstoneNetwork;
import powercrystals.minefactoryreloaded.tile.rednet.TileEntityRedNetCable;
import powercrystals.minefactoryreloaded.tile.rednet.TileEntityRedNetEnergy;

import javax.annotation.Nonnull;
import java.util.List;

public class BlockRedNetCable extends BlockFactory implements IRedNetNetworkContainer, IBlockInfo, IRedNetInfo, IBakeryProvider {

	public static final PropertyEnum<Variant> VARIANT = PropertyEnum.create("variant", Variant.class);
	public static final IUnlistedProperty<Boolean>[] CABLE_CONNECTION = new IUnlistedProperty[6];
	public static final IUnlistedProperty<Boolean>[] IFACE = new IUnlistedProperty[6];
	public static final IUnlistedProperty<Boolean>[] BAND = new IUnlistedProperty[6];
	public static final IUnlistedProperty<Boolean>[] PLATE = new IUnlistedProperty[6];
	public static final IUnlistedProperty<Boolean>[] PLATE_FACE = new IUnlistedProperty[6];
	public static final IUnlistedProperty<Boolean>[] GRIP = new IUnlistedProperty[6];
	public static final IUnlistedProperty<Boolean>[] WIRE = new IUnlistedProperty[6];
	public static final IUnlistedProperty<Boolean>[] CAPS = new IUnlistedProperty[6];
	public static final IUnlistedProperty<Integer>[] COLOR = new IUnlistedProperty[6];

	static {
		for (int i=0; i<6; i++) {
			CABLE_CONNECTION[i] = Properties.toUnlisted(PropertyBool.create("cable_" + i));
			IFACE[i] = Properties.toUnlisted(PropertyBool.create("iface_" + i));
			BAND[i] = Properties.toUnlisted(PropertyBool.create("band_" + i));
			PLATE[i] = Properties.toUnlisted(PropertyBool.create("plate_" + i));
			PLATE_FACE[i] = Properties.toUnlisted(PropertyBool.create("platef_" + i));
			GRIP[i] = Properties.toUnlisted(PropertyBool.create("grip_" + i));
			WIRE[i] = Properties.toUnlisted(PropertyBool.create("wire_" + i));
			CAPS[i] = Properties.toUnlisted(PropertyBool.create("caps_" + i));
			COLOR[i] = Properties.toUnlisted(PropertyInteger.create("color_" + i, 0, 16));
		}
	}

	public static final String[] NAMES = { null, "glass", "energy", "energyglass" };

	//@formatter:off
	private static float _wireSize   =  4.0F / 16.0F;
	private static float _cageSize   =  6.0F / 16.0F;
	private static float _gripWidth  =  8.0F / 16.0F;
	private static float _plateWidth = 14.0F / 16.0F;
	private static float _plateDepth =  2.0F / 16.0F;
	private static float _bandWidth  =  5.0F / 16.0F;
	private static float _bandOffset =  2.0F / 16.0F;
	private static float _bandDepth  =  1.0F / 16.0F;

	private static float _wireStart      = 0.5F - _wireSize   / 2.0F;
	private static float _wireEnd        = 0.5F + _wireSize   / 2.0F;
	private static float _cageStart      = 0.5F - _cageSize   / 2.0F;
	private static float _cageEnd        = 0.5F + _cageSize   / 2.0F;
	private static float _gripStart      = 0.5F - _gripWidth  / 2.0F;
	private static float _gripEnd        = 0.5F + _gripWidth  / 2.0F;
	private static float _plateStart     = 0.5F - _plateWidth / 2.0F;
	private static float _plateEnd       = 0.5F + _plateWidth / 2.0F;
	private static float _bandWidthStart = 0.5F - _bandWidth  / 2.0F;
	private static float _bandWidthEnd   = 0.5F + _bandWidth  / 2.0F;

	private static float _bandDepthStart = _bandOffset;
	private static float _bandDepthEnd   = _bandOffset + _bandDepth;
	//@formatter:on

	public static int[] _subSideMappings = new int[] { 6, 6,
			0, 1, 2, 3, 4, 5, // 6[0]
			0, 1, 2, 3, 4, 5, // 6[1]
			0, 1, 2, 3, 4, 5, // 6[2]
			0, 1, 2, 3, 4, 5, // 6[3]
			0, 1, 2, 3, 4, 5, // 6[4]
			0, 1, 2, 3, 4, 5, // 6[5]
			7, 8, 9, 10, 11, 12 };

	public static Cuboid6[] subSelection = new Cuboid6[2 + 6 * 6];

	static {
		int i = 0;
		// ** 0 **  cable hitbox
		subSelection[i++] = new Cuboid6(_wireStart, _wireStart, _wireStart, _wireEnd, _wireEnd, _wireEnd);
		// ** 1 **  wire cage hitbox
		subSelection[i++] = new Cuboid6(_cageStart, _cageStart, _cageStart, _cageEnd, _cageEnd, _cageEnd);

		// ** 6[0] ** grip hitbox
		subSelection[i++] = new Cuboid6(_gripStart, 0, _gripStart, _gripEnd, _plateDepth, _gripEnd);
		subSelection[i++] = new Cuboid6(_gripStart, 1 - _plateDepth, _gripStart, _gripEnd, 1, _gripEnd);
		subSelection[i++] = new Cuboid6(_gripStart, _gripStart, 0, _gripEnd, _gripEnd, _plateDepth);
		subSelection[i++] = new Cuboid6(_gripStart, _gripStart, 1 - _plateDepth, _gripEnd, _gripEnd, 1);
		subSelection[i++] = new Cuboid6(0, _gripStart, _gripStart, _plateDepth, _gripEnd, _gripEnd);
		subSelection[i++] = new Cuboid6(1 - _plateDepth, _gripStart, _gripStart, 1, _gripEnd, _gripEnd);

		// ** 6[1] ** plate hitbox
		subSelection[i++] = new Cuboid6(_plateStart, 0, _plateStart, _plateEnd, _plateDepth, _plateEnd);
		subSelection[i++] = new Cuboid6(_plateStart, 1 - _plateDepth, _plateStart, _plateEnd, 1, _plateEnd);
		subSelection[i++] = new Cuboid6(_plateStart, _plateStart, 0, _plateEnd, _plateEnd, _plateDepth);
		subSelection[i++] = new Cuboid6(_plateStart, _plateStart, 1 - _plateDepth, _plateEnd, _plateEnd, 1);
		subSelection[i++] = new Cuboid6(0, _plateStart, _plateStart, _plateDepth, _plateEnd, _plateEnd);
		subSelection[i++] = new Cuboid6(1 - _plateDepth, _plateStart, _plateStart, 1, _plateEnd, _plateEnd);

		// ** 6[2] ** color band hitbox
		subSelection[i++] = new Cuboid6(_bandWidthStart, _bandDepthStart, _bandWidthStart, _bandWidthEnd, _bandDepthEnd, _bandWidthEnd);
		subSelection[i++] = new Cuboid6(_bandWidthStart, 1 - _bandDepthEnd, _bandWidthStart, _bandWidthEnd, 1 - _bandDepthStart, _bandWidthEnd);
		subSelection[i++] = new Cuboid6(_bandWidthStart, _bandWidthStart, _bandDepthStart, _bandWidthEnd, _bandWidthEnd, _bandDepthEnd);
		subSelection[i++] = new Cuboid6(_bandWidthStart, _bandWidthStart, 1 - _bandDepthEnd, _bandWidthEnd, _bandWidthEnd, 1 - _bandDepthStart);
		subSelection[i++] = new Cuboid6(_bandDepthStart, _bandWidthStart, _bandWidthStart, _bandDepthEnd, _bandWidthEnd, _bandWidthEnd);
		subSelection[i++] = new Cuboid6(1 - _bandDepthEnd, _bandWidthStart, _bandWidthStart, 1 - _bandDepthStart, _bandWidthEnd, _bandWidthEnd);

		// ** 6[3] ** cable connection hitbox
		subSelection[i++] = new Cuboid6(_wireStart, 0, _wireStart, _wireEnd, _wireStart, _wireEnd);
		subSelection[i++] = new Cuboid6(_wireStart, _wireEnd, _wireStart, _wireEnd, 1 - 0, _wireEnd);
		subSelection[i++] = new Cuboid6(_wireStart, _wireStart, 0, _wireEnd, _wireEnd, _wireStart);
		subSelection[i++] = new Cuboid6(_wireStart, _wireStart, _wireEnd, _wireEnd, _wireEnd, 1 - 0);
		subSelection[i++] = new Cuboid6(0, _wireStart, _wireStart, _wireStart, _wireEnd, _wireEnd);
		subSelection[i++] = new Cuboid6(_wireEnd, _wireStart, _wireStart, 1 - 0, _wireEnd, _wireEnd);

		// ** 6[4] ** wire cage minus band connection hitbox
		subSelection[i++] = new Cuboid6(_cageStart, _bandDepthEnd, _cageStart, _cageEnd, _cageStart, _cageEnd);
		subSelection[i++] = new Cuboid6(_cageStart, _cageEnd, _cageStart, _cageEnd, 1 - _bandDepthEnd, _cageEnd);
		subSelection[i++] = new Cuboid6(_cageStart, _cageStart, _bandDepthEnd, _cageEnd, _cageEnd, _cageStart);
		subSelection[i++] = new Cuboid6(_cageStart, _cageStart, _cageEnd, _cageEnd, _cageEnd, 1 - _bandDepthEnd);
		subSelection[i++] = new Cuboid6(_bandDepthEnd, _cageStart, _cageStart, _cageStart, _cageEnd, _cageEnd);
		subSelection[i++] = new Cuboid6(_cageEnd, _cageStart, _cageStart, 1 - _bandDepthEnd, _cageEnd, _cageEnd);

		// ** 6[5] ** wire cage connection hitbox
		subSelection[i++] = new Cuboid6(_cageStart, 0, _cageStart, _cageEnd, _cageStart, _cageEnd);
		subSelection[i++] = new Cuboid6(_cageStart, _cageEnd, _cageStart, _cageEnd, 1 - 0, _cageEnd);
		subSelection[i++] = new Cuboid6(_cageStart, _cageStart, 0, _cageEnd, _cageEnd, _cageStart);
		subSelection[i++] = new Cuboid6(_cageStart, _cageStart, _cageEnd, _cageEnd, _cageEnd, 1 - 0);
		subSelection[i++] = new Cuboid6(0, _cageStart, _cageStart, _cageStart, _cageEnd, _cageEnd);
		subSelection[i++] = new Cuboid6(_cageEnd, _cageStart, _cageStart, 1 - 0, _cageEnd, _cageEnd);
	}

	public BlockRedNetCable() {

		super(0.8F);
		setUnlocalizedName("mfr.cable.redstone"); // FIXME: relocalize to rednet_cable
		providesPower = true;
	}

	@Override
	protected BlockStateContainer createBlockState() {

		BlockStateContainer.Builder builder = new BlockStateContainer.Builder(this);
		builder.add(VARIANT);

		for(int i=0; i < 6; i++) {
			builder.add(CABLE_CONNECTION[i]);
			builder.add(IFACE[i]);
			builder.add(BAND[i]);
			builder.add(PLATE[i]);
			builder.add(PLATE_FACE[i]);
			builder.add(GRIP[i]);
			builder.add(WIRE[i]);
			builder.add(CAPS[i]);
			builder.add(COLOR[i]);
		}
		return builder.build();
	}

	@Override
	public BlockRenderLayer getBlockLayer() {

		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {

		return getDefaultState().withProperty(VARIANT, Variant.byMetadata(meta));
	}

	@Override
	public int getMetaFromState(IBlockState state) {

		return state.getValue(VARIANT).getMetadata();
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {

		return getDefaultState().withProperty(VARIANT, Variant.byMetadata(meta));
	}

	@Override
	public boolean activated(World world, BlockPos pos, EntityPlayer player, EnumFacing side, EnumHand hand, @Nonnull ItemStack heldItem) {

		TileEntity te = world.getTileEntity(pos);
		if (te instanceof TileEntityRedNetCable) {
			TileEntityRedNetCable cable = (TileEntityRedNetCable) te;

			harvesters.set(player);
			IBlockState state = world.getBlockState(pos);
			RayTraceResult part = collisionRayTrace(state, world, pos,
					RayTracer.getStartVec(player), RayTracer.getEndVec(player));
			harvesters.set(null);
			if (part == null)
				return false;

			int subHit = part.subHit;
			if (subHit < 0) {
				MineFactoryReloadedCore.instance().getLogger().error("subHit was " + subHit, new Throwable());
				return false;
			}
			int subSide = _subSideMappings[subHit];

			if (cable.onPartHit(player, hand, subSide, subHit)) {
				;
			} else if (subHit >= (2 + 6 * 2) && subHit < (2 + 6 * 3)) {
				if (MFRUtil.isHoldingUsableTool(player, hand, pos, side)) {
					if (!world.isRemote) {
						int nextColor;
						if (!player.isSneaking()) {
							nextColor = cable.getSideColor(EnumFacing.VALUES[subSide]) + 1;
							if (nextColor > 15) nextColor = 0;
						} else {
							nextColor = cable.getSideColor(EnumFacing.VALUES[subSide]) - 1;
							if (nextColor < 0) nextColor = 15;
						}
						cable.setSideColor(EnumFacing.VALUES[subSide], nextColor);
					}
					// no calling back to the tool for just changing color
					return true;
				} else if (!heldItem.isEmpty() && heldItem.getItem().equals(Items.DYE)) {
					if (!world.isRemote) {
						cable.setSideColor(EnumFacing.VALUES[subSide], 15 - heldItem.getItemDamage());
					}
					return true;
				}
			} else if (subHit < (2 + 6 * 2) || subHit >= (2 + 6 * 5)) {
				l: if (MFRUtil.isHoldingUsableTool(player, hand, pos, side)) {
					if (!world.isRemote) {
						if (subSide > 6) {
							EnumFacing dir = EnumFacing.VALUES[subSide - 7];

							TileEntityRedNetCable cable2 = null;
							if (world.getTileEntity(pos.offset(dir)) instanceof TileEntityRedNetCable) {
								cable2 = (TileEntityRedNetCable) world.getTileEntity(pos.offset(dir));
							}

							cable.toggleSide(EnumFacing.VALUES[subSide - 7]);
							if (cable2 != null) {
								cable2.toggleSide(EnumFacing.VALUES[1 ^ subSide - 7]);
								if (cable.canInterface(cable2, dir)) {
									cable.getNetwork().addConduit(cable2);
								}
							}
							break l;
						}

						byte mode = cable.getMode(subSide);
						mode++;
						if (subSide == 6) {
							if (mode > 1)
								mode = 0;
							cable.setMode(subSide, mode);
							switch (mode) {
							case 0:
								player.sendMessage(new TextComponentTranslation("chat.info.mfr.rednet.tile.standard"));
								break;
							case 1:
								player.sendMessage(new TextComponentTranslation("chat.info.mfr.rednet.tile.cableonly"));
								break;
							default:
							}
							break l;
						}
						if (mode > 3) {
							mode = 0;
						}
						cable.setMode(subSide, mode);
						switch (mode) {
						case 0:
							player.sendMessage(new TextComponentTranslation("chat.info.mfr.rednet.connection.standard"));
							break;
						case 1:
							player.sendMessage(new TextComponentTranslation("chat.info.mfr.rednet.connection.forced"));
							break;
						case 2:
							player.sendMessage(new TextComponentTranslation("chat.info.mfr.rednet.connection.forcedstrong"));
							break;
						case 3:
							player.sendMessage(new TextComponentTranslation("chat.info.mfr.rednet.connection.cableonly"));
							break;
						default:
						}
					}
					MFRUtil.usedWrench(player, hand, pos, side);
					return true;
				} else if (!heldItem.isEmpty() && heldItem.getItem().equals(Items.DYE)) {
					if (!world.isRemote && subSide < 6) {
						cable.setSideColor(EnumFacing.VALUES[subSide], 15 - heldItem.getItemDamage());
						MFRUtil.notifyBlockUpdate(world, pos, state);
					}
					return subSide < 6;
				}
			}
		}
		return false;
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing face) {

		TileEntity te = world.getTileEntity(pos);
		if (te instanceof TileEntityRedNetCable) {
			RedNetConnectionType type = ((TileEntityRedNetCable)te).getCachedConnectionState(face);
			if (type.isPlate) {
				return BlockFaceShape.SOLID;
			} else if (type.isSingleSubnet && type.isCable) {
				return BlockFaceShape.MIDDLE_POLE;
			}
		}
		return BlockFaceShape.UNDEFINED;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {

		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {

		return false;
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase entity, @Nonnull ItemStack stack) {

		if (MFRConfig.defaultRedNetCableOnly.getBoolean(false)) {
			TileEntity te = world.getTileEntity(pos);
			if (te instanceof TileEntityRedNetCable) {
				TileEntityRedNetCable cable = (TileEntityRedNetCable) te;
				cable.setMode(6, (byte) 1);
			}
		}
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {

		super.breakBlock(world, pos, state);
		MFRUtil.wideNotifyNearbyBlocksExcept(world, pos, world.getBlockState(pos).getBlock());
	}

	@Override
	public boolean canDismantle(World world, BlockPos pos, IBlockState state, EntityPlayer player) {

		RayTraceResult part = collisionRayTrace(state, world, pos,
				RayTracer.getStartVec(player), RayTracer.getEndVec(player));
		if (part == null)
			return false;
		int subHit = part.subHit;
		return subHit < (2 + 6 * 2) | (subHit > (2 + 6 * 3));
	}

	@Override
	public int getWeakPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {

		int power = 0;
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof TileEntityRedNetCable) {
			power = ((TileEntityRedNetCable) te).getWeakPower(side.getOpposite());
		}
		return power;
	}

	@Override
	public int getStrongPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {

		int power = 0;
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof TileEntityRedNetCable) {
			power = ((TileEntityRedNetCable) te).getStrongPower(side.getOpposite());
		}
		return power;
	}

	@Override
	public boolean canPlaceTorchOnTop(IBlockState state, IBlockAccess world, BlockPos pos) {

		return isSideSolid(state, world, pos, EnumFacing.UP);
	}

	@Override
	public boolean isSideSolid(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {

		TileEntity te = world.getTileEntity(pos);
		if (te instanceof TileEntityRedNetCable)
			return ((TileEntityRedNetCable) te).isSolidOnSide(side);

		return false;
	}

	@Override
	public boolean hasTileEntity(IBlockState state) {

		return true;
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {

		switch (state.getValue(VARIANT)) {
		default:
		case REDSTONE:
			return new TileEntityRedNetCable();
		case ENERGY:
			return new TileEntityRedNetEnergy();
		}
	}

	@Override
	public void updateNetwork(World world, BlockPos pos, EnumFacing from) {

		TileEntity te = world.getTileEntity(pos);
		if (te instanceof TileEntityRedNetCable) {
			((TileEntityRedNetCable) te).updateNearbyNode(from);
		}
	}

	@Override
	public void updateNetwork(World world, BlockPos pos, int subnet, EnumFacing from) {

		TileEntity te = world.getTileEntity(pos);
		if (te instanceof TileEntityRedNetCable) {
			((TileEntityRedNetCable) te).updateNearbyNode(subnet, from);
		}
	}

	@Override
	public RedNetConnectionType getConnectionType(World world, BlockPos pos, EnumFacing side) {

		return RedNetConnectionType.CableAll;
	}

	@Override
	public void getBlockInfo(List<ITextComponent> info, IBlockAccess world, BlockPos pos, EnumFacing side, EntityPlayer player, boolean debug) {

		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof TileEntityRedNetCable) {
			RayTraceResult part = collisionRayTrace(world.getBlockState(pos), world, pos, RayTracer.getStartVec(player), RayTracer.getEndVec(player));
			if (part == null)
				return;

			int subHit = part.subHit;
			int mappedSide = _subSideMappings[subHit];
			if (mappedSide >= 0 && mappedSide < 6) {
				side = EnumFacing.VALUES[mappedSide];
			} else {
				side = null;
			}

			((TileEntityRedNetCable) tile).getTileInfo(info, side, player, debug);
		}
	}

	@Override
	public void getRedNetInfo(IBlockAccess world, BlockPos pos, EnumFacing side, EntityPlayer player, List<ITextComponent> info) {

		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof TileEntityRedNetCable) {
			RayTraceResult part = collisionRayTrace(world.getBlockState(pos), world, pos, RayTracer.getStartVec(player), RayTracer.getEndVec(player));
			if (part == null)
				return;

			int subHit = part.subHit;
			int mappedSide = _subSideMappings[subHit];
			if (mappedSide >= 0 && mappedSide < 6) {
				side = EnumFacing.VALUES[mappedSide];
			} else {
				side = null;
			}
			info.add(new TextComponentString(((TileEntityRedNetCable) tile).getRedNetInfo(side, player)));

			int value;
			int foundNonZero = 0;
			RedstoneNetwork _network = ((TileEntityRedNetCable) tile).getNetwork();
			for (int i = 0; i < 16; i++) {
				value = _network.getPowerLevelOutput(i);

				if (value != 0) {
					// TODO: localize color names v
					info.add(new TextComponentString(ItemRedNetMeter._colorNames[i]).appendText(": " + value));
					++foundNonZero;
				}
			}

			if (foundNonZero == 0) {
				info.add(new TextComponentTranslation("chat.info.mfr.rednet.meter.cable.allzero"));
			} else if (foundNonZero < 16) {
				info.add(new TextComponentTranslation("chat.info.mfr.rednet.meter.cable.restzero"));
			}
		}
	}

	@Override
	public boolean initialize() {

		MFRRegistry.registerBlock(this, new ItemBlockFactory(this, BlockRedNetCable.NAMES));
		GameRegistry.registerTileEntity(TileEntityRedNetCable.class, new ResourceLocation(MFRProps.MOD_ID, "rednet_cable"));
		GameRegistry.registerTileEntity(TileEntityRedNetEnergy.class, new ResourceLocation(MFRProps.MOD_ID, "rednet_cable_energy"));
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels() {

		ModelLoader.setCustomStateMapper(this, new StateMapperBase() {
			@Override protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
				return RedNetCableRenderer.MODEL_LOCATION;
			}
		});

		ModelResourceLocation location = new ModelResourceLocation(getRegistryName(), "normal");
		for (Variant variant : Variant.values()) {
			ModelLoader.setCustomModelResourceLocation(MFRRegistry.getItemBlock(this), variant.getMetadata(), location);
		}

		ModelRegistryHelper.register(RedNetCableRenderer.MODEL_LOCATION, new CCBakeryModel() {
			@Override
			public TextureAtlasSprite getParticleTexture() {
				return RedNetCableRenderer.sprite;
			}
		});
		ModelBakery.registerBlockKeyGenerator(this,
				state -> state.getBlock().getRegistryName().toString() + "," + state.getValue(VARIANT).getMetadata()
						+ "," + getBooleanPropertyArrayKey(state, CABLE_CONNECTION)
						+ "," + getBooleanPropertyArrayKey(state, IFACE)
						+ "," + getBooleanPropertyArrayKey(state, BAND)
						+ "," + getBooleanPropertyArrayKey(state, PLATE)
						+ "," + getBooleanPropertyArrayKey(state, PLATE_FACE)
						+ "," + getBooleanPropertyArrayKey(state, GRIP)
						+ "," + getBooleanPropertyArrayKey(state, WIRE)
						+ "," + getBooleanPropertyArrayKey(state, CAPS)
						+ "," + getIntegerPropertyArrayKey(state, COLOR)
		);
	}

	private String getBooleanPropertyArrayKey(IExtendedBlockState state, IUnlistedProperty<Boolean>[] propertyArray) {

		StringBuilder sb = new StringBuilder();

		for (IUnlistedProperty<Boolean> aPropertyArray : propertyArray) {
			sb.append(state.getValue(aPropertyArray) ? 1 : 0);
		}

		return sb.toString();
	}

	private String getIntegerPropertyArrayKey(IExtendedBlockState state, IUnlistedProperty<Integer>[] propertyArray) {

		StringBuilder sb = new StringBuilder();

		for(int i=0; i < propertyArray.length; i++) {
			if (i > 0)
				sb.append(",");
			sb.append(state.getValue(propertyArray[i]));
		}

		return sb.toString();
	}

	@Override
	public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {

		return RedNetCableRenderer.INSTANCE.handleState((IExtendedBlockState) super.getExtendedState(state, world, pos), world, pos);
	}

	@Override
	public IBakery getBakery() {

		return RedNetCableRenderer.INSTANCE;
	}

	public enum Variant implements IStringSerializable {

		REDSTONE(0, "redstone"),
		REDSTONE_GLASS(1, "redstone_glass"),
		ENERGY(2, "energy"),
		ENERGY_GLASS(3, "energy_glass");

		private final int meta;
		private final String name;

		private static final Variant[] META_LOOKUP = new Variant[values().length];
		public static final String[] NAMES = new String[values().length];

		Variant(int meta, String name) {

			this.meta = meta;
			this.name = name;
		}

		public static Variant byMetadata(int meta) {
			return META_LOOKUP[meta];
		}

		@Override
		public String getName() {
			return name;
		}

		private int getMetadata() {

			return meta;
		}

		static {
			for (Variant variant : values()) {
				META_LOOKUP[variant.getMetadata()] = variant;
				NAMES[variant.meta] = variant.name;
			}
		}
	}
}

