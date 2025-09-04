package org.tcathebluecreper.totally_lib.client;

import blusunrize.immersiveengineering.api.multiblocks.blocks.registry.MultiblockBlockEntityMaster;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import org.joml.Vector3f;
import org.tcathebluecreper.totally_lib.recipe.TLRecipeProcess;
import org.tcathebluecreper.totally_lib.lib.TIDynamicModel;
import org.tcathebluecreper.totally_lib.multiblock.TIMultiblockState;
import org.tcathebluecreper.totally_immersive.Multiblock.chemical_bath.ChemicalBathState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TIBEAnimation<P extends TLRecipeProcess<?,S>, S extends TIMultiblockState<?,S>> {
    public final SubTickAnimationSetting setting;
    public final Map<String,List<AnimationLayer>> layers;
    public final Map<String, TIDynamicModel> bones;

    public TIBEAnimation(SubTickAnimationSetting setting, List<AnimationLayer> layers, Map<String, TIDynamicModel> bones) { // enables caching
        this.setting = setting;
        this.bones = bones;
        Map<String,List<AnimationLayer>> map = new HashMap<>();
        layers.forEach((animationLayer -> {
            if(!map.containsKey(animationLayer.bone)) {
                map.put(animationLayer.bone, new ArrayList<>());
            }
            map.get(animationLayer.bone).add(animationLayer);
        }));
        this.layers = map;
    }
    public void render(MultiblockBlockEntityMaster<ChemicalBathState> te, float pPartialTick, PoseStack matrixStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        float frame = te.getHelper().getContext().getState().getRecipeProcess().tick[0] + pPartialTick;
        bones.forEach((name, model) -> {
            matrixStack.pushPose();
            try {
                layers.getOrDefault(name, new ArrayList<>()).forEach(layer -> {
                    if(layer.startFrame >= frame && (layer.endFrame >= frame || layer.holdOnLastFrame)) {
                        
                    }
                });
            } catch(Exception e) {
                System.out.println(e);
            }
            matrixStack.popPose();
        });
    }

    public static class AnimationLayer {
        public final String bone;
        public final boolean holdOnLastFrame;
        public final int startFrame;
        public final int endFrame;

        public AnimationLayer(String bone, int startFrame, int endFrame, boolean holdOnLastFrame) {
            this.bone = bone;
            this.holdOnLastFrame = holdOnLastFrame;
            this.startFrame = startFrame;
            this.endFrame = endFrame;
        }
    }
    public static class AnimationLayerPosition extends AnimationLayer {
        public final Vector3f value;
        public AnimationLayerPosition(String bone, int startFrame, int endFrame, boolean holdOnLastFrame, Vector3f value) {
            super(bone, startFrame, endFrame, holdOnLastFrame);
            this.value = value;
        }
    }
}
