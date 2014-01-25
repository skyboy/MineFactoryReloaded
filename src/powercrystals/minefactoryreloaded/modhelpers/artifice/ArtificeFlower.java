package powercrystals.minefactoryreloaded.modhelpers.artifice;

import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.api.HarvestType;
import powercrystals.minefactoryreloaded.api.IFactoryHarvestable;

public class ArtificeFlower implements IFactoryHarvestable
{
	private int id;

	public ArtificeFlower(int id)
	{
		this.id = id;
	}

	@Override
	public int getPlantId()
	{
		return this.id;
	}

	@Override
	public HarvestType getHarvestType()
	{
		return HarvestType.Normal;
	}

	@Override
	public boolean breakBlock()
	{
		return true;
	}

	@Override
	public boolean canBeHarvested( World world, Map<String, Boolean> harvesterSettings, int x, int y, int z )
	{
		return true;
	}

	@Override
	public List<ItemStack> getDrops( World world, Random rand, Map<String, Boolean> harvesterSettings, int x, int y, int z )
	{
		return Block.blocksList[world.getBlockId(x, y, z)].getBlockDropped(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
	}

	@Override
	public void preHarvest( World world, int x, int y, int z )
	{
	}

	@Override
	public void postHarvest( World world, int x, int y, int z )
	{
	}
}