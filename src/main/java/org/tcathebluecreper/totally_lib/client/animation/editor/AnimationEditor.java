package org.tcathebluecreper.totally_lib.client.animation.editor;

import com.google.gson.JsonObject;
import com.lowdragmc.lowdraglib.client.renderer.impl.IModelRenderer;
import com.lowdragmc.lowdraglib.gui.texture.*;
import com.lowdragmc.lowdraglib.gui.widget.*;
import com.lowdragmc.lowdraglib.gui.widget.layout.Layout;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import org.tcathebluecreper.totally_lib.ldlib.InspectorViewable;
import org.tcathebluecreper.totally_lib.ldlib.MultiblockDisplayPanelWidget;
import org.tcathebluecreper.totally_lib.ldlib.ScreenSpaceWidget;
import org.tcathebluecreper.totally_lib.multiblock.MachineAnimation;

import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AnimationEditor extends WidgetGroup {
    private static final Logger log = LogManager.getLogger(AnimationEditor.class);
    final MultiblockDisplayPanelWidget mbDisplay;

    public final ScreenSpaceWidget top;
    public final ScreenSpaceWidget bottom;
    public final ScreenSpaceWidget inspector;
    public final DraggableScrollableWidgetGroup inspectorPane;
    public final TabContainer tabs;
    public final TabButton inspectorTab;
    public final TabButton selectorTab;
    public final WidgetGroup selectorTabPane;
    public final DraggableScrollableWidgetGroup partsList;
    public final WidgetGroup partsListContainer;
    public InspectorViewable inspecting = null;
    public final List<AnimationElement> animationParts = new ArrayList<>();
    public AnimationElement selectedAnimationPart;
    final AnimationTimeLine timeLine;
    public MachineAnimation machineAnimation = new MachineAnimation();

    public final ScreenSpaceWidget importWindow;
    public final ScreenSpaceWidget exportWindow;
    public AtomicReference<String> exportText = new AtomicReference<>("");

    public AnimationEditor() {
        ResourceBorderTexture tabSelectedTexture = new ResourceBorderTexture("minecraft:textures/gui/container/creative_inventory/tabs.png", 26, 32, 4, 4);
        tabSelectedTexture.imageWidth = 1/10f;
        tabSelectedTexture.imageHeight = 1/8f;

        ResourceBorderTexture tabUnselectedTexture = new ResourceBorderTexture("minecraft:textures/gui/container/creative_inventory/tabs.png", 26, 32, 4, 4);
        tabUnselectedTexture.imageWidth = 1/10f;
        tabUnselectedTexture.imageHeight = 1/8f;
        tabUnselectedTexture.offsetY = 1/8f;

        importWindow = new ScreenSpaceWidget(0,0,0,0) {
            @Override
            public void updateScreen() {
                Widget w = this.widgets.get(0);
                w.setSelfPosition(getSizeWidth() / 2 + w.getSizeWidth() / 2, getSizeHeight() / 2 + w.getSizeHeight() / 2);
            }
        };
        importWindow.initTemplate();
        WidgetGroup importPanel = new WidgetGroup(0,0,300,100);
        importPanel.initTemplate();
        importWindow.addWidget(importPanel);
//        importWindow.setLayout(Layout.HORIZONTAL_CENTER);
        TextFieldWidget importField = new TextFieldWidget(6, 6, 200, 15, null, null);
        importField.initTemplate();
        ButtonWidget importButton = new ButtonWidget(6, 24, 100, 15, new GuiTextureGroup(ResourceBorderTexture.BUTTON_COMMON, new TextTexture("Import")), clickData -> {
            try {
                importString(importField.getCurrentString());
            } catch (Exception e) {
                log.error(e);
            }
            importWindow.setVisible(false);
        });
        importPanel.addWidget(importButton);
        importPanel.addWidget(importField);
        importWindow.setVisible(false);

        exportWindow = new ScreenSpaceWidget(0,0,0,0) {
            @Override
            public void updateScreen() {
                Widget w = this.widgets.get(0);
                w.setSelfPosition(getSizeWidth() / 2 - w.getSizeWidth() / 2, getSizeHeight() / 2 - w.getSizeHeight() / 2);
            }
        };
        WidgetGroup exportPanel = new WidgetGroup(0,0,300,50);
        exportWindow.addWidget(exportPanel);
        exportWindow.setLayout(Layout.HORIZONTAL_CENTER);
        TextFieldWidget exportField = new TextFieldWidget(6,6,100,15, null, null) {
            @Override
            protected void onTextChanged(String newTextString) {
                if(!getCurrentString().equals(exportText.get())) setCurrentString(exportText);
            }
        };
        exportField.setCurrentString(exportText);


        mbDisplay = new MultiblockDisplayPanelWidget(0,30,-200,-100, this::postRender);
        addWidget(mbDisplay);


        mbDisplay.loadMultiblock(ResourceLocation.fromNamespaceAndPath("test","multiblock"));

        top = new ScreenSpaceWidget(0, 0, 0, 30);
        addWidget(top);

        top.addWidget(mbDisplay.crateInputField(7,7,100, 15));
        top.addWidget(new ButtonWidget(120, 7, 40, 15, new GuiTextureGroup(ResourceBorderTexture.BUTTON_COMMON, new TextTexture("Export")), clickData -> {
            log.info(exportString(machineAnimation.serialize()));
        }));
        top.addWidget(new ButtonWidget(170, 7, 40, 15, new GuiTextureGroup(ResourceBorderTexture.BUTTON_COMMON, new TextTexture("Import")), clickData -> {
            importWindow.setVisible(true);
        }));

        bottom = new ScreenSpaceWidget(0, -100, 0, 0);
        bottom.addWidget(timeLine = new AnimationTimeLine(this));
        addWidget(bottom);

        inspector = new ScreenSpaceWidget(-200, 55, 0, -100);
        addWidget(inspector);

        tabs = new TabContainer(0, -25, 300,30);
        inspector.addWidget(tabs);

        inspectorTab = new TabButton(0,0,100,30);
        inspectorTab.setTexture(new GuiTextureGroup(tabSelectedTexture, new TextTexture("INSPECTOR")), new GuiTextureGroup(tabUnselectedTexture, new TextTexture("INSPECTOR")));
        selectorTab = new TabButton(100,0,100,30);
        selectorTab.setTexture(new GuiTextureGroup(tabSelectedTexture, new TextTexture("SELECTOR")), new GuiTextureGroup(tabUnselectedTexture, new TextTexture("SELECTOR")));
        selectorTabPane = new WidgetGroup(4, 31, 192, 192);

        partsListContainer = new WidgetGroup();
        partsList = new DraggableScrollableWidgetGroup(4, 47, 192, 178);
        selectorTabPane.addWidget(new ButtonWidget(1,1, 16, 12, new GuiTextureGroup(ResourceBorderTexture.BUTTON_COMMON, new TextTexture("+")), clickData -> {
            AnimationElementPreview part = new AnimationElementPreview(this, animationParts);
            if(selectedAnimationPart instanceof AnimationElementGroupWidget) {
                ((AnimationElementGroupWidget) selectedAnimationPart).otherElements.add(part);
                selectedAnimationPart.addWidget(part);
                ((MachineAnimation.AnimatedModelGroup)selectedAnimationPart.getAnimationElement()).subElements.add(part.getAnimationElement());
            } else {
                animationParts.add(part);
                partsListContainer.addWidget(part);
            }
        }));
        selectorTabPane.addWidget(new ButtonWidget(20,1, 16, 12, new GuiTextureGroup(ResourceBorderTexture.BUTTON_COMMON, new TextTexture("[+]")), clickData -> {
            AnimationElementGroupWidget part = new AnimationElementGroupWidget(this, animationParts);
            animationParts.add(part);
            partsListContainer.addWidget(part);
        }));
        selectorTabPane.addWidget(new ButtonWidget(41,1, 16, 12, new GuiTextureGroup(ResourceBorderTexture.BUTTON_COMMON, new TextTexture("-")), clickData -> {}));
        partsListContainer.setDynamicSized(true);
        partsListContainer.setLayout(Layout.VERTICAL_LEFT);

        partsList.addWidget(partsListContainer);
        ScreenSpaceWidget partsListTab = new ScreenSpaceWidget(-200, 55, 0, -100);
        partsListTab.addWidget(partsList);
        selectorTabPane.addWidget(partsList);
        tabs.addTab(selectorTab, selectorTabPane);

        inspectorPane = new DraggableScrollableWidgetGroup(4,34, 200, 300);

        tabs.addTab(inspectorTab, inspectorPane);


        addWidget(importWindow);
    }

    private String exportString(JsonObject data) {
        StringBuilder output = new StringBuilder().append("/n");
        output.append(".fromJson(").append(data).append(")");
        output.append(".editorInfo(\"machine\", \"").append(mbDisplay.getLastMachineID()).append("\")");
        List<DyeColor> colors = new ArrayList<>();
        for(AnimationElement element : animationParts) {
            for(AnimationKeyframeData frame : element.posKeyframes) {
                colors.add(frame.color);
            }
            for(AnimationKeyframeData frame : element.rotKeyframes) {
                colors.add(frame.color);
            }
            for(AnimationKeyframeData frame : element.sizeKeyframes) {
                colors.add(frame.color);
            }
        }
        if(!colors.isEmpty()) {
            output.append(".editorInfo(\"colors\", [");
            for(DyeColor color : colors) {
                output.append(color).append(",");
            }
            output.append("])");
        }
        return output.append(";").toString();
    }

    public void importString(String data) {
        if(data.startsWith("{")) {
            try {
                machineAnimation.deserialize(data);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return;
        }
        Pattern machinePattern = Pattern.compile("\\.editorInfo\\(\"machine\",([^)]*)\\)", Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
        Matcher machineMatcher = machinePattern.matcher(data);
        machineMatcher.find();
        String machineData = machineMatcher.group(1);
        machineData = machineData.substring(2, machineData.length() - 1);
        mbDisplay.loadMultiblock(ResourceLocation.parse(machineData));

        Pattern dataPattern = Pattern.compile("\\.fromJson\\(([^)]*)\\)", Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
        Matcher dataMatcher = dataPattern.matcher(data);
        dataMatcher.find();
        String jsonData = dataMatcher.group();
        jsonData = jsonData.substring(10, jsonData.length() - 1);
        machineAnimation.deserialize(jsonData);

        machineAnimation.forAllParts(part -> {
            AnimationElement element = null;
            if(part instanceof MachineAnimation.AnimatedModelPart) element = new AnimationElementPreview(this, animationParts, (MachineAnimation.AnimatedModelPart) part);
            if(part instanceof MachineAnimation.AnimatedModelGroup) element = new AnimationElementGroupWidget(this, animationParts, (MachineAnimation.AnimatedModelGroup) part);
            if(element != null) {
                animationParts.add(element);
                partsListContainer.addWidget(element);
            }
        });
    }

    public void postRender(SceneWidget scene) {
        var poseStack = new PoseStack();
        var tessellator = Tesselator.getInstance();
        var bufferSource = MultiBufferSource.immediate(tessellator.getBuilder());

        var lightTexture = Minecraft.getInstance().gameRenderer.lightTexture();
        lightTexture.turnOnLightLayer();
        RenderSystem.clearColor(1,1,1,1);

        machineAnimation.render(timeLine.cursorPos + (timeLine.playing ? Minecraft.getInstance().getPartialTick() : 0), poseStack, bufferSource, mbDisplay.level);

        if(selectedAnimationPart != null) {
            machineAnimation.renderWireframe(selectedAnimationPart.getAnimationElement(), timeLine.cursorPos + (timeLine.playing ? Minecraft.getInstance().getPartialTick() : 0), poseStack, bufferSource);
        }

        if(tessellator.getBuilder().building()) tessellator.end();
    }

    WidgetGroup inspectorContent;

    public void reloadInspector() {
        inspectorPane.removeWidget(inspectorContent);
        inspectorContent = new WidgetGroup();
        inspectorContent.setDynamicSized(true);
        inspecting.loadInspector(inspectorContent, 0, 0, inspectorPane.getSizeWidth() - 4, inspectorPane.getSizeHeight());
        inspectorPane.addWidget(inspectorContent);
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
