package powercrystals.minefactoryreloaded.entity;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;


public class DebugTracker extends Entity {

	public static final DataParameter<Float> ENTITY_BB_MIN_X;
	public static final DataParameter<Float> ENTITY_BB_MIN_Y;
	public static final DataParameter<Float> ENTITY_BB_MIN_Z;
	public static final DataParameter<Float> ENTITY_BB_MAX_X;
	public static final DataParameter<Float> ENTITY_BB_MAX_Y;
	public static final DataParameter<Float> ENTITY_BB_MAX_Z;
	public static final DataParameter<Float> ENTITY_EYE_HEIGHT;

	public static final DataParameter<Float> PROJECTILE_BB_MIN_X;
	public static final DataParameter<Float> PROJECTILE_BB_MIN_Y;
	public static final DataParameter<Float> PROJECTILE_BB_MIN_Z;
	public static final DataParameter<Float> PROJECTILE_BB_MAX_X;
	public static final DataParameter<Float> PROJECTILE_BB_MAX_Y;
	public static final DataParameter<Float> PROJECTILE_BB_MAX_Z;
	public static final DataParameter<Float> PROJECTILE_MOTION_X;
	public static final DataParameter<Float> PROJECTILE_MOTION_Y;
	public static final DataParameter<Float> PROJECTILE_MOTION_Z;

	public DebugTracker(World world) {

		super(world);
		ignoreFrustumCheck = true;
	}

	public DebugTracker(World world, Entity src, Entity proj) {

		this(world);

		setPosition(src.posX, src.posY, src.posZ);

		AxisAlignedBB bb = src.getEntityBoundingBox();
		dataManager.set(ENTITY_BB_MIN_X, (float)bb.minX);
		dataManager.set(ENTITY_BB_MIN_Y, (float)bb.minY);
		dataManager.set(ENTITY_BB_MIN_Z, (float)bb.minZ);
		dataManager.set(ENTITY_BB_MAX_X, (float)bb.maxX);
		dataManager.set(ENTITY_BB_MAX_Y, (float)bb.maxY);
		dataManager.set(ENTITY_BB_MAX_Z, (float)bb.maxZ);

		bb = proj.getEntityBoundingBox();
		dataManager.set(PROJECTILE_BB_MIN_X, (float)bb.minX);
		dataManager.set(PROJECTILE_BB_MIN_Y, (float)bb.minY);
		dataManager.set(PROJECTILE_BB_MIN_Z, (float)bb.minZ);
		dataManager.set(PROJECTILE_BB_MAX_X, (float)bb.maxX);
		dataManager.set(PROJECTILE_BB_MAX_Y, (float)bb.maxY);
		dataManager.set(PROJECTILE_BB_MAX_Z, (float)bb.maxZ);

		dataManager.set(PROJECTILE_MOTION_X, (float)proj.motionX);
		dataManager.set(PROJECTILE_MOTION_Y, (float)proj.motionY);
		dataManager.set(PROJECTILE_MOTION_Z, (float)proj.motionZ);

		dataManager.set(ENTITY_EYE_HEIGHT, src.getEyeHeight());
	}

	@Override
	protected void entityInit() {

		dataManager.register(ENTITY_BB_MIN_X, 0F);
		dataManager.register(ENTITY_BB_MIN_Y, 0F);
		dataManager.register(ENTITY_BB_MIN_Z, 0F);
		dataManager.register(ENTITY_BB_MAX_X, 0F);
		dataManager.register(ENTITY_BB_MAX_Y, 0F);
		dataManager.register(ENTITY_BB_MAX_Z, 0F);
		dataManager.register(ENTITY_EYE_HEIGHT, 0F);
		
		dataManager.register(PROJECTILE_BB_MIN_X, 0F);
		dataManager.register(PROJECTILE_BB_MIN_Y, 0F);
		dataManager.register(PROJECTILE_BB_MIN_Z, 0F);
		dataManager.register(PROJECTILE_BB_MAX_X, 0F);
		dataManager.register(PROJECTILE_BB_MAX_Y, 0F);
		dataManager.register(PROJECTILE_BB_MAX_Z, 0F);
		dataManager.register(PROJECTILE_MOTION_X, 0F);
		dataManager.register(PROJECTILE_MOTION_Y, 0F);
		dataManager.register(PROJECTILE_MOTION_Z, 0F);
	}
	public AxisAlignedBB getSrcBB() {

		return new AxisAlignedBB(
			dataManager.get(ENTITY_BB_MIN_X),
			dataManager.get(ENTITY_BB_MIN_Y),
			dataManager.get(ENTITY_BB_MIN_Z),
			dataManager.get(ENTITY_BB_MAX_X),
			dataManager.get(ENTITY_BB_MAX_Y),
			dataManager.get(ENTITY_BB_MAX_Z)
			);
	}

	public AxisAlignedBB getPrjBB() {

		return new AxisAlignedBB(
			dataManager.get(PROJECTILE_BB_MIN_X),
			dataManager.get(PROJECTILE_BB_MIN_Y),
			dataManager.get(PROJECTILE_BB_MIN_Z),
			dataManager.get(PROJECTILE_BB_MAX_X),
			dataManager.get(PROJECTILE_BB_MAX_Y),
			dataManager.get(PROJECTILE_BB_MAX_Z)
			);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		if (this.ticksExisted > 600)
			setDead();
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound p_70037_1_) {

		setDead();
	}


	@Override
	protected void writeEntityToNBT(NBTTagCompound p_70014_1_) {

	}

	static {
		ENTITY_BB_MIN_X = EntityDataManager.createKey(DebugTracker.class, DataSerializers.FLOAT);
		ENTITY_BB_MIN_Y = EntityDataManager.createKey(DebugTracker.class, DataSerializers.FLOAT);
		ENTITY_BB_MIN_Z = EntityDataManager.createKey(DebugTracker.class, DataSerializers.FLOAT);
		ENTITY_BB_MAX_X = EntityDataManager.createKey(DebugTracker.class, DataSerializers.FLOAT);
		ENTITY_BB_MAX_Y = EntityDataManager.createKey(DebugTracker.class, DataSerializers.FLOAT);
		ENTITY_BB_MAX_Z = EntityDataManager.createKey(DebugTracker.class, DataSerializers.FLOAT);
		ENTITY_EYE_HEIGHT = EntityDataManager.createKey(DebugTracker.class, DataSerializers.FLOAT);
		PROJECTILE_BB_MIN_X = EntityDataManager.createKey(DebugTracker.class, DataSerializers.FLOAT);
		PROJECTILE_BB_MIN_Y = EntityDataManager.createKey(DebugTracker.class, DataSerializers.FLOAT);
		PROJECTILE_BB_MIN_Z = EntityDataManager.createKey(DebugTracker.class, DataSerializers.FLOAT);
		PROJECTILE_BB_MAX_X = EntityDataManager.createKey(DebugTracker.class, DataSerializers.FLOAT);
		PROJECTILE_BB_MAX_Y = EntityDataManager.createKey(DebugTracker.class, DataSerializers.FLOAT);
		PROJECTILE_BB_MAX_Z = EntityDataManager.createKey(DebugTracker.class, DataSerializers.FLOAT);
		PROJECTILE_MOTION_X = EntityDataManager.createKey(DebugTracker.class, DataSerializers.FLOAT);
		PROJECTILE_MOTION_Y = EntityDataManager.createKey(DebugTracker.class, DataSerializers.FLOAT);
		PROJECTILE_MOTION_Z = EntityDataManager.createKey(DebugTracker.class, DataSerializers.FLOAT);
	}
}
