package org.tcathebluecreper.totally_lib;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import org.tcathebluecreper.totally_lib.lib.CacheMap;

import java.util.HashMap;

public class Registration {
    private CacheMap<String, MultiRegister> registries = new CacheMap<>();

    public MultiRegister getRegistry(String modId) {
        return registries.getAndAdd(modId, () -> new MultiRegister(modId));
    }

    public <R> void register(ResourceKey<Registry<R>> key, String modId, String id, R registered) {
        registries.getAndAdd(modId, () -> new MultiRegister(modId)).getRegistry(key).register(id, () -> registered);
    }

    public void registerBlock(String modId, String id, Block block) {
        registries.getAndAdd(modId, () -> new MultiRegister(modId)).getRegistry(Registries.BLOCK).register(id, () -> block);
    }

    public void registerItem(String modId, String id, Item item) {
        registries.getAndAdd(modId, () -> new MultiRegister(modId)).getRegistry(Registries.ITEM).register(id, () -> item);
    }


    public static class MultiRegister {
        public final String modId;
        HashMap<ResourceKey<?>, DeferredRegister<?>> reg;

        public MultiRegister(String modId) {
            this.modId = modId;
        }

        public <R> DeferredRegister<R> getRegistry(ResourceKey<Registry<R>> key) {
            if(!reg.containsKey(key)) {
                reg.put(key, DeferredRegister.create(key, modId));
            }
            return (DeferredRegister<R>) reg.get(key);
        }
    }
}
