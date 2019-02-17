package powercrystals.minefactoryreloaded.block.transport;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import powercrystals.minefactoryreloaded.setup.MFRConfig;

import java.util.ArrayList;
import java.util.List;

public class BlockRailPassengerDropoff extends BlockFactoryRail {

	public BlockRailPassengerDropoff() {

		super(true, false);
		setUnlocalizedName("mfr.rail.passenger.dropoff");
	}

	@Override
	public void onMinecartPass(World world, EntityMinecart minecart, BlockPos pos) {

		if (world.isRemote || minecart.getPassengers().size() < 1)
			return;

		Class<? extends EntityLivingBase> target = isPowered(world, pos) ? EntityLiving.class : EntityPlayer.class;
		for (int i = minecart.getPassengers().size(); i-- > 0; ) {
			Entity entity = minecart.getPassengers().get(i);
			if (!target.isInstance(entity))
				continue;

			AxisAlignedBB dropCoords = findSpaceForEntity(entity, pos, world);
			if (dropCoords == null)
				continue;

			entity.dismountRidingEntity();
			entity.setPositionAndUpdate(dropCoords.minX, dropCoords.minY, dropCoords.minZ);
		}
	}

	private AxisAlignedBB findSpaceForEntity(Entity entity, BlockPos pos, World world) {

		AxisAlignedBB bb = entity.getEntityBoundingBox();

		final double halfX = (bb.maxX - bb.minX) / 2;
		final double halfZ = (bb.maxZ - bb.minZ) / 2;

		bb = bb.offset(
				pos.getX() - bb.minX + .5 - halfX,
				pos.getY() - bb.minY + 0.09375,
				pos.getZ() - bb.minZ + .5 - halfZ
		); // center on a block, raise by 3/32

		for (double yLevel = 0; yLevel <= MFRConfig.passengerRailSearchMaxVertical.getInt(); yLevel += 0.5) {

			for (double horizontalRadius = 1;
				 horizontalRadius <= MFRConfig.passengerRailSearchMaxHorizontal.getInt(); horizontalRadius += 0.5) {

				AxisAlignedBB result = searchHorizontalRectangle(world, horizontalRadius, entity, bb.offset(0, yLevel, 0), halfX,
						halfZ);
				if (result != null)
					return result;

				if (yLevel > 0) {
					result = searchHorizontalRectangle(world, horizontalRadius, entity, bb.offset(0, -yLevel, 0), halfX, halfZ);
					if (result != null)
						return result;
				}
			}
		}

		return null;
	}

	private AxisAlignedBB searchHorizontalRectangle(World world, double radius, Entity entity, AxisAlignedBB bb, double halfX,
			double halfZ) {

		double xOffset = -radius;
		double zOffset = -radius;

		for (; xOffset < radius; xOffset += 0.5) {
			if (isGoodBlockToStand(world, bb.offset(xOffset, 0, zOffset), entity, halfX, halfZ)) {
				return bb.offset(xOffset + halfX, 0, zOffset + halfZ);
			}
		}

		for (; zOffset < radius; zOffset += 0.5) {
			if (isGoodBlockToStand(world, bb.offset(xOffset, 0, zOffset), entity, halfX, halfZ)) {
				return bb.offset(xOffset + halfX, 0, zOffset + halfZ);
			}
		}

		for (; xOffset > -radius; xOffset -= 0.5) {
			if (isGoodBlockToStand(world, bb.offset(xOffset, 0, zOffset), entity, halfX, halfZ)) {
				return bb.offset(xOffset + halfX, 0, zOffset + halfZ);
			}
		}

		for (; zOffset > -radius; zOffset -= 0.5) {
			if (isGoodBlockToStand(world, bb.offset(xOffset, 0, zOffset), entity, halfX, halfZ)) {
				return bb.offset(xOffset + halfX, 0, zOffset + halfZ);
			}
		}

		return null;
	}

	private boolean isGoodBlockToStand(World world, AxisAlignedBB bb, Entity entity, double halfX, double halfZ) {

		if (!isBadBlockToStandIn(world, bb, entity) &&
				world.getEntitiesWithinAABBExcludingEntity(entity, bb).isEmpty()) {
			int targetX = MathHelper.floor(bb.minX + halfX);
			int targetY = MathHelper.floor(bb.minY);
			int targetZ = MathHelper.floor(bb.minZ + halfZ);

			if (!isBadBlockToStandOn(world,
					new BlockPos(targetX, targetY, targetZ))) // may be on top of a slab or other thin block
				return true;

			targetY = MathHelper.floor(bb.minY - 0.15625);
			if (!isBadBlockToStandOn(world, new BlockPos(targetX, targetY, targetZ)))
				return true;
		}

		return false;
	}

	private boolean isBadBlockToStandOn(World world, BlockPos pos) {

		IBlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		if (block.isAir(state, world, pos) ||
				block.isBurning(world, pos) ||
				BlockRailBase.isRailBlock(state) ||
				NULL_AABB == state.getCollisionBoundingBox(world, pos)) {
			return true;
		}
		return false;
	}

	private List<AxisAlignedBB> collisionList = new ArrayList<>();

	private boolean isBadBlockToStandIn(World world, AxisAlignedBB bb, Entity entity) {

		int i = MathHelper.floor(bb.minX);
		int j = MathHelper.floor(bb.maxX) + 1;
		int k = MathHelper.floor(bb.minY) - 1; // fences.
		int l = MathHelper.floor(bb.maxY) + 1;
		int i1 = MathHelper.floor(bb.minZ);
		int j1 = MathHelper.floor(bb.maxZ) + 1;

		BlockPos.PooledMutableBlockPos pos = BlockPos.PooledMutableBlockPos.retain();

		for (int k1 = i; k1 < j; ++k1) {
			for (int l1 = k; l1 < l; ++l1) {
				for (int i2 = i1; i2 < j1; ++i2) {
					IBlockState state = world.getBlockState(pos.setPos(k1, l1, i2));
					Block block = state.getBlock();

					if (block == Blocks.FIRE ||
							state.getMaterial().isLiquid() ||
							block.isBurning(world, pos) ||
							BlockRailBase.isRailBlock(state)) {
						pos.release();
						return true;
					}

					state.addCollisionBoxToList(world, pos, bb, collisionList, entity, false);
					if (!collisionList.isEmpty()) {
						collisionList.clear();
						pos.release();
						return true;
					}
				}
			}
		}

		pos.release();
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels() {

		registerRailModel(this, "passenger_dropoff");
	}
}
