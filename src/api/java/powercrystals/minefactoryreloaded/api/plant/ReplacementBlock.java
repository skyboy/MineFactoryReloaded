package powercrystals.minefactoryreloaded.api.plant;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class ReplacementBlock implements IReplacementBlock {

	protected boolean _hasMeta;
	protected IBlockState _block;
	protected final NBTTagCompound _tileTag;

	public ReplacementBlock(ItemStack block) {

		this(Block.getBlockFromItem(block.getItem()), block.getSubCompound("BlockEntityTag"));
	}

	public ReplacementBlock(Item block) {

		this(Block.getBlockFromItem(block));
	}

	public ReplacementBlock(Item block, NBTTagCompound tag) {

		this(Block.getBlockFromItem(block), tag);
	}

	public ReplacementBlock(Block block) {

		this(block, null);
	}

	public ReplacementBlock(Block block, NBTTagCompound tag) {

		this(block.getDefaultState(), tag);
	}

	public ReplacementBlock(IBlockState block) {

		this(block, null);
	}

	public ReplacementBlock(IBlockState block, NBTTagCompound tag) {

		_block = block;
		_tileTag = tag;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean replaceBlock(World world, BlockPos pos, @Nonnull ItemStack stack) {

		IBlockState state = _block;
		if (_hasMeta)
			state = state.getBlock().getStateFromMeta(getMeta(world, pos, stack));
		if (world.setBlockState(pos, state, 3)) {
			if (hasTag(stack) && _block.getBlock().hasTileEntity(state)) {
				TileEntity tile = world.getTileEntity(pos);
				if (tile != null) {
					NBTTagCompound source = tile.writeToNBT(new NBTTagCompound());
					NBTTagCompound comparison = source.copy();
					source.merge(getTag(world, pos, stack));
					source.setInteger("x", pos.getX());
					source.setInteger("y", pos.getY());
					source.setInteger("z", pos.getZ());

					if (!source.equals(comparison)) {
						tile.readFromNBT(source);
						tile.markDirty();
					}
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * Called to get the metadata of the replacement block in the world.
	 *
	 * @param world
	 * 		The world object
	 * @param pos
	 * 		Block position
	 * @param stack
	 * 		The @Nonnull ItemStack being used to replace the block
	 *
	 * @return The metadata of the block
	 */
	protected int getMeta(World world, BlockPos pos, @Nonnull ItemStack stack) {

		return stack.getItem().getMetadata(stack.getMetadata()); // wee?
	}

	/**
	 * Called to set the metadata of this ReplacementBlock to a fixed value
	 *
	 * @param meta
	 * 		The metadata of the block
	 *
	 * @return This instance
	 */
	public ReplacementBlock setMeta(int meta) {

		_block = _block.getBlock().getStateFromMeta(meta);
		return this;
	}

	/**
	 * Called to set the metadata of this ReplacementBlock to a value read from an @Nonnull ItemStack
	 *
	 * @param hasMeta
	 * 		The metadata of the block
	 *
	 * @return This instance
	 */
	public ReplacementBlock setMeta(boolean hasMeta) {

		_hasMeta = hasMeta;
		return this;
	}

	/**
	 * Called to get the NBTTagCompound a TileEntity will read its state from
	 *
	 * @param world
	 * 		The world object
	 * @param pos
	 * 		Block position
	 * @param stack
	 * 		The @Nonnull ItemStack being used to replace the block
	 *
	 * @return The NBTTagCompound a TileEntity will read its state from
	 */
	protected NBTTagCompound getTag(World world, BlockPos pos, @Nonnull ItemStack stack) {

		return _tileTag != null ? _tileTag : stack.getSubCompound("BlockEntityTag");
	}

	/**
	 * Called to see if a TileEntity should have its state set
	 *
	 * @param stack
	 * 		The @Nonnull ItemStack being used to replace the block
	 *
	 * @return True if the TileEntity should have its state set
	 */
	protected boolean hasTag(@Nonnull ItemStack stack) {

		return _tileTag != null || stack.getSubCompound("BlockEntityTag") != null;
	}

}
