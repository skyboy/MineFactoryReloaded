package powercrystals.minefactoryreloaded.core;

import cofh.core.util.helpers.InventoryHelper;
import cofh.core.util.helpers.ItemHelper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.EmptyHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.List;

public abstract class UtilInventory {

	//TODO probably just delete this code as item handlers should cover all of these cases
	/**
	 * Searches from position x, y, z, checking for TE-compatible pipes in all directions.
	 *
	 * @return Map<EnumFacing, IItemDuct> specifying all found pipes and their directions.
	 *//*
	public static Map<EnumFacing, IItemDuct> findConduits(World world, BlockPos pos)
	{
	return findConduits(world, pos, EnumFacing.VALUES);
	}//*/

	/**
	 * Searches from position x, y, z, checking for TE-compatible pipes in each directionToCheck.
	 *
	 * @return Map<EnumFacing, IItemDuct> specifying all found pipes and their directions.
	 *//*
	public static Map<EnumFacing, IItemDuct> findConduits(World world, BlockPos pos,
			EnumFacing[] directionsToCheck)
	{
		Map<EnumFacing, IItemDuct> pipes = new LinkedHashMap<EnumFacing, IItemDuct>();
		for (EnumFacing direction : directionsToCheck)
		{
			TileEntity te = world.getTileEntity(pos.offset(direction));
			if (te instanceof IItemDuct)
			{
				pipes.put(direction, (IItemDuct) te);
			}
		}
		return pipes;
	}//*/

/*
	*/
	/**
	 * Searches from position x, y, z, checking for BC-compatible pipes in all directions.
	 *
	 * @return Map<EnumFacing, IPipeTile> specifying all found pipes and their directions.
	 *//*

	public static Map<EnumFacing, IPipeTile> findPipes(World world, BlockPos pos)
	{
		return findPipes(world, pos, EnumFacing.VALUES);
	}

	*/
	/**
	 * Searches from position x, y, z, checking for BC-compatible pipes in each directionToCheck.
	 *
	 * @return Map<EnumFacing, IPipeTile> specifying all found pipes and their directions.
	 *//*

	public static Map<EnumFacing, IPipeTile> findPipes(World world, BlockPos pos,
			EnumFacing[] directionsToCheck)
	{
		Map<EnumFacing, IPipeTile> pipes = new LinkedHashMap<EnumFacing, IPipeTile>();
		for (EnumFacing direction : directionsToCheck)
		{
			TileEntity te = world.getTileEntity(pos.offset(direction));
			if (te instanceof IPipeTile)
			{
				pipes.put(direction, (IPipeTile) te);
			}
		}
		return pipes;
	}
*/

	/**
	 * Searches from position x, y, z, checking for inventories in all directions.
	 *
	 * @return Map<EnumFacing, IItemHandler> specifying all found inventories and their directions.
	 */
	public static List<IItemHandler> findChests(World world, BlockPos pos) {

		return findChests(world, pos, EnumFacing.VALUES);
	}

	/**
	 * Searches from position x, y, z, checking for inventories in each directionToCheck.
	 *
	 * @return Map<EnumFacing, IItemHandler> specifying all found inventories and their directions.
	 */
	public static List<IItemHandler> findChests(World world, BlockPos pos,
			EnumFacing[] directionsToCheck) {

		List<IItemHandler> chests = new LinkedList<>();
		for (EnumFacing direction : directionsToCheck) {
			BlockPos chestPos = pos.offset(direction);
			TileEntity te = world.getTileEntity(chestPos);
			if (te != null) {
				chests.add(InventoryHelper.getItemHandlerCap(te, direction.getOpposite()));
			}
		}
		return chests;
	}

	/**
	 * Drops an @Nonnull ItemStack, checking all directions for pipes > chests. DOESN'T drop items into the world.
	 * Example of this behavior: Cargo dropoff rail, item collector.
	 *
	 * @return The remainder of the @Nonnull ItemStack. Whatever -wasn't- successfully dropped.
	 */
	@Nonnull
	public static ItemStack dropStack(TileEntity from, @Nonnull ItemStack stack) {

		return dropStack(from.getWorld(), from.getPos(),
				stack, EnumFacing.VALUES, null);
	}

