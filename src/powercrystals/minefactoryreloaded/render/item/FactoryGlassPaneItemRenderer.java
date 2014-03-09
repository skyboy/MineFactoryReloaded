package powercrystals.minefactoryreloaded.render.item;

import cpw.mods.fml.client.registry.RenderingRegistry;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.block.BlockFactoryGlassPane;

public class FactoryGlassPaneItemRenderer implements IItemRenderer
{
	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type)
	{
		return type.ordinal() < ItemRenderType.FIRST_PERSON_MAP.ordinal();
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper)
	{
		return helper.ordinal() < ItemRendererHelper.EQUIPPED_BLOCK.ordinal();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data)
	{
		RenderBlocks renderer = (RenderBlocks)data[0];

		BlockFactoryGlassPane pane = (BlockFactoryGlassPane)Block.blocksList[((ItemBlock)item.getItem()).getBlockID()];

		GL11.glPushMatrix();
		GL11.glDisable(GL11.GL_CULL_FACE);

		if(type == ItemRenderType.INVENTORY)
		{
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glScalef(16f, 16f, 16f);
			GL11.glTranslatef(0.5f, 0.5f, 0.5f);

			RenderingRegistry.instance().renderInventoryBlock(renderer, pane, item.getItemDamage(), MineFactoryReloadedCore.renderIdFactoryGlassPane);

			GL11.glTranslatef(-0.5f, -0.5f, -0.5f);
			GL11.glScalef(1 / 16f, 1 / 16f, 1 / 16f);
			GL11.glEnable(GL11.GL_LIGHTING);
		}
		else
		{
			GL11.glEnable(GL12.GL_RESCALE_NORMAL);
			
			switch (type)
			{
			case EQUIPPED_FIRST_PERSON:
			case EQUIPPED:
				GL11.glTranslatef(10 / 16f, 7 / 16f, 0f);
				break;
			case ENTITY:
				GL11.glScalef(0.75f, 0.75f, 0.75f);
	            GL11.glTranslatef(0f, 4 / 16f, 0f);
				break;
			default:
			}

			RenderingRegistry.instance().renderInventoryBlock(renderer, pane, item.getItemDamage(), MineFactoryReloadedCore.renderIdFactoryGlassPane);

			GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		}
		
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glPopMatrix();
	}
}
