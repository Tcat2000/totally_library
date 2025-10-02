package org.tcathebluecreper.totally_lib.dev_utils;

import blusunrize.immersiveengineering.client.utils.RenderUtils;
import com.lowdragmc.lowdraglib.client.renderer.block.RendererBlock;
import com.lowdragmc.lowdraglib.client.renderer.block.RendererBlockEntity;
import com.lowdragmc.lowdraglib.client.renderer.impl.IModelRenderer;
import com.lowdragmc.lowdraglib.gui.editor.ColorPattern;
import com.lowdragmc.lowdraglib.gui.texture.*;
import com.lowdragmc.lowdraglib.gui.util.ClickData;
import com.lowdragmc.lowdraglib.gui.widget.*;
import com.lowdragmc.lowdraglib.gui.widget.layout.Layout;
import com.lowdragmc.lowdraglib.utils.BlockInfo;
import com.lowdragmc.lowdraglib.utils.TrackedDummyWorld;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.tcathebluecreper.totally_lib.ldlib.InspectorViewable;
import org.tcathebluecreper.totally_lib.ldlib.MultiblockDisplayPanelWidget;
import org.tcathebluecreper.totally_lib.ldlib.ScreenSpaceWidget;
import org.tcathebluecreper.totally_lib.lib.GuiDrawer;
import org.tcathebluecreper.totally_lib.lib.TIDynamicModel;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.BiConsumer;

public class AnimationEditor extends WidgetGroup {
    private static final Logger log = LogManager.getLogger(AnimationEditor.class);
    private final MultiblockDisplayPanelWidget mbDisplay;

    public final ScreenSpaceWidget top;
    public final ScreenSpaceWidget bottom;
    public final ScreenSpaceWidget inspector;
    public final DraggableScrollableWidgetGroup inspectorPane;
    public final TabContainer tabs;
    public final TabButton inspectorTab;
    public final TabButton selectorTab;
    public final DraggableScrollableWidgetGroup partsList;
    public final WidgetGroup partsListContainer;
    public InspectorViewable inspecting = null;
    public final List<AnimationPart> animationParts = new ArrayList<>();
    public AnimationPart selectedAnimationPart;

    public AnimationEditor() {
        ResourceBorderTexture tabSelectedTexture = new ResourceBorderTexture("minecraft:textures/gui/container/creative_inventory/tabs.png", 26, 32, 4, 4);
        tabSelectedTexture.imageWidth = 1/10f;
        tabSelectedTexture.imageHeight = 1/8f;

        ResourceBorderTexture tabUnselectedTexture = new ResourceBorderTexture("minecraft:textures/gui/container/creative_inventory/tabs.png", 26, 32, 4, 4);
        tabUnselectedTexture.imageWidth = 1/10f;
        tabUnselectedTexture.imageHeight = 1/8f;
        tabUnselectedTexture.offsetY = 1/8f;

        mbDisplay = new MultiblockDisplayPanelWidget(0,30,-200,-100, this::postRender);
        addWidget(mbDisplay);


        mbDisplay.loadMultiblock(ResourceLocation.fromNamespaceAndPath("test","multiblock"));

        top = new ScreenSpaceWidget(0, 0, 0, 30);
        addWidget(top);

        top.addWidget(mbDisplay.crateInputField(7,7,100, 15));

        bottom = new ScreenSpaceWidget(0, -100, 0, 0);
        bottom.addWidget(new AnimationTimeLine());
        addWidget(bottom);

        inspector = new ScreenSpaceWidget(-200, 55, 0, -100);
        addWidget(inspector);

        tabs = new TabContainer(0, -25, 300,30);
        inspector.addWidget(tabs);

        inspectorTab = new TabButton(0,0,100,30);
        inspectorTab.setTexture(new GuiTextureGroup(tabSelectedTexture, new TextTexture("INSPECTOR")), new GuiTextureGroup(tabUnselectedTexture, new TextTexture("INSPECTOR")));
        selectorTab = new TabButton(100,0,100,30);
        selectorTab.setTexture(new GuiTextureGroup(tabSelectedTexture, new TextTexture("SELECTOR")), new GuiTextureGroup(tabUnselectedTexture, new TextTexture("SELECTOR")));

        partsList = new DraggableScrollableWidgetGroup(4, 31, 192, 192);
        partsListContainer = new WidgetGroup();
        partsListContainer.setDynamicSized(true);
        partsListContainer.setLayout(Layout.VERTICAL_LEFT);

        partsListContainer.addWidget(new AnimationPart());
        partsListContainer.addWidget(new AnimationPart());
        partsListContainer.addWidget(new AnimationPart());
        partsListContainer.addWidget(new AnimationPart());
        partsListContainer.addWidget(new AnimationPart());
        partsListContainer.addWidget(new AnimationPart());
        partsListContainer.addWidget(new AnimationPart());
        partsListContainer.addWidget(new AnimationPart());
        partsListContainer.addWidget(new AnimationPart());

        partsList.addWidget(partsListContainer);
        ScreenSpaceWidget partsListTab = new ScreenSpaceWidget(-200, 55, 0, -100);
        partsListTab.addWidget(partsList);
        tabs.addTab(selectorTab, partsList);

        inspectorPane = new DraggableScrollableWidgetGroup(4,34, 200, 300);

        tabs.addTab(inspectorTab, inspectorPane);
    }

