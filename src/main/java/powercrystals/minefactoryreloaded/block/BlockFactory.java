package powercrystals.minefactoryreloaded.block;

import codechicken.lib.raytracer.IndexedCuboid6;
import codechicken.lib.raytracer.RayTracer;
import cofh.api.block.IDismantleable;
import cofh.core.render.IModelRegister;
import cofh.core.render.hitbox.ICustomHitBox;
import cofh.core.render.hitbox.RenderHitbox;
import cofh.core.util.core.IInitializer;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.api.rednet.connectivity.IRedNetConnection;
import powercrystals.minefactoryreloaded.api.rednet.connectivity.RedNetConnectionType;
import powercrystals.minefactoryreloaded.core.IEntityCollidable;
import powercrystals.minefactoryreloaded.core.IRotatableTile;
import powercrystals.minefactoryreloaded.core.ITankContainerBucketable;
import powercrystals.minefactoryreloaded.core.ITraceable;
import powercrystals.minefactoryreloaded.core.MFRUtil;
import powercrystals.minefactoryreloaded.core.UtilInventory;
import powercrystals.minefactoryreloaded.gui.MFRCreativeTab;
import powercrystals.minefactoryreloaded.setup.MFRThings;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.base.TileEntityBase;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class BlockFactory extends Block implements IRedNetConnection, IDismantleable, IInitializer, IModelRegister
{
	private static final float SHRINK_AMOUNT = 0.125F;
	private static final AxisAlignedBB SHRUNK_AABB = new AxisAlignedBB(SHRINK_AMOUNT, SHRINK_AMOUNT, SHRINK_AMOUNT, 1F - SHRINK_AMOUNT, 1F - SHRINK_AMOUNT, 1F - SHRINK_AMOUNT);

	protected boolean providesPower;

	protected BlockFactory(float hardness)
	{
		super(Machine.MATERIAL);
		setHardness(hardness);
		setSoundType(SoundType.METAL);
		setCreativeTab(MFRCreativeTab.tab);
		setHarvestLevel("pickaxe", 0);
		MFRThings.registerInitializer(this);
		MineFactoryReloadedCore.proxy.addModelRegister(this);
	}

	protected BlockFactory(Material material)
	{
		super(material);
		setCreativeTab(MFRCreativeTab.tab);
		setHarvestLevel("pickaxe", 0);
		MFRThings.registerInitializer(this);
		MineFactoryReloadedCore.proxy.addModelRegister(this);
	}

	protected static final TileEntity getTile(IBlockAccess world, BlockPos pos)
	{
		return MFRUtil.getTile(world, pos);
	}

	@Override
	public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player)
	{ // HACK: called before block is destroyed by the player prior to the player getting the drops. destroy block here.
		// hack is needed because the player sets the block to air *before* getting the drops. woo good logic from mojang.
		if (!player.capabilities.isCreativeMode)
		{
			//TODO verify that this logic doesn't conflict with vanilla one now
			harvesters.set(player);
			dropBlockAsItem(world, pos, state, EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, player.getActiveItemStack()));
			harvesters.set(null);
		}
	}

	@Override
	public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, @Nonnull
			ItemStack stack) {
	}

	@Override
	public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis)
	{
		if (world.isRemote)
		{
			return false;
		}
		TileEntity te = getTile(world, pos);
		if (te instanceof IRotatableTile)
		{
			IRotatableTile tile = ((IRotatableTile)te);
			if (tile.canRotate(axis))
			{
				tile.rotate(axis);
				return true;
			}
		}
		return false;
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase entity, @Nonnull ItemStack stack)
	{
		TileEntity te = getTile(world, pos);

		if (te instanceof TileEntityBase && stack.getTagCompound() != null)
		{
			te.readFromNBT(stack.getTagCompound());
		}
	}

	@Override
	public final boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float xOffset, float yOffset, float zOffset)
	{
		@Nonnull ItemStack heldItem = player.getHeldItem(hand);
		PlayerInteractEvent.RightClickBlock e = new PlayerInteractEvent.RightClickBlock(player, hand, pos, side, new Vec3d(xOffset, yOffset, zOffset));
		if (MinecraftForge.EVENT_BUS.post(e) || e.getResult() == Result.DENY || e.getUseBlock() == Result.DENY)
			return false;

		activationOffsets(xOffset, yOffset, zOffset);
		return activated(world, pos, player, side, hand, heldItem);
	}

	protected void activationOffsets(float xOffset, float yOffset, float zOffset) {}

	protected boolean activated(World world, BlockPos pos, EntityPlayer player, EnumFacing side, EnumHand hand, @Nonnull ItemStack heldItem)
	{
		TileEntity te = world.getTileEntity(pos);
		if (te == null)
		{
			return false;
		}
		if (!heldItem.isEmpty() && te instanceof ITankContainerBucketable)
		{
			if(heldItem.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null))
			{
				if (world.isRemote)
					return true;

				ITankContainerBucketable itcb = (ITankContainerBucketable) te;
				IItemHandler playerInventory = new InvWrapper(player.inventory);


				if (itcb.allowBucketDrain(side, heldItem)) {

					FluidActionResult result = FluidUtil.tryFillContainerAndStow(heldItem,
							new ITankContainerBucketable.FluidHandlerWrapper(itcb, side), playerInventory, Integer.MAX_VALUE, player);
					if (result.isSuccess()) {
						player.setHeldItem(hand, result.getResult());
						return true;
					}
				}

				if (itcb.allowBucketFill(side, heldItem)) {

					FluidActionResult result = FluidUtil.tryEmptyContainerAndStow(heldItem,
							new ITankContainerBucketable.FluidHandlerWrapper(itcb, side), playerInventory, Integer.MAX_VALUE, player);
					if (result.isSuccess()) {
						player.setHeldItem(hand, result.getResult());
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public boolean canDismantle(World world, BlockPos pos, IBlockState state, EntityPlayer player)
	{
		return true;
	}

	@Override
	public ArrayList<ItemStack> dismantleBlock(World world, BlockPos pos, IBlockState state, EntityPlayer player, boolean returnBlock)
	{
		ArrayList<ItemStack> list = getDrops(world, pos, world.getBlockState(pos), 0);

		world.setBlockToAir(pos);
		if (!returnBlock)
			for (@Nonnull ItemStack item : list) {
				UtilInventory.dropStackInAir(world, pos, item);
			}
		return list;
	}

	@Override
	public ArrayList<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
	{
		ArrayList<ItemStack> drops = new ArrayList<>();

		Random rand = world instanceof World ? ((World)world).rand : RANDOM;

		@Nonnull ItemStack machine = new ItemStack(getItemDropped(state, rand, fortune), 1,
				damageDropped(state));

		TileEntity te = getTile(world, pos);
		if (te instanceof TileEntityBase)
		{
			NBTTagCompound tag = new NBTTagCompound();
			((TileEntityBase)te).writeItemNBT(tag);
			if (!tag.hasNoTags())
				machine.setTagCompound(tag);
		}

		drops.add(machine);
		return drops;
	}

	public void getBlockInfo(List<ITextComponent> info, IBlockAccess world, BlockPos pos, EnumFacing side,
			EntityPlayer player, boolean debug)
	{
		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof TileEntityBase)
			((TileEntityBase)tile).getTileInfo(info, side, player, debug);
	}

	@Override
	public void onBlockAdded(World world, BlockPos pos, IBlockState state)
	{
		neighborChanged(state, world, pos, this, pos);
	}

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos)
	{
		super.neighborChanged(state, world, pos, block, fromPos);
		if (world.isRemote)
		{
			return;
		}

		TileEntity te = getTile(world, pos);
		if (te instanceof TileEntityBase)
		{
			if (block != this)
				((TileEntityBase)te).onNeighborBlockChange();
			else
				((TileEntityBase)te).onMatchedNeighborBlockChange();
		}
	}

	@Override
	public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor)
	{
		TileEntity te = getTile(world, pos);

		if (te instanceof TileEntityBase)
		{
			((TileEntityBase)te).onNeighborTileChange(neighbor);
		}
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos)
	{
		TileEntity te = getTile(world, pos);
		if (te instanceof IEntityCollidable)
		{
			return SHRUNK_AABB;
		}
		else
		{
			return super.getCollisionBoundingBox(state, world, pos);
		}
	}

	@Override
	public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity)
	{
		if (world.isRemote)
			return;

		TileEntity te = getTile(world, pos);
		if (te instanceof IEntityCollidable)
			((IEntityCollidable)te).onEntityCollided(entity);

		super.onEntityCollidedWithBlock(world, pos, state, entity);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB collisionTest, List collisionBoxList,
			Entity entity, boolean p_185477_7_)
	{
		TileEntity te = getTile(world, pos);
		if (te instanceof ITraceable)
		{
			List<IndexedCuboid6> cuboids = new LinkedList<>();
			((ITraceable)te).addTraceableCuboids(cuboids, false, false, true);
			for (IndexedCuboid6 c : cuboids)
			{
				AxisAlignedBB aabb = c.aabb();
				if (collisionTest.intersects(aabb))
					collisionBoxList.add(aabb);
			}
		}
		else
		{
			super.addCollisionBoxToList(state, world, pos, collisionTest, collisionBoxList, entity, p_185477_7_);
		}
	}

	@Override
	public RayTraceResult collisionRayTrace(IBlockState state, World world, BlockPos pos, Vec3d start, Vec3d end)
	{
		if (world.isRemote) {
			harvesters.set(MineFactoryReloadedCore.proxy.getPlayer());
		}
		RayTraceResult r = collisionRayTrace(state, (IBlockAccess) world, pos, start, end);
		if (world.isRemote) {
			harvesters.set(null);
		}
		return r;
	}

	public RayTraceResult collisionRayTrace(IBlockState state, IBlockAccess world, BlockPos pos, Vec3d start, Vec3d end)
	{
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof ITraceable)
		{
			List<IndexedCuboid6> cuboids = new LinkedList<>();
			((ITraceable)te).addTraceableCuboids(cuboids, true, MFRUtil.isHoldingUsableTool(harvesters.get(), EnumHand.MAIN_HAND, pos, EnumFacing.UP), false);
			return RayTracer.rayTraceCuboidsClosest(start, end, pos, cuboids);
		}
		else if (world instanceof World)
		{
			return super.collisionRayTrace(state, (World) world, pos, start, end);
		}
		return null;
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent(priority= EventPriority.HIGHEST)
	public void onBlockHighlight(DrawBlockHighlightEvent event) {
		EntityPlayer player = event.getPlayer();
		World world = player.world;
		RayTraceResult mop = event.getTarget();
		if (mop == null)
			return;
		if (mop.typeOfHit != RayTraceResult.Type.BLOCK)
			return;
		TileEntity te = getTile(world, mop.getBlockPos());
		if (te instanceof ITraceable) {
			int subHit = mop.subHit;
			if (te instanceof ICustomHitBox)
			{
				ICustomHitBox tile = ((ICustomHitBox)te);
				if (tile.shouldRenderCustomHitBox(subHit, player))
				{
					event.setCanceled(true);
					RenderHitbox.drawSelectionBox(player, mop, event.getPartialTicks(), tile.getCustomHitBox(subHit, player));
					return;
				}
			}
			// CCL captures and renders this for us now
			//event.getContext().drawSelectionBox(player, mop, 0, event.getPartialTicks());
			//event.setCanceled(true);
		}
	}

	@Override
	public int getWeakPower(IBlockState state, IBlockAccess blockAccess, BlockPos pos, EnumFacing side)
	{
		return 0;
	}

	@Override
	public int getStrongPower(IBlockState state, IBlockAccess blockAccess, BlockPos pos, EnumFacing side)
	{
		return getWeakPower(state, blockAccess, pos, side);
	}

	@Override
	public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side)
	{
		return true;
	}

	@Override
	public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos)
	{
		return !providesPower;
	}

	@Override
	public boolean canProvidePower(IBlockState state)
	{
		return providesPower;
	}

	@Override
	public int damageDropped(IBlockState state)
	{
		return this.getMetaFromState(state);
	}

	@Override
	public RedNetConnectionType getConnectionType(World world, BlockPos pos, EnumFacing side)
	{
		if (providesPower)
			return RedNetConnectionType.DecorativeSingle;
		else
			return RedNetConnectionType.ForcedDecorativeSingle;
	}

	@Override public boolean preInit() {

		return false;
	}

	@Override
	public boolean initialize()
	{
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels() {

	}

}
