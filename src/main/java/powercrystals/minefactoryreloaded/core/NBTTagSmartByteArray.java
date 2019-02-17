package powercrystals.minefactoryreloaded.core;

import cofh.core.util.helpers.ItemHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import javax.annotation.Nonnull;
import java.io.ByteArrayOutputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * This class is only for writing smarter ByteArrays; when it's read back it will be the standard NBTTagByteArray
 */
public final class NBTTagSmartByteArray extends NBTTagByteArray {

	private class _ByteArrayOutputStream extends ByteArrayOutputStream {

		_ByteArrayOutputStream(int initialSize) {

			super(initialSize);
		}

		byte[] getByteArray() {

			return this.buf;
		}
	}

	private _ByteArrayOutputStream arrayOut;
	private DataOutputStream dataOut;

	public NBTTagSmartByteArray() {

		this(64);
	}

	public NBTTagSmartByteArray(int initialSize) {

		super(new byte[0]);

		arrayOut = new _ByteArrayOutputStream(initialSize);
		dataOut = new DataOutputStream(arrayOut);
	}

	public NBTTagSmartByteArray addString(String theString) {

		try {
			dataOut.writeUTF(theString);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return this;
	}

	public NBTTagSmartByteArray addUUID(UUID theUUID) {

		try {
			dataOut.writeLong(theUUID.getMostSignificantBits());
			dataOut.writeLong(theUUID.getLeastSignificantBits());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return this;
	}

	public NBTTagSmartByteArray addLong(long theLong) {

		try {
			dataOut.writeLong(theLong);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return this;
	}

	public NBTTagSmartByteArray addInt(int theInteger) {

		try {
			dataOut.writeInt(theInteger);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return this;
	}

	public NBTTagSmartByteArray addVarInt(int theInteger) {

		try {
			int v = 0x00;
			if (theInteger < 0) {
				v |= 0x40;
				theInteger = ~theInteger;
			}
			if ((theInteger & ~0x3F) != 0) {
				v |= 0x80;
			}
			dataOut.writeByte(v | (theInteger & 0x3F));
			theInteger >>>= 6;
			while (theInteger != 0) {
				dataOut.writeByte((theInteger & 0x7F) | ((theInteger & ~0x7F) != 0 ? 0x80 : 0));
				theInteger >>>= 7;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return this;
	}

	public NBTTagSmartByteArray addBool(boolean theBoolean) {

		try {
			dataOut.writeBoolean(theBoolean);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return this;
	}

	public NBTTagSmartByteArray addByte(byte theByte) {

		try {
			dataOut.writeByte(theByte);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return this;
	}

	public NBTTagSmartByteArray addByte(int theByte) {

		return addByte((byte) theByte);
	}

	public NBTTagSmartByteArray addShort(short theShort) {

		try {
			dataOut.writeShort(theShort);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return this;
	}

	public NBTTagSmartByteArray addShort(int theShort) {

		return addShort((short) theShort);
	}

	public NBTTagSmartByteArray addByteArray(byte theByteArray[]) {

		try {
			dataOut.write(theByteArray);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return this;
	}

	public NBTTagSmartByteArray addFloat(float theFloat) {

		try {
			dataOut.writeFloat(theFloat);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return this;
	}

	public NBTTagSmartByteArray addItemStack(ItemStack theStack) {

		try {
			if (theStack.isEmpty()) {
				addShort(-1);
			} else {
				addShort(Item.getIdFromItem(theStack.getItem()));
				addByte(theStack.getCount());
				addShort(ItemHelper.getItemDamage(theStack));
				addNBT(theStack.getTagCompound());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return this;
	}

	public void addNBT(NBTTagCompound nbt) throws IOException {

		if (nbt == null) {
			addShort(-1);
		} else {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			CompressedStreamTools.writeCompressed(nbt, baos);
			byte[] bytes = baos.toByteArray();
			addShort((short) bytes.length);
			addByteArray(bytes);
		}
	}

	public NBTTagSmartByteArray addCoords(TileEntity theTile) {

		addInt(theTile.getPos().getX());
		addInt(theTile.getPos().getY());
		return addInt(theTile.getPos().getZ());
	}

	public NBTTagSmartByteArray addCoords(int x, int y, int z) {

		addInt(x);
		addInt(y);
		return addInt(z);
	}

	public void write(DataOutput output) throws IOException {

		output.writeInt(arrayOut.size());
		output.write(arrayOut.getByteArray(), 0, arrayOut.size());
	}

	@Override
	public String toString() {

		return "[" + arrayOut.size() + " bytes]";
	}

	@Override
	public NBTBase copy() {

		return new NBTTagByteArray(getByteArray());
	}

	@Override
	public boolean equals(Object o) {

		if (super.equals(o) && o.getClass() == NBTTagSmartByteArray.class) {
			NBTTagSmartByteArray other = (NBTTagSmartByteArray) o;
			return other.dataOut.equals(dataOut);
		}
		return false;
	}

	@Override
	public int hashCode() {

		return super.hashCode() ^ dataOut.hashCode();
	}

	@Nonnull
	@Override
	public byte[] getByteArray() {

		return arrayOut.toByteArray();
	}

}
