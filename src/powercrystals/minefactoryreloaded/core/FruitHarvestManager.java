package powercrystals.minefactoryreloaded.core;

import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import powercrystals.core.position.Area;
import powercrystals.core.position.BlockPosition;

public class FruitHarvestManager implements IHarvestManager
{
	private List<BlockPosition> _treeBlocks;
	private int _currentBlock;
	private boolean _isDone;
	
	private HarvestMode _harvestMode; 
	
	private Area _area;
	
	public FruitHarvestManager(World world, Area area, HarvestMode harvestMode)
	{
		reset(world, area, harvestMode);
	}
	
	@Override
	public BlockPosition getNextBlock()
	{
		return _treeBlocks.get(_currentBlock);
	}
	
	@Override
	public void moveNext()
	{
		_currentBlock++;
		if (_currentBlock >= _treeBlocks.size())
		{
			_isDone = true;
		}
	}
	
	@Override
	public void reset(World world, Area area, HarvestMode harvestMode)
	{
		_harvestMode = harvestMode;
		_area = area;
		_currentBlock = 0;
		_isDone = false;
		_treeBlocks = (_harvestMode.isInverted ? _area.getPositionsTopFirst() : _area.getPositionsBottomFirst());
	}

	@Override
	public void setWorld(World world) { }
	
	@Override
	public boolean getIsDone()
	{
		return _isDone;
	}

	@Override
	public BlockPosition getOrigin()
	{
		return _area.getOrigin();
	}

	@Override
	public void writeToNBT(NBTTagCompound tag)
	{
		NBTTagCompound data = new NBTTagCompound();
		data.setBoolean("done", _isDone);
		data.setInteger("curPos", _currentBlock);
		data.setInteger("mode", _harvestMode.ordinal());
		BlockPosition o = getOrigin();
		data.setIntArray("area", new int[] {o.x - _area.xMin, o.y - _area.yMin, _area.yMax - o.y});
		data.setIntArray("origin", new int[] {o.x, o.y, o.z});
		tag.setCompoundTag("harvestManager", data);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		NBTTagCompound data = tag.getCompoundTag("harvestManager");
		_isDone = data.getBoolean("done");
		_currentBlock = data.getInteger("curPos");
		_harvestMode = HarvestMode.values()[data.getInteger("mode")];
		int[] area = data.getIntArray("area"), o = data.getIntArray("origin");
		if (area == null | o == null || o.length < 3 | area.length < 3)
		{
			_isDone = true;
			return;
		}
		_area = new Area(new BlockPosition(o[0], o[1], o[2]), area[0], area[1], area[2]);
	}

	@Override
	public void free() { }
}
