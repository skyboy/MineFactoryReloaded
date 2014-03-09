package powercrystals.minefactoryreloaded.block;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.setup.MFRConfig;

public class BlockRailPassengerPickup extends BlockFactoryRail
{
	public BlockRailPassengerPickup(int blockId)
	{
		super(blockId, true, false);
		setUnlocalizedName("mfr.rail.passenger.pickup");
	}

	@Override
	public void onMinecartPass(World world, EntityMinecart minecart, int x, int y, int z)
	{
		if (world.isRemote || !minecart.canBeRidden())
			return;

		int searchX = MFRConfig.passengerRailSearchMaxHorizontal.getInt();
		int searchY = MFRConfig.passengerRailSearchMaxVertical.getInt();
		AxisAlignedBB bb = AxisAlignedBB.getBoundingBox(x - searchX, y - searchY, z - searchX,
				x + searchX + 1, y + searchY + 1, z + searchX + 1);

		Class<? extends Entity> target = isPowered(world, x, y, z) ? EntityLiving.class : EntityPlayer.class;
		List<? extends Entity> entities = world.getEntitiesWithinAABB(target, bb);

		for (Entity o : entities)
			if (!o.isDead)
			{
				o.mountEntity(minecart);
				return;
			}
	}
}