    public void postRender(SceneWidget scene) {
        var poseStack = new PoseStack();
        var tessellator = Tesselator.getInstance();
        var buffer = tessellator.getBuilder();
        var matrix4f = poseStack.last().pose();
        var normal = poseStack.last().normal();

        buffer.begin(RenderType.solid().mode(), RenderType.solid().format());

        TIDynamicModel model = new TIDynamicModel("chemical_bath/top");
        IModelRenderer mr = new IModelRenderer(ResourceLocation.fromNamespaceAndPath("totally_immersive","dynamic/chemical_bath/chemical_bath"));

        var lightTexture = Minecraft.getInstance().gameRenderer.lightTexture();
        lightTexture.turnOnLightLayer();

        RenderSystem.clearColor(1,1,1,1);

        poseStack.pushPose();
        poseStack.translate(0, 4, 0);
//        List<BakedQuad> quads = new ArrayList<>();
//        quads.addAll(mr.renderModel(mbDisplay.level, new BlockPos(0,4,0), null, null, Minecraft.getInstance().level.random));
//        quads.addAll(mr.renderModel(mbDisplay.level, new BlockPos(0,4,0), null, Direction.UP, Minecraft.getInstance().level.random));
//        quads.addAll(mr.renderModel(mbDisplay.level, new BlockPos(0,4,0), null, Direction.DOWN, Minecraft.getInstance().level.random));
//        quads.addAll(mr.renderModel(mbDisplay.level, new BlockPos(0,4,0), null, Direction.NORTH, Minecraft.getInstance().level.random));
//        quads.addAll(mr.renderModel(mbDisplay.level, new BlockPos(0,4,0), null, Direction.EAST, Minecraft.getInstance().level.random));
//        quads.addAll(mr.renderModel(mbDisplay.level, new BlockPos(0,4,0), null, Direction.SOUTH, Minecraft.getInstance().level.random));
//        quads.addAll(mr.renderModel(mbDisplay.level, new BlockPos(0,4,0), null, Direction.WEST, Minecraft.getInstance().level.random));

        for(int i = 0; i < animationParts.size(); i++) {
            RenderUtils.renderModelTESRFancy(animationParts.get(i).quads, buffer, poseStack, mbDisplay.level, BlockPos.ZERO, false,-1, 15);
        }

        tessellator.end();
        buffer.begin(VertexFormat.Mode.LINES, DefaultVertexFormat.POSITION_COLOR_NORMAL);
        RenderSystem.setShader(GameRenderer::getRendertypeLinesShader);
        RenderSystem.lineWidth(5);
        RenderSystem.disableCull();

        if(selectedAnimationPart != null) {
            matrix4f = poseStack.last().pose();
            normal = poseStack.last().normal();
            for(int i = 0; i < selectedAnimationPart.quads.size(); i++) {
                BakedQuad quad = selectedAnimationPart.quads.get(i);
                int[] verts = quad.getVertices();

                for(int j = 0; j < 32; j += 8) {
                    float x0 = Float.intBitsToFloat(verts[j]);
                    float y0 = Float.intBitsToFloat(verts[j + 1]);
                    float z0 = Float.intBitsToFloat(verts[j + 2]);

                    float x1 = Float.intBitsToFloat(verts[(j + 8) % 32]);
                    float y1 = Float.intBitsToFloat(verts[(j + 9) % 32]);
                    float z1 = Float.intBitsToFloat(verts[(j + 10) % 32]);

                    float f = x1 - x0;
                    float f1 = y1 - y0;
                    float f2 = z1 - z0;
                    float f3 = Mth.sqrt(f * f + f1 * f1 + f2 * f2);
                    f /= f3;
                    f1 /= f3;
                    f2 /= f3;

                    buffer.vertex(matrix4f, x0, y0, z0).color(255, 0, 0, 255).normal(normal, f, f1, f2).endVertex();
                    buffer.vertex(matrix4f, x1, y1, z1).color(255, 0, 0, 255).normal(normal, f, f1, f2).endVertex();
                }
            }
        }

        tessellator.end();
    }

    WidgetGroup inspectorContent;

    public void reloadInspector() {
        inspectorPane.removeWidget(inspectorContent);
        inspectorContent = new WidgetGroup();
        inspectorContent.setDynamicSized(true);
        inspecting.loadInspector(inspectorContent, 0, 0, inspectorPane.getSizeWidth() - 4, inspectorPane.getSizeHeight());
        inspectorPane.addWidget(inspectorContent);
    }

    private static class AnimationElementWidget extends WidgetGroup {
        List<AnimationElementWidget> list;
        AABB value = new AABB(0,0,0,0,0,0);
        AABB oldValue = value;
        ColorBorderTexture selectTexture = new ColorBorderTexture(1, Color.RED.getRGB());
        static HashMap<List<AnimationElementWidget>, AnimationElementWidget> selected = new HashMap<>();
        final Runnable trigger;

        final TextFieldWidget minX;
        final TextFieldWidget minY;
        final TextFieldWidget minZ;
        TextFieldWidget maxX;
        TextFieldWidget maxY;
        TextFieldWidget maxZ;

        final LabelWidget minLabel;
        final LabelWidget maxLabel;

        boolean mode = false;

