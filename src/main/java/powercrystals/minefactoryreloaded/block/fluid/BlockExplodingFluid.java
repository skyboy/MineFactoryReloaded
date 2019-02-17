package powercrystals.minefactoryreloaded.block.fluid;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.BiomeDictionary;
import powercrystals.minefactoryreloaded.setup.MFRConfig;

import java.util.Random;

public class BlockExplodingFluid extends BlockFactoryFluid {

	public BlockExplodingFluid(String liquidName) {

		super(liquidName);
	}
	
	protected void explode(World world, BlockPos pos, Random rand, boolean noReplace) {

		if (noReplace || world.setBlockToAir(pos)) {
			if (isSourceBlock(world, pos) && MFRConfig.enableFuelExploding.getBoolean(true))
				world.createExplosion(null, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 8, true);
			fizz(world, pos, rand);
			return;
		}
	}
	
	@Override
	protected void checkCanStay(World world, BlockPos pos, Random rand) {

		if (BiomeDictionary.hasType(world.getBiome(pos), BiomeDictionary.Type.NETHER))  {
			explode(world, pos, rand, false);
		}
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {

		if (world.getBlockState(pos).getBlock() == Blocks.FIRE) {
			explode(world, pos, world.rand, true);
		}
	}

}
