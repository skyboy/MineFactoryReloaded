package powercrystals.minefactoryreloaded.farmables.harvestables;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import powercrystals.minefactoryreloaded.api.plant.HarvestType;
import powercrystals.minefactoryreloaded.api.util.IFactorySettings;
import powercrystals.minefactoryreloaded.api.util.IFactorySettings.SettingNames;

public class HarvestableMushroom extends HarvestableStandard {

	public HarvestableMushroom(net.minecraft.block.Block block) {

		super(block, HarvestType.Normal);
	}

	@Override
	public boolean canBeHarvested(World world, BlockPos pos, IBlockState harvestState, IFactorySettings settings) {

		return settings.getBoolean(SettingNames.HARVEST_SMALL_MUSHROOMS);
	}

}
