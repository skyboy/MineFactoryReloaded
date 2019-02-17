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

import java.util.Locale;

public class BlockFactoryDecoration extends BlockFactory
{
	public static final PropertyEnum<Variant> VARIANT = PropertyEnum.create("variant", Variant.class);
	
	public BlockFactoryDecoration() {
		
		super(0.5f);
		setUnlocalizedName("mfr.machine_block");
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

		MFRRegistry.registerBlock(this, new ItemBlockFactory(this, Variant.NAMES));
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels() {

		ModelHelper.registerModel(this, "variant", Variant.NAMES);
	}

	public enum Variant implements IStringSerializable {
		BASE,
		PRC;

		private final int meta;
		private final String name;

		public static final String[] NAMES;

		Variant() {

			this.meta = ordinal();
			this.name = name().toLowerCase(Locale.ROOT);
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
			for (Variant variant : values()) {
				NAMES[variant.meta] = variant.name;
			}
		}
	}
}
