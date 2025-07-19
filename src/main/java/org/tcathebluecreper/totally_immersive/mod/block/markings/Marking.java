package org.tcathebluecreper.totally_immersive.mod.block.markings;

import net.minecraft.core.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class Marking implements Comparable<Marking> {
    public static final List<Marking> ALL_MARKINGS = new ArrayList<>();

    public Marking() {
        ALL_MARKINGS.add(this);
    }

    public abstract String name();

    @Override
    public int compareTo(@NotNull Marking o) {
        return 0;
    }
    public VoxelShape getShape() {
        return Shapes.box(0,0,0,1,1/16f,1);
    }
    public boolean allowOnSide(Direction side) {
        return true;
    }
    @Nullable
    public ResourceLocation model() {
        return ResourceLocation.fromNamespaceAndPath("markings","block/marking/" + name());
    }
    @Nullable
    public ResourceLocation sideModel() {
        return model();
    }
    public ResourceLocation texture() {
        return null;
    }
    public boolean canBeSupported(Level level, BlockState state, BlockPos pos, Direction direction) {
        return state.isFaceSturdy(level, pos, direction, SupportType.FULL);
    }
}
