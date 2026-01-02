package org.tcathebluecreper.totally_lib.client.animation2;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.tcathebluecreper.totally_lib.client.animation.ProgressMode;

import java.util.ArrayList;
import java.util.List;

public class AnimationElement {
    @NotNull
    public final Animation owner; // The animation this is element of.
    @Nullable
    public final AnimationGroup parent; // The parent group, null if none.

    public final Timeline.PosTimeline positionFrames = new Timeline.PosTimeline(); // The frames of the animation
    public final Timeline.RotationTimeline rotationFrames = new Timeline.RotationTimeline();
    public final Timeline.RotationTimeline scaleFrames = new Timeline.RotationTimeline();

    public ProgressMode mode;
    public int length;
    public int repeatCount; // How many times the animation will repeat after started, 0 is indefinitely.
    public InterruptMode interruptMode; // How the animation behaves when stopped mid-run.
    public ArrayList<ConfigurableAnimationTrigger> startTriggers; // Triggers to start animation.
    public ArrayList<ConfigurableAnimationTrigger> stopTriggers; // Triggers to start animation.

    public AnimationElement(@NotNull Animation owner, @Nullable AnimationGroup parent) {
        this.owner = owner;
        this.parent = parent;
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
}
