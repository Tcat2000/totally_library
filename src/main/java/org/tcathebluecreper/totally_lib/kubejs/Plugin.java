package org.tcathebluecreper.totally_lib.kubejs;

import blusunrize.immersiveengineering.api.IEProperties;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.serialization.JsonOps;
import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.generator.AssetJsonGenerator;
import dev.latvian.mods.kubejs.script.BindingsEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import org.tcathebluecreper.totally_lib.TotallyLibrary;
import org.tcathebluecreper.totally_lib.lib.AnimationUtils;
import org.tcathebluecreper.totally_lib.multiblock.ModMultiblocks;
import org.tcathebluecreper.totally_lib.multiblock.MultiblockBuilder;
import org.tcathebluecreper.totally_lib.multiblock.trait.EnergyTrait;
import org.tcathebluecreper.totally_lib.multiblock.trait.FluidTrait;
import org.tcathebluecreper.totally_lib.multiblock.trait.ItemTrait;
import org.tcathebluecreper.totally_lib.multiblock.trait.TraitIOSides;
import org.tcathebluecreper.totally_lib.recipe.provider.*;


public class Plugin extends KubeJSPlugin {
    public static EventGroup multiblockEventsGroup = EventGroup.of("IEMultiblockEvents");
    public static EventHandler multiblockRegisterEventJS = multiblockEventsGroup.startup("registerMultiblocks", () -> TLMultiblockRegistrationEventJS.class);

    @Override
    public void registerEvents() {
        super.registerEvents();
        multiblockEventsGroup.register();
    }

