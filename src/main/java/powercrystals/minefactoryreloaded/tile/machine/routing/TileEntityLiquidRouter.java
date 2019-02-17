package powercrystals.minefactoryreloaded.tile.machine.routing;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import powercrystals.minefactoryreloaded.core.MFRUtil;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryInventory;
import powercrystals.minefactoryreloaded.gui.client.GuiLiquidRouter;
import powercrystals.minefactoryreloaded.gui.container.ContainerLiquidRouter;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryTickable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileEntityLiquidRouter extends TileEntityFactoryTickable {

	//TODO this really needs a rewrite - very confusing that the order here doesn't match EnumFacing
	private static final EnumFacing[] _outputDirections = new EnumFacing[]
			{ EnumFacing.DOWN, EnumFacing.UP, EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.EAST, EnumFacing.WEST };

	protected boolean[] _filledDirection = new boolean[6];

	public TileEntityLiquidRouter() {

		super(Machine.LiquidRouter);
		for (int i = 0; i < 6; i++) {
			_filledDirection[i] = false;
		}
		setManageFluids(true);
	}

	@Override
	public void update() {

		super.update();
		for (int i = 0; i < 6; i++) {
			_filledDirection[i] = false;
		}
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

	@Override
	public IFluidTankProperties[] getTankProperties(EnumFacing facing) {

		return emptyIFluidTankProperties;
	}

	@Override
	public int fill(EnumFacing facing, FluidStack resource, boolean doFill) {

		int tankIndex = facing.ordinal();
		if (tankIndex >= _filledDirection.length || _filledDirection[tankIndex])
			return 0;
		int r = pumpLiquid(resource, doFill);
		_filledDirection[tankIndex] = doFill & r > 0;
		return r;
	}

	private int pumpLiquid(FluidStack resource, boolean doFill) {

		if (resource == null || resource.amount <= 0)
			return 0;

		int amountRemaining = resource.amount;
		int[] routes = getRoutesForLiquid(resource);
		int[] defaultRoutes = getDefaultRoutes();

		if (hasRoutes(routes)) {
			amountRemaining = weightedRouteLiquid(resource, routes, amountRemaining, doFill);
		} else if (hasRoutes(defaultRoutes)) {
			amountRemaining = weightedRouteLiquid(resource, defaultRoutes, amountRemaining, doFill);
		}

		return resource.amount - amountRemaining;
	}

	private int weightedRouteLiquid(FluidStack resource, int[] routes, int amountRemaining, boolean doFill) {

		if (amountRemaining >= totalWeight(routes)) {
			int startingAmount = amountRemaining;
			for (int i = 0; i < routes.length; i++) {
				TileEntity te = MFRUtil.getTile(world, pos.offset(_outputDirections[i]));
				int amountForThisRoute = startingAmount * routes[i] / totalWeight(routes);
				if (te != null && te.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, _outputDirections[i].getOpposite()) && amountForThisRoute > 0) {
					amountRemaining -= te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, _outputDirections[i].getOpposite())
							.fill(new FluidStack(resource, amountForThisRoute), doFill);
					if (amountRemaining <= 0) {
						break;
					}
				}
			}
		}

		if (0 < amountRemaining && amountRemaining < totalWeight(routes)) {
			int outDir = weightedRandomSide(routes);
			TileEntity te = MFRUtil.getTile(world, pos.offset(_outputDirections[outDir]));
			if (te != null && te.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, _outputDirections[outDir].getOpposite())) {
				amountRemaining -= te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, _outputDirections[outDir].getOpposite())
						.fill(new FluidStack(resource, amountRemaining), doFill);
			}
		}

		return amountRemaining;
	}

	private int weightedRandomSide(int[] routeWeights) {

		int random = world.rand.nextInt(totalWeight(routeWeights));
		for (int i = 0; i < routeWeights.length; i++) {
			random -= routeWeights[i];
			if (random < 0) {
				return i;
			}
		}

		return -1;
	}

	private int totalWeight(int[] routeWeights) {

		int total = 0;

		for (int weight : routeWeights) {
			total += weight;
		}
		return total;
	}

	private boolean hasRoutes(int[] routeWeights) {

		for (int weight : routeWeights) {
			if (weight > 0)
				return true;
		}
		return false;
	}

	private int[] getRoutesForLiquid(FluidStack resource) {

		int[] routeWeights = new int[6];

		for (int i = 0; i < 6; i++) {
			ItemStack stack = _inventory.get(i);
			if (resource.isFluidEqual(MFRUtil.getFluidContents(stack))) {
				routeWeights[i] = stack.getCount();
			} else {
				routeWeights[i] = 0;
			}
		}
		return routeWeights;
	}

	private int[] getDefaultRoutes() {

		int[] routeWeights = new int[6];

		for (int i = 0; i < 6; i++) {
			ItemStack stack = _inventory.get(i);
			if (!stack.isEmpty() && MFRUtil.getFluidContents(stack) == null) {
				routeWeights[i] = stack.getCount();
			} else {
				routeWeights[i] = 0;
			}
		}
		return routeWeights;
	}

	@Override
	public void writePortableData(EntityPlayer player, NBTTagCompound tag) {

	}

	@Override
	public void readPortableData(EntityPlayer player, NBTTagCompound tag) {

		// TODO: save/write items
	}

	@Override
	public int getSizeInventory() {

		return 6;
	}

	@Override
	public boolean shouldDropSlotWhenBroken(int slot) {

		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiFactoryInventory getGui(InventoryPlayer inventoryPlayer) {

		return new GuiLiquidRouter(getContainer(inventoryPlayer), this);
	}

	@Override
	public ContainerLiquidRouter getContainer(InventoryPlayer inventoryPlayer) {

		return new ContainerLiquidRouter(this, inventoryPlayer);
	}

	@Override
	public boolean canInsertItem(int slot, @Nonnull ItemStack itemstack, EnumFacing side) {

		return false;
	}

	@Override
	public boolean canExtractItem(int slot, @Nonnull ItemStack itemstack, EnumFacing side) {

		return false;
	}

}