	/**
	 * Drops an @Nonnull ItemStack, checking all directions for pipes > chests. Drops items into the world.
	 * Example of this behavior: Harvesters, sludge boilers, etc.
	 *
	 * @param airDropDirection the direction that the stack may be dropped into air.
	 * @return The remainder of the @Nonnull ItemStack. Whatever -wasn't- successfully dropped.
	 */
	@Nonnull
	public static ItemStack dropStack(TileEntity from, @Nonnull ItemStack stack, EnumFacing airDropDirection) {

		return dropStack(from.getWorld(), from.getPos(),
				stack, EnumFacing.VALUES, airDropDirection);
	}

	/**
	 * Drops an @Nonnull ItemStack, into chests > pipes > the world, but only in a single direction.
	 * Example of this behavior: Item Router, Ejector
	 *
	 * @param dropDirection    a -single- direction in which to check for pipes/chests
	 * @param airDropDirection the direction that the stack may be dropped into air.
	 * @return The remainder of the @Nonnull ItemStack. Whatever -wasn't- successfully dropped.
	 */
	@Nonnull
	public static ItemStack dropStack(TileEntity from, @Nonnull ItemStack stack, EnumFacing dropDirection,
			EnumFacing airDropDirection) {

		EnumFacing[] dropDirections = { dropDirection };
		return dropStack(from.getWorld(), from.getPos(),
				stack, dropDirections, airDropDirection);
	}

	/**
	 * Drops an @Nonnull ItemStack, checks pipes > chests > world in that order.
	 *
	 * @param from             the TileEntity doing the dropping
	 * @param stack            the @Nonnull ItemStack being dropped
	 * @param dropDirections   directions in which stack may be dropped into chests or pipes
	 * @param airDropDirection the direction that the stack may be dropped into air.
	 *                         null or other invalid directions indicate that stack shouldn't be
	 *                         dropped into the world.
	 * @return The remainder of the @Nonnull ItemStack. Whatever -wasn't- successfully dropped.
	 */
	@Nonnull
	public static ItemStack dropStack(TileEntity from, @Nonnull ItemStack stack, EnumFacing[] dropDirections,
			EnumFacing airDropDirection) {

		return dropStack(from.getWorld(), from.getPos(),
				stack, dropDirections, airDropDirection);
	}

	/**
	 * Drops an @Nonnull ItemStack, checks pipes > chests > world in that order. It generally shouldn't be necessary to call this explicitly.
	 *
	 * @param world            the world
	 * @param pos              the BlockPos to drop from
	 * @param stack            the @Nonnull ItemStack being dropped
	 * @param dropDirections   directions in which stack may be dropped into chests or pipes
	 * @param airDropDirection the direction that the stack may be dropped into air.
	 *                         null or other invalid directions indicate that stack shouldn't be
	 *                         dropped into the world.
	 * @return The remainder of the @Nonnull ItemStack. Whatever -wasn't- successfully dropped.
	 */
	@Nonnull
	public static ItemStack dropStack(World world, BlockPos pos, @Nonnull ItemStack stack,
			EnumFacing[] dropDirections, EnumFacing airDropDirection) {
		// (0) Sanity check. Don't bother dropping if there's nothing to drop, and never try to drop items on the client.
		if (world.isRemote || stack.isEmpty())
			return ItemStack.EMPTY;

		stack = stack.copy();/*
		// (0.5) Try to put stack in conduits that are in valid directions
		for (Entry<EnumFacing, IItemDuct> pipe : findConduits(world, pos, dropDirections).entrySet())
		{
			EnumFacing from = pipe.getKey().getOpposite();
			stack = pipe.getValue().insertItem(from, stack);
			if (stack.isEmpty() || stack.getCount() <= 0)
			{
				return ItemStack.EMPTY;
			}
		}//*/
		// (1) Try to put stack in pipes that are in valid directions
/*		TODO: readd once BC team figures out what they want to do with IPipeTile
		if (handlePipeTiles) {
			stack = handleIPipeTile(world, pos, dropDirections, stack);
			if (stack == null || stack.getCount() <= 0)
			{
				return null;
			}
		}
*/
		// (2) Try to put stack in chests that are in valid directions
		for (IItemHandler chest : findChests(world, pos, dropDirections)) {
			stack = InventoryHelper.insertStackIntoInventory(chest, stack, false);
			if (stack.isEmpty()) {
				return ItemStack.EMPTY;
			}
		}
		// (3) Having failed to put it in a chest or a pipe, drop it in the air if airDropDirection is a valid direction.
		if (MFRUtil.VALID_DIRECTIONS.contains(airDropDirection) && isAirDrop(world, pos.offset(airDropDirection))) {
			dropStackInAir(world, pos, stack, airDropDirection);
			return ItemStack.EMPTY;
		}
		// (4) Is the stack still here? :( Better give it back.
		return stack;
	}

