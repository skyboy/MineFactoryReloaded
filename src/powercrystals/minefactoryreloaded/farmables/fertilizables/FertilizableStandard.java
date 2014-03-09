package powercrystals.minefactoryreloaded.farmables.fertilizables;

import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.api.FertilizerType;

public abstract class FertilizableStandard extends FertilizableBase
{
	public FertilizableStandard(int id, FertilizerType type)
	{
		super(id, type);
	}
	
	public FertilizableStandard(int id)
	{
		this(id, FertilizerType.GrowPlant);
	}
	
	@Override
	public boolean canFertilizeBlock(World world, int x, int y, int z, FertilizerType fertilizerType)
	{
		return fertilizerType == validFertilizer && canFertilize(world.getBlockMetadata(x, y, z));
	}
	
	protected abstract boolean canFertilize(int metadata);
}
