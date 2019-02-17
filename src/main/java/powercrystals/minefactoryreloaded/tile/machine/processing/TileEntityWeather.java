package powercrystals.minefactoryreloaded.tile.machine.processing;

import cofh.core.fluid.FluidTankCore;
import cofh.core.util.helpers.BlockHelper;
import cofh.core.util.helpers.FluidHelper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryInventory;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryPowered;
import powercrystals.minefactoryreloaded.gui.container.ContainerFactoryPowered;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryPowered;

import javax.annotation.Nonnull;

import static net.minecraftforge.fluids.FluidRegistry.WATER;

public class TileEntityWeather extends TileEntityFactoryPowered {

	protected int _canSeeSky = 0;
	protected boolean _canWeather = false, _willSnow = false, _openSky = false;
	protected Biome _biome = null;

	public TileEntityWeather() {

		super(Machine.WeatherCollector);
		setManageSolids(true);
		_tanks[0].setLock(WATER);
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

	@Override
	public int getWorkMax() {

		return 50;
	}

	@Override
	public int getIdleTicksMax() {

		return 600;
	}

	@Override
	public boolean activateMachine() {

		if (world.getWorldInfo().isRaining()) {
			l:
			{
				Biome bgb = world.getBiome(pos);

				if (_canWeather && _biome == bgb)
					break l;
				_biome = bgb;
				if (!bgb.canRain() && !bgb.getEnableSnow()) {
					_canWeather = false;
					setIdleTicks(getIdleTicksMax());
					return false;
				}
				_canWeather = true;
				_willSnow = bgb.getTemperature(pos) < 0.15F;
			}
			if (!canSeeSky()) {
				setIdleTicks(getIdleTicksMax());
				return false;
			}
			if (!incrementWorkDone())
				return false;
			if (getWorkDone() >= getWorkMax()) {
				if (!_willSnow) {
					if (_tanks[0].getSpace() >= BUCKET_VOLUME &&
							_tanks[0].fill(FluidHelper.WATER, true) > 0) {
						setWorkDone(0);
						setIdleTicks(3);
						return true;
					} else {
						setWorkDone(getWorkMax());
						setIdleTicks(10);
						return false;
					}
				} else {
					doDrop(new ItemStack(Items.SNOWBALL, 3));
					setWorkDone(0);
					setIdleTicks(1);
				}
			}
			return true;
		}
		setIdleTicks(getIdleTicksMax());
		return false;
	}

	@Override
	public EnumFacing getDropDirection() {

		return EnumFacing.DOWN;
	}

	private boolean canSeeSky() {

		if (--_canSeeSky > 0)
			return _openSky;
		_canSeeSky = 70;
		int h = BlockHelper.getHighestY(world, pos.getX(), pos.getZ());
		_openSky = true;
		for (int y = pos.getY() + 1; y < h; y++) {
			BlockPos offsetPos = new BlockPos(pos.getX(), y, pos.getZ());
			IBlockState state = world.getBlockState(offsetPos);
			Block block = state.getBlock();
			if (block.getCollisionBoundingBox(state, world, offsetPos) == null)
				continue;
			_openSky = false;
			break;
		}
		return _openSky;
	}

	@Override
	public int getSizeInventory() {

		return 0;
	}

	@Override
	public boolean shouldPumpLiquid() {

		return true;
	}

	@Override
	protected FluidTankCore[] createTanks() {

		return new FluidTankCore[] { new FluidTankCore(4 * BUCKET_VOLUME) };
	}

	@Override
	public boolean allowBucketDrain(EnumFacing facing, @Nonnull ItemStack stack) {

		return true;
	}

	@Override
	protected boolean canFillTank(EnumFacing facing, int index) {

		return false;
	}

	@Override
	public int fill(EnumFacing facing, FluidStack resource, boolean doFill) {

		return 0;
	}

}
