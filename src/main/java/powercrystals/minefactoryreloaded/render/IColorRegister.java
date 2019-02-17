package powercrystals.minefactoryreloaded.render;

//TODO possibly consider pushing this up to core if useful to others
/**
 * Interface which can be attached to classes which have to register IItemColor or IBlockColor color multipliers. Useful for iteration.
 *
 * @author P3pp3rF1y
 */
public interface IColorRegister {
	
	void registerColorHandlers();
}
