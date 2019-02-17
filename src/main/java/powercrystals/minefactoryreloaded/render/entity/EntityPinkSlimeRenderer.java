package powercrystals.minefactoryreloaded.render.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSlime;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.util.ResourceLocation;
import powercrystals.minefactoryreloaded.MFRProps;

public class EntityPinkSlimeRenderer extends RenderSlime {

	private static final ResourceLocation pinkSlimeTexture = new ResourceLocation(
			MFRProps.MOB_TEXTURE_FOLDER + "pinkslime.png");

	public EntityPinkSlimeRenderer(RenderManager renderManager, ModelBase modelBase, float shadowSize) {

		super(renderManager);
		this.mainModel = modelBase;
		this.shadowSize = shadowSize;
	}

	@Override
	protected ResourceLocation getEntityTexture(EntitySlime par1EntitySlime) {

		return pinkSlimeTexture;
	}

}
