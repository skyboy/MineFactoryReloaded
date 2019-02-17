package powercrystals.minefactoryreloaded.item.tool;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.api.handler.ILiquidDrinkHandler;
import powercrystals.minefactoryreloaded.item.base.ItemFactoryTool;
import powercrystals.minefactoryreloaded.render.ModelHelper;

import javax.annotation.Nonnull;
import java.util.Map;

public class ItemStraw extends ItemFactoryTool {

	public ItemStraw() {

		setUnlocalizedName("mfr.straw");
		setMaxStackSize(1);
	}

	@Nonnull
	@Override
	public ItemStack onItemUseFinish(@Nonnull ItemStack stack, World world, EntityLivingBase entity) {

		if (!world.isRemote && entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entity;
			RayTraceResult result = rayTrace(world, player, true);
			Map<String, ILiquidDrinkHandler> map = MFRRegistry.getLiquidDrinkHandlers();
			if (result != null && result.typeOfHit == Type.BLOCK) {
				BlockPos pos = result.getBlockPos();
				IBlockState state = world.getBlockState(pos);
				Block block = state.getBlock();
				Fluid fluid = FluidRegistry.lookupFluidForBlock(block);
				if (fluid != null && map.containsKey(fluid.getName())) {
					map.get(fluid.getName()).onDrink(player, new FluidStack(fluid, 1000));
					world.setBlockToAir(pos);
				} else if (block.hasTileEntity(state)) {
					TileEntity tile = world.getTileEntity(pos);
					if (tile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, result.sideHit)) {
						IFluidHandler handler = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, result.sideHit);
						IFluidTankProperties[] info = handler.getTankProperties();
						for (int i = info.length; i-- > 0;) {
							FluidStack fStack = info[i].getContents();
							if (fStack != null) {
								fluid = fStack.getFluid();
								if (fluid != null && map.containsKey(fluid.getName()) && fStack.amount >= 1000) {
									fStack = fStack.copy();
									fStack.amount = 1000;
									FluidStack r = handler.drain(fStack.copy(), false);
									if (r != null && r.amount >= 1000) {
										map.get(fluid.getName()).onDrink(player, r);
										handler.drain(fStack, true);
										break;
									}
								}
							}
						}
					}
				}
			}
		}

		return stack;
	}

	@Override
	public int getMaxItemUseDuration(@Nonnull ItemStack stack) {

		return 32;
	}

	@Override
	public EnumAction getItemUseAction(@Nonnull ItemStack stack) {

		return EnumAction.DRINK;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {

		@Nonnull ItemStack stack = player.getHeldItem(hand);

		RayTraceResult result = rayTrace(world, player, true);
		Map<String, ?> map = MFRRegistry.getLiquidDrinkHandlers();
		if (result != null && result.typeOfHit == Type.BLOCK) {
			BlockPos pos = result.getBlockPos();
			IBlockState state = world.getBlockState(pos);
			Block block = state.getBlock();
			Fluid fluid = FluidRegistry.lookupFluidForBlock(block);
			if (fluid != null && map.containsKey(fluid.getName())) {
				player.setActiveHand(hand);
			} else if (block.hasTileEntity(state)) {
				TileEntity tile = world.getTileEntity(pos);
				if (tile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, result.sideHit)) {
					IFluidHandler handler = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, result.sideHit);
					IFluidTankProperties[] info = handler.getTankProperties();
					for (int i = info.length; i-- > 0;) {
						FluidStack fStack = info[i].getContents();
						if (fStack != null) {
							fluid = fStack.getFluid();
							if (fluid != null && map.containsKey(fluid.getName()) && fStack.amount >= 1000) {
								FluidStack r = handler.drain(fStack, false);
								if (r != null && r.amount >= 1000) {
									player.setActiveHand(hand);
									break;
								}
							}
						}
					}
				}
			}
		}
		return new ActionResult<>(EnumActionResult.SUCCESS, stack);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels() {

		ModelHelper.registerModel(this, "tool", "variant=straw");
	}
}

