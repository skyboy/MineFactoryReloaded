package powercrystals.minefactoryreloaded.net;

import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.server.management.PlayerInstance;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import powercrystals.core.net.PacketWrapper;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.entity.EntityRocket;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactory;
import powercrystals.minefactoryreloaded.tile.machine.TileEntityAutoAnvil;
import powercrystals.minefactoryreloaded.tile.machine.TileEntityAutoDisenchanter;
import powercrystals.minefactoryreloaded.tile.machine.TileEntityAutoEnchanter;
import powercrystals.minefactoryreloaded.tile.machine.TileEntityAutoJukebox;
import powercrystals.minefactoryreloaded.tile.machine.TileEntityAutoSpawner;
import powercrystals.minefactoryreloaded.tile.machine.TileEntityBlockSmasher;
import powercrystals.minefactoryreloaded.tile.machine.TileEntityChronotyper;
import powercrystals.minefactoryreloaded.tile.machine.TileEntityChunkLoader;
import powercrystals.minefactoryreloaded.tile.machine.TileEntityEjector;
import powercrystals.minefactoryreloaded.tile.machine.TileEntityEnchantmentRouter;
import powercrystals.minefactoryreloaded.tile.machine.TileEntityHarvester;
import powercrystals.minefactoryreloaded.tile.machine.TileEntityItemRouter;
import powercrystals.minefactoryreloaded.tile.rednet.TileEntityRedNetLogic;

