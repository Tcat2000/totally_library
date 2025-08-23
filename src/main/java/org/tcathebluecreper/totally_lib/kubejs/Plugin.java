package org.tcathebluecreper.totally_lib.kubejs;

import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.generator.AssetJsonGenerator;
import dev.latvian.mods.kubejs.script.BindingsEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import org.tcathebluecreper.totally_lib.TotallyLibrary;
import org.tcathebluecreper.totally_lib.multiblock.ModMultiblocks;
import org.tcathebluecreper.totally_lib.multiblock.TLMultiblockBuilder;
import org.tcathebluecreper.totally_lib.multiblock.trait.EnergyTrait;
import org.tcathebluecreper.totally_lib.multiblock.trait.ItemTrait;

import java.io.IOException;
import java.util.Optional;


public class Plugin extends KubeJSPlugin {
    public static EventGroup multiblockEventsGroup = EventGroup.of("IEMultiblockEvents");
    public static EventHandler multiblockRegisterEventJS = multiblockEventsGroup.startup("registerMultiblocks", () -> TLMultiblockRegistrationEventJS.class);

    @Override
    public void registerEvents() {
        super.registerEvents();
        multiblockEventsGroup.register();
    }

    @Override
    public void initStartup() { // 1
        Plugin.multiblockRegisterEventJS.post(new TLMultiblockRegistrationEventJS(TotallyLibrary.regManager, ModMultiblocks.allMultiblocks::add));
        TLMultiblockBuilder.init();
    }

    @Override
    public void registerBindings(BindingsEvent event) {
        event.add("TLEnergyTrait", EnergyTrait.class);
        event.add("TLItemTrait", ItemTrait.class);
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
//                out.putBoolean("dynamic", false);
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

                JsonObject json = NbtOps.INSTANCE.convertTo(JsonOps.INSTANCE, out).getAsJsonObject();

                json.addProperty("dynamic", false);

                generator.json(ResourceLocation.fromNamespaceAndPath("test","models/block/multiblock/multiblock/multiblock_split"), json);

            } catch(IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
