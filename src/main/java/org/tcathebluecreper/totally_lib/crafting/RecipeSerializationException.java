package org.tcathebluecreper.totally_lib.crafting;

import net.minecraft.resources.ResourceLocation;

public class RecipeSerializationException extends RuntimeException {
    public RecipeSerializationException(ResourceLocation recipe, String message) {
        super("Error loading recipe: " + recipe.toString() + ": " + message);
    }
}
