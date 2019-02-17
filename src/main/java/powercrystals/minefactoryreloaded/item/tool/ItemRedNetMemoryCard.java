package powercrystals.minefactoryreloaded.item.tool;

import cofh.api.core.IPortableData;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import powercrystals.minefactoryreloaded.core.MFRUtil;
import powercrystals.minefactoryreloaded.item.base.ItemFactory;
import powercrystals.minefactoryreloaded.render.ModelHelper;
import powercrystals.minefactoryreloaded.tile.rednet.TileEntityRedNetLogic;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class ItemRedNetMemoryCard extends ItemFactory {

	public ItemRedNetMemoryCard() {

		setUnlocalizedName("mfr.rednet.memorycard");
		setMaxStackSize(1);
	}

	@Override
	public void addInformation(@Nonnull ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag tooltipFlag) {

		super.addInformation(stack, world, tooltip, tooltipFlag);
		NBTTagCompound tag = stack.getTagCompound();
		if (tag != null) {
			l: {
				if (tag.hasKey("Type")) {
					String type = tag.getString("Type");
					String entry = MFRUtil.localize("tip.info.mfr.memorycard.programmedFor", new Object[] {
						MFRUtil.localize(type)
					});
					tooltip.addAll(Arrays.asList(entry.split("\n", -1)));

					if (!type.equals("tile.mfr.rednet.logic.name"))
						break l;
				}
				if (tag.hasKey("circuits", 9)) {
					int c = stack.getTagCompound().getTagList("circuits", 10).tagCount();
					tooltip.add(MFRUtil.localize("tip.info.mfr.memorycard.programmed", c));
				}
			}
			tooltip.add(MFRUtil.localize("tip.info.mfr.memorycard.wipe", true));
		}
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side,
			float xOffset, float yOffset, float zOffset) {

		@Nonnull ItemStack stack = player.getHeldItem(hand);

		if (world.isRemote) {
			return EnumActionResult.SUCCESS;
		}

		TileEntity te = world.getTileEntity(pos);
		NBTTagCompound tag = stack.getTagCompound();
		boolean read = tag == null || !tag.hasKey("Type"), special = false;
		if (tag == null)
			stack.setTagCompound(tag = new NBTTagCompound());
		else if (read && tag.hasKey("circuits", 9)) {
			special = true;
			read = false;
		}

		l: if (!special && te instanceof IPortableData) {
			if (read) {
				NBTTagCompound tag2 = tag.copy();
				((IPortableData) te).writePortableData(player, tag2);
				if (!tag2.equals(tag)) {
					tag = tag2;
					tag.setString("Type", ((IPortableData) te).getDataType());
				}
			} else {
				if (tag.getString("Type").equals(((IPortableData) te).getDataType())) {
					((IPortableData) te).readPortableData(player, tag);
				} else
					break l;
			}
			stack.setTagCompound(tag);

			return EnumActionResult.SUCCESS;
		}
		else if (te instanceof TileEntityRedNetLogic) {
			if (special)
				((IPortableData) te).readPortableData(player, tag);

			return EnumActionResult.SUCCESS;
		}

		return EnumActionResult.PASS;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(@Nonnull ItemStack stack) {

		NBTTagCompound tag = stack.getTagCompound();
		return tag != null && (tag.hasKey("Type") || tag.hasKey("circuits", 9));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels() {

		ModelHelper.registerModel(this, "memory_card");
	}
}
