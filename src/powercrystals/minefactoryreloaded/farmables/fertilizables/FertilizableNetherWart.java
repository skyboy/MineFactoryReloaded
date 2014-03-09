package powercrystals.minefactoryreloaded.farmables.fertilizables;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.api.FertilizerType;

public class FertilizableNetherWart extends FertilizableCropPlant
{
	public FertilizableNetherWart()
	{
		super(Block.netherStalk.blockID, 3);
	}
	
	@Override
	public boolean fertilize(World world, Random rand, int x, int y, int z, FertilizerType fertilizerType)
	{
		world.setBlockMetadataWithNotify(x, y, z, 3, 2);
		return true;
	}
}
