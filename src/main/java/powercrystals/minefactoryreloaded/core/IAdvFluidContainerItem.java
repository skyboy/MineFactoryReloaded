package powercrystals.minefactoryreloaded.core;

import net.minecraftforge.fluids.capability.IFluidHandlerItem;

public interface IAdvFluidContainerItem extends IFluidHandlerItem
{
	boolean canBeFilledFromWorld();
	boolean canPlaceInWorld();
	boolean shouldReplaceWhenFilled();
}
