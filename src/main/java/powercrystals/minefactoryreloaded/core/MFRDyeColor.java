package powercrystals.minefactoryreloaded.core;

import net.minecraft.util.IStringSerializable;

public enum MFRDyeColor implements IStringSerializable {
	
	WHITE(0, "white", "white", 0xf0f0f0),
	ORANGE(1, "orange", "orange", 0xe36600),
	MAGENTA(2, "magenta", "magenta", 0xc730a5),
	LIGHT_BLUE(3, "light_blue", "lightblue", 0x8da7f6),
	YELLOW(4, "yellow", "yellow", 0xd8b920),
	LIME(5, "lime", "lime", 0x49c14a),
	PINK(6, "pink", "pink", 0xd881a4),
	GRAY(7, "gray", "gray", 0x505050),
	SILVER(8, "silver", "lightgray", 0xa3a3a3),
	CYAN(9, "cyan", "cyan", 0x54a69b),
	PURPLE(10, "purple", "purple", 0x803880),
	BLUE(11, "blue", "blue", 0x404080),
	BROWN(12, "brown", "brown", 0x804020),
	GREEN(13, "green", "green", 0x47691D),
	RED(14, "red", "red", 0xb62222),
	BLACK(15, "black", "black", 0x1e1e1e);

	private static final MFRDyeColor[] META_LOOKUP = new MFRDyeColor[values().length];
	public static final String[] NAMES = new String[values().length];
	public static final String[] UNLOC_NAMES = new String[values().length];
	private final int meta;
	private final String name;
	private final String unlocalizedName;
	private final int color;

	MFRDyeColor(int meta, String name, String unlocalizedName, int color)
	{
		this.meta = meta;
		this.name = name;
		this.unlocalizedName = unlocalizedName;
		this.color = color;
	}

	public int getMetadata()
	{
		return this.meta;
	}

	public String getUnlocalizedName()
	{
		return this.unlocalizedName;
	}

	public int getColor()
	{
		return this.color;
	}

	public static MFRDyeColor byMetadata(int meta)
	{
		if (meta < 0 || meta >= META_LOOKUP.length)
		{
			meta = 0;
		}

		return META_LOOKUP[meta];
	}

	public String toString()
	{
		return this.unlocalizedName;
	}

	@Override
	public String getName()
	{
		return this.name;
	}

	static
	{
		for (MFRDyeColor color : values())
		{
			META_LOOKUP[color.getMetadata()] = color;
			NAMES[color.getMetadata()] = color.getName();
			UNLOC_NAMES[color.getMetadata()] = color.getUnlocalizedName();
		}
	}
}
