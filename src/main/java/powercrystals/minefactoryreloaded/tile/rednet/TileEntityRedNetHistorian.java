package powercrystals.minefactoryreloaded.tile.rednet;

//import buildcraft.api.transport.IPipeTile.PipeType;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.ITickable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import powercrystals.minefactoryreloaded.core.ArrayQueue;
import powercrystals.minefactoryreloaded.net.Packets;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactory;

public class TileEntityRedNetHistorian extends TileEntityFactory {

	private int _currentSubnet = 0;
	private int[] _lastValues = new int[16];

	public TileEntityRedNetHistorian() {

		super(null);
	}

	@Override
	protected NBTTagCompound writePacketData(NBTTagCompound tag) {

		tag.setInteger("subnet", _currentSubnet);
		tag.setInteger("current", _lastValues[_currentSubnet]);

		return super.writePacketData(tag);
	}

	@Override
	protected void handlePacketData(NBTTagCompound tag) {

		super.handlePacketData(tag);
		_currentSubnet = tag.getInteger("subnet");
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {

		NBTTagCompound data = pkt.getNbtCompound();
		switch (pkt.getTileEntityType()) {
		case 0:
			super.onDataPacket(net, pkt);
			break;
		}
	}

	@Override
	public void validate() {

		if (!world.isRemote) {
			setSelectedSubnet(_currentSubnet);
		}
	}

	public void setSelectedSubnet(int newSubnet) {

		_currentSubnet = newSubnet;
		if (!world.isRemote) {
			sendValue(_lastValues[_currentSubnet]);
		}
	}

	public void valuesChanged(int[] values) {

		for (int i = 0; i < 16; i++) {
			if (values[i] != _lastValues[i]) {
				_lastValues[i] = values[i];
				if (i == _currentSubnet) {
					sendValue(values[i]);
				}
			}
		}
	}

	private void sendValue(int value) {

		NBTTagCompound data = new NBTTagCompound();
		data.setInteger("value", value);
		Packets.sendToAllPlayersInRange(world, pos, 50,
				new SPacketUpdateTileEntity(pos, 1, data));
	}

	@Override
	public boolean canRotate() {

		return true;
	}

	@Override
	public String getDataType() {

		return "tile.mfr.rednet.panel.historian.name";
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {

		super.readFromNBT(nbttagcompound);
		_currentSubnet = nbttagcompound.getInteger("subnet");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbttagcompound) {

		nbttagcompound = super.writeToNBT(nbttagcompound);
		nbttagcompound.setInteger("subnet", _currentSubnet);

		return nbttagcompound;
	}

/*	TODO readd when BC team figure out what they want to do
	@Override
	public ConnectOverride overridePipeConnection(PipeType type, EnumFacing with) {
		return ConnectOverride.DISCONNECT;
	}
*/

	public static class Client extends TileEntityFactory implements ITickable {

		@SideOnly(Side.CLIENT)
		private ArrayQueue<Integer> _valuesClient;
		@SideOnly(Side.CLIENT)
		private int _currentValueClient;

		public Client() {

			super(null);
		}

		@Override
		protected void handlePacketData(NBTTagCompound tag) {

			super.handlePacketData(tag);
			_currentValueClient = tag.getInteger("current");
		}

		@Override
		public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {

			NBTTagCompound data = pkt.getNbtCompound();
			switch (pkt.getTileEntityType()) {
			default:
				super.onDataPacket(net, pkt);
				break;
			case 1:
				_currentValueClient = data.getInteger("value");
				break;
			}
		}

		@Override
		public void validate() {

			super.validate();
			if (world.isRemote) {
				_valuesClient = new ArrayQueue<>(100);
				_currentValueClient = 0;
			}
		}

		@Override
		public void update() {

			if (world.isRemote) {
				_valuesClient.pop();
				_valuesClient.push(_currentValueClient);
			}
		}

		@SideOnly(Side.CLIENT)
		public Integer[] getValues() {

			Integer[] values = new Integer[_valuesClient.size()];
			return _valuesClient.toArray(values);
		}

		@Override
		@SideOnly(Side.CLIENT)
		public double getMaxRenderDistanceSquared() {

			return 4096.0D;
		}
	}

}
