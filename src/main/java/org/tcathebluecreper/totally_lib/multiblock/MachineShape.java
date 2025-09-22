package org.tcathebluecreper.totally_lib.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MachineShape {
    HashMap<BlockPos, VoxelShape> cache = new HashMap<>();
    final VoxelShape shape;

    public MachineShape(List<VoxelShape> list) {
        VoxelShape[] shapes = new VoxelShape[list.size()];
        for(int i = 0; i < list.size(); i++) {
            shapes[i] = list.get(i);
        }
        this.shape = Shapes.or(Shapes.empty(), shapes);
    }

    public VoxelShape get(BlockPos pos) {
        return Shapes.join(shape, Shapes.box(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1), BooleanOp.AND).move(-pos.getX(), -pos.getY(), -pos.getZ());
    }
    public VoxelShape get(BlockPos machinePos, BlockPos pos) {
        return get(pos.subtract(machinePos));
    }
    public boolean between(double mid, double min, double max) {
        return mid >= min && mid <= max;
    }

    public static class SolidMachineShape extends MachineShape {
        public SolidMachineShape() {
            super(new ArrayList<>());
        }

        @Override
        public VoxelShape get(BlockPos pos) {
            return Shapes.block();
        }
    }
}
