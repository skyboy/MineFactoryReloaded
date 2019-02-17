package powercrystals.minefactoryreloaded.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import powercrystals.minefactoryreloaded.item.ItemSafariNet;

import javax.annotation.Nonnull;

public class EntitySafariNet extends EntityThrowable {

	protected static final DataParameter<ItemStack> STORED_ENTITY = EntityDataManager.createKey(EntitySafariNet.class, DataSerializers.ITEM_STACK);
	public EntitySafariNet(World world) {

		super(world);
		dataManager.register(STORED_ENTITY, ItemStack.EMPTY);
	}

	public EntitySafariNet(World world, double x, double y, double z, @Nonnull ItemStack netStack) {

		super(world, x, y, z);
		dataManager.register(STORED_ENTITY, netStack);
	}

	public EntitySafariNet(World world, EntityLivingBase owner, @Nonnull ItemStack netStack) {

		super(world, owner);
		dataManager.register(STORED_ENTITY, netStack);
	}

	@SideOnly(Side.CLIENT)
	public boolean isInRangeToRenderDist(double distance) {
		double d0 = this.getEntityBoundingBox().getAverageEdgeLength() * 10.0D;
		if(Double.isNaN(d0)) {
			d0 = 1.0D;
		}

		d0 = d0 * 64.0D * getRenderDistanceWeight();
		return distance < d0 * d0;
	}
	
	@Nonnull
	public ItemStack getStoredEntity() {

		return dataManager.get(STORED_ENTITY);
	}

	public void setStoredEntity(@Nonnull ItemStack s) {

		dataManager.set(STORED_ENTITY, s);
	}

	protected boolean onHitBlock(@Nonnull ItemStack storedEntity, RayTraceResult result) {

		if (ItemSafariNet.isEmpty(storedEntity)) {
			dropAsStack(storedEntity);
		} else {
			ItemSafariNet.releaseEntity(storedEntity, world, result.getBlockPos(), result.sideHit);
			if (ItemSafariNet.isSingleUse(storedEntity)) {
				dropAsStack(ItemStack.EMPTY);
			} else {
				dropAsStack(storedEntity);
			}
		}
		return true;
	}

	protected boolean onHitEntity(@Nonnull ItemStack storedEntity, RayTraceResult result) {

		if (ItemSafariNet.isEmpty(storedEntity) && result.entityHit instanceof EntityLivingBase) {
			ItemSafariNet.captureEntity(storedEntity, (EntityLivingBase) result.entityHit);
			dropAsStack(storedEntity);
		} else {
			if (!ItemSafariNet.isEmpty(storedEntity)) {
				Entity releasedEntity = ItemSafariNet.releaseEntity(storedEntity, world, result.entityHit.getPosition(), EnumFacing.UP);

				if (result.entityHit instanceof EntityLivingBase) {
					if (releasedEntity instanceof EntityLiving) {
						//Functional for skeletons.
						((EntityLiving) releasedEntity).setAttackTarget((EntityLivingBase) result.entityHit);
					}

					if (releasedEntity instanceof EntityCreature && result.entityHit instanceof EntityLivingBase) {
						//functional for mobs that extend EntityCreature (everything but Ghasts) and not Skeletons.
						((EntityCreature) releasedEntity).setAttackTarget((EntityLivingBase) result.entityHit);
					}
				}

				if (ItemSafariNet.isSingleUse(storedEntity)) {
					setDead();
					return true;
				}
			}
			dropAsStack(storedEntity);
		}
		return true;
	}

	protected void impact(double x, double y, double z, EnumFacing side) {

	}

	@Override
	protected void onImpact(RayTraceResult result) {

		@Nonnull ItemStack storedEntity = dataManager.get(STORED_ENTITY);

		boolean r = false;
		double x, y, z;
		EnumFacing side;
		if (result.typeOfHit == Type.ENTITY) {
			r = onHitEntity(storedEntity, result);
			x = result.entityHit.posX;
			y = result.entityHit.posY;
			z = result.entityHit.posZ;
			side = null;
		} else {
			r = onHitBlock(storedEntity, result);
			x = result.getBlockPos().getX();
			y = result.getBlockPos().getY();
			z = result.getBlockPos().getZ();
			side = result.sideHit;
		}
		if (r)
			impact(x, y, z, side);
	}

	protected void dropAsStack(@Nonnull ItemStack stack) {

		if (!world.isRemote && !stack.isEmpty()) {
			EntityItem ei = new EntityItem(world, posX, posY, posZ, stack.copy());
			ei.setPickupDelay(40);
			world.spawnEntity(ei);
		}
		setDead();
	}

/*
	public IIcon getIcon() {

		return dataWatcher.getWatchableObjectItemStack(13).getIconIndex();
	}
*/

	@Override
	public void writeEntityToNBT(NBTTagCompound nbttagcompound) {

		super.writeEntityToNBT(nbttagcompound);
		NBTTagCompound stackTag = new NBTTagCompound();
		@Nonnull ItemStack entity = dataManager.get(STORED_ENTITY);
		if (!entity.isEmpty()) {
			entity.writeToNBT(stackTag);
		}
		nbttagcompound.setTag("safariNetStack", stackTag);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbttagcompound) {

		super.readEntityFromNBT(nbttagcompound);
		NBTTagCompound stackTag = nbttagcompound.getCompoundTag("safariNetStack");
		setStoredEntity(new ItemStack(stackTag));
	}
}
