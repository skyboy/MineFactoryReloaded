package powercrystals.minefactoryreloaded.tile.tank;

import cofh.core.fluid.FluidTankCore;
import com.google.common.base.Throwables;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import java.util.Arrays;

public class FluidTankMulti implements IFluidTank, IFluidHandler {

	FluidTankCore[] tanks = new FluidTankCore[2];
	int length, index;
	private FluidStack fluid = null;
	private TankNetwork grid;
	private IFluidTankProperties[] tankProperties;

	public FluidTankMulti(TankNetwork network) {
		grid = network;
	}

	public void addTank(FluidTankCore tank) {
		if (tank == null)
			throw new IllegalArgumentException("null");
		for (int i = length; i --> 0; )
			if (tanks[i] == tank)
				return;

		if (++length >= tanks.length) {
			FluidTankCore[] old = tanks;
			tanks = new FluidTankCore[length * 2];
			System.arraycopy(old, 0, tanks, 0, length - 1);
		}
		tanks[length - 1] = tank;
		fill(tank.drain(tank.getCapacity(), true), true);
	}

	public void removeTank(FluidTankCore tank) {
		if (tank == null)
			throw new IllegalArgumentException("null");
		int i = length;
		while (i --> 0) if (tanks[i] == tank) break;
		if (i < 0) return;

		{
			FluidTankCore[] old = tanks;
			if (--length != i) {
				System.arraycopy(old, i + 1, old, i, length - i + 1);
			}
			if (length <= old.length / 4) {
				tanks = new FluidTankCore[old.length / 2];
				System.arraycopy(old, 0, tanks, 0, tanks.length);
			}
		}

		if (index >= i && index > 0) --index;

		FluidStack r = tank.getFluid();
		if (r != null) {
			fluid.amount -= r.amount;
			if (fluid.amount <= 0) {
				fluid = null;
				grid.updateNodes();
			}
		}

		tanks[length] = null;
		tank.setFluid(r);
	}

	public void empty() {
		for (int i = length; i --> 0;)
			tanks[i] = null;
		length = 0;
		index = 0;
		fluid = null;
	}

	@Override
	public FluidStack getFluid() {
		return fluid;
	}

	@Override
	public int getFluidAmount() {
		return fluid == null ? 0 : fluid.amount;
	}

	@Override
	public int getCapacity() {
		return length * TileEntityTank.CAPACITY;
	}

	@Override
	public FluidTankInfo getInfo() {
		return new FluidTankInfo(this);
	}

	@Override
	public IFluidTankProperties[] getTankProperties() {

		return new IFluidTankProperties[] { new FluidTankProperties(getFluid(), getCapacity()) }; //TODO cache and update value
	}

	@Override
	public int fill(FluidStack resource, boolean doFill) {
		try {
		int f = 0;
		if (resource != null && (fluid == null || fluid.isFluidEqual(resource))) {
			int i = index;
			while (i < length) {
				f += tanks[i].fill(resource, doFill);
				if (f >= resource.amount)
					break;
				++i;
			}
			if (i == length)
				 --i;
			if (doFill) {
				index = i;
				boolean u = false;
				if (fluid == null) {
					u = true;
					fluid = new FluidStack(resource, 0);
				}
				fluid.amount += f;
				if (u)
					grid.updateNodes();
			}
		}
		return f;
		} catch (Throwable t) {
			System.out.format("%s, ", Arrays.toString(tanks));
			System.out.format("index: %s, length: %s, tanks.length: %s, grid: %s\n", index, length, tanks.length, grid);
			throw Throwables.propagate(t);
		}
	}

	@Override
	public FluidStack drain(int maxDrain, boolean doDrain) {
		try {
		if (fluid != null) {
			FluidStack r = new FluidStack(fluid, 0);
			int i = index;
			while (i >= 0) {
				int c = tanks[i].getFluidAmount();
				FluidStack d = tanks[i].drain(maxDrain, doDrain);
				if (d != null) {
					r.amount += d.amount;
					maxDrain -= d.amount;
					if (c == d.amount)
						--i;
				}
				if (d == null || maxDrain <= 0)
					break;
			}
			if (i == -1)
				 ++i;
			if (doDrain) {
				index = i;
				fluid.amount -= r.amount;
				if (fluid.amount <= 0) {
					fluid = null;
					grid.updateNodes();
				}
			}
			return r;
		}
		} catch (Throwable t) {
			System.out.format("%s, ", Arrays.toString(tanks));
			System.out.format("index: %s, length: %s, tanks.length: %s, grid: %s\n", index, length, tanks.length, grid);
			throw Throwables.propagate(t);
		}
		return null;
	}

	public FluidStack drain(FluidStack resource, boolean doDrain) {
		if (fluid != null && fluid.isFluidEqual(resource)) {
			return drain(resource.amount, doDrain);
		}
		return null;
	}

}