	public static boolean isAirDrop(World world, BlockPos pos) {

		IBlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		if (world.isAirBlock(pos))
			return true;
		return block.getCollisionBoundingBox(state, world, pos) == null;
	}

/*
	@SuppressWarnings("deprecation")
	private static @Nonnull ItemStack handleIPipeTile(World world, BlockPos pos, EnumFacing[] dropDirections, @Nonnull ItemStack stack)
	{
		for (Entry<EnumFacing, IPipeTile> pipe : findPipes(world, pos, dropDirections).entrySet())
		{
			EnumFacing from = pipe.getKey().getOpposite();
			if (pipe.getValue().isPipeConnected(from))
			{
				@Nonnull ItemStack returnedStack = pipe.getValue().injectItem(stack.copy(), false, from, null, 0);
				if (returnedStack == null || returnedStack.getCount() < stack.getCount())
				{
					stack = pipe.getValue().injectItem(stack.copy(), true, from, null, 0);
					if (stack != null && stack.getCount() <= 0)
					{
						return null;
					}
				}
			}
		}
		return stack;
	}
*/

	public static void dropStackInAir(World world, BlockPos pos, @Nonnull ItemStack stack) {

		dropStackInAir(world, pos, stack, null);
	}

	public static void dropStackInAir(World world, BlockPos pos, @Nonnull ItemStack stack, int delay) {

		dropStackInAir(world, pos, stack, delay, null);
	}

	public static void dropStackInAir(World world, BlockPos pos, @Nonnull ItemStack stack, EnumFacing towards) {

		dropStackInAir(world, pos, stack, 20, towards);
	}

	public static void dropStackInAir(World world, Entity entity, @Nonnull ItemStack stack) {

		dropStackInAir(world, entity, stack, null);
	}

	public static void dropStackInAir(World world, Entity entity, @Nonnull ItemStack stack, int delay) {

		dropStackInAir(world, entity, stack, delay, null);
	}

	public static void dropStackInAir(World world, Entity entity, @Nonnull ItemStack stack, EnumFacing towards) {

		dropStackInAir(world, entity, stack, 20, towards);
	}

	public static void dropStackInAir(World world, Entity entity, @Nonnull ItemStack stack, int delay, EnumFacing towards) {

		dropStackInAir(world, entity.getPosition(), stack, delay, towards);
	}

	public static void dropStackInAir(World world, BlockPos pos, @Nonnull ItemStack stack,
			int delay, EnumFacing towards) {

		if (stack.isEmpty())
			return;

		double dropOffsetX = 0.0F;
		double dropOffsetY = 0.0F;
		double dropOffsetZ = 0.0F;

		if (towards == null) {
			float f = 0.3F;
			dropOffsetX = world.rand.nextFloat() * f + (1.0D - f) * 0.5D;
			dropOffsetY = world.rand.nextFloat() * f + (1.0D - f) * 0.5D;
			dropOffsetZ = world.rand.nextFloat() * f + (1.0D - f) * 0.5D;
		} else {
			switch (towards) {
			case UP:
				dropOffsetX = 0.5F;
				dropOffsetY = 1.5F;
				dropOffsetZ = 0.5F;
				break;
			case DOWN:
				dropOffsetX = 0.5F;
				dropOffsetY = -0.75F;
				dropOffsetZ = 0.5F;
				break;
			case NORTH:
				dropOffsetX = 0.5F;
				dropOffsetY = 0.5F;
				dropOffsetZ = -0.5F;
				break;
			case SOUTH:
				dropOffsetX = 0.5F;
				dropOffsetY = 0.5F;
				dropOffsetZ = 1.5F;
				break;
			case EAST:
				dropOffsetX = 1.5F;
				dropOffsetY = 0.5F;
				dropOffsetZ = 0.5F;
				break;
			case WEST:
				dropOffsetX = -0.5F;
				dropOffsetY = 0.5F;
				dropOffsetZ = 0.5F;
				break;
			}
		}

		EntityItem entityitem = new EntityItem(world, pos.getX() + dropOffsetX, pos.getY() + dropOffsetY,
				pos.getZ() + dropOffsetZ, stack.copy());
		if (towards != null) {
			entityitem.motionX = 0.0D;
			if (towards != EnumFacing.DOWN)
				entityitem.motionY = 0.3D;
			entityitem.motionZ = 0.0D;
		}
		entityitem.setPickupDelay(delay);
		world.spawnEntity(entityitem);
	}

