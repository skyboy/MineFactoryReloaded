package powercrystals.minefactoryreloaded.core;

import com.google.common.collect.ImmutableMap;

import java.util.HashMap;
import java.util.Map;

public enum SideOffset
{
	UNKNOWN(0, 0, 0),

	/** +Y */
	UP(0, 1, 0),

	/** +Y -Z */
	UP_NORTH(0, 1, -1),

	/** +Y +Z */
	UP_SOUTH(0, 1, 1),

	/** +Y -X */
	UP_WEST(-1, 1, 0),

	/** +Y +X */
	UP_EAST(1, 1, 0),

	/** +Y -Z -X */
	UP_NORTH_WEST(-1, 1, -1),

	/** +Y -Z +X */
	UP_NORTH_EAST(1, 1, -1),

	/** +Y +Z -X */
	UP_SOUTH_WEST(-1, 1, 1),

	/** +Y +Z +X */
	UP_SOUTH_EAST(1, 1, 1),

	/** -Z */
	NORTH(0, 0, -1),

	/** +Z */
	SOUTH(0, 0, 1),

	/** -X */
	WEST(-1, 0, 0),

	/** +X */
	EAST(1, 0, 0),

	/** -Z -X */
	NORTH_WEST(-1, 0, -1),

	/** -Z +X */
	NORTH_EAST(1, 0, -1),

	/** +Z -X */
	SOUTH_WEST(-1, 0, 1),

	/** +Z +X */
	SOUTH_EAST(1, 0, 1),

	/** -Y */
	DOWN(0, -1, 0),

	/** -Y -Z */
	DOWN_NORTH(0, -1, -1),

	/** -Y +Z */
	DOWN_SOUTH(0, -1, 1),

	/** -Y -X */
	DOWN_WEST(-1, -1, 0),

	/** -Y +X */
	DOWN_EAST(1, -1, 0),

	/** -Y -Z -X */
	DOWN_NORTH_WEST(-1, -1, -1),

	/** -Y -Z +X */
	DOWN_NORTH_EAST(1, -1, -1),

	/** -Y +Z -X */
	DOWN_SOUTH_WEST(-1, -1, 1),

	/** -Y +Z +X */
	DOWN_SOUTH_EAST(1, -1, 1);

	public static SideOffset[] ADJACENT_CUBE = {UP, NORTH, SOUTH, WEST, EAST, NORTH_WEST, NORTH_EAST,
		SOUTH_WEST, SOUTH_EAST, DOWN, UP_NORTH, UP_SOUTH, UP_WEST, UP_EAST, UP_NORTH_WEST,
		UP_NORTH_EAST, UP_SOUTH_WEST, UP_SOUTH_EAST, DOWN_NORTH, DOWN_SOUTH, DOWN_WEST,
		DOWN_EAST, DOWN_NORTH_WEST, DOWN_NORTH_EAST, DOWN_SOUTH_WEST, DOWN_SOUTH_EAST};
	public static SideOffset[] ADJACENT_CUBE_INVERTED = {DOWN, NORTH, SOUTH, WEST, EAST, NORTH_WEST,
		NORTH_EAST, SOUTH_WEST, SOUTH_EAST, UP, DOWN_NORTH, DOWN_SOUTH, DOWN_WEST, DOWN_EAST,
		DOWN_NORTH_WEST, DOWN_NORTH_EAST, DOWN_SOUTH_WEST, DOWN_SOUTH_EAST, UP_NORTH,
		UP_SOUTH, UP_WEST, UP_EAST, UP_NORTH_WEST, UP_NORTH_EAST, UP_SOUTH_WEST, UP_SOUTH_EAST};

	public static SideOffset[] ADJACENT_SPHERE = {UP, NORTH, SOUTH, WEST, EAST, DOWN, UP_NORTH,
		UP_SOUTH, UP_WEST, UP_EAST, NORTH_WEST, NORTH_EAST, SOUTH_WEST, SOUTH_EAST,
		DOWN_NORTH, DOWN_SOUTH, DOWN_WEST, DOWN_EAST};
	public static SideOffset[] ADJACENT_SPHERE_INVERTED = {DOWN, NORTH, SOUTH, WEST, EAST, UP,
		DOWN_NORTH, DOWN_SOUTH, DOWN_WEST, DOWN_EAST, NORTH_WEST, NORTH_EAST, SOUTH_WEST, SOUTH_EAST,
		UP_NORTH, UP_SOUTH, UP_WEST, UP_EAST};

	public static SideOffset[] ADJACENT = {UP, NORTH, SOUTH, WEST, EAST, DOWN};
	public static SideOffset[] ADJACENT_INVERTED = {DOWN, NORTH, SOUTH, WEST, EAST, UP};

	public static SideOffset[] SIDES = {NORTH, SOUTH, WEST, EAST};
    
    public static final Map<Integer, SideOffset> lookup;
    static
    {
    	HashMap<Integer, SideOffset> a = new HashMap<Integer, SideOffset>();
    	for (int i = 0x40; i --> 0;)
    		a.put(i, UNKNOWN);
    	for (SideOffset v : SideOffset.values())
    		a.put(v.flag, v);
    	lookup = ImmutableMap.copyOf(a);
    }

	public final int offsetX;
	public final int offsetY;
	public final int offsetZ;
    public final int flag;

	private SideOffset(int x, int y, int z)
	{
		offsetX = x;
		offsetY = y;
		offsetZ = z;
		int f = ((y & 3) << 0) | ((z & 3) << 2) | ((x & 3) << 4);
		f ^= (f & 21) & ((f & 42) >> 1); // unset low bits that also have a set high bit (negative values)
		flag = f;
	}

    public SideOffset getOpposite()
    {
		int f = ~flag & 0x3F;
		int t = (f & 21) & ((f & 42) >> 1);
		f &= ~(t | (t << 1)); // unset fields with high&low bits set (invalid state)
        return lookup.get(f);
    }
}