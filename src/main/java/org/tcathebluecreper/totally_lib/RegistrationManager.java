package org.tcathebluecreper.totally_lib;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import org.tcathebluecreper.totally_lib.lib.CacheMap;

import java.util.HashMap;

public class RegistrationManager {
    private final IEventBus eventBus;
    private CacheMap<String, MultiRegister> registries = new CacheMap<>();

    public RegistrationManager(IEventBus eventBus) {
        this.eventBus = eventBus;
    }

    public MultiRegister getRegistry(String modId) {
        return registries.getAndAdd(modId, () -> new MultiRegister(modId, eventBus));
    }

    public <R> void register(ResourceKey<Registry<R>> key, String modId, String id, R registered) {
        registries.getAndAdd(modId, () -> new MultiRegister(modId, eventBus)).getRegistry(key).register(id, () -> registered);
    }

    public void registerBlock(String modId, String id, Block block) {
        registries.getAndAdd(modId, () -> new MultiRegister(modId, eventBus)).getRegistry(Registries.BLOCK).register(id, () -> block);
    }

    public void registerItem(String modId, String id, Item item) {
        registries.getAndAdd(modId, () -> new MultiRegister(modId, eventBus)).getRegistry(Registries.ITEM).register(id, () -> item);
    }


    public static class MultiRegister {
        public final String modId;
        private final IEventBus eventBus;
        HashMap<ResourceKey<?>, DeferredRegister<?>> reg;

        public MultiRegister(String modId, IEventBus eventBus) {
            this.modId = modId;
            this.eventBus = eventBus;
        }

        public <R> DeferredRegister<R> getRegistry(ResourceKey<Registry<R>> key) {
            if(!reg.containsKey(key)) {
                reg.put(key, DeferredRegister.create(key, modId));
                reg.get(key).register(eventBus);
            }
            return (DeferredRegister<R>) reg.get(key);
        }

        public DeferredRegister<Block> blocks() {
            return getRegistry(Registries.BLOCK);
        }

        public DeferredRegister<Item> items() {
            return getRegistry(Registries.ITEM);
        }

        public DeferredRegister<BlockEntityType<?>> blockEntityType() {
            return getRegistry(Registries.BLOCK_ENTITY_TYPE);
        }
    }
}
