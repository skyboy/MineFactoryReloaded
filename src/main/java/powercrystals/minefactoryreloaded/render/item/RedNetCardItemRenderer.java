package powercrystals.minefactoryreloaded.render.item;

import codechicken.lib.texture.TextureUtils;
import codechicken.lib.util.TransformUtils;
import com.google.common.collect.ImmutableMap;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.model.TRSRTransformation;
import powercrystals.minefactoryreloaded.MFRProps;
import powercrystals.minefactoryreloaded.render.model.RedNetCardsModel;

import javax.annotation.Nonnull;

public class RedNetCardItemRenderer extends BaseItemRenderer {

	private static RedNetCardsModel cardsModel = new RedNetCardsModel();
	public static ResourceLocation textureLocation = new ResourceLocation(MFRProps.TILE_ENTITY_FOLDER + "cards.png");
	
	public RedNetCardItemRenderer() {

		TRSRTransformation thirdPerson = TransformUtils.create(0, 5, 3, 90, 180, 0, 0.7f);
		ImmutableMap.Builder<ItemCameraTransforms.TransformType, TRSRTransformation> builder = ImmutableMap.builder();
		builder.put(ItemCameraTransforms.TransformType.GUI, TransformUtils.create(-3, 2, 0, 30, -45, 0, 1f));
		builder.put(ItemCameraTransforms.TransformType.GROUND, TransformUtils.create(0, 3, 0, 0, 0, 0, 0.7f));
		builder.put(ItemCameraTransforms.TransformType.FIXED, TransformUtils.create(1, 8, 10, 0, 0, 0, 1.3f));
		builder.put(ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, thirdPerson);
		builder.put(ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND, TransformUtils.flipLeft(thirdPerson));
		builder.put(ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND, TransformUtils.create(3, 6, 0, -45, 0, 15, 0.7f));
		builder.put(ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND, TransformUtils.create(3, 6, 0, -45, 0, 15, 0.7f));
		transformations =  builder.build();
	}

	@Override
	public void renderItem(@Nonnull ItemStack stack, ItemCameraTransforms.TransformType transformType) {

		GlStateManager.pushMatrix();

		TextureUtils.changeTexture(textureLocation);

		switch(stack.getItemDamage())
		{
			case 0:
				cardsModel.renderLevel1(0.0625f);
				break;
			case 1:
				cardsModel.renderLevel2(0.0625f);
				break;
			case 2:
				cardsModel.renderLevel3(0.0625f);
				break;
			default:
				cardsModel.renderEmptySlot(0.0625f);
		}

		GlStateManager.popMatrix();
	}
}
