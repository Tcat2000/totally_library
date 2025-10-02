package org.tcathebluecreper.totally_lib.dev_utils;

import com.lowdragmc.lowdraglib.gui.texture.*;
import com.lowdragmc.lowdraglib.gui.widget.*;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;
import org.tcathebluecreper.totally_lib.ldlib.MultiblockDisplayPanelWidget;
import org.tcathebluecreper.totally_lib.ldlib.ScreenSpaceWidget;
import org.tcathebluecreper.totally_lib.multiblock.*;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VoxelShapeEditor extends WidgetGroup {
    private static final Logger log = LogManager.getLogger(VoxelShapeEditor.class);
    private final MultiblockDisplayPanelWidget mbDisplay;
    public VoxelShape shape = null;

    public ArrayList<AABBListWidget> parts = new ArrayList<>();

    private final ScreenSpaceWidget side;
    private final ScreenSpaceWidget top;
    private final ScreenSpaceWidget sideTop;
    private final DraggableScrollableWidgetGroup list;
    private final TextFieldWidget displayID;
    private ResourceLocation lastDisplayID;
    private final WidgetGroup importPanel;
    private final TextFieldWidget importInput;

    public VoxelShapeEditor() {
        super(0,0,100,100);
        initTemplate();

        mbDisplay = new MultiblockDisplayPanelWidget(0,30,-205,0, this::postRender);
        top = new ScreenSpaceWidget(0,0,-205, 30);
        sideTop = new ScreenSpaceWidget(-205,0,0,30);
        side = new ScreenSpaceWidget(-205,30,0,0);

        list = new DraggableScrollableWidgetGroup(5,4,197,0);
        side.addWidget(list);

        ButtonWidget export = new ButtonWidget(190,7,50,15, new GuiTextureGroup(ResourceBorderTexture.BUTTON_COMMON, new TextTexture("export")), clickData -> {
            log.info(export());
        });
        export.initTemplate();
        top.addWidget(export);

        addWidget(top);
        addWidget(sideTop);
        addWidget(mbDisplay);
        addWidget(side);

        displayID = new TextFieldWidget(60, 7, 120, 15, null, null);
        if(!TLModMultiblocks.allMultiblocks.isEmpty()) displayID.setCurrentString(TLModMultiblocks.allMultiblocks.get(0).getId());
        top.addWidget(new LabelWidget(5,10, "multiblock:"));
        top.addWidget(displayID);



        ButtonWidget add = new ButtonWidget(7,7,15,15, new GuiTextureGroup(ResourceBorderTexture.BUTTON_COMMON, new TextTexture("+")), clickData -> new AABBListWidget(parts, list, this::generateShape));
        add.initTemplate();
        sideTop.addWidget(add);

        ButtonWidget remove = new ButtonWidget(26,7,15,15, new GuiTextureGroup(ResourceBorderTexture.BUTTON_COMMON, new TextTexture("-")), clickData -> {
            AABBListWidget p = AABBListWidget.selected.get(parts);
            if(p == null) return;
            list.removeWidget(p);
            parts.remove(p);
            parts.forEach(AABBListWidget::update);
        });
        remove.initTemplate();
        sideTop.addWidget(remove);



        importPanel = new WidgetGroup(0,0,300,40);
        importPanel.initTemplate();
        importPanel.addWidget(new LabelWidget(130, 4, "Import Text"));
        importInput = new TextFieldWidget(4,15,292, 15, null, null);
        importInput.initTemplate();
        importPanel.addWidget(importInput);
        importPanel.setVisible(false);
        addWidget(importPanel);

        ButtonWidget load = new ButtonWidget(190,7,30,15, new GuiTextureGroup(ResourceBorderTexture.BUTTON_COMMON, new TextTexture("load")), clickData -> {
            importData();
            importPanel.setVisible(false);
        });
        load.initTemplate();
        importPanel.addWidget(load);



        ButtonWidget imp = new ButtonWidget(245,7,50,15, new GuiTextureGroup(ResourceBorderTexture.BUTTON_COMMON, new TextTexture("import")), clickData -> {
            importPanel.setVisible(true);
        });
        imp.initTemplate();
        top.addWidget(imp);
    }

    public String export() {
        try {
            StringBuilder output = new StringBuilder();
            output.append(".shape([");

            for(AABBListWidget data : parts) {
                if(data == null) continue;
                output.append(data);
            }

            output.append("])");
            return output.toString();
        } catch(Exception e) {
            log.error(e);
            return "";
        }
    }

    public void importData() {
        int ct = parts.size();
        for(int i = 0; i < ct; i++) {
            list.removeWidget(parts.get(0));
            parts.remove(0);
        }
        String data = importInput.getCurrentString();

        Pattern regex = Pattern.compile("(?:Shapes\\.(?:size|offset)\\(\\s*\\d*\\.?\\d*\\s*,\\s*\\d*\\.?\\d*\\s*,\\s*\\d*\\.?\\d*\\s*,\\s*\\d*\\.?\\d*\\s*,\\s*\\d*\\.?\\d*\\s*,\\s*\\d*\\.?\\d*\\s*\\))<?", Pattern.MULTILINE);
        Pattern subRegex = Pattern.compile("Shapes\\.(size|offset)\\(\\s*(\\d*\\.?\\d*)\\s*,\\s*(\\d*\\.?\\d*)\\s*,\\s*(\\d*\\.?\\d*)\\s*,\\s*(\\d*\\.?\\d*)\\s*,\\s*(\\d*\\.?\\d*)\\s*,\\s*(\\d*\\.?\\d*)\\s*\\)", Pattern.MULTILINE);
        Matcher match = regex.matcher(data);


        while(match.find()) {
            try {
                Matcher subMatch = subRegex.matcher(match.group(0));
                subMatch.find();
                String type = subMatch.group(1);
                double minX = Double.parseDouble(subMatch.group(2));
                double minY = Double.parseDouble(subMatch.group(3));
                double minZ = Double.parseDouble(subMatch.group(4));
                double maxX = Double.parseDouble(subMatch.group(5));
                double maxY = Double.parseDouble(subMatch.group(6));
                double maxZ = Double.parseDouble(subMatch.group(7));

                AABBListWidget widget = new AABBListWidget(parts, list, this::generateShape);

                widget.mode = !type.equals("size");

                widget.minX.setCurrentString(minX);
                widget.minY.setCurrentString(minY);
                widget.minZ.setCurrentString(minZ);
                widget.maxX.setCurrentString(maxX);
                widget.maxY.setCurrentString(maxY);
                widget.maxZ.setCurrentString(maxZ);
            } catch(Exception ignored) {}
        }
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        setSize(gui.getWidth(), gui.getHeight());
        list.setSize(197, gui.getHeight() - 46);

        importPanel.setSelfPosition(gui.getScreenWidth() / 2 - 292 / 2, gui.getScreenHeight() / 2 - 40 / 2);

        if(ResourceLocation.isValidResourceLocation(displayID.getCurrentString()) && !ResourceLocation.parse(displayID.getCurrentString()).equals(lastDisplayID)) {
            ResourceLocation id = ResourceLocation.parse(displayID.getCurrentString());
            lastDisplayID = id;
            mbDisplay.loadMultiblock(id);
        }
    }

    private void postRender(SceneWidget sceneWidget) {
        var poseStack = new PoseStack();
        var tessellator = Tesselator.getInstance();
        var buffer = tessellator.getBuilder();
        var matrix4f = poseStack.last().pose();
        var normal = poseStack.last().normal();

        poseStack.pushPose();

        buffer.begin(VertexFormat.Mode.LINES, DefaultVertexFormat.POSITION_COLOR_NORMAL);
        RenderSystem.enableBlend();
        RenderSystem.disableDepthTest();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        RenderSystem.disableCull();
        RenderSystem.setShader(GameRenderer::getRendertypeLinesShader);
        RenderSystem.lineWidth(5);


        if(shape != null) shape.forAllEdges((x0, y0, z0, x1, y1, z1) -> {
            float f = (float)(x1 - x0);
            float f1 = (float)(y1 - y0);
            float f2 = (float)(z1 - z0);
            float f3 = Mth.sqrt(f * f + f1 * f1 + f2 * f2);
            f /= f3;
            f1 /= f3;
            f2 /= f3;
            buffer.vertex(matrix4f, (float)(x0), (float)(y0), (float)(z0)).color(-1).normal(normal, f, f1, f2).endVertex();
            buffer.vertex(matrix4f, (float)(x1), (float)(y1), (float)(z1)).color(-1).normal(normal, f, f1, f2).endVertex();
        });
        if(AABBListWidget.selected != null && AABBListWidget.selected.get(parts) != null && AABBListWidget.selected.get(parts).value != null) AABBListWidget.selected.get(parts).getShape().forAllEdges((x0, y0, z0, x1, y1, z1) -> {
            float f = (float)(x1 - x0);
            float f1 = (float)(y1 - y0);
            float f2 = (float)(z1 - z0);
            float f3 = Mth.sqrt(f * f + f1 * f1 + f2 * f2);
            f /= f3;
            f1 /= f3;
            f2 /= f3;
            buffer.vertex(matrix4f, (float)(x0), (float)(y0), (float)(z0)).color(Color.RED.getRGB()).normal(normal, f, f1, f2).endVertex();
            buffer.vertex(matrix4f, (float)(x1), (float)(y1), (float)(z1)).color(Color.RED.getRGB()).normal(normal, f, f1, f2).endVertex();
        });

        tessellator.end();
        poseStack.popPose();
        RenderSystem.enableDepthTest();
        RenderSystem.enableCull();
    }

    private void generateShape() {
        VoxelShape[] shapes = new VoxelShape[parts.size()];
        for(int i = 0; i < parts.size(); i++) {
            shapes[i] = parts.get(i).getShape();
        }
        try {
//            System.out.println(parts.stream().map(AABBListWidget::getAABB).map(this::aabb16).map(this::shape).distinct().toList().toArray(new VoxelShape[parts.size()]));
//            VoxelShape[] array = (VoxelShape[]) parts.stream().map(AABBListWidget::getAABB).map(this::aabb16).map(this::shape).distinct().toArray();
            shape = Shapes.or(Shapes.empty(), shapes);
        } catch(Exception e) { log.error(e); }
    }

    private static class AABBListWidget extends WidgetGroup {
        List<AABBListWidget> list;
        AABB value = new AABB(0,0,0,0,0,0);
        AABB oldValue = value;
        ColorBorderTexture selectTexture = new ColorBorderTexture(1, Color.RED.getRGB());
        static HashMap<List<AABBListWidget>, AABBListWidget> selected = new HashMap<>();
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

        public AABBListWidget(List<AABBListWidget> list, WidgetGroup parent, Runnable trigger) {
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
            list.forEach(AABBListWidget::update);
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
                list.forEach(AABBListWidget::update);
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
}
