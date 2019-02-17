package powercrystals.minefactoryreloaded.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import powercrystals.minefactoryreloaded.setup.MFRThings;

import javax.annotation.Nonnull;

public class ItemBlockVineScaffold extends ItemBlock
{
	public ItemBlockVineScaffold(net.minecraft.block.Block id)
	{
		super(id);
	}

	@Override
	public EnumActionResult onItemUseFirst(EntityPlayer player, World world,
			BlockPos pos, EnumFacing side, float xOffset, float yOffset, float zOffset, EnumHand hand)
	{
		IBlockState state = world.getBlockState(pos);
		if (world.isRemote && !player.isSneaking() &&
				state.getBlock().equals(MFRThings.vineScaffoldBlock))
		{
			if (MFRThings.vineScaffoldBlock.onBlockActivated(world, pos, state,
						player, hand, side, xOffset, yOffset, zOffset))
				player.setActiveHand(hand);
		}
		return EnumActionResult.PASS;
	}

    @Override
	@SideOnly(Side.CLIENT)
    public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side,
    		EntityPlayer player, @Nonnull ItemStack stack)
    {
    	return (player.isSneaking() ||
    			!world.getBlockState(pos).getBlock().equals(MFRThings.vineScaffoldBlock)) &&
    			super.canPlaceBlockOnSide(world, pos, side, player, stack);
    }
}
