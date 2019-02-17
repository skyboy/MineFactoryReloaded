package powercrystals.minefactoryreloaded.farmables.plantables;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import powercrystals.minefactoryreloaded.api.plant.IFactoryPlantable;
import powercrystals.minefactoryreloaded.api.plant.IReplacementBlock;
import powercrystals.minefactoryreloaded.api.plant.ReplacementBlock;

import javax.annotation.Nonnull;

/*
 * Used for directly placing blocks (ie saplings) and items (ie sugarcane). Pass in source ID to constructor,
 * so one instance per source ID.
 */

public class PlantableStandard implements IFactoryPlantable {

	public static final int WILDCARD = OreDictionary.WILDCARD_VALUE;

	protected Item _seed;
	protected Block _block;
	protected IReplacementBlock _plantedBlock;
	protected int _validMeta;

	public PlantableStandard(Block block) {

		this(Item.getItemFromBlock(block), block);
	}

	public PlantableStandard(Block block, Block plantedBlock) {

		this(Item.getItemFromBlock(block), plantedBlock);
	}

	public PlantableStandard(Block block, Block plantedBlock, boolean useItemMeta) {

		this(Item.getItemFromBlock(block), plantedBlock, useItemMeta);
	}

	public PlantableStandard(Item block, Block plantedBlock) {

		this(block, plantedBlock, WILDCARD);
	}

	public PlantableStandard(Block block, int meta) {

		this(Item.getItemFromBlock(block), block, meta);
	}

	public PlantableStandard(Block block, Block plantedBlock, int meta) {

		this(Item.getItemFromBlock(block), plantedBlock, meta);
	}

	public PlantableStandard(Item block, Block plantedBlock, int validMeta) {

		this(block, plantedBlock, validMeta, new ReplacementBlock(plantedBlock));
	}

	public PlantableStandard(Item block, Block plantedBlock, int validMeta, int plantedMeta) {

		this(block, plantedBlock, validMeta, new ReplacementBlock(plantedBlock).setMeta(plantedMeta));
	}

	public PlantableStandard(Item block, Block plantedBlock, boolean useItemMeta) {

		this(block, plantedBlock, WILDCARD, new ReplacementBlock(plantedBlock).setMeta(useItemMeta));
	}

	public PlantableStandard(Item block, Block plantedBlock, int validMeta, boolean useItemMeta) {

		this(block, plantedBlock, validMeta, new ReplacementBlock(plantedBlock).setMeta(useItemMeta));
	}

	public PlantableStandard(Item block, Block plantedBlock, int validMeta, IReplacementBlock repl) {

		_seed = block;
		_block = plantedBlock;
		_validMeta = validMeta;
		_plantedBlock = repl;
	}

	@Override
	public boolean canBePlanted(@Nonnull ItemStack stack, boolean forFermenting) {

		return _validMeta == WILDCARD || stack.getItemDamage() == _validMeta;
	}

	@Override
	public boolean canBePlantedHere(World world, BlockPos pos, @Nonnull ItemStack stack) {

		if (!world.isAirBlock(pos))
			return false;

		return (_block.canPlaceBlockAt(world, pos) && _block.canPlaceBlockOnSide(world, pos, EnumFacing.DOWN));
	}

	@Override
	public IReplacementBlock getPlantedBlock(World world, BlockPos pos, @Nonnull ItemStack stack) {

		return _plantedBlock;
	}

	@Override
	public Item getSeed() {

		return _seed;
	}

}
