package powercrystals.minefactoryreloaded.api.rednet;

import net.minecraft.nbt.NBTTagCompound;

// FIXME: documentation
public interface IRedNetLogicCircuit {

	byte getInputCount();

	byte getOutputCount();

	int[] recalculateOutputValues(long worldTime, int[] inputValues);

	String getUnlocalizedName();

	String getInputPinLabel(int pin);

	String getOutputPinLabel(int pin);

	void readFromNBT(NBTTagCompound tag);

	void writeToNBT(NBTTagCompound tag);

}
