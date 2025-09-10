package org.tcathebluecreper.totally_lib.kubejs;

import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ShapesWrapper {
    public VoxelShape size(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        return Shapes.box(minX, minY, minZ, maxX, maxY, maxZ);
    }
    public VoxelShape offset(double sizeX, double sizeY, double sizeZ, double posX, double posY, double posZ) {
        return Shapes.box(posX, posY, posZ, posX + sizeX, posY + sizeY, posZ + sizeZ);
    }
}
