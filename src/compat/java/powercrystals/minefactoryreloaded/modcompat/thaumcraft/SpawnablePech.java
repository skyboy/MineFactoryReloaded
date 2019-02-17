/*
package powercrystals.minefactoryreloaded.modhelpers.thaumcraft;

import java.lang.reflect.Field;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

import powercrystals.minefactoryreloaded.api.IMobSpawnHandler;

public class SpawnablePech implements IMobSpawnHandler
{
	private Class<? extends EntityLivingBase> _tcPechClass;
	private Field _tcPechLootField;

	@SuppressWarnings("unchecked")
	public SpawnablePech(Class<?> pech)
	{
		_tcPechClass = (Class<? extends EntityLivingBase>)pech;
		try
		{
			_tcPechLootField = _tcPechClass.getDeclaredField("loot");
		}
		catch (Throwable e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public Class<? extends EntityLivingBase> getMobClass()
	{
		return _tcPechClass;
	}

	@Override
	public void onMobSpawn(EntityLivingBase entity) {}

	@Override
	public void onMobExactSpawn(EntityLivingBase entity)
	{
		try
		{
			_tcPechLootField.set(entity, new ItemStack[9]);
		}
		catch (Throwable e)
		{
			e.printStackTrace();
			entity.setDead();
		}
	}
}
*/
