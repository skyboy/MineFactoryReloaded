package powercrystals.minefactoryreloaded.modhelpers.vanilla.handlers.ranchables;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.IItemHandler;
import powercrystals.minefactoryreloaded.api.mob.IFactoryRanchable;
import powercrystals.minefactoryreloaded.api.mob.RanchedItem;
import powercrystals.minefactoryreloaded.core.UtilInventory;

import java.util.LinkedList;
import java.util.List;

public class RanchableCow implements IFactoryRanchable {

	@Override
	public Class<? extends EntityLivingBase> getRanchableEntity() {

		return EntityCow.class;
	}

	@Override
	public List<RanchedItem> ranch(World world, EntityLivingBase entity, IInventory rancher) {

		NBTTagCompound tag = entity.getEntityData();
		if (tag.getLong("mfr:lastRanched") > world.getTotalWorldTime())
			return null;
		tag.setLong("mfr:lastRanched", world.getTotalWorldTime() + 20 * 30);

		List<RanchedItem> drops = new LinkedList<>();

		IItemHandler handler = UtilInventory.getItemHandlerCap(rancher, EnumFacing.UP);

		if (!UtilInventory.extractItem(handler, new ItemStack(Items.BUCKET), false).isEmpty()) {
			drops.add(new RanchedItem(Items.MILK_BUCKET));
		} else {
			FluidStack milk = FluidRegistry.getFluidStack("milk", Fluid.BUCKET_VOLUME);
			drops.add(new RanchedItem(milk));
		}

		return drops;
	}

}
