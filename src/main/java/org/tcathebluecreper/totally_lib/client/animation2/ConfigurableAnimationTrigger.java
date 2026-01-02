package org.tcathebluecreper.totally_lib.client.animation2;

import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class ConfigurableAnimationTrigger {
    public AnimationTrigger trigger; // Source
    public int channel; // Channel is used for things like starting and stopping with machine parallel. -1 any

    public boolean check(List<Pair<AnimationTrigger, Integer>> triggers) {
        for(Pair<AnimationTrigger, Integer> pair : triggers) {
            if(trigger == pair.getKey() && (channel == pair.getValue() || channel == -1 || pair.getValue() == -1)) return true;
        }
        return false;
    }
}
