package powercrystals.minefactoryreloaded.render.tileentity;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import powercrystals.minefactoryreloaded.tile.machine.power.TileEntityLaserDrillPrecharger;

public class LaserDrillPrechargerRenderer extends TileEntitySpecialRenderer<TileEntityLaserDrillPrecharger> {

	@Override
	public void render(TileEntityLaserDrillPrecharger laserDrillPrecharger, double x, double y, double z, float partialTicks,
			int destroyStage, float alpha) {

		if (laserDrillPrecharger.shouldDrawBeam()) {
			this.bindTexture(LaserDrillRenderer.beaconBeam);
			LaserRendererBase
					.renderLaser(laserDrillPrecharger, x, y, z, 1, laserDrillPrecharger.getDirectionFacing(), partialTicks);
		}
	}
}
