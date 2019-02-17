package powercrystals.minefactoryreloaded.item;

import cofh.core.util.helpers.ItemHelper;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import powercrystals.minefactoryreloaded.core.MFRUtil;
import powercrystals.minefactoryreloaded.item.base.ItemFactory;
import powercrystals.minefactoryreloaded.net.Packets;
import powercrystals.minefactoryreloaded.render.ModelHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ItemPortaSpawner extends ItemFactory {

	private static Block _block = Blocks.MOB_SPAWNER;
	public static final String spawnerTag = "spawner";
	private static final String placeTag = "placeDelay";
	private static final String entityNamesTag = "entityNames";

	public ItemPortaSpawner() {

		setUnlocalizedName("mfr.porta_spawner");
		setMaxStackSize(1);
	}

	public static NBTTagCompound getSpawnerTag(@Nonnull ItemStack stack) {

		NBTTagCompound tag = stack.getTagCompound();
		if (tag != null) {
			if (tag.hasKey(spawnerTag))
				return tag.getCompoundTag(spawnerTag);
		}
		return null;
	}

	public static boolean hasData(@Nonnull ItemStack stack) {

		return getSpawnerTag(stack) != null;
	}

	private static int getDelay(@Nonnull ItemStack stack) {

		NBTTagCompound tag = stack.getTagCompound();
		if (tag != null) {
			return tag.getInteger(placeTag);
		}
		return 0;
	}

	@Override
	public void addInformation(@Nonnull ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag tooltipFlag) {

		super.addInformation(stack, world, tooltip, tooltipFlag);
		if (hasData(stack)) {
			StringBuilder entities = new StringBuilder();
			for (String entityUnlocalized : getEntityNames(stack)) {
				if (entities.length() > 0)
					entities.append(", ");
				entities.append(MFRUtil.localize("entity.", entityUnlocalized));
			}
			tooltip.add(MFRUtil.localize("tile.mobSpawner") + ": " + entities);
		}
		int delay = getDelay(stack);
		if (delay > 0) {
			String s = MFRUtil.localize("tip.info.mfr.cannotplace", true, "%s");
			tooltip.add(String.format(s, Math.ceil(delay / 20f)));
		}
	}

	@Override
	public void onUpdate(@Nonnull ItemStack stack, World world, Entity entity, int par4, boolean par5) {

		NBTTagCompound tag = stack.getTagCompound();
		if (tag != null && tag.hasKey(placeTag) && tag.getInteger(placeTag) > 0) {
			tag.setInteger(placeTag, tag.getInteger(placeTag) - 1);
		}
	}

	@Override
	public boolean shouldCauseReequipAnimation(@Nonnull ItemStack oldStack, @Nonnull ItemStack newStack, boolean slotChanged) {

		return !ItemHelper.areItemStacksEqualIgnoreTags(oldStack, newStack, placeTag);
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side,
			float xOffset, float yOffset, float zOffset) {

		@Nonnull ItemStack itemstack = player.getHeldItem(hand);

		if (world.isRemote) {
			return EnumActionResult.SUCCESS;
		}
		if (!hasData(itemstack)) {
			if (world.getBlockState(pos).getBlock().equals(_block)) {
				TileEntity te = world.getTileEntity(pos);
				NBTTagCompound tag = new NBTTagCompound();
				tag.setTag(spawnerTag, new NBTTagCompound());
				te.writeToNBT(tag.getCompoundTag(spawnerTag));
				tag.setInteger(placeTag, 40 * 20);
				tag.setTag(entityNamesTag, getEntityNames(tag));
				itemstack.setTagCompound(tag);
				world.setBlockToAir(pos);
				return EnumActionResult.SUCCESS;
			} else
				return EnumActionResult.PASS;
		} else {
			if (getDelay(itemstack) <= 0 &&
					placeBlock(itemstack, player, hand, world, pos, side, xOffset, yOffset, zOffset)) {
				return EnumActionResult.SUCCESS;
			}
			return EnumActionResult.PASS;
		}
	}

	private NBTTagList getEntityNames(NBTTagCompound tag) {

		NBTTagList entityNames = new NBTTagList();

		NBTTagCompound spawnerNBT = tag.getCompoundTag(spawnerTag);
		NBTTagList potentialSpawns = spawnerNBT.getTagList("SpawnPotentials", 10);

		for (int i = 0; i < potentialSpawns.tagCount(); i++) {
			NBTTagCompound entityWeight = potentialSpawns.getCompoundTagAt(i);

			if (!entityWeight.hasKey("Entity"))
				continue;
			NBTTagCompound entity = entityWeight.getCompoundTag("Entity");

			String entityId = entity.getString("id");
			if (!entityId.isEmpty())
				entityNames.appendTag(new NBTTagString(EntityList.getTranslationName(new ResourceLocation(entityId))));
		}
		return entityNames;
	}

	private List<String> getEntityNames(ItemStack stack) {

		List<String> entityNames = new ArrayList<>();
		NBTTagCompound tag = stack.getTagCompound();

		if (tag != null && tag.hasKey(entityNamesTag)) {
			NBTTagList list = tag.getTagList(entityNamesTag, 8);
			for(NBTBase name : list) {
				entityNames.add(((NBTTagString)name).getString());
			}
		}

		return entityNames;
	}

	private boolean placeBlock(@Nonnull ItemStack itemstack, EntityPlayer player, EnumHand hand, World world, BlockPos pos,
			EnumFacing side,
			float xOffset, float yOffset, float zOffset) {

		IBlockState state = world.getBlockState(pos);
		Block block = state.getBlock();

		BlockPos placePos = pos;

		if (block == Blocks.SNOW_LAYER) {
			side = EnumFacing.UP;
		} else if (!block.isReplaceable(world, placePos)) {
			placePos = placePos.offset(side);
		}

		if (itemstack.getCount() == 0) {
			return false;
		} else if (!player.canPlayerEdit(placePos, side, itemstack)) {
			return false;
		} else if (placePos.getY() == 255 && state.getMaterial().isSolid()) {
			return false;
		} else if (world.mayPlace(_block, placePos, false, side, player)) {
			IBlockState placedState = _block
					.getStateForPlacement(world, placePos, side, xOffset, yOffset, zOffset, 0, player, hand);

			if (placeBlockAt(itemstack, player, world, placePos, side, xOffset, yOffset, zOffset, placedState)) {
				SoundType soundType = block.getSoundType(state, world, placePos, null);
				world.playSound(null, placePos.getX() + 0.5F, placePos.getY() + 0.5F, placePos.getZ() + 0.5F,
						soundType.getStepSound(), SoundCategory.BLOCKS,
						(soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F);
				itemstack.shrink(1);
			}

			return true;
		} else {
			return false;
		}
	}

	private boolean placeBlockAt(@Nonnull ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side,
			float hitX, float hitY, float hitZ, IBlockState state) {

		// TODO: record and read the block that was consumed
		if (!world.setBlockState(pos, state, 3)) {
			return false;
		}

		Block block = world.getBlockState(pos).getBlock();
		if (block.equals(_block)) {
			block.onBlockPlacedBy(world, pos, state, player, stack);
			TileEntity te = world.getTileEntity(pos);
			NBTTagCompound source = te.writeToNBT(new NBTTagCompound());
			NBTTagCompound tag = stack.getSubCompound(spawnerTag);
			source.merge(tag);
			tag = source;
			tag.setInteger("x", pos.getX());
			tag.setInteger("y", pos.getY());
			tag.setInteger("z", pos.getZ());
			te.readFromNBT(tag);
			Packets.sendToAllPlayersWatching(world, pos, te.getUpdatePacket());
		}
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(@Nonnull ItemStack stack) {

		return hasData(stack);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public EnumRarity getRarity(@Nonnull ItemStack stack) {

		return hasData(stack) ? EnumRarity.EPIC : EnumRarity.RARE;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels() {

		ModelHelper.registerModel(this, "porta_spawner");
	}
}
