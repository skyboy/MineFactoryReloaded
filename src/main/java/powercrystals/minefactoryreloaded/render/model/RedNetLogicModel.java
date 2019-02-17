package powercrystals.minefactoryreloaded.render.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class RedNetLogicModel extends ModelBase
{
	ModelRenderer Top;
	ModelRenderer Bottom;
	ModelRenderer North;
	ModelRenderer South;
	ModelRenderer West;
	ModelRenderer Mandatory1;
	ModelRenderer Mandatory2;
	ModelRenderer Mandatory3;
	ModelRenderer Mandatory4;
	
	public RedNetLogicModel()
	{
		textureWidth = 64;
		textureHeight = 64;
		
		Top = new ModelRenderer(this, 0, 0);
		Top.mirror = true;
		Top.addBox(-8F, 0, -8F, 16, 1, 16);
		Top.setRotationPoint(0F, -7, 0F);
		Top.setTextureSize(textureWidth, textureHeight);
		setRotation(Top, 0, 0F,(float)Math.PI);
		Bottom = new ModelRenderer(this, 0, 0);
		Bottom.addBox(-8F, 7F, -8F, 16, 1, 16);
		Bottom.setRotationPoint(0F, 0F, 0F);
		Bottom.setTextureSize(textureWidth, textureHeight);
		setRotation(Bottom, 0F, 0F, 0F);
		North = new ModelRenderer(this, 30, 17);
		North.mirror = true;
		North.addBox(-8F, -7F, -8F, 16, 14, 1);
		North.setRotationPoint(0F, 0F, 0F);
		North.setTextureSize(textureWidth, textureHeight);
		setRotation(North, 0F, 0F, 0F);
		South = new ModelRenderer(this, 30, 17);
		South.addBox(-8F, -7F, 0F, 16, 14, 1);
		South.setRotationPoint(0F, 0F, 8F);
		South.setTextureSize(textureWidth, textureHeight);
		setRotation(South, 0F, (float)Math.PI, 0F);
		West = new ModelRenderer(this, 0, 17);
		West.addBox(-8F, -7F, -7F, 1, 14, 14);
		West.setRotationPoint(0F, 0F, 0F);
		West.setTextureSize(textureWidth, textureHeight);
		setRotation(West, 0F, 0F, 0F);
		
		int cardU = 1, cardV = 47;
		Mandatory1 = new ModelRenderer(this, cardU, cardV);
		Mandatory1.addBox(-7F, -6F, 5F, 14, 5, 1);
		Mandatory1.setRotationPoint(0F, 0F, 0F);
		Mandatory1.setTextureSize(textureWidth, textureHeight);
		setRotation(Mandatory1, 0F, 0F, 0F);
		Mandatory2 = new ModelRenderer(this, cardU, cardV);
		Mandatory2.addBox(-7F, 0F, 5F, 14, 5, 1);
		Mandatory2.setRotationPoint(0F, 0F, 0F);
		Mandatory2.setTextureSize(textureWidth, textureHeight);
		setRotation(Mandatory2, 0F, 0F, 0F);
		cardV = 53;
		Mandatory3 = new ModelRenderer(this, cardU, cardV);
		Mandatory3.addBox(-7F, 5.5F, -6F, 14, 1, 5);
		Mandatory3.setRotationPoint(0F, 0F, 0F);
		Mandatory3.setTextureSize(textureWidth, textureHeight);
		setRotation(Mandatory3, 0F, 0F, 0F);
		Mandatory4 = new ModelRenderer(this, cardU, cardV);
		Mandatory4.addBox(-7F, 5.5F, 0F, 14, 1, 5);
		Mandatory4.setRotationPoint(0F, 0F, 0F);
		Mandatory4.setTextureSize(textureWidth, textureHeight);
		setRotation(Mandatory4, 0F, 0F, 0F);
	}
	
	public void render(float f5)
	{
		Top.render(f5);
		Bottom.render(f5);
		North.render(f5);
		South.render(f5);
		West.render(f5);
		Mandatory1.render(f5);
		Mandatory2.render(f5);
		Mandatory3.render(f5);
		Mandatory4.render(f5);
	}
	
	private void setRotation(ModelRenderer model, float x, float y, float z)
	{
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}
}