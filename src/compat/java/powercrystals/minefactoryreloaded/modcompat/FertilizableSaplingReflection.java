package powercrystals.minefactoryreloaded.modcompat;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import powercrystals.minefactoryreloaded.api.plant.FertilizerType;
import powercrystals.minefactoryreloaded.api.plant.IFactoryFertilizable;

import java.lang.reflect.Method;
import java.util.Random;

public class FertilizableSaplingReflection implements IFactoryFertilizable {

	private Method _fertilize;
	private Block _blockId;

	public FertilizableSaplingReflection(Block blockId, Method fertilize) {

		_blockId = blockId;
		_fertilize = fertilize;
	}

	@Override
	public Block getPlant() {

		return _blockId;
	}

	@Override
	public boolean canFertilize(World world, BlockPos pos, FertilizerType fertilizerType) {

		return fertilizerType == FertilizerType.GrowPlant;
	}

	@Override
	public boolean fertilize(World world, Random rand, BlockPos pos, FertilizerType fertilizerType) {

		try {
			_fertilize.invoke(_blockId, world, pos);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return world.getBlockState(pos).getBlock() != _blockId;
	}

}
