package powercrystals.minefactoryreloaded.net;

import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityMinecartMobSpawner;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.item.ItemExpireEvent;
import net.minecraftforge.event.entity.minecart.MinecartInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import powercrystals.minefactoryreloaded.item.ItemPortaSpawner;
import powercrystals.minefactoryreloaded.setup.MFRConfig;

import javax.annotation.Nonnull;

import static powercrystals.minefactoryreloaded.setup.MFRThings.portaSpawnerItem;
import static powercrystals.minefactoryreloaded.setup.MFRThings.rubberLeavesItem;

public class EntityHandler {

	@SubscribeEvent
	public void onEntityJoinWorldEvent(EntityJoinWorldEvent evt) {

		if (evt.getWorld().isRemote || !(evt.getEntity() instanceof EntitySkeleton))
			return;
	}

	@SubscribeEvent
	public void onMinecartInteract(MinecartInteractEvent e) {

		if (e.getPlayer().world.isRemote)
			return;
		if (!MFRConfig.enableSpawnerCarts.getBoolean(true))
			return;

		EntityMinecart minecart = e.getMinecart();
		if (minecart != null && !minecart.isDead) {
			@Nonnull ItemStack item = e.getPlayer().getHeldItem(e.getHand());
			if (!item.isEmpty() && item.getItem().equals(portaSpawnerItem) &
					minecart.getRidingEntity() == null &
					!minecart.isBeingRidden()) {
				if (minecart.getType() == EntityMinecart.Type.RIDEABLE) {
					if (ItemPortaSpawner.hasData(item)) {
						e.setCanceled(true);
						NBTTagCompound tag = ItemPortaSpawner.getSpawnerTag(item);
						e.getPlayer().setHeldItem(e.getHand(), ItemStack.EMPTY);
						minecart.writeToNBT(tag);
						tag.removeTag("UUIDMost");
						tag.removeTag("UUIDLeast");
						minecart.setDead();
						EntityMinecartMobSpawner ent = new EntityMinecartMobSpawner(minecart.world, minecart.getPosition().getX(), minecart.getPosition().getY(), minecart.getPosition().getZ());
						ent.readFromNBT(tag);
						ent.world.spawnEntity(ent);
						ent.world.playEvent(null, 2004, ent.getPosition(), 0); // particles
					}
				}
				else if (e.getMinecart().getType() == EntityMinecart.Type.SPAWNER) {
					// maybe
				}
			}
		}
	}

	@SubscribeEvent
	public void onItemExpire(ItemExpireEvent e) {

		@Nonnull ItemStack stack = e.getEntityItem().getItem();
		if (stack.getItem().equals(rubberLeavesItem) && stack.getItemDamage() == 0) {
			e.setCanceled(true);
			e.setExtraLife(e.getEntityItem().lifespan);
			e.getEntityItem().setItem(new ItemStack(stack.getItem(), stack.getCount(), 1));
		}
	}

}
