package powercrystals.minefactoryreloaded.block.transport;

import cofh.core.render.IModelRegister;
import cofh.core.util.core.IInitializer;
import com.google.common.base.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import powercrystals.minefactoryreloaded.MFRProps;
import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.gui.MFRCreativeTab;
import powercrystals.minefactoryreloaded.setup.MFRThings;

public class BlockFactoryRail extends BlockRailBase implements IInitializer, IModelRegister {

	private static IProperty[] containerData = null;
	private static boolean generateContainerData(boolean powered, boolean slopes) {

		containerData = new IProperty[1 + (powered ? 1 : 0)];
		containerData[0] = powered ? (!slopes ? SHAPE_STRAIGHT_FLAT : SHAPE_STRAIGHT) : (!slopes ? SHAPE_FLAT : SHAPE);
		if (powered)
			containerData[1] = POWERED;
		return powered;
	}

	private static final Predicate<BlockRailBase.EnumRailDirection> straight = dir ->
			dir != EnumRailDirection.NORTH_EAST && dir != EnumRailDirection.NORTH_WEST &&
					dir != EnumRailDirection.SOUTH_EAST && dir != EnumRailDirection.SOUTH_WEST;
	private static final Predicate<BlockRailBase.EnumRailDirection> no_slope = dir ->
			dir != EnumRailDirection.ASCENDING_EAST && dir != EnumRailDirection.ASCENDING_WEST &&
					dir != EnumRailDirection.ASCENDING_NORTH && dir != EnumRailDirection.ASCENDING_SOUTH;

	public static final PropertyEnum<EnumRailDirection> SHAPE = PropertyEnum.create("shape", BlockRailBase.EnumRailDirection.class);
	public static final PropertyEnum<EnumRailDirection> SHAPE_STRAIGHT = PropertyEnum.create("shape", BlockRailBase.EnumRailDirection.class, straight);
	public static final PropertyEnum<EnumRailDirection> SHAPE_FLAT = PropertyEnum.create("shape", BlockRailBase.EnumRailDirection.class, no_slope);
	public static final PropertyEnum<EnumRailDirection> SHAPE_STRAIGHT_FLAT = PropertyEnum.create("shape", BlockRailBase.EnumRailDirection.class, dir ->
			straight.apply(dir) && no_slope.apply(dir));
	public static final PropertyBool POWERED = PropertyBool.create("powered");

	protected boolean canSlope;
	protected final IProperty<EnumRailDirection> shapeProperty;

	public BlockFactoryRail(boolean powered, boolean slopes) {

		super(generateContainerData(powered, slopes));
		shapeProperty = (IProperty<EnumRailDirection>) blockState.getProperty("shape");
		containerData = null;
		setHardness(0.5F);
		setSoundType(SoundType.METAL);
		setCreativeTab(MFRCreativeTab.tab);
		canSlope = slopes;
		MFRThings.registerInitializer(this);
		MineFactoryReloadedCore.proxy.addModelRegister(this);
	}

	@Override
	protected BlockStateContainer createBlockState() {

		/**
		 * static property used here because this is called by Block(), as such isPowered and canSlope will not yet have been set
		 */
		IProperty[] data = containerData == null ? new IProperty[] { SHAPE } : containerData;
		return new BlockStateContainer(this, data);
	}

	/**
	 * allows for powered not-sloping rails that can curve
	 */
	private int getShapeMeta(int meta, boolean forLookup) {

		if (canSlope) {
			return meta % (isPowered ? 6 : 15);
		}
		if (forLookup) {
			return meta > 1 ? meta + 4 : meta;
		} else {
			return meta > 5 ? meta - 4 : meta;
		}
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {

		IBlockState r = getDefaultState().withProperty(shapeProperty, BlockRailBase.EnumRailDirection.byMetadata(getShapeMeta(meta & 7, true)));
		if (isPowered) {
			r = r.withProperty(POWERED, (meta & 8) > 0);
		}
		return r;
	}

	@Override
	public int getMetaFromState(IBlockState state) {

		int meta = getShapeMeta(state.getValue(shapeProperty).getMetadata(), false);
		if (isPowered) {
			meta |= (state.getValue(POWERED) ? 8 : 0);
		}
		return meta;
	}

	@Override
	public boolean canMakeSlopes(IBlockAccess world, BlockPos pos) {

		return canSlope;
	}

	public boolean isPowered(World world, BlockPos pos) {

		if (!isPowered) {
			return false;
		}
		return world.getBlockState(pos).getValue(POWERED);
	}

	@Override
	protected void updateState(IBlockState state, World world, BlockPos pos, Block neighbor) {

		if (!isPowered) {
			return;
		}
		boolean newPowered = world.isBlockIndirectlyGettingPowered(pos) > 0;
		boolean oldPowered = state.getValue(POWERED);
		if (newPowered != oldPowered) {
			world.setBlockState(pos, state.withProperty(POWERED, newPowered));
		}
	}

	@Override
	public IProperty<EnumRailDirection> getShapeProperty() {

		return shapeProperty;
	}

	@Override public boolean preInit() {

		return false;
	}

	@Override
	public boolean initialize() {

		MFRRegistry.registerBlock(this, new ItemBlock(this));
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels() {
		
	}

	@SideOnly(Side.CLIENT)
	static void registerRailModel(final BlockRailBase railBlock, final String typeVariant) {
		
		ModelLoader.setCustomStateMapper(railBlock, new StateMapperBase() {
			@Override
			protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
				return new ModelResourceLocation(MFRProps.PREFIX + "rail",
						"shape=" + state.getValue(railBlock.getShapeProperty()) + ",type=" + typeVariant);
			}
		});

		Item item = MFRRegistry.getItemBlock(railBlock);
		if (item != null)
			ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(MFRProps.PREFIX + "rail_" + typeVariant, "inventory"));
	}
}
