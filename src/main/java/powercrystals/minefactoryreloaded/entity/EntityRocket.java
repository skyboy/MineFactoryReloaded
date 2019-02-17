package powercrystals.minefactoryreloaded.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import powercrystals.minefactoryreloaded.setup.MFRConfig;

import java.util.List;
import java.util.UUID;

public class EntityRocket extends Entity
{
	private int _ticksAlive = 0;
	private String _owner;
	private Entity _target;
	private NBTTagCompound _lostTarget;
	
	public EntityRocket(World world)
	{
		super(world);
		_lostTarget = null;
	}
	
	public EntityRocket(World world, EntityLivingBase owner)
	{
		this(world);
		setSize(1.0F, 1.0F);
		setLocationAndAngles(owner.posX, owner.posY + owner.getEyeHeight(), owner.posZ, owner.rotationYaw, owner.rotationPitch);
		setPosition(posX, posY, posZ);
		recalculateVelocity();
		if (owner instanceof EntityPlayer)
			_owner = owner.getName();
	}
	
	public EntityRocket(World world, EntityLivingBase owner, Entity target)
	{
		this(world, owner);
		_target = target;
	}

	public boolean isInRangeToRenderDist(double distance) {
		double d0 = this.getEntityBoundingBox().getAverageEdgeLength() * 10.0D;
		if(Double.isNaN(d0)) {
			d0 = 1.0D;
		}

		d0 = d0 * 64.0D * getRenderDistanceWeight();
		return distance < d0 * d0;
	}
	
