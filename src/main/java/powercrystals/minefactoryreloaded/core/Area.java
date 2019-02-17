package powercrystals.minefactoryreloaded.core;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class Area {

    public int xMin;
    public int xMax;
    public int yMin;
    public int yMax;
    public int zMin;
    public int zMax;

    private BlockPos origin;

    public Area(int xMin, int xMax, int yMin, int yMax, int zMin, int zMax) {

        this.xMin = xMin;
        this.xMax = xMax;
        this.yMin = yMin;
        this.yMax = yMax;
        this.zMin = zMin;
        this.zMax = zMax;
    }

    public Area(BlockPos center, int radius, int yNegOffset, int yPosOffset) {

        xMin = center.getX() - radius;
        xMax = center.getX() + radius;
        yMin = center.getY() - yNegOffset;
        yMax = center.getY() + yPosOffset;
        zMin = center.getZ() - radius;
        zMax = center.getZ() + radius;

        origin = center;
    }

    public BlockPos getMin() {

        return new BlockPos(xMin, yMin, zMin);
    }

    public BlockPos getMax() {

        return new BlockPos(xMax, yMax, zMax);
    }

    public boolean contains(BlockPos pos) {

        return pos.getX() >= xMin & pos.getX() <= xMax & pos.getY() >= yMin & pos.getY() <= yMax & pos.getZ() >= zMin & pos.getZ() <= zMax;
    }

    public List<BlockPos> getPositionsTopFirst() {

        ArrayList<BlockPos> positions = new ArrayList<>();
        for (int y = yMax; y >= yMin; y--) {
            for (int x = xMin; x <= xMax; x++) {
                for (int z = zMin; z <= zMax; z++) {
                    positions.add(new BlockPos(x, y, z));
                }
            }
        }
        return positions;
    }

    public List<BlockPos> getPositionsBottomFirst() {

        ArrayList<BlockPos> positions = new ArrayList<>();
        for (int y = yMin; y <= yMax; y++) {
            for (int x = xMin; x <= xMax; x++) {
                for (int z = zMin; z <= zMax; z++) {
                    positions.add(new BlockPos(x, y, z));
                }
            }
        }
        return positions;
    }

    public BlockPos getOrigin() {

        return origin;
    }

    public AxisAlignedBB toAxisAlignedBB() {

        return new AxisAlignedBB(xMin, yMin, zMin, xMax + 1, yMax + 1, zMax + 1);
    }

}
