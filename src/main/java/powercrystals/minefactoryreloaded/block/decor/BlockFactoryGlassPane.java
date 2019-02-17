package powercrystals.minefactoryreloaded.block.decor;

import codechicken.lib.model.ModelRegistryHelper;
import codechicken.lib.model.bakery.CCBakeryModel;
import codechicken.lib.model.bakery.IBakeryProvider;
import codechicken.lib.model.bakery.ModelBakery;
import codechicken.lib.model.bakery.generation.IBakery;
import cofh.core.render.IModelRegister;
import cofh.core.util.core.IInitializer;
import net.minecraft.block.BlockPane;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.common.property.Properties;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import powercrystals.minefactoryreloaded.MFRProps;
import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.api.rednet.connectivity.IRedNetDecorative;
import powercrystals.minefactoryreloaded.block.ItemBlockFactory;
import powercrystals.minefactoryreloaded.core.MFRDyeColor;
import powercrystals.minefactoryreloaded.gui.MFRCreativeTab;
import powercrystals.minefactoryreloaded.render.IColorRegister;
import powercrystals.minefactoryreloaded.render.block.FactoryGlassPaneRenderer;
import powercrystals.minefactoryreloaded.render.block.FactoryGlassRenderer;
import powercrystals.minefactoryreloaded.setup.MFRThings;

import java.util.HashMap;
import java.util.Map;

public class BlockFactoryGlassPane extends BlockPane implements IRedNetDecorative, IBakeryProvider, IInitializer, IModelRegister, IColorRegister
{
	public static final PropertyEnum<MFRDyeColor> COLOR = PropertyEnum.create("color", MFRDyeColor.class); //TODO move properties to one place
	public static final IUnlistedProperty<Integer>[] CTM_VALUE = new IUnlistedProperty[4];
	public static final IUnlistedProperty<Integer> FACES = Properties.toUnlisted(PropertyInteger.create("faces", 0, 16384));

	static {
		for (int i = 0; i < 4; i++)
			CTM_VALUE[i] = Properties.toUnlisted(PropertyInteger.create("ctm_value_" + i, 0, 255));
	}

	public BlockFactoryGlassPane()
	{
		this(true);
	}

	public BlockFactoryGlassPane(boolean mfr)
	{
		super(Material.GLASS, false);
		setHardness(0.3F);
		setSoundType(SoundType.GLASS);
		if (mfr)
		{
			setCreativeTab(MFRCreativeTab.tab);
			setUnlocalizedName("mfr.stained_glass.pane");
		}
		else
			setCreativeTab(CreativeTabs.DECORATIONS);

		MFRThings.registerInitializer(this);
		MineFactoryReloadedCore.proxy.addModelRegister(this);
		MineFactoryReloadedCore.proxy.addColorRegister(this);
	}

	@Override
	public int damageDropped(IBlockState state)
	{
		return getMetaFromState(state);
	}

	@Override
	public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {

		IExtendedBlockState extState = (IExtendedBlockState) state;

		MFRDyeColor color = extState.getValue(COLOR);
		Map<Integer, Tuple<Boolean, Boolean>> connections = getConnections(world, pos, color);

		for(EnumFacing facing : EnumFacing.HORIZONTALS) {
			extState = extState.withProperty(CTM_VALUE[facing.getHorizontalIndex()], getCTMValue(connections, facing));
		}

		extState = extState.withProperty(FACES, getFacesValue(connections));

		return extState;
	}

	private int getFacesValue(Map<Integer, Tuple<Boolean, Boolean>> connections) {

		int facesValue = 0;

		for(int i=0; i<10; i++) {
			facesValue |= (connections.get(i).getSecond() ? 1 : 0) << i;
		}

		for(int i=14; i<18; i++) {
			facesValue |= (connections.get(i).getSecond() ? 1 : 0) << (i - 4);
		}

		return facesValue;
	}

	private Map<Integer, Tuple<Boolean, Boolean>> getConnections(IBlockAccess world, BlockPos pos, MFRDyeColor color)
	{
		Map<Integer, Tuple<Boolean, Boolean>> connections = new HashMap<>();

		for(EnumFacing facing : EnumFacing.VALUES) {
			updateSideConnection(world, pos, color, connections, facing);
		}

		for(EnumFacing facing : EnumFacing.HORIZONTALS) {
			updateDiagonalConnection(world, pos, connections, facing, EnumFacing.UP, 6);
			updateDiagonalConnection(world, pos, connections, facing, EnumFacing.HORIZONTALS[(facing.getHorizontalIndex() + 1) % 4], 10);
			updateDiagonalConnection(world, pos, connections, facing, EnumFacing.DOWN, 14);
		}

		return connections;
	}

	private void updateDiagonalConnection(IBlockAccess world, BlockPos pos, Map<Integer, Tuple<Boolean, Boolean>> connections, EnumFacing facing,
			EnumFacing secondFacing, int initialIndex)
	{
		boolean showFace = !(secondFacing != EnumFacing.UP && secondFacing != EnumFacing.DOWN) && !canPaneConnectTo(world, pos.offset(secondFacing), facing);
		if(connections.get(secondFacing.ordinal()).getFirst() && connections.get(facing.ordinal()).getFirst()) {
			IBlockState neighborState = world.getBlockState(pos.offset(secondFacing).offset(facing));
			connections.put(initialIndex + facing.getHorizontalIndex(),	new Tuple<>(neighborState.getBlock() == this, showFace));
		} else {
			connections.put(initialIndex + facing.getHorizontalIndex(), new Tuple<>(false, showFace));
		}
	}