        public AnimationElementWidget(List<AnimationElementWidget> list, WidgetGroup parent, Runnable trigger) {
            this.trigger = trigger;

            this.list = list;
            this.setSize(parent.getSizeWidth() - 3, 47);
            this.initTemplate();
            parent.addWidget(this);
            list.add(this);
            addWidget(new LabelWidget(4,10, String.valueOf(list.size())));
            addWidget(minLabel = new LabelWidget(27,10, "MIN:"));
            addWidget(maxLabel = new LabelWidget(27,26, "MAX:"));
            addWidget(minX = new TextFieldWidget(49, 7, 45, 16, () -> String.valueOf(value.min(Direction.Axis.X)), null));
            addWidget(minY = new TextFieldWidget(95, 7, 45, 16, () -> String.valueOf(value.min(Direction.Axis.Y)), null));
            addWidget(minZ = new TextFieldWidget(141, 7, 45, 16, () -> String.valueOf(value.min(Direction.Axis.Z)), null));
            addWidget(maxX = new TextFieldWidget(49, 23, 45, 16, () -> String.valueOf(value.max(Direction.Axis.X)), null));
            addWidget(maxY = new TextFieldWidget(95, 23, 45, 16, () -> String.valueOf(value.max(Direction.Axis.Y)), null));
            addWidget(maxZ = new TextFieldWidget(141, 23, 45, 16, () -> String.valueOf(value.max(Direction.Axis.Z)), null));
            update();

            addWidget(new ButtonWidget(4,22, 20, 10, new GuiTextureGroup(ResourceBorderTexture.BUTTON_COMMON, new TextTexture("mode")), clickData -> toggleMode()));

            selected.put(list, this);
            list.forEach(AnimationElementWidget::update);
            this.setBackground(ResourceBorderTexture.BORDERED_BACKGROUND, selectTexture);
        }

        public void toggleMode() {
            mode = !mode;
            if(value == null) return;
            try {
                if(mode) {
//                value = new AABB(value.maxX - value.minX, value.maxY - value.minY, value.maxZ - value.minZ, value.minX, value.minY, value.minZ);
                    minLabel.setText("SIZE");
                    maxLabel.setText("OFF");

                    double newMinX = Math.min(Double.parseDouble(minX.getCurrentString()), Double.parseDouble(maxX.getCurrentString()));
                    double newMinY = Math.min(Double.parseDouble(minY.getCurrentString()), Double.parseDouble(maxY.getCurrentString()));
                    double newMinZ = Math.min(Double.parseDouble(minZ.getCurrentString()), Double.parseDouble(maxZ.getCurrentString()));
                    double newMaxX = Math.max(Double.parseDouble(minX.getCurrentString()), Double.parseDouble(maxX.getCurrentString())) - Math.min(Double.parseDouble(minX.getCurrentString()), Double.parseDouble(maxX.getCurrentString()));
                    double newMaxY = Math.max(Double.parseDouble(minY.getCurrentString()), Double.parseDouble(maxY.getCurrentString())) - Math.min(Double.parseDouble(minY.getCurrentString()), Double.parseDouble(maxY.getCurrentString()));
                    double newMaxZ = Math.max(Double.parseDouble(minZ.getCurrentString()), Double.parseDouble(maxZ.getCurrentString())) - Math.min(Double.parseDouble(minZ.getCurrentString()), Double.parseDouble(maxZ.getCurrentString()));
                    maxX.setCurrentString(newMinX);
                    maxY.setCurrentString(newMinY);
                    maxZ.setCurrentString(newMinZ);
                    minX.setCurrentString(newMaxX);
                    minY.setCurrentString(newMaxY);
                    minZ.setCurrentString(newMaxZ);
                } else {
//                value = new AABB(value.maxX + value.minX, value.maxY + value.minY, value.maxZ + value.minZ, value.minX, value.minY, value.minZ);
                    minLabel.setText("MIN");
                    maxLabel.setText("MAX");

                    double newMinX = Double.parseDouble(maxX.getCurrentString()) + Double.parseDouble(minX.getCurrentString());
                    double newMinY = Double.parseDouble(maxY.getCurrentString()) + Double.parseDouble(minY.getCurrentString());
                    double newMinZ = Double.parseDouble(maxZ.getCurrentString()) + Double.parseDouble(minZ.getCurrentString());
                    double newMaxX = Double.parseDouble(maxX.getCurrentString());
                    double newMaxY = Double.parseDouble(maxY.getCurrentString());
                    double newMaxZ = Double.parseDouble(maxZ.getCurrentString());
                    maxX.setCurrentString(newMaxX);
                    maxY.setCurrentString(newMaxY);
                    maxZ.setCurrentString(newMaxZ);
                    minX.setCurrentString(newMinX);
                    minY.setCurrentString(newMinY);
                    minZ.setCurrentString(newMinZ);
                }
            } catch(NumberFormatException ignored) {}
        }

        public void update() {
            this.setSelfPosition(2, list.indexOf(this) * 47 + 1);
            this.initTemplate();
        }

        public VoxelShape getShape() {
            return Shapes.box(value.minX / 16, value.minY / 16, value.minZ / 16, value.maxX / 16, value.maxY / 16, value.maxZ / 16);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if(isMouseOverElement(mouseX, mouseY) && !super.mouseClicked(mouseX, mouseY, button)) {
                list.forEach(AnimationElementWidget::update);
                this.setBackground(ResourceBorderTexture.BORDERED_BACKGROUND, selectTexture);
                parent.removeWidget(this);
                parent.addWidget(this);
                selected.put(list, this);
                return true;
            }
            return false;
        }

