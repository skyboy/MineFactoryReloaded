package powercrystals.minefactoryreloaded.world;

import cofh.core.util.helpers.BlockHelper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import powercrystals.minefactoryreloaded.setup.MFRThings;

import java.util.Random;

public class WorldGenRubberTree extends WorldGenerator {

	// of course, in super this identical flag is private
	protected boolean doBlockNotify;

	public WorldGenRubberTree() {

		this(false);
	}

	public WorldGenRubberTree(boolean doNotify) {

		super(doNotify);
		doBlockNotify = doNotify;
	}

	@Override
	public boolean generate(World world, Random rand, BlockPos pos) {

		int x = pos.getX();
		int retries = pos.getY();
		int z = pos.getZ();

		for (int c = 0; c < retries; c++) {
			int y = BlockHelper.getSurfaceBlockY(world, x, z);

			if (y > 0 && !growTree(world, rand, x, y + 1, z))
				retries--;

			x += rand.nextInt(16) - 8;
			z += rand.nextInt(16) - 8;
		}

		return true;
	}

	public boolean growTree(World world, Random rand, int x, int y, int z) {

		int treeHeight = rand.nextInt(3) + 5, worldHeight = world.getHeight();

		if (y >= 1 && y + treeHeight + 1 <= worldHeight) {
			int xOffset;
			int yOffset;
			int zOffset;

			BlockPos pos = new BlockPos(x, y - 1, z);
			IBlockState state = world.getBlockState(pos);
			Block block = state.getBlock();

			if ((block.canSustainPlant(state, world, pos, EnumFacing.UP, MFRThings.rubberSaplingBlock)) &&
					y < worldHeight - treeHeight - 1) {
				for (yOffset = y; yOffset <= y + 1 + treeHeight; ++yOffset) {
					byte radius;

					if (yOffset >= y + 1 + treeHeight - 2) {
						radius = 2;
					} else {
						radius = 0;
					}

					if (yOffset >= 0 & yOffset < worldHeight) {
						if (radius == 0) {
							pos = new BlockPos(x, yOffset, z);
							state = world.getBlockState(pos);
							block = state.getBlock();
							if (!(block.isLeaves(state, world, pos) ||
									block.isAir(state, world, pos) ||
									block.isReplaceable(world, pos) || block.canBeReplacedByLeaves(state, world, pos))) {
								return false;
							}

							if (yOffset >= y + 1) {
								radius = 1;
								for (xOffset = x - radius; xOffset <= x + radius; ++xOffset) {
									for (zOffset = z - radius; zOffset <= z + radius; ++zOffset) {
										state = world.getBlockState(new BlockPos(xOffset, yOffset, zOffset));

										if (state.getMaterial().isLiquid()) {
											return false;
										}
									}
								}
							}
						} else {
							for (xOffset = x - radius; xOffset <= x + radius; ++xOffset) {
								for (zOffset = z - radius; zOffset <= z + radius; ++zOffset) {
									pos = new BlockPos(xOffset, yOffset, zOffset);
									state = world.getBlockState(pos);
									block = state.getBlock();

									if (!(block.isLeaves(state, world, pos) ||
											block.isAir(state, world, pos) || block.canBeReplacedByLeaves(state, world, pos))) {
										return false;
									}
								}
							}
						}
					} else {
						return false;
					}
				}

				pos = new BlockPos(x, y - 1, z);
				state = world.getBlockState(pos);
				block = state.getBlock();
				if (!block.canSustainPlant(state, world, pos, EnumFacing.UP, MFRThings.rubberSaplingBlock)) {
					return false; // another chunk generated and invalidated our location
				}
				block.onPlantGrow(state, world, pos, new BlockPos(x, y, z));

				for (yOffset = y - 3 + treeHeight; yOffset <= y + treeHeight; ++yOffset) {
					int var12 = yOffset - (y + treeHeight), center = 1 - var12 / 2;

					for (xOffset = x - center; xOffset <= x + center; ++xOffset) {
						int xPos = xOffset - x, t = xPos >> 31;
						xPos = (xPos + t) ^ t;

						for (zOffset = z - center; zOffset <= z + center; ++zOffset) {
							int zPos = zOffset - z;
							zPos = (zPos + (t = zPos >> 31)) ^ t;

							pos = new BlockPos(xOffset, yOffset, zOffset);
							state = world.getBlockState(pos);
							block = state.getBlock();

							if (((xPos != center | zPos != center) ||
									rand.nextInt(2) != 0 && var12 != 0) &&
									(block.isLeaves(state, world, pos) ||
											block.isAir(state, world, pos) ||
									block.canBeReplacedByLeaves(state, world, pos))) {

								this.setBlockAndNotifyAdequately(world, pos, MFRThings.rubberLeavesBlock.getDefaultState());
							}
						}
					}
				}

				for (yOffset = 0; yOffset < treeHeight; ++yOffset) {
					pos = new BlockPos(x, y + yOffset, z);
					state = world.getBlockState(pos);
					block = state.getBlock();

					if (block.isAir(state, world, pos) ||
							block.isLeaves(state, world, pos) ||
							block.isReplaceable(world, pos)) { // replace snow

						this.setBlockAndNotifyAdequately(world, pos, MFRThings.rubberWoodBlock.getDefaultState());
					}
				}

				return true;
			}
		}
		return false;
	}

	/*
	 * Local override provided to ensure immunity to ASM that may destructively interfere with our data
	 */
	@Override
	protected void setBlockAndNotifyAdequately(World world, BlockPos pos, IBlockState state) {

		final int flag;
		if (this.doBlockNotify) {
			flag = 3;
		} else {
			flag = 2;
		}
		world.setBlockState(pos, state, flag);
	}
}
