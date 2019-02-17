package powercrystals.minefactoryreloaded.render.entity;

import codechicken.lib.render.CCModel;
import codechicken.lib.render.OBJParser;
import codechicken.lib.render.CCRenderState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

import powercrystals.minefactoryreloaded.MFRProps;
import powercrystals.minefactoryreloaded.entity.EntityRocket;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class EntityRocketRenderer extends Render
{
	public static final ResourceLocation rocket =
			new ResourceLocation(MFRProps.MODEL_TEXTURE_FOLDER + "rocket.png");
	private CCModel model;

	public EntityRocketRenderer(RenderManager renderManager)
	{
		super(renderManager);
		try
		{
			model = OBJParser.parseModels(new ResourceLocation(
					MFRProps.MODEL_FOLDER + "rocket.obj")).get("Tube");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
    public void renderRocket(EntityRocket rocket, double x, double y, double z, float yaw, float partialTicks)
    {
		GlStateManager.pushMatrix();
		
		GlStateManager.translate((float)x, (float)y, (float)z);
		GlStateManager.rotate(rocket.prevRotationYaw + (rocket.rotationYaw - rocket.prevRotationYaw) * partialTicks - 90.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(90, 0, 0, 1);
		GlStateManager.rotate(rocket.prevRotationPitch + (rocket.rotationPitch - rocket.prevRotationPitch) * partialTicks, 0.0F, 0.0F, 1.0F);
		GlStateManager.scale(0.01F, 0.01F, 0.01F);

		CCRenderState ccrs = CCRenderState.instance();
		ccrs.reset();

		ccrs.startDrawing(4, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);

		model.render(ccrs);

		ccrs.draw();

		GlStateManager.popMatrix();
    }

    @Override
	public void doRender(Entity entity, double x, double y, double z, float yaw, float partialTicks)
    {
    	this.bindEntityTexture(entity);
        this.renderRocket((EntityRocket)entity, x, y, z, yaw, partialTicks);
    }

	@Override
	protected ResourceLocation getEntityTexture(Entity entity)
	{
		return rocket;
	}
}
