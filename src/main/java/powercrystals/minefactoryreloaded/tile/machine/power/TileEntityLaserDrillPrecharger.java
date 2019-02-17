package powercrystals.minefactoryreloaded.tile.machine.power;

import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.EnumFacing;

import powercrystals.minefactoryreloaded.api.laser.IFactoryLaserSource;
import powercrystals.minefactoryreloaded.api.laser.IFactoryLaserTarget;
import powercrystals.minefactoryreloaded.block.BlockFakeLaser;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryInventory;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryPowered;
import powercrystals.minefactoryreloaded.gui.container.ContainerFactoryPowered;
import powercrystals.minefactoryreloaded.setup.MFRThings;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryPowered;
import powercrystals.minefactoryreloaded.tile.machine.processing.TileEntityLaserDrill;

public class TileEntityLaserDrillPrecharger extends TileEntityFactoryPowered implements IFactoryLaserSource {

	public TileEntityLaserDrillPrecharger() {

		super(Machine.LaserDrillPrecharger);
		setCanRotate(true);
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
	public int getSizeInventory() {

		return 0;
	}

	@Override
	protected boolean activateMachine() {

		IFactoryLaserTarget drill = getDrill();
		if (drill == null) {
			setIdleTicks(getIdleTicksMax());
		} else {
			int energy = getActivationEnergy();
			EnumFacing facing = getDirectionFacing().getOpposite();
			if (drill.canFormBeamWith(facing)) {
				stripBlock(false);
				int excess = drill.addEnergy(facing, energy, true);
				if (excess == 0) {
					drill.addEnergy(facing, energy, false);
					return true;
				} else {
					excess = drill.addEnergy(facing, energy, false);
					drainEnergy(energy - excess);
					return false; // energy is manually drained because it's less than activation energy
				}
			} else
				stripBlock(true);
		}
		return false;
	}

	@Override
	public int getWorkMax() {

		return 1;
	}

	@Override
	public int getIdleTicksMax() {

		return 200;
	}

	public boolean shouldDrawBeam() {

		IFactoryLaserTarget drill = getDrill();
		return drill != null && drill.canFormBeamWith(getDirectionFacing().getOpposite());
	}

	protected IFactoryLaserTarget getDrill() {

		BlockPos bp = pos.offset(getDirectionFacing());

		if (!world.isBlockLoaded(bp))
			return null;

		if (!TileEntityLaserDrill.canReplaceBlock(world.getBlockState(bp).getBlock(),
				world, bp))
			return null;

		bp = bp.offset(getDirectionFacing());

		TileEntity te = world.getTileEntity(bp);
		if (te instanceof IFactoryLaserTarget)
			return ((IFactoryLaserTarget) te);

		return null;
	}

	private int stripTick = 0;

	protected void stripBlock(boolean set) {

		if (stripTick > 0) {
			--stripTick;
			return;
		}
		stripTick = 20;

		EnumFacing facing = getDirectionFacing();
		BlockPos laserPos = pos.offset(facing);
		if (set == world.getBlockState(laserPos).getBlock().equals(MFRThings.fakeLaserBlock))
			world.setBlockState(laserPos, MFRThings.fakeLaserBlock.getDefaultState().withProperty(BlockFakeLaser.FACING, facing.getOpposite()), 3);
		else if (set)
			world.scheduleBlockUpdate(laserPos, MFRThings.fakeLaserBlock, 1, 1);
	}

	@Override
	public boolean canFormBeamFrom(EnumFacing from) {

		if (from != getDirectionFacing().getOpposite())
			return false;
		IFactoryLaserTarget drill = getDrill();
		return drill != null && drill.canFormBeamWith(getDirectionFacing().getOpposite());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() {

		return INFINITE_EXTENT_AABB;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public double getMaxRenderDistanceSquared() {

		return 65536;
	}

}
