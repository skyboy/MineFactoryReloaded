package powercrystals.minefactoryreloaded.tile.machine.mobs;

import cofh.core.fluid.FluidTankCore;

import java.util.List;

import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.core.GrindingDamage;
import powercrystals.minefactoryreloaded.setup.MFRFluids;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.machine.mobs.TileEntityGrinder;

public class TileEntitySlaughterhouse extends TileEntityGrinder
{
	public TileEntitySlaughterhouse()
	{
		super(Machine.Slaughterhouse);
		_damageSource = new GrindingDamage("mfr.slaughterhouse", 2);
		setManageSolids(false);
		_tanks[0].setLock(MFRFluids.getFluid("meat"));
		_tanks[1].setLock(MFRFluids.getFluid("pink_slime"));
	}

	@Override
	public void setWorld(World world)
	{
		super.setWorld(world);
		if (_grindingWorld != null)
			this._grindingWorld.setAllowSpawns(true);
	}

	@Override
	protected FluidTankCore[] createTanks()
	{
		return new FluidTankCore[]{new FluidTankCore(4 * BUCKET_VOLUME),
				new FluidTankCore(2 * BUCKET_VOLUME)};
	}

	@Override
	public boolean activateMachine()
	{
		_grindingWorld.cleanReferences();
		List<?> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, _areaManager.getHarvestArea().toAxisAlignedBB());

		entityList: for(Object o : entities)
		{
			EntityLivingBase e = (EntityLivingBase)o;
			for(Class<?> t : MFRRegistry.getSlaughterhouseBlacklist())
			{
				if(t.isInstance(e))
				{
					continue entityList;
				}
			}
			if((e instanceof EntityAgeable && ((EntityAgeable)e).getGrowingAge() < 0) || e.isEntityInvulnerable(_damageSource) ||
					e.getHealth() <= 0 || !_grindingWorld.addEntityForGrinding(e))
			{
				continue;
			}
			float massFound = (float)Math.pow(e.getEntityBoundingBox().getAverageEdgeLength(), 2);
			damageEntity(e);
			if(e.getHealth() <= 0)
			{
				if (_rand.nextInt(8) != 0)
					fillTank(_tanks[0], "meat", massFound);
				else
					fillTank(_tanks[1], "pink_slime", massFound);
				setIdleTicks(10);
			}
			else
			{
				setIdleTicks(5);
			}
			return true;
		}
		setIdleTicks(getIdleTicksMax());
		return false;
	}

	@Override
	public void acceptXPOrb(EntityXPOrb orb)
	{
	}

	@Override
	protected void damageEntity(EntityLivingBase entity)
	{
		setRecentlyHit(entity, 0);
		entity.attackEntityFrom(_damageSource, DAMAGE);
	}
}
