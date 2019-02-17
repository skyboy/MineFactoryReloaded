
package powercrystals.minefactoryreloaded.modcompat.backtools;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import powercrystals.minefactoryreloaded.api.integration.IMFRIntegrator;

import static powercrystals.minefactoryreloaded.api.integration.IMFRIntegrator.findItem;
import static powercrystals.minefactoryreloaded.modcompat.Compats.ModIds.BACKTOOLS;
import static powercrystals.minefactoryreloaded.modcompat.Compats.ModIds.MFR;

@IMFRIntegrator.DependsOn(BACKTOOLS)
public class BackTools implements IMFRIntegrator {

	public void load() {

		final Item safariNetLauncherItem = findItem(MFR, "safarinet_launcher");
		final Item spyglassItem = findItem(MFR, "spyglass");
		final Item factoryHammerItem = findItem(MFR, "hammer");

		/*
		 *  orientation is 0-3, and rotates counterclockwise by 90 deg * orientation
		 *  flipped true for vertical flipping of the texture
		 */
		FMLInterModComms.sendMessage(BACKTOOLS, "backtool", new ItemStack(safariNetLauncherItem, 2)); // flip?
		FMLInterModComms.sendMessage(BACKTOOLS, "backtool", new ItemStack(spyglassItem, 1)); // flip?
		FMLInterModComms.sendMessage(BACKTOOLS, "backtool", new ItemStack(factoryHammerItem, 1)); // flip?
	}

}