	@Nonnull
	public static ItemStack consumeItem(@Nonnull ItemStack stack, EntityPlayer player) {

		return ItemHelper.consumeItem(stack, player);
	}

	public static void mergeStacks(@Nonnull ItemStack to, @Nonnull ItemStack from) {

		if (!stacksEqual(to, from))
			return;

		int amountToCopy = Math.min(to.getMaxStackSize() - to.getCount(), from.getCount());
		to.grow(amountToCopy);
		from.shrink(amountToCopy);
	}

	public static boolean stacksEqual(@Nonnull ItemStack s1, @Nonnull ItemStack s2) {

		return stacksEqual(s1, s2, true);
	}

	public static boolean stacksEqual(@Nonnull ItemStack s1, @Nonnull ItemStack s2, boolean nbtSensitive) {

		if (s1.isEmpty() || s2.isEmpty())
			return false;
		if (!s1.isItemEqual(s2))
			return false;
		if (!nbtSensitive)
			return true;

		if (s1.getTagCompound() == s2.getTagCompound())
			return true;
		if (s1.getTagCompound() == null || s2.getTagCompound() == null)
			return false;
		return s1.getTagCompound().equals(s2.getTagCompound());
	}

	public static boolean stackIsItem(@Nonnull ItemStack stack, Item item) {

		return !stack.isEmpty() && stack.getItem() == item;
	}

	private static boolean handlePipeTiles = false;
	private static final String pipeClass = "buildcraft.api.transport.IPipeTile";

	static {
		try {
			Class.forName(pipeClass);
			handlePipeTiles = true;
		} catch (Throwable t) {}
	}

	public static boolean playerHasItem(EntityPlayer player, Item item) {

		for (int i = 0; i < player.inventory.getSizeInventory(); ++i) {
			if (stackIsItem(player.inventory.getStackInSlot(i), item))
				return true;
		}
		return false;
	}

	public static IItemHandler getItemHandlerCap(IInventory inventory, EnumFacing facing) {

		if (inventory instanceof ISidedInventory) {
			return new SidedInvWrapper((ISidedInventory) inventory, facing);
		} else if (inventory != null) {
			return new InvWrapper(inventory);
		}
		return EmptyHandler.INSTANCE;
	}

	public static ItemStack extractItem(EntityPlayer player, Item item) {
		return extractItem(player, new ItemStack(item));
	}

	public static ItemStack extractItem(EntityPlayer player, ItemStack stack) {
		return extractItem(player, stack, false);
	}

	public static ItemStack extractItem(EntityPlayer player, ItemStack stack, boolean simulate) {

		return extractItem(player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null), stack, simulate);
	}

	public static ItemStack extractItem(IItemHandler itemHandler, ItemStack stack, boolean simulate) {

		int countToExtract = stack.getCount();
		int countExtracted = 0;

		for (int slot = 0; slot < itemHandler.getSlots(); slot++) {
			if (areItemStacksEqualIgnoreCount(stack, itemHandler.getStackInSlot(slot))) {
				countExtracted += itemHandler.extractItem(slot, countToExtract - countExtracted, simulate).getCount();

				if (countToExtract - countExtracted <= 0)
					break;
			}
		}
		if (countExtracted == 0)
			return ItemStack.EMPTY;

		ItemStack copy = stack.copy();
		copy.setCount(countExtracted);
		return copy;
	}

	public static boolean areItemStacksEqualIgnoreCount(ItemStack a, ItemStack b) {

		ItemStack copy = a.copy();
		copy.setCount(b.getCount());
		return ItemStack.areItemStacksEqual(copy, b);
	}
}
