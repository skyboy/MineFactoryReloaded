package powercrystals.minefactoryreloaded.block.decor;

import static powercrystals.minefactoryreloaded.item.base.ItemMulti.getName;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;

import powercrystals.minefactoryreloaded.block.BlockFactory;

public class BlockFactoryPlastic extends BlockFactory {

	public static final String[] _names = new String[] { null, "paver", "column", "bricks_large", "chiseled", "road", "bricks" };
	private IIcon[] _icons = new IIcon[_names.length];

	public BlockFactoryPlastic() {

		super(0.3f);
		slipperiness = 1f / 0.9801f;
		setBlockName("mfr.plastic");
		setHarvestLevel("axe", 0);
		providesPower = false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister ir) {

		for (int i = 0; i < _icons.length; i++) {
			String name = getName(unlocalizedName, _names[i]);
			_icons[i] = ir.registerIcon("minefactoryreloaded:tile." + name);
		}
	}

	@Override
	public IIcon getIcon(int side, int meta) {

		if (side < 2 & meta == 2) {
			--meta;
		}
		return _icons[Math.min(meta, _icons.length - 1)];
	}

}
