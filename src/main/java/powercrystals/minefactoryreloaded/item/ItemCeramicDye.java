package powercrystals.minefactoryreloaded.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import powercrystals.minefactoryreloaded.item.base.ItemFactoryColored;
import powercrystals.minefactoryreloaded.render.ModelHelper;
import powercrystals.minefactoryreloaded.setup.MFRThings;

import javax.annotation.Nonnull;

public class ItemCeramicDye extends ItemFactoryColored {

	public ItemCeramicDye() {

		setUnlocalizedName("mfr.ceramic.dye");
	}

	@Override
	public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {

		@Nonnull ItemStack stack = player.getHeldItem(hand);

		Block block = world.getBlockState(pos).getBlock();
		if (!world.isRemote) {
			if (Blocks.GLASS.equals(block)) {
				if (world.setBlockState(pos, MFRThings.factoryGlassBlock.getStateFromMeta(stack.getItemDamage()), 3)) {
					if (!player.capabilities.isCreativeMode)
						stack.shrink(1);
					return EnumActionResult.SUCCESS;
				}
			}
			if (Blocks.GLASS_PANE.equals(block)) {
				if (world.setBlockState(pos, MFRThings.factoryGlassPaneBlock.getStateFromMeta(stack.getItemDamage()), 3)) {
					if (!player.capabilities.isCreativeMode)
						stack.shrink(1);
					return EnumActionResult.SUCCESS;
				}
			}
			if (block.recolorBlock(world, pos, side, EnumDyeColor.byMetadata(stack.getItemDamage()))) {
				if (!player.capabilities.isCreativeMode)
					stack.shrink(1);
				return EnumActionResult.SUCCESS;
			}
		}
		return EnumActionResult.PASS;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels() {

		ModelHelper.registerColoredItemModels(this, "ceramic_dye");
	}
}
