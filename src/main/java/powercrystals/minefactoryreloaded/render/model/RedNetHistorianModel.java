package powercrystals.minefactoryreloaded.render.model;

import powercrystals.minefactoryreloaded.tile.rednet.TileEntityRedNetHistorian;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class RedNetHistorianModel extends ModelBase
{
	private ModelRenderer _main;
	
	public RedNetHistorianModel()
	{
		textureWidth = 64;
		textureHeight = 32;
		
		_main = new ModelRenderer(this, 0, 0);
		_main.addBox(0F, 0F, 0F, 16, 16, 4);
		_main.setRotationPoint(0F, 0F, 0F);
		_main.setTextureSize(64, 32);
		_main.mirror = true;
		setRotation(_main, 0F, 0F, 0F);
	}
	
	public void render(TileEntityRedNetHistorian.Client historian)
	{
		_main.render(0.0625F);
	}
	
	private void setRotation(ModelRenderer model, float x, float y, float z)
	{
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}
}
