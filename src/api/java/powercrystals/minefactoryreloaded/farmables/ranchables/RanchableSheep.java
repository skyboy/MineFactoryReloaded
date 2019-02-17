package powercrystals.minefactoryreloaded.farmables.ranchables;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.world.World;
import powercrystals.minefactoryreloaded.api.mob.IFactoryRanchable;
import powercrystals.minefactoryreloaded.api.mob.RanchedItem;

import java.util.LinkedList;
import java.util.List;

public class RanchableSheep implements IFactoryRanchable {

	@Override
	public Class<? extends EntityLivingBase> getRanchableEntity() {

		return EntitySheep.class;
	}

	@Override
	public List<RanchedItem> ranch(World world, EntityLivingBase entity, IInventory rancher) {

		EntitySheep s = (EntitySheep) entity;

		if (s.getSheared() || s.getGrowingAge() < 0) {
			return null;
		}

		List<RanchedItem> stacks = new LinkedList<>();
		stacks.add(new RanchedItem(Blocks.WOOL, 1, s.getFleeceColor().getMetadata()));
		s.setSheared(true);

		return stacks;
	}

}
