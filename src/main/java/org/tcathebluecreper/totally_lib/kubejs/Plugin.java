package org.tcathebluecreper.totally_lib.kubejs;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.generator.AssetJsonGenerator;
import dev.latvian.mods.kubejs.generator.DataJsonGenerator;
import dev.latvian.mods.rhino.mod.util.NBTSerializable;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.level.levelgen.structure.Structure;
import org.tcathebluecreper.totally_lib.TotallyLibrary;
import org.tcathebluecreper.totally_lib.multiblock.ModMultiblocks;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Optional;


public class Plugin extends KubeJSPlugin {
    public static EventGroup multiblockEventsGroup = EventGroup.of("IEMultiblockEvents");
    public static EventHandler multiblockRegisterEventJS = multiblockEventsGroup.startup("registerMultiblocks", () -> TLMultiblockRegistrationEventJS.class);
    public static DataJsonGenerator dataJsonGenerator;

    @Override
    public void registerEvents() {
        super.registerEvents();
        multiblockEventsGroup.register();
    }

    @Override
    public void generateAssetJsons(AssetJsonGenerator generator) {
        ModMultiblocks.allMultiblocks.forEach(multiblock -> {
            try {
                Optional<Resource> structure = Minecraft.getInstance().getResourceManager().getResource(ResourceLocation.fromNamespaceAndPath("test","structures/multiblock.nbt"));
//                Optional<Resource> model = Minecraft.getInstance().getResourceManager().getResource(ResourceLocation.fromNamespaceAndPath("totally_immersive","block/multiblock/chemical_bath/chemical_bath.json"));

                if(structure.isEmpty()) return;
                CompoundTag src = NbtIo.readCompressed(structure.get().open());
                CompoundTag out = new CompoundTag();
                ListTag blockPoses = new ListTag();

                out.putString("parent","minecraft:block/block");
                CompoundTag textures = new CompoundTag();
                textures.putString("particle","minecraft:block/diamond_block");
                out.put("textures", textures);

                out.putString("loader","immersiveengineering:basic_split");
                out.putBoolean("dynamic", false);
                CompoundTag innerModel = new CompoundTag();
                innerModel.putString("parent","block/multiblock/chemical_bath/chemical_bath");
                out.put("inner_model", innerModel);

                for(Tag tag : ((ListTag) src.get("blocks"))) {
                    ListTag pos = (ListTag) ((CompoundTag)tag).get("pos");
                    pos.set(0, IntTag.valueOf(pos.getInt(0) - multiblock.multiblock.masterFromOrigin.getX()));
                    pos.set(1, IntTag.valueOf(pos.getInt(1) - multiblock.multiblock.masterFromOrigin.getY()));
                    pos.set(2, IntTag.valueOf(pos.getInt(2) - multiblock.multiblock.masterFromOrigin.getZ()));
                    blockPoses.add(pos);
                }
                out.put("split_parts", blockPoses);

                generator.json(ResourceLocation.fromNamespaceAndPath("test","block/multiblock/multiblock/multiblock_split"), NbtOps.INSTANCE.convertTo(JsonOps.INSTANCE, out));

            } catch(IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
