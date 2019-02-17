package powercrystals.minefactoryreloaded.item.tool;

import buildcraft.api.tools.IToolWrench;
import cofh.api.block.IDismantleable;
import cofh.api.item.IToolHammer;
import cofh.core.util.helpers.BlockHelper;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import powercrystals.minefactoryreloaded.api.IMFRHammer;
import powercrystals.minefactoryreloaded.item.base.ItemFactoryTool;
import powercrystals.minefactoryreloaded.modhelpers.Compats;
import powercrystals.minefactoryreloaded.render.ModelHelper;
import powercrystals.minefactoryreloaded.setup.Machine;

import javax.annotation.Nonnull;
import java.util.Random;

@Optional.Interface(iface = "buildcraft.api.tools.IToolWrench", modid = Compats.ModIds.BUILDCRAFT)
public class ItemFactoryHammer extends ItemFactoryTool
		implements IMFRHammer,
				   IToolHammer,
				   IToolWrench
{

	public ItemFactoryHammer() {

		setHarvestLevel("wrench", 1);
		setUnlocalizedName("mfr.hammer");
		setMaxStackSize(1);
	}

	@Override
	public EnumActionResult onItemUseFirst(EntityPlayer player, World world,
			BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {

		{
			PlayerInteractEvent.RightClickBlock e = new PlayerInteractEvent.RightClickBlock(player, hand, pos, side, new Vec3d(hitX, hitY, hitZ));
			if (MinecraftForge.EVENT_BUS.post(e) || e.getResult() == Result.DENY
					|| e.getUseBlock() == Result.DENY || e.getUseItem() == Result.DENY) {
				return EnumActionResult.PASS;
			}
			IBlockState state = world.getBlockState(pos);
			Block block = state.getBlock();

			if (player.isSneaking() && block instanceof IDismantleable &&
					((IDismantleable) block).canDismantle(world, pos, state, player)) {
				if (!world.isRemote)
					((IDismantleable) block).dismantleBlock(world, pos, state, player, false);
				player.swingArm(hand);
				return EnumActionResult.PASS;
			}

			if (!player.isSneaking()) {
				player.swingArm(hand);
				if (BlockHelper.canRotate(state.getBlock()) ? // ternary in the condition!
						world.setBlockState(pos, BlockHelper.rotateVanillaBlock(world, state, pos), 3) :
						block.rotateBlock(world, pos, side)) {
					world.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
							block.getSoundType(state, world, pos, player).getBreakSound(), SoundCategory.PLAYERS,
							1.0F, 0.8F);
				}

				return !world.isRemote ? EnumActionResult.SUCCESS : EnumActionResult.PASS;
			}
		}
		return EnumActionResult.PASS;
	}

	@Override
	public boolean isUsable(@Nonnull ItemStack item, EntityLivingBase user, BlockPos pos) {

		return true;
	}

	@Override
	public void toolUsed(@Nonnull ItemStack item, EntityLivingBase user, BlockPos pos) {

	}

	@Override
	public boolean isUsable(@Nonnull ItemStack item, EntityLivingBase user, Entity ent) {

		return true;
	}

	@Override
	public void toolUsed(@Nonnull ItemStack item, EntityLivingBase user, Entity ent) {

	}

	//@Override
	public boolean canWrench(EntityPlayer player, EnumHand hand, ItemStack wrench, RayTraceResult rayTrace) {

		return true;
	}

	//@Override
	public void wrenchUsed(EntityPlayer player, EnumHand hand, ItemStack wrench, RayTraceResult rayTrace) {

	}

	@Override
	public boolean doesSneakBypassUse(@Nonnull ItemStack stack, IBlockAccess world, BlockPos pos, EntityPlayer player) {

		return true;
	}

	@Override
	public boolean canHarvestBlock(IBlockState state, @Nonnull ItemStack stack) {

		Material mat = state.getMaterial();
		return mat == Material.ICE ||
				mat == Material.CAKE ||
				mat == Material.IRON ||
				mat == Material.ROCK ||
				mat == Material.WOOD ||
				mat == Material.GOURD ||
				mat == Material.ANVIL ||
				mat == Material.GLASS ||
				mat == Material.PISTON ||
				mat == Material.PLANTS ||
				mat == Machine.MATERIAL ||
				mat == Material.CIRCUITS ||
				mat == Material.PACKED_ICE;
	}

	@Override
	public float getDestroySpeed(@Nonnull ItemStack stack, IBlockState state) {

		Material mat = state.getMaterial();
		if (mat == Material.ICE ||
				mat == Material.CAKE ||
				mat == Material.GOURD ||
				mat == Material.GLASS)
			return 15f;
		return canHarvestBlock(state, stack) ? 1.35f : 0.15f;
	}

	@Override
	public boolean onBlockStartBreak(@Nonnull ItemStack stack, BlockPos pos, EntityPlayer player) {

		IBlockState state = player.world.getBlockState(pos);
		if (state.getBlockHardness(player.world, pos) > 2.9f) {
			Random rnd = player.getRNG();
			player.world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ITEM_BREAK,
					SoundCategory.PLAYERS, 0.8F + rnd.nextFloat() * 0.4F, 0.4F);

			for (int i = 0, e = 10 + rnd.nextInt(5); i < e; ++i) {
				Vec3d vec3 = new Vec3d((rnd.nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, 0.0D);
				vec3.rotatePitch(-player.rotationPitch * (float) Math.PI / 180.0F);
				vec3.rotateYaw(-player.rotationYaw * (float) Math.PI / 180.0F);
				Vec3d vec31 = new Vec3d((rnd.nextFloat() - 0.5D) * 0.3D, rnd.nextFloat(), 0.6D);
				vec31.rotatePitch(-player.rotationPitch * (float) Math.PI / 180.0F);
				vec31.rotateYaw(-player.rotationYaw * (float) Math.PI / 180.0F);
				vec31 = vec31.addVector(player.posX, player.posY + player.getEyeHeight(), player.posZ);
				player.world.spawnParticle(EnumParticleTypes.BLOCK_CRACK, vec31.x, vec31.y, vec31.z, vec3.x,
						vec3.y + 0.05D, vec3.z, Block.getStateId(Blocks.FIRE.getDefaultState()));
			}
			return true;
		}
		return false;
	}

	@Override
	protected int getWeaponDamage(@Nonnull ItemStack stack) {

		return 4;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels() {

		ModelHelper.registerModel(this, "tool", "variant=hammer");
	}

}
