package org.tcathebluecreper.totally_lib.event;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;
import org.tcathebluecreper.totally_lib.RegistrationManager;
import org.tcathebluecreper.totally_lib.multiblock.MultiblockBuilder;
import org.tcathebluecreper.totally_lib.multiblock.TLMultiblockInfo;

import java.util.function.Consumer;

public class TLMultiblockRegistrationEvent extends Event implements IModBusEvent {
    private final RegistrationManager manager;
    private final Consumer<TLMultiblockInfo> consumer;
    private final boolean reload;

    public TLMultiblockRegistrationEvent(RegistrationManager manager, Consumer<TLMultiblockInfo> consumer, boolean reload) {
        this.manager = manager;
        this.consumer = consumer;
        this.reload = reload;
    }

    public MultiblockBuilder multiblock(ResourceLocation id) {
        return new MultiblockBuilder(id, manager, consumer, reload);
    }
}
