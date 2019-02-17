package powercrystals.minefactoryreloaded.asmhooks;

import net.minecraft.world.WorldServer;

public abstract class WorldServerProxy extends WorldServerShim {

	protected WorldServer proxiedWorld;

	public WorldServerProxy(WorldServer world) {

		super(world);
		this.proxiedWorld = world;

		cofh_updatePropsInternal(world);
	}

	protected void cofh_updatePropsInternal(WorldServer server) {

		super.cofh_updatePropsInternal(server);
	}

}