        @Override
        public void updateScreen() {
            super.updateScreen();
            try {
                if(mode) value = new AABB(Double.parseDouble(maxX.getCurrentString()), Double.parseDouble(maxY.getCurrentString()), Double.parseDouble(maxZ.getCurrentString()), Double.parseDouble(maxX.getCurrentString()) + Double.parseDouble(minX.getCurrentString()), Double.parseDouble(maxY.getCurrentString()) + Double.parseDouble(minY.getCurrentString()), Double.parseDouble(maxZ.getCurrentString()) + Double.parseDouble(minZ.getCurrentString()));
                else value = new AABB(Double.parseDouble(minX.getCurrentString()), Double.parseDouble(minY.getCurrentString()), Double.parseDouble(minZ.getCurrentString()), Double.parseDouble(maxX.getCurrentString()), Double.parseDouble(maxY.getCurrentString()), Double.parseDouble(maxZ.getCurrentString()));
                if(value != oldValue) {
                    trigger.run();
                    oldValue = value;
                }
            } catch(NumberFormatException e) {value = new AABB(0,0,0,0,0,0);}
        }

        @Override
        public String toString() {
            try {
                if(mode) return "Shapes.offset(" +
                    Double.parseDouble(minX.getCurrentString()) +
                    "," +
                    Double.parseDouble(minY.getCurrentString()) +
                    "," +
                    Double.parseDouble(minZ.getCurrentString()) +
                    "," +
                    Double.parseDouble(maxX.getCurrentString()) +
                    "," +
                    Double.parseDouble(maxY.getCurrentString()) +
                    "," +
                    Double.parseDouble(maxZ.getCurrentString()) +
                    "),";
                return "Shapes.size(" +
                    Double.parseDouble(minX.getCurrentString()) +
                    "," +
                    Double.parseDouble(minY.getCurrentString()) +
                    "," +
                    Double.parseDouble(minZ.getCurrentString()) +
                    "," +
                    Double.parseDouble(maxX.getCurrentString()) +
                    "," +
                    Double.parseDouble(maxY.getCurrentString()) +
                    "," +
                    Double.parseDouble(maxZ.getCurrentString()) +
                    "),";
            } catch(NumberFormatException ignored) {return "";}
        }
    }

    public boolean mouseInside(Widget widget, double x, double y, int posX, int posY, int sizeX, int sizeY) {
        boolean a = false;
        if(posX >= 0 && sizeX >= 0) a = x >= posX + widget.getPositionX() && x <= posX + sizeX + widget.getPositionX();
        if(posX >= 0 && sizeX < 0) a = x >= posX + widget.getPositionX();
        if(posX < 0 && sizeX >= 0) a = x <= sizeX + widget.getPositionX();
        if(posX < 0 && sizeX < 0) a = true;
        boolean b = false;
        if(posY >= 0 && sizeY >= 0) b = y >= posY + widget.getPositionY() && y <= posY + sizeY + widget.getPositionY();
        if(posY >= 0 && sizeY < 0) b = y >= posY + widget.getPositionY();
        if(posY < 0 && sizeY >= 0) b = y <= sizeY + widget.getPositionY();
        if(posY < 0 && sizeY < 0) b = true;
        return a && b;
    }

    private class AnimationTimeLine extends Widget {
        int cursorPos = 0;
        float cursorSubPos = 0;
        int scroll = 35;
        boolean isDragging = false;
        int animationLength = 100;
        boolean playing = false;
        long lastTick = 0;


        public List<AnimationKeyframe> posKeyframes = new ArrayList<>();
        public List<AnimationKeyframe> rotKeyframes = new ArrayList<>();
        public List<AnimationKeyframe> sizeKeyframes = new ArrayList<>();

        public AnimationTimeLine() {
            super(5,5, 100, 90);
        }

        @Override
        public void updateScreen() {
            this.setSize(parent.getSizeWidth() - 10, 90);
            if(playing && lastTick != Minecraft.getInstance().level.getGameTime()) {
                lastTick = Minecraft.getInstance().level.getGameTime();
                cursorPos++;
                cursorSubPos = cursorPos;
                if(cursorPos > animationLength) cursorPos = 0;
            }
            partsList.setSize(inspector.getSizeWidth() - 8, inspector.getSizeHeight() - 10);
        }

