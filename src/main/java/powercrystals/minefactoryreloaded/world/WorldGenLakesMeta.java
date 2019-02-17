package powercrystals.minefactoryreloaded.world;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.util.Random;

public class WorldGenLakesMeta extends WorldGenerator
{
	private IBlockState state;

	public WorldGenLakesMeta(IBlockState state)
	{
		this.state = state;
	}
	
	@Override
	public boolean generate(World world, Random random, BlockPos pos)
	{
		int x = pos.getX() - 8;
		int z = pos.getZ() - 8;
		int y = pos.getY();

		for(; y > 5 && world.isAirBlock(new BlockPos(x, y, z)); --y)
		{
			;
		}
		
		if(y <= 4)
		{
			return false;
		}
		else
		{
			y -= 4;
			boolean[] booleans = new boolean[2048];
			int l = random.nextInt(4) + 4;
			int i1;
			
			for(i1 = 0; i1 < l; ++i1)
			{
				double d0 = random.nextDouble() * 6.0D + 3.0D;
				double d1 = random.nextDouble() * 4.0D + 2.0D;
				double d2 = random.nextDouble() * 6.0D + 3.0D;
				double d3 = random.nextDouble() * (16.0D - d0 - 2.0D) + 1.0D + d0 / 2.0D;
				double d4 = random.nextDouble() * (8.0D - d1 - 4.0D) + 2.0D + d1 / 2.0D;
				double d5 = random.nextDouble() * (16.0D - d2 - 2.0D) + 1.0D + d2 / 2.0D;
				
				for(int j1 = 1; j1 < 15; ++j1)
				{
					for(int k1 = 1; k1 < 15; ++k1)
					{
						for(int l1 = 1; l1 < 7; ++l1)
						{
							double d6 = (j1 - d3) / (d0 / 2.0D);
							double d7 = (l1 - d4) / (d1 / 2.0D);
							double d8 = (k1 - d5) / (d2 / 2.0D);
							double d9 = d6 * d6 + d7 * d7 + d8 * d8;
							
							if(d9 < 1.0D)
							{
								booleans[(j1 * 16 + k1) * 8 + l1] = true;
							}
						}
					}
				}
			}
			
			int i2;
			int j2;
			boolean flag;
			
			for(i1 = 0; i1 < 16; ++i1)
			{
				for(j2 = 0; j2 < 16; ++j2)
				{
					for(i2 = 0; i2 < 8; ++i2)
					{
						flag = !booleans[(i1 * 16 + j2) * 8 + i2]
								&& (i1 < 15 && booleans[((i1 + 1) * 16 + j2) * 8 + i2] || i1 > 0 && booleans[((i1 - 1) * 16 + j2) * 8 + i2] || j2 < 15
										&& booleans[(i1 * 16 + j2 + 1) * 8 + i2] || j2 > 0 && booleans[(i1 * 16 + (j2 - 1)) * 8 + i2] || i2 < 7
										&& booleans[(i1 * 16 + j2) * 8 + i2 + 1] || i2 > 0 && booleans[(i1 * 16 + j2) * 8 + (i2 - 1)]);
						
						if(flag)
						{
							Material material = world.getBlockState(new BlockPos(x + i1, y + i2, z + j2)).getMaterial();
							
							if(i2 >= 4 && material.isLiquid())
							{
								return false;
							}
							
							if(i2 < 4 && !material.isSolid() && !world.getBlockState(new BlockPos(x + i1, y + i2, z + j2)).getBlock().equals(state.getBlock()))
							{
								return false;
							}
						}
					}
				}
			}
			
			for(i1 = 0; i1 < 16; ++i1)
			{
				for(j2 = 0; j2 < 16; ++j2)
				{
					for(i2 = 0; i2 < 8; ++i2)
					{
						if(booleans[(i1 * 16 + j2) * 8 + i2])
						{
							world.setBlockState(new BlockPos(x + i1, y + i2, z + j2), i2 >= 4 ? Blocks.AIR.getDefaultState() : state, 2);
						}
					}
				}
			}
			
			for(i1 = 0; i1 < 16; ++i1)
			{
				for(j2 = 0; j2 < 16; ++j2)
				{
					for(i2 = 4; i2 < 8; ++i2)
					{
						BlockPos placementPos = new BlockPos(x + i1, y + i2, z + j2);
						if(booleans[(i1 * 16 + j2) * 8 + i2] && world.getBlockState(new BlockPos(x + i1, y + i2 - 1, z + j2)).getBlock().equals(Blocks.DIRT)
								&& world.getLightFor(EnumSkyBlock.SKY, placementPos) > 0)
						{
							Biome biome = world.getBiome(placementPos);
							
							world.setBlockState(placementPos.down(), biome.topBlock, 2);
						}
					}
				}
			}
			
			return true;
		}
	}
}
