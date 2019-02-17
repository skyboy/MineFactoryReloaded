package powercrystals.minefactoryreloaded.render.tileentity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

public class LaserRendererBase {

	public static void setColor(int color) {

		r = (color >> 16) & 255;
		g = (color >> 8) & 255;
		b = (color >> 0) & 255;
	}

	private static int r = 255, g = 255, b = 255;

	public static void renderLaser(TileEntity host, double x, double y, double z, int length, EnumFacing orientation, float partialTicks) {

		Tessellator tessellator = Tessellator.getInstance();
		GlStateManager.pushMatrix();

		double xStart = x;
		double yStart = y;
		double zStart = z;

		switch (orientation) {
		case DOWN:
			yStart -= length;
			break;
		case NORTH:
			GlStateManager.rotate(270, 1, 0, 0);
			yStart = -z;
			zStart = y;
			break;
		case SOUTH:
			GlStateManager.rotate(90, 1, 0, 0);
			yStart = z + 1;
			zStart = -y - 1;
			break;
		case EAST:
			GlStateManager.rotate(270, 0, 0, 1);
			yStart = x + 1;
			xStart = -y - 1;
			break;
		case WEST:
			GlStateManager.rotate(90, 0, 0, 1);
			yStart = -x;
			xStart = y;
			break;
		default:
			break;
		}
		GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
		GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
		GL11.glPushAttrib(GL11.GL_LIGHTING_BIT);
		RenderHelper.disableStandardItemLighting();
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
		GlStateManager.disableCull();
		GlStateManager.disableBlend();
		GlStateManager.depthMask(true);
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		float ticks = (Minecraft.getMinecraft().ingameGUI.getUpdateCounter() + partialTicks);
		float f3 = -ticks * 0.2F - MathHelper.floor(-ticks * 0.1F);
		double d3 = ticks * 0.025D * (1.0D - 2.5D);
		BufferBuilder buffer = tessellator.getBuffer();
		buffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
		double d4 = 0.2D;
		double d5 = 0.5D + Math.cos(d3 + 2.356194490192345D) * d4;
		double d6 = 0.5D + Math.sin(d3 + 2.356194490192345D) * d4;
		double d7 = 0.5D + Math.cos(d3 + (Math.PI / 4D)) * d4;
		double d8 = 0.5D + Math.sin(d3 + (Math.PI / 4D)) * d4;
		double d9 = 0.5D + Math.cos(d3 + 3.9269908169872414D) * d4;
		double d10 = 0.5D + Math.sin(d3 + 3.9269908169872414D) * d4;
		double d11 = 0.5D + Math.cos(d3 + 5.497787143782138D) * d4;
		double d12 = 0.5D + Math.sin(d3 + 5.497787143782138D) * d4;
		double height = length;
		double uStart = 0.0D;
		double uEnd = 1.0D;
		double vStart = -1.0F + f3;
		double vEnd = (height * 4) + vStart;
		buffer.pos(xStart + d5, yStart + height, zStart + d6).tex(uEnd, vEnd).color(r, g, b, 32).endVertex();
		buffer.pos(xStart + d5, yStart, zStart + d6).tex(uEnd, vStart).color(r, g, b, 32).endVertex();
		buffer.pos(xStart + d7, yStart, zStart + d8).tex(uStart, vStart).color(r, g, b, 32).endVertex();
		buffer.pos(xStart + d7, yStart + height, zStart + d8).tex(uStart, vEnd).color(r, g, b, 32).endVertex();
		buffer.pos(xStart + d11, yStart + height, zStart + d12).tex(uEnd, vEnd).color(r, g, b, 32).endVertex();
		buffer.pos(xStart + d11, yStart, zStart + d12).tex(uEnd, vStart).color(r, g, b, 32).endVertex();
		buffer.pos(xStart + d9, yStart, zStart + d10).tex(uStart, vStart).color(r, g, b, 32).endVertex();
		buffer.pos(xStart + d9, yStart + height, zStart + d10).tex(uStart, vEnd).color(r, g, b, 32).endVertex();
		buffer.pos(xStart + d7, yStart + height, zStart + d8).tex(uEnd, vEnd).color(r, g, b, 32).endVertex();
		buffer.pos(xStart + d7, yStart, zStart + d8).tex(uEnd, vStart).color(r, g, b, 32).endVertex();
		buffer.pos(xStart + d11, yStart, zStart + d12).tex(uStart, vStart).color(r, g, b, 32).endVertex();
		buffer.pos(xStart + d11, yStart + height, zStart + d12).tex(uStart, vEnd).color(r, g, b, 32).endVertex();
		buffer.pos(xStart + d9, yStart + height, zStart + d10).tex(uEnd, vEnd).color(r, g, b, 32).endVertex();
		buffer.pos(xStart + d9, yStart, zStart + d10).tex(uEnd, vStart).color(r, g, b, 32).endVertex();
		buffer.pos(xStart + d5, yStart, zStart + d6).tex(uStart, vStart).color(r, g, b, 32).endVertex();
		buffer.pos(xStart + d5, yStart + height, zStart + d6).tex(uStart, vEnd).color(r, g, b, 32).endVertex();
		tessellator.draw();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.depthMask(false);
		buffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
		double d18 = 0.2D;
		double d19 = 0.2D;
		double d20 = 0.8D;
		double d21 = 0.2D;
		double d22 = 0.2D;
		double d23 = 0.8D;
		double d24 = 0.8D;
		double d25 = 0.8D;
		double d29 = -1.0F + f3;
		double d30 = (height * 2) + d29;
		buffer.pos(xStart + d18, yStart + height, zStart + d19).tex(uEnd, d30).color(255, 255, 255, 48).endVertex();
		buffer.pos(xStart + d18, yStart, zStart + d19).tex(uEnd, d29).color(255, 255, 255, 48).endVertex();
		buffer.pos(xStart + d20, yStart, zStart + d21).tex(uStart, d29).color(255, 255, 255, 48).endVertex();
		buffer.pos(xStart + d20, yStart + height, zStart + d21).tex(uStart, d30).color(255, 255, 255, 48).endVertex();
		buffer.pos(xStart + d24, yStart + height, zStart + d25).tex(uEnd, d30).color(255, 255, 255, 48).endVertex();
		buffer.pos(xStart + d24, yStart, zStart + d25).tex(uEnd, d29).color(255, 255, 255, 48).endVertex();
		buffer.pos(xStart + d22, yStart, zStart + d23).tex(uStart, d29).color(255, 255, 255, 48).endVertex();
		buffer.pos(xStart + d22, yStart + height, zStart + d23).tex(uStart, d30).color(255, 255, 255, 48).endVertex();
		buffer.pos(xStart + d20, yStart + height, zStart + d21).tex(uEnd, d30).color(255, 255, 255, 48).endVertex();
		buffer.pos(xStart + d20, yStart, zStart + d21).tex(uEnd, d29).color(255, 255, 255, 48).endVertex();
		buffer.pos(xStart + d24, yStart, zStart + d25).tex(uStart, d29).color(255, 255, 255, 48).endVertex();
		buffer.pos(xStart + d24, yStart + height, zStart + d25).tex(uStart, d30).color(255, 255, 255, 48).endVertex();
		buffer.pos(xStart + d22, yStart + height, zStart + d23).tex(uEnd, d30).color(255, 255, 255, 48).endVertex();
		buffer.pos(xStart + d22, yStart, zStart + d23).tex(uEnd, d29).color(255, 255, 255, 48).endVertex();
		buffer.pos(xStart + d18, yStart, zStart + d19).tex(uStart, d29).color(255, 255, 255, 48).endVertex();
		buffer.pos(xStart + d18, yStart + height, zStart + d19).tex(uStart, d30).color(255, 255, 255, 48).endVertex();

		tessellator.draw();
		setColor(0xFFFFFF);
		GlStateManager.enableLighting();
		GlStateManager.enableTexture2D();
		GlStateManager.depthMask(true);
		GL11.glPopAttrib();
		GlStateManager.popMatrix();
	}

}
