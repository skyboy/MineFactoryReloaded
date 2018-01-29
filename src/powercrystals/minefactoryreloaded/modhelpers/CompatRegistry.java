package powercrystals.minefactoryreloaded.modhelpers;

import com.google.common.collect.Sets;
import net.minecraftforge.fml.common.Loader;
import powercrystals.minefactoryreloaded.modhelpers.agricraft.Agricraft;

import java.util.Set;

public class CompatRegistry {

	private static final Set<ICompat> compats = Sets.newHashSet();

	static {
		compats.add(Agricraft.INSTANCE);
	}

	public static void init() {

		for (ICompat compat : compats) {
			if (Loader.isModLoaded(compat.getModId())) {
				compat.init();
			}
		}
	}
}
