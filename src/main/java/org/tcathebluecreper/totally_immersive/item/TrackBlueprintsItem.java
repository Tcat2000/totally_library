package org.tcathebluecreper.totally_immersive.item;

import com.lowdragmc.lowdraglib.client.scene.ISceneBlockRenderHook;
import com.lowdragmc.lowdraglib.gui.editor.data.UIProject;
import com.lowdragmc.lowdraglib.gui.factory.HeldItemUIFactory;
import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.gui.texture.GuiTextureGroup;
import com.lowdragmc.lowdraglib.gui.texture.ItemStackTexture;
import com.lowdragmc.lowdraglib.gui.texture.ResourceBorderTexture;
import com.lowdragmc.lowdraglib.gui.widget.*;
import com.lowdragmc.lowdraglib.misc.ItemStackTransfer;
import com.lowdragmc.lowdraglib.utils.TrackedDummyWorld;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import org.tcathebluecreper.totally_immersive.block.TIBlocks;
import org.tcathebluecreper.totally_immersive.block.track.TrackBlock;
import org.tcathebluecreper.totally_immersive.block.track.TrackBlockEntity;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class TrackBlueprintsItem extends Item implements HeldItemUIFactory.IHeldItemUIHolder {
    public static TrackedDummyWorld world;
    public static Supplier<WidgetGroup> uiCache;
    public TrackBlueprintsItem() {
        super(new Properties());
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level p_41432_, Player player, InteractionHand hand) {
        if(!(player instanceof ServerPlayer)) return super.use(p_41432_, player, hand);
        HeldItemUIFactory.INSTANCE.openUI((ServerPlayer) player, hand);
        return super.use(p_41432_, player, hand);
    }

    @Override
    public ModularUI createUI(Player player, HeldItemUIFactory.HeldItemHolder heldItemHolder) {
        if(uiCache == null) uiCache = UIProject.loadUIFromFile(ResourceLocation.fromNamespaceAndPath("ldlib", "track_config"));
        WidgetGroup root = uiCache.get();
        AtomicReference<CompoundTag> tag = new AtomicReference<>(heldItemHolder.held.getTag());
        if(tag.get() == null) tag.set(new CompoundTag());

        // Overall Preview

        SceneWidget preview = createTrackPreviewWidget(0,8, 8,184, 98);
        root.addWidget(preview);
        preview.setScalable(true);
        preview.setDraggable(true);
        preview.setIntractable(true);
        preview.setRenderSelect(true);
        preview.setRenderFacing(true);
        preview.setOrthoRange(3);

        setTrackPreviewSettingUseBallast(0, true);

        // Track Ballast

        WidgetGroup selectBallastMenu = (WidgetGroup) root.getFirstWidgetById("select_ballast_menu");
        selectBallastMenu.setVisible(false);

        WidgetGroup selectBallastList = (WidgetGroup) selectBallastMenu.getFirstWidgetById("ballast_list");
        List<ItemStack> items = List.of(Ingredient.of(TagKey.create(BuiltInRegistries.ITEM.key(), ResourceLocation.fromNamespaceAndPath("minecraft", "tools"))).getItems());

        ButtonWidget selectBallastMaterial = (ButtonWidget) root.getFirstWidgetById("select_ballast_material");
        selectBallastMaterial.setOnPressCallback((clickData -> selectBallastMenu.setVisible(true)));

        final ItemStack[] selectedBallastItem = {Objects.requireNonNullElse(items.get(0), ItemStack.EMPTY)};

        AtomicInteger row = new AtomicInteger();
        int maxPerRow = 4;
        final WidgetGroup[] rowGroup = {new WidgetGroup(0,0,0,18)};
        items.forEach((stack -> {
            if(row.get() >= maxPerRow) {
                row.set(0);
                assert selectBallastList != null;
                selectBallastList.addWidget(rowGroup[0]);
                rowGroup[0] = new WidgetGroup(0,0,0,18);
            }
            else {
                rowGroup[0].addWidget(new SlotWidget(new ItemStackTransfer(stack.copy()), 0, row.get() * 18, 0, false, false) {
                    @Override
                    public boolean mouseClicked(double mouseX, double mouseY, int button) {
                        selectedBallastItem[0] = this.getItem();
                        selectBallastMaterial.setBackground(new GuiTextureGroup(new ResourceBorderTexture("ldlib:textures/gui/button.png", 32, 32, 2, 2), new ItemStackTexture(this.getItem()).scale(16/20f)));
                        return true;
                    }
                });
                row.getAndIncrement();
            }
        }));
        if(row.get() > 0) {
            selectBallastList.addWidget(rowGroup[0]);
        }

        // Gauge Options

        WidgetGroup selectTrackGaugeMenu = (WidgetGroup) root.getFirstWidgetById("select_gauge_menu");
        selectTrackGaugeMenu.setVisible(false);
        ButtonWidget selectTackGauge = (ButtonWidget) root.getFirstWidgetById("select_tack_gauge");
        selectTackGauge.setOnPressCallback(clickData -> {
            selectTrackGaugeMenu.setVisible(true);
        });
        WidgetGroup gaugeList = (WidgetGroup) root.getFirstWidgetById("gauge_list");


        gaugeList.addWidget(new WidgetGroup(0, 0, 90, 30).addWidget(createTrackPreviewWidget(5,0,0,90,40)));
        gaugeList.addWidget(new WidgetGroup(0, 0, 90, 30).addWidget(createTrackPreviewWidget(10,0,0,90,40)));
        gaugeList.addWidget(new WidgetGroup(0, 0, 90, 30).addWidget(createTrackPreviewWidget(15,0,0,90,40)));

        // Tie Options

        AtomicReference<Float> tieSpacing = new AtomicReference<>(tag.get().contains("TieSpacing") ? tag.get().getFloat("TieSpacing") : 0.5f);
        LabelWidget spacingText = (LabelWidget) root.getFirstWidgetById("spacing");
        spacingText.setTextProvider(() -> String.valueOf(tieSpacing.get()));

        TextBoxWidget spacingInfoText = (TextBoxWidget) root.getFirstWidgetById("spacing_info");

        ButtonWidget incTieSpace = (ButtonWidget) root.getFirstWidgetById("inc_tie_space");
        incTieSpace.setOnPressCallback((clickData) -> {
            tieSpacing.set(tieSpacing.get() + 0.05f);
            tieSpacing.set(Math.max(Math.round(tieSpacing.get() * 100) / 100f, 0.25f));
            tag.get().putFloat("TieSpacing", tieSpacing.get());
            heldItemHolder.held.setTag(tag.get());

            String text = "-error-";
            if(tieSpacing.get() == 0.5f) text = "Standard";
            if(tieSpacing.get() < 0.5f) text = "Unnecessary";
            if(tieSpacing.get() > 1f) text = "Really Bad";
            if(tieSpacing.get() > 0.75f) text = "Too Few";
            if(tieSpacing.get() > 0.5f) text = "Sparse";
            spacingInfoText.setContent(List.of(text));
        });
        ButtonWidget decTieSpace = (ButtonWidget) root.getFirstWidgetById("dec_tie_space");
        decTieSpace.setOnPressCallback((clickData) -> {
            tieSpacing.set(tieSpacing.get() - 0.05f);
            tieSpacing.set(Math.max(Math.round(tieSpacing.get() * 100) / 100f, 0.25f));
            tag.get().putFloat("TieSpacing", tieSpacing.get());
            heldItemHolder.held.setTag(tag.get());

            String text = "-error-";
            if(tieSpacing.get() == 0.5f) text = "Standard";
            if(tieSpacing.get() < 0.5f) text = "Unnecessary";
            if(tieSpacing.get() > 1f) text = "Really Bad";
            if(tieSpacing.get() > 0.75f) text = "Too Few";
            if(tieSpacing.get() > 0.5f) text = "Space";
            spacingInfoText.setContent(List.of(text));
        });
        return new ModularUI(root, heldItemHolder, player);
    }

    public SceneWidget createTrackPreviewWidget(int x, int posX, int posY, int sizeX, int sizeY) {
        if(world == null) world = new TrackedDummyWorld();
        SceneWidget wig = new SceneWidget(0,0,90,30, world);
        wig.setRenderedCore(List.of(new BlockPos(x, 0, 10)), new ISceneBlockRenderHook() {
            @Override
            public void apply(boolean isTESR, RenderType layer) {
                RenderSystem.enableBlend();
                RenderSystem.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
            }
        });
        wig.useOrtho(true);
        wig.setOrthoRange(1);
        wig.setScalable(false);
        wig.setDraggable(false);
        wig.setIntractable(false);
        wig.setRenderSelect(false);
        wig.setRenderFacing(false);
        wig.useCacheBuffer(false);
        wig.setCenter(new Vector3f(0.5f,0,0.5f));
        wig.setCameraYawAndPitch(89.999f,0);
        world.setBlock(new BlockPos(x,0,10), TIBlocks.TRACK_BLOCK.get().defaultBlockState(), 3);
        TrackBlockEntity track = (TrackBlockEntity) world.getBlockEntity(new BlockPos(x,0,10));
        track.targetPos = new BlockPos(x,0,-10);
        track.targetVector = new Vec3(0,0,0);
        track.localVector = new Vec3(0,0,0);
        track.previewMinBallastHeight = -2f;
        track.constructed = true;
        track.needUpdate = true;
        ((TrackBlock)world.getBlockState(new BlockPos(x,0,10)).getBlock()).updateTrack(world, new BlockPos(x,0,10), world.getBlockState(new BlockPos(x,0,10)), null);
        wig.setSize(sizeX,sizeY);
        wig.setSelfPosition(posX,posY);
        return wig;
    }
    public void setTrackPreviewSettingUseBallast(int x, boolean useBallast) {
        TrackBlockEntity be = (TrackBlockEntity) world.getBlockEntity(new BlockPos(x,0,10));
        be.previewForceBallast = useBallast;
        be.needUpdate = true;
        ((TrackBlock)world.getBlockState(new BlockPos(x,0,10)).getBlock()).updateTrack(world, new BlockPos(x,0,10), world.getBlockState(new BlockPos(x,0,10)), null);
    }
}
