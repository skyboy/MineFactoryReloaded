package powercrystals.minefactoryreloaded.core;

public enum HarvestMode
{
	HarvestTree,
	HarvestTreeInverted,
	FruitTree,
	FruitTreeInverted;
	
	public boolean isTree = this.name().contains("Tree");
	public boolean isInverted = this.name().endsWith("Inverted");
}
