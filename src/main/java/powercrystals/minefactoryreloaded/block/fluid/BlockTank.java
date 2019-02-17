package powercrystals.minefactoryreloaded.block.fluid;

import codechicken.lib.model.ModelRegistryHelper;
import codechicken.lib.model.bakery.CCBakeryModel;
import codechicken.lib.model.bakery.IBakeryProvider;
import codechicken.lib.model.bakery.ModelBakery;
import codechicken.lib.model.bakery.generation.IBakery;
import codechicken.lib.texture.TextureUtils;
import cofh.api.block.IBlockInfo;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import powercrystals.minefactoryreloaded.MFRProps;
import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.block.BlockFactory;
import powercrystals.minefactoryreloaded.block.ItemBlockTank;
import powercrystals.minefactoryreloaded.core.MFRUtil;
import powercrystals.minefactoryreloaded.render.ModelHelper;
import powercrystals.minefactoryreloaded.render.block.BlockTankRenderer;
import powercrystals.minefactoryreloaded.setup.MFRThings;
import powercrystals.minefactoryreloaded.tile.tank.TileEntityTank;

import javax.annotation.Nonnull;
import java.util.List;

public class BlockTank extends BlockFactory implements IBlockInfo, IBakeryProvider {

	public static final IUnlistedProperty<String> FLUID = new IUnlistedProperty<String>() {

		@Override public String getName() {

			return "fluid_rl";
		}

		@Override public boolean isValid(String value) {

			return true;
		}

		@Override public Class<String> getType() {

			return String.class;
		}

		@Override public String valueToString(String value) {

			return value;
		}
	};

	public static final IUnlistedProperty<Byte> SIDES = new IUnlistedProperty<Byte>() {

		@Override public String getName() {

			return "sides";
		}

		@Override public boolean isValid(Byte value) {

			return value >= 0 && value <= 15;
		}

		@Override public Class<Byte> getType() {

			return Byte.class;
		}

		@Override public String valueToString(Byte value) {

			return value.toString();
		}
	};

	public BlockTank() {

		super(0.5f);
		setUnlocalizedName("mfr.tank");
		setLightOpacity(1);
	}

	@Override
	protected BlockStateContainer createBlockState() {

		return new BlockStateContainer.Builder(this).add(FLUID, SIDES).build();
	}

	@Override
	public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {

		return ModelBakery.handleExtendedState((IExtendedBlockState) super.getExtendedState(state, world, pos), world, pos);
	}

	@Override
	public BlockRenderLayer getBlockLayer() {

		return BlockRenderLayer.TRANSLUCENT;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {

		if (side.getAxis().isHorizontal() && world.getBlockState(pos.offset(side)).getBlock().equals(this)) {
			return false;
		}
		return true;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {

		return false;
	}

	@Override
	public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {

		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof TileEntityTank) {
			TileEntityTank tank = (TileEntityTank) tile;
			FluidStack fluid = tank.getFluid();
			if (fluid != null)
				return fluid.getFluid().getLuminosity(fluid);
		}
		return 0;
	}

	@Override
	protected boolean activated(World world, BlockPos pos, EntityPlayer player, EnumFacing side, EnumHand hand,
			@Nonnull ItemStack heldItem) {

		if (heldItem.getItem() == MFRThings.strawItem)
			return false;

		super.activated(world, pos, player, side, hand, heldItem);
		return true; // don't allow accidentally placing fluids/tanks off us
	}

	@Override
	public boolean hasTileEntity(IBlockState state) {

		return true;
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {

		return new TileEntityTank();
	}

	@Override
	public void getBlockInfo(List<ITextComponent> info, IBlockAccess world, BlockPos pos, EnumFacing side, EntityPlayer player,
			boolean debug) {

		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof TileEntityTank) {
			((TileEntityTank) tile).getTileInfo(info, side, player, debug);
		}
	}

	@Override
	public IBakery getBakery() {

		return BlockTankRenderer.INSTANCE;
	}

	@Override
	public boolean initialize() {

		MFRRegistry.registerBlock(this, new ItemBlockTank(this));
		GameRegistry.registerTileEntity(TileEntityTank.class, new ResourceLocation(MFRProps.MOD_ID, "tank"));
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels() {

		ModelHelper.registerModel(this, BlockTankRenderer.MODEL_LOCATION);

		ModelRegistryHelper.register(BlockTankRenderer.MODEL_LOCATION, new CCBakeryModel() {
			@Override
			public TextureAtlasSprite getParticleTexture() {
				return TextureUtils.getTexture(MFRProps.PREFIX + "blocks/machines/tile.mfr.tank.bottom");
			}

		});

		ModelBakery.registerBlockKeyGenerator(this,
				state -> state.getBlock().getRegistryName().toString() + "," + state.getValue(FLUID) + "," +
						state.getValue(SIDES));

		ModelBakery.registerItemKeyGenerator(MFRRegistry.getItemBlock(this), stack -> {
			String key = stack.getItem().getRegistryName().toString();
			if (stack.getItem() instanceof ItemBlockTank) {
				FluidStack fluidStack = MFRUtil.getFluidContents(stack);
				if (fluidStack != null) {
					key += "," + fluidStack.getFluid().getStill().toString();
				}
			}
			return key;
		});
	}
}

