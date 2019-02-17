package powercrystals.minefactoryreloaded.block.transport;

import cofh.core.render.IModelRegister;
import cofh.core.util.CoreUtils;
import cofh.core.util.core.IInitializer;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.block.ItemBlockFactoryRoad;
import powercrystals.minefactoryreloaded.gui.MFRCreativeTab;
import powercrystals.minefactoryreloaded.render.ModelHelper;
import powercrystals.minefactoryreloaded.setup.MFRThings;

public class BlockFactoryRoad extends Block implements IInitializer, IModelRegister {

	private static final PropertyEnum<Variant> VARIANT = PropertyEnum.create("variant", Variant.class);
	
	public BlockFactoryRoad() {

		super(Material.ROCK);
		setHardness(2.0F);
		setUnlocalizedName("mfr.road");
		setResistance(25.0F);
		setSoundType(SoundType.STONE);
		setCreativeTab(MFRCreativeTab.tab);
		MFRThings.registerInitializer(this);
		MineFactoryReloadedCore.proxy.addModelRegister(this);
	}

	@Override
	// TODO: continue to verify this is called every tick by walking entities (last check: 1.10.2)
	public void onEntityWalk(World world, BlockPos pos, Entity e) {

		final double boost = .99 * slipperiness;
		final double minSpeed = 1e-9;

		if (Math.abs(e.motionX) > minSpeed || Math.abs(e.motionZ) > minSpeed) {
			e.motionX += e.motionX * boost;
			e.motionZ += e.motionZ * boost;
		}
	}

	@Override
	protected BlockStateContainer createBlockState() {

		return new BlockStateContainer(this, VARIANT);
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
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {

		if (!world.isRemote) {
			Variant variant = state.getValue(VARIANT);
			boolean isPowered = CoreUtils.isRedstonePowered(world, pos);
			Variant newVariant = null;

			if (variant == Variant.LIGHT_OFF && isPowered) {
				newVariant = Variant.LIGHT_ON;
			}
			else if (variant == Variant.LIGHT_ON && !isPowered) {
				newVariant = Variant.LIGHT_OFF;
			}
			else if (variant == Variant.LIGHT_INVERTED_OFF && !isPowered) {
				newVariant = Variant.LIGHT_INVERTED_ON;
			}
			else if (variant == Variant.LIGHT_INVERTED_ON && isPowered) {
				newVariant = Variant.LIGHT_INVERTED_OFF;
			}

			if (newVariant != null) {
				world.setBlockState(pos, state.withProperty(VARIANT, newVariant));
			}
		}
	}

	@Override
	public boolean canCreatureSpawn(IBlockState state, IBlockAccess world, BlockPos pos, EntityLiving.SpawnPlacementType type) {

		return false;
	}

	@Override
	public int damageDropped(IBlockState state) {

		Variant variant = state.getValue(VARIANT);

		switch (variant) {
			case LIGHT_OFF:
			case LIGHT_ON:
				return Variant.LIGHT_OFF.getMetadata();
			case LIGHT_INVERTED_ON:
			case LIGHT_INVERTED_OFF:
				return Variant.LIGHT_INVERTED_ON.getMetadata();
			default:
				return variant.getMetadata();
		}
	}

	@Override
	public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {

		Variant variant = state.getValue(VARIANT);
		return variant == Variant.LIGHT_ON || variant == Variant.LIGHT_INVERTED_ON ? 15 : 0;
	}

	@Override
	public void onBlockAdded(World world, BlockPos pos, IBlockState state) {

		neighborChanged(state, world, pos, this, pos);
	}

	@Override
	public boolean canEntityDestroy(IBlockState state, IBlockAccess world, BlockPos pos, Entity entity) {

		if (entity instanceof EntityDragon) {
			return false;
		}

		return true;
	}

	@Override public boolean preInit() {

		return false;
	}

	@Override
	public boolean initialize() {

		MFRRegistry.registerBlock(this, new ItemBlockFactoryRoad(this));
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels() {

		ModelHelper.registerModel(this, "variant", Variant.NAMES);
	}

	public enum Variant implements IStringSerializable {
		
		NORMAL(0, "normal"),
		LIGHT_OFF(1, "light_off"),
		LIGHT_ON(2, "light_on"),
		LIGHT_INVERTED_OFF(3, "light_inverted_off"),
		LIGHT_INVERTED_ON(4, "light_inverted_on");

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
