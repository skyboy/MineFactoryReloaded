package powercrystals.minefactoryreloaded.tile.machine.routing;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import powercrystals.minefactoryreloaded.core.IEntityCollidable;
import powercrystals.minefactoryreloaded.core.MFRUtil;
import powercrystals.minefactoryreloaded.core.UtilInventory;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryInventory;

import javax.annotation.Nonnull;

public class TileEntityCollector extends TileEntityFactoryInventory implements IEntityCollidable {

	private boolean canStuff;

	public TileEntityCollector() {

		super(Machine.ItemCollector);
		setManageSolids(true);
		canStuff = false;
	}

	@Override
	public void onEntityCollided(Entity entity) {

		if (failedDrops == null && entity instanceof EntityItem)
			addToChests((EntityItem) entity);
	}

	private void addToChests(EntityItem i) {

		if (i.isDead)
			return;

		@Nonnull ItemStack s = addToChests(i.getItem());
		if (s.isEmpty()) {
			i.setDead();
			return;
		}
		i.setItem(s);
	}

	@Nonnull
	private ItemStack addToChests(@Nonnull ItemStack s) {

		s = UtilInventory.dropStack(this, s,
				MFRUtil.directionsWithoutConveyors(world, pos), null);
		if (canStuff && failedDrops == null & !s.isEmpty()) {
			doDrop(s);
			s = ItemStack.EMPTY;
		}
		return s;
	}

	@Override
	public boolean hasWorld() {

		return world != null & failedDrops != null;
	}

	@Override
	public int getComparatorOutput() {

		return failedDrops != null ? 15 : 0;
	}

	@Override
	public EnumFacing getDropDirection() {

		return null;
	}

	@Override
	public EnumFacing[] getDropDirections() {

		return MFRUtil.directionsWithoutConveyors(world, pos);
	}

	@Override
	public int getSizeInventory() {

		return 0;
	}

	@Override
	public void writeItemNBT(NBTTagCompound tag) {

		super.writeItemNBT(tag);
		if (canStuff)
			tag.setBoolean("hasTinkerStuff", true);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {

		super.readFromNBT(tag);
		canStuff = tag.getBoolean("hasTinkerStuff");
		setIsActive(canStuff);
	}

}
