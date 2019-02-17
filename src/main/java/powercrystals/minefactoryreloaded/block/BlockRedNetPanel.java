package powercrystals.minefactoryreloaded.block;

import codechicken.lib.model.ModelRegistryHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import powercrystals.minefactoryreloaded.MFRProps;
import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.api.rednet.IRedNetInputNode;
import powercrystals.minefactoryreloaded.api.rednet.connectivity.RedNetConnectionType;
import powercrystals.minefactoryreloaded.core.MFRUtil;
import powercrystals.minefactoryreloaded.render.ModelHelper;
import powercrystals.minefactoryreloaded.render.tileentity.RedNetHistorianRenderer;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactory;
import powercrystals.minefactoryreloaded.tile.rednet.TileEntityRedNetHistorian;

import javax.annotation.Nonnull;

public class BlockRedNetPanel extends BlockFactory implements IRedNetInputNode {

	private static final AxisAlignedBB AABB_NORTH = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.25D);
	private static final AxisAlignedBB AABB_SOUTH = new AxisAlignedBB(0.0D, 0.0D, 0.75D, 1.0D, 1.0D, 1.0D);
	private static final AxisAlignedBB AABB_EAST = new AxisAlignedBB(0.75D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
	private static final AxisAlignedBB AABB_WEST = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.25D, 1.0D, 1.0D);

	public BlockRedNetPanel() {

		super(0.8F);
		setUnlocalizedName("mfr.rednet.panel");
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {

		return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {

		TileEntity te = source.getTileEntity(pos);
		if (te instanceof TileEntityFactory) {
			EnumFacing facing = ((TileEntityFactory) te).getDirectionFacing();
			switch (facing) {
			case NORTH:
				return AABB_NORTH;
			case SOUTH:
				return AABB_SOUTH;
			case EAST:
				return AABB_EAST;
			case WEST:
				return AABB_WEST;
			}
		}

		return super.getBoundingBox(state, source, pos);
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase entity, @Nonnull ItemStack stack) {

		super.onBlockPlacedBy(world, pos, state, entity, stack);
		if (entity == null) {
			return;
		}

		TileEntity te = getTile(world, pos);
		if (te instanceof TileEntityFactory && ((TileEntityFactory) te).canRotate()) {
			int facing = MathHelper.floor((entity.rotationYaw * 4F) / 360F + 0.5D) & 3;
			if (facing == 0) {
				((TileEntityFactory) te).rotateDirectlyTo(3);
			} else if (facing == 1) {
				((TileEntityFactory) te).rotateDirectlyTo(4);
			} else if (facing == 2) {
				((TileEntityFactory) te).rotateDirectlyTo(2);
			} else if (facing == 3) {
				((TileEntityFactory) te).rotateDirectlyTo(5);
			}
		}
	}

	@Override
	public boolean activated(World world, BlockPos pos, EntityPlayer player, EnumFacing side, EnumHand hand, @Nonnull ItemStack heldItem) {

		IBlockState state = world.getBlockState(pos);

		TileEntity te = getTile(world, pos);
		if (MFRUtil.isHoldingUsableTool(player, hand, pos, side) && te instanceof TileEntityFactory && ((TileEntityFactory) te).canRotate()) {
			((TileEntityFactory) te).rotate(side);
			MFRUtil.notifyBlockUpdate(world, pos, state);
			MFRUtil.usedWrench(player, hand, pos, side);
			return true;
		} else if (te instanceof TileEntityFactory && ((TileEntityFactory) te).getContainer(player.inventory) != null) {
			player.openGui(MineFactoryReloadedCore.instance(), 0, world, pos.getX(), pos.getY(), pos.getZ());
			return true;
		} else if (te instanceof TileEntityRedNetHistorian && !heldItem.isEmpty() && heldItem.getItem().equals(Items.DYE)) {
			((TileEntityRedNetHistorian) te).setSelectedSubnet(15 - heldItem.getItemDamage());
			MFRUtil.notifyBlockUpdate(world, pos, state);
			return true;
		}
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {

		return false;
	}

	@Override
	public boolean isSideSolid(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {

		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {

		return false;
	}

	@Override
	public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos)
	{
		return false;
	}

	@Override
	public boolean isNormalCube(IBlockState state) {

		return false;
	}

	@Override
	public boolean hasTileEntity(IBlockState state) {

		return true;
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {

		return world.isRemote ? new TileEntityRedNetHistorian.Client() : new TileEntityRedNetHistorian();
	}

	@Override
	public RedNetConnectionType getConnectionType(World world, BlockPos pos, EnumFacing side) {

		TileEntity te = world.getTileEntity(pos);
		if (te instanceof TileEntityFactory) {
			return side == ((TileEntityFactory) te).getDirectionFacing() ? RedNetConnectionType.CableAll : RedNetConnectionType.None;
		}
		return RedNetConnectionType.None;
	}

	@Override
	public void onInputsChanged(World world, BlockPos pos, EnumFacing side, int[] inputValues) {

		TileEntity te = world.getTileEntity(pos);
		if (te instanceof TileEntityRedNetHistorian) {
			((TileEntityRedNetHistorian) te).valuesChanged(inputValues);
		}
	}

	@Override
	public void onInputChanged(World world, BlockPos pos, EnumFacing side, int inputValue) {

	}

	@Override
	public boolean initialize() {

		MFRRegistry.registerBlock(this, new ItemBlockRedNetPanel(this));
		GameRegistry.registerTileEntity(TileEntityRedNetHistorian.class, new ResourceLocation(MFRProps.MOD_ID, "rednet_panel"));
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels() {

		RedNetHistorianRenderer historianRenderer = new RedNetHistorianRenderer();
		ModelHelper.registerModel(MFRRegistry.getItemBlock(this), "rednet_historian");
		ModelRegistryHelper.register(new ModelResourceLocation(MFRProps.PREFIX + "rednet_historian", "inventory"), historianRenderer);
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityRedNetHistorian.Client.class, historianRenderer);
	}

}
