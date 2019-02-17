package powercrystals.minefactoryreloaded.block.transport;

import cofh.core.util.helpers.InventoryHelper;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import powercrystals.minefactoryreloaded.core.UtilInventory;

import javax.annotation.Nonnull;

public class BlockRailCargoPickup extends BlockFactoryRail {

	public BlockRailCargoPickup() {

		super(true, false);
		setUnlocalizedName("mfr.rail.cargo.pickup");
	}

	@Override
	public void onMinecartPass(World world, EntityMinecart entity, BlockPos pos) {

		if (world.isRemote || !(entity instanceof IInventory))
			return;

		IItemHandler cartInventory = entity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);

		for (IItemHandler chest : UtilInventory.findChests(world, pos)) {
			for (int slot = 0; slot < chest.getSlots(); slot++) {
				ItemStack slotStack = chest.getStackInSlot(slot);
				if (chest.getStackInSlot(slot).isEmpty() || chest.extractItem(slot, slotStack.getCount(), true).isEmpty()) {
					continue;
				}

				@Nonnull ItemStack remaining = InventoryHelper.insertStackIntoInventory(cartInventory, slotStack.copy(), false);

				chest.extractItem(slot, slotStack.getCount() - remaining.getCount(), false);
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels() {

		registerRailModel(this, "cargo_pickup");
	}
}
