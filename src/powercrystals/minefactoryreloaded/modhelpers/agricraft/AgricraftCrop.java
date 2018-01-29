package powercrystals.minefactoryreloaded.modhelpers.agricraft;

import com.google.common.collect.Lists;
import com.infinityraider.agricraft.api.v1.util.MethodResult;
import com.infinityraider.agricraft.compat.vanilla.BonemealWrapper;
import com.infinityraider.agricraft.init.AgriBlocks;
import com.infinityraider.agricraft.tiles.TileEntityCrop;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import powercrystals.minefactoryreloaded.api.FertilizerType;
import powercrystals.minefactoryreloaded.api.HarvestType;
import powercrystals.minefactoryreloaded.api.IFactoryFertilizable;
import powercrystals.minefactoryreloaded.api.IFactoryHarvestable;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class AgricraftCrop implements IFactoryHarvestable, IFactoryFertilizable {

	@Override
	public Block getPlant() {

		return AgriBlocks.getInstance().CROP;
	}

	@Override
	public boolean canFertilize(World world, BlockPos pos, FertilizerType fertilizerType) {

		if (fertilizerType != FertilizerType.GrowPlant && fertilizerType != FertilizerType.Grass) {
			return false;
		}

		TileEntity te = world.getTileEntity(pos);
		if (te instanceof TileEntityCrop) {
			return ((TileEntityCrop) te).acceptsFertilizer(BonemealWrapper.INSTANCE);
		}
		return false;
	}

	@Override
	public boolean fertilize(World world, Random rand, BlockPos pos, FertilizerType fertilizerType) {

		if (fertilizerType != FertilizerType.GrowPlant && fertilizerType != FertilizerType.Grass) {
			return false;
		}

		TileEntity te = world.getTileEntity(pos);
		if (te instanceof TileEntityCrop) {
			return ((TileEntityCrop) te).onApplyFertilizer(BonemealWrapper.INSTANCE, rand) == MethodResult.SUCCESS;
		}
		return false;
	}

	@Override
	public HarvestType getHarvestType() {

		return HarvestType.Normal;
	}

	@Override
	public boolean breakBlock() {

		return false;
	}

	@Override
	public boolean canBeHarvested(World world, Map<String, Boolean> harvesterSettings, BlockPos pos) {

		TileEntity te = world.getTileEntity(pos);
		if (te instanceof TileEntityCrop) {
			return ((TileEntityCrop) te).canBeHarvested();
		}
		return false;
	}

	@Override
	public List<ItemStack> getDrops(World world, Random rand, Map<String, Boolean> harvesterSettings, BlockPos pos) {

		TileEntity te = world.getTileEntity(pos);
		List<ItemStack> drops = Lists.newArrayList();
		if (te instanceof TileEntityCrop) {
			TileEntityCrop crop = (TileEntityCrop) te;
			crop.getDrops(drops::add, false, false);

			//add the bonus chance seeds
			if (crop.getSeed().getPlant().getSeedDropChanceBonus() > rand.nextDouble()) {
				drops.add(crop.getSeed().toStack());
			}
		}

		return drops;
	}

	@Override
	public void preHarvest(World world, BlockPos pos) {

	}

	@Override
	public void postHarvest(World world, BlockPos pos) {

		TileEntity te = world.getTileEntity(pos);
		if (te instanceof TileEntityCrop) {
			((TileEntityCrop) te).setGrowthStage(0);
		}
	}

}
