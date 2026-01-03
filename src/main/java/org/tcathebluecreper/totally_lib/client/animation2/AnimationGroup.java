package org.tcathebluecreper.totally_lib.client.animation2;

import com.mojang.blaze3d.vertex.PoseStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class AnimationGroup extends AnimationElement {
    public List<AnimationElement> subElements = new ArrayList<>();

    public AnimationGroup(@NotNull Animation owner, @Nullable AnimationGroup parent, String filter) {
        super(owner, parent, filter);
    }

    @Override
    public void postRender(int frame, PoseStack poseStack, AnimationInstance state, String filter) {
        subElements.forEach(element -> element.render(frame, poseStack, state, filter));
    }
}
