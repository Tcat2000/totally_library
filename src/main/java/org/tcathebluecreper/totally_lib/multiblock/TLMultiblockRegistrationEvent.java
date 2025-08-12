package org.tcathebluecreper.totally_lib.multiblock;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.Event;
import org.tcathebluecreper.totally_lib.RegistrationManager;

import java.util.function.Consumer;

public class TLMultiblockRegistrationEvent extends Event {
    private final RegistrationManager manager;
    private final Consumer<RegisterableMultiblock> consumer;

    public TLMultiblockRegistrationEvent(RegistrationManager manager, Consumer<RegisterableMultiblock> consumer) {
        this.manager = manager;
        this.consumer = consumer;
    }

    public TLMultiblockBuilder multiblock(ResourceLocation id) {return new TLMultiblockBuilder(id, manager, consumer);}
}
