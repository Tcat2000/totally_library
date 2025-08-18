package org.tcathebluecreper.totally_lib;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.event.server.ServerLifecycleEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.IModBusEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.ModLifecycleEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegisterEvent;
import org.slf4j.Logger;
import org.tcathebluecreper.totally_lib.kubejs.Plugin;
import org.tcathebluecreper.totally_lib.kubejs.TLMultiblockRegistrationEventJS;
import org.tcathebluecreper.totally_lib.multiblock.ModMultiblocks;
import org.tcathebluecreper.totally_lib.multiblock.TLMultiblockBuilder;
import org.tcathebluecreper.totally_lib.multiblock.TLMultiblockRegistrationEvent;

@Mod(TotallyLibrary.MODID)
public class TotallyLibrary {
    public static final String MODID = "totally_lib";
    private static final Logger LOGGER = LogUtils.getLogger();
    public static MinecraftServer server;
    public static final RegistrationManager regManager = new RegistrationManager(FMLJavaModLoadingContext.get().getModEventBus());

    public TotallyLibrary() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        MinecraftForge.EVENT_BUS.register(new ForgeEvents());
        modEventBus.register(new ModEvents());


        modEventBus.post(new TLMultiblockRegistrationEvent(TotallyLibrary.regManager, ModMultiblocks.allMultiblocks::add));


        ModMultiblocks.init();
    }

    private static class ModEvents {
        @SubscribeEvent
        public void registerMultiblocks(TLMultiblockRegistrationEvent event) {
//            event.multiblock(ResourceLocation.fromNamespaceAndPath("test","multiblock")).size(3,3,3).masterOffset(1,1,2).triggerOffset(1,1,2).bake();
        }

    }
    private static class ForgeEvents {
    }
}
