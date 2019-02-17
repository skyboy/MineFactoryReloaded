package powercrystals.minefactoryreloaded.farmables.harvestables;

import net.minecraft.block.Block;
import net.minecraft.block.BlockStem;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import powercrystals.minefactoryreloaded.api.plant.HarvestType;
import powercrystals.minefactoryreloaded.api.util.IFactorySettings;
import powercrystals.minefactoryreloaded.api.util.IFactorySettings.SettingNames;

public class HarvestableStemPlant extends HarvestableCropMeta {

	protected final Block _fruit;

	public HarvestableStemPlant(Block block, Block fruit) {

		super(block, HarvestType.PlantStem, BlockStem.AGE, 7);
		_fruit = fruit;
	}

	@Override
	public boolean canBeHarvested(World world, BlockPos pos, IBlockState harvestState, IFactorySettings settings) {

		if (harvestState.getBlock().equals(getPlant()))
			return super.canBeHarvested(world, pos, harvestState, settings);
		else if (!harvestState.getBlock().equals(_fruit))
			return false;
		else {
			BlockPos start = new BlockPos(settings.getVec(SettingNames.START_POSITION));
			EnumFacing facing = EnumFacing.getFacingFromVector(pos.getX() - start.getX(), pos.getY() - start.getY(), pos.getZ() - start.getZ());
			harvestState = world.getBlockState(start).getActualState(world, start);
			return harvestState.getValue(BlockStem.FACING) == facing;
		}
	}

}
