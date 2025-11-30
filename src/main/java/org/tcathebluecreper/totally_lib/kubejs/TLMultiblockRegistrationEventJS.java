package org.tcathebluecreper.totally_lib.kubejs;

import dev.latvian.mods.kubejs.event.EventJS;
import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.resources.ResourceLocation;
import org.tcathebluecreper.totally_lib.RegistrationManager;
import org.tcathebluecreper.totally_lib.multiblock.TLMultiblockBuilder;
import org.tcathebluecreper.totally_lib.multiblock.TLMultiblockInfo;

import java.util.function.Consumer;

@Info("Main event for registering IE multiblocks, call in Startup Events.\nUse /kubejs reload startup_scripts, then /tl_utils reload, and /reload for recipes, to reload machines.")
public class TLMultiblockRegistrationEventJS extends EventJS {
    private final RegistrationManager manager;
    private final Consumer<TLMultiblockInfo> consumer;
    private final boolean reload;

    public TLMultiblockRegistrationEventJS(RegistrationManager manager, Consumer<TLMultiblockInfo> consumer, boolean reload) {
        this.manager = manager;
        this.consumer = consumer;
        this.reload = reload;
    }

    public TLMultiblockBuilder multiblock(ResourceLocation id) {
        return new TLMultiblockBuilder(id, manager, consumer, reload);
    }
}
