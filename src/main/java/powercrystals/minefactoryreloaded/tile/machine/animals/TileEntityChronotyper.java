package powercrystals.minefactoryreloaded.tile.machine.animals;

import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.NBTTagCompound;

import powercrystals.minefactoryreloaded.gui.client.GuiChronotyper;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryInventory;
import powercrystals.minefactoryreloaded.gui.container.ContainerChronotyper;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryPowered;

public class TileEntityChronotyper extends TileEntityFactoryPowered {

	private boolean _moveOld;

	public TileEntityChronotyper() {

		super(Machine.Chronotyper);
		createEntityHAM(this);
		setCanRotate(true);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiFactoryInventory getGui(InventoryPlayer inventoryPlayer) {

		return new GuiChronotyper(getContainer(inventoryPlayer), this);
	}

	@Override
	public ContainerChronotyper getContainer(InventoryPlayer inventoryPlayer) {

		return new ContainerChronotyper(this, inventoryPlayer);
	}

	@Override
	public int getSizeInventory() {

		return 0;
	}

	@Override
	protected boolean activateMachine() {

		List<EntityAgeable> entities = world.getEntitiesWithinAABB(EntityAgeable.class, _areaManager.getHarvestArea().toAxisAlignedBB());

		for (EntityAgeable a : entities) {
			if ((a.getGrowingAge() < 0 && !_moveOld) || (a.getGrowingAge() >= 0 && _moveOld)) {
				BlockPos bp = pos.offset(getDirectionFacing().getOpposite());
				a.setPosition(bp.getX() + 0.5, bp.getY() + 0.5, bp.getZ() + 0.5);
				// TODO: take entity size into account so they don't clip inside the chronotyper

				return true;
			}
		}
		setIdleTicks(getIdleTicksMax());
		return false;
	}

	public boolean getMoveOld() {

		return _moveOld;
	}

	public void setMoveOld(boolean moveOld) {

		_moveOld = moveOld;
	}

	@Override
	public int getWorkMax() {

		return 1;
	}

	@Override
	public int getIdleTicksMax() {

		return 200;
	}

	@Override
	public void writePortableData(EntityPlayer player, NBTTagCompound tag) {

		tag.setBoolean("moveOld", _moveOld);
	}

	@Override
	public void readPortableData(EntityPlayer player, NBTTagCompound tag) {

		_moveOld = tag.getBoolean("moveOld");
	}

	@Override
	public void writeItemNBT(NBTTagCompound tag) {

		super.writeItemNBT(tag);

		if (_moveOld)
			tag.setBoolean("moveOld", _moveOld);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {

		super.readFromNBT(tag);
		_moveOld = tag.getBoolean("moveOld");
	}

}
