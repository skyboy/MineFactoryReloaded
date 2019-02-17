package powercrystals.minefactoryreloaded.net;

import net.minecraft.network.Packet;
import net.minecraft.server.management.PlayerChunkMap;
import net.minecraft.server.management.PlayerChunkMapEntry;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;

public final class Packets
{
	public static void sendToServer(IMessage message)
	{
		MineFactoryReloadedCore.networkWrapper.sendToServer(message);
	}
	public static void sendToAllPlayersWatching(World world, BlockPos pos, Packet packet)
	{
		if (packet == null)
			return;
		if (world instanceof WorldServer)
		{
			PlayerChunkMap map = ((WorldServer)world).getPlayerChunkMap();
			PlayerChunkMapEntry entry = map.getEntry(pos.getX() >> 4, pos.getZ() >> 4);
			if (entry != null)
				entry.sendPacket(packet);
		}
	}
	public static void sendToAllPlayersInRange(World world, BlockPos pos, int range, Packet packet)
	{
		if (packet == null)
			return;
		if (world instanceof WorldServer)
		{
			// FIXME: this logic sends the packet multiple times
			PlayerChunkMap map = ((WorldServer)world).getPlayerChunkMap();
			int xS = (pos.getX() - range) >> 4, zS = (pos.getZ() - range) >> 4;
			int xE = (pos.getX() + range) >> 4, zE = (pos.getZ() + range) >> 4;
			for (; xS < xE; ++xS) for (; zS < zE; ++zS)
			{
				PlayerChunkMapEntry entry = map.getEntry(xS, zS);
				if (entry != null)
				{
					entry.sendPacket(packet);
					/*
					List<EntityPlayerMP> players = entry.playersWatchingChunk;
					for (int i = players.size(); i --> 0; )
					{
						EntityPlayerMP player = players.get(i);
						if (Math.abs(x - player.posX) < range)
							if (Math.abs(y - player.posY) < range)
								if (Math.abs(z - player.posZ) < range)
									player.connection.sendPacket(packet);
					}
*/
				}
			}
		}
	}

	public static final short EnchanterButton	= 0;
	public static final short HarvesterButton	= 1;
	public static final short ChronotyperButton	= 2;
	public static final short HAMUpdate			= 3;
	public static final short AutoJukeboxButton	= 4;
	public static final short AutoSpawnerButton	= 5;
	public static final short CircuitDefinition	= 6;
	public static final short LogicSetCircuit	= 7;
	public static final short LogicSetPin		= 8;
	public static final short LogicReinitialize	= 9;
	public static final short RouterButton		= 10;
	public static final short RocketLaunch		= 11;
	public static final short FakeSlotChange		= 20; // TODO: remove in favor of CoFH fake slots

	public static void sendToAllPlayersWatching(TileEntity te, Packet packet)
	{
		sendToAllPlayersWatching(te.getWorld(), te.getPos(), packet);
	}
	public static void sendToAllPlayersWatching(TileEntity te)
	{
		sendToAllPlayersWatching(te, te.getUpdatePacket());
	}
}