	private void recalculateVelocity()
	{
		motionX = -MathHelper.sin(rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(rotationPitch / 180.0F * (float)Math.PI);
		motionZ = MathHelper.cos(rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(rotationPitch / 180.0F * (float)Math.PI);
		motionY = (-MathHelper.sin(rotationPitch / 180.0F * (float)Math.PI));
	}
	
	@Override
	protected void entityInit()
	{
	}
	
	@Override
	public void onUpdate()
	{
		super.onUpdate();
		
		_ticksAlive++;
		if(_ticksAlive > 600)
		{
			if (MFRConfig.enableSPAMRExploding.getBoolean(true))
			{
				world.newExplosion(this, posX, posY, posZ, 4.0F, true, true);
			}
			setDead();
		}
		
		if(world.isRemote)
		{
			for(int i = 0; i < 4; i++)
			{
				world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, posX + motionX * i / 4.0D, posY + motionY * i / 4.0D, posZ + motionZ * i / 4.0D,
						-motionX, -motionY + 0.2D, -motionZ);
			}
		}
		
		if(!world.isRemote)
		{
			Vec3d pos = new Vec3d(this.posX, this.posY, this.posZ);
			Vec3d nextPos = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
			RayTraceResult hit = this.world.rayTraceBlocks(pos, nextPos, false, true, false);
			pos = new Vec3d(this.posX, this.posY, this.posZ);
			nextPos = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
			
			if(hit != null)
			{
				nextPos = new Vec3d(hit.hitVec.x, hit.hitVec.y,	hit.hitVec.z);
			}
			
			Entity entityHit = null;
			List<Entity> list = this.world.getEntitiesWithinAABBExcludingEntity(this,
					this.getEntityBoundingBox().expand(this.motionX, this.motionY, this.motionZ).grow(1.0D, 1.0D, 1.0D));
			double closestRange = 0.0D;
			double collisionRange = 0.3D;
			EntityPlayer owner = _owner == null ? null : this.world.getPlayerEntityByName(_owner);

			for (Entity e : list) {
				if ((e != owner | _ticksAlive > 5) && e.canBeCollidedWith()) {
					AxisAlignedBB entitybb = e.getEntityBoundingBox().grow(collisionRange, collisionRange, collisionRange);
					RayTraceResult entityHitPos = entitybb.calculateIntercept(pos, nextPos);

					if (entityHitPos != null) {
						double range = pos.distanceTo(entityHitPos.hitVec);

						if ((range < closestRange) | closestRange == 0D) {
							entityHit = e;
							closestRange = range;
						}
					}
				}
			}
			
			if(entityHit != null)
			{
				hit = new RayTraceResult(entityHit);
			}
			
			if(hit != null && hit.entityHit != null && hit.entityHit instanceof EntityPlayer)
			{
				EntityPlayer entityplayer = (EntityPlayer)hit.entityHit;
				
				if(entityplayer.capabilities.disableDamage || (owner != null && !owner.canAttackPlayer(entityplayer)))
				{
					hit = null;
				}
			}
			
			if(hit != null && !world.isRemote)
			{
				if(hit.entityHit != null)
				{
					world.newExplosion(this, hit.entityHit.posX, hit.entityHit.posY,
							hit.entityHit.posZ, 4.0F, true, true);
				}
				else
				{ // spawn explosion at nextPos x/y/z?
					world.newExplosion(this, hit.getBlockPos().getX(), hit.getBlockPos().getY(), hit.getBlockPos().getZ(), 4.0F, true, true);
				}
				setDead();
			}
		}
		
		Vec3d targetVector = findTarget();
		if (targetVector != null)
		{
			// At this point, I suspect literally no one on this project actually understands what this does or how it works
			
			float targetYaw = clampAngle(360 - (float)(Math.atan2(targetVector.x, targetVector.z) * 180.0D / Math.PI), 360, false);
			float targetPitch = clampAngle(-(float)(Math.atan2(targetVector.y, Math.sqrt(targetVector.x * targetVector.x + targetVector.z * targetVector.z)) * 180.0D / Math.PI), 360, false);
			
			float yawDifference = clampAngle(targetYaw - rotationYaw, 3, true);
			float pitchDifference = clampAngle(targetPitch - rotationPitch, 3, true);

			float newYaw;
			float newPitch;
			
			if(Math.max(targetYaw, rotationYaw) - Math.min(targetYaw, rotationYaw) > 180)
			{
				newYaw = rotationYaw - yawDifference;
			}
			else
			{
				newYaw = rotationYaw + yawDifference;
			}
			
			if(Math.max(targetPitch, rotationPitch) - Math.min(targetPitch, rotationPitch) > 180)
			{
				newPitch = rotationPitch - pitchDifference;
			}
			else
			{
				newPitch = rotationPitch + pitchDifference;
			}
			
			rotationYaw = clampAngle(newYaw, 360F, false);
			rotationPitch = clampAngle(newPitch, 360F, false);
			recalculateVelocity();
		}
		
		setPosition(posX + motionX, posY + motionY, posZ + motionZ);
	}
	
	private float clampAngle(float angle, float maxValue, boolean allowNegative)
	{
		if(angle >= 0F)
		{
			angle %= 360F;
		}
		else
		{ // pretty sure that negativeValue % postiveValue has the same result
			angle = -(-angle % 360);
		}
		
		if(angle < 0 & !allowNegative)
		{
			angle += 360;
		}
		
		if(Math.abs(angle) > maxValue)
		{
			angle = Math.copySign(maxValue, angle);
		}
		
		return angle;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void setVelocity(double x, double y, double z)
	{
		motionX = x;
		motionY = y;
		motionZ = z;
		
		if(prevRotationPitch == 0.0F && prevRotationYaw == 0.0F)
		{
			double f = MathHelper.sqrt(x * x + z * z);
			prevRotationYaw = rotationYaw = (float)(Math.atan2(x, z) * 180.0D / Math.PI);
			prevRotationPitch = rotationPitch = (float)(Math.atan2(y, f) * 180.0D / Math.PI);
			setLocationAndAngles(posX, posY, posZ, rotationYaw, rotationPitch);
		}
	}
	
	@SuppressWarnings("rawtypes")
	private Vec3d findTarget()
	{
		findTarget: if (_lostTarget != null)
		{
            UUID uuid = new UUID(_lostTarget.getLong("UUIDMost"), _lostTarget.getLong("UUIDLeast"));
            double x = _lostTarget.getDouble("xTarget");
            double y = _lostTarget.getDouble("yTarget");
            double z = _lostTarget.getDouble("zTarget");
            List<Entity> list = this.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(x - 5, y - 5, z - 5, x + 5, y + 5, z + 5));

			for (Entity e : list) {
				if (e.getUniqueID().equals(uuid)) {
					_target = e;
					_lostTarget = null;
					break findTarget;
				}
			}
            return new Vec3d(x - posX, y - posY, z - posZ);
		}
		if (_target != null)
		{
			return new Vec3d(_target.posX - posX,
					_target.posY - posY + _target.getEyeHeight(), _target.posZ - posZ);
		}
		return null;
	}
	
	@Override
	protected void writeEntityToNBT(NBTTagCompound tag)
	{
		if (_target != null)
		{
			NBTTagCompound target = new NBTTagCompound();
			tag.setDouble("xTarget", _target.posX);
			tag.setDouble("yTarget", _target.posY);
			tag.setDouble("zTarget", _target.posZ);
			UUID uuid = _target.getUniqueID();
			tag.setLong("UUIDMost", uuid.getMostSignificantBits());
			tag.setLong("UUIDLeast", uuid.getLeastSignificantBits());
			tag.setTag("target", target);
		}
		if (_owner != null)
			tag.setString("owner", _owner);
	}
	
	@Override
	protected void readEntityFromNBT(NBTTagCompound tag)
	{
		if(tag.hasKey("target"))
		{
			_lostTarget = tag.getCompoundTag("target");
		}
		if (tag.hasKey("owner"))
			_owner = tag.getString("owner");
	}
}
