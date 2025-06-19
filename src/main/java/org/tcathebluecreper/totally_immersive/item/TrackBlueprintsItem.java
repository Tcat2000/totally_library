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
import net.minecraft.world.level.block.Blocks;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class TrackBlueprintsItem extends Item implements HeldItemUIFactory.IHeldItemUIHolder {
    public static Supplier<WidgetGroup> uiCache = UIProject.loadUIFromFile(ResourceLocation.fromNamespaceAndPath("ldlib", "track_config"));
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
        WidgetGroup root = UIProject.loadUIFromFile(new ResourceLocation("ldlib:track_config")).get();
        AtomicReference<CompoundTag> tag = new AtomicReference<>(heldItemHolder.held.getTag());
        if(tag.get() == null) tag.set(new CompoundTag());

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
        TrackedDummyWorld world = new TrackedDummyWorld();
        SceneWidget wig = new SceneWidget(0,0,90,30, world);
        wig.setRenderedCore(List.of(new BlockPos(0, 0, 0), new BlockPos(0, 1, 0)), new ISceneBlockRenderHook() {
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
        wig.setCameraYawAndPitch(89.999f,0);
        world.setBlock(new BlockPos(0,0,0), Blocks.DIAMOND_BLOCK.defaultBlockState(), 3);
        world.setBlock(new BlockPos(0,1,0), Blocks.CHEST.defaultBlockState(), 3);
//        TrackBlockEntity track = (TrackBlockEntity) world.getBlockEntity(new BlockPos(0,0,0));
//        track.targetPos = new BlockPos(10,0,0);
        wig.setSize(90,30);

        gaugeList.addWidget(new WidgetGroup(0, 0, 90, 30).addWidget(wig));

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
}
