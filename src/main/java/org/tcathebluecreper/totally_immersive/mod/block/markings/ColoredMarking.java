package org.tcathebluecreper.totally_immersive.mod.block.markings;

import net.minecraft.resources.ResourceLocation;

import java.util.*;

public class ColoredMarking {
    public static final List<String> MC_COLORS = List.of("red", "green", "blue", "yellow", "orange", "purple"); // what's the 16th?  "magenta", "lime" , "light_blue", "black", "white", "gray", "light_gray", "pink",
    public final String name;
    public final Map markings;
    public ColoredMarking(String name, String textureNamespace, String texturePath) {
        this.name = name;

        HashMap<String, Marking> map = new HashMap<>();
        MC_COLORS.forEach(color -> {
            System.out.println(color);

            new Marking() {
                private final String markingName = name.replace("[color]", color);
                @Override
                public String name() {
                    return markingName;
                }

                @Override
                public ResourceLocation texture() {
                    return ResourceLocation.fromNamespaceAndPath(textureNamespace, texturePath.replace("[color]", color));
                }
            };
//            map.put(color, new Marking() {
//                private final String markingName = name.replace("[color]", color);
//                @Override
//                public String name() {
//                    return markingName;
//                }
//
//                @Override
//                public ResourceLocation texture() {
//                    return ResourceLocation.fromNamespaceAndPath(textureNamespace, texturePath.replace("[color]", color));
//                }
//            });
        });
        this.markings = map;
    }
}
