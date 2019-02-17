package powercrystals.minefactoryreloaded.item.base;

import cofh.core.render.IModelRegister;
import cofh.core.util.core.IInitializer;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.core.MFRUtil;
import powercrystals.minefactoryreloaded.gui.MFRCreativeTab;
import powercrystals.minefactoryreloaded.render.ModelHelper;
import powercrystals.minefactoryreloaded.setup.MFRThings;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemFactory extends Item implements IInitializer, IModelRegister {

	protected String modelName;
	protected String variant;

	public ItemFactory() {

		setCreativeTab(MFRCreativeTab.tab);
		MFRThings.registerInitializer(this);
		MineFactoryReloadedCore.proxy.addModelRegister(this);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(@Nonnull ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag tooltipFlag) {

		super.addInformation(stack, world, tooltip, tooltipFlag);
		String str = "tip.info" + getUnlocalizedName(stack).substring(4);
		str = MFRUtil.localize(str, true, null);
		if (str != null)
			tooltip.add(str);
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {

		if (this.isInCreativeTab(tab)) {
			items.add(new ItemStack(this, 1, 0));
		}
	}

	@Override
	public String toString() {

		StringBuilder b = new StringBuilder(getClass().getName());
		b.append('@').append(System.identityHashCode(this)).append('{');
		b.append("l:").append(getUnlocalizedName());
		b.append('}');
		return b.toString();
	}

	@Override public boolean preInit() {

		return false;
	}

	@Override
	public boolean initialize() {

		MFRRegistry.registerItem(this);
		return true;
	}

	public ItemFactory setModelLocation(String modelName, String variant) {

		this.modelName = modelName;
		this.variant = variant;

		return this;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels() {

		ModelHelper.registerModel(this, modelName, variant);
	}
}
