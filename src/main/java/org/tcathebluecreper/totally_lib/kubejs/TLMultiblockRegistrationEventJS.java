package org.tcathebluecreper.totally_lib.kubejs;

import blusunrize.immersiveengineering.api.multiblocks.blocks.MultiblockRegistration;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import dev.latvian.mods.kubejs.event.EventJS;
import net.minecraft.resources.ResourceLocation;
import org.tcathebluecreper.totally_lib.RegistrationManager;
import org.tcathebluecreper.totally_lib.multiblock.RegisterableMultiblock;
import org.tcathebluecreper.totally_lib.multiblock.TLMultiblockBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class TLMultiblockRegistrationEventJS extends EventJS {
    private final RegistrationManager manager;
    private final Consumer<RegisterableMultiblock> consumer;

    public TLMultiblockRegistrationEventJS(RegistrationManager manager, Consumer<RegisterableMultiblock> consumer) {
        this.manager = manager;
        this.consumer = consumer;
    }

    public TLMultiblockBuilder multiblock(ResourceLocation id) {
        return new TLMultiblockBuilder(id, manager, consumer);
    }
}
