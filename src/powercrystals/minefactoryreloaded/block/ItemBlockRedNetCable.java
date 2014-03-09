package powercrystals.minefactoryreloaded.block;

public class ItemBlockRedNetCable extends ItemBlockFactory
{
	public ItemBlockRedNetCable(int id)
	{
		super(id);
		setHasSubtypes(true);
		setNames(new String[]{null, "glass", "energy", "energyglass"});
	}
}
