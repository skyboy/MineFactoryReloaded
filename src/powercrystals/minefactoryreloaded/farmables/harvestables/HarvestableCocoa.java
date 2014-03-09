package powercrystals.minefactoryreloaded.farmables.harvestables;

import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.api.HarvestType;

public class HarvestableCocoa extends HarvestableStandard
{
	public HarvestableCocoa()
	{
		this(Block.cocoaPlant.blockID);
	}
	
	public HarvestableCocoa(int blockId)
	{
		super(blockId, HarvestType.Normal);
	}
	
	@Override
	public boolean canBeHarvested(World world, Map<String, Boolean> harvesterSettings, int x, int y, int z)
	{
		int blockMetadata = world.getBlockMetadata(x, y, z);
		return ((blockMetadata & 12) >> 2) >= 2;
	}
}
