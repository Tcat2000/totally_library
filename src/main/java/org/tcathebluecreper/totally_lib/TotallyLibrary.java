package org.tcathebluecreper.totally_lib;

import blusunrize.immersiveengineering.api.ManualHelper;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import org.tcathebluecreper.totally_lib.dev_utils.*;
import org.tcathebluecreper.totally_lib.kubejs.ManualEntriesEventJS;
import org.tcathebluecreper.totally_lib.kubejs.Plugin;
import org.tcathebluecreper.totally_lib.multiblock.TLModMultiblocks;
import org.tcathebluecreper.totally_lib.event.TLMultiblockRegistrationEvent;

import java.util.Map;
import java.util.function.Supplier;

@Mod(TotallyLibrary.MODID)
public class TotallyLibrary {
    public static final String MODID = "totally_lib";
    private static final Logger LOGGER = LogUtils.getLogger();
    public static MinecraftServer server;
    public static final RegistrationManager regManager = new RegistrationManager(FMLJavaModLoadingContext.get().getModEventBus());

//    public static final File multiblockFileLocation = new File(Platform.getGamePath().toFile(), "tl_multiblock_metadata");

    public TotallyLibrary() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        MinecraftForge.EVENT_BUS.register(new ForgeEvents());
        modEventBus.register(new ModEvents());

        MultiblockDesigner.init();
        MultiblockDesignerEntity.init();
        StructureArg.init();


        modEventBus.post(new TLMultiblockRegistrationEvent(TotallyLibrary.regManager, TLModMultiblocks.allMultiblocks::add, false));
    }

    public static boolean isLDLibLoaded() {
        return ModList.get().isLoaded("ldlib");
    }

    private static class ModEvents {
        @SubscribeEvent
        public void registerMultiblocks(TLMultiblockRegistrationEvent event) {
//            event.multiblock(ResourceLocation.fromNamespaceAndPath("test","multiblock")).size(3,3,3).masterOffset(1,1,2).triggerOffset(1,1,2).bake();
        }
        @SubscribeEvent
        public void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
            TLModMultiblocks.  allMultiblocks.forEach(mb -> {
                if(mb.needsBER()) registerBERenderNoContext(event, mb.getMultiblock().multiblockRegistration.masterBE().get(), (Supplier) mb::createRenderer);
            });
        }

        private static <T extends BlockEntity> void registerBERenderNoContext(EntityRenderersEvent.RegisterRenderers event, BlockEntityType<? extends T> type, Supplier<BlockEntityRenderer<T>> render) {
            event.registerBlockEntityRenderer(type, $ -> render.get());
        }
    }
    private static class ForgeEvents {
        @SubscribeEvent
        public void onRegisterCommands(RegisterCommandsEvent event) {
            TLCommands.registerServer(event.getDispatcher());
        }
        @SubscribeEvent
        public void onRegisterClientCommands(RegisterClientCommandsEvent event) {
            TLCommands.registerClient(event.getDispatcher());
        }

        @SubscribeEvent
        public void render(RenderLevelStageEvent event) {
            SelectionManager.render(event);
        }

        @SubscribeEvent
        public void serverStarted(ServerStartedEvent event) {
            server = event.getServer();
        }

        @SubscribeEvent
        public void addReloadListener(AddReloadListenerEvent event) {
            event.addListener(new SimpleJsonResourceReloadListener(new Gson(), "") {
                @Override
                protected void apply(Map<ResourceLocation, JsonElement> pObject, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
//                    TLRegistrableRecipeSerializer.getSerializers().forEach(TLRegistrableRecipeSerializer::clearRecipes);
                }
            });
        }
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            Plugin.manualEntriesEventJS.post(new ManualEntriesEventJS(ManualHelper.getManual().getRoot(), ManualHelper.getManual()));
        }

        @SubscribeEvent
        public static void registerModels(ModelEvent.RegisterAdditional ev) {
            ev.register(ResourceLocation.fromNamespaceAndPath("tlib", "multiblocks/advanced_coke_oven"));
        }
    }
}
