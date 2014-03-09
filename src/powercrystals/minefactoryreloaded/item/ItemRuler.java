package powercrystals.minefactoryreloaded.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import powercrystals.minefactoryreloaded.setup.MFRConfig;

public class ItemRuler extends ItemFactory
{
	public ItemRuler(int id)
	{
		super(id);
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		if (world.isRemote)
		{
			MovingObjectPosition mop = player.rayTrace(MFRConfig.spyglassRange.getInt(), 1.0F);
			if(mop == null || (mop.typeOfHit == EnumMovingObjectType.ENTITY && mop.entityHit == null))
			{
				player.sendChatToPlayer(new ChatMessageComponent()
						.addKey("chat.info.mfr.ruler.nosight"));
			}
			else if(mop.typeOfHit == EnumMovingObjectType.ENTITY)
			{
				player.sendChatToPlayer(new ChatMessageComponent()
						.addKey("chat.info.mfr.ruler.hitentity"));
			}
			else
			{
				NBTTagCompound data = player.getEntityData();
				NBTTagCompound tag = data.getCompoundTag("ruler");
				if (!data.hasKey("ruler"))
				{
					tag.setInteger("x", mop.blockX);
					tag.setInteger("y", mop.blockY);
					tag.setInteger("z", mop.blockZ);
					data.setCompoundTag("ruler", tag);
					player.sendChatToPlayer(new ChatMessageComponent()
							.addKey("chat.info.mfr.ruler.startposition"));
				}
				else
				{
					int x = tag.getInteger("x");
					int y = tag.getInteger("y");
					int z = tag.getInteger("z");
					data.removeTag("ruler");
					
					int distX = Math.abs(mop.blockX - x);
					int distY = Math.abs(mop.blockY - y);
					int distZ = Math.abs(mop.blockZ - z);
					
					double distAll = Math.sqrt(Math.pow(distX, 2) +
							Math.pow(distY, 2) + Math.pow(distZ, 2));
					
					player.sendChatToPlayer(new ChatMessageComponent()
							.addText("X: ").addText(StatCollector
									.translateToLocalFormatted("chat.info.mfr.ruler.distance",
											distX, distX + 1)));
					player.sendChatToPlayer(new ChatMessageComponent()
							.addText("Y: ").addText(StatCollector
									.translateToLocalFormatted("chat.info.mfr.ruler.distance",
											distY, distY + 1)));
					player.sendChatToPlayer(new ChatMessageComponent()
							.addText("Z: ").addText(StatCollector
									.translateToLocalFormatted("chat.info.mfr.ruler.distance",
											distZ, distZ + 1)));
					player.sendChatToPlayer(new ChatMessageComponent()
							.addText(StatCollector
									.translateToLocalFormatted("chat.info.mfr.ruler.total",
											distAll)));
				}
			}
		}
		
		return super.onItemRightClick(stack, world, player);
	}
}
