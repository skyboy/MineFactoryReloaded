package powercrystals.minefactoryreloaded.modcompat;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import powercrystals.minefactoryreloaded.api.plant.FertilizerType;
import powercrystals.minefactoryreloaded.api.plant.IFactoryFertilizable;

import java.lang.reflect.Method;
import java.util.Random;

public class FertilizableCropReflection implements IFactoryFertilizable {

	private Method _fertilize;
	private Block _block;
	protected int _targetMeta;

	public FertilizableCropReflection(Block block, Method fertilize, int targetMeta) {

		_block = block;
		_fertilize = fertilize;
		_targetMeta = targetMeta;
	}

	@Override
	public Block getPlant() {

		return _block;
	}

	@Override
	public boolean canFertilize(World world, BlockPos pos, FertilizerType fertilizerType) {

		IBlockState state = world.getBlockState(pos);
		return state.getBlock().getMetaFromState(state) < _targetMeta && fertilizerType == FertilizerType.GrowPlant;
	}

	@Override
	public boolean fertilize(World world, Random rand, BlockPos pos, FertilizerType fertilizerType) {

		try {
			_fertilize.invoke(_block, world, pos);
		} catch (Exception e) {
			e.printStackTrace();
		}
		IBlockState state = world.getBlockState(pos);
		return state.getBlock().getMetaFromState(state) >= _targetMeta;
	}

}
