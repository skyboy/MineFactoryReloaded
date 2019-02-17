package powercrystals.minefactoryreloaded.tile.machine.animals;

import cofh.core.fluid.FluidTankCore;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.api.mob.IFactoryRanchable;
import powercrystals.minefactoryreloaded.api.mob.RanchedItem;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryInventory;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryPowered;
import powercrystals.minefactoryreloaded.gui.container.ContainerFactoryPowered;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryPowered;

import javax.annotation.Nonnull;
import java.util.List;

public class TileEntityRancher extends TileEntityFactoryPowered {

	public TileEntityRancher() {

		super(Machine.Rancher);
		setManageSolids(true);
		createEntityHAM(this);
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
	protected boolean shouldPumpLiquid() {

		return true;
	}

	@Override
	public int getWorkMax() {

		return 1;
	}

	@Override
	public int getIdleTicksMax() {

		return 400;
	}

	@Override
	public boolean activateMachine() {

		boolean didDrop = false;

		List<?> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, _areaManager.getHarvestArea().toAxisAlignedBB());

		for (Object o : entities) {
			EntityLivingBase e = (EntityLivingBase) o;
			if (MFRRegistry.getRanchables().containsKey(e.getClass())) {
				IFactoryRanchable r = MFRRegistry.getRanchables().get(e.getClass());
				List<RanchedItem> drops = r.ranch(world, e, this);
				if (drops != null) {
					for (RanchedItem s : drops) {
						if (s.hasFluid()) {
							// whitelist fluids? multiple tanks?
							super.fill(null, (FluidStack) s.getResult(), true);
							didDrop = true;
							continue;
						}

						doDrop((ItemStack) s.getResult());
						didDrop = true;
					}
					if (didDrop) {
						setIdleTicks(20);
						return true;
					}
				}
			}
		}

		setIdleTicks(getIdleTicksMax());
		return false;
	}

	@Override
	public int getSizeInventory() {

		return 9;
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
