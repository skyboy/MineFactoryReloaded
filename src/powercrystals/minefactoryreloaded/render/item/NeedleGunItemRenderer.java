package powercrystals.minefactoryreloaded.render.item;

import cofh.repack.codechicken.lib.lighting.LightModel;
import cofh.repack.codechicken.lib.render.CCModel;
import cofh.repack.codechicken.lib.render.CCRenderState;
import cofh.repack.codechicken.lib.vec.Scale;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;

@SideOnly(Side.CLIENT)
public class NeedleGunItemRenderer implements IItemRenderer
{
	private static final ResourceLocation needleGun =
			new ResourceLocation(MineFactoryReloadedCore.modelTextureFolder + "NeedleGun.png");
	private static CCModel base;
	private static CCModel mag;

	public static void updateModel() {
		try
		{
			Map<String, CCModel> gunModels = CCModel.parseObjModels(new ResourceLocation(
					MineFactoryReloadedCore.modelFolder + "NeedleGun.obj"), 4, new Scale(0.03, 0.03, 0.03));
			base = gunModels.get("gun").backfacedCopy();
			mag = gunModels.get("magazine").backfacedCopy();

			base.computeNormals();
			base.computeLighting(LightModel.standardLightModel);

			mag.computeNormals();
			mag.computeLighting(LightModel.standardLightModel);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type)
	{
		return true;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper)
	{
		return helper != ItemRendererHelper.EQUIPPED_BLOCK;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data)
	{
		GL11.glEnable(GL11.GL_LIGHTING);
		TextureManager renderengine = Minecraft.getMinecraft().renderEngine;

		if (renderengine != null)
		{
			renderengine.bindTexture(needleGun);
		}

		CCRenderState.reset();
		GL11.glPushMatrix();

		if (type == ItemRenderType.EQUIPPED_FIRST_PERSON)
		{
			GL11.glRotatef(270, 0, 1, 0);
			GL11.glRotatef(300, 1, 0, 0);
			GL11.glTranslatef(-0.2F, 0.5F, 0.2F);
		}
		else if (type == ItemRenderType.EQUIPPED)
		{
			GL11.glRotatef(270, 1, 0, 0);
			GL11.glTranslatef(1.0F, 0, 0.2F);
		}
		else
		{
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glRotatef(270, 1, 0, 0);
			GL11.glTranslatef(0, -0.4F, 0);
		}

		Tessellator.instance.startDrawing(4);
		base.render();
		if (item.stackTagCompound != null && item.stackTagCompound.hasKey("ammo") &&
				!item.stackTagCompound.getCompoundTag("ammo").hasNoTags())
			mag.render();
		Tessellator.instance.draw();

		GL11.glPopMatrix();
		GL11.glEnable(GL11.GL_LIGHTING);
	}
}
