package powercrystals.minefactoryreloaded.modcompat.ic2;

import ic2.api.crops.CropCard;
import ic2.api.crops.ICropTile;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import powercrystals.minefactoryreloaded.api.plant.*;
import powercrystals.minefactoryreloaded.api.util.IFactorySettings;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Random;

public class IC2Crop implements IFactoryHarvestable, IFactoryFertilizable, IFactoryFruit {

	private Block _block;

	IC2Crop(Block block) {

		_block = block;
	}

	@Nonnull
	@Override
	public Block getPlant() {

		return _block;
	}

	@Override
	public boolean canFertilize(World world, BlockPos pos, FertilizerType fertilizerType) {

		return fertilizerType != FertilizerType.Grass && canFert(world, pos);
	}

	private boolean canFert(World world, BlockPos pos) {

		try {
			TileEntity te = world.getTileEntity(pos);
			if (!(te instanceof ICropTile))
				return false;
			ICropTile tec = (ICropTile) te;

			return tec.getStorageNutrients() < 15;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public boolean fertilize(World world, Random rand, BlockPos pos, FertilizerType fertilizerType) {

		try {
			ICropTile tec = (ICropTile) world.getTileEntity(pos);
			tec.setStorageNutrients(100);
			tec.updateState();
			return tec.getStorageNutrients() == 100;
		} catch (Exception e) {
			return false;
		}
	}

	@Nonnull
	@Override
	public HarvestType getHarvestType() {

		return HarvestType.Normal;
	}

	@Override
	public boolean breakBlock() {

		return false;
	}

	@Override
	public boolean canBeHarvested(World world, BlockPos pos, IBlockState harvestState, IFactorySettings harvesterSettings) {

		return canHarvest(world, pos);
	}

	@Override
	public boolean canBePicked(World world, BlockPos pos) {

		return canHarvest(world, pos);
	}

	private boolean canHarvest(World world, BlockPos pos) {

		TileEntity te = world.getTileEntity(pos);
		if (!(te instanceof ICropTile))
			return false;

		ICropTile tec = (ICropTile) te;
		CropCard crop;
		try {
			crop = tec.getCrop();
			if (crop == null)
				return false;
			if (!crop.canBeHarvested(tec) || crop.canGrow(tec)) {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return true;
	}

	@Override
	public List<ItemStack> getDrops(World world, BlockPos pos, IBlockState harvestState, Random rand, IFactorySettings harvesterSettings) {

		NonNullList<ItemStack> drops = NonNullList.create();
		getDrops(drops, world, rand, pos);
		return drops;
	}

	@Override
	public List<ItemStack> getDrops(World world, Random rand, BlockPos pos) {

		NonNullList<ItemStack> drops = NonNullList.create();
		getDrops(drops, world, rand, pos);
		return drops;
	}

	private void getDrops(NonNullList<ItemStack> drops, World world, Random rand, BlockPos pos) {

		try {
			ICropTile tec = (ICropTile) world.getTileEntity(pos);
			CropCard crop = tec.getCrop();

			double chance = crop.dropGainChance();
			for (int i = 0; i < tec.getStatGain(); i++) {
				chance *= 1.03F;
			}

			chance -= rand.nextFloat();
			int numDrops = 0;
			while (chance > 0.0F) {
				numDrops++;
				chance -= rand.nextFloat();
			}
			NonNullList<ItemStack> cropDrops = NonNullList.withSize(numDrops, ItemStack.EMPTY);
			for (int i = 0; i < numDrops; i++) {
				cropDrops.set(i, crop.getGain(tec));
				if ((!cropDrops.get(i).isEmpty()) && (rand.nextInt(100) <= tec.getStatGain())) {
					cropDrops.get(i).grow(1);
				}
			}

			tec.setCurrentSize(crop.getSizeAfterHarvest(tec));
			tec.updateState();

			drops.addAll(cropDrops);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public IReplacementBlock getReplacementBlock(World world, BlockPos pos) {

		return IReplacementBlock.NO_OP;
	}

}