    @Override
    public void initStartup() {
        try {
            Plugin.multiblockRegisterEventJS.post(new TLMultiblockRegistrationEventJS(TotallyLibrary.regManager, ModMultiblocks.allMultiblocks::add, false));
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
        MultiblockBuilder.init();
    }

    @Override
    public void registerBindings(BindingsEvent event) {
        event.add("TLEnergyTrait", EnergyTrait.class);
        event.add("TLItemTrait", ItemTrait.class);
        event.add("TLFluidTrait", FluidTrait.class);
        event.add("TraitIOSides", TraitIOSides.class);

        event.add("BooleanProvider", BooleanProvider.class);
        event.add("FloatProvider", FloatProvider.class);
        event.add("FluidProvider", FluidProvider.class);
        event.add("FluidStackProvider", FluidStackProvider.class);
        event.add("IntProvider", IntProvider.class);
        event.add("ItemProvider", ItemProvider.class);
        event.add("ItemStackProvider", ItemStackProvider.class);
        event.add("IngredientProvider", IngredientProvider.class);

        event.add("Shapes", ShapesWrapper.class);

        event.add("LevelRenderer", LevelRenderer.class);
        event.add("RenderType", RenderType.class);
        event.add("AnimationUtils", AnimationUtils.class);
        event.add("IClientFluidTypeExtensions", IClientFluidTypeExtensions.class);
        event.add("Minecraft", Minecraft.class);
        event.add("RenderUtils", RenderUtils.class);
        event.add("Sheets", Sheets.class);
        event.add("InventoryMenu", InventoryMenu.class);
        event.add("IEProperties", IEProperties.class);
        event.add("RenderUtils", RenderUtils.class);
    }

    @Override
    public void generateAssetJsons(AssetJsonGenerator generator) {
        ModMultiblocks.allMultiblocks.forEach(multiblock -> {
            if(multiblock.getAssetGenData() == null) return;

            String path = multiblock.getId().getPath();
            String name = path.split("/")[path.split("/").length - 1];
            ResourceLocation pathToSplitModel = ResourceLocation.fromNamespaceAndPath(multiblock.getId().getNamespace(), "models/block/multiblock/" + path + "/" + name + "_split");
            ResourceLocation readablePathToSplitModel = ResourceLocation.fromNamespaceAndPath(multiblock.getId().getNamespace(), "block/multiblock/" + path + "/" + name + "_split");
            ResourceLocation pathToBlockstate = ResourceLocation.fromNamespaceAndPath(multiblock.getId().getNamespace(), "blockstates/" + name);
            ResourceLocation pathToItemModel = ResourceLocation.fromNamespaceAndPath(multiblock.getId().getNamespace(), "models/item/" + name);

            CompoundTag out = new CompoundTag();
            ListTag blockPoses = new ListTag();

            out.putString("parent","minecraft:block/block");
            CompoundTag textures = new CompoundTag();
            textures.putString("particle","minecraft:block/diamond_block");
            out.put("textures", textures);

            out.putString("loader","immersiveengineering:basic_split");
//                out.putBoolean("dynamic", false);

            out.put("inner_model", JsonOps.INSTANCE.convertTo(NbtOps.INSTANCE, multiblock.getAssetGenData().getInnerModel()));

            for(int i = 0; i < multiblock.getAssetGenData().getBlocks().length; i++) {
                int[] pos = multiblock.getAssetGenData().getBlocks()[i];
                ListTag tag = new ListTag();
                tag.add(IntTag.valueOf(pos[0] - multiblock.getMultiblock().masterFromOrigin.getX()));
                tag.add(IntTag.valueOf(pos[1] - multiblock.getMultiblock().masterFromOrigin.getY()));
                tag.add(IntTag.valueOf(pos[2] - multiblock.getMultiblock().masterFromOrigin.getZ()));
                blockPoses.add(tag);
            }
            out.put("split_parts", blockPoses);

            JsonObject json = NbtOps.INSTANCE.convertTo(JsonOps.INSTANCE, out).getAsJsonObject();

            json.addProperty("dynamic", false);

            System.out.println("generated json asset: " + json);
            generator.json(pathToSplitModel, json);

            JsonObject blockstate = new JsonObject();
            JsonObject variants = new JsonObject();

            JsonObject model = new JsonObject();
            model.addProperty("model", readablePathToSplitModel.toString());
            model.addProperty("uvlock", true);

            model.addProperty("y", 0);
            variants.add("facing=north,mirrored=false", model.deepCopy());
            model.addProperty("y", 90);
            variants.add("facing=east,mirrored=false", model.deepCopy());
            model.addProperty("y", 180);
            variants.add("facing=south,mirrored=false", model.deepCopy());
            model.addProperty("y", 270);
            variants.add("facing=west,mirrored=false", model.deepCopy());
            model.addProperty("y", 0);
            variants.add("facing=north,mirrored=true", model.deepCopy());
            model.addProperty("y", 90);
            variants.add("facing=east,mirrored=true", model.deepCopy());
            model.addProperty("y", 180);
            variants.add("facing=south,mirrored=true", model.deepCopy());
            model.addProperty("y", 270);
            variants.add("facing=west,mirrored=true", model.deepCopy());

            blockstate.add("variants", variants);
            generator.json(pathToBlockstate, blockstate);

            float size = Math.max(multiblock.getMultiblock().size.getX(), Math.max(multiblock.getMultiblock().size.getY(), multiblock.getMultiblock().size.getZ()));

            JsonObject itemModel = multiblock.getAssetGenData().getInnerModel().deepCopy();
            JsonObject displayOptions = new JsonObject();


            JsonObject display = new JsonObject();
            JsonArray array = new JsonArray(3);
            array.add(new JsonPrimitive(30));
            array.add(new JsonPrimitive(225));
            array.add(new JsonPrimitive(0));
            display.add("rotation", array);
            array = new JsonArray(3);
            array.add(new JsonPrimitive(0));
            array.add(new JsonPrimitive(0));
            array.add(new JsonPrimitive(0));
            display.add("translation", array);
            array = new JsonArray(3);
            array.add(new JsonPrimitive(0.625 / size));
            array.add(new JsonPrimitive(0.625 / size));
            array.add(new JsonPrimitive(0.625 / size));
            display.add("scale", array);
            displayOptions.add("gui", display);

            display = new JsonObject();
            array = new JsonArray(3);
            array.add(new JsonPrimitive(0));
            array.add(new JsonPrimitive(0));
            array.add(new JsonPrimitive(0));
            display.add("rotation", array);
            array = new JsonArray(3);
            array.add(new JsonPrimitive(0));
            array.add(new JsonPrimitive(3));
            array.add(new JsonPrimitive(0));
            display.add("translation", array);
            array = new JsonArray(3);
            array.add(new JsonPrimitive(0.25 / size));
            array.add(new JsonPrimitive(0.25 / size));
            array.add(new JsonPrimitive(0.25 / size));
            display.add("scale", array);
            displayOptions.add("ground", display);

            display = new JsonObject();
            array = new JsonArray(3);
            array.add(new JsonPrimitive(0));
            array.add(new JsonPrimitive(0));
            array.add(new JsonPrimitive(0));
            display.add("rotation", array);
            array = new JsonArray(3);
            array.add(new JsonPrimitive(0));
            array.add(new JsonPrimitive(0));
            array.add(new JsonPrimitive(0));
            display.add("translation", array);
            array = new JsonArray(3);
            array.add(new JsonPrimitive(0.5 / size));
            array.add(new JsonPrimitive(0.5 / size));
            array.add(new JsonPrimitive(0.5 / size));
            display.add("scale", array);
            displayOptions.add("fixed", display);

            display = new JsonObject();
            array = new JsonArray(3);
            array.add(new JsonPrimitive(75));
            array.add(new JsonPrimitive(45));
            array.add(new JsonPrimitive(0));
            display.add("rotation", array);
            array = new JsonArray(3);
            array.add(new JsonPrimitive(0));
            array.add(new JsonPrimitive(2.5));
            array.add(new JsonPrimitive(0));
            display.add("translation", array);
            array = new JsonArray(3);
            array.add(new JsonPrimitive(0.375 / size));
            array.add(new JsonPrimitive(0.375 / size));
            array.add(new JsonPrimitive(0.375 / size));
            display.add("scale", array);
            displayOptions.add("thirdperson_righthand", display);

            display = new JsonObject();
            array = new JsonArray(3);
            array.add(new JsonPrimitive(0));
            array.add(new JsonPrimitive(45));
            array.add(new JsonPrimitive(0));
            display.add("rotation", array);
            array = new JsonArray(3);
            array.add(new JsonPrimitive(0));
            array.add(new JsonPrimitive(0));
            array.add(new JsonPrimitive(0));
            display.add("translation", array);
            array = new JsonArray(3);
            array.add(new JsonPrimitive(0.4 / size));
            array.add(new JsonPrimitive(0.4 / size));
            array.add(new JsonPrimitive(0.4 / size));
            display.add("scale", array);
            displayOptions.add("firstperson_righthand", display);

            display = new JsonObject();
            array = new JsonArray(3);
            array.add(new JsonPrimitive(0));
            array.add(new JsonPrimitive(225));
            array.add(new JsonPrimitive(0));
            display.add("rotation", array);
            array = new JsonArray(3);
            array.add(new JsonPrimitive(0));
            array.add(new JsonPrimitive(0));
            array.add(new JsonPrimitive(0));
            display.add("translation", array);
            array = new JsonArray(3);
            array.add(new JsonPrimitive(0.4 / size));
            array.add(new JsonPrimitive(0.4 / size));
            array.add(new JsonPrimitive(0.4 / size));
            display.add("scale", array);
            displayOptions.add("firstperson_lefthand", display);


            itemModel.add("display", displayOptions);

            generator.json(pathToItemModel, itemModel);
        });
    }
}
