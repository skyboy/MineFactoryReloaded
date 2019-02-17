package powercrystals.minefactoryreloaded.block;

import cofh.core.render.IModelRegister;
import cofh.core.util.core.IInitializer;
import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.core.UtilInventory;
import powercrystals.minefactoryreloaded.gui.MFRCreativeTab;
import powercrystals.minefactoryreloaded.render.ModelHelper;
import powercrystals.minefactoryreloaded.setup.MFRThings;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Random;

public class BlockFertileSoil extends Block implements IGrowable, IInitializer, IModelRegister {

	public static final PropertyInteger MOISTURE = PropertyInteger.create("moisture", 0, 15);
	public static final AxisAlignedBB SOIL_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.9375D, 1.0D);

	public BlockFertileSoil() {

		super(Material.GROUND);
		setHardness(0.6F);
		setLightOpacity(255);
		setTickRandomly(false);
		setHarvestLevel("hoe", 0);
		setHarvestLevel("shovel", 0);
		setUnlocalizedName("mfr.farmland");
		setSoundType(SoundType.GROUND);
		setCreativeTab(MFRCreativeTab.tab);
		useNeighborBrightness = true; // THIS IS SET IN THE DUMBEST DAMN WAY ON FARMLAND
		setDefaultState(blockState.getBaseState().withProperty(MOISTURE, 0));
		MFRThings.registerInitializer(this);
		MineFactoryReloadedCore.proxy.addModelRegister(this);
	}

	@Override
	protected BlockStateContainer createBlockState() {

		return new BlockStateContainer(this, MOISTURE);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {

		return getDefaultState().withProperty(MOISTURE, meta);
	}

	@Override
	public int getMetaFromState(IBlockState state) {

		return state.getValue(MOISTURE);
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {

		if (source.getBlockState(pos.up()).getMaterial().isSolid()) {
			return FULL_BLOCK_AABB;
		} else {
			return SOIL_AABB;
		}
	}

	@Override
	public boolean canSustainPlant(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing direction,
			IPlantable plantable) {

		if (direction != EnumFacing.UP)
			return false;
		EnumPlantType plantType = plantable.getPlantType(world, pos.up());

		switch (plantType) {
		case Cave:
		case Crop:
		case Beach:
		case Desert:
		case Nether:
		case Plains:
			return true;
		default:
			return false;
		}
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {

		return FULL_BLOCK_AABB;
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
			EnumFacing side, float hitX, float hitY, float hitZ) {

		@Nonnull ItemStack heldItem = player.getHeldItem(hand);
		if (!canGrow(world, pos, state, world.isRemote))
			return false;

		if (!heldItem.isEmpty()) {
			if (MFRRegistry.getFertilizers().get(heldItem.getItem()) != null) {
				if (canUseBonemeal(world, world.rand, pos, state)) {
					grow(world, world.rand, pos, state);
					world.playEvent(null, 2005, pos, 0);
				}
				if (!player.capabilities.isCreativeMode) {
					EntityEquipmentSlot slot =
							hand == EnumHand.OFF_HAND ? EntityEquipmentSlot.OFFHAND : EntityEquipmentSlot.MAINHAND;
					player.setItemStackToSlot(slot, UtilInventory.consumeItem(heldItem, player));
				}
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isFertile(World world, BlockPos pos) {

		return world.getBlockState(pos).getValue(MOISTURE) > 0;
	}

	protected void changeMoisture(World world, BlockPos pos, IBlockState state, int d) {

		world.setBlockState(pos, state.withProperty(MOISTURE, Math.min(15, Math.max(0, state.getValue(MOISTURE) + d))));
	}

	@Override
	public void onPlantGrow(IBlockState state, World world, BlockPos pos, BlockPos source) {

		if (!isFertile(world, pos))
			world.setBlockState(pos, Blocks.SAND.getDefaultState());
		else
			changeMoisture(world, pos, state, -1);
	}

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {

		super.neighborChanged(state, world, pos, block, fromPos);
		if (true)
			return; // TODO: if neighborChange becomes sided, remove
		IBlockState stateAbove = world.getBlockState(pos.up());
		Block above = stateAbove.getBlock();
		if (block != above)
			return;
		Material material = above.getMaterial(stateAbove);
		boolean flag = material == Material.GOURD || isFertile(world, pos);

		if (!flag && material.isSolid()) {
			world.setBlockState(pos, Blocks.DIRT.getDefaultState());
		} else {
			changeMoisture(world, pos, state, -1);
		}
	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {

	}

	@Override
	public void onFallenUpon(World world, BlockPos pos, Entity ent, float distance) {

		if (distance > 10) {
			if (!world.isRemote && world.rand.nextFloat() * 20 < distance) {
				if (!(ent instanceof EntityPlayer) && !world.getGameRules().getBoolean("mobGriefing")) {
					return;
				}

				world.setBlockState(pos, Blocks.FARMLAND.getDefaultState());
				if (world.rand.nextFloat() >= 0.25)
					UtilInventory.dropStackInAir(world, pos, new ItemStack(MFRThings.fertilizerItem));
			}
		}
	}

	@Override
	public boolean isSideSolid(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {

		return side != EnumFacing.UP;
	}

	@Override
	public ArrayList<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {

		ArrayList<ItemStack> ret = new ArrayList<>();

		Random rand = world instanceof World ? ((World) world).rand : RANDOM;

		ret.add(new ItemStack(Blocks.DIRT));
		if (rand.nextFloat() >= 0.5)
			ret.add(new ItemStack(MFRThings.fertilizerItem));

		return ret;
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
	public boolean canGrow(World world, BlockPos pos, IBlockState state, boolean isClient) { // canFertilize
		return state.getValue(MOISTURE) < 15;
	}

	@Override
	public boolean canUseBonemeal(World world, Random rand, BlockPos pos, IBlockState state) { // shouldFertilize
		return state.getValue(MOISTURE) < 15 && rand.nextInt(3) == 0;
	}

	@Override
	public void grow(World world, Random rand, BlockPos pos, IBlockState state) { // fertilize
		changeMoisture(world, pos, state, 1 + (int) (rand.nextFloat() * 1.5f));
	}

	@Override public boolean preInit() {

		return false;
	}

	@Override
	public boolean initialize() {

		MFRRegistry.registerBlock(this, new ItemBlockFactory(this, 3));
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels() {

		ModelHelper.registerModel(MFRThings.fertileSoil, BlockFertileSoil.MOISTURE);
	}
}
