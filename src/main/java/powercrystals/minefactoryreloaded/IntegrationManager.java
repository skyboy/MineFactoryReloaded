package powercrystals.minefactoryreloaded;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.discovery.ModCandidate;
import net.minecraftforge.fml.common.toposort.ModSortingException;
import net.minecraftforge.fml.common.toposort.TopologicalSort;
import org.apache.logging.log4j.Level;
import org.objectweb.asm.Type;
import powercrystals.minefactoryreloaded.api.integration.IMFRIntegrator;
import powercrystals.minefactoryreloaded.modhelpers.vanilla.Minecraft;
import powercrystals.minefactoryreloaded.setup.MFRConfig;

import java.io.File;
import java.util.*;
import java.util.function.Function;

import static powercrystals.minefactoryreloaded.MineFactoryReloadedCore.log;

public class IntegrationManager {

	private static List<IntegrationContainer> integrationSets = new LinkedList<>();

	public static void load(ASMDataTable data) {

		for (ASMDataTable.ASMData obj : data.getAll(Type.getInternalName(IMFRIntegrator.class))) {
			integrationSets.add(new IntegrationContainer(obj));
		}
		boolean sorted = false;
		do {
			TopologicalSort.DirectedGraph<IntegrationContainer> graph = new TopologicalSort.DirectedGraph<>();
			for (IntegrationContainer container : integrationSets) {
				graph.addNode(container);
			}
			try {
				for (IntegrationContainer container : integrationSets) {
					for (String after : container.sortAfter) {
						IntegrationContainer other = IntegrationContainer.createdSets.get(after);
						if (other != null) { // there are no required deps for this
							graph.addEdge(other, container);
						}
					}
				}
				integrationSets = TopologicalSort.topologicalSort(graph);
				sorted = true;
			} catch (ModSortingException e) {
				ModSortingException.SortingExceptionData<IntegrationContainer> exceptionData = e.getExceptionData();
				IntegrationContainer badNode = exceptionData.getFirstBadNode();
				ArrayList<String> badAfter = new ArrayList<>(Arrays.asList(badNode.sortAfter));
				for (IntegrationContainer otherNode : exceptionData.getVisitedNodes()) {
					int idx = badAfter.indexOf(otherNode.sortName);
					if (idx >= 0) {
						badAfter.remove(idx);
					}
				}
				badNode.sortAfter = badAfter.toArray(new String[0]);
			}
		} while (!sorted);
	}

	private static void iterate(final ThrowingConsumer<IMFRIntegrator> consumer, String error) {

		iterate((c, name) -> consumer.accept(c), error);
	}

	private static void iterate(ThrowingBiConsumer<IMFRIntegrator, String> consumer, String error) {

		for (IntegrationContainer container : integrationSets) {
			if (container.isEnabled()) {
				try {
					consumer.accept(container.integrator, container.name);
				} catch (Throwable t) {
					container.enabled = false;
					log().error(error, container.name, container.mod);
					log().catching(Level.ERROR, t);
				}
			}
		}
	}

	public static void configure(final Function<String, File> getConfig) {

		for (IntegrationContainer container : integrationSets) {
			if (container.integrator != null) {
				container.enabled = MFRConfig.isIntegrationEnabled(container.name, container.mod);
			} else {
				container.enabled = false;
			}
		}
		final String path = "integration/";
		getConfig.apply(path + "common").getParentFile().mkdirs();
		iterate((c, name) -> c.readConfig(getConfig.apply(path + name)), "Error reading config for MFR integration {} from {}");
	}

	public static void preInit() {

		iterate(IMFRIntegrator::preLoad, "Error during pre-initialization for MFR integration {} from {}");
	}

	public static void init() {

		iterate(IMFRIntegrator::load, "Error during initialization for MFR integration {} from {}");
	}

	public static void postInit() {

		iterate(IMFRIntegrator::postLoad, "Error during post-initialization for MFR integration {} from {}");
	}

	public static void completeInit() {

		iterate(IMFRIntegrator::completeLoad, "Error during load complete for MFR integration {} from {}");
		integrationSets.clear();
		integrationSets = null;
		IntegrationContainer.createdSets.clear();
		IntegrationContainer.createdSets = null;
	}

	private static class IntegrationContainer {

		private static HashMap<String, IntegrationContainer> createdSets = new HashMap<>();

		private static String asString(ModCandidate candidate) {

			String str = null;

			List<ModContainer> data = candidate.getContainedMods();

			if (data.size() == 1) {
				str = data.get(0).getName();
				if (str != null && str.length() > 60)
					str = null; // that's a long name, we'll use the jar name thanks
			}

			if (str == null) {
				str = "Mod jar: `" + candidate.getModContainer().getName() + "`";
			}

			return str;
		}

		IntegrationContainer(ASMDataTable.ASMData asmData) {

			try {
				mod = asString(asmData.getCandidate());
				String className = Type.getObjectType(asmData.getClassName()).getClassName();
				Class<?> integration = Class.forName(className, false, this.getClass().getClassLoader());
				notFound:
				{
					IMFRIntegrator.DependsOn depends = integration.getAnnotation(IMFRIntegrator.DependsOn.class);
					if (depends != null) for (String entry : Objects.requireNonNull(depends.value(), "Cannot have null @DependsOn")) {
						if (!Loader.isModLoaded(Objects.requireNonNull(entry, "Cannot @DependsOn null")))
							break notFound;
					}
					IMFRIntegrator.After after = integration.getAnnotation(IMFRIntegrator.After.class);
					if (after != null) sortAfter = Objects.requireNonNull(after.value(), "Cannot have null @After");
					Arrays.asList(sortAfter).replaceAll(s -> Objects.requireNonNull(s, "Cannot be @After null").toLowerCase(Locale.ROOT));
					integrator = (IMFRIntegrator) integration.newInstance();
					String name = integrator.getIntegratorName();
					String lowerName = String.valueOf(name).toLowerCase(Locale.ROOT);
					if (name == null || name.contains(".") || lowerName.equals("common") ||
							name.contains("/") || name.contains("\\") ||
							name.contains(";") || name.contains(":")) {
						throw new IllegalArgumentException(String.format("Invalid integration name `%s`", name));
					}
					if ("minecraft".equals(lowerName)  && !className.equals(Minecraft.class.getName())) {
						throw new IllegalArgumentException("Cannot replace vanilla integration");
					}
					if (createdSets.containsKey(lowerName)) {
						throw new IllegalArgumentException(String.format("Duplicate integration name `%s`", name));
					}
					createdSets.put(lowerName, this);
					this.name = name;
					sortName = lowerName;
				}
			} catch (Throwable t) {
				log().error("Error loading MFR integration from {}", mod);
				log().catching(Level.ERROR, t);
				integrator = null;
			}
		}

		String mod;
		String name;
		String sortName = "";
		IMFRIntegrator integrator;
		String[] sortAfter = new String[0];
		boolean enabled = false;

		public boolean isEnabled() {

			return name != null && enabled;
		}

		@Override
		public String toString() {

			return "MFR Integrator: " + name + (sortAfter.length > 0 ? "(After: " + Arrays.toString(sortAfter) + ")" : "");
		}

	}

	@FunctionalInterface
	private interface ThrowingConsumer<T> {

		void accept(T a) throws Throwable;
	}

	@FunctionalInterface
	private interface ThrowingBiConsumer<T1, T2> {

		void accept(T1 a, T2 b) throws Throwable;
	}

}
