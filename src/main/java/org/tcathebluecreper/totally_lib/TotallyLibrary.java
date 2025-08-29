package org.tcathebluecreper.totally_lib;

import com.lowdragmc.lowdraglib.Platform;
import com.mojang.logging.LogUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import org.tcathebluecreper.totally_lib.dev_utils.*;
import org.tcathebluecreper.totally_lib.multiblock.ModMultiblocks;
import org.tcathebluecreper.totally_lib.multiblock.TLMultiblockRegistrationEvent;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@Mod(TotallyLibrary.MODID)
public class TotallyLibrary {
    public static final String MODID = "totally_lib";
    private static final Logger LOGGER = LogUtils.getLogger();
    public static MinecraftServer server;
    public static final RegistrationManager regManager = new RegistrationManager(FMLJavaModLoadingContext.get().getModEventBus());

    public static final File multiblockFileLocation = new File(Platform.getGamePath().toFile(), "tl_multiblock_metadata");

    public TotallyLibrary() {
        multiblockFileLocation.mkdirs();
        File infoFile = new File(multiblockFileLocation, "info.txt");
        try {
            if(!infoFile.exists()) infoFile.createNewFile();
            FileWriter fw = new FileWriter(infoFile);
            fw.flush();
            fw.write("""
                This folder contains the editor files for any multiblocks created using Totally Lib.
                These files are *NOT* required for your multiblocks to work; they are only the values set in the editor before exporting.
                When exporting a modpack, this folder can be safely left out.
                IF your delete these files, you multiblock editor will not remember your machines, and you will no longer be able to open/edit/export them.""");
            fw.close();
        } catch(IOException e) {
            throw new RuntimeException(e);
        }

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        MinecraftForge.EVENT_BUS.register(new ForgeEvents());
        modEventBus.register(new ModEvents());

        MultiblockDesigner.init();
        MultiblockDesignerEntity.init();
        StructureArg.init();


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
        @SubscribeEvent
        public void onRegisterCommands(RegisterCommandsEvent event) {
            TLCommand.register(event.getDispatcher());
        }

        @SubscribeEvent
        public void render(RenderLevelStageEvent event) {
            SelectionManager.render(event);
        }

        @SubscribeEvent
        public void serverStarted(ServerStartedEvent event) {
            server = event.getServer();
        }
    }
}
