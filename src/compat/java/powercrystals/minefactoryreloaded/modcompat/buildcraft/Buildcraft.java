package powercrystals.minefactoryreloaded.modcompat.buildcraft;

import buildcraft.api.fuels.BuildcraftFuelRegistry;
import buildcraft.api.tools.IToolWrench;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fluids.FluidRegistry;
import powercrystals.minefactoryreloaded.api.handler.IFactoryTool;
import powercrystals.minefactoryreloaded.api.integration.IMFRIntegrator;

import static powercrystals.minefactoryreloaded.modcompat.Compats.ModIds.BUILDCRAFT;

@IMFRIntegrator.DependsOn(BUILDCRAFT)
public class Buildcraft implements IMFRIntegrator {

	@Override
	public void load() {

		REGISTRY.addToolHandler(new IFactoryTool() {

			@Override
			public boolean isFactoryToolUsable(EntityPlayer player, EnumHand hand, ItemStack stack, BlockPos pos, EnumFacing side) {

				if (stack.getItem() instanceof IToolWrench)
					return ((IToolWrench)stack.getItem()).canWrench(player, hand, stack, new RayTraceResult(new Vec3d(pos), side, pos));
				return false;
			}

			@Override
			public boolean onFactoryToolUsed(EntityPlayer player, EnumHand hand, ItemStack stack, BlockPos pos, EnumFacing side) {

				if (stack.getItem() instanceof IToolWrench) {
					((IToolWrench) stack.getItem()).wrenchUsed(player, hand, stack, new RayTraceResult(new Vec3d(pos), side, pos));
					return true;
				}
				return false;
			}
		});
	}

	@Override
	public void postLoad() {

		if (BuildcraftFuelRegistry.fuel != null)
			BuildcraftFuelRegistry.fuel.addFuel(FluidRegistry.getFluid("biofuel"), 40, 15000);
	}

}
