package org.tcathebluecreper.totally_lib.client.animation2;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.tcathebluecreper.totally_lib.lib.AnimationUtils;
import org.tcathebluecreper.totally_lib.multiblock.MachineAnimation;

import java.util.*;

public class AnimationInstance {
    public final Animation animation;

    public final HashMap<AnimationElement, Integer> frames;
    public final HashMap<AnimationElement, ElementState> states;
    public final ArrayList<Pair<AnimationTrigger, Integer>> triggers = new ArrayList<>();

    public AnimationInstance(Animation animation) {
        this.animation = animation;

        frames = new HashMap<>();
        states = new HashMap<>();
        for(AnimationElement element : animation.allElements) {
            frames.put(element, 0);
            states.put(element, ElementState.STOPPED);
        }
    }

    public void trigger(AnimationTrigger trigger) {
        triggers.add(new ImmutablePair<>(trigger, -1));
    }
    public void trigger(AnimationTrigger trigger, int channel) {
        triggers.add(new ImmutablePair<>(trigger, channel));
    }

    public void tickAnimation(PoseStack poseStack, MultiBufferSource bufferSource, Level level) {
        for(AnimationElement element : animation.allElements) {
            int frame = frames.get(element);
            ElementState state = states.get(element);

            if(state != ElementState.RUNNING) if(element.checkStartTriggers(triggers)) {
                state = ElementState.RUNNING;
            }
            if(state != ElementState.RUNNING) if(element.checkStopTriggers(triggers)) {
                state = ElementState.STOPPED;
            }

            switch(state) {
                case RUNNING -> frame++;
                case REVERSING -> frame--;
            }

            frames.put(element, frame);
        }
        triggers.clear();
    }

    public void renderAnimation(PoseStack poseStack, MultiBufferSource bufferSource, Level level) {
        for(AnimationElement element : animation.elements) {
            int frame = frames.get(element);
            int nextFrame = frame;
            ElementState state = states.get(element);

            switch(state) {
                case RUNNING -> nextFrame = frame + 1;
                case REVERSING -> nextFrame = frame - 1;
            }
            nextFrame = frame % element.length;


        }
    }
}
