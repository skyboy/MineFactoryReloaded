package powercrystals.minefactoryreloaded.block;

import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.gui.MFRCreativeTab;
import powercrystals.minefactoryreloaded.render.ModelHelper;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.transport.TileEntityDetCord;

import java.util.List;

public class BlockDetCord extends BlockFactory {

	public BlockDetCord() {

		super(Machine.MATERIAL);
		setHardness(2.0F);
		setResistance(10.0F);
		setSoundType(SoundType.SNOW);
		setUnlocalizedName("mfr.detcord");
		setCreativeTab(MFRCreativeTab.tab);
	}

	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		//TODO does this really need a tile?
		return new TileEntityDetCord();
	}

	@Override
	public boolean canPlaceBlockAt(World world, BlockPos pos) {

		return false; // temporary
	}

	@Override
	public boolean isSideSolid(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {

		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {

		return false;
	}

	@Override
	public boolean isNormalCube(IBlockState state) {

		return false;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void addCollisionBoxToList(IBlockState state, World w, BlockPos pos, AxisAlignedBB t, List l, Entity e, boolean p_185477_7_) {

	}

	@Override
	public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side) {

		if (!canPlaceBlockAt(world, pos))
			return false;

		BlockPos neighborPos = pos.offset(side.getOpposite());
		IBlockState neighborState = world.getBlockState(neighborPos);
		return neighborState.isSideSolid(world, neighborPos, side);
	}

	@Override
	public boolean initialize() {

		MFRRegistry.registerBlock(this, new ItemBlockDetCord(this));
		Blocks.FIRE.setFireInfo(this, 100, 20);
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels() {

		ModelHelper.registerModel(this);
	}
}
