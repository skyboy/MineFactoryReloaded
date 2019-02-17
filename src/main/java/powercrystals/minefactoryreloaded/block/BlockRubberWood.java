package powercrystals.minefactoryreloaded.block;

import cofh.core.render.IModelRegister;
import cofh.core.util.core.IInitializer;
import net.minecraft.block.BlockLog;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.api.rednet.connectivity.IRedNetDecorative;
import powercrystals.minefactoryreloaded.gui.MFRCreativeTab;
import powercrystals.minefactoryreloaded.render.ModelHelper;
import powercrystals.minefactoryreloaded.setup.MFRThings;

import java.util.ArrayList;
import java.util.Random;

public class BlockRubberWood extends BlockLog implements IRedNetDecorative, IInitializer, IModelRegister
{
	public static final PropertyBool RUBBER_FILLED = PropertyBool.create("rubber");

	public BlockRubberWood()
	{
		setUnlocalizedName("mfr.rubber_wood.log");
		setCreativeTab(MFRCreativeTab.tab);
		setHarvestLevel("axe", 0);
		setDefaultState(blockState.getBaseState().withProperty(RUBBER_FILLED, true).withProperty(LOG_AXIS, EnumAxis.Y));
		MFRThings.registerInitializer(this);
		MineFactoryReloadedCore.proxy.addModelRegister(this);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, LOG_AXIS, RUBBER_FILLED);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {

		IBlockState state = getDefaultState().withProperty(RUBBER_FILLED, (meta & 3) == 1);
		
		switch (meta & 12)
		{
			case 0:
				state = state.withProperty(LOG_AXIS, BlockLog.EnumAxis.Y);
				break;
			case 4:
				state = state.withProperty(LOG_AXIS, BlockLog.EnumAxis.X);
				break;
			case 8:
				state = state.withProperty(LOG_AXIS, BlockLog.EnumAxis.Z);
				break;
			default:
				state = state.withProperty(LOG_AXIS, BlockLog.EnumAxis.NONE);
		}
		
		return state;
	}

	@Override
	public int getMetaFromState(IBlockState state) {

		int meta = state.getValue(RUBBER_FILLED) ? 1 : 0;
		switch (state.getValue(LOG_AXIS))
		{
			case X:
				meta |= 4;
				break;
			case Z:
				meta |= 8;
				break;
			case NONE:
				meta |= 12;
		}
		
		return meta;
	}

	@Override
	public ArrayList<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
	{
		ArrayList<ItemStack> drops = new ArrayList<>();

		drops.add(new ItemStack(this, 1, 0));
		if(state.getValue(RUBBER_FILLED)) {
			Random rand = world instanceof World ? ((World)world).rand : RANDOM;
			drops.add(new ItemStack(MFRThings.rawRubberItem,
					fortune <= 0 ? 1 : 1 + rand.nextInt(fortune)));
		}

		return drops;
	}

	@Override
	public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face)
	{
		return super.getFireSpreadSpeed(world, pos, face) * (world.getBlockState(pos).getValue(RUBBER_FILLED) ? 2 : 1);
	}

	@Override
	public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face)
	{
		return super.getFlammability(world, pos, face) * (world.getBlockState(pos).getValue(RUBBER_FILLED) ? 2 : 1);
	}

	@Override
	public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {

		items.add(new ItemStack(this, 1, 0));
		items.add(new ItemStack(this, 1, 1));
	}

	@Override
	protected boolean canSilkHarvest()
	{
		return false;
	}

	@Override public boolean preInit() {

		return false;
	}

	@Override
	public boolean initialize() {

		MFRRegistry.registerBlock(this, new ItemBlock(this) {

			@Override
			public int getItemBurnTime(ItemStack stack) {

				return 350;
			}
		});
		Blocks.FIRE.setFireInfo(this, 50, 15);
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels() {

		ModelLoader.setCustomStateMapper(this, new StateMap.Builder().ignore(RUBBER_FILLED).build());
		Item item = MFRRegistry.getItemBlock(this);
		ModelHelper.registerModel(item, "rubber_wood_log", "axis=y");
		ModelHelper.registerModel(item, 1, "rubber_wood_log", "axis=y");
	}
}
