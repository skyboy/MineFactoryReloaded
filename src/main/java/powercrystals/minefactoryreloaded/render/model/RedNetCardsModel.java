package powercrystals.minefactoryreloaded.render.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class RedNetCardsModel extends ModelBase {
	// fields
	ModelRenderer Empty;
	ModelRenderer Lvl1Base;
	ModelRenderer Lvl1Fan;
	ModelRenderer Lvl2Base;
	ModelRenderer Lvl2Fan;
	ModelRenderer Lvl2Bit1;
	ModelRenderer Lvl2Bit2;
	ModelRenderer Lvl3Base;
	ModelRenderer Lvl3Fan;
	ModelRenderer Lvl3Bit1;
	ModelRenderer Lvl3Cord1;
	ModelRenderer Lvl3Cord2;
	ModelRenderer Lvl3Cord3;
	ModelRenderer Lvl3Bit2;
	
	public RedNetCardsModel() {
		textureWidth = 64;
		textureHeight = 32;
		
		Empty = new ModelRenderer(this, 42, 16);
		Empty.addBox(0F, 0F, 0F, 1, 5, 1);
		Empty.setRotationPoint(0F, 0F, 0F);
		Empty.setTextureSize(textureWidth, 24);
		setRotation(Empty, 0F, 0F, 0F);
		Lvl2Base = new ModelRenderer(this, 0, 0);
		Lvl2Base.addBox(0F, 0F, 0F, 14, 5, 1);
		Lvl2Base.setRotationPoint(0F, 0F, 0F);
		Lvl2Base.setTextureSize(textureWidth, textureHeight);
		setRotation(Lvl2Base, 0F, 0F, 0F);
		Lvl2Fan = new ModelRenderer(this, 30, 0);
		Lvl2Fan.addBox(3F, 1F, 1F, 3, 3, 1);
		Lvl2Fan.setRotationPoint(0F, 0F, 0F);
		Lvl2Fan.setTextureSize(textureWidth, textureHeight);
		setRotation(Lvl2Fan, 0F, 0F, 0F);
		Lvl2Bit1 = new ModelRenderer(this, 30, 4);
		Lvl2Bit1.addBox(0F, 0F, 0F, 3, 1, 1);
		Lvl2Bit1.setRotationPoint(9F, 1F, 1F);
		Lvl2Bit1.setTextureSize(textureWidth, textureHeight);
		setRotation(Lvl2Bit1, 0F, 0F, 0F);
		Lvl2Bit2 = new ModelRenderer(this, 30, 4);
		Lvl2Bit2.addBox(8F, 3F, 1F, 3, 1, 1);
		Lvl2Bit2.setRotationPoint(0F, 0F, 0F);
		Lvl2Bit2.setTextureSize(textureWidth, textureHeight);
		setRotation(Lvl2Bit2, 0F, 0F, 0F);
		Lvl3Base = new ModelRenderer(this, 0, 6);
		Lvl3Base.addBox(0F, 0F, 0F, 14, 5, 1);
		Lvl3Base.setRotationPoint(0F, 0F, 0F);
		Lvl3Base.setTextureSize(textureWidth, textureHeight);
		setRotation(Lvl3Base, 0F, 0F, 0F);
		Lvl3Fan = new ModelRenderer(this, 30, 6);
		Lvl3Fan.addBox(0F, 0F, 0F, 3, 3, 1);
		Lvl3Fan.setRotationPoint(3F, 1F, 1F);
		Lvl3Fan.setTextureSize(textureWidth, textureHeight);
		setRotation(Lvl3Fan, 0F, 0F, 0F);
		Lvl3Bit1 = new ModelRenderer(this, 38, 5);
		Lvl3Bit1.addBox(0F, 0F, 0F, 2, 2, 2);
		Lvl3Bit1.setRotationPoint(11F, 1F, 1F);
		Lvl3Bit1.setTextureSize(textureWidth, textureHeight);
		setRotation(Lvl3Bit1, 0F, 0F, 0F);
		Lvl3Cord1 = new ModelRenderer(this, 30, 10);
		Lvl3Cord1.addBox(9F, 3F, 1F, 3, 1, 1);
		Lvl3Cord1.setRotationPoint(0F, 0F, 0F);
		Lvl3Cord1.setTextureSize(textureWidth, textureHeight);
		setRotation(Lvl3Cord1, 0F, 0F, 0F);
		Lvl3Cord2 = new ModelRenderer(this, 38, 2);
		Lvl3Cord2.addBox(8F, 1F, 1F, 1, 2, 1);
		Lvl3Cord2.setRotationPoint(0F, 0F, 0F);
		Lvl3Cord2.setTextureSize(textureWidth, textureHeight);
		setRotation(Lvl3Cord2, 0F, 0F, 0F);
		Lvl3Cord3 = new ModelRenderer(this, 38, 0);
		Lvl3Cord3.addBox(6F, 1F, 1F, 2, 1, 1);
		Lvl3Cord3.setRotationPoint(0F, 0F, 0F);
		Lvl3Cord3.setTextureSize(textureWidth, textureHeight);
		setRotation(Lvl3Cord3, 0F, 0F, 0F);
		Lvl1Base = new ModelRenderer(this, 0, 12);
		Lvl1Base.addBox(0F, 0F, 0F, 14, 5, 1);
		Lvl1Base.setRotationPoint(0F, 0F, 0F);
		Lvl1Base.setTextureSize(textureWidth, textureHeight);
		setRotation(Lvl1Base, 0F, 0F, 0F);
		Lvl1Fan = new ModelRenderer(this, 30, 12);
		Lvl1Fan.addBox(3F, 1F, 1F, 3, 3, 1);
		Lvl1Fan.setRotationPoint(0F, 0F, 0F);
		Lvl1Fan.setTextureSize(textureWidth, textureHeight);
		setRotation(Lvl1Fan, 0F, 0F, 0F);
		Lvl3Bit2 = new ModelRenderer(this, 38, 5);
		Lvl3Bit2.addBox(6F, 3F, 1F, 2, 2, 2);
		Lvl3Bit2.setRotationPoint(0F, 0F, 0F);
		Lvl3Bit2.setTextureSize(textureWidth, textureHeight);
		setRotation(Lvl3Bit2, 0F, 0F, 0F);
	}
	
	public void renderEmptySlot(float scale) {
		Empty.render(scale);
	}
	
	public void renderLevel1(float scale) {
		Lvl1Base.render(scale);
		Lvl1Fan.render(scale);
	}
	
	public void renderLevel2(float scale) {
		Lvl2Base.render(scale);
		Lvl2Fan.render(scale);
		Lvl2Bit1.render(scale);
		Lvl2Bit2.render(scale);
	}
	
	public void renderLevel3(float scale) {
		Lvl3Base.render(scale);
		Lvl3Fan.render(scale);
		Lvl3Bit1.render(scale);
		Lvl3Bit2.render(scale);
		Lvl3Cord1.render(scale);
		Lvl3Cord2.render(scale);
		Lvl3Cord3.render(scale);
	}
	
	private void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}
}