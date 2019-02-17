package powercrystals.minefactoryreloaded.core;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import powercrystals.minefactoryreloaded.api.util.IFactorySettings;

import java.util.List;

public class FruitHarvestManager implements IHarvestManager
{
	private List<BlockPos> _treeBlocks;
	private int _currentBlock;
	private boolean _isDone;
	
	private HarvestMode _harvestMode; 
	
	private Area _area;
	
	public FruitHarvestManager(World world, Area area, HarvestMode harvestMode)
	{
		reset(world, area, harvestMode, null);
	}
	
	@Override
	public BlockPos getNextBlock()
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
	public void reset(World world, Area area, HarvestMode harvestMode, IFactorySettings s)
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
	public BlockPos getOrigin()
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
		BlockPos o = getOrigin();
		data.setIntArray("area", new int[] {o.getX() - _area.xMin, o.getY() - _area.yMin, _area.yMax - o.getY()});
		data.setIntArray("origin", new int[] {o.getX(), o.getY(), o.getZ()});
		tag.setTag("harvestManager", data);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		NBTTagCompound data = tag.getCompoundTag("harvestManager");
		_isDone = data.getBoolean("done");
		_currentBlock = data.getInteger("curPos");
		_harvestMode = HarvestMode.values()[data.getInteger("mode")];
		int[] area = data.getIntArray("area"), o = data.getIntArray("origin");
		if (o.length < 3 || area.length < 3)
		{
			_isDone = true;
			return;
		}
		_area = new Area(new BlockPos(o[0], o[1], o[2]), area[0], area[1], area[2]);
	}

	@Override
	public void free() { }
}
