package powercrystals.minefactoryreloaded.core;

import cofh.api.item.IToolHammer;
import cofh.core.util.helpers.StringHelper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.api.IMFRHammer;
import powercrystals.minefactoryreloaded.api.handler.IFactoryTool;
import powercrystals.minefactoryreloaded.setup.MFRThings;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static net.minecraft.util.text.TextFormatting.ITALIC;
import static net.minecraft.util.text.TextFormatting.RESET;
import static org.lwjgl.input.Keyboard.*;

public class MFRUtil {

	@SideOnly(Side.CLIENT)
	public static boolean isAltKeyDown() {

		return Keyboard.isKeyDown(KEY_LMENU) || Keyboard.isKeyDown(KEY_RMENU);
	}

	@SideOnly(Side.CLIENT)
	public static boolean isCtrlKeyDown() { // logic lifted from net.minecraft.client.gui.GuiScreen.isCtrlKeyDown()

		if (Minecraft.IS_RUNNING_ON_MAC)
			return Keyboard.isKeyDown(KEY_LMETA) || Keyboard.isKeyDown(KEY_RMETA);
		return Keyboard.isKeyDown(KEY_LCONTROL) || Keyboard.isKeyDown(KEY_RCONTROL);
	}

	@SideOnly(Side.CLIENT)
	public static boolean isShiftKeyDown() { // logic lifted from net.minecraft.client.gui.GuiScreen.isCtrlKeyDown()

		return Keyboard.isKeyDown(KEY_LSHIFT) || Keyboard.isKeyDown(KEY_RSHIFT);
	}

	//
	private static final String mojangString = "\u00c0\u00c1\u00c2\u00c8\u00ca\u00cb\u00cd\u00d3\u00d4\u00d5\u00da\u00df\u00e3\u00f5\u011f\u0130\u0131\u0152\u0153\u015e\u015f\u0174\u0175\u017e\u0207\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&\'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000\u00c7\u00fc\u00e9\u00e2\u00e4\u00e0\u00e5\u00e7\u00ea\u00eb\u00e8\u00ef\u00ee\u00ec\u00c4\u00c5\u00c9\u00e6\u00c6\u00f4\u00f6\u00f2\u00fb\u00f9\u00ff\u00d6\u00dc\u00f8\u00a3\u00d8\u00d7\u0192\u00e1\u00ed\u00f3\u00fa\u00f1\u00d1\u00aa\u00ba\u00bf\u00ae\u00ac\u00bd\u00bc\u00a1\u00ab\u00bb\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556\u2555\u2563\u2551\u2557\u255d\u255c\u255b\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u255e\u255f\u255a\u2554\u2569\u2566\u2560\u2550\u256c\u2567\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256b\u256a\u2518\u250c\u2588\u2584\u258c\u2590\u2580\u03b1\u03b2\u0393\u03c0\u03a3\u03c3\u03bc\u03c4\u03a6\u0398\u03a9\u03b4\u221e\u2205\u2208\u2229\u2261\u00b1\u2265\u2264\u2320\u2321\u00f7\u2248\u00b0\u2219\u00b7\u221a\u207f\u00b2\u25a0\u0000";

	public static String shiftForInfo() {

		return StringHelper.shiftForDetails();
	}

	public static String empty() {

		return ITALIC + "<" + localize("info.cofh.empty", true) + ">" + RESET;
	}

	public static String energy() {

		return localize("info.cofh.energy", true);
	}

	public static String idle() {

		return localize("info.mfr.idle", true);
	}

	public static String work() {

		return localize("info.mfr.work", true);
	}

	public static String buffer() {

		return localize("info.mfr.buffer", true);
	}

	public static String efficiency() {

		return localize("info.mfr.efficiency", true);
	}

	public static String getFluidName(FluidStack fluid) {

		return StringHelper.getFluidName(fluid);
	}

	@Nonnull
	public static ItemStack getBucketFor(String name) {

		return getBucketFor(FluidRegistry.getFluid(name));
	}

	@Nonnull
	public static ItemStack getBucketFor(Fluid fluid) {
		
		return FluidUtil.getFilledBucket(new FluidStack(fluid, 1000));
	}

	public static FluidStack getFluidContents(@Nonnull ItemStack stack) {

		if (!stack.isEmpty() && stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)) {
			IFluidTankProperties[] tankProps = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null).getTankProperties();

