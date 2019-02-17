package powercrystals.minefactoryreloaded.block;

import cofh.core.util.helpers.StringHelper;
import cofh.redstoneflux.api.IEnergyContainerItem;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import powercrystals.minefactoryreloaded.setup.Machine;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemBlockFactoryMachine extends ItemBlockFactory implements IEnergyContainerItem {

	private int _machineBlockIndex;

	public ItemBlockFactoryMachine(net.minecraft.block.Block blockId) {

		super(blockId);
		setMaxDamage(0);
		setHasSubtypes(true);

		_machineBlockIndex = ((BlockFactoryMachine) blockId).getBlockIndex();
		int highestMeta = Machine.getHighestMetadata(_machineBlockIndex);
		String[] names = new String[highestMeta + 1];
		for (int i = 0; i <= highestMeta; i++) {
			names[i] = Machine.getMachineFromIndex(_machineBlockIndex, i).getInternalName();
		}
		setNames(names);
	}

	@Override
	public String getUnlocalizedName(@Nonnull ItemStack stack) {

		return _names[Math.min(stack.getItemDamage(), _names.length - 1)];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag tooltipFlag) {

		Machine machine = getMachine(stack);
		if (!machine.hasTooltip(stack))
			return;
		if (StringHelper.displayShiftForDetail && !StringHelper.isShiftKeyDown()) {
			tooltip.add(StringHelper.shiftForDetails());
		} else {
			machine.addInformation(stack, world, tooltip, tooltipFlag);
		}
	}

	private Machine getMachine(@Nonnull ItemStack stack) {

		return Machine.getMachineFromIndex(_machineBlockIndex, stack.getItemDamage());
	}

	// TE methods

	private int getTransferRate(@Nonnull ItemStack container) {

		if (container.getCount() != 1)
			return 0;
		return getMachine(container).getActivationEnergy();
	}

	private void setEnergy(@Nonnull ItemStack container, int newEnergy) {

		NBTTagCompound tag = container.getTagCompound();
		if (tag == null) container.setTagCompound(tag = new NBTTagCompound());
		tag.setInteger("energyStored", newEnergy);
	}

	@Override
	public int receiveEnergy(@Nonnull ItemStack container, int maxReceive, boolean simulate) {

		maxReceive = Math.min(getTransferRate(container), maxReceive);
		if (maxReceive <= 0)
			return 0;
		int energy = getEnergyStored(container);
		int maxEnergy = getMaxEnergyStored(container);
		int newEnergy = Math.max(0, Math.min(maxEnergy, energy + maxReceive));
		int received = newEnergy - energy;
		if (!simulate & received > 0)
			setEnergy(container, newEnergy);
		return received;
	}

	@Override
	public int extractEnergy(@Nonnull ItemStack container, int maxExtract, boolean simulate) {

		maxExtract = Math.min(getTransferRate(container), maxExtract);
		if (maxExtract <= 0)
			return 0;
		int energy = getEnergyStored(container);
		int newEnergy = Math.max(0, energy - maxExtract);
		int removed = energy - newEnergy;
		if (!simulate & removed > 0)
			setEnergy(container, newEnergy);
		return removed;
	}

	@Override
	public int getEnergyStored(@Nonnull ItemStack container) {

		if (container.hasTagCompound())
			return container.getTagCompound().getInteger("energyStored");
		return 0;
	}

	@Override
	public int getMaxEnergyStored(@Nonnull ItemStack container) {

		return getMachine(container).getMaxEnergyStorage();
	}
}
