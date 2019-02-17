package powercrystals.minefactoryreloaded.farmables.plantables;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import powercrystals.minefactoryreloaded.api.plant.ReplacementBlock;

import javax.annotation.Nonnull;

public class PlantableSoil extends PlantableStandard {

	public PlantableSoil(Block block, boolean useItemMeta) {

		super(Item.getItemFromBlock(block), block, useItemMeta);
	}

	public PlantableSoil(Item block, Block plantedBlock) {

		super(block, plantedBlock);
	}

	public PlantableSoil(Item block, Block plantedBlock, int validMeta) {

		super(block, plantedBlock, validMeta);
	}

	public PlantableSoil(Block block, int plantedMeta) {

		this(Item.getItemFromBlock(block), plantedMeta, block);
	}

	public PlantableSoil(Item block, int plantedMeta, Block plantedBlock) {

		super(block, plantedBlock, WILDCARD, new ReplacementBlock(plantedBlock).setMeta(plantedMeta));
	}

	@Override
	public boolean canBePlanted(@Nonnull ItemStack stack, boolean forFermenting) {

		return !forFermenting && super.canBePlanted(stack, forFermenting);
	}

	@Override
	public boolean canBePlantedHere(World world, BlockPos pos, @Nonnull ItemStack stack) {

		if (!world.isAirBlock(pos)) {
			IBlockState state = world.getBlockState(pos);
			if (FluidRegistry.lookupFluidForBlock(state.getBlock()) == WATER.getFluid()) {
				IBlockState stateUp = world.getBlockState(pos.up());
				if (FluidRegistry.lookupFluidForBlock(stateUp.getBlock()) == WATER.getFluid())
					return false;
				else
					return state.getValue(BlockFluidBase.LEVEL) != 0;
			} else {
				Block block = state.getBlock();
				return !state.getMaterial().isLiquid() && block.isReplaceable(world, pos);
			}
		}

		return true;
	}

	/**
	 * Storing the Fluid doesn't work: if it changes on world remap, you get false;
	 * FluidStack#getFluid() however always returns the correct fluid for the world.
	 */
	protected static final FluidStack WATER = new FluidStack(FluidRegistry.WATER, 0);

}
