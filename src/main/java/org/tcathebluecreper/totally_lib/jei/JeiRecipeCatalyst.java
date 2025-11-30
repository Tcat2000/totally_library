package org.tcathebluecreper.totally_lib.jei;

import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.function.Supplier;

public abstract class JeiRecipeCatalyst {
    private static final Logger log = LoggerFactory.getLogger(JeiRecipeCatalyst.class);
    public final ResourceLocation recipeType;

    protected JeiRecipeCatalyst(ResourceLocation recipeType) {
        this.recipeType = recipeType;
    }
    
    public abstract void register(IRecipeCatalystRegistration registration);

    protected void logMissingRecipeType() {
        log.error("No JEI recipe category with id {}, skipping add catalyst", recipeType);
    }
    
    public static class ItemCatalyst extends JeiRecipeCatalyst {
        public final Supplier<ItemLike> item;
        
        public ItemCatalyst(ResourceLocation recipeType, Supplier<ItemLike> item) {
            super(recipeType);
            this.item = item;
        }

        @Override
        public void register(IRecipeCatalystRegistration registration) {
            Optional<RecipeType<?>> type = registration.getJeiHelpers().getAllRecipeTypes().filter(rt -> rt.getUid() == recipeType).findFirst();
            if(type.isEmpty()) {
                logMissingRecipeType();
                return;
            }
            registration.addRecipeCatalysts(type.get(), item.get());
        }
    }

    public static class ItemStackCatalyst extends JeiRecipeCatalyst {
        public final Supplier<ItemStack> itemStack;

        public ItemStackCatalyst(ResourceLocation recipeType, Supplier<ItemStack> itemStack) {
            super(recipeType);
            this.itemStack = itemStack;
        }

        @Override
        public void register(IRecipeCatalystRegistration registration) {
            Optional<RecipeType<?>> type = registration.getJeiHelpers().getAllRecipeTypes().filter(rt -> rt.getUid() == recipeType).findFirst();
            if(type.isEmpty()) {
                logMissingRecipeType();
                return;
            }
            registration.addRecipeCatalysts(type.get(), itemStack.get());
        }
    }
}
