package powercrystals.minefactoryreloaded.net;

import cpw.mods.fml.common.IScheduledTickHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.packet.Packet51MapChunk;
import net.minecraft.server.management.PlayerInstance;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.event.TextureStitchEvent.Post;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.LoadingCallback;
import net.minecraftforge.common.ForgeChunkManager.Ticket;

import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.tile.machine.TileEntityChunkLoader;

public class CommonProxy implements IMFRProxy, IScheduledTickHandler, LoadingCallback 
{
	private GridTickHandler gridTickHandler;
		
	@Override
	public void ticketsLoaded(List<Ticket> tickets, World world) 
	{
		for (Ticket ticket : tickets) 
		{
			int x = ticket.getModData().getInteger("X");
			int y = ticket.getModData().getInteger("Y");
			int z = ticket.getModData().getInteger("Z");
			
			TileEntity tile = world.getBlockTileEntity(x, y, z);
			if (!(tile instanceof TileEntityChunkLoader))
			{
				ForgeChunkManager.releaseTicket(ticket);
				continue;
			}
			((TileEntityChunkLoader)tile).receiveTicket(ticket);			
		}
	}
	
	@Override
	public void init()
	{
		gridTickHandler = new GridTickHandler();
		TickRegistry.registerScheduledTickHandler(gridTickHandler, Side.SERVER);
		TickRegistry.registerScheduledTickHandler(this, Side.SERVER);
		ForgeChunkManager.setForcedChunkLoadingCallback(MineFactoryReloadedCore.instance(), this);
	}

	@Override
	public void movePlayerToCoordinates(EntityLivingBase e, double x, double y, double z)
	{
		if (e instanceof EntityPlayerMP)
		{
			EntityPlayerMP ep = (EntityPlayerMP)e;
			ep.playerNetServerHandler.setPlayerLocation(x, y, z, ep.cameraYaw, ep.cameraPitch);
		}
		e.setPositionAndUpdate(x, y, z);
	}

	@Override
	public void onPostTextureStitch(Post e)
	{
	}

	private long lastTime = 0;
	private LinkedList<Chunk> chunksToRelight = new LinkedList<Chunk>();

	@Override
	public void addRelightChunk(Chunk chunk)
	{
		if (chunk != null)
		{
			chunksToRelight.add(chunk);
			chunk.resetRelightChecks();
			chunk.isModified = true;
			World world = chunk.worldObj;
			if (world instanceof WorldServer)
			{
				PlayerInstance watcher = ((WorldServer)world).getPlayerManager().
						getOrCreateChunkWatcher(chunk.xPosition, chunk.zPosition, false);
				if (watcher != null)
					watcher.sendToAllPlayersWatchingChunk(new Packet51MapChunk(chunk, false, -1));
			}
		}
	}

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData)
	{
		lastTime = System.nanoTime();
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData)
	{
		long time;
		if (chunksToRelight.size() > 0) do
		{
			Chunk chunk = chunksToRelight.pollFirst();
			if (chunk == null)
				break;
			chunk.generateSkylightMap();
			chunk.updateSkylight();
			chunk.enqueueRelightChecks();
			// TODO: manually relight chunks, then send them to all players in range
			/*
			World world = chunk.worldObj;
			if (world instanceof WorldServer)
			{
				PlayerInstance watcher = ((WorldServer)world).getPlayerManager().
						getOrCreateChunkWatcher(chunk.xPosition, chunk.zPosition, false);
				if (watcher != null)
					watcher.sendToAllPlayersWatchingChunk(new Packet51MapChunk(chunk, false, 15));
			}//*/
			time = System.nanoTime() - lastTime;
		} while (time < 50);
	}

	@Override
	public EnumSet<TickType> ticks()
	{
		return EnumSet.of(TickType.SERVER);
	}

	@Override
	public String getLabel()
	{
		return "MFR server chunk relight";
	}

	@Override
	public int nextTickSpacing()
	{
		return 1;
	}
}
