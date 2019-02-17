package powercrystals.minefactoryreloaded.farmables.harvestables;

import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import powercrystals.minefactoryreloaded.api.plant.HarvestType;
import powercrystals.minefactoryreloaded.api.util.IFactorySettings;

public class HarvestableCropMeta extends HarvestableStandard {

	private final PropertyInteger _ageProperty;
	private final int _targetAge;

	public HarvestableCropMeta(net.minecraft.block.Block block, HarvestType harvestType, PropertyInteger prop, int age) {

		super(block, harvestType);
		_ageProperty = prop;
		_targetAge = age;
	}

	public HarvestableCropMeta(net.minecraft.block.Block block, PropertyInteger prop, int age) {

		this(block, HarvestType.Normal, prop, age);
	}

	@Override
	public boolean canBeHarvested(World world, BlockPos pos, IBlockState harvestState, IFactorySettings settings) {

		IBlockState state = harvestState.getActualState(world, pos);
		return state.getValue(_ageProperty) >= _targetAge;
	}

}
