package org.tcathebluecreper.totally_lib.client.animation2;

import com.mojang.blaze3d.vertex.PoseStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AnimationPart extends AnimationElement {
    public AnimationPart(@NotNull Animation owner, @Nullable AnimationGroup parent, String filter) {
        super(owner, parent, filter);
    }

    @Override
    public void postRender(int frame, PoseStack poseStack, AnimationInstance state, String filter) {}
}
