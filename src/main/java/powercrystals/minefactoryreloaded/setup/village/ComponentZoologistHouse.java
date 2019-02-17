package powercrystals.minefactoryreloaded.setup.village;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraft.world.gen.structure.StructureVillagePieces.PieceWeight;
import net.minecraft.world.gen.structure.StructureVillagePieces.Start;
import net.minecraft.world.gen.structure.template.TemplateManager;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import powercrystals.minefactoryreloaded.block.BlockRubberWood;
import powercrystals.minefactoryreloaded.setup.MFRLoot;
import powercrystals.minefactoryreloaded.setup.MFRThings;

import java.util.List;
import java.util.Random;

public class ComponentZoologistHouse extends StructureVillagePieces.Village
{
	private IBlockState paneState;
	private IBlockState brickState;
	private int benchMeta;
	private int lightMeta;
	private boolean hasMadeChest;

	public ComponentZoologistHouse() {}

	public ComponentZoologistHouse(Start startPiece, int componentType, Random rand, StructureBoundingBox sbb, EnumFacing coordBaseMode)
	{
		super(startPiece, componentType);
		this.setCoordBaseMode(coordBaseMode);
		boundingBox = sbb;

		Block brickId = MFRThings.factoryDecorativeBrickBlock;
		int brickMeta = rand.nextInt(16);
		if (brickMeta > 0) ++brickMeta; // 1 is glowstone small bricks
		if (brickMeta > 6) ++brickMeta; // 7 is glowstone large bricks
		if (brickMeta > 11) // 12 & 13 are meat
			brickMeta += 2;
		if (brickMeta == 15) // 15 is sugar charcoal, but we don't build houses out of charcoal
		{
			brickId = Blocks.BRICK_BLOCK; // because large red bricks are also an option
			brickMeta = 0; // no need for weird meta, and makes lights correct size
		}

		if (brickMeta > 15) // covers the 4 skipped blocks in factoryDecorativeBrickBlock
		{
			brickId = MFRThings.factoryDecorativeStoneBlock;
			brickMeta -= 12; // shift into proper range (16 - 12 = 4: first stone brick block)
			lightMeta = brickMeta < 6 ? 7 : 1;
		}
		else
			lightMeta = brickMeta < 6 ? 1 : 7;

		brickState = brickId.getStateFromMeta(brickMeta);
		paneState = Blocks.GLASS_PANE.getDefaultState();
		if (getBiomeSpecificBlockState(paneState).equals(paneState))
		{
			paneState = MFRThings.factoryGlassPaneBlock.getDefaultState(); 
		}

		benchMeta = rand.nextInt(10) == 0 ? 12 : 0;
	}

	@Override // write to NBT
	protected void writeStructureToNBT(NBTTagCompound tag)
	{
		super.writeStructureToNBT(tag);
		tag.setBoolean("Chest", this.hasMadeChest);
		tag.setIntArray("blocks", new int[]{brickState.getBlock().getMetaFromState(brickState), paneState.getBlock().getMetaFromState(paneState), lightMeta, benchMeta});
		tag.setString("brick", brickState.getBlock().getRegistryName().toString());
		tag.setString("pane", paneState.getBlock().getRegistryName().toString());
	}

	@Override // read from NBT
	protected void readStructureFromNBT(NBTTagCompound tag, TemplateManager p_143011_2_)
	{
		super.readStructureFromNBT(tag, p_143011_2_);
		this.hasMadeChest = tag.getBoolean("Chest");
		int[] blocks = tag.getIntArray("blocks");
		if (blocks.length != 4)
			return;

		lightMeta = blocks[2];
		benchMeta = blocks[3];
		brickState = Block.REGISTRY.getObject(new ResourceLocation(tag.getString("brick"))).getStateFromMeta(blocks[0]);
		paneState = Block.REGISTRY.getObject(new ResourceLocation(tag.getString("pane"))).getStateFromMeta(blocks[1]);
	}

	public static ComponentZoologistHouse buildComponent(PieceWeight villagePiece,
			Start startPiece, List<StructureComponent> pieces, Random random, int p1, int p2,
			int p3, EnumFacing facing, int p5)
	{
		StructureBoundingBox sbb = StructureBoundingBox.getComponentToAddBoundingBox(p1, p2, p3,
				0, 0, 0, 9, 9, 6, facing);
		return (!canVillageGoDeeper(sbb) || (StructureComponent.findIntersecting(pieces, sbb) != null))
				? null : new ComponentZoologistHouse(startPiece, p5, random, sbb, facing);
	}

