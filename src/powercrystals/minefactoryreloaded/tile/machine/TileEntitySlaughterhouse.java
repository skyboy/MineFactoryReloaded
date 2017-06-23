package powercrystals.minefactoryreloaded.tile.machine;

import cofh.core.fluid.FluidTankCore;

import java.util.List;

import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidRegistry;

import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.core.GrindingDamage;
import powercrystals.minefactoryreloaded.setup.Machine;

public class TileEntitySlaughterhouse extends TileEntityGrinder
{
	public TileEntitySlaughterhouse()
	{
		super(Machine.Slaughterhouse);
		_damageSource = new GrindingDamage("mfr.slaughterhouse", 2);
		setManageSolids(false);
		_entityEventController.setAllowItemDrops(false);
		_entityEventController.setConsumeXP(false);
		_tanks[0].setLock(FluidRegistry.getFluid("meat"));
		_tanks[1].setLock(FluidRegistry.getFluid("pink_slime"));
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

		List<?> entities = worldObj.getEntitiesWithinAABB(EntityLivingBase.class, _areaManager.getHarvestArea().toAxisAlignedBB());

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
					e.getHealth() <= 0 )
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
	public int acceptXP(int XP)
	{
		return 0;
	}

	@Override
	protected void damageEntity(EntityLivingBase entity)
	{
		setRecentlyHit(entity, 0);
		entity.attackEntityFrom(_damageSource, DAMAGE);
	}
}
