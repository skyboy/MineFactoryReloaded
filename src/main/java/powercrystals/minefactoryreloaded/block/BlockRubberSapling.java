package powercrystals.minefactoryreloaded.block;

import cofh.core.render.IModelRegister;
import cofh.core.util.core.IInitializer;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.IGrowable;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.terraingen.TerrainGen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.api.rednet.connectivity.IRedNetNoConnection;
import powercrystals.minefactoryreloaded.gui.MFRCreativeTab;
import powercrystals.minefactoryreloaded.render.ModelHelper;
import powercrystals.minefactoryreloaded.setup.MFRThings;
import powercrystals.minefactoryreloaded.world.MineFactoryReloadedWorldGen;
import powercrystals.minefactoryreloaded.world.WorldGenMassiveTree;
import powercrystals.minefactoryreloaded.world.WorldGenRubberTree;

import java.util.Locale;
import java.util.Random;

public class BlockRubberSapling extends BlockBush implements IRedNetNoConnection, IGrowable, IInitializer, IModelRegister {

	public static final PropertyEnum<Type> TYPE = PropertyEnum.create("type", Type.class);
	private static WorldGenRubberTree treeGen = new WorldGenRubberTree(true);

	public BlockRubberSapling() {

		setHardness(0.0F);
		setSoundType(SoundType.PLANT);
		setUnlocalizedName("mfr.rubber_wood.sapling");
		setCreativeTab(MFRCreativeTab.tab);
		this.setDefaultState(blockState.getBaseState().withProperty(TYPE, Type.NORMAL).withProperty(STAGE, 0));
		MFRThings.registerInitializer(this);
		MineFactoryReloadedCore.proxy.addModelRegister(this);
	}

	@Override
	public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {

		for (Type type : Type.values())	{
			items.add(new ItemStack(this, 1, type.getMetadata()));
		}
	}

	private void generateTree(World world, BlockPos pos, IBlockState state, Random rand) {

		if (world.isRemote || !TerrainGen.saplingGrowTree(world, rand, pos))
			return;

		Type type = state.getValue(TYPE);
		world.setBlockToAir(pos);

		switch (type) {
			case SACRED_SPRING:
				if (MineFactoryReloadedWorldGen.generateSacredSpringRubberTree(world, rand, pos))
					return;
				break;
			case MEGA:
				if (MineFactoryReloadedWorldGen.generateMegaRubberTree(world, rand, pos, true))
					return;
				break;
			case MASSIVE:
				if (new WorldGenMassiveTree().setSloped(true).generate(world, rand, pos))
					return;
				break;
			default:
			case NORMAL:
				Biome b = world.getBiome(pos);
				if (b.getBiomeName().toLowerCase(Locale.US).contains("mega"))
					if (rand.nextInt(50) == 0)
						if (MineFactoryReloadedWorldGen.generateMegaRubberTree(world, rand, pos, true))
							return;
				if (treeGen.growTree(world, rand, pos.getX(), pos.getY(), pos.getZ()))
					return;
				break;
		}
		world.setBlockState(pos, getDefaultState().withProperty(TYPE, type), 4);
	}

	@Override
	public int damageDropped(IBlockState state) {

		return state.getValue(TYPE).getMetadata();
	}

	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(TYPE, Type.byMetadata(meta & 7)).withProperty(STAGE, (meta & 8) >> 3);
	}

	public int getMetaFromState(IBlockState state) {
		byte i = 0;
		int i1 = i | (state.getValue(TYPE)).getMetadata();
		i1 |= state.getValue(STAGE) << 3;
		return i1;
	}

	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, TYPE, STAGE);
	}

	@Override public boolean preInit() {

		return false;
	}

	@Override
	public boolean initialize() {

		MFRRegistry.registerBlock(this, new ItemBlockFactoryTree(this));
		Blocks.FIRE.setFireInfo(this, 30, 20);
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels() {

		ModelLoader.setCustomStateMapper(this, new StateMap.Builder().ignore(TYPE, STAGE).build());
		Item item = MFRRegistry.getItemBlock(this);
		for (int i=0; i<4; i++) {
			ModelHelper.registerModel(item, i, "rubber_wood_sapling");
		}
	}

	private enum Type implements IStringSerializable {
		
		NORMAL(0, "normal"),
		SACRED_SPRING(1, "sacred_spring"),
		MEGA(2, "mega"),
		MASSIVE(3, "massive");

		private int meta;
		private String name;
		private static final Type[] META_LOOKUP = new Type[values().length];

		Type(int meta, String name) {

			this.meta = meta;
			this.name = name;
		}
		
		public String getName() {
			return name;
		}

		public int getMetadata() {
			return meta;
		}
		
		public static Type byMetadata(int meta) {

			if(meta < 0 || meta >= META_LOOKUP.length) {
				meta = 0;
			}

			return META_LOOKUP[meta];
		}

		static {
			for(int i = 0; i < values().length; ++i) {
				Type variant = values()[i];
				META_LOOKUP[variant.getMetadata()] = variant;
			}

		}
	}
	
	////// Vanilla BlockSapling functionality (because it has call in constructor because of which it can't be inherited) //////
	public static final PropertyInteger STAGE = BlockSapling.STAGE;
	protected static final AxisAlignedBB SAPLING_AABB = new AxisAlignedBB(0.09999999403953552D, 0.0D, 0.09999999403953552D, 0.8999999761581421D, 0.800000011920929D, 0.8999999761581421D);

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
	{
		return SAPLING_AABB;
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
	{
		if (!worldIn.isRemote)
		{
			super.updateTick(worldIn, pos, state, rand);

			if (worldIn.getLightFromNeighbors(pos.up()) >= 9 && rand.nextInt(7) == 0)
			{
				this.grow(worldIn, pos, state, rand);
			}
		}
	}

	public void grow(World worldIn, BlockPos pos, IBlockState state, Random rand)
	{
		if (state.getValue(STAGE) == 0)
		{
			worldIn.setBlockState(pos, state.cycleProperty(STAGE), 4);
		}
		else
		{
			this.generateTree(worldIn, pos, state, rand);
		}
	}

	/**
	 * Whether this IGrowable can grow
	 */
	@Override
	public boolean canGrow(World world, BlockPos pos, IBlockState state, boolean isClient)
	{
		return true;
	}

	@Override
	public boolean canUseBonemeal(World world, Random rand, BlockPos pos, IBlockState state)
	{
		return (double)world.rand.nextFloat() < 0.45D;
	}

	@Override
	public void grow(World world, Random rand, BlockPos pos, IBlockState state)
	{
		this.grow(world, pos, state, rand);
	}
}