        @Override
        public void drawInBackground(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
            GuiDrawer g = new GuiDrawer(graphics, this);

            g.drawRect(10, 18 * 2, getSizeWidth() - 10, 1, GuiDrawer.text_dark);
            g.drawRect(10, 18 * 3, getSizeWidth() - 10, 1, GuiDrawer.text_dark);
            g.drawRect(10, 18 * 4, getSizeWidth() - 10, 1, GuiDrawer.text_dark);

            g.drawRect(32, 24, 1, 62, GuiDrawer.text_dark);

            g.drawString("TIMELINE", 0, 0, GuiDrawer.text_light);
            g.drawString(String.valueOf(cursorPos), 0, 20, GuiDrawer.text_light);
            g.drawString("POS", 0, 6 + 18 * 2, GuiDrawer.text_light);
            g.drawString("ROT", 0, 6 + 18 * 3, GuiDrawer.text_light);
            g.drawString("SIZE", 0, 6 + 18 * 4, GuiDrawer.text_light);

            g.drawRect(23, 6 + 18 * 2, 8, 8, GuiDrawer.dark);
            g.drawRect(24, 7 + 18 * 2, 6, 6, GuiDrawer.mid);
            g.drawRect(24, 9 + 18 * 2, 6, 2, GuiDrawer.text_dark);
            g.drawRect(26, 7 + 18 * 2, 2, 6, GuiDrawer.text_dark);

            g.drawRect(23, 6 + 18 * 3, 8, 8, GuiDrawer.dark);
            g.drawRect(24, 7 + 18 * 3, 6, 6, GuiDrawer.mid);
            g.drawRect(24, 9 + 18 * 3, 6, 2, GuiDrawer.text_dark);
            g.drawRect(26, 7 + 18 * 3, 2, 6, GuiDrawer.text_dark);

            g.drawRect(23, 6 + 18 * 4, 8, 8, GuiDrawer.dark);
            g.drawRect(24, 7 + 18 * 4, 6, 6, GuiDrawer.mid);
            g.drawRect(24, 9 + 18 * 4, 6, 2, GuiDrawer.text_dark);
            g.drawRect(26, 7 + 18 * 4, 2, 6, GuiDrawer.text_dark);

            g.drawRect(50, 0, 10, 10, GuiDrawer.dark);
            g.drawRect(51, 1, 8, 8, GuiDrawer.mid);
            g.drawRect(52, 2, 2, 6, GuiDrawer.text_dark);
            g.drawRect(54, 3, 2, 4, GuiDrawer.text_dark);
            g.drawRect(56, 4, 2, 2, GuiDrawer.text_dark);

            g.drawRect(65, 0, 10, 10, GuiDrawer.dark);
            g.drawRect(66, 1, 8, 8, GuiDrawer.mid);
            g.drawRect(67, 2, 2, 6, GuiDrawer.text_dark);
            g.drawRect(71, 2, 2, 6, GuiDrawer.text_dark);

            for(int i = scroll / 20; i < (getSizeWidth() - 20 + scroll) / 20; i++) {
                if(20 * i + 35 - scroll >= 35) g.drawRect(20 * i + 35 - scroll, 22, 1, 12, GuiDrawer.light);
                g.drawString(String.valueOf(i), 20 * i + 33 - scroll, 12, GuiDrawer.light);
            }

            for(int i = 0; i < posKeyframes.size(); i++) {
                AnimationKeyframe frame = posKeyframes.get(i);
                if(!(frame.frame + 35 - scroll >= 35)) continue;
                Color color = frame.color == null ? frame.frame % 2 == 0 ? GuiDrawer.text_dark : GuiDrawer.text_light : new Color(frame.color.getTextColor());
                g.drawRect(frame.frame + 35 - scroll, 42, 1,1, color);
                g.drawRect(frame.frame + 34 - scroll, 43, 3,1, color);
                g.drawRect(frame.frame + 33 - scroll, 44, 5,1, color);
                g.drawRect(frame.frame + 34 - scroll, 45, 3,1, color);
                g.drawRect(frame.frame + 35 - scroll, 46, 1,1, color);

                if(inspecting == frame) {
                    g.drawRect(frame.frame + 35 - scroll, 42, 1,1, GuiDrawer.mid);
                    g.drawRect(frame.frame + 34 - scroll, 43, 1,1, GuiDrawer.mid);
                    g.drawRect(frame.frame + 36 - scroll, 43, 1,1, GuiDrawer.mid);
                    g.drawRect(frame.frame + 33 - scroll, 44, 1,1, GuiDrawer.mid);
                    g.drawRect(frame.frame + 37 - scroll, 44, 1,1, GuiDrawer.mid);
                    g.drawRect(frame.frame + 34 - scroll, 45, 1,1, GuiDrawer.mid);
                    g.drawRect(frame.frame + 36 - scroll, 45, 1,1, GuiDrawer.mid);
                    g.drawRect(frame.frame + 35 - scroll, 46, 1,1, GuiDrawer.mid);
                }
            }

            for(int i = 0; i < rotKeyframes.size(); i++) {
                AnimationKeyframe frame = rotKeyframes.get(i);
                if(!(frame.frame + 35 - scroll >= 35)) continue;
                Color color = frame.color == null ? frame.frame % 2 == 0 ? GuiDrawer.text_dark : GuiDrawer.text_light : new Color(frame.color.getTextColor());
                g.drawRect(frame.frame + 35 - scroll, 62, 1,1, color);
                g.drawRect(frame.frame + 34 - scroll, 63, 3,1, color);
                g.drawRect(frame.frame + 33 - scroll, 64, 5,1, color);
                g.drawRect(frame.frame + 34 - scroll, 65, 3,1, color);
                g.drawRect(frame.frame + 35 - scroll, 66, 1,1, color);

                if(inspecting == frame) {
                    g.drawRect(frame.frame + 35 - scroll, 62, 1,1, GuiDrawer.mid);
                    g.drawRect(frame.frame + 34 - scroll, 63, 1,1, GuiDrawer.mid);
                    g.drawRect(frame.frame + 36 - scroll, 63, 1,1, GuiDrawer.mid);
                    g.drawRect(frame.frame + 33 - scroll, 64, 1,1, GuiDrawer.mid);
                    g.drawRect(frame.frame + 37 - scroll, 64, 1,1, GuiDrawer.mid);
                    g.drawRect(frame.frame + 34 - scroll, 65, 1,1, GuiDrawer.mid);
                    g.drawRect(frame.frame + 36 - scroll, 65, 1,1, GuiDrawer.mid);
                    g.drawRect(frame.frame + 35 - scroll, 66, 1,1, GuiDrawer.mid);
                }
            }

            for(int i = 0; i < sizeKeyframes.size(); i++) {
                AnimationKeyframe frame = sizeKeyframes.get(i);
                if(!(frame.frame + 35 - scroll >= 35)) continue;
                Color color = frame.color == null ? frame.frame % 2 == 0 ? GuiDrawer.text_dark : GuiDrawer.text_light : new Color(frame.color.getTextColor());
                g.drawRect(frame.frame + 35 - scroll, 82, 1,1, color);
                g.drawRect(frame.frame + 34 - scroll, 83, 3,1, color);
                g.drawRect(frame.frame + 33 - scroll, 84, 5,1, color);
                g.drawRect(frame.frame + 34 - scroll, 85, 3,1, color);
                g.drawRect(frame.frame + 35 - scroll, 86, 1,1, color);

                if(inspecting == frame) {
                    g.drawRect(frame.frame + 35 - scroll, 82, 1,1, GuiDrawer.mid);
                    g.drawRect(frame.frame + 34 - scroll, 83, 1,1, GuiDrawer.mid);
                    g.drawRect(frame.frame + 36 - scroll, 83, 1,1, GuiDrawer.mid);
                    g.drawRect(frame.frame + 33 - scroll, 84, 1,1, GuiDrawer.mid);
                    g.drawRect(frame.frame + 37 - scroll, 84, 1,1, GuiDrawer.mid);
                    g.drawRect(frame.frame + 34 - scroll, 85, 1,1, GuiDrawer.mid);
                    g.drawRect(frame.frame + 36 - scroll, 85, 1,1, GuiDrawer.mid);
                    g.drawRect(frame.frame + 35 - scroll, 86, 1,1, GuiDrawer.mid);
                }
            }

            g.drawRect(Math.max(Math.min(cursorPos + 34 - scroll, getSizeWidth() - 1), 32), 30, 3, 1, GuiDrawer.light);
            g.drawRect(Math.max(Math.min(cursorPos + 35 - scroll, getSizeWidth()), 33), 30, 1, 60, GuiDrawer.light);
        }