public class ServerPacketHandler implements IPacketHandler
{
	public static void sendToAllPlayersWatching(World world, int x, int y, int z, Packet packet)
	{
		if (packet == null)
			return;
		if (world instanceof WorldServer)
		{
			PlayerInstance watcher = ((WorldServer)world).getPlayerManager().
					getOrCreateChunkWatcher(x >> 4, x >> 4, false);
			if (watcher != null)
				watcher.sendToAllPlayersWatchingChunk(packet);
		}
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player)
	{
		DataInputStream data = new DataInputStream(new ByteArrayInputStream(packet.data));
		int packetType = PacketWrapper.readPacketID(data);
		
		Class[] decodeAs;
		Object[] packetReadout;
		TileEntity te;
		
		switch (packetType)
		{
		case Packets.EnchanterButton: // client -> server: autoenchanter GUI buttons
			decodeAs = new Class[]{ Integer.class, Integer.class, Integer.class, Integer.class };
			packetReadout = PacketWrapper.readPacketData(data, decodeAs);
			
			te = ((EntityPlayer)player).worldObj.getBlockTileEntity((Integer)packetReadout[0], (Integer)packetReadout[1], (Integer)packetReadout[2]);
			if(te instanceof TileEntityAutoEnchanter)
			{
				((TileEntityAutoEnchanter)te).setTargetLevel(((TileEntityAutoEnchanter)te).getTargetLevel() + (Integer)packetReadout[3]);
			}
			else if(te instanceof TileEntityBlockSmasher)
			{
				((TileEntityBlockSmasher)te).setFortune(((TileEntityBlockSmasher)te).getFortune() + (Integer)packetReadout[3]);
			}
			else if(te instanceof TileEntityAutoDisenchanter)
			{
				Integer v = (Integer)packetReadout[3];
				((TileEntityAutoDisenchanter)te).setRepeatDisenchant(v == 1 ? true : false);
			}
			break;
		case Packets.HarvesterButton: // client -> server: harvester setting
			decodeAs = new Class[]{ Integer.class, Integer.class, Integer.class, String.class, Boolean.class };
			packetReadout = PacketWrapper.readPacketData(data, decodeAs);
			
			te = ((EntityPlayer)player).worldObj.getBlockTileEntity((Integer)packetReadout[0], (Integer)packetReadout[1], (Integer)packetReadout[2]);
			if(te instanceof TileEntityHarvester)
			{
				((TileEntityHarvester)te).getSettings().put((String)packetReadout[3], (Boolean)packetReadout[4]);
			}
			break;
		case Packets.ChronotyperButton: // client -> server: toggle chronotyper
			decodeAs = new Class[]{ Integer.class, Integer.class, Integer.class };
			packetReadout = PacketWrapper.readPacketData(data, decodeAs);
			
			te = ((EntityPlayer)player).worldObj.getBlockTileEntity((Integer)packetReadout[0], (Integer)packetReadout[1], (Integer)packetReadout[2]);
			if(te instanceof TileEntityChronotyper)
			{
				((TileEntityChronotyper)te).setMoveOld(!((TileEntityChronotyper)te).getMoveOld());
			}
			break;
		case Packets.AutoJukeboxButton: // client -> server: copy record
			decodeAs = new Class[]{ Integer.class, Integer.class, Integer.class, Integer.class };
			packetReadout = PacketWrapper.readPacketData(data, decodeAs);
			
			te = ((EntityPlayer)player).worldObj.getBlockTileEntity((Integer)packetReadout[0], (Integer)packetReadout[1], (Integer)packetReadout[2]);
			if(te instanceof TileEntityAutoJukebox)
			{
				TileEntityAutoJukebox j = ((TileEntityAutoJukebox)te);
				int button = (Integer)packetReadout[3];
				if(button == 1) j.playRecord();
				else if(button == 2) j.stopRecord();
				else if(button == 3) j.copyRecord();
			}
			break;
		case Packets.AutoSpawnerButton: // client -> server: toggle autospawner
			decodeAs = new Class[]{ Integer.class, Integer.class, Integer.class };
			packetReadout = PacketWrapper.readPacketData(data, decodeAs);
			
			te = ((EntityPlayer)player).worldObj.getBlockTileEntity((Integer)packetReadout[0], (Integer)packetReadout[1], (Integer)packetReadout[2]);
			if(te instanceof TileEntityAutoSpawner)
			{
				((TileEntityAutoSpawner)te).setSpawnExact(!((TileEntityAutoSpawner)te).getSpawnExact());
			}
			break;
		case Packets.LogicRequestCircuitDefinition: // client -> server: request circuit from server
			decodeAs = new Class[]{ Integer.class, Integer.class, Integer.class, Integer.class };
			packetReadout = PacketWrapper.readPacketData(data, decodeAs);
			
			te = ((EntityPlayer)player).worldObj.getBlockTileEntity((Integer)packetReadout[0], (Integer)packetReadout[1], (Integer)packetReadout[2]);
			if(te instanceof TileEntityRedNetLogic)
			{
				((TileEntityRedNetLogic)te).sendCircuitDefinition((Integer)packetReadout[3]);
			}
			break;
		case Packets.LogicSetCircuit: // client -> server: set circuit
			decodeAs = new Class[]{ Integer.class, Integer.class, Integer.class, Integer.class, String.class };
			packetReadout = PacketWrapper.readPacketData(data, decodeAs);
			
			te = ((EntityPlayer)player).worldObj.getBlockTileEntity((Integer)packetReadout[0], (Integer)packetReadout[1], (Integer)packetReadout[2]);
			if(te instanceof TileEntityRedNetLogic)
			{
				((TileEntityRedNetLogic)te).initCircuit((Integer)packetReadout[3], (String)packetReadout[4]);
				((TileEntityRedNetLogic)te).sendCircuitDefinition((Integer)packetReadout[3]);
			}
			break;
		case Packets.LogicSetPin: // client -> server: set pin
			decodeAs = new Class[]{ Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class };
			packetReadout = PacketWrapper.readPacketData(data, decodeAs);
			
			te = ((EntityPlayer)player).worldObj.getBlockTileEntity((Integer)packetReadout[0], (Integer)packetReadout[1], (Integer)packetReadout[2]);
			if(te instanceof TileEntityRedNetLogic)
			{
				if((Integer)packetReadout[3] == 0)
				{
					((TileEntityRedNetLogic)te).setInputPinMapping((Integer)packetReadout[4], (Integer)packetReadout[5], (Integer)packetReadout[6], (Integer)packetReadout[7]);
				}
				else if((Integer)packetReadout[3] == 1)
				{
					((TileEntityRedNetLogic)te).setOutputPinMapping((Integer)packetReadout[4], (Integer)packetReadout[5], (Integer)packetReadout[6], (Integer)packetReadout[7]);
				}
				((TileEntityRedNetLogic)te).sendCircuitDefinition((Integer)packetReadout[4]);
			}
			break;
		case Packets.LogicReinitialize: // client -> server: set circuit
			decodeAs = new Class[]{ Integer.class, Integer.class, Integer.class };
			packetReadout = PacketWrapper.readPacketData(data, decodeAs);
			
			te = ((EntityPlayer)player).worldObj.getBlockTileEntity((Integer)packetReadout[0], (Integer)packetReadout[1], (Integer)packetReadout[2]);
			if(te instanceof TileEntityRedNetLogic)
			{
				((TileEntityRedNetLogic)te).reinitialize((EntityPlayer)player);
			}
			break;
		case Packets.RouterButton: // client -> server: toggle 'levels'/'reject unmapped' mode
			decodeAs = new Class[]{ Integer.class, Integer.class, Integer.class, Integer.class };
			packetReadout = PacketWrapper.readPacketData(data, decodeAs);
			
			te = ((EntityPlayer)player).worldObj.getBlockTileEntity((Integer)packetReadout[0], (Integer)packetReadout[1], (Integer)packetReadout[2]);
			if(te instanceof TileEntityEnchantmentRouter)
			{
				switch ((Integer)packetReadout[3])
				{
				case 2:
					((TileEntityItemRouter)te).setRejectUnmapped(!((TileEntityItemRouter)te).getRejectUnmapped());
					break;
				case 1:
					((TileEntityEnchantmentRouter)te).setMatchLevels(!((TileEntityEnchantmentRouter)te).getMatchLevels());
					break;
				}
			}
			else if(te instanceof TileEntityItemRouter)
			{
				((TileEntityItemRouter)te).setRejectUnmapped(!((TileEntityItemRouter)te).getRejectUnmapped());
			}
			else if(te instanceof TileEntityEjector)
			{
				switch ((Integer)packetReadout[3])
				{
				case 1:
					((TileEntityEjector)te).setIsWhitelist(!((TileEntityEjector)te).getIsWhitelist());
					break;
				case 2:
					((TileEntityEjector)te).setIsNBTMatch(!((TileEntityEjector)te).getIsNBTMatch());
					break;
				case 3:
					((TileEntityEjector)te).setIsIDMatch(!((TileEntityEjector)te).getIsIDMatch());
					break;
				}
			}
			else if(te instanceof TileEntityAutoAnvil)
			{
				((TileEntityAutoAnvil)te).setRepairOnly(!((TileEntityAutoAnvil)te).getRepairOnly());
			}
			else if(te instanceof TileEntityChunkLoader)
			{
				((TileEntityChunkLoader)te).setRadius((short)(int)(Integer)packetReadout[3]);
			}
			break;
		case Packets.FakeSlotChange: // client -> server: client clicked on a fake slot
			decodeAs = new Class[]{ Integer.class, Integer.class, Integer.class, Integer.class };
			packetReadout = PacketWrapper.readPacketData(data, decodeAs);
			
			ItemStack playerStack = ((EntityPlayer)player).inventory.getItemStack();
			Integer slotNumber = (Integer)packetReadout[3];
			te = ((EntityPlayer)player).worldObj.getBlockTileEntity((Integer)packetReadout[0], (Integer)packetReadout[1], (Integer)packetReadout[2]);
			if(te instanceof IInventory)
			{
				if(playerStack == null)
				{
					((IInventory)te).setInventorySlotContents(slotNumber, null);
				}
				else
				{
					playerStack = playerStack.copy();
					playerStack.stackSize = 1;
					((IInventory)te).setInventorySlotContents(slotNumber, playerStack);
				}
			}
			break;
		case Packets.RocketLaunchWithLock: // client -> server: client firing SPAMR missile
			decodeAs = new Class[]{ Integer.class, Integer.class };
			packetReadout = PacketWrapper.readPacketData(data, decodeAs);
			
			World world = ((EntityPlayer)player).worldObj;
			Entity owner = world.getEntityByID((Integer)packetReadout[0]);
			Entity target = null;
			if(((Integer)packetReadout[1]) != Integer.MIN_VALUE)
			{
				target = world.getEntityByID((Integer)packetReadout[1]);
			}
			
			if(owner instanceof EntityLivingBase)
			{
				EntityRocket r = new EntityRocket(world, ((EntityLivingBase)owner), target);
				world.spawnEntityInWorld(r);
			}
			break;
		case Packets.HAMUpdate: // client -> server: client requesting harvest area update
			decodeAs = new Class[]{ Integer.class, Integer.class, Integer.class };
			packetReadout = PacketWrapper.readPacketData(data, decodeAs);
			
			te = ((EntityPlayer)player).worldObj.getBlockTileEntity((Integer)packetReadout[0], (Integer)packetReadout[1], (Integer)packetReadout[2]);
			if (te instanceof TileEntityFactory)
			{
				TileEntityFactory tef = (TileEntityFactory)te;
				if (tef.hasHAM())
				{
					Packet t = PacketWrapper.createPacket(MineFactoryReloadedCore.modNetworkChannel,
							Packets.HAMUpdate, new Object[]{te.xCoord, te.yCoord, te.zCoord, tef.getHAM().getUpgradeLevel()});;
					PacketDispatcher.sendPacketToPlayer(t, player);
				}
			}
		}
	}
}
