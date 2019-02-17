package powercrystals.minefactoryreloaded.net;

import cofh.core.network.PacketBase;
import cofh.core.network.PacketHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.core.UtilInventory;
import powercrystals.minefactoryreloaded.core.settings.BooleanSetting;
import powercrystals.minefactoryreloaded.entity.EntityRocket;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactory;
import powercrystals.minefactoryreloaded.tile.machine.*;
import powercrystals.minefactoryreloaded.tile.machine.animals.TileEntityChronotyper;
import powercrystals.minefactoryreloaded.tile.machine.blocks.TileEntityBlockSmasher;
import powercrystals.minefactoryreloaded.tile.machine.enchantment.TileEntityAutoAnvil;
import powercrystals.minefactoryreloaded.tile.machine.enchantment.TileEntityAutoDisenchanter;
import powercrystals.minefactoryreloaded.tile.machine.enchantment.TileEntityAutoEnchanter;
import powercrystals.minefactoryreloaded.tile.machine.storage.TileEntityDeepStorageUnit;
import powercrystals.minefactoryreloaded.tile.machine.routing.TileEntityEjector;
import powercrystals.minefactoryreloaded.tile.machine.mobs.TileEntityAutoSpawner;
import powercrystals.minefactoryreloaded.tile.machine.mobs.TileEntityMobRouter;
import powercrystals.minefactoryreloaded.tile.machine.plants.TileEntityHarvester;
import powercrystals.minefactoryreloaded.tile.machine.plants.TileEntityPlanter;
import powercrystals.minefactoryreloaded.tile.machine.routing.TileEntityEnchantmentRouter;
import powercrystals.minefactoryreloaded.tile.machine.routing.TileEntityItemRouter;
import powercrystals.minefactoryreloaded.tile.rednet.TileEntityRedNetLogic;

import javax.annotation.Nonnull;

public class MFRPacket extends PacketBase {

	public static void initialize() {

		PacketHandler.INSTANCE.registerPacket(MFRPacket.class);
	}

	public enum PacketType {

		HAM_UPDATE, UPGRADE_LEVEL, ENCHANTER_BUTTON, HARVESTER_BUTTON, CHRONOTYPER_BUTTON, AUTO_JUKEBOX_BUTTON, AUTO_SPAWNER_BUTTON, CIRCUIT_DEFINITION,
		LOGIC_SET_CIRCUIT, LOGIC_SET_PIN, LOGIC_REINITIALIZE, ROUTER_BUTTON, FAKE_SLOT_CHANGE, ROCKET_LAUNCH
	}