	@Override
	public boolean addComponentParts(World world, Random random, StructureBoundingBox sbb)
	{
		if (averageGroundLvl < 0)
		{
			averageGroundLvl = getAverageGroundLevel(world, sbb);

			if (averageGroundLvl < 0)
			{
				return true;
			}

			boundingBox.offset(0, averageGroundLvl - boundingBox.maxY + 9 - 1, 0);
		}
		Block mfrBrickId = MFRThings.factoryDecorativeBrickBlock;
		IBlockState logState = MFRThings.rubberWoodBlock.getDefaultState().withProperty(BlockRubberWood.LOG_AXIS, BlockLog.EnumAxis.NONE);

		fillWithAir(world, sbb, 1, 1, 1, 7, 5, 4);
		fillWithBlocks(world, sbb, 0, 0, 0, 8, 0, 5, brickState, brickState, false);
		fillWithBlocks(world, sbb, 0, 5, 0, 8, 5, 5, brickState, brickState, false);
		fillWithBlocks(world, sbb, 0, 6, 1, 8, 6, 4, brickState, brickState, false);
		fillWithBlocks(world, sbb, 0, 7, 2, 8, 7, 3, brickState, brickState, false);
		IBlockState stairsNorth = Blocks.OAK_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.NORTH);
		IBlockState stairsSouth = Blocks.OAK_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.SOUTH);
		IBlockState stairsEast = Blocks.OAK_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.EAST);
		int k;
		int l;

		for (k = -1; k <= 2; ++k)
		{
			for (l = 0; l <= 8; ++l)
			{
				setBlockState(world, stairsNorth, l, 6 + k, k, sbb);
				setBlockState(world, stairsSouth, l, 6 + k, 5 - k, sbb);
			}
		}

		fillWithBlocks(world, sbb, 0, 1, 0, 0, 1, 5, brickState, brickState, false);
		fillWithBlocks(world, sbb, 1, 1, 5, 8, 1, 5, brickState, brickState, false);
		fillWithBlocks(world, sbb, 8, 1, 0, 8, 1, 4, brickState, brickState, false);
		fillWithBlocks(world, sbb, 2, 1, 0, 7, 1, 0, brickState, brickState, false);
		fillWithBlocks(world, sbb, 0, 2, 0, 0, 4, 0, brickState, brickState, false);
		fillWithBlocks(world, sbb, 0, 2, 5, 0, 4, 5, brickState, brickState, false);
		fillWithBlocks(world, sbb, 8, 2, 5, 8, 4, 5, brickState, brickState, false);
		fillWithBlocks(world, sbb, 8, 2, 0, 8, 4, 0, brickState, brickState, false);
		fillWithBlocks(world, sbb, 0, 2, 1, 0, 4, 4, Blocks.PLANKS.getDefaultState(), Blocks.PLANKS.getDefaultState(), false);
		fillWithBlocks(world, sbb, 1, 2, 5, 7, 4, 5, Blocks.PLANKS.getDefaultState(), Blocks.PLANKS.getDefaultState(), false);
		fillWithBlocks(world, sbb, 8, 2, 1, 8, 4, 4, Blocks.PLANKS.getDefaultState(), Blocks.PLANKS.getDefaultState(), false);
		fillWithBlocks(world, sbb, 1, 2, 0, 7, 4, 0, Blocks.PLANKS.getDefaultState(), Blocks.PLANKS.getDefaultState(), false);
		//{ Windows
		//{ Large front window
		setBlockState(world, logState, 3, 2, 0, sbb);
		setBlockState(world, paneState, 4, 2, 0, sbb);
		setBlockState(world, paneState, 5, 2, 0, sbb);
		setBlockState(world, paneState, 6, 2, 0, sbb);
		setBlockState(world, logState, 7, 2, 0, sbb);
		setBlockState(world, logState, 3, 3, 0, sbb);
		setBlockState(world, paneState, 4, 3, 0, sbb);
		setBlockState(world, paneState, 5, 3, 0, sbb);
		setBlockState(world, paneState, 6, 3, 0, sbb);
		setBlockState(world, logState, 7, 3, 0, sbb);
		//}
		//{ Side window near door
		setBlockState(world, logState, 0, 2, 1, sbb);
		setBlockState(world, paneState, 0, 2, 2, sbb);
		setBlockState(world, paneState, 0, 2, 3, sbb);
		setBlockState(world, logState, 0, 2, 4, sbb);
		setBlockState(world, logState, 0, 3, 1, sbb);
		setBlockState(world, paneState, 0, 3, 2, sbb);
		setBlockState(world, paneState, 0, 3, 3, sbb);
		setBlockState(world, logState, 0, 3, 4, sbb);
		//}
		//{ Window opposite above window
		setBlockState(world, logState, 8, 2, 1, sbb);
		setBlockState(world, paneState, 8, 2, 2, sbb);
		setBlockState(world, paneState, 8, 2, 3, sbb);
		setBlockState(world, logState, 8, 2, 4, sbb);
		setBlockState(world, logState, 8, 3, 1, sbb);
		setBlockState(world, paneState, 8, 3, 2, sbb);
		setBlockState(world, paneState, 8, 3, 3, sbb);
		setBlockState(world, logState, 8, 3, 4, sbb);
		//}
		//{ Two back windows
		setBlockState(world, logState, 1, 2, 5, sbb);
		setBlockState(world, paneState, 2, 2, 5, sbb);
		setBlockState(world, paneState, 3, 2, 5, sbb);
		setBlockState(world, logState, 4, 2, 5, sbb);
		setBlockState(world, paneState, 5, 2, 5, sbb);
		setBlockState(world, paneState, 6, 2, 5, sbb);
		setBlockState(world, logState, 7, 2, 5, sbb);
		//}
		//}
		fillWithBlocks(world, sbb, 1, 4, 1, 7, 4, 1, Blocks.PLANKS.getDefaultState(), Blocks.PLANKS.getDefaultState(), false);
		fillWithBlocks(world, sbb, 1, 4, 4, 7, 4, 4, Blocks.PLANKS.getDefaultState(), Blocks.PLANKS.getDefaultState(), false);
		fillWithBlocks(world, sbb, 1, 3, 4, 7, 3, 4, Blocks.BOOKSHELF.getDefaultState(), Blocks.BOOKSHELF.getDefaultState(), false);
		//{ Wrap around bench
		setBlockState(world, stairsEast, 7, 1, 4, sbb);
		setBlockState(world, stairsEast, 7, 1, 3, sbb);
		setBlockState(world, stairsNorth, 6, 1, 4, sbb);
		setBlockState(world, stairsNorth, 5, 1, 4, sbb);
		setBlockState(world, stairsNorth, 4, 1, 4, sbb);
		setBlockState(world, stairsNorth, 3, 1, 4, sbb);
		//{ "Tables"
		setBlockState(world, Blocks.OAK_FENCE.getDefaultState(), 6, 1, 3, sbb);
		setBlockState(world, Blocks.WOODEN_PRESSURE_PLATE.getDefaultState(), 6, 2, 3, sbb);
		setBlockState(world, Blocks.OAK_FENCE.getDefaultState(), 4, 1, 3, sbb);
		setBlockState(world, Blocks.WOODEN_PRESSURE_PLATE.getDefaultState(), 4, 2, 3, sbb);
		//}
		//}
		k = benchMeta; // workbench or meat
		setBlockState(world, k == 0 ? Blocks.CRAFTING_TABLE.getDefaultState() : mfrBrickId.getStateFromMeta(k), 7, 1, 1, sbb);

		k = lightMeta; // overhead lights (match large/small bricks)
		fillWithBlocks(world, sbb, 1, 5, 2, 7, 5, 3, mfrBrickId.getStateFromMeta(k), mfrBrickId.getStateFromMeta(k), false);
		//{ Door
		setBlockState(world, Blocks.AIR.getDefaultState(), 1, 1, 0, sbb);
		setBlockState(world, Blocks.AIR.getDefaultState(), 1, 2, 0, sbb);
		createVillageDoor(world, sbb, random, 1, 1, 0, EnumFacing.NORTH);
		//}

		if (getBlockStateFromPos(world, 1, 0, -1, sbb).getBlock().equals(Blocks.AIR) &&
				!getBlockStateFromPos(world, 1, -1, -1, sbb).getBlock().equals(Blocks.AIR))
		{
			setBlockState(world, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.NORTH), 1, 0, -1, sbb);
		}

		for (l = 0; l < 6; ++l)
		{
			for (int i1 = 0; i1 < 9; ++i1)
			{
				clearCurrentPositionBlocksUpwards(world, i1, 9, l, sbb);
				replaceAirAndLiquidDownwards(world, brickState, i1, -1, l, sbb);
			}
		}
		if (!this.hasMadeChest)
		{
			int i = this.getYWithOffset(1);
			int j = this.getXWithOffset(1, 4);
			k = this.getZWithOffset(1, 4);

			if (sbb.isVecInside(new Vec3i(j, i, k)))
			{
				this.hasMadeChest = true;
				generateChest(world, sbb, random, 1, 1, 4, MFRLoot.ZOOLOGIST_CHEST);
				// TODO: loot table shit is broken, needs ASM in net.minecraft.world.storage.loot.LootTable.shuffleItems to fix
			}
		}

		spawnVillagers(world, sbb, 2, 1, 2, 1);
		return true;
	}

	@Override
	protected VillagerRegistry.VillagerProfession chooseForgeProfession(int count, VillagerRegistry.VillagerProfession prof) {
		
		return Zoologist.zoologistProfession;
	}
}
