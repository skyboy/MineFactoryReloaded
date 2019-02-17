/*
package powercrystals.minefactoryreloaded.modhelpers.thaumcraft;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.farmables.plantables.PlantableSapling;

public class PlantableThaumcraftTree extends PlantableSapling
{
	public PlantableThaumcraftTree(Block sourceId)
	{
		super(sourceId);
	}
	
	@Override
	public boolean canBePlantedHere(World world, BlockPos pos, @Nonnull ItemStack stack)
	{
		int saplingMeta = world.getBlockMetadata(x, y, z);
		if(!super.canBePlantedHere(world, x, y, z, stack))
		{
			return false;
		}
		return saplingMeta != 0 || (world.isAirBlock(x + 1, y, z) && world.isAirBlock(x + 1, y, z + 1) && world.isAirBlock(x, y, z + 1));
	}
}
*/