	@Override
	public void handlePacket(EntityPlayer player, boolean isServer) {

		try {
			int type = getByte();
			BlockPos pos;
			TileEntity te;
			World world = player.world;

			switch(PacketType.values()[type]) {
				case HAM_UPDATE:
					pos = getCoords();
					te = world.getTileEntity(pos);
					if (te instanceof TileEntityFactory && ((TileEntityFactory) te).hasHAM()) {
						sendUpgradeLevelToClient((TileEntityFactory) te);
					}
					return;
				case UPGRADE_LEVEL:
					pos = getCoords();
					te = world.getTileEntity(pos);
					if (te instanceof TileEntityFactory && ((TileEntityFactory) te).hasHAM()) {
						((TileEntityFactory) te).getHAM().setUpgradeLevel(getByte());
					}
					return;
				case ENCHANTER_BUTTON: // client -> server: autoenchanter GUI buttons
					pos = getCoords();
					te = world.getTileEntity(pos);

					byte amt = getByte();
					if (te instanceof TileEntityAutoEnchanter) {
						((TileEntityAutoEnchanter) te).setTargetLevel(((TileEntityAutoEnchanter) te).getTargetLevel() + amt);
					} else if (te instanceof TileEntityBlockSmasher) {
						((TileEntityBlockSmasher) te).setFortune(((TileEntityBlockSmasher) te).getFortune() + amt);
					} else if (te instanceof TileEntityAutoDisenchanter) {
						((TileEntityAutoDisenchanter) te).setRepeatDisenchant(amt == 1 ? true : false);
					}
					break;
				case HARVESTER_BUTTON: // client -> server: harvester setting
					pos = getCoords();
					te = world.getTileEntity(pos);

					if (te instanceof TileEntityHarvester) {
						((TileEntityHarvester) te).getSettings().put(getString(), getBool() ? BooleanSetting.TRUE : BooleanSetting.FALSE);
					}
					break;
				case CHRONOTYPER_BUTTON:
					pos = getCoords();
					te = world.getTileEntity(pos);

					if (te instanceof TileEntityChronotyper) {
						((TileEntityChronotyper) te).setMoveOld(!((TileEntityChronotyper) te).getMoveOld());
					} else if (te instanceof TileEntityDeepStorageUnit) {
						((TileEntityDeepStorageUnit) te).setIsActive(!((TileEntityDeepStorageUnit) te).isActive());
						((TileEntityDeepStorageUnit) te).markForUpdate();
						Packets.sendToAllPlayersWatching(te);
					}
					break;
				case AUTO_JUKEBOX_BUTTON: // client -> server: copy record
					pos = getCoords();
					te = world.getTileEntity(pos);

					if (te instanceof TileEntityAutoJukebox) {
						TileEntityAutoJukebox j = ((TileEntityAutoJukebox) te);
						int button = getByte();
						if (button == 1)
							j.playRecord();
						else if (button == 2)
							j.stopRecord();
						else if (button == 3) j.copyRecord();
					}
					break;
				case AUTO_SPAWNER_BUTTON:
					pos = getCoords();
					te = world.getTileEntity(pos);

					if (te instanceof TileEntityAutoSpawner) {
						((TileEntityAutoSpawner) te).setSpawnExact(!((TileEntityAutoSpawner) te).getSpawnExact());
					}
					break;
				case CIRCUIT_DEFINITION:
					pos = getCoords();
					te = world.getTileEntity(pos);

					if (te instanceof TileEntityRedNetLogic) {
						((TileEntityRedNetLogic) te).sendCircuitDefinition(getInt());
					}
					break;
				case LOGIC_SET_CIRCUIT:
					pos = getCoords();
					te = world.getTileEntity(pos);

					int circuit = getInt();
					if (te instanceof TileEntityRedNetLogic) {
						((TileEntityRedNetLogic) te).initCircuit(circuit, getString());
						((TileEntityRedNetLogic) te).sendCircuitDefinition(circuit);
					}
					break;
				case LOGIC_SET_PIN:
					pos = getCoords();
					te = world.getTileEntity(pos);

					amt = getByte();
					int circuitIndex = getInt(),
							pinIndex = getInt(),
							buffer = getInt(),
							pin = getInt();
					if (te instanceof TileEntityRedNetLogic) {
						if (amt == 0) {
							((TileEntityRedNetLogic) te).setInputPinMapping(circuitIndex, pinIndex, buffer, pin);
						} else if (amt == 1) {
							((TileEntityRedNetLogic) te).setOutputPinMapping(circuitIndex, pinIndex, buffer, pin);
						}
						((TileEntityRedNetLogic) te).sendCircuitDefinition(circuitIndex);
					}
					break;
				case LOGIC_REINITIALIZE:
					pos = getCoords();
					te = world.getTileEntity(pos);
					player = (EntityPlayer) world.getEntityByID(getInt());

					if (te instanceof TileEntityRedNetLogic) {
						((TileEntityRedNetLogic) te).reinitialize(player);
					}
					break;
				case ROUTER_BUTTON:
					pos = getCoords();
					te = world.getTileEntity(pos);

					int data = getInt();
					if (te instanceof TileEntityEnchantmentRouter) {
						switch (data) {
							case 2:
								((TileEntityItemRouter) te).setRejectUnmapped(!((TileEntityItemRouter) te).getRejectUnmapped());
								break;
							case 1:
								((TileEntityEnchantmentRouter) te).setMatchLevels(!((TileEntityEnchantmentRouter) te).getMatchLevels());
								break;
						}
					} else if (te instanceof TileEntityItemRouter) {
						((TileEntityItemRouter) te).setRejectUnmapped(!((TileEntityItemRouter) te).getRejectUnmapped());
					} else if (te instanceof TileEntityEjector) {
						switch (data) {
							case 1:
								((TileEntityEjector) te).setIsWhitelist(!((TileEntityEjector) te).getIsWhitelist());
								break;
							case 2:
								((TileEntityEjector) te).setIsNBTMatch(!((TileEntityEjector) te).getIsNBTMatch());
								break;
							case 3:
								((TileEntityEjector) te).setIsIDMatch(!((TileEntityEjector) te).getIsIDMatch());
								break;
						}
					} else if (te instanceof TileEntityAutoAnvil) {
						((TileEntityAutoAnvil) te).setRepairOnly(!((TileEntityAutoAnvil) te).getRepairOnly());
					} else if (te instanceof TileEntityChunkLoader) {
						((TileEntityChunkLoader) te).setRadius((short) data);
					} else if (te instanceof TileEntityPlanter) {
						((TileEntityPlanter) te).setConsumeAll(!((TileEntityPlanter) te).getConsumeAll());
					} else if (te instanceof TileEntityMobRouter) {
						switch (data) {
							case 1:
								((TileEntityMobRouter) te).setWhiteList(!((TileEntityMobRouter) te).getWhiteList());
								break;
							case 2:
								((TileEntityMobRouter) te).setMatchMode(((TileEntityMobRouter) te).getMatchMode() + 1);
								break;
							case 3:
								((TileEntityMobRouter) te).setMatchMode(((TileEntityMobRouter) te).getMatchMode() - 1);
								break;
						}
					}
					break;
				case FAKE_SLOT_CHANGE:
					pos = getCoords();
					te = world.getTileEntity(pos);
					player = (EntityPlayer) world.getEntityByID(getInt());

					@Nonnull ItemStack playerStack = player.inventory.getItemStack();
					int slotNumber = getInt(),
							click = getByte();
					if (te instanceof IInventory) {
						if (playerStack.isEmpty()) {
							((IInventory) te).setInventorySlotContents(slotNumber, ItemStack.EMPTY);
						} else {
							playerStack = playerStack.copy();
							playerStack.setCount(click == 1 ? -1 : 1);
							@Nonnull ItemStack s = ((IInventory) te).getStackInSlot(slotNumber);
							if (!UtilInventory.stacksEqual(s, playerStack))
								playerStack.setCount(1);
							else
								playerStack.setCount(Math.max(playerStack.getCount() + s.getCount(), 1));
							((IInventory) te).setInventorySlotContents(slotNumber, playerStack);
						}
					}
					break;
				case ROCKET_LAUNCH:
					Entity owner = world.getEntityByID(getInt());
					boolean hasLock = getBool();
					int t = getVarInt();
					Entity target = null;
					if (hasLock) {
						target = world.getEntityByID(t);
					}

					if (owner instanceof EntityLivingBase) {
						EntityRocket r = new EntityRocket(world, ((EntityLivingBase) owner), target);
						world.spawnEntity(r);
					}
					break;
			}

		} catch (Exception e) {
			MineFactoryReloadedCore.log().error("Packet payload failure! Please check your config files!");
			e.printStackTrace();
		}
	}

