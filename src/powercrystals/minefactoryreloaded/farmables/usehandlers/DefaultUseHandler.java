package powercrystals.minefactoryreloaded.farmables.usehandlers;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidBlock;

import powercrystals.minefactoryreloaded.core.IAdvFluidContainerItem;
import powercrystals.minefactoryreloaded.core.IUseHandler;
import powercrystals.minefactoryreloaded.core.IUseable;
import powercrystals.minefactoryreloaded.core.MFRLiquidMover;

public class DefaultUseHandler implements IUseHandler {
	@Override
	public boolean canUse(ItemStack bucket, EntityLivingBase entity) {
		IAdvFluidContainerItem item = (IAdvFluidContainerItem)bucket.getItem();
		FluidStack liquid = item.getFluid(bucket);
		if (liquid == null || liquid.amount <= 0)
			return item.canBeFilledFromWorld();
		return item.canPlaceInWorld();
	}

	@Override
	public ItemStack onTryUse(ItemStack bucket, World world, EntityLivingBase entity) {
		EntityPlayer player = entity instanceof EntityPlayer ? (EntityPlayer)entity : null;
		if (world.isRemote) return bucket;
		Item item = bucket.getItem();
		IAdvFluidContainerItem container = (IAdvFluidContainerItem)item;
		ItemStack q = new ItemStack(Item.bucketEmpty, 1, 0);
		FluidStack liquid = container.getFluid(bucket);
		if (liquid == null || liquid.amount <= 0) {
			if (!container.canBeFilledFromWorld()) return bucket;
			ItemStack bucket2 = bucket.stackSize > 1 ? bucket.copy() : bucket;
			bucket2.stackSize = 1;
			MovingObjectPosition objectPosition = ((IUseable)container).rayTrace(world, entity, false);
			if (objectPosition != null && objectPosition.typeOfHit == EnumMovingObjectType.TILE) {
				int x = objectPosition.blockX;
				int y = objectPosition.blockY;
				int z = objectPosition.blockZ;
				if (canEntityAct(world, entity, x, y, z, objectPosition.sideHit, bucket, false))
				{
					int blockID = world.getBlockId(x, y, z);
					Block block = Block.blocksList[blockID];
					if (block instanceof IFluidBlock) {
						liquid = ((IFluidBlock)block).drain(world, x, y, z, false);
						if (liquid != null) {
							if (container.fill(bucket2, liquid, false) == liquid.amount) {
								container.fill(bucket2, ((IFluidBlock)block).drain(world, x, y, z, true), true);
								if (!container.shouldReplaceWhenFilled() || bucket2 != bucket)
									MFRLiquidMover.disposePlayerItem(bucket, bucket2, player,
											true, container.shouldReplaceWhenFilled());
								return bucket;
							}
						}
					}
				}
			}
			if (player == null) return bucket;
			q = q.getItem().onItemRightClick(q, world, player);
			if (FluidContainerRegistry.isEmptyContainer(q)) return bucket;
			container.fill(bucket2, FluidContainerRegistry.getFluidForFilledItem(q), true);
			if (!container.shouldReplaceWhenFilled() || bucket2 != bucket)
				MFRLiquidMover.disposePlayerItem(bucket, bucket2, player, true, container.shouldReplaceWhenFilled());
			return bucket;
		}
		if (container.canPlaceInWorld()) {
			if (!liquid.getFluid().canBePlacedInWorld()) return bucket;
			Block block = Block.blocksList[liquid.getFluid().getBlockID()];
			if (!(block instanceof IFluidBlock)) return bucket;
			MovingObjectPosition objectPosition = ((IUseable)container).rayTrace(world, entity, false);
			if (objectPosition != null && objectPosition.typeOfHit == EnumMovingObjectType.TILE) {
				int x = objectPosition.blockX;
				int y = objectPosition.blockY;
				int z = objectPosition.blockZ;
				if (canEntityAct(world, entity, x, y, z, objectPosition.sideHit, bucket, true))
				{
					if (world.setBlock(x, y, z, block.blockID, 0, 3))
					{
						liquid = ((IFluidBlock)block).drain(world, x, y, z, false);
						ItemStack drop = bucket.splitStack(1);
						container.drain(drop, liquid.amount, true);
						if (item.hasContainerItem()) {
							drop = item.getContainerItemStack(drop);
							if (drop.isItemStackDamageable() && drop.getItemDamage() > drop.getMaxDamage())
								drop = null;
						}
						return drop;
					}
				}
			}
		}
		return bucket;
	}

	private boolean canEntityAct(World world, EntityLivingBase entity, int x, int y, int z, int side,
			ItemStack item, boolean isPlace) {
		EntityPlayer player = (entity instanceof EntityPlayer) ? (EntityPlayer)entity : null;
		return (player == null || (world.canMineBlock(player, x, y, z) &&
						player.canPlayerEdit(x, y, z, side, item))) &&
						(!isPlace || world.isAirBlock(x, y, z) ||
								!world.getBlockMaterial(x, y, z).isSolid());
	}

	@Override
	public int getMaxUseDuration(ItemStack item) {
		return 0;
	}
	@Override
	public boolean isUsable(ItemStack item) {
		return false;
	}
	@Override
	public EnumAction useAction(ItemStack item) {
		return EnumAction.none;
	}
	@Override
	public ItemStack onUse(ItemStack item, EntityLivingBase entity) {
		return item;
	}
}
