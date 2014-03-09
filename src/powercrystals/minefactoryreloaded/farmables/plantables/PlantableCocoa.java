package powercrystals.minefactoryreloaded.farmables.plantables;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLog;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.world.World;

public class PlantableCocoa extends PlantableStandard
{
	public PlantableCocoa(int sourceId, int plantedBlockId)
	{
		super(sourceId, plantedBlockId);
	}
	
	public PlantableCocoa(int sourceId, int plantedBlockId, int validMeta)
	{
		super(sourceId, plantedBlockId, validMeta);
	}
	
	@Override
	public boolean canBePlantedHere(World world, int x, int y, int z, ItemStack stack)
	{
		if(!world.isAirBlock(x, y, z) || (_validMeta != WILDCARD && stack.getItemDamage() != _validMeta))
		{
			return false;
		}
		return isNextToAcceptableLog(world, x, y, z);
	}
	
	protected boolean isNextToAcceptableLog(World world, int x, int y, int z)
	{
		return isGoodLog(world, x+1, y, z) ||
				isGoodLog(world, x-1, y, z) ||
				isGoodLog(world, x, y, z+1) ||
				isGoodLog(world, x, y, z-1);
	}
	
	protected boolean isGoodLog(World world, int x, int y, int z)
	{
		return world.getBlockId(x, y, z) == Block.wood.blockID && BlockLog.limitToValidMetadata(world.getBlockMetadata(x, y, z)) == 3;
	}
	
	@Override
	public int getPlantedBlockMetadata(World world, int x, int y, int z, ItemStack stack)
	{
		int blockDirection = 4; // NORTH
		if (isGoodLog(world, x-1, y, z))
		{
			blockDirection = 5; // SOUTH
		}
		else if (isGoodLog(world, x, y, z+1))
		{
			blockDirection = 2; // EAST
		}
		else if (isGoodLog(world, x, y, z-1))
		{
			blockDirection = 3; // WEST
		}
		
		return Direction.rotateOpposite[Direction.facingToDirection[blockDirection]];
	}
}
