package powercrystals.minefactoryreloaded.farmables.harvestables;

import net.minecraft.block.BlockCrops;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import powercrystals.minefactoryreloaded.api.plant.HarvestType;
import powercrystals.minefactoryreloaded.api.util.IFactorySettings;

public class HarvestableCropPlant extends HarvestableStandard {

	public HarvestableCropPlant(net.minecraft.block.BlockCrops block) {

		super(block, HarvestType.Normal);
	}

	protected BlockCrops getCrop() {

		return (BlockCrops) getPlant();
	}

	@Override
	public boolean canBeHarvested(World world, BlockPos pos, IBlockState harvestState, IFactorySettings settings) {

		return getCrop().isMaxAge(harvestState);
	}

}
