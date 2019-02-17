package powercrystals.minefactoryreloaded.tile.rednet;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class RedstoneNode {

	BlockPos pos;
	EnumFacing facing;

	public RedstoneNode(BlockPos pos, EnumFacing facing) {

		this.pos = pos;
		this.facing = facing;
	}

	public BlockPos getPos() {

		return pos;
	}

	public EnumFacing getFacing() {

		return facing;
	}

	@Override
	public boolean equals(Object o) {

		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		RedstoneNode that = (RedstoneNode) o;

		return (pos != null ? pos.equals(that.pos) : that.pos == null) && facing == that.facing;
	}

	@Override
	public int hashCode() {

		int result = pos != null ? pos.hashCode() : 0;
		result = 31 * result + (facing != null ? facing.hashCode() : 0);
		return result;
	}
}
