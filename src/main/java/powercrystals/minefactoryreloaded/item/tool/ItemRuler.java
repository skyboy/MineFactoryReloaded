package powercrystals.minefactoryreloaded.item.tool;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import powercrystals.minefactoryreloaded.item.base.ItemFactoryTool;
import powercrystals.minefactoryreloaded.render.ModelHelper;
import powercrystals.minefactoryreloaded.setup.MFRConfig;

import javax.annotation.Nonnull;

public class ItemRuler extends ItemFactoryTool {

	public ItemRuler() {

		setUnlocalizedName("mfr.ruler");
		setMaxStackSize(1);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {

		if (world.isRemote) {
			RayTraceResult mop = player.rayTrace(MFRConfig.spyglassRange.getInt(), 1.0F);
			if (mop == null || (mop.typeOfHit == Type.ENTITY && mop.entityHit == null)) {
				player.sendMessage(new TextComponentTranslation("chat.info.mfr.ruler.nosight"));
			} else if (mop.typeOfHit == Type.ENTITY) {
				player.sendMessage(new TextComponentTranslation("chat.info.mfr.ruler.hitentity"));
			} else {
				NBTTagCompound data = player.getEntityData();
				NBTTagCompound tag = data.getCompoundTag("ruler");
				if (!data.hasKey("ruler")) {
					tag.setInteger("x", mop.getBlockPos().getX());
					tag.setInteger("y", mop.getBlockPos().getY());
					tag.setInteger("z", mop.getBlockPos().getZ());
					data.setTag("ruler", tag);
					player.sendMessage(new TextComponentTranslation("chat.info.mfr.ruler.startposition"));
				} else {
					int x = tag.getInteger("x");
					int y = tag.getInteger("y");
					int z = tag.getInteger("z");
					data.removeTag("ruler");

					int distX = Math.abs(mop.getBlockPos().getX() - x);
					int distY = Math.abs(mop.getBlockPos().getY() - y);
					int distZ = Math.abs(mop.getBlockPos().getZ() - z);

					double distAll = Math.sqrt(Math.pow(distX, 2) +
							Math.pow(distY, 2) + Math.pow(distZ, 2));

					player.sendMessage(new TextComponentString("X: ").appendText(I18n
									.translateToLocalFormatted("chat.info.mfr.ruler.distance",
											distX, distX + 1)));
					player.sendMessage(new TextComponentString("Y: ").appendText(I18n
									.translateToLocalFormatted("chat.info.mfr.ruler.distance",
											distY, distY + 1)));
					player.sendMessage(new TextComponentString("Z: ").appendText(I18n
									.translateToLocalFormatted("chat.info.mfr.ruler.distance",
											distZ, distZ + 1)));
					player.sendMessage(new TextComponentString("")
							.appendText(I18n
									.translateToLocalFormatted("chat.info.mfr.ruler.total",
											distAll)));
				}
			}
		}

		return super.onItemRightClick(world, player, hand);
	}

	@Override
	protected int getWeaponDamage(@Nonnull ItemStack stack) {
		return 1;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels() {

		ModelHelper.registerModel(this, "tool", "variant=ruler");
	}
}
