package org.tcathebluecreper.totally_lib.multiblock;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;
import org.tcathebluecreper.totally_lib.RegistrationManager;

import java.util.function.Consumer;

public class TLMultiblockRegistrationEvent extends Event implements IModBusEvent {
    private final RegistrationManager manager;
    private final Consumer<RegistrableMultiblock> consumer;
    private final boolean reload;

    public TLMultiblockRegistrationEvent(RegistrationManager manager, Consumer<RegistrableMultiblock> consumer, boolean reload) {
        this.manager = manager;
        this.consumer = consumer;
        this.reload = reload;
    }

    public TLMultiblockBuilder multiblock(ResourceLocation id) {
        return new TLMultiblockBuilder(id, manager, consumer, reload);
    }
}
