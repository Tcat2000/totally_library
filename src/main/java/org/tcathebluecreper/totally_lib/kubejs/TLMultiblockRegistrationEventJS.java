package org.tcathebluecreper.totally_lib.kubejs;

import dev.latvian.mods.kubejs.event.EventJS;
import net.minecraft.resources.ResourceLocation;
import org.tcathebluecreper.totally_lib.RegistrationManager;
import org.tcathebluecreper.totally_lib.multiblock.MultiblockBuilder;
import org.tcathebluecreper.totally_lib.multiblock.RegistrableMultiblock;

import java.util.function.Consumer;

public class TLMultiblockRegistrationEventJS extends EventJS {
    private final RegistrationManager manager;
    private final Consumer<RegistrableMultiblock> consumer;
    private final boolean reload;

    public TLMultiblockRegistrationEventJS(RegistrationManager manager, Consumer<RegistrableMultiblock> consumer, boolean reload) {
        this.manager = manager;
        this.consumer = consumer;
        this.reload = reload;
    }

    public MultiblockBuilder multiblock(ResourceLocation id) {
        return new MultiblockBuilder(id, manager, consumer, reload);
    }
}
