package org.tcathebluecreper.totally_lib.kubejs;

import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.bindings.event.StartupEvents;
import dev.latvian.mods.kubejs.event.EventHandler;


public class Plugin extends KubeJSPlugin {
    public static EventHandler MultiblockRegisterEventJS;
    @Override
    public void registerEvents() {
        super.registerEvents();
        MultiblockRegisterEventJS = StartupEvents.GROUP.startup("multiblocks", () -> TLMultiblockRegistrationEventJS.class);
    }
}
