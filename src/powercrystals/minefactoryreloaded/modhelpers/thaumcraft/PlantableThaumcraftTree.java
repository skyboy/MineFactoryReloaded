package powercrystals.minefactoryreloaded.modhelpers.thaumcraft;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.farmables.plantables.PlantableStandard;

public class PlantableThaumcraftTree extends PlantableStandard
{
	public PlantableThaumcraftTree(int sourceId, int plantedBlockId)
	{
		super(sourceId, plantedBlockId);
	}
	
	@Override
	public boolean canBePlantedHere(World world, int x, int y, int z, ItemStack stack)
	{
		int saplingMeta = world.getBlockMetadata(x, y, z);
		if(!super.canBePlantedHere(world, x, y, z, stack))
		{
			return false;
		}
		return saplingMeta != 0 || (world.isAirBlock(x + 1, y, z) && world.isAirBlock(x + 1, y, z + 1) && world.isAirBlock(x, y, z + 1));
	}
}
