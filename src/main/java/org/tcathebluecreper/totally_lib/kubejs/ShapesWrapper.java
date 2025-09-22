package org.tcathebluecreper.totally_lib.kubejs;

import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ShapesWrapper {
    public static VoxelShape size(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        return Shapes.box(minX / 16, minY / 16, minZ / 16, maxX / 16, maxY / 16, maxZ / 16);
    }
    public static VoxelShape offset(double sizeX, double sizeY, double sizeZ, double posX, double posY, double posZ) {
        return Shapes.box(posX / 16, posY / 16, posZ / 16, (posX + sizeX) / 16, (posY + sizeY) / 16, (posZ + sizeZ) / 16);
    }
}