	private void updateSideConnection(IBlockAccess world, BlockPos pos, MFRDyeColor color, Map<Integer, Tuple<Boolean, Boolean>> connections, EnumFacing facing)
	{
		IBlockState stateNeighbor = world.getBlockState(pos.offset(facing));
		connections.put(facing.ordinal(), new Tuple<>(stateNeighbor.getBlock() == this, stateNeighbor.getBlock() == this && stateNeighbor.getValue(COLOR) != color));
	}

	@Override
	protected BlockStateContainer createBlockState()
	{
		BlockStateContainer.Builder builder = new BlockStateContainer.Builder(this);
		builder.add(COLOR);
		builder.add(NORTH);
		builder.add(SOUTH);
		builder.add(EAST);
		builder.add(WEST);
		for (int i = 0; i < 4; i++) {
			builder.add(CTM_VALUE[i]);
		}
		builder.add(FACES);
		return builder.build();
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return getDefaultState().withProperty(COLOR, MFRDyeColor.byMetadata(meta));
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		return state.getValue(COLOR).getMetadata();
	}

	@Override
	public BlockRenderLayer getBlockLayer()
	{
		return BlockRenderLayer.TRANSLUCENT;
	}

	private int getCTMValue(Map<Integer, Tuple<Boolean, Boolean>> connections, EnumFacing side)
	{
		boolean[] sides = new boolean[8];
		EnumFacing left = EnumFacing.HORIZONTALS[(side.getHorizontalIndex() + 1) % 4];
		EnumFacing right = left.getOpposite();

		sides[0] = connections.get(right.ordinal()).getFirst(); //right
		sides[4] = connections.get(14 + right.getHorizontalIndex()).getFirst(); //right down
		sides[1] = connections.get(EnumFacing.DOWN.ordinal()).getFirst(); //down
		sides[5] = connections.get(14 + left.getHorizontalIndex()).getFirst(); //left down
		sides[3] = connections.get(left.ordinal()).getFirst(); //left
		sides[6] = connections.get(6 + left.getHorizontalIndex()).getFirst(); //left up
		sides[2] = connections.get(EnumFacing.UP.ordinal()).getFirst(); //up
		sides[7] = connections.get(6 + right.getHorizontalIndex()).getFirst(); //right up

		return toInt(sides) & 255;
	}

	private static int toInt(boolean ...flags) {
		int ret = 0;
		for (int i = flags.length; i --> 0;)
			ret |= (flags[i] ? 1 : 0) << i;
		return ret;
	}

	@Override
	public IBakery getBakery() {
		
		return FactoryGlassPaneRenderer.INSTANCE;
	}

	@Override public boolean preInit() {

		return false;
	}

	@Override
	public boolean initialize() {

		MFRRegistry.registerBlock(this, new ItemBlockFactory(this, MFRDyeColor.UNLOC_NAMES));
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels() {

		ModelLoader.setCustomStateMapper(this, new StateMapperBase() {
			@Override
			protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
				return FactoryGlassPaneRenderer.MODEL_LOCATION;
			}
		});
		ModelRegistryHelper.register(FactoryGlassPaneRenderer.MODEL_LOCATION, new CCBakeryModel() {
			@Override public TextureAtlasSprite getParticleTexture() {
				return FactoryGlassRenderer.spriteSheet.getSprite(FactoryGlassRenderer.FULL_FRAME);
			}
		});
		ModelBakery.registerBlockKeyGenerator(this,
				state -> state.getBlock().getRegistryName().toString() + "," + state.getValue(BlockFactoryGlassPane.COLOR).getMetadata()
						+ "," + state.getValue(BlockFactoryGlassPane.CTM_VALUE[0])
						+ "," + state.getValue(BlockFactoryGlassPane.CTM_VALUE[1])
						+ "," + state.getValue(BlockFactoryGlassPane.CTM_VALUE[2])
						+ "," + state.getValue(BlockFactoryGlassPane.CTM_VALUE[3])
						+ "," + state.getValue(BlockFactoryGlassPane.FACES)
						+ "," + (state.getValue(BlockPane.NORTH) ? 1 : 0)
						+ "," + (state.getValue(BlockPane.SOUTH) ? 1 : 0)
						+ "," + (state.getValue(BlockPane.WEST) ? 1 : 0)
						+ "," + (state.getValue(BlockPane.EAST) ? 1 : 0)
		);
		ModelResourceLocation glassPaneItemModel = new ModelResourceLocation(MFRProps.PREFIX + "stained_glass_pane", "inventory");
		Item item = MFRRegistry.getItemBlock(this);
		ModelLoader.setCustomMeshDefinition(item, stack -> glassPaneItemModel);
		ModelLoader.registerItemVariants(item, glassPaneItemModel);

	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerColorHandlers() {

		Minecraft.getMinecraft().getItemColors().registerItemColorHandler((stack, tintIndex) -> {

			if (tintIndex != 0 || stack.getMetadata() > 15 || stack.getMetadata() < 0)
				return 0xFFFFFF;

			return MFRDyeColor.byMetadata(stack.getMetadata()).getColor();
		}, this);
	}
}
