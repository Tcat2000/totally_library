package org.tcathebluecreper.totally_lib.client.animation.editor;

import blusunrize.immersiveengineering.client.utils.RenderUtils;
import com.lowdragmc.lowdraglib.client.renderer.block.RendererBlock;
import com.lowdragmc.lowdraglib.client.renderer.block.RendererBlockEntity;
import com.lowdragmc.lowdraglib.client.renderer.impl.IModelRenderer;
import com.lowdragmc.lowdraglib.gui.editor.ColorPattern;
import com.lowdragmc.lowdraglib.gui.texture.ColorBorderTexture;
import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.texture.ResourceBorderTexture;
import com.lowdragmc.lowdraglib.gui.widget.SceneWidget;
import com.lowdragmc.lowdraglib.gui.widget.TextFieldWidget;
import com.lowdragmc.lowdraglib.utils.BlockInfo;
import com.lowdragmc.lowdraglib.utils.TrackedDummyWorld;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import org.tcathebluecreper.totally_lib.multiblock.MachineAnimation;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AnimationElementWidget extends AnimationElement {
    private final AnimationEditor animationEditor;
    final List<BakedQuad> quads;
    final List<AnimationElement> otherElements;
    SceneWidget renderer;
    RendererBlockEntity holder;
    IGuiTexture selectedTexture = new ColorBorderTexture(-1, Color.RED.getRGB());

    MachineAnimation.AnimatedModelPart animationPart;

    public AnimationElementWidget(AnimationEditor animationEditor, List<AnimationElement> otherElements) {
        this(animationEditor, otherElements, new MachineAnimation.AnimatedModelPart(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>()));
    }

    public AnimationElementWidget(AnimationEditor animationEditor, List<AnimationElement> otherElements, MachineAnimation.AnimatedModelPart animationPart) {
        super(0, 16, 192, 50);
        this.animationEditor = animationEditor;
        this.otherElements = otherElements;
        this.setBackground(ResourceBorderTexture.BORDERED_BACKGROUND);

        animationEditor.machineAnimation.parts.add(animationPart);
        quads = animationPart.quads;
        this.animationPart = animationPart;

        posKeyframes = animationPart.positionFrames.stream().map(frame -> new AnimationKeyframeData("Pos", frame, (ClickData, f) -> animationPart.positionFrames.remove(f), animationEditor.selectedAnimationPart.getAnimationElement().positionFrames)).toList();

        AnimationEditor.IModelRenderer2 model = new AnimationEditor.IModelRenderer2(ResourceLocation.fromNamespaceAndPath("minecraft", "block/lectern"));

        quads.addAll(model.renderModel(animationEditor.mbDisplay.level, new BlockPos(0, 4, 0), null, null, Minecraft.getInstance().level.random));
        quads.addAll(model.renderModel(animationEditor.mbDisplay.level, new BlockPos(0, 4, 0), null, Direction.UP, Minecraft.getInstance().level.random));
        quads.addAll(model.renderModel(animationEditor.mbDisplay.level, new BlockPos(0, 4, 0), null, Direction.DOWN, Minecraft.getInstance().level.random));
        quads.addAll(model.renderModel(animationEditor.mbDisplay.level, new BlockPos(0, 4, 0), null, Direction.NORTH, Minecraft.getInstance().level.random));
        quads.addAll(model.renderModel(animationEditor.mbDisplay.level, new BlockPos(0, 4, 0), null, Direction.EAST, Minecraft.getInstance().level.random));
        quads.addAll(model.renderModel(animationEditor.mbDisplay.level, new BlockPos(0, 4, 0), null, Direction.SOUTH, Minecraft.getInstance().level.random));
        quads.addAll(model.renderModel(animationEditor.mbDisplay.level, new BlockPos(0, 4, 0), null, Direction.WEST, Minecraft.getInstance().level.random));

        animationPart.model = model;

        TrackedDummyWorld level = new TrackedDummyWorld();
        level.addBlock(BlockPos.ZERO, BlockInfo.fromBlock(RendererBlock.BLOCK));
        this.holder = (RendererBlockEntity) level.getBlockEntity(BlockPos.ZERO);

        holder.setRenderer(new IModelRenderer(ResourceLocation.fromNamespaceAndPath("minecraft", "block/diamond_block")));

        renderer = new SceneWidget(4, 4, 42, 42, level);
        renderer.setRenderedCore(List.of(BlockPos.ZERO), null);
        renderer.setAfterWorldRender(sceneWidget -> {
            var poseStack = new PoseStack();
            var tessellator = Tesselator.getInstance();
            var buffer = tessellator.getBuilder();

            poseStack.pushPose();
            poseStack.translate(-0.5, 0 - .5, -0.5);
            buffer.begin(RenderType.solid().mode(), RenderType.solid().format());

            var lightTexture = Minecraft.getInstance().gameRenderer.lightTexture();
            lightTexture.turnOnLightLayer();
            RenderSystem.setShader(GameRenderer::getRendertypeTranslucentShader);

            RenderSystem.clearColor(1, 1, 1, 1);

            RenderUtils.renderModelTESRFancy(quads, buffer, poseStack, animationEditor.mbDisplay.level, BlockPos.ZERO, false, -1, 15);


//                model.renderItem(ItemStack.EMPTY, ItemDisplayContext.GUI, false, poseStack, MultiBufferSource.immediate(buffer), 15, 15, model.getItemBakedModel());
            poseStack.popPose();
            tessellator.end();
        });
        renderer.setRenderSelect(false);
        renderer.setRenderFacing(false);
        renderer.getRenderer().setOnLookingAt(null);
        renderer.createScene(level);
        renderer.setBackground(new ColorBorderTexture(1, ColorPattern.T_WHITE.color));
//            renderer.setIntractable(false);
        addWidget(renderer);
        var lightTexture = Minecraft.getInstance().gameRenderer.lightTexture();
        lightTexture.turnOnLightLayer();
        RenderSystem.setShader(GameRenderer::getRendertypeTranslucentShader);

        RenderSystem.clearColor(1, 1, 1, 1);

        addWidget(new TextFieldWidget(52, 20, 50, 15, null, null) {
            @Override
            protected void onTextChanged(String newTextString) {
                if(!ResourceLocation.isValidResourceLocation(newTextString)) return;
                IModelRenderer newModel = new IModelRenderer(ResourceLocation.parse(newTextString));

                animationPart.model = newModel;
                quads.clear();
                quads.addAll(newModel.renderModel(animationEditor.mbDisplay.level, new BlockPos(0, 4, 0), null, null, Minecraft.getInstance().level.random));
                quads.addAll(newModel.renderModel(animationEditor.mbDisplay.level, new BlockPos(0, 4, 0), null, Direction.UP, Minecraft.getInstance().level.random));
                quads.addAll(newModel.renderModel(animationEditor.mbDisplay.level, new BlockPos(0, 4, 0), null, Direction.DOWN, Minecraft.getInstance().level.random));
                quads.addAll(newModel.renderModel(animationEditor.mbDisplay.level, new BlockPos(0, 4, 0), null, Direction.NORTH, Minecraft.getInstance().level.random));
                quads.addAll(newModel.renderModel(animationEditor.mbDisplay.level, new BlockPos(0, 4, 0), null, Direction.EAST, Minecraft.getInstance().level.random));
                quads.addAll(newModel.renderModel(animationEditor.mbDisplay.level, new BlockPos(0, 4, 0), null, Direction.SOUTH, Minecraft.getInstance().level.random));
                quads.addAll(newModel.renderModel(animationEditor.mbDisplay.level, new BlockPos(0, 4, 0), null, Direction.WEST, Minecraft.getInstance().level.random));
            }
        });
    }

    public AnimationElementWidget setModel(String s) {
        quads.clear();

        if(!ResourceLocation.isValidResourceLocation(s)) return this;

//                    holder.setRenderer(new IModelRenderer(ResourceLocation.parse(newTextString)));
        AnimationEditor.IModelRenderer2 model = new AnimationEditor.IModelRenderer2(ResourceLocation.parse(s));

        quads.addAll(model.renderModel(animationEditor.mbDisplay.level, new BlockPos(0, 4, 0), null, null, Minecraft.getInstance().level.random));
        quads.addAll(model.renderModel(animationEditor.mbDisplay.level, new BlockPos(0, 4, 0), null, Direction.UP, Minecraft.getInstance().level.random));
        quads.addAll(model.renderModel(animationEditor.mbDisplay.level, new BlockPos(0, 4, 0), null, Direction.DOWN, Minecraft.getInstance().level.random));
        quads.addAll(model.renderModel(animationEditor.mbDisplay.level, new BlockPos(0, 4, 0), null, Direction.NORTH, Minecraft.getInstance().level.random));
        quads.addAll(model.renderModel(animationEditor.mbDisplay.level, new BlockPos(0, 4, 0), null, Direction.EAST, Minecraft.getInstance().level.random));
        quads.addAll(model.renderModel(animationEditor.mbDisplay.level, new BlockPos(0, 4, 0), null, Direction.SOUTH, Minecraft.getInstance().level.random));
        quads.addAll(model.renderModel(animationEditor.mbDisplay.level, new BlockPos(0, 4, 0), null, Direction.WEST, Minecraft.getInstance().level.random));
        return this;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(isMouseOverElement(mouseX, mouseY) && !super.mouseClicked(mouseX, mouseY, button)) {
            this.setBackground(ResourceBorderTexture.BORDERED_BACKGROUND, selectedTexture);
            animationEditor.selectedAnimationPart = this;
            animationEditor.timeLine.posKeyframes = posKeyframes;
            animationEditor.timeLine.rotKeyframes = rotKeyframes;
            animationEditor.timeLine.sizeKeyframes = sizeKeyframes;
            otherElements.forEach(element -> {
                if(element != this) element.unselect();
            });
            return true;
        }
        return false;
    }

    @Override
    public MachineAnimation.AnimatedModelPart getAnimationElement() {
        return animationPart;
    }
}
