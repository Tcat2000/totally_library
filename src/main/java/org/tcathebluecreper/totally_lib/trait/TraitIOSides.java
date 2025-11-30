package org.tcathebluecreper.totally_lib.trait;

import blusunrize.immersiveengineering.api.multiblocks.blocks.util.RelativeBlockFace;

import java.util.function.Predicate;

public class TraitIOSides {
    public static final Predicate<RelativeBlockFace> TOP = side -> side == RelativeBlockFace.UP;
    public static final Predicate<RelativeBlockFace> BOTTOM = side -> side == RelativeBlockFace.DOWN;
    public static final Predicate<RelativeBlockFace> LEFT = side -> side == RelativeBlockFace.LEFT;
    public static final Predicate<RelativeBlockFace> RIGHT = side -> side == RelativeBlockFace.RIGHT;
    public static final Predicate<RelativeBlockFace> FRONT = side -> side == RelativeBlockFace.FRONT;
    public static final Predicate<RelativeBlockFace> BACK = side -> side == RelativeBlockFace.BACK;

    public static final Predicate<RelativeBlockFace> SIDES = side -> side == RelativeBlockFace.FRONT || side == RelativeBlockFace.BACK || side == RelativeBlockFace.LEFT || side == RelativeBlockFace.RIGHT;
    public static final Predicate<RelativeBlockFace> TOP_BOTTOM = side -> side == RelativeBlockFace.UP || side == RelativeBlockFace.DOWN;
    public static final Predicate<RelativeBlockFace> LEFT_RIGHT = side -> side == RelativeBlockFace.LEFT || side == RelativeBlockFace.RIGHT;
    public static final Predicate<RelativeBlockFace> FRONT_BACK = side -> side == RelativeBlockFace.LEFT || side == RelativeBlockFace.RIGHT;
}
