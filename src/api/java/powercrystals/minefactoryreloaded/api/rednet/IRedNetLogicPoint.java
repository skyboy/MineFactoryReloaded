package powercrystals.minefactoryreloaded.api.rednet;

/**
 * @author skyboy
 */
public interface IRedNetLogicPoint {

	/**
	 * @param out
	 *
	 * @return
	 */
	void transformOutput(int[] out);

	/**
	 * @param out
	 *
	 * @return
	 */
	void transformOutput(int out);

	/**
	 * @param in
	 *
	 * @return
	 */
	int[] transformInput(int[] in);

	/**
	 * @param in
	 *
	 * @return
	 */
	int transformInput(int in);

}
