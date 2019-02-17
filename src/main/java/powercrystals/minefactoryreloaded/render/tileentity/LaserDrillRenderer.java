package powercrystals.minefactoryreloaded.render.tileentity;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.EnumFacing;

import powercrystals.minefactoryreloaded.tile.machine.processing.TileEntityLaserDrill;

@SideOnly(Side.CLIENT)
public class LaserDrillRenderer extends TileEntitySpecialRenderer<TileEntityLaserDrill> {

	public static final ResourceLocation beaconBeam = new ResourceLocation("textures/entity/beacon_beam.png");

	@Override
	public void render(TileEntityLaserDrill laserDrill, double x, double y, double z, float partialTicks, int destroyStage,
			float alpha) {

		if (laserDrill.shouldDrawBeam()) {
			this.bindTexture(beaconBeam);
			LaserRendererBase.setColor(laserDrill.getColor());
			LaserRendererBase.renderLaser(laserDrill, x, y, z, laserDrill.getBeamHeight(), EnumFacing.DOWN, partialTicks);
		}
	}
}