			if (tankProps.length > 0) {
				return tankProps[0].getContents();
			}
		}
		return null;
	}

	public static boolean containsForcedUnicode(String str) {

		for (int i = 0, e = str.length(); i < e; ++i)
			if (mojangString.indexOf(str.charAt(i)) < 0)
				return true;
		return false;
	}

	public static String localize(String s) {

		return localize(s + ".name", false, s);
	}

	public static String localize(String prefix, String s) {

		return localize(prefix + s + ".name", true, s);
	}

	public static String localize(String s, boolean exists) {

		return localize(s, exists, s);
	}

	public static String localize(String s, boolean exists, String def) {

		if (!I18n.canTranslate(s))
			return exists ? def :
					I18n.canTranslate(def) ? localize(I18n.translateToLocal(def), true) : def;
		return I18n.translateToLocal(s);
	}

	public static String localize(String s, Object... data) {

		if (!I18n.canTranslate(s))
			return s;
		return I18n.translateToLocalFormatted(s, data);
	}

	private static enum Numeral {
		M(1000),
		CM(900),
		D(500),
		CD(400),
		C(100),
		XC(90),
		L(50),
		XL(40),
		X(10),
		IX(9),
		V(5),
		IV(4),
		I(1);

		public final String name = name();
		public final int value;

		private Numeral(int val) {

			value = val;
		}

		private static final Numeral[] values = values();
	}

	public static String toNumerals(short i) {

		String s = "potion.potency." + i;
		if (I18n.canTranslate(s))
			return I18n.translateToLocal(s);
		StringBuilder r = new StringBuilder();
		if (i < 0) {
			i = (short) -i;
			r.append('-');
		}
		for (Numeral k : Numeral.values) {
			for (int j = i / k.value; j-- > 0; r.append(k.name))
				;
			i %= k.value;
		}
		return r.toString();
	}

	public static final List<EnumFacing> VALID_DIRECTIONS = Arrays.asList(EnumFacing.VALUES);

	public static boolean isHoldingUsableTool(EntityPlayer player, EnumHand hand, BlockPos pos, EnumFacing side) {

		if (player == null) {
			return false;
		}

		@Nonnull ItemStack heldItem = player.getHeldItem(hand);

		if (heldItem.getItem() instanceof IToolHammer) {
			return ((IToolHammer) heldItem.getItem()).isUsable(heldItem, player, pos);
		}
		else if (heldItem.getItem() instanceof IMFRHammer) {
			return true;
		}

		for (IFactoryTool handler : MFRRegistry.getToolHandlers())
			return handler.isFactoryToolUsable(player, hand, heldItem, pos, side);

		return false;
	}

	public static void usedWrench(EntityPlayer player, EnumHand hand, BlockPos pos, EnumFacing side) {

		if (player == null) {
			return;
		}
		player.swingArm(hand);
		@Nonnull ItemStack heldItem = player.getHeldItem(hand);

		if (heldItem.getItem() instanceof IToolHammer) {
			((IToolHammer) heldItem.getItem()).toolUsed(player.inventory.getCurrentItem(), player, pos);
		}
		else if (heldItem.getItem() instanceof IMFRHammer) {
			return;
		}

		for (IFactoryTool handler : MFRRegistry.getToolHandlers())
			if (handler.isFactoryToolUsable(player, hand, heldItem, pos, side))
				handler.onFactoryToolUsed(player, hand, heldItem, pos, side);
	}

	public static boolean isHolding(EntityPlayer player, @Nonnull Item item, EnumHand hand) {

		if (player == null) {
			return false;
		}
		if (player.getHeldItem(hand).isEmpty()) {
			return false;
		}
		Item currentItem = player.getHeldItem(hand).getItem();
		if (item == currentItem) {
			return true;
		}
		return false;
	}

	public static TileEntity getTile(IBlockAccess world, BlockPos pos) {

		if (world instanceof World) {
			return ((World) world).getChunkFromBlockCoords(pos).getTileEntity(pos, Chunk.EnumCreateEntityType.CHECK);
		}
		return world.getTileEntity(pos);
	}

	public static <T extends TileEntity> T getTile(IBlockAccess world, BlockPos pos, Class<T> type) {

		TileEntity te = getTile(world, pos);

		if (type.isInstance(te))
			return (T) te;

		return null;
	}

	public static EnumFacing[] directionsWithoutConveyors(World world, BlockPos pos) {

		ArrayList<EnumFacing> nonConveyors = new ArrayList<>();
		Block id = MFRThings.conveyorBlock;

		for (EnumFacing direction : EnumFacing.VALUES) {
			if (!world.getBlockState(pos.offset(direction)).getBlock().equals(id))
				nonConveyors.add(direction);
		}

		return nonConveyors.toArray(new EnumFacing[nonConveyors.size()]);
	}

	public static void notifyBlockUpdate(World world, BlockPos pos) {

		IBlockState state = world.getBlockState(pos);
		notifyBlockUpdate(world, pos, state);
	}

	public static void notifyBlockUpdate(World world, BlockPos pos, IBlockState state) {

		world.notifyBlockUpdate(pos, state, state, 3);
	}

	public static void notifyNearbyBlocks(World world, BlockPos pos, Block block) {

		if (world.isBlockLoaded(pos)) {
			for (EnumFacing facing : EnumFacing.VALUES) {
				if (world.isBlockLoaded(pos.offset(facing)))
					world.neighborChanged(pos.offset(facing), block, pos);
			}
		}
	}

	public static void notifyNearbyBlocksExcept(World world, BlockPos pos, Block block) {

		if (world.isBlockLoaded(pos) && world.getBlockState(pos).getBlock() != block) {
			world.neighborChanged(pos, block, pos);
			for (EnumFacing d2 : EnumFacing.VALUES) {
				BlockPos neighborPos = pos.offset(d2);
				if (world.isBlockLoaded(neighborPos) && world.getBlockState(neighborPos).getBlock() != block)
					world.neighborChanged(neighborPos, block, pos);
			}
		}
	}

	public static void wideNotifyNearbyBlocksExcept(World world, BlockPos pos, Block block) {

		for (EnumFacing d : EnumFacing.VALUES) {
			BlockPos firstPos = pos.offset(d);
			if (world.isBlockLoaded(firstPos) && world.getBlockState(firstPos).getBlock() != block) {
				world.notifyNeighborsOfStateChange(firstPos, block, false);
				for (EnumFacing d2 : EnumFacing.VALUES) {
					if (d2.getOpposite() == d)
						continue;
					BlockPos secondPos = firstPos.offset(d2);
					if (world.isBlockLoaded(secondPos) && world.getBlockState(secondPos).getBlock() != block)
						world.notifyNeighborsOfStateChange(secondPos, block, false);
				}
			}
		}
	}

	public static NBTTagCompound writeModifierToNBT(String name, AttributeModifier modifier) {

		NBTTagCompound tag = SharedMonsterAttributes.writeAttributeModifierToNBT(modifier);
		tag.setString("AttributeName", name);
		return tag;
	}
}
