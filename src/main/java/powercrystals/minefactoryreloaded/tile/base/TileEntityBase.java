package powercrystals.minefactoryreloaded.tile.base;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import powercrystals.minefactoryreloaded.core.MFRUtil;

import javax.annotation.Nullable;
import java.util.List;

public abstract class TileEntityBase extends net.minecraft.tileentity.TileEntity {

	protected String _invName;
	protected boolean inWorld;

	public void setBlockName(String name) {

		if (name != null && name.length() == 0)
			name = null;
		this._invName = name;
	}

	public String getBlockName() {

		return _invName;
	}

	@Override
	public void invalidate() {

		super.invalidate();
		invalidateInternal();
	}

	private void invalidateInternal() {

		inWorld = false;
		markChunkDirty();
	}

	@Override
	public void onChunkUnload() {

		invalidateInternal();
	}

	@Override
	public void onLoad() {

		super.onLoad();

		inWorld = true;
	}

	protected final ITextComponent text(String str) {

		return new TextComponentString(str);
	}

	public void markChunkDirty() {

		world.markChunkDirty(this.pos, this);
	}

	public void notifyNeighborTileChange() {

		if (getBlockType() != null) {
			world.updateComparatorOutputLevel(this.pos, this.getBlockType());
		}
	}

	public void onNeighborTileChange(BlockPos pos) {

	}

	public void onNeighborBlockChange() {

	}

	public void onMatchedNeighborBlockChange() {

	}

	public void getTileInfo(List<ITextComponent> info, EnumFacing side, EntityPlayer player, boolean debug) {

	}

	@Nullable
	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {

		if (world != null && !isInvalid()) {
			return new SPacketUpdateTileEntity(pos, 0, writePacketData(new NBTTagCompound()));
		}
		return null;
	}

	@Override
	public NBTTagCompound getUpdateTag() {

		return writePacketData(super.getUpdateTag());
	}

	@Override
	public void handleUpdateTag(NBTTagCompound tag) {

		super.handleUpdateTag(tag);
		handlePacketData(tag);

		MFRUtil.notifyBlockUpdate(world, pos);
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {

		NBTTagCompound data = pkt.getNbtCompound();
		switch (pkt.getTileEntityType()) {
		case 0:
			handlePacketData(data);
			break;
		}

		MFRUtil.notifyBlockUpdate(world, pos);
	}

	protected NBTTagCompound writePacketData(NBTTagCompound tag) {

		return tag;
	}

	protected void handlePacketData(NBTTagCompound tag) {

	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {

		if (tag.hasKey("x") && tag.hasKey("y") && tag.hasKey("z")) {
			super.readFromNBT(tag);
		}

		if (tag.hasKey("display")) {
			NBTTagCompound display = tag.getCompoundTag("display");
			if (display.hasKey("Name")) {
				this.setBlockName(display.getString("Name"));
			}
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {

		tag = super.writeToNBT(tag);
		writeItemNBT(tag);

		return tag;
	}

	public void writeItemNBT(NBTTagCompound tag) {

		if (_invName != null) {
			NBTTagCompound display = new NBTTagCompound();
			display.setString("Name", _invName);
			tag.setTag("display", display);
		}
	}

	private static final long HASH_A = 0x1387D;
	private static final long HASH_C = 0x3A8F05C5;

	@Override
	public int hashCode() {

		final int xTransform = (int) ((HASH_A * (pos.getX() ^ 0x1AFF2BAD) + HASH_C) & 0xFFFFFFFF);
		final int zTransform = (int) ((HASH_A * (pos.getZ() ^ 0x25C8B353) + HASH_C) & 0xFFFFFFFF);
		final int yTransform = (int) ((HASH_A * (pos.getY() ^ 0x39531FCD) + HASH_C) & 0xFFFFFFFF);
		return xTransform ^ zTransform ^ yTransform;
	}

	@Override
	public boolean equals(Object obj) {

		if (obj instanceof TileEntityBase) {
			TileEntityBase te = (TileEntityBase) obj;
			return te.getPos().equals(this.getPos()) && world == te.world && te.isInvalid() == isInvalid();
		}
		return false;
	}

	@Override
	public String toString() {

		return getClass().getSimpleName() + "(x=" + pos.getX() + ",y=" + pos.getY() + ",z=" + pos.getZ() + ")@" +
				System.identityHashCode(this);
	}
}
