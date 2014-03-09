package powercrystals.minefactoryreloaded.farmables.plantables;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.oredict.OreDictionary;
import powercrystals.minefactoryreloaded.api.IFactoryPlantable;

/*
 * Used for directly placing blocks (ie saplings) and items (ie sugarcane). Pass in source ID to constructor,
 * so one instance per source ID.
 */

public class PlantableStandard implements IFactoryPlantable
{
	public static final int WILDCARD = OreDictionary.WILDCARD_VALUE;
	
	protected int _sourceId;
	protected int _plantedBlockId;
	protected int _validMeta;
	
	public PlantableStandard(int sourceId, int plantedBlockId)
	{
		this(sourceId, plantedBlockId, WILDCARD);
	}
	
	public PlantableStandard(int sourceId, int plantedBlockId, int validMeta)
	{
		if(plantedBlockId >= Block.blocksList.length)
		{
			throw new IllegalArgumentException("Passed an Item ID to FactoryPlantableStandard's planted block argument");
		}
		this._sourceId = sourceId;
		this._plantedBlockId = plantedBlockId;
		this._validMeta = validMeta;
	}
	
	@Override
	public boolean canBePlantedHere(World world, int x, int y, int z, ItemStack stack)
	{
		int groundId = world.getBlockId(x, y - 1, z);
		if(!world.isAirBlock(x, y, z) || (_validMeta != WILDCARD && stack.getItemDamage() != _validMeta))
		{
			return false;
		}
		return (Block.blocksList[_plantedBlockId].canPlaceBlockAt(world, x, y, z) && Block.blocksList[_plantedBlockId].canBlockStay(world, x, y, z)) ||
				(Block.blocksList[_plantedBlockId] instanceof IPlantable && Block.blocksList[groundId] != null &&
				Block.blocksList[groundId].canSustainPlant(world, x, y, z, ForgeDirection.UP, ((IPlantable)Block.blocksList[_plantedBlockId])));
	}
	
	@Override
	public void prePlant(World world, int x, int y, int z, ItemStack stack)
	{
		return;
	}
	
	@Override
	public void postPlant(World world, int x, int y, int z, ItemStack stack)
	{
		return;
	}
	
	@Override
	public int getPlantedBlockId(World world, int x, int y, int z, ItemStack stack)
	{
		if(stack.itemID != _sourceId)
		{
			return -1;
		}
		return _plantedBlockId;
	}
	
	@Override
	public int getPlantedBlockMetadata(World world, int x, int y, int z, ItemStack stack)
	{
		return stack.getItemDamage();
	}
	
	@Override
	public int getSeedId()
	{
		return _sourceId;
	}
}
