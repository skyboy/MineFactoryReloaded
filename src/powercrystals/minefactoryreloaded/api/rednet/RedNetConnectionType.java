package powercrystals.minefactoryreloaded.api.rednet;

import java.util.HashMap;
import java.util.Map;

public enum RedNetConnectionType
{
	None,				// 0
	CableSingle,		// 11
	PlateSingle,		// 13
	CableAll,			// 19
	PlateAll,			// 21
	ForcedCableSingle,	// 43
	ForcedPlateSingle,	// 45
	ForcedCableAll,		// 51
	ForcedPlateAll;		// 53
	
	public final boolean isConnected = this.ordinal() != 0;
	public final boolean isSingleSubnet = this.name().endsWith("Single");
	public final boolean isAllSubnets = this.name().endsWith("All");
	public final boolean isConnectionForced = this.name().startsWith("Forced");
	public final boolean isPlate = this.name().contains("Plate");
	public final boolean isCable = this.name().contains("Cable");
	public final short flags = toFlags(isConnected, isCable, isPlate, isSingleSubnet, isAllSubnets, isConnectionForced);
	
	public static final RedNetConnectionType fromFlags(short flags)
	{
		return connections.get(flags);
	}
	
	private static final short toFlags(boolean ...flags)
	{
		short ret = 0;
		for (int i = flags.length; i --> 0;)
			ret |= (flags[i] ? 1 : 0) << i;
		return ret;
	}
	
	private static final Map<Short, RedNetConnectionType> connections = new HashMap<Short, RedNetConnectionType>();
	
	static {
		for (RedNetConnectionType type : RedNetConnectionType.values())
			connections.put(type.flags, type);
	}
}
