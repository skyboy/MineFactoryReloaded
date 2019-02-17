package powercrystals.minefactoryreloaded.setup;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;

public class MachineMaterial extends Material
{
    public MachineMaterial(MapColor color)
    {
    	super(color);
    	setAdventureModeExempt();
    }
}
