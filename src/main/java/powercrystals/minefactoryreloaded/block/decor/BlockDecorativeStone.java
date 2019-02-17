package powercrystals.minefactoryreloaded.block.decor;

import net.minecraft.block.BlockFalling;
import net.minecraft.block.BlockSand;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.block.BlockFactory;
import powercrystals.minefactoryreloaded.block.ItemBlockFactory;
import powercrystals.minefactoryreloaded.core.UtilInventory;
import powercrystals.minefactoryreloaded.render.ModelHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

public class BlockDecorativeStone extends BlockFactory {

	public static final PropertyEnum<Variant> VARIANT = PropertyEnum.create("variant", Variant.class);

	public BlockDecorativeStone() {

		super(Material.ROCK);
		setHardness(2.0F);
		setResistance(10.0F);
		setSoundType(SoundType.STONE);
		setUnlocalizedName("mfr.decorative.stone");
		providesPower = false;
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

		return state.getValue(VARIANT).meta;
	}

	@Override
	public int damageDropped(IBlockState state) {

		int meta = getMetaFromState(state);
		if (meta == 0 | meta == 1) {
			meta += 2; // smooth -> cobble
		}
		return meta;
	}

	@Override
	public ArrayList<ItemStack> dismantleBlock(World world, BlockPos pos, IBlockState state, EntityPlayer player, boolean returnBlock) {

		ArrayList<ItemStack> list = new ArrayList<>();
		int meta = getMetaFromState(state);
		list.add(new ItemStack(getItemDropped(state, world.rand, 0), quantityDropped(world.rand), meta)); // persist metadata

		world.setBlockToAir(pos);
		if (!returnBlock)
			for (@Nonnull ItemStack item : list) {
				UtilInventory.dropStackInAir(world, pos, item);	
			}
		return list;
	}

	@Override
	public void onBlockAdded(World world, BlockPos pos, IBlockState state) {

		world.scheduleBlockUpdate(pos, this, tickRate(world), 1);
	}

	@Override
	public void onNeighborChange(IBlockAccess blockAccess, BlockPos pos, BlockPos neighbor) {

		if (blockAccess instanceof World)
		{
			World world = (World) blockAccess;
			world.scheduleBlockUpdate(pos, this, tickRate(world), 1);
		}
	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random random) {

		if (!world.isRemote) {
			tryToFall(world, pos, state);
		}
	}

	private void tryToFall(World world, BlockPos pos, IBlockState state) {

		int meta = getMetaFromState(state);
		if (meta != 8 & meta != 9)
			return;
		if (BlockFalling.canFallThrough(world.getBlockState(new BlockPos(pos.down()))) && pos.getY() >= 0) {
			if (!BlockSand.fallInstantly && world.isAreaLoaded(pos.add(-32, -32, -32), pos.add(32, 32, 32))) {
				if (!world.isRemote) {
					EntityFallingBlock entityFallingBlock = new EntityFallingBlock(world, (double)pos.getX() + 0.5D, (double)pos.getY(), (double)pos.getZ() + 0.5D, state);
					world.spawnEntity(entityFallingBlock);
				}
			} else {
				world.setBlockToAir(pos);
				BlockPos blockpos;

				for (blockpos = pos.down(); (world.isAirBlock(blockpos) || BlockFalling.canFallThrough(world.getBlockState(blockpos))) && blockpos.getY() > 0; blockpos = blockpos.down())
				{
				}

				if (blockpos.getY() > 0)
				{
					world.setBlockState(blockpos.up(), state); //Forge: Fix loss of state information during world gen.
				}
			}
		}
	}

	@Override
	public int tickRate(World world) {

		return 2;
	}

	@Override
	public boolean initialize() {

		MFRRegistry.registerBlock(this, new ItemBlockFactory(this, Variant.UNLOC_NAMES));
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels() {

		ModelHelper.registerModel(this, "variant", Variant.NAMES);
	}

	@Override
	public SoundType getSoundType(IBlockState state, World world, BlockPos pos, @Nullable Entity entity) {

		switch (state.getValue(VARIANT).meta) {
		case 8:
		case 9:
			return SoundType.GROUND;
		default:
			return this.getSoundType();
		}
	}

	public enum Variant implements IStringSerializable{

		BLACK_SMOOTH,
		WHITE_SMOOTH,
		BLACK_COBBLE,
		WHITE_COBBLE,
		BLACK_BRICK_LARGE,
		WHITE_BRICK_LARGE,
		BLACK_BRICK_SMALL,
		WHITE_BRICK_SMALL,
		BLACK_GRAVEL,
		WHITE_GRAVEL,
		BLACK_PAVED,
		WHITE_PAVED;

		private final int meta;
		private final String name;

		public static final String[] NAMES;
		public static final String[] UNLOC_NAMES;

		Variant() {

			this.meta = ordinal();
			this.name = name().toLowerCase(Locale.ROOT);
		}

		@Override
		public String getName() {

			return name;
		}

		public static Variant byMetadata(int meta) {

			return values()[meta];
		}

		static {
			NAMES = new String[values().length];
			UNLOC_NAMES = new String[values().length];
			for (Variant variant : values()) {
				NAMES[variant.meta] = variant.name;
				UNLOC_NAMES[variant.meta] = variant.name.replace("_", ".");
			}
		}
	}

}
