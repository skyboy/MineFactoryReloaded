package powercrystals.minefactoryreloaded.world;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import powercrystals.minefactoryreloaded.core.UtilInventory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

// Nigel says: This is a
public class SmashingWorld implements IBlockAccess {

	protected Block block;
	protected int meta;
	protected BlockPos pos = new BlockPos(0, 1, 0);

	@Override
	public IBlockState getBlockState(BlockPos pos) {

		return this.pos.equals(pos) ? block.getStateFromMeta(meta) : Blocks.AIR.getDefaultState();
	}

	@Override
	public boolean isAirBlock(BlockPos pos) {

		return false;
	}

	@Override
	public Biome getBiome(BlockPos pos) {

		return Biomes.PLAINS;
	}

	@Override
	public int getStrongPower(BlockPos pos, EnumFacing direction) {

		return 0;
	}

	@Override
	public WorldType getWorldType() {

		return WorldType.DEFAULT;
	}

	@Override
	public boolean isSideSolid(BlockPos pos, EnumFacing side, boolean _default) {

		return true;
	}

	@Nullable
	@Override
	public TileEntity getTileEntity(BlockPos pos) {

		return null;
	}

	@Override
	public int getCombinedLight(BlockPos pos, int lightValue) {

		return 0;
	}

	public NonNullList<ItemStack> smashBlock(@Nonnull ItemStack input, Block block, int meta, int fortune) {

		NonNullList<ItemStack> drops = NonNullList.create();
		if (block != null) {
			this.block = block;
			this.meta = meta;

			drops.addAll(block.getDrops(this, pos, block.getStateFromMeta(meta), fortune));
			if (drops.size() == 1)
				if (UtilInventory.stacksEqual(drops.get(0), input, false))
					return null;
		}
		return drops;
	}

}
