package powercrystals.minefactoryreloaded.gui.container;

import cofh.core.gui.container.ContainerCore;
import cofh.core.util.CoreUtils;
import cofh.core.util.helpers.InventoryHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryInventory;

import java.util.Map;

public class ContainerFactoryInventory extends ContainerCore {

	protected TileEntityFactoryInventory _te;

	private int _tankAmount;
	private int _tankIndex;
	public boolean drops, redstone;

	public ContainerFactoryInventory(TileEntityFactoryInventory tileentity, InventoryPlayer inv) {

		_te = tileentity;
		if (_te.getSizeInventory() > 0) {
			addSlots();
		}
		bindPlayerInventory(inv);
	}

	protected void addSlots() {

		IItemHandler handler = InventoryHelper.getItemHandlerCap(_te, null);
		addSlotToContainer(new SlotItemHandler(handler, 0, 8, 15));
		addSlotToContainer(new SlotItemHandler(handler, 1, 26, 15));
		addSlotToContainer(new SlotItemHandler(handler, 2, 44, 15));
		addSlotToContainer(new SlotItemHandler(handler, 3, 8, 33));
		addSlotToContainer(new SlotItemHandler(handler, 4, 26, 33));
		addSlotToContainer(new SlotItemHandler(handler, 5, 44, 33));
		addSlotToContainer(new SlotItemHandler(handler, 6, 8, 51));
		addSlotToContainer(new SlotItemHandler(handler, 7, 26, 51));
		addSlotToContainer(new SlotItemHandler(handler, 8, 44, 51));
	}

	@Override
	public void detectAndSendChanges() {

		super.detectAndSendChanges();

		FluidTankInfo[] tank = _te.getTankInfo();
		int n = tank.length;
		for (IContainerListener listener : listeners) {
			listener.sendWindowProperty(this, 33, (_te.hasDrops() ? 1 : 0) |
					(CoreUtils.isRedstonePowered(_te) ? 2 : 0));
			for (int j = n; j-- > 0; ) {
				listener.sendWindowProperty(this, 30, j);
				if (tank[j] != null && tank[j].fluid != null) {
					listener.sendWindowProperty(this, 31, tank[j].fluid.amount);
					listener
							.sendWindowProperty(this, 32, FluidRegistry.getRegisteredFluidIDs().get(tank[j].fluid.getFluid()));
				} else if (tank[j] != null) {
					listener.sendWindowProperty(this, 31, 0);
					listener.sendWindowProperty(this, 32, 0);
				}
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int var, int value) {

		super.updateProgressBar(var, value);

		if (var == 30)
			_tankIndex = value;
		else if (var == 31)
			_tankAmount = value;
		else if (var == 32) {
			Fluid fluid = null;
			for (Map.Entry<Fluid, Integer> entry : FluidRegistry.getRegisteredFluidIDs().entrySet()) {
				if (entry.getValue() == value) {
					fluid = entry.getKey();
				}
			}

			if (fluid == null) {
				_te.getTanks()[_tankIndex].setFluid(null);
			} else {
				_te.getTanks()[_tankIndex].setFluid(new FluidStack(fluid, _tankAmount));
			}
		} else if (var == 33) {
			drops = (value & 1) != 0;
			redstone = (value & 2) != 0;
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {

		return !_te.isInvalid() && _te.isUsableByPlayer(player);
	}

	@Override
	protected int getSizeInventory() {

		return _te.getSizeInventory();
	}

	@Override
	protected int getPlayerInventoryVerticalOffset() {

		return 84;
	}

}
