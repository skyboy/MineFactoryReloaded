package powercrystals.minefactoryreloaded.block;

import cofh.core.render.IModelRegister;
import cofh.core.util.core.IInitializer;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeColorHelper;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import powercrystals.minefactoryreloaded.MFRProps;
import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.api.rednet.connectivity.IRedNetNoConnection;
import powercrystals.minefactoryreloaded.gui.MFRCreativeTab;
import powercrystals.minefactoryreloaded.render.IColorRegister;
import powercrystals.minefactoryreloaded.setup.MFRThings;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BlockRubberLeaves extends BlockLeaves implements IRedNetNoConnection, IInitializer, IModelRegister, IColorRegister {

	public static final PropertyEnum<Variant> VARIANT = PropertyEnum.create("variant", Variant.class, input -> input.getMetadata() < 4);
	public static final PropertyBool FANCY = PropertyBool.create("fancy");
	
	public BlockRubberLeaves() {

		setUnlocalizedName("mfr.rubber_wood.leaves");
		setCreativeTab(MFRCreativeTab.tab);
		MFRThings.registerInitializer(this);
		MineFactoryReloadedCore.proxy.addModelRegister(this);
		MineFactoryReloadedCore.proxy.addColorRegister(this);
	}

	@Override
	protected BlockStateContainer createBlockState() {

		return new BlockStateContainer(this, VARIANT, FANCY, DECAYABLE, CHECK_DECAY);
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {

		return state.withProperty(FANCY, !isOpaqueCube(state));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public BlockRenderLayer getBlockLayer()	{
		
		return Blocks.LEAVES.getBlockLayer();
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {

		return Blocks.LEAVES.isOpaqueCube(state);
	}

	@Override
	public BlockPlanks.EnumType getWoodType(int i) {

		return null;
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {

		return Item.getItemFromBlock(MFRThings.rubberSaplingBlock);
	}

	@Override
	protected boolean canSilkHarvest() {
		return false;
	}

	private ThreadLocal<Boolean> updating = new ThreadLocal<>();

	@Override
	public void dropBlockAsItemWithChance(World world, BlockPos pos, IBlockState state, float chance, int fortune) {

		if (updating.get() != null)
			return;
		super.dropBlockAsItemWithChance(world, pos, state, chance, fortune);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {

		return this.getDefaultState().withProperty(VARIANT, this.getVariant(meta)).withProperty(DECAYABLE, (meta & 4) == 0).withProperty(CHECK_DECAY, (meta & 8) > 0);
	}

	private Variant getVariant(int meta) {

		return Variant.byMetadata(meta & 3);
	}
	
	@Override
	public int getMetaFromState(IBlockState state) {

		int meta = state.getValue(VARIANT).getMetadata();
		if(!state.getValue(DECAYABLE)) {
			meta |= 4;
		}

		if(state.getValue(CHECK_DECAY)) {
			meta |= 8;
		}

		return meta;	
	}

	@Override
	public ArrayList<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {

		ArrayList<ItemStack> ret = new ArrayList<>();
		Random rand = world instanceof World ? ((World)world).rand : RANDOM;

		if (!state.getValue(DECAYABLE)) // HACK: shears drop saplings AND the block because forge doesn't pay attention to the code they edit
			return ret;

		int chance = 20 + 15 * state.getValue(VARIANT).getMetadata();

		if (fortune > 0)
			chance = Math.max(chance - (2 << fortune), 10);

		if (rand.nextInt(chance) == 0)
			ret.add(new ItemStack(getItemDropped(getDefaultState().withProperty(VARIANT, state.getValue(VARIANT)), rand, fortune), 1,
					rand.nextInt(50000) == 0 ? 2 : 0));

		return ret;
	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {

		if (world.isRemote)
			return;
		if (state.getValue(VARIANT) == Variant.NORMAL && state.getValue(DECAYABLE)) {
			boolean decay = state.getValue(CHECK_DECAY);
			if (decay) {
				updating.set(Boolean.TRUE);
				super.updateTick(world, pos, state, rand);
				updating.set(null);
				if (!world.getBlockState(pos).getBlock().equals(this))
					dropBlockAsItem(world, pos, state, 0);
				return;
			}
			int chance = 15;
			Biome b = world.getBiome(pos);
			{
				float temp = b.getTemperature(pos);
				float rain = b.getRainfall();
				boolean t;
				decay = (t = rain <= 0.05f);
				if (t) chance -= 5;
				decay |= ((rain <= 0.2f) & temp >= 1.2f);
				decay |= (t = temp > 1.8f);
				if (t) chance -= 5;
				if (rain >= 0.4f & temp <= 1.4f)
					chance += 7;
				else if (temp < 0.8f)
					chance += 3;
			}
			if (decay && rand.nextInt(chance) == 0) {
				world.setBlockState(pos, state.withProperty(VARIANT, Variant.DRY));
				return;
			}
		}
		super.updateTick(world, pos, state, rand);
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {

		if (updating.get() != null) {
			boolean decay;
			int chance = 15;
			Biome b = world.getBiome(pos);
			{
				float temp = b.getTemperature(pos);
				float rain = b.getRainfall();
				boolean t;
				decay = (t = rain <= 0.05f);
				if (t) chance -= 5;
				decay |= ((rain <= 0.2f) & temp >= 1.2f);
				decay |= (t = temp > 1.8f);
				if (t) chance -= 5;
				if (rain >= 0.4f & temp <= 1.4f)
					chance += 7;
				else if (temp < 0.8f)
					chance += 3;
			}
			if (decay && world.rand.nextInt(chance) == 0)
				world.setBlockState(pos, state.withProperty(VARIANT, Variant.DRY));
		}
	}

	@Override
	public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face) {

		return super.getFireSpreadSpeed(world, pos, face) * ((world.getBlockState(pos).getValue(VARIANT).getMetadata()) * 2 + 1);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {

		boolean cube = isOpaqueCube(state);
		return cube ? super.shouldSideBeRendered(state, world, pos, side) : true;
	}

	@Override
	public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {

		items.add(new ItemStack(this, 1, 0));
		items.add(new ItemStack(this, 1, 1));
	}

	@Override
	public List<ItemStack> onSheared(@Nonnull ItemStack itemStack, IBlockAccess iBlockAccess, BlockPos blockPos, int i) {

		ArrayList<ItemStack> ret = new ArrayList<>();
		ret.add(new ItemStack(this, 1, this.getMetaFromState(iBlockAccess.getBlockState(blockPos)) & 3));
		return ret;
	}

	@Override public boolean preInit() {

		return false;
	}

	@Override
	public boolean initialize() {

		Blocks.FIRE.setFireInfo(this, 80, 25);
		MFRRegistry.registerBlock(this, new ItemBlockFactoryLeaves(this));
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels() {

		ModelLoader.setCustomStateMapper(this, new StateMap.Builder().ignore(BlockRubberLeaves.CHECK_DECAY, BlockRubberLeaves.DECAYABLE).build());
		Item item = MFRRegistry.getItemBlock(this);
		final ModelResourceLocation[] leavesModels = new ModelResourceLocation[4];
		for(int i = 0; i < 4; i++) {
			String variant = "fancy=" + (i < 2) + ",variant=" + (((i % 2) == 0) ? "normal" : "dry");
			leavesModels[i] = new ModelResourceLocation(MFRProps.PREFIX + "rubber_wood_leaves", variant);
			ModelLoader.registerItemVariants(item, leavesModels[i]);
		}
		ModelLoader.setCustomMeshDefinition(item, stack -> {
			int id = Minecraft.getMinecraft().gameSettings.fancyGraphics ? 0 : 2;
			id += (stack.getMetadata() == 0 ? 0 : 1);
			return leavesModels[id];
		});
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerColorHandlers() {

		Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler((state, world, pos, tintIndex) -> {

			BlockRubberLeaves.Variant variant = state.getValue(VARIANT);

			int foliageColor;
			if (world != null && pos != null) {
				foliageColor = BiomeColorHelper.getFoliageColorAtPos(world, pos);
			} else {
				foliageColor = ColorizerFoliage.getFoliageColorBasic();
			}

			if (variant == BlockRubberLeaves.Variant.DRY) {
				int r = (foliageColor & 16711680) >> 16;
				int g = (foliageColor & 65280) >> 8;
				int b = foliageColor & 255;
				return ( r / 4 << 16 | g / 4 << 8 | b / 4) + 0xc0c0c0;
			}

			return foliageColor;
		}, MFRThings.rubberLeavesBlock);

		Minecraft.getMinecraft().getItemColors().registerItemColorHandler(
				(stack, tintIndex) -> stack.getMetadata() == 1 ? 0xFFFFFF : ColorizerFoliage.getFoliageColorBasic(), this);

	}

	public enum Variant implements IStringSerializable {

		NORMAL (0, "normal"),
		DRY(1, "dry");

		private int meta;
		private String name;

		private static final Variant[] META_LOOKUP = new Variant[values().length];
		Variant(int meta, String name) {

			this.meta = meta;
			this.name = name;
		}

		@Override
		public String getName() {
			return name;
		}

		public int getMetadata() {
			return meta;
		}

		public static Variant byMetadata(int meta) {
			
			if(meta < 0 || meta >= META_LOOKUP.length) {
				meta = 0;
			}

			return META_LOOKUP[meta];
		}

		static {
			for(int i = 0; i < values().length; ++i) {
				Variant variant = values()[i];
				META_LOOKUP[variant.getMetadata()] = variant;
			}
		}

		public static final String[] NAMES;
		static {
			NAMES = new String[values().length];
			for (Variant variant : values()) {
				NAMES[variant.meta] = variant.name;
			}
		}

	}

}
