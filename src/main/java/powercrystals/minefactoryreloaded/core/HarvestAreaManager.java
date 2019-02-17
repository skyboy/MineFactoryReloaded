package powercrystals.minefactoryreloaded.core;

import cofh.api.item.IAugmentItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import powercrystals.minefactoryreloaded.item.ItemUpgrade;

import javax.annotation.Nonnull;
import java.util.List;

public class HarvestAreaManager <T extends TileEntity & IRotatableTile>
{
	private T _owner;

	private int _originX;
	private int _originY;
	private int _originZ;
	private EnumFacing _originOrientation;

	private EnumFacing _overrideDirection;
	private Area _harvestArea;
	private int _radius;
	private int _areaUp;
	private int _areaDown;

	private int _originOffsetX;
	private int _originOffsetY;
	private int _originOffsetZ;

	private List<BlockPos> _harvestedBlocks;
	private int _currentBlock;
	private boolean _usesBlocks;
	private boolean _upgradeVertical;

	private int _upgradeLevel;
	private float _upgradeModifier;

	public HarvestAreaManager(T owner, int harvestRadius,
			int harvestAreaUp, int harvestAreaDown, float upgradeModifier, boolean usesBlocks)
	{
		_owner = owner;
		_overrideDirection = null;
		_radius = harvestRadius;
		_areaUp = harvestAreaUp;
		_areaDown = harvestAreaDown;
		_upgradeModifier = upgradeModifier;

		_originX = owner.getPos().getX();
		_originY = owner.getPos().getY();
		_originZ = owner.getPos().getZ();
		_originOrientation = owner.getDirectionFacing();
		_usesBlocks = usesBlocks;
		_upgradeVertical = false;
	}

	@Override
	public String toString()
	{
		return String.format("%s-> %s:%s:%s:%s", _owner, _upgradeLevel, _usesBlocks, _upgradeVertical, _upgradeModifier);
	}

	public void setOriginOffset(BlockPos pos)
	{
		_originOffsetX = pos.getX();
		_originOffsetY = pos.getY();
		_originOffsetZ = pos.getZ();
		checkRecalculate();
	}

	public Area getHarvestArea()
	{
		checkRecalculate();
		return _harvestArea;
	}

	public int getOriginX()
	{
		return _originX;
	}

	public int getOriginY()
	{
		return _originY;
	}

	public int getOriginZ()
	{
		return _originZ;
	}

	public int getRadius()
	{
		return _radius + _upgradeLevel;
	}

	public BlockPos getNextBlock()
	{
		checkRecalculate();
		BlockPos next = _harvestedBlocks.get(_currentBlock);
		_currentBlock++;
		if(_currentBlock >= _harvestedBlocks.size())
		{
			_currentBlock = 0;
		}

		return next;
	}

	public void rewindBlock()
	{
		_currentBlock--;
		if(_currentBlock < 0)
		{
			_currentBlock = _harvestedBlocks.size() - 1;
		}
	}

	public void setPosition(int position) {

		_currentBlock = position % _harvestedBlocks.size();
	}

	public int getPosition() {

		return _currentBlock;
	}

	public void setOverrideDirection(EnumFacing dir)
	{
		_overrideDirection = dir;
	}

	public void setUpgradeVertical(boolean val)
	{
		_upgradeVertical = val;
	}

	public void setUpgradeLevel(int level)
	{
		_upgradeLevel = level;
		recalculateArea();
	}

	public void setAreaUp(int amt)
	{
		_areaUp = amt;
	}

	public void setAreaDown(int amt)
	{
		_areaDown = amt;
	}

	public int getUpgradeLevel()
	{
		return _upgradeLevel;
	}

	public void updateUpgradeLevel(@Nonnull ItemStack stack)
	{
		if (stack.isEmpty())
		{
			if (_upgradeLevel != 0)
				setUpgradeLevel(0);
			return;
		}

		int newUpgradeLevel = 0;
		if (stack.getItem() instanceof IAugmentItem)
		{
			IAugmentItem upgrade = (IAugmentItem)stack.getItem();
			int r = "radius".equals(upgrade.getAugmentIdentifier(stack)) ? ((ItemUpgrade)upgrade).getAugmentLevel(stack, "radius") : 0;
			if (r != 0)
				newUpgradeLevel = (int)(r * _upgradeModifier);
		}

		if(newUpgradeLevel != _upgradeLevel)
			setUpgradeLevel(newUpgradeLevel);
	}

	private void checkRecalculate()
	{
		if(_harvestArea == null)
		{
			recalculateArea();
			return;
		}

		if(		(_overrideDirection != null && _originOrientation != _overrideDirection)
				|| (_overrideDirection == null && _originOrientation != _owner.getDirectionFacing())
				|| _originX != _owner.getPos().getX() + _originOffsetX
				|| _originY != _owner.getPos().getY() + _originOffsetY
				|| _originZ != _owner.getPos().getZ() + _originOffsetZ)
		{
			recalculateArea();
		}
	}

	private void recalculateArea()
	{
		BlockPos ourPos = _owner.getPos();
		EnumFacing facing = _owner.getDirectionFacing();
		if (_overrideDirection != null)
		{
			facing = _overrideDirection;
		}

		_originX = ourPos.getX() + _originOffsetX;
		_originY = ourPos.getY() + _originOffsetY;
		_originZ = ourPos.getZ() + _originOffsetZ;
		_originOrientation = facing;

		int radius = _radius + _upgradeLevel;
		int areaUp = _areaUp;
		int areaDown = _areaDown;

		if (facing == EnumFacing.UP || facing == EnumFacing.DOWN)
		{
			if (_upgradeVertical)
				if (facing == EnumFacing.UP)
					areaUp += _upgradeLevel * 2;
				else
					areaDown += _upgradeLevel * 2;
			ourPos = ourPos.offset(facing);
		}
		else
		{
			ourPos = ourPos.offset(facing, radius + 1);
		}

		ourPos = new BlockPos(ourPos.getX() + _originOffsetX, ourPos.getY() + _originOffsetY, ourPos.getZ() + _originOffsetZ);

		_harvestArea = new Area(ourPos, radius, areaDown, areaUp);
		if (_usesBlocks)
			_harvestedBlocks = _harvestArea.getPositionsBottomFirst();
		_currentBlock = 0;
	}
}
