package powercrystals.minefactoryreloaded.block.decor;

import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.IStringSerializable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.block.BlockFactory;
import powercrystals.minefactoryreloaded.block.ItemBlockFactory;
import powercrystals.minefactoryreloaded.render.ModelHelper;

public class BlockFactoryPlastic extends BlockFactory {

	public static final PropertyEnum<Variant> VARIANT = PropertyEnum.create("variant", Variant.class);

	public BlockFactoryPlastic() {

		super(0.3f);
		slipperiness = 1f / 0.9801f;
		setUnlocalizedName("mfr.plastic.block");
		setHarvestLevel("axe", 0);
		providesPower = false;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, VARIANT);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {

		return getDefaultState().withProperty(VARIANT, Variant.byMetadata(meta));
	}

	@Override
	public int getMetaFromState(IBlockState state) {

		return state.getValue(VARIANT).meta;
	}

	@Override
	public boolean initialize() {

		MFRRegistry.registerBlock(this, new ItemBlockFactory(this, Variant.UNLOC_NAMES));
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels() {

		ModelHelper.registerModel(this, "variant", Variant.NAMES);
	}

	public enum Variant implements IStringSerializable {

		PLAIN(0, "plain"),
		PAVER(1, "paver"),
		COLUMN(2, "column"),
		BRICKS_LARGE(3, "bricks_large"),
		CHISELED(4, "chiseled"),
		ROAD(5, "road"),
		BRICKS(6, "bricks");

		private final int meta;
		private final String name;

		public static final String[] NAMES;
		public static final String[] UNLOC_NAMES;

		Variant(int meta, String name) {

			this.meta = meta;
			this.name = name;
		}

		@Override
		public String getName() {

			return name;
		}

		public static Variant byMetadata(int meta) {

			return values()[meta];
		}

		static {
			NAMES = new String[values().length];
			UNLOC_NAMES = new String[values().length];
			for (Variant variant : values()) {
				NAMES[variant.meta] = variant.name;
				UNLOC_NAMES[variant.meta] = variant.name.replace("_", ".");
			}
		}
	}

}
