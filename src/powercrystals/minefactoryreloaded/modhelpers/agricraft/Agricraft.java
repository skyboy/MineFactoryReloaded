package powercrystals.minefactoryreloaded.modhelpers.agricraft;

import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.modhelpers.ICompat;

public class Agricraft implements ICompat {

	public static final Agricraft INSTANCE = new Agricraft();

	private Agricraft() {}

	@Override
	public void init() {

		AgricraftCrop crop = new AgricraftCrop();
		MFRRegistry.registerHarvestable(crop);
		MFRRegistry.registerFertilizable(crop);
	}

	@Override
	public String getModId() {

		return "agricraft";
	}
}
