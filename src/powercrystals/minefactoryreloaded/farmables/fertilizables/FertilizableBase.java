package powercrystals.minefactoryreloaded.farmables.fertilizables;

import java.util.Random;

import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.api.FertilizerType;
import powercrystals.minefactoryreloaded.api.IFactoryFertilizable;

public abstract class FertilizableBase implements IFactoryFertilizable
{
	protected final int fertilizableId;
	protected final FertilizerType validFertilizer;
	
	public FertilizableBase(int id, FertilizerType type)
	{
		fertilizableId = id;
		validFertilizer = type;
	}
	
	public FertilizableBase(int id)
	{
		this(id, FertilizerType.GrowPlant);
	}
	
	@Override
	public int getFertilizableBlockId()
	{
		return fertilizableId;
	}
	
	@Override
	public boolean canFertilizeBlock(World world, int x, int y, int z, FertilizerType fertilizerType)
	{
		return fertilizerType == validFertilizer;
	}

	@Override
	public abstract boolean fertilize(World world, Random rand, int x, int y, int z, FertilizerType fertilizerType);
}
