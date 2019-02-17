package powercrystals.minefactoryreloaded.core;

public interface INode
{
	boolean isNotValid();
	void firstTick(IGridController grid);
	void updateInternalTypes(IGridController grid);
}
