package powercrystals.minefactoryreloaded.block.fluid;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import powercrystals.minefactoryreloaded.entity.EntityPinkSlime;

public class BlockPinkSlimeFluid extends BlockFactoryFluid
{
	public BlockPinkSlimeFluid(String liquidName)
	{
		super(liquidName);
	}

	@Override
	public void updateTick(net.minecraft.world.World world, BlockPos pos, IBlockState state, java.util.Random rand)
	{
		if (isSourceBlock(world, pos))
		{
			if ((world.getTotalWorldTime() & 15) == rand.nextInt(16))
			{
				world.setBlockToAir(pos);
				EntityPinkSlime s = new EntityPinkSlime(world);
				s.onInitialSpawn(world.getDifficultyForLocation(pos), new EntityPinkSlime.GroupData(true));
				s.setPosition(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
				world.spawnEntity(s);
				return;
			}
			world.scheduleBlockUpdate(pos, this, tickRate, 1); // Does not run immediately if that flag is set. Can't stack overflow.
		}
		super.updateTick(world, pos, state, rand);
	}
}
