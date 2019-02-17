package powercrystals.minefactoryreloaded.tile.base;

import cofh.core.fluid.FluidTankCore;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryInventory;
import powercrystals.minefactoryreloaded.gui.client.GuiLiquidGenerator;
import powercrystals.minefactoryreloaded.gui.container.ContainerFactoryGenerator;
import powercrystals.minefactoryreloaded.setup.Machine;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Locale;

public abstract class TileEntityLiquidGenerator extends TileEntityFactoryGenerator {

	private int _liquidConsumedPerTick;
	private int _powerProducedPerConsumption;

	public TileEntityLiquidGenerator(Machine machine, int liquidConsumedPerTick,
			int ticksBetweenConsumption) {

		this(machine, liquidConsumedPerTick,
				machine.getActivationEnergy() * ticksBetweenConsumption,
				ticksBetweenConsumption);
	}

	public TileEntityLiquidGenerator(Machine machine, int liquidConsumedPerTick,
			int powerProducedPerConsumption, int ticksBetweenConsumption) {

		super(machine, ticksBetweenConsumption);
		_powerProducedPerConsumption = powerProducedPerConsumption;
		_liquidConsumedPerTick = liquidConsumedPerTick;
	}

	@Override
	protected boolean consumeFuel() {

		int drained = drain(_liquidConsumedPerTick, false, _tanks[0]);

		if (drained != _liquidConsumedPerTick)
			return false;

		drain(_liquidConsumedPerTick, true, _tanks[0]);
		return true;
	}

	@Override
	protected boolean hasFuel() {

		return _tanks[0].getFluidAmount() != 0;
	}

	@Override
	protected boolean canConsumeFuel(int space) {

		return space >= _powerProducedPerConsumption;
	}

	@Override
	protected int produceEnergy() {

		return _powerProducedPerConsumption;
	}

	protected abstract boolean isFluidFuel(FluidStack fuel);

	@Override
	protected FluidTankCore[] createTanks() {

		return new FluidTankCore[] {new FluidTankCore(BUCKET_VOLUME * 4)};
	}

	protected String getFluidName(FluidStack fluid) {

		if (fluid == null || fluid.getFluid() == null)
			return null;
		String name = fluid.getFluid().getName();
		if (name == null)
			return null;
		return name.trim().toLowerCase(Locale.US);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiFactoryInventory getGui(InventoryPlayer inventoryPlayer) {

		return new GuiLiquidGenerator(getContainer(inventoryPlayer), this);
	}

	@Override
	public ContainerFactoryGenerator getContainer(InventoryPlayer inventoryPlayer) {

		return new ContainerFactoryGenerator(this, inventoryPlayer);
	}

	@Override
	public String getGuiBackground() {

		return "fluidgenerator";
	}

	@Override
	public int fill(EnumFacing facing, FluidStack resource, boolean doFill) {

		if (resource != null && isFluidFuel(resource))
			for (FluidTankCore tank : getTanks())
				if (tank.getFluidAmount() == 0 || resource.isFluidEqual(tank.getFluid()))
					return tank.fill(resource, doFill);
		return 0;
	}

	@Override
	public boolean allowBucketFill(EnumFacing facing, @Nonnull ItemStack stack) {

		return true;
	}

	@Override
	protected boolean canDrainTank(EnumFacing facing, int index) {

		return false;
	}

	@Nullable
	@Override
	public FluidStack drain(EnumFacing facing, FluidStack resource, boolean doDrain) {

		return null;
	}

	@Nullable
	@Override
	public FluidStack drain(EnumFacing facing, int maxDrain, boolean doDrain) {

		return null;
	}

}