	public static void sendHAMUpdateToServer(TileEntity te) {

		PacketHandler.sendToServer(getPacket(PacketType.HAM_UPDATE).addCoords(te));
	}

	public static void sendUpgradeLevelToClient(TileEntityFactory te) {

		if (te.hasHAM()) {
			PacketHandler.sendToDimension(getPacket(PacketType.UPGRADE_LEVEL).addCoords(te).addByte(te.getHAM().getUpgradeLevel()),
					te.getWorld().provider.getDimension());
		}
	}

	public static void sendEnchanterButtonToServer(TileEntity te, byte amt) {
		
		PacketHandler.sendToServer(getPacket(PacketType.ENCHANTER_BUTTON).addCoords(te).addByte(amt));
	}
	
	public static void sendHarvesterButtonToServer(TileEntity te, String setting, boolean value) {
		
		PacketHandler.sendToServer(getPacket(PacketType.HARVESTER_BUTTON).addCoords(te).addString(setting).addBool(value));
	}

	public static void sendChronotyperButtonToServer(TileEntity te) {
		
		PacketHandler.sendToServer(getPacket(PacketType.CHRONOTYPER_BUTTON).addCoords(te));
	}

	public static void sendAutoJukeboxButtonToServer(TileEntity te, byte button) {

		PacketHandler.sendToServer(getPacket(PacketType.AUTO_JUKEBOX_BUTTON).addCoords(te).addByte(button));
	}

	public static void sendAutoSpawnerButtonToServer(TileEntity te) {

		PacketHandler.sendToServer(getPacket(PacketType.AUTO_SPAWNER_BUTTON).addCoords(te));
	}
	
	public static void requestCircuitDefinitionFromServer(TileEntity te, int circuit) {

		PacketHandler.sendToServer(getPacket(PacketType.CIRCUIT_DEFINITION).addCoords(te).addInt(circuit));
	}

	public static void requestLogicSetCircuitFromServer(TileEntity te, int circuit, String className) {

		PacketHandler.sendToServer(getPacket(PacketType.LOGIC_SET_CIRCUIT).addCoords(te).addInt(circuit).addString(className));
	}

	public static void sendLogicSetPinToServer(TileEntity te, byte amt, int circuit, int pinIndex, int buffer, int pin) {

		PacketHandler.sendToServer(getPacket(PacketType.LOGIC_SET_PIN).addCoords(te).addByte(amt).addInt(circuit).addInt(pinIndex).addInt(buffer).addInt(pin));
	}

	public static void sendLogicReinitializeToServer(TileEntity te, int entityId) {

		PacketHandler.sendToServer(getPacket(PacketType.LOGIC_REINITIALIZE).addCoords(te).addInt(entityId));
	}

	public static void sendRouterButtonToServer(TileEntity te, int data) {

		PacketHandler.sendToServer(getPacket(PacketType.ROUTER_BUTTON).addCoords(te).addInt(data));
	}

	public static void sendFakeSlotToServer(TileEntity te, int entityId, int slotNumber, byte click) {

		PacketHandler.sendToServer(getPacket(PacketType.FAKE_SLOT_CHANGE).addCoords(te).addInt(entityId).addInt(slotNumber).addByte(click));
	}

	public static void sendRocketLaunchToServer(int ownerId, boolean hasLock, int entityId) {

		PacketHandler.sendToServer(getPacket(PacketType.ROCKET_LAUNCH).addInt(ownerId).addBool(hasLock).addVarInt(entityId));
	}

	public static PacketBase getPacket(PacketType type) {

		return new MFRPacket().addByte(type.ordinal());
	}
}
