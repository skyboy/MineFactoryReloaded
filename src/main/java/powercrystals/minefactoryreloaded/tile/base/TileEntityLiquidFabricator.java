package powercrystals.minefactoryreloaded.tile.base;

import cofh.core.fluid.FluidTankCore;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryInventory;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryPowered;
import powercrystals.minefactoryreloaded.gui.container.ContainerFactoryPowered;
import powercrystals.minefactoryreloaded.setup.Machine;

import javax.annotation.Nonnull;

public abstract class TileEntityLiquidFabricator extends TileEntityFactoryPowered {

	private FluidStack fluid;
	private int _liquidFabPerTick;

	protected TileEntityLiquidFabricator(Fluid liquid, int liquidFabPerTick, Machine machine) {

		super(machine, machine.getActivationEnergy() * liquidFabPerTick);
		_liquidFabPerTick = liquidFabPerTick;
		if (liquid != null)
			fluid = new FluidStack(liquid, liquidFabPerTick);
		else
			fluid = null;
	}

	@Override
	protected boolean activateMachine() {

		if (fluid != null && _tanks[0].getSpace() >= _liquidFabPerTick)
			if (_tanks[0].fill(fluid, true) > 0)
				return true;

		setIdleTicks(getIdleTicksMax());
		return false;
	}

	@Override
	public int getWorkMax() {

		return 0;
	}

	@Override
	public int getIdleTicksMax() {

		return 200;
	}

	@Override
	protected boolean shouldPumpLiquid() {

		return true;
	}

	@Override
	public int getSizeInventory() {

		return 0;
	}

	@Override
	protected FluidTankCore[] createTanks() {

		return new FluidTankCore[] {new FluidTankCore(BUCKET_VOLUME)};
	}

	@Override
	public boolean allowBucketDrain(EnumFacing facing, @Nonnull ItemStack stack) {

		return true;
	}

	@Override
	public int fill(EnumFacing facing, FluidStack resource, boolean doFill) {

		return 0;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiFactoryInventory getGui(InventoryPlayer inventoryPlayer) {

		return new GuiFactoryPowered(getContainer(inventoryPlayer), this);
	}

	@Override
	public ContainerFactoryPowered getContainer(InventoryPlayer inventoryPlayer) {

		return new ContainerFactoryPowered(this, inventoryPlayer);
	}

}
