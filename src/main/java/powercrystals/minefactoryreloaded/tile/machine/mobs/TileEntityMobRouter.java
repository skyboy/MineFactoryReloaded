package powercrystals.minefactoryreloaded.tile.machine.mobs;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryInventory;
import powercrystals.minefactoryreloaded.gui.client.GuiMobRouter;
import powercrystals.minefactoryreloaded.gui.container.ContainerMobRouter;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryPowered;

import java.util.List;

import static powercrystals.minefactoryreloaded.item.ItemSafariNet.*;

public class TileEntityMobRouter extends TileEntityFactoryPowered {

	protected int _matchMode;
	protected boolean _blacklist;

	public TileEntityMobRouter() {

		super(Machine.MobRouter);
		createEntityHAM(this);
		setCanRotate(true);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiFactoryInventory getGui(InventoryPlayer inventoryPlayer) {

		return new GuiMobRouter(getContainer(inventoryPlayer), this);
	}

	@Override
	public ContainerMobRouter getContainer(InventoryPlayer inventoryPlayer) {

		return new ContainerMobRouter(this, inventoryPlayer);
	}

	@Override
	protected boolean activateMachine() {

		Class<?> matchClass;
		if (!_inventory.get(0).isEmpty()) {
			if (!isSafariNet(_inventory.get(0)) || isSingleUse(_inventory.get(0)))
				return false;
			matchClass = getEntityClass(_inventory.get(0));
		} else
			matchClass = EntityLivingBase.class;

		List<? extends EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class,
			_areaManager.getHarvestArea().toAxisAlignedBB());
		List<Class<?>> blacklist = MFRRegistry.getSafariNetBlacklist();

		switch (_matchMode) {
		case 3:
			if (matchClass != EntityLivingBase.class)
				matchClass = matchClass.getSuperclass();
		case 2:
			if (matchClass != EntityLivingBase.class)
				matchClass = matchClass.getSuperclass();
		}

		for (EntityLivingBase entity : entities) {
			Class<?> entityClass = entity.getClass();
			if (blacklist.contains(entityClass) || EntityPlayer.class.isAssignableFrom(entityClass))
				continue;
			boolean match;
			switch (_matchMode) {
			case 0:
				match = matchClass == entityClass;
				break;
			case 1:
			case 2:
			case 3:
				match = matchClass.isAssignableFrom(entityClass);
				break;
			default:
				match = false;
			}
			if (match ^ _blacklist) {
				BlockPos bp = pos.offset(getDirectionFacing().getOpposite());
				entity.setPosition(bp.getX() + 0.5, bp.getY() + 0.5, bp.getZ() + 0.5);

				return true;
			}
		}
		setIdleTicks(getIdleTicksMax());
		return false;
	}

	public boolean getWhiteList() {

		return !_blacklist;
	}

	public void setWhiteList(boolean whitelist) {

		_blacklist = !whitelist;
	}

	public int getMatchMode() {

		return _matchMode;
	}

	public void setMatchMode(int matchMode) {

		if (matchMode < 0)
			_matchMode = 3;
		else
			_matchMode = matchMode % 4;
	}

	@Override
	public int getSizeInventory() {

		return 1;
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

		tag.setInteger("mode", _matchMode);
		tag.setBoolean("blacklist", _blacklist);
	}

	@Override
	public void readPortableData(EntityPlayer player, NBTTagCompound tag) {

		setMatchMode(tag.getInteger("mode"));
		_blacklist = tag.getBoolean("blacklist");
	}

	@Override
	public void writeItemNBT(NBTTagCompound tag) {

		super.writeItemNBT(tag);
		if (_matchMode != 0)
			tag.setInteger("mode", _matchMode);
		if (_blacklist)
			tag.setBoolean("blacklist", _blacklist);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {

		super.readFromNBT(tag);
		setMatchMode(tag.getInteger("mode"));
		_blacklist = tag.getBoolean("blacklist");
	}
}