        @Override
        public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
            //((button == 0 && mouseY - dragY <= getPositionY() + 24 && mouseY - dragY >= getPositionY() + 19) || isDragging) && mouseX - dragX <= getPositionX() + cursorSubPos + 35 && mouseX - dragX >= getPositionX() + cursorSubPos + 30
            if((button == 0 && mouseInside(this, mouseX - dragX, mouseY - dragY, Math.max(Math.min(cursorPos + 34 - scroll, getSizeWidth() - 1), 29), -1, 6, -1)) && (mouseInside(this, mouseX - dragX, mouseY - dragY, -1, 27, -1, 6) || isDragging)) {
                log.info(dragX);
                cursorSubPos += (float) dragX;
                cursorPos = (int) Math.max(mouseX - 40 + scroll, 0);
//                if(cursorPos < 0) cursorPos = 0;
                isDragging = true;
                return false;
            }
            else isDragging = false;
            return false;
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if(mouseX >= 21 + getPositionX() && mouseX <= 29 + getPositionX()) {
                if(mouseY >= 6 + 18 * 2 + getPositionY() && mouseY <= 14 + 18 * 2 + getPositionY()) {
                    posKeyframes.add(new AnimationKeyframe(cursorPos, "Pos", 0, (clickData, frame) -> posKeyframes.remove(frame)));
                    posKeyframes.sort(Comparator.comparingInt(a -> a.frame));
                }
                if(mouseY >= 6 + 18 * 3 + getPositionY() && mouseY <= 14 + 18 * 3 + getPositionY()) {
                    rotKeyframes.add(new AnimationKeyframe(cursorPos, "Rot", 0, (clickData, frame) -> rotKeyframes.remove(frame)));
                    rotKeyframes.sort(Comparator.comparingInt(a -> a.frame));
                }
                if(mouseY >= 6 + 18 * 4 + getPositionY() && mouseY <= 14 + 18 * 4 + getPositionY()) {
                    sizeKeyframes.add(new AnimationKeyframe(cursorPos, "Size", 1, (clickData, frame) -> sizeKeyframes.remove(frame)));
                    sizeKeyframes.sort(Comparator.comparingInt(a -> a.frame));
                }
            }

            if(mouseInside(this, mouseX, mouseY, 35, 36, -1, 16)) {
                Optional<AnimationKeyframe> frame = posKeyframes.stream().filter(k -> k.frame == (int) (mouseX - getPositionX() - 34 + scroll)).findFirst();
                if(frame.isPresent()) {
                    inspecting = frame.get();
                    reloadInspector();
                }
                return true;
            }
            if(mouseInside(this, mouseX, mouseY, 35, 56, -1, 16)) {
                Optional<AnimationKeyframe> frame = rotKeyframes.stream().filter(k -> k.frame == (int) (mouseX - getPositionX() - 34 + scroll)).findFirst();
                if(frame.isPresent()) {
                    inspecting = frame.get();
                    reloadInspector();
                }
                return true;
            }
            if(mouseInside(this, mouseX, mouseY, 35, 76, -1, 16)) { // mouseX >= getPositionX() + 31 && mouseY <= getPositionY() + 85 && mouseY >= getPositionY() + 69
                Optional<AnimationKeyframe> frame = sizeKeyframes.stream().filter(k -> k.frame == (int) (mouseX - getPositionX() - 34 + scroll)).findFirst();
                if(frame.isPresent()) {
                    inspecting = frame.get();
                    reloadInspector();
                }
                return true;
            }
            if(mouseInside(this, mouseX, mouseY, 50,0, 10, 10)) {
                playing = true;
            }
            if(mouseInside(this, mouseX, mouseY, 65,0, 10, 10)) {
                playing = false;
            }
            return false;
        }

