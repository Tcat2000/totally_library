package org.tcathebluecreper.totally_immersive.mod;

import blusunrize.immersiveengineering.api.wires.utils.IElectricDamageSource;
import blusunrize.immersiveengineering.common.util.IEDamageSources;
import com.mojang.logging.LogUtils;
import mcjty.theoneprobe.TheOneProbe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;
import org.tcathebluecreper.totally_immersive.mod.Multiblock.chemical_bath.ChemicalBathRenderer;
import org.tcathebluecreper.totally_immersive.mod.block.TIBlocks;
import org.tcathebluecreper.totally_immersive.mod.block.track.BridgeBlockEntityRenderer;
import org.tcathebluecreper.totally_immersive.mod.block.track.BridgeSlaveBlockEntity;
import org.tcathebluecreper.totally_immersive.mod.block.track.BridgeSlaveBlockEntityRenderer;
import org.tcathebluecreper.totally_immersive.mod.block.track.TrackBlockEntityRenderer;
import org.tcathebluecreper.totally_immersive.mod.integration.theoneprobe.MultiblocksTOPProvider;
import org.tcathebluecreper.totally_immersive.mod.item.TIItems;
import org.tcathebluecreper.totally_immersive.api.lib.TIDynamicModel;

import java.util.function.Supplier;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(TotallyImmersive.MODID)
public class TotallyImmersive {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "totally_immersive";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();
    // Create a Deferred Register to hold Blocks which will all be registered under the "totally_immersive" namespace
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    // Create a Deferred Register to hold Items which will all be registered under the "totally_immersive" namespace
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    // Create a Deferred Register to hold CreativeModeTabs which will all be registered under the "totally_immersive" namespace
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    // Creates a new Block with the id "totally_immersive:example_block", combining the namespace and path
    public static final RegistryObject<Block> EXAMPLE_BLOCK = BLOCKS.register("example_block", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE)));
    // Creates a new BlockItem with the id "totally_immersive:example_block", combining the namespace and path
    public static final RegistryObject<Item> EXAMPLE_BLOCK_ITEM = ITEMS.register("example_block", () -> new BlockItem(EXAMPLE_BLOCK.get(), new Item.Properties()));

    // Creates a new food item with the id "totally_immersive:example_id", nutrition 1 and saturation 2
    public static final RegistryObject<Item> EXAMPLE_ITEM = ITEMS.register("example_item", () -> new Item(new Item.Properties().food(new FoodProperties.Builder().alwaysEat().nutrition(1).saturationMod(2f).build())));

    // Creates a creative tab with the id "totally_immersive:example_tab" for the example item, that is placed after the combat tab
    public static final RegistryObject<CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.register("example_tab", () -> CreativeModeTab.builder().withTabsBefore(CreativeModeTabs.COMBAT).icon(() -> EXAMPLE_ITEM.get().getDefaultInstance()).displayItems((parameters, output) -> {
        output.accept(EXAMPLE_ITEM.get()); // Add the example item to the tab. For your own tabs, this method is preferred over the event
    }).build());

    public TotallyImmersive() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::clientSetup);

        // Register the Deferred Register to the mod event bus so blocks get registered
        BLOCKS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so items get registered
        ITEMS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so tabs get registered
        CREATIVE_MODE_TABS.register(modEventBus);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);

        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);


        TIBlocks.BLOCKS.register(modEventBus);
        TIItems.ITEMS.register(modEventBus);
        TIBlocks.BETs.register(modEventBus);
        TIMultiblocks.init();
        TIContent.TIRecipes.RECIPE_TYPES.register(modEventBus);
        TIContent.TIRecipes.RECIPE_SERIALIZERS.register(modEventBus);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");
        LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));

        if(Config.logDirtBlock) LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));

        LOGGER.info(Config.magicNumberIntroduction + Config.magicNumber);

        Config.items.forEach((item) -> LOGGER.info("ITEM >> {}", item.toString()));
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        TheOneProbe.theOneProbeImp.registerProvider(new MultiblocksTOPProvider());
    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if(event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) event.accept(EXAMPLE_BLOCK_ITEM);
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }
    @SubscribeEvent
    public void onDamage(LivingDamageEvent event) {
        if((event.getSource() instanceof IEDamageSources.ElectricDamageSource) && !event.getEntity().onGround()) event.setCanceled(true);
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            // Some client setup code
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
            ManualEntries.AddManualEntries();
            MinecraftForge.EVENT_BUS.register(new ClientRenderEvents());
        }

        @SubscribeEvent
        public static void registerModels(ModelEvent.RegisterAdditional ev)
        {
            ManualEntries.RegisterModels(ev);
        }
    }
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientProxy {
        @SubscribeEvent
        public static void registerModelLoaders(ModelEvent.RegisterGeometryLoaders ev) {
            ChemicalBathRenderer.craneTop = new TIDynamicModel(ChemicalBathRenderer.craneTopId);
            ChemicalBathRenderer.craneMiddle = new TIDynamicModel(ChemicalBathRenderer.craneMiddleId);
            ChemicalBathRenderer.craneBottom = new TIDynamicModel(ChemicalBathRenderer.craneBottomId);

            TrackBlockEntityRenderer.tie = new TIDynamicModel(TrackBlockEntityRenderer.tieLocation);
            TrackBlockEntityRenderer.rail = new TIDynamicModel(TrackBlockEntityRenderer.railLocation);

            BridgeBlockEntityRenderer.beam = new TIDynamicModel(BridgeBlockEntityRenderer.beamLocation);
            BridgeBlockEntityRenderer.beamHorizontal = new TIDynamicModel(BridgeBlockEntityRenderer.beamHorizontalLocation);
        }
        @SubscribeEvent
        public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
            registerBERenderNoContext(event, TIMultiblocks.CHEMICAL_BATH.masterBE().get(), ChemicalBathRenderer::new);
            registerBERenderNoContext(event, TIBlocks.TRACK_BLOCK_ENTITY.get(), TrackBlockEntityRenderer::new);
            registerBERenderNoContext(event, TIBlocks.BRIDGE_BLOCK_ENTITY.get(), BridgeBlockEntityRenderer::new);
            registerBERenderNoContext(event, TIBlocks.BRIDGE_SLAVE_BLOCK_ENTITY.get(), BridgeSlaveBlockEntityRenderer::new);
        }

        private static <T extends BlockEntity>
        void registerBERenderNoContext(
                EntityRenderersEvent.RegisterRenderers event, BlockEntityType<? extends T> type, Supplier<BlockEntityRenderer<T>> render) {
            event.registerBlockEntityRenderer(type, $ -> render.get());
        }
    }
}
