package powercrystals.minefactoryreloaded.farmables.ranchables;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import powercrystals.minefactoryreloaded.api.mob.IFactoryRanchable;
import powercrystals.minefactoryreloaded.api.mob.RanchedItem;

import java.util.ArrayList;
import java.util.List;

public class RanchableSquid implements IFactoryRanchable {

	@Override
	public Class<? extends EntityLivingBase> getRanchableEntity() {

		return EntitySquid.class;
	}

	@Override
	public List<RanchedItem> ranch(World world, EntityLivingBase entity, IInventory rancher) {

		NBTTagCompound tag = entity.getEntityData();
		if (tag.getLong("mfr:lastRanched") > world.getTotalWorldTime())
			return null;
		tag.setLong("mfr:lastRanched", world.getTotalWorldTime() + 20 * 15);

		List<RanchedItem> drops = new ArrayList<>();
		drops.add(new RanchedItem(Items.DYE, 1, EnumDyeColor.BLACK.getDyeDamage()));
		return drops;
	}

}
