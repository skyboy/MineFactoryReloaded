package powercrystals.minefactoryreloaded.render.entity;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import powercrystals.minefactoryreloaded.entity.EntityNeedle;

@SideOnly(Side.CLIENT)
public class EntityNeedleRenderer extends Render {

	private static final ResourceLocation needle = new ResourceLocation("textures/entity/arrow.png");

	public EntityNeedleRenderer(RenderManager renderManager) {

		super(renderManager);
	}

	public void renderNeedle(EntityNeedle needle, double x, double y, double z, float entityYaw, float partialTicks) {

		GlStateManager.pushMatrix();
		GlStateManager.translate((float) x, (float) y, (float) z);
		GlStateManager
				.rotate(needle.prevRotationYaw + (needle.rotationYaw - needle.prevRotationYaw) * partialTicks - 90.0F, 0.0F, 1.0F,
						0.0F);
		GlStateManager
				.rotate(needle.prevRotationPitch + (needle.rotationPitch - needle.prevRotationPitch) * partialTicks, 0.0F, 0.0F,
						1.0F);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();
		GlStateManager.enableRescaleNormal();

		GlStateManager.rotate(45.0F, 1.0F, 0.0F, 0.0F);
		GlStateManager.scale(0.05625F, 0.05625F, 0.05625F);
		GlStateManager.translate(-4.0F, 0.0F, 0.0F);

		GlStateManager.glNormal3f(0.05625F, 0.0F, 0.0F);
		buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
		buffer.pos(-7.0D, -2.0D, -2.0D).tex(0.0D, 0.15625D).endVertex();
		buffer.pos(-7.0D, -2.0D, 2.0D).tex(0.15625D, 0.15625D).endVertex();
		buffer.pos(-7.0D, 2.0D, 2.0D).tex(0.15625D, 0.3125D).endVertex();
		buffer.pos(-7.0D, 2.0D, -2.0D).tex(0.0D, 0.3125D).endVertex();
		tessellator.draw();
		GlStateManager.glNormal3f(-0.05625F, 0.0F, 0.0F);
		buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
		buffer.pos(-7.0D, 2.0D, -2.0D).tex(0.0D, 0.15625D).endVertex();
		buffer.pos(-7.0D, 2.0D, 2.0D).tex(0.15625D, 0.15625D).endVertex();
		buffer.pos(-7.0D, -2.0D, 2.0D).tex(0.15625D, 0.3125D).endVertex();
		buffer.pos(-7.0D, -2.0D, -2.0D).tex(0.0D, 0.3125D).endVertex();
		tessellator.draw();

		for (int j = 0; j < 4; ++j) {
			GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
			GlStateManager.glNormal3f(0.0F, 0.0F, 0.05625F);
			buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
			buffer.pos(-8.0D, -2.0D, 0.0D).tex(0.0D, 0.0D).endVertex();
			buffer.pos(8.0D, -2.0D, 0.0D).tex(0.5D, 0.0D).endVertex();
			buffer.pos(8.0D, 2.0D, 0.0D).tex(0.5D, 0.15625D).endVertex();
			buffer.pos(-8.0D, 2.0D, 0.0D).tex(0.0D, 0.15625D).endVertex();
			tessellator.draw();
		}

		GlStateManager.disableRescaleNormal();
		GlStateManager.popMatrix();
	}

	@Override
	public void doRender(Entity entity, double par2, double par4, double par6, float par8, float par9) {

		this.bindEntityTexture(entity);
		this.renderNeedle((EntityNeedle) entity, par2, par4, par6, par8, par9);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {

		return needle;
	}
}