        @Override
        public boolean mouseWheelMove(double mouseX, double mouseY, double wheelDelta) {
            if(mouseInside(this, mouseX, mouseY, 35, 0, getSizeHeight(), -1)) {
                scroll += (int) (wheelDelta * (Screen.hasShiftDown() ? 5 : 1) * (Screen.hasControlDown() ? 5 : 1));
                if(scroll < 0) scroll = 0;

                return true;
            }
            return false;
        }
    }

    public static class AnimationKeyframe implements InspectorViewable {
        public int frame;
        public float subFrame = 0;
        public DyeColor color = null;
        public final String type;
        public float valueX;
        public float valueY;
        public float valueZ;

        public final BiConsumer<ClickData, AnimationKeyframe> remove;

        public AnimationKeyframe(int frame, String type, int defaultValue, BiConsumer<ClickData, AnimationKeyframe> remove) {
            this.frame = frame;
            this.type = type;
            valueX = defaultValue;
            valueY = defaultValue;
            valueZ = defaultValue;
            this.remove = remove;
        }

        @Override
        public void loadInspector(WidgetGroup parent, int offsetX, int offsetY, int sizeX, int sizeY) {
            parent.addWidget(new LabelWidget(8, 12, "Frame:").setHoverTooltips("Frame in ticks.", "(timeline shows seconds, tick = 1/20 second)"));
            TextFieldWidget frameField = new TextFieldWidget(65, 10, 60, 15, null, null) {
                @Override
                protected void onTextChanged(String newTextString) {
                    try {
                        frame = Integer.parseInt(newTextString);
                    } catch(NumberFormatException ignored) {}
                }
            };
            frameField.setCurrentString(frame);
            parent.addWidget(frameField);


            parent.addWidget(new LabelWidget(8, 32, "Sub Frame:").setHoverTooltips("Sub frame, from 0 to 1", "(if you need frames smaller than 1 tick, this the part of the frame)"));
            TextFieldWidget subFrameField = new TextFieldWidget(65, 30, 60, 15, null, null) {
                @Override
                protected void onTextChanged(String newTextString) {
                    try {
                        subFrame = Math.max(0, Math.min(Float.parseFloat(newTextString), 1));
                    } catch(NumberFormatException ignored) {}
                }
            };
            subFrameField.setCurrentString(subFrame);
            parent.addWidget(subFrameField);


            List<String> options = new ArrayList();
            options.add("DEFAULT");
            options.addAll(Arrays.stream(DyeColor.values()).map(DyeColor::getName).toList());

            parent.addWidget(new LabelWidget(8, 52, "Color:").setHoverTooltips("Display color on the timeline.", "(default is white on odd and black on even frames)"));
            SelectorWidget colorField = new SelectorWidget(65, 50, 60, 15, options, GuiDrawer.text_dark.getRGB());
            colorField.setOnChanged(newColor -> color = DyeColor.valueOf(newColor.toUpperCase()));
            colorField.setButtonBackground(ColorPattern.T_GRAY.rectTexture());
            colorField.setValue(color == null ? "DEFAULT" : color.getName());


            parent.addWidget(new LabelWidget(8, 72, type + " X:"));
            TextFieldWidget xField = new TextFieldWidget(65, 70, 60, 15, null, null) {
                @Override
                protected void onTextChanged(String newTextString) {
                    try {
                        valueX = Float.parseFloat(newTextString);
                    } catch(NumberFormatException ignored) {}
                }
            };
            xField.setCurrentString(valueX);
            parent.addWidget(xField);

            parent.addWidget(new LabelWidget(8, 92, type + " Y:"));
            TextFieldWidget yField = new TextFieldWidget(65, 90, 60, 15, null, null) {
                @Override
                protected void onTextChanged(String newTextString) {
                    try {
                        valueX = Float.parseFloat(newTextString);
                    } catch(NumberFormatException ignored) {}
                }
            };
            yField.setCurrentString(valueY);
            parent.addWidget(yField);

            parent.addWidget(new LabelWidget(8, 112, type + " Z:"));
            TextFieldWidget zField = new TextFieldWidget(65, 110, 60, 15, null, null) {
                @Override
                protected void onTextChanged(String newTextString) {
                    try {
                        valueX = Float.parseFloat(newTextString);
                    } catch(NumberFormatException ignored) {}
                }
            };
            zField.setCurrentString(valueZ);
            parent.addWidget(zField);

            ButtonWidget deleteButton = new ButtonWidget(65, 130, 60, 15, new GuiTextureGroup(new IGuiTexture[]{ResourceBorderTexture.BUTTON_COMMON, new TextTexture("DELETE")}), clickData -> remove.accept(clickData, this));
            parent.addWidget(deleteButton);

            parent.addWidget(colorField);
        }
    }

    public class AnimationPart extends WidgetGroup {
        List<BakedQuad> quads = new ArrayList<>();
        SceneWidget renderer;
        RendererBlockEntity holder;
        public AnimationPart() {
            super(0,0,192,50);
            initTemplate();

            IModelRenderer2 model = new IModelRenderer2(ResourceLocation.fromNamespaceAndPath("minecraft", "block/diamond_block"));

            quads.addAll(model.renderModel(mbDisplay.level, new BlockPos(0,4,0), null, null, Minecraft.getInstance().level.random));
            quads.addAll(model.renderModel(mbDisplay.level, new BlockPos(0,4,0), null, Direction.UP, Minecraft.getInstance().level.random));
            quads.addAll(model.renderModel(mbDisplay.level, new BlockPos(0,4,0), null, Direction.DOWN, Minecraft.getInstance().level.random));
            quads.addAll(model.renderModel(mbDisplay.level, new BlockPos(0,4,0), null, Direction.NORTH, Minecraft.getInstance().level.random));
            quads.addAll(model.renderModel(mbDisplay.level, new BlockPos(0,4,0), null, Direction.EAST, Minecraft.getInstance().level.random));
            quads.addAll(model.renderModel(mbDisplay.level, new BlockPos(0,4,0), null, Direction.SOUTH, Minecraft.getInstance().level.random));
            quads.addAll(model.renderModel(mbDisplay.level, new BlockPos(0,4,0), null, Direction.WEST, Minecraft.getInstance().level.random));

            addWidget(new TextFieldWidget(52,20,50,15, null, null) {
                @Override
                protected void onTextChanged(String newTextString) {
                    quads.clear();

                    if(!ResourceLocation.isValidResourceLocation(newTextString)) return;

//                    holder.setRenderer(new IModelRenderer(ResourceLocation.parse(newTextString)));

                    quads.addAll(model.renderModel(mbDisplay.level, new BlockPos(0,4,0), null, null, Minecraft.getInstance().level.random));
                    quads.addAll(model.renderModel(mbDisplay.level, new BlockPos(0,4,0), null, Direction.UP, Minecraft.getInstance().level.random));
                    quads.addAll(model.renderModel(mbDisplay.level, new BlockPos(0,4,0), null, Direction.DOWN, Minecraft.getInstance().level.random));
                    quads.addAll(model.renderModel(mbDisplay.level, new BlockPos(0,4,0), null, Direction.NORTH, Minecraft.getInstance().level.random));
                    quads.addAll(model.renderModel(mbDisplay.level, new BlockPos(0,4,0), null, Direction.EAST, Minecraft.getInstance().level.random));
                    quads.addAll(model.renderModel(mbDisplay.level, new BlockPos(0,4,0), null, Direction.SOUTH, Minecraft.getInstance().level.random));
                    quads.addAll(model.renderModel(mbDisplay.level, new BlockPos(0,4,0), null, Direction.WEST, Minecraft.getInstance().level.random));
                }
            });

//            TrackedDummyWorld level = new TrackedDummyWorld();
//            level.addBlock(BlockPos.ZERO, BlockInfo.fromBlock(RendererBlock.BLOCK));
//            this.holder = (RendererBlockEntity)level.getBlockEntity(BlockPos.ZERO);
//
//            holder.setRenderer(new IModelRenderer(ResourceLocation.fromNamespaceAndPath("minecraft", "block/diamond_block")));
//
//            renderer = new SceneWidget(4, 4, 42, 42, level);
//            renderer.setRenderedCore(List.of(BlockPos.ZERO), null);
////            renderer.setAfterWorldRender(sceneWidget -> {
////                var poseStack = new PoseStack();
////                var tessellator = Tesselator.getInstance();
////                var buffer = tessellator.getBuilder();
////
////                buffer.begin(RenderType.solid().mode(), RenderType.solid().format());
////
////                var lightTexture = Minecraft.getInstance().gameRenderer.lightTexture();
////                lightTexture.turnOnLightLayer();
////
////                RenderSystem.clearColor(1,1,1,1);
////
////                RenderUtils.renderModelTESRFancy(quads, buffer, poseStack, mbDisplay.level, BlockPos.ZERO, false,-1, 15);
////
////
////                model.renderItem(ItemStack.EMPTY, ItemDisplayContext.GUI, false, poseStack, MultiBufferSource.immediate(buffer), 15, 15, model.getItemBakedModel());
////
////                tessellator.end();
////            });
////            renderer.setRenderSelect(false);
//            renderer.setRenderFacing(false);
//            renderer.getRenderer().setOnLookingAt(null);
//            renderer.createScene(level);
//            renderer.setBackground(new ColorBorderTexture(1, ColorPattern.T_WHITE.color));
//            renderer.setIntractable(false);
//            addWidget(renderer);

            var level = new TrackedDummyWorld();
            level.addBlock(BlockPos.ZERO, BlockInfo.fromBlock(RendererBlock.BLOCK));
            Optional.ofNullable(level.getBlockEntity(BlockPos.ZERO)).ifPresent(blockEntity -> {
                if (blockEntity instanceof RendererBlockEntity holder) {
                    holder.setRenderer(model);
                }
            });

            var sceneWidget = new SceneWidget(5, 5, 40, 40, level);
//            sceneWidget.setRenderFacing(false);
//            sceneWidget.setRenderSelect(false);
            sceneWidget.createScene(level);
//            sceneWidget.getRenderer().setOnLookingAt(null); // better performance
            sceneWidget.setRenderedCore(Collections.singleton(BlockPos.ZERO), null);
            sceneWidget.setBackground(new ColorBorderTexture(2, ColorPattern.T_WHITE.color));

            addWidget(sceneWidget);
        }
    }
    public static class IModelRenderer2 extends IModelRenderer {
        public IModelRenderer2(ResourceLocation modelLocation) {
            super(modelLocation);
        }

        @Override
        public @Nullable BakedModel getItemBakedModel() {
            return super.getItemBakedModel();
        }
    }
}
