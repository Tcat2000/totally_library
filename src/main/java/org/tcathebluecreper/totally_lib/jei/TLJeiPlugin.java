package org.tcathebluecreper.totally_lib.jei;

import blusunrize.immersiveengineering.common.util.compat.jei.JEIRecipeTypes;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.tcathebluecreper.totally_lib.multiblock.TLModMultiblocks;

@JeiPlugin
public class TLJeiPlugin implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath("totally_lib","plugin");
    }

    @Override
    public void registerRecipes(@NotNull IRecipeRegistration registration) {
        ClientLevel world = Minecraft.getInstance().level;
        assert world != null;
        TLModMultiblocks.allMultiblocks.forEach(tlMultiblockInfo -> {
            if(tlMultiblockInfo.getJeiInfo() != null) registration.addRecipes(tlMultiblockInfo.getJeiInfo().UID, tlMultiblockInfo.getRecipeType().getSerializer().getRecipes(world));
        });
    }

    @Override
    public void registerRecipeCatalysts(@NotNull IRecipeCatalystRegistration registration) {
        ClientLevel world = Minecraft.getInstance().level;
        assert world != null;
        TLModMultiblocks.allMultiblocks.forEach(tlMultiblockInfo -> {
            tlMultiblockInfo.getRecipeCatalysts().forEach(catalyst -> catalyst.register(registration));
            if(tlMultiblockInfo.getJeiInfo() != null) {
                tlMultiblockInfo.getJeiInfo().catalystItemStacks.forEach(stack -> registration.addRecipeCatalyst(stack.get(), tlMultiblockInfo.getJeiInfo().UID));
                tlMultiblockInfo.getJeiInfo().catalystItems.forEach(item -> registration.addRecipeCatalyst(item.get(), tlMultiblockInfo.getJeiInfo().UID));
            }
        });
    }

    @Override
    public void registerCategories(@NotNull IRecipeCategoryRegistration registration) {
        TLModMultiblocks.allMultiblocks.forEach(tlMultiblockInfo -> {
            if(tlMultiblockInfo.getJeiInfo() != null) registration.addRecipeCategories(tlMultiblockInfo.getJeiInfo().constructor.apply(registration.getJeiHelpers().getGuiHelper()));
        });
    }
}
