package powercrystals.minefactoryreloaded.core.safarinethandlers;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import powercrystals.minefactoryreloaded.api.handler.ISafariNetHandler;

import javax.annotation.Nonnull;
import java.util.List;

public class SlimeHandler implements ISafariNetHandler {

	@Override
	public Class<?> validFor() {

		return EntitySlime.class;
	}

	private static double log2 = Math.log(2);
	private static String[] sizes = { "Tiny", "Medium", "Large", "Extra Large", "Massive", "Incomprehensible" };

	@Override
	public void addInformation(@Nonnull NBTTagCompound safariNetEntity, World world, List<String> infoList, ITooltipFlag tooltipFlag) {

		int index = (int) Math.round(Math.log1p(safariNetEntity.getInteger("Size")) / log2);
		infoList.add("Size: " + sizes[Math.min(index, sizes.length - 1)]);
	}

}
