package powercrystals.minefactoryreloaded.modhelpers.vanilla.handlers.spawnhandlers;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import powercrystals.minefactoryreloaded.api.handler.IMobSpawnHandler;

public class SpawnableHorse implements IMobSpawnHandler {

	private final Class<? extends AbstractHorse> clazz;

	public SpawnableHorse(Class<? extends AbstractHorse> clazz) {

		this.clazz = clazz;
	}

	@Override
	public Class<? extends EntityLivingBase> getMobClass() {

		return clazz;
	}

	@Override
	public void onMobSpawn(EntityLivingBase entity) {

	}

	@Override
	public void onMobExactSpawn(EntityLivingBase entity) {

		AbstractHorse ent = (AbstractHorse) entity;

		try {
			IItemHandlerModifiable inv = (IItemHandlerModifiable) ent.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
			for (int i = 0, e = inv.getSlots(); i < e; ++i) {
				inv.setStackInSlot(i, ItemStack.EMPTY);
			}
		} catch (Throwable e) {
			e.printStackTrace();
			entity.setDead();
		}
	}

}
