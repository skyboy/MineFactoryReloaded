package powercrystals.minefactoryreloaded.asmhooks;

import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.IChunkProvider;

/**
 * Do not extend this class directly, extend WorldServerProxy instead. <br>
 * This class is never used at runtime, and is simply a compile-time shim.
 */
public abstract class WorldServerShim extends WorldServer {

	public WorldServerShim(WorldServer server) {

		super(server.getMinecraftServer(), server.getSaveHandler(), server.getWorldInfo(), server.provider.getDimension(), server.profiler);
		throw new IllegalAccessError("WorldServerShim cannot be extended. Extend WorldServerProxy instead.");
	}

	protected void cofh_updatePropsInternal(WorldServer server) {

		// no-op: handled via ASM
	}

	@Override
	public IChunkProvider createChunkProvider() {

		return null;
	}

}
