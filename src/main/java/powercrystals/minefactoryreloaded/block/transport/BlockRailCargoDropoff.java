package powercrystals.minefactoryreloaded.block.transport;

import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import powercrystals.minefactoryreloaded.core.UtilInventory;

import javax.annotation.Nonnull;

public class BlockRailCargoDropoff extends BlockFactoryRail {

	public BlockRailCargoDropoff() {

		super(true, false);
		setUnlocalizedName("mfr.rail.cargo.dropoff");
	}

	@Override
	public void onMinecartPass(World world, EntityMinecart entity, BlockPos pos) {

		if (world.isRemote || !(entity instanceof IInventory))
			return;

		IItemHandler cartInventory = entity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);

		for (int slot = 0; slot < cartInventory.getSlots(); slot++) {
			@Nonnull ItemStack slotStack = cartInventory.getStackInSlot(slot);
			if (slotStack.isEmpty()) {
				continue;
			}

			@Nonnull ItemStack remaining = UtilInventory.dropStack(world, pos, slotStack, EnumFacing.values(), null);

			cartInventory.extractItem(slot, slotStack.getCount() - remaining.getCount(), false);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels() {

		registerRailModel(this, "cargo_dropoff");
	}
}
