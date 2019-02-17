package powercrystals.minefactoryreloaded.core;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import powercrystals.minefactoryreloaded.api.util.IFactorySettings;
import powercrystals.minefactoryreloaded.core.BlockPool.BlockNode;

public class FluidFillingManager implements IHarvestManager {

	private BlockPool _blocks;
	private boolean _isDone;
	
	private Area _area;
	private World _world;

	private BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

	public FluidFillingManager(World world, Area area) {

		reset(world, area, null, null);
		_isDone = true;
	}

	@Override
	public BlockPos getNextBlock() {

		BlockNode bn = _blocks.poke();
		return pos.setPos(bn.x, bn.y, bn.z);
	}

	@Override
	public void moveNext() {

		searchForFreeBlocks(_blocks.shift());
		if (_blocks.size() == 0) {
			_isDone = true;
		}
	}

	private void searchForFreeBlocks(BlockNode bn) {

		BlockNode cur;

		for (EnumFacing side : EnumFacing.VALUES) {

			cur = BlockPool.getNext(
					bn.x + side.getFrontOffsetX(),
					bn.y + side.getFrontOffsetY(),
					bn.z + side.getFrontOffsetZ()
			);
			if (isValid(cur))
				_blocks.push(cur);
			else
				cur.free();
		}

		bn.free();
	}

	private boolean isValid(BlockNode bp) {

		Area area = _area;
		if (bp.x < area.xMin || bp.x > area.xMax ||
				bp.y < area.yMin || bp.y > area.yMax ||
				bp.z < area.zMin || bp.z > area.zMax)
			return false;

		BlockPos pos = this.pos.setPos(bp.x, bp.y, bp.z);
		if (!_world.isBlockLoaded(pos))
			return false;

		Block block = _world.getBlockState(pos).getBlock();
		return block.isReplaceable(_world, pos);
	}

	@Override
	public void reset(World world, Area area, HarvestMode harvestMode, IFactorySettings s) {

		setWorld(world);
		_area = area;
		free();
		_isDone = false;
		_blocks = new BlockPool();
		_blocks.push(BlockPool.getNext(area.getOrigin()));
	}

	@Override
	public void setWorld(World world) {

		_world = world;
	}

	@Override
	public boolean getIsDone() {

		return _isDone;
	}

	@Override
	public BlockPos getOrigin() {

		return _area.getOrigin();
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {

		NBTTagCompound data = new NBTTagCompound();
		data.setBoolean("done", _isDone);
		BlockPos o = getOrigin();
		data.setIntArray("area", new int[] {o.getX() - _area.xMin, o.getY() - _area.yMin, _area.yMax - o.getY()});
		data.setIntArray("origin", new int[] {o.getX(), o.getY(), o.getZ()});
		NBTTagList list = new NBTTagList();
		BlockNode bn = _blocks.poke();
		while (bn != null) {
			NBTTagCompound p = new NBTTagCompound();
			p.setInteger("x", bn.x);
			p.setInteger("y", bn.y);
			p.setInteger("z", bn.z);
			list.appendTag(p);
			bn = bn.next;
		}
		data.setTag("curPos", list);
		tag.setTag("harvestManager", data);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {

		free();
		_blocks = new BlockPool();

		NBTTagCompound data = tag.getCompoundTag("harvestManager");
		_isDone = data.getBoolean("done");
		int[] area = data.getIntArray("area"), o = data.getIntArray("origin");
		if (o.length < 3 | area.length < 3) {
			_area = new Area(new BlockPos(0,-1,0),0,0,0);
			_isDone = true;
			return;
		}

		_area = new Area(new BlockPos(o[0], o[1], o[2]), area[0], area[1], area[2]);
		NBTTagList list = (NBTTagList)data.getTag("curPos");
		for (int i = 0, e = list.tagCount(); i < e; ++i) {
			NBTTagCompound p = list.getCompoundTagAt(i);
			_blocks.push(BlockPool.getNext(p.getInteger("x"), p.getInteger("y"), p.getInteger("z")));
		}
	}

	@Override
	public void free() {

		if (_blocks != null) while (_blocks.poke() != null)
			_blocks.shift().free();
		_isDone = true;
	}

}
