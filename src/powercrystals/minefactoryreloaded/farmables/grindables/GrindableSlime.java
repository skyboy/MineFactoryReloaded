package powercrystals.minefactoryreloaded.farmables.grindables;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.api.IFactoryGrindable;
import powercrystals.minefactoryreloaded.api.MobDrop;

public class GrindableSlime implements IFactoryGrindable
{
	protected Class<?> grindable;
	protected ArrayList<MobDrop> drops;
	protected int dropSize;

	public GrindableSlime(Class<?> slime, MobDrop[] drops, int dropSize)
	{
		grindable = slime;
		ArrayList<MobDrop> q = new ArrayList<MobDrop>();
		q.addAll(Arrays.asList(drops));
		this.drops = q;
		this.dropSize = dropSize;
	}

	public GrindableSlime(Class<?> slime, MobDrop drop, int dropSize)
	{
		this(slime, new MobDrop[]{drop}, dropSize);
	}

	public GrindableSlime(Class<?> slime, ItemStack[] drops, int dropSize)
	{
		grindable = slime;
		ArrayList<MobDrop> q = new ArrayList<MobDrop>();
		for (ItemStack drop : drops)
			q.add(new MobDrop(10, drop));
		this.drops = q;
		this.dropSize = dropSize;
	}

	public GrindableSlime(Class<?> slime, ItemStack drop, int dropSize)
	{
		this(slime, new MobDrop[]{new MobDrop(10, drop), new MobDrop(20, null)}, dropSize);
	}

	@Override
	public Class<?> getGrindableEntity() {
		return grindable;
	}

	@Override
	public List<MobDrop> grind(World world, EntityLivingBase entity, Random random)
	{
		if (shouldDrop((EntitySlime)entity))
			return drops;
		return null;
	}
	
	protected boolean shouldDrop(EntitySlime slime)
	{
		return slime.getSlimeSize() > dropSize;
	}

	@Override
	public boolean processEntity(EntityLivingBase entity) {
		return false;
	}

}
