package powercrystals.minefactoryreloaded.render.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import org.lwjgl.opengl.GL11;
import powercrystals.minefactoryreloaded.entity.DebugTracker;

public class EntityDebugTrackerRenderer extends Render {

	protected EntityDebugTrackerRenderer(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	public void doRender(Entity p_76986_1_, double x, double y, double z, float yaw, float partialTicks) {

		EntityPlayer player = Minecraft.getMinecraft().player;

		float playerOffsetX = -(float)(player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks);
		float playerOffsetY = -(float)(player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks);
		float playerOffsetZ = -(float)(player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks);

		DebugTracker ent = (DebugTracker) p_76986_1_;
		GlStateManager.colorMask(true, true, true, true);
		GlStateManager.enableAlpha();
		GlStateManager.disableFog();
		GlStateManager.enableCull();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.disableTexture2D();
		GlStateManager.shadeModel(GL11.GL_FLAT);

		GlStateManager.pushMatrix();
		x = ent.posX;
		y = ent.posY;
		z = ent.posZ;

		{
			float r = colorFromCoord(Float.floatToIntBits((float) z), 0xF8525888);
			float g = colorFromCoord(Float.floatToIntBits((float) x), 0x85BDBD8C);
			float b = colorFromCoord(Float.floatToIntBits((float) y), 0x997696BF);

			GlStateManager.pushMatrix();
			GlStateManager.color(r, g, b, 0.4F);
			GlStateManager.translate(playerOffsetX, playerOffsetY, playerOffsetZ);
			AxisAlignedBB bb = ent.getPrjBB();
			float q = ent.getDataManager().get(DebugTracker.PROJECTILE_MOTION_X);
			float w = ent.getDataManager().get(DebugTracker.PROJECTILE_MOTION_Y);
			float e = ent.getDataManager().get(DebugTracker.PROJECTILE_MOTION_Z);
			for (int i = 3; i --> 0; ) {
				renderOffsetAABB(bb, 0, 0, 0);
				bb.offset(q, w, e);
			}
			GlStateManager.popMatrix();
		}

		{
			float r = colorFromCoord(Float.floatToIntBits((float) x), 0xF8525888);
			float g = colorFromCoord(Float.floatToIntBits((float) y), 0x85BDBD8C);
			float b = colorFromCoord(Float.floatToIntBits((float) z), 0x997696BF);

			GlStateManager.pushMatrix();
			GlStateManager.color(r, g, b, 0.4F);
			GlStateManager.translate(playerOffsetX, playerOffsetY, playerOffsetZ);
			AxisAlignedBB bb = ent.getSrcBB();
			renderOffsetAABB(bb, 0, 0, 0);
			float e = ent.getDataManager().get(DebugTracker.ENTITY_EYE_HEIGHT);
			double minY = bb.minY + e;
			bb = new AxisAlignedBB(bb.minX, minY, bb.minZ, bb.maxX, minY + (1/8D), bb.maxZ);
			renderOffsetAABB(bb.grow(0.006, 0, 0.006), 0, 0, 0);
			GlStateManager.popMatrix();
		}

		GlStateManager.popMatrix();

		GlStateManager.enableTexture2D();

	}

	private float colorFromCoord(int c, long h)
	{

		h = (h * c) + 0xBA;
		h ^= (h >>> 20) ^ (h >>> 12);
		h ^= (h >>> 7) ^ (h >>> 4);
		return ((h & 255) / 319f) + 0.1f; // odd values bound to 0.1 <= x < 0.9
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity p_110775_1_) {

		return null;
	}

}
