package powercrystals.minefactoryreloaded.modhelpers.vanilla.handlers.spawnhandlers;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.init.Blocks;

import powercrystals.minefactoryreloaded.api.handler.IMobSpawnHandler;

public class SpawnableEnderman implements IMobSpawnHandler
{
	@Override
	public Class<? extends EntityLivingBase> getMobClass()
	{
		return EntityEnderman.class;
	}

	@Override
	public void onMobSpawn(EntityLivingBase entity)
	{
	}

	@Override
	public void onMobExactSpawn(EntityLivingBase entity) {
		((EntityEnderman)entity).setHeldBlockState(Blocks.AIR.getDefaultState());
	}
}
