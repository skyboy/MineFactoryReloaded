package powercrystals.minefactoryreloaded.world;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.item.ItemStack;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.WorldServer;
import powercrystals.minefactoryreloaded.asmhooks.WorldServerProxy;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryPowered;
import powercrystals.minefactoryreloaded.tile.machine.mobs.TileEntityGrinder;

import javax.annotation.Nonnull;
import java.util.LinkedList;

public class GrindingWorldServer extends WorldServerProxy {

	protected TileEntityFactoryPowered grinder;
	protected boolean allowSpawns;
	protected LinkedList<Entity> entitiesToGrind = new LinkedList<>();

	public GrindingWorldServer(WorldServer world, TileEntityFactoryPowered grinder) {

		this(world, grinder, false);
	}

	public GrindingWorldServer(WorldServer world, TileEntityFactoryPowered grinder, boolean allowSpawns) {

		super(world);
		this.grinder = grinder;
		this.allowSpawns = allowSpawns;
	}

	protected void cofh_updateProps() {

		cofh_updatePropsInternal(this.proxiedWorld);
	}

	public void setAllowSpawns(boolean allow) {

		this.allowSpawns = allow;
	}

	public void setMachine(TileEntityFactoryPowered machine) {

		this.grinder = machine;
	}

	@Override
	public boolean spawnEntity(Entity entity) {

		if (grinder != null) {
			if (entity instanceof EntityItem) {
				if (grinder.manageSolids()) {
					@Nonnull ItemStack drop = ((EntityItem) entity).getItem();
					if (!drop.isEmpty())
						grinder.doDrop(drop);
				}
				entity.setDead();
				return true;
			} else if (entity instanceof EntityXPOrb) {
				EntityXPOrb orb = (EntityXPOrb) entity;
				if (grinder instanceof TileEntityGrinder) {
					((TileEntityGrinder) grinder).acceptXPOrb(orb);
				}
				entity.setDead();
				return true; // consume any orbs not made into essence
			}
		}

		if (allowSpawns) {
			entity.world = this.proxiedWorld;
			cofh_updateProps();
			return super.spawnEntity(entity);
		}
		entity.setDead();
		return true;
	}

	public EnumDifficulty getDifficulty() {

		return super.getDifficulty() == EnumDifficulty.PEACEFUL ? EnumDifficulty.EASY : super.getDifficulty();
	}

	public boolean addEntityForGrinding(Entity entity) {

		cofh_updateProps();
		if (entity.world == this)
			return true;
		if (entity.world == this.proxiedWorld) {
			entity.world = this;
			entitiesToGrind.add(entity);
			return true;
		}
		return false;
	}

	public void clearReferences() {

		for (Entity ent : entitiesToGrind) {
			if (ent.world == this)
				ent.world = this.proxiedWorld;
		}
		entitiesToGrind.clear();
	}

	public void cleanReferences() {

		entitiesToGrind.removeIf(e -> e.isDead);
	}

}
