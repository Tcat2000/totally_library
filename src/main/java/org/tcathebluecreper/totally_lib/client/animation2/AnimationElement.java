package org.tcathebluecreper.totally_lib.client.animation2;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.entity.AnimationState;
import org.apache.commons.lang3.tuple.Pair;
import org.checkerframework.checker.regex.qual.Regex;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.tcathebluecreper.totally_lib.client.animation.ProgressMode;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public abstract class AnimationElement {
    @NotNull
    public final Animation owner; // The animation this is element of.
    @Nullable
    public final AnimationGroup parent; // The parent group, null if none.

    public final Timeline.PosTimeline positionFrames = new Timeline.PosTimeline(); // The frames of the animation
    public final Timeline.RotationTimeline rotationFrames = new Timeline.RotationTimeline();
    public final Timeline.ScaleTimeline scaleFrames = new Timeline.ScaleTimeline();

    public ProgressMode mode;
    public int length;
    public int repeatCount; // How many times the animation will repeat after started, 0 is indefinitely.
    public InterruptMode interruptMode; // How the animation behaves when stopped mid-run.
    public ArrayList<ConfigurableAnimationTrigger> startTriggers; // Triggers to start animation.
    public ArrayList<ConfigurableAnimationTrigger> stopTriggers; // Triggers to start animation.

    public final Pattern filter;

    public AnimationElement(@NotNull Animation owner, @Nullable AnimationGroup parent, String filter) {
        this.owner = owner;
        this.parent = parent;
        this.filter = Pattern.compile(filter);
    }

    public boolean checkStartTriggers(List<Pair<AnimationTrigger, Integer>> triggers) {
        return checkTriggers(startTriggers, triggers);
    }

    public boolean checkStopTriggers(List<Pair<AnimationTrigger, Integer>> triggers) {
        return checkTriggers(stopTriggers, triggers);
    }

    private boolean checkTriggers(List<ConfigurableAnimationTrigger> checks, List<Pair<AnimationTrigger, Integer>> triggers) {
        for(ConfigurableAnimationTrigger check : checks) {
            if(check.check(triggers)) return true;
        }
        return false;
    }

    public boolean checkFilter(String filter) {
        return this.filter.matcher(filter).matches();
    }

    public void render(int frame, PoseStack poseStack, AnimationInstance state, String filter) {
        if(!filter.isBlank() && !checkFilter(filter)) return;

        positionFrames.applyFrameTranslations(frame, poseStack, state, this);
        rotationFrames.applyFrameTranslations(frame, poseStack, state, this);
        scaleFrames.applyFrameTranslations(frame, poseStack, state, this);

        postRender(frame, poseStack, state, filter);
    }

    public abstract void postRender(int frame, PoseStack poseStack, AnimationInstance state, String filter);
}
