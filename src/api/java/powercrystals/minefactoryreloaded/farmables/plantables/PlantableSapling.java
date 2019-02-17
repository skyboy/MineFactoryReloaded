package powercrystals.minefactoryreloaded.farmables.plantables;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;

import javax.annotation.Nonnull;

public class PlantableSapling extends PlantableStandard
{
	public PlantableSapling(Item seed, Block plant)
	{
		super(seed, plant, true);
	}
	
	public PlantableSapling(Block plant)
	{
		super(plant, plant, true);
	}
	
	@Override
	public boolean canBePlantedHere(World world, BlockPos pos, @Nonnull ItemStack stack)
	{
		IBlockState stateDown = world.getBlockState(pos.down());
		Block ground = stateDown.getBlock();
		if(!world.isAirBlock(pos))
		{
			return false;
		}
		return _block.canPlaceBlockAt(world, pos) || (
						_block instanceof IPlantable &&
						ground.canSustainPlant(stateDown, world, pos,
								EnumFacing.UP, (IPlantable)_block));
	}
}
