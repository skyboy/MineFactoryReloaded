package powercrystals.minefactoryreloaded.item.tool;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ItemSpyglass extends ItemFactoryTool {

	private static final int BASE_CHAT_LINE_ID = 2525772;

	public ItemSpyglass() {

		setUnlocalizedName("mfr.spyglass");
		setMaxStackSize(1);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {

		if (world.isRemote) {
			RayTraceResult result = rayTrace();
			if (result == null || (result.typeOfHit == Type.ENTITY && result.entityHit == null)) {
				printChatMessageNoSpam(new TextComponentTranslation("chat.info.mfr.spyglass.nosight"));
			} else if (result.typeOfHit == Type.ENTITY) {
				printChatMessageNoSpam(new TextComponentString("")
						.appendText(I18n
								.translateToLocalFormatted("chat.info.mfr.spyglass.hitentity",
										getEntityName(result.entityHit),
										result.entityHit.posX, result.entityHit.posY, result.entityHit.posZ)));
			} else {
				IBlockState state = world.getBlockState(result.getBlockPos());
				Block block = state.getBlock();
				@Nonnull
				ItemStack tempStack = block.getPickBlock(state, result, world, result.getBlockPos(), player);
				if (tempStack.isEmpty())
					tempStack = new ItemStack(block, 1, block.getMetaFromState(state));
				if (!tempStack.isEmpty()) {
					state = block.getActualState(state, world, result.getBlockPos());
					List<ITextComponent> messages = new ArrayList<>();
					messages.add(new TextComponentString("")
							.appendText(I18n
									.translateToLocalFormatted("chat.info.mfr.spyglass.hitblock",
											tempStack.getDisplayName(), block.getRegistryName(),
											(float) result.getBlockPos().getX(), (float) result.getBlockPos().getY(),
											(float) result.getBlockPos().getZ())));
					for (Map.Entry<IProperty<?>, Comparable<?>> entry : state.getProperties().entrySet()) {
						messages.add(new TextComponentString(entry.getKey().getName() + ":" + entry.getValue()));
					}
					printChatMessagesNoSpam(messages);
				} else {
					// TODO: move property scan?
					printChatMessageNoSpam(new TextComponentString("")
							.appendText(I18n
									.translateToLocalFormatted("chat.info.mfr.spyglass.hitunknown",
											result.getBlockPos())));
				}
			}
		}

		return super.onItemRightClick(world, player, hand);
	}

	@SideOnly(Side.CLIENT)
	private void printChatMessagesNoSpam(List<ITextComponent> messages) {

		int chatLineId = BASE_CHAT_LINE_ID;

		deleteChatMessages(BASE_CHAT_LINE_ID + messages.size() - 1);

		for (ITextComponent message : messages) {
			printChatMessageNoSpam(message, chatLineId++);
		}
	}

	@SideOnly(Side.CLIENT)
	private void printChatMessageNoSpam(ITextComponent message) {

		deleteChatMessages(BASE_CHAT_LINE_ID + 1);

		printChatMessageNoSpam(message, BASE_CHAT_LINE_ID);
	}

	@SideOnly(Side.CLIENT)
	private void deleteChatMessages(int startId) {

		for (int i = startId; i < BASE_CHAT_LINE_ID + 10; i++) {
			Minecraft.getMinecraft().ingameGUI.getChatGUI().deleteChatLine(i);
		}
	}

	@SideOnly(Side.CLIENT)
	private void printChatMessageNoSpam(ITextComponent message, int chatLineId) {

		Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(message, chatLineId);
	}

	private String getEntityName(Entity entity) {

		String name = EntityList.getEntityString(entity);
		return name != null ? I18n.translateToLocal("entity." + name + ".name") : "Unknown Entity";
	}

	private RayTraceResult rayTrace() {

		Entity renderViewEntity = Minecraft.getMinecraft().getRenderViewEntity();
		if (renderViewEntity == null || Minecraft.getMinecraft().world == null) {
			return null;
		}

		double range = MFRConfig.spyglassRange.getInt();
		RayTraceResult objHit = renderViewEntity.rayTrace(range, 1.0F);
		double blockDist = range;
		Vec3d playerPos = new Vec3d(renderViewEntity.posX, renderViewEntity.posY, renderViewEntity.posZ);
		playerPos = playerPos.addVector(0, renderViewEntity.getEyeHeight(), 0);

		if (objHit != null) {
			if (objHit.typeOfHit == RayTraceResult.Type.MISS) {
				objHit = null;
			} else {
				blockDist = objHit.hitVec.distanceTo(playerPos);
			}
		}

		Vec3d playerLook = renderViewEntity.getLook(1.0F);
		Vec3d playerLookRel = playerPos
				.addVector(playerLook.x * range, playerLook.y * range, playerLook.z * range);
		List<Entity> list = Minecraft.getMinecraft().world.getEntitiesWithinAABBExcludingEntity(
				renderViewEntity,
				renderViewEntity
						.getEntityBoundingBox()
						.expand(playerLook.x * range, playerLook.y * range, playerLook.z * range)
						.grow(1, 1, 1));

		double entityDistTotal = blockDist;
		Entity pointedEntity = null;
		for (Entity entity : list) {
			if (entity.canBeCollidedWith()) {
				double entitySize = entity.getCollisionBorderSize();
				AxisAlignedBB axisalignedbb = entity.getEntityBoundingBox().grow(entitySize, entitySize, entitySize);
				RayTraceResult traceResult = axisalignedbb.calculateIntercept(playerPos, playerLookRel);

				if (axisalignedbb.contains(playerPos)) {
					if (0.0D < entityDistTotal || entityDistTotal == 0.0D) {
						pointedEntity = entity;
						entityDistTotal = 0.0D;
					}
				} else if (traceResult != null) {
					double entityDist = playerPos.distanceTo(traceResult.hitVec);

					if (entityDist < entityDistTotal || entityDistTotal == 0.0D) {
						pointedEntity = entity;
						entityDistTotal = entityDist;
					}
				}
			}
		}

		if (pointedEntity != null && (entityDistTotal < blockDist || objHit == null)) {
			objHit = new RayTraceResult(pointedEntity);
		}
		return objHit;
	}

	@Override
	protected int getWeaponDamage(@Nonnull ItemStack stack) {

		return 2;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels() {

		ModelHelper.registerModel(this, "tool", "variant=spyglass");
	}
}
