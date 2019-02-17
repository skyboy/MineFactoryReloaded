package powercrystals.minefactoryreloaded.core;

import java.util.List;

import codechicken.lib.raytracer.IndexedCuboid6;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;

public interface ITraceable
{
	void addTraceableCuboids(List<IndexedCuboid6> list, boolean forTrace, boolean hasTool, boolean offsetCuboids);
	
	boolean onPartHit(EntityPlayer player, EnumHand hand, int side, int subHit);
}
