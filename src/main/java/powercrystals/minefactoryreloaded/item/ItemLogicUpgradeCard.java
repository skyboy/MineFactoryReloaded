package powercrystals.minefactoryreloaded.item;

import codechicken.lib.model.ModelRegistryHelper;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import powercrystals.minefactoryreloaded.MFRProps;
import powercrystals.minefactoryreloaded.item.base.ItemMulti;
import powercrystals.minefactoryreloaded.render.item.RedNetCardItemRenderer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemLogicUpgradeCard extends ItemMulti {

	private static String[] _upgradeNames = { "100", "300", "500" };

	public ItemLogicUpgradeCard() {

		setNames(_upgradeNames);
		setUnlocalizedName("mfr.upgrade.logic");
		setMaxStackSize(1);
	}

	@Override
	public void addInformation(@Nonnull ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag tooltipFlag) {

		super.addInformation(stack, world, tooltip, tooltipFlag);
		tooltip.add("Circuits: " + getCircuitsForLevel(stack.getItemDamage() + 1));
		tooltip.add("Variables: " + getVariablesForLevel(stack.getItemDamage() + 1));
	}

	public static int getCircuitsForLevel(int level) {

		return level == 0 ? 0 : 1 + 2 * (level - 1);
	}

	public static int getVariablesForLevel(int level) {

		return level == 0 ? 0 : 8 * level;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels() {

		ModelResourceLocation rednetCard = new ModelResourceLocation(MFRProps.PREFIX + "rednet_card", "inventory");
		ModelLoader.setCustomMeshDefinition(this, stack -> rednetCard);
		ModelLoader.registerItemVariants(this, rednetCard);
		ModelRegistryHelper.register(rednetCard, new RedNetCardItemRenderer());
	}
}
