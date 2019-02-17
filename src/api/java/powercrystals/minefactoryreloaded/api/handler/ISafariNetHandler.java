package powercrystals.minefactoryreloaded.api.handler;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Defines an object that can display information about a captured mob in a
 * Safari net.
 *
 * @author PowerCrystals
 */
public interface ISafariNetHandler {

	/**
	 * @return The class of mob that this handler applies to.
	 */
	Class<?> validFor();

	/**
	 * Called to add information regarding a mob contained in a SafariNet.
	 *
	 * @param safariNetEntity
	 * 		The Entity a Safari Net is requesting information for.
	 * @param world
	 * 		World reference.
	 * @param infoList
	 * 		The current list of information strings. Add yours to this.
	 * @param tooltipFlag
	 * 		Normal or Advanced tooltip.
	 */
	@SideOnly(Side.CLIENT) // TODO: verify ITooltipFlag is client-only post 1.12.2
	void addInformation(@Nonnull NBTTagCompound safariNetEntity, World world, List<String> infoList, ITooltipFlag tooltipFlag);

}
