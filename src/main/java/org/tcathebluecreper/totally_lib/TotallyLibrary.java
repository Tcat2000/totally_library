package org.tcathebluecreper.totally_lib;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegisterEvent;
import org.slf4j.Logger;
import org.tcathebluecreper.totally_lib.kubejs.Plugin;
import org.tcathebluecreper.totally_lib.kubejs.TLMultiblockRegistrationEventJS;
import org.tcathebluecreper.totally_lib.multiblock.ModMultiblocks;
import org.tcathebluecreper.totally_lib.multiblock.TLMultiblockRegistrationEvent;

@Mod(TotallyLibrary.MODID)
public class TotallyLibrary {
    public static final String MODID = "totally_lib";
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final RegistrationManager regManager = new RegistrationManager(FMLJavaModLoadingContext.get().getModEventBus());

    public TotallyLibrary() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        MinecraftForge.EVENT_BUS.register(this);
        modEventBus.register(this);

        ModMultiblocks.init();
    }

    @SubscribeEvent
    public static void RegisterEvent(RegisterEvent event) {
        MinecraftForge.EVENT_BUS.post(new TLMultiblockRegistrationEvent(TotallyLibrary.regManager, ModMultiblocks.allMultiblocks::add));
        Plugin.MultiblockRegisterEventJS.post(new TLMultiblockRegistrationEventJS(TotallyLibrary.regManager, ModMultiblocks.allMultiblocks::add));
    }

    @SubscribeEvent
    public static void registerMultiblocks(TLMultiblockRegistrationEvent event) {
        System.out.println("running event!");
    }
}
