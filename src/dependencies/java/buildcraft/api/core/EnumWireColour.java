package buildcraft.api.core;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Set;

import net.minecraft.item.EnumDyeColor;

/** A subset of colours from {@link EnumDyeColor} that are suitable for use in LED's or wires (or equivalent). In other
 * words they must all be uniquely identifiable from both their lit and dark colours, and not look similar to other
 * colours. */
public enum EnumWireColour {
    // We disallow all variants of grey and black, as they don't make much sense relative to LED's.
    // In theory we could keep black OR dark gey LED's (as they are very distant) but it's simpler not to.
    WHITE(EnumDyeColor.WHITE, EnumDyeColor.SILVER, EnumDyeColor.GRAY, EnumDyeColor.BLACK),
    ORANGE(EnumDyeColor.ORANGE),
    // MAGENTA -> PINK
    LIGHT_BLUE(EnumDyeColor.LIGHT_BLUE, EnumDyeColor.CYAN),
    YELLOW(EnumDyeColor.YELLOW),
    LIME(EnumDyeColor.LIME),
    PINK(EnumDyeColor.PINK, EnumDyeColor.MAGENTA),
    // GRAY -> WHITE
    // SILVER (LIGHT_GRAY) -> WHITE
    // CYAN -> LIGHT_BLUE
    PURPLE(EnumDyeColor.PURPLE),
    BLUE(EnumDyeColor.BLUE),
    BROWN(EnumDyeColor.BROWN),
    GREEN(EnumDyeColor.GREEN),
    RED(EnumDyeColor.RED),
    // BLACK -> WHITE
    ;

    private static final EnumMap<EnumDyeColor, EnumWireColour> DYE_TO_WIRE;

    static {
        DYE_TO_WIRE = new EnumMap<>(EnumDyeColor.class);
        for (EnumWireColour wire : values()) {
            for (EnumDyeColor dye : wire.similarBasedColours) {
                EnumWireColour prev = DYE_TO_WIRE.put(dye, wire);
                if (prev != null) {
                    throw new Error(wire + " attempted to override " + prev + " for the dye " + dye + "!");
                }
            }
        }

        for (EnumDyeColor dye : EnumDyeColor.values()) {
            EnumWireColour wire = DYE_TO_WIRE.get(dye);
            if (wire == null) {
                throw new Error(dye + " isn't mapped to a wire colour!");
            }
        }
    }

    /** The primary minecraft colour that this is based on. */
    public final EnumDyeColor primaryIdenticalColour;

    /** A set of similar minecraft colours that this single colour is based on. Always includes
     * {@link #primaryIdenticalColour}. */
    public final Set<EnumDyeColor> similarBasedColours;

    private EnumWireColour(EnumDyeColor primary, EnumDyeColor... secondary) {
        this.primaryIdenticalColour = primary;
        this.similarBasedColours = EnumSet.of(primary, secondary);
    }

    public static EnumWireColour convertToWire(EnumDyeColor dye) {
        return DYE_TO_WIRE.get(dye);
    }
}
