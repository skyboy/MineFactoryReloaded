package powercrystals.minefactoryreloaded.setup.village;

import net.minecraft.util.EnumFacing;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraftforge.fml.common.registry.VillagerRegistry.IVillageCreationHandler;

import java.util.List;
import java.util.Random;

import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraft.world.gen.structure.StructureVillagePieces.PieceWeight;
import net.minecraft.world.gen.structure.StructureVillagePieces.Start;
import net.minecraft.world.gen.structure.StructureVillagePieces.Village;

public class VillageCreationHandler implements IVillageCreationHandler
{
	@Override
	public PieceWeight getVillagePieceWeight(Random random, int i)
	{
		return new PieceWeight(ComponentZoologistHouse.class, 20, random.nextInt(1) + i);
	}
	
	@Override
	public Class<?> getComponentClass()
	{
		MapGenStructureIO.registerStructureComponent(ComponentZoologistHouse.class, "minefactoryreloaded:ZoologistHouseStructure");
		return ComponentZoologistHouse.class;
	}

	@Override
	public Village buildComponent(PieceWeight villagePiece, Start startPiece, List<StructureComponent> pieces, Random random, int p1, int p2, int p3, EnumFacing p4, int p5)
	{
		return ComponentZoologistHouse.buildComponent(villagePiece, startPiece, pieces, random, p1, p2, p3, p4, p5);
	}
}
