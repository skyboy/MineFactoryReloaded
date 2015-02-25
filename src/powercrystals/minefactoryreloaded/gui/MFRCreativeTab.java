package powercrystals.minefactoryreloaded.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import powercrystals.minefactoryreloaded.setup.MFRThings;

public class MFRCreativeTab extends CreativeTabs
{
	public static final MFRCreativeTab tab = new MFRCreativeTab(MineFactoryReloadedCore.modName, true);
	private boolean search;

	public MFRCreativeTab(String label, boolean searchbar)
	{
		super(label);
        this.search = searchbar;
	}
	
	@Override
    public boolean hasSearchBar() {

        return search;
    }

	@Override
	public ItemStack getIconItemStack()
	{
		return new ItemStack(MFRThings.conveyorBlock, 1, 16);
	}

	@Override
	public String getTranslatedTabLabel()
	{
		return this.getTabLabel();
	}
	
	@Override
    @SideOnly(Side.CLIENT)
    public String getBackgroundImageName() {

        return search ? "item_search.png" : super.getBackgroundImageName();
    }

    @Override
    public int getSearchbarWidth() {

        return 89;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public boolean drawInForegroundOfTab() {
        return false;
    }

	@Override
	@SideOnly(Side.CLIENT)
	public Item getTabIconItem()
	{
		return null;
	}
}
