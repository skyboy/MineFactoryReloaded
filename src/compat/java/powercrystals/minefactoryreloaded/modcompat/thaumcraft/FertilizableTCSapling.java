/*
package powercrystals.minefactoryreloaded.modhelpers.thaumcraft;

import java.lang.reflect.Method;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.api.FertilizerType;
import powercrystals.minefactoryreloaded.farmables.fertilizables.FertilizableBase;

public class FertilizableTCSapling extends FertilizableBase
{
	private Method growSilverWood, growGreatWood;
	private Block plant;
	private boolean error = false;
	
	public FertilizableTCSapling(Block tcplant)
	{
		super(tcplant, FertilizerType.GrowMagicalCrop);
		plant = tcplant;
		try
		{
			Class<?> plant = Class.forName("thaumcraft.common.blocks.BlockCustomPlant");
			growSilverWood = plant.getDeclaredMethod("growSilverTree",
					World.class, int.class, int.class, int.class, Random.class);
			growGreatWood = plant.getDeclaredMethod("growGreatTree",
					World.class, int.class, int.class, int.class, Random.class);
		}
		catch(Throwable _) {_.printStackTrace();}
	}

	@Override
	protected boolean canFertilize(int metadata)
	{
		return !error & metadata < 2;
	}

	@Override
	public boolean fertilize(World world, Random rand, BlockPos pos, FertilizerType fertilizerType)
	{
		try {
			switch (world.getBlockMetadata(x, y, z))
			{
			case 0:
					growGreatWood.invoke(plant, world, x, y, z, rand);
				break;
			case 1:
					growSilverWood.invoke(plant, world, x, y, z, rand);
				break;
			default:
			}
		}
		catch(Throwable _)
		{
			_.printStackTrace();
			error = true;
		}
		return world.getBlock(x, y, z) != getPlant();
	}
}
*/
