package powercrystals.minefactoryreloaded.farmables.harvestables;

import net.minecraft.block.BlockCocoa;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import powercrystals.minefactoryreloaded.api.plant.HarvestType;
import powercrystals.minefactoryreloaded.api.util.IFactorySettings;
import powercrystals.minefactoryreloaded.api.util.IFactorySettings.SettingNames;

public class HarvestableCocoa extends HarvestableCropMeta {

	public HarvestableCocoa(net.minecraft.block.Block blockId) {

		super(blockId, HarvestType.TreeFruit, BlockCocoa.AGE, 2);
	}

	@Override
	public boolean canBeHarvested(World world, BlockPos pos, IBlockState harvestState, IFactorySettings settings) {

		if (settings.getBoolean(SettingNames.HARVESTING_TREE))
			return true;
		return super.canBeHarvested(world, pos, harvestState, settings);
	}

}
